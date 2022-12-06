import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.instrumentation.ktor.v2_0.KtorServerTracing

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson()
        }
        val otel = GlobalOpenTelemetry.get()
        install(KtorServerTracing) {
            setOpenTelemetry(otel)
        }
        routing(otel)
    }.start(wait = true)
}