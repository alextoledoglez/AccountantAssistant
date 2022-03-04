package com.personal.accountantAssistant.ui.currency

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.personal.accountantAssistant.extensions.COMMA
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orFalse
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

class MoneyEditText : AppCompatEditText {

    private var ctx: Context? = null
    private val editText = this@MoneyEditText

    private var spacing: Boolean = false
    private var decimals: Boolean = true
    private var delimiter: Boolean = false

    private var current = String.EMPTY
    private var currency = String.EMPTY
    private var separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context)
    }

    private fun init(
        context: Context,
        currency: String? = null,
        separator: String? = null,
        spacing: Boolean? = null,
        delimiter: Boolean? = null,
        decimals: Boolean? = null
    ) {
        ctx = context
        this.currency = currency.orEmpty()
        this.separator = separator.orEmpty()
        this.spacing = spacing.orFalse()
        this.delimiter = delimiter.orFalse()
        this.decimals = decimals.orFalse()
        initByAttributes()
    }

    private fun initByAttributes() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString() != current) {
                    editText.removeTextChangedListener(this)
                    val unmaskedText = unmaskText(charSequence)
                    if (unmaskedText.isNotEmpty()) {
                        try {
                            current = getFormattedTextBy(unmaskedText)
                            editText.apply {
                                setText(
                                    if (separator != String.COMMA && !decimals)
                                        current.replace(String.COMMA.toRegex(), separator)
                                    else
                                        current
                                )
                                setSelection(current.length)
                            }
                        } catch (e: NumberFormatException) {
                        }
                    }
                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun unmaskText(sequence: CharSequence) = sequence.toString()
        .replace("[$,.]".toRegex(), String.EMPTY)
        .replace(currency.toRegex(), String.EMPTY)
        .replace("\\s+".toRegex(), String.EMPTY)

    private fun getFormattedTextBy(unmaskedText: String): String {
        val currencyFormat = if (spacing)
            if (delimiter) "$currency. " else "$currency "
        else
            if (delimiter) "$currency." else currency
        return if (decimals) {
            val number = (unmaskedText.toDouble() / 100)
            NumberFormat.getCurrencyInstance().format(number).replace(
                NumberFormat.getCurrencyInstance().currency?.symbol.orEmpty(), currencyFormat
            )
        } else {
            val locale = Locale.getDefault()
            val parsed = unmaskedText.toInt()
            "$currencyFormat${NumberFormat.getNumberInstance(locale).format(parsed.toLong())}"
        }
    }
}