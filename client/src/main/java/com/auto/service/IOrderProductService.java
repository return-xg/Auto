package com.auto.service;

import com.auto.domain.OrderProduct;
import com.auto.domain.vo.OrderProductVO;

import java.util.List;

public interface IOrderProductService {
    OrderProductVO getOrderProductById(Long id);

    List<OrderProductVO> getOrderProductListByOrderId(Long orderId);

    List<OrderProductVO> getOrderProductList(OrderProduct orderProduct);

    int insertOrderProduct(OrderProduct orderProduct);

    int insertOrderProductBatch(List<OrderProduct> orderProducts);

    int updateOrderProduct(OrderProduct orderProduct);

    int deleteOrderProductById(Long id);

    int deleteOrderProductByIds(Long[] ids);

    int deleteOrderProductByOrderId(Long orderId);
}
