package com.auto.web.controller.admin;

import com.auto.common.core.controller.BaseController;
import com.auto.common.core.domain.AjaxResult;
import com.auto.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private IStatisticsService statisticsService;

    @GetMapping("/sales")
    public AjaxResult getSalesStats(
            @RequestParam String dimension,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand) {
        try {
            Map<String, Object> data = statisticsService.getSalesStats(dimension, categoryId, brand);
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("获取销售数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/order-trend")
    public AjaxResult getOrderTrend(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Map<String, Object> data = statisticsService.getOrderTrend(startDate, endDate);
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("获取订单趋势数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/hot-products")
    public AjaxResult getHotProducts(
            @RequestParam String sortType,
            @RequestParam Integer topN,
            @RequestParam(required = false) Long categoryId) {
        try {
            Map<String, Object> data = statisticsService.getHotProducts(sortType, topN, categoryId);
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("获取热销商品数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/overview")
    public AjaxResult getOverview() {
        try {
            Map<String, Object> data = statisticsService.getOverview();
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error("获取概览数据失败：" + e.getMessage());
        }
    }
}
