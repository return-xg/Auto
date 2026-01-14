package com.auto.service;

import com.auto.domain.dto.CreateOrderDTO;
import com.auto.domain.dto.RefundApplyDTO;
import com.auto.domain.vo.LogisticsVO;
import com.auto.domain.vo.OrderVO;

import java.util.List;

public interface IOrderService {
    OrderVO createOrder(CreateOrderDTO createOrderDTO);

    OrderVO getOrderById(Long id);

    OrderVO getOrderByOrderNo(String orderNo);

    List<OrderVO> getOrderListByUserId(Long userId);

    List<OrderVO> getOrderList(Long userId, Long status);

    OrderVO payOrder(Long orderId, Integer payType, Boolean success);

    OrderVO cancelOrder(Long orderId, String reason);

    OrderVO confirmOrder(Long orderId);

    OrderVO deliverOrder(Long orderId);

    LogisticsVO getLogistics(Long orderId);

    OrderVO applyRefund(RefundApplyDTO refundApplyDTO);

    OrderVO cancelRefund(Long orderId);

    void autoConfirmOrder(Long orderId);

    void autoUpdateLogistics(Long orderId);

    int deleteOrder(Long orderId);

    int deleteOrderBatch(Long[] orderIds);
}
