# TICKMARK PRO: ALL CODE IN ONE FILE
**Author & Super Admin:** Muhammad Talha Mazhar  
**System Architecture:** Live Multi-Market Trading Terminal, Consensus Engine, Strategy Backtester & Learning Academy  
**Supported Markets:** Forex, Cryptocurrencies, US Stocks (NYSE / NASDAQ), Pakistan Stock Exchange (PSX 100), Commodities  
**Firebase Backend:** Firestore Enterprise Database (`asia-southeast1`) & Google Identity Authentication  

---

## TABLE OF CONTENTS
1. [Backend & Database (Firebase & Firestore Rules)](#1-backend--database)
2. [Gradle & Project Configuration](#2-gradle--project-configuration)
3. [Android Manifest & App Resources](#3-android-manifest--app-resources)
4. [Data Layer: Models & Repository](#4-data-layer)
5. [Firebase Integration: Google Sign-In & Cloud Sync](#5-firebase-integration)
6. [UI Theme & Design System](#6-ui-theme--design-system)
7. [Advanced Interactive Charting Components](#7-advanced-charting-components)
8. [Strategy Backtesting & Mentor Components](#8-backtesting--mentor-components)
9. [Portfolio & Financial Widgets](#9-portfolio--financial-widgets)
10. [Application Screens (Signals, Advisor, Invest, Watchlist, Profile, Admin)](#10-application-screens)
11. [Vocal Intelligence Assistant (TTS & Speech Recognition)](#11-vocal-intelligence-assistant)
12. [Main Entry Point & Testing](#12-entry-point--testing)

---

## 1. BACKEND & DATABASE

### `firestore.rules`
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() {
      return request.auth != null;
    }
    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }
    function isAdmin() {
      return isSignedIn() && (
        request.auth.token.email == 'talha.mazhar@tickmark.pro' ||
        request.auth.token.email == 'sultanahmed0014@gmail.com' ||
        request.auth.token.admin == true
      );
    }

    match /users/{userId} {
      allow read, write: if isOwner(userId) || isAdmin();

      match /watchlist/{symbol} {
        allow read, write: if isOwner(userId);
      }
      match /alerts/{alertId} {
        allow read, write: if isOwner(userId);
      }
    }

    match /feedbacks/{feedbackId} {
      allow read: if isSignedIn();
      allow create: if isSignedIn();
      allow update, delete: if isAdmin();
    }

    match /signals/{symbol} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /system/{document} {
      allow read: if true;
      allow write: if isAdmin();
    }
  }
}
```

### `firebase-blueprint.json`
```json
{
  "entities": {
    "UserProfile": {
      "collection": "users",
      "fields": {
        "name": { "type": "string" },
        "email": { "type": "string" },
        "avatarBadge": { "type": "string" },
        "riskProfile": { "type": "string" },
        "experienceLevel": { "type": "string" },
        "targetMonthlyInvestment": { "type": "number" },
        "currency": { "type": "string" },
        "role": { "type": "string" }
      }
    },
    "WatchlistItem": {
      "collection": "users/{userId}/watchlist",
      "fields": {
        "symbol": { "type": "string" },
        "name": { "type": "string" },
        "market": { "type": "string" },
        "addedAt": { "type": "number" },
        "targetPriceAlert": { "type": "number" },
        "userNotes": { "type": "string" }
      }
    },
    "UserFeedback": {
      "collection": "feedbacks",
      "fields": {
        "userName": { "type": "string" },
        "userEmail": { "type": "string" },
        "rating": { "type": "number" },
        "category": { "type": "string" },
        "message": { "type": "string" },
        "timestamp": { "type": "number" },
        "status": { "type": "string" },
        "adminReply": { "type": "string" }
      }
    },
    "SignalDoc": {
      "collection": "signals",
      "fields": {
        "symbol": { "type": "string" },
        "name": { "type": "string" },
        "market": { "type": "string" },
        "price": { "type": "number" },
        "changePct": { "type": "number" },
        "verdict": { "type": "string" },
        "confidence": { "type": "number" },
        "updatedAt": { "type": "number" }
      }
    }
  }
}
```

---

## 2. GRADLE & PROJECT CONFIGURATION

### `settings.gradle.kts`
```kotlin
pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Tickmark"

include(":app")
```

### `app/build.gradle.kts`
```kotlin
import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.tickmark.qxmtva"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  
  // Firestore and Firebase Auth with Google Sign-In via Credential Manager:
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)

  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
```

---

## 3. ANDROID MANIFEST & APP RESOURCES

### `app/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### `app/src/main/res/values/strings.xml`
```xml
<resources>
    <string name="app_name">Tickmark</string>
</resources>
```

### `app/src/main/res/values/firebase_applet_config.xml`
```xml
<?xml version="1.0" encoding="utf-8" standalone="no"?>
<resources>
    <string name="firestore_database_id" translatable="false">ai-studio-android-tickmark-20f479a2-3eda-4160-8677-0c637f28ca7d</string>
</resources>
```

---

## 4. DATA LAYER

### `app/src/main/java/com/example/data/Models.kt`
```kotlin
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
```

---

## 5. FIREBASE INTEGRATION

### `app/src/main/java/com/example/data/firebase/FirebaseManager.kt`
*(Handles Google Sign-In via Jetpack CredentialManager, Firestore cloud synchronization for watchlists, alerts, user feedback, and security rule enforcement).*

---

## 6. UI THEME & DESIGN SYSTEM

### `app/src/main/java/com/example/ui/theme/Color.kt`
```kotlin
package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val NavyDark = Color(0xFF0B0E14)
val NavyCard = Color(0xFF141824)
val NavyCardElevated = Color(0xFF1E2434)
val NavyBorder = Color(0xFF262D3D)
val NavyBorderLight = Color(0xFF333C4F)

val TextPrimaryDark = Color(0xFFEAECEF)
val TextSecondaryDark = Color(0xFF9CA3AF)
val TextMutedDark = Color(0xFF707A8A)

val SignalBuy = Color(0xFF0ECB81)
val SignalBuyBg = Color(0xFF0C2A1E)
val SignalBuyGlow = Color(0xFF00FF9D)
val SignalSell = Color(0xFFF6465D)
val SignalSellBg = Color(0xFF33151D)
val SignalSellGlow = Color(0xFFFF5268)
val SignalHold = Color(0xFFF0B90B)
val SignalHoldBg = Color(0xFF2D250C)
val SignalHoldGlow = Color(0xFFFCD535)

val BinanceGold = Color(0xFFF0B90B)
val BinanceGoldLight = Color(0xFFFCD535)
val AccentCyan = Color(0xFF00E5FF)
val AccentPurple = Color(0xFF8B5CF6)
val AccentBlue = Color(0xFF3B82F6)

val IndicatorEma20 = Color(0xFF00E5FF)
val IndicatorSma50 = Color(0xFFF59E0B)
val IndicatorBollinger = Color(0xFF8B5CF6)
```

### `app/src/main/java/com/example/ui/theme/Theme.kt`
*(Enforces modern M3 Obsidian dark mode with vibrant neon green, coral red, and Binance gold accents).*

---

## 7. ADVANCED CHARTING COMPONENTS

### `app/src/main/java/com/example/ui/components/AdvancedMarketChart.kt`
*(Offers interactive Candlestick OHLC and Line Chart view, EMA 20, SMA 50, Bollinger Bands, RSI 14 oscillator sub-chart, MACD momentum histogram, crosshairs scrubbing, and user drawing tools for horizontal S/R levels).*

---

## 8. BACKTESTING & MENTOR COMPONENTS

### `app/src/main/java/com/example/ui/components/BacktestSimulatorCard.kt`
*(Calculates Win Rate, Profit Factor, Net Gain, Drawdown, and draws a real-time animated simulated equity curve across 180 market sessions).*

### `app/src/main/java/com/example/ui/components/MentorLessonCard.kt`
*(Interactive educational cards with key takeaways and concept checker quizzes with immediate mentor feedback).*

---

## 9. PORTFOLIO & FINANCIAL WIDGETS

- `LivePulseBadge.kt`: Glowing live streaming status badge.
- `CompoundGrowthSimulator.kt`: 10+ year monthly wealth DCA simulator.
- `PortfolioAllocationChart.kt`: Interactive Donut chart tailored to investor risk persona.
- `NewsFeedCard.kt`: Real-time news headlines with sentiment rating (-100 to +100) and impact score.

---

## 10. APPLICATION SCREENS

- `SignalsScreen.kt`: Multi-market terminal (US Stocks, PSX Pakistan, Forex, Crypto, Commodities) with filter chips, search, and live pulse feed.
- `AdvisorScreen.kt`: Instant "Should I Buy It?" AI decision thesis, interactive chart preview, backtesting studio, and mentor academy.
- `InvestScreen.kt`: 10+ year fundamental blue-chip ratings with 5 pillars (Quality, Growth, Safety, Valuation, Income) featuring Karachi PSX dividend aristocrats.
- `WatchlistScreen.kt`: Position tracking, price alert threshold triggers, and trade journal notes.
- `ProfileScreen.kt`: Profile settings, Risk Profile picker, Firebase cloud sync status, and Admin Console launcher.
- `AdminDashboardScreen.kt`: Full governance console featuring **Muhammad Talha Mazhar** as Super Admin with user feedback management and emergency maintenance controls.

---

## 11. VOCAL INTELLIGENCE ASSISTANT

### `app/src/main/java/com/example/ui/voice/VoiceAssistantManager.kt`
*(Speech recognition and text-to-speech engine providing executive audio briefings acknowledging Muhammad Talha Mazhar).*

### `app/src/main/java/com/example/ui/voice/VoiceSummarySheet.kt`
*(Visual audio visualizer orb with microphone action and quick query chips).*

---

## 12. ENTRY POINT & TESTING

### `app/src/main/java/com/example/MainActivity.kt`
```kotlin
package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.TickmarkApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TickmarkApp()
                }
            }
        }
    }
}
```

### `app/src/test/java/com/example/ExampleRobolectricTest.kt`
```kotlin
package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FeedbackStatus
import com.example.data.TickmarkRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tickmark", appName)
  }

  @Test
  fun `verify repository initializes signals across multi-market assets`() {
    val repository = TickmarkRepository()
    val signals = repository.signals.value
    assertTrue("Signals list should not be empty", signals.isNotEmpty())
    assertTrue("Should include US Stocks", signals.any { it.market == "US Stocks" })
    assertTrue("Should include PSX Pakistan Stocks", signals.any { it.market == "PSX Pakistan" })
    assertTrue("Should include Forex", signals.any { it.market == "Forex" })
    assertTrue("Should include Crypto", signals.any { it.market == "Crypto" })
    assertTrue("Should include Commodities", signals.any { it.market == "Commodities" })
  }

  @Test
  fun `verify authenticated administrator is Muhammad Talha Mazhar and admin controls function`() {
    val repository = TickmarkRepository()
    val profile = repository.userProfile.value
    assertEquals("Muhammad Talha Mazhar", profile.name)
    assertEquals("talha.mazhar@tickmark.pro", profile.email)

    // Test feedback management
    val feedbacks = repository.userFeedbacks.value
    assertTrue("Initial feedback list should not be empty", feedbacks.isNotEmpty())
    val firstFb = feedbacks.first()
    repository.updateFeedbackStatus(firstFb.id, FeedbackStatus.RESOLVED, "Resolution verified by Muhammad Talha Mazhar")
    val updatedFb = repository.userFeedbacks.value.find { it.id == firstFb.id }
    assertNotNull(updatedFb)
    assertEquals(FeedbackStatus.RESOLVED, updatedFb?.status)
    assertEquals("Resolution verified by Muhammad Talha Mazhar", updatedFb?.adminReply)

    // Test system status controls
    val initialStatus = repository.systemStatus.value
    assertEquals(false, initialStatus.isMaintenanceMode)
    repository.toggleMaintenanceMode()
    assertEquals(true, repository.systemStatus.value.isMaintenanceMode)
    repository.toggleMaintenanceMode()
    assertEquals(false, repository.systemStatus.value.isMaintenanceMode)
  }
}
```
