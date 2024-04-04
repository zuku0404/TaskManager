package external.gui

import domain.logger.ILogger
import model.Status
import domain.gui.IGui
import domain.manager.IAccountManager
import domain.manager.IBoardManager
import domain.manager.ITaskManager
import model.Board
import model.Task
import model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.properties.Delegates

class ConsoleApplication : IGui, KoinComponent {
    private val accountManager: IAccountManager by inject()
    private val boardManager: IBoardManager by inject()
    private val taskManager: ITaskManager by inject()
    private val logger: ILogger by inject { parametersOf(this::class.simpleName) }
    private var logIn = false

    override fun show() {
        logger.log("Welcome to Jira Application ")
        while (true) {
            showWelcomeScreen()
            var option by Delegates.notNull<Int>()
            while (readln().toIntOrNull()?.let { if(it !in 1..2) null else option = it } == null) {
                logger.log("incorrect data try again")
            }

            when (option) {
                1 -> {
                    if (showLogInMenu()) {
                        logIn = true
                        while (logIn){
                            showMainMenu()
                            handleMainMenuSelection()
                        }
                    }
                }

                2 -> {
                    logger.log("Write your login: ")
                    val login = readln()
                    logger.log("Write your password:")
                    val password = readln()
                    logger.log("Write your first name:")
                    val firstName = readln()
                    logger.log("Write your last name:")
                    val lastName = readln()
                    val result = accountManager.signUp(login, password, firstName, lastName)
                    logger.log("${result.status} ${result.msg}")
                }
            }
        }
    }

    private fun showWelcomeScreen() {
        logger.log("what would you like to do? Please enter num?")
        logger.log("1| sign in")
        logger.log("2| sign up")
    }

    private fun showLogInMenu(): Boolean {
        var response: Boolean
        do {
            logger.log("Enter your login and below enter your password ")
            logger.log("login: ")
            val login = readln()
            logger.log("password: ")
            val password = readln()
            val result = accountManager.signIn(login, password)
            if (result.status == Status.SUCCESS) {
                return true
            } else {
                logger.log(result.msg)
                response = awaitResponseAndProceed()
            }
        } while (response)
        return false
    }


    private fun showMainMenu() {
        println()
        logger.log("1| add new board")
        logger.log("2| see all exist boards")
        logger.log("3| show tasks for board")
        logger.log("4| delete board")
        logger.log("5| show task for user")
        logger.log("6| show all task")
        logger.log("7| add task")
        logger.log("8| edit task")
        logger.log("9| remove task")
        logger.log("10| show all your tasks")
        logger.log("11| sign out")
        println()
    }

    private fun handleMainMenuSelection() {
        var option by Delegates.notNull<Int>()
        while (readln().toIntOrNull()?.let { option = it } == null && option !in 1..6) {
            logger.log("not correct input try again")
        }
        when (option) {
            1 -> {
                var correctAnswer = false
                while (!correctAnswer) {
                    logger.log("Write name of board which you want to create")
                    val boardName = readln()
                    val result = boardManager.createBoard(boardName)
                    logger.log("${result.status} ${result.msg}")
                    correctAnswer = if (result.status != Status.SUCCESS) {
                        !awaitResponseAndProceed()
                    } else true
                }
            }

            2 -> {
                boardManager.getAllBoards().forEach { logger.log("${it.id}) ${it.name}") }
            }

            3 -> {
                boardManager.getAllBoards().forEach { logger.log("${it.id}) ${it.name}") }
                logger.log("Write id of board which you want to see tasks")
                lateinit var board: Board
                while (readln().toIntOrNull()
                        ?.let { boardManager.getBoard(it.toLong())?.let { b -> board = b } } == null
                ) {
                    logger.log("not correct data try again")
                }
                showTasksForList(taskManager.getTasksForBoard(board))
            }

            4 -> {
                logger.log("Write id of board which you want to delete remember you delete all task which belong to this board")
                while (readln().toIntOrNull()?.let { option = it } == null) {
                    logger.log("not correct data try again")
                }
                val result = boardManager.deleteBoard(option.toLong())
                logger.log("${result.status} ${result.msg}")

            }

            5 ->  {
                logger.log("Write id of user which you want to see tasks 0 -> to unknown")
                var user: User? = null
                while (readln().toIntOrNull()?.let { user = accountManager.getUser(it.toLong()) } == null) {
                    logger.log("not correct data try again")
                }
                showTasksForList(taskManager.getTasksForUser(user))
            }

            6 -> {
                showTasksForList(taskManager.getAllTasks())
            }

            7 -> {
                logger.log("USERS")
                accountManager.getAllUsers().forEach{logger.log("id: ${it.id} first name: ${it.name} last name: ${it.lastName}")}
                logger.log("BOARDS")
                boardManager.getAllBoards().forEach { logger.log("${it.id}) ${it.name}")}
                logger.log("title: ")
                val title = readln()
                logger.log("description: ")
                val description = readln()
                logger.log("to which user do you want assign task write id: ")
                var userId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { userId = it } == null) {
                    logger.log("incorrect try again")
                }

                logger.log("to which board do you want assign task write id: ")
                lateinit var board: Board
                while (readln().toIntOrNull()
                        ?.let { boardManager.getBoard(it.toLong())?.let { b -> board = b } } == null
                ) {
                    logger.log("not correct data try again")
                }
                val result = taskManager.createTask(title, description,board.id, userId.toLong())
                logger.log("${result.status} ${result.msg}")
            }

            8 -> {
                showTasksForList(taskManager.getAllTasks())
                logger.log("Write number of task which you want edit")
                var taskId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { taskId = it } == null) {
                    logger.log("incorrect try again")
                }
                logger.log("USERS")
                accountManager.getAllUsers().forEach{logger.log("id: ${it.id} first name: ${it.name} last name: ${it.lastName}")}
                logger.log("BOARDS")
                boardManager.getAllBoards().forEach { logger.log("${it.id}) ${it.name}")}
                logger.log("title: ")
                val title = readln()
                logger.log("description: ")
                val description = readln()
                logger.log("to which user do you want assign task write id: ")
                var userId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { userId = it } == null) {
                    logger.log("incorrect try again")
                }
                logger.log("to which board do you want assign task write id: ")
                lateinit var board: Board
                while (readln().toIntOrNull()
                        ?.let { boardManager.getBoard(it.toLong())?.let { b -> board = b } } == null
                ) {
                    logger.log("not correct data try again")
                }
                val result = taskManager.editTak(taskId.toLong(), title, description,board.id, userId.toLong())
                logger.log("${result.status} ${result.msg}")
            }

            9 -> {
                showTasksForList(taskManager.getAllTasks())
                logger.log("Write number of task which you want delete")
                var taskId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { taskId = it } == null) {
                    logger.log("incorrect try again")
                }
                val result = taskManager.removeTask(taskId.toLong())
                logger.log("${result.status} ${result.msg}")

            }

            10 -> {
                showTasksForList(taskManager.showAllYourTasks())
            }

            11 -> {
                accountManager.signOut()
                logIn = false
            }
        }
    }

    private fun awaitResponseAndProceed(): Boolean {
        lateinit var answer: String
        do {
            logger.log("Do you want try again? (N/Y)")
            answer = readln().uppercase()
        } while (answer != "Y" && answer != "N")
        return answer == "Y"
    }

    private fun showTasksForList(taskList: List<Task>){
        logger.log(String.format("%-10s%-35s%-75s%-20s%-20s%-20s","ID", "TITLE", "DESCRIPTION", "USER_NAME", "USER_LASTNAME", "BOARD"))
        taskList.forEach {logger.log(String.format("%-10s%-35s%-75s%-20s%-20s%-20s",
            it.id, it.title, it.description, it.user?.name ?: "UNKNOWN", it.user?.lastName ?: "UNKNOWN", it.board.name))
        }
    }
}