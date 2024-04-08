package external.gui

import domain.logger.ILogger
import model.Status
import domain.gui.IGui
import domain.manager.IAccountManager
import domain.manager.IBoardManager
import domain.manager.ITaskManager
import model.Task
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
        var boardIndex by Delegates.notNull<Int>()
        var userIndex by Delegates.notNull<Int>()
        var taskIndex by Delegates.notNull<Int>()
        var interrupted = false

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
                boardManager.getAllBoards().forEachIndexed { index, board ->  logger.log("${index + 1}| ${board.name}") }
            }

            3 -> {
                val boards = boardManager.getAllBoards()
                boards.forEachIndexed { index, board ->  logger.log("${index + 1}| ${board.name}") }
                logger.log("Write number of board which you want to see tasks")
                while (readln().toIntOrNull()?.let { if (it in 1..boards.size) {boardIndex = it - 1 } else null } == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("Write number of board which you want to see tasks")
                    }
                }
                if (!interrupted) {
                    showTasksForList(taskManager.getTasksForBoard(boards[boardIndex]))
                }

            }

            4 -> {
                val boards = boardManager.getAllBoards()
                boards.forEachIndexed { index, board ->  logger.log("${index + 1}| ${board.name}") }
                logger.log("Write number of board which you want to delete. Remember you delete all task which belong to this board")
                while (readln().toIntOrNull()?.let { if (it in 1..boards.size) {boardIndex = it - 1 } else null } == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("Write number of board which you want to delete. Remember you delete all task which belong to this board")
                    }
                }
                if (!interrupted) {
                    val result = boardManager.deleteBoard(boards[boardIndex].id)
                    logger.log("${result.status} ${result.msg}")
                }
            }

            5 ->  {
                logger.log("Write id of user which you want to see tasks")
                val users = accountManager.getAllUsers()
                logger.log("0| UNKNOWN USER")
                users.forEachIndexed { index, user ->  logger.log("${index + 1}| ${user.name}") }
                while (readln().toIntOrNull()?.let { if (it in 0..users.size) {userIndex = it - 1 } else null } == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("Write id of user which you want to see tasks")
                    }
                }
                if (!interrupted) {
                    val user = if (userIndex < 0) null else users[userIndex]
                    showTasksForList(taskManager.getTasksForUser(user))
                }
            }

            6 -> {
                showTasksForList(taskManager.getAllTasks())
            }

            7 -> {
                logger.log("USERS")
                val users = accountManager.getAllUsers()
                logger.log("0| UNKNOWN USER")
                users.forEachIndexed { index, user ->  logger.log("${index + 1}| first name: ${user.name} last name: ${user.lastName}") }

                logger.log("BOARDS")
                val boards = boardManager.getAllBoards()
                boards.forEachIndexed { index, board ->  logger.log("${index + 1}| ${board.name}") }

                logger.log("title: ")
                val title = readln()
                logger.log("description: ")
                val description = readln()

                logger.log("to which user do you want assign task write number: ")
                while (readln().toIntOrNull()?.let { if (it in 0..users.size) {userIndex = it - 1 } else null } == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("to which user do you want assign task write number: ")
                    }
                }

                if (!interrupted) {
                    val user = if (userIndex < 0) null else users[userIndex]
                    logger.log("to which board do you want assign task write number: ")
                    while (readln().toIntOrNull()?.let { if (it in 1..boards.size) {boardIndex = it - 1 } else null } == null) {
                        logger.log("incorrect data try again")
                        if (!awaitResponseAndProceed()) {
                            interrupted = true
                            break
                        } else {
                            logger.log("to which board do you want assign task write number: ")
                        }
                    }
                    if (!interrupted) {
                        val result = taskManager.createTask(title, description, boards[boardIndex].id, user?.id)
                        logger.log("${result.status} ${result.msg}")
                    }
                }
            }

            8 -> {
                val tasks = taskManager.getAllTasks()
                showTasksForList(tasks)
                logger.log("Write number of task which you want edit")
                while (readln().toIntOrNull()?.let { if (it in 1..tasks.size) taskIndex = it - 1 else null } == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("Write number of task which you want edit")
                    }
                }

                if (!interrupted) {
                    logger.log("USERS")
                    val users = accountManager.getAllUsers()
                    logger.log("0| UNKNOWN USER")
                    users.forEachIndexed { index, user -> logger.log("${index + 1}| first name: ${user.name} last name: ${user.lastName}") }

                    logger.log("BOARDS")
                    val boards = boardManager.getAllBoards()
                    boards.forEachIndexed { index, board -> logger.log("${index + 1}| ${board.name}") }

                    logger.log("title: ")
                    val title = readln()
                    logger.log("description: ")
                    val description = readln()

                    logger.log("to which user do you want assign task write number: ")
                    while (readln().toIntOrNull()?.let { if (it in 0..users.size) { userIndex = it - 1 } else null } == null) {
                        logger.log("incorrect data try again")
                        if (!awaitResponseAndProceed()) {
                            interrupted = true
                            break
                        } else {
                            logger.log("to which user do you want assign task write number: ")
                        }
                    }

                    if (!interrupted) {
                        val user = if (userIndex < 0) null else users[userIndex]
                        logger.log("to which board do you want assign task write number: ")
                        while (readln().toIntOrNull()?.let { if (it in 1..boards.size) { boardIndex = it - 1 } else null } == null) {
                            logger.log("incorrect data try again")
                            if (!awaitResponseAndProceed()) {
                                interrupted = true
                                break
                            } else {
                                logger.log("to which board do you want assign task write number: ")
                            }
                        }

                        if (!interrupted) {
                            val result = taskManager.editTak(tasks[taskIndex].id, title, description, boards[boardIndex].id, user?.id)
                            logger.log("${result.status} ${result.msg}")
                        }
                    }
                }
            }

            9 -> {
                val tasks = taskManager.getAllTasks()
                showTasksForList(tasks)
                logger.log("Write number of task which you want delete")
                while (readln().toIntOrNull()?.let { if(it in 1.. tasks.size) taskIndex = it - 1 else null} == null) {
                    logger.log("incorrect data try again")
                    if (!awaitResponseAndProceed()) {
                        interrupted = true
                        break
                    } else {
                        logger.log("Write number of task which you want delete")
                    }
                }

                if (!interrupted) {
                    val result = taskManager.removeTask(tasks[taskIndex].id)
                    logger.log("${result.status} ${result.msg}")
                }
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
            print("answer: ")
            answer = readln().uppercase()
        } while (answer != "Y" && answer != "N")
        return answer == "Y"
    }

    private fun showTasksForList(taskList: List<Task>) {
        logger.log(String.format("%-10s%-35s%-75s%-20s%-20s%-20s","ID", "TITLE", "DESCRIPTION", "USER_NAME", "USER_LASTNAME", "BOARD"))
        taskList.forEachIndexed { index, task -> logger.log(String.format("%-10s%-35s%-75s%-20s%-20s%-20s",
            index + 1, task.title, task.description, task.user?.name ?: "UNKNOWN", task.user?.lastName ?: "UNKNOWN", task.board.name))
        }
    }
}