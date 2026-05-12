package com.prunance.app.ui.screens.ledger

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.models.Category
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(viewModel: LedgerViewModel = viewModel()) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle(initialValue = false)
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val haptic = LocalHapticFeedback.current

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
                .padding(top = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────
            PrunanceText(
                text = "Ledger",
                style = PrunanceTheme.typography.headlineMedium,
                color = PrunanceTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Search Bar ────────────────────────────────────────────
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberVectorPainter(Icons.Default.Search),
                        contentDescription = "Search",
                        colorFilter = ColorFilter.tint(PrunanceTheme.colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                        singleLine = true,
                        cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                PrunanceText(
                                    text = "Search by merchant or notes...",
                                    style = PrunanceTheme.typography.bodyLarge,
                                    color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Category Filter Chips ─────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedCategory == null
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { viewModel.onCategorySelected(null) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        PrunanceText(
                            text = "All",
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
                items(Category.entries.filter { it != Category.Income }.toList()) { category ->
                    val isSelected = selectedCategory == category.name
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable {
                                viewModel.onCategorySelected(
                                    if (selectedCategory == category.name) null else category.name
                                )
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        PrunanceText(
                            text = category.name,
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Expense List ──────────────────────────────────────────
            if (expenses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PrunanceText(
                        text = "No expenses yet",
                        style = PrunanceTheme.typography.titleMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrunanceText(
                        text = "Tap + to log your first expense",
                        style = PrunanceTheme.typography.bodySmall,
                        color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        val dismissState = rememberDismissState(
                            confirmValueChange = {
                                if (it == DismissValue.DismissedToStart) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.deleteExpense(expense)
                                    true
                                } else false
                            }
                        )

                        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val parsedDate = try { format.parse(expense.date) } catch(e: Exception) { null }
                        val displayDate = if (parsedDate != null) {
                            val today = java.util.Calendar.getInstance()
                            val itemCal = java.util.Calendar.getInstance().apply { time = parsedDate }
                            val diff = today.get(java.util.Calendar.DAY_OF_YEAR) - itemCal.get(java.util.Calendar.DAY_OF_YEAR)
                            if (today.get(java.util.Calendar.YEAR) == itemCal.get(java.util.Calendar.YEAR)) {
                                when (diff) {
                                    0 -> "Today"
                                    1 -> "Yesterday"
                                    else -> expense.date
                                }
                            } else expense.date
                        } else expense.date

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                                        .padding(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberVectorPainter(Icons.Default.Delete),
                                        contentDescription = "Delete",
                                        colorFilter = ColorFilter.tint(PrunanceTheme.colors.error)
                                    )
                                }
                            },
                            dismissContent = {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            PrunanceText(
                                                text = expense.notes.ifBlank { expense.category },
                                                style = PrunanceTheme.typography.bodyLarge,
                                                color = PrunanceTheme.colors.textPrimary
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            PrunanceText(
                                                text = "${expense.category} · $displayDate",
                                                style = PrunanceTheme.typography.bodySmall,
                                                color = PrunanceTheme.colors.textSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        PrunanceText(
                                            text = if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.2f".format(expense.amount)}",
                                            style = PrunanceTheme.typography.titleMedium,
                                            color = PrunanceTheme.colors.primary
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
