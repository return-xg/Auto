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
        <el-tab-pane label="售后" name="refund"></el-tab-pane>
      </el-tabs>

      <div v-loading="loading">
        <div v-if="orderList.length > 0">
          <div class="batch-actions" v-if="selectedOrders.length > 0">
            <span>已选择 {{ selectedOrders.length }} 个订单</span>
            <el-button type="danger" size="small" @click="handleBatchDelete">
              <i class="el-icon-delete"></i> 批量删除
            </el-button>
            <el-button size="small" @click="clearSelection">取消选择</el-button>
          </div>

          <div v-for="order in orderList" :key="order.id" class="order-item">
            <div class="order-header">
              <div class="order-info">
                <el-checkbox v-model="order.selected" @change="handleSelectChange"></el-checkbox>
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatOrderTime(order.createTime) }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
                <el-tag v-if="order.refundStatus !== undefined && order.refundStatus !== null" :type="getRefundStatusType(order.refundStatus)" style="margin-left: 10px;">{{ getRefundStatusText(order.refundStatus) }}</el-tag>
              </div>
            </div>

            <div class="order-content">
              <div class="product-list">
                <div v-for="item in order.products" :key="item.id" class="product-item">
                  <div class="product-image-wrapper">
                    <el-image
                      :src="getImageUrl(item.productImage)"
                      fit="cover"
                      :preview-src-list="[getImageUrl(item.productImage)]"
                      class="product-image"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                        <span>暂无图片</span>
                      </div>
                      <div slot="placeholder" class="image-placeholder">
                        <i class="el-icon-loading"></i>
                      </div>
                    </el-image>
                  </div>
                  <div class="product-detail">
                    <div class="product-name">{{ item.productName }}</div>
                    <div class="product-spec">
                      <span v-if="item.productSpec" class="spec-tag" v-for="(value, key) in parseProductSpec(item.productSpec)" :key="key">
                        {{ key }}：{{ value }}
                      </span>
                      <span v-else class="spec-default">默认规格</span>
                    </div>
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
                  v-if="order.status === 2 && order.deliveryType === 1"
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
                  v-if="canApplyRefund(order)"
                  type="info"
                  size="small"
                  @click="handleRefund(order)"
                >
                  申请退款
                </el-button>
                <el-button
                  v-if="order.refundStatus === 0"
                  type="danger"
                  size="small"
                  @click="handleCancelRefund(order)"
                >
                  取消退款
                </el-button>
                <el-button
                  v-if="order.refundStatus === 3 || order.refundStatus === 4 || order.refundStatus === 5 || order.refundStatus === 6"
                  type="info"
                  size="small"
                  @click="viewRefundDetail(order)"
                >
                  退款详情
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  plain
                  @click="handleDelete(order)"
                >
                  删除
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
      @cancelRefund="handleRefundCancel"
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
import { listOrder, cancelOrder, confirmOrder, deleteOrder, deleteOrderBatch, cancelRefund } from '@/api/order/order'
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
  computed: {
    selectedOrders() {
      return this.orderList.filter(order => order.selected)
    }
  },
  mounted() {
    this.loadOrderList()
  },
  methods: {
    getImageUrl(imageUrl) {
      if (!imageUrl) return '/static/images/default-product.png'
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl
      }
      return process.env.VUE_APP_BASE_API + imageUrl
    },
    parseProductSpec(specStr) {
      if (!specStr) return {}
      try {
        if (typeof specStr === 'string') {
          return JSON.parse(specStr)
        }
        return specStr
      } catch (e) {
        return {}
      }
    },
    formatOrderTime(timeStr) {
      if (!timeStr) return '-'
      try {
        const date = new Date(timeStr)
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      } catch (e) {
        return timeStr
      }
    },
    loadOrderList() {
      this.loading = true
      const userId = this.$store.getters.id
      const queryParams = {
        userId
      }
      
      if (this.activeTab !== 'all' && this.activeTab !== 'refund') {
        queryParams.status = this.activeTab
      }

      listOrder(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          let orderList = response.data || []
          
          if (this.activeTab === 'refund') {
            orderList = orderList.filter(order => 
              order.refundStatus !== undefined && 
              order.refundStatus !== null
            )
          }
          
          this.orderList = orderList
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
    getRefundStatusType(refundStatus) {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger',
        3: 'primary',
        4: 'success',
        5: 'primary',
        6: 'success'
      }
      return typeMap[refundStatus] || 'info'
    },
    getRefundStatusText(refundStatus) {
      const textMap = {
        0: '待审核',
        1: '审核通过',
        2: '审核拒绝',
        3: '退款中',
        4: '退款成功',
        5: '退货中',
        6: '退货完成'
      }
      return textMap[refundStatus] || '未知'
    },
    canApplyRefund(order) {
      if (order.status !== 3) {
        return false
      }
      if (order.refundStatus === undefined || order.refundStatus === null) {
        return true
      }
      if (order.refundStatus === 0) {
        return false
      }
      if (order.refundStatus === 3 || order.refundStatus === 4 || order.refundStatus === 5 || order.refundStatus === 6) {
        return false
      }
      return true
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
      if (order.refundStatus === 0) {
        this.$message.warning('该订单已有退款申请，请等待管理员审核或取消当前申请')
        return
      }
      if (order.refundStatus === 3 || order.refundStatus === 4 || order.refundStatus === 5 || order.refundStatus === 6) {
        this.$message.warning('该订单退款流程正在进行中，无法再次申请')
        return
      }
      this.currentOrder = order
      this.refundDialogVisible = true
    },
    handleRefundSubmit(data) {
      this.$message.success('退款申请已提交')
      this.refundDialogVisible = false
      this.loadOrderList()
    },
    handleRefundCancel() {
      this.loadOrderList()
    },
    handleCancelRefund(order) {
      this.$confirm('确定要取消该退款申请吗？取消后订单将恢复为可申请退款状态。', '取消退款申请', {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }).then(() => {
        return cancelRefund(order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('退款申请已取消')
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
    viewRefundDetail(order) {
      this.$message.info('退款详情功能开发中')
    },
    handleSelectChange() {
    },
    clearSelection() {
      this.orderList.forEach(order => {
        order.selected = false
      })
    },
    handleDelete(order) {
      this.$confirm('确定要删除该订单吗？删除后无法恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteOrder(order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('删除成功')
          this.loadOrderList()
        } else {
          this.$message.error(response.msg || '删除失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      })
    },
    handleBatchDelete() {
      const selectedCount = this.selectedOrders.length
      const orderIds = this.selectedOrders.map(order => order.id)
      
      this.$confirm(`确定要删除选中的 ${selectedCount} 个订单吗？删除后无法恢复。`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteOrderBatch(orderIds)
      }).then(response => {
        if (response.code === 200) {
          const successCount = response.data || 0
          if (successCount === selectedCount) {
            this.$message.success(`成功删除 ${successCount} 个订单`)
          } else if (successCount > 0) {
            this.$message.warning(`成功删除 ${successCount} 个订单，${selectedCount - successCount} 个订单删除失败`)
          } else {
            this.$message.error('删除失败，请检查订单状态')
          }
          this.clearSelection()
          this.loadOrderList()
        }
      }).catch(error => {
        if (error !== 'cancel') {
          const errorMsg = error.response?.data?.msg || error.message || '批量删除失败'
          this.$message.error(errorMsg)
        }
      })
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

.batch-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 20px;
  background-color: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 4px;
  margin-bottom: 20px;

  span {
    font-size: 14px;
    color: #fa8c16;
    font-weight: bold;
  }
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
    align-items: center;
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

      .product-image-wrapper {
        flex-shrink: 0;

        .product-image {
          width: 80px;
          height: 80px;
          border-radius: 4px;
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.05);
          }
        }
      }

      .image-error {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        background-color: #f5f7fa;
        color: #909399;
        font-size: 12px;

        i {
          font-size: 24px;
          margin-bottom: 5px;
        }

        span {
          font-size: 12px;
        }
      }

      .image-placeholder {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        background-color: #f5f7fa;
        color: #909399;
      }

      .product-detail {
        flex: 1;

        .product-name {
          font-size: 14px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
          line-height: 1.4;
        }

        .product-spec {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          .spec-tag {
            display: inline-block;
            padding: 2px 8px;
            background-color: #f0f2f5;
            color: #606266;
            border-radius: 3px;
            font-size: 12px;
            line-height: 1.5;
          }

          .spec-default {
            color: #909399;
          }
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
