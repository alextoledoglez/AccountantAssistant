package com.personal.accountantAssistant.core.extensions

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.personal.accountantAssistant.data.dto.Expenses
import kotlin.math.abs


fun PieChart.drawFrom(expenses: Expenses, label: String?) {
    setDefaultSettings()
    val entries = listOf(
            PieEntry(expenses.total.orZero(), expenses.totalStr),
            PieEntry(abs(expenses.needed.orZero()), expenses.neededStr)
    )
    val dataSet = PieDataSet(entries, label).apply { setupDataSetBy(expenses) }
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

fun PieDataSet.setupDataSetBy(expenses: Expenses) {
    colors = listOf(expenses.totalColor, expenses.neededColor)
    valueTextColor = Color.WHITE
    valueTextSize = 18f
    valueTypeface = Typeface.DEFAULT_BOLD
    sliceSpace = 4F
}

fun getPieDataBy(dataSet: IPieDataSet) = PieData(dataSet).apply {
    setValueFormatter(getMonetaryValueFormatter())
}