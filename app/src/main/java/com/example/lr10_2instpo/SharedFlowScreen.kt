package com.example.lr10_2instpo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

@Composable
fun SharedFlowScreen() {
    val eventsSharedFlow = remember { MutableSharedFlow<String>(replay = 3) }
    val eventsFlow: SharedFlow<String> = eventsSharedFlow.asSharedFlow()

    var events by remember { mutableStateOf<List<String>>(emptyList()) }
    var eventCount by remember { mutableStateOf(0) }
    var eventCounter by remember { mutableStateOf(0) }
    var isAutoGenerating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var autoGenerationJob by remember { mutableStateOf<Job?>(null) }

    // ✅ CORRECT: LaunchedEffect для сбора SharedFlow
    LaunchedEffect(Unit) {
        eventsFlow.collect { event ->
            events = (events + event).takeLast(10)
            eventCount++
        }
    }

    fun emitEvent(message: String) {
        scope.launch {
            eventsSharedFlow.emit(message)
        }
    }

    fun startAutoGeneration() {
        if (autoGenerationJob?.isActive == true) return

        isAutoGenerating = true
        autoGenerationJob = scope.launch {
            while (true) {
                delay(2000)
                eventCounter++
                val randomNumber = Random.nextInt(1, 101)
                emitEvent("Событие #$eventCounter: $randomNumber")
            }
        }
    }

    fun stopAutoGeneration() {
        isAutoGenerating = false
        autoGenerationJob?.cancel()
        autoGenerationJob = null
    }

    DisposableEffect(Unit) {
        onDispose {
            autoGenerationJob?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Всего событий: $eventCount",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(events.reversed()) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { }
                ) {
                    Text(
                        text = event,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emitEvent("Ручное событие #${eventCounter + 1}")
                eventCounter++
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сгенерировать событие")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isAutoGenerating) {
                    stopAutoGeneration()
                } else {
                    startAutoGeneration()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isAutoGenerating)
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            else
                ButtonDefaults.buttonColors()
        ) {
            Text(if (isAutoGenerating) "Остановить автогенерацию" else "Запустить автогенерацию")
        }
    }
}