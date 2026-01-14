<template>
  <el-dialog
    title="物流信息"
    :visible.sync="dialogVisible"
    width="50%"
    :before-close="handleClose"
    append-to-body
  >
    <div v-loading="loading" class="logistics-detail">
      <div v-if="order.deliveryType === 1" class="logistics-content">
        <div class="logistics-header">
          <div class="logistics-info">
            <div class="info-item">
              <span class="label">物流公司：</span>
              <span class="value">{{ logistics.company || '顺丰快递' }}</span>
            </div>
            <div class="info-item">
              <span class="label">运单号：</span>
              <span class="value">{{ logistics.trackingNo || order.logisticsNo || 'SF1234567890' }}</span>
            </div>
            <div class="info-item">
              <span class="label">发货时间：</span>
              <span class="value">{{ order.shipTime || '-' }}</span>
            </div>
          </div>
        </div>

        <el-divider></el-divider>

        <div class="logistics-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in logistics.tracks"
              :key="index"
              :timestamp="item.time"
              :type="index === 0 ? 'primary' : 'info'"
              placement="top"
            >
              <div class="timeline-content">
                <div class="status">{{ item.status }}</div>
                <div class="location">{{ item.location }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <div v-else class="store-pickup-info">
        <i class="el-icon-house"></i>
        <p>此订单为门店自提，无需物流配送</p>
        <p v-if="order.storeInfo">自提门店：{{ order.storeInfo }}</p>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
// 注意：此组件使用模拟数据展示物流信息，不发送真实的网络请求
// 实际项目中应将 getMockLogistics 方法替换为真实的 API 调用
export default {
  name: 'LogisticsDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    order: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      dialogVisible: this.visible,
      loading: false,
      logistics: {
        company: '',
        trackingNo: '',
        tracks: []
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadLogistics()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    loadLogistics() {
      if (this.order.deliveryType !== 1) {
        return
      }
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.logistics = this.getMockLogistics()
      }, 300)
    },
    // 获取模拟物流信息数据（实际项目中应替换为真实的API调用）
    getMockLogistics() {
      const orderStatus = this.order.status || 2
      const now = new Date()
      let tracks = []
      let company = '顺丰快递'
      let trackingNo = this.order.logisticsNo || 'SF1234567890'
      
      // 根据订单状态生成不同的物流轨迹（模拟数据）
      if (orderStatus === 2) {
        tracks = [
          {
            time: this.formatTime(new Date(now.getTime() + 60000)),
            status: '派送中',
            location: '快递员正在派送，请保持电话畅通'
          },
          {
            time: this.formatTime(new Date(now.getTime() + 30000)),
            status: '运输中',
            location: '快件已到达目的地城市，准备派送'
          },
          {
            time: this.formatTime(new Date(now.getTime() + 10000)),
            status: '运输中',
            location: '快件已到达中转站'
          },
          {
            time: this.formatTime(now),
            status: '已发货',
            location: '商家已发货，快件已揽收'
          }
        ]
      } else if (orderStatus === 3) {
        tracks = [
          {
            time: this.formatTime(new Date(now.getTime() + 60000)),
            status: '已签收',
            location: '您的订单已签收，感谢您的购买'
          },
          {
            time: this.formatTime(new Date(now.getTime() + 30000)),
            status: '派送中',
            location: '快递员正在派送，请保持电话畅通'
          },
          {
            time: this.formatTime(new Date(now.getTime() + 10000)),
            status: '运输中',
            location: '快件已到达目的地城市，准备派送'
          },
          {
            time: this.formatTime(now),
            status: '已发货',
            location: '商家已发货，快件已揽收'
          }
        ]
      } else {
        tracks = [
          {
            time: this.formatTime(new Date(now.getTime() + 30000)),
            status: '运输中',
            location: '快件已到达中转站'
          },
          {
            time: this.formatTime(now),
            status: '已发货',
            location: '商家已发货，快件已揽收'
          }
        ]
      }
      
      return {
        company,
        trackingNo,
        tracks
      }
    },
    formatTime(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    handleClose() {
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.logistics-detail {
  padding: 20px 0;
}

.store-pickup-info {
  text-align: center;
  padding: 60px 20px;
  color: #909399;

  i {
    font-size: 64px;
    margin-bottom: 20px;
    display: block;
    color: #409eff;
  }

  p {
    font-size: 16px;
    margin: 10px 0;

    &:last-child {
      font-size: 14px;
      color: #606266;
    }
  }
}

.logistics-header {
  .logistics-info {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 8px;

    .info-item {
      display: flex;
      margin-bottom: 15px;
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
        font-weight: 500;
      }
    }
  }
}

.logistics-timeline {
  padding: 20px 0;

  .timeline-content {
    .status {
      font-size: 14px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
    }

    .location {
      font-size: 12px;
      color: #909399;
    }
  }
}

.el-timeline-item__timestamp {
  font-size: 12px;
  color: #909399;
}
</style>
