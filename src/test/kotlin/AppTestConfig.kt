import io.kotest.core.spec.style.StringSpec
import modules.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

open class AppTestConfig: KoinComponent, StringSpec() {
    init {
        beforeSpec {
            startKoin {
                modules(appModule)
            }
        }
    }
}