package com.example.lr10_2instpo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Composable
fun StateFlowScreen() {
    val counterStateFlow = remember { MutableStateFlow(0) }
    val counter: StateFlow<Int> = counterStateFlow.asStateFlow()
    val counterValue by counter.collectAsState()

    val isAutoIncrementingStateFlow = remember { MutableStateFlow(false) }
    val isAutoIncrementing: StateFlow<Boolean> = isAutoIncrementingStateFlow.asStateFlow()
    val isAutoIncrementingValue by isAutoIncrementing.collectAsState()

    val scope = rememberCoroutineScope()
    var autoIncrementJob by remember { mutableStateOf<Job?>(null) }

    fun increment() {
        counterStateFlow.value += 1
    }

    fun decrement() {
        counterStateFlow.value -= 1
    }

    fun reset() {
        counterStateFlow.value = 0
    }

    fun incrementBy(value: Int) {
        counterStateFlow.value += value
    }

    fun toggleAutoIncrement() {
        if (isAutoIncrementingValue) {
            isAutoIncrementingStateFlow.value = false
            autoIncrementJob?.cancel()
            autoIncrementJob = null
        } else {
            isAutoIncrementingStateFlow.value = true
            autoIncrementJob = scope.launch {
                while (true) {
                    delay(1000)
                    counterStateFlow.value += 1
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            autoIncrementJob?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = counterValue.toString(),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (isAutoIncrementingValue) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 8.dp)
                )
                Text("Автоинкремент активен")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(onClick = { decrement() }) {
                Text("-1")
            }
            Button(onClick = { increment() }) {
                Text("+1")
            }
        }

        Button(
            onClick = { reset() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text("Сброс")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { incrementBy(5) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+5")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { toggleAutoIncrement() },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isAutoIncrementingValue)
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            else
                ButtonDefaults.buttonColors()
        ) {
            Text(if (isAutoIncrementingValue) "Остановить автоинкремент" else "Запустить автоинкремент")
        }
    }
}