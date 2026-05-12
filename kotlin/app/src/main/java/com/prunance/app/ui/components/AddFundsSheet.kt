package com.prunance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.prunance.app.ui.theme.PrunanceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsSheet(
    currencySymbol: String,
    goalName: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amount by remember { mutableStateOf("") }

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
                text = "Add to $goalName",
                style = PrunanceTheme.typography.titleLarge,
                color = PrunanceTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            val isEnabled = amount.toDoubleOrNull()?.let { it > 0 } == true
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isEnabled) PrunanceTheme.colors.primary else PrunanceTheme.colors.surfaceGlass)
                    .clickable(enabled = isEnabled) {
                        val parsedAmount = amount.toDoubleOrNull()
                        if (parsedAmount != null && parsedAmount > 0) {
                            onSave(parsedAmount)
                            onDismiss()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                PrunanceText("Add Funds", style = PrunanceTheme.typography.titleMedium, color = if (isEnabled) Color.Black else PrunanceTheme.colors.textSecondary)
            }
        }
    }
}
