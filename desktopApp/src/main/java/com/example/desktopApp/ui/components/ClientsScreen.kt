package com.example.desktopApp.ui.components

import androidx.compose.foundation.background
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
import com.example.desktopApp.database.dao.ClientDao
import com.example.desktopApp.database.entities.Client
import java.time.LocalDate
import androidx.compose.ui.graphics.Color

@Composable
fun ClientsScreen() {
    var clients by remember { mutableStateOf(emptyList<Client>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingClient by remember { mutableStateOf<Client?>(null) }

    // Загрузка клиентов из БД
    LaunchedEffect(Unit) {
        clients = ClientDao.getAllClients()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Заголовок и кнопка добавления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Text("Управление клиентами", style = MaterialTheme.typography.h4)
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
                Spacer(Modifier.width(8.dp))
                Text("Добавить клиента")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Таблица клиентов
        ClientTable(
            clients = clients,
            onEditClick = { client -> editingClient = client },
            onDeleteClick = { client ->
                ClientDao.deleteClient(client.id)
                clients = clients.filter { it.id != client.id }
            }
        )
    }

    // Диалог добавления/редактирования
    if (showAddDialog || editingClient != null) {
        ClientEditDialog(
            client = editingClient,
            onSave = { client ->
                if (editingClient == null) {
                    val newId = ClientDao.addClient(client)
                    clients = clients + client.copy(id = newId)
                } else {
                    ClientDao.updateClient(client)
                    clients = clients.map { if (it.id == client.id) client else it }
                }
                showAddDialog = false
                editingClient = null
            },
            onDismiss = {
                showAddDialog = false
                editingClient = null
            }
        )
    }
}

@Composable
fun ClientTable(
    clients: List<Client>,
    onEditClick: (Client) -> Unit,
    onDeleteClick: (Client) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = 4.dp,

    ) {
        Column {
            // Заголовки таблицы
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,

            ) {
                Text("ID", style = MaterialTheme.typography.h6, modifier = Modifier.weight(1f))
                Text("Имя", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Фамилия", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Телефон", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Email", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Дата регистрации", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Действия", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
            }

            Divider()

            // Список клиентов
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(clients) { client ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Text("${client.id}", modifier = Modifier.weight(1f))
                        Text(client.firstName, modifier = Modifier.weight(2f))
                        Text(client.lastName, modifier = Modifier.weight(2f))
                        Text(client.phone ?: "-", modifier = Modifier.weight(2f))
                        Text(client.email ?: "-", modifier = Modifier.weight(2f))
                        Text(client.registrationDate.toString(), modifier = Modifier.weight(2f))

                        Row(modifier = Modifier.weight(2f)) {
                            IconButton(onClick = { onEditClick(client) }) {
                                Icon(Icons.Default.Edit, "Редактировать")
                            }
                            IconButton(onClick = { onDeleteClick(client) }) {
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
fun ClientEditDialog(
    client: Client?,
    onSave: (Client) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf(client?.firstName ?: "") }
    var lastName by remember { mutableStateOf(client?.lastName ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (client == null) "Добавить клиента" else "Редактировать клиента",
                    style = MaterialTheme.typography.h5
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Имя *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Фамилия *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newClient = Client(
                                id = client?.id ?: 0,
                                firstName = firstName,
                                lastName = lastName,
                                phone = if (phone.isBlank()) null else phone,
                                email = if (email.isBlank()) null else email,
                                registrationDate = client?.registrationDate ?: LocalDate.now()
                            )
                            onSave(newClient)
                        },
                        enabled = firstName.isNotBlank() && lastName.isNotBlank()
                    ) {
                        Text(if (client == null) "Добавить" else "Сохранить")
                    }
                }
            }
        }
    }
}