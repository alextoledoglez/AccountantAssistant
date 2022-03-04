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

    private var current = String.EMPTY
    private var currency = String.EMPTY
    private var decimals: Boolean = true
    private val editText = this@MoneyEditText
    private var separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init(
        currency: String? = null, separator: String? = null, decimals: Boolean? = null
    ) {
        this.currency = currency.orEmpty()
        this.separator = separator.orEmpty()
        this.decimals = decimals.orFalse()
        initByAttributes()
    }

    private fun initByAttributes() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString() != current) {
                    editText.removeTextChangedListener(this)
                    val unmaskedText = getUnmaskTextBy(charSequence)
                    if (unmaskedText.isNotEmpty()) {
                        getMaskedTextBy(unmaskedText)?.let {
                            current = it
                            val masked = getOrReplaceMaskedTextBy(it)
                            editText.apply {
                                setText(masked)
                                setSelection(masked.length)
                            }
                        }
                    }
                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun getUnmaskTextBy(sequence: CharSequence) = sequence.toString()
        .replace("[$,.]".toRegex(), String.EMPTY)
        .replace(currency.toRegex(), String.EMPTY)
        .replace("\\s+".toRegex(), String.EMPTY)

    private fun getMaskedTextBy(unmaskedText: String) = try {
        if (decimals) {
            val number = (unmaskedText.toDouble() / 100)
            NumberFormat.getCurrencyInstance().format(number).replace(
                NumberFormat.getCurrencyInstance().currency?.symbol.orEmpty(), currency
            )
        } else {
            val locale = Locale.getDefault()
            val parsed = unmaskedText.toInt()
            "$currency${NumberFormat.getNumberInstance(locale).format(parsed.toLong())}"
        }
    } catch (e: NumberFormatException) {
        null
    }

    private fun getOrReplaceMaskedTextBy(
        maskedText: String
    ) = if (separator != String.COMMA && !decimals)
        maskedText.replace(String.COMMA.toRegex(), separator)
    else
        maskedText
}