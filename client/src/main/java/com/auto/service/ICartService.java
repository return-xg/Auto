package com.auto.service;

import com.auto.domain.Cart;
import com.auto.domain.vo.CartVO;
import com.auto.domain.vo.CartItemVO;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author auto
 * @date 2026-01-08
 */
public interface ICartService {
    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @return 购物车项
     */
    Cart addProductToCart(Long userId, Long productId, Long quantity);

    /**
     * 查看购物车列表
     *
     * @param userId 用户ID
     * @return 购物车视图对象
     */
    CartVO getCartList(Long userId);

    /**
     * 修改购物车商品数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @return 购物车项
     */
    Cart updateProductQuantity(Long userId, Long productId, Long quantity);

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 删除结果
     */
    boolean deleteProductFromCart(Long userId, Long productId);

    /**
     * 批量删除购物车商品
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 删除结果
     */
    boolean deleteProductsFromCart(Long userId, List<Long> productIds);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 清空结果
     */
    boolean clearCart(Long userId);

    /**
     * 切换商品选中状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @return 购物车项
     */
    Cart toggleProductSelected(Long userId, Long productId, Integer selected);

    /**
     * 全选/取消全选购物车商品
     *
     * @param userId   用户ID
     * @param selected 选中状态
     * @return 操作结果
     */
    boolean toggleAllProductsSelected(Long userId, Integer selected);

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    Long getCartProductCount(Long userId);
}
