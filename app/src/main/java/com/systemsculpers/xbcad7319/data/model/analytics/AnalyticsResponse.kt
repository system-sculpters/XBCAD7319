package com.systemsculpers.xbcad7319.data.model.analytics

data class AnalyticsResponse(
    val totalRevenueAmount: Double,
    val users: UserStats,
    val properties: PropertyStats,
    val valuations: ValuationStats,
    val revenueByMonth: List<MonthlyRevenue>
)
