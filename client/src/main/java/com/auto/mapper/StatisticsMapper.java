package com.auto.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计数据Mapper接口
 * 
 * 说明：
 * SQL查询只返回基础数据
 * 具体的销售额、销量、利润等计算逻辑在StatisticsCalculationService中实现
 * 
 * @author auto
 * @date 2026-01-15
 */
@Mapper
public interface StatisticsMapper {

    /**
     * 查询销售统计的基础数据
     * 
     * 返回数据包含：
     * - orderId: 订单ID
     * - total_price: 订单总价
     * - product_id: 商品ID
     * - quantity: 商品数量
     * - order_product_total_price: 订单商品总价
     * - price: 商品单价
     * - category_id: 分类ID
     * - brand: 品牌
     * 
     * @param dimension 时间维度（day/week/month/quarter/year）
     * @param categoryId 分类ID（可选）
     * @param brand 品牌名称（可选）
     * @return 订单和订单商品的基础数据列表
     */
    List<Map<String, Object>> selectSalesStats(
            @Param("dimension") String dimension, 
            @Param("categoryId") Long categoryId, 
            @Param("brand") String brand);

    /**
     * 查询订单趋势的基础数据
     * 
     * 返回数据包含：
     * - date: 日期（格式：yyyy-MM-dd）
     * - orderId: 订单ID
     * - total_price: 订单总价
     * 
     * 说明：
     * 按日期分组，每个日期返回所有订单数据
     * Java层负责按日期聚合计算订单数和金额
     * 
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 按日期分组的订单基础数据列表
     */
    List<Map<String, Object>> selectOrderTrend(
            @Param("startDate") String startDate, 
            @Param("endDate") String endDate);

    /**
     * 查询热销商品的基础数据
     * 
     * 返回数据包含：
     * - id: 商品ID
     * - name: 商品名称
     * - main_image: 商品主图
     * - category_id: 分类ID
     * - brand: 品牌
     * - status: 商品状态
     * 
     * 说明：
     * 只返回商品基础信息
     * 销量和销售额统计在Java层通过关联订单商品数据计算
     * 
     * @param sortType 排序类型（volume-按销量/sales-按销售额）
     * @param topN 返回前N条
     * @param categoryId 分类ID（可选）
     * @return 商品基础数据列表
     */
    List<Map<String, Object>> selectHotProducts(
            @Param("sortType") String sortType, 
            @Param("topN") Integer topN, 
            @Param("categoryId") Long categoryId);

    /**
     * 查询仪表盘概览的基础数据
     * 
     * 返回数据包含：
     * - orderId: 订单ID
     * - user_id: 用户ID
     * - total_price: 订单总价
     * - status: 订单状态
     * - create_time: 创建时间
     * 
     * 说明：
     * 只返回今日订单的基础数据
     * 销售额、订单数、访客数、转化率等计算在Java层实现
     * 
     * @return 今日订单数据列表
     */
    List<Map<String, Object>> selectOverview();
}
