package com.example.projecttracker.ui.screens.pomodoro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PomodoroScreen() {
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$minutes:$seconds",
            fontSize = 64.sp,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(onClick = { isRunning = true }, enabled = !isRunning) {
                Text("Start")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { isRunning = false }, enabled = isRunning) {
                Text("Pause")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                isRunning = false
                timeLeft = 25 * 60
            }) {
                Text("Reset")
            }
        }
    }
}
