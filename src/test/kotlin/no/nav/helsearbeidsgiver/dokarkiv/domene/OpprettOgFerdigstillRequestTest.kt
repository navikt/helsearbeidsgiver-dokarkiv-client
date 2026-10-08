package no.nav.helsearbeidsgiver.dokarkiv.domene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.time.LocalDate
import java.util.UUID

class OpprettOgFerdigstillRequestTest :
    FunSpec({

        test("Journalpost opprettes og ferdigstilles med kanal NAV_NO") {
            val datoMottatt = LocalDate.of(2024, 1, 1)
            val request =
                OpprettOgFerdigstillRequest(
                    tittel = "test",
                    bruker = Bruker(Fnr.genererGyldig().verdi, IdType.FNR),
                    avsenderMottaker =
                        AvsenderMottaker(
                            Orgnr.genererGyldig().verdi,
                            IdType.ORGNR,
                            "Gjensidig Tiger AS",
                        ),
                    datoMottatt = datoMottatt,
                    dokumenter = emptyList(),
                    eksternReferanseId = UUID.randomUUID().toString(),
                    kanal = Kanal.NAV_NO,
                    journalposttype = Journalposttype.INNGAAENDE,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldContain
                """
                "tittel":"test"
                """.trimIndent()

            request shouldContain
                """
                "kanal":"NAV_NO"
                """.trimIndent()

            request shouldContain
                """
                "tema":"SYK"
                """.trimIndent()

            request shouldContain
                """
                "journalfoerendeEnhet":"9999"
                """.trimIndent()

            request shouldContain
                """
                "datoMottatt":"$datoMottatt"
                """.trimIndent()

            request shouldContain
                """
                "journalposttype":"INNGAAENDE"
                """.trimIndent()
        }

        test("Journalpost opprettes og ferdigstilles med kanal HR_SYSTEM_API") {
            val request =
                OpprettOgFerdigstillRequest(
                    tittel = "test",
                    bruker = Bruker(Fnr.genererGyldig().verdi, IdType.FNR),
                    avsenderMottaker =
                        AvsenderMottaker(
                            Orgnr.genererGyldig().verdi,
                            IdType.ORGNR,
                            "Gjensidig Tiger AS",
                        ),
                    datoMottatt = LocalDate.now(),
                    dokumenter = emptyList(),
                    eksternReferanseId = UUID.randomUUID().toString(),
                    kanal = Kanal.HR_SYSTEM_API,
                    journalposttype = Journalposttype.INNGAAENDE,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldContain
                """
                "kanal":"HR_SYSTEM_API"
                """.trimIndent()
        }

        test("Utgående journalpost sender datoMottatt som null") {
            val request =
                OpprettOgFerdigstillRequest(
                    tittel = "test",
                    bruker = Bruker(Fnr.genererGyldig().verdi, IdType.FNR),
                    avsenderMottaker =
                        AvsenderMottaker(
                            Orgnr.genererGyldig().verdi,
                            IdType.ORGNR,
                            "Gjensidig Tiger AS",
                        ),
                    datoMottatt = null,
                    dokumenter = emptyList(),
                    eksternReferanseId = UUID.randomUUID().toString(),
                    kanal = Kanal.HR_SYSTEM_API,
                    journalposttype = Journalposttype.UTGAAENDE,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldContain
                """
                "datoMottatt":null
                """.trimIndent()

            request shouldContain
                """
                "journalposttype":"UTGAAENDE"
                """.trimIndent()
        }
    })
