package com.auto.service;

import com.auto.domain.RefundReturn;

public interface IRefundReturnService {
    RefundReturn selectRefundReturnById(Long id);

    RefundReturn selectRefundReturnByOrderId(Long orderId);

    int updateRefundReturn(RefundReturn refundReturn);
}
