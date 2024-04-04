package domain.data_base.impl

import domain.serializer.ISerializer
import domain.data_base.IDataBaseConnectionConfig
import model.ConnectionData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class DataBaseConnectionConfig(private val configurationFilePath: String): IDataBaseConnectionConfig, KoinComponent {
    private val serializer: ISerializer by inject()
    private var data: ConnectionData

    init {
        val dataFromFile = File(configurationFilePath).bufferedReader().use {
            it.readLine()
        }
        data = serializer.fromJson(dataFromFile, ConnectionData::class.java)!!
    }

    override val driver: String = data.driver
    override val url: String = data.url
    override val user: String = data.user
    override val password: String = data.password
}