package no.nav.helsearbeidsgiver.dokarkiv

@kotlinx.serialization.Serializable
data class OpprettJournalpostResponse(
    val journalpostId: String,
    val journalpostFerdigstilt: Boolean,

    /**
     * Status på journalposten ihht denne listen:
     * https://confluence.adeo.no/display/BOA/Enum%3A+Journalstatus
     */
    val journalStatus: String,
    val melding: String? = null,
    val dokumenter: List<DokumentResponse>
)

@kotlinx.serialization.Serializable
data class DokumentResponse(
    val brevkode: String?,
    val dokumentInfoId: Int?,
    val tittel: String?
)
