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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.FeedbackStatus
import com.example.data.SystemStatusState
import com.example.data.UserFeedback
import com.example.data.UserProfile
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalBuyBg
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalHoldBg
import com.example.ui.theme.SignalSell
import com.example.ui.theme.SignalSellBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    userProfile: UserProfile,
    systemStatus: SystemStatusState,
    feedbacks: List<UserFeedback>,
    onToggleMaintenance: () -> Unit,
    onTriggerIntegrityCheck: () -> Unit,
    onFlushCache: () -> Unit,
    onUpdateSystemNotice: (String) -> Unit,
    onUpdateFeedbackStatus: (String, FeedbackStatus, String?) -> Unit,
    onSubmitNewFeedback: (String, String, Int, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFeedbackFilter by remember { mutableStateOf<FeedbackStatus?>(null) }
    var replyingToFeedback by remember { mutableStateOf<UserFeedback?>(null) }
    var replyText by remember { mutableStateOf("") }

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastNoticeText by remember { mutableStateOf(systemStatus.systemNotice) }

    var showAddFeedbackDialog by remember { mutableStateOf(false) }
    var testFeedbackUser by remember { mutableStateOf("Bilal Aslam") }
    var testFeedbackEmail by remember { mutableStateOf("bilal.trader@lahore.pk") }
    var testFeedbackRating by remember { mutableStateOf("5") }
    var testFeedbackCategory by remember { mutableStateOf("PSX Execution") }
    var testFeedbackMsg by remember { mutableStateOf("Incredible speed and accuracy on the Engro Corporation ATR plan!") }

    val filteredFeedbacks = remember(feedbacks, selectedFeedbackFilter) {
        if (selectedFeedbackFilter == null) feedbacks
        else feedbacks.filter { it.status == selectedFeedbackFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 95.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar Navigation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Admin Master Console",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Governance, User Feedback & Terminal Infrastructure",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Authenticated Administrator Hero Identity Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BinanceGold)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(BinanceGold.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, BinanceGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 30.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Muhammad Talha Mazhar",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Authenticated System Administrator",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BinanceGold
                            )
                            Text(
                                text = "talha.mazhar@tickmark.pro • ID: MT-MAZHAR-01",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Verification & Clearance Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = SignalBuy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cryptographic Root Auth Active",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SignalBuy
                            )
                        }
                        Text(
                            text = "CLEARANCE: LEVEL 5",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = BinanceGold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Admin Quick Diagnostic Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTriggerIntegrityCheck,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ping Engine",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = onFlushCache,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = BinanceGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Flush Cache",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { showBroadcastDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BinanceGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = Color(0xFF0B0E14),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Broadcast",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0B0E14)
                            )
                        }
                    }
                }
            }
        }

        // Live System Status Controls Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (systemStatus.isMaintenanceMode) SignalSell else SignalBuy,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM STATUS & ENGINE CONTROLS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (systemStatus.isMaintenanceMode) SignalSellBg else SignalBuyBg,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (systemStatus.isMaintenanceMode) "MAINTENANCE" else "OPERATIONAL",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (systemStatus.isMaintenanceMode) SignalSell else SignalBuy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // System Diagnostic Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DiagnosticMetricCard(
                            label = "Feed Latency",
                            value = "${systemStatus.feedLatencyMs} ms",
                            color = SignalBuy,
                            modifier = Modifier.weight(1f)
                        )
                        DiagnosticMetricCard(
                            label = "Server Uptime",
                            value = "${systemStatus.serverUptimePct}%",
                            color = AccentCyan,
                            modifier = Modifier.weight(1f)
                        )
                        DiagnosticMetricCard(
                            label = "Active Traders",
                            value = "${systemStatus.activeConnectionsCount}",
                            color = BinanceGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gateway sync details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        GatewayRow("🇵🇰 PSX Exchange Gateway", "Synchronized (KSE-100 Low Latency)", SignalBuy)
                        GatewayRow("🇺🇸 US Equities (NYSE / NASDAQ)", "Synchronized (TwelveData)", SignalBuy)
                        GatewayRow("🌐 Forex & Crypto Streaming", "Active Real-Time Ticks", SignalBuy)
                        GatewayRow("⚙️ Consensus Model Engine", systemStatus.consensusEngineStatus, AccentCyan)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Maintenance Mode Master Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Maintenance Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Pause external market orders and broadcast maintenance warning",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = systemStatus.isMaintenanceMode,
                            onCheckedChange = { onToggleMaintenance() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SignalSell
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Broadcast Notice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Active Terminal System Notice",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = systemStatus.systemNotice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                        TextButton(onClick = { showBroadcastDialog = true }) {
                            Text("Edit Notice", color = BinanceGold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // User Feedback Management Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "USER FEEDBACK MANAGEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "Review trader inquiries, resolve tickets, and post admin replies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddFeedbackDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("+ Test Ticket", fontSize = 11.sp, color = AccentCyan)
                }
            }
        }

        // Feedback Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Filter
                FeedbackFilterChip(
                    label = "All (${feedbacks.size})",
                    isSelected = selectedFeedbackFilter == null,
                    onClick = { selectedFeedbackFilter = null }
                )
                FeedbackFilterChip(
                    label = "Under Review (${feedbacks.count { it.status == FeedbackStatus.PENDING }})",
                    isSelected = selectedFeedbackFilter == FeedbackStatus.PENDING,
                    onClick = { selectedFeedbackFilter = FeedbackStatus.PENDING }
                )
                FeedbackFilterChip(
                    label = "Acknowledged (${feedbacks.count { it.status == FeedbackStatus.ACKNOWLEDGED }})",
                    isSelected = selectedFeedbackFilter == FeedbackStatus.ACKNOWLEDGED,
                    onClick = { selectedFeedbackFilter = FeedbackStatus.ACKNOWLEDGED }
                )
                FeedbackFilterChip(
                    label = "Resolved (${feedbacks.count { it.status == FeedbackStatus.RESOLVED }})",
                    isSelected = selectedFeedbackFilter == FeedbackStatus.RESOLVED,
                    onClick = { selectedFeedbackFilter = FeedbackStatus.RESOLVED }
                )
            }
        }

        // List of Feedback Submissions
        if (filteredFeedbacks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No user feedback matching this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredFeedbacks, key = { it.id }) { fb ->
                UserFeedbackItemCard(
                    feedback = fb,
                    onStatusChange = { newStatus ->
                        onUpdateFeedbackStatus(fb.id, newStatus, null)
                    },
                    onOpenReply = {
                        replyingToFeedback = fb
                        replyText = fb.adminReply ?: "Hello ${fb.userName}, thank you for your input. - Muhammad Talha Mazhar"
                    }
                )
            }
        }
    }

    // Admin Reply Dialog
    replyingToFeedback?.let { targetFb ->
        AlertDialog(
            onDismissRequest = { replyingToFeedback = null },
            title = {
                Text(
                    text = "Reply to ${targetFb.userName}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "\"${targetFb.message}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Official Response as Muhammad Talha Mazhar") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateFeedbackStatus(targetFb.id, FeedbackStatus.RESOLVED, replyText)
                        replyingToFeedback = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BinanceGold)
                ) {
                    Text("Post Official Reply", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { replyingToFeedback = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Broadcast Notice Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Update System Broadcast Notice") },
            text = {
                OutlinedTextField(
                    value = broadcastNoticeText,
                    onValueChange = { broadcastNoticeText = it },
                    label = { Text("Broadcast Message to all terminal users") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSystemNotice(broadcastNoticeText)
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BinanceGold)
                ) {
                    Text("Broadcast", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Test Feedback Submission Dialog
    if (showAddFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showAddFeedbackDialog = false },
            title = { Text("Create Test User Feedback Ticket") },
            text = {
                Column {
                    OutlinedTextField(
                        value = testFeedbackUser,
                        onValueChange = { testFeedbackUser = it },
                        label = { Text("Trader Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testFeedbackEmail,
                        onValueChange = { testFeedbackEmail = it },
                        label = { Text("Trader Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testFeedbackCategory,
                        onValueChange = { testFeedbackCategory = it },
                        label = { Text("Category (e.g. PSX, Signals, Speed)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testFeedbackMsg,
                        onValueChange = { testFeedbackMsg = it },
                        label = { Text("Feedback Message") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitNewFeedback(
                            testFeedbackUser,
                            testFeedbackEmail,
                            testFeedbackRating.toIntOrNull() ?: 5,
                            testFeedbackCategory,
                            testFeedbackMsg
                        )
                        showAddFeedbackDialog = false
                    }
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFeedbackDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DiagnosticMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GatewayRow(name: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
private fun FeedbackFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(
                if (isSelected) BinanceGold
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0B0E14) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun UserFeedbackItemCard(
    feedback: UserFeedback,
    onStatusChange: (FeedbackStatus) -> Unit,
    onOpenReply: () -> Unit
) {
    val statusColor = when (feedback.status) {
        FeedbackStatus.PENDING -> SignalHold
        FeedbackStatus.ACKNOWLEDGED -> AccentCyan
        FeedbackStatus.RESOLVED -> SignalBuy
    }
    val statusBg = when (feedback.status) {
        FeedbackStatus.PENDING -> SignalHoldBg
        FeedbackStatus.ACKNOWLEDGED -> Color(0xFF003847)
        FeedbackStatus.RESOLVED -> SignalBuyBg
    }

    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.US)
    val timeLabel = dateFormat.format(Date(feedback.timestamp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: User Name, Rating & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = feedback.userName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = feedback.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "${feedback.userEmail} • $timeLabel",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(99.dp))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = feedback.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Stars
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(feedback.rating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = BinanceGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Message text
            Text(
                text = feedback.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 19.sp
            )

            // Official Admin Reply if present
            feedback.adminReply?.let { reply ->
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            BinanceGold.copy(alpha = 0.08f),
                            RoundedCornerShape(10.dp)
                        )
                        .border(1.dp, BinanceGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Official Admin Response:",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BinanceGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reply,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Admin Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change status triggers
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (feedback.status != FeedbackStatus.ACKNOWLEDGED) {
                        TextButton(onClick = { onStatusChange(FeedbackStatus.ACKNOWLEDGED) }) {
                            Text("Acknowledge", fontSize = 11.sp, color = AccentCyan)
                        }
                    }
                    if (feedback.status != FeedbackStatus.RESOLVED) {
                        TextButton(onClick = { onStatusChange(FeedbackStatus.RESOLVED) }) {
                            Text("Resolve", fontSize = 11.sp, color = SignalBuy)
                        }
                    }
                }

                // Reply button
                Button(
                    onClick = onOpenReply,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reply",
                        tint = BinanceGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (feedback.adminReply == null) "Reply" else "Edit Reply",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
