package com.prunance.app.ui.screens.ledger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.prunance.app.data.local.entity.ExpenseEntity
import com.prunance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    val privacyMode: Flow<Boolean> = repository.prefs.privacyMode
    val currency: Flow<String> = repository.prefs.currency

    // Search / filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    // Reactive expense list — re-queries when search or filter changes
    val expenses: Flow<List<ExpenseEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            _selectedCategory.flatMapLatest { category ->
                if (category == null) repository.getAllExpenses()
                else repository.getExpensesByCategory(category)
            }
        } else {
            repository.searchExpenses(query)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.addExpense(expense) }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }
}
