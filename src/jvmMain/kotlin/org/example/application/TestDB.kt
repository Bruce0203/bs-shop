package org.example.application

import io.ktor.server.html.*
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object TestDB : Table<Nothing>("test_db") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val location = varchar("location")
}
fun initTestDB() {
    Class.forName("oracle.jdbc.driver.OracleDriver");

    val database = Database.connect(dotenv.get("DB_URL"),
        user = dotenv.get("DB_USERNAME"),
        password = dotenv.get("DB_PASSWORD")
    )

    database.insert(TestDB) {
        set(it.name, "Bruce0203")
        set(it.location, "South Korea")
    }

    for (row in database.from(TestDB).select()) {
        println(row[TestDB.name])
    }
}
