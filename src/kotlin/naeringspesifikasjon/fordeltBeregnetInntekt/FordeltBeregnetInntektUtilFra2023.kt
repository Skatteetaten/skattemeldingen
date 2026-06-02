package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal fun skalBeregnePersoninntektFra2023(gm: GeneriskModell): Boolean {
    val virksomhetstypeVerdi = gm.verdiFor(modell.virksomhet.virksomhetstype)
    val forekomsterAvFordeltBeregnetNaeringsinntekt =
        gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
    return virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode
        && forekomsterAvFordeltBeregnetNaeringsinntekt.isNotEmpty()
}

object FordeltBeregnetNaeringsinntektUtilFra2023 {
    private fun sortertFordeltBeregnetNaeringsinntektForekomster(gm: GeneriskModell): List<GeneriskGruppe> {
        return gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
            .sortedBy { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) }
    }

    internal fun finnDefaultFordeltBeregnetNaeringsinntekt(
        gm: GeneriskModell
    ): GeneriskGruppe? {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm)
            .mapNotNull { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) }
            .toSet()
    }
}

object FordeltBeregnetPersoninntektUtilFra2023 {

    private fun sortertFordeltBeregnetPersoninntektForekomster(gm: GeneriskModell): List<GeneriskGruppe> {
        return gm.grupperV2(modell.fordeltBeregnetPersoninntekt)
            .sortedBy { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }
    }

    internal fun finnDefaultFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskGruppe? {
        return sortertFordeltBeregnetPersoninntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetPersoninntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetPersoninntektForekomster(gm)
            .map { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)!! }
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
        gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf),
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupperV2(modell.fordeltBeregnetPersoninntekt),
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt
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
        gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf),
        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupperV2(modell.fordeltBeregnetPersoninntekt),
        modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
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

    return gm.grupperV2(modell.fordeltBeregnetPersoninntekt)
        .mapNotNull { fordeltPersoninntekt ->
            val nid = fordeltPersoninntekt
                .verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt)
            if (!FordeltBeregnetNaeringsinntektUtilFra2023.identifikatorerForFordeltBeregnetNaeringsinntekt(gm)
                    .contains(nid)
            ) {
                fordeltPersoninntekt.lagNyttFeltForGruppe(
                    modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                    defaultIdentifikatorForFordeltBeregnetNaeringsinntekt
                )
            } else {
                null
            }
        }
        .tilGeneriskModell()
}

private fun synkroniserFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetPersoninntekt = FordeltBeregnetPersoninntektUtilFra2023
        .finnDefaultFordeltBeregnetPersoninntekt(gm)
        ?.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)
        ?: throw IllegalStateException("$FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE. gm=$gm")

    return gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        .mapNotNull { fordeltNaeringsinntekt ->
            val pid = fordeltNaeringsinntekt
                .verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt)
            if (!FordeltBeregnetPersoninntektUtilFra2023
                    .identifikatorerForFordeltBeregnetPersoninntekt(gm)
                    .contains(pid)
            ) {
                fordeltNaeringsinntekt.lagNyttFeltForGruppe(
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                    defaultIdentifikatorForFordeltBeregnetPersoninntekt
                )
            } else {
                null
            }
        }.tilGeneriskModell()
}