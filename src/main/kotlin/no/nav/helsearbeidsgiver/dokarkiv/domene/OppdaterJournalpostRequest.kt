package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
internal data class OppdaterJournalpostRequest(
    val bruker: Bruker?,
    val avsenderMottaker: AvsenderMottaker?,
    val sak: Sak?,
)
