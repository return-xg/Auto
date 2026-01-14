<template>
  <el-dialog
    title="申请退款"
    :visible.sync="dialogVisible"
    width="50%"
    :before-close="handleClose"
    append-to-body
  >
    <div v-if="order.refundStatus === 0" class="refund-status-warning">
      <i class="el-icon-warning"></i>
      <div class="warning-content">
        <div class="warning-title">该订单已有退款申请</div>
        <div class="warning-desc">当前退款申请状态：待审核，请等待管理员审核或取消当前申请</div>
      </div>
      <el-button type="danger" size="small" @click="handleCancelRefund">取消退款申请</el-button>
    </div>

    <el-form :model="refundForm" :rules="rules" ref="refundForm" label-width="100px" :disabled="order.refundStatus === 0">
      <el-form-item label="退款类型" prop="type">
        <el-select v-model="refundForm.type" placeholder="请选择退款类型" style="width: 100%;">
          <el-option label="仅退款" :value="1" />
          <el-option label="退货退款" :value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="退款原因" prop="reason">
        <el-input
          v-model="refundForm.reason"
          type="textarea"
          :rows="4"
          placeholder="请详细描述退款原因"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="退款金额" prop="amount">
        <el-input-number
          v-model="refundForm.amount"
          :min="0.01"
          :max="order.totalPrice || order.totalAmount || 9999"
          :precision="2"
          :step="0.01"
          style="width: 100%;"
        />
        <div class="amount-tip">最多可退款 ¥{{ Number(order.totalPrice || order.totalAmount || 0).toFixed(2) }}</div>
      </el-form-item>

      <el-form-item label="上传凭证" prop="evidence">
        <el-upload
          ref="upload"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          :on-exceed="handleExceed"
          :before-upload="beforeUpload"
          :file-list="fileList"
          list-type="picture-card"
          :limit="5"
          :disabled="order.refundStatus === 0"
        >
          <i class="el-icon-plus"></i>
          <div slot="tip" class="el-upload__tip">
            最多上传5张图片，每张图片不超过5MB，支持jpg/png格式
          </div>
        </el-upload>
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">{{ order.refundStatus === 0 ? '关闭' : '取消' }}</el-button>
      <el-button v-if="order.refundStatus !== 0" type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { refundOrder, cancelRefund } from '@/api/order/order'
import { getToken } from '@/utils/auth'
import { mapGetters } from 'vuex'

export default {
  name: 'RefundDialog',
  computed: {
    ...mapGetters(['id'])
  },
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
        type: 1,
        reason: '',
        amount: 0,
        evidence: ''
      },
      fileList: [],
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      },
      rules: {
        type: [
          { required: true, message: '请选择退款类型', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '请输入退款原因', trigger: 'blur' },
          { min: 10, message: '退款原因至少10个字符', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '请输入退款金额', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '退款金额不能小于0.01元', trigger: 'blur' },
          { validator: this.validateAmount, trigger: 'blur' }
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
    'order.totalPrice': {
      handler(val) {
        if (val && this.refundForm.amount === 0) {
          this.refundForm.amount = Number(val)
        }
      },
      immediate: true
    },
    'order.totalAmount': {
      handler(val) {
        if (val && this.refundForm.amount === 0 && !this.order.totalPrice) {
          this.refundForm.amount = Number(val)
        }
      },
      immediate: true
    }
  },
  beforeDestroy() {
    this.submitting = false
  },
  methods: {
    validateAmount(rule, value, callback) {
      const maxAmount = Number(this.order.totalPrice || this.order.totalAmount) || 0
      if (value > maxAmount) {
        callback(new Error(`退款金额不能超过商品总额 ¥${maxAmount.toFixed(2)}`))
      } else {
        callback()
      }
    },
    beforeUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        this.$message.error('只能上传 JPG/PNG 格式的图片!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    },
    handleExceed(files, fileList) {
      this.$message.warning(`最多只能上传5张图片，当前已选择${fileList.length}张`)
    },
    resetForm() {
      this.refundForm = {
        type: 1,
        reason: '',
        amount: Number(this.order.totalPrice || this.order.totalAmount) || 0,
        evidence: ''
      }
      this.fileList = []
      if (this.$refs.refundForm) {
        this.$refs.refundForm.clearValidate()
      }
    },
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        const currentEvidence = this.refundForm.evidence ? this.refundForm.evidence.split(',') : []
        currentEvidence.push(response.url)
        this.refundForm.evidence = currentEvidence.join(',')
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
        this.fileList = fileList.filter(item => item.uid !== file.uid)
      }
    },
    handleRemove(file, fileList) {
      const fileUrl = file.response?.url || file.url
      const currentEvidence = this.refundForm.evidence ? this.refundForm.evidence.split(',') : []
      const index = currentEvidence.indexOf(fileUrl)
      if (index > -1) {
        currentEvidence.splice(index, 1)
        this.refundForm.evidence = currentEvidence.join(',')
      }
    },
    handleSubmit() {
      if (!this.order || !this.order.id) {
        this.$message.error('订单信息不完整，无法提交退款申请')
        return
      }

      if (!this.id) {
        this.$message.error('用户信息不完整，请重新登录')
        return
      }

      if (this.order.refundStatus === 0) {
        this.$message.warning('该订单已有退款申请，请等待管理员审核或取消当前申请')
        return
      }

      if (this.order.refundStatus === 3 || this.order.refundStatus === 4 || this.order.refundStatus === 5 || this.order.refundStatus === 6) {
        this.$message.warning('该订单退款流程正在进行中，无法再次申请')
        return
      }

      this.$refs.refundForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const data = {
            orderId: this.order.id,
            userId: this.id,
            type: this.refundForm.type,
            reason: this.refundForm.reason,
            amount: Number(this.refundForm.amount).toFixed(2),
            evidence: this.refundForm.evidence
          }

          refundOrder(this.order.id, data).then(response => {
            this.submitting = false
            if (response.code === 200) {
              this.$message.success('退款申请已提交')
              this.$emit('submit', data)
              this.handleClose()
            } else {
              this.$message.error(response.msg || '提交失败，请稍后重试')
            }
          }).catch(error => {
            this.submitting = false
            console.error('退款申请提交失败:', error)
            if (error.response) {
              const status = error.response.status
              if (status === 401) {
                this.$message.error('登录已过期，请重新登录')
              } else if (status === 403) {
                this.$message.error('无权限操作')
              } else if (status === 404) {
                this.$message.error('订单不存在')
              } else if (status >= 500) {
                this.$message.error('服务器错误，请稍后重试')
              } else {
                this.$message.error(error.response.data?.msg || '提交失败，请稍后重试')
              }
            } else if (error.request) {
              this.$message.error('网络连接失败，请检查网络设置')
            } else {
              this.$message.error('提交失败，请稍后重试')
            }
          })
        }
      })
    },
    handleClose() {
      if (this.submitting) {
        this.$message.warning('正在提交中，请稍候...')
        return
      }
      this.dialogVisible = false
      this.$emit('close')
    },
    handleCancelRefund() {
      this.$confirm('确定要取消该退款申请吗？取消后订单将恢复为可申请退款状态。', '取消退款申请', {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }).then(() => {
        return cancelRefund(this.order.id)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('退款申请已取消')
          this.$emit('cancelRefund')
          this.handleClose()
        } else {
          this.$message.error(response.msg || '取消失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('取消失败')
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.refund-status-warning {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #fef8e7;
  border: 1px solid #ffd591;
  border-radius: 4px;
  margin-bottom: 20px;

  i {
    font-size: 24px;
    color: #fa8c16;
    margin-right: 15px;
  }

  .warning-content {
    flex: 1;

    .warning-title {
      font-size: 16px;
      font-weight: bold;
      color: #fa8c16;
      margin-bottom: 5px;
    }

    .warning-desc {
      font-size: 14px;
      color: #8c6d46;
    }
  }
}

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
