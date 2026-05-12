package com.prunance.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun GlassProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = PrunanceTheme.colors.primary,
    trackColor: Color = PrunanceTheme.colors.outlineGlass
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}
