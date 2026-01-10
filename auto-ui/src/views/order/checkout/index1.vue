<template>
  <div class="checkout-container">
    <el-card v-loading="loading">
      <div slot="header" class="card-header">
        <span>确认订单</span>
      </div>

      <div v-if="checkoutData">
        <div class="section">
          <div class="section-header">
            <h3 class="section-title">收货地址</h3>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="showAddAddressDialog">添加收货地址</el-button>
          </div>
          <div v-if="addressList.length > 0">
            <div
              v-for="address in addressList"
              :key="address.id"
              class="address-item"
              :class="{ active: selectedAddressId === address.id }"
              @click="selectAddress(address.id)"
            >
              <div class="address-info">
                <div class="address-name">
                  <span class="name">{{ address.receiverName || address.consignee }}</span>
                  <span class="phone">{{ address.receiverPhone || address.phone }}</span>
                  <el-tag v-if="address.isDefault === 1" type="success" size="mini" style="margin-left: 10px;">默认</el-tag>
                </div>
                <div class="address-detail">{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress || address.detail }}</div>
              </div>
              <div class="address-check">
                <i v-if="selectedAddressId === address.id" class="el-icon-success"></i>
              </div>
            </div>
          </div>
          <div v-else class="empty-address">
            <p>暂无收货地址</p>
            <el-button type="primary" size="small" @click="showAddAddressDialog">添加收货地址</el-button>
          </div>
        </div>

        <div class="section">
          <h3 class="section-title">配送方式</h3>
          <el-radio-group v-model="deliveryType" @change="handleDeliveryTypeChange">
            <el-radio :label="1">快递配送</el-radio>
            <el-radio :label="2">门店自提</el-radio>
          </el-radio-group>
          <div v-if="deliveryType === 2" class="store-select">
            <el-select v-model="storeId" placeholder="请选择门店" style="width: 300px; margin-top: 10px;">
              <el-option
                v-for="store in storeList"
                :key="store.id"
                :label="store.name"
                :value="store.id"
              />
            </el-select>
          </div>
        </div>

        <div class="section">
          <h3 class="section-title">商品清单</h3>
          <el-table :data="checkoutData.selectedItems" border stripe>
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
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column label="小计" width="120">
              <template slot-scope="scope">
                <span class="subtotal">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="section">
          <h3 class="section-title">支付方式</h3>
          <div class="payment-methods">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="payment-method-item"
              :class="{ active: selectedPaymentMethod === method.value }"
              @click="selectPaymentMethod(method.value)"
            >
              <div class="payment-icon">
                <i :class="method.icon"></i>
              </div>
              <div class="payment-info">
                <div class="payment-name">{{ method.name }}</div>
                <div class="payment-desc">{{ method.desc }}</div>
              </div>
              <div class="payment-check">
                <i v-if="selectedPaymentMethod === method.value" class="el-icon-success"></i>
              </div>
            </div>
          </div>
        </div>

        <div class="section">
          <h3 class="section-title">订单备注</h3>
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            placeholder="请输入订单备注（选填）"
            maxlength="200"
            show-word-limit
          />
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span>商品件数：</span>
            <span class="summary-value">{{ checkoutData.selectedCount }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品总额：</span>
            <span class="summary-value">¥{{ checkoutData.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span class="summary-value">¥{{ freight.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应付金额：</span>
            <span class="summary-value total-price">¥{{ (checkoutData.totalAmount + freight).toFixed(2) }}</span>
          </div>
        </div>

        <div class="submit-section">
          <el-button type="primary" size="large" @click="submitOrder" :disabled="!canSubmit">
            提交订单
          </el-button>
        </div>
      </div>

      <div v-else class="empty-checkout">
        <i class="el-icon-warning"></i>
        <p>订单信息已失效</p>
        <el-button type="primary" @click="goToCart">返回购物车</el-button>
      </div>
    </el-card>

    <el-dialog
      title="确认支付"
      :visible.sync="paymentDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="payment-confirm">
        <div class="payment-info">
          <div class="payment-label">支付方式：</div>
          <div class="payment-value">
            <i :class="getPaymentIcon(selectedPaymentMethod)"></i>
            <span>{{ getPaymentName(selectedPaymentMethod) }}</span>
          </div>
        </div>
        <div class="payment-amount">
          <span>支付金额：</span>
          <span class="amount">¥{{ (checkoutData.totalAmount + freight).toFixed(2) }}</span>
        </div>
        <div class="payment-tips">
          <i class="el-icon-info"></i>
          <span>点击确定后将跳转至支付页面，请确保支付成功</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPayment">确定支付</el-button>
      </span>
    </el-dialog>

    <address-dialog
      :visible.sync="addressDialogVisible"
      @success="handleAddressSuccess"
    />
  </div>
</template>

<script>
import { createOrder, payOrder } from '@/api/order/order'
import { listAddress } from '@/api/address/address'
import { listStore } from '@/api/store/store'
import AddressDialog from '@/views/address/address/AddressDialog.vue'

export default {
  name: 'OrderCheckout',
  components: {
    AddressDialog
  },
  data() {
    return {
      loading: false,
      checkoutData: null,
      addressList: [],
      storeList: [],
      selectedAddressId: null,
      deliveryType: 1,
      storeId: null,
      remark: '',
      freight: 0,
      paymentDialogVisible: false,
      addressDialogVisible: false,
      selectedPaymentMethod: '',
      paymentMethods: [
        {
          value: 'wechat',
          name: '微信支付',
          desc: '推荐使用微信支付',
          icon: 'el-icon-chat-dot-round'
        },
        {
          value: 'alipay',
          name: '支付宝',
          desc: '安全便捷的支付方式',
          icon: 'el-icon-wallet'
        }
      ]
    }
  },
  computed: {
    canSubmit() {
      return this.selectedAddressId && 
             this.selectedPaymentMethod &&
             (this.deliveryType === 1 || this.storeId) &&
             this.checkoutData &&
             this.checkoutData.selectedItems.length > 0
    }
  },
  mounted() {
    this.loadCheckoutData()
    this.loadAddressList()
    this.loadStoreList()
  },
  methods: {
    loadCheckoutData() {
      const checkoutItems = localStorage.getItem('checkoutItems')
      if (checkoutItems) {
        this.checkoutData = JSON.parse(checkoutItems)
      } else {
        this.$message.warning('订单信息已失效')
        this.goToCart()
      }
    },
    loadAddressList() {
      const userId = this.$store.getters.id
      listAddress({ userId }).then(response => {
        if (response.code === 200) {
          this.addressList = response.rows || []
          const defaultAddress = this.addressList.find(addr => addr.isDefault === 1)
          if (defaultAddress) {
            this.selectedAddressId = defaultAddress.id
          } else if (this.addressList.length > 0 && !this.selectedAddressId) {
            this.selectedAddressId = this.addressList[0].id
          }
        }
      }).catch(error => {
        console.error('获取收货地址失败:', error)
      })
    },
    loadStoreList() {
      listStore({}).then(response => {
        if (response.code === 200) {
          this.storeList = response.data || []
        }
      }).catch(error => {
        console.error('获取门店列表失败:', error)
      })
    },
    selectAddress(addressId) {
      this.selectedAddressId = addressId
    },
    handleDeliveryTypeChange(val) {
      if (val === 1) {
        this.storeId = null
        this.freight = 10
      } else {
        this.freight = 0
      }
    },
    getPaymentIcon(method) {
      const icons = {
        wechat: 'el-icon-chat-dot-round',
        alipay: 'el-icon-wallet'
      }
      return icons[method] || 'el-icon-wallet'
    },
    getPaymentName(method) {
      const names = {
        wechat: '微信支付',
        alipay: '支付宝'
      }
      return names[method] || '未知支付方式'
    },
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    selectPaymentMethod(method) {
      this.selectedPaymentMethod = method
    },
    submitOrder() {
      if (!this.selectedAddressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      if (this.deliveryType === 2 && !this.storeId) {
        this.$message.warning('请选择门店')
        return
      }
      if (!this.selectedPaymentMethod) {
        this.$message.warning('请选择支付方式')
        return
      }
      
      this.paymentDialogVisible = true
    },
    confirmPayment() {
      const userId = this.$store.getters.id
      const payType = this.selectedPaymentMethod === 'wechat' ? 1 : 2
      
      const orderData = {
        userId: userId,
        productIds: this.checkoutData.selectedItems.map(item => item.productId),
        addressId: this.selectedAddressId,
        deliveryType: this.deliveryType,
        storeId: this.storeId,
        payType: payType,
        remark: this.remark
      }
      
      this.loading = true
      this.paymentDialogVisible = false
      
      createOrder(orderData).then(response => {
        if (response.code === 200) {
          const orderId = response.data.id
          this.$message.success('订单创建成功')
          
          const payData = {
            orderId: orderId,
            payType: payType,
            success: true
          }
          
          return payOrder(orderId, payData)
        } else {
          this.loading = false
          this.$message.error(response.msg || '订单创建失败')
        }
      }).then(response => {
        this.loading = false
        if (response && response.code === 200) {
          this.$message.success('支付成功')
          localStorage.removeItem('checkoutItems')
          this.$router.push('/order/detail/' + response.data.id)
        } else {
          this.$message.error('支付失败')
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('操作失败')
        console.error('订单创建或支付失败:', error)
      })
    },
    goToAddAddress() {
      this.$router.push('/user/address')
    },
    goToCart() {
      this.$router.push('/cart/cart')
    },
    showAddAddressDialog() {
      this.addressDialogVisible = true
    },
    handleAddressSuccess(newAddress) {
      this.loadAddressList()
      if (newAddress && newAddress.id) {
        this.selectedAddressId = newAddress.id
      }
    }
  }
}
</script>

<style scoped lang="scss">
.checkout-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e4e7ed;

  &:last-child {
    border-bottom: none;
  }
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .section-title {
    margin-bottom: 0;
  }
}

.address-item {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409EFF;
    background-color: #f0f7ff;
  }

  &.active {
    border-color: #409EFF;
    background-color: #ecf5ff;
  }

  .address-info {
    flex: 1;

    .address-name {
      font-size: 15px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 12px;
      display: flex;
      align-items: center;

      .name {
        margin-right: 15px;
        font-size: 16px;
      }

      .phone {
        color: #606266;
        font-size: 15px;
        font-weight: normal;
      }
    }

    .address-detail {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      word-break: break-all;
    }
  }

  .address-check {
    display: flex;
    align-items: center;
    padding-left: 15px;

    i {
      font-size: 24px;
      color: #67C23A;
    }
  }
}

.empty-address {
  text-align: center;
  padding: 40px 0;
  color: #909399;

  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.store-select {
  margin-top: 15px;
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

.order-summary {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;

  .summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 14px;
    color: #606266;

    &.total {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #e4e7ed;
      font-size: 16px;
      font-weight: bold;

      .summary-value.total-price {
        color: #f56c6c;
        font-size: 24px;
      }
    }

    .summary-value {
      font-weight: bold;
      color: #303133;
    }
  }
}

.submit-section {
  margin-top: 30px;
  text-align: right;

  .el-button {
    min-width: 200px;
  }
}

.empty-checkout {
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

.payment-methods {
  margin-bottom: 20px;
}

.payment-method-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409EFF;
    background-color: #f0f7ff;
  }

  &.active {
    border-color: #409EFF;
    background-color: #ecf5ff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }

  .payment-icon {
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    border-radius: 50%;
    margin-right: 15px;

    i {
      font-size: 24px;
      color: #409EFF;
    }
  }

  .payment-info {
    flex: 1;

    .payment-name {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
    }

    .payment-desc {
      font-size: 13px;
      color: #909399;
    }
  }

  .payment-check {
    i {
      font-size: 24px;
      color: #67C23A;
    }
  }
}

.payment-confirm {
  padding: 20px 0;

  .payment-info {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 8px;

    .payment-label {
      font-size: 14px;
      color: #606266;
      margin-right: 20px;
    }

    .payment-value {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: bold;
      color: #303133;

      i {
        font-size: 24px;
        color: #409EFF;
        margin-right: 10px;
      }

      span {
        font-weight: bold;
      }
    }
  }

  .payment-amount {
    text-align: center;
    padding: 30px 0;
    font-size: 18px;
    color: #606266;

    .amount {
      color: #f56c6c;
      font-size: 32px;
      font-weight: bold;
      margin-left: 10px;
    }
  }

  .payment-tips {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 15px;
    background-color: #fff7e6;
    border: 1px solid #ffd591;
    border-radius: 4px;
    font-size: 14px;
    color: #fa8c16;

    i {
      margin-right: 8px;
      font-size: 16px;
    }
  }
}
</style>
