<template>
  <el-dialog
    title="商品详情"
    :visible.sync="dialogVisible"
    width="70%"
    :before-close="handleClose"
    append-to-body
  >
    <div v-loading="loading" class="product-detail">
      <el-row :gutter="30">
        <el-col :span="12">
          <div class="image-gallery">
            <el-carousel :interval="5000" arrow="always" height="400px">
              <el-carousel-item v-for="(image, index) in allImages" :key="index">
                <el-image
                  :src="image"
                  fit="contain"
                  style="width: 100%; height: 100%;"
                  :preview-src-list="allImages"
                  :initial-index="index"
                >
                  <div slot="error" class="image-error">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="product-info">
            <h2 class="product-title">{{ product.name }}</h2>
            <div class="product-tags">
              <el-tag v-if="product.isHot === 1" type="danger" size="small">热销</el-tag>
              <el-tag v-if="product.isNew === 1" type="success" size="small">新品</el-tag>
            </div>

            <div class="price-section">
              <div class="price-label">价格</div>
              <div class="price-value">
                <span class="symbol">¥</span>
                <span class="amount">{{ Number(product.price).toFixed(2) }}</span>
              </div>
            </div>

            <div class="info-section">
              <div class="info-item">
                <span class="label">品牌：</span>
                <span class="value">{{ product.brand }}</span>
              </div>
              <div class="info-item">
                <span class="label">销量：</span>
                <span class="value">{{ product.sales }}件</span>
              </div>
              <div class="info-item">
                <span class="label">库存：</span>
                <span class="value" :class="{ 'low-stock': product.stock <= product.warnStock }">
                  {{ product.stock }}件
                  <span v-if="product.stock <= product.warnStock" class="warn-text">(库存紧张)</span>
                </span>
              </div>
            </div>

            <div class="spec-section" v-if="parsedSpecList.length > 0">
              <div v-for="(specItem, index) in parsedSpecList" :key="index" class="spec-item">
                <div class="spec-name">{{ specItem.name }}：</div>
                <div class="spec-values">
                  <el-tag
                    v-for="(value, vIndex) in specItem.values"
                    :key="vIndex"
                    :type="selectedSpecs[specItem.name] === value ? 'primary' : 'info'"
                    :effect="selectedSpecs[specItem.name] === value ? 'dark' : 'plain'"
                    @click="selectSpec(specItem.name, value)"
                    class="spec-tag"
                  >
                    {{ value }}
                  </el-tag>
                </div>
              </div>
            </div>

            <div class="quantity-section">
              <span class="quantity-label">购买数量：</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="small"
                controls-position="right"
              />
            </div>

            <div class="action-buttons">
              <el-button type="primary" size="large" @click="handleAddToCart" :disabled="product.stock === 0">
                <i class="el-icon-shopping-cart-2"></i> 加入购物车
              </el-button>
              <el-button type="warning" size="large" @click="handleBuyNow" :disabled="product.stock === 0">
                <i class="el-icon-s-goods"></i> 立即购买
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider></el-divider>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="detail-content" v-html="product.detail || '暂无详情'"></div>
        </el-tab-pane>

        <el-tab-pane label="规格参数" name="spec">
          <div v-if="parsedSpecList.length > 0" class="spec-detail">
            <div v-for="(specItem, index) in parsedSpecList" :key="index" class="spec-detail-item">
              <div class="spec-detail-name">{{ specItem.name }}</div>
              <div class="spec-detail-values">
                <el-tag v-for="(value, vIndex) in specItem.values" :key="vIndex" type="info" size="small">
                  {{ value }}
                </el-tag>
              </div>
            </div>
          </div>
          <div v-else class="no-spec">暂无规格参数</div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getProduct } from '@/api/product/product'
import { listCategory } from '@/api/category/category'

export default {
  name: 'ProductDetailDialog',
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
      dialogVisible: this.visible,
      loading: false,
      quantity: 1,
      activeTab: 'detail',
      categoryList: [],
      selectedSpecs: {},
      specList: [],
      comments: [
        {
          id: 1,
          userName: '用户A',
          rating: 5,
          content: '商品质量很好，包装完整，物流速度快！',
          createTime: '2024-01-10 10:30:00'
        },
        {
          id: 2,
          userName: '用户B',
          rating: 4,
          content: '配件很合适，安装方便，性价比高。',
          createTime: '2024-01-09 15:20:00'
        }
      ]
    }
  },
  computed: {
    allImages() {
      const images = []
      if (this.product.mainImage) {
        images.push(this.getImageUrl(this.product.mainImage))
      }
      if (this.product.subImages) {
        try {
          const subImages = JSON.parse(this.product.subImages)
          if (Array.isArray(subImages)) {
            subImages.forEach(img => {
              images.push(this.getImageUrl(img))
            })
          }
        } catch (e) {
          console.error('解析附图失败:', e)
        }
      }
      return images.length > 0 ? images : ['/static/images/default-product.png']
    },
    parsedSpecList() {
      if (!this.product.spec) return []
      try {
        const specData = typeof this.product.spec === 'string' ? JSON.parse(this.product.spec) : this.product.spec
        if (Array.isArray(specData)) {
          return specData
        }
      } catch (e) {
        console.error('解析规格参数失败:', e)
      }
      return []
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadCategories()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    },
    product() {
      this.quantity = 1
      this.selectedSpecs = {}
    }
  },
  methods: {
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return process.env.VUE_APP_BASE_API + url
    },
    loadCategories() {
      listCategory({ pageNum: 1, pageSize: 100 }).then(response => {
        if (response.code === 200) {
          this.categoryList = response.rows || []
        }
      })
    },
    getCategoryName() {
      if (!this.product.categoryId) return '-'
      const category = this.categoryList.find(cat => cat.id === this.product.categoryId)
      return category ? category.name : '-'
    },
    selectSpec(specName, value) {
      if (this.selectedSpecs[specName] === value) {
        this.$delete(this.selectedSpecs, specName)
      } else {
        this.$set(this.selectedSpecs, specName, value)
      }
    },
    getSelectedSpecText() {
      const specs = {}
      for (const [key, value] of Object.entries(this.selectedSpecs)) {
        specs[key] = value
      }
      return Object.keys(specs).length > 0 ? JSON.stringify(specs) : null
    },
    handleAddToCart() {
      if (this.parsedSpecList.length > 0 && Object.keys(this.selectedSpecs).length === 0) {
        this.$message.warning('请选择商品规格')
        return
      }
      this.$emit('add-to-cart', {
        ...this.product,
        quantity: this.quantity,
        selectedSpecs: { ...this.selectedSpecs },
        specText: this.getSelectedSpecText()
      })
    },
    handleBuyNow() {
      if (this.parsedSpecList.length > 0 && Object.keys(this.selectedSpecs).length === 0) {
        this.$message.warning('请选择商品规格')
        return
      }
      
      // 构建立即购买的商品数据
      const buyNowItem = {
        cartId: null, // 立即购买没有购物车ID
        productId: this.product.id,
        productName: this.product.name,
        productImage: this.product.mainImage,
        price: this.product.price,
        quantity: this.quantity,
        subtotal: this.product.price * this.quantity,
        spec: this.getSelectedSpecText()
      }
      
      // 存储到localStorage，与购物车结算使用相同的数据结构
      localStorage.setItem('checkoutItems', JSON.stringify({
        selectedItems: [buyNowItem],
        totalAmount: buyNowItem.subtotal,
        selectedCount: buyNowItem.quantity
      }))
      
      // 跳转到结账页面
      this.$router.push('/order/checkout')
    },
    handleClose() {
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.product-detail {
  padding: 20px 0;
}

.image-gallery {
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f7fa;

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 64px;
  }
}

.product-info {
  .product-title {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin: 0 0 15px 0;
  }

  .product-tags {
    margin-bottom: 20px;
  }

  .price-section {
    background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    color: white;

    .price-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 5px;
    }

    .price-value {
      .symbol {
        font-size: 24px;
        margin-right: 5px;
      }

      .amount {
        font-size: 36px;
        font-weight: bold;
      }
    }
  }

  .info-section {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;

    .info-item {
      display: flex;
      margin-bottom: 12px;
      font-size: 14px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #909399;
        min-width: 80px;
      }

      .value {
        color: #303133;
        flex: 1;

        &.low-stock {
          color: #f56c6c;

          .warn-text {
            margin-left: 5px;
            font-size: 12px;
          }
        }
      }
    }
  }

  .spec-section {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;

    .spec-item {
      margin-bottom: 15px;

      &:last-child {
        margin-bottom: 0;
      }

      .spec-name {
        font-size: 14px;
        color: #606266;
        margin-bottom: 10px;
        font-weight: 500;
      }

      .spec-values {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;

        .spec-tag {
          cursor: pointer;
          transition: all 0.3s;
          user-select: none;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
          }
        }
      }
    }
  }

  .quantity-section {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .quantity-label {
      margin-right: 15px;
      font-size: 14px;
      color: #606266;
    }
  }

  .action-buttons {
    display: flex;
    gap: 15px;

    .el-button {
      flex: 1;
      height: 50px;
      font-size: 16px;
    }
  }
}

.detail-content {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  min-height: 200px;
  line-height: 1.8;
}

.spec-detail {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;

  .spec-detail-item {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    .spec-detail-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }

    .spec-detail-values {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }
  }
}

.no-spec {
  padding: 40px;
  text-align: center;
  color: #909399;
  background-color: #f9f9f9;
  border-radius: 8px;
}
</style>
