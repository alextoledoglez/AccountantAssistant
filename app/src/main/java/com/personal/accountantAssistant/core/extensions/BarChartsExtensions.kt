package com.personal.accountantAssistant.core.extensions

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.dto.Expenses

const val BAR_STACK_MIN_SIZE = 10f

fun BarChart.drawFrom(expenses: Expenses) {
    val xAxisValues: ArrayList<String> = ArrayList()
    setDefaultSettings()

    val models = listOf(
            Pair(0, "A"),
            Pair(1, "B"),
            Pair(2, "C"),
            Pair(3, "D"),
            Pair(4, "E"),
            Pair(5, "F")
    )

    val barDataSets = models.mapIndexed { index, model ->
        val barEntries = listOf(
                BarEntry(index.toFloat(), toBarStackSize(model.first.toFloat()))
        )
        xAxisValues.add(index, model.second)
        BarDataSet(barEntries, String.EMPTY).apply {
            setDefaultSettings()
            colors = listOf(Color.GREEN)
            gradientColors = listOf(GradientColor(Color.GREEN, Color.GRAY))
        }
    }
    data = getBarDataBy(barDataSets)
    xAxis.setDefaultSettings(xAxisValues.toTypedArray())
    legend.setDefaultSettings().setCustom(
            models.distinctBy { it.second }.map { newLegendEntry(context, R.color.colorBlack, it.second) }
    )
    invalidate()
}

fun getBarDataBy(dataSets: List<IBarDataSet>, color: Int = Color.WHITE) = BarData(dataSets).apply {
    setValueTextColor(color)
}

fun BarChart.setDefaultSettings() {
    description.isEnabled = false
    isHighlightFullBarEnabled = true
    axisRight.isEnabled = false
    axisLeft.apply {
        axisMinimum = 0f
        isEnabled = false
    }
    animateY(500)
    setPinchZoom(false)
    setDrawValueAboveBar(false)
    setDrawGridBackground(false)
    setExtraOffsets(5f, 5f, 5f, 15f)
    setTouchEnabled(false)
    setFitBars(true)
}

fun XAxis.setDefaultSettings(array: Array<String>) {
    granularity = 1f
    textSize = 9f
    labelRotationAngle = -30f
    typeface = Typeface.DEFAULT_BOLD
    position = XAxis.XAxisPosition.BOTTOM
    valueFormatter = toFormattedValue(array)
    setDrawGridLines(false)
}

fun BarDataSet.setDefaultSettings() {
    valueTextSize = 12f
    valueTypeface = Typeface.DEFAULT_BOLD
    valueFormatter = getRoundedValueFormatter()
}

fun toBarStackSize(percent: Float) = (percent + BAR_STACK_MIN_SIZE)

fun toBarStackPercent(value: Float) = if (value > BAR_STACK_MIN_SIZE)
    (value - BAR_STACK_MIN_SIZE)
else
    (BAR_STACK_MIN_SIZE - value)