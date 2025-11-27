package com.roadi.budgesfram.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.models.Period
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.models.TransactionType
import com.roadi.budgesfram.data.models.TransactionWithCategory
import com.roadi.budgesfram.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HistoryState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val filteredTransactions: List<TransactionWithCategory> = emptyList(),
    val selectedPeriod: Period = Period.THIS_MONTH,
    val selectedType: TransactionType? = null,
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class HistoryViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.getTransactionsWithCategoryInPeriod(_state.value.selectedPeriod).collect { transactions ->
                _state.value = _state.value.copy(
                    transactions = transactions,
                    filteredTransactions = filterTransactions(transactions),
                    isLoading = false
                )
            }
        }
    }

    private fun filterTransactions(transactions: List<TransactionWithCategory>): List<TransactionWithCategory> {
        val state = _state.value
        return transactions.filter { transactionWithCategory ->
            val transaction = transactionWithCategory.transaction
            
            // Filter by type
            val typeMatch = state.selectedType == null || transaction.type == state.selectedType

            // Filter by category
            val categoryMatch = state.selectedCategoryId == null || transaction.categoryId == state.selectedCategoryId

            // Filter by search query
            val searchMatch = state.searchQuery.isBlank() ||
                    transaction.comment?.contains(state.searchQuery, ignoreCase = true) == true ||
                    transactionWithCategory.category?.name?.contains(state.searchQuery, ignoreCase = true) == true

            typeMatch && categoryMatch && searchMatch
        }
    }

    fun setPeriod(period: Period) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadTransactions()
    }

    fun setTypeFilter(type: TransactionType?) {
        _state.value = _state.value.copy(selectedType = type)
        updateFilteredTransactions()
    }

    fun setCategoryFilter(categoryId: Long?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
        updateFilteredTransactions()
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        updateFilteredTransactions()
    }

    private fun updateFilteredTransactions() {
        val filtered = filterTransactions(_state.value.transactions)
        _state.value = _state.value.copy(filteredTransactions = filtered)
    }

    fun deleteTransaction(transactionWithCategory: TransactionWithCategory) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionWithCategory.transaction)
        }
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            selectedType = null,
            selectedCategoryId = null,
            searchQuery = ""
        )
        updateFilteredTransactions()
    }
}
