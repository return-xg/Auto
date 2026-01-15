<template>
  <el-dialog
    title="订单发货"
    :visible.sync="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="shipForm" label-width="100px">
      <el-form-item label="订单号">
        <el-input v-model="order.orderNo" disabled />
      </el-form-item>
      <el-form-item label="收货人">
        <el-input v-model="order.receiverName" disabled />
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="order.receiverPhone" disabled />
      </el-form-item>
      <el-form-item label="收货地址">
        <el-input v-model="fullAddress" type="textarea" :rows="2" disabled />
      </el-form-item>
      <el-form-item label="物流公司" prop="logisticsCompany">
        <el-select v-model="form.logisticsCompany" placeholder="请选择物流公司" style="width: 100%">
          <el-option label="顺丰快递" value="顺丰快递" />
          <el-option label="中通快递" value="中通快递" />
          <el-option label="圆通快递" value="圆通快递" />
          <el-option label="韵达快递" value="韵达快递" />
          <el-option label="申通快递" value="申通快递" />
          <el-option label="京东物流" value="京东物流" />
          <el-option label="邮政EMS" value="邮政EMS" />
          <el-option label="德邦快递" value="德邦快递" />
        </el-select>
      </el-form-item>
      <el-form-item label="物流单号" prop="logisticsNo">
        <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" clearable />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleSubmit">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { shipOrder } from '@/api/order/order'

export default {
  name: 'ShipDialog',
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
      dialogVisible: false,
      form: {
        logisticsCompany: '',
        logisticsNo: ''
      },
      rules: {
        logisticsCompany: [
          { required: true, message: '请选择物流公司', trigger: 'change' }
        ],
        logisticsNo: [
          { required: true, message: '请输入物流单号', trigger: 'blur' },
          { min: 5, max: 50, message: '物流单号长度在 5 到 50 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    fullAddress() {
      if (!this.order.receiverProvince && !this.order.receiverCity && !this.order.receiverDistrict && !this.order.receiverDetailAddress) {
        return '-'
      }
      return `${this.order.receiverProvince || ''}${this.order.receiverCity || ''}${this.order.receiverDistrict || ''}${this.order.receiverDetailAddress || ''}`
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
        logisticsCompany: '',
        logisticsNo: ''
      }
      if (this.$refs.shipForm) {
        this.$refs.shipForm.clearValidate()
      }
    },
    handleClose() {
      this.resetForm()
      this.$emit('close')
    },
    handleSubmit() {
      this.$refs.shipForm.validate(valid => {
        if (valid) {
          shipOrder(this.order.id, this.form).then(response => {
            if (response.code === 200) {
              this.$message.success('发货成功')
              this.$emit('success')
            } else {
              this.$message.error(response.msg || '发货失败')
            }
          }).catch(error => {
            this.$message.error('发货失败：' + error.msg)
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.dialog-footer {
  text-align: right;
}
</style>
