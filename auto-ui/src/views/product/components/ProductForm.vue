<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="800px"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择商品分类">
              <el-option
                v-for="category in categoryList"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="品牌" prop="brand">
            <el-input v-model="form.brand" placeholder="请输入品牌名称" />
          </el-form-item>
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input v-model.number="form.price" placeholder="请输入商品价格" />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input v-model.number="form.stock" placeholder="请输入库存数量" />
          </el-form-item>
          <el-form-item label="库存预警" prop="warnStock">
            <el-input v-model.number="form.warnStock" placeholder="请输入库存预警值" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="适用车型" prop="fitCarModel">
            <el-input v-model="form.fitCarModel" placeholder="请输入适用车型" />
          </el-form-item>
          <el-form-item label="年款" prop="year">
            <el-input v-model="form.year" placeholder="请输入年款" />
          </el-form-item>
          <el-form-item label="型号" prop="model">
            <el-input v-model="form.model" placeholder="请输入型号" />
          </el-form-item>
          <el-form-item label="颜色" prop="color">
            <el-input v-model="form.color" placeholder="请输入颜色" />
          </el-form-item>
          <el-form-item label="重量" prop="weight">
            <el-input v-model.number="form.weight" placeholder="请输入重量(kg)" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="上架" value="1" />
              <el-option label="下架" value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="商品描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="规格" prop="specification">
            <el-input v-model="form.specification" type="textarea" :rows="3" placeholder="请输入商品规格" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="图片">
            <el-upload
              :action="uploadUrl"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
              :limit="5"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addProduct, updateProduct } from '@/api/product/product'

export default {
  name: 'ProductForm',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editMode: {
      type: Boolean,
      default: false
    },
    product: {
      type: Object,
      default: () => ({})
    },
    categoryList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      title: '',
      form: {
        id: null,
        categoryId: null,
        brand: '',
        name: '',
        price: null,
        stock: 0,
        warnStock: 0,
        fitCarModel: '',
        year: '',
        model: '',
        color: '',
        weight: null,
        status: 1,
        description: '',
        specification: '',
        images: []
      },
      fileList: [],
      uploadUrl: '/file/upload',
      rules: {
        categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
        brand: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }, { type: 'number', min: 0.01 }],
        stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }, { type: 'number', min: 0 }],
        warnStock: [{ required: true, message: '请输入库存预警值', trigger: 'blur' }, { type: 'number', min: 0 }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.title = this.editMode ? '编辑商品' : '新增商品'
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
      if (this.editMode && this.product) {
        this.form = { ...this.product }
        this.initFileList()
      } else {
        this.form = {
          id: null,
          categoryId: null,
          brand: '',
          name: '',
          price: null,
          stock: 0,
          warnStock: 0,
          fitCarModel: '',
          year: '',
          model: '',
          color: '',
          weight: null,
          status: 1,
          description: '',
          specification: '',
          images: []
        }
        this.fileList = []
      }
    },
    initFileList() {
      if (this.form.images && this.form.images.length > 0) {
        this.fileList = this.form.images.map(image => ({
          url: image
        }))
      } else {
        this.fileList = []
      }
    },
    handleUploadSuccess(response, file) {
      if (response.code === 200) {
        this.form.images.push(response.data.url)
        this.$message.success('上传成功')
      } else {
        this.$message.error('上传失败：' + response.msg)
      }
    },
    handleRemove(file, fileList) {
      if (file.url) {
        const index = this.form.images.indexOf(file.url)
        if (index > -1) {
          this.form.images.splice(index, 1)
        }
      }
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const submitData = { ...this.form }
          if (this.editMode) {
            updateProduct(submitData).then(response => {
              if (response.code === 200) {
                this.$message.success('修改成功')
                this.$emit('success')
                this.handleClose()
              } else {
                this.$message.error('修改失败：' + response.msg)
              }
            })
          } else {
            addProduct(submitData).then(response => {
              if (response.code === 200) {
                this.$message.success('添加成功')
                this.$emit('success')
                this.handleClose()
              } else {
                this.$message.error('添加失败：' + response.msg)
              }
            })
          }
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