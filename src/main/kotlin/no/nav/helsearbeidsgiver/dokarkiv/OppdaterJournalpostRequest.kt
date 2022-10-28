package no.nav.helsearbeidsgiver.dokarkiv
/**
 * val journalpostId = inngaendeJournalpost.journalpostId
 * val avsenderNr = inngaendeJournalpost.arbeidsgiverOrgnummer
 * ?: inngaendeJournalpost.arbeidsgiverPrivat
 * ?: throw RuntimeException("Mangler avsender")
 *
 * journalpostId = inngaendeJournalpost.journalpostId
 * avsender (id=avsenderNr og navn="Arbeidsgiver")
 * arkivSak (arkivSakId = inngaendeJournalpost.gsakId og arkivSakSystem = "FS22")
 *
 **/
@kotlinx.serialization.Serializable
data class OppdaterJournalpostRequest(
    val bruker: Bruker?,
    val avsenderMottaker: AvsenderMottaker?,
    val sak: Sak?
)
