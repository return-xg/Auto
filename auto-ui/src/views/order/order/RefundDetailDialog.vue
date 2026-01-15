<template>
  <el-dialog
    title="退款详情"
    :visible.sync="dialogVisible"
    width="700px"
    @close="handleClose"
  >
    <div class="refund-info">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ Number(order.payPrice).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="退款类型">{{ getRefundTypeText(order.refundType) }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ Number(order.refundAmount).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ order.refundReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ formatTime(order.refundCreateTime) }}</el-descriptions-item>
        <el-descriptions-item label="凭证图片" :span="2" v-if="order.refundEvidence">
          <el-image
            v-for="(img, index) in getRefundImages(order.refundEvidence)"
            :key="index"
            :src="img"
            :preview-src-list="getRefundImages(order.refundEvidence)"
            style="width: 100px; height: 100px; margin-right: 10px;"
            fit="cover"
          />
          <span v-if="!order.refundEvidence">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核结果" :span="2" v-if="order.refundStatus !== 0">
          <el-tag :type="order.refundStatus === 1 ? 'success' : 'danger'">
            {{ getRefundStatusText(order.refundStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2" v-if="order.refundAdminRemark">
          {{ order.refundAdminRemark }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'RefundDetailDialog',
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
      dialogVisible: false
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    }
  },
  methods: {
    getRefundTypeText(type) {
      if (type === 1) {
        return '退款'
      } else if (type === 2) {
        return '退货'
      }
      return '-'
    },
    getRefundImages(evidence) {
      if (!evidence) return []
      return evidence.split(',').filter(img => img.trim())
    },
    getRefundStatusText(status) {
      if (status === 0) {
        return '待审核'
      } else if (status === 1) {
        return '退款审核通过'
      } else if (status === 2) {
        return '退款审核拒绝'
      } else if (status === 3) {
        return '退款中'
      } else if (status === 4) {
        return '退款成功'
      } else if (status === 5) {
        return '退货中'
      } else if (status === 6) {
        return '退货完成'
      }
      return '未知'
    },
    formatTime(timeStr) {
      if (!timeStr) return '-'
      try {
        const date = new Date(timeStr)
        return date.toLocaleString('zh-CN')
      } catch (e) {
        return timeStr
      }
    },
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.refund-info {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
