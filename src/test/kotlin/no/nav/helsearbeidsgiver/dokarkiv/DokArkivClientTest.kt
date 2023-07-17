package no.nav.helsearbeidsgiver.dokarkiv

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillResponse
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import java.time.LocalDate

class DokArkivClientTest : FunSpec({

    context("opprettOgFerdigstillJournalpost") {
        test("Journalpost opprettes og ferdigstilles") {
            val expected = mockOpprettOgFerdigstillResponse()

            val mockDokArkivClient = mockDokArkivClient(
                expected.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
                HttpStatusCode.OK,
            )

            val actual = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()

            actual shouldBe expected
        }

        test("Feiler ikke dersom journalpost opprettes, men ikke ferdigstilles") {
            val expected = mockOpprettOgFerdigstillResponse().copy(
                journalpostFerdigstilt = false,
            )

            val mockDokArkivClient = mockDokArkivClient(
                expected.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
                HttpStatusCode.OK,
            )

            val actual = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()

            actual shouldBe expected
        }
    }

    context("oppdaterJournalpost") {
        test("Journalpost oppdateres uten feil") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)

            shouldNotThrowAny {
                mockDokArkivClient.oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            }
        }
    }

    context("ferdigstillJournalpost") {
        test("Journalpost ferdigstilles uten feil") {
            val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.OK)

            shouldNotThrowAny {
                mockDokArkivClient.ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone")
            }
        }
    }

    context("Feilrespons") {
        listOf<Pair<String, suspend DokArkivClient.() -> Unit>>(
            "opprettOgFerdigstillJournalpost" to { opprettOgFerdigstillJournalpostMedMockInput() },
            "oppdaterJournalpost" to {
                oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            },
            "ferdigstillJournalpost" to { ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone") },
        )
            .forEach { (metodeNavn, metode) ->

                context(metodeNavn) {
                    test("BadRequest gir ClientRequestException med status BadRequest") {
                        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.BadRequest)

                        val e = shouldThrowExactly<ClientRequestException> {
                            mockDokArkivClient.metode()
                        }

                        e.response.status shouldBe HttpStatusCode.BadRequest
                    }

                    test("InternalServerError gir ServerResponseException med status InternalServerError") {
                        val mockDokArkivClient = mockDokArkivClient("", HttpStatusCode.InternalServerError)

                        val e = shouldThrowExactly<ServerResponseException> {
                            mockDokArkivClient.metode()
                        }

                        e.response.status shouldBe HttpStatusCode.InternalServerError
                    }
                }
            }
    }
})

private suspend fun DokArkivClient.opprettOgFerdigstillJournalpostMedMockInput(): OpprettOgFerdigstillResponse =
    opprettOgFerdigstillJournalpost(
        tittel = "",
        gjelderPerson = mockGjelderPerson(),
        avsender = mockAvsender(),
        datoMottatt = LocalDate.now(),
        dokumenter = emptyList(),
        eksternReferanseId = "",
        callId = "",
    )
