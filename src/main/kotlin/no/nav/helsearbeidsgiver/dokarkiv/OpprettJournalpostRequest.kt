@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.dokarkiv

import kotlinx.serialization.UseSerializers
import java.time.LocalDate

/**
 * Oppretter en journalpost i Joark/dokarkiv, med eller uten dokumenter.
 *
 * Fullstendig dokumentasjon her: https://confluence.adeo.no/display/BOA/opprettJournalpost
 */
@kotlinx.serialization.Serializable
data class OpprettJournalpostRequest(
    /** Temaet som forsendelsen tilhører */
    val tema: String?,
    /** Brukeren som forsendelsen gjelder */
    val bruker: Bruker? = null,
    val journalposttype: Journalposttype,
    val avsenderMottaker: AvsenderMottaker? = null,

    /**
     * Tittel som beskriver forsendelsen samlet, feks "Ettersendelse til søknad om foreldrepenger".
     */
    val tittel: String?,

    /**
     * NAV-enheten som har journalført, eventuelt skal journalføre, forsendelsen.
     * Ved automatisk journalføring uten mennesker involvert skal enhet settes til "9999".
     * Konsument må sette journalfoerendeEnhet dersom tjenesten skal ferdigstille journalføringen.
     */
    val journalfoerendeEnhet: String?,

    /**
     * Hvilken kanal kommunikasjonen har foregått i, feks ALTINN, NAV_NO.
     * Liste over gyldige verdier:
     * https://confluence.adeo.no/display/BOA/Mottakskanal
     * https://confluence.adeo.no/display/BOA/Utsendingskanal
     */
    val kanal: String?,

    /**
     * Unik id for forsendelsen som kan brukes til sporing gjennom verdikjeden.
     */
    val eksternReferanseId: String?,

    val dokumenter: List<Dokument>,
    val sak: Sak? = null,
    val datoMottatt: LocalDate?,
    val behandlingsTema: String? = null,
)
