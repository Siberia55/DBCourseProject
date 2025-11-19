package com.example.desktopApp.ui.components

import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.desktopApp.database.dao.ComputerDao
import com.example.desktopApp.database.entities.Computer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.desktopApp.database.dao.ClientDao
import com.example.desktopApp.database.entities.Client
import java.time.LocalDate
import org.jetbrains.exposed.sql.Column
/*
@Composable
fun ComputersScreen() {
    var computers by remember { mutableStateOf(emptyList<Computer>()) }

    LaunchedEffect(Unit) {
        computers = ComputerDao.getAllComputers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Управление компьютерами", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(16.dp))

        // Список компьютеров из БД
        LazyColumn {
            items(computers) { computer ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = 4.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(computer.name, style = MaterialTheme.typography.h6)
                        Text("Характеристики: ${computer.specifications ?: "не указаны"}")
                        Text("Тариф: ${computer.hourlyRate} руб/час")
                        Text("Статус: ${if (computer.isActive) "Активен" else "Неактивен"}")
                        Button(onClick = { /* Старт сессии */ }) {
                            Text("Начать сессию")
                        }
                    }
                }
            }
        }
    }
}*/
@Composable
fun ComputersScreen() {
    var computers by remember { mutableStateOf(emptyList<Computer>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingComputer by remember { mutableStateOf<Computer?>(null) }

    LaunchedEffect(Unit) {
        computers = ComputerDao.getAllComputers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Заголовок и кнопка добавления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Управление компьютерами", style = MaterialTheme.typography.h4)
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
                Spacer(Modifier.width(8.dp))
                Text("Добавить компьютер")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Таблица компьютеров
        ComputerTable(
            computers = computers,
            onEditClick = { computer -> editingComputer = computer },
            onDeleteClick = { computer ->
                ComputerDao.deleteComputer(computer.id)
                computers = computers.filter { it.id != computer.id }
            }
        )
    }

    // Диалог добавления/редактирования
    if (showAddDialog || editingComputer != null) {
        ComputerEditDialog(
            computer = editingComputer,
            onSave = { computer ->
                if (editingComputer == null) {
                    val newId = ComputerDao.addComputer(computer)
                    computers = computers + computer.copy(id = newId)
                } else {
                    ComputerDao.updateComputer(computer)
                    computers = computers.map { if (it.id == computer.id) computer else it }
                }
                showAddDialog = false
                editingComputer = null
            },
            onDismiss = {
                showAddDialog = false
                editingComputer = null
            }
        )
    }
}

@Composable
fun ComputerTable(
    computers: List<Computer>,
    onEditClick: (Computer) -> Unit,
    onDeleteClick: (Computer) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = 4.dp
    ) {
        Column {
            // Заголовки таблицы
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID", style = MaterialTheme.typography.h6, modifier = Modifier.weight(1f))
                Text("Название", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Характеристики", style = MaterialTheme.typography.h6, modifier = Modifier.weight(3f))
                Text("Тариф", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Статус", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Действия", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
            }

            Divider()

            // Список компьютеров
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(computers) { computer ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${computer.id}", modifier = Modifier.weight(1f))
                        Text(computer.name, modifier = Modifier.weight(2f))
                        Text(computer.specifications ?: "-", modifier = Modifier.weight(3f))
                        Text("${computer.hourlyRate} руб/час", modifier = Modifier.weight(2f))
                        Text(if (computer.isActive) "Активен" else "Неактивен", modifier = Modifier.weight(2f))

                        Row(modifier = Modifier.weight(2f)) {
                            IconButton(onClick = { onEditClick(computer) }) {
                                Icon(Icons.Default.Edit, "Редактировать")
                            }
                            IconButton(onClick = { onDeleteClick(computer) }) {
                                Icon(Icons.Default.Delete, "Удалить")
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ComputerEditDialog(
    computer: Computer?,
    onSave: (Computer) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(computer?.name ?: "") }
    var specifications by remember { mutableStateOf(computer?.specifications ?: "") }
    var hourlyRate by remember { mutableStateOf(computer?.hourlyRate?.toString() ?: "") }
    var isActive by remember { mutableStateOf(computer?.isActive ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (computer == null) "Добавить компьютер" else "Редактировать компьютер",
                    style = MaterialTheme.typography.h5
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = specifications,
                    onValueChange = { specifications = it },
                    label = { Text("Характеристики") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = hourlyRate,
                    onValueChange = { hourlyRate = it },
                    label = { Text("Тариф (руб/час) *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text("Компьютер активен")
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newComputer = Computer(
                                id = computer?.id ?: 0,
                                name = name,
                                specifications = if (specifications.isBlank()) null else specifications,
                                hourlyRate = hourlyRate.toBigDecimal(),
                                isActive = isActive
                            )
                            onSave(newComputer)
                        },
                        enabled = name.isNotBlank() && hourlyRate.isNotBlank()
                    ) {
                        Text(if (computer == null) "Добавить" else "Сохранить")
                    }
                }
            }
        }
    }
}