package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestRetryConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import no.nav.helsearbeidsgiver.utils.json.jsonConfig

internal fun createHttpClient(): HttpClient =
    HttpClient(Apache5) { configure() }

internal fun HttpClientConfig<*>.configure() {
    expectSuccess = true

    install(ContentNegotiation) {
        json(jsonConfig)
    }

    install(HttpRequestRetry) { configureRetry() }

    install(HttpTimeout) {
        connectTimeoutMillis = 500
        requestTimeoutMillis = 500
        socketTimeoutMillis = 500
    }
}

internal fun HttpRequestRetryConfig.configureRetry() {
    retryOnException(
        maxRetries = 3,
        retryOnTimeout = true,
    )
    exponentialDelay()
}

internal fun HttpRequestBuilder.navCallId(callId: String) {
    header("Nav-Call-Id", callId)
}
