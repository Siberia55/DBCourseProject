package com.example.desktopApp.database.dao

import com.example.desktopApp.database.entities.Service
import com.example.desktopApp.database.entities.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object ServiceDao {

    fun getAllServices(): List<Service> = transaction {
        Services.selectAll().map { rowToService(it) }
    }

    fun getServiceById(id: Int): Service? = transaction {
        Services.select { Services.serviceId eq id }.singleOrNull()?.let { rowToService(it) }
    }

    fun addService(service: Service): Int = transaction {
        Services.insert {
            it[Services.name] = service.name
            it[Services.price] = service.price
        } get Services.serviceId
    }

    fun updateService(service: Service): Boolean = transaction {
        Services.update({ Services.serviceId eq service.serviceId }) {
            it[Services.name] = service.name
            it[Services.price] = service.price
        } > 0
    }

    fun deleteService(id: Int): Boolean = transaction {
        // First delete from session_services
        SessionServiceDao.deleteServiceFromAllSessions(id)
        Services.deleteWhere { Services.serviceId eq id } > 0
    }

    private fun rowToService(row: ResultRow): Service = Service(
        serviceId = row[Services.serviceId],
        name = row[Services.name],
        price = row[Services.price]
    )
}