package com.personal.accountantAssistant.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.utils.ParserUtils.isNullObject
import java.util.concurrent.atomic.AtomicInteger

object SpinnerUtils {
    fun fill(context: Context?,
             defaultValue: Int,
             selectorObject: Spinner): Int {
        val selectedValue = AtomicInteger(defaultValue)
        val arrayAdapter = ArrayAdapter.createFromResource(context!!,
                R.array.quantity_values,
                android.R.layout.simple_spinner_item)
        selectorObject.adapter = arrayAdapter
        selectorObject.setSelection(selectedValue.get())
        selectorObject.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position)
                if (!isNullObject(selectedItem)) {
                    selectedValue.set(selectedItem.toString().toInt())
                } else {
                    selectedValue.set(defaultValue)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        return selectedValue.get()
    }
}