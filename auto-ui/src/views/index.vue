<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>数据统计与分析</h2>
      <div class="refresh-controls">
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="refreshData">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :lg="24">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix">
            <span class="card-title">销售数据统计</span>
            <div class="filter-controls">
              <el-radio-group v-model="salesTimeDimension" size="small" @change="handleSalesTimeChange">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="quarter">季度</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div v-loading="salesLoading" class="sales-stats">
            <el-row :gutter="20">
              <el-col :xs="12" :sm="6" :lg="6">
                <div class="stat-card">
                  <div class="stat-icon sales-icon">
                    <i class="el-icon-s-finance" />
                  </div>
                  <div class="stat-content">
                    <div class="stat-label">销售额</div>
                    <div class="stat-value">¥{{ formatNumber(salesData.totalSales) }}</div>
                    <div class="stat-trend" :class="salesData.salesTrend >= 0 ? 'trend-up' : 'trend-down'">
                      <i :class="salesData.salesTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'" />
                      {{ Math.abs(salesData.salesTrend) }}%
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6" :lg="6">
                <div class="stat-card">
                  <div class="stat-icon volume-icon">
                    <i class="el-icon-s-goods" />
                  </div>
                  <div class="stat-content">
                    <div class="stat-label">销量</div>
                    <div class="stat-value">{{ formatNumber(salesData.totalVolume) }}</div>
                    <div class="stat-trend" :class="salesData.volumeTrend >= 0 ? 'trend-up' : 'trend-down'">
                      <i :class="salesData.volumeTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'" />
                      {{ Math.abs(salesData.volumeTrend) }}%
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6" :lg="6">
                <div class="stat-card">
                  <div class="stat-icon profit-icon">
                    <i class="el-icon-coin" />
                  </div>
                  <div class="stat-content">
                    <div class="stat-label">利润</div>
                    <div class="stat-value">¥{{ formatNumber(salesData.totalProfit) }}</div>
                    <div class="stat-trend" :class="salesData.profitTrend >= 0 ? 'trend-up' : 'trend-down'">
                      <i :class="salesData.profitTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'" />
                      {{ Math.abs(salesData.profitTrend) }}%
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6" :lg="6">
                <div class="stat-card">
                  <div class="stat-icon orders-icon">
                    <i class="el-icon-s-order" />
                  </div>
                  <div class="stat-content">
                    <div class="stat-label">订单数</div>
                    <div class="stat-value">{{ formatNumber(salesData.totalOrders) }}</div>
                    <div class="stat-trend" :class="salesData.ordersTrend >= 0 ? 'trend-up' : 'trend-down'">
                      <i :class="salesData.ordersTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'" />
                      {{ Math.abs(salesData.ordersTrend) }}%
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
            <div class="sales-chart-container">
              <div ref="salesChart" class="sales-chart" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :lg="24">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix">
            <span class="card-title">订单趋势分析</span>
            <div class="filter-controls">
              <el-date-picker
                v-model="orderDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                style="width: 300px"
                @change="handleOrderDateChange"
              />
              <el-button-group style="margin-left: 10px">
                <el-button size="small" :type="orderQuickRange === '7days' ? 'primary' : ''" @click="setOrderQuickRange('7days')">近7天</el-button>
                <el-button size="small" :type="orderQuickRange === '30days' ? 'primary' : ''" @click="setOrderQuickRange('30days')">近30天</el-button>
                <el-button size="small" :type="orderQuickRange === '90days' ? 'primary' : ''" @click="setOrderQuickRange('90days')">近90天</el-button>
              </el-button-group>
            </div>
          </div>
          <div v-loading="orderLoading" class="order-trend">
            <div ref="orderTrendChart" class="order-trend-chart" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :lg="24">
        <el-card class="box-card" shadow="hover">
          <div slot="header" class="clearfix">
            <span class="card-title">热销商品排行</span>
            <div class="filter-controls">
              <el-radio-group v-model="hotProductSortType" size="small" @change="handleHotProductSortChange">
                <el-radio-button label="volume">按销量</el-radio-button>
                <el-radio-button label="sales">按销售额</el-radio-button>
              </el-radio-group>
              <el-select v-model="hotProductTopN" placeholder="Top N" size="small" style="width: 100px; margin-left: 10px" @change="handleHotProductTopNChange">
                <el-option label="Top 5" :value="5" />
                <el-option label="Top 10" :value="10" />
                <el-option label="Top 20" :value="20" />
              </el-select>
            </div>
          </div>
          <div v-loading="hotProductLoading" class="hot-products">
            <el-table :data="hotProducts" style="width: 100%" stripe>
              <el-table-column type="index" label="排名" width="80" align="center">
                <template slot-scope="scope">
                  <span v-if="scope.$index < 3" class="rank-badge" :class="'rank-' + (scope.$index + 1)">{{ scope.$index + 1 }}</span>
                  <span v-else>{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="商品图片" width="100" align="center">
                <template slot-scope="scope">
                  <el-image
                    v-if="scope.row.image"
                    :src="getImageUrl(scope.row.image)"
                    fit="cover"
                    style="width: 60px; height: 60px; border-radius: 4px"
                    :preview-src-list="[getImageUrl(scope.row.image)]"
                  />
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="商品名称" min-width="200" />
              <el-table-column prop="category" label="分类" width="120" />
              <el-table-column prop="brand" label="品牌" width="120" />
              <el-table-column label="销量" width="120" align="right">
                <template slot-scope="scope">
                  <span>{{ formatNumber(scope.row.volume) }}</span>
                  <span v-if="scope.row.volumeTrend" class="trend-text" :class="scope.row.volumeTrend >= 0 ? 'trend-up' : 'trend-down'">
                    ({{ scope.row.volumeTrend >= 0 ? '+' : '' }}{{ scope.row.volumeTrend }}%)
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="销售额" width="150" align="right">
                <template slot-scope="scope">
                  <span>¥{{ formatNumber(scope.row.sales) }}</span>
                  <span v-if="scope.row.salesTrend" class="trend-text" :class="scope.row.salesTrend >= 0 ? 'trend-up' : 'trend-down'">
                    ({{ scope.row.salesTrend >= 0 ? '+' : '' }}{{ scope.row.salesTrend }}%)
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="同比" width="100" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.yoy >= 0 ? 'success' : 'danger'" size="small">
                    {{ scope.row.yoy >= 0 ? '+' : '' }}{{ scope.row.yoy }}%
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { getSalesStats, getOrderTrend, getHotProducts, getCategoryStats, getBrandStats } from '@/api/statistics/statistics'
import { listCategory } from '@/api/category/category'
import { Message } from 'element-ui'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: false,
      
      salesTimeDimension: 'month',
      salesLoading: false,
      salesData: {
        totalSales: 0,
        totalVolume: 0,
        totalProfit: 0,
        totalOrders: 0,
        salesTrend: 0,
        volumeTrend: 0,
        profitTrend: 0,
        ordersTrend: 0
      },
      salesChart: null,
      
      orderDateRange: [],
      orderQuickRange: '30days',
      orderLoading: false,
      orderTrendChart: null,
      
      hotProductSortType: 'volume',
      hotProductTopN: 10,
      hotProductLoading: false,
      hotProducts: []
    }
  },
  mounted() {
    this.initData()
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    if (this.salesChart) {
      this.salesChart.dispose()
    }
    if (this.orderTrendChart) {
      this.orderTrendChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    initData() {
      this.refreshData()
      this.setOrderQuickRange('30days')
    },
    
    refreshData() {
      this.loading = true
      Promise.all([
        this.fetchSalesData(),
        this.fetchOrderTrendData(),
        this.fetchHotProducts()
      ]).finally(() => {
        this.loading = false
      })
    },
    
    handleResize() {
      if (this.salesChart) {
        this.salesChart.resize()
      }
      if (this.orderTrendChart) {
        this.orderTrendChart.resize()
      }
    },
    
    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(2) + '万'
      }
      return num.toLocaleString()
    },
    
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    getImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      return process.env.VUE_APP_BASE_API + url
    },
    
    initCharts() {
      this.$nextTick(() => {
        if (this.$refs.salesChart) {
          this.salesChart = echarts.init(this.$refs.salesChart, 'macarons')
        }
        if (this.$refs.orderTrendChart) {
          this.orderTrendChart = echarts.init(this.$refs.orderTrendChart, 'macarons')
        }
      })
    },
    
    handleSalesTimeChange() {
      this.fetchSalesData()
    },
    
    fetchSalesData() {
      this.salesLoading = true
      const params = {
        dimension: this.salesTimeDimension
      }
      return getSalesStats(params)
        .then(response => {
          if (response.code === 200) {
            this.salesData = {
              totalSales: response.data.totalSales || 0,
              totalVolume: response.data.totalVolume || 0,
              totalProfit: response.data.totalProfit || 0,
              totalOrders: response.data.totalOrders || 0,
              salesTrend: response.data.salesTrend || 0,
              volumeTrend: response.data.volumeTrend || 0,
              profitTrend: response.data.profitTrend || 0,
              ordersTrend: response.data.ordersTrend || 0
            }
            this.updateSalesChart(response.data.chartData || {})
          } else {
            Message.error(response.msg || '获取销售数据失败')
          }
        })
        .catch(error => {
          console.error('获取销售数据失败:', error)
          Message.error('获取销售数据失败，请稍后重试')
        })
        .finally(() => {
          this.salesLoading = false
        })
    },
    
    updateSalesChart(chartData) {
      if (!this.salesChart) return
      
      const xAxisData = chartData.xAxisData || this.getXAxisData(this.salesTimeDimension)
      const salesData = chartData.salesData || []
      const volumeData = chartData.volumeData || []
      const profitData = chartData.profitData || []
      
      this.salesChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['销售额', '销量', '利润']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxisData
        },
        yAxis: [
          {
            type: 'value',
            name: '金额(元)',
            position: 'left',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#5470C6'
              }
            },
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '销量',
            position: 'right',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#91CC75'
              }
            },
            axisLabel: {
              formatter: '{value}'
            }
          }
        ],
        series: [
          {
            name: '销售额',
            type: 'line',
            smooth: true,
            data: salesData,
            itemStyle: {
              color: '#5470C6'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(84, 112, 198, 0.3)' },
                { offset: 1, color: 'rgba(84, 112, 198, 0.05)' }
              ])
            }
          },
          {
            name: '销量',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            data: volumeData,
            itemStyle: {
              color: '#91CC75'
            }
          },
          {
            name: '利润',
            type: 'line',
            smooth: true,
            data: profitData,
            itemStyle: {
              color: '#FAC858'
            }
          }
        ]
      })
    },
    
    getXAxisData(dimension) {
      const now = new Date()
      const data = []
      
      switch (dimension) {
        case 'day':
          for (let i = 23; i >= 0; i--) {
            const hour = (now.getHours() - i + 24) % 24
            data.push(`${hour}:00`)
          }
          break
        case 'week':
          const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
          for (let i = 6; i >= 0; i--) {
            const date = new Date(now)
            date.setDate(date.getDate() - i)
            data.push(weekDays[date.getDay()])
          }
          break
        case 'month':
          for (let i = 29; i >= 0; i--) {
            const date = new Date(now)
            date.setDate(date.getDate() - i)
            data.push(`${date.getMonth() + 1}/${date.getDate()}`)
          }
          break
        case 'quarter':
          for (let i = 2; i >= 0; i--) {
            const date = new Date(now)
            date.setMonth(date.getMonth() - i)
            data.push(`${date.getMonth() + 1}月`)
          }
          break
        case 'year':
          for (let i = 11; i >= 0; i--) {
            const date = new Date(now)
            date.setMonth(date.getMonth() - i)
            data.push(`${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`)
          }
          break
      }
      
      return data
    },
    
    handleOrderDateChange() {
      this.orderQuickRange = ''
      this.fetchOrderTrendData()
    },
    
    setOrderQuickRange(range) {
      this.orderQuickRange = range
      const now = new Date()
      let startDate
      
      switch (range) {
        case '7days':
          startDate = new Date(now)
          startDate.setDate(startDate.getDate() - 7)
          break
        case '30days':
          startDate = new Date(now)
          startDate.setDate(startDate.getDate() - 30)
          break
        case '90days':
          startDate = new Date(now)
          startDate.setDate(startDate.getDate() - 90)
          break
      }
      
      this.orderDateRange = [startDate, now]
      this.fetchOrderTrendData()
    },
    
    fetchOrderTrendData() {
      this.orderLoading = true
      if (!this.orderDateRange || this.orderDateRange.length !== 2) {
        this.orderLoading = false
        return Promise.resolve()
      }
      
      const params = {
        startDate: this.formatDate(this.orderDateRange[0]),
        endDate: this.formatDate(this.orderDateRange[1])
      }
      
      return getOrderTrend(params)
        .then(response => {
          if (response.code === 200) {
            this.updateOrderTrendChart(response.data || {})
          } else {
            Message.error(response.msg || '获取订单趋势数据失败')
          }
        })
        .catch(error => {
          console.error('获取订单趋势数据失败:', error)
          Message.error('获取订单趋势数据失败，请稍后重试')
        })
        .finally(() => {
          this.orderLoading = false
        })
    },
    
    updateOrderTrendChart(chartData) {
      if (!this.orderTrendChart) return
      
      const xAxisData = chartData.xAxisData || []
      const orderCountData = chartData.orderCountData || []
      const orderAmountData = chartData.orderAmountData || []
      const yoyData = chartData.yoyData || []
      
      this.orderTrendChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          formatter: function(params) {
            let result = params[0].axisValue + '<br/>'
            params.forEach(param => {
              if (param.seriesName === '订单数量') {
                result += `${param.marker}${param.seriesName}: ${param.value}单<br/>`
              } else if (param.seriesName === '订单金额') {
                result += `${param.marker}${param.seriesName}: ¥${param.value.toLocaleString()}<br/>`
              } else if (param.seriesName === '同比') {
                result += `${param.marker}${param.seriesName}: ${param.value}%<br/>`
              }
            })
            return result
          }
        },
        legend: {
          data: ['订单数量', '订单金额', '同比']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxisData
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数量',
            position: 'left',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#5470C6'
              }
            }
          },
          {
            type: 'value',
            name: '订单金额(元)',
            position: 'right',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#EE6666'
              }
            },
            axisLabel: {
              formatter: '¥{value}'
            }
          }
        ],
        series: [
          {
            name: '订单数量',
            type: 'line',
            yAxisIndex: 0,
            smooth: true,
            data: orderCountData,
            itemStyle: {
              color: '#5470C6'
            },
            markLine: {
              data: [
                { type: 'average', name: '平均值' }
              ]
            }
          },
          {
            name: '订单金额',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            data: orderAmountData,
            itemStyle: {
              color: '#EE6666'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(238, 102, 102, 0.3)' },
                { offset: 1, color: 'rgba(238, 102, 102, 0.05)' }
              ])
            }
          },
          {
            name: '同比',
            type: 'line',
            yAxisIndex: 0,
            smooth: true,
            data: yoyData,
            itemStyle: {
              color: '#73C0DE'
            },
            lineStyle: {
              type: 'dashed'
            }
          }
        ]
      })
    },
    
    handleHotProductSortChange() {
      this.fetchHotProducts()
    },
    
    handleHotProductTopNChange() {
      this.fetchHotProducts()
    },
    
    fetchHotProducts() {
      this.hotProductLoading = true
      const params = {
        sortType: this.hotProductSortType,
        topN: this.hotProductTopN
      }
      
      return getHotProducts(params)
        .then(response => {
          if (response.code === 200) {
            this.hotProducts = response.data.list || []
          } else {
            Message.error(response.msg || '获取热销商品数据失败')
          }
        })
        .catch(error => {
          console.error('获取热销商品数据失败:', error)
          Message.error('获取热销商品数据失败，请稍后重试')
        })
        .finally(() => {
          this.hotProductLoading = false
        })
    },
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
    font-weight: 600;
  }
  
  .refresh-controls {
    display: flex;
    align-items: center;
  }
}

.box-card {
  border-radius: 8px;
  
  ::v-deep .el-card__header {
    padding: 18px 20px;
    border-bottom: 1px solid #ebeef5;
    
    .clearfix {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
      
      .filter-controls {
        display: flex;
        align-items: center;
      }
    }
  }
  
  ::v-deep .el-card__body {
    padding: 20px;
  }
}

.sales-stats {
  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
    }
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      margin-right: 16px;
      
      &.sales-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
      }
      
      &.volume-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: #fff;
      }
      
      &.profit-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        color: #fff;
      }
      
      &.orders-icon {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        color: #fff;
      }
    }
    
    .stat-content {
      flex: 1;
      
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .stat-trend {
        font-size: 12px;
        display: flex;
        align-items: center;
        
        &.trend-up {
          color: #67c23a;
        }
        
        &.trend-down {
          color: #f56c6c;
        }
        
        i {
          margin-right: 2px;
        }
      }
    }
  }
  
  .sales-chart-container {
    margin-top: 20px;
    
    .sales-chart {
      width: 100%;
      height: 400px;
    }
  }
}

.order-trend {
  .order-trend-chart {
    width: 100%;
    height: 450px;
  }
}

.hot-products {
  .rank-badge {
    display: inline-block;
    width: 28px;
    height: 28px;
    line-height: 28px;
    text-align: center;
    border-radius: 50%;
    font-weight: 600;
    color: #fff;
    
    &.rank-1 {
      background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
    }
    
    &.rank-2 {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: #666;
    }
    
    &.rank-3 {
      background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
      color: #666;
    }
  }
  
  .trend-text {
    font-size: 12px;
    margin-left: 4px;
    
    &.trend-up {
      color: #67c23a;
    }
    
    &.trend-down {
      color: #f56c6c;
    }
  }
  
  ::v-deep .el-table {
    th {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: 600;
    }
    
    .el-table__row:hover {
      background-color: #f5f7fa;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    
    .refresh-controls {
      margin-top: 10px;
      width: 100%;
      
      .el-button {
        flex: 1;
      }
    }
  }
  
  .box-card {
    ::v-deep .el-card__header {
      .clearfix {
        flex-direction: column;
        align-items: flex-start;
        
        .filter-controls {
          margin-top: 10px;
          flex-wrap: wrap;
          width: 100%;
        }
      }
    }
  }
  
  .sales-stats {
    .stat-card {
      margin-bottom: 10px;
    }
  }
}
</style>
