package com.prunance.app.ui.screens.strategy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.data.local.entity.BillEntity
import com.prunance.app.data.local.entity.ExpenseEntity
import com.prunance.app.data.local.entity.SavingsGoalEntity
import com.prunance.app.ui.components.GlassButton
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.GlassProgressIndicator
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme
import java.util.Calendar

private val tabLabels = listOf("Protocol", "Obligations", "Aspirations")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyScreen(
    onNavigateToGoalDetail: (String) -> Unit = {},
    viewModel: StrategyViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = "NGN")
    val privacyMode by viewModel.privacyMode.collectAsStateWithLifecycle(initialValue = false)
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle(initialValue = 0.0)
    val budgetSplits by viewModel.budgetSplits.collectAsStateWithLifecycle(initialValue = emptyMap())
    val currentExpenses by viewModel.currentMonthExpenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val goals by viewModel.goals.collectAsStateWithLifecycle(initialValue = emptyList())
    val bills by viewModel.bills.collectAsStateWithLifecycle(initialValue = emptyList())

    var showAddGoalSheet by remember { mutableStateOf(false) }
    var showAddBillSheet by remember { mutableStateOf(false) }
    var selectedGoalForFunds by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────
            PrunanceText(
                text = "Strategy",
                style = PrunanceTheme.typography.headlineMedium,
                color = PrunanceTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sub-Tab Selector ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabLabels.forEachIndexed { index, label ->
                    val isSelected = activeTab == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.onTabChanged(index) }
                            .padding(vertical = 8.dp)
                    ) {
                        PrunanceText(
                            text = label,
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth(0.6f)
                                .background(if (isSelected) PrunanceTheme.colors.primary else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tab Content ───────────────────────────────────────────
            when (activeTab) {
                0 -> BudgetTab(
                    monthlyIncome = monthlyIncome,
                    budgetSplits = budgetSplits,
                    expenses = currentExpenses,
                    currencySymbol = currencySymbol,
                    privacyMode = privacyMode
                )
                1 -> BillsTab(
                    bills = bills,
                    currencySymbol = currencySymbol,
                    privacyMode = privacyMode,
                    onCreateBill = { showAddBillSheet = true },
                    onToggleBillPaid = viewModel::toggleBillPaid,
                    onDeleteBill = viewModel::deleteBill
                )
                2 -> GoalsTab(
                    goals = goals,
                    currencySymbol = currencySymbol,
                    privacyMode = privacyMode,
                    onCreateGoal = { showAddGoalSheet = true },
                    onDeleteGoal = viewModel::deleteGoal,
                    onNavigateToGoalDetail = onNavigateToGoalDetail
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

        if (showAddBillSheet) {
            com.prunance.app.ui.components.AddBillSheet(
                currencySymbol = currencySymbol,
                onDismiss = { showAddBillSheet = false },
                onSave = { viewModel.addBill(it) }
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
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    PrunanceText(
                        text = "Monthly Budget",
                        style = PrunanceTheme.typography.labelMedium,
                        color = PrunanceTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrunanceText(
                        text = if (privacyMode) "$currencySymbol •••• of $currencySymbol •••• left"
                        else "$currencySymbol ${"%,.0f".format((totalBudgeted - totalSpent).coerceAtLeast(0.0))} of $currencySymbol ${"%,.0f".format(totalBudgeted)} left",
                        style = PrunanceTheme.typography.headlineMedium,
                        color = PrunanceTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GlassProgressIndicator(
                        progress = overallProgress.coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (overallProgress > 0.85f) PrunanceTheme.colors.error 
                                else if (overallProgress > 0.60f) PrunanceTheme.colors.warning 
                                else PrunanceTheme.colors.primary
                    )
                }
            }
        }

        items(budgetSplits.entries.toList()) { (category, percent) ->
            val limit = monthlyIncome * (percent / 100.0)
            if (limit > 0) {
                val spent = expenses.filter { it.category == category }.sumOf { it.amount }
                val progress = (spent / limit).toFloat()
                val remaining = limit - spent
                val dailyAllowance = remaining / daysRemaining

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrunanceText(
                                text = category,
                                style = PrunanceTheme.typography.titleMedium,
                                color = PrunanceTheme.colors.textPrimary
                            )
                            PrunanceText(
                                text = if (privacyMode) "••••" else "${(progress * 100).toInt()}%",
                                style = PrunanceTheme.typography.labelMedium,
                                color = PrunanceTheme.colors.textSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        PrunanceText(
                            text = if (privacyMode) "$currencySymbol •••• / $currencySymbol ••••"
                            else "$currencySymbol ${"%,.0f".format(spent)} / $currencySymbol ${"%,.0f".format(limit)}",
                            style = PrunanceTheme.typography.bodyMedium,
                            color = PrunanceTheme.colors.textSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        GlassProgressIndicator(
                            progress = progress.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (progress > 0.85f) PrunanceTheme.colors.error 
                                    else if (progress > 0.60f) PrunanceTheme.colors.warning 
                                    else PrunanceTheme.colors.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        PrunanceText(
                            text = if (remaining < 0) "⚠️ Over budget by $currencySymbol ${"%,.0f".format(-remaining)}"
                                   else if (privacyMode) "~$currencySymbol ••••/day for $daysRemaining days"
                                   else "~$currencySymbol ${"%,.0f".format(dailyAllowance)}/day for $daysRemaining days",
                            style = PrunanceTheme.typography.labelMedium,
                            color = if (remaining < 0) PrunanceTheme.colors.error else PrunanceTheme.colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillsTab(
    bills: List<BillEntity>,
    currencySymbol: String,
    privacyMode: Boolean,
    onCreateBill: () -> Unit,
    onToggleBillPaid: (BillEntity) -> Unit,
    onDeleteBill: (BillEntity) -> Unit
) {
    if (bills.isEmpty()) {
        PlaceholderContent(
            title = "No Obligations",
            subtitle = "Manage recurring bills and subscription decay",
            buttonText = "Add Bill",
            onAction = onCreateBill
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrunanceText(
                        text = "Your Obligations",
                        style = PrunanceTheme.typography.titleLarge,
                        color = PrunanceTheme.colors.textPrimary
                    )
                    GlassButton(onClick = onCreateBill, modifier = Modifier.height(40.dp)) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Add),
                            contentDescription = "Add",
                            colorFilter = ColorFilter.tint(Color.Black),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        PrunanceText("New", color = Color.Black, style = PrunanceTheme.typography.labelMedium)
                    }
                }
            }

            items(bills, key = { it.id }) { bill ->
                val haptic = LocalHapticFeedback.current
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToStart) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDeleteBill(bill)
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
                                .clip(RoundedCornerShape(24.dp))
                                .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberVectorPainter(Icons.Default.Delete),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(PrunanceTheme.colors.error)
                            )
                        }
                    },
                    dismissContent = {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (bill.isPaid) PrunanceTheme.colors.primary else PrunanceTheme.colors.surfaceGlass)
                                        .clickable { 
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onToggleBillPaid(bill)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (bill.isPaid) {
                                        Image(
                                            painter = rememberVectorPainter(Icons.Default.Check),
                                            contentDescription = "Paid",
                                            modifier = Modifier.size(16.dp),
                                            colorFilter = ColorFilter.tint(Color.Black)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    PrunanceText(
                                        text = bill.name,
                                        style = PrunanceTheme.typography.titleMedium,
                                        color = if (bill.isPaid) PrunanceTheme.colors.textSecondary else PrunanceTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    PrunanceText(
                                        text = "Due: ${bill.dueDate} • ${bill.frequency}",
                                        style = PrunanceTheme.typography.labelMedium,
                                        color = PrunanceTheme.colors.textSecondary
                                    )
                                }
                                
                                PrunanceText(
                                    text = if (privacyMode) "$currencySymbol ••••" else "$currencySymbol ${"%,.0f".format(bill.amount)}",
                                    style = PrunanceTheme.typography.titleMedium,
                                    color = if (bill.isPaid) PrunanceTheme.colors.textSecondary else PrunanceTheme.colors.primary
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalsTab(
    goals: List<SavingsGoalEntity>,
    currencySymbol: String,
    privacyMode: Boolean,
    onCreateGoal: () -> Unit,
    onDeleteGoal: (SavingsGoalEntity) -> Unit,
    onNavigateToGoalDetail: (String) -> Unit
) {
    if (goals.isEmpty()) {
        PlaceholderContent(
            title = "No Active Aspirations",
            subtitle = "Set long-term capital accumulation targets",
            buttonText = "Create Goal",
            onAction = onCreateGoal
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrunanceText(
                        text = "Your Targets",
                        style = PrunanceTheme.typography.titleLarge,
                        color = PrunanceTheme.colors.textPrimary
                    )
                    GlassButton(onClick = onCreateGoal, modifier = Modifier.height(40.dp)) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Add),
                            contentDescription = "Add Goal",
                            colorFilter = ColorFilter.tint(Color.Black),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        PrunanceText("New", color = Color.Black, style = PrunanceTheme.typography.labelMedium)
                    }
                }
            }
            items(goals, key = { it.id }) { goal ->
                val haptic = LocalHapticFeedback.current
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
                                .clip(RoundedCornerShape(24.dp))
                                .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberVectorPainter(Icons.Default.Delete),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(PrunanceTheme.colors.error)
                            )
                        }
                    },
                    dismissContent = {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp).fillMaxWidth().clickable { onNavigateToGoalDetail(goal.id) }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PrunanceText(
                                        text = goal.name,
                                        style = PrunanceTheme.typography.titleMedium,
                                        color = PrunanceTheme.colors.textPrimary
                                    )
                                    PrunanceText(
                                        text = if (privacyMode) "••••" else "${(progress * 100).toInt()}%",
                                        style = PrunanceTheme.typography.labelMedium,
                                        color = PrunanceTheme.colors.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                PrunanceText(
                                    text = if (privacyMode) "$currencySymbol •••• / $currencySymbol ••••"
                                    else "$currencySymbol ${"%,.0f".format(goal.currentAmount)} / $currencySymbol ${"%,.0f".format(goal.targetAmount)}",
                                    style = PrunanceTheme.typography.bodyMedium,
                                    color = PrunanceTheme.colors.textSecondary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GlassProgressIndicator(
                                    progress = progress.coerceIn(0f, 1f),
                                    modifier = Modifier.fillMaxWidth().height(8.dp)
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
                                    
                                    PrunanceText(
                                        text = "Target Date: $displayDate",
                                        style = PrunanceTheme.typography.labelMedium,
                                        color = PrunanceTheme.colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderContent(title: String, subtitle: String, buttonText: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrunanceText(
            text = title,
            style = PrunanceTheme.typography.titleMedium,
            color = PrunanceTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrunanceText(
            text = subtitle,
            style = PrunanceTheme.typography.bodySmall,
            color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        GlassButton(onClick = onAction) {
            PrunanceText(buttonText, color = Color.Black, style = PrunanceTheme.typography.titleMedium)
        }
    }
}
