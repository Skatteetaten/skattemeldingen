package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.oppdaterVerdiEllerLagElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.FordeltBeregnetNaeringsinntektUtil
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.FordeltBeregnetPersoninntektUtil
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

object FordeltBeregnetPersoninntektUnntak2022 : HarKalkylesamling {

    private const val ID_FRA_SME = "generertIdFraSme"

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
        val forekomsterAvFordeltBeregnetPersoninntekt =
            gm.grupperV2(modell2022.fordeltBeregnetPersoninntekt)
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
        val harNyNaeringsinntekt = forekomsterAvFordeltBeregnetNaeringsinntekt.any {
            it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }
        return if (harNyNaeringsinntekt) {
            opprettPersoninntektKortISammenhengMedNaeringsinntektKort(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt,
                forekomsterAvFordeltBeregnetNaeringsinntekt,
            )
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.size == 1) {
            fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt.first()
            )
        } else GeneriskModell.tom()
    }

    private fun finnesFlereLikeNaeringtyper(
        gm: GeneriskModell,
        naeringtype: String,
    ): Boolean {
        return gm.alleInformasjonsElementer()
            .filter { it.harNoekkel(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype) && it.verdi == naeringtype }
            .size > 1
    }

    private fun opprettPersoninntektKortISammenhengMedNaeringsinntektKort(
        gm: GeneriskModell,
        forekomsterAvFordeltBeregnetPersoninntekt: List<GeneriskGruppe>,
        forekomsterAvFordeltBeregnetNaeringsinntekt: List<GeneriskGruppe>,
    ): GeneriskModell {
        val nyttNaeringsKort = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.harFeltMedVerdi(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                ID_FRA_SME
            )
        }
        val naeringtypeForNyttKort =
            nyttNaeringsKort.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype)!!
        if (finnesFlereLikeNaeringtyper(gm, naeringtypeForNyttKort)) {
            val foersteIdentifikatorForFordeltBeregnetPersoninntekt = forekomsterAvFordeltBeregnetNaeringsinntekt
                .first {
                    it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype) == naeringtypeForNyttKort
                        && it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) !=
                        ID_FRA_SME
                }
                .verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt)

            val nyNid = genererNyNaeringsIdentifikator(gm)

            return nyttNaeringsKort.erstattEllerLeggTilNyttFeltForGruppe(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
                foersteIdentifikatorForFordeltBeregnetPersoninntekt
            ).erstattEllerLeggTilNyttFeltForGruppe(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                nyNid
            ).tilGeneriskModell()
        }

        // NID
        val nyNid = genererNyNaeringsIdentifikator(gm)

        // PID
        val antallPersoninntekter = forekomsterAvFordeltBeregnetPersoninntekt.size
        var nyPid = antallPersoninntekter + 1
        val eksisterendePider = FordeltBeregnetPersoninntektUtil.identifikatorerForFordeltBeregnetPersoninntekt(gm)
        while (eksisterendePider.contains(nyPid.toString())) {
            nyPid++
        }

        // Naeringsinntekt kort
        val naeringsInntektSomSkalOppdateres = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }.erstattEllerLeggTilNyttFeltForGruppe(
            modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
            nyNid
        ).erstattEllerLeggTilNyttFeltForGruppe(
            modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
            nyPid
        )

        // Personinntekt kort
        val alleForekomstIder = forekomsterAvFordeltBeregnetPersoninntekt.map {
            it.rotIdVerdi()
        }
        var forekomstId = nyPid
        while (alleForekomstIder.contains(nyPid.toString())) {
            forekomstId++
        }
        val fordeltBeregnetPersoninntektForekomstId = mapOf(
            modell2022.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to forekomstId.toString()
        )
        return GeneriskGruppe(fordeltBeregnetPersoninntektForekomstId)
            .leggTilNyttFeltForGruppe(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                nyPid
            )
            .leggTilNyttFeltForGruppe(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                nyNid
            )
            .leggTilNyttFeltForGruppe(
                modell2022.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                100
            ).tilGeneriskModell().concat(naeringsInntektSomSkalOppdateres.tilGeneriskModell())
    }

    private fun genererNyNaeringsIdentifikator(gm: GeneriskModell): Long {
        val eksisterendeNider = FordeltBeregnetNaeringsinntektUtil
            .identifikatorerForFordeltBeregnetNaeringsinntekt(gm)
            .toMutableSet()
        eksisterendeNider.remove(ID_FRA_SME)
        val nyNid = (eksisterendeNider.lastOrNull()?.toLong() ?: 0) + 1
        return nyNid
    }

    private fun fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetPersoninntektForekomst: GeneriskGruppe? = null,
    ): GeneriskModell {
        val forekomst = fordeltBeregnetPersoninntektForekomst
            ?: GeneriskGruppe(mapOf(modell2022.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to "1"))

        val medStandardverdier = GeneriskModell.fra(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell2022.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? =
            gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat)
                ?: gm.verdiFor(modell2022.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier.leggTilFelter(
                forekomst.oppdaterVerdiEllerLagElement(
                    modell2022.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
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