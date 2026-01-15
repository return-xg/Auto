package com.auto.service;

import java.util.Map;

public interface IStatisticsService {

    Map<String, Object> getSalesStats(String dimension, Long categoryId, String brand);

    Map<String, Object> getOrderTrend(String startDate, String endDate);

    Map<String, Object> getHotProducts(String sortType, Integer topN, Long categoryId);

    Map<String, Object> getOverview();
}
