<template>
  <div class="cart-container">
    <el-card>
      <div slot="header" class="card-header">
        <span>购物车</span>
        <span class="cart-count">共 {{ cartList.length }} 件商品</span>
      </div>

      <div v-loading="loading">
        <div v-if="cartList.length > 0">
          <el-table
            ref="table"
            :data="cartList"
            border
            stripe
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column label="商品信息" width="400">
              <template slot-scope="scope">
                <div class="product-info">
                  <el-image
                    :src="scope.row.productImage || '/static/images/default-product.png'"
                    fit="cover"
                    style="width: 80px; height: 80px; border-radius: 4px;"
                  >
                    <div slot="error" class="image-error">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                  <div class="product-detail">
                    <div class="product-name" :title="scope.row.productName">{{ scope.row.productName }}</div>
                    <div class="product-spec">数量: {{ scope.row.quantity }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="120" :formatter="formatPrice" />
            <el-table-column label="数量" width="200">
              <template slot-scope="scope">
                <el-input-number
                  v-model.number="scope.row.quantity"
                  :min="1"
                  :max="99"
                  size="small"
                  controls-position="right"
                  @change="handleQuantityChange(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template slot-scope="scope">
                <span class="subtotal">¥{{ Number(scope.row.subtotal).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button type="danger" size="small" @click="handleDelete(scope.row)">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="cart-footer">
            <div class="footer-left">
              <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
              <el-button type="text" @click="handleClearCart" style="margin-left: 20px;">
                <i class="el-icon-delete"></i> 清空购物车
              </el-button>
            </div>
            <div class="footer-right">
              <div class="selected-info">
                已选择 <span class="highlight">{{ selectedCount }}</span> 件商品，
                合计：<span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
              </div>
              <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedCount === 0">
                结算
              </el-button>
            </div>
          </div>
        </div>

        <div v-else class="empty-cart">
          <i class="el-icon-shopping-cart-2"></i>
          <p>购物车空空如也</p>
          <el-button type="primary" @click="goToShop">去逛逛</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listCart, updateQuantity, deleteProduct, clearCart, updateSelected, updateAllSelected } from '@/api/cart/cart'

export default {
  name: 'CartIndex',
  data() {
    return {
      loading: false,
      cartList: [],
      selectedItems: [],
      selectAll: false
    }
  },
  computed: {
    selectedCount() {
      return this.selectedItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
    }
  },
  mounted() {
    this.loadCartList()
  },
  methods: {
    loadCartList() {
      this.loading = true
      const userId = this.$store.getters.id
      listCart({ userId }).then(response => {
        this.loading = false
        if (response.code === 200) {
          const cartData = response.data || {}
          this.cartList = cartData.cartItems || []
          this.selectedItems = this.cartList.filter(item => item.selected === 1)
          this.selectAll = cartData.isAllSelected || false
          this.saveCartToLocalStorage()
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取购物车失败')
      })
    },
    handleSelectionChange(selection) {
      this.selectedItems = selection
      this.selectAll = this.cartList.length > 0 && selection.length === this.cartList.length
      this.updateSelectedStatus()
    },
    handleSelectAll(val) {
      this.$refs.table?.clearSelection()
      if (val) {
        this.cartList.forEach(row => {
          this.$refs.table?.toggleRowSelection(row, true)
        })
      }
      const userId = this.$store.getters.id
      updateAllSelected({ userId, selected: val ? 1 : 0 }).then(response => {
        if (response.code === 200) {
          this.updateSelectedStatus()
        }
      }).catch(error => {
        console.error('全选/取消全选失败:', error)
      })
    },
    updateSelectedStatus() {
      const userId = this.$store.getters.id
      const selectedIds = this.selectedItems.map(item => item.cartId)
      this.cartList.forEach(item => {
        const isSelected = selectedIds.includes(item.cartId)
        if ((item.selected === 1) !== isSelected) {
          item.selected = isSelected ? 1 : 0
          updateSelected({ userId, productId: item.productId, selected: item.selected }).catch(error => {
            console.error('更新选中状态失败:', error)
          })
        }
      })
      this.saveCartToLocalStorage()
    },
    handleQuantityChange(row) {
      const userId = this.$store.getters.id
      updateQuantity({ userId, productId: row.productId, quantity: row.quantity }).then(response => {
        if (response.code === 200) {
          this.$message.success('数量已更新')
          this.saveCartToLocalStorage()
        } else {
          this.$message.error(response.msg || '更新失败')
          this.loadCartList()
        }
      }).catch(error => {
        this.$message.error('更新失败')
        this.loadCartList()
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const userId = this.$store.getters.id
        return deleteProduct({ userId, productId: row.productId })
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('删除成功')
          this.loadCartList()
        } else {
          this.$message.error(response.msg || '删除失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      })
    },
    handleClearCart() {
      if (this.cartList.length === 0) {
        this.$message.warning('购物车已经是空的')
        return
      }
      this.$confirm('确定要清空购物车吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const userId = this.$store.getters.id
        return clearCart({ userId })
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('购物车已清空')
          this.loadCartList()
        } else {
          this.$message.error(response.msg || '清空失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('清空失败')
        }
      })
    },
    handleCheckout() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请选择要结算的商品')
        return
      }
      localStorage.setItem('checkoutItems', JSON.stringify(this.selectedItems))
      this.$router.push('/order/checkout')
    },
    goToShop() {
      this.$router.push('/product/userIndex')
    },
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    saveCartToLocalStorage() {
      localStorage.setItem('cartData', JSON.stringify({
        cartList: this.cartList,
        selectedItems: this.selectedItems
      }))
    }
  }
}
</script>

<style scoped lang="scss">
.cart-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;

  .cart-count {
    font-size: 14px;
    color: #909399;
    font-weight: normal;
  }
}

.product-info {
  display: flex;
  gap: 15px;
  align-items: center;

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 32px;
  }

  .product-detail {
    flex: 1;

    .product-name {
      font-size: 14px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .product-spec {
      font-size: 12px;
      color: #909399;
    }
  }
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;

  .footer-left {
    display: flex;
    align-items: center;
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .selected-info {
      font-size: 14px;
      color: #606266;

      .highlight {
        color: #409EFF;
        font-weight: bold;
        font-size: 16px;
        margin: 0 5px;
      }

      .total-price {
        color: #f56c6c;
        font-weight: bold;
        font-size: 24px;
        margin-left: 10px;
      }
    }
  }
}

.empty-cart {
  text-align: center;
  padding: 80px 0;
  color: #909399;

  i {
    font-size: 80px;
    margin-bottom: 20px;
    display: block;
  }

  p {
    font-size: 18px;
    margin-bottom: 30px;
  }
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}
</style>
