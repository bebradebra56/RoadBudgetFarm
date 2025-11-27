package com.roadi.budgesfram.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.preferences.PreferencesManager
import com.roadi.budgesfram.data.repository.CategoryRepository
import com.roadi.budgesfram.data.repository.TransactionRepository
import com.roadi.budgesfram.data.util.DataExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val currency: String = "USD",
    val dateFormat: String = "MM/dd/yyyy",
    val theme: String = "Farm",
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val message: String? = null
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val dataExporter = DataExporter(context, transactionRepository, categoryRepository)

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            preferencesManager.currency.collect { currency ->
                _state.value = _state.value.copy(currency = currency)
            }
        }

        viewModelScope.launch {
            preferencesManager.dateFormat.collect { format ->
                _state.value = _state.value.copy(dateFormat = format)
            }
        }

        viewModelScope.launch {
            preferencesManager.theme.collect { theme ->
                _state.value = _state.value.copy(theme = theme)
            }
        }
    }

    fun setCurrency(currency: String) {
        preferencesManager.setCurrency(currency)
        _state.value = _state.value.copy(currency = currency)
    }

    fun setDateFormat(format: String) {
        preferencesManager.setDateFormat(format)
        _state.value = _state.value.copy(dateFormat = format)
    }

    fun setTheme(theme: String) {
        preferencesManager.setTheme(theme)
        _state.value = _state.value.copy(theme = theme)
    }

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, message = null)
            val success = dataExporter.exportToJson(uri)
            _state.value = _state.value.copy(
                isExporting = false,
                message = if (success) "Data exported successfully" else "Export failed"
            )
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isImporting = true, message = null)
            val success = dataExporter.importFromJson(uri)
            _state.value = _state.value.copy(
                isImporting = false,
                message = if (success) "Data imported successfully" else "Import failed"
            )
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }
}
