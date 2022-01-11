package com.personal.accountantAssistant.extensions

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

fun getMonetaryValueFormatter() = object : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "$ $value"
    }
}

fun getRoundedValueFormatter() = object : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${toBarStackPercent(value).roundToInt()}%"
    }
}

fun toFormattedValue(array: Array<String>) = object : ValueFormatter() {
    override fun getFormattedValue(index: Float): String = if (array.size > index.toInt())
        array[index.toInt()]
    else
        String.EMPTY
}

fun Description.setDefaultSettings() = apply {
    textAlign = Paint.Align.CENTER
    typeface = Typeface.DEFAULT_BOLD
}

fun Legend.setDefaultSettings() = apply {
    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    textSize = 15f
    isWordWrapEnabled = true
    typeface = Typeface.DEFAULT_BOLD
    xEntrySpace = 10f
}

fun Legend.setCircularSettings() = apply {
    setDefaultSettings()
    form = Legend.LegendForm.CIRCLE
    formSize = 10f
    formLineWidth = 10f
}

fun newLegendEntry(context: Context, @ColorRes color: Int, label: String) = LegendEntry(
        label, Legend.LegendForm.CIRCLE, 50f, 50f, null, ContextCompat.getColor(context, color)
)
