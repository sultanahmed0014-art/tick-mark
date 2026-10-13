package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlueChip
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueChipDetailSheet(
    blueChip: BlueChip,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    currencySymbol: String = "$"
) {
    val scoreToneColor = when {
        blueChip.score >= 75 -> SignalBuy
        blueChip.score >= 60 -> SignalHold
        else -> SignalSell
    }
    val scoreToneBg = when {
        blueChip.score >= 75 -> SignalBuyBg
        blueChip.score >= 60 -> SignalHoldBg
        else -> SignalSellBg
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("bluechip_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = blueChip.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = blueChip.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "${blueChip.sector} • ${blueChip.region} • ${blueChip.priceFormatted}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_bluechip_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = scoreToneBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, scoreToneColor.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedGaugeRing(
                        targetScore = blueChip.score,
                        size = 72.dp,
                        strokeWidth = 8.dp,
                        overrideColor = scoreToneColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "10+ YEAR LONG-TERM OUTLOOK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreToneColor
                        )
                        Text(
                            text = blueChip.verdict,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = scoreToneColor
                        )
                        blueChip.note?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5 Fundamental Pillars
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5 Fundamental Pillars (0-100)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PillarBarRow("Business Quality", "High ROE & Operating Margins", blueChip.factors.quality)
                    PillarBarRow("Growth Runway", "5-Year Revenue & EPS Trajectory", blueChip.factors.growth)
                    PillarBarRow("Financial Safety", "Conservative Debt/Equity & Low Beta", blueChip.factors.safety)
                    PillarBarRow("Valuation Fairness", "P/E Multiple relative to Moat", blueChip.factors.valuation)
                    PillarBarRow("Shareholder Income", "Dividend Yield & Capital Returns", blueChip.factors.income)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance Sheet & Key Financials
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reported Financial Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val m = blueChip.metrics
                    MetricRow("P/E Ratio (TTM)", m.pe?.let { String.format(Locale.US, "%.1fx", it) } ?: "N/A")
                    MetricRow("Return on Equity (ROE)", m.roe?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A")
                    MetricRow("Net Profit Margin", m.margin?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A")
                    MetricRow("5-Year Revenue CAGR", m.rev5?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A")
                    MetricRow("5-Year EPS Growth", m.eps5?.let { String.format(Locale.US, "%.1f%%", it) } ?: "N/A")
                    MetricRow("Dividend Yield", m.dy?.let { String.format(Locale.US, "%.2f%%", it) } ?: "0.00%")
                    MetricRow("Debt / Equity Ratio", m.de?.let { String.format(Locale.US, "%.2f", it) } ?: "N/A")
                    MetricRow("Beta (Price swings)", m.beta?.let { String.format(Locale.US, "%.2f", it) } ?: "N/A")
                    MetricRow("Market Capitalization", m.capB?.let { "$currencySymbol${String.format(Locale.US, "%,.0fB", it)}" } ?: "N/A")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Integrated Wealth Simulator
            CompoundGrowthSimulator(currencySymbol = currencySymbol)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PillarBarRow(title: String, subtitle: String, score: Int) {
    val barColor = when {
        score >= 75 -> SignalBuy
        score >= 55 -> SignalHold
        else -> SignalSell
    }
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$score / 100",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
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
                    .fillMaxWidth(score / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
