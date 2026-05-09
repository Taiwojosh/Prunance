package com.prunance.app.ui.screens.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    // Step tracking (0 = Welcome, 1 = Income, 2 = Budget)
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep

    // Form state
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _monthlyIncome = MutableStateFlow("")
    val monthlyIncome: StateFlow<String> = _monthlyIncome

    private val _payday = MutableStateFlow("25")
    val payday: StateFlow<String> = _payday

    private val _currency = MutableStateFlow("NGN")
    val currency: StateFlow<String> = _currency

    // Budget splits (percentage of income)
    private val _budgetSplits = MutableStateFlow(
        mapOf(
            "Food" to 30,
            "Transport" to 15,
            "Shopping" to 10,
            "Entertainment" to 5,
            "Health" to 5,
            "Bills" to 25,
            "Other" to 10
        )
    )
    val budgetSplits: StateFlow<Map<String, Int>> = _budgetSplits

    fun onNameChanged(value: String) { _name.value = value }
    fun onMonthlyIncomeChanged(value: String) {
        // Only allow digits and decimal point
        if (value.all { it.isDigit() || it == '.' }) {
            _monthlyIncome.value = value
        }
    }
    fun onPaydayChanged(value: String) {
        val day = value.toIntOrNull()
        if (day != null && day in 1..31) {
            _payday.value = value
        } else if (value.isEmpty()) {
            _payday.value = ""
        }
    }
    fun onCurrencyChanged(value: String) { _currency.value = value }

    fun onBudgetSplitChanged(category: String, percentage: Int) {
        _budgetSplits.value = _budgetSplits.value.toMutableMap().apply {
            this[category] = percentage.coerceIn(0, 100)
        }
    }

    fun nextStep() {
        if (_currentStep.value < 2) _currentStep.value++
    }

    fun previousStep() {
        if (_currentStep.value > 0) _currentStep.value--
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val income = _monthlyIncome.value.toDoubleOrNull() ?: 0.0
            val day = _payday.value.toIntOrNull() ?: 25

            repository.prefs.saveProfile(
                name = _name.value.trim(),
                monthlyIncome = income,
                payday = day,
                currency = _currency.value,
                lowBalanceThreshold = income * 0.1 // Default: 10% of income
            )

            onComplete()
        }
    }
}
