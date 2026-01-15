<template>
  <div class="order-admin-container">
    <el-card>
      <div slot="header" class="card-header">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="订单管理" name="order"></el-tab-pane>
          <el-tab-pane label="售后管理" name="afterSale"></el-tab-pane>
        </el-tabs>
      </div>

      <div v-show="activeTab === 'order'">
        <el-form :inline="true" :model="queryForm" class="mb-4" @submit.native.prevent.stop>
          <el-form-item label="订单号">
            <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="queryForm.userName" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="queryForm.status" placeholder="请选择订单状态" clearable>
              <el-option label="待支付" :value="0" />
              <el-option label="待发货" :value="1" />
              <el-option label="已发货" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="退款状态">
            <el-select v-model="queryForm.refundStatus" placeholder="请选择退款状态" clearable>
              <el-option label="待审核" :value="0" />
              <el-option label="审核通过" :value="1" />
              <el-option label="审核拒绝" :value="2" />
              <el-option label="退款中" :value="3" />
              <el-option label="退款成功" :value="4" />
              <el-option label="退货中" :value="5" />
              <el-option label="退货完成" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="下单时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              @change="handleDateChange"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click.stop="handleQuery">
              <i class="el-icon-search"></i> 查询
            </el-button>
            <el-button @click.stop="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="table-actions mb-4">
          <el-button type="success" @click="handleBatchShip" :disabled="selectedIds.length === 0">
            <i class="el-icon-truck"></i> 批量发货
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </div>

        <el-table
          v-loading="loading"
          :data="orderList"
          border
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="订单ID" width="80" />
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="userName" label="用户名" width="120" />
          <el-table-column prop="totalPrice" label="商品总额" width="100" :formatter="formatPrice" />
          <el-table-column prop="freightPrice" label="运费" width="100" :formatter="formatPrice" />
          <el-table-column prop="payPrice" label="实付金额" width="100" :formatter="formatPrice" />
          <el-table-column prop="status" label="订单状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="refundStatus" label="退款状态" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.refundStatus !== undefined && scope.row.refundStatus !== null" :type="getRefundStatusType(scope.row.refundStatus)">
                {{ getRefundStatusText(scope.row.refundStatus) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="deliveryType" label="配送方式" width="100">
            <template slot-scope="scope">
              <span v-if="scope.row.deliveryType === 1">快递配送</span>
              <span v-else>门店自提</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" width="180" :formatter="formatDate" />
          <el-table-column label="操作" width="350" fixed="right">
            <template slot-scope="scope">
              <el-button type="primary" size="small" @click="viewOrderDetail(scope.row)" style="margin-right: 5px;">
                查看详情
              </el-button>
              <el-button
                v-if="scope.row.status === 1"
                type="success"
                size="small"
                @click="handleShip(scope.row)"
                style="margin-right: 5px;"
              >
                发货
              </el-button>
              <el-button
                v-if="scope.row.refundStatus === 0"
                type="warning"
                size="small"
                @click="handleRefundAudit(scope.row)"
                style="margin-right: 5px;"
              >
                退款审核
              </el-button>
              <el-button
                v-if="scope.row.status === 2"
                type="info"
                size="small"
                @click="viewLogistics(scope.row)"
                style="margin-right: 5px;"
              >
                物流信息
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            :current-page.sync="pagination.current"
            :page-size.sync="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <div v-show="activeTab === 'afterSale'">
        <el-form :inline="true" :model="refundQueryForm" class="mb-4" @submit.native.prevent.stop>
          <el-form-item label="订单号">
            <el-input v-model="refundQueryForm.orderNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="refundQueryForm.userName" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="售后类型">
            <el-select v-model="refundQueryForm.refundType" placeholder="请选择售后类型" clearable>
              <el-option label="退款" :value="1" />
              <el-option label="退货" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="售后状态">
            <el-select v-model="refundQueryForm.refundStatus" placeholder="请选择售后状态" clearable>
              <el-option label="待审核" :value="0" />
              <el-option label="审核通过" :value="1" />
              <el-option label="审核拒绝" :value="2" />
              <el-option label="退款中" :value="3" />
              <el-option label="退款成功" :value="4" />
              <el-option label="退货中" :value="5" />
              <el-option label="退货完成" :value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="申请时间">
            <el-date-picker
              v-model="refundDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              @change="handleRefundDateChange"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click.stop="handleRefundQuery">
              <i class="el-icon-search"></i> 查询
            </el-button>
            <el-button @click.stop="resetRefundQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table
          v-loading="refundLoading"
          :data="refundList"
          border
          stripe
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="userName" label="用户名" width="120" />
          <el-table-column prop="refundType" label="售后类型" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.refundType === 1 ? 'warning' : 'danger'">
                {{ scope.row.refundType === 1 ? '退款' : '退货' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="refundReason" label="退款原因" width="200" show-overflow-tooltip />
          <el-table-column prop="refundAmount" label="退款金额" width="120" :formatter="formatPrice" />
          <el-table-column prop="refundStatus" label="售后状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getRefundStatusType(scope.row.refundStatus)">
                {{ getRefundStatusText(scope.row.refundStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="refundCreateTime" label="申请时间" width="180" :formatter="formatDate" />
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.refundStatus === 0"
                type="primary"
                size="small"
                @click="handleRefundAudit(scope.row)"
                style="margin-right: 5px;"
              >
                审核
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="viewRefundDetail(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            :current-page.sync="refundPagination.current"
            :page-size.sync="refundPagination.size"
            :total="refundPagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleRefundSizeChange"
            @current-change="handleRefundCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <order-detail-dialog
      :visible="detailDialogVisible"
      :order="currentOrder"
      @close="detailDialogVisible = false"
    />

    <logistics-dialog
      :visible="logisticsDialogVisible"
      :order="currentOrder"
      @close="logisticsDialogVisible = false"
    />

    <ship-dialog
      :visible="shipDialogVisible"
      :order="currentOrder"
      @close="shipDialogVisible = false"
      @success="handleShipSuccess"
    />

    <refund-audit-dialog
      :visible="refundAuditDialogVisible"
      :order="currentOrder"
      :isViewMode="isRefundViewMode"
      @close="refundAuditDialogVisible = false"
      @success="handleRefundAuditSuccess"
    />
  </div>
</template>

<script>
import { listAdminOrder, deleteOrder, deleteOrderBatch, shipOrder, auditRefund } from '@/api/order/order'
import OrderDetailDialog from '../order/OrderDetailDialog.vue'
import LogisticsDialog from '../order/LogisticsDialog.vue'
import ShipDialog from './ShipDialog.vue'
import RefundAuditDialog from './RefundAuditDialog.vue'

export default {
  name: 'OrderAdmin',
  components: {
    OrderDetailDialog,
    LogisticsDialog,
    ShipDialog,
    RefundAuditDialog
  },
  data() {
    return {
      activeTab: 'order',
      loading: false,
      queryForm: {
        orderNo: '',
        userName: '',
        status: null,
        refundStatus: null,
        startTime: '',
        endTime: ''
      },
      dateRange: [],
      orderList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      selectedIds: [],
      detailDialogVisible: false,
      logisticsDialogVisible: false,
      shipDialogVisible: false,
      refundAuditDialogVisible: false,
      currentOrder: {},
      refundLoading: false,
      refundQueryForm: {
        orderNo: '',
        userName: '',
        refundType: null,
        refundStatus: null,
        startTime: '',
        endTime: ''
      },
      refundDateRange: [],
      refundList: [],
      refundPagination: {
        current: 1,
        size: 10,
        total: 0
      },
      isRefundViewMode: false
    }
  },
  mounted() {
    this.loadOrderList()
  },
  methods: {
    handleTabClick(tab) {
      if (tab.name === 'order') {
        this.loadOrderList()
      } else if (tab.name === 'afterSale') {
        this.loadRefundList()
      }
    },
    loadOrderList() {
      this.loading = true
      const queryParams = {
        ...this.queryForm,
        beginTime: this.queryForm.startTime,
        endTime: this.queryForm.endTime,
        pageNum: this.pagination.current,
        pageSize: this.pagination.size
      }
      listAdminOrder(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.orderList = response.rows || []
          this.pagination.total = response.total || 0
        } else {
          this.$message.error('获取订单列表失败')
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取订单列表失败：' + (error.msg || error.message))
      })
    },
    loadRefundList() {
      this.refundLoading = true
      const queryParams = {
        ...this.refundQueryForm,
        beginTime: this.refundQueryForm.startTime,
        endTime: this.refundQueryForm.endTime,
        pageNum: this.refundPagination.current,
        pageSize: this.refundPagination.size
      }
      listAdminOrder(queryParams).then(response => {
        this.refundLoading = false
        if (response.code === 200) {
          const rows = response.rows || []
          this.refundList = rows.filter(row => row.refundId !== null)
          this.refundPagination.total = this.refundList.length || 0
        } else {
          this.$message.error('获取售后列表失败')
        }
      }).catch(error => {
        this.refundLoading = false
        this.$message.error('获取售后列表失败：' + (error.msg || error.message))
      })
    },
    handleQuery() {
      this.pagination.current = 1
      this.loadOrderList()
    },
    resetQuery() {
      this.queryForm = {
        orderNo: '',
        userName: '',
        status: null,
        refundStatus: null,
        startTime: '',
        endTime: ''
      }
      this.dateRange = []
      this.pagination.current = 1
      this.loadOrderList()
    },
    handleDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.queryForm.startTime = dateRange[0]
        this.queryForm.endTime = dateRange[1]
      } else {
        this.queryForm.startTime = ''
        this.queryForm.endTime = ''
      }
    },
    handleRefundDateChange(dateRange) {
      if (dateRange && dateRange.length === 2) {
        this.refundQueryForm.startTime = dateRange[0]
        this.refundQueryForm.endTime = dateRange[1]
      } else {
        this.refundQueryForm.startTime = ''
        this.refundQueryForm.endTime = ''
      }
    },
    handleRefundQuery() {
      this.refundPagination.current = 1
      this.loadRefundList()
    },
    resetRefundQuery() {
      this.refundQueryForm = {
        orderNo: '',
        userName: '',
        refundType: null,
        refundStatus: null,
        startTime: '',
        endTime: ''
      }
      this.refundDateRange = []
      this.refundPagination.current = 1
      this.loadRefundList()
    },
    handleRefundSizeChange(size) {
      this.refundPagination.size = size
      this.loadRefundList()
    },
    handleRefundCurrentChange(current) {
      this.refundPagination.current = current
      this.loadRefundList()
    },
    viewRefundDetail(row) {
      this.currentOrder = row
      this.refundAuditDialogVisible = true
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadOrderList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadOrderList()
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    formatDate(row, column) {
      if (!row[column.property]) return ''
      const date = new Date(row[column.property])
      return date.toLocaleString('zh-CN')
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
    viewOrderDetail(order) {
      this.currentOrder = order
      this.detailDialogVisible = true
    },
    viewLogistics(order) {
      this.currentOrder = order
      this.logisticsDialogVisible = true
    },
    handleShip(order) {
      this.currentOrder = order
      this.shipDialogVisible = true
    },
    handleShipSuccess() {
      this.shipDialogVisible = false
      this.$message.success('发货成功')
      this.loadOrderList()
    },
    handleBatchShip() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请选择要发货的订单')
        return
      }
      this.$confirm('确定要批量发货选中的订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const promises = this.selectedIds.map(id => shipOrder(id, { logisticsCompany: '顺丰快递', logisticsNo: 'SF' + Date.now() }))
        return Promise.all(promises)
      }).then(() => {
        this.$message.success('批量发货成功')
        this.loadOrderList()
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('批量发货失败：' + error.msg)
        }
      })
    },
    handleRefundAudit(order) {
      this.currentOrder = order
      this.isRefundViewMode = false
      this.refundAuditDialogVisible = true
    },
    handleRefundAuditSuccess() {
      this.refundAuditDialogVisible = false
      this.$message.success('退款审核成功')
      this.loadOrderList()
    },
    viewRefundDetail(row) {
      this.currentOrder = row
      this.isRefundViewMode = true
      this.refundAuditDialogVisible = true
    },
    handleDelete(id) {
      this.$confirm('确定要删除该订单吗？删除后无法恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteOrder(id)
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
      if (this.selectedIds.length === 0) {
        this.$message.warning('请选择要删除的订单')
        return
      }
      this.$confirm(`确定要批量删除选中的 ${this.selectedIds.length} 个订单吗？删除后无法恢复。`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteOrderBatch(this.selectedIds)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('批量删除成功')
          this.loadOrderList()
        } else {
          this.$message.error(response.msg || '批量删除失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败：' + error.msg)
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.order-admin-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #fafafa;
}

.el-table tbody tr:hover > td {
  background-color: #f5f7fa;
}

.el-form--inline .el-form-item {
  margin-right: 20px;
}

.el-table .el-button {
  margin-right: 5px;
  margin-bottom: 5px;
}
</style>
