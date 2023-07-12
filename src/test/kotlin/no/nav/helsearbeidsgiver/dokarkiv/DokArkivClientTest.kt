package no.nav.helsearbeidsgiver.dokarkiv

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode

class DokArkivClientTest : FunSpec({

    test("Skal ferdigstille journalpost når man får status OK") {
        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)

        val resultat = mockDokArkivClient.ferdigstillJournalpost("111", "1001")

        resultat shouldBe ""
    }

    test("Skal håndtere at ferdigstilling av journalpost feiler") {
        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)

        shouldThrowAny {
            mockDokArkivClient.ferdigstillJournalpost("111", "1001")
        }
    }

    test("Skal opprette journalpost") {
        val mockDokArkivClient = mockDokArkivClient(Mock.response, HttpStatusCode.OK)

        val response = mockDokArkivClient.opprettJournalpost(Mock.request, false, "1001")

        response.journalpostId shouldBe "123"
    }

    test("Skal håndtere at opprett journalpost feiler") {
        val mockDokArkivClient = mockDokArkivClient(Mock.response, HttpStatusCode.InternalServerError)

        shouldThrowExactly<DokArkivException> {
            mockDokArkivClient.opprettJournalpost(Mock.request, false, "1001")
        }
    }
})
