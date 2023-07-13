package no.nav.helsearbeidsgiver.dokarkiv.domene

@JvmInline
value class GjelderPerson(
    private val fnr: String,
) {
    internal fun tilBruker(): Bruker =
        Bruker(
            id = fnr,
            idType = IdType.FNR,
        )
}

sealed class Avsender {
    internal abstract fun tilAvsenderMottaker(): AvsenderMottaker

    data class Person(
        val fnr: String,
    ) : Avsender() {
        override fun tilAvsenderMottaker(): AvsenderMottaker =
            AvsenderMottaker(
                id = fnr,
                idType = IdType.FNR,
                navn = null,
            )
    }

    data class Organisasjon(
        val orgnr: String,
        val navn: String,
    ) : Avsender() {
        override fun tilAvsenderMottaker(): AvsenderMottaker =
            AvsenderMottaker(
                id = orgnr,
                idType = IdType.ORGNR,
                navn = navn,
            )
    }
}
