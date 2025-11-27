package com.roadi.budgesfram.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.models.Period
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DashboardState(
    val currentBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val currentMonthYear: String = "",
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val currentMonthYear = getCurrentMonthYear()

            // Get recent transactions for the mini chart
            transactionRepository.getTransactionsInPeriod(Period.THIS_MONTH).collect { transactions ->
                val monthlyIncome = transactionRepository.getTotalIncomeInPeriod(Period.THIS_MONTH)
                val monthlyExpense = transactionRepository.getTotalExpenseInPeriod(Period.THIS_MONTH)
                val currentBalance = monthlyIncome - monthlyExpense

                _state.value = DashboardState(
                    currentBalance = currentBalance,
                    monthlyIncome = monthlyIncome,
                    monthlyExpense = monthlyExpense,
                    transactions = transactions,
                    currentMonthYear = currentMonthYear,
                    isLoading = false
                )
            }
        }
    }

    private fun getCurrentMonthYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }

    fun refreshData() {
        loadDashboardData()
    }
}
