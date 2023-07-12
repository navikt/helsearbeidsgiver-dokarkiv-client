@file:Suppress("NonAsciiCharacters")

package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DokArkivClientTest {

    @Test
    fun `Skal ferdigstille journalpost når man får status OK`() {
        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)
        val resultat = runBlocking {
            mockDokArkivClient.ferdigstillJournalpost("111", "1001")
        }
        assertEquals("", resultat)
    }

    @Test
    fun `Skal håndtere at ferdigstilling av journalpost feiler`() {
        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)
        assertThrows<Exception> {
            runBlocking {
                mockDokArkivClient.ferdigstillJournalpost("111", "1001")
            }
        }
    }

    @Test
    fun `Skal opprette journalpost`() {
        val mockDokArkivClient = mockDokArkivClient(Mock.response, HttpStatusCode.OK)
        val response = runBlocking {
            mockDokArkivClient.opprettJournalpost(Mock.request, false, "1001")
        }
        assertEquals("123", response.journalpostId)
    }

    @Test
    fun `Skal håndtere at opprett journalpost feiler`() {
        val mockDokArkivClient = mockDokArkivClient(Mock.response, HttpStatusCode.InternalServerError)
        assertThrows<DokArkivException> {
            runBlocking {
                mockDokArkivClient.opprettJournalpost(Mock.request, false, "1001")
            }
        }
    }
}
