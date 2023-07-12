package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import no.nav.helsearbeidsgiver.dokarkiv.domene.AvsenderMottaker
import no.nav.helsearbeidsgiver.dokarkiv.domene.Bruker
import no.nav.helsearbeidsgiver.dokarkiv.domene.FerdigstillRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.IdType
import no.nav.helsearbeidsgiver.dokarkiv.domene.OppdaterJournalpostRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettJournalpostRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettJournalpostResponse
import no.nav.helsearbeidsgiver.dokarkiv.domene.Sak
import org.slf4j.LoggerFactory
import java.io.IOException

// NAV-enheten som personen som utfører journalføring jobber for. Ved automatisk journalføring uten
// mennesker involvert, skal enhet settes til "9999".
private const val AUTOMATISK_JOURNALFOERING_ENHET = "9999"

class DokArkivClient(
    private val url: String,
    private val getAccessToken: () -> String,
) {
    private val log: org.slf4j.Logger = LoggerFactory.getLogger(this.javaClass.name)

    private val httpClient = createHttpClient()

    private suspend fun ferdigstill(
        journalpostId: String,
        msgId: String,
        ferdigstillRequest: FerdigstillRequest,
    ): String {
        try {
            return httpClient.patch("$url/journalpost/$journalpostId/ferdigstill") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                header("Nav-Callid", msgId)
                setBody(ferdigstillRequest)
            }
                .body<String>().also { log.info("ferdigstilling av journalpost ok for journalpostid {}, msgId {}", journalpostId, msgId) }
        } catch (e: Exception) {
            log.error("Dokarkiv svarte med feilmelding ved ferdigstilling av journalpost for msgId $msgId", e)
            if (e is ClientRequestException) {
                when (e.response.status) {
                    HttpStatusCode.NotFound -> {
                        log.error("Journalposten finnes ikke for journalpostid {}, msgId {}", journalpostId, msgId)
                        throw RuntimeException("Ferdigstilling: Journalposten finnes ikke for journalpostid $journalpostId msgid $msgId")
                    }

                    else -> {
                        log.error("Fikk http status {} for journalpostid {}, msgId {}", e.response.status, journalpostId, msgId)
                        throw RuntimeException("Fikk feilmelding ved ferdigstilling av journalpostid $journalpostId msgid $msgId")
                    }
                }
            }
            throw IOException("Dokarkiv svarte med feilmelding ved ferdigstilling av journalpost for $journalpostId msgid $msgId")
        }
    }


    /**
     * Tjeneste som lar konsument endre status på en journalpost fra midlerdidig til endelig. Dersom journalposten
     * ikke er mulig å ferdigstille, for eksempel fordi den mangler påkrevde metadata, får konsument beskjed om hva
     * som mangler.
     *
     * Dokumentasjon: [ferdigstillJournalpost](https://confluence.adeo.no/display/BOA/ferdigstillJournalpost)
     *
     * Ved suksessfull ferdigstilling: 200 OK.
     *
     * Ved feil:
     * - 400 Bad Request. Kan ikke ferdigstille. Enten lar ikke journalposten seg ferdigstille eller så er input ugyldig.
     * - 401 Unauthorized. Konsument kaller tjenesten med ugyldig OIDC-token.
     * - 403 Forbidden. Konsument har ikke tilgang til å ferdigstille journalpost.
     * - 500 Internal Server Error. Dersom en uventet feil oppstår i dokarkiv.
     */
    suspend fun ferdigstillJournalpost(
        journalpostId: String,
        msgId: String,
    ): String {
        return ferdigstill(journalpostId, msgId, FerdigstillRequest(AUTOMATISK_JOURNALFOERING_ENHET))
    }

    private suspend fun oppdater(
        journalpostId: String,
        oppdaterJournalpostRequest: OppdaterJournalpostRequest,
        msgId: String,
    ): HttpResponse {
        try {
            return httpClient.put("$url/journalpost/$journalpostId") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                header("Nav-Callid", msgId)
                setBody(oppdaterJournalpostRequest)
            }
                .body<HttpResponse>().also { log.info("Oppdatering av journalpost ok for journalpostid {}, msgId {}", journalpostId, msgId) }
        } catch (e: Exception) {
            log.error("Dokarkiv svarte med feilmelding ved oppdatering av journalpost for msgId {}", msgId, e)
            if (e is ClientRequestException) {
                when (e.response.status) {
                    HttpStatusCode.NotFound -> {
                        log.error("Oppdatering: Journalposten finnes ikke for journalpostid {}, msgId {}", journalpostId, msgId)
                        throw RuntimeException("Oppdatering: Journalposten finnes ikke for journalpostid $journalpostId msgid $msgId")
                    }

                    else -> {
                        log.error("Fikk http status {} ved oppdatering av journalpostid {}, msgId {}", e.response.status, journalpostId, msgId)
                        throw RuntimeException("Fikk feilmelding ved oppdatering av journalpostid $journalpostId msgid $msgId")
                    }
                }
            }
            throw IOException("Dokarkiv svarte med feilmelding ved oppdatering av journalpost for $journalpostId msgid $msgId")
        }
    }

    /** Dokumentasjon: [oppdaterJournalpost](https://confluence.adeo.no/display/BOA/oppdaterJournalpost) */
    suspend fun oppdaterJournalpost(
        journalpostId: String,
        fnr: String,
        isFnr: Boolean,
        arbeidsgiverNavn: String,
        msgId: String,
    ): HttpResponse {
        val req = OppdaterJournalpostRequest(
            bruker = Bruker(
                fnr,
                if (isFnr) {
                    IdType.FNR
                } else {
                    IdType.ORGNR
                },
            ),
            avsenderMottaker = AvsenderMottaker(fnr, IdType.FNR, arbeidsgiverNavn),
            sak = Sak(Sak.SaksType.GENERELL_SAK, "GSAK"),
        )
        return oppdater(journalpostId, req, msgId)
    }

    /**
     * Oppretter en journalpost i Joark/dokarkiv, med eller uten dokumenter.
     *
     * Dokumentasjon: [opprettJournalpost](https://confluence.adeo.no/display/BOA/opprettJournalpost)
     */
    suspend fun opprettJournalpost(
        journalpost: OpprettJournalpostRequest,
        forsoekFerdigstill: Boolean,
        callId: String,
    ): OpprettJournalpostResponse {
        try {
            return httpClient.post("$url/journalpost?forsoekFerdigstill=$forsoekFerdigstill") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(getAccessToken())
                header("Nav-Call-Id", callId)
                setBody(journalpost)
            }.body<OpprettJournalpostResponse>()
        } catch (e: Exception) {
            if (e is ClientRequestException) {
                throw DokArkivException(e, e.response.status.value)
            }
            throw DokArkivException(e)
        }
    }
}
