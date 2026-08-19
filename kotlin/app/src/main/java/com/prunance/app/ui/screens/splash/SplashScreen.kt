package com.prunance.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prunance.app.ui.theme.PrunanceTheme
import com.prunance.app.ui.components.PrunanceText

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = PrunanceTheme.colors.primary,
                strokeWidth = 4.dp
            )
            PrunanceText(
                text = "Loading Prunance...",
                color = PrunanceTheme.colors.textSecondary,
                style = PrunanceTheme.typography.bodyLarge
            )
        }
    }
}
