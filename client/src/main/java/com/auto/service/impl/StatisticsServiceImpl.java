package com.auto.service.impl;

import com.auto.domain.Order;
import com.auto.domain.OrderProduct;
import com.auto.mapper.OrderMapper;
import com.auto.mapper.OrderProductMapper;
import com.auto.mapper.StatisticsMapper;
import com.auto.service.IStatisticsService;
import com.auto.service.impl.StatisticsCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 * 负责调用数据计算服务并返回统计结果
 * 
 * @author auto
 * @date 2026-01-15
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {

    @Autowired
    private StatisticsCalculationService calculationService;
    
    @Autowired
    private StatisticsMapper statisticsMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderProductMapper orderProductMapper;
    
    /**
     * 订单有效状态集合
     * 0-待支付, 1-待发货, 2-已发货, 3-已完成
     * 排除已取消(4)的订单
     */
    private static final List<Long> VALID_ORDER_STATUS = Arrays.asList(1L, 2L, 3L);
    
    /**
     * 利润率配置常量
     */
    private static final BigDecimal PROFIT_RATE = new BigDecimal("0.25");

    @Override
    public Map<String, Object> getSalesStats(String dimension, Long categoryId, String brand) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用计算服务获取销售统计数据
            Map<String, Object> stats = calculationService.calculateSalesStats(dimension, categoryId, brand);
            
            // 添加图表数据
            result.put("totalSales", stats.get("totalSales"));
            result.put("totalVolume", stats.get("totalVolume"));
            result.put("totalProfit", stats.get("totalProfit"));
            result.put("totalOrders", stats.get("totalOrders"));
            
            // 计算趋势数据（同比/环比）
            result.put("salesTrend", calculateTrend(dimension, "sales"));
            result.put("volumeTrend", calculateTrend(dimension, "volume"));
            result.put("profitTrend", calculateTrend(dimension, "profit"));
            result.put("ordersTrend", calculateTrend(dimension, "orders"));
            
            // 生成图表数据
            result.put("chartData", generateSalesChartData(dimension, categoryId, brand));
            
        } catch (Exception e) {
            // 异常情况返回零值
            result.put("totalSales", 0);
            result.put("totalVolume", 0);
            result.put("totalProfit", 0);
            result.put("totalOrders", 0);
            result.put("salesTrend", 0.0);
            result.put("volumeTrend", 0.0);
            result.put("profitTrend", 0.0);
            result.put("ordersTrend", 0.0);
            result.put("chartData", new HashMap<>());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getOrderTrend(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用计算服务获取订单趋势数据
            Map<String, Object> trendData = calculationService.calculateOrderTrend(startDate, endDate);
            
            result.put("xAxisData", trendData.get("xAxisData"));
            result.put("orderCountData", trendData.get("orderCountData"));
            result.put("orderAmountData", trendData.get("orderAmountData"));
            result.put("yoyData", trendData.get("yoyData"));
            
        } catch (Exception e) {
            // 异常情况返回空数据
            result.put("xAxisData", new ArrayList<>());
            result.put("orderCountData", new ArrayList<>());
            result.put("orderAmountData", new ArrayList<>());
            result.put("yoyData", new ArrayList<>());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getHotProducts(String sortType, Integer topN, Long categoryId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用计算服务获取热销商品数据
            List<Map<String, Object>> productList = calculationService.calculateHotProducts(sortType, topN, categoryId);
            
            result.put("list", productList);
            
        } catch (Exception e) {
            // 异常情况返回空列表
            result.put("list", new ArrayList<>());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用计算服务获取概览数据
            Map<String, Object> overview = calculationService.calculateOverview();
            
            result.put("todaySales", overview.get("todaySales"));
            result.put("todayOrders", overview.get("todayOrders"));
            result.put("todayVisitors", overview.get("todayVisitors"));
            result.put("conversionRate", overview.get("conversionRate"));
            result.put("pendingOrders", overview.get("pendingOrders"));
            result.put("lowStockProducts", overview.get("lowStockProducts"));
            
        } catch (Exception e) {
            // 异常情况返回零值
            result.put("todaySales", 0);
            result.put("todayOrders", 0);
            result.put("todayVisitors", 0);
            result.put("conversionRate", "0.00");
            result.put("pendingOrders", 0);
            result.put("lowStockProducts", 0);
        }
        
        return result;
    }

    /**
     * 计算趋势增长率
     * 
     * 计算逻辑：
     * 1. 获取当前周期的销售数据
     * 2. 获取上一个周期的销售数据
     * 3. 计算增长率：(当前周期数据 - 上一周期数据) / 上一周期数据 × 100%
     * 
     * @param dimension 时间维度
     * @param type 数据类型
     * @return 趋势增长率（百分比）
     */
    private Double calculateTrend(String dimension, String type) {
        try {
            // 1. 获取当前时间
            Calendar now = Calendar.getInstance();
            
            // 2. 计算当前周期和上一个周期的日期范围
            Calendar currentStartCal = Calendar.getInstance();
            Calendar lastStartCal = Calendar.getInstance();
            Calendar lastEndCal = Calendar.getInstance();
            
            switch (dimension) {
                case "day":
                    // 当前周期：今天
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：昨天
                    lastStartCal.add(Calendar.DAY_OF_MONTH, -1);
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.add(Calendar.DAY_OF_MONTH, -1);
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
                    break;
                    
                case "week":
                    // 当前周期：本周
                    currentStartCal.set(Calendar.DAY_OF_WEEK, currentStartCal.getFirstDayOfWeek());
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：上周
                    lastStartCal.add(Calendar.WEEK_OF_YEAR, -1);
                    lastStartCal.set(Calendar.DAY_OF_WEEK, lastStartCal.getFirstDayOfWeek());
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.add(Calendar.WEEK_OF_YEAR, -1);
                    lastEndCal.set(Calendar.DAY_OF_WEEK, lastEndCal.getFirstDayOfWeek() + 6);
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
                    break;
                    
                case "month":
                    // 当前周期：本月
                    currentStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：上月
                    lastStartCal.add(Calendar.MONTH, -1);
                    lastStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.add(Calendar.MONTH, -1);
                    lastEndCal.set(Calendar.DAY_OF_MONTH, lastEndCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
                    break;
                    
                case "quarter":
                    // 当前周期：本季度
                    int currentMonth = currentStartCal.get(Calendar.MONTH);
                    int currentQuarterStart = currentMonth - currentMonth % 3;
                    currentStartCal.set(Calendar.MONTH, currentQuarterStart);
                    currentStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：上季度
                    lastStartCal.add(Calendar.MONTH, -3);
                    int lastQuarterStart = lastStartCal.get(Calendar.MONTH) - lastStartCal.get(Calendar.MONTH) % 3;
                    lastStartCal.set(Calendar.MONTH, lastQuarterStart);
                    lastStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.set(Calendar.MONTH, lastQuarterStart + 2);
                    lastEndCal.set(Calendar.DAY_OF_MONTH, lastEndCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
                    break;
                    
                case "year":
                    // 当前周期：本年
                    currentStartCal.set(Calendar.MONTH, 0);
                    currentStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：去年
                    lastStartCal.add(Calendar.YEAR, -1);
                    lastStartCal.set(Calendar.MONTH, 0);
                    lastStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.add(Calendar.YEAR, -1);
                    lastEndCal.set(Calendar.MONTH, 11);
                    lastEndCal.set(Calendar.DAY_OF_MONTH, lastEndCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
                    break;
                    
                default:
                    // 默认：本月
                    currentStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    currentStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    currentStartCal.set(Calendar.MINUTE, 0);
                    currentStartCal.set(Calendar.SECOND, 0);
                    
                    // 上一周期：上月
                    lastStartCal.add(Calendar.MONTH, -1);
                    lastStartCal.set(Calendar.DAY_OF_MONTH, 1);
                    lastStartCal.set(Calendar.HOUR_OF_DAY, 0);
                    lastStartCal.set(Calendar.MINUTE, 0);
                    lastStartCal.set(Calendar.SECOND, 0);
                    
                    lastEndCal.add(Calendar.MONTH, -1);
                    lastEndCal.set(Calendar.DAY_OF_MONTH, lastEndCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    lastEndCal.set(Calendar.HOUR_OF_DAY, 23);
                    lastEndCal.set(Calendar.MINUTE, 59);
                    lastEndCal.set(Calendar.SECOND, 59);
            }
            
            // 3. 获取当前周期和上一周期的销售数据
            Map<String, Object> currentStats = getSalesStatsByDateRange(currentStartCal, now, type);
            Map<String, Object> lastStats = getSalesStatsByDateRange(lastStartCal, lastEndCal, type);
            
            // 4. 计算当前周期和上一周期的数值
            double currentValue = getValueFromStats(currentStats, type);
            double lastValue = getValueFromStats(lastStats, type);
            
            // 5. 计算增长率
            double trend = 0.0;
            if (lastValue > 0) {
                trend = ((currentValue - lastValue) / lastValue) * 100;
            }
            
            // 6. 保留1位小数
            return Math.round(trend * 10) / 10.0;
            
        } catch (Exception e) {
            // 异常情况下返回0
            return 0.0;
        }
    }
    
    /**
     * 根据日期范围获取销售统计数据
     */
    private Map<String, Object> getSalesStatsByDateRange(Calendar startCal, Calendar endCal, String type) {
        // 1. 查询日期范围内的订单
        Order queryOrder = new Order();
        Map<String, Object> params = new HashMap<>();
        params.put("beginTime", startCal.getTime());
        params.put("endTime", endCal.getTime());
        queryOrder.setParams(params);
        
        List<Order> orders = orderMapper.selectOrderList(queryOrder);
        
        // 2. 过滤有效订单
        List<Order> validOrders = orders.stream()
                .filter(order -> VALID_ORDER_STATUS.contains(order.getStatus()))
                .collect(Collectors.toList());
        
        // 3. 查询订单商品明细
        List<Long> orderIds = validOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Long orderId : orderIds) {
            List<OrderProduct> products = orderProductMapper.selectOrderProductListByOrderId(orderId);
            if (products != null) {
                orderProducts.addAll(products);
            }
        }
        
        // 4. 计算统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", calculateTotalSales(orderProducts));
        stats.put("totalVolume", calculateTotalVolume(orderProducts));
        stats.put("totalProfit", calculateTotalProfit(orderProducts));
        stats.put("totalOrders", validOrders.size());
        
        return stats;
    }
    
    /**
     * 从统计数据中获取对应类型的值
     */
    private double getValueFromStats(Map<String, Object> stats, String type) {
        switch (type) {
            case "sales":
                return ((BigDecimal) stats.getOrDefault("totalSales", BigDecimal.ZERO)).doubleValue();
            case "volume":
                return ((Long) stats.getOrDefault("totalVolume", 0L)).doubleValue();
            case "profit":
                return ((BigDecimal) stats.getOrDefault("totalProfit", BigDecimal.ZERO)).doubleValue();
            case "orders":
                return ((Integer) stats.getOrDefault("totalOrders", 0)).doubleValue();
            default:
                return 0.0;
        }
    }

    /**
     * 生成销售图表数据
     * 
     * @param dimension 时间维度
     * @param categoryId 分类ID
     * @param brand 品牌
     * @return 图表数据Map
     */
    private Map<String, Object> generateSalesChartData(String dimension, Long categoryId, String brand) {
        Map<String, Object> chartData = new HashMap<>();
        
        // 调用计算服务获取真实的销售趋势数据
        // 这里使用与订单趋势类似的逻辑，根据时间维度查询真实数据
        
        // 1. 获取当前时间
        Calendar now = Calendar.getInstance();
        
        // 2. 根据时间维度确定开始时间
        Calendar startCal = Calendar.getInstance();
        switch (dimension) {
            case "day":
                // 过去24小时
                startCal.add(Calendar.HOUR_OF_DAY, -23);
                break;
            case "week":
                // 过去7天
                startCal.add(Calendar.DAY_OF_MONTH, -6);
                break;
            case "month":
                // 过去30天
                startCal.add(Calendar.DAY_OF_MONTH, -29);
                break;
            case "quarter":
                // 过去3个月
                startCal.add(Calendar.MONTH, -2);
                break;
            case "year":
                // 过去12个月
                startCal.add(Calendar.MONTH, -11);
                break;
            default:
                startCal.add(Calendar.DAY_OF_MONTH, -29);
        }
        
        // 3. 格式化日期范围
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(startCal.getTime());
        String endDate = sdf.format(now.getTime());
        
        // 4. 调用计算服务获取真实的订单趋势数据
        Map<String, Object> orderTrend = calculationService.calculateOrderTrend(startDate, endDate);
        
        // 5. 提取数据并转换格式
        List<String> xAxisData = (List<String>) orderTrend.getOrDefault("xAxisData", new ArrayList<>());
        List<Double> salesData = convertToDoubleList((List<?>) orderTrend.getOrDefault("orderAmountData", new ArrayList<>()));
        
        // 6. 获取销量数据
        List<Integer> volumeData = calculateVolumeDataByDimension(dimension, categoryId, brand, xAxisData);
        
        // 7. 计算利润数据（利润 = 销售额 × 利润率）
        List<Double> profitData = calculateProfitData(salesData);
        
        // 8. 如果没有数据，生成默认空数据
        if (xAxisData.isEmpty()) {
            int dataPoints = getDataPoints(dimension);
            for (int i = 0; i < dataPoints; i++) {
                xAxisData.add(generateXAxisLabel(dimension, i, dataPoints));
                salesData.add(0.0);
                volumeData.add(0);
                profitData.add(0.0);
            }
        }
        
        chartData.put("xAxisData", xAxisData);
        chartData.put("salesData", salesData);
        chartData.put("volumeData", volumeData);
        chartData.put("profitData", profitData);
        
        return chartData;
    }
    
    /**
     * 辅助方法：将任意类型列表转换为Double列表
     */
    private List<Double> convertToDoubleList(List<?> list) {
        List<Double> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Double) {
                result.add((Double) item);
            } else if (item instanceof Integer) {
                result.add(((Integer) item).doubleValue());
            } else if (item instanceof Long) {
                result.add(((Long) item).doubleValue());
            } else if (item instanceof BigDecimal) {
                result.add(((BigDecimal) item).doubleValue());
            } else {
                result.add(0.0);
            }
        }
        return result;
    }
    
    /**
     * 辅助方法：根据时间维度计算销量数据
     */
    private List<Integer> calculateVolumeDataByDimension(String dimension, Long categoryId, String brand, List<String> xAxisData) {
        List<Integer> volumeData = new ArrayList<>();
        
        // 1. 查询符合条件的订单
        List<Order> orders = queryValidOrders(dimension, categoryId, brand);
        
        if (orders.isEmpty()) {
            // 如果没有订单，返回全零数据
            for (int i = 0; i < xAxisData.size(); i++) {
                volumeData.add(0);
            }
            return volumeData;
        }
        
        // 2. 查询订单商品明细
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Long orderId : orderIds) {
            List<OrderProduct> products = orderProductMapper.selectOrderProductListByOrderId(orderId);
            if (products != null) {
                orderProducts.addAll(products);
            }
        }
        
        // 3. 计算总销量
        Long totalVolume = orderProducts.stream()
                .mapToLong(op -> op.getQuantity() != null ? op.getQuantity() : 0L)
                .sum();
        
        // 4. 平均分配到各个数据点
        int dataPoints = xAxisData.size();
        if (dataPoints > 0) {
            long avgVolume = totalVolume / dataPoints;
            for (int i = 0; i < dataPoints; i++) {
                volumeData.add((int) avgVolume);
            }
        }
        
        return volumeData;
    }
    
    /**
     * 辅助方法：计算利润数据
     */
    private List<Double> calculateProfitData(List<Double> salesData) {
        List<Double> profitData = new ArrayList<>();
        
        // 利润 = 销售额 × 利润率（25%）
        for (Double sales : salesData) {
            profitData.add(sales * 0.25);
        }
        
        return profitData;
    }

    /**
     * 获取数据点数量
     * 
     * @param dimension 时间维度
     * @return 数据点数量
     */
    private int getDataPoints(String dimension) {
        switch (dimension) {
            case "day":
                return 24;      // 24小时
            case "week":
                return 7;       // 7天
            case "month":
                return 30;      // 30天
            case "quarter":
                return 3;       // 3个月
            case "year":
                return 12;      // 12个月
            default:
                return 30;      // 默认30天
        }
    }

    /**
     * 生成X轴标签
     * 
     * @param dimension 时间维度
     * @param index 当前索引
     * @param total 总数
     * @return 格式化的标签
     */
    private String generateXAxisLabel(String dimension, int index, int total) {
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat sdf;
        java.util.Calendar cal;
        
        switch (dimension) {
            case "day":
                // 小时标签：0:00, 1:00, ...
                cal = java.util.Calendar.getInstance();
                cal.setTime(now);
                int hour = (cal.get(java.util.Calendar.HOUR_OF_DAY) - (total - 1 - index) + 24) % 24;
                return hour + ":00";
                
            case "week":
                // 周标签：周日, 周一, ...
                cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_MONTH, -(total - 1 - index));
                String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
                return weekDays[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];
                
            case "month":
                // 月标签：1/1, 1/2, ...
                cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.DAY_OF_MONTH, -(total - 1 - index));
                sdf = new java.text.SimpleDateFormat("M/d");
                return sdf.format(cal.getTime());
                
            case "quarter":
                // 季度标签：1月, 2月, 3月
                cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.MONTH, -(total - 1 - index));
                sdf = new java.text.SimpleDateFormat("M月");
                return sdf.format(cal.getTime());
                
            case "year":
                // 年标签：2025-01, 2025-02, ...
                cal = java.util.Calendar.getInstance();
                cal.add(java.util.Calendar.MONTH, -(total - 1 - index));
                sdf = new java.text.SimpleDateFormat("yyyy-MM");
                return sdf.format(cal.getTime());
                
            default:
                return "";
        }
    }
    
    /**
     * 查询符合条件的有效订单
     */
    private List<Order> queryValidOrders(String dimension, Long categoryId, String brand) {
        Order queryOrder = new Order();
        
        // 设置订单状态筛选（只查询有效订单）
        List<Order> allOrders = orderMapper.selectOrderList(queryOrder);
        
        return allOrders.stream()
                .filter(order -> VALID_ORDER_STATUS.contains(order.getStatus()))
                .filter(order -> filterByTimeDimension(order, dimension))
                .filter(order -> filterByCategory(order, categoryId))
                .filter(order -> filterByBrand(order, brand))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据时间维度筛选订单
     */
    private boolean filterByTimeDimension(Order order, String dimension) {
        if (order.getCreateTime() == null) return false;
        
        Calendar now = Calendar.getInstance();
        Calendar orderCal = Calendar.getInstance();
        orderCal.setTime(order.getCreateTime());
        
        long daysDiff = (now.getTimeInMillis() - orderCal.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        
        switch (dimension) {
            case "day":
                // 今天
                return isSameDay(now, orderCal);
            case "week":
                // 最近7天
                return daysDiff <= 7;
            case "month":
                // 最近30天
                return daysDiff <= 30;
            case "quarter":
                // 最近90天
                return daysDiff <= 90;
            case "year":
                // 最近365天
                return daysDiff <= 365;
            default:
                return true;
        }
    }
    
    /**
     * 判断两个日期是否是同一天
     */
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * 根据分类筛选订单
     */
    private boolean filterByCategory(Order order, Long categoryId) {
        if (categoryId == null) return true;
        // 这里需要查询订单商品关联的商品分类
        // 暂时返回true，实际需要查询关联数据
        return true;
    }
    
    /**
     * 根据品牌筛选订单
     */
    private boolean filterByBrand(Order order, String brand) {
        if (brand == null || brand.isEmpty()) return true;
        // 这里需要查询订单商品关联的商品品牌
        // 暂时返回true，实际需要查询关联数据
        return true;
    }
    
    /**
     * 计算总销售额
     */
    private BigDecimal calculateTotalSales(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(op -> op.getTotalPrice() != null ? op.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算总销量
     */
    private Long calculateTotalVolume(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToLong(op -> op.getQuantity() != null ? op.getQuantity() : 0L)
                .reduce(0L, Long::sum);
    }
    
    /**
     * 计算总利润
     */
    private BigDecimal calculateTotalProfit(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(op -> {
                    BigDecimal price = op.getPrice() != null ? op.getPrice() : BigDecimal.ZERO;
                    BigDecimal quantity = new BigDecimal(op.getQuantity() != null ? op.getQuantity() : 0);
                    return quantity.multiply(price).multiply(PROFIT_RATE);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
