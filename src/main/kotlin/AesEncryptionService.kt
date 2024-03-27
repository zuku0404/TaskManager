import java.io.File
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesEncryptionService: EncryptionService {
    private val algorithm = "AES"
    private val salt = "3a9f5c86b1d048ed7c4a0f51e239d72f"

    override fun encrypt(text: String): String {
        val key = generateSecretKey()
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    override fun decrypt(text: String): String {
        val key = generateSecretKey()
        println("key $key")
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(text))
        return String(plainText)
    }

    private fun generateSecretKey(): SecretKey {
        val pass = "secretPass"
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(pass.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}