package external.data_base

import domain.data_base.Connection
import domain.data_base.IAccountRepository
import model.Account

class AccountRepository : IAccountRepository {

    override fun createAccount(login: String, password: String): Boolean {
        val sql = "INSERT INTO accounts (login, password) VALUES(?,?)"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, login)
            prepareStatement.setString(2, password)
            return prepareStatement.executeUpdate() > 0
        }
    }

    override fun findAccountByLogin(login: String): Account? {
        val sql = "SELECT * FROM accounts WHERE login = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, login)
            val resultSet = prepareStatement.executeQuery()
            if (resultSet.next()) {
                val id = resultSet.getLong(1)
                val log = resultSet.getString(2)
                val password = resultSet.getString(3)
                println(id)
                println(log)
                println(password)
                return Account(id, log, password)
            } else return null
        }
    }

    override fun findAccountById(accountId: Long): Account? {
        val sql = "SELECT * FROM accounts WHERE account_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, accountId)
            val resultSet = prepareStatement.executeQuery()
            if (resultSet.next()) {
                val id = resultSet.getLong(2)
                val log = resultSet.getString(3)
                val password = resultSet.getString(4)
                return Account(id, log, password)
            } else return null
        }
    }

    override fun editAccount(account: Account): Boolean {
        val sql = "UPDATE accounts SET login = ?, password = ? WHERE account_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, account.login)
            prepareStatement.setString(2, account.password)
            prepareStatement.setLong(3, account.id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }

    override fun deleteAccountByLogin(login: String): Boolean {
        val sql = "DELETE FROM accounts where login = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, login)
            val result = prepareStatement.executeUpdate()
            return result > 0
        }
    }
}