/*package com.example.desktopApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.desktopApp.ui.components.ClientsScreen


@Composable
fun MainScreen() {
    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("💻 Компьютеры", "👥 Клиенты")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Computer Club Manager") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Вкладки
            TabRow(selectedTabIndex = currentTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title, ) },
                        selected = currentTab == index,
                        onClick = { currentTab = index }
                    )
                }
            }

            // Содержимое вкладок
            when (currentTab) {
                0 -> ComputersScreen()
                1 -> ClientsScreen()
            }
        }
    }
}

@Composable
fun ComputersScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Управление компьютерами", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(16.dp))

        // TODO: Добавить функционал компьютеров
        Card(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PC-01", style = MaterialTheme.typography.h6)
                Text("Статус: Свободен")
                Button(onClick = { /* Старт сессии */ }) {
                    Text("Начать сессию")
                }
            }
        }
    }
}
*/
// DrawerContent можно удалить если используешь вкладки
package com.example.desktopApp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.desktopApp.ui.components.ClientsScreen
import com.example.desktopApp.ui.components.ComputersScreen
import com.example.desktopApp.ui.components.ServicesScreen
import com.example.desktopApp.ui.components.SessionsScreen

@Composable
fun MainScreen() {
    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("💻 Компьютеры", "👥 Клиенты", "🕐 Сессии", "🍕 Услуги")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Computer Club Manager") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Вкладки
            TabRow(selectedTabIndex = currentTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = currentTab == index,
                        onClick = { currentTab = index }
                    )
                }
            }

            // Содержимое вкладок
            when (currentTab) {
                0 -> ComputersScreen()
                1 -> ClientsScreen()
                2 -> SessionsScreen()
                3 -> ServicesScreen()
            }
        }
    }
}