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
    val allExpenses: Flow<List<ExpenseEntity>> = repository.getAllExpenses()

    // Time period selection: 0 = Week, 1 = Month, 2 = Quarter
    private val _selectedPeriod = MutableStateFlow(1)
    val selectedPeriod: StateFlow<Int> = _selectedPeriod

    fun onPeriodChanged(index: Int) {
        _selectedPeriod.value = index
    }
}
