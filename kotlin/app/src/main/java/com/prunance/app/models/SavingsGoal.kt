package com.prunance.app.models

import java.util.UUID

data class SavingsGoal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: String,
    val monthlyContribution: Double? = null,
    val isLocked: Boolean = false
)
