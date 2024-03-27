package manager

import ActionResult
import model.Board
import model.Task
import model.User

interface ITaskManager {
    fun createTask(title: String, description: String = "",  boardId: Long, userId: Long?): ActionResult
    fun getTasksForBoard(board: Board): List<Task>
    fun getTasksForUser(user: User?): List<Task>
    fun getAllTasks(): List<Task>
    fun showAllYourTasks(): List<Task>
    fun editTak(taskId: Long, title: String, description: String, boardId: Long, userId: Long?): ActionResult
    fun removeTask(taskId: Long): ActionResult
}