package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.helsearbeidsgiver.tokenprovider.AccessTokenProvider
import no.nav.syfo.client.dokarkiv.DokArkivClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DokArkivClientTest {

    private val mockStsClient = mockk<AccessTokenProvider>(relaxed = true)

    private lateinit var dokArkivClient: DokArkivClient

    @Test
    fun `Skal ferdigstille journalpost når man får status OK`() {
        dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.OK, ""))
        val resultat = runBlocking {
            dokArkivClient.ferdigstillJournalpost("111", "1001")
        }
        assertEquals("", resultat)
    }

    @Test
    fun `Skal håndtere at ferdigstilling av journalpost feiler`() {
        dokArkivClient = DokArkivClient("", mockStsClient, buildHttpClientText(HttpStatusCode.InternalServerError, ""))
        assertThrows<Exception> {
            runBlocking {

                dokArkivClient.ferdigstillJournalpost("111", "1001")
            }
        }
    }
}
