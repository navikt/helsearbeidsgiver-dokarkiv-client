package no.nav.helsearbeidsgiver.dokarkiv

open class DokArkivException(exception: java.lang.Exception, status: Int? = null) : Exception("Klarte ikke opprette journalpost! Status: $status", exception)
