package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic
import java.time.LocalDate

object Mock {
    val request = OpprettJournalpostRequest(
        tittel = "",
        journalfoerendeEnhet = null,
        tema = null,
        journalposttype = Journalposttype.INNGAAENDE,
        kanal = "NAV_NO",
        bruker = Bruker("00000000000", IdType.FNR),
        eksternReferanseId = "#",
        avsenderMottaker = AvsenderMottaker(
            id = "000000000",
            idType = IdType.ORGNR,
            navn = "Arbeidsgiver",
        ),
        dokumenter = emptyList(),
        datoMottatt = LocalDate.now(),
    )

    val response = """
        {
            "journalpostId": "123",
            "journalpostFerdigstilt": true,
            "journalStatus": "STATUS",
            "melding": "",
            "dokumenter": []
        }
    """.trimIndent()
}

fun mockDokArkivClient(content: String, status: HttpStatusCode): DokArkivClient {
    val mockEngine = MockEngine {
        respond(
            content = content,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )
    }

    val mockHttpClient = HttpClient(mockEngine) { configure() }

    return mockStatic(::createHttpClient) {
        every { createHttpClient() } returns mockHttpClient
        DokArkivClient("mock url") { "mock access token" }
    }
}
