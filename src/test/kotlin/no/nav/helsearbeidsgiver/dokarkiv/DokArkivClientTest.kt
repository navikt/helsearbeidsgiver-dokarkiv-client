package no.nav.helsearbeidsgiver.dokarkiv

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillResponse
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import java.io.IOException
import java.time.LocalDate

class DokArkivClientTest : FunSpec({

    context("opprettOgFerdigstillJournalpost") {
        test("Journalpost opprettes og ferdigstilles") {
            val mockDokArkivClient = mockDokArkivClient(
                Mock.opprettOgFerdigstillResponse.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
                HttpStatusCode.OK,
            )

            val response = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()

            response shouldBe Mock.opprettOgFerdigstillResponse
        }

        test("BadRequest gir ClientRequestException med status BadRequest som wrappes med status i DokArkivException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.BadRequest)

            val e = shouldThrowExactly<DokArkivException> {
                mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()
            }

            e.message shouldBe "Klarte ikke opprette journalpost! Status: 400"
        }

        test("InternalServerError gir ServerResponseException med status InternalServerError som wrappes _uten_ status i DokArkivException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)

            val e = shouldThrowExactly<DokArkivException> {
                mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()
            }

            e.message shouldBe "Klarte ikke opprette journalpost! Status: null"
        }
    }

    context("oppdaterJournalpost") {
        test("Journalpost oppdateres uten feil") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)

            shouldNotThrowAny {
                mockDokArkivClient.oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            }
        }

        test("BadRequest gir ClientRequestException med status BadRequest som wrappes i RuntimeException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.BadRequest)

            val e = shouldThrowExactly<RuntimeException> {
                mockDokArkivClient.oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            }

            e.message shouldBe "oppdatering: Fikk http status 400 Bad Request. journalpostId=[jid-doven-isolasjon] callId=[cid-krigersk-hamster]"
        }

        test("InternalServerError gir ServerResponseException med status InternalServerError som wrappes i IOException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)

            val e = shouldThrowExactly<IOException> {
                mockDokArkivClient.oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            }

            e.message shouldBe "Dokarkiv svarte med feilmelding ved oppdatering av journalpost. " +
                "journalpostId=[jid-doven-isolasjon] callId=[cid-krigersk-hamster]"
        }
    }

    context("ferdigstillJournalpost") {
        test("Journalpost ferdigstilles uten feil") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)

            shouldNotThrowAny {
                mockDokArkivClient.ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone")
            }
        }

        test("BadRequest gir ClientRequestException med status BadRequest som wrappes i RuntimeException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.BadRequest)

            val e = shouldThrowExactly<RuntimeException> {
                mockDokArkivClient.ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone")
            }

            e.message shouldBe "ferdigstilling: Fikk http status 400 Bad Request. journalpostId=[jid-lystig-lemen] callId=[cid-kjølig-krone]"
        }

        test("InternalServerError gir ServerResponseException med status InternalServerError som wrappes i IOException") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)

            val e = shouldThrowExactly<IOException> {
                mockDokArkivClient.ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone")
            }

            e.message shouldBe "Dokarkiv svarte med feilmelding ved ferdigstilling av journalpost. journalpostId=[jid-lystig-lemen] callId=[cid-kjølig-krone]"
        }
    }
})

private suspend fun DokArkivClient.opprettOgFerdigstillJournalpostMedMockInput(): OpprettOgFerdigstillResponse =
    opprettOgFerdigstillJournalpost(
        behandlingsTema = "",
        tittel = "",
        gjelderPerson = mockGjelderPerson(),
        avsender = mockAvsender(),
        datoMottatt = LocalDate.now(),
        dokumenter = emptyList(),
        eksternReferanseId = "",
        callId = "",
    )
