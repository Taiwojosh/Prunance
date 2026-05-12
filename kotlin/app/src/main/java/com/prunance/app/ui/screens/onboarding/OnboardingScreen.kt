package com.prunance.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prunance.app.ui.components.GlassButton
import com.prunance.app.ui.components.GlassCard
import com.prunance.app.ui.components.GlassProgressIndicator
import com.prunance.app.ui.components.PrunanceText
import com.prunance.app.ui.theme.PrunanceTheme

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val name by viewModel.name.collectAsStateWithLifecycle()
    val monthlyIncome by viewModel.monthlyIncome.collectAsStateWithLifecycle()
    val payday by viewModel.payday.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val budgetSplits by viewModel.budgetSplits.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrunanceTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // ── Progress indicator ────────────────────────────────────
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (index <= currentStep) PrunanceTheme.colors.primary
                                else PrunanceTheme.colors.surfaceGlass
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Animated step content ─────────────────────────────────
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState > initialState) width else -width } togetherWith
                            slideOutHorizontally { width -> if (targetState > initialState) -width else width }
                },
                modifier = Modifier.weight(1f),
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    0 -> WelcomeStep(name = name, onNameChanged = viewModel::onNameChanged)
                    1 -> IncomeStep(
                        monthlyIncome = monthlyIncome,
                        payday = payday,
                        currency = currency,
                        onIncomeChanged = viewModel::onMonthlyIncomeChanged,
                        onPaydayChanged = viewModel::onPaydayChanged,
                        onCurrencyChanged = viewModel::onCurrencyChanged
                    )
                    2 -> BudgetStep(
                        budgetSplits = budgetSplits,
                        monthlyIncome = monthlyIncome.toDoubleOrNull() ?: 0.0,
                        currency = currency,
                        onSplitChanged = viewModel::onBudgetSplitChanged
                    )
                }
            }

            // ── Navigation buttons ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrunanceTheme.colors.surfaceGlass)
                            .clickable { viewModel.previousStep() },
                        contentAlignment = Alignment.Center
                    ) {
                        PrunanceText("Back", color = PrunanceTheme.colors.textPrimary)
                    }
                }
                
                val isNextEnabled = when (currentStep) {
                    0 -> name.isNotBlank()
                    1 -> monthlyIncome.isNotBlank() && payday.isNotBlank()
                    else -> true
                }
                
                GlassButton(
                    onClick = {
                        if (currentStep < 2) {
                            viewModel.nextStep()
                        } else {
                            viewModel.completeOnboarding(onComplete)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isNextEnabled
                ) {
                    PrunanceText(
                        if (currentStep < 2) "Continue" else "Get Started",
                        color = if (isNextEnabled) Color.Black else PrunanceTheme.colors.textSecondary,
                        style = PrunanceTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

// ── Step 1: Welcome ───────────────────────────────────────────────────

@Composable
private fun WelcomeStep(name: String, onNameChanged: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PrunanceText(
            text = "Welcome to\nPrunance",
            style = PrunanceTheme.typography.headlineLarge,
            color = PrunanceTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrunanceText(
            text = "Your personal finance command center.\nLet's set things up in under a minute.",
            style = PrunanceTheme.typography.bodyLarge,
            color = PrunanceTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrunanceText(
            text = "What should we call you?",
            style = PrunanceTheme.typography.titleMedium,
            color = PrunanceTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
            BasicTextField(
                value = name,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                singleLine = true,
                cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                decorationBox = { innerTextField ->
                    if (name.isEmpty()) {
                        PrunanceText("Your name", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            )
        }
    }
}

// ── Step 2: Income ────────────────────────────────────────────────────

@Composable
private fun IncomeStep(
    monthlyIncome: String,
    payday: String,
    currency: String,
    onIncomeChanged: (String) -> Unit,
    onPaydayChanged: (String) -> Unit,
    onCurrencyChanged: (String) -> Unit
) {
    val currencies = listOf("NGN" to "₦ Naira", "USD" to "$ Dollar", "EUR" to "€ Euro")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PrunanceText(
            text = "Your Income",
            style = PrunanceTheme.typography.headlineMedium,
            color = PrunanceTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrunanceText(
            text = "This helps us calculate your daily budget and track spending.",
            style = PrunanceTheme.typography.bodyLarge,
            color = PrunanceTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrunanceText("Currency", style = PrunanceTheme.typography.labelLarge, color = PrunanceTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            currencies.forEach { (code, label) ->
                val isSelected = currency == code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrunanceTheme.colors.primary.copy(alpha = 0.2f) else PrunanceTheme.colors.surfaceGlass)
                        .clickable { onCurrencyChanged(code) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    PrunanceText(
                        text = label,
                        style = PrunanceTheme.typography.labelMedium,
                        color = if (isSelected) PrunanceTheme.colors.primary else PrunanceTheme.colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrunanceText("Monthly Income", style = PrunanceTheme.typography.labelLarge, color = PrunanceTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
            BasicTextField(
                value = monthlyIncome,
                onValueChange = onIncomeChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                decorationBox = { innerTextField ->
                    if (monthlyIncome.isEmpty()) {
                        PrunanceText("e.g. 300000", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrunanceText("When do you get paid?", style = PrunanceTheme.typography.labelLarge, color = PrunanceTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth().height(56.dp), cornerRadius = 16.dp) {
            BasicTextField(
                value = payday,
                onValueChange = onPaydayChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.CenterStart),
                textStyle = PrunanceTheme.typography.bodyLarge.copy(color = PrunanceTheme.colors.textPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(PrunanceTheme.colors.primary),
                decorationBox = { innerTextField ->
                    if (payday.isEmpty()) {
                        PrunanceText("Day of month (1-31)", style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textSecondary.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            )
        }
    }
}

// ── Step 3: Budget ────────────────────────────────────────────────────

@Composable
private fun BudgetStep(
    budgetSplits: Map<String, Int>,
    monthlyIncome: Double,
    currency: String,
    onSplitChanged: (String, Int) -> Unit
) {
    val currencySymbol = when (currency) {
        "NGN" -> "₦"
        "USD" -> "$"
        "EUR" -> "€"
        else -> currency
    }
    val totalPercent = budgetSplits.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PrunanceText(
            text = "Set Your Budget",
            style = PrunanceTheme.typography.headlineMedium,
            color = PrunanceTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrunanceText(
            text = "Allocate your income across categories. You can always adjust later.",
            style = PrunanceTheme.typography.bodyLarge,
            color = PrunanceTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Total allocation indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PrunanceText("Total allocated", style = PrunanceTheme.typography.labelMedium, color = PrunanceTheme.colors.textSecondary)
            PrunanceText(
                text = "${totalPercent}%",
                style = PrunanceTheme.typography.titleMedium,
                color = if (totalPercent > 100) PrunanceTheme.colors.error else PrunanceTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        GlassProgressIndicator(
            progress = (totalPercent / 100f).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = if (totalPercent > 100) PrunanceTheme.colors.error else PrunanceTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Category sliders
        budgetSplits.forEach { (category, percentage) ->
            val amount = monthlyIncome * percentage / 100.0
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PrunanceText(category, style = PrunanceTheme.typography.bodyLarge, color = PrunanceTheme.colors.textPrimary)
                    PrunanceText("$currencySymbol${"%,.0f".format(amount)} (${percentage}%)", style = PrunanceTheme.typography.bodyMedium, color = PrunanceTheme.colors.textSecondary)
                }
                Slider(
                    value = percentage.toFloat(),
                    onValueChange = { onSplitChanged(category, it.toInt()) },
                    valueRange = 0f..50f,
                    steps = 49,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
