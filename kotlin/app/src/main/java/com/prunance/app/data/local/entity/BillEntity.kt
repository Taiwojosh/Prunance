package com.prunance.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Double,
    val dueDate: String,     // ISO-8601 date string
    val frequency: String,   // Weekly, Monthly, Yearly
    val category: String,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
