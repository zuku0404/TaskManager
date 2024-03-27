package data_base

import java.sql.Connection
import java.sql.DriverManager

object Connection {
    private val driver = "com.mysql.cj.jdbc.Driver"
    private val url = "jdbc:mysql://localhost:3306/jira"
    private val user = "root"
    private val password = "password"

    fun getConnection(): Connection {
        Class.forName(driver)
        return DriverManager.getConnection(url, user, password)
    }
}