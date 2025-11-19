package com.example.desktopApp.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.math.BigDecimal
import java.time.LocalDateTime

object Sessions : Table("sessions") {
    val sessionId = integer("id").autoIncrement()
    val clientId = integer("client_id").references(Clients.id)
    val computerId = integer("computer_id").references(Computers.id)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time").nullable()
    val totalCost = decimal("total_cost", 10, 2).default(BigDecimal.ZERO)

    override val primaryKey = PrimaryKey(sessionId)
}

data class Session(
    val sessionId: Int = 0,
    val clientId: Int,
    val computerId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val totalCost: BigDecimal = BigDecimal.ZERO
)