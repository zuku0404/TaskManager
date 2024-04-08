package domain.manager.impl

import model.ActionResultDescription
import model.ActionResult
import model.Status
import domain.validator.Validator
import domain.data_base.IBoardRepository
import domain.manager.IBoardManager
import model.Board
import model.CurrentLoggedUser
import org.koin.core.component.KoinComponent

class BoardManager(private val boardRepository: IBoardRepository): IBoardManager, KoinComponent {

    override fun createBoard(name: String): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            val trimmedName = name.trim()
            val resultOfValidation = Validator.boardValidator(trimmedName)
            if (resultOfValidation.isEmpty()) {
                boardRepository.findBoardByName(trimmedName)?.let {
                    ActionResult(Status.INVALID, ActionResultDescription.BOARD_NAME_EXIST.description)
                } ?: run {
                    if (boardRepository.createBoard(trimmedName)) {
                        ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                    } else {
                        ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                    }
                }
            } else {
                ActionResult(Status.INVALID, resultOfValidation)
            }
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun editBoard(oldName: String, newName: String): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            val resultOfValidation = Validator.boardValidator(newName)
            if (resultOfValidation.isEmpty()) {
                boardRepository.findBoardByName(oldName)?.let {
                    if (boardRepository.editBoard(Board(it.id, newName))) {
                        ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                    } else {
                        ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                    }
                } ?: ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description)
            } else {
                ActionResult(Status.INVALID, resultOfValidation)
            }
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun deleteBoard(id: Long): ActionResult {
        return CurrentLoggedUser.getInstance().getUser()?.let {
            boardRepository.findBoardById(id)?.let {
                if (boardRepository.deleteBoardById(id)) {
                    ActionResult(Status.SUCCESS, ActionResultDescription.SUCCESS.description)
                } else {
                    ActionResult(Status.INVALID, ActionResultDescription.FAIL_SAVE_CHANGES_DB.description)
                }
            } ?: ActionResult(Status.INVALID, ActionResultDescription.BOARD_NOT_EXIST.description)
        } ?: ActionResult(Status.INVALID, ActionResultDescription.NO_USER_LOGGED_IN.description)
    }

    override fun getBoard(id: Long): Board? {
        return boardRepository.findBoardById(id)
    }

    override fun getAllBoards(): List<Board> {
        return boardRepository.findAllBoards()
    }
}