<template>
  <div class="checkout-container">
    <el-card>
      <div slot="header" class="card-header">
        <span>确认订单</span>
      </div>

      <div v-loading="loading">
        <el-steps :active="currentStep" finish-status="success" align-center>
          <el-step title="确认订单" description="核对商品和配送信息"></el-step>
          <el-step title="选择配送" description="选择配送方式和地址"></el-step>
          <el-step title="提交订单" description="确认并提交订单"></el-step>
        </el-steps>

        <el-divider></el-divider>

        <div class="checkout-content">
          <div class="section">
            <h3><i class="el-icon-goods"></i> 商品清单</h3>
            <el-table :data="checkoutItems" border>
              <el-table-column label="商品信息" width="400">
                <template slot-scope="scope">
                  <div class="product-info">
                    <el-image
                      :src="scope.row.mainImage || '/static/images/default-product.png'"
                      fit="cover"
                      style="width: 60px; height: 60px; border-radius: 4px;"
                    />
                    <div class="product-detail">
                      <div class="product-name">{{ scope.row.productName }}</div>
                      <div class="product-spec">{{ scope.row.spec || '默认规格' }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="单价" :formatter="formatPrice" />
              <el-table-column prop="quantity" label="数量" />
              <el-table-column label="小计">
                <template slot-scope="scope">
                  <span class="subtotal">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <el-divider></el-divider>

          <div class="section">
            <h3><i class="el-icon-truck"></i> 配送方式</h3>
            <el-radio-group v-model="deliveryType" class="delivery-options">
              <el-radio label="home" border class="delivery-option">
                <div class="option-content">
                  <div class="option-title">
                    <i class="el-icon-truck"></i>
                    <span>送货上门</span>
                  </div>
                  <div class="option-desc">快递配送，预计3-5天送达</div>
                </div>
              </el-radio>
              <el-radio label="store" border class="delivery-option">
                <div class="option-content">
                  <div class="option-title">
                    <i class="el-icon-house"></i>
                    <span>门店安装</span>
                  </div>
                  <div class="option-desc">到店安装，专业技师服务</div>
                </div>
              </el-radio>
            </el-radio-group>

            <div v-if="deliveryType === 'home'" class="address-section">
              <div class="address-header">
                <span>收货地址</span>
                <el-button type="text" @click="showAddressDialog">新增地址</el-button>
              </div>
              <el-radio-group v-model="selectedAddressId" class="address-list">
                <el-radio
                  v-for="address in addressList"
                  :key="address.id"
                  :label="address.id"
                  border
                  class="address-item"
                >
                  <div class="address-content">
                    <div class="address-info">
                      <span class="name">{{ address.name }}</span>
                      <span class="phone">{{ address.phone }}</span>
                      <el-tag v-if="address.isDefault" type="success" size="mini">默认</el-tag>
                    </div>
                    <div class="address-detail">
                      {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
                    </div>
                  </div>
                </el-radio>
              </el-radio-group>
            </div>

            <div v-if="deliveryType === 'store'" class="store-section">
              <div class="store-header">
                <span>选择门店</span>
              </div>
              <el-select v-model="selectedStoreId" placeholder="请选择门店" style="width: 100%;">
                <el-option
                  v-for="store in storeList"
                  :key="store.id"
                  :label="store.name"
                  :value="store.id"
                >
                  <div class="store-option">
                    <div class="store-name">{{ store.name }}</div>
                    <div class="store-address">{{ store.address }}</div>
                  </div>
                </el-option>
              </el-select>
            </div>
          </div>

          <el-divider></el-divider>

          <div class="section">
            <h3><i class="el-icon-s-finance"></i> 订单金额</h3>
            <div class="price-summary">
              <div class="price-item">
                <span class="label">商品总额：</span>
                <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
              </div>
              <div class="price-item">
                <span class="label">运费：</span>
                <span class="value">¥{{ freight.toFixed(2) }}</span>
              </div>
              <div class="price-item">
                <span class="label">优惠金额：</span>
                <span class="value">-¥{{ discountAmount.toFixed(2) }}</span>
              </div>
              <div class="price-item total">
                <span class="label">订单总价：</span>
                <span class="value">¥{{ finalAmount.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="checkout-footer">
          <el-button @click="goBack">返回购物车</el-button>
          <el-button type="primary" size="large" @click="handleSubmitOrder" :loading="submitting">
            提交订单
          </el-button>
        </div>
      </div>
    </el-card>

    <address-dialog
      :visible="addressDialogVisible"
      @close="addressDialogVisible = false"
      @success="handleAddressSuccess"
    />
  </div>
</template>

<script>
import { listAddress } from '@/api/address/address'
import { listStore } from '@/api/store/store'
import { createOrder } from '@/api/order/order'
import AddressDialog from '@/views/address/address/AddressDialog.vue'

export default {
  name: 'Checkout',
  components: {
    AddressDialog
  },
  data() {
    return {
      loading: false,
      submitting: false,
      currentStep: 0,
      checkoutItems: [],
      deliveryType: 'home',
      addressList: [],
      selectedAddressId: null,
      storeList: [],
      selectedStoreId: null,
      addressDialogVisible: false,
      freight: 0,
      discountAmount: 0
    }
  },
  computed: {
    totalAmount() {
      return this.checkoutItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
    },
    finalAmount() {
      return this.totalAmount + this.freight - this.discountAmount
    }
  },
  mounted() {
    this.loadCheckoutItems()
    this.loadAddressList()
    this.loadStoreList()
  },
  methods: {
    loadCheckoutItems() {
      const items = localStorage.getItem('checkoutItems')
      if (items) {
        this.checkoutItems = JSON.parse(items)
      } else {
        this.$message.warning('请先选择要结算的商品')
        this.$router.push('/cart/cart')
      }
    },
    loadAddressList() {
      const userId = this.$store.getters.id
      listAddress({ pageNum: 1, pageSize: 100, userId }).then(response => {
        if (response.code === 200) {
          this.addressList = response.rows || []
          const defaultAddress = this.addressList.find(addr => addr.isDefault)
          if (defaultAddress) {
            this.selectedAddressId = defaultAddress.id
          }
        }
      }).catch(error => {
        console.error('获取地址列表失败:', error)
      })
    },
    loadStoreList() {
      listStore({ pageNum: 1, pageSize: 100, status: 1 }).then(response => {
        if (response.code === 200) {
          this.storeList = response.rows || []
          if (this.storeList.length > 0) {
            this.selectedStoreId = this.storeList[0].id
          }
        }
      }).catch(error => {
        console.error('获取门店列表失败:', error)
      })
    },
    showAddressDialog() {
      this.addressDialogVisible = true
    },
    handleAddressSuccess() {
      this.loadAddressList()
    },
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    handleSubmitOrder() {
      if (this.deliveryType === 'home' && !this.selectedAddressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      if (this.deliveryType === 'store' && !this.selectedStoreId) {
        this.$message.warning('请选择安装门店')
        return
      }

      this.submitting = true
      const userId = this.$store.getters.id
      const orderData = {
        userId,
        items: this.checkoutItems.map(item => ({
          productId: item.productId,
          productName: item.productName,
          price: item.price,
          quantity: item.quantity,
          spec: item.spec
        })),
        deliveryType: this.deliveryType,
        totalAmount: this.totalAmount,
        freight: this.freight,
        discountAmount: this.discountAmount
      }

      if (this.deliveryType === 'home') {
        const address = this.addressList.find(addr => addr.id === this.selectedAddressId)
        orderData.receiverName = address.name
        orderData.receiverPhone = address.phone
        orderData.receiverAddress = `${address.province}${address.city}${address.district}${address.detail}`
      } else {
        const store = this.storeList.find(s => s.id === this.selectedStoreId)
        orderData.storeId = store.id
        orderData.storeName = store.name
      }

      createOrder(orderData).then(response => {
        this.submitting = false
        if (response.code === 200) {
          this.$message.success('订单创建成功')
          localStorage.removeItem('checkoutItems')
          this.$router.push('/order/order')
        } else {
          this.$message.error(response.msg || '订单创建失败')
        }
      }).catch(error => {
        this.submitting = false
        this.$message.error('订单创建失败')
      })
    },
    goBack() {
      this.$router.push('/cart/cart')
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

.checkout-content {
  margin-top: 30px;
}

.section {
  margin-bottom: 30px;

  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 20px;

    i {
      margin-right: 10px;
      color: #409EFF;
    }
  }
}

.product-info {
  display: flex;
  gap: 10px;
  align-items: center;

  .product-detail {
    flex: 1;

    .product-name {
      font-size: 14px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
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
}

.delivery-options {
  display: flex;
  flex-direction: column;
  gap: 15px;

  .delivery-option {
    width: 100%;

    ::v-deep .el-radio__label {
      width: 100%;
    }

    .option-content {
      .option-title {
        display: flex;
        align-items: center;
        gap: 10px;
        font-weight: bold;
        font-size: 14px;
        margin-bottom: 5px;
      }

      .option-desc {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.address-section {
  margin-top: 20px;

  .address-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    font-weight: bold;
    font-size: 14px;
  }

  .address-list {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .address-item {
      width: 100%;

      ::v-deep .el-radio__label {
        width: 100%;
      }

      .address-content {
        .address-info {
          display: flex;
          align-items: center;
          gap: 15px;
          margin-bottom: 8px;

          .name {
            font-weight: bold;
            font-size: 14px;
          }

          .phone {
            color: #606266;
            font-size: 14px;
          }
        }

        .address-detail {
          color: #909399;
          font-size: 13px;
          line-height: 1.6;
        }
      }
    }
  }
}

.store-section {
  margin-top: 20px;

  .store-header {
    font-weight: bold;
    font-size: 14px;
    margin-bottom: 15px;
  }

  .store-option {
    .store-name {
      font-weight: bold;
      font-size: 14px;
    }

    .store-address {
      font-size: 12px;
      color: #909399;
    }
  }
}

.price-summary {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;

  .price-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;

    &.total {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #e4e7ed;
      font-weight: bold;
      font-size: 16px;

      .value {
        color: #f56c6c;
        font-size: 24px;
      }
    }

    .label {
      color: #909399;
    }

    .value {
      color: #303133;
    }
  }
}

.checkout-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
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
