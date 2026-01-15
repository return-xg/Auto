package com.auto.service.impl;

import com.auto.mapper.StatisticsMapper;
import com.auto.service.IStatisticsService;
import com.auto.service.impl.StatisticsCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * 根据时间维度生成基础趋势值，加上随机波动
     * 实际项目中应基于历史数据计算真实的同比/环比
     * 
     * @param dimension 时间维度
     * @param type 数据类型
     * @return 趋势增长率（百分比）
     */
    private Double calculateTrend(String dimension, String type) {
        // 基础趋势值：不同时间维度使用不同的基础值
        double baseTrend = 10.0;
        
        switch (dimension) {
            case "day":
                // 日维度波动较小
                baseTrend = 5.0;
                break;
            case "week":
                // 周维度波动适中
                baseTrend = 8.0;
                break;
            case "month":
                // 月维度波动较大
                baseTrend = 12.0;
                break;
            case "quarter":
                // 季度波动更大
                baseTrend = 15.0;
                break;
            case "year":
                // 年度波动最大
                baseTrend = 20.0;
                break;
            default:
                baseTrend = 10.0;
        }
        
        // 添加随机波动因子：-5%到+15%之间
        double randomFactor = Math.random() * 20 - 5;
        
        // 返回保留1位小数的百分比
        return Math.round((baseTrend + randomFactor) * 10) / 10.0;
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
        List<String> xAxisData = new ArrayList<>();
        List<Double> salesData = new ArrayList<>();
        List<Integer> volumeData = new ArrayList<>();
        List<Double> profitData = new ArrayList<>();
        
        // 根据时间维度确定数据点数量
        int dataPoints = getDataPoints(dimension);
        
        // 生成模拟数据
        // 注意：实际项目中应从数据库查询历史数据
        for (int i = 0; i < dataPoints; i++) {
            xAxisData.add(generateXAxisLabel(dimension, i, dataPoints));
            
            // 销售额：50000到150000之间的随机值
            salesData.add(50000.0 + Math.random() * 100000);
            
            // 销量：200到800之间的随机值
            volumeData.add(200 + (int) (Math.random() * 600));
            
            // 利润：10000到50000之间的随机值
            profitData.add(10000.0 + Math.random() * 40000);
        }
        
        chartData.put("xAxisData", xAxisData);
        chartData.put("salesData", salesData);
        chartData.put("volumeData", volumeData);
        chartData.put("profitData", profitData);
        
        return chartData;
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
                int hour = (now.getHours() - (total - 1 - index) + 24) % 24;
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
}
