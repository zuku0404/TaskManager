package external.data_base

import domain.data_base.Connection
import domain.data_base.IBoardRepository
import domain.data_base.ITaskRepository
import domain.data_base.IUserRepository
import model.Board
import model.Task
import model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Types

class TaskRepository : ITaskRepository, KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val boardRepository: IBoardRepository by inject()

    override fun create(title: String, description: String, user: User?, board: Board): Boolean {
        val sql = "INSERT INTO tasks (title, description, user_id, board_id) VALUES(?,?,?,?)"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, title)
            prepareStatement.setString(2, description)
            user?.let { user -> prepareStatement.setLong(3, user.id) } ?:
            prepareStatement.setNull(3, Types.NULL)
            prepareStatement.setLong(4, board.id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }

    override fun findTaskByTitle(title: String): Task? {
        val sql = "SELECT task_id, description, user_id, board_id FROM tasks WHERE title = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, title)
            val resultSet = prepareStatement.executeQuery()
            return if (resultSet.next()) {
                val taskId = resultSet.getLong(1)
                val taskDescription = resultSet.getString(2)
                val userId = resultSet.getLong(3)
                val user = if (userId == 0L) null else userRepository.findUserById(userId)
                val boardName = resultSet.getLong(4)
                val board = boardRepository.findBoardById(boardName)
                Task(taskId, title, taskDescription, user, board!!)
            } else null
        }
    }

    override fun findTaskById(taskId: Long): Task? {
        val sql = "SELECT title, description, user_id, board_id FROM tasks WHERE task_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, taskId)
            val resultSet = prepareStatement.executeQuery()
            return if (resultSet.next()) {
                val title = resultSet.getString(1)
                val taskDescription = resultSet.getString(2)
                val userId = resultSet.getLong(3)
                val user = if (userId == 0L) null else userRepository.findUserById(userId)
                val boardName = resultSet.getLong(4)
                val board = boardRepository.findBoardById(boardName)
                Task(taskId, title, taskDescription, user, board!!)
            } else null
        }
    }

    override fun findTasks(): List<Task> {
        val sql = "SELECT task_id, title, description, user_id, board_id FROM tasks"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            val resultSet = prepareStatement.executeQuery()
            val tasks = mutableListOf<Task>()
            while (resultSet.next()) {
                val taskId = resultSet.getLong(1)
                val title = resultSet.getString(2)
                val description = resultSet.getString(3)
                val userId = resultSet.getLong(4)
                val user = if (userId != 0L) userRepository.findUserById(userId) else null
                val boardId = resultSet.getLong(5)
                val board = boardRepository.findBoardById(boardId)
                tasks.add(Task(taskId, title, description, user, board!!))
            }
            return tasks
        }
    }

    override fun findTasksForBoard(board: Board): List<Task> {
        val sql = "SELECT task_id, title, description, user_id, board_id FROM tasks WHERE board_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, board.id)
            val resultSet = prepareStatement.executeQuery()
            val boardTasks = mutableListOf<Task>()
            while (resultSet.next()) {
                val taskId = resultSet.getLong(1)
                val title = resultSet.getString(2)
                val description = resultSet.getString(3)
                val userId = resultSet.getLong(4)
                val user = if (userId != 0L) userRepository.findUserById(userId) else null
                boardTasks.add(Task(taskId, title, description, user, board))
            }
            return boardTasks
        }
    }

    override fun findTasksForUser(user: User?): List<Task> {
        val sql = "SELECT task_id, title, description, user_id, board_id FROM tasks WHERE user_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            user?.let { prepareStatement.setLong(1, user.id) } ?:
            prepareStatement.setNull(1, Types.NULL)
            val resultSet = prepareStatement.executeQuery()
            val userTasks = mutableListOf<Task>()
            while (resultSet.next()) {
                val taskId = resultSet.getLong(1)
                val title = resultSet.getString(2)
                val description = resultSet.getString(3)
                val boardId = resultSet.getLong(5)
                val board = boardRepository.findBoardById(boardId)
                userTasks.add(Task(taskId, title, description, user, board!!))
            }
            return userTasks
        }
    }

    override fun editTask(task: Task): Boolean {
        val sql = "UPDATE tasks SET title = ?, description = ?, user_id = ?, board_id = ? WHERE task_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, task.title)
            prepareStatement.setString(2, task.description)
            task.user?.let { user -> prepareStatement.setLong(3, user.id) } ?:
            prepareStatement.setNull(3, Types.NULL)
            prepareStatement.setLong(4, task.board.id)
            prepareStatement.setLong(5, task.id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }

    override fun deleteTask(id: Long): Boolean {
        val sql = "DELETE FROM tasks WHERE task_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }
}