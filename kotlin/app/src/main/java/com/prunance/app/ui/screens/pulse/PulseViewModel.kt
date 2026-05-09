package com.prunance.app.ui.screens.pulse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
}
