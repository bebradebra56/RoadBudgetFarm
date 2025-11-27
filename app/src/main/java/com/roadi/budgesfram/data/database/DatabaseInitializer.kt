package com.roadi.budgesfram.data.database

import com.roadi.budgesfram.data.models.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(
    private val categoryDao: CategoryDao
) {
    fun initializeDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if default categories exist
            val existingCategories = categoryDao.getAllCategories()
            existingCategories.collect { categories ->
                if (categories.isEmpty()) {
                    // Insert default categories
                    categoryDao.insertCategories(Category.defaultCategories)
                }
            }
        }
    }
}
