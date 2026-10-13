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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.example.data.NewsImpact
import com.example.data.NewsItem
import com.example.data.NewsSentiment
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg

@Composable
fun NewsFeedCard(
    newsItem: NewsItem,
    onSymbolClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sentimentColor = when (newsItem.sentiment) {
        NewsSentiment.BULLISH -> SignalBuy
        NewsSentiment.BEARISH -> SignalSell
        NewsSentiment.NEUTRAL -> SignalHold
    }
    val sentimentBg = when (newsItem.sentiment) {
        NewsSentiment.BULLISH -> SignalBuyBg
        NewsSentiment.BEARISH -> SignalSellBg
        NewsSentiment.NEUTRAL -> SignalHoldBg
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("news_feed_card_${newsItem.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Source & Sentiment Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = newsItem.source,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${newsItem.timeAgo}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sentiment pill
                Box(
                    modifier = Modifier
                        .background(sentimentBg, RoundedCornerShape(99.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(sentimentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${newsItem.sentiment.name} (${if (newsItem.sentimentScore >= 0) "+" else ""}${newsItem.sentimentScore})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = sentimentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Headline
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Summary
            Text(
                text = newsItem.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Related symbols chips & impact badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    newsItem.relatedSymbols.forEach { sym ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = sym,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text(
                    text = "${newsItem.impact.name} IMPACT",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (newsItem.impact) {
                        NewsImpact.HIGH -> SignalSell
                        NewsImpact.MEDIUM -> SignalHold
                        NewsImpact.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
