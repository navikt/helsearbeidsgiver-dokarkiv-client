package no.nav.helsearbeidsgiver.dokarkiv.domene

enum class InnsynsRegler {
    /** Brukes når en maskinell prosess har besluttet at journalposten og underliggende dokumenter kan vises til bruker på nav.no.*/
    VISES_MASKINELT_GODKJENT,

    /** Brukes når en NAV-ansatt har sett over og godkjent at journalposten og underliggende dokumenter kan vises til bruker på nav.no.*/
    VISES_MANUELT_GODKJENT,
}
