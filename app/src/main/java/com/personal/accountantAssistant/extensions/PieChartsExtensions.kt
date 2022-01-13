package com.personal.accountantAssistant.extensions

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.personal.accountantAssistant.domain.models.home.SummaryModel
import kotlin.math.abs


fun PieChart.drawFrom(model: SummaryModel, label: String?) {
    setDefaultSettings()
    val entries = listOf(
        PieEntry(model.expenses?.value.orZero(), model.expenses?.strResource),
        PieEntry(abs(model.gainOrNeeded?.value.orZero()), model.gainOrNeeded?.strResource)
    )
    val dataSet = PieDataSet(entries, label).apply { setupDataSetBy(model) }
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

fun PieDataSet.setupDataSetBy(model: SummaryModel) {
    colors = listOf(model.expenses?.colorResource, model.gainOrNeeded?.colorResource)
    valueTextColor = Color.WHITE
    valueTextSize = 18f
    valueTypeface = Typeface.DEFAULT_BOLD
    sliceSpace = 4F
}

fun getPieDataBy(dataSet: IPieDataSet) = PieData(dataSet).apply {
    setValueFormatter(getMonetaryValueFormatter())
}