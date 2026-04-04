package com.example.lr10_2instpo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    var showMenu by remember { mutableStateOf(false) }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Лабораторная 10.2 Выполнили Челяпов и Кузнецов") },
                                actions = {
                                    IconButton(onClick = { showMenu = true }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = "coroutines",
                            modifier = Modifier.padding(padding)
                        ) {
                            composable("coroutines") { CoroutinesScreen() }
                            composable("flow") { FlowScreen() }
                            composable("stateflow") { StateFlowScreen() }
                            composable("sharedflow") { SharedFlowScreen() }
                            composable("errors") { ErrorScreen() }
                        }

                        if (showMenu) {
                            AlertDialog(
                                onDismissRequest = { showMenu = false },
                                title = { Text("Перейти к:") },
                                text = {
                                    Column {
                                        Text("Корутины", modifier = Modifier.clickable {
                                            navController.navigate("coroutines");
                                            showMenu = false
                                        })
                                        Text("Flow", modifier = Modifier.clickable {
                                            navController.navigate("flow");
                                            showMenu = false
                                        })
                                        Text("StateFlow", modifier = Modifier.clickable {
                                            navController.navigate("stateflow");
                                            showMenu = false
                                        })
                                        Text("SharedFlow", modifier = Modifier.clickable {
                                            navController.navigate("sharedflow");
                                            showMenu = false
                                        })
                                        Text("Ошибки", modifier = Modifier.clickable {
                                            navController.navigate("errors");
                                            showMenu = false
                                        })
                                    }
                                },
                                confirmButton = {}
                            )
                        }
                    }
                }
            }
        }
    }
}