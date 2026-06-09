package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun HologramWaves(
    modifier: Modifier = Modifier,
    isPulsing: Boolean = false,
    colorAccent: Color = Color(0xFF00F0FF),
    secondaryColor: Color = Color(0xFF7000FF),
    waveCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hologram_waves")
    
    val phaseShift1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_1"
    )

    val phaseShift2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (-2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_2"
    )

    val pulseMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        val width = size.width
        val height = size.height
        val midY = height / 2

        for (i in 0 until waveCount) {
            val path = Path()
            val phase = if (i % 2 == 0) phaseShift1 + (i * 0.5f) else phaseShift2 + (i * 0.5f)
            
            // Adjust amplitude based on state
            val baseAmplitude = (height * 0.25f) * (1f - (i * 0.25f))
            val amplitude = if (isPulsing) baseAmplitude * pulseMultiplier * 1.5f else baseAmplitude * pulseMultiplier

            val frequency = (2 * Math.PI / width) * (1.5 + (i * 0.5))

            path.moveTo(0f, midY)
            for (x in 0..width.toInt() step 5) {
                val y = midY + sin(x * frequency + phase) * amplitude
                path.lineTo(x.toFloat(), y.toFloat())
            }

            val strokeAlpha = 1f - (i * 0.3f)
            val strokeWidth = (3f - (i * 0.8f)).coerceAtLeast(1f)
            
            val waveBrush = Brush.linearGradient(
                colors = listOf(
                    colorAccent.copy(alpha = strokeAlpha),
                    secondaryColor.copy(alpha = strokeAlpha * 0.6f),
                    colorAccent.copy(alpha = strokeAlpha * 0.1f)
                )
            )

            drawPath(
                path = path,
                brush = waveBrush,
                style = Stroke(width = strokeWidth.dp.toPx())
            )
        }
    }
}
