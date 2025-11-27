package com.roadi.budgesfram.data.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.roadi.budgesfram.data.models.Category
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.repository.CategoryRepository
import com.roadi.budgesfram.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

data class ExportData(
    val transactions: List<Transaction>,
    val categories: List<Category>
)

class DataExporter(
    private val context: Context,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    private val gson = Gson()

    suspend fun exportToJson(uri: Uri): Boolean {
        return try {
            val transactions = transactionRepository.getAllTransactions().first()
            val categories = categoryRepository.getAllCategories().first()
            
            val exportData = ExportData(transactions, categories)
            val json = gson.toJson(exportData)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(json)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromJson(uri: Uri): Boolean {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return false

            val type = object : TypeToken<ExportData>() {}.type
            val exportData: ExportData = gson.fromJson(json, type)

            // Import categories first
            exportData.categories.forEach { category ->
                try {
                    categoryRepository.insertCategory(category)
                } catch (e: Exception) {
                    // Category might already exist, continue
                }
            }

            // Import transactions
            exportData.transactions.forEach { transaction ->
                try {
                    transactionRepository.insertTransaction(transaction)
                } catch (e: Exception) {
                    // Transaction might already exist, continue
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
