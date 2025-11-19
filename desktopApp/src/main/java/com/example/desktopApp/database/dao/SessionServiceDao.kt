package com.example.desktopApp.database.dao

import com.example.desktopApp.database.entities.SessionService
import com.example.desktopApp.database.entities.SessionServices
import com.example.desktopApp.database.entities.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object SessionServiceDao {

    fun getServicesForSession(sessionId: Int): List<SessionService> = transaction {
        SessionServices.select { SessionServices.sessionId eq sessionId }
            .map { rowToSessionService(it) }
    }

    fun addServiceToSession(sessionId: Int, serviceId: Int, quantity: Int = 1): Boolean =
        transaction {
            SessionServices.insert {
                it[SessionServices.sessionId] = sessionId
                it[SessionServices.serviceId] = serviceId
                it[SessionServices.quantity] = quantity
            }.insertedCount > 0
        }

    fun updateServiceQuantity(sessionId: Int, serviceId: Int, quantity: Int): Boolean =
        transaction {
            SessionServices.update({
                (SessionServices.sessionId eq sessionId) and
                        (SessionServices.serviceId eq serviceId)
            }) {
                it[SessionServices.quantity] = quantity
            } > 0
        }

    fun removeServiceFromSession(sessionId: Int, serviceId: Int): Boolean = transaction {
        SessionServices.deleteWhere {
            (SessionServices.sessionId eq sessionId) and
                    (SessionServices.serviceId eq serviceId)
        } > 0
    }

    fun deleteServicesForSession(sessionId: Int): Boolean = transaction {
        SessionServices.deleteWhere { SessionServices.sessionId eq sessionId } > 0
    }

    fun deleteServiceFromAllSessions(serviceId: Int): Boolean = transaction {
        SessionServices.deleteWhere { SessionServices.serviceId eq serviceId } > 0
    }

    fun getServicesCostForSession(sessionId: Int): BigDecimal = transaction {
        val services = (SessionServices innerJoin Services)
            .select { SessionServices.sessionId eq sessionId }

        services.fold(BigDecimal.ZERO) { acc, row ->
            val quantity = row[SessionServices.quantity]
            val price = row[Services.price]
            acc + (price * BigDecimal(quantity))
        }
    }

    private fun rowToSessionService(row: ResultRow): SessionService = SessionService(
        sessionId = row[SessionServices.sessionId],
        serviceId = row[SessionServices.serviceId],
        quantity = row[SessionServices.quantity]
    )
}
