package modules

import domain.logger.ILogger
import domain.serializer.ISerializer
import external.logger.MyLogger
import external.cypher.CezarEncrypt
import domain.cypher.IEncryptionService
import domain.data_base.*
import domain.data_base.impl.DataBaseConnectionConfig
import external.data_base.AccountRepository
import external.data_base.BoardRepository
import external.data_base.TaskRepository
import external.data_base.UserRepository
import external.gui.ConsoleApplication
import domain.gui.IGui
import domain.manager.IAccountManager
import domain.manager.IBoardManager
import domain.manager.ITaskManager
import domain.manager.impl.AccountManager
import domain.manager.impl.BoardManager
import domain.manager.impl.TaskManager
import external.serializer.GsonSerializer
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { AccountRepository() } bind IAccountRepository::class
    single { UserRepository() } bind IUserRepository::class
    single { BoardRepository() } bind IBoardRepository::class
    single { TaskRepository() } bind ITaskRepository::class

    single { AccountManager(get(), get(), get()) } bind IAccountManager::class
    single { BoardManager(get()) } bind IBoardManager::class
    single { TaskManager(get(), get(), get()) } bind ITaskManager::class
    single { CezarEncrypt() } bind IEncryptionService::class
    single { ConsoleApplication() } bind IGui::class
    single { GsonSerializer() } bind ISerializer::class
    single { DataBaseConnectionConfig("src/main/resources/connectionData.json") } bind IDataBaseConnectionConfig::class


    single { params -> MyLogger(className = params.getOrNull<String>()?.let { it } ?: "") } bind ILogger::class
}