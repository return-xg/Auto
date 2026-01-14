package com.auto.mapper;

import com.auto.domain.RefundReturn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RefundReturnMapper {
    RefundReturn selectRefundReturnById(Long id);

    RefundReturn selectRefundReturnByOrderId(Long orderId);

    List<RefundReturn> selectRefundReturnListByOrderId(Long orderId);

    List<RefundReturn> selectRefundReturnListByUserId(Long userId);

    List<RefundReturn> selectRefundReturnList(RefundReturn refundReturn);

    int insertRefundReturn(RefundReturn refundReturn);

    int updateRefundReturn(RefundReturn refundReturn);

    int deleteRefundReturnById(Long id);

    int deleteRefundReturnByIds(Long[] ids);
}
