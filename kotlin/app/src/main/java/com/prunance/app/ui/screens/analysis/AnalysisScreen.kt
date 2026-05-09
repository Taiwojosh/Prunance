package com.prunance.app.ui.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val periodLabels = listOf("Week", "Month", "Quarter")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = viewModel()) {
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val expenses by viewModel.filteredExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle(initialValue = false)
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle(initialValue = 0.0)

    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }

    // ── Calculations ──────────────────────────────────────────
    val totalSpent = expenses.sumOf { it.amount }

    val topCategory = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { exp -> exp.amount } }
        .maxByOrNull { it.value }

    // Parse dates to find Most Expensive Day of Week
    val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
    
    val mostExpensiveDay = expenses.groupBy { exp ->
        try {
            val date = format.parse(exp.date)
            if (date != null) dayFormat.format(date) else "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }.mapValues { it.value.sumOf { exp -> exp.amount } }
    .maxByOrNull { it.value }

    // Unusual Spikes: single expense > 20% of monthly income
    val spikeThreshold = monthlyIncome * 0.2
    val spikes = expenses.filter { it.amount > spikeThreshold && spikeThreshold > 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ── Header ────────────────────────────────────────────────
        Text(
            text = "Analysis",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Period Selector ───────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(periodLabels) { index, label ->
                FilterChip(
                    selected = selectedPeriod == index,
                    onClick = { viewModel.onPeriodChanged(index) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Insights Board ────────────────────────────────────────
        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Total Spent
            item {
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Total Spent (${periodLabels[selectedPeriod]})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(totalSpent)}",
                            style = MaterialTheme.typography.headlineLarge,
                            androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Habits Grid
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top Category
                    androidx.compose.material3.Card(
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Top Category",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = topCategory?.key ?: "N/A",
                                style = MaterialTheme.typography.titleMedium,
                                androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (topCategory == null) "No data" 
                                       else if (privacyMode) "$currencySymbol ••••"
                                       else "$currencySymbol ${"%,.0f".format(topCategory.value)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Top Day
                    androidx.compose.material3.Card(
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Highest Day",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mostExpensiveDay?.key ?: "N/A",
                                style = MaterialTheme.typography.titleMedium,
                                androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (mostExpensiveDay == null) "No data" 
                                       else if (privacyMode) "$currencySymbol ••••"
                                       else "$currencySymbol ${"%,.0f".format(mostExpensiveDay.value)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            // Spikes Warning
            if (spikes.isNotEmpty()) {
                item {
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "⚠️ Unusual Spikes Detected",
                                style = MaterialTheme.typography.titleMedium,
                                androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            spikes.forEach { spike ->
                                Text(
                                    text = "• ${spike.category} on ${spike.date}: " + 
                                           (if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(spike.amount)}"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
