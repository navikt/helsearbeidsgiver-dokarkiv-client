package no.nav.helsearbeidsgiver.dokarkiv

@kotlinx.serialization.Serializable
data class FerdigstillRequest(
    val journalfoerendeEnhet: String
)
