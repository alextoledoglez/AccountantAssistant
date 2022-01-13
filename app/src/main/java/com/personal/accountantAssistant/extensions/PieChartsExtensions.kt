package com.personal.accountantAssistant.extensions

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.personal.accountantAssistant.domain.models.home.DashboardModel
import kotlin.math.abs


fun PieChart.drawFrom(model: DashboardModel, label: String?) {
    setDefaultSettings()
    val entries = listOf(
        PieEntry(
            model.expensesItems?.total?.value.orZero().toFloat(),
            model.expensesItems?.total?.strResource
        ),
        PieEntry(abs(model.gainOrNeeded?.value.orZero().toFloat()), model.gainOrNeeded?.strResource)
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

fun PieDataSet.setupDataSetBy(model: DashboardModel) {
    colors = listOf(model.expensesItems?.total?.colorResource, model.gainOrNeeded?.colorResource)
    valueTextColor = Color.WHITE
    valueTextSize = 18f
    valueTypeface = Typeface.DEFAULT_BOLD
    sliceSpace = 4F
}

fun getPieDataBy(dataSet: IPieDataSet) = PieData(dataSet).apply {
    setValueFormatter(getMonetaryValueFormatter())
}