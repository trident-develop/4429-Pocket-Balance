package co.imba.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.imba.ui.theme.AppFont
import co.imba.ui.theme.TextSecondary
import co.imba.viewmodel.BudgetViewModel

enum class BottomTab(val label: String, val icon: ImageVector) {
    ADD("Add", Icons.Rounded.AddCircle),
    JOURNAL("Journal", Icons.Rounded.ListAlt),
    REPORT("Report", Icons.Rounded.BarChart)
}

@Composable
fun MainScreen(viewModel: BudgetViewModel) {
    var selectedTab by remember { mutableStateOf(BottomTab.ADD) }
    val expenses by viewModel.expenses.collectAsState()
    val limit by viewModel.monthlyLimit.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    val spentByCategory by viewModel.spentByCategory.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp
            ) {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(
                                tab.label,
                                fontFamily = AppFont,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomTab.ADD -> QuickAddScreen(
                    totalSpent = totalSpent,
                    monthlyLimit = limit,
                    onLimitChange = { viewModel.setLimit(it) },
                    onAdd = { amount, cat ->
                        viewModel.addExpense(amount, cat)
                    },
                    onClearAll = { viewModel.clearAll() }
                )
                BottomTab.JOURNAL -> JournalScreen(
                    expenses = expenses,
                    onDelete = { viewModel.deleteExpense(it) }
                )
                BottomTab.REPORT -> ReportScreen(
                    spentByCategory = spentByCategory,
                    totalSpent = totalSpent,
                    monthlyLimit = limit
                )
            }
        }
    }
}
