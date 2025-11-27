package com.roadi.budgesfram.data.repository

import com.roadi.budgesfram.data.database.TransactionDao
import com.roadi.budgesfram.data.models.Period
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
class TransactionRepository(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    fun getTransactionsInPeriod(period: Period): Flow<List<Transaction>> {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTransactionsInPeriod(startDate, endDate)
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByTypeInPeriod(type: TransactionType, period: Period): Flow<List<Transaction>> {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTransactionsByTypeInPeriod(type, startDate, endDate)
    }

    fun getTransactionsByCategoryInPeriod(categoryId: Long, period: Period): Flow<List<Transaction>> {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTransactionsByCategoryInPeriod(categoryId, startDate, endDate)
    }

    suspend fun getTotalIncomeInPeriod(period: Period): Double {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTotalAmountByTypeInPeriod(TransactionType.INCOME, startDate, endDate) ?: 0.0
    }

    suspend fun getTotalExpenseInPeriod(period: Period): Double {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTotalAmountByTypeInPeriod(TransactionType.EXPENSE, startDate, endDate) ?: 0.0
    }

    suspend fun getBalanceInPeriod(period: Period): Double {
        return getTotalIncomeInPeriod(period) - getTotalExpenseInPeriod(period)
    }

    suspend fun getTotalAmountByCategoryInPeriod(categoryId: Long, period: Period): Double {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTotalAmountByCategoryInPeriod(categoryId, startDate, endDate) ?: 0.0
    }

    suspend fun getTransactionCountInPeriod(period: Period): Int {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTransactionCountInPeriod(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)

    fun getAllTransactionsWithCategory(): Flow<List<com.roadi.budgesfram.data.models.TransactionWithCategory>> =
        transactionDao.getAllTransactionsWithCategory()

    fun getTransactionsWithCategoryInPeriod(period: Period): Flow<List<com.roadi.budgesfram.data.models.TransactionWithCategory>> {
        val (startDate, endDate) = period.getDateRange()
        return transactionDao.getTransactionsWithCategoryInPeriod(startDate, endDate)
    }

    suspend fun getAllTransactionsList(): List<Transaction> =
        transactionDao.getAllTransactionsList()
}
