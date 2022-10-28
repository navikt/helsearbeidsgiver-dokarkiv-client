package no.nav.helsearbeidsgiver.dokarkiv

open class DokArkivException(melding: String) : Exception(melding)

class DokArkivStatusException(val status: Int, melding: String) : DokArkivException(melding)
