package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * A highly interactive, visually striking holographic Anime-style AI Avatar.
 * Intricately crafted with Canvas curves to render hair, cybernetic glowing visor glasses, 
 * digital orbital shields, and ambient visual tracking metrics.
 */
@Composable
fun AnimeAvatar(
    modifier: Modifier = Modifier,
    isGenerating: Boolean = false,
    isListening: Boolean = false,
    currentLanguageName: String = "Bangla + English",
    onToggleLanguage: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "anime_avatar")

    // Slow breath animation for hair and visor swaying
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_breath"
    )

    // VISOR GLOW pulse
    val visorGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "visor_glow"
    )

    // Spin animation for background futuristic target indicators
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cyber_ring_rotation"
    )

    // Dynamic wave values for voice amplitude simulation
    val systemResponseWave by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "audio_ripple"
    )

    var speechBubbleText by remember { mutableStateOf("Ready to deploy codes, Sir. / আমি প্রস্তুত, স্যার।") }
    val speechBubbles = listOf(
        "I am scanning Web tutorials to optimize layouts. / আমি কোডিং টিউটোরিয়াল বিশ্লেষণ করছি।",
        "SEO parameters verified. Lighthouse score is ideal. / এসইও অপ্টিমাইজেশন সম্পন্ন হয়েছে!",
        "Double-tap custom parameters in the Vault to configure. / প্যারামিটার ইনজেক্ট করতে ডাবল ট্যাপ করুন।",
        "Bengali honorifics fully loaded, ready to serve, sir! / বাংলা ও ইংরেজি দুই ভাষাতেই আমি প্রস্তুত, স্যার!",
        "Awaiting instruction parameters... / আপনার নির্দেশের অপেক্ষায় আছি, স্যার।"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x2200F0FF),
                        Color(0x117000FF),
                        Color(0x050F1115)
                    )
                )
            )
            .border(
                width = 0.8.dp,
                color = Color(0x3300F0FF),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                // Pick a new interactive speech nugget
                speechBubbleText = speechBubbles.random()
                onAvatarClick()
            }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header for Avatar HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00F0FF))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "JARVIS SYNTHETIC AVATAR v1.0",
                        color = Color(0xAA00F0FF),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x227000FF))
                        .border(0.5.dp, Color(0xFF7000FF), RoundedCornerShape(4.dp))
                        .clickable { onToggleLanguage() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = currentLanguageName.uppercase(),
                        color = Color(0xFF00F0FF),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // High aesthetic anime-style drawing inside Canvas
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val center = Offset(width / 2, height / 2)

                    // 1. Draw glowing rotating cyber rings
                    withTransform({
                        rotate(rotationAngle, center)
                    }) {
                        // Outer tracking ring
                        drawCircle(
                            color = Color(0xFF7000FF).copy(alpha = 0.2f),
                            radius = (width / 2.2f) * breatheScale,
                            style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 24f), 0f))
                        )
                        // Inner telemetry compass ring
                        drawCircle(
                            color = Color(0xFF00F0FF).copy(alpha = 0.25f),
                            radius = (width / 2.5f) * breatheScale,
                            style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 15f, 5f, 15f), 0f))
                        )
                    }

                    // Static data nodes
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.5f),
                        radius = 4.dp.toPx(),
                        center = Offset(center.x - width / 3, center.y - height / 5)
                    )
                    drawCircle(
                        color = Color(0xFF00F0FF).copy(alpha = 0.5f),
                        radius = 3.dp.toPx(),
                        center = Offset(center.x + width / 3.2f, center.y + height / 6)
                    )

                    // Draw connection grid lines
                    drawLine(
                        color = Color(0x3300F0FF),
                        start = Offset(center.x - width / 3, center.y - height / 5),
                        end = Offset(center.x - width / 4.5f, center.y - height / 6),
                        strokeWidth = 1f
                    )

                    // Base face/neck holographic layout
                    val headRadius = width * 0.16f
                    val neckTopY = center.y + headRadius * 0.5f
                    val neckBottomY = center.y + headRadius * 1.3f

                    // Cyber collar shoulder glow
                    val collarPath = Path().apply {
                        moveTo(center.x - headRadius * 1.5f, neckBottomY + 15f)
                        quadraticTo(
                            center.x, neckBottomY,
                            center.x + headRadius * 1.5f, neckBottomY + 15f
                        )
                        lineTo(center.x + headRadius * 1.2f, neckBottomY + 30f)
                        lineTo(center.x - headRadius * 1.2f, neckBottomY + 30f)
                        close()
                    }
                    drawPath(
                        path = collarPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x667000FF), Color(0x1100F0FF))
                        )
                    )

                    // Neck
                    drawRect(
                        color = Color(0xFF0F1115).copy(alpha = 0.8f),
                        topLeft = Offset(center.x - headRadius * 0.3f, neckTopY),
                        size = Size(headRadius * 0.6f, neckBottomY - neckTopY)
                    )
                    drawRect(
                        color = Color(0xFF00F0FF).copy(alpha = 0.4f),
                        topLeft = Offset(center.x - headRadius * 0.3f, neckTopY),
                        size = Size(headRadius * 0.6f, neckBottomY - neckTopY),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Anime style chin and face mask outline
                    val facePath = Path().apply {
                        moveTo(center.x - headRadius, center.y - headRadius * 0.3f)
                        lineTo(center.x - headRadius * 0.9f, center.y + headRadius * 0.4f)
                        // Dynamic pointed chin
                        lineTo(center.x, center.y + headRadius * 1.1f)
                        lineTo(center.x + headRadius * 0.9f, center.y + headRadius * 0.4f)
                        lineTo(center.x + headRadius, center.y - headRadius * 0.3f)
                        quadraticTo(
                            center.x, center.y - headRadius * 0.5f,
                            center.x - headRadius, center.y - headRadius * 0.3f
                        )
                    }
                    drawPath(
                        path = facePath,
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1B1B1F), Color(0xFF0F1115)),
                            center = center,
                            radius = headRadius
                        )
                    )
                    drawPath(
                        path = facePath,
                        color = Color(0xFF00F0FF).copy(alpha = 0.6f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // 2. Beautiful Cyber Anime Visor Glasses (Glow highlights)
                    val visorHeight = headRadius * 0.4f
                    val visorWidth = headRadius * 1.6f
                    val visorTopY = center.y - visorHeight * 0.6f

                    val visorPath = Path().apply {
                        moveTo(center.x - visorWidth / 2, visorTopY)
                        lineTo(center.x + visorWidth / 2, visorTopY)
                        lineTo(center.x + visorWidth / 2 - 10f, visorTopY + visorHeight)
                        lineTo(center.x - visorWidth / 2 + 10f, visorTopY + visorHeight)
                        close()
                    }

                    // Visor filling
                    drawPath(
                        path = visorPath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00F0FF).copy(alpha = 0.7f * visorGlowAlpha),
                                Color(0xFF7000FF).copy(alpha = 0.8f * visorGlowAlpha),
                                Color(0xFF00F0FF).copy(alpha = 0.7f * visorGlowAlpha)
                            )
                        )
                    )
                    // Visor edge neon
                    drawPath(
                        path = visorPath,
                        color = Color(0xFF00F0FF),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Cyber target horizontal data line across visor
                    drawLine(
                        color = Color(0xFF10B981).copy(alpha = visorGlowAlpha),
                        start = Offset(center.x - visorWidth / 1.8f, visorTopY + visorHeight / 2),
                        end = Offset(center.x + visorWidth / 1.8f, visorTopY + visorHeight / 2),
                        strokeWidth = 2f
                    )
                    // Visual tracker ticks on glasses
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x - 10f, visorTopY),
                        end = Offset(center.x - 10f, visorTopY + 10f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x + 10f, visorTopY),
                        end = Offset(center.x + 10f, visorTopY + 10f),
                        strokeWidth = 3f
                    )

                    // 3. Cyber Anime layered Hair flow (Front & Side Bangs)
                    // Left hair flow path
                    val leftHairPath = Path().apply {
                        moveTo(center.x - headRadius * 0.8f, center.y - headRadius * 1.1f)
                        cubicTo(
                            center.x - headRadius * 1.4f, center.y - headRadius * 0.5f,
                            center.x - headRadius * 1.6f, center.y + headRadius * 0.8f,
                            center.x - headRadius * 1.1f, center.y + headRadius * 1.4f
                        )
                        lineTo(center.x - headRadius * 0.8f, center.y + headRadius * 0.6f)
                        close()
                    }
                    drawPath(
                        path = leftHairPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF7000FF), Color(0xFF00F0FF).copy(alpha = 0.3f))
                        )
                    )
                    drawPath(
                        path = leftHairPath,
                        color = Color(0xFF7000FF).copy(alpha = 0.5f),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Right hair flow path
                    val rightHairPath = Path().apply {
                        moveTo(center.x + headRadius * 0.8f, center.y - headRadius * 1.1f)
                        cubicTo(
                            center.x + headRadius * 1.4f, center.y - headRadius * 0.5f,
                            center.x + headRadius * 1.6f, center.y + headRadius * 0.8f,
                            center.x + headRadius * 1.1f, center.y + headRadius * 1.4f
                        )
                        lineTo(center.x + headRadius * 0.8f, center.y + headRadius * 0.6f)
                        close()
                    }
                    drawPath(
                        path = rightHairPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF7000FF), Color(0xFF00F0FF).copy(alpha = 0.3f))
                        )
                    )
                    drawPath(
                        path = rightHairPath,
                        color = Color(0xFF7000FF).copy(alpha = 0.5f),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Top anime hair spikes/hair tuft
                    val spikePath = Path().apply {
                        moveTo(center.x - headRadius * 0.6f, center.y - headRadius * 0.4f)
                        quadraticTo(
                            center.x - headRadius * 0.4f, center.y - headRadius * 1.4f * breatheScale,
                            center.x, center.y - headRadius * 1.6f * breatheScale
                        )
                        quadraticTo(
                            center.x + headRadius * 0.4f, center.y - headRadius * 1.4f * breatheScale,
                            center.x + headRadius * 0.6f, center.y - headRadius * 0.4f
                        )
                        quadraticTo(
                            center.x, center.y - headRadius * 0.8f,
                            center.x - headRadius * 0.6f, center.y - headRadius * 0.4f
                        )
                    }
                    drawPath(
                        path = spikePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF00F0FF), Color(0xFF7000FF))
                        )
                    )
                    drawPath(
                        path = spikePath,
                        color = Color(0xFF00F0FF).copy(alpha = 0.8f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Futuristic HUD parameters near head
                    val trackerY = center.y + headRadius * 1.5f
                    drawTextHUD(
                        this,
                        "AI CONFIDENCE : 99.8%",
                        center.x - width / 2.3f,
                        trackerY - 10f,
                        Color(0xFF10B981)
                    )
                    drawTextHUD(
                        this,
                        "SYSTEM STATE: " + if (isGenerating) "RUNNING" else "STANDBY",
                        center.x + width / 12f,
                        trackerY - 10f,
                        if (isGenerating) Color(0xFF7000FF) else Color(0xFF00F0FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Premium digital glass speech text panel
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x351B1B1F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Avatar State",
                        tint = Color(0xFF00F0FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = speechBubbleText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Utility to write custom futuristic text targets without complex native text measures
private fun drawTextHUD(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    text: String,
    x: Float,
    y: Float,
    color: Color
) {
    // Custom geometric design overlay matching futurism HUDs
    drawScope.drawRect(
        color = color.copy(alpha = 0.15f),
        topLeft = Offset(x, y - 10f),
        size = Size(100f, 15f)
    )
    drawScope.drawRect(
        color = color,
        topLeft = Offset(x, y - 10f),
        size = Size(2f, 15f)
    )
    // Draw telemetry dash indicators matching visual data plates
    drawScope.drawLine(
        color = color.copy(alpha = 0.4f),
        start = Offset(x, y + 8f),
        end = Offset(x + 120f, y + 8f),
        strokeWidth = 1f
    )
}
