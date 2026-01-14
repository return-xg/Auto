<template>
  <el-dialog
    title="订单详情"
    :visible.sync="dialogVisible"
    width="70%"
    :before-close="handleClose"
    append-to-body
  >
    <div v-loading="loading" class="order-detail">
      <el-steps :active="getOrderStep()" finish-status="success" align-center>
        <el-step title="待支付" description="等待买家付款"></el-step>
        <el-step title="待发货" description="商家准备商品"></el-step>
        <el-step title="已发货" description="商品配送中"></el-step>
        <el-step title="已完成" description="订单完成"></el-step>
      </el-steps>

      <el-divider></el-divider>

      <div class="detail-section">
        <h3>订单信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(orderDetail.status)">{{ getStatusText(orderDetail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ orderDetail.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="配送方式">
            {{ orderDetail.deliveryTypeText || getDeliveryTypeText(orderDetail.deliveryType) }}
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" v-if="isHomeDelivery(orderDetail.deliveryType)">
            {{ getFullAddress() }}
          </el-descriptions-item>
          <el-descriptions-item label="安装门店" v-if="!isHomeDelivery(orderDetail.deliveryType)">
            {{ orderDetail.storeInfo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="收货人" v-if="isHomeDelivery(orderDetail.deliveryType) && getReceiverName()">
            {{ getReceiverName() }}
          </el-descriptions-item>
          <el-descriptions-item label="联系方式" v-if="isHomeDelivery(orderDetail.deliveryType) && getReceiverPhone()">
            {{ getReceiverPhone() }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider></el-divider>

      <div class="detail-section">
        <h3>商品清单</h3>
        <el-table :data="orderDetail.products || orderDetail.items" border>
          <el-table-column label="商品信息" width="400">
            <template slot-scope="scope">
              <div class="product-info">
                <el-image
                  :src="getImageUrl(scope.row.productImage || scope.row.mainImage)"
                  fit="cover"
                  style="width: 60px; height: 60px; border-radius: 4px;"
                >
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <div class="product-detail">
                  <div class="product-name">{{ scope.row.productName }}</div>
                  <div class="product-spec">{{ getProductSpecText(scope.row) }}</div>
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

      <div class="detail-section">
        <h3>费用明细</h3>
        <div class="price-summary">
          <div class="price-item">
            <span class="label">商品总额：</span>
            <span class="value">¥{{ Number(orderDetail.totalPrice || orderDetail.totalAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="price-item">
            <span class="label">运费：</span>
            <span class="value">¥{{ Number(orderDetail.freightPrice || orderDetail.freight || 0).toFixed(2) }}</span>
          </div>
          <div class="price-item" v-if="orderDetail.discountAmount || orderDetail.couponAmount">
            <span class="label">优惠金额：</span>
            <span class="value">-¥{{ Number(orderDetail.discountAmount || orderDetail.couponAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="price-item total">
            <span class="label">订单总价：</span>
            <span class="value">¥{{ Number(orderDetail.payPrice || orderDetail.totalAmount || 0).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
      <el-button
        v-if="orderDetail.status === 2 || orderDetail.status === 'shipped'"
        type="warning"
        @click="handleConfirm"
      >
        确认收货
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { confirmOrder, getOrder } from '@/api/order/order'

export default {
  name: 'OrderDetailDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    order: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      dialogVisible: this.visible,
      loading: false,
      orderDetail: {}
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.order.id) {
        this.loadOrderDetail()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    loadOrderDetail() {
      this.loading = true
      getOrder(this.order.id).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.orderDetail = response.data || {}
        } else {
          this.$message.error('获取订单详情失败')
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取订单详情失败')
      })
    },
    getFullAddress() {
      const addressInfo = this.orderDetail.addressInfo
      if (!addressInfo) return '-'
      
      const parts = []
      if (addressInfo.province) parts.push(addressInfo.province)
      if (addressInfo.city) parts.push(addressInfo.city)
      if (addressInfo.district) parts.push(addressInfo.district)
      if (addressInfo.detail) parts.push(addressInfo.detail)
      
      return parts.join(' ') || '-'
    },
    getReceiverName() {
      const addressInfo = this.orderDetail.addressInfo
      if (!addressInfo) return ''
      return addressInfo.consignee || ''
    },
    getReceiverPhone() {
      const addressInfo = this.orderDetail.addressInfo
      if (!addressInfo) return ''
      return addressInfo.phone || ''
    },
    getImageUrl(imageUrl) {
      if (!imageUrl) return '/static/images/default-product.png'
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl
      }
      return process.env.VUE_APP_BASE_API + imageUrl
    },
    getProductSpecText(item) {
      if (item.productSpec) {
        try {
          const spec = typeof item.productSpec === 'string' ? JSON.parse(item.productSpec) : item.productSpec
          return Object.entries(spec).map(([key, value]) => `${key}：${value}`).join(' / ')
        } catch (e) {
          return item.productSpec
        }
      }
      return item.spec || '默认规格'
    },
    isHomeDelivery(deliveryType) {
      return deliveryType === 1
    },
    getDeliveryTypeText(deliveryType) {
      const textMap = {
        1: '送货上门',
        2: '门店安装'
      }
      return textMap[deliveryType] || '未知'
    },
    getOrderStep() {
      const stepMap = {
        0: 0,
        1: 1,
        2: 2,
        3: 3,
        4: -1,
        pending_payment: 0,
        pending_shipment: 1,
        shipped: 2,
        completed: 3,
        cancelled: -1
      }
      return stepMap[this.orderDetail.status] || 0
    },
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'danger',
        pending_payment: 'warning',
        pending_shipment: 'info',
        shipped: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = {
        0: '待支付',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消',
        pending_payment: '待支付',
        pending_shipment: '待发货',
        shipped: '已发货',
        completed: '已完成',
        cancelled: '已取消'
      }
      return textMap[status] || '未知'
    },
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    handleConfirm() {
      this.$confirm('确认已收到商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return confirmOrder(this.orderDetail.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('确认收货成功')
          this.$emit('confirm-receipt', this.orderDetail)
        } else {
          this.$message.error(response.msg || '操作失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      })
    },
    handleClose() {
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.order-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 30px;

  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 15px;
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

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 20px;
  }
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
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
</style>
