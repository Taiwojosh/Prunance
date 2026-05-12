package com.prunance.app.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun PrunanceText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = PrunanceTheme.typography.bodyLarge,
    color: Color = PrunanceTheme.colors.textPrimary
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color)
    )
}
