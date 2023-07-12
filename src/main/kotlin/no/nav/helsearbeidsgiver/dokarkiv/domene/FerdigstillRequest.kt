package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
internal data class FerdigstillRequest(
    val journalfoerendeEnhet: String,
)
