package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.oppdaterVerdiEllerLagElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

object FordeltBeregnetPersoninntektUnntak2021 : HarKalkylesamling {

    val fordeltBerengetPersoninntektUnntak = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val resultat = if (skalBeregnePersoninntekt(gm)) {
            GeneriskModell.fra(unntakForEnkeltmannsforetak(gm))
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): List<InformasjonsElement> {
        val forekomsterAvFordeltBeregnetPersoninntekt = gm.grupperV2(modell2021.fordeltBeregnetPersoninntekt)
        return if (forekomsterAvFordeltBeregnetPersoninntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.size == 1) {
            fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt.first()
            )
        } else {
            emptyList()
        }
    }

    private fun fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetPersoninntektForekomst: GeneriskGruppe? = null,
    ): List<InformasjonsElement> {
        val forekomst = fordeltBeregnetPersoninntektForekomst
            ?: GeneriskGruppe(mapOf(modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to "1"))

        val medStandardverdier = listOf(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? = gm.verdiFor(modell2021.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell2021.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier +
                forekomst.oppdaterVerdiEllerLagElement(
                    modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                    aarsresultatEllerSkattemessigResultat
                )
        } else {
            medStandardverdier
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBerengetPersoninntektUnntak)
    }
}