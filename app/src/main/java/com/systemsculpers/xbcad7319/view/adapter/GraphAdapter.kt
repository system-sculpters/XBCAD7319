package com.systemsculpers.xbcad7319.view.adapter

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systemsculpers.xbcad7319.AppConstants
import com.systemsculpers.xbcad7319.R
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import com.systemsculpers.xbcad7319.data.model.analytics.MonthlyRevenue

class GraphAdapter(
    private val context: Context,
    private val incomeChart: BarChart,
    private val textColor: Int,
) {
    // Updates all charts and statistics displayed in the analytics section.
    fun updateGraph(value: AnalyticsResponse) {
        setupIncome(value.revenueByMonth) // Set up pie chart based on category statistics
    }


    private fun setupIncome(incomeExpense: List<MonthlyRevenue>) {
        // This method was adapted from YouTube
        // https://youtu.be/-TGUV_LbcmE?si=VItXcDdnX_I8CsiE
        // Admin Grabs Media
        // https://www.youtube.com/@AdminGrabsMedia
        val lastSixMonths = incomeExpense.reversed()
        Log.d("incomeExpense list", "this is the count: ${incomeExpense.size}")
        //totalIncome.text = totalIncome(lastSixMonths)

        val incomeEntries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        // Populating data for the income bars
        for (i in lastSixMonths.indices) {
            incomeEntries.add(BarEntry(i.toFloat(), lastSixMonths[i].totalRevenue.toFloat()))
            labels.add(lastSixMonths[i].month)
        }

        // Creating the dataset for income
        val incomeDataSet = BarDataSet(incomeEntries, "Income")

        // Setting color for income bars
        val colorBlue = ContextCompat.getColor(context, R.color.gold)  // Replace with your desired color
        incomeDataSet.color = colorBlue
        incomeDataSet.valueTextColor = textColor

        // Create BarData with the dataset
        val data = BarData(incomeDataSet)

        // Set bar width
        data.barWidth = 0.7f  // Adjust this value for desired bar width
        if(incomeEntries.size > 6){
            data.barWidth = 0.4f  // Adjust this value for desired bar width
        }
        // Set the data to the chart
        incomeChart.data = data

        val legend = incomeChart.legend
        legend.textColor = textColor
        // Customize X-axis labels
        val xAxis = incomeChart.xAxis
        //xAxis.setLabelCount(lastSixMonths.size, true)
        //xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)  // Set custom labels
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)  // Disable grid lines
        xAxis.granularity = 1f  // Ensure labels align with bars
        xAxis.isGranularityEnabled = true
        xAxis.textColor = textColor

        // if there are more than six entries
        if(incomeEntries.size > 6){
            xAxis.setLabelRotationAngle(45f)  // Rotate labels to avoid overlapping
            xAxis.spaceMin = 0.35f  // Adjust spacing to make room for labels

        }


        val yAxisLeft = incomeChart.axisLeft
        yAxisLeft.textColor = textColor // Replace with your color

        val yAxisRight = incomeChart.axisRight
        yAxisRight.textColor = textColor

        // Customize the Y-axis and other chart properties
        incomeChart.axisRight.isEnabled = false  // Disable right Y-axis
        incomeChart.description.isEnabled = false  // Disable description
        incomeChart.setFitBars(true)  // Make bars fit nicely
        incomeChart.setPinchZoom(false)  // Disable pinch-to-zoom
        incomeChart.animateY(1500)  // Add animation

        // Refresh the chart
        incomeChart.invalidate()
    }

    // Function to calculate the total income from a list of IncomeExpense objects
    private fun totalIncome(income: List<MonthlyRevenue>): String {
        var total = 0.0 // Initialize total income to 0.0

        // Iterate through each IncomeExpense object in the income list
        for (inc in income) {
            total += inc.totalRevenue // Add the income of the current object to the total
        }

        // Return the total income formatted as a string with a "R" prefix
        return "R ${AppConstants.formatAmount(total)}"
    }
}