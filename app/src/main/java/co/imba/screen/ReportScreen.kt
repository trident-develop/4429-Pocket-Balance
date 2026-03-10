package co.imba.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.imba.model.ExpenseCategory
import co.imba.ui.theme.AppFont
import co.imba.ui.theme.DividerColor
import co.imba.ui.theme.Green700
import co.imba.ui.theme.Red100
import co.imba.ui.theme.Red400
import co.imba.ui.theme.Teal400
import co.imba.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReportScreen(
    spentByCategory: Map<ExpenseCategory, Double>,
    totalSpent: Double,
    monthlyLimit: Double
) {
    val remaining = monthlyLimit - totalSpent
    val overBudget = remaining < 0
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Report",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Pie chart
        if (spentByCategory.isNotEmpty()) {
            val entries = spentByCategory.entries.toList()
            val total = entries.sumOf { it.value }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 36.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    var startAngle = -90f

                    entries.forEach { (cat, amount) ->
                        val sweep = (amount / total * 360).toFloat()
                        drawArc(
                            color = cat.color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$${fmt.format(totalSpent)}",
                        fontFamily = AppFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        "this month",
                        fontFamily = AppFont,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    entries.forEachIndexed { index, (cat, amount) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(cat.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                cat.label,
                                fontFamily = AppFont,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "$${fmt.format(amount)}",
                                fontFamily = AppFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        if (index < entries.size - 1) {
                            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No data for this month",
                    fontFamily = AppFont,
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                label = "Spent",
                value = "$${fmt.format(totalSpent)}",
                color = Red400,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Limit",
                value = "$${fmt.format(monthlyLimit)}",
                color = Green700,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Left",
                value = "$${fmt.format(kotlin.math.abs(remaining))}",
                color = if (overBudget) Red400 else Teal400,
                modifier = Modifier.weight(1f),
                warning = overBudget
            )
        }

        if (overBudget) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Red100)
            ) {
                Text(
                    "⚠ Budget exceeded by $${fmt.format(-remaining)}",
                    fontFamily = AppFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Red400,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    warning: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontFamily = AppFont,
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontFamily = AppFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
        }
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
private fun ReportScreenPreview() {
    ReportScreen(
        spentByCategory = linkedMapOf(
            ExpenseCategory.FOOD to 420.0,
            ExpenseCategory.COFFEE to 180.0,
            ExpenseCategory.TAXI to 250.0,
            ExpenseCategory.SHOPPING to 310.0
        ),
        totalSpent = 1164440.0,
        monthlyLimit = 2000.0
    )
}