package com.auto.service.impl;

import com.auto.common.utils.DateUtils;
import com.auto.domain.OrderProduct;
import com.auto.domain.vo.OrderProductVO;
import com.auto.mapper.OrderProductMapper;
import com.auto.service.IOrderProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderProductServiceImpl implements IOrderProductService {

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Override
    public OrderProductVO getOrderProductById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("订单商品ID不能为空");
        }

        OrderProduct orderProduct = orderProductMapper.selectOrderProductById(id);
        if (orderProduct == null) {
            throw new RuntimeException("订单商品不存在");
        }

        return convertToOrderProductVO(orderProduct);
    }

    @Override
    public List<OrderProductVO> getOrderProductListByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        List<OrderProduct> orderProductList = orderProductMapper.selectOrderProductListByOrderId(orderId);
        List<OrderProductVO> orderProductVOList = new ArrayList<>();

        for (OrderProduct orderProduct : orderProductList) {
            orderProductVOList.add(convertToOrderProductVO(orderProduct));
        }

        return orderProductVOList;
    }

    @Override
    public List<OrderProductVO> getOrderProductList(OrderProduct orderProduct) {
        List<OrderProduct> orderProductList = orderProductMapper.selectOrderProductList(orderProduct);
        List<OrderProductVO> orderProductVOList = new ArrayList<>();

        for (OrderProduct op : orderProductList) {
            orderProductVOList.add(convertToOrderProductVO(op));
        }

        return orderProductVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrderProduct(OrderProduct orderProduct) {
        if (orderProduct == null) {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        if (orderProduct.getOrderId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        if (orderProduct.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        if (orderProduct.getQuantity() == null || orderProduct.getQuantity() <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }

        if (orderProduct.getPrice() == null || orderProduct.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("商品价格必须大于0");
        }

        if (orderProduct.getTotalPrice() == null) {
            orderProduct.setTotalPrice(orderProduct.getPrice().multiply(new java.math.BigDecimal(orderProduct.getQuantity())));
        }

        orderProduct.setCreateTime(DateUtils.getNowDate());
        orderProduct.setUpdateTime(DateUtils.getNowDate());

        return orderProductMapper.insertOrderProduct(orderProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrderProductBatch(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new IllegalArgumentException("订单商品列表不能为空");
        }

        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getOrderId() == null) {
                throw new IllegalArgumentException("订单ID不能为空");
            }

            if (orderProduct.getProductId() == null) {
                throw new IllegalArgumentException("商品ID不能为空");
            }

            if (orderProduct.getQuantity() == null || orderProduct.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }

            if (orderProduct.getPrice() == null || orderProduct.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("商品价格必须大于0");
            }

            if (orderProduct.getTotalPrice() == null) {
                orderProduct.setTotalPrice(orderProduct.getPrice().multiply(new java.math.BigDecimal(orderProduct.getQuantity())));
            }

            orderProduct.setCreateTime(DateUtils.getNowDate());
            orderProduct.setUpdateTime(DateUtils.getNowDate());
        }

        return orderProductMapper.insertOrderProductBatch(orderProducts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrderProduct(OrderProduct orderProduct) {
        if (orderProduct == null) {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        if (orderProduct.getId() == null) {
            throw new IllegalArgumentException("订单商品ID不能为空");
        }

        OrderProduct existingOrderProduct = orderProductMapper.selectOrderProductById(orderProduct.getId());
        if (existingOrderProduct == null) {
            throw new RuntimeException("订单商品不存在");
        }

        orderProduct.setUpdateTime(DateUtils.getNowDate());

        return orderProductMapper.updateOrderProduct(orderProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrderProductById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("订单商品ID不能为空");
        }

        OrderProduct orderProduct = orderProductMapper.selectOrderProductById(id);
        if (orderProduct == null) {
            throw new RuntimeException("订单商品不存在");
        }

        return orderProductMapper.deleteOrderProductById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrderProductByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("订单商品ID列表不能为空");
        }

        return orderProductMapper.deleteOrderProductByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrderProductByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }

        return orderProductMapper.deleteOrderProductByOrderId(orderId);
    }

    private OrderProductVO convertToOrderProductVO(OrderProduct orderProduct) {
        OrderProductVO orderProductVO = new OrderProductVO();
        BeanUtils.copyProperties(orderProduct, orderProductVO);
        return orderProductVO;
    }
}
