package no.nav.helsearbeidsgiver.dokarkiv.domene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import no.nav.helsearbeidsgiver.utils.json.toJsonStr
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import no.nav.helsearbeidsgiver.utils.wrapper.Orgnr
import java.time.LocalDate
import java.util.UUID

class InnsynsReglerTest :
    FunSpec({

        test("Journalpost opprettes og ferdigstilles med innsynsregler VISES_MASKINELT_GODKJENT") {
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
                    kanal = Kanal.NAV_NO,
                    overstyrInnsynsregler = InnsynsRegler.VISES_MASKINELT_GODKJENT,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldContain
                """
                "overstyrInnsynsregler":"VISES_MASKINELT_GODKJENT"
                """.trimIndent()
        }

        test("Journalpost opprettes og ferdigstilles med innsynsregler VISES_MANUELT_GODKJENT") {
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
                    kanal = Kanal.NAV_NO,
                    overstyrInnsynsregler = InnsynsRegler.VISES_MANUELT_GODKJENT,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldContain
                """
                "overstyrInnsynsregler":"VISES_MANUELT_GODKJENT"
                """.trimIndent()
        }

        test("Journalpost opprettes og ferdigstilles uten innsynsregler") {
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
                    kanal = Kanal.NAV_NO,
                ).toJsonStr(OpprettOgFerdigstillRequest.serializer())

            request shouldNotContain "overstyrInnsynsregler"
        }
    })
