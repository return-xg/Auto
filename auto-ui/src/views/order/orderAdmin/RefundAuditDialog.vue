<template>
  <el-dialog
    :title="isViewMode ? '退款详情' : '退款审核'"
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

    <el-form v-if="!isViewMode" :model="form" :rules="rules" ref="auditForm" label-width="100px" style="margin-top: 20px;">
      <el-form-item label="审核结果" prop="auditStatus">
        <el-radio-group v-model="form.auditStatus" @change="handleStatusChange">
          <el-radio :label="1">审核通过</el-radio>
          <el-radio :label="2">审核拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="form.auditRemark"
          type="textarea"
          :rows="4"
          :placeholder="form.auditStatus === 2 ? '请输入拒绝原因' : '请输入审核备注（选填）'"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button v-if="!isViewMode" type="primary" @click="handleSubmit">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { auditRefund } from '@/api/order/order'

export default {
  name: 'RefundAuditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    order: {
      type: Object,
      default: () => ({})
    },
    isViewMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: false,
      form: {
        auditStatus: 1,
        auditRemark: ''
      },
      rules: {
        auditStatus: [
          { required: true, message: '请选择审核结果', trigger: 'change' }
        ],
        auditRemark: []
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.resetForm()
      }
    }
  },
  methods: {
    resetForm() {
      this.form = {
        auditStatus: 1,
        auditRemark: ''
      }
      if (this.$refs.auditForm) {
        this.$refs.auditForm.clearValidate()
      }
    },
    handleStatusChange(status) {
      if (status === 2) {
        this.rules.auditRemark = [
          { required: true, message: '请输入拒绝原因', trigger: 'blur' }
        ]
      } else {
        this.rules.auditRemark = []
      }
      if (this.$refs.auditForm) {
        this.$refs.auditForm.clearValidate(['auditRemark'])
      }
    },
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
      this.resetForm()
      this.$emit('close')
    },
    handleSubmit() {
      this.$refs.auditForm.validate(valid => {
        if (valid) {
          const confirmText = this.form.auditStatus === 1 ? '确定要通过该退款申请吗？' : '确定要拒绝该退款申请吗？'
          this.$confirm(confirmText, '确认审核', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            return auditRefund(this.order.id, this.form)
          }).then(response => {
            if (response.code === 200) {
              this.$message.success('审核成功')
              this.$emit('success')
            } else {
              this.$message.error(response.msg || '审核失败')
            }
          }).catch(error => {
            if (error !== 'cancel') {
              this.$message.error('审核失败：' + error.msg)
            }
          })
        }
      })
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
