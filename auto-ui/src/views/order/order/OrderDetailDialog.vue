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
          <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="配送方式">
            {{ order.deliveryType === 'home' ? '送货上门' : '门店安装' }}
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" v-if="order.deliveryType === 'home'">
            {{ order.receiverName }} {{ order.receiverPhone }} {{ order.receiverAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="安装门店" v-else>
            {{ order.storeName || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider></el-divider>

      <div class="detail-section">
        <h3>商品清单</h3>
        <el-table :data="order.items" border>
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

      <div class="detail-section">
        <h3>费用明细</h3>
        <div class="price-summary">
          <div class="price-item">
            <span class="label">商品总额：</span>
            <span class="value">¥{{ Number(order.totalAmount).toFixed(2) }}</span>
          </div>
          <div class="price-item">
            <span class="label">运费：</span>
            <span class="value">¥{{ Number(order.freight).toFixed(2) }}</span>
          </div>
          <div class="price-item">
            <span class="label">优惠金额：</span>
            <span class="value">-¥{{ Number(order.discountAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="price-item total">
            <span class="label">订单总价：</span>
            <span class="value">¥{{ Number(order.totalAmount + order.freight - (order.discountAmount || 0)).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
      <el-button
        v-if="order.status === 'shipped'"
        type="warning"
        @click="handleConfirm"
      >
        确认收货
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { confirmOrder } from '@/api/order/order'

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
      loading: false
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    getOrderStep() {
      const stepMap = {
        pending_payment: 0,
        pending_shipment: 1,
        shipped: 2,
        completed: 3
      }
      return stepMap[this.order.status] || 0
    },
    getStatusType(status) {
      const typeMap = {
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
        return confirmOrder(this.order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('确认收货成功')
          this.$emit('confirm-receipt', this.order)
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
