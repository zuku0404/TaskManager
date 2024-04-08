package external.cypher

import AppTestConfig
import org.junit.jupiter.api.Assertions.assertNotEquals
import kotlin.test.assertEquals

class CezarEncryptTest: AppTestConfig() {
    val password = "cypherForTest"
    val cezarEncrypt = CezarEncrypt()
    val incorrectPassword = "cypherBad"
    init {
        "diffrent encrypted and decrypted password" {
            val encrypt = cezarEncrypt.encrypt(password)
            val decrypt = cezarEncrypt.decrypt(incorrectPassword)
            assertNotEquals(password,decrypt)
        }

        "equals encrypted and decrypted password" {
            val encrypt = cezarEncrypt.encrypt(password)
            val decrypt = cezarEncrypt.decrypt(encrypt)
            assertEquals(password, decrypt)
        }
    }
}