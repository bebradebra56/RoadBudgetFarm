package com.roadi.budgesfram.data.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Material icon name
    val colorHex: String, // Color in hex format
    val type: TransactionType, // Income or Expense
    val isDefault: Boolean = false // System categories can't be deleted
) {
    val color: Color
        get() = Color(android.graphics.Color.parseColor(colorHex))

    companion object {
        // Default categories with farm-themed icons
        val defaultCategories = listOf(
            // Expense categories
            Category(
                name = "Feed",
                icon = "Grass",  // Material Icons: Grass (корм/трава)
                colorHex = "#4CAF50",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            Category(
                name = "Equipment",
                icon = "Build",  // Material Icons: Build (оборудование)
                colorHex = "#3E7BB6",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            Category(
                name = "Transport",
                icon = "LocalShipping",  // Material Icons: LocalShipping (грузовик)
                colorHex = "#3E7BB6",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            Category(
                name = "Maintenance",
                icon = "Handyman",  // Material Icons: Handyman (обслуживание)
                colorHex = "#F28C38",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            Category(
                name = "Veterinary",
                icon = "MedicalServices",  // Material Icons: MedicalServices (ветеринар)
                colorHex = "#E53935",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            Category(
                name = "Other Expenses",
                icon = "MoreHoriz",  // Material Icons: MoreHoriz (другое)
                colorHex = "#7A7A7A",
                type = TransactionType.EXPENSE,
                isDefault = true
            ),
            // Income categories
            Category(
                name = "Egg Sales",
                icon = "Egg",  // Material Icons: Egg (яйца)
                colorHex = "#F4B400",
                type = TransactionType.INCOME,
                isDefault = true
            ),
            Category(
                name = "Livestock Sales",
                icon = "Pets",  // Material Icons: Pets (животные)
                colorHex = "#4CAF50",
                type = TransactionType.INCOME,
                isDefault = true
            ),
            Category(
                name = "Produce Sales",
                icon = "LocalFlorist",  // Material Icons: LocalFlorist (продукция)
                colorHex = "#8BC34A",
                type = TransactionType.INCOME,
                isDefault = true
            ),
            Category(
                name = "Other Income",
                icon = "AttachMoney",  // Material Icons: AttachMoney (другой доход)
                colorHex = "#4CAF50",
                type = TransactionType.INCOME,
                isDefault = true
            )
        )
    }
}
