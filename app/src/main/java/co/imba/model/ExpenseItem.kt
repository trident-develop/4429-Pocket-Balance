package co.imba.model

import java.util.UUID

data class ExpenseItem(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: ExpenseCategory,
    val timestamp: Long = System.currentTimeMillis()
)
