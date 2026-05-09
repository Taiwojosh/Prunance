package com.prunance.app.ui.screens.strategy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val tabLabels = listOf("Protocol", "Obligations", "Aspirations")

@Composable
fun StrategyScreen(viewModel: StrategyViewModel = viewModel()) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ── Header ────────────────────────────────────────────────
        Text(
            text = "Strategy",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Sub-Tab Selector ──────────────────────────────────────
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = activeTab == index,
                    onClick = { viewModel.onTabChanged(index) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }

        // ── Tab Content ───────────────────────────────────────────
        when (activeTab) {
            0 -> BudgetTab()
            1 -> BillsTab()
            2 -> GoalsTab()
        }
    }
}

@Composable
private fun BudgetTab() {
    PlaceholderContent(
        title = "Budget Protocol",
        subtitle = "Set spending limits per category to track deployment efficiency"
    )
}

@Composable
private fun BillsTab() {
    PlaceholderContent(
        title = "Obligations",
        subtitle = "Manage recurring bills and subscription decay"
    )
}

@Composable
private fun GoalsTab() {
    PlaceholderContent(
        title = "Aspirations",
        subtitle = "Set long-term capital accumulation targets"
    )
}

@Composable
private fun PlaceholderContent(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
