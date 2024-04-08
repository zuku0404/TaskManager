package domain.validator

import model.ValidationDescription

class Validator {
    companion object {
        private const val loginPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])"
        private const val passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])"
        const val loginLength = 6
        const val passwordLength = 8
        const val boardNameLength = 8


        fun loginValidate(login: String): String {
            return if (login.filterNot { it.isWhitespace() } == login) {
                if (login.length < loginLength) {
                    ValidationDescription.LOGIN_LENGTH.description
                } else {
                    if (!login.contains(Regex(loginPattern))) {
                        ValidationDescription.LOGIN_WITHOUT_NUMBER_AND_LETTER.description
                    } else
                        ValidationDescription.LOGIN_SUCCESS.description
                }
            } else
                ValidationDescription.LOGIN_WHITESPACE.description
        }

        fun passwordValidate(password: String): String {
            return if (password.filterNot { it.isWhitespace() } == password) {
                if (password.length < passwordLength) {
                    ValidationDescription.PASSWORD_LENGTH.description
                } else {
                    if (!password.contains(Regex(passwordPattern))) {
                        ValidationDescription.PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER.description
                    } else
                        ValidationDescription.PASSWORD_SUCCESS.description
                }
            } else
                ValidationDescription.PASSWORD_WHITESPACE.description
        }

        fun boardValidator(name: String): String {
            return if (name.length < boardNameLength) {
                ValidationDescription.BOARD_LENGTH.description
            } else
                ValidationDescription.BOARD_SUCCESS.description
        }
    }
}
