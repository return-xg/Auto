package com.auto.mapper;

import com.auto.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order selectOrderById(Long id);

    Order selectOrderByOrderNo(String orderNo);

    List<Order> selectOrderListByUserId(Long userId);

    List<Order> selectOrderList(Order order);

    int insertOrder(Order order);

    int updateOrder(Order order);

    int deleteOrderById(Long id);

    int deleteOrderByIds(Long[] ids);

    int updateOrderStatus(@Param("id") Long id, @Param("status") Long status);

    int updateOrderPayInfo(@Param("id") Long id, @Param("payType") Integer payType, @Param("transactionId") String transactionId, @Param("payTime") java.util.Date payTime);

    int updateOrderDelivery(@Param("id") Long id, @Param("deliveryTime") java.util.Date deliveryTime);

    int updateOrderReceive(@Param("id") Long id, @Param("receiveTime") java.util.Date receiveTime);

    int updateOrderClose(@Param("id") Long id, @Param("closeTime") java.util.Date closeTime);
}
