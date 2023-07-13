package no.nav.helsearbeidsgiver.dokarkiv

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.dokarkiv.domene.Avsender
import no.nav.helsearbeidsgiver.dokarkiv.domene.Dokument
import no.nav.helsearbeidsgiver.dokarkiv.domene.FerdigstillRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.GjelderPerson
import no.nav.helsearbeidsgiver.dokarkiv.domene.OppdaterRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillRequest
import no.nav.helsearbeidsgiver.dokarkiv.domene.OpprettOgFerdigstillResponse
import no.nav.helsearbeidsgiver.utils.log.logger
import java.io.IOException
import java.time.LocalDate

class DokArkivClient(
    private val url: String,
    private val getAccessToken: () -> String,
) {
    private val logger = logger()

    private val httpClient = createHttpClient()

    /**
     * Oppretter en journalpost i Joark/dokarkiv, med eller uten dokumenter, og forsøker å ferdigstille.
     *
     * Dokumentasjon: [opprettJournalpost](https://confluence.adeo.no/display/BOA/opprettJournalpost)
     */
    suspend fun opprettOgFerdigstillJournalpost(
        behandlingsTema: String,
        /** Tittel som beskriver forsendelsen samlet, feks "Ettersendelse til søknad om foreldrepenger". */
        tittel: String,
        gjelderPerson: GjelderPerson,
        avsender: Avsender,
        datoMottatt: LocalDate,
        dokumenter: List<Dokument>,
        /** Unik id for forsendelsen som kan brukes til sporing gjennom verdikjeden. */
        eksternReferanseId: String,
        callId: String,
    ): OpprettOgFerdigstillResponse {
        val request = OpprettOgFerdigstillRequest(
            behandlingsTema = behandlingsTema,
            tittel = tittel,
            bruker = gjelderPerson.tilBruker(),
            avsenderMottaker = avsender.tilAvsenderMottaker(),
            datoMottatt = datoMottatt,
            dokumenter = dokumenter,
            eksternReferanseId = eksternReferanseId,
        )

        return try {
            httpClient.post("$url/journalpost?forsoekFerdigstill=true") {
                contentType(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(request)
            }
                .body()
        } catch (e: Exception) {
            if (e is ClientRequestException) {
                throw DokArkivException(e, e.response.status.value)
            }
            throw DokArkivException(e)
        }
    }

    /**
     * NB! I skrivende stund ikke i bruk av noen apper. Må testes bedre før den tas i bruk.
     *
     * Dokumentasjon: [oppdaterJournalpost](https://confluence.adeo.no/display/BOA/oppdaterJournalpost)
     */
    suspend fun oppdaterJournalpost(
        journalpostId: String,
        gjelderPerson: GjelderPerson,
        avsender: Avsender,
        callId: String,
    ) {
        val idFragment = "journalpostId=[$journalpostId] callId=[$callId]"

        val request = OppdaterRequest(
            bruker = gjelderPerson.tilBruker(),
            avsenderMottaker = avsender.tilAvsenderMottaker(),
        )

        runCatching {
            httpClient.put("$url/journalpost/$journalpostId") {
                contentType(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(request)
            }
        }
            .onFailure {
                haandterFeil(it, "oppdatering", idFragment)
            }

        logger.info("Oppdatering av journalpost OK. $idFragment")
    }

    /**
     * NB! I skrivende stund ikke i bruk av noen apper. Må testes bedre før den tas i bruk.
     *
     * Dokumentasjon: [ferdigstillJournalpost](https://confluence.adeo.no/display/BOA/ferdigstillJournalpost)
     */
    suspend fun ferdigstillJournalpost(
        journalpostId: String,
        callId: String,
    ) {
        val idFragment = "journalpostId=[$journalpostId] callId=[$callId]"

        val request = FerdigstillRequest()

        runCatching {
            httpClient.patch("$url/journalpost/$journalpostId/ferdigstill") {
                contentType(ContentType.Application.Json)
                bearerAuth(getAccessToken())
                navCallId(callId)
                setBody(request)
            }
        }
            .onFailure {
                haandterFeil(it, "ferdigstilling", idFragment)
            }

        logger.info("Ferdigstilling av journalpost OK. $idFragment")
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
