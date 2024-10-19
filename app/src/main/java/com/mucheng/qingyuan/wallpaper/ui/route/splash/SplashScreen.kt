package com.mucheng.qingyuan.wallpaper.ui.route.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Forest
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    popUpToMain: () -> Unit
) {
    val scaleAnimatable = remember { Animatable(0f) }
    LaunchedEffect(null) {
        scaleAnimatable.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessVeryLow)
        )
        delay(220L)
        popUpToMain()
    }
    Scaffold(
        Modifier
            .fillMaxSize()
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.TwoTone.Forest,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .scale(scaleAnimatable.value)
                    .background(Color(0xFF79BD9A), CircleShape)
                    .size(168.dp)
                    .scale(0.5f)
                    .align(Alignment.Center)
            )
        }
    }
}