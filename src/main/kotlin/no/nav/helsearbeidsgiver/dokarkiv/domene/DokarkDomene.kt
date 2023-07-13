package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

// NAV-enheten som personen som utfører journalføring jobber for. Ved automatisk journalføring uten
// mennesker involvert, skal enhet settes til "9999".
internal const val AUTOMATISK_JOURNALFOERING_ENHET = "9999"

internal enum class IdType {
    FNR,
    ORGNR,
}

/** Bruker er den posteringen gjelder */
@Serializable
internal data class Bruker(
    /** Fnr eller orgnr */
    val id: String,
    /** Hva som er i id-feltet */
    val idType: IdType,
)

@Serializable
internal data class AvsenderMottaker(
    /** Fnr eller orgnr */
    val id: String,
    /** Hva som er i id-feltet */
    val idType: IdType,
    /** Navn er påkrevd for ferdigstilling, enten personnavn eller virksomhetsnavn */
    val navn: String?,
)

/**
 * GENERELL_SAK kan brukes for dokumenter som skal journalføres, men som ikke tilhører en konkret fagsak.
 * Generell sak kan ses på som brukerens "mappe" på et gitt tema.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal class GenerellSak {
    @EncodeDefault
    val sakstype = "GENERELL_SAK"
}

@Serializable
data class Dokument(
    /**
     * Dokumentets tittel, f.eks. "Søknad om foreldrepenger ved fødsel" eller "Legeerklæring".
     * Dokumentets tittel blir synlig i brukers journal på nav.no, samt i NAVs fagsystemer.
     */
    val tittel: String,
    /**
     * Kode som sier noe om dokumentets innhold og oppbygning.
     * For inngående dokumenter kan brevkoden være en NAV-skjemaID f.eks. "NAV 14-05.09" eller en SED-id.
     */
    val brevkode: String,

    /** De forskjellige varientene av samme dokument, feks kan et dokument ha en XML variant og en PDF-variant. */
    val dokumentVarianter: List<DokumentVariant>,
)

/** Holder et dokument som skal journalføres som en Base64 enkodet string */
@Serializable
data class DokumentVariant(
    /** Gyldige filtyper: [Filtype](https://confluence.adeo.no/display/BOA/Filtype) */
    val filtype: String,
    /** Dokumentet  som en Base64-enkodet string */
    val fysiskDokument: String,
    /** Gyldige verdier: [Variantformat](https://confluence.adeo.no/display/BOA/Variantformat) */
    val variantFormat: String,
    val filnavn: String?,
)
