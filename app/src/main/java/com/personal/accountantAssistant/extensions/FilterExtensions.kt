package com.personal.accountantAssistant.extensions

import android.widget.Filter
import android.widget.Filterable
import java.util.function.Predicate
import java.util.stream.Collectors

fun <T> Filterable.settingFilter(
    current: MutableList<T>,
    filter: (text: String) -> Predicate<T>,
    onResults: (list: ArrayList<T>?) -> Unit
) = object : Filter() {
    override fun performFiltering(charSequence: CharSequence): FilterResults {
        val text = charSequence.toString()
        val list = if (text.isEmpty()) current else current.stream().filter(
            filter(text)
        )?.collect(Collectors.toList())
        return FilterResults().also { it.values = list }
    }

    @Suppress("UNCHECKED_CAST")
    override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
        onResults(filterResults?.values as ArrayList<T>?)
    }
}