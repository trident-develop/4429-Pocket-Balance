package co.imba.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.imba.model.ExpenseCategory
import co.imba.ui.theme.AppFont
import co.imba.ui.theme.Green700
import co.imba.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun QuickAddScreen(
    totalSpent: Double,
    monthlyLimit: Double,
    onLimitChange: (Double) -> Unit,
    onAdd: (Double, ExpenseCategory) -> Unit,
    onClearAll: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var limitText by remember(monthlyLimit) { mutableStateOf(if (monthlyLimit % 1.0 == 0.0) monthlyLimit.toLong().toString() else monthlyLimit.toString()) }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    val remaining = monthlyLimit - totalSpent
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Budget summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Green700)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Spent", color = Color.White.copy(0.7f), fontSize = 12.sp, fontFamily = AppFont)
                    Text(
                        "$${fmt.format(totalSpent)}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Left", color = Color.White.copy(0.7f), fontSize = 12.sp, fontFamily = AppFont)
                    Text(
                        "$${fmt.format(remaining)}",
                        color = if (remaining >= 0) Color.White else Color(0xFFFFCDD2),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Limit input
        OutlinedTextField(
            value = limitText,
            onValueChange = { v ->
                val digits = v.filter { it.isDigit() }
                if (digits.length <= 7 && (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$")))) {
                    limitText = v
                    v.toDoubleOrNull()?.let { onLimitChange(it) }
                }
            },
            label = { Text("Monthly budget", fontFamily = AppFont) },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontFamily = AppFont
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Amount input
        OutlinedTextField(
            value = amountText,
            onValueChange = { v ->
                val digits = v.filter { it.isDigit() }
                if (digits.length <= 7 && (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d{0,2}$")))) {
                    amountText = v
                }
            },
            label = { Text("Amount", fontFamily = AppFont) },
            placeholder = { Text("0", fontFamily = AppFont, fontSize = 28.sp) },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Category",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ExpenseCategory.entries) { cat ->
                val isSelected = selectedCategory == cat
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isSelected) Modifier.border(2.dp, cat.color, RoundedCornerShape(14.dp))
                            else Modifier
                        )
                        .background(
                            if (isSelected) cat.color.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            selectedCategory = cat
                            focusManager.clearFocus()
                        }
                        .padding(vertical = 14.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = cat.icon,
                        contentDescription = cat.label,
                        tint = cat.color,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        cat.label,
                        fontSize = 11.sp,
                        fontFamily = AppFont,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) cat.color else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                focusManager.clearFocus()
                val amount = amountText.toDoubleOrNull()
                if (amount != null && amount > 0 && selectedCategory != null) {
                    onAdd(amount, selectedCategory!!)
                    amountText = ""
                    selectedCategory = null
                }
            },
            enabled = amountText.toDoubleOrNull()?.let { it > 0 } == true && selectedCategory != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green700)
        ) {
            Text(
                "Save",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = AppFont
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                focusManager.clearFocus()
                onClearAll()
                amountText = ""
                selectedCategory = null
                limitText = "20000"
                onLimitChange(20000.0)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = co.imba.ui.theme.Red400)
        ) {
            Text(
                "Clear All",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = AppFont
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)

@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 360,
    heightDp = 640
)

@Preview(
    name = "mdpi (160)",
    widthDp = 320,
    heightDp = 680,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Preview(
    name = "hdpi (240)",
    widthDp = 450,
    heightDp = 800,
    fontScale = 1.0f,
    showBackground = true,
    showSystemUi = true
)

@Composable
private fun ScreenPreview() {
    QuickAddScreen(
        totalSpent = 8500.0,
        monthlyLimit = 20000.0,
        onLimitChange = {},
        onAdd = { _, _ -> },
        onClearAll = {}
    )
}