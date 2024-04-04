package external.logger

import domain.logger.ILogger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyLogger(val className: String) : ILogger {
    override fun log(msg: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        println("$current $className \"$msg\"")
    }
}