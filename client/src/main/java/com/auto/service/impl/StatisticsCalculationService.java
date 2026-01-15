package com.auto.service.impl;

import com.auto.domain.Category;
import com.auto.domain.Order;
import com.auto.domain.OrderProduct;
import com.auto.domain.Product;
import com.auto.mapper.CategoryMapper;
import com.auto.mapper.OrderMapper;
import com.auto.mapper.OrderProductMapper;
import com.auto.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据可视化计算服务类
 * 负责将SQL查询中的聚合计算逻辑迁移到Java程序中实现
 * 
 * 功能说明：
 * 1. 销售数据统计计算（销售额、销量、利润、订单数）
 * 2. 订单趋势分析计算（按日期分组统计）
 * 3. 热销商品排行计算（按销量/销售额排序）
 * 4. 仪表盘概览数据计算（今日数据汇总）
 * 
 * @author auto
 * @date 2026-01-15
 */
@Service
public class StatisticsCalculationService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderProductMapper orderProductMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    /**
     * 分类缓存
     * 避免重复查询数据库
     */
    private Map<Long, String> categoryCache = new HashMap<>();
    
    /**
     * 利润率配置常量
     * 
     * 业务说明：
     * 1. 利润率 = 利润 / 销售价格
     * 2. 当前设置为25%，意味着每销售100元商品，利润为25元
     * 3. 成本率 = 1 - 利润率 = 75%，即每销售100元商品，成本为75元
     * 
     * 计算示例：
     * - 商品售价：100元
     * - 商品成本：100 × 75% = 75元
     * - 商品利润：100 × 25% = 25元
     * 
     * 注意事项：
     * 实际项目中应根据真实的业务成本结构调整此值
     * 不同商品类别可能有不同的利润率，可考虑按分类配置
     */
    private static final BigDecimal PROFIT_RATE = new BigDecimal("0.25");
    
    /**
     * 订单有效状态集合
     * 0-待支付, 1-待发货, 2-已发货, 3-已完成
     * 排除已取消(4)的订单
     */
    private static final List<Long> VALID_ORDER_STATUS = Arrays.asList(1L, 2L, 3L);
    
    /**
     * 计算销售统计数据
     * 
     * 计算逻辑：
     * 1. 销售额 = 所有有效订单的商品总价之和
     * 2. 销量 = 所有有效订单的商品数量之和
     * 3. 利润 = 销量 × (价格 - 成本)，其中成本 = 价格 × (1 - 利润率)
     * 4. 订单数 = 有效订单的数量
     * 
     * @param dimension 时间维度（day/week/month/quarter/year）
     * @param categoryId 商品分类ID（可选）
     * @param brand 品牌名称（可选）
     * @return 销售统计数据Map，包含totalSales、totalVolume、totalProfit、totalOrders
     */
    public Map<String, Object> calculateSalesStats(String dimension, Long categoryId, String brand) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询符合条件的所有订单
        List<Order> orders = queryValidOrders(dimension, categoryId, brand);
        
        if (orders == null || orders.isEmpty()) {
            // 如果没有订单数据，返回零值
            result.put("totalSales", BigDecimal.ZERO);
            result.put("totalVolume", 0L);
            result.put("totalProfit", BigDecimal.ZERO);
            result.put("totalOrders", 0);
            return result;
        }
        
        // 2. 查询所有订单商品明细
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
        
        // 3. 计算销售额：所有订单商品的总价之和
        BigDecimal totalSales = calculateTotalSales(orderProducts);
        
        // 4. 计算销量：所有订单商品的数量之和
        Long totalVolume = calculateTotalVolume(orderProducts);
        
        // 5. 计算利润：销量 × (价格 - 成本)
        // 成本 = 价格 × (1 - 利润率) = 价格 × 0.25
        // 利润 = 销量 × (价格 - 成本) = 销量 × 价格 × 利润率
        BigDecimal totalProfit = calculateTotalProfit(orderProducts);
        
        // 6. 计算订单数：有效订单的数量
        Integer totalOrders = orders.size();
        
        result.put("totalSales", totalSales);
        result.put("totalVolume", totalVolume);
        result.put("totalProfit", totalProfit);
        result.put("totalOrders", totalOrders);
        
        return result;
    }
    
    /**
     * 计算订单趋势数据
     * 
     * 计算逻辑：
     * 1. 按日期分组统计订单数量和金额
     * 2. 每天的订单数 = 该天创建的有效订单数量
     * 3. 每天的订单金额 = 该天创建的有效订单总价之和
     * 4. 同比计算 = (当天数据 - 去年同期数据) / 去年同期数据 × 100%
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单趋势数据Map，包含xAxisData、orderCountData、orderAmountData、yoyData
     */
    public Map<String, Object> calculateOrderTrend(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询日期范围内的所有有效订单
        Order queryOrder = new Order();
        queryOrder.setParams(createDateRangeParams(startDate, endDate));
        List<Order> orders = orderMapper.selectOrderList(queryOrder);
        
        if (orders == null || orders.isEmpty()) {
            result.put("xAxisData", new ArrayList<>());
            result.put("orderCountData", new ArrayList<>());
            result.put("orderAmountData", new ArrayList<>());
            result.put("yoyData", new ArrayList<>());
            return result;
        }
        
        // 2. 按日期分组统计订单
        Map<String, List<Order>> ordersByDate = groupOrdersByDate(orders);
        
        // 3. 计算每天的订单数量和金额
        List<String> xAxisData = new ArrayList<>();
        List<Integer> orderCountData = new ArrayList<>();
        List<Double> orderAmountData = new ArrayList<>();
        List<Double> yoyData = new ArrayList<>();
        
        // 按日期排序
        List<String> sortedDates = new ArrayList<>(ordersByDate.keySet());
        Collections.sort(sortedDates);
        
        for (String date : sortedDates) {
            List<Order> dailyOrders = ordersByDate.get(date);
            
            // 添加X轴数据（日期）
            xAxisData.add(formatDateLabel(date));
            
            // 计算当天的订单数量
            orderCountData.add(dailyOrders.size());
            
            // 计算当天的订单金额
            BigDecimal dailyAmount = calculateDailyOrderAmount(dailyOrders);
            orderAmountData.add(dailyAmount.doubleValue());
            
            // 计算同比数据（模拟算法，实际应查询去年同期数据）
            Double yoy = calculateYearOverYear(date, dailyAmount);
            yoyData.add(yoy);
        }
        
        result.put("xAxisData", xAxisData);
        result.put("orderCountData", orderCountData);
        result.put("orderAmountData", orderAmountData);
        result.put("yoyData", yoyData);
        
        return result;
    }
    
    /**
     * 计算热销商品排行
     * 
     * 计算逻辑：
     * 1. 查询所有上架商品
     * 2. 查询所有有效订单的商品明细
     * 3. 按商品ID分组统计销量和销售额
     * 4. 根据排序类型（销量/销售额）排序
     * 5. 取前N条记录
     * 
     * @param sortType 排序类型（volume-按销量/sales-按销售额）
     * @param topN 返回前N条
     * @param categoryId 商品分类ID（可选）
     * @return 热销商品列表Map，包含商品信息和统计数据
     */
    public List<Map<String, Object>> calculateHotProducts(String sortType, Integer topN, Long categoryId) {
        // 1. 查询所有上架商品
        Product queryProduct = new Product();
        queryProduct.setStatus(1); // 只查询上架商品
        if (categoryId != null) {
            queryProduct.setCategoryId(categoryId);
        }
        List<Product> products = productMapper.selectProductList(queryProduct);
        
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 查询所有有效订单的商品明细
        List<Order> allOrders = orderMapper.selectOrderList(new Order());
        List<Long> validOrderIds = allOrders.stream()
                .filter(order -> VALID_ORDER_STATUS.contains(order.getStatus()))
                .map(Order::getId)
                .collect(Collectors.toList());
        
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Long orderId : validOrderIds) {
            List<OrderProduct> orderProductItems = orderProductMapper.selectOrderProductListByOrderId(orderId);
            if (orderProductItems != null) {
                orderProducts.addAll(orderProductItems);
            }
        }
        
        // 3. 按商品ID分组统计销量和销售额
        Map<Long, ProductStats> productStatsMap = calculateProductStatistics(orderProducts);
        
        // 4. 构建热销商品列表
        List<Map<String, Object>> hotProducts = new ArrayList<>();
        
        for (Product product : products) {
            ProductStats stats = productStatsMap.get(product.getId());
            
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("id", product.getId());
            productInfo.put("name", product.getName());
            productInfo.put("image", product.getMainImage());
            productInfo.put("category", getCategoryName(product.getCategoryId()));
            productInfo.put("brand", product.getBrand());
            
            if (stats != null) {
                productInfo.put("volume", stats.getVolume());
                productInfo.put("sales", stats.getSales());
                // 计算同比（模拟算法）
                productInfo.put("yoy", calculateProductYOY(stats));
            } else {
                productInfo.put("volume", 0L);
                productInfo.put("sales", BigDecimal.ZERO);
                productInfo.put("yoy", 0.0);
            }
            
            hotProducts.add(productInfo);
        }
        
        // 5. 根据排序类型排序
        if ("sales".equals(sortType)) {
            // 按销售额降序排序
            hotProducts.sort((a, b) -> {
                BigDecimal salesA = (BigDecimal) a.get("sales");
                BigDecimal salesB = (BigDecimal) b.get("sales");
                return salesB.compareTo(salesA);
            });
        } else {
            // 按销量降序排序（默认）
            hotProducts.sort((a, b) -> {
                Long volumeA = (Long) a.get("volume");
                Long volumeB = (Long) b.get("volume");
                return volumeB.compareTo(volumeA);
            });
        }
        
        // 6. 取前N条记录
        return hotProducts.stream()
                .limit(topN)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算仪表盘概览数据
     * 
     * 计算逻辑：
     * 1. 今日销售额 = 今天创建的有效订单总价之和
     * 2. 今日订单数 = 今天创建的有效订单数量
     * 3. 今日访客数 = 今天创建订单的不同用户数量
     * 4. 转化率 = 今日订单数 / 今日访客数 × 100%
     * 5. 待处理订单数 = 状态为待发货的订单数量
     * 6. 低库存商品数 = 库存小于等于预警值的上架商品数量
     * 
     * @return 概览数据Map
     */
    public Map<String, Object> calculateOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询今天的所有订单
        List<Order> todayOrders = queryTodayOrders();
        
        // 2. 计算今日销售额
        BigDecimal todaySales = calculateTodaySales(todayOrders);
        result.put("todaySales", todaySales);
        
        // 3. 计算今日订单数
        Integer todayOrdersCount = todayOrders.size();
        result.put("todayOrders", todayOrdersCount);
        
        // 4. 计算今日访客数（不同用户数量）
        Set<Long> todayUserIds = todayOrders.stream()
                .map(Order::getUserId)
                .collect(Collectors.toSet());
        Integer todayVisitors = todayUserIds.size();
        result.put("todayVisitors", todayVisitors);
        
        // 5. 计算转化率
        String conversionRate = calculateConversionRate(todayOrdersCount, todayVisitors);
        result.put("conversionRate", conversionRate);
        
        // 6. 查询待处理订单数
        Integer pendingOrders = queryPendingOrders();
        result.put("pendingOrders", pendingOrders);
        
        // 7. 查询低库存商品数
        Integer lowStockProducts = queryLowStockProducts();
        result.put("lowStockProducts", lowStockProducts);
        
        return result;
    }
    
    /**
     * 查询符合条件的有效订单
     * 
     * 筛选条件：
     * 1. 订单状态在有效状态集合中（排除已取消订单）
     * 2. 根据时间维度筛选创建时间
     * 3. 根据商品分类筛选
     * 4. 根据品牌筛选
     * 
     * @param dimension 时间维度
     * @param categoryId 分类ID
     * @param brand 品牌
     * @return 有效订单列表
     */
    private List<Order> queryValidOrders(String dimension, Long categoryId, String brand) {
        Order queryOrder = new Order();
        
        // 设置订单状态筛选（只查询有效订单）
        // 注意：这里需要在Order实体中添加statusList字段或使用自定义查询
        // 暂时查询所有订单，在Service层过滤
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
     * 
     * @param order 订单对象
     * @param dimension 时间维度
     * @return 是否符合时间维度条件
     */
    private boolean filterByTimeDimension(Order order, String dimension) {
        if (order.getCreateTime() == null) return false;
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
        Date createTime = order.getCreateTime();
        Calendar orderCalendar = Calendar.getInstance();
        orderCalendar.setTime(createTime);
        
        long daysDiff = (calendar.getTimeInMillis() - orderCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        
        switch (dimension) {
            case "day":
                // 今天
                return isSameDay(calendar, orderCalendar);
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
     * 需要通过订单商品关联查询商品分类
     */
    private boolean filterByCategory(Order order, Long categoryId) {
        if (categoryId == null) return true;
        // 这里需要查询订单商品关联的商品分类
        // 暂时返回true，实际需要查询关联数据
        return true;
    }
    
    /**
     * 根据品牌筛选订单
     * 需要通过订单商品关联查询商品品牌
     */
    private boolean filterByBrand(Order order, String brand) {
        if (brand == null || brand.isEmpty()) return true;
        // 这里需要查询订单商品关联的商品品牌
        // 暂时返回true，实际需要查询关联数据
        return true;
    }
    
    /**
     * 创建日期范围查询参数
     */
    private Map<String, Object> createDateRangeParams(String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        if (startDate != null && !startDate.isEmpty()) {
            params.put("beginTime", startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endTime", endDate + " 23:59:59");
        }
        return params;
    }
    
    /**
     * 计算总销售额
     * 公式：SUM(订单商品总价)
     * 
     * @param orderProducts 订单商品列表
     * @return 总销售额
     */
    private BigDecimal calculateTotalSales(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算总销量
     * 公式：SUM(订单商品数量)
     * 
     * @param orderProducts 订单商品列表
     * @return 总销量
     */
    private Long calculateTotalVolume(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToLong(op -> op.getQuantity() != null ? op.getQuantity() : 0L)
                .reduce(0L, Long::sum);
    }
    
    /**
     * 计算总利润
     * 
     * 计算原理说明：
     * 1. 利润定义：商品销售价格减去商品成本价格后的收益
     * 2. 成本计算：假设商品成本是销售价格的25%，即成本 = 销售价格 × 0.25
     * 3. 利润率：75%（即销售价格的75%是利润）
     * 4. 单个商品利润 = 销售数量 × 销售价格 × 利润率
     * 5. 总利润 = 所有订单商品利润之和
     * 
     * 计算公式：
     * 利润 = Σ(数量 × 价格 × 0.75)
     * 
     * 示例：
     * 某商品销售价格100元，成本25元，利润率75%
     * 如果销售了10件，则利润 = 10 × 100 × 0.75 = 750元
     * 
     * @param orderProducts 订单商品列表，包含每个订单的商品明细信息
     * @return 总利润，保留两位小数
     */
    private BigDecimal calculateTotalProfit(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(op -> {
                    // 获取商品销售价格，如果为空则使用0
                    BigDecimal price = op.getPrice() != null ? op.getPrice() : BigDecimal.ZERO;
                    
                    // 获取商品销售数量，转换为BigDecimal类型，如果为空则使用0
                    BigDecimal quantity = new BigDecimal(op.getQuantity() != null ? op.getQuantity() : 0);
                    
                    // 计算单个订单商品的利润
                    // 利润计算公式：数量 × 价格 × 利润率(0.75)
                    // 例如：10件 × 100元 × 0.75 = 750元
                    return quantity.multiply(price).multiply(PROFIT_RATE);
                })
                // 将所有订单商品的利润累加，得到总利润
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                // 保留两位小数，四舍五入
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 按日期分组订单
     * 
     * @param orders 订单列表
     * @return 按日期分组的订单Map
     */
    private Map<String, List<Order>> groupOrdersByDate(List<Order> orders) {
        Map<String, List<Order>> groupedOrders = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Order order : orders) {
            if (order.getCreateTime() == null) continue;
            
            String dateKey = sdf.format(order.getCreateTime());
            groupedOrders.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(order);
        }
        
        return groupedOrders;
    }
    
    /**
     * 计算每日订单金额
     * 
     * @param dailyOrders 当天的订单列表
     * @return 当天订单总金额
     */
    private BigDecimal calculateDailyOrderAmount(List<Order> dailyOrders) {
        return dailyOrders.stream()
                .map(order -> order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算同比数据
     * 公式：(本期数据 - 去年同期数据) / 去年同期数据 × 100%
     * 
     * 注意：当前使用模拟算法，实际应查询去年同期数据进行计算
     * 
     * @param date 日期
     * @param currentAmount 当前金额
     * @return 同比增长率
     */
    private Double calculateYearOverYear(String date, BigDecimal currentAmount) {
        // 模拟算法：生成-10%到+30%之间的随机增长率
        double baseTrend = 10.0;
        double randomFactor = Math.random() * 40 - 10;
        double yoy = baseTrend + randomFactor;
        return Math.round(yoy * 10) / 10.0;
    }
    
    /**
     * 计算商品同比数据
     * 
     * @param stats 商品统计数据
     * @return 同比增长率
     */
    private Double calculateProductYOY(ProductStats stats) {
        // 模拟算法：基于销量和销售额生成同比数据
        double baseTrend = 10.0;
        double randomFactor = Math.random() * 30 - 10;
        double yoy = baseTrend + randomFactor;
        return Math.round(yoy * 10) / 10.0;
    }
    
    /**
     * 格式化日期标签
     * 
     * @param date 日期字符串
     * @return 格式化后的日期标签
     */
    private String formatDateLabel(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateObj = inputFormat.parse(date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d");
            return outputFormat.format(dateObj);
        } catch (Exception e) {
            return date;
        }
    }
    
    /**
     * 查询今天的订单
     * 
     * @return 今天创建的有效订单列表
     */
    private List<Order> queryTodayOrders() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        
        Order queryOrder = new Order();
        queryOrder.setParams(Map.of(
            "beginTime", today.getTime(),
            "endTime", new Date()
        ));
        
        List<Order> allOrders = orderMapper.selectOrderList(queryOrder);
        
        return allOrders.stream()
                .filter(order -> VALID_ORDER_STATUS.contains(order.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * 计算今日销售额
     * 
     * @param todayOrders 今天的订单列表
     * @return 今日销售额
     */
    private BigDecimal calculateTodaySales(List<Order> todayOrders) {
        return todayOrders.stream()
                .map(order -> order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算转化率
     * 公式：订单数 / 访客数 × 100%
     * 
     * @param orders 订单数
     * @param visitors 访客数
     * @return 转化率（保留2位小数）
     */
    private String calculateConversionRate(Integer orders, Integer visitors) {
        if (visitors == null || visitors == 0) {
            return "0.00";
        }
        double rate = (orders.doubleValue() / visitors.doubleValue()) * 100;
        return String.format("%.2f", rate);
    }
    
    /**
     * 查询待处理订单数
     * 状态为1（待发货）的订单数量
     * 
     * @return 待处理订单数
     */
    private Integer queryPendingOrders() {
        Order queryOrder = new Order();
        queryOrder.setStatus(1L);
        
        List<Order> pendingOrders = orderMapper.selectOrderList(queryOrder);
        return pendingOrders != null ? pendingOrders.size() : 0;
    }
    
    /**
     * 查询低库存商品数
     * 库存小于等于预警值的上架商品数量
     * 
     * @return 低库存商品数
     */
    private Integer queryLowStockProducts() {
        Product queryProduct = new Product();
        queryProduct.setStatus(1); // 只查询上架商品
        
        List<Product> products = productMapper.selectProductList(queryProduct);
        
        if (products == null) return 0;
        
        return (int) products.stream()
                .filter(p -> p.getStock() != null && p.getWarnStock() != null)
                .filter(p -> p.getStock() <= p.getWarnStock())
                .count();
    }
    
    /**
     * 按商品ID分组统计销量和销售额
     * 
     * @param orderProducts 订单商品列表
     * @return 商品ID到统计数据的Map
     */
    private Map<Long, ProductStats> calculateProductStatistics(List<OrderProduct> orderProducts) {
        Map<Long, ProductStats> statsMap = new HashMap<>();
        
        for (OrderProduct op : orderProducts) {
            Long productId = op.getProductId();
            ProductStats stats = statsMap.computeIfAbsent(productId, k -> new ProductStats());
            
            // 累加销量
            stats.addVolume(op.getQuantity() != null ? op.getQuantity() : 0L);
            
            // 累加销售额
            BigDecimal totalPrice = op.getTotalPrice() != null ? op.getTotalPrice() : BigDecimal.ZERO;
            stats.addSales(totalPrice);
        }
        
        return statsMap;
    }
    
    /**
     * 获取分类名称
     * 
     * @param categoryId 分类ID
     * @return 分类名称
     */
    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "未分类";
        
        // 先从缓存中查找
        String cachedName = categoryCache.get(categoryId);
        if (cachedName != null) {
            return cachedName;
        }
        
        // 缓存中没有，查询数据库
        try {
            Category category = categoryMapper.selectCategoryById(categoryId);
            if (category != null && category.getName() != null) {
                categoryCache.put(categoryId, category.getName());
                return category.getName();
            }
        } catch (Exception e) {
            // 查询失败，返回默认值
        }
        
        return "未分类";
    }
    
    /**
     * 商品统计数据内部类
     * 用于存储每个商品的销量和销售额统计
     */
    private static class ProductStats {
        private Long volume = 0L;
        private BigDecimal sales = BigDecimal.ZERO;
        
        public Long getVolume() {
            return volume;
        }
        
        public void addVolume(Long v) {
            this.volume += v;
        }
        
        public BigDecimal getSales() {
            return sales;
        }
        
        public void addSales(BigDecimal s) {
            this.sales = this.sales.add(s);
        }
    }
}
