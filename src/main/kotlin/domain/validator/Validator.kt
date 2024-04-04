package domain.validator

class Validator {
    companion object {
        private const val loginPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])"
        private const val loginLength = 6
        private const val passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])"
        private const val passwordLength = 8
        private const val boardNameLength = 8


        fun loginValidate(login: String): String {
            return if (login.filterNot { it.isWhitespace() } == login) {
                if (login.length < loginLength) {
                    "login should have more than $loginLength characters "
                } else {
                    if (!login.contains(Regex(loginPattern))) {
                        "login should contains letter, number "
                    } else ""
                }
            } else "login should not contain white space "
        }

        fun passwordValidate(password: String): String {
            return if (password.filterNot { it.isWhitespace() } == password) {
                if (password.length < passwordLength) {
                    "password should have more than $passwordLength characters"
                } else {
                    if (!password.contains(Regex(passwordPattern))) {
                        "password should contains letter, number and special character"
                    } else ""
                }
            } else "login should not contain white space"
        }

        fun boardValidator(name: String): String {
            return if (name.length < boardNameLength) {
                "board should have more than $boardNameLength characters"
            } else ""
        }
    }
}
