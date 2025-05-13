package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.oppdaterVerdiEllerLagElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

object FordeltBeregnetPersoninntektUnntak2021 : HarKalkylesamling {

    val fordeltBerengetPersoninntektUnntak = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val resultat = if (skalBeregnePersoninntekt(gm)) {
            unntakForEnkeltmannsforetak(gm)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetPersoninntekt = gm.grupper(modell2021.fordeltBeregnetPersoninntekt)
        var unntak = GeneriskModell.tom()
        if (forekomsterAvFordeltBeregnetPersoninntekt.isEmpty()) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.size == 1) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt[0]
            )
        }

        return unntak
    }

    private fun fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetPersoninntektForekomst: GeneriskModell? = null,
    ): GeneriskModell {
        val eksisterendeForekomstId = fordeltBeregnetPersoninntektForekomst?.rotIdVerdi() ?: "1"
        val fordeltBeregnetPersoninntektForekomstId =
            modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to eksisterendeForekomstId

        val medStandardverdier = GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2021.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2021.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2021.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? = gm.verdiFor(modell2021.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell2021.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier.erstattEllerLeggTilFelter(
                oppdaterVerdiEllerLagElement(
                    gm,
                    modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                    mapOf(
                        fordeltBeregnetPersoninntektForekomstId,
                        modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                    ),
                    aarsresultatEllerSkattemessigResultat
                )
            )
        } else {
            medStandardverdier
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBerengetPersoninntektUnntak)
    }
}