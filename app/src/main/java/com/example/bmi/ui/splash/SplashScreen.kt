package com.example.bmi.ui.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.bmi.R

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreen(onAnimationEnd = {})
}

@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit = {}
) {
    //初始状态，透明度为0，位移为100dp，旋转角度为0
    var startAnimation by remember {
        mutableStateOf(false)
    }

    //记录动画阶段
    var pointerStage by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }//启动动画

    val splashEasing = CubicBezierEasing(
        0.25f,
        0f,
        0.1f,
        0.1f
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        ),
        label = "alpha"
    )

    val translationY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 100.dp,
        animationSpec = tween(
            durationMillis = 1000,
            easing = splashEasing
        ),
        label = "translationY"
    )

    val pointerRotation by animateFloatAsState(
        targetValue = when (pointerStage) {
            0 -> 0f
            1 -> 40f
            else -> -40f
        },
        animationSpec = tween(
            durationMillis = 1000,
            easing = splashEasing
        ),
        finishedListener = {
            if (pointerStage == 1) {
                // 第一段旋转结束，进入第二段
                pointerStage = 2
            } else if (pointerStage == 2) {
                // 第二段旋转结束
                onAnimationEnd()
            }
        },
        label = "pointerRotation"
    )

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            pointerStage = 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3659CF))
            .navigationBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .offset(
                    x = 32.dp,
                    y = 293.5.dp
                )
                .width(73.dp)
                .height(53.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.splash6),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = translationY.toPx()
                    }


            )

            Image(
                painter = painterResource(R.drawable.splash3),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(23.dp)
                    .offset(y = 17.5.dp)
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = translationY.toPx()
                        rotationZ = pointerRotation

                        transformOrigin = TransformOrigin(
                            pivotFractionX = 0.5f,
                            pivotFractionY = 19f / 23f
                        )
                    }
            )
        }

        Image(
            painter = painterResource(R.drawable.splash4),
            contentDescription = null,
            modifier = Modifier
                .width(170.dp)
                .height(70.dp)
                .offset(
                    x = 32.dp,
                    y = 352.5.dp
                )
                .graphicsLayer {
                    this.alpha = alpha
                    this.translationY = translationY.toPx()
                }
        )

        Image(
            painter = painterResource(R.drawable.splash2),
            contentDescription = null,
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.4f)
        )
    }
}