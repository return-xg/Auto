<template>
  <el-dialog
    title="库存管理"
    :visible.sync="visible"
    width="400px"
    append-to-body
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="form.name" placeholder="商品名称" disabled />
      </el-form-item>
      <el-form-item label="当前库存" prop="stock">
        <el-input v-model.number="form.stock" placeholder="请输入库存数量" />
      </el-form-item>
      <el-form-item label="库存预警" prop="warnStock">
        <el-input v-model.number="form.warnStock" placeholder="请输入库存预警值" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { updateStock, updateWarnStock } from '@/api/product/product'

export default {
  name: 'ProductStockForm',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    product: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      form: {
        id: null,
        name: '',
        stock: 0,
        warnStock: 0
      },
      rules: {
        stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }, { type: 'number', min: 0 }],
        warnStock: [{ required: true, message: '请输入库存预警值', trigger: 'blur' }, { type: 'number', min: 0 }]
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.initForm()
      }
    },
    product(newVal) {
      if (this.visible) {
        this.initForm()
      }
    }
  },
  methods: {
    initForm() {
      if (this.product) {
        this.form = {
          id: this.product.id || null,
          name: this.product.name || '',
          stock: this.product.stock || 0,
          warnStock: this.product.warnStock || 0
        }
      }
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const updateStockPromise = updateStock(this.form.id, this.form.stock)
          const updateWarnStockPromise = updateWarnStock(this.form.id, this.form.warnStock)
          
          Promise.all([updateStockPromise, updateWarnStockPromise]).then(responses => {
            const allSuccess = responses.every(response => response.code === 200)
            if (allSuccess) {
              this.$message.success('库存更新成功')
              this.$emit('success')
              this.handleClose()
            } else {
              const errors = responses.filter(r => r.code !== 200).map(r => r.msg || '更新失败')
              this.$message.error('库存更新失败：' + errors.join(', '))
            }
          }).catch(error => {
            this.$message.error('库存更新失败：' + error.msg)
          })
        }
      })
    },
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: center;
}
</style>