package com.example.desktopApp.database

import com.example.desktopApp.database.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Duration

object SessionManager {

    fun getSessionDetails(sessionId: Int): SessionDetails? = transaction {
        val session = SessionDao.getSessionById(sessionId) ?: return@transaction null
        val client =
            ClientDao.getAllClients().find { it.id == session.clientId } ?: return@transaction null
        val computer = ComputerDao.getAllComputers().find { it.id == session.computerId }
            ?: return@transaction null
        val services = SessionServiceDao.getServicesForSession(sessionId)

        SessionDetails(session, client, computer, services)
    }

    fun calculateCurrentCost(sessionId: Int): BigDecimal {
        val session = SessionDao.getSessionById(sessionId) ?: return BigDecimal.ZERO
        val computer = ComputerDao.getAllComputers().find { it.id == session.computerId }
            ?: return BigDecimal.ZERO

        val endTime = session.endTime ?: LocalDateTime.now()
        val hours = maxOf(1, Duration.between(session.startTime, endTime).toHours())
        val computerCost = computer.hourlyRate * BigDecimal(hours)

        val servicesCost = SessionServiceDao.getServicesCostForSession(sessionId)

        return computerCost + servicesCost
    }
}

data class SessionDetails(
    val session: com.example.desktopApp.database.entities.Session,
    val client: com.example.desktopApp.database.entities.Client,
    val computer: com.example.desktopApp.database.entities.Computer,
    val services: List<com.example.desktopApp.database.entities.SessionService>
)