package domain.manager.impl

import model.ActionResultDescription
import domain.cypher.IEncryptionService
import domain.data_base.IAccountRepository
import domain.data_base.IUserRepository
import domain.manager.IAccountManager
import domain.validator.Validator
import model.*
import org.koin.core.component.KoinComponent


class AccountManager(
        private val accountRepository: IAccountRepository,
        private val userRepository: IUserRepository,
        private val cypher: IEncryptionService
    ) : IAccountManager, KoinComponent {
        private val currentUser = CurrentLoggedUser.getInstance()

    override fun signIn(login: String, password: String): ActionResult {
        return  accountRepository.findAccountByLogin(login)?.let {
            if (cypher.decrypt(it.password) == password) {
                val user = userRepository.findUserByLogin(login)
                currentUser.setUser(user)
                return ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
            }
            else ActionResult(Status.INVALID, ActionResultDescription.INCORRECT_PASSWORD.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.LOGIN_NOT_EXIST.description)
    }

    override fun signOut(): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            CurrentLoggedUser.getInstance().setUser()
            ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun signUp(login: String, password: String, firstName: String , lastName:String): ActionResult {
        val stringBuilder = StringBuilder()
        stringBuilder.append(Validator.loginValidate(login))
        stringBuilder.append(Validator.passwordValidate(password))
        if (stringBuilder.isNotBlank()) {
            return ActionResult(Status.INVALID, stringBuilder.toString())
        } else {
            accountRepository.findAccountByLogin(login)?.let {
                return ActionResult(Status.INVALID, ActionResultDescription.LOGIN_EXIST.description)
            } ?: run{
                val encryptPassword = cypher.encrypt(password)
                if (accountRepository.createAccount(login, encryptPassword)) {
                    val account = accountRepository.findAccountByLogin(login)
                    userRepository.createUser(firstName,lastName, account!!)
                    return ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                } else {
                    return  ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                }
            }
        }
    }

    override fun unregister() : ActionResult {
        return currentUser.getUser()?.let {
            if (userRepository.deleteUserById(it.id)) {
                currentUser.setUser()
                ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
            } else
                ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun editUserData(firstName: String?, lastName: String?): ActionResult {
        return currentUser.getUser()?.let {
            val editedUser = User(it.id, firstName ?: it.name, lastName ?: it.lastName, it.account)
            if (userRepository.editUser(editedUser)) {
                ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
            } else ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun editPassword(oldPassword: String, newPassword: String): ActionResult {
        return currentUser.getUser()?.let {
            if (cypher.decrypt(it.account.password) != oldPassword) {
                ActionResult(Status.INVALID, ActionResultDescription.INCORRECT_PASSWORD.description)
            } else {
                val msg = Validator.passwordValidate(newPassword)
                if (msg.isEmpty()) {
                    val account = Account(it.account.id, it.account.login, newPassword)
                    if (accountRepository.editAccount(account)) {
                        ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                    } else {
                        ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                    }
                } else {
                    ActionResult(Status.INVALID, msg)
                }
            }
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun getUser(userId: Long): User? {
        return userRepository.findUserById(userId)
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findUsers()
    }
}