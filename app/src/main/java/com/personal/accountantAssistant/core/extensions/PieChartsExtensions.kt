package com.personal.accountantAssistant.core.extensions

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.personal.accountantAssistant.data.dto.Summary
import kotlin.math.abs


fun PieChart.drawFrom(summary: Summary, label: String?) {
    setDefaultSettings()
    val entries = listOf(
            PieEntry(summary.total.orZero(), summary.totalStr),
            PieEntry(abs(summary.needed.orZero()), summary.neededStr)
    )
    val dataSet = PieDataSet(entries, label).apply { setupDataSetBy(summary) }
    data = getPieDataBy(dataSet)
    legend.isEnabled = false
    invalidate()
}

fun PieChart.setDefaultSettings() {
    val extraOffset = 15f
    isDrawHoleEnabled = false
    description.text = String.EMPTY
    animateY(1000)
    setDrawEntryLabels(true)
    setUsePercentValues(false)
    setExtraOffsets(extraOffset, extraOffset, extraOffset, extraOffset)
}

fun PieDataSet.setupDataSetBy(summary: Summary) {
    colors = listOf(summary.totalColor, summary.neededColor)
    valueTextColor = Color.WHITE
    valueTextSize = 18f
    valueTypeface = Typeface.DEFAULT_BOLD
    sliceSpace = 4F
}

fun getPieDataBy(dataSet: IPieDataSet) = PieData(dataSet).apply {
    setValueFormatter(getMonetaryValueFormatter())
}