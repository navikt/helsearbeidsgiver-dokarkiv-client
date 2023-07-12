package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
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
import no.nav.helsearbeidsgiver.utils.log.logger
import java.io.IOException

// NAV-enheten som personen som utfører journalføring jobber for. Ved automatisk journalføring uten
// mennesker involvert, skal enhet settes til "9999".
private const val AUTOMATISK_JOURNALFOERING_ENHET = "9999"

class DokArkivClient(
    private val url: String,
    private val getAccessToken: () -> String,
) {
    private val logger = logger()

    private val httpClient = createHttpClient()

    /**
     * Oppretter en journalpost i Joark/dokarkiv, med eller uten dokumenter.
     *
     * Dokumentasjon: [opprettJournalpost](https://confluence.adeo.no/display/BOA/opprettJournalpost)
     */
    suspend fun opprettJournalpost(
        journalpost: OpprettJournalpostRequest,
        forsoekFerdigstill: Boolean,
        callId: String,
    ): OpprettJournalpostResponse =
        try {
            httpClient.post("$url/journalpost?forsoekFerdigstill=$forsoekFerdigstill") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(journalpost)
            }
                .body<OpprettJournalpostResponse>()
        } catch (e: Exception) {
            if (e is ClientRequestException) {
                throw DokArkivException(e, e.response.status.value)
            }
            throw DokArkivException(e)
        }

    /**
     * NB! I skrivende stund ikke i bruk av noen apper. Må testes bedre før den tas i bruk.
     *
     * Dokumentasjon: [oppdaterJournalpost](https://confluence.adeo.no/display/BOA/oppdaterJournalpost)
     */
    suspend fun oppdaterJournalpost(
        journalpostId: String,
        fnr: String,
        arbeidsgiverNavn: String,
        callId: String,
    ): HttpResponse {
        val idFragment = "journalpostId=[$journalpostId] callId=[$callId]"

        val request = OppdaterJournalpostRequest(
            bruker = Bruker(
                id = fnr,
                idType = IdType.FNR,
            ),
            avsenderMottaker = AvsenderMottaker(
                id = fnr,
                idType = IdType.FNR,
                navn = arbeidsgiverNavn,
            ),
            sak = Sak.GENERELL,
        )

        return runCatching {
            httpClient.put("$url/journalpost/$journalpostId") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(request)
            }
                .body<HttpResponse>()
                .also {
                    logger.info("Oppdatering av journalpost OK. $idFragment")
                }
        }
            .getOrElse {
                haandterFeil(it, "oppdatering", idFragment)
            }
    }

    /**
     * NB! I skrivende stund ikke i bruk av noen apper. Må testes bedre før den tas i bruk.
     *
     * Dokumentasjon: [ferdigstillJournalpost](https://confluence.adeo.no/display/BOA/ferdigstillJournalpost)
     */
    suspend fun ferdigstillJournalpost(
        journalpostId: String,
        callId: String,
    ): String {
        val idFragment = "journalpostId=[$journalpostId] callId=[$callId]"

        val request = FerdigstillRequest(AUTOMATISK_JOURNALFOERING_ENHET)

        return runCatching {
            httpClient.patch("$url/journalpost/$journalpostId/ferdigstill") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(request)
            }
                .body<String>()
                .also { logger.info("Ferdigstilling av journalpost OK. $idFragment") }
        }
            .getOrElse {
                haandterFeil(it, "ferdigstilling", idFragment)
            }
    }

    private fun haandterFeil(feil: Throwable, handling: String, idFragment: String): Nothing {
        logger.error("Dokarkiv svarte med feilmelding ved $handling av journalpost. $idFragment", feil)

        if (feil is ClientRequestException) {
            if (feil.response.status == HttpStatusCode.NotFound) {
                "$handling: Journalposten finnes ikke. $idFragment".let {
                    logger.error(it)
                    throw RuntimeException(it)
                }
            } else {
                "$handling: Fikk http status ${feil.response.status}. $idFragment".let {
                    logger.error(it)
                    throw RuntimeException(it)
                }
            }
        }

        throw IOException("Dokarkiv svarte med feilmelding ved $handling av journalpost. $idFragment")
    }
}
