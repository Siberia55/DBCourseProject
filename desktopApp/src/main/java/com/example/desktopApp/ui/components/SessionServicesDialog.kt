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
import com.example.desktopApp.database.entities.Service
import com.example.desktopApp.database.entities.SessionService
import com.example.desktopApp.database.SessionManager
import java.math.BigDecimal
/*
@Composable
fun SessionServicesDialog(
    session: Session,
    onClose: () -> Unit
) {
    var services by remember { mutableStateOf(emptyList<Service>()) }
    var sessionServices by remember { mutableStateOf(emptyList<SessionService>()) }
    var showAddServiceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(session.sessionId) {
        services = ServiceDao.getAllServices()
        sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
    }

    val currentCost = remember(session, sessionServices) {
        SessionManager.calculateCurrentCost(session.sessionId)
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier.fillMaxWidth().height(600.dp).padding(16.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Управление услугами для сессии #${session.sessionId}",
                    style = MaterialTheme.typography.h5)

                Spacer(Modifier.height(8.dp))
                Text("Текущая стоимость: $currentCost руб",
                    style = MaterialTheme.typography.h6)

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Услуги в сессии", style = MaterialTheme.typography.h6)
                    Button(onClick = { showAddServiceDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить услугу")
                        Spacer(Modifier.width(4.dp))
                        Text("Добавить услугу")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Список услуг в сессии
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(sessionServices) { sessionService ->
                        val service = services.find { it.serviceId == sessionService.serviceId }
                        ServiceItem(
                            service = service,
                            sessionService = sessionService,
                            onQuantityChange = { newQuantity ->
                                if (newQuantity > 0) {
                                    SessionServiceDao.updateServiceQuantity(
                                        session.sessionId,
                                        sessionService.serviceId,
                                        newQuantity
                                    )
                                    sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                                } else {
                                    SessionServiceDao.removeServiceFromSession(
                                        session.sessionId,
                                        sessionService.serviceId
                                    )
                                    sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                                }
                            }
                        )
                        Divider()
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть")
                }
            }
        }
    }

    if (showAddServiceDialog) {
        AddServiceDialog(
            services = services,
            existingServices = sessionServices,
            onAddService = { serviceId, quantity ->
                SessionServiceDao.addServiceToSession(session.sessionId, serviceId, quantity)
                sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                showAddServiceDialog = false
            },
            onDismiss = { showAddServiceDialog = false }
        )
    }
}
*/
@Composable
fun SessionServicesDialog(
    session: Session,
    onClose: () -> Unit
) {
    var services by remember { mutableStateOf(emptyList<Service>()) }
    var sessionServices by remember { mutableStateOf(emptyList<SessionService>()) }
    var showAddServiceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(session.sessionId) {
        services = ServiceDao.getAllServices()
        // Используем обычный метод без JOIN
        sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
    }

    val currentCost = remember(session, sessionServices) {
        SessionManager.calculateCurrentCost(session.sessionId)
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier.fillMaxWidth().height(600.dp).padding(16.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Управление услугами для сессии #${session.sessionId}",
                    style = MaterialTheme.typography.h5)

                Spacer(Modifier.height(8.dp))
                Text("Текущая стоимость: $currentCost руб",
                    style = MaterialTheme.typography.h6)

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Услуги в сессии", style = MaterialTheme.typography.h6)
                    Button(onClick = { showAddServiceDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить услугу")
                        Spacer(Modifier.width(4.dp))
                        Text("Добавить услугу")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Список услуг в сессии
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(sessionServices) { sessionService ->
                        val service = services.find { it.serviceId == sessionService.serviceId }
                        ServiceItem(
                            service = service,
                            sessionService = sessionService,
                            onQuantityChange = { newQuantity ->
                                if (newQuantity > 0) {
                                    SessionServiceDao.updateServiceQuantity(
                                        session.sessionId,
                                        sessionService.serviceId,
                                        newQuantity
                                    )
                                    // Обновляем список
                                    sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                                } else {
                                    SessionServiceDao.removeServiceFromSession(
                                        session.sessionId,
                                        sessionService.serviceId
                                    )
                                    // Обновляем список
                                    sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                                }
                            }
                        )
                        Divider()
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть")
                }
            }
        }
    }

    if (showAddServiceDialog) {
        AddServiceDialog(
            services = services,
            existingServices = sessionServices,
            onAddService = { serviceId, quantity ->
                SessionServiceDao.addServiceToSession(session.sessionId, serviceId, quantity)
                // Обновляем список
                sessionServices = SessionServiceDao.getServicesForSession(session.sessionId)
                showAddServiceDialog = false
            },
            onDismiss = { showAddServiceDialog = false }
        )
    }
}
@Composable
fun ServiceItem(
    service: Service?,
    sessionService: SessionService,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(service?.name ?: "Неизвестная услуга", style = MaterialTheme.typography.body1)
            Text("${service?.price ?: BigDecimal.ZERO} руб/шт",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onQuantityChange(sessionService.quantity - 1) },
                enabled = sessionService.quantity > 1
            ) {
                Icon(Icons.Default.Remove, "Уменьшить")
            }

            Text("${sessionService.quantity} шт",
                modifier = Modifier.padding(horizontal = 8.dp))

            IconButton(
                onClick = { onQuantityChange(sessionService.quantity + 1) }
            ) {
                Icon(Icons.Default.Add, "Увеличить")
            }

            IconButton(
                onClick = { onQuantityChange(0) }
            ) {
                Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colors.error)
            }
        }
    }
}

@Composable
fun AddServiceDialog(
    services: List<Service>,
    existingServices: List<SessionService>,
    onAddService: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedServiceId by remember { mutableStateOf<Int?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var serviceExpanded by remember { mutableStateOf(false) }

    val availableServices = services.filter { service ->
        !existingServices.any { it.serviceId == service.serviceId }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Добавить услугу", style = MaterialTheme.typography.h5)

                Spacer(Modifier.height(16.dp))

                // Выбор услуги
                Box {
                    OutlinedTextField(
                        value = selectedServiceId?.let { id ->
                            services.find { it.serviceId == id }?.name
                        } ?: "",
                        onValueChange = {},
                        label = { Text("Услуга *") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { serviceExpanded = !serviceExpanded }) {
                                Icon(
                                    if (serviceExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    "Раскрыть список"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = serviceExpanded,
                        onDismissRequest = { serviceExpanded = false }
                    ) {
                        availableServices.forEach { service ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedServiceId = service.serviceId
                                    serviceExpanded = false
                                }
                            ) {
                                Text("${service.name} - ${service.price} руб")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Количество
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = {
                        quantity = it.toIntOrNull() ?: 1
                    },
                    label = { Text("Количество") },
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
                            selectedServiceId?.let { serviceId ->
                                onAddService(serviceId, quantity)
                            }
                        },
                        enabled = selectedServiceId != null && quantity > 0
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}