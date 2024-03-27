package data_base.impl

import data_base.Connection
import data_base.IBoardRepository
import model.Board

class BoardRepository : IBoardRepository {
    override fun createBoard(name: String): Boolean {
        val sql = "INSERT INTO boards (name) VALUES(?)"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, name)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }

    override fun findBoardByName(name: String): Board? {
        val sql = "SELECT board_id FROM boards WHERE name = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, name)
            val resultSet = prepareStatement.executeQuery()
            return if (resultSet.next()) {
                val boardId = resultSet.getLong(1)
                Board(boardId, name)
            } else null
        }
    }

    override fun findBoardById(boardId: Long): Board? {
        val sql = "SELECT name FROM boards WHERE board_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, boardId)
            val resultSet = prepareStatement.executeQuery()
            if (resultSet.next()) {
                val name = resultSet.getString(1)
                return Board(boardId, name)
            } else return null
        }
    }

    override fun findAllBoards(): List<Board> {
        val sql = "SELECT * FROM boards ORDER BY board_id "
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            val resultSet = prepareStatement.executeQuery()
            val boards = mutableListOf<Board>()
            while (resultSet.next()){
                val id = resultSet.getLong(1)
                val name = resultSet.getString(2)
                boards.add(Board(id,name))
            }
            return boards
        }
    }

    override fun editBoard(board: Board): Boolean {
        val sql = "UPDATE task SET name = ? WHERE board_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setString(1, board.name)
            prepareStatement.setLong(2, board.id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }

    override fun deleteBoardById(id: Long): Boolean {
        val sql = "DELETE FROM boards WHERE board_id = ?"
        Connection.getConnection().use {
            val prepareStatement = it.prepareStatement(sql)
            prepareStatement.setLong(1, id)
            val executeUpdate = prepareStatement.executeUpdate()
            return executeUpdate > 0
        }
    }
}