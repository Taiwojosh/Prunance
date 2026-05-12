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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prunance.app.data.local.entity.BillEntity
import com.prunance.app.ui.theme.PrunanceTheme
import java.util.UUID

private val frequencyOptions = listOf("Weekly", "Monthly", "Quarterly", "Yearly")
private val categoryOptions = listOf(
    "🏠 Rent", "📱 Phone", "⚡ Electricity", "🌐 Internet",
    "🚗 Transport", "🎬 Entertainment", "🏥 Health", "📚 Education",
    "💧 Water", "🔒 Insurance", "💳 Subscriptions", "📦 Other"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBillSheet(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (BillEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("Monthly") }
    var selectedCategory by remember { mutableStateOf("") }

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
            PrunanceText(
                text = "Add Recurring Bill",
                style = PrunanceTheme.typography.titleLarge,
                color = PrunanceTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrunanceText("Bill Name", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                    textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                    singleLine = true,
                    cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (name.isEmpty()) {
                            PrunanceText("e.g. Netflix, Rent, DSTV", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrunanceText("Amount ($currencySymbol)", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
                BasicTextField(
                    value = amount,
                    onValueChange = { value -> if (value.all { it.isDigit() || it == '.' }) amount = value },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                    textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (amount.isEmpty()) {
                            PrunanceText("0.00", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrunanceText("Next Due Date", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
                BasicTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                    textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                    singleLine = true,
                    cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (dueDate.isEmpty()) {
                            PrunanceText("YYYY-MM-DD", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrunanceText("Frequency", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                frequencyOptions.forEach { freq ->
                    val isSelected = freq == selectedFrequency
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { selectedFrequency = freq }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PrunanceText(
                            text = freq,
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrunanceText("Category", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categoryOptions.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    val bgColor = if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PrunanceText(
                            text = cat,
                            style = PrunanceTheme.typography.labelSmall,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isEnabled = amount.toDoubleOrNull()?.let { it > 0 } == true && name.isNotBlank() && selectedCategory.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isEnabled) PrunanceTheme.colors.primary else PrunanceTheme.colors.surfaceGlass)
                    .clickable(enabled = isEnabled) {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (parsedAmount != null && parsedAmount > 0 && name.isNotBlank() && selectedCategory.isNotBlank()) {
                            onSave(
                                BillEntity(
                                    id = UUID.randomUUID().toString(),
                                    name = name.trim(),
                                    amount = parsedAmount,
                                    dueDate = dueDate.ifBlank {
                                        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                                    },
                                    frequency = selectedFrequency,
                                    category = selectedCategory,
                                    isPaid = false
                                )
                            )
                            onDismiss()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                PrunanceText("Add Bill", style = PrunanceTheme.typography.titleMedium, color = if (isEnabled) Color.Black else PrunanceTheme.colors.textSecondary)
            }
        }
    }
}
