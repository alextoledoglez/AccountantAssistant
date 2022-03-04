package com.personal.accountantAssistant.bases.watchers

import android.text.Editable
import android.text.TextWatcher
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.isNotEmptyAndLengthEqualTo

class DateMaskWatcher : TextWatcher {

    private var updatedText: String? = null

    override fun beforeTextChanged(
        charSequence: CharSequence?, start: Int, before: Int, count: Int
    ) {
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        updatedText = when {
            isDayAndMonthText(text) -> toDayMonthStringFormat(text)
            isDateText(text) -> toDateStringFormat(text)
            else -> null
        }
    }

    override fun afterTextChanged(editable: Editable) {
        updatedText?.let {
            editable.apply {
                clear()
                editable.insert(0, it)
            }
        }
    }

    private fun isDayAndMonthText(text: CharSequence?) = text.toString().let {
        it.isNotEmptyAndLengthEqualTo(DateLength.DAY_MONTH.length) && !it.contains(DateFormat.SEPARATOR.value)
    }

    private fun isDateText(text: CharSequence?) = text.toString().let {
        it.isNotEmptyAndLengthEqualTo(DateLength.DATE.length) && it.filter { char ->
            char.toString() == DateFormat.SEPARATOR.value
        }.count() == 1
    }

    private fun toDayMonthStringFormat(text: CharSequence): String {
        val values = DateValues(text)
        return String.format(DateFormat.DAY_MONTH.value, values.day, values.month)
    }

    private fun toDateStringFormat(text: CharSequence): String {
        val separatorCount = 1
        val values = DateValues(text, separatorCount)
        return String.format(DateFormat.DATE.value, values.day, values.month, values.year)
    }

    private enum class DateIndexes(val index: Int) {
        START(0),
        DAY(2),
        MONTH(4)
    }

    private enum class DateLength(val length: Int) {
        DAY_MONTH(4),
        DATE(9)
    }

    private enum class DateFormat(val value: String) {
        SEPARATOR("/"),
        DAY_MONTH("%s${SEPARATOR.value}%s"),
        DATE("${DAY_MONTH.value}${SEPARATOR.value}%s")
    }

    private data class DateValues(
        val text: CharSequence,
        var day: String = String.EMPTY,
        var month: String = String.EMPTY,
        var year: String = String.EMPTY
    ) {
        constructor(text: CharSequence, separatorCount: Int = 0) : this(
            text, String.EMPTY, String.EMPTY, String.EMPTY
        ) {
            this.day = text.substring(DateIndexes.START.index, DateIndexes.DAY.index)
            this.month = text.substring(
                DateIndexes.DAY.index + separatorCount, DateIndexes.MONTH.index + separatorCount
            )
            this.year = text.substring(DateIndexes.MONTH.index + separatorCount)
        }
    }
}