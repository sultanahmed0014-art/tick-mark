package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalSell

@Composable
fun AnimatedFactorMeter(
    value: Float, // -1.0 to 1.0
    modifier: Modifier = Modifier
) {
    val clamped = value.coerceIn(-1f, 1f)
    val animatedPos by animateFloatAsState(
        targetValue = (clamped + 1f) / 2f, // 0.0 to 1.0
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "meter_position"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .padding(vertical = 4.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val trackHeight = 6.dp.toPx()
            val trackY = (height - trackHeight) / 2

            // Gradient track
            val trackBrush = Brush.horizontalGradient(
                colors = listOf(
                    SignalSell.copy(alpha = 0.85f),
                    SignalHold.copy(alpha = 0.85f),
                    SignalBuy.copy(alpha = 0.85f)
                )
            )

            drawRoundRect(
                brush = trackBrush,
                topLeft = Offset(0f, trackY),
                size = Size(width, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2, trackHeight / 2)
            )

            // Center mark (Neutral zero)
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(width / 2, trackY - 2.dp.toPx()),
                end = Offset(width / 2, trackY + trackHeight + 2.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )

            // Animated thumb indicator
            val thumbX = width * animatedPos
            val thumbRadius = 6.5.dp.toPx()

            // Outer ring glow
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = thumbRadius + 1.5.dp.toPx(),
                center = Offset(thumbX, height / 2)
            )

            // Main thumb circle
            drawCircle(
                color = Color.White,
                radius = thumbRadius,
                center = Offset(thumbX, height / 2)
            )

            // Inner accent dot
            val dotColor = when {
                clamped > 0.22f -> SignalBuy
                clamped < -0.22f -> SignalSell
                else -> SignalHold
            }
            drawCircle(
                color = dotColor,
                radius = thumbRadius * 0.55f,
                center = Offset(thumbX, height / 2)
            )
        }
    }
}
