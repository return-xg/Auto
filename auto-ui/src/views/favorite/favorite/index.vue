<template>
  <div class="favorite-container">
    <el-card>
      <div slot="header" class="card-header">
        <span>我的收藏</span>
        <span class="favorite-count">共 {{ favoriteList.length }} 件商品</span>
      </div>

      <div v-loading="loading">
        <div v-if="favoriteList.length > 0">
          <el-table
            ref="table"
            :data="favoriteList"
            border
            stripe
          >
            <el-table-column label="商品信息" width="450">
              <template slot-scope="scope">
                <div class="product-info">
                  <el-image
                    :src="(scope.row.product && scope.row.product.mainImage) || '/static/images/default-product.png'"
                    fit="cover"
                    style="width: 100px; height: 100px; border-radius: 4px;"
                  >
                    <div slot="error" class="image-error">
                      <i class="el-icon-picture-outline"></i>
                    </div>
                  </el-image>
                  <div class="product-detail">
                    <div class="product-name" :title="scope.row.product && scope.row.product.name">{{ scope.row.product && scope.row.product.name }}</div>
                    <div class="product-brand">品牌: {{ scope.row.product && scope.row.product.brand }}</div>
                    <div class="product-meta">
                      <span>销量: {{ scope.row.product && scope.row.product.sales || 0 }}</span>
                      <span>库存: {{ scope.row.product && scope.row.product.stock || 0 }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="价格" width="150">
              <template slot-scope="scope">
                <span class="price">¥{{ Number(scope.row.product && scope.row.product.price).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="收藏时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.product && scope.row.product.status === 1" type="success">在售</el-tag>
                <el-tag v-else type="info">已下架</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280">
              <template slot-scope="scope">
                <div class="action-buttons">
                  <el-button
                    type="primary"
                    size="small"
                    icon="el-icon-shopping-cart-2"
                    @click="handleAddToCart(scope.row)"
                    :disabled="!(scope.row.product && scope.row.product.status === 1 && scope.row.product.stock)"
                  >
                    加入购物车
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    icon="el-icon-delete"
                    @click="handleDelete(scope.row)"
                  >
                    取消收藏
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              :current-page.sync="pagination.current"
              :page-size.sync="pagination.size"
              :total="pagination.total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>

        <div v-else class="empty-favorite">
          <i class="el-icon-star-off"></i>
          <p>暂无收藏商品</p>
        </div>
      </div>
    </el-card>

    <el-dialog
      title="加入购物车"
      :visible.sync="cartDialogVisible"
      width="400px"
    >
      <div v-if="currentProduct">
        <div class="dialog-product-info">
          <el-image
            :src="currentProduct.product && currentProduct.product.mainImage || '/static/images/default-product.png'"
            fit="cover"
            style="width: 80px; height: 80px; border-radius: 4px;"
          />
          <div class="dialog-product-detail">
            <div class="dialog-product-name">{{ currentProduct.product && currentProduct.product.name }}</div>
            <div class="dialog-product-price">¥{{ Number(currentProduct.product && currentProduct.product.price).toFixed(2) }}</div>
            <div class="dialog-product-stock">库存: {{ currentProduct.product && currentProduct.product.stock || 0 }}</div>
          </div>
        </div>
        <div class="quantity-selector">
          <span class="quantity-label">数量：</span>
          <el-input-number
            v-model="cartQuantity"
            :min="1"
            :max="currentProduct.product && currentProduct.product.stock || 99"
            size="small"
            controls-position="right"
          />
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cartDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddToCart">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listFavorite, delFavorite } from '@/api/favorite/favorite'
import { addToCart } from '@/api/cart/cart'

export default {
  name: 'FavoriteIndex',
  data() {
    return {
      loading: false,
      favoriteList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      cartDialogVisible: false,
      currentProduct: null,
      cartQuantity: 1
    }
  },
  mounted() {
    this.loadFavoriteList()
  },
  methods: {
    loadFavoriteList() {
      this.loading = true
      const queryParams = {
        pageNum: this.pagination.current,
        pageSize: this.pagination.size
      }
      
      listFavorite(queryParams).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.favoriteList = response.rows || []
          this.pagination.total = response.total || 0
        }
      }).catch(error => {
        this.loading = false
        this.$message.error('获取收藏列表失败')
      })
    },
    handleDelete(row) {
      this.$confirm('确定要取消收藏该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delFavorite(row.productId)
      }).then(response => {
        if (response.code === 200) {
          this.$message.success('已取消收藏')
          this.loadFavoriteList()
        } else {
          this.$message.error(response.msg || '操作失败')
        }
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      })
    },
    handleAddToCart(row) {
      const userId = this.$store.getters.id
      if (!userId) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      if (!row.product || !row.product.stock) {
        this.$message.warning('商品库存不足')
        return
      }
      
      this.currentProduct = row
      this.cartQuantity = 1
      this.cartDialogVisible = true
    },
    confirmAddToCart() {
      const userId = this.$store.getters.id
      if (!userId) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      if (!this.currentProduct.product || !this.currentProduct.product.stock) {
        this.$message.warning('商品库存不足')
        return
      }
      
      addToCart({
        userId,
        productId: this.currentProduct.productId,
        quantity: this.cartQuantity
      }).then(response => {
        if (response.code === 200) {
          this.$message.success(`已添加${this.cartQuantity}件商品到购物车`)
          this.cartDialogVisible = false
        } else {
          this.$message.error(response.msg || '添加失败')
        }
      }).catch(error => {
        this.$message.error('添加失败')
      })
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.loadFavoriteList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadFavoriteList()
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped lang="scss">
.favorite-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;

  .favorite-count {
    font-size: 14px;
    color: #909399;
    font-weight: normal;
  }
}

.product-info {
  display: flex;
  gap: 15px;
  align-items: center;

  .image-error {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    background-color: #f5f7fa;
    color: #909399;
    font-size: 32px;
  }

  .product-detail {
    flex: 1;

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
      margin-bottom: 5px;
    }

    .product-meta {
      font-size: 12px;
      color: #909399;

      span {
        margin-right: 15px;
      }
    }
  }
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-bottom: 20px;
}

.empty-favorite {
  text-align: center;
  padding: 80px 0;
  color: #909399;

  i {
    font-size: 80px;
    margin-bottom: 20px;
    display: block;
  }

  p {
    font-size: 18px;
    margin-bottom: 30px;
  }
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
}

.action-buttons {
  display: flex;
  gap: 8px;
  
  .el-button {
    flex: 1;
    border-radius: 4px;
    font-size: 13px;
    padding: 8px 12px;
    transition: all 0.3s;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    &:active:not(:disabled) {
      transform: translateY(0);
    }
  }
}

.dialog-product-info {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;

  .dialog-product-detail {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .dialog-product-name {
      font-size: 14px;
      font-weight: bold;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .dialog-product-price {
      font-size: 18px;
      font-weight: bold;
      color: #f56c6c;
    }

    .dialog-product-stock {
      font-size: 12px;
      color: #909399;
    }
  }
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 15px;

  .quantity-label {
    font-size: 14px;
    color: #606266;
  }

  .el-input-number {
    flex: 1;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
