package no.nav.helsearbeidsgiver.dokarkiv.domene

import kotlinx.serialization.Serializable

@Serializable
data class Dokument(
    /**
     * Kode som sier noe om dokumentets innhold og oppbygning.
     * For inngående dokumenter kan brevkoden være en NAV-skjemaID f.eks. "NAV 14-05.09" eller en SED-id.
     */
    val brevkode: String?,

    /**
     * Dokumentets tittel, f.eks. "Søknad om foreldrepenger ved fødsel" eller "Legeerklæring".
     * Dokumentets tittel blir synlig i brukers journal på nav.no, samt i NAVs fagsystemer.
     */
    val tittel: String?,

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

enum class Journalposttype {
    /**
     * INNGAAENDE brukes for dokumentasjon som NAV har mottatt fra en ekstern part.
     * Dette kan være søknader, ettersendelser av dokumentasjon til sak eller meldinger fra arbeidsgivere.
     */
    INNGAAENDE,

    /**
     * UTGAAENDE brukes for dokumentasjon som NAV har produsert og sendt ut til en ekstern part.
     * Dette kan for eksempel være informasjons- eller vedtaksbrev til privatpersoner eller organisasjoner.
     */
    UTGAAENDE,

    /**
     * NOTAT brukes for dokumentasjon som NAV har produsert selv og uten mål om å distribuere dette ut av NAV.
     * Eksempler på dette er forvaltningsnotater og referater fra telefonsamtaler med brukere.
     */
    NOTAT,
}

/** Bruker er den posteringen gjelder */
@Serializable
data class Bruker(
    /** Org nummer eller FNR */
    val id: String,
    /** Hva som er i id-feltet */
    val idType: IdType,
)

/**
 * AvsenderMottaker er den som enten har mottatt (NAV er ansender)
 * eller den som har sendt (NAV er mottaker)
 *
 * Dette avgjøres av feltet
 */
@Serializable
data class AvsenderMottaker(
    /** Org nummer eller FNR */
    val id: String,
    /** Hva som er i id-feltet */
    val idType: IdType,
    /** Navn er påkrevd for ferdigstilling, enten personnavn eller virksomhetsnavn */
    val navn: String?,
    val land: String? = null,
)

enum class IdType {
    FNR,
    ORGNR,
    HPRNR,
    UTL_ORG,
}

@Serializable
data class Sak(
    val sakstype: SaksType,
    /** Liste over gyldige verdier: [opprettJournalpost](https://confluence.adeo.no/display/BOA/opprettJournalpost) */
    val fagsaksystem: String?,
    val fagsakId: String? = null,
) {
    companion object {
        val GENERELL = Sak(SaksType.GENERELL_SAK, null, null)
    }

    enum class SaksType {
        /**
         * FAGSAK vil si at dokumentene tilhører en sak i et fagsystem.
         * Dersom FAGSAK velges, må fagsakid og fagsaksystem oppgis.
         */
        FAGSAK,

        /**
         * GENERELL_SAK kan brukes for dokumenter som skal journalføres, men som ikke tilhører en konkret fagsak.
         * Generell sak kan ses på som brukerens "mappe" på et gitt tema.
         */
        GENERELL_SAK,
    }
}
