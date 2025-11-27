package com.roadi.budgesfram.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.models.Period
import com.roadi.budgesfram.data.models.TransactionType
import com.roadi.budgesfram.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CategoryStats(
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val color: androidx.compose.ui.graphics.Color
)

data class StatisticsState(
    val selectedPeriod: Period = Period.THIS_MONTH,
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netIncome: Double = 0.0,
    val categoryStats: List<CategoryStats> = emptyList(),
    val isLoading: Boolean = true
)

class StatisticsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: com.roadi.budgesfram.data.repository.CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        loadStatistics()
    }

    fun setPeriod(period: Period) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadStatistics()
    }

    fun setTransactionType(type: TransactionType) {
        _state.value = _state.value.copy(selectedType = type)
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val period = _state.value.selectedPeriod
            val totalIncome = transactionRepository.getTotalIncomeInPeriod(period)
            val totalExpense = transactionRepository.getTotalExpenseInPeriod(period)
            val netIncome = totalIncome - totalExpense

            // Load category statistics
            val categoryStats = loadCategoryStatistics(period, _state.value.selectedType)

            _state.value = _state.value.copy(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                netIncome = netIncome,
                categoryStats = categoryStats,
                isLoading = false
            )
        }
    }

    private suspend fun loadCategoryStatistics(
        period: Period,
        type: TransactionType
    ): List<CategoryStats> {
        val stats = mutableListOf<CategoryStats>()
        
        // Get transactions with categories for the period
        val transactions = transactionRepository.getTransactionsWithCategoryInPeriod(period).first()
        
        // Filter by selected type (Income or Expense)
        val filteredTransactions = transactions.filter { 
            it.transaction.type == type
        }
        
        if (filteredTransactions.isEmpty()) {
            return emptyList()
        }
        
        val total = filteredTransactions.sumOf { it.transaction.amount }
        
        // Group by category and calculate stats
        val categoryMap = filteredTransactions.groupBy { it.category?.id ?: 0L }
        
        categoryMap.forEach { (categoryId, categoryTransactions) ->
            val category = categoryTransactions.firstOrNull()?.category
            if (category != null) {
                val categoryTotal = categoryTransactions.sumOf { it.transaction.amount }
                val percentage = ((categoryTotal / total) * 100).toFloat()
                
                stats.add(
                    CategoryStats(
                        categoryName = category.name,
                        amount = categoryTotal,
                        percentage = percentage,
                        color = category.color
                    )
                )
            }
        }
        
        // Sort by amount descending
        return stats.sortedByDescending { it.amount }
    }

    suspend fun getAllTransactions(): List<com.roadi.budgesfram.data.models.Transaction> {
        return transactionRepository.getAllTransactionsList()
    }
}
