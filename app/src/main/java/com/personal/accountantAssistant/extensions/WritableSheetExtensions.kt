package com.personal.accountantAssistant.extensions

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import jxl.write.*
import java.util.concurrent.atomic.AtomicInteger

fun WritableSheet.addHeaderCellsValues(values: List<String>) {
    values.forEachIndexed { index, value -> addHeaderCellValue(index, value) }
}

private fun WritableSheet.addHeaderCellValue(colIndex: Int, cellValue: String) {
    try {
        val cellFont = WritableFont(WritableFont.ARIAL, Int.TITLE_POINT_SIZE)
        cellFont.setBoldStyle(WritableFont.BOLD)
        val cellFormat = WritableCellFormat(cellFont)
        setColumnView(colIndex, Int.TITLE_POINT_SIZE)
        addCell(colIndex, Int.HEADER_ROW, cellValue, cellFormat)
    } catch (e: WriteException) {
        e.printStackTrace()
    }
}

fun WritableSheet.addCell(
    colIndex: Int, rowIndex: Int, value: String, format: WritableCellFormat? = null
) {
    try {
        val cellLabel: Label = format?.let {
            Label(colIndex, rowIndex, value, it)
        } ?: Label(colIndex, rowIndex, value)
        addCell(cellLabel)
    } catch (e: WriteException) {
        e.printStackTrace()
    }
}

fun WritableSheet.fillSheetFrom(expenses: List<ExpenseModel>?, type: ExpensesType) {
    addHeaderCellsValues(ExpenseEntity.FIELDS)
    val rowIndex = AtomicInteger(Int.BODY_ROW)
    expenses?.stream()?.filter { type == it.type }?.forEach {
        val currentRowIndex = rowIndex.get()
        addCell(0, currentRowIndex, it.name.toString())
        addCell(1, currentRowIndex, it.quantity.toString())
        addCell(2, currentRowIndex, it.date.toDateStr())
        addCell(3, currentRowIndex, it.unitaryValue.toString())
        addCell(4, currentRowIndex, it.totalValue.toString())
        addCell(5, currentRowIndex, it.type.toString())
        addCell(6, currentRowIndex, it.isActive.toString())
        rowIndex.getAndIncrement()
    }
}