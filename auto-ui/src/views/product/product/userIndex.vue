<template>
  <div class="user-product-container">
    <el-row :gutter="20">
      <el-col :span="4">
        <el-card class="category-card">
          <div slot="header" class="card-header">
            <span>商品分类</span>
          </div>
          <el-menu
            :default-active="activeCategory"
            @select="handleCategorySelect"
            text-color="#303133"
            active-text-color="#409EFF"
          >
            <el-menu-item index="">
              <i class="el-icon-s-grid"></i>
              <span>全部商品</span>
            </el-menu-item>
            <el-menu-item
              v-for="category in categoryList"
              :key="category.id"
              :index="String(category.id)"
            >
              <i class="el-icon-menu"></i>
              <span>{{ category.name }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>

        <el-card class="filter-card" style="margin-top: 20px;">
          <div slot="header" class="card-header">
            <span>筛选条件</span>
          </div>
          <el-form :model="filterForm" label-position="top" size="small">
            <el-form-item label="品牌">
              <el-select v-model="filterForm.brand" placeholder="选择品牌" clearable style="width: 100%;">
                <el-option
                  v-for="brand in brandOptions"
                  :key="brand"
                  :label="brand"
                  :value="brand"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="适配车型">
              <el-select v-model="filterForm.carModel" placeholder="选择车型" clearable style="width: 100%;">
                <el-option
                  v-for="model in carModelOptions"
                  :key="model"
                  :label="model"
                  :value="model"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="适用年份">
              <el-select v-model="filterForm.year" placeholder="选择年份" clearable style="width: 100%;">
                <el-option
                  v-for="year in yearOptions"
                  :key="year"
                  :label="year"
                  :value="year"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="价格区间">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-input v-model="filterForm.priceMin" type="number" placeholder="最低价" />
                </el-col>
                <el-col :span="12">
                  <el-input v-model="filterForm.priceMax" type="number" placeholder="最高价" />
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFilter" style="width: 100%;">应用筛选</el-button>
              <el-button @click="resetFilter" style="width: 100%; margin-top: 10px;">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="20">
        <el-card class="search-card">
          <el-row :gutter="20">
            <el-col :span="18">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索配件名称、品牌、车型、生产年份等"
                clearable
                @keyup.enter.native="handleSearch"
              >
                <el-button slot="append" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort">
                <el-option label="默认排序" value="" />
                <el-option label="价格从低到高" value="price_asc" />
                <el-option label="价格从高到低" value="price_desc" />
                <el-option label="销量从高到低" value="sales_desc" />
                <el-option label="上架时间" value="time_desc" />
              </el-select>
            </el-col>
          </el-row>
        </el-card>

        <el-card class="product-list-card" style="margin-top: 20px;">
          <div v-loading="loading" class="product-grid">
            <div
              v-for="product in productList"
              :key="product.id"
              class="product-item"
              @click="viewProductDetail(product)"
            >
              <div class="product-image">
                <el-image
                  :src="product.mainImage || '/static/images/default-product.png'"
                  fit="cover"
                  style="width: 100%; height: 200px;"
                >
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <div v-if="product.isHot === 1" class="tag-hot">热销</div>
                <div v-if="product.isNew === 1" class="tag-new">新品</div>
              </div>
              <div class="product-info">
                <div class="product-name" :title="product.name">{{ product.name }}</div>
                <div class="product-brand">品牌: {{ product.brand }}</div>
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ Number(product.price).toFixed(2) }}</span>
                </div>
                <div class="product-meta">
                  <span>销量: {{ product.sales }}</span>
                  <span>库存: {{ product.stock }}</span>
                </div>
                <div class="product-actions">
                  <el-button
                    :type="product.isFavorite ? 'warning' : 'default'"
                    size="small"
                    @click.stop="toggleFavorite(product)"
                  >
                    <i :class="product.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                    {{ product.isFavorite ? '已收藏' : '收藏' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="productList.length === 0 && !loading" class="empty-state">
            <i class="el-icon-goods"></i>
            <p>暂无商品</p>
          </div>

          <div class="pagination-container">
            <el-pagination
              :current-page.sync="pagination.current"
              :page-size.sync="pagination.size"
              :total="pagination.total"
              :page-sizes="[12, 24, 48]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <product-detail-dialog
      :visible="detailDialogVisible"
      :product="currentProduct"
      @close="detailDialogVisible = false"
      @add-to-cart="handleAddToCartFromDetail"
    />
  </div>
</template>

<script>
import { listProduct, searchProduct, getFilterOptions } from '@/api/product/product'
import { listCategory } from '@/api/category/category'
import { addToCart } from '@/api/cart/cart'
import { addFavorite, delFavorite, checkFavorite, listFavorite } from '@/api/favorite/favorite'
import ProductDetailDialog from './ProductDetailDialog.vue'

export default {
  name: 'UserProductIndex',
  components: {
    ProductDetailDialog
  },
  data() {
    return {
      loading: false,
      productList: [],
      categoryList: [],
      activeCategory: '',
      searchKeyword: '',
      sortBy: '',
      filterForm: {
        brand: '',
        carModel: '',
        year: '',
        priceMin: null,
        priceMax: null
      },
      brandOptions: [],
      carModelOptions: [],
      yearOptions: [],
      pagination: {
        current: 1,
        size: 12,
        total: 0
      },
      detailDialogVisible: false,
      currentProduct: {},
      favoriteProducts: new Set()
    }
  },
  mounted() {
    this.loadCategories()
    this.loadFavorites()
    this.loadProductList()
    this.loadFilterOptions()
  },
  methods: {
    loadCategories() {
      listCategory({ pageNum: 1, pageSize: 100 }).then(response => {
        if (response.code === 200) {
          this.categoryList = response.rows || []
        }
      }).catch(error => {
        console.error('获取分类列表失败:', error)
      })
    },
    loadFavorites() {
      listFavorite().then(response => {
        if (response.code === 200) {
          const favorites = response.rows || []
          this.favoriteProducts = new Set(favorites.map(f => f.productId))
        }
      }).catch(error => {
        console.error('获取收藏列表失败:', error)
      })
    },
    loadProductList() {
      this.loading = true
      const queryParams = {
        pageNum: this.pagination.current,
        pageSize: this.pagination.size,
        status: 1
      }
      
      if (this.activeCategory) {
        queryParams.categoryId = Number(this.activeCategory)
      }
      
      if (this.searchKeyword) {
        queryParams.keyword = this.searchKeyword
      }
      
      if (this.filterForm.brand) {
        queryParams.brand = this.filterForm.brand
      }
      
      if (this.filterForm.carModel) {
        queryParams.carModel = this.filterForm.carModel
      }
      
      if (this.filterForm.year) {
        queryParams.year = Number(this.filterForm.year)
      }
      
      if (this.filterForm.priceMin) {
        queryParams.priceMin = Number(this.filterForm.priceMin)
      }
      
      if (this.filterForm.priceMax) {
        queryParams.priceMax = Number(this.filterForm.priceMax)
      }
      
      if (this.sortBy) {
        const sortMap = {
          price_asc: 'price_asc',
          price_desc: 'price_desc',
          sales_desc: 'sales_desc',
          time_desc: 'time_desc'
        }
        queryParams.orderBy = sortMap[this.sortBy]
      }

      const apiCall = this.searchKeyword ? searchProduct : listProduct
      
      apiCall(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.productList = (response.rows || []).map(product => ({
            ...product,
            isFavorite: this.favoriteProducts.has(product.id)
          }))
          this.pagination.total = response.total || 0
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取商品列表失败')
      })
    },
    loadFilterOptions() {
      getFilterOptions(this.activeCategory).then(response => {
        if (response.code === 200) {
          const data = response.data || {}
          this.brandOptions = data.brands || []
          this.carModelOptions = data.carModels || []
          this.yearOptions = data.years || []
        }
      }).catch(error => {
        console.error('获取筛选选项失败:', error)
      })
    },
    handleCategorySelect(index) {
      this.activeCategory = index
      this.pagination.current = 1
      this.loadFilterOptions()
      this.loadProductList()
    },
    handleSearch() {
      this.pagination.current = 1
      this.loadProductList()
    },
    handleSort() {
      this.pagination.current = 1
      this.loadProductList()
    },
    handleFilter() {
      this.pagination.current = 1
      this.loadProductList()
    },
    resetFilter() {
      this.filterForm = {
        brand: '',
        carModel: '',
        year: '',
        priceMin: null,
        priceMax: null
      }
      this.pagination.current = 1
      this.loadProductList()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadProductList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadProductList()
    },
    viewProductDetail(product) {
      this.currentProduct = product
      this.detailDialogVisible = true
    },
    toggleFavorite(product) {
      if (product.isFavorite) {
        delFavorite(product.id).then(response => {
          if (response.code === 200) {
            product.isFavorite = false
            this.favoriteProducts.delete(product.id)
            this.$message.success('已取消收藏')
          }
        }).catch(error => {
          this.$message.error('操作失败')
        })
      } else {
        addFavorite(product.id).then(response => {
          if (response.code === 200) {
            product.isFavorite = true
            this.favoriteProducts.add(product.id)
            this.$message.success('已添加到收藏')
          }
        }).catch(error => {
          this.$message.error('操作失败')
        })
      }
    },
    handleAddToCartFromDetail(product) {
      const userId = this.$store.getters.id
      const quantity = product.quantity || 1
      addToCart({
        userId,
        productId: product.id,
        quantity
      }).then(response => {
        if (response.code === 200) {
          this.$message.success(`已添加${quantity}件商品到购物车`)
        } else {
          this.$message.error(response.msg || '添加失败')
        }
      }).catch(error => {
        this.$message.error('添加失败')
      })
    }
  }
}
</script>

<style scoped lang="scss">
.user-product-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.category-card, .filter-card, .search-card, .product-list-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.product-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 48px;
  }

  .tag-hot, .tag-new {
    position: absolute;
    top: 10px;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    color: white;
    font-weight: bold;
  }

  .tag-hot {
    left: 10px;
    background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  }

  .tag-new {
    right: 10px;
    background: linear-gradient(135deg, #51cf66, #40c057);
  }
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-brand {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.product-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;

  .price-symbol {
    font-size: 14px;
    color: #f56c6c;
    margin-right: 2px;
  }

  .price-value {
    font-size: 20px;
    font-weight: bold;
    color: #f56c6c;
  }
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.product-actions {
  display: flex;
  gap: 10px;

  .el-button {
    flex: 1;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;

  i {
    font-size: 64px;
    margin-bottom: 20px;
    display: block;
  }

  p {
    font-size: 16px;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
