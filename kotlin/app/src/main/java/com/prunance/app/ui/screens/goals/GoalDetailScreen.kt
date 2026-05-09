package com.prunance.app.ui.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prunance.app.ui.components.AddFundsSheet
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalDetailViewModel,
    onBack: () -> Unit
) {
    val goal by viewModel.goal.collectAsStateWithLifecycle()
    var showAddFunds by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    goal?.let { currentGoal ->
        val progress = if (currentGoal.targetAmount > 0) (currentGoal.currentAmount / currentGoal.targetAmount).toFloat() else 0f
        val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "goal_progress")
        val remaining = (currentGoal.targetAmount - currentGoal.currentAmount).coerceAtLeast(0.0)
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentGoal.name, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showAddFunds = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Funds") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Progress Ring / Large Display ───────────────────────────
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        strokeWidth = 12.dp
                    )
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 12.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Achieved",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── Stats Grid ──────────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        label = "Saved",
                        value = "₦${"%,.0f".format(currentGoal.currentAmount)}",
                        icon = Icons.Default.Savings,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Target",
                        value = "₦${"%,.0f".format(currentGoal.targetAmount)}",
                        icon = Icons.Default.Flag,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Math / Strategy ─────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Strategy", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DetailRow(label = "Remaining Amount", value = "₦${"%,.0f".format(remaining)}")
                        
                        currentGoal.deadline.let { deadline ->
                            if (deadline.isNotBlank() && deadline != "No deadline") {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val date = sdf.parse(deadline)
                                val diff = date.time - System.currentTimeMillis()
                                val daysLeft = (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
                                
                                DetailRow(label = "Days Left", value = "$daysLeft days")
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val monthlyNeeded = if (daysLeft > 30) remaining / (daysLeft / 30.0) else remaining
                                Text(
                                    text = "To reach your goal, save approximately ₦${"%,.0f".format(monthlyNeeded)} per month.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddFunds) {
            AddFundsSheet(
                currencySymbol = "₦",
                goalName = currentGoal.name,
                onDismiss = { showAddFunds = false },
                onSave = { amount ->
                    viewModel.addFunds(amount)
                    showAddFunds = false
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Goal?") },
                text = { Text("Are you sure you want to delete this goal? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteGoal()
                        onBack()
                    }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    } ?: run {
        // Loading or Not Found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
