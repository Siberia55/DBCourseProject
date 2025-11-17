package com.example.desktopApp.database.dao


import com.example.desktopApp.database.entities.Client
import com.example.desktopApp.database.entities.Clients
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object ClientDao {

    fun getAllClients(): List<Client> = transaction {
        Clients.selectAll().map { rowToClient(it) }
    }

    fun getClientById(id: Int): Client? = transaction {
        Clients.select { Clients.id eq id }.singleOrNull()?.let { rowToClient(it) }
    }

    fun addClient(client: Client): Int = transaction {
        Clients.insert {
            it[firstName] = client.firstName
            it[lastName] = client.lastName
            it[phone] = client.phone
            it[email] = client.email
            it[registrationDate] = client.registrationDate
        } get Clients.id
    }

    fun updateClient(client: Client): Boolean = transaction {
        Clients.update({ Clients.id eq client.id }) {
            it[firstName] = client.firstName
            it[lastName] = client.lastName
            it[phone] = client.phone
            it[email] = client.email
        } > 0
    }

    fun deleteClient(id: Int): Boolean = transaction {
        Clients.deleteWhere { Clients.id eq id } > 0
    }

    private fun rowToClient(row: ResultRow): Client = Client(
        id = row[Clients.id],
        firstName = row[Clients.firstName],
        lastName = row[Clients.lastName],
        phone = row[Clients.phone],
        email = row[Clients.email],
        registrationDate = row[Clients.registrationDate]
    )
}