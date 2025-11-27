package com.roadi.budgesfram.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roadi.budgesfram.data.models.Category
import com.roadi.budgesfram.data.models.TransactionType
import com.roadi.budgesfram.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null,
    val errorMessage: String? = null
)

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun setTransactionType(type: TransactionType) {
        _state.value = _state.value.copy(selectedType = type)
        loadCategoriesForType(type)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _state.value = _state.value.copy(
                    categories = categories,
                    isLoading = false
                )
            }
        }
    }

    private fun loadCategoriesForType(type: TransactionType) {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(type).collect { categories ->
                _state.value = _state.value.copy(
                    categories = categories.filter { it.type == type },
                    isLoading = false
                )
            }
        }
    }

    fun showAddCategoryDialog() {
        _state.value = _state.value.copy(showAddDialog = true)
    }

    fun hideAddCategoryDialog() {
        _state.value = _state.value.copy(
            showAddDialog = false,
            editingCategory = null,
            errorMessage = null
        )
    }

    fun editCategory(category: Category) {
        _state.value = _state.value.copy(
            editingCategory = category,
            showAddDialog = true
        )
    }

    fun saveCategory(name: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val state = _state.value
            val editingCategory = state.editingCategory

            try {
                if (editingCategory != null) {
                    // Update existing category
                    if (editingCategory.name != name && categoryRepository.isCategoryNameTaken(name, editingCategory.id)) {
                        _state.value = state.copy(errorMessage = "Category name already exists")
                        return@launch
                    }

                    val updatedCategory = editingCategory.copy(
                        name = name,
                        icon = icon,
                        colorHex = colorHex
                    )
                    categoryRepository.updateCategory(updatedCategory)
                } else {
                    // Create new category
                    if (categoryRepository.isCategoryNameTaken(name)) {
                        _state.value = state.copy(errorMessage = "Category name already exists")
                        return@launch
                    }

                    val newCategory = Category(
                        name = name,
                        icon = icon,
                        colorHex = colorHex,
                        type = state.selectedType
                    )
                    categoryRepository.insertCategory(newCategory)
                }

                hideAddCategoryDialog()
            } catch (e: Exception) {
                _state.value = state.copy(errorMessage = "Failed to save category: ${e.message}")
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Failed to delete category: ${e.message}")
            }
        }
    }
}
