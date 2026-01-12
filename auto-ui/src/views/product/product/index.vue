<template>
  <div class="product-container">
    <el-card>
      <div slot="header" class="card-header">
        <span>商品管理</span>
      </div>

      <el-form :inline="true" :model="queryForm" class="mb-4" @submit.native.prevent.stop>
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="queryForm.brand" placeholder="请输入品牌" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="适配车型">
          <el-input v-model="queryForm.carModel" placeholder="适配车型" clearable />
        </el-form-item>
        <el-form-item label="适配年份">
          <el-input v-model="queryForm.year" placeholder="适配年份" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click.stop="handleQuery">
            <i class="el-icon-search"></i> 查询
          </el-button>
          <el-button @click.stop="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-actions mb-4">
        <el-button type="primary" @click="handleAdd">
          <i class="el-icon-plus"></i> 新增商品
        </el-button>
        <el-button type="success" @click="handleBatchPutOnSale" :disabled="selectedIds.length === 0">
          <i class="el-icon-upload"></i> 批量上架
        </el-button>
        <el-button type="warning" @click="handleBatchPutOffSale" :disabled="selectedIds.length === 0">
          <i class="el-icon-download"></i> 批量下架
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
          <i class="el-icon-delete"></i> 批量删除
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="productList"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip width="200" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="categoryId" label="分类" width="120">
          <template slot-scope="scope">
            {{ getCategoryName(scope.row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" :formatter="formatPrice" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="warnStock" label="预警值" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="mainImage" label="主图" width="120">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.mainImage"
              :src="getImageUrl(scope.row.mainImage)"
              :preview-src-list="[getImageUrl(scope.row.mainImage)]"
              fit="cover"
              style="width: 80px; height: 80px;"
              :preview-title="scope.row.name"
              preview-teleported>
            </el-image>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="spec" label="规格参数" show-overflow-tooltip width="150" />
        <el-table-column prop="fitCarModel" label="适配车型" show-overflow-tooltip width="150" />
        <el-table-column prop="subImages" label="附图" width="150">
          <template slot-scope="scope">
            <div v-if="getSubImages(scope.row.subImages).length > 0" class="image-preview">
              <el-image
                v-for="(image, index) in getSubImages(scope.row.subImages)"
                :key="index"
                :src="getImageUrl(image)"
                :preview-src-list="getSubImages(scope.row.subImages).map(img => getImageUrl(img))"
                fit="cover"
                style="width: 40px; height: 40px; margin-right: 5px;"
                :preview-title="scope.row.name + ' - 附图'"
                preview-teleported
                :initial-index="index">
              </el-image>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isHot" label="热销" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isHot === 1 ? 'danger' : 'info'">
              {{ scope.row.isHot === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isNew" label="新品" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isNew === 1 ? 'success' : 'info'">
              {{ scope.row.isNew === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商品详情" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="300" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" style="margin-right: 5px;">
              编辑
            </el-button>
            <el-button type="success" size="small" @click="handlePutOnSale(scope.row.id)" v-if="scope.row.status === 0" style="margin-right: 5px;">
              上架
            </el-button>
            <el-button type="warning" size="small" @click="handlePutOffSale(scope.row.id)" v-if="scope.row.status === 1" style="margin-right: 5px;">
              下架
            </el-button>
            <el-button type="info" size="small" @click="handleStock(scope.row)" style="margin-right: 5px;">
              库存
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
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
    </el-card>

    <!-- 新增/编辑商品弹窗 -->
    <product-form
      :visible="productFormVisible"
      :edit-mode="editMode"
      :product="currentProduct"
      :category-list="categoryList"
      @close="handleFormClose"
      @success="handleFormSuccess"
    />

    <!-- 库存管理弹窗 -->
    <product-stock-form
      :visible="stockFormVisible"
      :product="currentProduct"
      @close="handleStockFormClose"
      @success="handleStockFormSuccess"
    />

    <!-- 商品详情弹窗 -->
    <el-dialog
      title="商品详情"
      :visible.sync="detailDialogVisible"
      width="60%"
      append-to-body
    >
      <div v-html="currentDetail"></div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ProductForm from '../components/ProductForm.vue'
import ProductStockForm from '../components/ProductStockForm.vue'
import { listProduct, batchPutOnSale, batchPutOffSale, delProduct, putOnSale, putOffSale } from '@/api/product/product'
import { listCategory } from '@/api/category/category'

export default {
  name: 'ProductIndex',
  components: {
    ProductForm,
    ProductStockForm
  },
  data() {
    return {
      // 加载状态
      loading: false,
      // 商品列表
      productList: [],
      // 分类列表
      categoryList: [],
      // 查询表单
      queryForm: {
        name: '',
        brand: '',
        categoryId: null,
        priceMin: null,
        priceMax: null,
        carModel: null,
        year: null,
        orderBy: null,
        pageNum: 1,
        pageSize: 10
      },
      // 分页信息
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      // 选中的商品ID
      selectedIds: [],
      // 商品表单
      productFormVisible: false,
      editMode: false,
      currentProduct: {},
      // 库存表单
      stockFormVisible: false,
      // 商品详情弹窗
      detailDialogVisible: false,
      currentDetail: ''
    }
  },
  mounted() {
    this.loadCategories()
    this.loadProductList()
  },
  methods: {
    // 加载分类列表
    loadCategories() {
      listCategory({ pageNum: 1, pageSize: 100 }).then(response => {
        if (response.code === 200) {
          this.categoryList = response.rows || []
        } else {
          this.$message.error('获取分类列表失败')
        }
      }).catch(error => {
        this.$message.error('获取分类列表失败：' + error.msg)
      })
    },
    // 加载商品列表
    loadProductList() {
      this.loading = true
      const queryParams = {
        ...this.queryForm,
        pageNum: this.pagination.current,
        pageSize: this.pagination.size
      }
      // Ensure numeric fields are properly formatted
      if (queryParams.priceMin !== null && queryParams.priceMin !== '') {
        queryParams.priceMin = Number(queryParams.priceMin)
      }
      if (queryParams.priceMax !== null && queryParams.priceMax !== '') {
        queryParams.priceMax = Number(queryParams.priceMax)
      }
      if (queryParams.year !== null && queryParams.year !== '') {
        queryParams.year = Number(queryParams.year)
      }
      listProduct(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.productList = response.rows || []
          this.pagination.total = response.total || 0
        } else {
          this.$message.error('获取商品列表失败')
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取商品列表失败：' + error.msg)
      })
    },
    // 查询
    handleQuery() {
      this.pagination.current = 1
      // 确保查询时不会意外打开弹窗
      if (this.stockFormVisible) {
        this.stockFormVisible = false
        this.currentProduct = {}
      }
      if (this.productFormVisible) {
        this.productFormVisible = false
        this.currentProduct = {}
      }
      this.loadProductList()
    },

    // 重置查询
    resetQuery() {
      // 确保重置查询时不会意外打开弹窗
      if (this.stockFormVisible) {
        this.stockFormVisible = false
        this.currentProduct = {}
      }
      if (this.productFormVisible) {
        this.productFormVisible = false
        this.currentProduct = {}
      }
      this.queryForm = {
        name: '',
        brand: '',
        categoryId: null,
        priceMin: null,
        priceMax: null,
        carModel: null,
        year: null,
        orderBy: null,
        pageNum: 1,
        pageSize: 10
      }
      this.pagination.current = 1
      this.loadProductList()
    },
    // 分页大小变化
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadProductList()
    },
    // 页码变化
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadProductList()
    },
    // 选择变化
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
    },
    // 新增商品
    handleAdd() {
      this.editMode = false
      this.currentProduct = {}
      this.productFormVisible = true
    },
    // 编辑商品
    handleEdit(row) {
      this.editMode = true
      this.currentProduct = { ...row }
      this.productFormVisible = true
    },
    // 关闭表单
    handleFormClose() {
      this.productFormVisible = false
      this.currentProduct = {}
    },
    // 表单提交成功
    handleFormSuccess() {
      this.handleQuery()
    },
    // 打开库存管理
    handleStock(row) {
      this.currentProduct = { ...row }
      this.stockFormVisible = true
    },
    // 关闭库存表单
    handleStockFormClose() {
      this.stockFormVisible = false
      this.currentProduct = {}
    },
    // 库存表单提交成功
    handleStockFormSuccess() {
      this.handleQuery()
    },
    // 商品上架
    handlePutOnSale(id) {
      putOnSale(id).then(response => {
        if (response.code === 200) {
          this.$message.success('上架成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '上架失败')
        }
      }).catch(error => {
        this.$message.error('上架失败：' + error.msg)
      })
    },
    // 商品下架
    handlePutOffSale(id) {
      putOffSale(id).then(response => {
        if (response.code === 200) {
          this.$message.success('下架成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '下架失败')
        }
      }).catch(error => {
        this.$message.error('下架失败：' + error.msg)
      })
    },
    // 批量上架
    handleBatchPutOnSale() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请选择要上架的商品')
        return
      }
      this.$confirm('确定要批量上架选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return batchPutOnSale(this.selectedIds)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('批量上架成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '批量上架失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('批量上架失败：' + error.msg)
        }
      })
    },
    // 批量下架
    handleBatchPutOffSale() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请选择要下架的商品')
        return
      }
      this.$confirm('确定要批量下架选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return batchPutOffSale(this.selectedIds)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('批量下架成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '批量下架失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('批量下架失败：' + error.msg)
        }
      })
    },
    // 删除商品
    handleDelete(id) {
      this.$confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delProduct([id])
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('删除成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '删除失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('删除失败：' + error.msg)
        }
      })
    },
    // 批量删除
    handleBatchDelete() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请选择要删除的商品')
        return
      }
      this.$confirm('确定要批量删除选中的商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delProduct(this.selectedIds)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('批量删除成功')
          this.handleQuery()
        } else {
          this.$message.error(response.msg || '批量删除失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败：' + error.msg)
        }
      })
    },
    // 格式化价格
    formatPrice(row, column) {
      return '¥' + Number(row[column.property]).toFixed(2)
    },
    // 获取图片完整路径
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return process.env.VUE_APP_BASE_API + url
    },
    // 获取分类名称
    getCategoryName(categoryId) {
      if (!categoryId) return '-'
      const category = this.categoryList.find(cat => cat.id === categoryId)
      return category ? category.name : '-'
    },
    // 格式化日期
    formatDate(row, column) {
      if (!row[column.property]) return ''
      const date = new Date(row[column.property])
      return date.toLocaleString('zh-CN')
    },
    // 显示商品详情
    showDetail(product) {
      this.currentDetail = product.detail || '暂无商品详情'
      this.detailDialogVisible = true
    },
    // 安全解析subImages JSON
    getSubImages(subImagesStr) {
      try {
        if (!subImagesStr) return []
        const parsed = JSON.parse(subImagesStr)
        return Array.isArray(parsed) ? parsed : []
      } catch (error) {
        console.error('解析附图数据失败:', error)
        return []
      }
    }
  }
}
</script>

<style scoped>
.product-container {
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

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

/* 商品详情对话框样式 */
.detail-dialog >>> .el-dialog__body {
  padding: 20px;
}

/* 表格操作按钮样式 */
.el-table .el-button {
  margin-right: 5px;
  margin-bottom: 5px;
}

/* 查询表单样式 */
.el-form--inline .el-form-item {
  margin-right: 20px;
}

/* 表格斑马纹样式 */
.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #fafafa;
}

/* 表格悬停样式 */
.el-table tbody tr:hover > td {
  background-color: #f5f7fa;
}

/* 卡片样式 */
.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 表格样式 */
.el-table {
  border-radius: 4px;
  overflow: hidden;
}

/* 按钮组样式 */
.el-button-group {
  display: flex;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
