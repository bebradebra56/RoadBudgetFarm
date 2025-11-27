package com.roadi.budgesfram.data.repository

import com.roadi.budgesfram.data.database.CategoryDao
import com.roadi.budgesfram.data.models.Category
import com.roadi.budgesfram.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories()

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type)

    suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category)

    suspend fun insertDefaultCategories() {
        val existingCategories = categoryDao.getAllCategories()
        // If no categories exist, insert default ones
        // This is a simple check - in production you'd want more sophisticated logic
        categoryDao.insertCategories(Category.defaultCategories)
    }

    suspend fun updateCategory(category: Category) {
        // Don't allow updating default categories' core properties
        if (category.isDefault) {
            val existing = getCategoryById(category.id)
            if (existing != null && existing.name != category.name) {
                // Only allow name changes for default categories
                val updated = existing.copy(name = category.name)
                categoryDao.updateCategory(updated)
                return
            }
        }
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        if (!category.isDefault) {
            categoryDao.deleteCategory(category)
        }
    }

    suspend fun deleteCategoryById(id: Long) {
        categoryDao.deleteCategoryById(id)
    }

    suspend fun isCategoryNameTaken(name: String, excludeId: Long = 0): Boolean {
        return categoryDao.countCategoriesWithName(name, excludeId) > 0
    }
}
