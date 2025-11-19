package com.example.desktopApp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.desktopApp.database.dao.*
import com.example.desktopApp.database.entities.Session
import com.example.desktopApp.database.entities.Client
import com.example.desktopApp.database.entities.Computer
import java.time.format.DateTimeFormatter

@Composable
fun SessionsScreen() {
    var sessions by remember { mutableStateOf(emptyList<Session>()) }
    var clients by remember { mutableStateOf(emptyList<Client>()) }
    var computers by remember { mutableStateOf(emptyList<Computer>()) }
    var showStartSessionDialog by remember { mutableStateOf(false) }
    var showServicesDialog by remember { mutableStateOf<Session?>(null) }

    LaunchedEffect(Unit) {
        sessions = SessionDao.getAllSessions()
        clients = ClientDao.getAllClients()
        computers = ComputerDao.getAllComputers().filter { it.isActive }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Управление сессиями", style = MaterialTheme.typography.h4)
            Button(
                onClick = { showStartSessionDialog = true },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Начать сессию")
                Spacer(Modifier.width(8.dp))
                Text("Начать сессию")
            }
        }

        Spacer(Modifier.height(16.dp))

        SessionTable(
            sessions = sessions,
            clients = clients,
            computers = computers,
            onEndSession = { session ->
                SessionDao.endSession(session.sessionId)
                sessions = SessionDao.getAllSessions()
            },
            onManageServices = { session ->
                showServicesDialog = session
            }
        )
    }

    if (showStartSessionDialog) {
        StartSessionDialog(
            clients = clients,
            computers = computers,
            onStartSession = { clientId, computerId ->
                val sessionId = SessionDao.startSession(clientId, computerId)
                sessions = SessionDao.getAllSessions()
                showStartSessionDialog = false
            },
            onDismiss = { showStartSessionDialog = false }
        )
    }

    showServicesDialog?.let { session ->
        SessionServicesDialog(
            session = session,
            onClose = {
                showServicesDialog = null
                sessions = SessionDao.getAllSessions()
            }
        )
    }
}

@Composable
fun SessionTable(
    sessions: List<Session>,
    clients: List<Client>,
    computers: List<Computer>,
    onEndSession: (Session) -> Unit,
    onManageServices: (Session) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    Card(modifier = Modifier.fillMaxSize(), elevation = 4.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID", style = MaterialTheme.typography.h6, modifier = Modifier.weight(1f))
                Text("Клиент", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Компьютер", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Начало", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Окончание", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Стоимость", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Действия", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
            }

            Divider()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sessions) { session ->
                    val client = clients.find { it.id == session.clientId }
                    val computer = computers.find { it.id == session.computerId }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${session.sessionId}", modifier = Modifier.weight(1f))
                        Text(
                            "${client?.firstName ?: ""} ${client?.lastName ?: ""}",
                            modifier = Modifier.weight(2f)
                        )
                        Text(computer?.name ?: "-", modifier = Modifier.weight(2f))
                        Text(session.startTime.format(formatter), modifier = Modifier.weight(2f))
                        Text(
                            session.endTime?.format(formatter) ?: "Активна",
                            modifier = Modifier.weight(2f)
                        )
                        Text("${session.totalCost} руб", modifier = Modifier.weight(2f))

                        Row(modifier = Modifier.weight(2f)) {
                            if (session.endTime == null) {
                                IconButton(onClick = { onManageServices(session) }) {
                                    Icon(Icons.Default.ShoppingCart, "Услуги")
                                }
                                IconButton(onClick = { onEndSession(session) }) {
                                    Icon(Icons.Default.Close, "Завершить")
                                }
                            } else {
                                Text("Завершена", color = MaterialTheme.colors.secondary)
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
fun StartSessionDialog(
    clients: List<Client>,
    computers: List<Computer>,
    onStartSession: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedClientId by remember { mutableStateOf<Int?>(null) }
    var selectedComputerId by remember { mutableStateOf<Int?>(null) }
    var clientExpanded by remember { mutableStateOf(false) }
    var computerExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Начать новую сессию", style = MaterialTheme.typography.h5)

                Spacer(Modifier.height(16.dp))

                // Выбор клиента
                Box {
                    OutlinedTextField(
                        value = selectedClientId?.let { id ->
                            clients.find { it.id == id }?.let { "${it.firstName} ${it.lastName}" }
                        } ?: "",
                        onValueChange = {},
                        label = { Text("Клиент *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { clientExpanded = !clientExpanded }) {
                                Icon(
                                    if (clientExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    "Раскрыть список"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = clientExpanded,
                        onDismissRequest = { clientExpanded = false }
                    ) {
                        clients.forEach { client ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedClientId = client.id
                                    clientExpanded = false
                                }
                            ) {
                                Text("${client.firstName} ${client.lastName}")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Выбор компьютера
                Box {
                    OutlinedTextField(
                        value = selectedComputerId?.let { id ->
                            computers.find { it.id == id }?.name
                        } ?: "",
                        onValueChange = {},
                        label = { Text("Компьютер *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { computerExpanded = !computerExpanded }) {
                                Icon(
                                    if (computerExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    "Раскрыть список"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = computerExpanded,
                        onDismissRequest = { computerExpanded = false }
                    ) {
                        computers.forEach { computer ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedComputerId = computer.id
                                    computerExpanded = false
                                }
                            ) {
                                Text("${computer.name} - ${computer.hourlyRate} руб/час")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedClientId?.let { clientId ->
                                selectedComputerId?.let { computerId ->
                                    onStartSession(clientId, computerId)
                                }
                            }
                        },
                        enabled = selectedClientId != null && selectedComputerId != null
                    ) {
                        Text("Начать сессию")
                    }
                }
            }
        }
    }
}