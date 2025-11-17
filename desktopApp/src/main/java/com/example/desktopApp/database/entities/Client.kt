package com.example.desktopApp.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Clients : Table("clients") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val phone = varchar("phone", 50).nullable()
    val email = varchar("email", 100).nullable()
    val registrationDate = date("registration_date").default(LocalDate.now())

    override val primaryKey = PrimaryKey(id)
}

data class Client(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val email: String? = null,
    val registrationDate: LocalDate = LocalDate.now()
)