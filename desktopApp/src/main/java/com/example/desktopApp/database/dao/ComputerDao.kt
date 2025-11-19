package com.example.desktopApp.database.dao

import com.example.desktopApp.database.entities.Computer
import com.example.desktopApp.database.entities.Computers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


object ComputerDao {
    fun getAllComputers(): List<Computer> = transaction {
        Computers.selectAll().map { rowToComputer(it) }
    }

    fun addComputer(computer: Computer): Int = transaction {
        Computers.insert {
            it[name] = computer.name
            it[specifications] = computer.specifications
            it[hourlyRate] = computer.hourlyRate
        } get Computers.id
    }
    fun updateComputer(computer: Computer): Boolean = transaction {
        Computers.update({ Computers.id eq computer.id }) {
            it[name] = computer.name
            it[specifications] = computer.specifications
            it[hourlyRate] = computer.hourlyRate
            it[isActive] = computer.isActive
        } > 0
    }
    fun deleteComputer(id: Int): Boolean = transaction {
        Computers.deleteWhere { Computers.id eq id } > 0
    }
    private fun rowToComputer(row: ResultRow): Computer = Computer(
        id = row[Computers.id],
        name = row[Computers.name],
        specifications = row[Computers.specifications],
        hourlyRate = row[Computers.hourlyRate],
        isActive = row[Computers.isActive]
    )
}
