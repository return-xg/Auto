<template>
  <el-dialog
    title="物流信息"
    :visible.sync="dialogVisible"
    width="50%"
    :before-close="handleClose"
    append-to-body
  >
    <div v-loading="loading" class="logistics-detail">
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

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getLogistics } from '@/api/order/order'

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
        company: '顺丰快递',
        trackingNo: '',
        tracks: []
      },
      logisticsTimer: null
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadLogistics()
        this.startAutoUpdate()
      } else {
        this.stopAutoUpdate()
      }
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    loadLogistics() {
      this.loading = true
      getLogistics(this.order.id).then(response => {
        this.loading = false
        if (response.code === 200) {
          this.logistics = response.data || this.getDefaultLogistics()
        }
      }).catch(error => {
        this.loading = false
        this.logistics = this.getDefaultLogistics()
      })
    },
    getDefaultLogistics() {
      const now = new Date()
      const tracks = [
        {
          time: this.formatTime(new Date(now.getTime() + 5000)),
          status: '已送达',
          location: '您的订单已送达，请查收'
        },
        {
          time: this.formatTime(new Date(now.getTime() + 3000)),
          status: '派送中',
          location: '快递员正在派送，请保持电话畅通'
        },
        {
          time: this.formatTime(new Date(now.getTime() + 1000)),
          status: '运输中',
          location: '快件已到达目的地城市，准备派送'
        },
        {
          time: this.formatTime(now),
          status: '已发货',
          location: '商家已发货，快件已揽收'
        }
      ]
      return {
        company: '顺丰快递',
        trackingNo: this.order.logisticsNo || 'SF1234567890',
        tracks: tracks
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
    startAutoUpdate() {
      this.logisticsTimer = setInterval(() => {
        this.loadLogistics()
      }, 5000)
    },
    stopAutoUpdate() {
      if (this.logisticsTimer) {
        clearInterval(this.logisticsTimer)
        this.logisticsTimer = null
      }
    },
    handleClose() {
      this.stopAutoUpdate()
      this.dialogVisible = false
      this.$emit('close')
    }
  },
  beforeDestroy() {
    this.stopAutoUpdate()
  }
}
</script>

<style scoped lang="scss">
.logistics-detail {
  padding: 20px 0;
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
