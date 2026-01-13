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
     * 添加商品到购物车（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @param spec      规格参数（JSON格式）
     * @return 购物车项
     */
    Cart addProductToCart(Long userId, Long productId, Long quantity, String spec);

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
     * 修改购物车商品数量（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @param spec      规格参数
     * @return 购物车项
     */
    Cart updateProductQuantity(Long userId, Long productId, Long quantity, String spec);

    /**
     * 根据购物车ID修改购物车商品数量
     *
     * @param cartId   购物车ID
     * @param quantity  新数量
     * @return 购物车项
     */
    Cart updateProductQuantityById(Long cartId, Long quantity);

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 删除结果
     */
    boolean deleteProductFromCart(Long userId, Long productId);

    /**
     * 删除购物车商品（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param spec      规格参数
     * @return 删除结果
     */
    boolean deleteProductFromCart(Long userId, Long productId, String spec);

    /**
     * 根据购物车ID删除购物车商品
     *
     * @param cartId 购物车ID
     * @return 删除结果
     */
    boolean deleteProductFromCartById(Long cartId);

    /**
     * 批量删除购物车商品
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 删除结果
     */
    boolean deleteProductsFromCart(Long userId, List<Long> productIds);

    /**
     * 删除选中的购物车商品
     *
     * @param userId  用户ID
     * @param cartIds 购物车ID列表
     * @return 删除结果
     */
    boolean deleteSelectedProductsFromCart(Long userId, List<Long> cartIds);

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 删除结果
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
     * 根据购物车ID切换商品选中状态
     *
     * @param cartId   购物车ID
     * @param selected  选中状态
     * @return 切换结果
     */
    boolean toggleProductSelectedById(Long cartId, Integer selected);

    /**
     * 切换所有商品选中状态
     *
     * @param userId   用户ID
     * @param selected 选中状态
     * @return 切换结果
     */
    boolean toggleAllProductsSelected(Long userId, Integer selected);

    /**
     * 切换商品选中状态（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @param spec      规格参数
     * @return 购物车项
     */
    Cart toggleProductSelected(Long userId, Long productId, Integer selected, String spec);

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    Long getCartProductCount(Long userId);
}
