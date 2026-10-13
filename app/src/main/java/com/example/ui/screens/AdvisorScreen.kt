package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BacktestStrategy
import com.example.data.BlueChip
import com.example.data.LearningLesson
import com.example.data.Signal
import com.example.data.UserProfile
import com.example.data.Verdict
import com.example.ui.components.AdvancedMarketChart
import com.example.ui.components.AnimatedFactorMeter
import com.example.ui.components.BacktestSimulatorCard
import com.example.ui.components.MentorLessonCard
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg
import java.util.Locale

@Composable
fun AdvisorScreen(
    signals: List<Signal>,
    blueChips: List<BlueChip>,
    backtestStrategies: List<BacktestStrategy>,
    learningLessons: List<LearningLesson>,
    userProfile: UserProfile,
    onOpenSignal: (Signal) -> Unit,
    onOpenBlueChip: (BlueChip) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }
    var selectedSymbol by remember { mutableStateOf("NVDA") }

    val activeSignal = signals.find { it.symbol.equals(selectedSymbol, ignoreCase = true) }
        ?: signals.firstOrNull()

    val matchingBlueChip = blueChips.find { it.symbol.equals(selectedSymbol, ignoreCase = true) }
    val quickPicks = listOf("NVDA", "BTC", "ENGRO", "AAPL", "MEBL", "TSLA", "SYS", "XAU", "FFC", "MSFT", "OGDC", "AMZN")

    val effectiveCurrency = if (activeSignal?.market?.contains("PSX", ignoreCase = true) == true) "Rs " else when (userProfile.currency) {
        "EUR" -> "€"
        "GBP" -> "£"
        "PKR" -> "Rs "
        else -> "$"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("advisor_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "AI Advisor & Mentor Studio",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Instant trade thesis, backtesting engine, and mentor interactive lessons.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchInput,
                onValueChange = {
                    searchInput = it
                    if (it.isNotBlank()) {
                        val match = signals.find { s -> s.symbol.contains(it, ignoreCase = true) }
                        if (match != null) {
                            selectedSymbol = match.symbol
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("advisor_search_input"),
                placeholder = { Text("Search ticker (e.g. AAPL, ENGRO, BTC, TSLA)...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        // Quick Pick Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPicks.forEach { ticker ->
                    val isSelected = selectedSymbol.equals(ticker, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(99.dp)
                            )
                            .clickable { selectedSymbol = ticker }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("advisor_quick_pick_$ticker"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ticker,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Selected Asset Advisor Card
        if (activeSignal != null) {
            val verdictColor = when (activeSignal.verdict) {
                Verdict.BUY -> SignalBuy
                Verdict.HOLD -> SignalHold
                Verdict.SELL -> SignalSell
            }
            val verdictBg = when (activeSignal.verdict) {
                Verdict.BUY -> SignalBuyBg
                Verdict.HOLD -> SignalHoldBg
                Verdict.SELL -> SignalSellBg
            }

            // Link to Blue-chip long term view if applicable
            if (matchingBlueChip != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenBlueChip(matchingBlueChip) }
                            .testTag("matching_bluechip_banner"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SignalBuy.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = SignalBuy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "10+ Year Outlook: ${matchingBlueChip.verdict}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Fundamental Blue-Chip Score: ${matchingBlueChip.score}/100 • Tap for deep dive",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Hero Decision Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenSignal(activeSignal) },
                    shape = RoundedCornerShape(20.dp),
                    color = verdictBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, verdictColor.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "SHOULD YOU BUY ${activeSignal.symbol} NOW?",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = verdictColor,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = activeSignal.verdict.title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = verdictColor
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$effectiveCurrency${formatPrice(activeSignal.price)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${if (activeSignal.changePct >= 0) "+" else ""}${String.format(Locale.US, "%.2f", activeSignal.changePct)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeSignal.changePct >= 0) SignalBuy else SignalSell
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeSignal.verdict.detailedAction,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Thesis: ${activeSignal.aiRationale}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Confidence bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(activeSignal.confidence / 100f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(verdictColor)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${activeSignal.confidence}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = verdictColor
                            )
                        }
                    }
                }
            }

            // Advanced Interactive Chart Preview
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Interactive Technical Chart View",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AdvancedMarketChart(
                            candles = activeSignal.candles,
                            indicators = activeSignal.indicators,
                            keySupport = activeSignal.keySupport,
                            keyResistance = activeSignal.keyResistance,
                            currencySymbol = effectiveCurrency
                        )
                    }
                }
            }

            // Consensus Factors
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Independent Indicator Consensus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FactorItem("Technical (RSI, MACD)", "Momentum oscillators", activeSignal.detail.tech)
                        FactorItem("News & Tone Sentiment", "Global headline evaluation", activeSignal.detail.news)
                        FactorItem("Analyst Ratings", "Wall Street price targets", activeSignal.detail.analyst)
                        FactorItem("Whale / Order Flow", "Institutional treasury shifts", activeSignal.detail.chain)
                    }
                }
            }
        }

        // Strategy Backtesting Studio Section
        item {
            BacktestSimulatorCard(strategies = backtestStrategies)
        }

        // Mentor Learning Modules Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MENTOR TRADING ACADEMY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
                Text(
                    text = "${learningLessons.size} MODULES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
            }
        }

        items(learningLessons.size) { idx ->
            MentorLessonCard(lesson = learningLessons[idx])
        }
    }
}

@Composable
private fun FactorItem(label: String, sub: String, value: Float?) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
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
            val word = when {
                value == null -> "No data"
                value > 0.22f -> "Bullish"
                value < -0.22f -> "Bearish"
                else -> "Neutral"
            }
            val col = when {
                value == null -> MaterialTheme.colorScheme.outline
                value > 0.22f -> SignalBuy
                value < -0.22f -> SignalSell
                else -> SignalHold
            }
            Text(
                text = word,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = col
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
