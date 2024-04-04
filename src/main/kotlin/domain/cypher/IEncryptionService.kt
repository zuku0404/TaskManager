package domain.cypher

interface IEncryptionService {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}