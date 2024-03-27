package gui

import Status
import manager.IAccountManager
import manager.IBoardManager
import manager.ITaskManager
import model.Board
import model.Task
import model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.Delegates

class ConsoleApplication : IGui, KoinComponent {
    private val accountManager: IAccountManager by inject()
    private val boardManager: IBoardManager by inject()
    private val taskManager: ITaskManager by inject()
    private var logIn = false

    override fun show() {
        print("Welcome to Jira Application ")
        while (true) {
            showWelcomeScreen()
            var option by Delegates.notNull<Int>()
            while (readln().toIntOrNull()?.let { if(it !in 1..2) null else option = it } == null) {
                println("incorrect data try again")
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
                    print("Write your login: ")
                    val login = readln()
                    print("Write your password:")
                    val password = readln()
                    print("Write your first name:")
                    val firstName = readln()
                    print("Write your last name:")
                    val lastName = readln()
                    println(accountManager.signUp(login, password, firstName, lastName))
                }
            }
        }
    }

    private fun showWelcomeScreen() {
        println("what would you like to do? Please enter num?")
        println("1| sign in")
        println("2| sign up")
    }

    private fun showLogInMenu(): Boolean {
        var response: Boolean
        do {
            println("Enter your login and below enter your password ")
            print("login: ")
            val login = readln()
            print("password: ")
            val password = readln()
            val result = accountManager.signIn(login, password)
            if (result.status == Status.SUCCESS) {
                return true
            } else {
                println(result.msg)
                response = awaitResponseAndProceed()
            }
        } while (response)
        return false
    }


    private fun showMainMenu() {
        println()
        println("1| add new board")
        println("2| see all exist boards")
        println("3| show tasks for board")
        println("4| delete board")
        println("5| show task for user")
        println("6| show all task")
        println("7| add task")
        println("8| edit task")
        println("9| remove task")
        println("10| show all your tasks")
        println("11| sign out")
        println()
    }

    private fun handleMainMenuSelection() {
        var option by Delegates.notNull<Int>()
        while (readln().toIntOrNull()?.let { option = it } == null && option !in 1..6) {
            println("not correct input try again")
        }
        when (option) {
            1 -> {
                var correctAnswer = false
                while (!correctAnswer) {
                    println("Write name of board which you want to create")
                    val boardName = readln()
                    val result = boardManager.createBoard(boardName)
                    println(result)
                    correctAnswer = if (result.status != Status.SUCCESS) {
                        !awaitResponseAndProceed()
                    } else true
                }
            }

            2 -> {
                boardManager.getAllBoards().forEach { println("${it.id}) ${it.name}") }
            }

            3 -> {
                boardManager.getAllBoards().forEach { println("${it.id}) ${it.name}") }
                println("Write id of board which you want to see tasks")
                lateinit var board: Board
                while (readln().toIntOrNull()
                        ?.let { boardManager.getBoard(it.toLong())?.let { b -> board = b } } == null
                ) {
                    println("not correct data try again")
                }
                showAllTasks(taskManager.getTasksForBoard(board))
            }

            4 -> {
                println("Write id of board which you want to delete remember you delete all task which belong to this board")
                while (readln().toIntOrNull()?.let { option = it } == null) {
                    println("not correct data try again")
                }
                println(boardManager.deleteBoard(option.toLong()))
            }

            5 ->  {
                println("Write id of user which you want to see tasks 0 -> to unknown")
                var user: User? = null
                while (readln().toIntOrNull()?.let { user = accountManager.getUser(it.toLong()) } == null) {
                    println("not correct data try again")
                }
                showAllTasks(taskManager.getTasksForUser(user))
            }

            6 -> {
                showAllTasks(taskManager.getAllTasks())
            }

            7 -> {
                setDataForTask()
            }

            8 -> {
                showAllTasks(taskManager.getAllTasks())
                println("Write number of task which you want edit")
                var taskId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { taskId = it } == null) {
                    println("incorrect try again")
                }
                setDataForTask(taskId.toLong())
            }

            9 -> {
                showAllTasks(taskManager.getAllTasks())
                println("Write number of task which you want delete")
                var taskId by Delegates.notNull<Int>()
                while (readln().toIntOrNull()?.let { taskId = it } == null) {
                    println("incorrect try again")
                }
                println(taskManager.removeTask(taskId.toLong()))
            }

            10 -> {
                showAllTasks(taskManager.showAllYourTasks())
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
            println("Do you want try again? (N/Y)")
            answer = readln().uppercase()
        } while (answer != "Y" && answer != "N")
        return answer == "Y"
    }

    private fun showAllTasks(taskList: List<Task>){
        println(String.format("%-10s%-35s%-75s%-20s%-20s%-20s\n","ID", "TITLE", "DESCRIPTION", "USER_NAME", "USER_LASTNAME", "BOARD"))
        taskList.forEach {println(String.format("%-10s%-35s%-75s%-20s%-20s%-20s",
            it.id, it.title, it.description, it.user?.name ?: "UNKNOWN", it.user?.lastName ?: "UNKNOWN", it.board.name))
        }
    }

    private fun setDataForTask(taskId: Long? = null) {
        println("USERS")
        accountManager.getAllUsers().forEach{println("id: ${it.id} first name: ${it.name} last name: ${it.lastName}")}
        println()
        println("BOARDS")
        boardManager.getAllBoards().forEach { println("${it.id}) ${it.name}")}
        println()
        print("title: ")
        val title = readln()
        print("description: ")
        val description = readln()
        print("to which user do you want assign task write id: ")
        var userId by Delegates.notNull<Int>()
        while (readln().toIntOrNull()?.let { userId = it } == null) {
            println("incorrect try again")
        }

        print("to which board do you want assign task write id: ")
        lateinit var board: Board
        while (readln().toIntOrNull()
                ?.let { boardManager.getBoard(it.toLong())?.let { b -> board = b } } == null
        ) {
            println("not correct data try again")
        }
        taskId?.let {
            println(taskManager.editTak(it,title, description,board.id, userId.toLong()))
        } ?: println(taskManager.createTask(title, description,board.id, userId.toLong()))
    }
}