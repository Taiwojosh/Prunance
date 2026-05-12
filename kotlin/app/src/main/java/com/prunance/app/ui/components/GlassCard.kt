package com.prunance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val glassBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.1f),
            Color.White.copy(alpha = 0.03f)
        )
    )
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassBrush)
            .border(1.dp, borderBrush, shape),
        content = content
    )
}
