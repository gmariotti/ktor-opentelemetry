import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.opentelemetry.api.OpenTelemetry

fun Application.routing(otel: OpenTelemetry) {
    routing {
        val client = FakeHttpClient(otel)

        get("/ping") {
            client.callClient()
            call.respond(HttpStatusCode.OK, "pong")
        }
    }
}
