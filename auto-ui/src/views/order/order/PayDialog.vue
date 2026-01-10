<template>
  <el-dialog
    title="支付订单"
    :visible.sync="dialogVisible"
    width="40%"
    :before-close="handleClose"
    :close-on-click-modal="false"
    append-to-body
  >
    <div v-loading="loading" class="pay-container">
      <div class="order-info">
        <div class="info-item">
          <span class="label">订单号：</span>
          <span class="value">{{ order.orderNo }}</span>
        </div>
        <div class="info-item">
          <span class="label">订单金额：</span>
          <span class="value amount">¥{{ Number(order.totalAmount + order.freight).toFixed(2) }}</span>
        </div>
      </div>

      <el-divider></el-divider>

      <div class="payment-methods">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="paymentMethod" class="method-group">
          <el-radio label="alipay" border class="method-item">
            <div class="method-content">
              <i class="icon-alipay"></i>
              <span>支付宝</span>
            </div>
          </el-radio>
          <el-radio label="wechat" border class="method-item">
            <div class="method-content">
              <i class="icon-wechat"></i>
              <span>微信支付</span>
            </div>
          </el-radio>
          <el-radio label="bank" border class="method-item">
            <div class="method-content">
              <i class="icon-bank"></i>
              <span>银行卡</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <el-divider></el-divider>

      <div class="pay-amount">
        <span class="label">支付金额：</span>
        <span class="value">¥{{ Number(order.totalAmount + order.freight).toFixed(2) }}</span>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose" :disabled="paying">取消</el-button>
      <el-button type="primary" @click="handlePay" :loading="paying">
        {{ paying ? '支付中...' : '立即支付' }}
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import { payOrder } from '@/api/order/order'

export default {
  name: 'PayDialog',
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
      paying: false,
      paymentMethod: 'alipay'
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    },
    dialogVisible(val) {
      this.$emit('update:visible', val)
    }
  },
  methods: {
    handlePay() {
      this.paying = true
      const data = {
        paymentMethod: this.paymentMethod,
        amount: this.order.totalAmount + this.order.freight
      }
      
      payOrder(this.order.id, data).then(response => {
        this.paying = false
        if (response.code === 200) {
          this.$message.success('支付成功')
          this.$emit('success', response.data)
        } else {
          this.$message.error(response.msg || '支付失败')
          this.$emit('fail', response)
        }
      }).catch(error => {
        this.paying = false
        this.$message.error('支付失败')
        this.$emit('fail', error)
      })
    },
    handleClose() {
      if (this.paying) {
        this.$message.warning('支付中，请稍候...')
        return
      }
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.pay-container {
  padding: 20px 0;
}

.order-info {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;

  .info-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 14px;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      color: #909399;
    }

    .value {
      color: #303133;
      font-weight: 500;

      &.amount {
        color: #f56c6c;
        font-size: 24px;
        font-weight: bold;
      }
    }
  }
}

.payment-methods {
  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 20px;
  }

  .method-group {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .method-item {
      width: 100%;

      ::v-deep .el-radio__label {
        width: 100%;
      }

      .method-content {
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 14px;

        i {
          font-size: 24px;
        }

        .icon-alipay {
          color: #1677ff;
        }

        .icon-wechat {
          color: #07c160;
        }

        .icon-bank {
          color: #f56c6c;
        }
      }
    }
  }
}

.pay-amount {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  border-radius: 8px;
  color: white;

  .label {
    font-size: 14px;
    margin-right: 10px;
  }

  .value {
    font-size: 36px;
    font-weight: bold;
  }
}
</style>
