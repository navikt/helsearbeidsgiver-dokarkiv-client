@file:UseSerializers(LocalDateSerializer::class)

package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.LocalDateSerializer
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class OpprettOgFerdigstillRequest(
    /** Tittel som beskriver forsendelsen samlet, feks "Ettersendelse til søknad om foreldrepenger". */
    val tittel: String,
    /** Brukeren som forsendelsen gjelder */
    val bruker: Bruker,
    val avsenderMottaker: AvsenderMottaker,
    val datoMottatt: LocalDate,
    val dokumenter: List<Dokument>,
    /** Unik id for forsendelsen som kan brukes til sporing gjennom verdikjeden. */
    val eksternReferanseId: String,
    /** Hvilken mottakskanal dokumentet er sendt inn gjennom feks NAV_NO for skjemaer på nav.no **/
    val kanal: Kanal,
) {
    @EncodeDefault
    val tema = "SYK"

    @EncodeDefault
    val journalposttype = "INNGAAENDE"

    @EncodeDefault
    val journalfoerendeEnhet = AUTOMATISK_JOURNALFOERING_ENHET

    @EncodeDefault
    val sak = GenerellSak()
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class OppdaterRequest(
    val bruker: Bruker,
    val avsenderMottaker: AvsenderMottaker,
) {
    @EncodeDefault
    val sak = GenerellSak()
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal class FerdigstillRequest {
    @EncodeDefault
    val journalfoerendeEnhet = AUTOMATISK_JOURNALFOERING_ENHET
}
