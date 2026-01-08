package com.auto.web.controller.client;

import com.auto.domain.Cart;
import com.auto.domain.vo.CartVO;
import com.auto.service.ICartService;
import com.auto.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车Controller
 *
 * @author auto
 * @date 2026-01-08
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  商品数量
     * @return 结果
     */
    @PostMapping("/add")
    public AjaxResult addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Long quantity) {
        try {
            Cart cart = cartService.addProductToCart(userId, productId, quantity);
            return AjaxResult.success("添加成功", cart);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 查看购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    @GetMapping("/list")
    public AjaxResult getCartList(@RequestParam Long userId) {
        try {
            CartVO cartVO = cartService.getCartList(userId);
            return AjaxResult.success(cartVO);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改购物车商品数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量
     * @return 结果
     */
    @PutMapping("/update/quantity")
    public AjaxResult updateQuantity(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Long quantity) {
        try {
            Cart cart = cartService.updateProductQuantity(userId, productId, quantity);
            return AjaxResult.success("修改成功", cart);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 结果
     */
    @DeleteMapping("/delete")
    public AjaxResult deleteProduct(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            boolean result = cartService.deleteProductFromCart(userId, productId);
            return result ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 批量删除购物车商品
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 结果
     */
    @DeleteMapping("/delete/batch")
    public AjaxResult deleteProducts(@RequestParam Long userId, @RequestBody List<Long> productIds) {
        try {
            boolean result = cartService.deleteProductsFromCart(userId, productIds);
            return result ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 清空购物车
     *
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/clear")
    public AjaxResult clearCart(@RequestParam Long userId) {
        try {
            boolean result = cartService.clearCart(userId);
            return result ? AjaxResult.success("清空成功") : AjaxResult.error("清空失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 切换商品选中状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  选中状态
     * @return 结果
     */
    @PutMapping("/update/selected")
    public AjaxResult updateSelected(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer selected) {
        try {
            Cart cart = cartService.toggleProductSelected(userId, productId, selected);
            return AjaxResult.success("修改成功", cart);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 全选/取消全选购物车商品
     *
     * @param userId   用户ID
     * @param selected 选中状态
     * @return 结果
     */
    @PutMapping("/update/selected/all")
    public AjaxResult updateAllSelected(@RequestParam Long userId, @RequestParam Integer selected) {
        try {
            boolean result = cartService.toggleAllProductsSelected(userId, selected);
            return result ? AjaxResult.success("操作成功") : AjaxResult.error("操作失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    @GetMapping("/count")
    public AjaxResult getCartCount(@RequestParam Long userId) {
        try {
            Long count = cartService.getCartProductCount(userId);
            return AjaxResult.success(count);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
