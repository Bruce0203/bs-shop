import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.example.application.mainModule
import kotlin.test.*

class AuthTest {

    @Test
    fun testRoot() = testApplication {
        this.application {
            mainModule()
        }
        val response = client.get("/") {
            header(HttpHeaders.Authorization, "Bearer abc123")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }
}