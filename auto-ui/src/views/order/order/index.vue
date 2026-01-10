<template>
  <div class="order-container">
    <el-card>
      <div slot="header" class="card-header">
        <span>我的订单</span>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待支付" name="0"></el-tab-pane>
        <el-tab-pane label="待发货" name="1"></el-tab-pane>
        <el-tab-pane label="已发货" name="2"></el-tab-pane>
        <el-tab-pane label="已完成" name="3"></el-tab-pane>
        <el-tab-pane label="已取消" name="4"></el-tab-pane>
      </el-tabs>

      <div v-loading="loading">
        <div v-if="orderList.length > 0">
          <div v-for="order in orderList" :key="order.id" class="order-item">
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ order.createTime }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
              </div>
            </div>

            <div class="order-content">
              <div class="product-list">
                <div v-for="item in order.products" :key="item.id" class="product-item">
                  <el-image
                    :src="item.productImage || '/static/images/default-product.png'"
                    fit="cover"
                    style="width: 80px; height: 80px; border-radius: 4px;"
                  >
                    <div slot="error" class="image-error">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                  <div class="product-detail">
                    <div class="product-name">{{ item.productName }}</div>
                    <div class="product-spec">{{ item.productSpec || '默认规格' }}</div>
                    <div class="product-price">
                      <span class="price">¥{{ Number(item.price).toFixed(2) }}</span>
                      <span class="quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="order-summary">
                <div class="summary-item">
                  <span class="label">商品总额：</span>
                  <span class="value">¥{{ Number(order.totalPrice).toFixed(2) }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">运费：</span>
                  <span class="value">¥{{ Number(order.freightPrice).toFixed(2) }}</span>
                </div>
                <div class="summary-item total">
                  <span class="label">订单总价：</span>
                  <span class="value">¥{{ Number(order.payPrice).toFixed(2) }}</span>
                </div>
              </div>
            </div>

            <div class="order-footer">
              <div class="delivery-info">
                <span v-if="order.deliveryType === 1">
                  <i class="el-icon-truck"></i> 配送方式：快递配送
                </span>
                <span v-else>
                  <i class="el-icon-house"></i> 配送方式：门店自提
                  <span v-if="order.storeInfo">（{{ order.storeInfo }}）</span>
                </span>
              </div>
              <div class="order-actions">
                <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
                <el-button
                  v-if="order.status === 0"
                  type="primary"
                  size="small"
                  @click="handlePay(order)"
                >
                  立即支付
                </el-button>
                <el-button
                  v-if="order.status === 0"
                  type="danger"
                  size="small"
                  @click="handleCancel(order)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.status === 2"
                  type="success"
                  size="small"
                  @click="viewLogistics(order)"
                >
                  查看物流
                </el-button>
                <el-button
                  v-if="order.status === 2"
                  type="warning"
                  size="small"
                  @click="handleConfirm(order)"
                >
                  确认收货
                </el-button>
                <el-button
                  v-if="order.status === 3"
                  type="info"
                  size="small"
                  @click="handleRefund(order)"
                >
                  申请退款
                </el-button>
              </div>
            </div>
          </div>

          <div class="pagination-container">
            <el-pagination
              :current-page.sync="pagination.current"
              :page-size.sync="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>

        <div v-else class="empty-orders">
          <i class="el-icon-document"></i>
          <p>暂无订单</p>
          <el-button type="primary" @click="goToShop">去购物</el-button>
        </div>
      </div>
    </el-card>

    <order-detail-dialog
      :visible="detailDialogVisible"
      :order="currentOrder"
      @close="detailDialogVisible = false"
      @confirm-receipt="handleConfirm"
    />

    <logistics-dialog
      :visible="logisticsDialogVisible"
      :order="currentOrder"
      @close="logisticsDialogVisible = false"
    />

    <refund-dialog
      :visible="refundDialogVisible"
      :order="currentOrder"
      @close="refundDialogVisible = false"
      @submit="handleRefundSubmit"
    />

    <pay-dialog
      :visible="payDialogVisible"
      :order="currentOrder"
      @close="payDialogVisible = false"
      @success="handlePaySuccess"
      @fail="handlePayFail"
    />
  </div>
</template>

<script>
import { listOrder, cancelOrder, confirmOrder } from '@/api/order/order'
import OrderDetailDialog from './OrderDetailDialog.vue'
import LogisticsDialog from './LogisticsDialog.vue'
import RefundDialog from './RefundDialog.vue'
import PayDialog from './PayDialog.vue'

export default {
  name: 'OrderIndex',
  components: {
    OrderDetailDialog,
    LogisticsDialog,
    RefundDialog,
    PayDialog
  },
  data() {
    return {
      loading: false,
      activeTab: 'all',
      orderList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      detailDialogVisible: false,
      logisticsDialogVisible: false,
      refundDialogVisible: false,
      payDialogVisible: false,
      currentOrder: {}
    }
  },
  mounted() {
    this.loadOrderList()
  },
  methods: {
    loadOrderList() {
      this.loading = true
      const userId = this.$store.getters.id
      const queryParams = {
        userId
      }
      
      if (this.activeTab !== 'all') {
        queryParams.status = this.activeTab
      }

      listOrder(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.orderList = response.data || []
          this.pagination.total = this.orderList.length
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取订单列表失败')
      })
    },
    handleTabClick() {
      this.pagination.current = 1
      this.loadOrderList()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadOrderList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadOrderList()
    },
    getStatusType(status) {
      const typeMap = {
        0: 'warning',
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'danger'
      }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = {
        0: '待支付',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消'
      }
      return textMap[status] || '未知'
    },
    viewOrderDetail(order) {
      this.currentOrder = order
      this.detailDialogVisible = true
    },
    handlePay(order) {
      this.currentOrder = order
      this.payDialogVisible = true
    },
    handlePaySuccess() {
      this.payDialogVisible = false
      this.$message.success('支付成功')
      this.$router.push('/product/userIndex')
    },
    handlePayFail() {
      this.payDialogVisible = false
      this.$message.error('支付失败')
    },
    handleCancel(order) {
      this.$confirm('确定要取消该订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cancelOrder(order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('订单已取消')
          this.loadOrderList()
        } else {
          this.$message.error(response.msg || '取消失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('取消失败')
        }
      })
    },
    viewLogistics(order) {
      this.currentOrder = order
      this.logisticsDialogVisible = true
    },
    handleConfirm(order) {
      this.$confirm('确认已收到商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return confirmOrder(order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('确认收货成功')
          this.loadOrderList()
        } else {
          this.$message.error(response.msg || '操作失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      })
    },
    handleRefund(order) {
      this.currentOrder = order
      this.refundDialogVisible = true
    },
    handleRefundSubmit(data) {
      this.$message.success('退款申请已提交')
      this.refundDialogVisible = false
    },
    goToShop() {
      this.$router.push('/product/userIndex')
    }
  }
}
</script>

<style scoped lang="scss">
.order-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.order-item {
  background: white;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #e4e7ed;

  .order-info {
    display: flex;
    gap: 20px;
    font-size: 14px;
    color: #606266;

    .order-no {
      font-weight: bold;
      color: #303133;
    }
  }
}

.order-content {
  display: flex;
  padding: 20px;
  gap: 30px;

  .product-list {
    flex: 1;

    .product-item {
      display: flex;
      gap: 15px;
      margin-bottom: 15px;
      padding-bottom: 15px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        margin-bottom: 0;
        padding-bottom: 0;
        border-bottom: none;
      }

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
        }

        .product-spec {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
        }

        .product-price {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .price {
            color: #f56c6c;
            font-weight: bold;
            font-size: 16px;
          }

          .quantity {
            color: #909399;
            font-size: 14px;
          }
        }
      }
    }
  }

  .order-summary {
    width: 250px;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .summary-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      font-size: 14px;

      &.total {
        margin-top: 10px;
        padding-top: 10px;
        border-top: 1px solid #e4e7ed;
        font-weight: bold;

        .value {
          color: #f56c6c;
          font-size: 18px;
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
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-top: 1px solid #e4e7ed;

  .delivery-info {
    font-size: 14px;
    color: #606266;

    i {
      margin-right: 5px;
    }
  }

  .order-actions {
    display: flex;
    gap: 10px;
  }
}

.empty-orders {
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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
