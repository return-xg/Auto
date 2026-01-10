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
              <div class="info-item">
                <span class="label">适配车型：</span>
                <span class="value">{{ product.fitCarModel || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">适用年份：</span>
                <span class="value">{{ product.year || '-' }}</span>
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
          <el-descriptions :column="2" border>
            <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
            <el-descriptions-item label="品牌">{{ product.brand }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ getCategoryName() }}</el-descriptions-item>
            <el-descriptions-item label="适配车型">{{ product.fitCarModel || '-' }}</el-descriptions-item>
            <el-descriptions-item label="适用年份">{{ product.year || '-' }}</el-descriptions-item>
            <el-descriptions-item label="库存">{{ product.stock }}</el-descriptions-item>
            <el-descriptions-item label="规格参数" :span="2">{{ product.spec || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <el-tab-pane label="用户评论" name="comment">
          <div v-if="comments.length > 0" class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <span class="comment-user">{{ comment.userName }}</span>
                <el-rate v-model="comment.rating" disabled show-score text-color="#ff9900"></el-rate>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>
          <div v-else class="empty-comments">
            <i class="el-icon-chat-line-square"></i>
            <p>暂无评论</p>
          </div>
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
        images.push(this.product.mainImage)
      }
      if (this.product.subImages) {
        try {
          const subImages = JSON.parse(this.product.subImages)
          if (Array.isArray(subImages)) {
            images.push(...subImages)
          }
        } catch (e) {
          console.error('解析附图失败:', e)
        }
      }
      return images.length > 0 ? images : ['/static/images/default-product.png']
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
    }
  },
  methods: {
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
    handleAddToCart() {
      this.$emit('add-to-cart', {
        ...this.product,
        quantity: this.quantity
      })
    },
    handleBuyNow() {
      this.$message.info('立即购买功能开发中')
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

.comment-list {
  .comment-item {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;

    &:last-child {
      border-bottom: none;
    }

    .comment-header {
      display: flex;
      align-items: center;
      gap: 20px;
      margin-bottom: 10px;

      .comment-user {
        font-weight: bold;
        color: #303133;
      }

      .comment-time {
        color: #909399;
        font-size: 12px;
        margin-left: auto;
      }
    }

    .comment-content {
      color: #606266;
      line-height: 1.6;
    }
  }
}

.empty-comments {
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
</style>
