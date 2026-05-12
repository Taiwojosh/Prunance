package com.prunance.app.ui.screens.pulse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.GlassProgressIndicator
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun PulseScreen(
    onNavigateToSettings: () -> Unit = {},
    viewModel: PulseViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle(initialValue = "")
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle(initialValue = 0.0)
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle(initialValue = false)
    val unpaidBills by viewModel.unpaidBillsTotal.collectAsStateWithLifecycle(initialValue = 0.0)
    val activeGoals by viewModel.activeGoalCount.collectAsStateWithLifecycle(initialValue = 0)
    val currentMonthSpent by viewModel.currentMonthSpent.collectAsStateWithLifecycle(initialValue = 0.0)
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle(initialValue = emptyList())

    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // ── Header ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    PrunanceText(
                        text = if (userName.isNotBlank()) "Hello, $userName" else "Welcome",
                        style = PrunanceTheme.typography.headlineMedium,
                        color = PrunanceTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PrunanceText(
                        text = "Your financial pulse",
                        style = PrunanceTheme.typography.bodyMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrunanceTheme.colors.surfaceGlass)
                            .clickable { viewModel.togglePrivacyMode(privacyMode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberVectorPainter(if (privacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility),
                            contentDescription = "Toggle Privacy",
                            colorFilter = ColorFilter.tint(PrunanceTheme.colors.textPrimary)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrunanceTheme.colors.surfaceGlass)
                            .clickable { onNavigateToSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Settings),
                            contentDescription = "Settings",
                            colorFilter = ColorFilter.tint(PrunanceTheme.colors.textPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Balance Card ──────────────────────────────────────────
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    PrunanceText(
                        text = "Monthly Income",
                        style = PrunanceTheme.typography.labelMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrunanceText(
                        text = if (privacyMode) "$currencySymbol ••••••" else "$currencySymbol ${"%,.2f".format(monthlyIncome)}",
                        style = PrunanceTheme.typography.headlineLarge,
                        color = PrunanceTheme.colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── KPI Grid ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KpiCard(
                    label = "Upcoming Bills",
                    value = if (privacyMode) "••••" else "$currencySymbol ${"%,.0f".format(unpaidBills)}",
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    label = "Active Goals",
                    value = activeGoals.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── System Health ─────────────────────────────────────────
            val healthRatio = if (monthlyIncome > 0) currentMonthSpent / monthlyIncome else 0.0
            val isHealthy = healthRatio < 0.9

            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                    PrunanceText(
                        text = "System Health",
                        style = PrunanceTheme.typography.labelMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        PrunanceText(
                            text = if (isHealthy) "Optimal" else "Critical",
                            style = PrunanceTheme.typography.titleMedium,
                            color = if (isHealthy) PrunanceTheme.colors.primary else PrunanceTheme.colors.error
                        )
                        PrunanceText(
                            text = if (privacyMode) "••••" else "${(healthRatio * 100).toInt()}% utilized",
                            style = PrunanceTheme.typography.labelMedium,
                            color = PrunanceTheme.colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    GlassProgressIndicator(
                        progress = healthRatio.coerceIn(0.0, 1.0).toFloat(),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (isHealthy) PrunanceTheme.colors.primary else PrunanceTheme.colors.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Recent Transactions ───────────────────────────────────
            if (recentTransactions.isNotEmpty()) {
                PrunanceText(
                    text = "Recent Logs",
                    style = PrunanceTheme.typography.titleLarge,
                    color = PrunanceTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    recentTransactions.forEach { tx ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    PrunanceText(
                                        text = tx.category,
                                        style = PrunanceTheme.typography.titleMedium,
                                        color = PrunanceTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    PrunanceText(
                                        text = tx.date,
                                        style = PrunanceTheme.typography.bodySmall,
                                        color = PrunanceTheme.colors.textSecondary
                                    )
                                }
                                PrunanceText(
                                    text = if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(tx.amount)}",
                                    style = PrunanceTheme.typography.titleMedium,
                                    color = PrunanceTheme.colors.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            PrunanceText(
                text = label,
                style = PrunanceTheme.typography.labelMedium,
                color = PrunanceTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrunanceText(
                text = value,
                style = PrunanceTheme.typography.titleLarge,
                color = PrunanceTheme.colors.textPrimary
            )
        }
    }
}
