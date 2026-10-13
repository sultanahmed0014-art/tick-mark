package com.example.data

enum class MarketType(val label: String, val shortBadge: String) {
    ALL("All", "ALL"),
    STOCKS("US Stocks", "US"),
    PSX("PSX Pakistan", "PK"),
    FOREX("Forex", "FX"),
    CRYPTO("Crypto", "COIN"),
    COMMODITIES("Commodities", "COMM")
}

enum class Verdict(val title: String, val shortDesc: String, val detailedAction: String) {
    BUY("Buy now", "Most checks point up", "High-probability setup based on algorithmic momentum, indicators & sentiment consensus"),
    HOLD("Wait", "Consolidating / Pullback", "Market is consolidating without asymmetric edge. Awaiting clear breakout or dip entry"),
    SELL("Don't buy", "Most checks point down", "Bearish structural pressure detected. If holding, consider locking profit or hedging")
}

data class Candle(
    val time: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

data class TechnicalIndicators(
    val ema20: Double,
    val sma50: Double,
    val rsi14: Double,
    val macdLine: Double,
    val macdSignal: Double,
    val macdHist: Double,
    val bbUpper: Double,
    val bbMiddle: Double,
    val bbLower: Double
)

data class SignalLevel(
    val atr: Double,
    val buy: Double,
    val stop: Double,
    val target: Double,
    val riskRewardRatio: Double = 2.4
)

data class SignalScores(
    val tech: Float? = null,     // -1.0 to 1.0 (RSI & MACD)
    val news: Float? = null,     // -1.0 to 1.0 (Headlines sentiment)
    val analyst: Float? = null,  // -1.0 to 1.0 (Wall St consensus)
    val chain: Float? = null     // -1.0 to 1.0 (Whale wallet & volume flow)
)

data class Signal(
    val symbol: String,
    val name: String,
    val market: String,
    val price: Double,
    val changePct: Double,
    val verdict: Verdict,
    val confidence: Int,         // 0 to 100
    val detail: SignalScores,
    val levels: SignalLevel?,
    val featured: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val historicalPoints: List<Float> = emptyList(),
    val candles: List<Candle> = emptyList(),
    val indicators: TechnicalIndicators? = null,
    val riskAlignmentScore: Int = 85,
    val aiRationale: String = "",
    val keyResistance: Double = 0.0,
    val keySupport: Double = 0.0,
    val sentimentScore: Int = 75 // -100 to +100
)

data class BlueChipFactors(
    val quality: Int,   // 0-100 ROE & Margins
    val growth: Int,    // 0-100 5Y Sales & EPS
    val safety: Int,    // 0-100 Debt/Equity & Beta
    val valuation: Int, // 0-100 P/E Multiple
    val income: Int     // 0-100 Dividend Yield
)

data class BlueChipMetrics(
    val pe: Double?,
    val roe: Double?,
    val margin: Double?,
    val rev5: Double?,
    val eps5: Double?,
    val dy: Double?,
    val de: Double?,
    val beta: Double?,
    val capB: Double?
)

data class BlueChip(
    val symbol: String,
    val name: String,
    val sector: String,
    val score: Int,       // 0-100 composite
    val verdict: String,  // e.g. "Strong long-term buy"
    val factors: BlueChipFactors,
    val metrics: BlueChipMetrics,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val region: String = "Global",
    val exchange: String = "NYSE / NASDAQ",
    val priceFormatted: String = "$0.00",
    val currencyCode: String = "USD"
)

enum class NewsSentiment {
    BULLISH,
    BEARISH,
    NEUTRAL
}

enum class NewsImpact {
    HIGH,
    MEDIUM,
    LOW
}

data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val market: String,
    val sentiment: NewsSentiment,
    val sentimentScore: Int, // -100 to +100
    val impact: NewsImpact,
    val summary: String,
    val relatedSymbols: List<String>
)

data class AlertItem(
    val id: String,
    val symbol: String,
    val condition: String, // "Price Above", "Price Below", "RSI < 30", "MACD Bullish Cross"
    val thresholdValue: Double,
    val isActive: Boolean = true,
    val triggeredAt: Long? = null,
    val note: String = ""
)

data class BacktestStrategy(
    val id: String,
    val name: String,
    val description: String,
    val winRatePct: Int,
    val profitFactor: Double,
    val netGainPct: Double,
    val maxDrawdownPct: Double,
    val totalTrades: Int,
    val equityCurve: List<Float>
)

data class LearningLesson(
    val id: String,
    val title: String,
    val category: String,
    val readTimeMinutes: Int,
    val summary: String,
    val keyTakeaways: List<String>,
    val quizQuestion: String,
    val quizOptions: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

data class WatchlistItem(
    val symbol: String,
    val name: String,
    val market: String = "Stocks",
    val addedAt: Long = System.currentTimeMillis(),
    val targetPriceAlert: Double? = null,
    val userNotes: String = ""
)

enum class RiskProfile(
    val title: String,
    val badge: String,
    val description: String,
    val idealAssets: String
) {
    CONSERVATIVE(
        "Capital Preservation",
        "🛡️ Conservative",
        "Prioritizes high safety, defensive dividend income, and blue-chip stability with low drawdowns.",
        "Blue Chips (KO, JNJ, PG), PSX Aristocrats (FFC, MEBL), Gold (XAU)"
    ),
    BALANCED(
        "Quality & Growth",
        "⚖️ Balanced",
        "Mixes robust tech compounders with reliable dividend payers. Moderate volatility tolerance.",
        "Mega-Cap Tech (AAPL, MSFT), Index ETFs, Financials (V, JPM, ENGRO)"
    ),
    AGGRESSIVE(
        "High Velocity",
        "🚀 Aggressive",
        "Maximized upside via high-beta momentum, crypto assets, and high-growth innovators.",
        "Crypto (BTC, ETH, SOL), High Beta Tech (NVDA, TSLA)"
    ),
    SWING_TRADER(
        "Tactical Momentum",
        "🎯 Swing Trader",
        "Short-to-medium horizon focused strictly on ATR levels, RSI reversals, and breakout signals.",
        "Forex (EUR/USD, GBP/JPY), High Volume Stocks, Commodities (WTI)"
    )
}

enum class ExperienceLevel(val label: String) {
    BEGINNER("Beginner (1-2 yrs)"),
    INTERMEDIATE("Intermediate (3-5 yrs)"),
    ADVANCED("Veteran Trader (5+ yrs)")
}

data class UserProfile(
    val id: String = "admin_talha",
    val name: String = "Muhammad Talha Mazhar",
    val email: String = "talha.mazhar@tickmark.pro",
    val avatarBadge: String = "👑 Super Admin",
    val riskProfile: RiskProfile = RiskProfile.BALANCED,
    val experienceLevel: ExperienceLevel = ExperienceLevel.ADVANCED,
    val targetMonthlyInvestment: Int = 1000,
    val currency: String = "USD",
    val notificationsEnabled: Boolean = true,
    val highConfidenceAlertsOnly: Boolean = true,
    val darkModePreferred: Boolean = true
)

data class SourceSetting(
    val key: String,
    val label: String,
    val description: String,
    val enabled: Boolean = true
)

enum class FeedbackStatus(val label: String) {
    PENDING("Under Review"),
    ACKNOWLEDGED("Acknowledged"),
    RESOLVED("Resolved")
}

data class UserFeedback(
    val id: String,
    val userName: String,
    val userEmail: String,
    val rating: Int,
    val category: String,
    val message: String,
    val timestamp: Long,
    val status: FeedbackStatus = FeedbackStatus.PENDING,
    val adminReply: String? = null
)

data class SystemStatusState(
    val isMaintenanceMode: Boolean = false,
    val consensusEngineStatus: String = "Optimal (Consensus v3.2)",
    val feedLatencyMs: Int = 38,
    val serverUptimePct: Double = 99.98,
    val activeConnectionsCount: Int = 1420,
    val psxGatewaySynced: Boolean = true,
    val lastIntegrityCheck: Long = System.currentTimeMillis(),
    val systemNotice: String = "All multi-market gateways operating nominally across Forex, Crypto, US & PSX."
)
