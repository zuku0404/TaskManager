package domain.data_base

import model.Board

interface IBoardRepository {
    fun createBoard(name: String): Boolean
    fun findBoardByName(name: String): Board?
    fun findBoardById(boardId: Long): Board?
    fun findAllBoards(): List<Board>
    fun editBoard(board: Board): Boolean
    fun deleteBoardById(id: Long): Boolean
}