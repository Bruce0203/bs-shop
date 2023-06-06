import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.example.application.model.databaseModule
import org.example.application.mainModule
import kotlin.test.assertEquals
import kotlin.test.*

class ArticleTest {

    @Test
    fun testRoot() = testApplication {
        this.application {
            databaseModule()
            mainModule()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}