package no.nav.helsearbeidsgiver.dokarkiv.domene

/** Mottakskanal dokumentet er sendt inn til
 * Inneholder bare subset av kanaler
 * Full liste av gyldige verdier finnes på confluence:
 * https://confluence.adeo.no/spaces/BOA/pages/316396050/Mottakskanal
 */
enum class Kanal {
    NAV_NO,
    HR_SYSTEM_API,
}
