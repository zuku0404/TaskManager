package domain.data_base

import model.Account
import model.User

interface IUserRepository {
    fun createUser(firstName: String, lastName: String, account:Account ): Boolean
    fun findUserByLogin(login: String): User
    fun findUserById(userId: Long): User?
    fun findUsers(): List<User>
    fun editUser(user: User): Boolean
    fun deleteUserById(userId: Long): Boolean
}