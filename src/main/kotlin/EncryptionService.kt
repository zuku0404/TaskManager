interface EncryptionService {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}