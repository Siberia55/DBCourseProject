package com.example.desktopApp

import androidx.compose.ui.unit.dp
import com.example.desktopApp.database.DatabaseManager
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.example.desktopApp.ui.screens.MainScreen

fun main() = application {
    // Инициализация БД
    DatabaseManager.init()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Computer Club Manager",
        state = WindowState(width = 900.0.dp, height = 700.0.dp)
    ) {
        MainScreen(

        )
    }
}
