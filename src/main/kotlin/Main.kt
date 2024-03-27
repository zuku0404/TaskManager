import data_base.impl.TaskRepository
import data_base.impl.UserRepository
import gui.IGui
import model.Account
import model.CurrentLoggedUser
import model.User
import modules.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
  startKoin {
    modules(appModule)
  }
   val koinHolder = object: KoinComponent {
     val app: IGui by inject()
   }
  koinHolder.app.show()


}
