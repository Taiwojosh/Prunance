package com.prunance.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FinanceRepository(application)
    private val prefs = repository.prefs

    val userName: StateFlow<String> = prefs.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val monthlyIncome: StateFlow<Double> = prefs.monthlyIncome.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val payday: StateFlow<Int> = prefs.payday.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)
    val currency: StateFlow<String> = prefs.currency.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "NGN")
    val lowBalanceThreshold: StateFlow<Double> = prefs.lowBalanceThreshold.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5000.0)
    val privacyMode: StateFlow<Boolean> = prefs.privacyMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val budgetSplits: StateFlow<Map<String, Int>> = prefs.budgetSplits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun updateProfile(
        name: String,
        income: Double,
        payday: Int,
        currency: String,
        threshold: Double,
        splits: Map<String, Int>
    ) {
        viewModelScope.launch {
            prefs.saveProfile(name, income, payday, currency, threshold, splits)
        }
    }

    fun togglePrivacy(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setPrivacyMode(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
