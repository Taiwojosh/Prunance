package com.prunance.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.local.entity.SavingsGoalEntity
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    private val repository: FinanceRepository,
    private val goalId: String
) : ViewModel() {

    val goal: StateFlow<SavingsGoalEntity?> = repository.getGoalById(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addFunds(amount: Double) {
        viewModelScope.launch {
            goal.value?.let { currentGoal ->
                val updatedGoal = currentGoal.copy(currentAmount = currentGoal.currentAmount + amount)
                repository.updateGoal(updatedGoal)
            }
        }
    }

    fun deleteGoal() {
        viewModelScope.launch {
            goal.value?.let { repository.deleteGoal(it) }
        }
    }
}
