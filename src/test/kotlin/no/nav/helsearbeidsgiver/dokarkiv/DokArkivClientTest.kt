package no.nav.helsearbeidsgiver.dokarkiv

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import no.nav.helsearbeidsgiver.dokarkiv.domene.Kanal
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillResponse
import no.nav.helsearbeidsgiver.utils.json.toJson
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import java.time.LocalDate

class DokArkivClientTest : FunSpec({

    context("opprettOgFerdigstillJournalpost") {
        test("Journalpost opprettes og ferdigstilles") {
            val expected = mockOpprettOgFerdigstillResponse()

            val mockDokArkivClient = mockDokArkivClient(
                HttpStatusCode.OK to expected.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
            )

            val actual = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()

            actual shouldBe expected
        }

        test("Håndter konflikt (status 409) ved duplikat forespørsel") {
            val expected = mockOpprettOgFerdigstillResponse()
            val mockDokArkivClient = mockDokArkivClient(
                HttpStatusCode.Conflict to expected.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
            )
            val actual = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()
            actual.journalpostId shouldBe expected.journalpostId
        }

        test("Feiler ikke dersom journalpost opprettes, men ikke ferdigstilles") {
            val expected = mockOpprettOgFerdigstillResponse().copy(
                journalpostFerdigstilt = false,
            )

            val mockDokArkivClient = mockDokArkivClient(
                HttpStatusCode.OK to expected.toJsonStr(OpprettOgFerdigstillResponse.serializer()),
            )

            val actual = mockDokArkivClient.opprettOgFerdigstillJournalpostMedMockInput()

            actual shouldBe expected
        }
    }

    context("oppdaterJournalpost") {
        test("Journalpost oppdateres uten feil") {
            val mockDokArkivClient = mockDokArkivClient(HttpStatusCode.OK to "")

            shouldNotThrowAny {
                mockDokArkivClient.oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            }
        }
    }

    context("ferdigstillJournalpost") {
        test("Journalpost ferdigstilles uten feil") {
            val mockDokArkivClient = mockDokArkivClient(HttpStatusCode.OK to "")

            shouldNotThrowAny {
                mockDokArkivClient.ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone")
            }
        }
    }

    context("håndterer feil") {
        listOf<Pair<String, suspend DokArkivClient.() -> Unit>>(
            "opprettOgFerdigstillJournalpost" to { opprettOgFerdigstillJournalpostMedMockInput() },
            "oppdaterJournalpost" to {
                oppdaterJournalpost("jid-doven-isolasjon", mockGjelderPerson(), mockAvsender(), "cid-krigersk-hamster")
            },
            "ferdigstillJournalpost" to { ferdigstillJournalpost("jid-lystig-lemen", "cid-kjølig-krone") },
        )
            .forEach { (metodeNavn, metode) ->

                context(metodeNavn) {

                    test("feiler ved 4xx-feil") {
                        val mockDokArkivClient = mockDokArkivClient(HttpStatusCode.BadRequest to "")

                        val e = shouldThrowExactly<ClientRequestException> {
                            mockDokArkivClient.metode()
                        }

                        e.response.status shouldBe HttpStatusCode.BadRequest
                    }

                    test("lykkes ved færre 5xx-feil enn max retries (3)") {
                        val mockDokArkivClient =
                            mockDokArkivClient(
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.OK to mockOpprettOgFerdigstillResponse().toJson(OpprettOgFerdigstillResponse.serializer()).toString(),
                            )

                        runTest {
                            shouldNotThrowAny {
                                mockDokArkivClient.metode()
                            }
                        }
                    }

                    test("feiler ved flere 5xx-feil enn max retries (3)") {
                        val mockDokArkivClient =
                            mockDokArkivClient(
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.InternalServerError to "",
                                HttpStatusCode.InternalServerError to "",
                            )

                        runTest {
                            val e = shouldThrowExactly<ServerResponseException> {
                                mockDokArkivClient.metode()
                            }

                            e.response.status shouldBe HttpStatusCode.InternalServerError
                        }
                    }

                    test("kall feiler og prøver på nytt ved timeout") {
                        val mockDokArkivClient =
                            mockDokArkivClient(
                                HttpStatusCode.OK to "timeout",
                                HttpStatusCode.OK to "timeout",
                                HttpStatusCode.OK to "timeout",
                                HttpStatusCode.OK to mockOpprettOgFerdigstillResponse().toJson(OpprettOgFerdigstillResponse.serializer()).toString(),
                            )

                        runTest {
                            shouldNotThrowAny {
                                mockDokArkivClient.metode()
                            }
                        }
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
        kanal = Kanal.NAV_NO,
    )
