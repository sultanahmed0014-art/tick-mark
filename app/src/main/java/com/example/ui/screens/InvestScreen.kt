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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.BlueChip
import com.example.data.UserProfile
import com.example.ui.components.CompoundGrowthSimulator
import com.example.ui.components.PortfolioAllocationChart
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalSell
import java.util.Locale

@Composable
fun InvestScreen(
    blueChips: List<BlueChip>,
    userProfile: UserProfile,
    onBlueChipClick: (BlueChip) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedSector by remember { mutableStateOf("All") }

    val regions = listOf("All", "Pakistan (PSX)", "United States", "Europe & Asia")

    val filteredList = blueChips.filter {
        val matchesRegion = when (selectedRegion) {
            "Pakistan (PSX)" -> it.region.equals("Pakistan", ignoreCase = true)
            "United States" -> it.region.equals("US", ignoreCase = true)
            "Europe & Asia" -> it.region.equals("Europe", ignoreCase = true) || it.region.equals("Asia", ignoreCase = true)
            else -> true
        }
        val matchesSector = selectedSector == "All" || it.sector.contains(selectedSector, ignoreCase = true)
        matchesRegion && matchesSector
    }.sortedByDescending { it.score }

    val availableSectors = remember(blueChips, selectedRegion) {
        val base = blueChips.filter {
            when (selectedRegion) {
                "Pakistan (PSX)" -> it.region.equals("Pakistan", ignoreCase = true)
                "United States" -> it.region.equals("US", ignoreCase = true)
                "Europe & Asia" -> it.region.equals("Europe", ignoreCase = true) || it.region.equals("Asia", ignoreCase = true)
                else -> true
            }
        }.map { it.sector }.distinct()
        listOf("All") + base
    }

    val currencySymbol = when (userProfile.currency) {
        "EUR" -> "€"
        "GBP" -> "£"
        "PKR" -> "Rs "
        else -> "$"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("invest_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Worldwide & PSX Blue Chips",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Institutional fundamental ratings for 10+ year wealth building",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(BinanceGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, BinanceGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "BLUE-CHIP DESK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = BinanceGold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Region Selector Tabs (Pakistan PSX vs Worldwide)
        item {
            Column {
                Text(
                    text = "SELECT MARKET JURISDICTION",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    regions.forEach { regionLabel ->
                        val isSelected = selectedRegion == regionLabel
                        val flagEmoji = when (regionLabel) {
                            "Pakistan (PSX)" -> "🇵🇰 "
                            "United States" -> "🇺🇸 "
                            "Europe & Asia" -> "🌍 "
                            else -> "🌐 "
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(
                                    if (isSelected) BinanceGold
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) BinanceGold else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(99.dp)
                                )
                                .clickable {
                                    selectedRegion = regionLabel
                                    selectedSector = "All"
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("region_chip_${regionLabel.take(4).lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$flagEmoji$regionLabel",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0B0E14) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Sector Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableSectors.forEach { sec ->
                    val isSelected = selectedSector == sec
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.dp,
                                if (isSelected) BinanceGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedSector = sec }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sec,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BinanceGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Integrated Wealth Simulator
        item {
            CompoundGrowthSimulator(
                initialMonthly = userProfile.targetMonthlyInvestment,
                currencySymbol = currencySymbol
            )
        }

        // Portfolio Asset Allocation Chart
        item {
            PortfolioAllocationChart(
                riskProfile = userProfile.riskProfile,
                currencySymbol = currencySymbol
            )
        }

        // Section Count Label
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredList.size} BLUE-CHIP ASSETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "SORT: COMPOSITE SCORE",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = BinanceGold
                )
            }
        }

        // List of Blue Chips (Binance / Exness Pro Trading Card Style)
        items(filteredList, key = { it.symbol }) { item ->
            BinanceStyleBlueChipCard(
                blueChip = item,
                onClick = { onBlueChipClick(item) }
            )
        }
    }
}

@Composable
private fun BinanceStyleBlueChipCard(
    blueChip: BlueChip,
    onClick: () -> Unit
) {
    val scoreToneColor = when {
        blueChip.score >= 75 -> SignalBuy
        blueChip.score >= 60 -> SignalHold
        else -> SignalSell
    }
    val isPakistan = blueChip.region.equals("Pakistan", ignoreCase = true)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("bluechip_card_${blueChip.symbol.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Symbol, Exchange tag, Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isPakistan) Color(0xFF0F3B25) else Color(0xFF1B2642),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isPakistan) "🇵🇰 PSX" else blueChip.exchange.take(8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPakistan) Color(0xFF4ADE80) else AccentCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = blueChip.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = blueChip.priceFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Name & Sector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = blueChip.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = blueChip.sector,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Trading Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(scoreToneColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Score: ${blueChip.score}/100",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreToneColor
                    )
                }

                // ROE metric
                blueChip.metrics.roe?.let { roe ->
                    Text(
                        text = "ROE: ${String.format(Locale.US, "%.0f%%", roe)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Dividend Yield metric
                blueChip.metrics.dy?.let { dy ->
                    if (dy > 0) {
                        Text(
                            text = "Yield: ${String.format(Locale.US, "%.1f%%", dy)}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dy >= 8.0) SignalBuy else BinanceGold
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
