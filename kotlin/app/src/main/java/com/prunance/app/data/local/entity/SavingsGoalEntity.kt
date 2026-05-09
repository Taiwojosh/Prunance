package com.prunance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: String,    // ISO-8601 date string
    val monthlyContribution: Double? = null,
    val isLocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
