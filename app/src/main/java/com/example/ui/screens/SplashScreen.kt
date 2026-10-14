package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrightWhite
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElegantGrey
import com.example.ui.theme.GoldCrown
import com.example.ui.theme.TechPurple
import com.example.ui.theme.VibrantRed
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isOnline: Boolean,
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.05f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onSplashFinished() }
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        // Ambient Cyber Glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TechPurple.copy(alpha = glowAlpha * 0.45f),
                            ElectricBlue.copy(alpha = glowAlpha * 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Logo Image with Crown and Neon Frame
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricBlue, TechPurple, VibrantRed)
                        )
                    )
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                        .background(CinemaBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_er_tok_logo),
                        contentDescription = "Logótipo EDMAR E RONALDO TOK",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Name
            Text(
                text = "EDMAR E RONALDO",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrightWhite,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RD TOK",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🔥",
                    fontSize = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slogan
            Text(
                text = "A TUA DIVERSÃO NÃO TEM LIMITES!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VibrantRed,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ASSISTE • CURTE • PARTILHA 🇦🇴",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ElegantGrey,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Animated loading bar
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricBlue,
                trackColor = CinemaBlack
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Network status notice
            Text(
                text = if (isOnline) "A ligar à rede oficial..." else "Sem internet — A iniciar Modo Offline",
                color = if (isOnline) ElegantGrey else GoldCrown,
                fontSize = 11.sp
            )
        }
    }
}
