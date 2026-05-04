package com.prunance.app.models

data class Budget(
    val category: Category,
    val limit: Double
)

data class QuickAddTemplate(
    val id: String,
    val name: String,
    val amount: Double,
    val category: Category
)

data class UserProfile(
    val name: String,
    val monthlyIncome: Double,
    val payday: Int, // Day of month
    val budgets: List<Budget>,
    val quickAdds: List<QuickAddTemplate>,
    val currency: String,
    val lowBalanceThreshold: Double,
    val hasSeenTour: Boolean,
    val privacyMode: Boolean = false,
    val privacyLock: String? = null,
    val notifiedAlerts: List<String> = emptyList()
)
