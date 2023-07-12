package no.nav.helsearbeidsgiver.dokarkiv.domene

@kotlinx.serialization.Serializable
data class FerdigstillRequest(
    val journalfoerendeEnhet: String,
)
