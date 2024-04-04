package domain.manager.impl

import model.ActionResult
import domain.cypher.IEncryptionService
import model.Status
import domain.validator.Validator
import domain.data_base.IAccountRepository
import domain.data_base.IUserRepository
import domain.manager.IAccountManager
import model.Account
import model.CurrentLoggedUser
import model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountManager : IAccountManager, KoinComponent {
    private val accountRepository: IAccountRepository by inject()
    private val userRepository: IUserRepository by inject()
    private val cypher: IEncryptionService by inject()
    private val currentUser = CurrentLoggedUser.getInstance()

    override fun signIn(login: String, password: String): ActionResult {
        return  accountRepository.findAccountByLogin(login)?.let {
            if (cypher.decrypt(it.password) == password) {
                val user = userRepository.findUserByLogin(login)
                currentUser.setUser(user)
                return ActionResult(Status.SUCCESS)
            }
            else ActionResult(Status.INVALID, "password is incorrect")
        } ?: ActionResult(Status.INVALID, "login not exist")
    }

    override fun signOut(): ActionResult {
        CurrentLoggedUser.getInstance().setUser(null)
        return ActionResult(Status.SUCCESS, "logout successful")
    }

    override fun signUp(login: String, password: String, firstName: String , lastName:String): ActionResult {
        val stringBuilder = StringBuilder()
        stringBuilder.append(Validator.loginValidate(login))
        stringBuilder.append(Validator.passwordValidate(password))
        if (stringBuilder.isNotBlank()) {
            return ActionResult(Status.INVALID, stringBuilder.toString())
        } else {
            accountRepository.findAccountByLogin(login)?.let {
                return ActionResult(Status.INVALID, "login exist")
            } ?: run{
                val encryptPassword = cypher.encrypt(password)
                if (accountRepository.createAccount(login, encryptPassword)) {
                    val account = accountRepository.findAccountByLogin(login)
                    userRepository.createUser(firstName,lastName, account!!)
                    return ActionResult(Status.SUCCESS)
                } else {
                    return  ActionResult(Status.INVALID, "something gores wrong with create account")
                }
            }
        }
    }

    override fun unregister(userId: Int) : ActionResult {
        currentUser.getUser()?.let {
            userRepository.deleteUserById(it.id)
            currentUser.setUser(null)
            return ActionResult(Status.SUCCESS)
        } ?: return ActionResult(Status.INVALID, "you are not signed in")
    }

    override fun getUser(userId: Long): User? {
        return userRepository.findUserById(userId)
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findUsers()
    }

    override fun editUserData(firstName: String?, lastName: String?): ActionResult {
        val user = currentUser.getUser()!!
        val editedUser = User(user.id, firstName ?: user.name, lastName ?: user.lastName, user.account)
        return if (userRepository.editUser(editedUser)) {
            ActionResult(Status.SUCCESS)
        } else ActionResult(Status.INVALID, "something gores wrong with save data to db")
    }


    override fun editPassword(oldPassword: String, newPassword: String): ActionResult {
        return if (cypher.decrypt(currentUser.getUser()!!.account.password) != oldPassword) {
            ActionResult(Status.INVALID, "old password is incorrect")
        } else {
            val msg = Validator.passwordValidate(newPassword)
            if (msg.isEmpty()) {
                val user = currentUser.getUser()!!
                val account = Account(user.account.id, user.account.login, newPassword)
                accountRepository.editAccount(account)
                ActionResult(Status.SUCCESS)
            } else {
                ActionResult(Status.INVALID, msg)
            }
        }
    }
}