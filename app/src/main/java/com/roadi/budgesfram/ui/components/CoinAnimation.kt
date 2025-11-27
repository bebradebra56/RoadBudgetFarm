package com.roadi.budgesfram.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roadi.budgesfram.ui.theme.GoldAccent
import kotlinx.coroutines.delay

@Composable
fun CoinAnimation(
    amount: Double,
    onAnimationComplete: () -> Unit
) {
    var showAnimation by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Show animation for 2 seconds
        showAnimation = false
        onAnimationComplete()
    }

    if (showAnimation) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Animated coins falling
            repeat(5) { index ->
                AnimatedCoin(index)
            }

            // Amount text overlay
            AmountTextOverlay(amount)
        }
    }
}

@Composable
private fun AnimatedCoin(index: Int) {
    val infiniteTransition = rememberInfiniteTransition()

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000 + index * 200,
                easing = EaseOutCubic
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val xOffset by infiniteTransition.animateFloat(
        initialValue = (index - 2) * 50f,
        targetValue = (index - 2) * 50f + (Math.random() * 100 - 50).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500 + index * 150,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000 + index * 100,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .offset(x = xOffset.dp, y = yOffset.dp)
            .size((30 + index * 5).dp)
            .background(GoldAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "💰",
            fontSize = (16 + index * 2).sp,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
private fun AmountTextOverlay(amount: Double) {
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.7f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$${String.format("%.2f", amount)}",
            color = GoldAccent,
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.scale(scale)
        )
    }
}
