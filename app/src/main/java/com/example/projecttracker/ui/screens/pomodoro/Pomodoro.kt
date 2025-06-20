package com.example.projecttracker.ui.screens.pomodoro

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PomodoroScreen() {
    var timeLeft by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val totalTime = 25 * 60f
    val progress by animateFloatAsState(targetValue = timeLeft / totalTime)

    // Access colors outside the Canvas draw scope
    val backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val progressColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(isRunning) {
        if (isRunning) {
            job = coroutineScope.launch {
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft--
                }
                isRunning = false
            }
        } else {
            job?.cancel()
        }
    }

    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular Progress Bar with Timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(250.dp)
            ) {
                Canvas(modifier = Modifier.size(250.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Background circle
                    drawArc(
                        color = backgroundColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progress arc
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$minutes:$seconds",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { isRunning = true },
                    enabled = !isRunning,
                    modifier = Modifier
                        .height(48.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = progressColor
                    )
                ) {
                    Text("Start", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { isRunning = false },
                    enabled = isRunning,
                    modifier = Modifier
                        .height(48.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = progressColor
                    )
                ) {
                    Text("Pause", fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        timeLeft = 25 * 60
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = progressColor
                    )
                ) {
                    Text("Reset", fontSize = 16.sp)
                }
            }
        }
    }
}