package com.example.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Signal
import com.example.data.Verdict
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg
import java.util.Locale

@Composable
fun SignalCard(
    signal: Signal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$"
) {
    val verdictColor = when (signal.verdict) {
        Verdict.BUY -> SignalBuy
        Verdict.HOLD -> SignalHold
        Verdict.SELL -> SignalSell
    }
    val verdictBg = when (signal.verdict) {
        Verdict.BUY -> SignalBuyBg
        Verdict.HOLD -> SignalHoldBg
        Verdict.SELL -> SignalSellBg
    }
    val isPositive = signal.changePct >= 0
    val effectivePrefix = if (signal.market.contains("PSX", ignoreCase = true)) "Rs " else currencySymbol

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("signal_card_${signal.symbol.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Symbol, Market Badge & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = signal.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = signal.market,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = signal.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                // Price & 24h change
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$effectivePrefix${formatPrice(signal.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", signal.changePct)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) SignalBuy else SignalSell
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Section: Verdict pill, confidence bar & profile match tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Verdict badge
                Box(
                    modifier = Modifier
                        .background(verdictBg, RoundedCornerShape(99.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(verdictColor, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = signal.verdict.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = verdictColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Confidence progress bar
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Confidence",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${signal.confidence}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = verdictColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(signal.confidence / 100f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(verdictColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Profile match tag
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${signal.riskAlignmentScore}% match",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price < 10.0) {
        String.format(Locale.US, "%.4f", price)
    } else {
        String.format(Locale.US, "%,.2f", price)
    }
}
