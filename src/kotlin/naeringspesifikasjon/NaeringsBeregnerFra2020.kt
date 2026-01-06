package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.xmldata.beregnedefelt.ErAvledetGeneriskNoekkel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.NaeringsBeregner
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.NullstillGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.validering.GlobaleFeltValidering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.validering.VirksomhetValidering

class NaeringsBeregnerFra2020(
    private val inntektsaar: Int,
    private val avlededeFelter: Set<ErAvledetGeneriskNoekkel> = emptySet()
) : NaeringsBeregner(
    defaultKalkyleSamlingPerAar[inntektsaar] ?: error("Finner ikke kalkylesamling for inntektsaar $inntektsaar"),
) {

    override fun sjekkInput(generiskModell: GeneriskModell) {
        if (inntektsaar > 2020) {
            GlobaleFeltValidering.validerGlobaleFelt(generiskModell)
            VirksomhetValidering.validerVirksomhet(inntektsaar, generiskModell)
        }
    }

    override fun nullstill(
        generiskModell: GeneriskModell,
        gjelderSkjoennsfastsetting: Boolean
    ): GeneriskModell {
        return NullstillGeneriskModell.nullstill(inntektsaar, generiskModell, avlededeFelter, gjelderSkjoennsfastsetting)
    }

    override fun filtrerBeregningsresultat(beregnetModell: GeneriskModell): GeneriskModell {
        return filtrerResultatAvBeregninger(beregnetModell, inntektsaar)
    }
}

private fun filtrerResultatAvBeregninger(gm: GeneriskModell, inntektsaar: Int): GeneriskModell {
    val filtrertGm = filtrerBortFordelBeregnetInntekt(gm, inntektsaar)
    return filtrerBortResultatOgBalanseregnskapstyperMedKunEkskluderesFraSkattemeldingenFelt(filtrertGm)
}

private fun filtrerBortResultatOgBalanseregnskapstyperMedKunEkskluderesFraSkattemeldingenFelt(gm: GeneriskModell): GeneriskModell {
    val balanseverdiForAnleggsmiddelForekomster =
        gm.grupper(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi)
    val filtrertBalanseverdiForAnleggsmiddelForekomster = balanseverdiForAnleggsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type) }

    val balanseverdiForOmloepsmiddelForekomster =
        gm.grupper(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi)
    val filtrertBalanseverdiForOmloepsmiddelForekomster = balanseverdiForOmloepsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type) }

    val egenkapitalForekomster =
        gm.grupper(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital)
    val filtrertEgenkapitalForekomster = egenkapitalForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type) }

    return gm
        .fjernFelter(GeneriskModell.merge(balanseverdiForAnleggsmiddelForekomster))
        .fjernFelter(GeneriskModell.merge(balanseverdiForOmloepsmiddelForekomster))
        .fjernFelter(GeneriskModell.merge(egenkapitalForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertBalanseverdiForAnleggsmiddelForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertBalanseverdiForOmloepsmiddelForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertEgenkapitalForekomster))
}

private fun filtrerBortFordelBeregnetInntekt(gm: GeneriskModell, inntektsaar: Int): GeneriskModell {
    if (inntektsaar < 2023) {
        val resultat = gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(
            modell2022.resultatregnskap_aarsresultat
        )
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupper(modell2022.fordeltBeregnetNaeringsinntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupper(modell2022.fordeltBeregnetPersoninntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            gm.fjernFelter(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetPersoninntektFelter)
        } else {
            gm
        }
    } else {
        val resultat = gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupper(modell.fordeltBeregnetPersoninntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetUpersonlig =
                gm.grupper(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            gm.fjernFelter(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetPersoninntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetUpersonlig)
        } else {
            gm
        }
    }
}
