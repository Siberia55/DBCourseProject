package com.example.desktopApp.database.entities

import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

object Services : Table("services") {
    val serviceId = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val price = decimal("price", 10, 2)

    override val primaryKey = PrimaryKey(serviceId)
}

data class Service(
    val serviceId: Int = 0,
    val name: String,
    val price: BigDecimal
)