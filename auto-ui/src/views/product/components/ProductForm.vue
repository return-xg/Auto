<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="900px"
    append-to-body
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <!-- 基本信息区域 -->
      <el-divider content-position="left">基本信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择商品分类" style="width: 100%;">
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
          <el-form-item label="适用车型" prop="fitCarModel">
            <el-input v-model="form.fitCarModel" placeholder="请输入适用车型" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="价格" prop="price">
            <el-input v-model.number="form.price" placeholder="请输入商品价格" />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input v-model.number="form.stock" placeholder="请输入库存数量" />
          </el-form-item>
          <el-form-item label="库存预警" prop="warnStock">
            <el-input v-model.number="form.warnStock" placeholder="请输入库存预警值" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%;">
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 图片区域 -->
      <el-divider content-position="left">商品图片</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="主图" prop="mainImage">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-change="handleMainImageChange"
              :on-remove="handleMainImageRemove"
              :on-exceed="handleMainImageExceed"
              :file-list="mainImageFileList"
              :limit="1"
              :before-upload="beforeMainImageUpload"
              style="display: inline-block;"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="附图">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-change="handleSubImageChange"
              :on-remove="handleSubImageRemove"
              :file-list="subImageFileList"
              :limit="10"
              :before-upload="beforeSubImageUpload"
              style="display: inline-block;"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 详情区域 -->
      <el-divider content-position="left">商品详情</el-divider>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="规格参数" prop="spec">
            <el-input v-model="form.spec" type="textarea" :rows="4" placeholder="请输入规格参数(JSON格式)" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="商品详情" prop="detail">
            <el-input v-model="form.detail" type="textarea" :rows="6" placeholder="请输入商品详情(支持HTML格式)" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
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
      submitting: false,
      form: {
        id: null,
        categoryId: null,
        brand: '',
        name: '',
        mainImage: '',
        subImages: '',
        detail: '',
        spec: '',
        fitCarModel: '',
        price: null,
        stock: 0,
        warnStock: 0,
        sales: 0,
        status: 1
      },
      mainImageFileList: [],
      subImageFileList: [],
      mainImageFile: null,
      subImageFiles: [],
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
    product: {
      handler(newVal) {
        if (this.visible && newVal && Object.keys(newVal).length > 0) {
          this.initForm()
        }
      },
      deep: true
    }
  },
  methods: {
    initForm() {
      if (this.editMode && this.product) {
        this.form = { ...this.product }
        if (Array.isArray(this.form.subImages)) {
          this.form.subImages = JSON.stringify(this.form.subImages)
        }
        this.initImageFileLists()
      } else {
        this.form = {
          id: null,
          categoryId: null,
          brand: '',
          name: '',
          mainImage: '',
          subImages: '',
          detail: '',
          spec: '',
          fitCarModel: '',
          price: null,
          stock: 0,
          warnStock: 0,
          sales: 0,
          status: 1
        }
        this.mainImageFileList = []
        this.subImageFileList = []
        this.mainImageFile = null
        this.subImageFiles = []
      }
    },
    initImageFileLists() {
      this.mainImageFileList = this.form.mainImage ? [{ url: this.getImageUrl(this.form.mainImage), name: '主图' }] : []
      try {
        if (this.form.subImages) {
          const subImages = typeof this.form.subImages === 'string' ? JSON.parse(this.form.subImages) : this.form.subImages
          if (Array.isArray(subImages)) {
            this.subImageFileList = subImages.map((img, index) => ({ url: this.getImageUrl(img), name: '附图' + (index + 1) }))
          } else {
            this.subImageFileList = []
          }
        } else {
          this.subImageFileList = []
        }
      } catch (e) {
        console.error('解析附图数据失败:', e)
        this.subImageFileList = []
      }
    },
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return process.env.VUE_APP_BASE_API + url
    },
    beforeMainImageUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/webp'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG/PNG/GIF/WEBP 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    beforeSubImageUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/webp'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('上传图片只能是 JPG/PNG/GIF/WEBP 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isImage && isLt2M
    },
    handleMainImageChange(file, fileList) {
      if (file.status === 'ready') {
        this.mainImageFile = file.raw
        this.mainImageFileList = fileList
      }
    },
    handleMainImageExceed(files, fileList) {
      this.$message.warning('主图只能上传一张，请先删除已上传的图片')
    },
    handleMainImageRemove(file, fileList) {
      this.mainImageFile = null
      this.mainImageFileList = fileList
    },
    handleSubImageChange(file, fileList) {
      if (file.status === 'ready') {
        this.subImageFiles = fileList.map(f => f.raw).filter(f => f)
        this.subImageFileList = fileList
      }
    },
    handleSubImageRemove(file, fileList) {
      this.subImageFiles = fileList.map(f => f.raw).filter(f => f)
      this.subImageFileList = fileList
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const formData = new FormData()
          formData.append('categoryId', this.form.categoryId || '')
          formData.append('brand', this.form.brand || '')
          formData.append('name', this.form.name || '')
          formData.append('detail', this.form.detail || '')
          formData.append('spec', this.form.spec || '')
          formData.append('fitCarModel', this.form.fitCarModel || '')
          formData.append('price', this.form.price || 0)
          formData.append('stock', this.form.stock || 0)
          formData.append('warnStock', this.form.warnStock || 0)
          formData.append('sales', this.form.sales || 0)
          formData.append('isHot', this.form.isHot || 0)
          formData.append('isNew', this.form.isNew || 0)
          formData.append('status', this.form.status !== null ? this.form.status : 1)

          if (this.editMode) {
            formData.append('id', this.form.id)
          }

          if (this.mainImageFile) {
            formData.append('mainImage', this.mainImageFile)
          }

          if (this.subImageFiles.length > 0) {
            this.subImageFiles.forEach(file => {
              formData.append('subImages', file)
            })
          }

          this.submitting = true
          const request = this.editMode ? updateProduct(formData) : addProduct(formData)
          request.then(response => {
            if (response.code === 200) {
              this.$message.success(this.editMode ? '修改成功' : '添加成功')
              this.$emit('success')
              this.handleClose()
            } else {
              this.$message.error(response.msg || (this.editMode ? '修改失败' : '添加失败'))
            }
          }).catch(error => {
            this.$message.error((this.editMode ? '修改失败' : '添加失败') + '：' + (error.msg || error.message))
          }).finally(() => {
            this.submitting = false
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

/* 调整分隔线样式 */
.el-divider {
  margin: 20px 0;
}

/* 调整表单项间距 */
.el-form-item {
  margin-bottom: 18px;
}

/* 调整上传组件样式 */
.el-upload--picture-card {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

.el-upload-list__item {
  width: 100px;
  height: 100px;
}
</style>