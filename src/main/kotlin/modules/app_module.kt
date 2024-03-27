package modules

import AesEncryptionService
import CezarEncrypt
import EncryptionService
import data_base.IAccountRepository
import data_base.IBoardRepository
import data_base.ITaskRepository
import data_base.IUserRepository
import data_base.impl.AccountRepository
import data_base.impl.BoardRepository
import data_base.impl.TaskRepository
import data_base.impl.UserRepository
import gui.ConsoleApplication
import gui.IGui
import manager.IAccountManager
import manager.IBoardManager
import manager.ITaskManager
import manager.impl.AccountManager
import manager.impl.BoardManager
import manager.impl.TaskManager
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { AccountRepository() } bind IAccountRepository::class
    single { UserRepository() } bind IUserRepository::class
    single { BoardRepository() } bind IBoardRepository::class
    single { TaskRepository() } bind ITaskRepository::class

    single { AccountManager() } bind IAccountManager::class
    single { BoardManager() } bind IBoardManager::class
    single { TaskManager() } bind ITaskManager::class
    single { CezarEncrypt() } bind EncryptionService::class
    single { ConsoleApplication() } bind IGui::class
}