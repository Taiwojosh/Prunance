package com.prunance.app.ui.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prunance.app.ui.components.AddFundsSheet
import com.prunance.app.ui.components.GlassButton
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme
import java.text.SimpleDateFormat
import java.util.*

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
                // ── Top Bar ────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberVectorPainter(Icons.Default.ArrowBack),
                                contentDescription = "Back",
                                colorFilter = ColorFilter.tint(PrunanceTheme.colors.textPrimary)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        PrunanceText(
                            text = currentGoal.name,
                            style = PrunanceTheme.typography.headlineMedium,
                            color = PrunanceTheme.colors.textPrimary
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                            .clickable { showDeleteDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Delete),
                            contentDescription = "Delete",
                            colorFilter = ColorFilter.tint(PrunanceTheme.colors.error)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── Progress Ring / Large Display ───────────────────────────
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.fillMaxSize(),
                            color = PrunanceTheme.colors.outlineGlass,
                            strokeWidth = 16.dp
                        )
                        CircularProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier.fillMaxSize(),
                            color = PrunanceTheme.colors.primary,
                            strokeWidth = 16.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PrunanceText(
                                text = "${(progress * 100).toInt()}%",
                                style = PrunanceTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                                color = PrunanceTheme.colors.textPrimary
                            )
                            PrunanceText(
                                text = "Achieved",
                                style = PrunanceTheme.typography.labelLarge,
                                color = PrunanceTheme.colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

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

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Math / Strategy ─────────────────────────────────────────
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            PrunanceText(
                                text = "Strategy",
                                style = PrunanceTheme.typography.titleLarge,
                                color = PrunanceTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            DetailRow(label = "Remaining Amount", value = "₦${"%,.0f".format(remaining)}")
                            
                            currentGoal.deadline.let { deadline ->
                                if (deadline.isNotBlank() && deadline != "No deadline") {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val date = sdf.parse(deadline)
                                    val diff = date.time - System.currentTimeMillis()
                                    val daysLeft = (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
                                    
                                    DetailRow(label = "Days Left", value = "$daysLeft days")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PrunanceTheme.colors.outlineGlass))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    val monthlyNeeded = if (daysLeft > 30) remaining / (daysLeft / 30.0) else remaining
                                    PrunanceText(
                                        text = "To reach your goal, save approximately ₦${"%,.0f".format(monthlyNeeded)} per month.",
                                        style = PrunanceTheme.typography.bodyMedium,
                                        color = PrunanceTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // FAB equivalent
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 24.dp)
            ) {
                GlassButton(onClick = { showAddFunds = true }) {
                    Image(
                        painter = rememberVectorPainter(Icons.Default.Add),
                        contentDescription = "Add Funds",
                        colorFilter = ColorFilter.tint(Color.Black),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PrunanceText("Add Funds", color = Color.Black, style = PrunanceTheme.typography.titleMedium)
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        cornerRadius = 24.dp
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            PrunanceText(
                                text = "Delete Goal?",
                                style = PrunanceTheme.typography.titleLarge,
                                color = PrunanceTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PrunanceText(
                                text = "Are you sure you want to delete this goal? This action cannot be undone.",
                                style = PrunanceTheme.typography.bodyMedium,
                                color = PrunanceTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showDeleteDialog = false }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    PrunanceText("Cancel", color = PrunanceTheme.colors.textPrimary)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrunanceTheme.colors.error.copy(alpha = 0.2f))
                                        .clickable {
                                            viewModel.deleteGoal()
                                            onBack()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    PrunanceText("Delete", color = PrunanceTheme.colors.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize().background(PrunanceTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrunanceTheme.colors.primary)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Image(
                painter = rememberVectorPainter(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(PrunanceTheme.colors.primary),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrunanceText(label, style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            PrunanceText(value, style = PrunanceTheme.typography.titleMedium, color = PrunanceTheme.colors.textPrimary)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PrunanceText(label, style = PrunanceTheme.typography.bodyMedium, color = PrunanceTheme.colors.textSecondary)
        PrunanceText(value, style = PrunanceTheme.typography.titleMedium, color = PrunanceTheme.colors.textPrimary)
    }
}
