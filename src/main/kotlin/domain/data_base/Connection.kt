package domain.data_base

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Connection
import java.sql.DriverManager

object Connection : KoinComponent {
    private val dataBaseConnectionConfig: IDataBaseConnectionConfig by inject()

    fun getConnection(): Connection {
        Class.forName(dataBaseConnectionConfig.driver)
        return DriverManager.getConnection(
            dataBaseConnectionConfig.url,
            dataBaseConnectionConfig.user,
            dataBaseConnectionConfig.password
        )
    }
}