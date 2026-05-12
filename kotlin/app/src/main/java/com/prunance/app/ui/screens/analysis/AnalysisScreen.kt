package com.prunance.app.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────
            PrunanceText(
                text = "Analysis",
                style = PrunanceTheme.typography.headlineMedium,
                color = PrunanceTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Period Selector ───────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(periodLabels) { index, label ->
                    val isSelected = selectedPeriod == index
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { viewModel.onPeriodChanged(index) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        PrunanceText(
                            text = label,
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Insights Board ────────────────────────────────────────
            LazyColumn(
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Total Spent
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            PrunanceText(
                                text = "Total Spent (${periodLabels[selectedPeriod]})",
                                style = PrunanceTheme.typography.labelMedium,
                                color = PrunanceTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            PrunanceText(
                                text = if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(totalSpent)}",
                                style = PrunanceTheme.typography.headlineLarge,
                                color = PrunanceTheme.colors.textPrimary
                            )
                        }
                    }
                }

                // Habits Grid
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Top Category
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                PrunanceText(
                                    text = "Top Category",
                                    style = PrunanceTheme.typography.labelMedium,
                                    color = PrunanceTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PrunanceText(
                                    text = topCategory?.key ?: "N/A",
                                    style = PrunanceTheme.typography.titleMedium,
                                    color = PrunanceTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PrunanceText(
                                    text = if (topCategory == null) "No data" 
                                           else if (privacyMode) "$currencySymbol ••••"
                                           else "$currencySymbol ${"%,.0f".format(topCategory.value)}",
                                    style = PrunanceTheme.typography.bodySmall,
                                    color = PrunanceTheme.colors.primary
                                )
                            }
                        }

                        // Top Day
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                PrunanceText(
                                    text = "Highest Day",
                                    style = PrunanceTheme.typography.labelMedium,
                                    color = PrunanceTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PrunanceText(
                                    text = mostExpensiveDay?.key ?: "N/A",
                                    style = PrunanceTheme.typography.titleMedium,
                                    color = PrunanceTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PrunanceText(
                                    text = if (mostExpensiveDay == null) "No data" 
                                           else if (privacyMode) "$currencySymbol ••••"
                                           else "$currencySymbol ${"%,.0f".format(mostExpensiveDay.value)}",
                                    style = PrunanceTheme.typography.bodySmall,
                                    color = PrunanceTheme.colors.warning
                                )
                            }
                        }
                    }
                }

                // Spikes Warning
                if (spikes.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                PrunanceText(
                                    text = "⚠️ Unusual Spikes Detected",
                                    style = PrunanceTheme.typography.titleMedium,
                                    color = PrunanceTheme.colors.error
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                spikes.forEach { spike ->
                                    PrunanceText(
                                        text = "• ${spike.category} on ${spike.date}: " + 
                                               (if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(spike.amount)}"),
                                        style = PrunanceTheme.typography.bodyMedium,
                                        color = PrunanceTheme.colors.error
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
