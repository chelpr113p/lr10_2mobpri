package com.example.lr10_2instpo

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val TAG = "ErrorScreen"

suspend fun riskyOperation(success: Boolean): String {
    delay(1000)
    if (!success) {
        throw IllegalStateException("Операция не удалась")
    }
    return "Операция выполнена успешно"
}

fun riskyFlow(): Flow<String> = flow {
    emit("Шаг 1")
    delay(500)
    emit("Шаг 2")
    delay(500)
    throw RuntimeException("Ошибка на шаге 3!")
    emit("Шаг 3")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}

suspend fun safeOperation(success: Boolean): Result<String> {
    return try {
        delay(1000)
        if (!success) {
            Result.failure(IllegalStateException("Операция не удалась"))
        } else {
            Result.success("Операция выполнена успешно")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Лог ошибки: ${e.message}", e)
        Result.failure(e)
    }
}

@Composable
fun ErrorScreen() {
    var result by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        result?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        errorMessage?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Ошибка: $it",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(true)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Неизвестная ошибка"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Успешная операция")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(false)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Неизвестная ошибка"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Операция с ошибкой")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    riskyFlow().collect { value ->
                        result = value
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Flow с обработкой ошибок")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    val safeResult = safeOperation(false)
                    safeResult.fold(
                        onSuccess = { result = it },
                        onFailure = { errorMessage = it.message ?: "Неизвестная ошибка" }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Безопасная операция (Result)")
        }
    }
}