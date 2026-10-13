package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketType
import com.example.data.NewsItem
import com.example.data.Signal
import com.example.data.UserProfile
import com.example.data.Verdict
import com.example.ui.components.LivePulseBadge
import com.example.ui.components.NewsFeedCard
import com.example.ui.components.SignalCard
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalsScreen(
    signals: List<Signal>,
    news: List<NewsItem>,
    userProfile: UserProfile,
    isLiveStreaming: Boolean,
    onSignalClick: (Signal) -> Unit,
    onRefresh: () -> Unit,
    onOpenVoiceAssistant: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedMarket by remember { mutableStateOf(MarketType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyHighFit by remember { mutableStateOf(false) }
    var showNewsFeedSection by remember { mutableStateOf(false) }

    val filteredList = signals.filter { sig ->
        val matchesMarket = when (selectedMarket) {
            MarketType.ALL -> true
            MarketType.STOCKS -> sig.market == "US Stocks"
            MarketType.PSX -> sig.market == "PSX Pakistan"
            MarketType.FOREX -> sig.market == "Forex"
            MarketType.CRYPTO -> sig.market == "Crypto"
            MarketType.COMMODITIES -> sig.market == "Commodities"
        }
        val matchesSearch = searchQuery.isBlank() ||
                sig.symbol.contains(searchQuery, ignoreCase = true) ||
                sig.name.contains(searchQuery, ignoreCase = true)
        val matchesFit = !filterOnlyHighFit || sig.riskAlignmentScore >= 85
        matchesMarket && matchesSearch && matchesFit
    }.sortedWith(
        compareByDescending<Signal> { it.verdict == Verdict.BUY }
            .thenByDescending { it.confidence }
    )

    val buyCount = signals.count { it.verdict == Verdict.BUY }
    val holdCount = signals.count { it.verdict == Verdict.HOLD }
    val sellCount = signals.count { it.verdict == Verdict.SELL }

    val currencySymbol = when (userProfile.currency) {
        "EUR" -> "€"
        "GBP" -> "£"
        "PKR" -> "Rs "
        else -> "$"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("signals_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Header & Live Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Market Terminal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LivePulseBadge(isStreaming = isLiveStreaming)
                    }
                    Text(
                        text = "Forex • Crypto • US & PSX Pakistan Stocks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenVoiceAssistant,
                        modifier = Modifier.testTag("voice_assistant_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Voice assistant summary",
                            tint = AccentCyan
                        )
                    }
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("refresh_signals_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh market data",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Summary Statistics Tiles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatTile(
                    count = buyCount,
                    label = "Buy Now",
                    color = SignalBuy,
                    bg = SignalBuyBg,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    count = holdCount,
                    label = "Wait / Dip",
                    color = SignalHold,
                    bg = SignalHoldBg,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    count = sellCount,
                    label = "Don't Buy",
                    color = SignalSell,
                    bg = SignalSellBg,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pro Trading Terminal Strip
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(BinanceGold, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRO TERMINAL • REAL-TIME FEED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BinanceGold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Toggle breaking news strip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (showNewsFeedSection) AccentBlue.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { showNewsFeedSection = !showNewsFeedSection }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = null,
                            tint = if (showNewsFeedSection) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showNewsFeedSection) "Hide News" else "Live News",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showNewsFeedSection) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Optional Live News Feed Dropdown
        if (showNewsFeedSection) {
            item {
                Text(
                    text = "BREAKING NEWS & SENTIMENT",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
            }
            items(news.take(3), key = { it.id }) { newsItem ->
                NewsFeedCard(newsItem = newsItem)
            }
        }

        // Personalized Profile Recommendation Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { filterOnlyHighFit = !filterOnlyHighFit }
                    .testTag("personalized_banner"),
                shape = RoundedCornerShape(16.dp),
                color = if (filterOnlyHighFit) AccentBlue.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (filterOnlyHighFit) AccentBlue else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(AccentBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AccentCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Personalized: ${userProfile.riskProfile.badge}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (filterOnlyHighFit) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AccentBlue, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (filterOnlyHighFit) "Showing high-conviction matches for your risk persona" else "Tap to filter setups matching your risk persona",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signal_search_field"),
                placeholder = { Text("Search asset (e.g. NVDA, ENGRO, BTC, XAU)...") },
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

        // Market Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MarketType.values().forEach { market ->
                    val isSelected = selectedMarket == market
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(99.dp)
                            )
                            .clickable { selectedMarket = market }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("market_chip_${market.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = market.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Signal Cards List
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching signals found for this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredList, key = { it.symbol }) { signal ->
                SignalCard(
                    signal = signal,
                    onClick = { onSignalClick(signal) },
                    currencySymbol = currencySymbol
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    count: Int,
    label: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
