package com.example.desktopApp.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseManager {
    fun init() {
        try {
            // ПРАВИЛЬНЫЙ путь к БД
            val dbFile = File("database/DBcoursePr.db")
            println("Путь к БД: ${dbFile.absolutePath}")
            println("Файл БД существует: ${dbFile.exists()}")
            println("Размер файла: ${dbFile.length()} байт")

            if (!dbFile.exists()) {
                println("❌ Файл БД не найден!")
                // Покажем все файлы в папке для диагностики
                val projectDir = File(".")
                println("Файлы в проекте:")
                projectDir.walk().forEach { file ->
                    if (file.isFile && file.extension == "db") {
                        println("  - ${file.relativeTo(projectDir)}")
                    }
                }
                return
            }

            // Подключаемся к БД
            Database.connect(
                "jdbc:sqlite:database/DBcoursePr.db",  // ← ИСПРАВЛЕННЫЙ ПУТЬ
                driver = "org.sqlite.JDBC"
            )

            // Тестируем соединение
            transaction {
                try {
                    // Пробуем выполнить простой запрос
                    val clientCount = com.example.desktopApp.database.entities.Clients.selectAll().count()
                    println("✅ База данных подключена успешно!")
                    println("✅ Найдено клиентов: $clientCount")

                    // Покажем несколько клиентов для проверки
                    val clients = com.example.desktopApp.database.entities.Clients.selectAll().limit(3)
                    clients.forEach { row ->
                        println("   Клиент: ${row[com.example.desktopApp.database.entities.Clients.firstName]} ${row[com.example.desktopApp.database.entities.Clients.lastName]}")
                    }

                } catch (e: Exception) {
                    println("❌ Ошибка при тестировании БД: ${e.message}")
                    println("Проверь структуру таблиц в БД")
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            println("❌ Ошибка подключения к БД: ${e.message}")
            e.printStackTrace()
        }
    }
}