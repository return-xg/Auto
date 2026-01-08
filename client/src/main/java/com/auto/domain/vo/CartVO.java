package com.auto.domain.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车展示对象
 *
 * @author auto
 * @date 2026-01-08
 */
public class CartVO {
    /** 购物车商品列表 */
    private List<CartItemVO> cartItems;

    /** 选中商品数量 */
    private Long selectedCount;

    /** 总数量 */
    private Long totalCount;

    /** 选中商品总金额 */
    private BigDecimal selectedTotalAmount;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 是否全选 */
    private boolean isAllSelected;

    // 计算总金额和数量
    public void calculate() {
        if (cartItems == null || cartItems.isEmpty()) {
            this.selectedCount = 0L;
            this.totalCount = 0L;
            this.selectedTotalAmount = BigDecimal.ZERO;
            this.totalAmount = BigDecimal.ZERO;
            this.isAllSelected = false;
            return;
        }

        this.totalCount = 0L;
        this.selectedCount = 0L;
        this.totalAmount = BigDecimal.ZERO;
        this.selectedTotalAmount = BigDecimal.ZERO;
        this.isAllSelected = true;

        for (CartItemVO item : cartItems) {
            // 计算单项小计
            item.calculateSubtotal();
            
            // 累加总数量和总金额
            this.totalCount += item.getQuantity();
            this.totalAmount = this.totalAmount.add(item.getSubtotal());

            // 处理选中状态
            if (item.getSelected() != null && item.getSelected() == 1) {
                this.selectedCount += item.getQuantity();
                this.selectedTotalAmount = this.selectedTotalAmount.add(item.getSubtotal());
            } else {
                this.isAllSelected = false;
            }
        }
    }

    // getter and setter methods
    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
        calculate();
    }

    public Long getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(Long selectedCount) {
        this.selectedCount = selectedCount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getSelectedTotalAmount() {
        return selectedTotalAmount;
    }

    public void setSelectedTotalAmount(BigDecimal selectedTotalAmount) {
        this.selectedTotalAmount = selectedTotalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }
}
