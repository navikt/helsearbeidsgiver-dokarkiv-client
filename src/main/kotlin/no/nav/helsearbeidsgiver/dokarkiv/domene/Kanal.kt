package no.nav.helsearbeidsgiver.dokarkiv.domene

/** Mottakskanal eller Utsendingskanal for dokumentet
 * Inneholder bare subset av kanaler
 * Full liste av gyldige verdier finnes på confluence:
 * https://confluence.adeo.no/spaces/BOA/pages/316396050/Mottakskanal
 * og https://confluence.adeo.no/spaces/BOA/pages/316407153/Utsendingskanal
 */
enum class Kanal {
    NAV_NO,
    HR_SYSTEM_API,
    ALTINN,
}
