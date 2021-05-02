package com.personal.accountantAssistant.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.db.DatabaseManager
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import com.personal.accountantAssistant.utils.DateUtils.toCurrentDateStr
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object ImportExportUtils {
    private const val FIRST_SHEET = 0
    private const val HEADER_ROW = 0
    private const val BODY_ROW = HEADER_ROW + 1
    private const val TITLE_POINT_SIZE = 16
    fun xlsImport(context: Context?, type: PaymentsType?) {
        //TODO
        print(type)
        ToastUtils.showShortText(context, R.string.excel_data_imported)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun xlsExport(context: Context, type: PaymentsType) {
        val storageDirectory = context.getExternalFilesDir("")
        val directory = storageDirectory?.absolutePath?.let { File(it) }
        val directoryExist = directory?.isDirectory?.not().let {
            directory?.mkdirs()
        }
        directoryExist?.let {
            try {
                val BUYS = "Buys"
                val BILLS = "Bills"
                var xlsFileName = Constants.EMPTY_STR
                var sheetName = Constants.EMPTY_STR
                if (PaymentsType.isBuy(type)) {
                    xlsFileName = BUYS
                    sheetName = BUYS
                } else if (PaymentsType.isBill(type)) {
                    xlsFileName = BILLS
                    sheetName = BILLS
                }
                xlsFileName += "(" + toCurrentDateStr() + ").xls"
                sheetName += "_list"
                val xlsFile = File(directory, xlsFileName)
                val wbSettings = WorkbookSettings()
                wbSettings.locale = Locale(LocaleTypes.EN.language, LocaleTypes.EN.name)
                val workbook = Workbook.createWorkbook(xlsFile, wbSettings)
                val sheet = workbook.createSheet(sheetName, FIRST_SHEET)
                fillSheetFrom(context, sheet, type)
                workbook.write()
                workbook.close()
                ToastUtils.showShortText(context, R.string.excel_data_exported)
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun fillSheetFrom(context: Context,
                              sheet: WritableSheet,
                              type: PaymentsType) {
        setHeaderCell(sheet)
        val rowIndex = AtomicInteger(BODY_ROW)
        DatabaseManager(context)
                .getPaymentsRecords()
                .stream()
                .filter { type == it.type }
                .forEach { payment: Payments ->
                    val currentRowIndex = rowIndex.get()
                    addCell(sheet, 0, currentRowIndex, payment.name.toString())
                    addCell(sheet, 1, currentRowIndex, payment.quantity.toString())
                    addCell(sheet, 2, currentRowIndex, getConditionalDateValueFrom(payment))
                    addCell(sheet, 3, currentRowIndex, payment.unitaryValue.toString())
                    addCell(sheet, 4, currentRowIndex, payment.totalValue.toString())
                    addCell(sheet, 5, currentRowIndex, payment.type?.name.toString())
                    addCell(sheet, 6, currentRowIndex, payment.isActive.toString())
                    rowIndex.getAndIncrement()
                }
    }

    private fun getConditionalDateValueFrom(payment: Payments): String {
        return if (payment.isBill == true) payment.date.toString() else Constants.DASH_SEPARATOR
    }

    private fun setHeaderCell(sheet: WritableSheet) {
        var colIndex = 0
        for (value in PaymentsEnum.values()) {
            addHeaderCell(sheet, colIndex, value.value)
            colIndex++
        }
    }

    private fun addHeaderCell(sheet: WritableSheet,
                              colIndex: Int,
                              cellValue: String) {
        try {
            val cellFont = WritableFont(WritableFont.ARIAL, TITLE_POINT_SIZE)
            cellFont.setBoldStyle(WritableFont.BOLD)
            val cellFormat = WritableCellFormat(cellFont)
            sheet.setColumnView(colIndex, TITLE_POINT_SIZE)
            addCell(sheet, colIndex, HEADER_ROW, cellValue, cellFormat)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    private fun addCell(sheet: WritableSheet,
                        colIndex: Int,
                        rowIndex: Int,
                        value: String,
                        cellFormat: WritableCellFormat? = null) {
        try {
            val cellLabel: Label = if (ParserUtils.isNullObject(cellFormat)) {
                Label(colIndex, rowIndex, value)
            } else {
                Label(colIndex, rowIndex, value, cellFormat)
            }
            sheet.addCell(cellLabel)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }
}