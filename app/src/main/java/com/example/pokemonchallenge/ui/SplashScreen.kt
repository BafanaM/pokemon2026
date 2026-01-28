package com.example.pokemonchallenge.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.pokemonchallenge.R

/**
 * Splash screen composable with full-page Pikachu image and beautiful animations.
 * @param onSplashComplete Callback when splash animation is complete
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Fade in animation
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "Alpha Animation"
    )

    // Scale animation with bounce effect
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Scale Animation"
    )

    // Continuous gentle rotation
    val infiniteRotation = rememberInfiniteTransition(label = "Infinite Rotation")
    val rotationAnim by infiniteRotation.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Rotation Animation"
    )

    // Pulsing scale for extra life
    val pulseAnim by infiniteRotation.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse Animation"
    )

    // Bounce animation
    val bounceAnim = rememberInfiniteTransition(label = "Bounce")
    val bounceY by bounceAnim.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseOutBounce),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Bounce Animation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000L)
        onSplashComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Pikachu image with multiple animations
        Image(
            painter = painterResource(id = R.drawable.ic_ash),
            contentDescription = "Ash",
            modifier = Modifier
                .fillMaxSize(0.6f)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value * pulseAnim)
                .rotate(rotationAnim)
                .offset(y = bounceY.dp),
            contentScale = ContentScale.Fit
        )

        // "Pokemon" text at the top - positioned above image
        Text(
            text = "Pokemon",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color(0xFF1A1A1A), // Dark color for visibility
            textAlign = TextAlign.Center
        )
    }
}
