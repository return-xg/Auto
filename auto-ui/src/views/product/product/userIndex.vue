<template>
  <div class="user-product-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <div class="nav-left">
          <h1 class="logo">AutoParts</h1>
        </div>
        <div class="nav-center">
          <el-button-group>
            <el-button type="text" icon="el-icon-location" @click="navigateTo('/address')" class="nav-btn">
              我的地址
            </el-button>
            <el-button type="text" icon="el-icon-star-off" @click="navigateTo('/favorite')" class="nav-btn">
              我的收藏
            </el-button>
            <el-button type="text" icon="el-icon-shopping-cart-2" @click="navigateTo('/cart')" class="nav-btn">
              我的购物车
            </el-button>
            <el-button type="text" icon="el-icon-document" @click="navigateTo('/order')" class="nav-btn">
              我的订单
            </el-button>
          </el-button-group>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="search-container">
        <el-row class="search-row">
          <el-col :span="24">
            <div class="search-layout">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索配件名称"
                clearable
                @keyup.enter.native="handleSearch"
                class="search-input-custom"
              ></el-input>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleSearch"
                class="search-button-custom"
              >搜索</el-button>
            </div>
          </el-col>
          <el-col :span="24" style="display: flex; justify-content: center; margin-top: 12px;">
            <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort" class="sort-select" style="width: 200px;">
              <el-option label="默认排序" value="" />
              <el-option label="价格从低到高" value="price_asc" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="销量从高到低" value="sales_desc" />
              <el-option label="新品优先" value="time_desc" />
            </el-select>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 销量前三轮播图 -->
    <div v-if="topProducts.length > 0" class="carousel-section">
      <div class="section-title">
        <h2>本周销量榜</h2>
        <span class="title-desc">热销商品推荐</span>
      </div>
      <el-carousel
        v-loading="carouselLoading"
        :interval="carouselInterval"
        arrow="hover"
        height="350px"
        indicator-position="outside"
        class="featured-carousel"
      >
        <el-carousel-item v-for="(product, index) in topProducts" :key="product.id">
          <div class="carousel-item" @click="viewProductDetail(product)">
            <el-row :gutter="40" style="height: 100%; align-items: center;">
              <el-col :span="12" class="carousel-img-col">
                <div class="carousel-img-wrapper">
                  <el-image
                    :src="getProductCoverImage(product)"
                    fit="contain"
                    style="max-width: 100%; max-height: 280px;"
                    class="carousel-img"
                  >
                    <div slot="error" class="image-error">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                </div>
              </el-col>
              <el-col :span="12" class="carousel-info-col">
                <div class="carousel-badge">
                  <span class="badge-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                  <span class="badge-label">TOP</span>
                </div>
                <h3 class="carousel-product-name">{{ product.name }}</h3>
                <div class="carousel-product-brand">品牌: <span class="brand-name">{{ product.brand }}</span></div>
                <div class="carousel-product-tags">
                  <el-tag v-if="product.isHot === 1" type="danger" size="small">热销</el-tag>
                  <el-tag v-if="product.isNew === 1" type="success" size="small">新品</el-tag>
                </div>
                <div class="carousel-product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ Number(product.price).toFixed(2) }}</span>
                  <span class="price-unit">元</span>
                </div>
                <div class="carousel-product-sales">
                  <i class="el-icon-sell"></i>
                  <span>销量: {{ product.sales }}件</span>
                </div>
                <div class="carousel-actions">
                  <el-button
                    :type="product.isFavorite ? 'warning' : 'default'"
                    size="medium"
                    @click.stop="toggleFavorite(product)"
                    class="btn-favorite"
                  >
                    <i :class="product.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                    {{ product.isFavorite ? '已收藏' : '收藏' }}
                  </el-button>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="main-content">
      <el-row :gutter="24">
        <!-- 左侧分类与筛选 -->
        <el-col :span="5" class="sidebar">
          <!-- 商品分类 -->
          <el-card class="category-card" shadow="hover">
            <div slot="header" class="card-header">
              <h3>商品分类</h3>
            </div>
            <el-menu
              :default-active="activeCategory"
              @select="handleCategorySelect"
              text-color="#333"
              active-text-color="#ff6b6b"
              background-color="#fff"
              class="category-menu"
            >
              <el-menu-item index="" class="menu-item">
                <i class="el-icon-s-grid menu-icon"></i>
                <span>全部商品</span>
              </el-menu-item>
              <el-menu-item
                v-for="category in categoryList"
                :key="category.id"
                :index="String(category.id)"
                class="menu-item"
              >
                <i class="el-icon-menu menu-icon"></i>
                <span>{{ category.name }}</span>
              </el-menu-item>
            </el-menu>
          </el-card>

          <!-- 筛选条件 -->
          <el-card class="filter-card" shadow="hover" style="margin-top: 20px;">
            <div slot="header" class="card-header">
              <h3>筛选条件</h3>
            </div>
            <el-form :model="filterForm" label-position="top" size="small" class="filter-form">
              <el-form-item label="品牌" class="filter-item">
                <el-select v-model="filterForm.brand" placeholder="选择品牌" clearable style="width: 100%;" class="filter-select">
                  <el-option
                    v-for="brand in brandOptions"
                    :key="brand"
                    :label="brand"
                    :value="brand"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="价格区间" class="filter-item">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-input v-model="filterForm.priceMin" type="number" placeholder="最低价" class="price-input" />
                    <el-button type="primary" @click="handleFilter" class="btn-filter btn-align">应用筛选</el-button>
                  </el-col>
                  <el-col :span="12">
                    <el-input v-model="filterForm.priceMax" type="number" placeholder="最高价" class="price-input" />
                    <el-button @click="resetFilter" class="btn-reset btn-align">重置</el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- 右侧商品列表 -->
        <el-col :span="19" class="content-area">
          <!-- 商品列表区域 -->
          <div class="product-section">
            <div class="section-header">
              <h2 class="section-title">全部商品</h2>
              <span class="product-count">{{ pagination.total }} 件商品</span>
            </div>

            <div v-loading="loading" class="product-grid">
              <div
                v-for="product in productList"
                :key="product.id"
                class="product-item"
                @click="viewProductDetail(product)"
              >
                <div class="product-image-wrapper">
                  <div class="product-image">
                    <el-image
                      :src="getProductCoverImage(product)"
                      fit="cover"
                      style="width: 100%; height: 240px;"
                      class="product-img"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="product-tags">
                      <span v-if="product.isHot === 1" class="tag tag-hot">热销</span>
                      <span v-if="product.isNew === 1" class="tag tag-new">新品</span>
                    </div>
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-name" :title="product.name">{{ product.name }}</div>
                  <div class="product-brand">品牌: <span class="brand-text">{{ product.brand }}</span></div>
                  <div class="product-price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ Number(product.price).toFixed(2) }}</span>
                  </div>
                  <div class="product-meta">
                    <span class="sales">销量: {{ product.sales }}</span>
                    <span class="stock" :class="{ 'low-stock': product.stock < 10 }">{{ product.stock > 0 ? `库存: ${product.stock}` : '缺货' }}</span>
                  </div>
                  <div class="product-actions">
                    <el-button
                      :type="product.isFavorite ? 'warning' : 'default'"
                      size="small"
                      @click.stop="toggleFavorite(product)"
                      class="btn-favorite-small"
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
              <el-button type="primary" @click="resetFilter">清空筛选</el-button>
            </div>

            <!-- 分页 -->
            <div class="pagination-container">
              <el-pagination
                :current-page.sync="pagination.current"
                :page-size.sync="pagination.size"
                :total="pagination.total"
                :page-sizes="[12, 24, 48]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                class="pagination"
              />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

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
        priceMin: null,
        priceMax: null
      },
      brandOptions: [],
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
    this.loadFavorites().then(() => {
      this.loadProductList()
      this.loadFilterOptions()
      this.loadTopProducts()
    })
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
      return listFavorite().then(response => {
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
            // 更新当前商品对象
            product.isFavorite = false
            this.favoriteProducts.delete(product.id)
            // 更新topProducts数组中的对应商品
            this.updateTopProductFavoriteStatus(product.id, false)
            this.$message.success('已取消收藏')
          }
        }).catch(error => {
          this.$message.error('操作失败')
        })
      } else {
        addFavorite(product.id).then(response => {
          if (response.code === 200) {
            // 更新当前商品对象
            product.isFavorite = true
            this.favoriteProducts.add(product.id)
            // 更新topProducts数组中的对应商品
            this.updateTopProductFavoriteStatus(product.id, true)
            this.$message.success('已添加到收藏')
          }
        }).catch(error => {
          this.$message.error('操作失败')
        })
      }
    },
    // 更新轮播图商品的收藏状态
    updateTopProductFavoriteStatus(productId, isFavorite) {
      const index = this.topProducts.findIndex(p => p.id === productId)
      if (index !== -1) {
        this.$set(this.topProducts, index, {
          ...this.topProducts[index],
          isFavorite
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
    // 用户下拉菜单命令处理
    handleUserCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/profile')
          break
        case 'settings':
          this.$router.push('/settings')
          break
        case 'logout':
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
          })
          break
        default:
          break
      }
    },
    navigateTo(path) {
      try {
        this.$router.push(path)
      } catch (error) {
        console.error('路由跳转失败:', error)
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 主题色彩变量
$primary-color: #ff6b6b;
$secondary-color: #4ecdc4;
$success-color: #51cf66;
$warning-color: #ffd43b;
$danger-color: #ff6b6b;
$info-color: #4dabf7;
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$bg-primary: #ffffff;
$bg-secondary: #f8f9fa;
$bg-tertiary: #f0f2f5;
$border-color: #e9ecef;
$shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
$shadow-md: 0 4px 16px rgba(0, 0, 0, 0.12);
$shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.16);
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$transition: all 0.3s ease;

// 全局容器
.user-product-container {
  background-color: $bg-tertiary;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

// 顶部导航栏
.top-nav {
  background-color: $bg-primary;
  box-shadow: $shadow-sm;
  position: sticky;
  top: 0;
  z-index: 100;

  .nav-content {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 20px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .nav-left {
    .logo {
      font-size: 24px;
      font-weight: bold;
      color: $primary-color;
      margin: 0;
      background: linear-gradient(135deg, $primary-color, $secondary-color);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .nav-center {
    .nav-btn {
      font-size: 14px;
      color: $text-secondary;
      padding: 8px 20px;
      transition: $transition;
      border-radius: $radius-md;

      &:hover {
        color: $primary-color;
        background-color: rgba($primary-color, 0.05);
      }
    }
  }

  .nav-right {
    .user-dropdown {
      display: flex;
      align-items: center;
      gap: 6px;
      color: $text-secondary;
      cursor: pointer;
      padding: 8px 16px;
      border-radius: $radius-md;
      transition: $transition;

      &:hover {
        color: $primary-color;
        background-color: rgba($primary-color, 0.05);
      }
    }
  }
}

// 搜索区域
  .search-section {
    background: linear-gradient(135deg, $primary-color, $secondary-color);
    padding: 20px 0;

    .search-container {
      max-width: 1280px;
      margin: 0 auto;
      padding: 0 20px;
    }

    .search-row {
      align-items: center;
      justify-content: center;
    }

    // 新的搜索布局，使用flexbox确保对齐
    .search-layout {
      display: flex;
      align-items: stretch;
      width: 100%;
      max-width: 800px;
      margin: 0 auto;
      background: $bg-primary;
      border-radius: $radius-lg;
      box-shadow: $shadow-md;
      overflow: hidden;
      height: 37px; /* 明确设置高度 */
    }

    // 确保el-input组件及其所有子元素都继承高度
    .search-input-custom {
      flex: 1;
      border: none !important;
      box-shadow: none !important;
      margin: 0 !important;
      padding: 0 !important;
      height: 100% !important;

      /* 确保el-input内部容器占满高度 */
      .el-input {
        height: 100% !important;
        width: 100% !important;
        margin: 0 !important;
        padding: 0 !important;
      }

      /* 输入框内部元素 */
      .el-input__inner {
        height: 100% !important;
        line-height: 56px !important;
        font-size: 16px;
        padding: 0 15px !important;
        border: none !important;
        border-radius: 0 !important;
        box-shadow: none !important;
      }

      /* 输入框图标 */
      .el-input__icon {
        line-height: 56px !important;
      }

      /* 清除按钮 */
      .el-input__clear {
        line-height: 56px !important;
      }

      /* 确保聚焦时没有额外阴影 */
      &:focus, &.is-focus {
        box-shadow: none !important;
      }
    }

    // 自定义搜索按钮
    .search-button-custom {
      height: 100% !important;
      width: 100px;
      border: none !important;
      border-radius: 0 !important;
      background-color: $primary-color !important;
      color: white !important;
      font-size: 16px !important;
      padding: 0 20px !important;
      margin: 0 !important;
      transition: $transition;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;

      &:hover {
        background-color: darken($primary-color, 5%) !important;
        transform: translateY(-1px);
        box-shadow: $shadow-md;
      }

      // 修复按钮内图标对齐
      .el-icon {
        font-size: 18px;
        margin-right: 4px;
      }
    }

    .sort-select {
      width: 100%;
      height: 56px;
      font-size: 14px;
      border-radius: $radius-lg;
    }
  }

// 轮播图区域
.carousel-section {
  max-width: 1280px;
  margin: 30px auto;
  padding: 0 20px;

  .section-title {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 28px;
      font-weight: bold;
      color: $text-primary;
      margin: 0 0 8px 0;
    }

    .title-desc {
      font-size: 14px;
      color: $text-tertiary;
    }
  }

  .featured-carousel {
    background: $bg-primary;
    border-radius: $radius-lg;
    overflow: hidden;
    box-shadow: $shadow-md;

    .el-carousel__container {
      height: 350px;
    }

    .el-carousel__arrow {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: rgba(0, 0, 0, 0.3);
      transition: $transition;

      &:hover {
        background-color: rgba(0, 0, 0, 0.5);
      }
    }
  }

  .carousel-item {
    cursor: pointer;
    height: 100%;
    padding: 0 40px;
  }

  .carousel-img-col {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .carousel-img-wrapper {
    background: $bg-secondary;
    border-radius: $radius-lg;
    padding: 20px;
    transition: $transition;

    &:hover {
      transform: scale(1.02);
    }
  }

  .carousel-img {
    transition: $transition;
  }

  .carousel-info-col {
    padding: 20px;
  }

  .carousel-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;

    .badge-rank {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      color: white;
      font-size: 20px;
      font-weight: bold;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .badge-label {
      color: $text-tertiary;
      font-size: 12px;
      font-weight: bold;
    }

    .rank-1 {
      background: linear-gradient(135deg, $warning-color, #ffc107);
    }

    .rank-2 {
      background: linear-gradient(135deg, #adb5bd, #6c757d);
    }

    .rank-3 {
      background: linear-gradient(135deg, #cd7f32, #b87333);
    }
  }

  .carousel-product-name {
    font-size: 24px;
    font-weight: bold;
    color: $text-primary;
    margin-bottom: 12px;
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .carousel-product-brand {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 12px;

    .brand-name {
      color: $primary-color;
      font-weight: bold;
    }
  }

  .carousel-product-tags {
    margin-bottom: 20px;
    display: flex;
    gap: 8px;
  }

  .carousel-product-price {
    display: flex;
    align-items: baseline;
    margin-bottom: 16px;

    .price-symbol {
      font-size: 18px;
      color: $danger-color;
      margin-right: 4px;
    }

    .price-value {
      font-size: 36px;
      font-weight: bold;
      color: $danger-color;
      line-height: 1;
    }

    .price-unit {
      font-size: 14px;
      color: $danger-color;
      margin-left: 4px;
    }
  }

  .carousel-product-sales {
    font-size: 16px;
    color: $text-secondary;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 8px;

    i {
      color: $primary-color;
    }
  }

  .carousel-actions {
    display: flex;
    gap: 12px;

    .btn-add-cart {
      background-color: $primary-color;
      border-color: $primary-color;
      padding: 10px 24px;
      font-size: 14px;
      font-weight: bold;
      border-radius: $radius-md;
      transition: $transition;

      &:hover {
        background-color: darken($primary-color, 5%);
        border-color: darken($primary-color, 5%);
        transform: translateY(-2px);
        box-shadow: $shadow-md;
      }
    }

    .btn-favorite {
      padding: 10px 24px;
      font-size: 14px;
      border-radius: $radius-md;
      transition: $transition;

      &:hover {
        transform: translateY(-2px);
        box-shadow: $shadow-md;
      }
    }
  }
}

// 主内容区域
.main-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

// 侧边栏
.sidebar {
  .category-card, .filter-card {
    background: $bg-primary;
    border: none;
    border-radius: $radius-lg;
    box-shadow: $shadow-sm;
    overflow: hidden;
    transition: $transition;

    &:hover {
      box-shadow: $shadow-md;
    }
  }

  .card-header {
    background-color: $bg-secondary;
    border-bottom: 1px solid $border-color;
    padding: 16px 20px;

    h3 {
      font-size: 16px;
      font-weight: bold;
      color: $text-primary;
      margin: 0;
    }
  }

  .category-menu {
    border: none;
    background: transparent;

    .menu-item {
      padding: 12px 20px;
      transition: $transition;
      border-radius: $radius-md;
      margin: 4px 12px;

      &:hover {
        color: $primary-color;
        background-color: rgba($primary-color, 0.05);
      }

      &.is-active {
        color: $primary-color;
        background-color: rgba($primary-color, 0.05);
        font-weight: bold;
      }
    }

    .menu-icon {
      color: $primary-color;
    }
  }

  .filter-form {
    padding: 20px;

    .filter-item {
      margin-bottom: 20px;

      .el-form-item__label {
        font-size: 14px;
        font-weight: bold;
        color: $text-primary;
        margin-bottom: 8px;
      }
    }

    .filter-select, .price-input {
      width: 100%;
      border-radius: $radius-md;
      border: 1px solid $border-color;
      transition: $transition;

      &:focus {
        border-color: $primary-color;
        box-shadow: 0 0 0 2px rgba($primary-color, 0.2);
      }
    }

    .btn-align {
      width: 100%;
      margin-top: 10px !important;
      padding: 8px;
      font-size: 13px;
      border-radius: $radius-md;
      transition: $transition;
      height: 36px;
    }

    .btn-filter {
      background-color: $primary-color;
      border-color: $primary-color;
      font-weight: bold;

      &:hover {
        background-color: darken($primary-color, 5%);
        border-color: darken($primary-color, 5%);
      }
    }

    .btn-reset {
      &:hover {
        color: $primary-color;
        border-color: $primary-color;
      }
    }
  }
}

// 内容区域
.content-area {
  .product-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 2px solid $border-color;

      .section-title {
        font-size: 24px;
        font-weight: bold;
        color: $text-primary;
        margin: 0;
      }

      .product-count {
        font-size: 14px;
        color: $text-tertiary;
      }
    }

    // 商品网格
    .product-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
      margin-bottom: 30px;
    }

    // 商品卡片
    .product-item {
      background: $bg-primary;
      border: 1px solid $border-color;
      border-radius: $radius-lg;
      overflow: hidden;
      cursor: pointer;
      transition: $transition;
      box-shadow: $shadow-sm;

      &:hover {
        transform: translateY(-8px);
        box-shadow: $shadow-lg;
        border-color: $primary-color;
      }
    }

    .product-image-wrapper {
      position: relative;
      overflow: hidden;
      border-radius: $radius-lg $radius-lg 0 0;
    }

    .product-image {
      position: relative;
      width: 100%;
      height: 240px;
      overflow: hidden;
      background-color: $bg-secondary;

      .product-img {
        transition: $transition;
      }

      &:hover .product-img {
        transform: scale(1.05);
      }

      .product-tags {
        position: absolute;
        top: 12px;
        left: 12px;
        display: flex;
        gap: 8px;
      }

      .tag {
        padding: 4px 12px;
        font-size: 12px;
        font-weight: bold;
        border-radius: $radius-lg;
        color: white;
      }

      .tag-hot {
        background: linear-gradient(135deg, $danger-color, #ee5a6f);
      }

      .tag-new {
        background: linear-gradient(135deg, $success-color, #40c057);
      }

      .hover-btn {
        background-color: $primary-color;
        border-color: $primary-color;
        padding: 8px 20px;
        font-size: 14px;
        border-radius: $radius-md;
        transition: $transition;

        &:hover {
          background-color: darken($primary-color, 5%);
          border-color: darken($primary-color, 5%);
        }
      }
    }

    .product-info {
      padding: 20px;
    }

    .product-name {
      font-size: 16px;
      font-weight: bold;
      color: $text-primary;
      margin-bottom: 8px;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .product-brand {
      font-size: 14px;
      color: $text-secondary;
      margin-bottom: 12px;

      .brand-text {
        color: $primary-color;
        font-weight: bold;
      }
    }

    .product-price {
      display: flex;
      align-items: baseline;
      margin-bottom: 12px;

      .price-symbol {
        font-size: 14px;
        color: $danger-color;
        margin-right: 4px;
      }

      .price-value {
        font-size: 24px;
        font-weight: bold;
        color: $danger-color;
      }
    }

    .product-meta {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: $text-tertiary;
      margin-bottom: 16px;

      .sales {
        display: flex;
        align-items: center;
        gap: 4px;
      }

      .stock {
        display: flex;
        align-items: center;
        gap: 4px;

        &.low-stock {
          color: $warning-color;
          font-weight: bold;
        }
      }
    }

    .product-actions {
      .btn-favorite-small {
        width: 100%;
        border-radius: $radius-md;
        transition: $transition;

        &:hover {
          transform: translateY(-2px);
        }
      }
    }

    // 空状态
    .empty-state {
      text-align: center;
      padding: 80px 20px;
      background: $bg-primary;
      border-radius: $radius-lg;
      box-shadow: $shadow-sm;

      i {
        font-size: 80px;
        color: $text-tertiary;
        margin-bottom: 20px;
        display: block;
      }

      p {
        font-size: 16px;
        color: $text-secondary;
        margin-bottom: 24px;
      }

      .el-button {
        background-color: $primary-color;
        border-color: $primary-color;
        padding: 10px 32px;
        border-radius: $radius-md;
        transition: $transition;

        &:hover {
          background-color: darken($primary-color, 5%);
          border-color: darken($primary-color, 5%);
        }
      }
    }

    // 分页
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 40px;
      padding-top: 20px;
      border-top: 1px solid $border-color;

      .pagination {
        .el-pagination__sizes .el-input .el-input__inner {
          border-radius: $radius-md;
        }

        .el-pager li {
          border-radius: $radius-md;
          margin: 0 4px;
          transition: $transition;

          &:hover {
            color: $primary-color;
          }

          &.is-active {
            background-color: $primary-color;
            border-color: $primary-color;
            color: white;
          }
        }

        .el-pagination__prev, .el-pagination__next {
          border-radius: $radius-md;
          transition: $transition;

          &:hover {
            color: $primary-color;
          }
        }
      }
    }
  }
}

// 图片错误状态
.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: $bg-secondary;
  color: $text-tertiary;
  font-size: 48px;
}

// 响应式设计
@media (max-width: 1200px) {
  .sidebar {
    .category-menu {
      .menu-item {
        padding: 10px 16px;
      }
    }
  }

  .content-area {
    .product-grid {
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
    }
  }
}

@media (max-width: 992px) {
  .top-nav {
    .nav-content {
      flex-direction: column;
      height: auto;
      padding: 12px 20px;
      gap: 12px;
    }
  }

  .carousel-section {
    .carousel-item {
      padding: 0 20px;
    }

    .carousel-img-wrapper {
      padding: 16px;
    }

    .carousel-img {
      max-height: 220px;
    }

    .carousel-product-name {
      font-size: 20px;
    }

    .carousel-product-price .price-value {
      font-size: 30px;
    }
  }

  .main-content {
    .el-row {
      flex-direction: column;
    }

    .sidebar {
      width: 100%;
      margin-bottom: 24px;
    }

    .content-area {
      width: 100%;
    }
  }
}

@media (max-width: 768px) {
  .search-section {
    .search-row {
      flex-direction: column;
      gap: 16px;
    }

    .search-box {
      padding: 6px;
    }

    .search-input, .sort-select {
      height: 44px;
    }
  }

  .carousel-section {
    margin: 20px auto;

    .featured-carousel {
      .el-carousel__container {
        height: auto;
      }

      .el-carousel__item {
        padding: 20px 0;
      }
    }

    .carousel-item {
      flex-direction: column;
      gap: 20px;
      text-align: center;
    }

    .carousel-info-col {
      padding: 0 20px;
    }

    .carousel-actions {
      justify-content: center;
    }
  }

  .content-area {
    .product-grid {
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 16px;
    }

    .product-image {
      height: 200px;
    }

    .product-info {
      padding: 16px;
    }
  }
}

@media (max-width: 576px) {
  .user-product-container {
    padding: 0;
  }

  .top-nav {
    .nav-center {
      width: 100%;

      .el-button-group {
        flex-wrap: wrap;
        gap: 8px;

        .nav-btn {
          padding: 6px 12px;
          font-size: 13px;
        }
      }
    }
  }

  .search-section {
    padding: 16px 0;

    .search-container {
      padding: 0 16px;
    }
  }

  .carousel-section {
    padding: 0 16px;

    .section-title h2 {
      font-size: 24px;
    }
  }

  .main-content {
    padding: 0 16px 30px;
  }

  .content-area {
    .product-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }

    .product-image {
      height: 180px;
    }

    .product-name {
      font-size: 14px;
    }

    .product-price .price-value {
      font-size: 20px;
    }
  }
}
</style>
