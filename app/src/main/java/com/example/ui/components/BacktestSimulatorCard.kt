package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BacktestStrategy
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalSell
import java.util.Locale

@Composable
fun BacktestSimulatorCard(
    strategies: List<BacktestStrategy>,
    modifier: Modifier = Modifier
) {
    if (strategies.isEmpty()) return

    var selectedStrategyId by remember { mutableStateOf(strategies.first().id) }
    val currentStrategy = strategies.find { it.id == selectedStrategyId } ?: strategies.first()

    val animProgress = remember(selectedStrategyId) { Animatable(0f) }
    LaunchedEffect(selectedStrategyId) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(750, easing = FastOutSlowInEasing)
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("backtest_simulator_card"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Algorithmic Strategy Backtesting",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Historical execution performance over 180 sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(SignalBuy.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${currentStrategy.netGainPct}% NET",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SignalBuy
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strategy Picker Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                strategies.forEach { strat ->
                    val isSelected = strat.id == selectedStrategyId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) AccentBlue
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedStrategyId = strat.id }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = strat.name.take(22),
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Strategy Description
            Text(
                text = currentStrategy.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Equity Curve Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070A0F))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                val points = currentStrategy.equityCurve
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    if (points.size < 2) return@Canvas

                    val minVal = points.minOrNull() ?: 90f
                    val maxVal = points.maxOrNull() ?: 150f
                    val range = (maxVal - minVal).coerceAtLeast(1f)

                    val linePath = Path()
                    val fillPath = Path()
                    val stepX = w / (points.size - 1)

                    points.forEachIndexed { idx, v ->
                        val x = idx * stepX
                        val normalizedY = (v - minVal) / range
                        val y = h - (normalizedY * h * animProgress.value)
                        if (idx == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, h)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }
                    fillPath.lineTo(w, h)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SignalBuy.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    drawPath(
                        path = linePath,
                        color = SignalBuy,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "Simulated Equity Curve (Capital Growth)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Performance Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "Win Rate",
                    value = "${currentStrategy.winRatePct}%",
                    color = SignalBuy,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Profit Factor",
                    value = String.format(Locale.US, "%.2f", currentStrategy.profitFactor),
                    color = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Max Drawdown",
                    value = "-${currentStrategy.maxDrawdownPct}%",
                    color = SignalHold,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Total Trades",
                    value = "${currentStrategy.totalTrades}",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
