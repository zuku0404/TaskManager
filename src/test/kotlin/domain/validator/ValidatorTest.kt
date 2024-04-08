package domain.validator

import AppTestConfig
import model.ValidationDescription
import kotlin.test.assertEquals

class ValidatorTest: AppTestConfig() {
    init {
        "login validator for login with white space" {
            val loginsWithWhiteSpace = listOf(
                "           ",
                "           z",
                "z           ",
                "12345 ",
                " z o d s t ",
            )
            loginsWithWhiteSpace.forEach{
                val result = Validator.loginValidate(it)
                assertEquals(result, ValidationDescription.LOGIN_WHITESPACE.description)
            }
        }

        "login validator for too low number of sign (6)" {
            val loginsWithTooLowNumberOfSign = listOf(
                "",
                "z",
                "zz",
                "zzz",
                "zzzz",
                "zzzzz",
            )
            loginsWithTooLowNumberOfSign.forEach{
                val result = Validator.loginValidate(it)
                assertEquals(result, ValidationDescription.LOGIN_LENGTH.description)
            }
        }

        "login validator without number and latter" {
            val loginsWithoutNumberLetter = listOf(
                "asdkmdkasd",
                "2324141412",
                "@#@$#@$#@$@#",
                "@#@kfdsmfdm",
                "@#@32423432",
            )
            loginsWithoutNumberLetter.forEach{
                val result = Validator.loginValidate(it)
                assertEquals(result,  ValidationDescription.LOGIN_WITHOUT_NUMBER_AND_LETTER.description)
            }
        }

        "login validator for correct logins" {
            val loginsCorrect = listOf(
                "a12345",
                "12345a",
                "aaaaa1",
                "1aaaaa",
                "dsa23a",
                "2323k323",
                "fsdfsdlfk32423423",
                "324324fsdfsf",
            )

            loginsCorrect.forEach{
                val result = Validator.loginValidate(it)
                assertEquals(result,  ValidationDescription.LOGIN_SUCCESS.description)
            }
        }

        "password validator for password with white space" {
            val passwordsWithWhiteSpace = listOf(
                "           ",
                "           z",
                "z           ",
                "123455454 ",
                " z o d s t  34 24 424  242 2",
            )
            passwordsWithWhiteSpace.forEach{
                val result = Validator.passwordValidate(it)
                assertEquals(result, ValidationDescription.PASSWORD_WHITESPACE.description)
            }
        }

        "password validator for too low number of sign (8)" {
            val passwordsWithTooLowNumberOfSign = listOf(
                "",
                "z",
                "zz",
                "zzz",
                "zzzz",
                "zzzzz",
                "zzzzzz",
                "zzzzzzz",
            )
            passwordsWithTooLowNumberOfSign.forEach{
                val result = Validator.passwordValidate(it)
                assertEquals(result, ValidationDescription.PASSWORD_LENGTH.description)
            }
        }

        "password validator without number latter and special character" {
            val passwordsWithoutNumberLetterCharacter = listOf(
                "asdkmdkasd",
                "2324141412",
                "@#@$#@$#@$@#",
                "@#@kfdsmfdm",
                "@#@32423432",
                "fsdfsdf34234234",
            )
            passwordsWithoutNumberLetterCharacter.forEach{
                val result = Validator.passwordValidate(it)
                assertEquals(result,  ValidationDescription.PASSWORD_WITHOUT_NUMBER_AND_LETTER_AND_SPECIAL_CHARACTER.description)
            }
        }

        "password validator for correct passwords" {
            val passwordsCorrect = listOf(
                "a12345#@",
                "123#$45a",
                "1aaaaa#1",
                "1aaaaa#@",
                "dsa2$@$@3a",
                "2323k32#3",
                "fsdfs$#dlfk32423423",
                "324324fsd#f#s#f",
            )

            passwordsCorrect.forEach{
                val result = Validator.passwordValidate(it)
                assertEquals(result,  ValidationDescription.PASSWORD_SUCCESS.description)
            }
        }

        "board validator for too low number of sign (8)" {
            val boardsWithTooLowNumberOfSign = listOf(
                "",
                "z",
                "zz",
                "zzz",
                "zzzz",
                "zzzzz",
                "zzzzzz",
                "zzzzzzz",
                )

            boardsWithTooLowNumberOfSign.forEach{
                val result = Validator.boardValidator(it)
                assertEquals(result, ValidationDescription.BOARD_LENGTH.description)
            }
        }

        "board validator for correct boards" {
            val boardsCorrect = listOf(
                "a12345#@",
                "123#$45a",
                "1aaaaa#1",
                "1aaaaa#@",
                "dsa2$@$@3a",
                "2323k32#3",
                "fsdfs$#dlfk32423423",
                "324324fsd#f#s#f",
                "               ",
                "s               a"
            )

            boardsCorrect.forEach{
                val result = Validator.boardValidator(it)
                assertEquals(result,  ValidationDescription.BOARD_SUCCESS.description)
            }
        }

    }
}