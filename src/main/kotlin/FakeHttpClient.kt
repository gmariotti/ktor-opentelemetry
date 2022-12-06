import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import kotlin.random.Random

private val logger = LoggerFactory.getLogger(FakeHttpClient::class.java)

class FakeHttpClient(otel: OpenTelemetry) {
    private val tracer = otel.getTracer(FakeHttpClient::class.java.name)
    private val meter = otel.getMeter(FakeHttpClient::class.java.name)
    private val doWorkHistogram = meter.histogramBuilder("do-work").ofLongs().build()
    private val random = Random(42)

    suspend fun callClient() {
        tracer.trace("doWork") {
            it.addEvent("starting work")
            val delayTime = random.nextLong(5_000)
            withContext(singleThreadDispatcher) {
                logger.info("dispatched to ${Thread.currentThread().name}")
                tracer.trace("fakeCall") { innerSpan ->
                    delay(delayTime)
                    innerSpan.setStatus(StatusCode.OK)
                }
                it.addEvent("completed delay")
            }
            it.addEvent("work completed")
            doWorkHistogram.record(delayTime, Attributes.of(AttributeKey.stringKey("method"), "ping"))
        }
    }
}

suspend fun <T> Tracer.trace(spanName: String, block: suspend (Span) -> T): T {
    val span = spanBuilder(spanName).startSpan()
    return try {
        span.makeCurrent().use { block(span) }
    } finally {
        span.end()
    }
}

private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()