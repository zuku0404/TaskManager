package domain.manager.impl

import model.ActionResultDescription
import domain.data_base.IBoardRepository
import domain.data_base.ITaskRepository
import domain.data_base.IUserRepository
import domain.manager.ITaskManager
import model.*
import org.koin.core.component.KoinComponent

class TaskManager(
    private val taskRepository: ITaskRepository,
    private val boardRepository: IBoardRepository,
    private val userRepository: IUserRepository
) : ITaskManager, KoinComponent {

    override fun createTask(title: String, description: String, boardId: Long, userId: Long?): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            taskRepository.findTaskByTitle(title)?.let {
                ActionResult(Status.INVALID, ActionResultDescription.TASK_NAME_EXIST.description)
            } ?: run {
                boardRepository.findBoardById(boardId)?.let {
                    val user = userId?.let { userRepository.findUserById(userId) }
                    if (taskRepository.create(title, description, user, it)) {
                        ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                    } else {
                        ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                    }
                } ?: ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description)
            }
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun editTak(taskId: Long, title: String, description: String, boardId: Long, userId: Long?): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            boardRepository.findBoardById(boardId)?.let { board ->
                taskRepository.findTaskById(taskId)?.let {
                    it.title = title
                    it.description = description
                    it.board = board
                    it.user = userId?.let { userRepository.findUserById(userId) }
                    if (taskRepository.editTask(it)) {
                        ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                    } else {
                        ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                    }
                } ?: ActionResult(Status.INVALID, ActionResultDescription.TASK_NOT_EXIST.description)
            } ?: ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun removeTask(taskId: Long): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            taskRepository.findTaskById(taskId)?.let {
                if (taskRepository.deleteTask(it.id)) {
                    ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                } else {
                    ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                }
            } ?: ActionResult(Status.INVALID, ActionResultDescription.TASK_NOT_EXIST.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
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

}