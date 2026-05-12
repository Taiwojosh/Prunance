package com.prunance.app.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.ui.components.GlassButton
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle()
    val payday by viewModel.payday.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle()

    var showClearDataDialog by remember { mutableStateOf(false) }

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
            // ── Top Bar ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberVectorPainter(Icons.Default.ArrowBack),
                        contentDescription = "Back",
                        colorFilter = ColorFilter.tint(PrunanceTheme.colors.textPrimary)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                PrunanceText(
                    text = "Settings",
                    style = PrunanceTheme.typography.headlineMedium,
                    color = PrunanceTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // ── Profile Section ───────────────────────────────────────────
                SettingsGroup(title = "Profile") {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Display Name",
                        subtitle = userName.ifBlank { "Not set" }
                    )
                    SettingsItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Monthly Income",
                        subtitle = "$currency ${"%,.0f".format(monthlyIncome)}"
                    )
                    SettingsItem(
                        icon = Icons.Default.CalendarMonth,
                        title = "Payday",
                        subtitle = "Day $payday of the month"
                    )
                }

                // ── Preferences Section ───────────────────────────────────────
                SettingsGroup(title = "Preferences") {
                    SettingsItem(
                        icon = Icons.Default.CurrencyExchange,
                        title = "Currency",
                        subtitle = currency
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Image(
                                painter = rememberVectorPainter(Icons.Default.Lock),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(PrunanceTheme.colors.primary),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                PrunanceText("Privacy Mode", style = PrunanceTheme.typography.bodyLarge)
                                PrunanceText(
                                    "Blur sensitive numbers",
                                    style = PrunanceTheme.typography.bodySmall,
                                    color = PrunanceTheme.colors.textSecondary
                                )
                            }
                        }
                        
                        // Custom Switch
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (privacyMode) PrunanceTheme.colors.primary else PrunanceTheme.colors.surfaceGlass)
                                .clickable { viewModel.togglePrivacy(!privacyMode) }
                                .padding(4.dp),
                            contentAlignment = if (privacyMode) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                // ── Data Section ──────────────────────────────────────────────
                SettingsGroup(title = "Data & Privacy") {
                    SettingsItem(
                        icon = Icons.Default.FileDownload,
                        title = "Export Data",
                        subtitle = "Download as CSV (Coming soon)",
                        enabled = false
                    )
                    SettingsItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Clear All Data",
                        subtitle = "Wipe all expenses and goals",
                        titleColor = PrunanceTheme.colors.error,
                        onClick = { showClearDataDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // App Info
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrunanceText(
                        "Prunance Pro v1.0",
                        style = PrunanceTheme.typography.labelMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PrunanceText(
                        "Made with ❤️ for financial freedom",
                        style = PrunanceTheme.typography.labelSmall,
                        color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Custom Dialog Overlay
        if (showClearDataDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    cornerRadius = 24.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        PrunanceText(
                            text = "Wipe Everything?",
                            style = PrunanceTheme.typography.titleLarge,
                            color = PrunanceTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PrunanceText(
                            text = "This will permanently delete all your expenses, bills, and savings goals. This action cannot be undone.",
                            style = PrunanceTheme.typography.bodyMedium,
                            color = PrunanceTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showClearDataDialog = false }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                PrunanceText("Cancel", color = PrunanceTheme.colors.textPrimary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                                    .clickable {
                                        viewModel.clearAllData()
                                        showClearDataDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                PrunanceText("Confirm Wipe", color = PrunanceTheme.colors.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PrunanceText(
            text = title.uppercase(),
            style = PrunanceTheme.typography.labelLarge,
            color = PrunanceTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PrunanceTheme.colors.outlineGlass))
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    titleColor: Color = PrunanceTheme.colors.textPrimary,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .let { if (enabled && onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberVectorPainter(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (enabled) PrunanceTheme.colors.primary else PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                PrunanceText(
                    text = title,
                    style = PrunanceTheme.typography.bodyLarge,
                    color = if (enabled) titleColor else PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                PrunanceText(
                    text = subtitle,
                    style = PrunanceTheme.typography.bodySmall,
                    color = PrunanceTheme.colors.textSecondary
                )
            }
        }
    }
}
