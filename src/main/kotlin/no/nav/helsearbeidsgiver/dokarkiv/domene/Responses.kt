package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class OpprettOgFerdigstillResponse(
    val journalpostId: String,
    @JsonNames("journalpostferdigstilt")
    val journalpostFerdigstilt: Boolean,
    val melding: String? = null,
    val dokumenter: List<DokumentInfoId>,
)

@Serializable
data class DokumentInfoId(
    val dokumentInfoId: String,
)
