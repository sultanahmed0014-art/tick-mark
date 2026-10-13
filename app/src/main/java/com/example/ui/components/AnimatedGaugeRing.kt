package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalSell

@Composable
fun AnimatedGaugeRing(
    targetScore: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    strokeWidth: Dp = 7.dp,
    overrideColor: Color? = null,
    suffix: String = ""
) {
    val animVal = remember { Animatable(0f) }
    LaunchedEffect(targetScore) {
        animVal.snapTo(0f)
        animVal.animateTo(
            targetValue = targetScore.toFloat(),
            animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
        )
    }

    val color = overrideColor ?: when {
        targetScore >= 75 -> SignalBuy
        targetScore >= 55 -> SignalHold
        else -> SignalSell
    }

    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = size.toPx() - strokePx
            val topLeft = strokePx / 2

            // Background track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Animated progress arc
            val sweep = (animVal.value / 100f) * 360f
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft),
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        val displayInt = animVal.value.toInt()
        Text(
            text = "$displayInt$suffix",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = (size.value * 0.30f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
