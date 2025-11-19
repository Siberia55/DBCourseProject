package com.example.desktopApp.database.dao

import com.example.desktopApp.database.entities.Session
import com.example.desktopApp.database.entities.Sessions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDateTime

object SessionDao {

    fun getAllSessions(): List<Session> = transaction {
        Sessions.selectAll().map { rowToSession(it) }
    }

    fun getSessionById(id: Int): Session? = transaction {
        Sessions.select { Sessions.sessionId eq id }.singleOrNull()?.let { rowToSession(it) }
    }

    fun getActiveSessions(): List<Session> = transaction {
        Sessions.select { Sessions.endTime.isNull() }.map { rowToSession(it) }
    }

    fun startSession(clientId: Int, computerId: Int): Int = transaction {
        Sessions.insert {
            it[Sessions.clientId] = clientId
            it[Sessions.computerId] = computerId
            it[Sessions.startTime] = LocalDateTime.now()
        } get Sessions.sessionId
    }

    fun endSession(sessionId: Int): Boolean = transaction {
        val session = getSessionById(sessionId) ?: return@transaction false

        val computer = ComputerDao.getAllComputers().find { it.id == session.computerId }
            ?: return@transaction false
        val endTime = LocalDateTime.now()

        // Calculate hours (minimum 1 hour)
        val hours = maxOf(1, java.time.Duration.between(session.startTime, endTime).toHours())
        val computerCost = computer.hourlyRate * BigDecimal(hours)

        // Calculate services cost
        val servicesCost = SessionServiceDao.getServicesCostForSession(sessionId)

        val totalCost = computerCost + servicesCost

        Sessions.update({ Sessions.sessionId eq sessionId }) {
            it[Sessions.endTime] = endTime
            it[Sessions.totalCost] = totalCost
        } > 0
    }

    fun updateSession(session: Session): Boolean = transaction {
        Sessions.update({ Sessions.sessionId eq session.sessionId }) {
            it[Sessions.clientId] = session.clientId
            it[Sessions.computerId] = session.computerId
            it[Sessions.startTime] = session.startTime
            it[Sessions.endTime] = session.endTime
            it[Sessions.totalCost] = session.totalCost
        } > 0
    }

    fun deleteSession(id: Int): Boolean = transaction {
        // First delete related services
        SessionServiceDao.deleteServicesForSession(id)
        Sessions.deleteWhere { Sessions.sessionId eq id } > 0
    }

    private fun rowToSession(row: ResultRow): Session = Session(
        sessionId = row[Sessions.sessionId],
        clientId = row[Sessions.clientId],
        computerId = row[Sessions.computerId],
        startTime = row[Sessions.startTime],
        endTime = row[Sessions.endTime],
        totalCost = row[Sessions.totalCost]
    )
}