package com.prunance.app.models

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: Category,
    val date: String, // ISO string
    val notes: String,
    val receiptUrl: String? = null
)
