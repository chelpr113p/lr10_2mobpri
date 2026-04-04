package com.example.lr10_2instpo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

fun numberFlow(): Flow<Int> = flow {
    for (i in 1..10) {
        delay(500)
        emit(i)
    }
}

fun transformedFlow(flow: Flow<Int>): Flow<Int> = flow
    .map { it * it }
    .filter { it % 2 == 0 }

fun errorFlow(): Flow<String> = flow {
    emit("Первое значение")
    delay(500)
    emit("Второе значение")
    delay(500)
    throw RuntimeException("Произошла ошибка!")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}

@Composable
fun FlowScreen() {
    var flowValues by remember { mutableStateOf<List<String>>(emptyList()) }
    var trigger by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var currentFlowType by remember { mutableStateOf<Flow<String>?>(null) }

    // ✅ CORRECT: LaunchedEffect для сбора Flow
    LaunchedEffect(trigger) {
        if (isRunning && currentFlowType != null) {
            currentFlowType?.collect { value ->
                flowValues = flowValues + value
            }
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(flowValues) { value ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { }
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                flowValues = emptyList()
                currentFlowType = numberFlow().map { "Число: $it" }
                isRunning = true
                trigger++
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить Flow")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                flowValues = emptyList()
                currentFlowType = transformedFlow(numberFlow()).map { "Квадрат четного: $it" }
                isRunning = true
                trigger++
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить преобразованный Flow")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                flowValues = emptyList()
                currentFlowType = errorFlow()
                isRunning = true
                trigger++
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить Flow с ошибкой")
        }
    }
}