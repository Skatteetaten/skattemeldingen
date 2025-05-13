package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

object FordeltBeregnetNaeringsinntektUnntak2022 : HarKalkylesamling {

    val fordeltBeregnetNaeringsinntektUnntak = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val virksomhetstypeVerdi = gm.verdiFor(modell2022.virksomhet.virksomhetstype)
        val resultat = gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(modell2022.resultatregnskap_aarsresultat)
        val unntak = if (resultat == null) {
            GeneriskModell.tom()
        } else if (virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode) {
            unntakForEnkeltmannsforetak(gm)
        } else {
            unntakForUpersonlige(gm)
        }
        leggTilIKontekst(GeneriskModellForKalkyler(unntak))
    }

    private fun unntakForUpersonlige(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupper(modell2022.fordeltBeregnetNaeringsinntekt)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            oppdaterFordeltSkattemessigResultat(gm, forekomsterAvFordeltBeregnetNaeringsinntekt[0])
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm)
        } else {
            GeneriskModell.tom()
        }
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupper(modell2022.fordeltBeregnetNaeringsinntekt)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            val forekomst = forekomsterAvFordeltBeregnetNaeringsinntekt[0]
            val oppdaterteFelter = fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm, forekomst)
            oppdaterteFelter.erstattEllerLeggTilFelter(oppdaterFordeltSkattemessigResultat(gm, forekomst))
        } else {
            GeneriskModell.tom()
        }
    }

    private fun fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell = GeneriskModell.tom(),
    ): GeneriskModell {
        val eksisterendeForekomstId = if (fordeltBeregnetNaeringsinntektForekomst.isEmpty) {
            "1"
        } else {
            fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()
        }
        val fordeltBeregnetNaeringsinntektForekomstId =
            mapOf(modell2022.fordeltBeregnetNaeringsinntekt.rotForekomstIdNoekkel to eksisterendeForekomstId)

        return GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
                fordeltBeregnetNaeringsinntektForekomstId,
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                fordeltBeregnetNaeringsinntektForekomstId,
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.naeringstype,
                fordeltBeregnetNaeringsinntektForekomstId,
                "annenNaering"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.naeringsbeskrivelse,
                fordeltBeregnetNaeringsinntektForekomstId,
                ""
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat,
                fordeltBeregnetNaeringsinntektForekomstId +
                    mapOf(
                        modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.gruppe!! to "fixed"
                    ),
                gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(modell2022.resultatregnskap_aarsresultat)
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell2022.fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver,
                fordeltBeregnetNaeringsinntektForekomstId,
                "100"
            ),
        )
    }

    private fun oppdaterFordeltSkattemessigResultat(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell,
    ): GeneriskModell {
        val skattemessigResultatVerdi = gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: return GeneriskModell.tom()
        val eksisterendeForekomstId = fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()!!
        val forekomstId = modell2022.fordeltBeregnetNaeringsinntekt.rotForekomstIdNoekkel to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat,
                mapOf(
                    forekomstId,
                    modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                skattemessigResultatVerdi
            )
        )
    }
    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetNaeringsinntektUnntak)
    }
}