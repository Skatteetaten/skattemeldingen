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
        gm.grupperV2(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi)
    val filtrertBalanseverdiForAnleggsmiddelForekomster = balanseverdiForAnleggsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type) }

    val balanseverdiForOmloepsmiddelForekomster =
        gm.grupperV2(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi)
    val filtrertBalanseverdiForOmloepsmiddelForekomster = balanseverdiForOmloepsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type) }

    val egenkapitalForekomster =
        gm.grupperV2(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital)
    val filtrertEgenkapitalForekomster = egenkapitalForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type) }

    return gm
        .fjernGrupperV2(balanseverdiForAnleggsmiddelForekomster)
        .fjernGrupperV2(balanseverdiForOmloepsmiddelForekomster)
        .fjernGrupperV2(egenkapitalForekomster)
        .erstattEllerLeggTilFelter(filtrertBalanseverdiForAnleggsmiddelForekomster)
        .erstattEllerLeggTilFelter(filtrertBalanseverdiForOmloepsmiddelForekomster)
        .erstattEllerLeggTilFelter(filtrertEgenkapitalForekomster)
}

private fun filtrerBortFordelBeregnetInntekt(gm: GeneriskModell, inntektsaar: Int): GeneriskModell {
    if (inntektsaar < 2023) {
        val resultat = gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(
            modell2022.resultatregnskap_aarsresultat
        )
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt)
            gm.fjernGrupperV2(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernGrupperV2(eksisterendeFordeltBeregnetPersoninntektFelter)
        } else {
            gm
        }
    } else {
        val resultat = gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupperV2(modell.fordeltBeregnetPersoninntekt)
            val eksisterendeFordeltBeregnetUpersonlig =
                gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig)
            gm.fjernGrupperV2(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernGrupperV2(eksisterendeFordeltBeregnetPersoninntektFelter)
                .fjernGrupperV2(eksisterendeFordeltBeregnetUpersonlig)
        } else {
            gm
        }
    }
}
