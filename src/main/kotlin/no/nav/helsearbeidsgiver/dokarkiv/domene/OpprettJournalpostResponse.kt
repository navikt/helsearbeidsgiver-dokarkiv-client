package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
data class OpprettJournalpostResponse(
    val journalpostId: String,
    val journalpostFerdigstilt: Boolean? = null,

    /**
     * Status på journalposten ihht denne listen:
     * https://confluence.adeo.no/display/BOA/Enum%3A+Journalstatus
     */
    val journalStatus: String? = null,
    val melding: String? = null,
    val dokumenter: List<DokumentResponse>,
)

@Serializable
data class DokumentResponse(
    val brevkode: String? = null,
    val dokumentInfoId: Int? = null,
    val tittel: String? = null,
)
