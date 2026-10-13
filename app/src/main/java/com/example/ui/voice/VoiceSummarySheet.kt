package com.example.ui.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.BlueChip
import com.example.data.Signal
import com.example.data.UserProfile
import com.example.data.WatchlistItem
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BinanceGold
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalSell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSummarySheet(
    voiceAssistant: VoiceAssistantManager,
    signals: List<Signal>,
    blueChips: List<BlueChip>,
    watchlist: List<WatchlistItem>,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    val context = LocalContext.current
    val voiceState by voiceAssistant.voiceState.collectAsState()
    val transcript by voiceAssistant.spokenTranscript.collectAsState()
    val soundLevel by voiceAssistant.soundLevel.collectAsState()
    val summaryResult by voiceAssistant.currentResult.collectAsState()
    val errorMessage by voiceAssistant.errorMessage.collectAsState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            voiceAssistant.startListening()
        }
    }

    // Process recognized speech when state becomes PROCESSING
    LaunchedEffect(voiceState, transcript) {
        if (voiceState == VoiceState.PROCESSING && transcript.isNotBlank()) {
            voiceAssistant.processQuery(
                query = transcript,
                signals = signals,
                blueChips = blueChips,
                watchlist = watchlist,
                userProfile = userProfile
            )
        }
    }

    // Cleanup when dismissed
    DisposableEffect(Unit) {
        onDispose {
            voiceAssistant.stopListening()
            voiceAssistant.stopSpeaking()
        }
    }

    val quickQueries = listOf(
        "Summarize recent signals",
        "Pakistan PSX briefing",
        "Analyze Engro (ENGRO)",
        "What's my portfolio status?",
        "Analyze Nvidia (NVDA)",
        "Analyze Bitcoin (BTC)",
        "10-Year Blue-Chip briefing"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("voice_summary_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(BinanceGold.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = BinanceGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vocal Intelligence Briefing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Executive audio briefings for Admin ${userProfile.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Microphone Visualizer Orb
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "mic_orb")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) 1.35f else 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse_scale"
                )

                val activeColor = when (voiceState) {
                    VoiceState.LISTENING -> SignalBuy
                    VoiceState.SPEAKING -> AccentCyan
                    VoiceState.PROCESSING -> BinanceGold
                    VoiceState.ERROR -> SignalSell
                    else -> BinanceGold
                }

                // Glowing outer halo
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(activeColor.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Mid ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(1f + soundLevel * 0.4f)
                        .background(activeColor.copy(alpha = 0.2f), shape = CircleShape)
                        .border(1.5.dp, activeColor.copy(alpha = 0.5f), CircleShape)
                )

                // Center Mic Action Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(activeColor)
                        .clickable {
                            if (!hasMicPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                when (voiceState) {
                                    VoiceState.LISTENING -> voiceAssistant.stopListening()
                                    VoiceState.SPEAKING -> voiceAssistant.stopSpeaking()
                                    else -> voiceAssistant.startListening()
                                }
                            }
                        }
                        .testTag("mic_action_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (voiceState) {
                            VoiceState.LISTENING -> Icons.Default.Mic
                            VoiceState.SPEAKING -> Icons.Default.VolumeUp
                            else -> Icons.Default.Mic
                        },
                        contentDescription = "Microphone",
                        tint = Color(0xFF0B0E14),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Voice State Status Label
            val statusText = when (voiceState) {
                VoiceState.LISTENING -> "Listening to your voice... Speak now"
                VoiceState.PROCESSING -> "Synthesizing vocal intelligence briefing..."
                VoiceState.SPEAKING -> "Speaking audio summary... Tap mic to mute"
                VoiceState.ERROR -> errorMessage ?: "Tap mic to retry voice search"
                else -> if (hasMicPermission) "Tap the microphone to ask a question" else "Tap microphone to grant voice permission"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (voiceState == VoiceState.ERROR) SignalSell else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Transcript text display
            if (transcript.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\"$transcript\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spoken Briefing Result Card
            summaryResult?.let { res ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BinanceGold.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = res.visualSummaryTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row {
                                if (voiceState == VoiceState.SPEAKING) {
                                    IconButton(
                                        onClick = { voiceAssistant.stopSpeaking() },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeOff,
                                            contentDescription = "Mute",
                                            tint = SignalSell,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { voiceAssistant.replaySummary() },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay,
                                            contentDescription = "Replay Audio",
                                            tint = BinanceGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key summary takeaways
                        res.keyPoints.forEach { point ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    color = BinanceGold,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Full Spoken Script Expandable view
                        Text(
                            text = res.vocalScript,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Query Suggestions Chips
            Text(
                text = "OR TAP A QUICK QUERY",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickQueries.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
                            .clickable {
                                voiceAssistant.processQuery(
                                    query = prompt,
                                    signals = signals,
                                    blueChips = blueChips,
                                    watchlist = watchlist,
                                    userProfile = userProfile
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
