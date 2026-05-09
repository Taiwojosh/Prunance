package com.prunance.app.ui.screens.analysis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prunance.app.data.repository.FinanceRepository
import com.prunance.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    val currency: Flow<String> = repository.prefs.currency
    val privacyMode: Flow<Boolean> = repository.prefs.privacyMode
    val monthlyIncome: Flow<Double> = repository.prefs.monthlyIncome
    
    private val allExpenses: Flow<List<ExpenseEntity>> = repository.getAllExpenses()

    // Time period selection: 0 = Week, 1 = Month, 2 = Quarter
    private val _selectedPeriod = MutableStateFlow(1)
    val selectedPeriod: StateFlow<Int> = _selectedPeriod

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val filteredExpenses: Flow<List<ExpenseEntity>> = kotlinx.coroutines.flow.combine(
        allExpenses,
        _selectedPeriod
    ) { expenses, period ->
        val calendar = java.util.Calendar.getInstance()
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayStr = format.format(calendar.time)
        
        when (period) {
            0 -> calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
            1 -> calendar.add(java.util.Calendar.MONTH, -1)
            2 -> calendar.add(java.util.Calendar.MONTH, -3)
        }
        val startDateStr = format.format(calendar.time)

        expenses.filter { it.date in startDateStr..todayStr }
    }

    fun onPeriodChanged(index: Int) {
        _selectedPeriod.value = index
    }
}
