package co.imba.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.imba.data.ExpenseStorage
import co.imba.model.ExpenseCategory
import co.imba.model.ExpenseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ExpenseStorage(application)

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    private val _monthlyLimit = MutableStateFlow(20000.0)
    val monthlyLimit: StateFlow<Double> = _monthlyLimit.asStateFlow()

    private fun startOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val totalSpent: StateFlow<Double> = _expenses
        .map { list ->
            val start = startOfMonth()
            list.filter { it.timestamp >= start }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val spentByCategory: StateFlow<Map<ExpenseCategory, Double>> = _expenses
        .map { list ->
            val start = startOfMonth()
            list.filter { it.timestamp >= start }
                .groupBy { it.category }
                .mapValues { (_, v) -> v.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val remaining: StateFlow<Double> =
        combine(totalSpent, _monthlyLimit) { spent, limit -> limit - spent }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    init {
        _expenses.value = storage.loadExpenses()
        _monthlyLimit.value = storage.loadLimit()
    }

    fun addExpense(amount: Double, category: ExpenseCategory) {
        val item = ExpenseItem(amount = amount, category = category)
        _expenses.value = listOf(item) + _expenses.value
        storage.saveExpenses(_expenses.value)
    }

    fun deleteExpense(id: String) {
        _expenses.value = _expenses.value.filter { it.id != id }
        storage.saveExpenses(_expenses.value)
    }

    fun setLimit(limit: Double) {
        _monthlyLimit.value = limit
        storage.saveLimit(limit)
    }

    fun clearAll() {
        _expenses.value = emptyList()
        _monthlyLimit.value = 20000.0
        storage.saveExpenses(emptyList())
        storage.saveLimit(20000.0)
    }
}
