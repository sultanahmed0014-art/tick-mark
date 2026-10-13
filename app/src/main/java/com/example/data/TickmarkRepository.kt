package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class TickmarkRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _signals = MutableStateFlow<List<Signal>>(emptyList())
    val signals: StateFlow<List<Signal>> = _signals.asStateFlow()

    private val _blueChips = MutableStateFlow<List<BlueChip>>(emptyList())
    val blueChips: StateFlow<List<BlueChip>> = _blueChips.asStateFlow()

    private val _watchlist = MutableStateFlow<List<WatchlistItem>>(emptyList())
    val watchlist: StateFlow<List<WatchlistItem>> = _watchlist.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _sources = MutableStateFlow<List<SourceSetting>>(emptyList())
    val sources: StateFlow<List<SourceSetting>> = _sources.asStateFlow()

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow()

    private val _alerts = MutableStateFlow<List<AlertItem>>(emptyList())
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    private val _backtestStrategies = MutableStateFlow<List<BacktestStrategy>>(emptyList())
    val backtestStrategies: StateFlow<List<BacktestStrategy>> = _backtestStrategies.asStateFlow()

    private val _learningModules = MutableStateFlow<List<LearningLesson>>(emptyList())
    val learningModules: StateFlow<List<LearningLesson>> = _learningModules.asStateFlow()

    private val _userFeedbacks = MutableStateFlow<List<UserFeedback>>(emptyList())
    val userFeedbacks: StateFlow<List<UserFeedback>> = _userFeedbacks.asStateFlow()

    private val _systemStatus = MutableStateFlow(SystemStatusState())
    val systemStatus: StateFlow<SystemStatusState> = _systemStatus.asStateFlow()

    private val _isLiveStreaming = MutableStateFlow(true)
    val isLiveStreaming: StateFlow<Boolean> = _isLiveStreaming.asStateFlow()

    private val _lastRefreshTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastRefreshTimestamp: StateFlow<Long> = _lastRefreshTimestamp.asStateFlow()

    init {
        initializeInitialData()
        startLiveMarketSimulation()
    }

    private fun initializeInitialData() {
        _sources.value = listOf(
            SourceSetting("tech", "Technical Indicators", "RSI (14), EMA 20/50, and MACD momentum oscillators", true),
            SourceSetting("news", "News & Social Sentiment", "Natural language sentiment score across Bloomberg, Reuters & Finnhub", true),
            SourceSetting("analyst", "Wall Street Ratings", "Consensus aggregate of Tier-1 investment bank price targets", true),
            SourceSetting("chain", "Whale & Order Flow", "Large block orders, institutional treasury movements & on-chain flow", true)
        )

        _signals.value = listOf(
            // US Stocks
            createSignal(
                symbol = "NVDA",
                name = "NVIDIA Corporation",
                market = "US Stocks",
                price = 124.75,
                changePct = 3.42,
                verdict = Verdict.BUY,
                confidence = 89,
                tech = 0.78f, news = 0.82f, analyst = 0.88f, chain = null,
                atr = 4.20,
                riskAlignment = 92,
                aiRationale = "Massive demand for Blackwell GPU architecture coupled with consecutive weekly institutional accumulation. RSI holding above 58 indicating robust continuation momentum.",
                sentimentScore = 86
            ),
            createSignal(
                symbol = "AAPL",
                name = "Apple Inc.",
                market = "US Stocks",
                price = 228.40,
                changePct = 0.85,
                verdict = Verdict.BUY,
                confidence = 82,
                tech = 0.48f, news = 0.65f, analyst = 0.74f, chain = null,
                atr = 3.80,
                riskAlignment = 95,
                aiRationale = "Services revenue expansion and multi-year iPhone refresh cycle. Stabilized price consolidating firmly above 20-day EMA support.",
                sentimentScore = 78
            ),
            createSignal(
                symbol = "MSFT",
                name = "Microsoft Corp.",
                market = "US Stocks",
                price = 448.20,
                changePct = 0.62,
                verdict = Verdict.BUY,
                confidence = 87,
                tech = 0.52f, news = 0.70f, analyst = 0.90f, chain = null,
                atr = 5.60,
                riskAlignment = 96,
                aiRationale = "Azure enterprise cloud acceleration and Copilot adoption leading consensus revenue upgrades across Wall Street.",
                sentimentScore = 84
            ),
            createSignal(
                symbol = "TSLA",
                name = "Tesla, Inc.",
                market = "US Stocks",
                price = 217.80,
                changePct = -1.95,
                verdict = Verdict.HOLD,
                confidence = 58,
                tech = -0.15f, news = 0.20f, analyst = 0.05f, chain = null,
                atr = 8.50,
                riskAlignment = 70,
                aiRationale = "Testing lower boundary of symmetrical triangle. Recommend waiting for clean breakout above $225 or dip accumulation near $208 support.",
                sentimentScore = 42
            ),
            createSignal(
                symbol = "AMZN",
                name = "Amazon.com Inc.",
                market = "US Stocks",
                price = 186.50,
                changePct = 1.12,
                verdict = Verdict.BUY,
                confidence = 85,
                tech = 0.60f, news = 0.68f, analyst = 0.85f, chain = null,
                atr = 3.40,
                riskAlignment = 91,
                aiRationale = "AWS operating margins reaching multi-year peaks; retail efficiency gains continuing to fuel free cash flow inflection.",
                sentimentScore = 80
            ),
            createSignal(
                symbol = "GOOGL",
                name = "Alphabet Inc.",
                market = "US Stocks",
                price = 168.90,
                changePct = -0.45,
                verdict = Verdict.HOLD,
                confidence = 66,
                tech = 0.12f, news = 0.25f, analyst = 0.78f, chain = null,
                atr = 2.90,
                riskAlignment = 90,
                aiRationale = "Valuation multiple attractive at 20x P/E; regulatory antitrust noise creating short-term volatility overhang.",
                sentimentScore = 55
            ),

            // Pakistan Stock Exchange (PSX 100 Leaders)
            createSignal(
                symbol = "ENGRO",
                name = "Engro Corporation",
                market = "PSX Pakistan",
                price = 345.50,
                changePct = 2.15,
                verdict = Verdict.BUY,
                confidence = 90,
                tech = 0.74f, news = 0.68f, analyst = 0.88f, chain = null,
                atr = 6.40,
                riskAlignment = 96,
                aiRationale = "Flagship conglomerate unlocking shareholder value via corporate restructuring, massive dollar dividends and LNG terminal revenues.",
                sentimentScore = 88
            ),
            createSignal(
                symbol = "MEBL",
                name = "Meezan Bank Limited",
                market = "PSX Pakistan",
                price = 238.50,
                changePct = 1.85,
                verdict = Verdict.BUY,
                confidence = 94,
                tech = 0.86f, news = 0.80f, analyst = 0.92f, chain = null,
                atr = 4.80,
                riskAlignment = 98,
                aiRationale = "Unrivaled Islamic banking franchise with 48.5% ROE, zero-cost deposit dominance and continuous quarterly dividend payouts.",
                sentimentScore = 92
            ),
            createSignal(
                symbol = "SYS",
                name = "Systems Limited",
                market = "PSX Pakistan",
                price = 442.00,
                changePct = 3.40,
                verdict = Verdict.BUY,
                confidence = 91,
                tech = 0.80f, news = 0.85f, analyst = 0.90f, chain = null,
                atr = 11.5,
                riskAlignment = 93,
                aiRationale = "Premier IT export champion benefiting from overseas dollar revenues across North America, Europe and GCC digital transformations.",
                sentimentScore = 89
            ),
            createSignal(
                symbol = "FFC",
                name = "Fauji Fertilizer Company",
                market = "PSX Pakistan",
                price = 168.00,
                changePct = 1.20,
                verdict = Verdict.BUY,
                confidence = 93,
                tech = 0.72f, news = 0.76f, analyst = 0.94f, chain = null,
                atr = 3.50,
                riskAlignment = 97,
                aiRationale = "Premier dividend aristocrat of PSX yielding 14.5% annual payout with pristine sovereign-grade cash flow visibility.",
                sentimentScore = 91
            ),
            createSignal(
                symbol = "OGDC",
                name = "Oil & Gas Development Co",
                market = "PSX Pakistan",
                price = 148.20,
                changePct = 0.95,
                verdict = Verdict.BUY,
                confidence = 87,
                tech = 0.58f, news = 0.65f, analyst = 0.80f, chain = null,
                atr = 3.20,
                riskAlignment = 94,
                aiRationale = "Debt-free upstream exploration powerhouse with enormous domestic energy reserves and aggressive circular debt clearance progress.",
                sentimentScore = 79
            ),
            createSignal(
                symbol = "LUCK",
                name = "Lucky Cement Limited",
                market = "PSX Pakistan",
                price = 865.00,
                changePct = 2.45,
                verdict = Verdict.BUY,
                confidence = 88,
                tech = 0.70f, news = 0.62f, analyst = 0.84f, chain = null,
                atr = 18.0,
                riskAlignment = 92,
                aiRationale = "Dominant industrial powerhouse with export cement channels, overseas energy assets, and local automotive joint ventures.",
                sentimentScore = 82
            ),
            createSignal(
                symbol = "MCB",
                name = "MCB Bank Limited",
                market = "PSX Pakistan",
                price = 225.40,
                changePct = 1.10,
                verdict = Verdict.BUY,
                confidence = 91,
                tech = 0.68f, news = 0.72f, analyst = 0.88f, chain = null,
                atr = 4.20,
                riskAlignment = 96,
                aiRationale = "Highest asset quality across Pakistani banking sector with low non-performing loans and defensive 13.8% dividend yield.",
                sentimentScore = 87
            ),

            // Forex Pairs
            createSignal(
                symbol = "EUR/USD",
                name = "Euro / US Dollar",
                market = "Forex",
                price = 1.0892,
                changePct = -0.12,
                verdict = Verdict.HOLD,
                confidence = 62,
                tech = -0.10f, news = -0.05f, analyst = 0.12f, chain = null,
                atr = 0.0055,
                riskAlignment = 84,
                aiRationale = "ECB and Federal Reserve interest rate policy convergence keeping the pair range-bound between 1.0840 support and 1.0950 resistance.",
                sentimentScore = 48
            ),
            createSignal(
                symbol = "GBP/JPY",
                name = "British Pound / Japanese Yen",
                market = "Forex",
                price = 194.35,
                changePct = 0.48,
                verdict = Verdict.BUY,
                confidence = 74,
                tech = 0.48f, news = 0.38f, analyst = 0.35f, chain = null,
                atr = 1.45,
                riskAlignment = 82,
                aiRationale = "BoJ yield curve control caution coupled with UK services inflation supporting persistent carry trade momentum.",
                sentimentScore = 68
            ),
            createSignal(
                symbol = "USD/JPY",
                name = "US Dollar / Japanese Yen",
                market = "Forex",
                price = 149.25,
                changePct = 0.35,
                verdict = Verdict.BUY,
                confidence = 72,
                tech = 0.45f, news = 0.32f, analyst = 0.30f, chain = null,
                atr = 1.20,
                riskAlignment = 80,
                aiRationale = "Treasury yield spread maintaining dollar bid; monitoring $150.50 key psychological resistance for possible intervention.",
                sentimentScore = 62
            ),
            createSignal(
                symbol = "GBP/USD",
                name = "British Pound / US Dollar",
                market = "Forex",
                price = 1.3045,
                changePct = -0.18,
                verdict = Verdict.HOLD,
                confidence = 65,
                tech = 0.05f, news = 0.10f, analyst = 0.20f, chain = null,
                atr = 0.0075,
                riskAlignment = 83,
                aiRationale = "Consolidating near 50-day moving average ahead of upcoming central bank monetary policy updates.",
                sentimentScore = 52
            ),

            // Cryptocurrencies
            createSignal(
                symbol = "BTC",
                name = "Bitcoin",
                market = "Crypto",
                price = 68420.00,
                changePct = 2.15,
                verdict = Verdict.BUY,
                confidence = 81,
                tech = 0.65f, news = 0.52f, analyst = null, chain = 0.75f,
                atr = 1850.0,
                riskAlignment = 80,
                aiRationale = "Institutional spot ETF inflows accelerating with exchange reserves hitting 6-year lows. On-chain whale wallets aggressively accumulating.",
                sentimentScore = 85
            ),
            createSignal(
                symbol = "ETH",
                name = "Ethereum",
                market = "Crypto",
                price = 2640.50,
                changePct = 1.45,
                verdict = Verdict.BUY,
                confidence = 76,
                tech = 0.54f, news = 0.42f, analyst = null, chain = 0.64f,
                atr = 95.0,
                riskAlignment = 78,
                aiRationale = "Layer-2 transaction throughput reaching all-time highs with deflationary staking dynamics locking circulating supply.",
                sentimentScore = 75
            ),
            createSignal(
                symbol = "SOL",
                name = "Solana",
                market = "Crypto",
                price = 152.40,
                changePct = 4.80,
                verdict = Verdict.BUY,
                confidence = 84,
                tech = 0.78f, news = 0.58f, analyst = null, chain = 0.70f,
                atr = 8.20,
                riskAlignment = 85,
                aiRationale = "Decentralized exchange volume exceeding competitors, Firedancer validator upgrade on testnet boosting network resilience.",
                sentimentScore = 88
            ),
            createSignal(
                symbol = "BNB",
                name = "BNB Chain",
                market = "Crypto",
                price = 585.20,
                changePct = 1.05,
                verdict = Verdict.BUY,
                confidence = 77,
                tech = 0.50f, news = 0.45f, analyst = null, chain = 0.62f,
                atr = 14.5,
                riskAlignment = 82,
                aiRationale = "Launchpool burn events continuing to reduce circulating supply while DeFi ecosystem total value locked remains steady.",
                sentimentScore = 72
            ),

            // Commodities
            createSignal(
                symbol = "XAU",
                name = "Gold / US Dollar",
                market = "Commodities",
                price = 2658.40,
                changePct = 0.92,
                verdict = Verdict.BUY,
                confidence = 92,
                tech = 0.84f, news = 0.78f, analyst = 0.68f, chain = null,
                atr = 28.5,
                riskAlignment = 95,
                aiRationale = "Global central banks aggressively expanding sovereign gold reserves alongside macroeconomic hedging and geopolitical risk demand.",
                sentimentScore = 90
            ),
            createSignal(
                symbol = "WTI",
                name = "Crude Oil WTI",
                market = "Commodities",
                price = 71.30,
                changePct = -2.10,
                verdict = Verdict.SELL,
                confidence = 78,
                tech = -0.70f, news = -0.48f, analyst = -0.35f, chain = null,
                atr = 2.10,
                riskAlignment = 65,
                aiRationale = "Non-OPEC global production surging while industrial manufacturing demand indicators in key export regions show cooling.",
                sentimentScore = 32
            )
        )

        _blueChips.value = listOf(
            BlueChip(
                symbol = "AAPL",
                name = "Apple Inc.",
                sector = "Technology",
                score = 88,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 96, growth = 82, safety = 88, valuation = 72, income = 65),
                metrics = BlueChipMetrics(pe = 33.5, roe = 154.0, margin = 26.4, rev5 = 9.8, eps5 = 14.2, dy = 0.44, de = 1.45, beta = 1.05, capB = 3480.0),
                note = "Pristine capital returns and massive active ecosystem with high moat and sticky services margin.",
                region = "US",
                exchange = "NASDAQ: AAPL",
                priceFormatted = "$228.40",
                currencyCode = "USD"
            ),
            BlueChip(
                symbol = "MSFT",
                name = "Microsoft Corp.",
                sector = "Technology",
                score = 92,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 98, growth = 90, safety = 94, valuation = 68, income = 70),
                metrics = BlueChipMetrics(pe = 35.8, roe = 38.5, margin = 36.2, rev5 = 14.6, eps5 = 18.2, dy = 0.72, de = 0.42, beta = 0.89, capB = 3320.0),
                note = "Dominant enterprise cloud and mission-critical software with premier balance sheet strength.",
                region = "US",
                exchange = "NASDAQ: MSFT",
                priceFormatted = "$448.20",
                currencyCode = "USD"
            ),
            BlueChip(
                symbol = "JPM",
                name = "JPMorgan Chase & Co.",
                sector = "Financials",
                score = 89,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 92, growth = 82, safety = 90, valuation = 86, income = 84),
                metrics = BlueChipMetrics(pe = 12.2, roe = 16.8, margin = 34.2, rev5 = 9.4, eps5 = 12.1, dy = 2.25, de = 1.15, beta = 1.08, capB = 620.0),
                note = "Fortress balance sheet, premier deposit franchise, and dominant global trading/banking desks.",
                region = "US",
                exchange = "NYSE: JPM",
                priceFormatted = "$216.50",
                currencyCode = "USD"
            ),
            BlueChip(
                symbol = "V",
                name = "Visa Inc.",
                sector = "Financials",
                score = 93,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 99, growth = 88, safety = 95, valuation = 74, income = 68),
                metrics = BlueChipMetrics(pe = 30.4, roe = 48.2, margin = 54.8, rev5 = 11.5, eps5 = 15.2, dy = 0.75, de = 0.54, beta = 0.92, capB = 575.0),
                note = "World's preeminent tollbooth on digital commerce; 54%+ net profit margin with zero credit risk.",
                region = "US",
                exchange = "NYSE: V",
                priceFormatted = "$282.10",
                currencyCode = "USD"
            ),
            // PSX Pakistan Blue Chip Giants
            BlueChip(
                symbol = "ENGRO",
                name = "Engro Corporation",
                sector = "Conglomerate & Energy",
                score = 91,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 94, growth = 84, safety = 86, valuation = 88, income = 92),
                metrics = BlueChipMetrics(pe = 5.8, roe = 32.5, margin = 18.2, rev5 = 22.4, eps5 = 18.6, dy = 13.5, de = 0.85, beta = 0.95, capB = 1.4),
                note = "Pakistan's premier conglomerate with flagship fertilizers, LNG terminals, and renewable power infrastructure.",
                region = "Pakistan",
                exchange = "PSX: ENGRO",
                priceFormatted = "Rs 345.50",
                currencyCode = "PKR"
            ),
            BlueChip(
                symbol = "MEBL",
                name = "Meezan Bank Limited",
                sector = "Financials",
                score = 95,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 98, growth = 94, safety = 94, valuation = 86, income = 92),
                metrics = BlueChipMetrics(pe = 4.5, roe = 48.5, margin = 44.0, rev5 = 28.5, eps5 = 35.0, dy = 11.5, de = 0.50, beta = 0.82, capB = 1.6),
                note = "World-class Islamic banking institution with industry-leading 48%+ ROE and expanding zero-cost deposit moat.",
                region = "Pakistan",
                exchange = "PSX: MEBL",
                priceFormatted = "Rs 238.50",
                currencyCode = "PKR"
            ),
            BlueChip(
                symbol = "FFC",
                name = "Fauji Fertilizer Company",
                sector = "Fertilizers & Food",
                score = 94,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 96, growth = 85, safety = 94, valuation = 90, income = 98),
                metrics = BlueChipMetrics(pe = 4.2, roe = 42.0, margin = 26.5, rev5 = 19.5, eps5 = 24.0, dy = 14.5, de = 0.35, beta = 0.65, capB = 0.9),
                note = "The premier dividend aristocrat of Pakistan; unmatched cash flow generation and multi-decade dividend track record.",
                region = "Pakistan",
                exchange = "PSX: FFC",
                priceFormatted = "Rs 168.00",
                currencyCode = "PKR"
            ),
            BlueChip(
                symbol = "SYS",
                name = "Systems Limited",
                sector = "Technology",
                score = 90,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 95, growth = 96, safety = 92, valuation = 72, income = 65),
                metrics = BlueChipMetrics(pe = 15.5, roe = 36.5, margin = 22.8, rev5 = 38.0, eps5 = 34.5, dy = 2.8, de = 0.15, beta = 1.15, capB = 0.6),
                note = "Pakistan's leading global technology exporter with over 35% dollar revenue CAGR across USA, Europe, and Middle East.",
                region = "Pakistan",
                exchange = "PSX: SYS",
                priceFormatted = "Rs 442.00",
                currencyCode = "PKR"
            ),
            BlueChip(
                symbol = "OGDC",
                name = "Oil & Gas Development Co.",
                sector = "Energy",
                score = 88,
                verdict = "Strong long-term buy",
                factors = BlueChipFactors(quality = 92, growth = 76, safety = 95, valuation = 92, income = 94),
                metrics = BlueChipMetrics(pe = 3.4, roe = 28.4, margin = 52.0, rev5 = 14.2, eps5 = 15.5, dy = 12.8, de = 0.05, beta = 0.88, capB = 2.3),
                note = "Debt-free upstream sovereign explorer with immense cash balances, 52%+ profit margins, and high dividend payouts.",
                region = "Pakistan",
                exchange = "PSX: OGDC",
                priceFormatted = "Rs 148.20",
                currencyCode = "PKR"
            )
        )

        _news.value = listOf(
            NewsItem(
                id = "n1",
                title = "Federal Reserve Signals Data-Dependent Neutral Rate Calibration",
                source = "Bloomberg Terminal",
                timeAgo = "12m ago",
                market = "Macro / US",
                sentiment = NewsSentiment.BULLISH,
                sentimentScore = 78,
                impact = NewsImpact.HIGH,
                summary = "Chair remarks emphasize resilient economic backdrop with easing PCE inflation, supporting steady soft landing trajectory for capital markets.",
                relatedSymbols = listOf("NVDA", "AAPL", "MSFT", "EUR/USD")
            ),
            NewsItem(
                id = "n2",
                title = "PSX KSE-100 Surpasses Record High as SBP Foreign Reserves Expand",
                source = "Karachi Financial Daily",
                timeAgo = "25m ago",
                market = "PSX Pakistan",
                sentiment = NewsSentiment.BULLISH,
                sentimentScore = 92,
                impact = NewsImpact.HIGH,
                summary = "Bumper quarterly profits in banking (Meezan Bank) and fertilizer majors (FFC, Engro) spark aggressive institutional foreign and local equity buying.",
                relatedSymbols = listOf("MEBL", "ENGRO", "FFC", "SYS")
            ),
            NewsItem(
                id = "n3",
                title = "Bitcoin Spot ETF Net Weekly Inflows Top $1.4B as Exchange Supply Plummets",
                source = "CoinDesk Intelligence",
                timeAgo = "44m ago",
                market = "Crypto",
                sentiment = NewsSentiment.BULLISH,
                sentimentScore = 84,
                impact = NewsImpact.HIGH,
                summary = "Institutional custodial holdings reach fresh all-time records with sovereign wealth funds exploring strategic allocation.",
                relatedSymbols = listOf("BTC", "ETH", "SOL")
            ),
            NewsItem(
                id = "n4",
                title = "Central Banks Ramp Up Physical Gold Purchases for Seventh Consecutive Quarter",
                source = "World Gold Council",
                timeAgo = "1h ago",
                market = "Commodities",
                sentiment = NewsSentiment.BULLISH,
                sentimentScore = 89,
                impact = NewsImpact.MEDIUM,
                summary = "De-dollarization strategies and macro hedging drive sovereign reserves accumulation, elevating bullion support above $2,640.",
                relatedSymbols = listOf("XAU", "USD/JPY")
            ),
            NewsItem(
                id = "n5",
                title = "Crude Oil Pressured by Non-OPEC Production Surge and Inventory Build",
                source = "Reuters Energy",
                timeAgo = "2h ago",
                market = "Commodities",
                sentiment = NewsSentiment.BEARISH,
                sentimentScore = -65,
                impact = NewsImpact.MEDIUM,
                summary = "US production reaches record highs as refining run rates slow, creating near-term downside price pressure below $72.",
                relatedSymbols = listOf("WTI")
            )
        )

        _alerts.value = listOf(
            AlertItem("a1", "NVDA", "Price Above", 126.0, true, null, "Breakout confirmation above key resistance"),
            AlertItem("a2", "BTC", "Price Above", 70000.0, true, null, "Psychological level re-test"),
            AlertItem("a3", "MEBL", "Price Above", 242.0, true, null, "PSX dividend capture run-up"),
            AlertItem("a4", "XAU", "Price Below", 2635.0, true, null, "ATR stop invalidation level")
        )

        _backtestStrategies.value = listOf(
            BacktestStrategy(
                id = "strat_rsi_atr",
                name = "RSI (14) Pullback & ATR Channel",
                description = "Enters long when RSI dips below 38 in an uptrend, with stop loss at 1.5x ATR and take profit at 3.0x ATR.",
                winRatePct = 71,
                profitFactor = 2.45,
                netGainPct = 38.6,
                maxDrawdownPct = 6.2,
                totalTrades = 84,
                equityCurve = listOf(100f, 103f, 102f, 107f, 105f, 112f, 115f, 113f, 119f, 124f, 122f, 128f, 134f, 132f, 138.6f)
            ),
            BacktestStrategy(
                id = "strat_ema_cross",
                name = "EMA 20 / SMA 50 Momentum Cross",
                description = "Captures secular trend expansions when 20-day exponential moving average crosses above 50-day simple moving average with volume confirmation.",
                winRatePct = 65,
                profitFactor = 2.18,
                netGainPct = 42.4,
                maxDrawdownPct = 8.5,
                totalTrades = 62,
                equityCurve = listOf(100f, 98f, 104f, 106f, 111f, 109f, 116f, 122f, 119f, 127f, 131f, 136f, 142.4f)
            ),
            BacktestStrategy(
                id = "strat_psx_dividend",
                name = "PSX Aristocrats Dividend Reinvestment",
                description = "Systematic quarterly DCA into top 5 PSX dividend payers (FFC, MEBL, ENGRO, OGDC, MCB) with dividend reinvestment compounding.",
                winRatePct = 82,
                profitFactor = 3.12,
                netGainPct = 64.8,
                maxDrawdownPct = 5.4,
                totalTrades = 48,
                equityCurve = listOf(100f, 105f, 109f, 114f, 118f, 124f, 130f, 136f, 142f, 149f, 156f, 164.8f)
            ),
            BacktestStrategy(
                id = "strat_macd_breakout",
                name = "MACD Zero-Line Acceleration",
                description = "Triggers trade entries when MACD histogram flips positive with histogram expanding above zero line in high volume sessions.",
                winRatePct = 68,
                profitFactor = 2.05,
                netGainPct = 31.2,
                maxDrawdownPct = 7.1,
                totalTrades = 96,
                equityCurve = listOf(100f, 102f, 101f, 106f, 108f, 107f, 114f, 118f, 116f, 123f, 127f, 131.2f)
            )
        )

        _learningModules.value = listOf(
            LearningLesson(
                id = "l1",
                title = "Mastering ATR Risk & Position Sizing",
                category = "Risk Management",
                readTimeMinutes = 3,
                summary = "Average True Range (ATR) quantifies the exact normal daily volatility of an instrument. Professional risk managers use 1.5x ATR for stop loss invalidation and 3x ATR for reward targets to guarantee a mathematically positive 2:1 risk/reward expectancy.",
                keyTakeaways = listOf(
                    "Never set arbitrary dollar stop losses; align stops to natural market volatility (ATR).",
                    "A 2:1 Reward-to-Risk ratio means you can be profitable even with a 40% win rate.",
                    "If ATR expands during high news volatility, reduce lot size to maintain constant dollar risk."
                ),
                quizQuestion = "If an asset trades at $100 with an ATR of $4, where is the ideal 1.5x ATR stop loss located?",
                quizOptions = listOf("$96.00 (1x ATR)", "$94.00 (1.5x ATR below entry)", "$92.00 (2x ATR)", "$98.00 (0.5x ATR)"),
                correctOptionIndex = 1,
                explanation = "A 1.5x ATR stop loss is $100 - (1.5 * $4) = $100 - $6 = $94.00. This keeps your stop beyond normal daily market noise."
            ),
            LearningLesson(
                id = "l2",
                title = "PSX Dividend Aristocrats & Karachi 100 Compounding",
                category = "Value & PSX Strategy",
                readTimeMinutes = 4,
                summary = "The Pakistan Stock Exchange (PSX) offers some of the highest cash dividend yields in emerging Asia (12% - 15% on leaders like FFC, MEBL, and Engro). Reinvesting these high-yield payouts creates extraordinary exponential compounding curves.",
                keyTakeaways = listOf(
                    "Meezan Bank (MEBL) delivers industry-leading 48%+ Return on Equity (ROE).",
                    "Fauji Fertilizer (FFC) has paid uninterrupted quarterly dividends for decades.",
                    "Compounding high dividend yield with dollar-cost averaging dramatically reduces drawdowns."
                ),
                quizQuestion = "Why is Return on Equity (ROE) above 30% a critical metric for long-term compounders?",
                quizOptions = listOf("It means the company uses lots of debt", "It proves management generates high profit on shareholder capital without excessive dilution", "It only applies to crypto assets", "It means the stock price will double every month"),
                correctOptionIndex = 1,
                explanation = "High ROE signifies that the business retains capital at superior rates of return, compounding intrinsic value over long horizons."
            ),
            LearningLesson(
                id = "l3",
                title = "RSI & MACD Divergence Detection",
                category = "Technical Analysis",
                readTimeMinutes = 3,
                summary = "When price makes a lower low but the RSI oscillator makes a higher low, Bullish Divergence occurs. This signals institutional accumulation and imminent upward trend reversal.",
                keyTakeaways = listOf(
                    "RSI below 30 signals oversold; above 70 signals overbought conditions.",
                    "Divergence between price peaks and MACD histogram often precedes trend exhaustion.",
                    "Always wait for price action confirmation (candle reversal pattern) before entering."
                ),
                quizQuestion = "What does a Bullish RSI Divergence indicate?",
                quizOptions = listOf("Momentum is strengthening even as price makes a temporary new low", "Price is about to crash lower", "Trading volume has reached zero", "The market has closed"),
                correctOptionIndex = 0,
                explanation = "Bullish divergence reveals that selling momentum is waning and buyers are stepping in before the price visibly breaks upward."
            ),
            LearningLesson(
                id = "l4",
                title = "Macro Forex Mechanics & Interest Rate Differentials",
                category = "Forex Markets",
                readTimeMinutes = 4,
                summary = "Currency valuation is primarily driven by central bank interest rate differentials (Carry Trade) and trade balances. Capital flows toward currencies offering higher real yields and sovereign stability.",
                keyTakeaways = listOf(
                    "The Carry Trade borrows low-yielding currencies (e.g. JPY) to invest in higher-yielding ones (e.g. USD, GBP).",
                    "Central bank interest rate decisions trigger the largest multi-month currency trends.",
                    "Always manage leverage carefully in 24/5 FX markets."
                ),
                quizQuestion = "In the GBP/JPY pair, what typically happens when Bank of Japan maintains negative or low rates while Bank of England raises rates?",
                quizOptions = listOf("GBP depreciates against JPY", "GBP tends to strengthen as investors seek the higher yield on British pounds", "Forex trading halts", "Both currencies drop to zero"),
                correctOptionIndex = 1,
                explanation = "Higher yields attract global capital looking for yield return, driving upward demand for GBP against the low-yielding JPY."
            )
        )

        _watchlist.value = listOf(
            WatchlistItem("NVDA", "NVIDIA Corporation", "US Stocks", System.currentTimeMillis() - 86400000L * 3, 135.0, "Core AI semiconductor play. Watching for breakout above $126."),
            WatchlistItem("BTC", "Bitcoin", "Crypto", System.currentTimeMillis() - 86400000L * 5, 72000.0, "DCA position on pullbacks to $65k."),
            WatchlistItem("ENGRO", "Engro Corporation", "PSX Pakistan", System.currentTimeMillis() - 86400000L * 6, 360.0, "Premier PSX conglomerate holding for quarterly dividend."),
            WatchlistItem("MEBL", "Meezan Bank Limited", "PSX Pakistan", System.currentTimeMillis() - 86400000L * 7, 250.0, "Islamic banking fortress with 48.5% ROE.")
        )

        _userFeedbacks.value = listOf(
            UserFeedback(
                id = "fb_1",
                userName = "Hamza Siddiqui",
                userEmail = "hamza.trader@gmail.com",
                rating = 5,
                category = "PSX Data Feed",
                message = "The inclusion of Meezan Bank (MEBL) and FFC with precise dividend yield calculation has made this my primary trading app in Karachi. Would love to see Lucky Cement (LUCK) historical earnings dates.",
                timestamp = System.currentTimeMillis() - 3600000L * 2,
                status = FeedbackStatus.ACKNOWLEDGED,
                adminReply = "Thank you Hamza. LUCK historical earnings metrics and dividend history are now fully integrated under PSX Blue Chips. - Muhammad Talha Mazhar"
            ),
            UserFeedback(
                id = "fb_2",
                userName = "Sarah Jenkins",
                userEmail = "s.jenkins@vanguardtrade.com",
                rating = 5,
                category = "Signal Accuracy",
                message = "The 1.5x ATR stop loss model on NVDA saved my capital before the earnings dip and triggered entry right at $118 support.",
                timestamp = System.currentTimeMillis() - 3600000L * 5,
                status = FeedbackStatus.RESOLVED,
                adminReply = "Great risk execution Sarah! Maintaining asymmetric 2:1 reward-to-risk is the core foundation of our quantitative engine. - Muhammad Talha Mazhar"
            ),
            UserFeedback(
                id = "fb_3",
                userName = "Tariq Mahmood",
                userEmail = "tariq.m@karachifx.pk",
                rating = 4,
                category = "Feature Request",
                message = "Please add high-priority alerts when RSI enters the oversold zone (<30) during active market sessions for GBP/JPY and EUR/USD.",
                timestamp = System.currentTimeMillis() - 3600000L * 12,
                status = FeedbackStatus.PENDING,
                adminReply = null
            ),
            UserFeedback(
                id = "fb_4",
                userName = "Jason Vance",
                userEmail = "jason.vance@cryptoalpha.io",
                rating = 5,
                category = "Execution Speed",
                message = "Real-time streaming ticks for Bitcoin and Solana are remarkably fast and battery efficient. Best financial terminal UI on Android.",
                timestamp = System.currentTimeMillis() - 86400000L,
                status = FeedbackStatus.RESOLVED,
                adminReply = "Thanks Jason. The WebSocket ticker emulator runs on dedicated low-overhead Coroutine dispatchers for zero UI thread stutter. - Muhammad Talha Mazhar"
            ),
            UserFeedback(
                id = "fb_5",
                userName = "Ayesha Khan",
                userEmail = "ayesha.k@lums.edu.pk",
                rating = 5,
                category = "Mentor Academy",
                message = "The lesson on ATR position sizing with the interactive quiz made risk management click for me. Excellent educational design!",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                status = FeedbackStatus.ACKNOWLEDGED,
                adminReply = "Glad the Mentor Academy helped Ayesha. More institutional risk modules are being authored. - Muhammad Talha Mazhar"
            )
        )
    }

    private fun createSignal(
        symbol: String,
        name: String,
        market: String,
        price: Double,
        changePct: Double,
        verdict: Verdict,
        confidence: Int,
        tech: Float?,
        news: Float?,
        analyst: Float?,
        chain: Float?,
        atr: Double,
        riskAlignment: Int,
        aiRationale: String,
        sentimentScore: Int
    ): Signal {
        val candles = generateCandles(price, atr, changePct >= 0)
        val historicalPoints = candles.map { it.close.toFloat() }
        val indicators = calculateIndicators(candles)
        val buyLevel = if (verdict == Verdict.BUY) price else price - atr
        val stopLevel = if (verdict == Verdict.BUY) price - (1.5 * atr) else price + (1.5 * atr)
        val targetLevel = if (verdict == Verdict.BUY) price + (3.0 * atr) else price - (3.0 * atr)

        return Signal(
            symbol = symbol,
            name = name,
            market = market,
            price = price,
            changePct = changePct,
            verdict = verdict,
            confidence = confidence,
            detail = SignalScores(tech = tech, news = news, analyst = analyst, chain = chain),
            levels = SignalLevel(
                atr = atr,
                buy = roundPrice(buyLevel, price < 10.0),
                stop = roundPrice(stopLevel, price < 10.0),
                target = roundPrice(targetLevel, price < 10.0)
            ),
            featured = true,
            historicalPoints = historicalPoints,
            candles = candles,
            indicators = indicators,
            riskAlignmentScore = riskAlignment,
            aiRationale = aiRationale,
            keyResistance = roundPrice(price + 1.2 * atr, price < 10.0),
            keySupport = roundPrice(price - 1.2 * atr, price < 10.0),
            sentimentScore = sentimentScore
        )
    }

    private fun generateCandles(currentPrice: Double, atr: Double, trendingUp: Boolean): List<Candle> {
        val count = 35
        val list = mutableListOf<Candle>()
        var startPrice = currentPrice * if (trendingUp) 0.90 else 1.10
        val baseTime = System.currentTimeMillis() - (count * 3600000L * 4) // 4h intervals

        for (i in 0 until count) {
            val progress = i.toFloat() / count
            val trendFactor = if (trendingUp) progress * 0.12 else -progress * 0.12
            val cycle = sin(i * 0.5) * (atr * 0.4)
            val openVal = startPrice * (1.0 + trendFactor) + cycle + Random.nextDouble(-atr * 0.2, atr * 0.2)
            val closeVal = if (i == count - 1) currentPrice else openVal + Random.nextDouble(-atr * 0.6, atr * 0.7)
            val highVal = maxOf(openVal, closeVal) + Random.nextDouble(atr * 0.1, atr * 0.5)
            val lowVal = minOf(openVal, closeVal) - Random.nextDouble(atr * 0.1, atr * 0.5)
            val volume = Random.nextDouble(15000.0, 950000.0)

            list.add(
                Candle(
                    time = baseTime + (i * 3600000L * 4),
                    open = roundPrice(openVal, currentPrice < 10.0),
                    high = roundPrice(highVal, currentPrice < 10.0),
                    low = roundPrice(lowVal, currentPrice < 10.0),
                    close = roundPrice(closeVal, currentPrice < 10.0),
                    volume = volume
                )
            )
        }
        return list
    }

    private fun calculateIndicators(candles: List<Candle>): TechnicalIndicators {
        val closes = candles.map { it.close }
        val lastClose = closes.lastOrNull() ?: 100.0

        // EMA 20
        val k = 2.0 / (20.0 + 1.0)
        var ema = closes.firstOrNull() ?: lastClose
        for (c in closes) {
            ema = (c * k) + (ema * (1.0 - k))
        }

        // SMA 50
        val sma50 = closes.takeLast(20).average()

        // RSI 14
        var gains = 0.0
        var losses = 0.0
        for (i in 1 until closes.size) {
            val diff = closes[i] - closes[i - 1]
            if (diff >= 0) gains += diff else losses += abs(diff)
        }
        val avgGain = gains / closes.size
        val avgLoss = losses / closes.size
        val rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
        val rsi14 = 100.0 - (100.0 / (1.0 + rs))

        // MACD
        val macdLine = (ema - sma50) * 0.8
        val macdSignal = macdLine * 0.75
        val macdHist = macdLine - macdSignal

        // Bollinger Bands
        val stdDev = lastClose * 0.025
        val bbUpper = lastClose + (2.0 * stdDev)
        val bbLower = lastClose - (2.0 * stdDev)

        return TechnicalIndicators(
            ema20 = roundPrice(ema, lastClose < 10.0),
            sma50 = roundPrice(sma50, lastClose < 10.0),
            rsi14 = Math.round(rsi14 * 10.0) / 10.0,
            macdLine = Math.round(macdLine * 100.0) / 100.0,
            macdSignal = Math.round(macdSignal * 100.0) / 100.0,
            macdHist = Math.round(macdHist * 100.0) / 100.0,
            bbUpper = roundPrice(bbUpper, lastClose < 10.0),
            bbMiddle = roundPrice(lastClose, lastClose < 10.0),
            bbLower = roundPrice(bbLower, lastClose < 10.0)
        )
    }

    private fun startLiveMarketSimulation() {
        scope.launch {
            while (isActive) {
                delay(2200) // Fast realistic tick interval
                if (_isLiveStreaming.value) {
                    val currentList = _signals.value
                    if (currentList.isNotEmpty()) {
                        val randomIndex = Random.nextInt(currentList.size)
                        val item = currentList[randomIndex]
                        val isSmall = item.price < 10.0
                        val deltaPct = (Random.nextDouble(-0.35, 0.45) / 100.0)
                        val newPrice = roundPrice(item.price * (1.0 + deltaPct), isSmall)
                        val newChange = Math.round((item.changePct + (deltaPct * 100.0)) * 100.0) / 100.0

                        // Update latest candle
                        val updatedCandles = item.candles.toMutableList()
                        if (updatedCandles.isNotEmpty()) {
                            val last = updatedCandles.last()
                            val newHigh = maxOf(last.high, newPrice)
                            val newLow = minOf(last.low, newPrice)
                            updatedCandles[updatedCandles.size - 1] = last.copy(
                                close = newPrice,
                                high = newHigh,
                                low = newLow,
                                volume = last.volume + Random.nextDouble(50.0, 500.0)
                            )
                        }

                        _signals.update { list ->
                            list.mapIndexed { index, sig ->
                                if (index == randomIndex) {
                                    sig.copy(
                                        price = newPrice,
                                        changePct = newChange,
                                        updatedAt = System.currentTimeMillis(),
                                        candles = updatedCandles,
                                        historicalPoints = updatedCandles.map { it.close.toFloat() }
                                    )
                                } else sig
                            }
                        }

                        // Check price alert triggers
                        checkAlertTriggers(item.symbol, newPrice)
                    }
                }
            }
        }
    }

    private fun checkAlertTriggers(symbol: String, currentPrice: Double) {
        _alerts.update { list ->
            list.map { alert ->
                if (alert.isActive && alert.symbol.equals(symbol, ignoreCase = true) && alert.triggeredAt == null) {
                    val triggered = when (alert.condition) {
                        "Price Above" -> currentPrice >= alert.thresholdValue
                        "Price Below" -> currentPrice <= alert.thresholdValue
                        else -> false
                    }
                    if (triggered) {
                        alert.copy(triggeredAt = System.currentTimeMillis())
                    } else alert
                } else alert
            }
        }
    }

    private fun roundPrice(p: Double, isSmall: Boolean): Double {
        return if (isSmall) {
            Math.round(p * 10000.0) / 10000.0
        } else {
            Math.round(p * 100.0) / 100.0
        }
    }

    fun toggleLiveStreaming() {
        _isLiveStreaming.update { !it }
    }

    fun refreshAll() {
        _lastRefreshTimestamp.value = System.currentTimeMillis()
        _signals.update { list ->
            list.map { sig -> sig.copy(updatedAt = System.currentTimeMillis()) }
        }
    }

    fun toggleSource(key: String, enabled: Boolean) {
        _sources.update { list ->
            list.map { if (it.key == key) it.copy(enabled = enabled) else it }
        }
    }

    fun toggleWatchlist(symbol: String, name: String, market: String) {
        _watchlist.update { list ->
            if (list.any { it.symbol == symbol }) {
                list.filter { it.symbol != symbol }
            } else {
                list + WatchlistItem(symbol = symbol, name = name, market = market)
            }
        }
    }

    fun updateWatchlistNote(symbol: String, notes: String, alertTarget: Double?) {
        _watchlist.update { list ->
            list.map {
                if (it.symbol == symbol) {
                    it.copy(userNotes = notes, targetPriceAlert = alertTarget)
                } else it
            }
        }
    }

    fun addAlert(symbol: String, condition: String, threshold: Double, note: String) {
        val newAlert = AlertItem(
            id = "alert_${System.currentTimeMillis()}",
            symbol = symbol,
            condition = condition,
            thresholdValue = threshold,
            isActive = true,
            note = note
        )
        _alerts.update { it + newAlert }
    }

    fun deleteAlert(id: String) {
        _alerts.update { list -> list.filter { it.id != id } }
    }

    fun toggleAlertActive(id: String) {
        _alerts.update { list ->
            list.map { if (it.id == id) it.copy(isActive = !it.isActive) else it }
        }
    }

    fun updateUserProfile(
        name: String,
        email: String,
        badge: String,
        risk: RiskProfile,
        exp: ExperienceLevel,
        monthlyInv: Int,
        currency: String,
        notifications: Boolean
    ) {
        _userProfile.update {
            it.copy(
                name = name,
                email = email,
                avatarBadge = badge,
                riskProfile = risk,
                experienceLevel = exp,
                targetMonthlyInvestment = monthlyInv,
                currency = currency,
                notificationsEnabled = notifications
            )
        }
    }

    fun updateFeedbackStatus(feedbackId: String, newStatus: FeedbackStatus, adminReply: String?) {
        _userFeedbacks.update { list ->
            list.map {
                if (it.id == feedbackId) {
                    it.copy(
                        status = newStatus,
                        adminReply = adminReply ?: it.adminReply
                    )
                } else it
            }
        }
    }

    fun submitUserFeedback(userName: String, userEmail: String, rating: Int, category: String, message: String) {
        val newFb = UserFeedback(
            id = "fb_${System.currentTimeMillis()}",
            userName = userName,
            userEmail = userEmail,
            rating = rating,
            category = category,
            message = message,
            timestamp = System.currentTimeMillis(),
            status = FeedbackStatus.PENDING
        )
        _userFeedbacks.update { listOf(newFb) + it }
    }

    fun toggleMaintenanceMode() {
        _systemStatus.update {
            it.copy(isMaintenanceMode = !it.isMaintenanceMode)
        }
    }

    fun updateSystemNotice(notice: String) {
        _systemStatus.update {
            it.copy(systemNotice = notice)
        }
    }

    fun triggerIntegrityCheck() {
        _systemStatus.update {
            it.copy(
                lastIntegrityCheck = System.currentTimeMillis(),
                feedLatencyMs = Random.nextInt(28, 48),
                activeConnectionsCount = Random.nextInt(1380, 1560)
            )
        }
    }

    fun flushCache() {
        _lastRefreshTimestamp.value = System.currentTimeMillis()
    }
}
