package com.prunance.app.data.repository

import android.content.Context
import com.prunance.app.data.local.PrunanceDatabase
import com.prunance.app.data.local.entity.BillEntity
import com.prunance.app.data.local.entity.ExpenseEntity
import com.prunance.app.data.local.entity.SavingsGoalEntity
import com.prunance.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Single-source-of-truth repository that abstracts Room + DataStore.
 * All ViewModels talk to this — never to DAOs or DataStore directly.
 */
class FinanceRepository(context: Context) {

    private val db = PrunanceDatabase.getDatabase(context)
    private val expenseDao = db.expenseDao()
    private val billDao = db.billDao()
    private val savingsGoalDao = db.savingsGoalDao()
    val prefs = UserPreferences(context)

    // ── Expenses ──────────────────────────────────────────────────────

    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    fun getRecentExpenses(limit: Int): Flow<List<ExpenseEntity>> = expenseDao.getRecentExpenses(limit)

    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesByCategory(category)

    fun searchExpenses(query: String): Flow<List<ExpenseEntity>> =
        expenseDao.searchExpenses(query)

    fun getExpensesBetweenDates(start: String, end: String): Flow<List<ExpenseEntity>> =
        expenseDao.getExpensesBetweenDates(start, end)

    fun getTotalSpentBetweenDates(start: String, end: String): Flow<Double?> =
        expenseDao.getTotalSpentBetweenDates(start, end)

    fun getTotalSpentByCategoryBetweenDates(category: String, start: String, end: String): Flow<Double?> =
        expenseDao.getTotalSpentByCategoryBetweenDates(category, start, end)

    suspend fun addExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: String) = expenseDao.deleteExpenseById(id)

    // ── Bills ─────────────────────────────────────────────────────────

    fun getAllBills(): Flow<List<BillEntity>> = billDao.getAllBills()
    fun getUnpaidBills(): Flow<List<BillEntity>> = billDao.getUnpaidBills()
    fun getTotalUnpaidAmount(): Flow<Double?> = billDao.getTotalUnpaidAmount()

    suspend fun addBill(bill: BillEntity) = billDao.insertBill(bill)
    suspend fun updateBill(bill: BillEntity) = billDao.updateBill(bill)
    suspend fun deleteBill(bill: BillEntity) = billDao.deleteBill(bill)
    suspend fun deleteBillById(id: String) = billDao.deleteBillById(id)

    // ── Savings Goals ─────────────────────────────────────────────────

    fun getAllGoals(): Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
    fun getActiveGoalCount(): Flow<Int> = savingsGoalDao.getActiveGoalCount()
    fun getTotalSaved(): Flow<Double?> = savingsGoalDao.getTotalSaved()

    suspend fun addGoal(goal: SavingsGoalEntity) = savingsGoalDao.insertGoal(goal)
    suspend fun updateGoal(goal: SavingsGoalEntity) = savingsGoalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: SavingsGoalEntity) = savingsGoalDao.deleteGoal(goal)
    suspend fun deleteGoalById(id: String) = savingsGoalDao.deleteGoalById(id)
}
