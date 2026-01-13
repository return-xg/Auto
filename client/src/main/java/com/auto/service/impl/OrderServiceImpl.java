package com.auto.service.impl;

import com.auto.common.utils.DateUtils;
import com.auto.common.utils.StringUtils;
import com.auto.domain.*;
import com.auto.domain.dto.CreateOrderDTO;
import com.auto.domain.dto.RefundApplyDTO;
import com.auto.domain.vo.LogisticsVO;
import com.auto.domain.vo.OrderProductVO;
import com.auto.domain.vo.OrderVO;
import com.auto.mapper.*;
import com.auto.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private RefundReturnMapper refundReturnMapper;

    @Autowired
    private IOrderProductService orderProductService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IStoreService storeService;

    private static final BigDecimal FREIGHT_PRICE = new BigDecimal("10.00");

    private static final Long ORDER_STATUS_PENDING_PAYMENT = 0L;
    private static final Long ORDER_STATUS_PENDING_DELIVERY = 1L;
    private static final Long ORDER_STATUS_DELIVERED = 2L;
    private static final Long ORDER_STATUS_COMPLETED = 3L;
    private static final Long ORDER_STATUS_CANCELLED = 4L;

    private static final Integer DELIVERY_TYPE_HOME = 1;
    private static final Integer DELIVERY_TYPE_STORE = 2;

    private final Map<Long, String> logisticsCache = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderDTO createOrderDTO) {
        if (createOrderDTO.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (createOrderDTO.getCartIds() == null || createOrderDTO.getCartIds().isEmpty()) {
            throw new IllegalArgumentException("请选择要购买的商品");
        }

        if (createOrderDTO.getAddressId() == null) {
            throw new IllegalArgumentException("请选择收货地址");
        }

        if (createOrderDTO.getDeliveryType() == null) {
            throw new IllegalArgumentException("请选择配送方式");
        }

        if (createOrderDTO.getDeliveryType().equals(DELIVERY_TYPE_STORE) && createOrderDTO.getStoreId() == null) {
            throw new IllegalArgumentException("请选择门店");
        }

        if (createOrderDTO.getPayType() == null) {
            throw new IllegalArgumentException("请选择支付方式");
        }

        UserAddress address = userAddressMapper.selectUserAddressById(createOrderDTO.getAddressId());
        if (address == null) {
            throw new IllegalArgumentException("收货地址不存在");
        }

        if (!address.getUserId().equals(createOrderDTO.getUserId())) {
            throw new IllegalArgumentException("收货地址不属于当前用户");
        }

        List<Cart> cartItems = cartMapper.selectCartByIds(createOrderDTO.getCartIds());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("购物车中没有选中的商品");
        }

        List<OrderProduct> orderProducts = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Cart cart : cartItems) {
            Product product = productMapper.selectProductById(cart.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            if (product.getStatus() == null || product.getStatus() != 1) {
                throw new RuntimeException("商品已下架");
            }

            if (product.getStock() == null || product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductId(product.getId());
            orderProduct.setProductName(product.getName());
            orderProduct.setProductImage(product.getMainImage());
            orderProduct.setProductSpec(cart.getSpec());
            orderProduct.setPrice(product.getPrice());
            orderProduct.setQuantity(cart.getQuantity());
            orderProduct.setTotalPrice(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));

            orderProducts.add(orderProduct);
            totalPrice = totalPrice.add(orderProduct.getTotalPrice());

            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateProduct(product);
        }

        BigDecimal freightPrice = FREIGHT_PRICE;
        BigDecimal payPrice = totalPrice.add(freightPrice);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(createOrderDTO.getUserId());
        order.setTotalPrice(totalPrice);
        order.setPayPrice(payPrice);
        order.setFreightPrice(freightPrice);
        order.setPayType(createOrderDTO.getPayType());
        order.setStatus(ORDER_STATUS_PENDING_PAYMENT);
        order.setDeliveryType(createOrderDTO.getDeliveryType());
        order.setAddressId(createOrderDTO.getAddressId());
        order.setStoreId(createOrderDTO.getStoreId());
        order.setCreateTime(DateUtils.getNowDate());
        order.setUpdateTime(DateUtils.getNowDate());

        orderMapper.insertOrder(order);

        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrderId(order.getId());
            orderProduct.setCreateTime(DateUtils.getNowDate());
            orderProduct.setUpdateTime(DateUtils.getNowDate());
            orderProductMapper.insertOrderProduct(orderProduct);
        }

        if (createOrderDTO.getDeliveryType().equals(DELIVERY_TYPE_STORE) && createOrderDTO.getStoreId() != null) {
            Appointment appointment = new Appointment();
            appointment.setOrderId(order.getId());
            appointment.setStoreId(createOrderDTO.getStoreId());
            appointment.setUserId(createOrderDTO.getUserId());
            appointment.setStatus(0L);
            appointment.setCreateTime(DateUtils.getNowDate());
            appointment.setUpdateTime(DateUtils.getNowDate());
            appointmentMapper.insertAppointment(appointment);
            order.setAppointmentId(appointment.getId());
            orderMapper.updateOrder(order);
        }

        cartMapper.deleteCartBatch(createOrderDTO.getUserId(), createOrderDTO.getCartIds());

        return getOrderById(order.getId());
    }

    @Override
    public OrderVO getOrderById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectOrderById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        return convertToOrderVO(order);
    }

    @Override
    public OrderVO getOrderByOrderNo(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new IllegalArgumentException("订单号不能为空");
        }

        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        return convertToOrderVO(order);
    }

    @Override
    public List<OrderVO> getOrderListByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<Order> orderList = orderMapper.selectOrderListByUserId(userId);
        List<OrderVO> orderVOList = new ArrayList<>();

        for (Order order : orderList) {
            orderVOList.add(convertToOrderVO(order));
        }

        return orderVOList;
    }

    @Override
    public List<OrderVO> getOrderList(Long userId, Long status) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<Order> orderList = orderMapper.selectOrderListByUserId(userId);
        List<OrderVO> orderVOList = new ArrayList<>();

        for (Order order : orderList) {
            if (status == null || order.getStatus().equals(status)) {
                orderVOList.add(convertToOrderVO(order));
            }
        }

        return orderVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO payOrder(Long orderId, Integer payType, Boolean success) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        if (payType == null) {
            throw new IllegalArgumentException("支付方式不能为空");
        }

        if (success == null) {
            throw new IllegalArgumentException("支付结果不能为空");
        }

        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(ORDER_STATUS_PENDING_PAYMENT)) {
            throw new RuntimeException("订单状态不允许支付");
        }

        if (success) {
            String transactionId = generateTransactionId();
            order.setPayType(payType);
            order.setTransactionId(transactionId);
            order.setPayTime(DateUtils.getNowDate());
            order.setStatus(ORDER_STATUS_PENDING_DELIVERY);
            order.setUpdateTime(DateUtils.getNowDate());
            orderMapper.updateOrder(order);

            scheduler.schedule(() -> autoUpdateLogistics(orderId), 5, TimeUnit.SECONDS);

            scheduler.schedule(() -> autoConfirmOrder(orderId), 24, TimeUnit.HOURS);
        } else {
            throw new RuntimeException("支付失败");
        }

        return getOrderById(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO cancelOrder(Long orderId, String reason) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(ORDER_STATUS_PENDING_PAYMENT)) {
            throw new RuntimeException("只有待支付订单才能取消");
        }

        List<OrderProduct> orderProducts = orderProductMapper.selectOrderProductListByOrderId(orderId);
        for (OrderProduct orderProduct : orderProducts) {
            Product product = productMapper.selectProductById(orderProduct.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + orderProduct.getQuantity());
                product.setSales(product.getSales() - orderProduct.getQuantity());
                productMapper.updateProduct(product);
            }
        }

        order.setStatus(ORDER_STATUS_CANCELLED);
        order.setCloseTime(DateUtils.getNowDate());
        order.setUpdateTime(DateUtils.getNowDate());
        orderMapper.updateOrder(order);

        return getOrderById(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO confirmOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(ORDER_STATUS_DELIVERED)) {
            throw new RuntimeException("只有已发货订单才能确认收货");
        }

        order.setStatus(ORDER_STATUS_COMPLETED);
        order.setReceiveTime(DateUtils.getNowDate());
        order.setUpdateTime(DateUtils.getNowDate());
        orderMapper.updateOrder(order);

        return getOrderById(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO deliverOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getStatus().equals(ORDER_STATUS_PENDING_DELIVERY)) {
            throw new RuntimeException("订单状态不允许发货");
        }

        order.setStatus(ORDER_STATUS_DELIVERED);
        order.setDeliveryTime(DateUtils.getNowDate());
        order.setUpdateTime(DateUtils.getNowDate());
        orderMapper.updateOrder(order);

        return getOrderById(orderId);
    }

    @Override
    public LogisticsVO getLogistics(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        LogisticsVO logisticsVO = new LogisticsVO();
        logisticsVO.setOrderId(order.getId());
        logisticsVO.setOrderNo(order.getOrderNo());

        if (order.getStatus().equals(ORDER_STATUS_PENDING_PAYMENT)) {
            logisticsVO.setStatus("pending_payment");
            logisticsVO.setStatusText("待支付");
            logisticsVO.setDescription("订单待支付");
        } else if (order.getStatus().equals(ORDER_STATUS_PENDING_DELIVERY)) {
            logisticsVO.setStatus("pending_delivery");
            logisticsVO.setStatusText("待发货");
            logisticsVO.setDescription("订单待发货");
        } else if (order.getStatus().equals(ORDER_STATUS_DELIVERED)) {
            logisticsVO.setStatus("delivered");
            logisticsVO.setStatusText("已发货");
            logisticsVO.setDescription(logisticsCache.getOrDefault(orderId, "商品已发货，正在配送中"));
        } else if (order.getStatus().equals(ORDER_STATUS_COMPLETED)) {
            logisticsVO.setStatus("completed");
            logisticsVO.setStatusText("已送达");
            logisticsVO.setDescription("订单已完成");
        } else if (order.getStatus().equals(ORDER_STATUS_CANCELLED)) {
            logisticsVO.setStatus("cancelled");
            logisticsVO.setStatusText("已取消");
            logisticsVO.setDescription("订单已取消");
        }

        logisticsVO.setUpdateTime(order.getUpdateTime());

        return logisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO applyRefund(RefundApplyDTO refundApplyDTO) {
        if (refundApplyDTO.getOrderId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        if (refundApplyDTO.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (refundApplyDTO.getType() == null) {
            throw new IllegalArgumentException("退款/退货类型不能为空");
        }

        if (StringUtils.isEmpty(refundApplyDTO.getReason())) {
            throw new IllegalArgumentException("退款/退货原因不能为空");
        }

        if (refundApplyDTO.getAmount() == null || refundApplyDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额不能为空且必须大于0");
        }

        Order order = orderMapper.selectOrderById(refundApplyDTO.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(refundApplyDTO.getUserId())) {
            throw new RuntimeException("订单不属于当前用户");
        }

        if (order.getStatus().equals(ORDER_STATUS_CANCELLED)) {
            throw new RuntimeException("已取消订单不能申请退款");
        }

        RefundReturn refundReturn = new RefundReturn();
        refundReturn.setOrderId(refundApplyDTO.getOrderId());
        refundReturn.setUserId(refundApplyDTO.getUserId());
        refundReturn.setType(refundApplyDTO.getType());
        refundReturn.setReason(refundApplyDTO.getReason());
        refundReturn.setAmount(refundApplyDTO.getAmount());
        refundReturn.setEvidence(refundApplyDTO.getEvidence());
        refundReturn.setStatus(0L);
        refundReturn.setCreateTime(DateUtils.getNowDate());
        refundReturn.setUpdateTime(DateUtils.getNowDate());
        refundReturnMapper.insertRefundReturn(refundReturn);

        return getOrderById(refundApplyDTO.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoConfirmOrder(Long orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order != null && order.getStatus().equals(ORDER_STATUS_DELIVERED)) {
                order.setStatus(ORDER_STATUS_COMPLETED);
                order.setReceiveTime(DateUtils.getNowDate());
                order.setUpdateTime(DateUtils.getNowDate());
                orderMapper.updateOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoUpdateLogistics(Long orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order != null && order.getStatus().equals(ORDER_STATUS_PENDING_DELIVERY)) {
                order.setStatus(ORDER_STATUS_DELIVERED);
                order.setDeliveryTime(DateUtils.getNowDate());
                order.setUpdateTime(DateUtils.getNowDate());
                orderMapper.updateOrder(order);

                logisticsCache.put(orderId, "商品已送达");

                scheduler.schedule(() -> {
                    try {
                        Order updatedOrder = orderMapper.selectOrderById(orderId);
                        if (updatedOrder != null && updatedOrder.getStatus().equals(ORDER_STATUS_DELIVERED)) {
                            updatedOrder.setStatus(ORDER_STATUS_COMPLETED);
                            updatedOrder.setReceiveTime(DateUtils.getNowDate());
                            updatedOrder.setUpdateTime(DateUtils.getNowDate());
                            orderMapper.updateOrder(updatedOrder);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 24, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "ORD" + sdf.format(new Date()) + String.format("%04d", new Random().nextInt(10000));
    }

    private String generateTransactionId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "TXN" + sdf.format(new Date()) + String.format("%04d", new Random().nextInt(10000));
    }

    private OrderVO convertToOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        orderVO.setStatusText(getStatusText(order.getStatus()));
        orderVO.setDeliveryTypeText(getDeliveryTypeText(order.getDeliveryType()));

        if (order.getAddressId() != null) {
            UserAddress address = userAddressMapper.selectUserAddressById(order.getAddressId());
            if (address != null) {
                orderVO.setAddressInfo(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
            }
        }

        if (order.getStoreId() != null) {
            Store store = storeMapper.selectStoreById(order.getStoreId());
            if (store != null) {
                orderVO.setStoreInfo(store.getName() + " " + store.getAddress());
            }
        }

        List<OrderProduct> orderProducts = orderProductMapper.selectOrderProductListByOrderId(order.getId());
        List<OrderProductVO> orderProductVOList = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            OrderProductVO orderProductVO = new OrderProductVO();
            BeanUtils.copyProperties(orderProduct, orderProductVO);
            orderProductVOList.add(orderProductVO);
        }
        orderVO.setProducts(orderProductVOList);

        orderVO.setCanCancel(order.getStatus().equals(ORDER_STATUS_PENDING_PAYMENT));
        orderVO.setCanPay(order.getStatus().equals(ORDER_STATUS_PENDING_PAYMENT));
        orderVO.setCanConfirm(order.getStatus().equals(ORDER_STATUS_DELIVERED));
        orderVO.setCanRefund(!order.getStatus().equals(ORDER_STATUS_CANCELLED));

        return orderVO;
    }

    private String getStatusText(Long status) {
        if (status == null) {
            return "未知";
        }
        switch (status.intValue()) {
            case 0:
                return "待支付";
            case 1:
                return "待发货";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }

    private String getDeliveryTypeText(Integer deliveryType) {
        if (deliveryType == null) {
            return "未知";
        }
        switch (deliveryType) {
            case 1:
                return "送货上门";
            case 2:
                return "门店安装";
            default:
                return "未知";
        }
    }
}
