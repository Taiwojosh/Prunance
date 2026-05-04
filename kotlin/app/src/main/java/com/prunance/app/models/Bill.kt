package com.prunance.app.models

import java.util.UUID

enum class Frequency {
    Weekly, Monthly, Yearly
}

data class Bill(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Double,
    val dueDate: String, // ISO string
    val frequency: Frequency,
    val category: Category
)
