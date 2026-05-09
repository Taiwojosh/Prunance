package com.prunance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prunance.app.models.Category

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val category: String,    // Stored as string, mapped to Category enum
    val date: String,        // ISO-8601 date string
    val notes: String,
    val receiptUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
