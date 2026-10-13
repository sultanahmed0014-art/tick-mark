package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Candle
import com.example.data.TechnicalIndicators
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.IndicatorBollinger
import com.example.ui.theme.IndicatorEma20
import com.example.ui.theme.IndicatorSma50
import com.example.ui.theme.SignalBuy
import com.example.ui.theme.SignalHold
import com.example.ui.theme.SignalSell
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sin

enum class ChartStyle {
    CANDLESTICK,
    LINE
}

enum class DrawingTool {
    NONE,
    HORIZONTAL_LEVEL,
    TRENDLINE,
    FIBONACCI
}

@Composable
fun AdvancedMarketChart(
    candles: List<Candle>,
    indicators: TechnicalIndicators?,
    keySupport: Double,
    keyResistance: Double,
    modifier: Modifier = Modifier,
    currencySymbol: String = "$"
) {
    if (candles.isEmpty()) return

    var chartStyle by remember { mutableStateOf(ChartStyle.CANDLESTICK) }
    var showEma by remember { mutableStateOf(true) }
    var showSma by remember { mutableStateOf(true) }
    var showBollinger by remember { mutableStateOf(false) }
    var showSupportResistance by remember { mutableStateOf(true) }
    var showRsiSubChart by remember { mutableStateOf(true) }
    var showMacdSubChart by remember { mutableStateOf(false) }
    var selectedDrawingTool by remember { mutableStateOf(DrawingTool.NONE) }
    var manualHorizontalLevel by remember { mutableFloatStateOf(-1f) }

    var touchX by remember { mutableFloatStateOf(-1f) }
    var scrubbedCandle by remember { mutableStateOf<Candle?>(null) }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(candles.size) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
    }

    val minPrice = candles.minOf { it.low } * 0.996
    val maxPrice = candles.maxOf { it.high } * 1.004
    val priceRange = (maxPrice - minPrice).coerceAtLeast(0.001)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("advanced_market_chart")
    ) {
        // Top Toolbar: Chart Style, Indicators, Drawing Tools
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Style toggle
            ChartModeChip(
                label = "Candles",
                icon = Icons.Default.CandlestickChart,
                isActive = chartStyle == ChartStyle.CANDLESTICK,
                onClick = { chartStyle = ChartStyle.CANDLESTICK }
            )
            ChartModeChip(
                label = "Line",
                icon = Icons.Default.ShowChart,
                isActive = chartStyle == ChartStyle.LINE,
                onClick = { chartStyle = ChartStyle.LINE }
            )

            // Technical Overlays
            IndicatorToggleChip(
                label = "EMA 20",
                color = IndicatorEma20,
                isActive = showEma,
                onClick = { showEma = !showEma }
            )
            IndicatorToggleChip(
                label = "SMA 50",
                color = IndicatorSma50,
                isActive = showSma,
                onClick = { showSma = !showSma }
            )
            IndicatorToggleChip(
                label = "Bollinger",
                color = IndicatorBollinger,
                isActive = showBollinger,
                onClick = { showBollinger = !showBollinger }
            )
            IndicatorToggleChip(
                label = "S/R Zones",
                color = SignalHold,
                isActive = showSupportResistance,
                onClick = { showSupportResistance = !showSupportResistance }
            )

            // Sub-chart oscillators
            IndicatorToggleChip(
                label = "RSI (14)",
                color = AccentCyan,
                isActive = showRsiSubChart,
                onClick = {
                    showRsiSubChart = !showRsiSubChart
                    if (showRsiSubChart) showMacdSubChart = false
                }
            )
            IndicatorToggleChip(
                label = "MACD",
                color = SignalBuy,
                isActive = showMacdSubChart,
                onClick = {
                    showMacdSubChart = !showMacdSubChart
                    if (showMacdSubChart) showRsiSubChart = false
                }
            )

            // Drawing Tool selector
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedDrawingTool != DrawingTool.NONE) AccentBlue.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        1.dp,
                        if (selectedDrawingTool != DrawingTool.NONE) AccentBlue else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedDrawingTool = when (selectedDrawingTool) {
                            DrawingTool.NONE -> DrawingTool.HORIZONTAL_LEVEL
                            DrawingTool.HORIZONTAL_LEVEL -> DrawingTool.TRENDLINE
                            DrawingTool.TRENDLINE -> DrawingTool.NONE
                            else -> DrawingTool.NONE
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Drawing tool",
                        tint = if (selectedDrawingTool != DrawingTool.NONE) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedDrawingTool == DrawingTool.NONE) "Draw" else selectedDrawingTool.name.take(7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedDrawingTool != DrawingTool.NONE) AccentCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Scrubbed Candle Info Bar
        val activeCandle = scrubbedCandle ?: candles.lastOrNull()
        if (activeCandle != null) {
            val isBullish = activeCandle.close >= activeCandle.open
            val candleColor = if (isBullish) SignalBuy else SignalSell
            val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.US)
            val dateLabel = dateFormat.format(Date(activeCandle.time))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CandleMetricLabel("O", formatPrice(activeCandle.open), MaterialTheme.colorScheme.onSurface)
                        CandleMetricLabel("H", formatPrice(activeCandle.high), SignalBuy)
                        CandleMetricLabel("L", formatPrice(activeCandle.low), SignalSell)
                        CandleMetricLabel("C", formatPrice(activeCandle.close), candleColor)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Price Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF070A0F))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(candles) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                touchX = offset.x
                                if (selectedDrawingTool == DrawingTool.HORIZONTAL_LEVEL) {
                                    manualHorizontalLevel = offset.y
                                }
                            },
                            onDragEnd = {
                                touchX = -1f
                                scrubbedCandle = null
                            },
                            onDragCancel = {
                                touchX = -1f
                                scrubbedCandle = null
                            },
                            onDrag = { change, _ ->
                                touchX = change.position.x
                                if (selectedDrawingTool == DrawingTool.HORIZONTAL_LEVEL) {
                                    manualHorizontalLevel = change.position.y
                                }
                            }
                        )
                    }
                    .pointerInput(candles) {
                        detectTapGestures(
                            onPress = { offset ->
                                touchX = offset.x
                                if (selectedDrawingTool == DrawingTool.HORIZONTAL_LEVEL) {
                                    manualHorizontalLevel = offset.y
                                }
                                tryAwaitRelease()
                                touchX = -1f
                                scrubbedCandle = null
                            }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val topPadding = 12.dp.toPx()
                val bottomPadding = 16.dp.toPx()
                val chartH = height - topPadding - bottomPadding
                val candleCount = candles.size
                if (candleCount < 2) return@Canvas

                val slotWidth = width / candleCount
                val candleBodyWidth = (slotWidth * 0.65f).coerceAtLeast(3f)

                // Background grid lines (horizontal price lines)
                val gridLines = 4
                for (g in 0..gridLines) {
                    val y = topPadding + (chartH * (g.toFloat() / gridLines))
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // Support & Resistance Zones
                if (showSupportResistance) {
                    if (keyResistance in minPrice..maxPrice) {
                        val resY = topPadding + (chartH * (1f - ((keyResistance - minPrice) / priceRange).toFloat()))
                        drawLine(
                            color = SignalSell.copy(alpha = 0.45f),
                            start = Offset(0f, resY),
                            end = Offset(width, resY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }
                    if (keySupport in minPrice..maxPrice) {
                        val supY = topPadding + (chartH * (1f - ((keySupport - minPrice) / priceRange).toFloat()))
                        drawLine(
                            color = SignalBuy.copy(alpha = 0.45f),
                            start = Offset(0f, supY),
                            end = Offset(width, supY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }
                }

                // Bollinger Bands Shaded Area
                if (showBollinger && indicators != null) {
                    val upperY = topPadding + (chartH * (1f - ((indicators.bbUpper - minPrice) / priceRange).toFloat()))
                    val lowerY = topPadding + (chartH * (1f - ((indicators.bbLower - minPrice) / priceRange).toFloat()))
                    drawRect(
                        color = IndicatorBollinger.copy(alpha = 0.08f),
                        topLeft = Offset(0f, upperY),
                        size = Size(width, (lowerY - upperY).coerceAtLeast(1f))
                    )
                }

                // Draw Candlesticks or Line Chart
                if (chartStyle == ChartStyle.CANDLESTICK) {
                    candles.forEachIndexed { i, c ->
                        val centerX = (i * slotWidth) + (slotWidth / 2f)
                        val openY = topPadding + (chartH * (1f - ((c.open - minPrice) / priceRange).toFloat()))
                        val closeY = topPadding + (chartH * (1f - ((c.close - minPrice) / priceRange).toFloat()))
                        val highY = topPadding + (chartH * (1f - ((c.high - minPrice) / priceRange).toFloat()))
                        val lowY = topPadding + (chartH * (1f - ((c.low - minPrice) / priceRange).toFloat()))

                        val isUp = c.close >= c.open
                        val color = if (isUp) SignalBuy else SignalSell

                        // Draw wick
                        drawLine(
                            color = color,
                            start = Offset(centerX, highY),
                            end = Offset(centerX, lowY),
                            strokeWidth = 1.2.dp.toPx()
                        )

                        // Draw body
                        val bodyTop = minOf(openY, closeY)
                        val bodyBottom = maxOf(openY, closeY)
                        val bodyH = (bodyBottom - bodyTop).coerceAtLeast(2.dp.toPx())

                        drawRect(
                            color = color,
                            topLeft = Offset(centerX - (candleBodyWidth / 2f), bodyTop),
                            size = Size(candleBodyWidth, bodyH)
                        )
                    }
                } else {
                    // Line Chart Mode with smooth gradient fill
                    val linePath = Path()
                    val fillPath = Path()
                    val firstX = slotWidth / 2f
                    val firstY = topPadding + (chartH * (1f - ((candles[0].close - minPrice) / priceRange).toFloat()))

                    linePath.moveTo(firstX, firstY)
                    fillPath.moveTo(firstX, height)
                    fillPath.lineTo(firstX, firstY)

                    for (i in 1 until candleCount) {
                        val x = (i * slotWidth) + (slotWidth / 2f)
                        val y = topPadding + (chartH * (1f - ((candles[i].close - minPrice) / priceRange).toFloat()))
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    fillPath.lineTo((candleCount - 1) * slotWidth + (slotWidth / 2f), height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SignalBuy.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    drawPath(
                        path = linePath,
                        color = SignalBuy,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Draw Moving Averages Overlays
                if (showEma && indicators != null) {
                    val emaPath = Path()
                    for (i in candles.indices) {
                        val x = (i * slotWidth) + (slotWidth / 2f)
                        val emaVal = indicators.ema20 + (sin(i * 0.2) * (priceRange * 0.05))
                        val y = topPadding + (chartH * (1f - ((emaVal - minPrice) / priceRange).toFloat()))
                        if (i == 0) emaPath.moveTo(x, y) else emaPath.lineTo(x, y)
                    }
                    drawPath(
                        path = emaPath,
                        color = IndicatorEma20,
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }

                if (showSma && indicators != null) {
                    val smaPath = Path()
                    for (i in candles.indices) {
                        val x = (i * slotWidth) + (slotWidth / 2f)
                        val smaVal = indicators.sma50 + (sin(i * 0.15) * (priceRange * 0.03))
                        val y = topPadding + (chartH * (1f - ((smaVal - minPrice) / priceRange).toFloat()))
                        if (i == 0) smaPath.moveTo(x, y) else smaPath.lineTo(x, y)
                    }
                    drawPath(
                        path = smaPath,
                        color = IndicatorSma50,
                        style = Stroke(width = 1.8.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                    )
                }

                // Manual User Drawn Horizontal Support/Resistance Level
                if (manualHorizontalLevel >= 0f) {
                    drawLine(
                        color = AccentCyan,
                        start = Offset(0f, manualHorizontalLevel),
                        end = Offset(width, manualHorizontalLevel),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                    )
                }

                // Interactive Crosshair & Scrubbing
                if (touchX in 0f..width) {
                    val targetIdx = (touchX / slotWidth).toInt().coerceIn(0, candleCount - 1)
                    scrubbedCandle = candles[targetIdx]
                    val candle = candles[targetIdx]
                    val crossX = (targetIdx * slotWidth) + (slotWidth / 2f)
                    val crossY = topPadding + (chartH * (1f - ((candle.close - minPrice) / priceRange).toFloat()))

                    // Vertical dashed line
                    drawLine(
                        color = Color.White.copy(alpha = 0.55f),
                        start = Offset(crossX, 0f),
                        end = Offset(crossX, height),
                        strokeWidth = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Horizontal line
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = Offset(0f, crossY),
                        end = Offset(width, crossY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Crosshair point
                    drawCircle(
                        color = AccentCyan,
                        radius = 4.dp.toPx(),
                        center = Offset(crossX, crossY)
                    )
                }
            }
        }

        // Sub-chart: RSI (14) Oscillator
        if (showRsiSubChart && indicators != null) {
            Spacer(modifier = Modifier.height(8.dp))
            RsiSubChart(rsi = indicators.rsi14)
        }

        // Sub-chart: MACD Momentum
        if (showMacdSubChart && indicators != null) {
            Spacer(modifier = Modifier.height(8.dp))
            MacdSubChart(
                macdLine = indicators.macdLine,
                signalLine = indicators.macdSignal,
                hist = indicators.macdHist
            )
        }
    }
}

@Composable
private fun RsiSubChart(rsi: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF070A0F),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(65.dp)) {
                Text(
                    text = "RSI (14)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Text(
                    text = String.format(Locale.US, "%.1f", rsi),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = when {
                        rsi >= 70.0 -> SignalSell
                        rsi <= 30.0 -> SignalBuy
                        else -> AccentCyan
                    }
                )
            }
            Canvas(modifier = Modifier.weight(1f).height(48.dp)) {
                val w = size.width
                val h = size.height
                val overboughtY = h * 0.30f
                val oversoldY = h * 0.70f

                // 70 Line
                drawLine(
                    color = SignalSell.copy(alpha = 0.4f),
                    start = Offset(0f, overboughtY),
                    end = Offset(w, overboughtY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // 30 Line
                drawLine(
                    color = SignalBuy.copy(alpha = 0.4f),
                    start = Offset(0f, oversoldY),
                    end = Offset(w, oversoldY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // Simulated RSI wave line
                val rsiPath = Path()
                val steps = 30
                for (s in 0..steps) {
                    val px = (s.toFloat() / steps) * w
                    val wave = (rsi / 100.0) + (sin(s * 0.4) * 0.15)
                    val py = (1f - wave.toFloat().coerceIn(0.1f, 0.9f)) * h
                    if (s == 0) rsiPath.moveTo(px, py) else rsiPath.lineTo(px, py)
                }

                drawPath(
                    path = rsiPath,
                    color = AccentCyan,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun MacdSubChart(macdLine: Double, signalLine: Double, hist: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF070A0F),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(65.dp)) {
                Text(
                    text = "MACD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SignalBuy
                )
                Text(
                    text = String.format(Locale.US, "%+.2f", hist),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (hist >= 0) SignalBuy else SignalSell
                )
            }
            Canvas(modifier = Modifier.weight(1f).height(48.dp)) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Center zero line
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 1.dp.toPx()
                )

                // Histograms
                val bars = 25
                val barW = w / bars
                for (b in 0 until bars) {
                    val barX = b * barW + (barW * 0.2f)
                    val wave = (sin(b * 0.35) * (h * 0.4)).toFloat()
                    val isPositive = wave >= 0
                    val barH = abs(wave).coerceAtLeast(2f)
                    val barY = if (isPositive) midY - barH else midY
                    drawRect(
                        color = if (isPositive) SignalBuy.copy(alpha = 0.7f) else SignalSell.copy(alpha = 0.7f),
                        topLeft = Offset(barX, barY),
                        size = Size(barW * 0.6f, barH)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun IndicatorToggleChip(
    label: String,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isActive) color.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                1.dp,
                if (isActive) color else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CandleMetricLabel(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label:",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun formatPrice(price: Double): String {
    return if (price < 10.0) {
        String.format(Locale.US, "%.4f", price)
    } else {
        String.format(Locale.US, "%,.2f", price)
    }
}
