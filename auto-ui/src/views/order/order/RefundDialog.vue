<template>
  <el-dialog
    title="申请退款"
    :visible.sync="dialogVisible"
    width="50%"
    :before-close="handleClose"
    append-to-body
  >
    <el-form :model="refundForm" :rules="rules" ref="refundForm" label-width="100px">
      <el-form-item label="退款原因" prop="reason">
        <el-select v-model="refundForm.reason" placeholder="请选择退款原因" style="width: 100%;">
          <el-option label="商品质量问题" value="quality" />
          <el-option label="商品与描述不符" value="description" />
          <el-option label="商品损坏" value="damage" />
          <el-option label="不想要了" value="not_want" />
          <el-option label="其他原因" value="other" />
        </el-select>
      </el-form-item>

      <el-form-item label="退款金额" prop="amount">
        <el-input-number
          v-model="refundForm.amount"
          :min="0.01"
          :max="order.totalAmount || 9999"
          :precision="2"
          :step="0.01"
          style="width: 100%;"
        />
        <div class="amount-tip">最多可退款 ¥{{ Number(order.totalAmount || 0).toFixed(2) }}</div>
      </el-form-item>

      <el-form-item label="退款说明" prop="description">
        <el-input
          v-model="refundForm.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述退款原因"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="上传凭证" prop="evidence">
        <el-upload
          ref="upload"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          :file-list="fileList"
          list-type="picture-card"
          :limit="5"
        >
          <i class="el-icon-plus"></i>
          <div slot="tip" class="el-upload__tip">
            最多上传5张图片，每张图片不超过5MB
          </div>
        </el-upload>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { refundOrder } from '@/api/order/order'
import { getToken } from '@/utils/auth'

export default {
  name: 'RefundDialog',
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
      submitting: false,
      refundForm: {
        reason: '',
        amount: 0,
        description: '',
        evidence: []
      },
      fileList: [],
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      },
      rules: {
        reason: [
          { required: true, message: '请选择退款原因', trigger: 'change' }
        ],
        amount: [
          { required: true, message: '请输入退款金额', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '退款金额不能小于0.01元', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入退款说明', trigger: 'blur' },
          { min: 10, message: '退款说明至少10个字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.resetForm()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    },
    'order.totalAmount'(val) {
      if (val) {
        this.refundForm.amount = Number(val)
      }
    }
  },
  methods: {
    resetForm() {
      this.refundForm = {
        reason: '',
        amount: Number(this.order.totalAmount) || 0,
        description: '',
        evidence: []
      }
      this.fileList = []
      this.$refs.refundForm?.clearValidate()
    },
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.refundForm.evidence.push(response.url)
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
        this.fileList = fileList.filter(item => item !== file)
      }
    },
    handleRemove(file, fileList) {
      const index = this.refundForm.evidence.indexOf(file.url)
      if (index > -1) {
        this.refundForm.evidence.splice(index, 1)
      }
    },
    handleSubmit() {
      this.$refs.refundForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const data = {
            orderId: this.order.id,
            ...this.refundForm
          }
          refundOrder(this.order.id, data).then(response => {
            this.submitting = false
            if (response.code === 200) {
              this.$message.success('退款申请已提交')
              this.$emit('submit', data)
              this.handleClose()
            } else {
              this.$message.error(response.msg || '提交失败')
            }
          }).catch(error => {
            this.submitting = false
            this.$message.error('提交失败')
          })
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
.amount-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
