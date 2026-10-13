package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Signal
import com.example.data.UserProfile
import com.example.data.Verdict
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalDetailSheet(
    signal: Signal,
    userProfile: UserProfile,
    isWatched: Boolean,
    onToggleWatchlist: () -> Unit,
    onSaveNotes: (String, Double?) -> Unit,
    onAddAlert: (String, Double, String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit,
    sheetState: SheetState,
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
    val effectiveCurrency = if (signal.market.contains("PSX", ignoreCase = true)) "Rs " else currencySymbol
    var isAlertCreated by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("signal_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = signal.symbol,
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
                                text = signal.market,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = signal.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Price & 24h Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Current Real-Time Price",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$effectiveCurrency${formatPrice(signal.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val isPos = signal.changePct >= 0
                Box(
                    modifier = Modifier
                        .background(
                            if (isPos) SignalBuy.copy(alpha = 0.15f) else SignalSell.copy(alpha = 0.15f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${if (isPos) "+" else ""}${String.format(Locale.US, "%.2f", signal.changePct)}% 24h",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPos) SignalBuy else SignalSell
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Advanced Interactive Chart Engine (Candlestick & Line Modes)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Interactive Trading Terminal Chart",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Touch & drag to scrub. Toggle EMA 20, SMA 50, Bollinger Bands, RSI & MACD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AdvancedMarketChart(
                        candles = signal.candles,
                        indicators = signal.indicators,
                        keySupport = signal.keySupport,
                        keyResistance = signal.keyResistance,
                        currencySymbol = effectiveCurrency
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Decision Verdict Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = verdictBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, verdictColor.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "AI CONSENSUS DECISION FOR ${signal.symbol}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = verdictColor,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = signal.verdict.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = verdictColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = signal.verdict.detailedAction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(signal.confidence / 100f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(verdictColor)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${signal.confidence}% Confidence",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = verdictColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Trade Thesis & Rationale Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Mentor Trade Thesis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = signal.aiRationale,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 21.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sentiment Score: +${signal.sentimentScore}/100",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SignalBuy
                        )
                        Text(
                            text = "Reward/Risk Ratio: ${signal.levels?.riskRewardRatio ?: 2.4}:1",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Execution Plan (ATR Levels)
            signal.levels?.let { lv ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Calculated Price Execution Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Calibrated to 1-Day Average True Range (ATR: ${formatPrice(lv.atr)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        when (signal.verdict) {
                            Verdict.BUY -> {
                                PriceLevelRow("Target Entry (Dip)", lv.buy, SignalBuy, effectiveCurrency)
                                PriceLevelRow("Take Profit Target (3x ATR)", lv.target, SignalBuy, effectiveCurrency)
                                PriceLevelRow("Stop Loss Invalidation (1.5x ATR)", lv.stop, SignalSell, effectiveCurrency)
                            }
                            Verdict.SELL -> {
                                PriceLevelRow("Selling / Short Entry", lv.buy, SignalSell, effectiveCurrency)
                                PriceLevelRow("Downside Target (3x ATR)", lv.target, SignalSell, effectiveCurrency)
                                PriceLevelRow("Invalidation Exit (1.5x ATR)", lv.stop, SignalHold, effectiveCurrency)
                            }
                            Verdict.HOLD -> {
                                PriceLevelRow("Accumulate if dips to (-1 ATR)", lv.buy, SignalBuy, effectiveCurrency)
                                PriceLevelRow("Take profit if rises to (+1 ATR)", lv.target, SignalSell, effectiveCurrency)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Why This Signal: Consensus Engine
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why This Signal: Consensus Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SourceFactorRow("Technical Momentum", "RSI (14) & MACD Trend", signal.detail.tech)
                    SourceFactorRow("News & Social Sentiment", "Global headlines evaluation", signal.detail.news)
                    SourceFactorRow("Wall Street Ratings", "Investment bank consensus targets", signal.detail.analyst)
                    SourceFactorRow("Whale / Order Flow", "Institutional treasury movements", signal.detail.chain)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Watchlist & Instant Alert Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("watchlist_toggle_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWatched) MaterialTheme.colorScheme.surfaceVariant else AccentBlue
                    )
                ) {
                    Icon(
                        imageVector = if (isWatched) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isWatched) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isWatched) "In Watchlist" else "Watchlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isWatched) MaterialTheme.colorScheme.onSurface else Color.White
                    )
                }

                Button(
                    onClick = {
                        val target = signal.levels?.target ?: (signal.price * 1.05)
                        onAddAlert(signal.symbol, target, "Target alert set from signal plan")
                        isAlertCreated = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("create_alert_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlertCreated) SignalBuy.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = null,
                        tint = if (isAlertCreated) SignalBuy else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAlertCreated) "Alert Set!" else "Set Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isAlertCreated) SignalBuy else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Information only, not financial advice. Automated trading signals are probabilistic and capital is at risk.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun PriceLevelRow(label: String, price: Double, color: Color, currencySymbol: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$currencySymbol${formatPrice(price)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
private fun SourceFactorRow(title: String, subtitle: String, value: Float?) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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
            val scoreLabel = when {
                value == null -> "No data"
                value > 0.22f -> "Bullish"
                value < -0.22f -> "Bearish"
                else -> "Neutral"
            }
            val scoreColor = when {
                value == null -> MaterialTheme.colorScheme.outline
                value > 0.22f -> SignalBuy
                value < -0.22f -> SignalSell
                else -> SignalHold
            }
            Text(
                text = scoreLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
        }
        if (value != null) {
            AnimatedFactorMeter(value = value)
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
