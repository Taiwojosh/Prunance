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

    // KPI data with strict null safety
    val unpaidBillsTotal: Flow<Double> = repository.getTotalUnpaidAmount().map { it ?: 0.0 }
    val activeGoalCount: Flow<Int> = repository.getActiveGoalCount().map { it ?: 0 }
    val totalSaved: Flow<Double> = repository.getTotalSaved().map { it ?: 0.0 }

    // Dashboard specific data
    val recentTransactions: Flow<List<com.prunance.app.data.local.entity.ExpenseEntity>> = repository.getRecentExpenses(5).map { it ?: emptyList() }

    // Calculate current month's spending safely
    val currentMonthSpent: Flow<Double> = repository.getAllExpenses().map { expenses ->
        try {
            val list = expenses ?: emptyList()
            if (list.isEmpty()) return@map 0.0
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val startOfMonth = format.format(calendar.time)
            
            list.filter { it.date >= startOfMonth }.sumOf { it.amount }
        } catch (e: Exception) {
            0.0
        }
    }

    fun togglePrivacyMode(currentMode: Boolean) {
        viewModelScope.launch {
            repository.prefs.setPrivacyMode(!currentMode)
        }
    }
}
