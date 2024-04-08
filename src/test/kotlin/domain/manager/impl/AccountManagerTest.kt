package domain.manager.impl

import model.ActionResultDescription
import AppTestConfig
import model.ValidationDescription
import domain.cypher.IEncryptionService
import domain.data_base.IAccountRepository
import domain.data_base.IUserRepository
import domain.manager.IAccountManager
import domain.validator.Validator
import io.kotest.core.test.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import model.*
import kotlin.test.assertEquals

class AccountManagerTest: AppTestConfig() {
    private val id = 1L
    private val login = "userLogin04"
    private val password = "userPassword1@"
    private val newPassword = "newUserPassword1@"
    private val incorrectPassword = "userPassword"
    private val firstName = "firstName"
    private val lastName = "lastName"

    private lateinit var accountRepository : IAccountRepository
    private lateinit var userRepository : IUserRepository
    private lateinit var cypher : IEncryptionService
    private lateinit var accountManager : IAccountManager

    override suspend fun beforeEach(testCase: TestCase) {
        accountRepository = mockk<IAccountRepository>(relaxed = true)
        userRepository = mockk<IUserRepository>(relaxed = true)
        cypher = mockk<IEncryptionService>(relaxed = true)
        accountManager = AccountManager(accountRepository, userRepository, cypher)
        CurrentLoggedUser.getInstance().setUser(null)
    }

    init {
        "signIn returns login not exist when searching for non-existing login" {
            val slot = slot<String>()
            every { accountRepository.findAccountByLogin(capture(slot)) } returns null
            val result = accountManager.signIn(login, password)
            assertEquals(login, slot.captured)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.LOGIN_NOT_EXIST.description))
        }

        "signIn returns password incorrect for exist login then use correct password and return success status" {
            val result = accountManager.signIn(login, password)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.INCORRECT_PASSWORD.description))
        }

        "sign in returns success status" {
            val user = userRepository.findUserByLogin(login)
            val decryptedPassword = cypher.decrypt(password)
            val result = accountManager.signIn(login, decryptedPassword)
            assertEquals(CurrentLoggedUser.getInstance().getUser(), user)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "Sign Out For Not Logged In User Returns Invalid State, Then Sign In And Sign Out Returns Success, And Then Sign Out Returns Invalid State" {
            val account = Account(id, login, password)
            val user = User(id, firstName,lastName, account)
            every { accountRepository.findAccountByLogin(any()) } returns account
            every { userRepository.findUserByLogin(any()) } returns user
            every { cypher.decrypt(any()) } returns password
            val accountManager = AccountManager(accountRepository, userRepository, cypher)
            var result = accountManager.signOut()
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
            accountManager.signIn(login, password)
            result = accountManager.signOut()
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
            result = accountManager.signOut()
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "signUp returns invalid state for incorrect password"{
            val result = accountManager.signUp(login, incorrectPassword, firstName, lastName)
            assertEquals(
                result,
                ActionResult(
                    Status.INVALID,
                    ValidationDescription.PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER.description
                )
            )
        }

        "signUp return invalid state for existing login" {
            val result = accountManager.signUp(login, password, firstName, lastName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.LOGIN_EXIST.description))
        }

        "signUp return invalid state for issue with create account" {
            every { accountRepository.findAccountByLogin(login) } returns null
            val result = accountManager.signUp(login, password, firstName, lastName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "singUp return success state" {
            val account = Account(id, login, password)
            every { accountRepository.findAccountByLogin(login) } returnsMany mutableListOf(null, account)
            every { cypher.encrypt(any()) } returns password
            every { accountRepository.createAccount(login, password) } returns true
            every { userRepository.createUser(firstName, lastName, account) } returns true

            val result = accountManager.signUp(login, password, firstName, lastName)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "unregister returns invalid for not sign in user" {
            val result = accountManager.unregister()
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "unregister returns invalid state for db issue" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            val result = accountManager.unregister()
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "unregister return success status" {
            every { cypher.decrypt(any()) } returns password
            every { userRepository.deleteUserById(any()) } returns true
            accountManager.signIn(login,password)
            val result = accountManager.unregister()
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "edit user data returns invalid status for not logged in user" {
            val result = accountManager.editUserData(firstName, lastName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "edit user data returns invalid status for issue with database" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            every { userRepository.editUser(any()) } returns false
            val result = accountManager.editUserData(firstName, lastName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "edit user data returns success status " {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            every { userRepository.editUser(any()) } returns true
            val result = accountManager.editUserData(firstName, lastName)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "edit password returns invalid status for not logged in user" {
            val result = accountManager.editPassword(password, newPassword)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "edit password returns invalid status for incorrect old password" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            val result = accountManager.editPassword(incorrectPassword, newPassword)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.INCORRECT_PASSWORD.description))
        }

        "edit password returns invalid status for password validation" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            mockkObject(Validator)
            every { Validator.Companion.passwordValidate(any()) } returns ValidationDescription.PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER.description
            val result = accountManager.editPassword(password, newPassword)
            assertEquals(result, ActionResult(Status.INVALID, ValidationDescription.PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER.description))
        }

        "edit password returns invalid status for save data for issue with database" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            mockkObject(Validator)
            every { Validator.Companion.passwordValidate(any()) } returns ValidationDescription.PASSWORD_SUCCESS.description
            every { accountRepository.editAccount(any()) } returns false
            val result = accountManager.editPassword(password, newPassword)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "edit password returns success status" {
            every { cypher.decrypt(any()) } returns password
            accountManager.signIn(login,password)
            mockkObject(Validator)
            every { Validator.Companion.passwordValidate(any()) } returns ValidationDescription.PASSWORD_SUCCESS.description
            every { accountRepository.editAccount(any()) } returns true
            val result = accountManager.editPassword(password, newPassword)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }
    }
}