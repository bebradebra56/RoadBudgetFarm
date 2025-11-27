package com.roadi.budgesfram.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.roadi.budgesfram.data.models.Transaction
import com.roadi.budgesfram.data.models.TransactionType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    fun exportTransactionsToPdf(
        context: Context,
        transactions: List<Transaction>,
        currency: String,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH)
            val fileName = "RoadBudgetFarm_Report_${dateFormatter.format(Date())}.pdf"
            
            // Use app-specific directory - no permissions needed
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            file.parentFile?.mkdirs()

            val writer = PdfWriter(file)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc)

            // Title
            val title = Paragraph("Road Budget Farm - Financial Report")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)

            // Date
            val reportDate = Paragraph("Generated: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH).format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(reportDate)

            document.add(Paragraph("\n"))

            // Summary
            val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val balance = totalIncome - totalExpense

            val summaryTitle = Paragraph("Financial Summary")
                .setFontSize(16f)
                .setBold()
            document.add(summaryTitle)

            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))

            summaryTable.addCell("Total Income")
            summaryTable.addCell(CurrencyFormatter.format(totalIncome, currency))
            summaryTable.addCell("Total Expenses")
            summaryTable.addCell(CurrencyFormatter.format(totalExpense, currency))
            summaryTable.addCell("Balance")
            summaryTable.addCell(CurrencyFormatter.format(balance, currency))

            document.add(summaryTable)
            document.add(Paragraph("\n"))

            // Transactions
            val transactionsTitle = Paragraph("Transaction History")
                .setFontSize(16f)
                .setBold()
            document.add(transactionsTitle)

            val transactionsTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f, 1f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))

            // Header
            transactionsTable.addHeaderCell("Date")
            transactionsTable.addHeaderCell("Description")
            transactionsTable.addHeaderCell("Type")
            transactionsTable.addHeaderCell("Amount")

            // Data
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            transactions.sortedByDescending { it.date }.forEach { transaction ->
                transactionsTable.addCell(dateFormat.format(transaction.date))
                transactionsTable.addCell(transaction.comment ?: "-")
                transactionsTable.addCell(transaction.type.name)
                
                val amountCell = com.itextpdf.layout.element.Cell()
                val amount = CurrencyFormatter.format(transaction.amount, currency)
                amountCell.add(Paragraph(amount))
                
                if (transaction.type == TransactionType.INCOME) {
                    amountCell.setBackgroundColor(DeviceRgb(76, 175, 80), 0.2f)
                } else {
                    amountCell.setBackgroundColor(DeviceRgb(229, 57, 53), 0.2f)
                }
                
                transactionsTable.addCell(amountCell)
            }

            document.add(transactionsTable)

            document.close()

            onSuccess(file)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Failed to generate PDF")
        }
    }

    fun sharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
