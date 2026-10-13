package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AlertItem
import com.example.data.BacktestStrategy
import com.example.data.BlueChip
import com.example.data.LearningLesson
import com.example.data.NewsItem
import com.example.data.Signal
import com.example.data.SourceSetting
import com.example.data.TickmarkRepository
import com.example.data.UserProfile
import com.example.data.WatchlistItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TickmarkViewModel(
    private val repository: TickmarkRepository = TickmarkRepository()
) : ViewModel() {

    val signals: StateFlow<List<Signal>> = repository.signals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blueChips: StateFlow<List<BlueChip>> = repository.blueChips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlist: StateFlow<List<WatchlistItem>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val sources: StateFlow<List<SourceSetting>> = repository.sources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val news: StateFlow<List<NewsItem>> = repository.news
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertItem>> = repository.alerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val backtestStrategies: StateFlow<List<BacktestStrategy>> = repository.backtestStrategies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learningModules: StateFlow<List<LearningLesson>> = repository.learningModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFeedbacks: StateFlow<List<com.example.data.UserFeedback>> = repository.userFeedbacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemStatus: StateFlow<com.example.data.SystemStatusState> = repository.systemStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.SystemStatusState())

    val isLiveStreaming: StateFlow<Boolean> = repository.isLiveStreaming
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val lastRefreshTime: StateFlow<Long> = repository.lastRefreshTimestamp
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), System.currentTimeMillis())

    fun updateFeedbackStatus(feedbackId: String, newStatus: com.example.data.FeedbackStatus, adminReply: String? = null) {
        repository.updateFeedbackStatus(feedbackId, newStatus, adminReply)
    }

    fun submitUserFeedback(userName: String, userEmail: String, rating: Int, category: String, message: String) {
        repository.submitUserFeedback(userName, userEmail, rating, category, message)
    }

    fun toggleMaintenanceMode() {
        repository.toggleMaintenanceMode()
    }

    fun updateSystemNotice(notice: String) {
        repository.updateSystemNotice(notice)
    }

    fun triggerIntegrityCheck() {
        repository.triggerIntegrityCheck()
    }

    fun flushCache() {
        repository.flushCache()
    }

    fun refreshAll() {
        repository.refreshAll()
    }

    fun toggleWatchlist(symbol: String, name: String, market: String) {
        repository.toggleWatchlist(symbol, name, market)
    }

    fun updateWatchlistNote(symbol: String, notes: String, alertTarget: Double?) {
        repository.updateWatchlistNote(symbol, notes, alertTarget)
    }

    fun addAlert(symbol: String, threshold: Double, note: String, condition: String = "Price Above") {
        repository.addAlert(symbol, condition, threshold, note)
    }

    fun deleteAlert(id: String) {
        repository.deleteAlert(id)
    }

    fun toggleAlertActive(id: String) {
        repository.toggleAlertActive(id)
    }

    fun updateUserProfile(profile: UserProfile) {
        repository.updateUserProfile(
            name = profile.name,
            email = profile.email,
            badge = profile.avatarBadge,
            risk = profile.riskProfile,
            exp = profile.experienceLevel,
            monthlyInv = profile.targetMonthlyInvestment,
            currency = profile.currency,
            notifications = profile.notificationsEnabled
        )
    }

    fun toggleSource(key: String, enabled: Boolean) {
        repository.toggleSource(key, enabled)
    }

    fun toggleLiveStream() {
        repository.toggleLiveStreaming()
    }
}
