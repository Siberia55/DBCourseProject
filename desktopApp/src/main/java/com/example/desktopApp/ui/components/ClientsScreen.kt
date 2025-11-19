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
import com.example.desktopApp.database.dao.ClientDao
import com.example.desktopApp.database.entities.Client
import java.time.LocalDate
import androidx.compose.ui.graphics.Color

@Composable
fun ClientsScreen() {
    var clients by remember { mutableStateOf(emptyList<Client>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingClient by remember { mutableStateOf<Client?>(null) }

    // Состояние фильтрации и сортировки
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf<ClientSortField?>(null) }
    var sortDirection by remember { mutableStateOf(FilterSortState.SortDirection.ASCENDING) }

    // Загрузка клиентов из БД
    LaunchedEffect(Unit) {
        clients = ClientDao.getAllClients()
    }

    // Фильтрация и сортировка
    val filteredAndSortedClients = remember(clients, searchQuery, sortBy, sortDirection) {
        var result = clients

        // Фильтрация по поисковому запросу
        if (searchQuery.isNotBlank()) {
            result = result.filter { client ->
                client.firstName.contains(searchQuery, ignoreCase = true) ||
                        client.lastName.contains(searchQuery, ignoreCase = true) ||
                        client.phone?.contains(searchQuery, ignoreCase = true) == true ||
                        client.email?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        // Сортировка
        sortBy?.let { field ->
            result = when (field) {
                ClientSortField.FIRST_NAME -> result.sortedBy { it.firstName }
                ClientSortField.LAST_NAME -> result.sortedBy { it.lastName }
                ClientSortField.REGISTRATION_DATE -> result.sortedBy { it.registrationDate }
                ClientSortField.PHONE -> result.sortedBy { it.phone ?: "" }
                ClientSortField.EMAIL -> result.sortedBy { it.email ?: "" }
            }

            // Обратный порядок если DESCENDING
            if (sortDirection == FilterSortState.SortDirection.DESCENDING) {
                result = result.reversed()
            }
        }

        result
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

        // Панель фильтрации и сортировки
        ClientFilterSortPanel(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            sortBy = sortBy,
            onSortByChange = { sortBy = it },
            sortDirection = sortDirection,
            onSortDirectionChange = { sortDirection = it },
            clientCount = filteredAndSortedClients.size,
            totalCount = clients.size
        )

        Spacer(Modifier.height(16.dp))

        // Таблица клиентов
        ClientTable(
            clients = filteredAndSortedClients,
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
fun ClientFilterSortPanel(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortBy: ClientSortField?,
    onSortByChange: (ClientSortField?) -> Unit,
    sortDirection: FilterSortState.SortDirection,
    onSortDirectionChange: (FilterSortState.SortDirection) -> Unit,
    clientCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Поиск
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Поиск:", modifier = Modifier.padding(end = 8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Имя, фамилия, телефон, email...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, "Очистить поиск")
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Сортировка
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Сортировка:", modifier = Modifier.padding(end = 8.dp))

                // Выбор поля для сортировки
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = sortBy?.name ?: "Не выбрано",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, "Выбрать поле для сортировки")
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            onSortByChange(null)
                            expanded = false
                        }) {
                            Text("Не выбрано")
                        }
                        listOf(
                            ClientSortField.FIRST_NAME,
                            ClientSortField.LAST_NAME,
                            ClientSortField.REGISTRATION_DATE,
                            ClientSortField.PHONE,
                            ClientSortField.EMAIL
                        ).forEach { field ->
                            DropdownMenuItem(onClick = {
                                onSortByChange(field)
                                expanded = false
                            }) {
                                Text(field.name)
                            }
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Кнопка смены направления сортировки
                IconButton(
                    onClick = {
                        onSortDirectionChange(
                            if (sortDirection == FilterSortState.SortDirection.ASCENDING)
                                FilterSortState.SortDirection.DESCENDING
                            else
                                FilterSortState.SortDirection.ASCENDING
                        )
                    },
                    enabled = sortBy != null
                ) {
                    Icon(
                        if (sortDirection == FilterSortState.SortDirection.ASCENDING)
                            Icons.Default.ArrowUpward
                        else
                            Icons.Default.ArrowDownward,
                        "Изменить порядок сортировки"
                    )
                }

                // Кнопка сброса сортировки
                IconButton(
                    onClick = {
                        onSortByChange(null)
                        onSortDirectionChange(FilterSortState.SortDirection.ASCENDING)
                    },
                    enabled = sortBy != null
                ) {
                    Icon(Icons.Default.Clear, "Сбросить сортировку")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Найдено клиентов: $clientCount из $totalCount",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.secondary
                )

                // Индикатор активной сортировки
                if (sortBy != null) {
                    Text(
                        "Сортировка: ${sortBy.name} (${if (sortDirection == FilterSortState.SortDirection.ASCENDING) "↑" else "↓"})",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

// Остальной код (ClientTable и ClientEditDialog) остается без изменений
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