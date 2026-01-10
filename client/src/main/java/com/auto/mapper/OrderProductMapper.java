package com.auto.mapper;

import com.auto.domain.OrderProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderProductMapper {
    OrderProduct selectOrderProductById(Long id);

    List<OrderProduct> selectOrderProductListByOrderId(Long orderId);

    List<OrderProduct> selectOrderProductList(OrderProduct orderProduct);

    int insertOrderProduct(OrderProduct orderProduct);

    int insertOrderProductBatch(List<OrderProduct> orderProducts);

    int updateOrderProduct(OrderProduct orderProduct);

    int deleteOrderProductById(Long id);

    int deleteOrderProductByIds(Long[] ids);

    int deleteOrderProductByOrderId(Long orderId);
}
