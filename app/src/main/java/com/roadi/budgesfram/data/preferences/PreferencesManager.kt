package com.roadi.budgesfram.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currency = MutableStateFlow(getCurrency())
    val currency: StateFlow<String> = _currency

    private val _theme = MutableStateFlow(getTheme())
    val theme: StateFlow<String> = _theme

    private val _dateFormat = MutableStateFlow(getDateFormat())
    val dateFormat: StateFlow<String> = _dateFormat

    fun getCurrency(): String = prefs.getString("currency", "USD") ?: "USD"

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
        _currency.value = currency
    }

    fun getTheme(): String = prefs.getString("theme", "Farm") ?: "Farm"

    fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
        _theme.value = theme
    }

    fun getDateFormat(): String = prefs.getString("date_format", "MM/dd/yyyy") ?: "MM/dd/yyyy"

    fun setDateFormat(format: String) {
        prefs.edit().putString("date_format", format).apply()
        _dateFormat.value = format
    }
}
