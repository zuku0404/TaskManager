class CezarEncrypt: EncryptionService {
    private val SHIFT = 4
    private val NUMBER_SIGNS_IN_ASCII = 127

    override fun encrypt(password: String): String {
        val encryptedPassword = StringBuilder()
        for (element in password) {
            val sign = element
            if (sign.code + SHIFT > NUMBER_SIGNS_IN_ASCII) {
                encryptedPassword.append((sign.code - (NUMBER_SIGNS_IN_ASCII - SHIFT)).toChar())
            } else {
                encryptedPassword.append((sign.code + SHIFT).toChar())
            }
        }
        return encryptedPassword.toString()
    }

    override fun decrypt(encryptedPassword: String): String {
        val shift = 4
        val numberSignsInAscii = 127
        val password = StringBuilder()
        for (element in encryptedPassword) {
            val sign = element
            if (sign.code - shift < 0) {
                password.append((sign.code + (numberSignsInAscii - shift)).toChar())
            } else {
                password.append((sign.code - shift).toChar())
            }
        }
        return password.toString()
    }
}