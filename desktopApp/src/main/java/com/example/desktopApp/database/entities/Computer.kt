package com.example.desktopApp.database.entities

import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

object Computers : Table("computers") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100).uniqueIndex()
    val specifications = text("specifications").nullable()
    val hourlyRate = decimal("hourly_rate", 10, 2)
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id)
}

data class Computer(
    val id: Int = 0,
    val name: String,
    val specifications: String? = null,
    val hourlyRate: BigDecimal,
    val isActive: Boolean = true
)