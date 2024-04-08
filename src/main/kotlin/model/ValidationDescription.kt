package model

import domain.validator.Validator

enum class ValidationDescription(val description: String) {
    LOGIN_WHITESPACE("login should not contain white space "),
    LOGIN_LENGTH(" login should have more than ${Validator.loginLength} characters "),
    LOGIN_WITHOUT_NUMBER_AND_LETTER("login should contains letter, number "),
    LOGIN_SUCCESS(""),
    PASSWORD_WHITESPACE("password should not contain white space "),
    PASSWORD_LENGTH(" password should have more than ${Validator.passwordLength} characters"),
    PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER("password should contains letter, number and special character"),
    PASSWORD_SUCCESS(""),
    BOARD_LENGTH("board should have more than ${Validator.boardNameLength} characters"),
    BOARD_SUCCESS(""),
}