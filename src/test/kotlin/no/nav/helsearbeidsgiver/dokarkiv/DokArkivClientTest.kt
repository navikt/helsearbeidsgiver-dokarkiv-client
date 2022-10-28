@file:Suppress("NonAsciiCharacters")

package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.helsearbeidsgiver.tokenprovider.AccessTokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class DokArkivClientTest {

    private val mockStsClient = mockk<AccessTokenProvider>(relaxed = true)
    private val request = OpprettJournalpostRequest(
        tittel = "",
        journalposttype = Journalposttype.INNGAAENDE,
        kanal = "NAV_NO",
        bruker = Bruker("00000000000", IdType.FNR),
        eksternReferanseId = "#",
        avsenderMottaker = AvsenderMottaker(
            id = "000000000",
            idType = IdType.ORGNR,
            navn = "Arbeidsgiver"
        ),
        dokumenter = emptyList(),
        datoMottatt = LocalDate.now()
    )
    private val journalpostResponse = """
            {
                "journalpostId": "123",
                "journalpostFerdigstilt": true,
                "journalStatus": "STATUS",
                "melding": "",
                "dokumenter": []
            }
    """.trimIndent()

    @Suppress("NonAsciiCharacters")
    @Test
    fun `Skal ferdigstille journalpost når man får status OK`() {
        val dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.OK, ""))
        val resultat = runBlocking {
            dokArkivClient.ferdigstillJournalpost("111", "1001")
        }
        assertEquals("", resultat)
    }

    @Test
    fun `Skal håndtere at ferdigstilling av journalpost feiler`() {
        val dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.InternalServerError, ""))
        assertThrows<Exception> {
            runBlocking {
                dokArkivClient.ferdigstillJournalpost("111", "1001")
            }
        }
    }

    @Test
    fun `Skal opprette journalpost`() {
        val dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.OK, journalpostResponse))
        val response = runBlocking {
            dokArkivClient.opprettJournalpost(request, false, "1001")
        }
        assertEquals("123", response.journalpostId)
    }

    @Test
    fun `Skal håndtere at opprett journalpost feiler`() {
        val dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.InternalServerError, journalpostResponse))
        assertThrows<DokArkivException> {
            runBlocking {
                dokArkivClient.opprettJournalpost(request, false, "1001")
            }
        }
    }
}
