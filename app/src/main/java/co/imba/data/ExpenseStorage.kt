package co.imba.data

import android.content.Context
import android.content.SharedPreferences
import co.imba.model.ExpenseCategory
import co.imba.model.ExpenseItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExpenseStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pocket_balance", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_EXPENSES = "expenses"
        private const val KEY_LIMIT = "monthly_limit"
        private const val DEFAULT_LIMIT = 20000.0
    }

    fun saveExpenses(expenses: List<ExpenseItem>) {
        val data = expenses.map { StoredExpense(it.id, it.amount, it.category.name, it.timestamp) }
        prefs.edit().putString(KEY_EXPENSES, gson.toJson(data)).apply()
    }

    fun loadExpenses(): List<ExpenseItem> {
        val json = prefs.getString(KEY_EXPENSES, null) ?: return emptyList()
        val type = object : TypeToken<List<StoredExpense>>() {}.type
        val stored: List<StoredExpense> = gson.fromJson(json, type)
        return stored.mapNotNull { s ->
            val cat = try { ExpenseCategory.valueOf(s.category) } catch (_: Exception) { null }
            cat?.let { ExpenseItem(s.id, s.amount, it, s.timestamp) }
        }
    }

    fun saveLimit(limit: Double) {
        prefs.edit().putLong(KEY_LIMIT, limit.toBits()).apply()
    }

    fun loadLimit(): Double {
        return if (prefs.contains(KEY_LIMIT)) {
            Double.fromBits(prefs.getLong(KEY_LIMIT, DEFAULT_LIMIT.toBits()))
        } else DEFAULT_LIMIT
    }

    private data class StoredExpense(
        val id: String,
        val amount: Double,
        val category: String,
        val timestamp: Long
    )
}
