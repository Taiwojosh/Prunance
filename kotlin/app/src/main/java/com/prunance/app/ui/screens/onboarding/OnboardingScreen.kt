package com.prunance.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
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
                            if (index <= currentStep) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
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
                OutlinedButton(
                    onClick = viewModel::previousStep,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
            }
            Button(
                onClick = {
                    if (currentStep < 2) {
                        viewModel.nextStep()
                    } else {
                        viewModel.completeOnboarding(onComplete)
                    }
                },
                enabled = when (currentStep) {
                    0 -> name.isNotBlank()
                    1 -> monthlyIncome.isNotBlank() && payday.isNotBlank()
                    else -> true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (currentStep < 2) "Continue" else "Get Started")
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
        Text(
            text = "Welcome to\nPrunance",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your personal finance command center.\nLet's set things up in under a minute.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            placeholder = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Step 2: Income ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
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
        Text(
            text = "Your Income",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This helps us calculate your daily budget and track spending.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Currency selection
        Text(
            text = "Currency",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            currencies.forEach { (code, label) ->
                FilterChip(
                    selected = currency == code,
                    onClick = { onCurrencyChanged(code) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Monthly income
        Text(
            text = "Monthly Income",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = monthlyIncome,
            onValueChange = onIncomeChanged,
            placeholder = { Text("e.g. 300000") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Payday
        Text(
            text = "When do you get paid?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = payday,
            onValueChange = onPaydayChanged,
            placeholder = { Text("Day of month (1-31)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
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
        Text(
            text = "Set Your Budget",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Allocate your income across categories. You can always adjust later.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Total allocation indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total allocated",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${totalPercent}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (totalPercent > 100) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (totalPercent / 100f).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Category sliders
        budgetSplits.forEach { (category, percentage) ->
            val amount = monthlyIncome * percentage / 100.0
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "$currencySymbol${"%,.0f".format(amount)} (${percentage}%)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
