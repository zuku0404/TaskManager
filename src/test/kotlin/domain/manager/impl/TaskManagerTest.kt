package domain.manager.impl

import AppTestConfig
import domain.cypher.IEncryptionService
import domain.data_base.IAccountRepository
import domain.data_base.IBoardRepository
import domain.data_base.ITaskRepository
import domain.data_base.IUserRepository
import domain.manager.IAccountManager
import io.kotest.core.test.TestCase
import io.mockk.every
import io.mockk.mockk
import model.*
import kotlin.test.assertEquals

class TaskManagerTest : AppTestConfig() {
    private lateinit var accountRepository: IAccountRepository
    private lateinit var userRepository: IUserRepository
    private lateinit var boardRepository: IBoardRepository
    private lateinit var taskRepository: ITaskRepository
    private lateinit var cypher: IEncryptionService
    private lateinit var accountManager: IAccountManager
    private val title = "taskTitle"
    private val description = "taskDescription"
    private val boardId = 1L
    private val userId = 1L
    private val taskId = 1L

    override suspend fun beforeEach(testCase: TestCase) {
        accountRepository = mockk<IAccountRepository>(relaxed = true)
        userRepository = mockk<IUserRepository>(relaxed = true)
        cypher = mockk<IEncryptionService>(relaxed = true)
        boardRepository = mockk<IBoardRepository>(relaxed = true)
        taskRepository = mockk<ITaskRepository>(relaxed = true)
        accountManager = AccountManager(accountRepository, userRepository, cypher)
        CurrentLoggedUser.getInstance().setUser(null)
    }

    init {
        "create new task returns invalid status for not logged in user" {
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.createTask(title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "create new task returns invalid status for existed title" {
            signIn()
            taskRepository.findTaskByTitle(title)
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.createTask(title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.TASK_NAME_EXIST.description))
        }

        "create new task returns invalid status for not existed board" {
            signIn()
            every { taskRepository.findTaskByTitle(title) } returns null
            every { boardRepository.findBoardById(any()) } returns null
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.createTask(title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description))
        }

        "create new task returns invalid status for issue with database" {
            signIn()
            every { taskRepository.findTaskByTitle(title) } returns null
            every { taskRepository.create(title, description, any(), any()) } returns false
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.createTask(title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "create new task returns success status" {
            signIn()
            every { taskRepository.findTaskByTitle(title) } returns null
            every { taskRepository.create(title, description, any(), any()) } returns true
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.createTask(title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "edit task returns invalid status for not logged in user" {
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.editTak(taskId, title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "edit task returns invalid status for not existed board" {
            signIn()
            every { boardRepository.findBoardById(any()) } returns null
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.editTak(taskId, title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description))
        }

        "edit task returns invalid status for not existed title" {
            signIn()
            every { taskRepository.findTaskById(any()) } returns null
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.editTak(taskId, title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.TASK_NOT_EXIST.description))
        }

        "edit task returns invalid status for issue with database" {
            signIn()
            every { taskRepository.editTask(any()) } returns false
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.editTak(taskId, title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "edit task returns success status" {
            signIn()
            every { taskRepository.editTask(any()) } returns true
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.editTak(taskId, title, description, boardId, userId)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }

        "remove task returns invalid status for not logged in user" {
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.removeTask(taskId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description))
        }

        "remove task returns invalid status because can not find task" {
            signIn()
            every { taskRepository.findTaskById(any()) } returns null
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.removeTask(taskId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.TASK_NOT_EXIST.description))
        }

        "remove task returns invalid status for issue with database" {
            signIn()
            every { taskRepository.deleteTask(any()) } returns false
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.removeTask(taskId)
            assertEquals(result, ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description))
        }

        "remove task returns success status" {
            signIn()
            every { taskRepository.deleteTask(any()) } returns true
            val taskManager = TaskManager(taskRepository, boardRepository, userRepository)
            val result = taskManager.removeTask(taskId)
            assertEquals(result, ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description))
        }
    }

    private fun signIn() {
        val login = "userLogin04"
        val password = "userPassword1@"
        every { cypher.decrypt(any()) } returns password
        accountManager.signIn(login, password)
    }
}