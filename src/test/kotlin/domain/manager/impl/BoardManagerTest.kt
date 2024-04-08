package domain.manager.impl

import AppTestConfig
import model.ValidationDescription
import domain.cypher.IEncryptionService
import domain.data_base.IAccountRepository
import domain.data_base.IBoardRepository
import domain.data_base.IUserRepository
import domain.manager.IAccountManager
import domain.validator.Validator
import io.kotest.core.test.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import model.ActionResult
import model.ActionResultDescription
import model.CurrentLoggedUser
import model.Status
import kotlin.test.assertEquals

class BoardManagerTest: AppTestConfig() {
    private lateinit var accountRepository: IAccountRepository
    private lateinit var userRepository: IUserRepository
    private lateinit var boardRepository: IBoardRepository
    private lateinit var cypher: IEncryptionService
    private lateinit var accountManager: IAccountManager
    private val boardName = "boardName"
    private val newBoardName = "NewBoardName"
    private val id = 1L

    override suspend fun beforeEach(testCase: TestCase) {
        accountRepository = mockk<IAccountRepository>(relaxed = true)
        userRepository = mockk<IUserRepository>(relaxed = true)
        cypher = mockk<IEncryptionService>(relaxed = true)
        boardRepository = mockk<IBoardRepository>(relaxed = true)
        accountManager = AccountManager(accountRepository, userRepository, cypher)
        CurrentLoggedUser.getInstance().setUser(null)
    }

    init {
        "create new board returns invalid status for not logged in user" {
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.createBoard(boardName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "create new board returns invalid status for incorrect board name" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_LENGTH.description
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.createBoard(boardName)
            assertEquals(result, ActionResult(Status.INVALID, ValidationDescription.BOARD_LENGTH.description))
        }

        "create new board returns invalid status for existed name" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.createBoard(boardName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.BOARD_NAME_EXIST.description))
        }

        "create new board returns invalid status for issue with database" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            every { boardRepository.findBoardByName(boardName) } returns null
            every { boardRepository.createBoard(any()) } returns false
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.createBoard(boardName)
            assertEquals(result,ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "create new board returns success status" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            every { boardRepository.findBoardByName(boardName) } returns null
            every { boardRepository.createBoard(any()) } returns true
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.createBoard(boardName)
            assertEquals(result,ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "edit board returns invalid status for not logged in user" {
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.editBoard(boardName, newBoardName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "edit new board returns invalid status for incorrect board name" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_LENGTH.description
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.editBoard(boardName, newBoardName)
            assertEquals(result, ActionResult(Status.INVALID, ValidationDescription.BOARD_LENGTH.description))
        }

        "edit new board returns invalid status because can not find board" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            every { boardRepository.findBoardByName(boardName) } returns null
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.editBoard(boardName, newBoardName)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description))
        }

        "edit new board returns invalid status for issue with database" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            every { boardRepository.editBoard(any()) } returns false
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.editBoard(boardName, newBoardName)
            assertEquals(result,ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "edit new board returns success status" {
            signIn()
            mockkObject(Validator)
            every { Validator.Companion.boardValidator(any()) } returns ValidationDescription.BOARD_SUCCESS.description
            every { boardRepository.editBoard(any()) } returns true
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.editBoard(boardName, newBoardName)
            assertEquals(result,ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "delete board returns invalid status for not logged in user" {
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.deleteBoard(id)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "delete board returns invalid status because can not find board" {
            signIn()
            every { boardRepository.findBoardById(any()) } returns null
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.deleteBoard(id)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description))
        }

        "delete board returns invalid status for issue with database" {
            signIn()
            every { boardRepository.deleteBoardById(any()) } returns false
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.deleteBoard(id)
            assertEquals(result,ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "delete board returns success status" {
            signIn()
            every { boardRepository.deleteBoardById(any()) } returns true
            val boardManager = BoardManager(boardRepository)
            val result = boardManager.deleteBoard(id)
            assertEquals(result,ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }
    }

    private fun signIn() {
        val login = "userLogin04"
        val password = "userPassword1@"
        every { cypher.decrypt(any()) } returns password
        accountManager.signIn(login, password)
    }
}