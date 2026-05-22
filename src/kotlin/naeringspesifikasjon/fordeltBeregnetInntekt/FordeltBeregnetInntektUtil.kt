package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

internal fun skalBeregnePersoninntekt(gm: GeneriskModell): Boolean {
    val virksomhetstypeVerdi = gm.verdiFor(modell2022.virksomhet.virksomhetstype)
    val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
    return virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode
        && forekomsterAvFordeltBeregnetNaeringsinntekt.isNotEmpty()
}

object FordeltBeregnetNaeringsinntektUtil {
    private fun sortertFordeltBeregnetNaeringsinntektForekomster(gm: GeneriskModell): List<GeneriskGruppe> {
        return gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
            .sortedBy { it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) }
    }

    internal fun finnDefaultFordeltBeregnetNaeringsinntekt(
        gm: GeneriskModell
    ): GeneriskGruppe? {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm)
            .map { it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt)!! }
            .toSet()
    }
}

object FordeltBeregnetPersoninntektUtil {

    private fun sortertFordeltBeregnetPersoninntektForekomster(gm: GeneriskModell): List<GeneriskGruppe> {
        return gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt)
            .sortedBy { it.verdiFor(modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }
    }

    internal fun finnDefaultFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskGruppe? {
        return sortertFordeltBeregnetPersoninntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetPersoninntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetPersoninntektForekomster(gm)
            .map { it.verdiFor(modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)!! }
            .toSet()
    }
}

private const val FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetNaeringsinntekt " +
        "med identifikatorForFordeltBeregnetNaeringsinntekt på dette tidspunktet"

private const val FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetPersoninntekt " +
        "med identifikatorForFordeltBeregnetPersoninntekt på dette tidspunktet"

internal fun opprettGyldigeIdentifikatorer(gm: GeneriskModell): GeneriskModell {
    val manglendeIdentifikatorer = opprettManglendeIdentifikatorer(gm)
    var oppdatertGm = gm.erstattEllerLeggTilFelter(manglendeIdentifikatorer)
    val manglendeOgUnikeIdentifikatorer = manglendeIdentifikatorer.erstattEllerLeggTilFelter(
        opprettUnikeIdentifikatorer(oppdatertGm)
    )
    oppdatertGm = oppdatertGm.erstattEllerLeggTilFelter(manglendeOgUnikeIdentifikatorer)
    val synkroniserteIdentifikatorer = synkroniserIdentifikatorer(oppdatertGm)
    return manglendeOgUnikeIdentifikatorer.erstattEllerLeggTilFelter(synkroniserteIdentifikatorer)
}

private fun opprettManglendeIdentifikatorer(gm: GeneriskModell): GeneriskModell {
    val fordeltBeregnetNaeringsinntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt),
        modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
        modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt),
        modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
        modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskGruppe>,
    identifikatorForFordeltBeregnetPersoninntekt: Felt<*>,
    identifikatorForFordeltBeregnetNaeringsinntekt: Felt<*>,
): GeneriskModell {
    val nyeIdentifikatorer = mutableListOf<InformasjonsElement>()

    val eksisterendePid = forekomster
        .mapNotNull { it.verdiFor(identifikatorForFordeltBeregnetPersoninntekt) }

    val eksisterendeNid = forekomster
        .mapNotNull { it.verdiFor(identifikatorForFordeltBeregnetNaeringsinntekt) }

    forekomster.forEachIndexed { index, forekomst ->
        val pid = forekomst
            .verdiFor(identifikatorForFordeltBeregnetPersoninntekt)
        if (pid == null) {
            var nyPid: Int = index + 1
            while (eksisterendePid.contains(nyPid.toString())) {
                nyPid++
            }
            nyeIdentifikatorer.add(
                forekomst.lagNyttFeltForGruppe(
                    identifikatorForFordeltBeregnetPersoninntekt,
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
                forekomst.lagNyttFeltForGruppe(
                    identifikatorForFordeltBeregnetNaeringsinntekt,
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
        gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt),
        modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt),
        modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskGruppe>,
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
        while (eksisterendeIdentifikatorerTilForekomster.containsKey(nyIdentifikatorVerdi.toString())) {
            nyIdentifikatorVerdi++
        }
        nyeIdentifikatorer.add(
            forekomst.lagNyttFeltForGruppe(
                identifikator,
                nyIdentifikatorVerdi
            )
        )
    }
    return GeneriskModell.fra(nyeIdentifikatorer)
}

internal fun synkroniserIdentifikatorer(
    gm: GeneriskModell
): GeneriskModell {
    val oppdatertePersoninntektIder = synkroniserFordeltBeregnetPersoninntekt(gm)
    val oppdaterteNaeringsinntektIder =
        synkroniserFordeltBeregnetNaeringsinntekt(gm.erstattEllerLeggTilFelter(oppdatertePersoninntektIder))

    return oppdatertePersoninntektIder.erstattEllerLeggTilFelter(oppdaterteNaeringsinntektIder)
}

private fun synkroniserFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetNaeringsinntekt =
        FordeltBeregnetNaeringsinntektUtil.finnDefaultFordeltBeregnetNaeringsinntekt(gm)
            ?.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt)
            ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE)

    return gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt)
        .mapNotNull { fordeltPersoninntekt ->
            val nid = fordeltPersoninntekt
                .verdiFor(modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt)
            if (!FordeltBeregnetNaeringsinntektUtil.identifikatorerForFordeltBeregnetNaeringsinntekt(gm)
                    .contains(nid)
            ) {
                fordeltPersoninntekt.lagNyttFeltForGruppe(
                    modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                    defaultIdentifikatorForFordeltBeregnetNaeringsinntekt
                )
            } else {
                null
            }
        }
        .tilGeneriskModell()
}

private fun synkroniserFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetPersoninntekt = FordeltBeregnetPersoninntektUtil
        .finnDefaultFordeltBeregnetPersoninntekt(gm)
        ?.verdiFor(modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)
        ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE)

    return gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
        .mapNotNull { fordeltNaeringsinntekt ->
            val pid = fordeltNaeringsinntekt
                .verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt)
            if (!FordeltBeregnetPersoninntektUtil
                    .identifikatorerForFordeltBeregnetPersoninntekt(gm)
                    .contains(pid)
            ) {
                fordeltNaeringsinntekt.lagNyttFeltForGruppe(
                    modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
                    defaultIdentifikatorForFordeltBeregnetPersoninntekt
                )
            } else {
                null
            }
        }
        .tilGeneriskModell()
}