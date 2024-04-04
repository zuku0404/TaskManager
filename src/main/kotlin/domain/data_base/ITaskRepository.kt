package domain.data_base

import model.Board
import model.Task
import model.User

interface ITaskRepository {
    fun create(title: String, description: String, user: User?, board: Board): Boolean
    fun findTaskByTitle(title: String): Task?
    fun findTasksForBoard(board: Board): List<Task>
    fun findTasksForUser(user: User?): List<Task>
    fun findTaskById(taskId: Long): Task?
    fun findTasks(): List<Task>
    fun editTask(task: Task): Boolean
    fun deleteTask(id: Long): Boolean
}