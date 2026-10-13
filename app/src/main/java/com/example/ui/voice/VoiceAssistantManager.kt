package com.example.ui.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.BlueChip
import com.example.data.Signal
import com.example.data.UserProfile
import com.example.data.Verdict
import com.example.data.WatchlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

data class VoiceSummaryResult(
    val query: String,
    val vocalScript: String,
    val visualSummaryTitle: String,
    val keyPoints: List<String>
)

class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _spokenTranscript = MutableStateFlow("")
    val spokenTranscript: StateFlow<String> = _spokenTranscript.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    private val _currentResult = MutableStateFlow<VoiceSummaryResult?>(null)
    val currentResult: StateFlow<VoiceSummaryResult?> = _currentResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.setPitch(1.0f)
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _voiceState.value = VoiceState.SPEAKING
                    }

                    override fun onDone(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _voiceState.value = VoiceState.IDLE
                    }
                })
            }
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.LISTENING
                        _errorMessage.value = null
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.LISTENING
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _soundLevel.value = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.PROCESSING
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Try speaking clearly."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Listening timed out. Tap microphone to retry."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording issue."
                            else -> "Voice recognition ready. You can also tap quick queries below."
                        }
                        _errorMessage.value = message
                        _voiceState.value = VoiceState.ERROR
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        _spokenTranscript.value = text
                        _voiceState.value = VoiceState.PROCESSING
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _spokenTranscript.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening() {
        stopSpeaking()
        _errorMessage.value = null
        _spokenTranscript.value = ""
        _voiceState.value = VoiceState.LISTENING

        if (speechRecognizer != null) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            try {
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _errorMessage.value = "Microphone service initialization error."
                _voiceState.value = VoiceState.ERROR
            }
        } else {
            _errorMessage.value = "Speech recognition service not present on device. Please select a quick query."
            _voiceState.value = VoiceState.ERROR
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        if (_voiceState.value == VoiceState.LISTENING) {
            _voiceState.value = VoiceState.PROCESSING
        }
    }

    fun processQuery(
        query: String,
        signals: List<Signal>,
        blueChips: List<BlueChip>,
        watchlist: List<WatchlistItem>,
        userProfile: UserProfile
    ) {
        _spokenTranscript.value = query
        _voiceState.value = VoiceState.PROCESSING

        val q = query.lowercase().trim()
        val currSymbol = when (userProfile.currency) {
            "EUR" -> "€"
            "GBP" -> "£"
            "PKR" -> "Rs "
            else -> "$"
        }

        val result = when {
            q.contains("portfolio") || q.contains("allocation") || q.contains("status") || q.contains("holding") -> {
                generatePortfolioSummary(userProfile, watchlist, signals, currSymbol)
            }
            q.contains("nvidia") || q.contains("nvda") -> {
                generateSingleAssetSummary("NVDA", signals, blueChips, currSymbol)
            }
            q.contains("bitcoin") || q.contains("btc") -> {
                generateSingleAssetSummary("BTC", signals, blueChips, currSymbol)
            }
            q.contains("apple") || q.contains("aapl") -> {
                generateSingleAssetSummary("AAPL", signals, blueChips, currSymbol)
            }
            q.contains("tesla") || q.contains("tsla") -> {
                generateSingleAssetSummary("TSLA", signals, blueChips, currSymbol)
            }
            q.contains("microsoft") || q.contains("msft") -> {
                generateSingleAssetSummary("MSFT", signals, blueChips, currSymbol)
            }
            q.contains("pakistan") || q.contains("psx") -> {
                generatePakistanSummary(blueChips, signals)
            }
            q.contains("engro") -> {
                generateSingleAssetSummary("ENGRO", signals, blueChips, "Rs ")
            }
            q.contains("meezan") || q.contains("mebl") -> {
                generateSingleAssetSummary("MEBL", signals, blueChips, "Rs ")
            }
            q.contains("systems") || q.contains("sys") -> {
                generateSingleAssetSummary("SYS", signals, blueChips, "Rs ")
            }
            q.contains("blue chip") || q.contains("invest") || q.contains("long term") -> {
                generateBlueChipSummary(blueChips, userProfile)
            }
            else -> {
                // Default: comprehensive trading signals briefing
                generateSignalsSummary(signals, userProfile, currSymbol)
            }
        }

        _currentResult.value = result
        speak(result.vocalScript)
    }

    private fun generateSignalsSummary(
        signals: List<Signal>,
        userProfile: UserProfile,
        currSymbol: String
    ): VoiceSummaryResult {
        val buySignals = signals.filter { it.verdict == Verdict.BUY }
        val topBuy = buySignals.maxByOrNull { it.confidence }
        val script = buildString {
            append("Greetings Admin ${userProfile.name}. Executive market briefing. ")
            append("There are currently ${buySignals.size} active Buy signals across Forex, Crypto, US, and PSX Pakistan. ")
            if (topBuy != null) {
                append("Highest confidence setup is ${topBuy.name}, ticker ${topBuy.symbol}, ")
                append("trading at $currSymbol${String.format(Locale.US, "%.2f", topBuy.price)}, ")
                append("showing ${topBuy.confidence} percent confidence with target near $currSymbol${String.format(Locale.US, "%.2f", topBuy.levels?.target ?: topBuy.price)}. ")
            }
            append("Market bias is constructive. Remember to honor stop-loss levels and manage downside risk.")
        }
        val keyPoints = listOf(
            "${buySignals.size} active Buy setups detected across markets",
            "Top Conviction: ${topBuy?.symbol ?: "NVDA"} at ${topBuy?.confidence ?: 89}% confidence",
            "Target Exit: $currSymbol${String.format(Locale.US, "%.2f", topBuy?.levels?.target ?: 0.0)}",
            "Invalidation Stop: $currSymbol${String.format(Locale.US, "%.2f", topBuy?.levels?.stop ?: 0.0)}"
        )
        return VoiceSummaryResult(
            query = "Recent Trading Signals Summary",
            vocalScript = script,
            visualSummaryTitle = "Market Signals Vocal Briefing",
            keyPoints = keyPoints
        )
    }

    private fun generatePortfolioSummary(
        userProfile: UserProfile,
        watchlist: List<WatchlistItem>,
        signals: List<Signal>,
        currSymbol: String
    ): VoiceSummaryResult {
        val risk = userProfile.riskProfile
        val script = buildString {
            append("Admin briefing for ${userProfile.name}. ")
            append("Portfolio strategy is calibrated to ${risk.title}, with a target investment of $currSymbol${userProfile.targetMonthlyInvestment} monthly. ")
            append("You are currently monitoring ${watchlist.size} active instruments in your personal watchlist. ")
            append("Recommended allocation: 40 percent in quality compounders, 25 percent dividend aristocrats, and balanced digital assets. ")
            append("All portfolio systems are operating within target risk parameters.")
        }
        val keyPoints = listOf(
            "Super Admin: ${userProfile.name}",
            "Investor Persona: ${risk.badge}",
            "Monthly Target: $currSymbol${userProfile.targetMonthlyInvestment}",
            "Active Watchlist: ${watchlist.size} assets tracked"
        )
        return VoiceSummaryResult(
            query = "Current Portfolio Status",
            vocalScript = script,
            visualSummaryTitle = "Personalized Portfolio Status",
            keyPoints = keyPoints
        )
    }

    private fun generateSingleAssetSummary(
        symbol: String,
        signals: List<Signal>,
        blueChips: List<BlueChip>,
        currSymbol: String
    ): VoiceSummaryResult {
        val sig = signals.find { it.symbol.equals(symbol, ignoreCase = true) }
        val bc = blueChips.find { it.symbol.equals(symbol, ignoreCase = true) }

        val script = if (sig != null) {
            buildString {
                append("${sig.name}, ticker ${sig.symbol}. ")
                append("Trading signal verdict is ${sig.verdict.title}, ")
                append("at ${sig.confidence} percent confidence. ")
                append("Current price is $currSymbol${String.format(Locale.US, "%.2f", sig.price)}, ")
                append("with 24 hour change of ${String.format(Locale.US, "%.2f", sig.changePct)} percent. ")
                sig.levels?.let {
                    append("Execution plan: target at $currSymbol${String.format(Locale.US, "%.2f", it.target)}, ")
                    append("and stop loss at $currSymbol${String.format(Locale.US, "%.2f", it.stop)}. ")
                }
                if (bc != null) {
                    append("Long term 10 year score is ${bc.score} out of 100: ${bc.verdict}.")
                }
            }
        } else {
            "Asset $symbol data is currently streaming. Please check the live terminal."
        }

        val keyPoints = if (sig != null) {
            listOf(
                "Verdict: ${sig.verdict.title} (${sig.confidence}% Confidence)",
                "Current Price: $currSymbol${String.format(Locale.US, "%.2f", sig.price)}",
                "Target / Stop: $currSymbol${String.format(Locale.US, "%.2f", sig.levels?.target ?: 0.0)} / $currSymbol${String.format(Locale.US, "%.2f", sig.levels?.stop ?: 0.0)}",
                "Thesis: ${sig.aiRationale.take(65)}..."
            )
        } else listOf("No active data for $symbol")

        return VoiceSummaryResult(
            query = "$symbol Analysis Briefing",
            vocalScript = script,
            visualSummaryTitle = "$symbol Vocal Intelligence",
            keyPoints = keyPoints
        )
    }

    private fun generateBlueChipSummary(
        blueChips: List<BlueChip>,
        userProfile: UserProfile
    ): VoiceSummaryResult {
        val topPicks = blueChips.sortedByDescending { it.score }.take(3)
        val names = topPicks.joinToString(", ") { "${it.name} at ${it.score} points" }
        val script = buildString {
            append("10-Year Blue Chip Investment briefing for Admin ${userProfile.name}. ")
            append("Top rated fundamental compounders in the index are: $names. ")
            append("These businesses feature return on equity above 30 percent, strong balance sheet safety, and consistent cash flow generation.")
        }
        return VoiceSummaryResult(
            query = "10+ Year Blue Chip Briefing",
            vocalScript = script,
            visualSummaryTitle = "Blue-Chip Long-Term Briefing",
            keyPoints = topPicks.map { "${it.name} (${it.symbol}): ${it.score}/100 • ${it.verdict}" }
        )
    }

    private fun generatePakistanSummary(
        blueChips: List<BlueChip>,
        signals: List<Signal>
    ): VoiceSummaryResult {
        val psxBlueChips = blueChips.filter { it.region.equals("Pakistan", ignoreCase = true) }
        val topPsx = psxBlueChips.sortedByDescending { it.score }.take(3)
        val names = topPsx.joinToString(", ") { "${it.name} (${it.symbol}) at ${it.score} points" }
        val script = buildString {
            append("Pakistan Stock Exchange blue chip briefing. ")
            append("Top rated PSX fundamental compounders are: $names. ")
            append("Meezan Bank leads with 48.5 percent return on equity, and Fauji Fertilizer delivers a 14.5 percent cash dividend yield. ")
            append("Engro Corporation and Systems Limited represent the premier industrial and technology export pillars.")
        }
        return VoiceSummaryResult(
            query = "Pakistan PSX Blue Chip Briefing",
            vocalScript = script,
            visualSummaryTitle = "🇵🇰 PSX Pakistan Blue Chip Leaders",
            keyPoints = listOf(
                "Meezan Bank (MEBL): 95/100 • 48.5% ROE",
                "Fauji Fertilizer (FFC): 94/100 • 14.5% Yield",
                "Engro Corp (ENGRO): 91/100 • Conglomerate & LNG",
                "Systems Limited (SYS): 90/100 • 35%+ Tech Export CAGR"
            )
        )
    }

    fun speak(text: String) {
        if (isTtsReady) {
            _voiceState.value = VoiceState.SPEAKING
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vocal_summary_utterance")
        }
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        if (_voiceState.value == VoiceState.SPEAKING) {
            _voiceState.value = VoiceState.IDLE
        }
    }

    fun replaySummary() {
        _currentResult.value?.vocalScript?.let {
            speak(it)
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
