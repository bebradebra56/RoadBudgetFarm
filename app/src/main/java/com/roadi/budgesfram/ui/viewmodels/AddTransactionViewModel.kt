package com.roadi.budgesfram.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.models.Category
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.models.TransactionType
import com.roadi.budgesfram.data.repository.CategoryRepository
import com.roadi.budgesfram.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class AddTransactionState(
    val categories: List<Category> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedCategoryId: Long? = null,
    val amount: String = "",
    val comment: String = "",
    val selectedDate: Date = Date(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(_state.value.selectedType).collect { categories ->
                _state.value = _state.value.copy(
                    categories = categories,
                    selectedCategoryId = categories.firstOrNull()?.id
                )
            }
        }
    }

    fun setTransactionType(type: TransactionType) {
        _state.value = _state.value.copy(
            selectedType = type,
            selectedCategoryId = null
        )
        loadCategories()
    }

    fun setSelectedCategory(categoryId: Long) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
    }

    fun setAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount)
    }

    fun setComment(comment: String) {
        _state.value = _state.value.copy(comment = comment)
    }

    fun setDate(date: Date) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    fun saveTransaction() {
        val state = _state.value

        // Validation
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = state.copy(errorMessage = "Please enter a valid amount")
            return
        }

        if (state.selectedCategoryId == null) {
            _state.value = state.copy(errorMessage = "Please select a category")
            return
        }

        viewModelScope.launch {
            _state.value = state.copy(isLoading = true, errorMessage = null)

            try {
                val transaction = Transaction(
                    amount = amount,
                    type = state.selectedType,
                    categoryId = state.selectedCategoryId!!,
                    date = state.selectedDate,
                    comment = state.comment.takeIf { it.isNotBlank() }
                )

                transactionRepository.insertTransaction(transaction)

                _state.value = state.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = state.copy(
                    isLoading = false,
                    errorMessage = "Failed to save transaction: ${e.message}"
                )
            }
        }
    }

    fun resetState() {
        _state.value = AddTransactionState()
        loadCategories()
    }
}
