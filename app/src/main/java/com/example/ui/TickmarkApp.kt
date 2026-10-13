package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.BlueChip
import com.example.data.Signal
import androidx.activity.compose.BackHandler
import com.example.ui.components.BlueChipDetailSheet
import com.example.ui.components.SignalDetailSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdvisorScreen
import com.example.ui.screens.InvestScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SignalsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BinanceGold
import com.example.ui.voice.VoiceAssistantManager
import com.example.ui.voice.VoiceSummarySheet
import kotlinx.coroutines.launch

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    SIGNALS("Signals", Icons.Filled.TrendingUp, Icons.Filled.TrendingUp, "tab_signals"),
    ADVISOR("Advisor", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb, "tab_advisor"),
    INVEST("Invest", Icons.Filled.Security, Icons.Outlined.Security, "tab_invest"),
    WATCHLIST("Watchlist", Icons.Filled.Bookmark, Icons.Filled.BookmarkBorder, "tab_watchlist"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TickmarkApp(
    viewModel: TickmarkViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(NavTab.SIGNALS) }
    var selectedSignalForDetail by remember { mutableStateOf<Signal?>(null) }
    var selectedBlueChipForDetail by remember { mutableStateOf<BlueChip?>(null) }
    var showTermsDisclaimer by remember { mutableStateOf(false) }

    val signals by viewModel.signals.collectAsState()
    val blueChips by viewModel.blueChips.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val sources by viewModel.sources.collectAsState()
    val news by viewModel.news.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val backtestStrategies by viewModel.backtestStrategies.collectAsState()
    val learningModules by viewModel.learningModules.collectAsState()
    val userFeedbacks by viewModel.userFeedbacks.collectAsState()
    val systemStatus by viewModel.systemStatus.collectAsState()
    val isLiveStreaming by viewModel.isLiveStreaming.collectAsState()
    val lastRefreshTime by viewModel.lastRefreshTime.collectAsState()

    var showAdminDashboard by remember { mutableStateOf(false) }

    val signalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val blueChipSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val voiceAssistantSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val voiceAssistant = remember { VoiceAssistantManager(context) }
    DisposableEffect(voiceAssistant) {
        onDispose { voiceAssistant.destroy() }
    }
    var showVoiceAssistantSheet by remember { mutableStateOf(false) }

    val currencySymbol = when (userProfile.currency) {
        "EUR" -> "€"
        "GBP" -> "£"
        "PKR" -> "Rs "
        else -> "$"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showVoiceAssistantSheet = true },
                containerColor = BinanceGold,
                contentColor = MaterialTheme.colorScheme.background,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.testTag("voice_assistant_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Vocal Intelligence Briefing",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BinanceGold,
                            selectedTextColor = BinanceGold,
                            indicatorColor = BinanceGold.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { width -> if (targetState.ordinal > initialState.ordinal) width / 4 else -width / 4 })
                        .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState.ordinal > initialState.ordinal) -width / 4 else width / 4 })
                },
                label = "screen_transition"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.SIGNALS -> {
                        SignalsScreen(
                            signals = signals,
                            news = news,
                            userProfile = userProfile,
                            isLiveStreaming = isLiveStreaming,
                            onSignalClick = { signal ->
                                selectedSignalForDetail = signal
                            },
                            onRefresh = { viewModel.refreshAll() },
                            onOpenVoiceAssistant = { showVoiceAssistantSheet = true }
                        )
                    }
                    NavTab.ADVISOR -> {
                        AdvisorScreen(
                            signals = signals,
                            blueChips = blueChips,
                            backtestStrategies = backtestStrategies,
                            learningLessons = learningModules,
                            userProfile = userProfile,
                            onOpenSignal = { signal ->
                                selectedSignalForDetail = signal
                            },
                            onOpenBlueChip = { blueChip ->
                                selectedBlueChipForDetail = blueChip
                            }
                        )
                    }
                    NavTab.INVEST -> {
                        InvestScreen(
                            blueChips = blueChips,
                            userProfile = userProfile,
                            onBlueChipClick = { blueChip ->
                                selectedBlueChipForDetail = blueChip
                            }
                        )
                    }
                    NavTab.WATCHLIST -> {
                        WatchlistScreen(
                            watchlist = watchlist,
                            alerts = alerts,
                            signals = signals,
                            userProfile = userProfile,
                            onOpenSignal = { signal ->
                                selectedSignalForDetail = signal
                            },
                            onRemoveFromWatchlist = { symbol ->
                                val target = watchlist.find { it.symbol == symbol }
                                if (target != null) {
                                    viewModel.toggleWatchlist(target.symbol, target.name, target.market)
                                }
                            },
                            onUpdateWatchlistNote = { symbol, notes, alert ->
                                viewModel.updateWatchlistNote(symbol, notes, alert)
                            },
                            onAddSymbol = { symbol ->
                                val match = signals.find { it.symbol.equals(symbol, ignoreCase = true) }
                                if (match != null) {
                                    viewModel.toggleWatchlist(match.symbol, match.name, match.market)
                                }
                            },
                            onToggleAlert = { id ->
                                viewModel.toggleAlertActive(id)
                            },
                            onDeleteAlert = { id ->
                                viewModel.deleteAlert(id)
                            },
                            onAddNewAlert = { sym, price, note ->
                                viewModel.addAlert(sym, price, note)
                            }
                        )
                    }
                    NavTab.PROFILE -> {
                        ProfileScreen(
                            userProfile = userProfile,
                            sources = sources,
                            isLiveStreaming = isLiveStreaming,
                            lastRefreshTime = lastRefreshTime,
                            totalSignalsCount = signals.size,
                            onUpdateProfile = { updated ->
                                viewModel.updateUserProfile(updated)
                            },
                            onToggleSource = { key, enabled ->
                                viewModel.toggleSource(key, enabled)
                            },
                            onToggleLiveStream = {
                                viewModel.toggleLiveStream()
                            },
                            onOpenAdminDashboard = {
                                showAdminDashboard = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Full Screen Interactive Admin Master Console Modal / Sub-screen
    if (showAdminDashboard) {
        BackHandler { showAdminDashboard = false }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AdminDashboardScreen(
                userProfile = userProfile,
                systemStatus = systemStatus,
                feedbacks = userFeedbacks,
                onToggleMaintenance = { viewModel.toggleMaintenanceMode() },
                onTriggerIntegrityCheck = { viewModel.triggerIntegrityCheck() },
                onFlushCache = { viewModel.flushCache() },
                onUpdateSystemNotice = { notice -> viewModel.updateSystemNotice(notice) },
                onUpdateFeedbackStatus = { id, status, reply -> viewModel.updateFeedbackStatus(id, status, reply) },
                onSubmitNewFeedback = { name, email, rating, cat, msg -> viewModel.submitUserFeedback(name, email, rating, cat, msg) },
                onNavigateBack = { showAdminDashboard = false }
            )
        }
    }

    // Modal Signal Details
    selectedSignalForDetail?.let { signal ->
        val isWatched = watchlist.any { it.symbol == signal.symbol }
        SignalDetailSheet(
            signal = signal,
            userProfile = userProfile,
            isWatched = isWatched,
            onToggleWatchlist = {
                viewModel.toggleWatchlist(signal.symbol, signal.name, signal.market)
            },
            onSaveNotes = { notes, alert ->
                viewModel.updateWatchlistNote(signal.symbol, notes, alert)
            },
            onAddAlert = { sym, threshold, note ->
                viewModel.addAlert(sym, threshold, note)
            },
            onDismiss = {
                coroutineScope.launch { signalSheetState.hide() }
                selectedSignalForDetail = null
            },
            sheetState = signalSheetState,
            currencySymbol = currencySymbol
        )
    }

    // Modal Blue Chip Details
    selectedBlueChipForDetail?.let { blueChip ->
        BlueChipDetailSheet(
            blueChip = blueChip,
            onDismiss = {
                coroutineScope.launch { blueChipSheetState.hide() }
                selectedBlueChipForDetail = null
            },
            sheetState = blueChipSheetState,
            currencySymbol = currencySymbol
        )
    }

    // Modal Voice Intelligence Briefing
    if (showVoiceAssistantSheet) {
        VoiceSummarySheet(
            voiceAssistant = voiceAssistant,
            signals = signals,
            blueChips = blueChips,
            watchlist = watchlist,
            userProfile = userProfile,
            onDismiss = {
                coroutineScope.launch { voiceAssistantSheetState.hide() }
                showVoiceAssistantSheet = false
            },
            sheetState = voiceAssistantSheetState
        )
    }

    // Terms / Initial Disclaimer Dialog
    if (showTermsDisclaimer) {
        AlertDialog(
            onDismissRequest = { showTermsDisclaimer = false },
            title = {
                Text(
                    text = "Before You Start",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Tickmark displays algorithmic market analysis and automated consensus signals. It is not licensed financial advice, and no signal guarantees profit. Financial markets involve inherent risk of capital loss.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { showTermsDisclaimer = false }) {
                    Text("I Understand")
                }
            }
        )
    }
}
