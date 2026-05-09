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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.text.font.FontWeight
import com.prunance.app.data.local.entity.ExpenseEntity
import java.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.rememberDismissState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Delete
import com.prunance.app.data.local.entity.SavingsGoalEntity

private val tabLabels = listOf("Protocol", "Obligations", "Aspirations")

@Composable
fun StrategyScreen(viewModel: StrategyViewModel = viewModel()) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle(initialValue = false)
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle(initialValue = 0.0)
    val budgetSplits by viewModel.budgetSplits.collectAsStateWithLifecycle(initialValue = emptyMap())
    val currentExpenses by viewModel.currentMonthExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val goals by viewModel.goals.collectAsStateWithLifecycle(initialValue = emptyList())

    var showAddGoalSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedGoalForFunds by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.prunance.app.data.local.entity.SavingsGoalEntity?>(null) }

    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }

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
            0 -> BudgetTab(
                monthlyIncome = monthlyIncome,
                budgetSplits = budgetSplits,
                expenses = currentExpenses,
                currencySymbol = currencySymbol,
                privacyMode = privacyMode
            )
            1 -> BillsTab()
            2 -> GoalsTab(
                goals = goals,
                currencySymbol = currencySymbol,
                privacyMode = privacyMode,
                onCreateGoal = { showAddGoalSheet = true },
                onAddFunds = { selectedGoalForFunds = it },
                onDeleteGoal = viewModel::deleteGoal
            )
        }
    }

    if (showAddGoalSheet) {
        com.prunance.app.ui.components.AddGoalSheet(
            currencySymbol = currencySymbol,
            onDismiss = { showAddGoalSheet = false },
            onSave = { viewModel.addGoal(it) }
        )
    }

    selectedGoalForFunds?.let { goal ->
        com.prunance.app.ui.components.AddFundsSheet(
            currencySymbol = currencySymbol,
            goalName = goal.name,
            onDismiss = { selectedGoalForFunds = null },
            onSave = { amount ->
                val newAmount = goal.currentAmount + amount
                viewModel.updateGoal(goal.copy(currentAmount = newAmount))
            }
        )
    }
}

@Composable
private fun BudgetTab(
    monthlyIncome: Double,
    budgetSplits: Map<String, Int>,
    expenses: List<ExpenseEntity>,
    currencySymbol: String,
    privacyMode: Boolean
) {
    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val daysRemaining = (daysInMonth - currentDay).coerceAtLeast(1)

    val totalBudgeted = monthlyIncome * (budgetSplits.values.sum() / 100.0)
    val totalSpent = expenses.sumOf { it.amount }
    val overallProgress = if (totalBudgeted > 0) (totalSpent / totalBudgeted).toFloat() else 0f

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Overall Budget Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Monthly Budget",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (privacyMode) "$currencySymbol •••• of $currencySymbol •••• left"
                        else "$currencySymbol ${"%,.0f".format((totalBudgeted - totalSpent).coerceAtLeast(0.0))} of $currencySymbol ${"%,.0f".format(totalBudgeted)} left",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = overallProgress.coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (overallProgress > 0.85f) MaterialTheme.colorScheme.error 
                                else if (overallProgress > 0.60f) MaterialTheme.colorScheme.tertiary 
                                else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        // Category Cards
        items(budgetSplits.entries.toList()) { (category, percent) ->
            val limit = monthlyIncome * (percent / 100.0)
            if (limit > 0) {
                val spent = expenses.filter { it.category == category }.sumOf { it.amount }
                val progress = (spent / limit).toFloat()
                val remaining = limit - spent
                val dailyAllowance = remaining / daysRemaining

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (privacyMode) "••••" else "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (privacyMode) "$currencySymbol •••• / $currencySymbol ••••"
                            else "$currencySymbol ${"%,.0f".format(spent)} / $currencySymbol ${"%,.0f".format(limit)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (progress > 0.85f) MaterialTheme.colorScheme.error 
                                    else if (progress > 0.60f) MaterialTheme.colorScheme.tertiary 
                                    else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = if (remaining < 0) "⚠️ Over budget by $currencySymbol ${"%,.0f".format(-remaining)}"
                                   else if (privacyMode) "~$currencySymbol ••••/day for $daysRemaining days"
                                   else "~$currencySymbol ${"%,.0f".format(dailyAllowance)}/day for $daysRemaining days",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillsTab() {
    PlaceholderContent(
        title = "Obligations",
        subtitle = "Manage recurring bills and subscription decay"
    )
}

@Composable
private fun GoalsTab(
    goals: List<SavingsGoalEntity>,
    currencySymbol: String,
    privacyMode: Boolean,
    onCreateGoal: () -> Unit,
    onAddFunds: (SavingsGoalEntity) -> Unit,
    onDeleteGoal: (SavingsGoalEntity) -> Unit
) {
    if (goals.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PlaceholderContent(
                title = "No Active Aspirations",
                subtitle = "Set long-term capital accumulation targets"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCreateGoal) {
                Text("Create Goal")
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Targets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(onClick = onCreateGoal) {
                        Icon(Icons.Default.Add, contentDescription = "Add Goal", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Goal")
                    }
                }
            }

            val haptic = LocalHapticFeedback.current
            items(goals, key = { it.id }) { goal ->
                val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToStart) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDeleteGoal(goal)
                            true
                        } else false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    },
                    dismissContent = {
                    Card(
                    modifier = Modifier.fillMaxWidth().clickable { onAddFunds(goal) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = goal.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (privacyMode) "••••" else "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (privacyMode) "$currencySymbol •••• / $currencySymbol ••••"
                            else "$currencySymbol ${"%,.0f".format(goal.currentAmount)} / $currencySymbol ${"%,.0f".format(goal.targetAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        if (goal.deadline.isNotBlank() && goal.deadline != "No deadline") {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val parsedDate = try { format.parse(goal.deadline) } catch(e: Exception) { null }
                            val displayDate = if (parsedDate != null) {
                                val today = java.util.Calendar.getInstance()
                                val itemCal = java.util.Calendar.getInstance().apply { time = parsedDate }
                                val diff = today.get(java.util.Calendar.DAY_OF_YEAR) - itemCal.get(java.util.Calendar.DAY_OF_YEAR)
                                if (today.get(java.util.Calendar.YEAR) == itemCal.get(java.util.Calendar.YEAR)) {
                                    when (diff) {
                                        0 -> "Today"
                                        1 -> "Yesterday"
                                        else -> goal.deadline
                                    }
                                } else goal.deadline
                            } else goal.deadline
                            
                            Text(
                                text = "Target Date: $displayDate",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
