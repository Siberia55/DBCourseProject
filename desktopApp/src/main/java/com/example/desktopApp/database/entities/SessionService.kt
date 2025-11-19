package com.example.desktopApp.database.entities

import org.jetbrains.exposed.sql.Table

object SessionServices : Table("session_services") {
    val sessionId = integer("session_id").references(Sessions.sessionId)
    val serviceId = integer("service_id").references(Services.serviceId)
    val quantity = integer("quantity").default(1)

    override val primaryKey = PrimaryKey(sessionId, serviceId)
}

data class SessionService(
    val sessionId: Int,
    val serviceId: Int,
    val quantity: Int = 1
)