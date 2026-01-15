package com.auto.service.impl;

import com.auto.domain.RefundReturn;
import com.auto.mapper.RefundReturnMapper;
import com.auto.service.IRefundReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundReturnServiceImpl implements IRefundReturnService {

    @Autowired
    private RefundReturnMapper refundReturnMapper;

    @Override
    public RefundReturn selectRefundReturnById(Long id) {
        return refundReturnMapper.selectRefundReturnById(id);
    }

    @Override
    public RefundReturn selectRefundReturnByOrderId(Long orderId) {
        return refundReturnMapper.selectRefundReturnByOrderId(orderId);
    }

    @Override
    public int updateRefundReturn(RefundReturn refundReturn) {
        return refundReturnMapper.updateRefundReturn(refundReturn);
    }
}
