package com.personal.accountantAssistant.ui.currency

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr

class CurrencyEditText : AppCompatEditText {

    private var current = String.EMPTY
    private val editText = this@CurrencyEditText

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

    private fun init() {
        current = String.EMPTY
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val charSequenceStr = charSequence.toString()
                if (charSequenceStr != current) {
                    editText.removeTextChangedListener(this)
                    val masked = charSequenceStr.toCurrencyMaskedStr()
                    current = masked
                    if (masked != charSequenceStr) {
                        editText.apply {
                            setText(masked)
                            setSelection(masked.length)
                        }
                    }
                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}