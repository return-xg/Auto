package com.auto.mapper;

import com.auto.domain.Cart;
import com.auto.domain.vo.CartItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 *
 * @author auto
 * @date 2026-01-08
 */
public interface CartMapper {
    /**
     * 查询用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> selectCartListByUserId(Long userId);

    /**
     * 根据用户ID和商品ID查询购物车项
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    Cart selectCartByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 插入购物车项
     *
     * @param cart 购物车项
     * @return 插入行数
     */
    int insertCart(Cart cart);

    /**
     * 更新购物车项数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @return 更新行数
     */
    int updateCartQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Long quantity);

    /**
     * 更新购物车项选中状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @return 更新行数
     */
    int updateCartSelected(@Param("userId") Long userId, @Param("productId") Long productId, @Param("selected") Integer selected);

    /**
     * 更新用户所有购物车项的选中状态
     *
     * @param userId   用户ID
     * @param selected 选中状态
     * @return 更新行数
     */
    int updateAllCartSelected(@Param("userId") Long userId, @Param("selected") Integer selected);

    /**
     * 删除购物车项
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 删除行数
     */
    int deleteCart(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 批量删除购物车项
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 删除行数
     */
    int deleteCartBatch(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

    /**
     * 清空用户购物车
     *
     * @param userId 用户ID
     * @return 删除行数
     */
    int clearCart(Long userId);
}
