package domain.manager

import model.ActionResult
import model.Board
import model.Task
import model.User

interface ITaskManager {
    fun createTask(title: String, description: String = "",  boardId: Long, userId: Long?): ActionResult
    fun editTak(taskId: Long, title: String, description: String, boardId: Long, userId: Long?): ActionResult
    fun removeTask(taskId: Long): ActionResult
    fun getTasksForBoard(board: Board): List<Task>
    fun getTasksForUser(user: User?): List<Task>
    fun getAllTasks(): List<Task>
    fun showAllYourTasks(): List<Task>
}