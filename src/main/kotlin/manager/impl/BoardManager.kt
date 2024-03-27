package manager.impl

import ActionResult
import Status
import Validator
import data_base.IBoardRepository
import manager.IBoardManager
import model.Board
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BoardManager: IBoardManager, KoinComponent {
    private val boardRepository: IBoardRepository by inject()

    override fun createBoard(name: String): ActionResult {
        val trimmedName = name.trim()
        val resultOfValidation = Validator.boardValidator(trimmedName)
        if (resultOfValidation.isEmpty()) {
            boardRepository.findBoardByName(trimmedName)?.let {
                return ActionResult(Status.INVALID, "name exist")
            } ?: run{
                boardRepository.createBoard(trimmedName)
                return ActionResult(Status.SUCCESS)
            }
        } else {
         return ActionResult(Status.INVALID,resultOfValidation)
        }
    }

    override fun getBoard(id: Long): Board? {
        return boardRepository.findBoardById(id)
    }

    override fun getAllBoards(): List<Board> {
        return boardRepository.findAllBoards()
    }

    override fun editBoard(oldName: String, newName: String): ActionResult {
        val resultOfValidation = Validator.boardValidator(newName)
        if (resultOfValidation.isEmpty()) {
            boardRepository.findBoardByName(oldName)?.let {
                boardRepository.editBoard(Board(it.id, newName))
                return ActionResult(Status.INVALID, "board not exist")
            } ?: return ActionResult(Status.SUCCESS)
        } else {
            return ActionResult(Status.INVALID, resultOfValidation)
        }
    }

    override fun deleteBoard(id: Long): ActionResult {
        val board = boardRepository.findBoardById(id)
        return if (board != null) {
            boardRepository.deleteBoardById(id)
            ActionResult(Status.SUCCESS)
        } else ActionResult(Status.INVALID, "board not exist")
    }
}