package external.data_base

import domain.data_base.Connection
import domain.data_base.IUserRepository
import model.Account
import model.User
import org.koin.core.component.KoinComponent

class UserRepository: IUserRepository,KoinComponent {

    override fun createUser(firstName: String, lastName: String, account:Account ): Boolean {
        val sql = "INSERT INTO users(first_name, last_name, account_id) VALUE(?,?,?)"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, firstName)
            prepareStatement.setString(2, lastName)
            prepareStatement.setLong(3, account.id)
            val numberOfRowChanged = prepareStatement.executeUpdate()
            return numberOfRowChanged > 0
        }
    }

    override fun findUserByLogin(login: String): User {
        val sql = "SELECT user_id, first_name, last_name, password, users.account_id FROM users JOIN accounts ON users.account_id = accounts.account_id WHERE login = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, login)
            val resultSet = prepareStatement.executeQuery()
            resultSet.next()
            val userId = resultSet.getLong(1)
            val firstName = resultSet.getString(2)
            val lastName = resultSet.getString(3)
            val password = resultSet.getString(4)
            val accountId = resultSet.getLong(5)
            return User(userId, firstName, lastName, Account(accountId, login, password))
        }
    }

    override fun findUserById(userId: Long): User? {
        val sql = "SELECT first_name, last_name, login, password, users.account_id FROM users left JOIN accounts ON users.account_id = accounts.account_id WHERE user_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, userId)
            val resultSet = prepareStatement.executeQuery()
            if (resultSet.next()) {
                val firstName = resultSet.getString(1)
                val lastName = resultSet.getString(2)
                val login = resultSet.getString(3)
                val password = resultSet.getString(4)
                val accountId = resultSet.getLong(5)
                return User(userId, firstName, lastName, Account(accountId, login, password))
            }
           return null
        }
    }

    override fun findUsers(): List<User> {
        val sql = "SELECT user_id, first_name, last_name, login, password, users.account_id FROM users left JOIN accounts ON users.account_id = accounts.account_id"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            val resultSet = prepareStatement.executeQuery()
            val users = mutableListOf<User>()
            while (resultSet.next()) {
                val userId = resultSet.getLong(1)
                val firstName = resultSet.getString(2)
                val lastName = resultSet.getString(3)
                val login = resultSet.getString(4)
                val password = resultSet.getString(5)
                val accountId = resultSet.getLong(6)
                users.add(User(userId, firstName, lastName, Account(accountId, login, password)))
            }
           return users
        }
    }

    override fun deleteUserById(userId: Long): Boolean {
        val sql = "DELETE FROM users WHERE user_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, userId)
            val numberOfRowChanged = prepareStatement.executeUpdate()
            return numberOfRowChanged > 0
        }
    }

    override fun editUser(user: User): Boolean {
        val sql = "UPDATE users(first_name, last_name) SET first_name = ?, last_name = ?) WHERE user_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, user.name)
            prepareStatement.setString(2,user.lastName)
            prepareStatement.setLong(3,user.id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }
}