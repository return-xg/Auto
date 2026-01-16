<template>
  <div class="user-product-container">
    <!-- 导航栏 -->
    <el-card class="nav-card" shadow="hover">
      <div class="nav-bar">
        <el-button-group>
          <el-button type="text" icon="el-icon-location" @click="navigateTo('/address')">
            我的地址
          </el-button>
          <el-button type="text" icon="el-icon-star-off" @click="navigateTo('/favorite')">
            我的收藏
          </el-button>
          <el-button type="text" icon="el-icon-shopping-cart-2" @click="navigateTo('/cart')">
            我的购物车
          </el-button>
          <el-button type="text" icon="el-icon-document" @click="navigateTo('/order')">
            我的订单
          </el-button>
        </el-button-group>
      </div>
    </el-card>
    
    <!-- 销量前三轮播图 -->
    <el-card v-if="topProducts.length > 0" class="carousel-card" shadow="hover" style="margin-top: 20px;">
      <div slot="header" class="card-header">
        <span>本周销量前三</span>
      </div>
      <el-carousel
        v-loading="carouselLoading"
        :interval="carouselInterval"
        arrow="always"
        height="300px"
        indicator-position="outside"
      >
        <el-carousel-item v-for="(product, index) in topProducts" :key="product.id">
          <div class="carousel-item" @click="viewProductDetail(product)">
            <el-row :gutter="20" style="height: 100%;">
              <el-col :span="12" style="display: flex; align-items: center; justify-content: center;">
                <el-image
                  :src="getProductCoverImage(product)"
                  fit="contain"
                  style="max-width: 100%; max-height: 240px;"
                >
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
              </el-col>
              <el-col :span="12" style="display: flex; flex-direction: column; justify-content: center;">
                <div class="carousel-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
                <div class="carousel-product-name">{{ product.name }}</div>
                <div class="carousel-product-brand">品牌: {{ product.brand }}</div>
                <div class="carousel-product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ Number(product.price).toFixed(2) }}</span>
                </div>
                <div class="carousel-product-sales">
                  <i class="el-icon-sell"></i>
                  <span>销量: {{ product.sales }}</span>
                </div>
                <el-button
                  :type="product.isFavorite ? 'warning' : 'default'"
                  size="small"
                  @click.stop="toggleFavorite(product)"
                  style="margin-top: 15px; align-self: flex-start;"
                >
                  <i :class="product.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                  {{ product.isFavorite ? '已收藏' : '收藏' }}
                </el-button>
              </el-col>
            </el-row>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-card>
    
    <el-row :gutter="20" style="margin-top: 20px;">
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
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-button type="primary" @click="handleFilter" style="width: 100%;">应用筛选</el-button>
                </el-col>
                <el-col :span="12">
                  <el-button @click="resetFilter" style="width: 100%;">重置</el-button>
                </el-col>
              </el-row>
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
                placeholder="搜索配件名称"
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
                  :src="getProductCoverImage(product)"
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
      favoriteProducts: new Set(),
      // 轮播图相关数据
      carouselLoading: false,
      topProducts: [],
      carouselInterval: 5000
    }
  },
  computed: {
    getProductCoverImage() {
      return (product) => {
        let imageUrl = ''
        if (product.mainImage) {
          imageUrl = product.mainImage
        } else if (product.subImages) {
          try {
            const subImages = JSON.parse(product.subImages)
            if (Array.isArray(subImages) && subImages.length > 0) {
              imageUrl = subImages[0]
            }
          } catch (e) {
            console.error('解析附图失败:', e)
          }
        }

        if (!imageUrl) {
          return '/static/images/default-product.png'
        }

        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
          return imageUrl
        }
        return process.env.VUE_APP_BASE_API + imageUrl
      }
    }
  },
  mounted() {
    this.loadCategories()
    this.loadFavorites()
    this.loadProductList()
    this.loadFilterOptions()
    this.loadTopProducts()
  },
  methods: {
    // 计算时间范围
    getDateRange(weeksAgo = 0) {
      const now = new Date()
      const dayOfWeek = now.getDay() || 7 // 将周日(0)转换为7
      
      // 计算本周一的日期
      const monday = new Date(now)
      monday.setDate(now.getDate() - dayOfWeek + 1 - (weeksAgo * 7))
      
      // 计算本周日的日期
      const sunday = new Date(monday)
      sunday.setDate(monday.getDate() + 6)
      
      // 格式化日期为YYYY-MM-DD格式
      const formatDate = (date) => {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return {
        startDate: formatDate(monday),
        endDate: formatDate(sunday)
      }
    },
    
    // 获取销量前三的商品
    loadTopProducts(weeksAgo = 0) {
      this.carouselLoading = true
      
      const { startDate, endDate } = this.getDateRange(weeksAgo)
      
      // 调用商品列表API，按销量降序排序，只取前3个
      const queryParams = {
        pageNum: 1,
        pageSize: 3,
        status: 1,
        orderBy: 'sales_desc',
        startTime: startDate,
        endTime: endDate
      }
      
      listProduct(queryParams).then(response => {
        this.carouselLoading = false
        if (response.code === 200) {
          const products = response.rows || []
          if (products.length > 0) {
            // 如果有数据，直接使用
            this.topProducts = products.map(product => ({
              ...product,
              isFavorite: this.favoriteProducts.has(product.id)
            }))
          } else if (weeksAgo < 4) {
            // 如果没有数据且查找周数小于4，递归查找上一周
            this.loadTopProducts(weeksAgo + 1)
          } else {
            // 如果查找了4周都没有数据，显示空
            this.topProducts = []
          }
        }
      }).catch(error => {
        this.carouselLoading = false
        console.error('获取销量前三商品失败:', error)
      })
    },
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
      const spec = product.specText || null
      addToCart({
        userId,
        productId: product.id,
        quantity,
        spec
      }).then(response => {
        if (response.code === 200) {
          this.$message.success(`已添加${quantity}件商品到购物车`)
        } else {
          this.$message.error(response.msg || '添加失败')
        }
      }).catch(error => {
        this.$message.error('添加失败')
      })
    },
    navigateTo(path) {
      console.log('导航点击事件触发，目标路径:', path)
      console.log('当前路由:', this.$route.path)
      try {
        this.$router.push(path)
        console.log('路由跳转执行成功')
      } catch (error) {
        console.error('路由跳转失败:', error)
      }
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

.nav-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.nav-bar {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

.el-button-group {
  width: 100%;
  display: flex;
  justify-content: space-around;
  max-width: 800px;
}

.el-button-group .el-button {
  font-size: 14px;
  padding: 8px 20px;
  transition: all 0.3s;
}

.el-button-group .el-button:hover {
  color: #409EFF;
  background-color: #ecf5ff;
  border-color: #b3d8ff;
}

.carousel-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.carousel-item {
  cursor: pointer;
  height: 100%;
  display: flex;
  align-items: center;
}

.carousel-rank {
  position: absolute;
  top: 10px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: white;
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
}

.carousel-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e0e0e0);
  box-shadow: 0 4px 12px rgba(192, 192, 192, 0.3);
}

.carousel-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #f0c040);
  box-shadow: 0 4px 12px rgba(205, 127, 50, 0.3);
}

.carousel-product-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.carousel-product-brand {
  font-size: 14px;
  color: #909399;
  margin-bottom: 15px;
}

.carousel-product-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 15px;
}

.carousel-product-price .price-symbol {
  font-size: 16px;
  color: #f56c6c;
  margin-right: 2px;
}

.carousel-product-price .price-value {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.carousel-product-sales {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.carousel-product-sales i {
  margin-right: 5px;
  color: #409eff;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
