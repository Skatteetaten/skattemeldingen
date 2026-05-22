package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

object FordeltBeregnetNaeringsinntektUnntak2021 : HarKalkylesamling {

    val fordeltBeregnetNaeringsinntektUnntak = kalkyle("fordeltBeregnetNaeringsinntektUnntak") {
        val gm = generiskModell.tilGeneriskModell()
        val virksomhetsstype = gm.verdiFor(modell2021.virksomhet.virksomhetstype)
        val resultat = gm.verdiFor(modell2021.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(modell2021.resultatregnskap_aarsresultat)
        val unntak = if (resultat == null) {
            GeneriskModell.tom()
        } else if (virksomhetsstype == virksomhetstype.kode_enkeltpersonforetak.kode) {
            unntakForEnkeltmannsforetak(gm)
        } else {
            unntakForUpersonlige(gm)
        }
        leggTilIKontekst(GeneriskModellForKalkyler(unntak))
    }

    private fun unntakForUpersonlige(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupperV2(modell2021.fordeltBeregnetNaeringsinntekt)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            GeneriskModell.fra(
                oppdaterFordeltSkattemessigResultat(gm, forekomsterAvFordeltBeregnetNaeringsinntekt[0])
            )
        } else {
            GeneriskModell.tom()
        }
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupperV2(modell2021.fordeltBeregnetNaeringsinntekt)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            val forekomst = forekomsterAvFordeltBeregnetNaeringsinntekt[0] ?: GeneriskGruppe.tom()
            val oppdaterteFelter = fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm, forekomst)
            oppdaterteFelter.erstattEllerLeggTilFelter(oppdaterFordeltSkattemessigResultat(gm, forekomst))
        } else {
            GeneriskModell.tom()
        }
    }

    private fun fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe? = null,
    ): GeneriskModell {
        val forekomst = fordeltBeregnetNaeringsinntektForekomst
            ?: GeneriskGruppe(mapOf(modell2021.fordeltBeregnetNaeringsinntekt.rotForekomstIdNoekkel to "1"))

        return GeneriskModell.fra(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.naeringstype,
                "annenNaering"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.naeringsbeskrivelse,
                ""
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat,
                gm.verdiFor(modell2021.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(modell2021.resultatregnskap_aarsresultat)
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2021.fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver,
                100
            ),
        )
    }

    private fun oppdaterFordeltSkattemessigResultat(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe,
    ): InformasjonsElement? {
        val skattemessigResultatVerdi = gm.verdiFor(modell2021.beregnetNaeringsinntekt_skattemessigResultat) ?: return null

        return fordeltBeregnetNaeringsinntektForekomst.lagFeltMedEgenskaper(
            modell2021.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat,
            skattemessigResultatVerdi
        )
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetNaeringsinntektUnntak)
    }
}