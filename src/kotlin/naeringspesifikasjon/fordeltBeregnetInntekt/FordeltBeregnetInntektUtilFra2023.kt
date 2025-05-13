package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.ForekomstType
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal fun skalBeregnePersoninntektFra2023(gm: GeneriskModell): Boolean {
    val virksomhetstypeVerdi = gm.verdiFor(modell.virksomhet.virksomhetstype)
    val forekomsterAvFordeltBeregnetNaeringsinntekt =
        gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
    return virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode
        && forekomsterAvFordeltBeregnetNaeringsinntekt.isNotEmpty()
}

object FordeltBeregnetNaeringsinntektUtilFra2023 {
    private fun sortertFordeltBeregnetNaeringsinntektForekomster(gm: GeneriskModell): List<GeneriskModell> {
        return gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
            .sortedBy { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) }
    }

    internal fun finnDefaultFordeltBeregnetNaeringsinntekt(
        gm: GeneriskModell
    ): GeneriskModell? {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm)
            .map { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) }
            .toSet()
    }
}

object FordeltBeregnetPersoninntektUtilFra2023 {

    private fun sortertFordeltBeregnetPersoninntektForekomster(gm: GeneriskModell): List<GeneriskModell> {
        return gm.grupper(modell.fordeltBeregnetPersoninntekt)
            .sortedBy { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }
    }

    internal fun finnDefaultFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskModell? {
        return sortertFordeltBeregnetPersoninntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetPersoninntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetPersoninntektForekomster(gm)
            .map { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }
            .toSet()
    }
}

private const val FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetNaeringsinntekt " +
        "med identifikatorForFordeltBeregnetNaeringsinntekt på dette tidspunktet"

private const val FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetPersoninntekt " +
        "med identifikatorForFordeltBeregnetPersoninntekt på dette tidspunktet"

internal fun opprettGyldigeIdentifikatorerFra2023(gm: GeneriskModell): GeneriskModell {
    val manglendeIdentifikatorer = opprettManglendeIdentifikatorer(gm)
    var oppdatertGm = gm.erstattEllerLeggTilFelter(manglendeIdentifikatorer)
    val manglendeOgUnikeIdentifikatorer = manglendeIdentifikatorer.erstattEllerLeggTilFelter(
        opprettUnikeIdentifikatorer(oppdatertGm)
    )
    oppdatertGm = oppdatertGm.erstattEllerLeggTilFelter(manglendeOgUnikeIdentifikatorer)
    val synkroniserteIdentifikatorer = synkroniserIdentifikatorerFra2023(oppdatertGm)
    return manglendeOgUnikeIdentifikatorer.erstattEllerLeggTilFelter(synkroniserteIdentifikatorer)
}

private fun opprettManglendeIdentifikatorer(gm: GeneriskModell): GeneriskModell {
    val fordeltBeregnetNaeringsinntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf),
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf,
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(modell.fordeltBeregnetPersoninntekt),
        modell.fordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskModell>,
    forekomstType: ForekomstType<*>,
    identifikatorForFordeltBeregnetPersoninntekt: Felt<*>,
    identifikatorForFordeltBeregnetNaeringsinntekt: Felt<*>,
): GeneriskModell {
    val nyeIdentifikatorer = mutableListOf<InformasjonsElement>()

    val eksisterendePid = forekomster
        .filter { it.harVerdiFor(identifikatorForFordeltBeregnetPersoninntekt) }
        .map { it.verdiFor(identifikatorForFordeltBeregnetPersoninntekt) }

    val eksisterendeNid = forekomster
        .filter { it.harVerdiFor(identifikatorForFordeltBeregnetNaeringsinntekt) }
        .map { it.verdiFor(identifikatorForFordeltBeregnetNaeringsinntekt) }

    forekomster.forEachIndexed { index, forekomst ->
        val forekomstId = forekomstType.rotForekomstIdNoekkel to forekomst.rotIdVerdi()
        val pid = forekomst
            .verdiFor(identifikatorForFordeltBeregnetPersoninntekt)
        if (pid == null) {
            var nyPid: Int = index + 1
            while (eksisterendePid.contains(nyPid.toString())) {
                nyPid++
            }
            nyeIdentifikatorer.add(
                InformasjonsElement(
                    identifikatorForFordeltBeregnetPersoninntekt,
                    mapOf(forekomstId),
                    nyPid
                )
            )
        }

        val nid = forekomst
            .verdiFor(identifikatorForFordeltBeregnetNaeringsinntekt)
        if (nid == null) {
            var nyNid: Int = index + 1
            while (eksisterendeNid.contains(nyNid.toString())) {
                nyNid++
            }
            nyeIdentifikatorer.add(
                InformasjonsElement(
                    identifikatorForFordeltBeregnetNaeringsinntekt,
                    mapOf(forekomstId),
                    nyNid
                )
            )
        }
    }
    return GeneriskModell.fra(nyeIdentifikatorer)
}

private fun opprettUnikeIdentifikatorer(
    gm: GeneriskModell
): GeneriskModell {
    val fordeltBeregnetNaeringsinntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf),
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf,
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(modell.fordeltBeregnetPersoninntekt),
        modell.fordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskModell>,
    forekomstType: ForekomstType<*>,
    identifikator: Felt<*>,
): GeneriskModell {
    val nyeIdentifikatorer = mutableListOf<InformasjonsElement>()

    val eksisterendeIdentifikatorerTilForekomster = forekomster
        .filter { it.harVerdiFor(identifikator) }
        .groupBy { it.verdiFor(identifikator) }

    val forekomsterSomMaaOppdateres = eksisterendeIdentifikatorerTilForekomster
        .filter { it.value.size > 1 }
        .flatMap { it.value.subList(1, it.value.size) }

    var nyIdentifikatorVerdi = 1
    forekomsterSomMaaOppdateres.forEach { forekomst ->
        val forekomstId = forekomstType.rotForekomstIdNoekkel to forekomst.rotIdVerdi()
        while (eksisterendeIdentifikatorerTilForekomster.containsKey(nyIdentifikatorVerdi.toString())) {
            nyIdentifikatorVerdi++
        }
        nyeIdentifikatorer.add(
            InformasjonsElement(
                identifikator,
                mapOf(forekomstId),
                nyIdentifikatorVerdi
            )
        )
    }
    return GeneriskModell.fra(nyeIdentifikatorer)
}

internal fun synkroniserIdentifikatorerFra2023(
    gm: GeneriskModell
): GeneriskModell {
    val oppdatertePersoninntektIder = synkroniserFordeltBeregnetPersoninntekt(gm)
    val oppdaterteNaeringsinntektIder =
        synkroniserFordeltBeregnetNaeringsinntekt(gm.erstattEllerLeggTilFelter(oppdatertePersoninntektIder))

    return oppdatertePersoninntektIder.erstattEllerLeggTilFelter(oppdaterteNaeringsinntektIder)
}

private fun synkroniserFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetNaeringsinntekt =
        FordeltBeregnetNaeringsinntektUtilFra2023.finnDefaultFordeltBeregnetNaeringsinntekt(gm)
            ?.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt)
            ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE)

    return gm.grupper(modell.fordeltBeregnetPersoninntekt)
        .stream()
        .map { fordeltPersoninntekt ->
            val nid = fordeltPersoninntekt
                .verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt)
            val rotForekomstId = fordeltPersoninntekt.rotIdVerdi()
            val forekomstIdFordeltBeregnetPersoninntekt =
                modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to rotForekomstId
            if (!FordeltBeregnetNaeringsinntektUtilFra2023.identifikatorerForFordeltBeregnetNaeringsinntekt(gm)
                    .contains(nid)
            ) {
                GeneriskModell.fra(
                    InformasjonsElement(
                        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                        mapOf(forekomstIdFordeltBeregnetPersoninntekt),
                        defaultIdentifikatorForFordeltBeregnetNaeringsinntekt
                    )
                )
            } else {
                GeneriskModell.tom()
            }
        }
        .collect(GeneriskModell.collectorFraGm())
}

private fun synkroniserFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetPersoninntekt = FordeltBeregnetPersoninntektUtilFra2023
        .finnDefaultFordeltBeregnetPersoninntekt(gm)
        ?.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)
        ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE)

    return gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        .stream()
        .map { fordeltNaeringsinntekt ->
            val pid = fordeltNaeringsinntekt
                .verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt)
            val rotForekomstId = fordeltNaeringsinntekt.rotIdVerdi()
            val forekomstIdFordeltBeregnetNaeringsinntekt =
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to rotForekomstId
            if (!FordeltBeregnetPersoninntektUtilFra2023
                    .identifikatorerForFordeltBeregnetPersoninntekt(gm)
                    .contains(pid)
            ) {
                GeneriskModell.fra(
                    InformasjonsElement(
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                        mapOf(forekomstIdFordeltBeregnetNaeringsinntekt),
                        defaultIdentifikatorForFordeltBeregnetPersoninntekt
                    )
                )
            } else {
                GeneriskModell.tom()
            }
        }
        .collect(GeneriskModell.collectorFraGm())
}