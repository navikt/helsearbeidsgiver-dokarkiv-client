package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.*

fun buildHttpClientText(status: HttpStatusCode, text: String = ""): HttpClient {
    return HttpClient(MockEngine) {
        install(Logging)
        install(ContentNegotiation) {
            json()
            // serializer = buildJacksonSerializer()
        }
        expectSuccess = true
        engine {
            addHandler {
                respond(
                    text,
                    headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                    status = status
                )
            }
        }
    }
}
