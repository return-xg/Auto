package com.auto.service.impl;

import com.auto.domain.Cart;
import com.auto.domain.Product;
import com.auto.domain.vo.CartItemVO;
import com.auto.domain.vo.CartVO;
import com.auto.mapper.CartMapper;
import com.auto.service.ICartService;
import com.auto.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 购物车服务实现类
 *
 * @author auto
 * @date 2026-01-08
 */
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private IProductService productService;

    // 商品数量上限
    private static final Long MAX_QUANTITY = 100L;

    // 商品数量下限
    private static final Long MIN_QUANTITY = 1L;

    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @return 购物车项
     */
    @Override
    public Cart addProductToCart(Long userId, Long productId, Long quantity) {
        return addProductToCart(userId, productId, quantity, null);
    }

    /**
     * 添加商品到购物车（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @param spec      规格参数（JSON格式）
     * @return 购物车项
     */
    @Override
    public Cart addProductToCart(Long userId, Long productId, Long quantity, String spec) {
        // 参数验证
        if (userId == null || productId == null || quantity == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 获取商品信息
        Product product = productService.selectProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查商品是否上架
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }

        // 检查库存
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查数量限制
        quantity = validateQuantity(quantity, product.getStock());

        // 查询购物车中是否已存在该商品（相同商品和规格）
        Cart existingCartItem = cartMapper.selectCartByUserIdAndProductIdAndSpec(userId, productId, spec);

        if (existingCartItem != null) {
            // 已存在相同规格，更新数量
            Long newQuantity = existingCartItem.getQuantity() + quantity;
            // 检查新数量是否超过库存
            newQuantity = Math.min(newQuantity, product.getStock());
            // 更新购物车
            cartMapper.updateCartQuantity(userId, productId, newQuantity);
            existingCartItem.setQuantity(newQuantity);
            return existingCartItem;
        }

        // 不存在或规格不同，创建新购物车项
        Cart cartItem = new Cart();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setSelected(1); // 默认选中
        cartItem.setSpec(spec); // 设置规格参数
        cartMapper.insertCart(cartItem);
        return cartItem;
    }

    /**
     * 查看购物车列表
     *
     * @param userId 用户ID
     * @return 购物车视图对象
     */
    @Override
    public CartVO getCartList(Long userId) {
        // 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询用户购物车列表
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            return new CartVO();
        }

        // 转换为CartItemVO列表
        List<CartItemVO> cartItemVOList = new ArrayList<>();
        for (Cart cart : cartList) {
            // 获取商品信息
            Product product = productService.selectProductById(cart.getProductId());
            if (product != null) {
                CartItemVO cartItemVO = new CartItemVO();
                cartItemVO.setCartId(cart.getId());
                cartItemVO.setProductId(product.getId());
                cartItemVO.setProductName(product.getName());
                cartItemVO.setProductImage(product.getMainImage());
                cartItemVO.setPrice(product.getPrice());
                cartItemVO.setQuantity(cart.getQuantity());
                cartItemVO.setSelected(cart.getSelected());
                cartItemVO.setSpec(cart.getSpec());
                cartItemVO.setCreateTime(cart.getCreateTime());
                cartItemVO.setUpdateTime(cart.getUpdateTime());
                cartItemVO.calculateSubtotal(); // 计算小计
                cartItemVOList.add(cartItemVO);
            }
        }

        // 构建CartVO并计算汇总信息
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);
        cartVO.calculate();
        return cartVO;
    }

    /**
     * 修改购物车商品数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @return 购物车项
     */
    @Override
    public Cart updateProductQuantity(Long userId, Long productId, Long quantity) {
        return updateProductQuantity(userId, productId, quantity, null);
    }

    /**
     * 修改购物车商品数量（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @param spec      规格参数
     * @return 购物车项
     */
    @Override
    public Cart updateProductQuantity(Long userId, Long productId, Long quantity, String spec) {
        // 参数验证
        if (userId == null || productId == null || quantity == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 获取商品信息
        Product product = productService.selectProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查库存
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查数量限制
        quantity = validateQuantity(quantity, product.getStock());

        // 更新购物车数量
        int result = cartMapper.updateCartQuantity(userId, productId, quantity);
        if (result > 0) {
            // 查询更新后的购物车项
            return cartMapper.selectCartByUserIdAndProductIdAndSpec(userId, productId, spec);
        } else {
            throw new RuntimeException("更新失败，购物车项不存在");
        }
    }

    /**
     * 根据购物车ID修改购物车商品数量
     *
     * @param cartId   购物车ID
     * @param quantity  新数量
     * @return 购物车项
     */
    @Override
    public Cart updateProductQuantityById(Long cartId, Long quantity) {
        // 参数验证
        if (cartId == null || quantity == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 查询购物车项
        Cart cartItem = cartMapper.selectCartByIds(Collections.singletonList(cartId)).get(0);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }

        // 获取商品信息
        Product product = productService.selectProductById(cartItem.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查库存
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查数量限制
        quantity = validateQuantity(quantity, product.getStock());

        // 更新购物车数量
        int result = cartMapper.updateCartQuantityById(cartId, quantity);
        if (result > 0) {
            // 查询更新后的购物车项
            return cartMapper.selectCartByIds(Collections.singletonList(cartId)).get(0);
        } else {
            throw new RuntimeException("更新失败");
        }
    }

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 删除结果
     */
    @Override
    public boolean deleteProductFromCart(Long userId, Long productId) {
        return deleteProductFromCart(userId, productId, null);
    }

    /**
     * 删除购物车商品（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param spec      规格参数
     * @return 删除结果
     */
    @Override
    public boolean deleteProductFromCart(Long userId, Long productId, String spec) {
        // 参数验证
        if (userId == null || productId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 检查购物车项是否存在
        Cart cartItem = cartMapper.selectCartByUserIdAndProductIdAndSpec(userId, productId, spec);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }

        // 删除购物车项
        int result = cartMapper.deleteCart(userId, productId);
        return result > 0;
    }

    /**
     * 根据购物车ID删除购物车商品
     *
     * @param cartId 购物车ID
     * @return 删除结果
     */
    @Override
    public boolean deleteProductFromCartById(Long cartId) {
        // 参数验证
        if (cartId == null) {
            throw new IllegalArgumentException("购物车ID不能为空");
        }

        // 删除购物车项
        int result = cartMapper.deleteCartById(cartId);
        return result > 0;
    }

    /**
     * 批量删除购物车商品
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 删除结果
     */
    @Override
    public boolean deleteProductsFromCart(Long userId, List<Long> productIds) {
        // 参数验证
        if (userId == null || productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 批量删除购物车项
        int result = cartMapper.deleteCartBatch(userId, productIds);
        return result > 0;
    }

    /**
     * 删除选中的购物车商品
     *
     * @param userId  用户ID
     * @param cartIds 购物车ID列表
     * @return 删除结果
     */
    @Override
    public boolean deleteSelectedProductsFromCart(Long userId, List<Long> cartIds) {
        // 参数验证
        if (userId == null || cartIds == null || cartIds.isEmpty()) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 批量删除选中的购物车项
        int result = cartMapper.deleteCartBatch(userId, cartIds);
        return result > 0;
    }

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 清空结果
     */
    @Override
    public boolean clearCart(Long userId) {
        // 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 清空购物车
        int result = cartMapper.clearCart(userId);
        return result >= 0;
    }

    /**
     * 切换商品选中状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @return 购物车项
     */
    @Override
    public Cart toggleProductSelected(Long userId, Long productId, Integer selected) {
        return toggleProductSelected(userId, productId, selected, null);
    }

    /**
     * 根据购物车ID切换商品选中状态
     *
     * @param cartId   购物车ID
     * @param selected  选中状态
     * @return 切换结果
     */
    @Override
    public boolean toggleProductSelectedById(Long cartId, Integer selected) {
        // 参数验证
        if (cartId == null || selected == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 更新选中状态
        int result = cartMapper.updateCartSelectedById(cartId, selected);
        return result > 0;
    }

    /**
     * 切换商品选中状态（带规格参数）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @param spec      规格参数
     * @return 购物车项
     */
    @Override
    public Cart toggleProductSelected(Long userId, Long productId, Integer selected, String spec) {
        // 参数验证
        if (userId == null || productId == null || selected == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 检查购物车项是否存在
        Cart cartItem = cartMapper.selectCartByUserIdAndProductIdAndSpec(userId, productId, spec);
        if (cartItem == null) {
            throw new RuntimeException("购物车项不存在");
        }

        // 更新选中状态
        int result = cartMapper.updateCartSelected(userId, productId, selected);
        if (result > 0) {
            cartItem.setSelected(selected);
            return cartItem;
        } else {
            throw new RuntimeException("更新失败");
        }
    }

    /**
     * 全选/取消全选购物车商品
     *
     * @param userId   用户ID
     * @param selected 选中状态
     * @return 操作结果
     */
    @Override
    public boolean toggleAllProductsSelected(Long userId, Integer selected) {
        // 参数验证
        if (userId == null || selected == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 更新所有购物车项的选中状态
        int result = cartMapper.updateAllCartSelected(userId, selected);
        return result >= 0;
    }

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    @Override
    public Long getCartProductCount(Long userId) {
        // 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询购物车列表
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        if (cartList == null || cartList.isEmpty()) {
            return 0L;
        }

        // 计算总数量
        long totalCount = 0;
        for (Cart cart : cartList) {
            if (cart.getQuantity() != null) {
                totalCount += cart.getQuantity();
            }
        }
        return totalCount;
    }

    /**
     * 验证商品数量是否在合理范围内
     *
     * @param quantity  请求数量
     * @param stock     商品库存
     * @return 验证后的数量
     */
    private Long validateQuantity(Long quantity, Long stock) {
        // 数量不能小于下限
        quantity = Math.max(quantity, MIN_QUANTITY);
        // 数量不能大于库存
        quantity = Math.min(quantity, stock);
        // 数量不能大于上限
        quantity = Math.min(quantity, MAX_QUANTITY);
        return quantity;
    }
}
