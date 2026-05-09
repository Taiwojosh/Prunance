package com.prunance.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the 4 bottom navigation tabs from the blueprint:
 * Pulse (Dashboard), Ledger (Expenses), Strategy (Planning), Analysis (Reports)
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    data object Pulse : BottomNavItem(
        route = "pulse",
        label = "Pulse",
        outlinedIcon = Icons.Outlined.Speed,
        filledIcon = Icons.Filled.Speed
    )
    data object Ledger : BottomNavItem(
        route = "ledger",
        label = "Ledger",
        outlinedIcon = Icons.Outlined.Receipt,
        filledIcon = Icons.Filled.Receipt
    )
    data object Strategy : BottomNavItem(
        route = "strategy",
        label = "Strategy",
        outlinedIcon = Icons.Outlined.TrendingUp,
        filledIcon = Icons.Filled.TrendingUp
    )
    data object Analysis : BottomNavItem(
        route = "analysis",
        label = "Analysis",
        outlinedIcon = Icons.Outlined.Analytics,
        filledIcon = Icons.Filled.Analytics
    )

    companion object {
        val items = listOf(Pulse, Ledger, Strategy, Analysis)
    }
}
