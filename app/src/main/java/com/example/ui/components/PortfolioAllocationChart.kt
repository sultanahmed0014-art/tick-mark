package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RiskProfile
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt

data class PortfolioSegment(
    val id: String,
    val label: String,
    val percentage: Float, // 0 to 100
    val color: Color,
    val topHoldings: String,
    val description: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortfolioAllocationChart(
    riskProfile: RiskProfile,
    totalPortfolioValue: Double = 35000.0,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val segments = remember(riskProfile) {
        when (riskProfile) {
            RiskProfile.CONSERVATIVE -> listOf(
                PortfolioSegment("bluechip", "Blue-Chip & PSX Aristocrats", 45f, AccentBlue, "FFC, MEBL, ENGRO, JNJ, KO", "High dividend cash flow & sovereign grade safety"),
                PortfolioSegment("comm", "Precious Metals & Gold", 25f, SignalHold, "XAU Gold, Trust Units", "Macro hedging & purchasing power preservation"),
                PortfolioSegment("fixed", "Treasuries & Cash", 20f, SignalBuy, "Short-term T-bills, USD cash", "Guaranteed yield & zero drawdown capital base"),
                PortfolioSegment("stocks", "Broad Index Equities", 10f, AccentCyan, "S&P 500, KSE-100", "Diversified long term market beta")
            )
            RiskProfile.BALANCED -> listOf(
                PortfolioSegment("tech", "Tech Giants & Moats", 40f, AccentBlue, "NVDA, AAPL, MSFT, SYS", "High ROE compounders with wide secular moats"),
                PortfolioSegment("bluechip", "Dividend Aristocrats", 25f, SignalBuy, "JPM, V, FFC, MEBL", "Steady dividend growth & low volatility"),
                PortfolioSegment("crypto", "Digital Assets", 15f, AccentPurple, "BTC, ETH", "Asymmetric monetary store of value"),
                PortfolioSegment("comm", "Commodities & Real Assets", 10f, SignalHold, "Gold XAU, Energy WTI", "Inflation protection & supply chain exposure"),
                PortfolioSegment("forex", "Cash Reserves", 10f, AccentCyan, "USD, EUR reserves", "Dry powder for market dip accumulation")
            )
            RiskProfile.AGGRESSIVE -> listOf(
                PortfolioSegment("crypto", "Digital Assets & Web3", 35f, AccentPurple, "BTC, ETH, SOL", "High-velocity layer-1 & institutional crypto"),
                PortfolioSegment("tech", "High-Beta Tech & AI", 35f, AccentBlue, "NVDA, TSLA, AMZN", "Breakout semiconductor & transformative computing"),
                PortfolioSegment("comm", "Tactical Commodities", 15f, SignalHold, "Crude Oil WTI, Silver", "High volatility momentum trades"),
                PortfolioSegment("stocks", "Growth Equities", 15f, SignalBuy, "Cloud & Semiconductor ETFs", "Secular expansion plays")
            )
            RiskProfile.SWING_TRADER -> listOf(
                PortfolioSegment("stocks", "ATR Breakout Stocks", 40f, AccentBlue, "NVDA, TSLA, SYS", "High volume stocks with active ATR trade plans"),
                PortfolioSegment("forex", "Major Forex Pairs", 25f, AccentCyan, "EUR/USD, GBP/JPY", "Liquid 24/5 FX momentum with tight invalidation"),
                PortfolioSegment("crypto", "Momentum Crypto", 20f, AccentPurple, "BTC, ETH, SOL", "High beta volatility for swing trading"),
                PortfolioSegment("comm", "Energy & Metals", 15f, SignalHold, "XAU, WTI", "Commodity breakout channels")
            )
        }
    }

    var selectedSegmentId by remember(riskProfile) {
        mutableStateOf(segments.first().id)
    }
    val selectedSegment = segments.find { it.id == selectedSegmentId } ?: segments.first()

    val animProgress = remember(riskProfile) { Animatable(0f) }
    LaunchedEffect(riskProfile) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("portfolio_allocation_chart"),
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
                        text = "Portfolio Asset Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Calibrated to your ${riskProfile.title} persona",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            AccentBlue.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = riskProfile.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Donut Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(190.dp)
                        .pointerInput(segments) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val dx = tapOffset.x - center.x
                                val dy = tapOffset.y - center.y
                                val dist = sqrt(dx * dx + dy * dy)
                                val innerRadius = 45.dp.toPx()
                                val outerRadius = 85.dp.toPx()
                                if (dist in innerRadius..outerRadius) {
                                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    angle = (angle + 90f + 360f) % 360f
                                    var currentAngle = 0f
                                    for (seg in segments) {
                                        val segSweep = (seg.percentage / 100f) * 360f
                                        if (angle >= currentAngle && angle < currentAngle + segSweep) {
                                            selectedSegmentId = seg.id
                                            break
                                        }
                                        currentAngle += segSweep
                                    }
                                }
                            }
                        }
                ) {
                    val strokeWidth = 26.dp.toPx()
                    val arcSize = size.width - strokeWidth
                    val topLeft = strokeWidth / 2f
                    var startAngle = -90f
                    val progress = animProgress.value

                    segments.forEach { seg ->
                        val sweep = (seg.percentage / 100f) * 360f * progress
                        val isSelected = seg.id == selectedSegmentId
                        val effectiveStroke = if (isSelected) strokeWidth + 6.dp.toPx() else strokeWidth
                        drawArc(
                            color = if (isSelected) seg.color else seg.color.copy(alpha = 0.85f),
                            startAngle = startAngle,
                            sweepAngle = sweep.coerceAtLeast(0.1f),
                            useCenter = false,
                            topLeft = Offset(topLeft, topLeft),
                            size = Size(arcSize, arcSize),
                            style = Stroke(
                                width = effectiveStroke,
                                cap = StrokeCap.Butt
                            )
                        )
                        startAngle += sweep
                    }
                }

                // Center readout
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${selectedSegment.percentage.toInt()}%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = selectedSegment.color
                    )
                    Text(
                        text = selectedSegment.label.take(16),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Segment Detail Card
            val segmentValue = (totalPortfolioValue * (selectedSegment.percentage / 100f))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = selectedSegment.color.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, selectedSegment.color.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(selectedSegment.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedSegment.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%,.0f", segmentValue)} (${selectedSegment.percentage.toInt()}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = selectedSegment.color
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedSegment.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Core Assets: ${selectedSegment.topHoldings}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chips
            Text(
                text = "TAP CATEGORY TO INSPECT",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                segments.forEach { seg ->
                    val isSelected = seg.id == selectedSegmentId
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) seg.color.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isSelected) seg.color else Color.Transparent,
                                RoundedCornerShape(99.dp)
                            )
                            .clickable { selectedSegmentId = seg.id }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(seg.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${seg.label} ${seg.percentage.toInt()}%",
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
