package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
data class OpprettOgFerdigstillResponse(
    val journalpostId: String,
    val journalpostFerdigstilt: Boolean,
    val melding: String,
    val dokumenter: List<DokumentInfoId>,
)

@Serializable
data class DokumentInfoId(
    val dokumentInfoId: String,
)
