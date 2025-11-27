package com.roadi.budgesfram.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.roadi.budgesfram.data.models.Period
import com.roadi.budgesfram.data.preferences.PreferencesManager
import com.roadi.budgesfram.ui.theme.BackgroundLight
import com.roadi.budgesfram.ui.theme.GoldAccent
import com.roadi.budgesfram.ui.theme.GreenPositive
import com.roadi.budgesfram.ui.theme.RedNegative
import com.roadi.budgesfram.ui.viewmodels.CategoryStats
import com.roadi.budgesfram.ui.viewmodels.StatisticsViewModel
import com.roadi.budgesfram.utils.CurrencyFormatter
import com.roadi.budgesfram.utils.PdfExporter
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val preferencesManager: PreferencesManager = koinInject()
    val currency by preferencesManager.currency.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics & Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Period selector
            item {
                PeriodSelector(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodSelected = { viewModel.setPeriod(it) }
                )
            }

            // Type selector (Income/Expense)
            item {
                TypeSelector(
                    selectedType = state.selectedType,
                    onTypeSelected = { viewModel.setTransactionType(it) }
                )
            }

            // Summary cards
            item {
                SummaryCards(
                    totalIncome = state.totalIncome,
                    totalExpense = state.totalExpense,
                    netIncome = state.netIncome,
                    currency = currency
                )
            }

            // Pie chart
            item {
                PieChartCard(
                    categoryStats = state.categoryStats,
                    transactionType = state.selectedType
                )
            }

            // Category breakdown
            item {
                CategoryBreakdownCard(
                    categoryStats = state.categoryStats,
                    currency = currency,
                    transactionType = state.selectedType
                )
            }

            // Export button
            item {
                ExportButton(
                    isExporting = isExporting,
                    onExport = {
                        isExporting = true
                        coroutineScope.launch {
                            val allTransactions = withContext(Dispatchers.IO) {
                                viewModel.getAllTransactions()
                            }
                            
                            withContext(Dispatchers.IO) {
                                PdfExporter.exportTransactionsToPdf(
                                    context = context,
                                    transactions = allTransactions,
                                    currency = currency,
                                    onSuccess = { file ->
                                        coroutineScope.launch(Dispatchers.Main) {
                                            isExporting = false
                                            Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_LONG).show()
                                            PdfExporter.sharePdf(context, file)
                                        }
                                    },
                                    onError = { error ->
                                        coroutineScope.launch(Dispatchers.Main) {
                                            isExporting = false
                                            Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodSelected: (Period) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPeriod.getDisplayName(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Period.values().forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.getDisplayName()) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TypeSelector(
    selectedType: com.roadi.budgesfram.data.models.TransactionType,
    onTypeSelected: (com.roadi.budgesfram.data.models.TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TypeButton(
            text = "Expenses",
            isSelected = selectedType == com.roadi.budgesfram.data.models.TransactionType.EXPENSE,
            onClick = { onTypeSelected(com.roadi.budgesfram.data.models.TransactionType.EXPENSE) },
            modifier = Modifier.weight(1f)
        )
        TypeButton(
            text = "Income",
            isSelected = selectedType == com.roadi.budgesfram.data.models.TransactionType.INCOME,
            onClick = { onTypeSelected(com.roadi.budgesfram.data.models.TransactionType.INCOME) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) GoldAccent else Color.White,
            contentColor = if (isSelected) Color.White else GoldAccent
        ),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun SummaryCards(
    totalIncome: Double,
    totalExpense: Double,
    netIncome: Double,
    currency: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Income",
            amount = totalIncome,
            color = GreenPositive,
            currency = currency,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Expenses",
            amount = totalExpense,
            color = RedNegative,
            currency = currency,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    SummaryCard(
        title = "Net Income",
        amount = netIncome,
        color = if (netIncome >= 0) GreenPositive else RedNegative,
        currency = currency,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    currency: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.format(amount, currency),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PieChartCard(
    categoryStats: List<CategoryStats>,
    transactionType: com.roadi.budgesfram.data.models.TransactionType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (transactionType == com.roadi.budgesfram.data.models.TransactionType.EXPENSE) 
                    "Expenses by Category" 
                else 
                    "Income by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (categoryStats.isEmpty()) {
                Text(
                    text = "No expense data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Simple bar representation instead of pie chart
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryStats.forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(stat.color, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stat.categoryName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${stat.percentage.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Progress bar
                        LinearProgressIndicator(
                            progress = stat.percentage / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = stat.color,
                            trackColor = stat.color.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    categoryStats: List<CategoryStats>,
    currency: String,
    transactionType: com.roadi.budgesfram.data.models.TransactionType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = if (transactionType == com.roadi.budgesfram.data.models.TransactionType.EXPENSE)
                    "Expense Breakdown"
                else
                    "Income Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (categoryStats.isEmpty()) {
                Text(
                    text = "No categories to display",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                categoryStats.forEach { stat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(stat.color, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stat.categoryName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Text(
                            text = CurrencyFormatter.format(stat.amount, currency),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = stat.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportButton(
    isExporting: Boolean,
    onExport: () -> Unit
) {
    Button(
        onClick = onExport,
        enabled = !isExporting,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
    ) {
        if (isExporting) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generating...",
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Text(
                text = "Export PDF Report",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

