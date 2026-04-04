package com.example.lr10_2instpo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.delay

suspend fun simulateLongOperation(duration: Long): String {
    delay(duration)
    return "Операция завершена за $duration мс"
}

suspend fun calculateSum(numbers: List<Int>): Int {
    return withContext(Dispatchers.Default) {
        delay(1000)
        numbers.sum()
    }
}

@Composable
fun CoroutinesScreen() {
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Выполняется операция...")
        }

        result?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    try {
                        val res = simulateLongOperation(2000)
                        result = res
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить долгую операцию")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    try {
                        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        val sum = calculateSum(numbers)
                        result = "Сумма чисел: $sum"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вычислить сумму")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.coroutineContext[Job]?.cancel()
        }
    }
}