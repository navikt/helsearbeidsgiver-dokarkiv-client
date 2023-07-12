package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
data class OppdaterJournalpostRequest(
    val bruker: Bruker?,
    val avsenderMottaker: AvsenderMottaker?,
    val sak: Sak?,
)
