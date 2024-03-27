package manager.impl

import ActionResult
import Status
import data_base.IBoardRepository
import data_base.ITaskRepository
import data_base.IUserRepository
import manager.ITaskManager
import model.Board
import model.CurrentLoggedUser
import model.Task
import model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskManager : ITaskManager, KoinComponent {
    private val taskRepository : ITaskRepository by inject()
    private val boardRepository : IBoardRepository by inject()
    private val userRepository : IUserRepository by inject()

    override fun createTask(title: String, description: String, boardId: Long, userId: Long?): ActionResult {
        taskRepository.findTaskByTitle(title)?.let {
            return ActionResult(Status.INVALID, "change title it is exist")
        } ?: run {
            boardRepository.findBoardById(boardId)?.let {
                val user = userId?.let {userRepository.findUserById(userId)}
                taskRepository.create(title, description, user, it)
                return ActionResult(Status.SUCCESS)
            } ?: return ActionResult(Status.INVALID, "board not exist")
        }
    }


    override fun getTasksForBoard(board: Board): List<Task> {
        return taskRepository.findTasksForBoard(board)
    }

    override fun getTasksForUser(user: User?): List<Task> {
        return taskRepository.findTasksForUser(user)
    }

    override fun getAllTasks(): List<Task> {
        return taskRepository.findTasks()
    }

    override fun showAllYourTasks(): List<Task> {
        return taskRepository.findTasksForUser(CurrentLoggedUser.getInstance().getUser())
    }

    override fun editTak(taskId: Long, title: String, description: String, boardId: Long, userId: Long?): ActionResult {
        val task = taskRepository.findTaskById(taskId)?.apply {
            this.title = title
            this.description = description
            this.board = boardRepository.findBoardById(boardId)!!
            this.user = userId?.let { userRepository.findUserById(userId) }
        } ?: return ActionResult(Status.INVALID, "task not exist")
        taskRepository.editTask(task)
        return ActionResult(Status.SUCCESS)
    }

    override fun removeTask(taskId: Long): ActionResult {
        val task = taskRepository.findTaskById(taskId)
        return if (task != null) {
            taskRepository.deleteTask(task.id)
            ActionResult(Status.SUCCESS)
        } else {
            ActionResult(Status.INVALID, "task not exist")
        }
    }


}