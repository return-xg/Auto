<template>
  <el-dialog
    :title="isEdit ? '编辑地址' : '新增地址'"
    :visible.sync="dialogVisible"
    width="50%"
    :before-close="handleClose"
    append-to-body
  >
    <el-form :model="form" :rules="rules" ref="addressForm" label-width="100px">
      <el-form-item label="收货人" prop="name">
        <el-input v-model="form.name" placeholder="请输入收货人姓名" maxlength="20" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="所在地区" prop="region">
        <el-cascader
          v-model="form.region"
          :options="regionData"
          :props="{ expandTrigger: 'hover' }"
          placeholder="请选择省/市/区"
          style="width: 100%;"
          @change="handleRegionChange"
        />
      </el-form-item>
      <el-form-item label="详细地址" prop="detail">
        <el-input
          v-model="form.detail"
          type="textarea"
          :rows="3"
          placeholder="请输入详细地址信息，如道路、门牌号、小区、楼栋号、单元等"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="设为默认">
        <el-switch v-model="form.isDefault" />
      </el-form-item>
    </el-form>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { addAddress, updateAddress } from '@/api/address/address'
import regionData from '@/utils/regionData'

export default {
  name: 'AddressDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    address: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      dialogVisible: this.visible,
      submitting: false,
      isEdit: false,
      regionData: regionData,
      form: {
        name: '',
        phone: '',
        region: [],
        province: '',
        city: '',
        district: '',
        detail: '',
        isDefault: false
      },
      rules: {
        name: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请选择所在地区', trigger: 'change' }
        ],
        detail: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.resetForm()
        if (this.address && this.address.id) {
          this.isEdit = true
          this.loadAddress()
        } else {
          this.isEdit = false
        }
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    resetForm() {
      this.form = {
        name: '',
        phone: '',
        region: [],
        province: '',
        city: '',
        district: '',
        detail: '',
        isDefault: false
      }
      this.$refs.addressForm?.clearValidate()
    },
    loadAddress() {
      this.form = {
        name: this.address.name || this.address.consignee,
        phone: this.address.phone,
        region: [this.address.province, this.address.city, this.address.district],
        province: this.address.province,
        city: this.address.city,
        district: this.address.district,
        detail: this.address.detail,
        isDefault: this.address.isDefault === 1 || this.address.isDefault === true
      }
    },
    handleRegionChange(value) {
      if (value && value.length === 3) {
        this.form.province = value[0]
        this.form.city = value[1]
        this.form.district = value[2]
      }
    },
    handleSubmit() {
      this.$refs.addressForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const data = {
            name: this.form.name,
            phone: this.form.phone,
            province: this.form.province,
            city: this.form.city,
            district: this.form.district,
            detail: this.form.detail,
            isDefault: this.form.isDefault ? 1 : 0
          }

          const apiCall = this.isEdit ? updateAddress : addAddress
          
          apiCall(data).then(response => {
            this.submitting = false
            if (response.code === 200) {
              this.$message.success(this.isEdit ? '修改成功' : '添加成功')
              this.$emit('success')
              this.handleClose()
            } else {
              this.$message.error(response.msg || '操作失败')
            }
          }).catch(error => {
            this.submitting = false
            this.$message.error('操作失败')
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
.el-form {
  padding: 20px 0;
}
</style>
