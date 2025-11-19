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
import com.example.desktopApp.database.dao.ServiceDao
import com.example.desktopApp.database.entities.Service
import java.math.BigDecimal

@Composable
fun ServicesScreen() {
    var services by remember { mutableStateOf(emptyList<Service>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<Service?>(null) }

    LaunchedEffect(Unit) {
        services = ServiceDao.getAllServices()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Управление услугами", style = MaterialTheme.typography.h4)
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
                Spacer(Modifier.width(8.dp))
                Text("Добавить услугу")
            }
        }

        Spacer(Modifier.height(16.dp))

        ServiceTable(
            services = services,
            onEditClick = { service -> editingService = service },
            onDeleteClick = { service ->
                ServiceDao.deleteService(service.serviceId)
                services = services.filter { it.serviceId != service.serviceId }
            }
        )
    }

    if (showAddDialog || editingService != null) {
        ServiceEditDialog(
            service = editingService,
            onSave = { service ->
                if (editingService == null) {
                    val newId = ServiceDao.addService(service)
                    services = services + service.copy(serviceId = newId)
                } else {
                    ServiceDao.updateService(service)
                    services = services.map { if (it.serviceId == service.serviceId) service else it }
                }
                showAddDialog = false
                editingService = null
            },
            onDismiss = {
                showAddDialog = false
                editingService = null
            }
        )
    }
}

@Composable
fun ServiceTable(
    services: List<Service>,
    onEditClick: (Service) -> Unit,
    onDeleteClick: (Service) -> Unit
) {
    Card(modifier = Modifier.fillMaxSize(), elevation = 4.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID", style = MaterialTheme.typography.h6, modifier = Modifier.weight(1f))
                Text("Название", style = MaterialTheme.typography.h6, modifier = Modifier.weight(3f))
                Text("Цена", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
                Text("Действия", style = MaterialTheme.typography.h6, modifier = Modifier.weight(2f))
            }

            Divider()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(services) { service ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${service.serviceId}", modifier = Modifier.weight(1f))
                        Text(service.name, modifier = Modifier.weight(3f))
                        Text("${service.price} руб", modifier = Modifier.weight(2f))

                        Row(modifier = Modifier.weight(2f)) {
                            IconButton(onClick = { onEditClick(service) }) {
                                Icon(Icons.Default.Edit, "Редактировать")
                            }
                            IconButton(onClick = { onDeleteClick(service) }) {
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
fun ServiceEditDialog(
    service: Service?,
    onSave: (Service) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var price by remember { mutableStateOf(service?.price?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    if (service == null) "Добавить услугу" else "Редактировать услугу",
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
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена (руб) *") },
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
                            val newService = Service(
                                serviceId = service?.serviceId ?: 0,
                                name = name,
                                price = price.toBigDecimal()
                            )
                            onSave(newService)
                        },
                        enabled = name.isNotBlank() && price.isNotBlank()
                    ) {
                        Text(if (service == null) "Добавить" else "Сохранить")
                    }
                }
            }
        }
    }
}
