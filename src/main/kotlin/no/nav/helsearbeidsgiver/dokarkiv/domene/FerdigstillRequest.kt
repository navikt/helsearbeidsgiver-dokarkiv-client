package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
data class FerdigstillRequest(
    val journalfoerendeEnhet: String,
)
