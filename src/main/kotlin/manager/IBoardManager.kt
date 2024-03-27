package manager

import ActionResult
import model.Board

interface IBoardManager {
    fun createBoard(name: String): ActionResult
    fun getBoard(id: Long): Board?
    fun getAllBoards(): List<Board>
    fun editBoard(oldName: String, newName: String): ActionResult
    fun deleteBoard(id: Long): ActionResult
}