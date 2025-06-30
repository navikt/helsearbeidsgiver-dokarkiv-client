package no.nav.helsearbeidsgiver.dokarkiv

import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.nav.helsearbeidsgiver.dokarkiv.domene.Avsender
import no.nav.helsearbeidsgiver.dokarkiv.domene.DokumentInfoId
import no.nav.helsearbeidsgiver.dokarkiv.domene.GjelderPerson
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillResponse
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun mockDokArkivClient(vararg responses: Pair<HttpStatusCode, String>): DokArkivClient {
    val mockEngine = MockEngine.create {
        reuseHandlers = false
        requestHandlers.addAll(
            responses.map { (status, content) ->
                {
                    if (content == "timeout") {
                        // Skrur den virtuelle klokka fremover, nok til at timeout forårsakes
                        dispatcher.shouldNotBeNull().testCoroutineScheduler.advanceTimeBy(1)
                    }
                    respond(
                        content = content,
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            },
        )
    }

    val mockHttpClient = HttpClient(mockEngine) { configure() }

    return mockStatic(::createHttpClient) {
        every { createHttpClient() } returns mockHttpClient
        DokArkivClient("mock url") { "mock access token" }
    }
}

fun mockOpprettOgFerdigstillResponse(): OpprettOgFerdigstillResponse =
    OpprettOgFerdigstillResponse(
        journalpostId = "jid-klassisk-pære",
        journalpostFerdigstilt = true,
        melding = "Ha en fin dag!",
        dokumenter = listOf(
            DokumentInfoId(
                dokumentInfoId = "dok-id-den-første",
            ),
            DokumentInfoId(
                dokumentInfoId = "dok-id-den-andre",
            ),
        ),
    )

fun mockGjelderPerson(): GjelderPerson =
    GjelderPerson(
        fnr = "fnr-apekatt",
    )

fun mockAvsender(): Avsender =
    Avsender.Organisasjon(
        orgnr = "orgnr-isenkram",
        navn = "Iskrem og isenkram AS",
    )
