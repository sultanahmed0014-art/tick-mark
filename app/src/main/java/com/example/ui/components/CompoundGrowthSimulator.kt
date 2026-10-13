package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import java.util.Locale
import kotlin.math.pow

@Composable
fun CompoundGrowthSimulator(
    modifier: Modifier = Modifier,
    initialMonthly: Int = 250,
    currencySymbol: String = "$"
) {
    var monthlyAmount by remember { mutableIntStateOf(initialMonthly) }
    var horizonYears by remember { mutableIntStateOf(10) }

    val months = horizonYears * 12
    val principal = monthlyAmount * months

    fun calculateFutureValue(annualRate: Double): Double {
        val monthlyRate = annualRate / 12.0
        return monthlyAmount * (((1.0 + monthlyRate).pow(months.toDouble()) - 1.0) / monthlyRate)
    }

    val cautiousFv = calculateFutureValue(0.05)
    val averageFv = calculateFutureValue(0.07)
    val strongFv = calculateFutureValue(0.09)
    val maxFv = strongFv.coerceAtLeast(1000.0)

    val contributionOptions = listOf(100, 250, 500, 1000, 2500)
    val yearOptions = listOf(5, 10, 15)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "10+ Year Wealth Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Monthly Dollar-Cost Averaging Projection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Years selector
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(2.dp)
                ) {
                    yearOptions.forEach { yrs ->
                        val isSelected = horizonYears == yrs
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AccentBlue else Color.Transparent)
                                .clickable { horizonYears = yrs }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${yrs}y",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monthly amount selector chips
            Text(
                text = "MONTHLY CONTRIBUTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                contributionOptions.forEach { amount ->
                    val isSelected = monthlyAmount == amount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) AccentBlue.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentBlue else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { monthlyAmount = amount }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$currencySymbol$amount",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparative Growth Bars
            GrowthBarRow(
                label = "Your Capital Invested",
                sub = "Principal contributions ($months months)",
                amount = principal.toDouble(),
                maxAmount = maxFv,
                color = MaterialTheme.colorScheme.outlineVariant,
                currencySymbol = currencySymbol
            )
            Spacer(modifier = Modifier.height(10.dp))
            GrowthBarRow(
                label = "Cautious (5% / yr)",
                sub = "Bonds & Defensive Value",
                amount = cautiousFv,
                maxAmount = maxFv,
                color = SignalHold,
                currencySymbol = currencySymbol
            )
            Spacer(modifier = Modifier.height(10.dp))
            GrowthBarRow(
                label = "Historical Index (7% / yr)",
                sub = "Broad Index Compounding",
                amount = averageFv,
                maxAmount = maxFv,
                color = AccentBlue,
                currencySymbol = currencySymbol
            )
            Spacer(modifier = Modifier.height(10.dp))
            GrowthBarRow(
                label = "Quality Blue-Chips (9% / yr)",
                sub = "Top Decile Dividend & Moat Aristocrats",
                amount = strongFv,
                maxAmount = maxFv,
                color = SignalBuy,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))
            val netGain = strongFv - principal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        SignalBuy.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, SignalBuy.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Projected Compound Gain",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+$currencySymbol${formatCurrency(netGain)} in growth",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SignalBuy
                        )
                    }
                    Text(
                        text = "${String.format(Locale.US, "%.1f", (strongFv / principal) * 100 - 100)}% ROI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SignalBuy
                    )
                }
            }
        }
    }
}

@Composable
private fun GrowthBarRow(
    label: String,
    sub: String,
    amount: Double,
    maxAmount: Double,
    color: Color,
    currencySymbol: String
) {
    val progress = (amount / maxAmount).toFloat().coerceIn(0.04f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = "growth_bar_progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = sub,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$currencySymbol${formatCurrency(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}

private fun formatCurrency(value: Double): String {
    return String.format(Locale.US, "%,.0f", value)
}
