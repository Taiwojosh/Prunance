package com.prunance.app.ui.screens.pulse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class PulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    // User preferences
    val userName: Flow<String> = repository.prefs.userName
    val monthlyIncome: Flow<Double> = repository.prefs.monthlyIncome
    val currency: Flow<String> = repository.prefs.currency
    val privacyMode: Flow<Boolean> = repository.prefs.privacyMode

    // KPI data
    val unpaidBillsTotal: Flow<Double> = repository.getTotalUnpaidAmount().map { it ?: 0.0 }
    val activeGoalCount: Flow<Int> = repository.getActiveGoalCount()
    val totalSaved: Flow<Double> = repository.getTotalSaved().map { it ?: 0.0 }

    // Dashboard specific data
    val recentTransactions: Flow<List<com.prunance.app.data.local.entity.ExpenseEntity>> = repository.getRecentExpenses(5)

    // Calculate current month's spending
    val currentMonthSpent: Flow<Double> = repository.getAllExpenses().map { expenses ->
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val startOfMonth = format.format(calendar.time)
        
        expenses.filter { it.date >= startOfMonth }.sumOf { it.amount }
    }

    fun togglePrivacyMode(currentMode: Boolean) {
        viewModelScope.launch {
            repository.prefs.setPrivacyMode(!currentMode)
        }
    }
}
