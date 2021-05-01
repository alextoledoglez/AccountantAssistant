package com.personal.accountantAssistant.utils

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.personal.accountantAssistant.utils.ActionUtils.runAction
import java.text.Normalizer
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object EditableTextsUtils {
    private const val EMPTY_VALUE = ""
    var editTextValue = AtomicReference(EMPTY_VALUE)
    fun initializeListeners(activity: Activity?, editText: EditText, actionWhenTextChange: () -> Unit) {
        editTextValue = AtomicReference(EMPTY_VALUE)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setEditTextValue(editText.text.toString())
                runAction(actionWhenTextChange)
            }

            override fun afterTextChanged(s: Editable) {
                setEditTextValue(editText.text.toString())
                runAction(actionWhenTextChange)
            }
        })
        editText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_DONE) {
                activity?.let { hideSoftInputFromWindow(it, editText) }
            }
            java.lang.Boolean.TRUE
        }
        editText.onFocusChangeListener = OnFocusChangeListener { view: View?, hasFocus: Boolean ->
            hasFocus.not().apply { hideSoftInputFromWindow(view, editText) }
        }
    }

    fun hideSoftInputFromWindow(activity: Activity?, editText: EditText) {
        activity?.currentFocus?.let { hideSoftInputFromWindow(it, editText) }
    }

    private fun hideSoftInputFromWindow(view: View?, editText: EditText) {
        view?.let {
            val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            editText.clearFocus()
        }
    }

    private fun setEditTextValue(textValue: String) {
        editTextValue.set(textValue)
    }

    fun getEditTextValue(): String {
        return editTextValue.get()
    }

    /**
     * Normalizing string, so it will be possible to search.
     */
    private fun toNormalizedString(strValue: String?): String {
        val regexTarget = "[^\\p{ASCII}]"
        return Normalizer.normalize(strValue, Normalizer.Form.NFD)
                .replace(regexTarget.toRegex(), Constants.EMPTY_STR)
    }

    @JvmStatic
    fun contains(currentStr: String?, filterStr: String?): Boolean {
        val normalizedCurrentStr = toNormalizedString(currentStr).toLowerCase(Locale.ROOT)
        val normalizedFilterName = toNormalizedString(filterStr).toLowerCase(Locale.ROOT)
        return normalizedCurrentStr.contains(normalizedFilterName)
    }
}