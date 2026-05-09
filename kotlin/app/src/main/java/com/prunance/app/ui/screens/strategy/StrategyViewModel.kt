package com.prunance.app.ui.screens.strategy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.local.entity.BillEntity
import com.prunance.app.data.local.entity.SavingsGoalEntity
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StrategyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    val currency: Flow<String> = repository.prefs.currency
    val privacyMode: Flow<Boolean> = repository.prefs.privacyMode

    // Active sub-tab: 0 = Budget, 1 = Bills, 2 = Goals
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab

    val bills: Flow<List<BillEntity>> = repository.getAllBills()
    val goals: Flow<List<SavingsGoalEntity>> = repository.getAllGoals()

    fun onTabChanged(index: Int) {
        _activeTab.value = index
    }

    fun addBill(bill: BillEntity) {
        viewModelScope.launch { repository.addBill(bill) }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch { repository.deleteBill(bill) }
    }

    fun addGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch { repository.addGoal(goal) }
    }

    fun deleteGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }
}
