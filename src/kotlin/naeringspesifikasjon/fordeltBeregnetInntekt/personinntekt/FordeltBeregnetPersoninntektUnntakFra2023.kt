package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.oppdaterVerdiEllerLagElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.FordeltBeregnetNaeringsinntektUtilFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.FordeltBeregnetPersoninntektUtilFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntektFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object FordeltBeregnetPersoninntektUnntakFra2023 : HarKalkylesamling {

    private const val ID_FRA_SME = "generertIdFraSme"

    val fordeltBerengetPersoninntektUnntak = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val resultat = if (skalBeregnePersoninntektFra2023(gm)) {
            unntakForEnkeltmannsforetak(gm)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetPersoninntekt =
            gm.grupperV2(modell.fordeltBeregnetPersoninntekt)
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        val harNyNaeringsinntekt = forekomsterAvFordeltBeregnetNaeringsinntekt.any {
            it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) ==
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
            .filter { it.harNoekkel(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype) && it.verdi == naeringtype }
            .size > 1
    }

    private fun opprettPersoninntektKortISammenhengMedNaeringsinntektKort(
        gm: GeneriskModell,
        forekomsterAvFordeltBeregnetPersoninntekt: List<GeneriskGruppe>,
        forekomsterAvFordeltBeregnetNaeringsinntekt: List<GeneriskGruppe>,
    ): GeneriskModell {
        val nyttNaeringsKort = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.harFeltMedVerdi(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                ID_FRA_SME
            )
        }
        val naeringtypeForNyttKort =
            nyttNaeringsKort.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype)!!
        if (finnesFlereLikeNaeringtyper(gm, naeringtypeForNyttKort)) {
            val foersteIdentifikatorForFordeltBeregnetPersoninntekt = forekomsterAvFordeltBeregnetNaeringsinntekt
                .first {
                    it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype) == naeringtypeForNyttKort
                        && it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) !=
                        ID_FRA_SME
                }
                .verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt)

            val nyNid = genererNyNaeringsIdentifikator(gm)

            return nyttNaeringsKort.erstattEllerLeggTilNyttFeltForGruppe(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                foersteIdentifikatorForFordeltBeregnetPersoninntekt
            ).erstattEllerLeggTilNyttFeltForGruppe(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                nyNid
            ).tilGeneriskModell()
        }

        // NID
        val nyNid = genererNyNaeringsIdentifikator(gm)

        // PID
        val antallPersoninntekter = forekomsterAvFordeltBeregnetPersoninntekt.size
        var nyPid = antallPersoninntekter + 1
        val eksisterendePider = FordeltBeregnetPersoninntektUtilFra2023.identifikatorerForFordeltBeregnetPersoninntekt(gm)
        while (eksisterendePider.contains(nyPid.toString())) {
            nyPid++
        }

        // Naeringsinntekt kort
        val naeringsInntektSomSkalOppdateres = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }.erstattEllerLeggTilNyttFeltForGruppe(
            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
            nyNid
        ).erstattEllerLeggTilNyttFeltForGruppe(
            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
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
            modell.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to forekomstId.toString()
        )
        return GeneriskGruppe(fordeltBeregnetPersoninntektForekomstId)
            .leggTilNyttFeltForGruppe(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                nyPid
            )
            .leggTilNyttFeltForGruppe(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                nyNid
            )
            .leggTilNyttFeltForGruppe(
                modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                100
            ).tilGeneriskModell().concat(naeringsInntektSomSkalOppdateres.tilGeneriskModell())
    }

    private fun genererNyNaeringsIdentifikator(gm: GeneriskModell): Long {
        val eksisterendeNider = FordeltBeregnetNaeringsinntektUtilFra2023
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
            ?: GeneriskGruppe(mapOf(modell.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to "1"))

        val medStandardverdier = GeneriskModell.fra(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier.erstattEllerLeggTilFelter(
                forekomst.oppdaterVerdiEllerLagElement(
                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
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