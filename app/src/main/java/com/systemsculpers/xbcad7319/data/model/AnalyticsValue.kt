package com.systemsculpers.xbcad7319.data.model

data class AnalyticsValue(
    val icon: Int,     // Label for the income/expense category (e.g., "January" or "Food")
    val value: String,    // Total income for the associated label
    val title: String,
    val color: Int,
)
