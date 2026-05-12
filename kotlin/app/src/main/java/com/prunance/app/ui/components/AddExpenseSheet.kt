package com.prunance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prunance.app.data.local.entity.ExpenseEntity
import com.prunance.app.models.Category
import com.prunance.app.ui.theme.PrunanceTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val categoryEmojis = mapOf(
    Category.Food to "🍔",
    Category.Transport to "🚗",
    Category.Shopping to "🛍️",
    Category.Entertainment to "🎮",
    Category.Health to "💊",
    Category.Bills to "📄",
    Category.Other to "📦"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheet(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.Food) }
    var notes by remember { mutableStateOf("") }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PrunanceTheme.colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.15f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(PrunanceTheme.colors.outlineGlass)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // ── Header ────────────────────────────────────────────
            PrunanceText(
                text = "Log Expense",
                style = PrunanceTheme.typography.titleLarge,
                color = PrunanceTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Amount field ──────────────────────────────────────
            PrunanceText(
                text = "Amount ($currencySymbol)",
                style = PrunanceTheme.typography.labelMedium,
                color = PrunanceTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                cornerRadius = 16.dp
            ) {
                BasicTextField(
                    value = amount,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() || it == '.' }) amount = value
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                    textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (amount.isEmpty()) {
                            PrunanceText(
                                text = "0.00",
                                style = PrunanceTheme.typography.bodyLarge,
                                color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Category selector ─────────────────────────────────
            PrunanceText(
                text = "Category",
                style = PrunanceTheme.typography.labelMedium,
                color = PrunanceTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.filter { it != Category.Income }.forEach { category ->
                    val emoji = categoryEmojis[category] ?: "📦"
                    val isSelected = selectedCategory == category
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    val borderColor = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.outlineGlass

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        PrunanceText(
                            text = "$emoji ${category.name}",
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Notes field ───────────────────────────────────────
            PrunanceText(
                text = "What was this for?",
                style = PrunanceTheme.typography.labelMedium,
                color = PrunanceTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                cornerRadius = 16.dp
            ) {
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                    textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                    singleLine = true,
                    cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (notes.isEmpty()) {
                            PrunanceText(
                                text = "e.g. Lunch at Chicken Republic",
                                style = PrunanceTheme.typography.bodyLarge,
                                color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Save button ───────────────────────────────────────
            val isEnabled = amount.toDoubleOrNull()?.let { it > 0 } ?: false
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isEnabled) PrunanceTheme.colors.primary else PrunanceTheme.colors.surfaceGlass)
                    .clickable(enabled = isEnabled) {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (parsedAmount != null && parsedAmount > 0) {
                            onSave(
                                ExpenseEntity(
                                    id = UUID.randomUUID().toString(),
                                    amount = parsedAmount,
                                    category = selectedCategory.name,
                                    date = today,
                                    notes = notes.trim()
                                )
                            )
                            onDismiss()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                PrunanceText(
                    text = "Save Expense",
                    style = PrunanceTheme.typography.titleMedium,
                    color = if (isEnabled) Color.Black else PrunanceTheme.colors.textSecondary
                )
            }
        }
    }
}
