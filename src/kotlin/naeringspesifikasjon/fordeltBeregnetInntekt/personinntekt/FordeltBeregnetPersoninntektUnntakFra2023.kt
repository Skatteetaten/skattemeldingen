package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
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
            gm.grupper(modell.fordeltBeregnetPersoninntekt)
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        var unntak = GeneriskModell.tom()
        val harNyNaeringsinntekt = forekomsterAvFordeltBeregnetNaeringsinntekt.any {
            it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }
        if (harNyNaeringsinntekt) {
            unntak = opprettPersoninntektKortISammenhengMedNaeringsinntektKort(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt,
                forekomsterAvFordeltBeregnetNaeringsinntekt,
            )
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.isEmpty()) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.size == 1) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt[0]
            )
        }

        return unntak
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
        forekomsterAvFordeltBeregnetPersoninntekt: List<GeneriskModell>,
        forekomsterAvFordeltBeregnetNaeringsinntekt: List<GeneriskModell>,
    ): GeneriskModell? {
        val nyttNaeringsKort = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.harFeltMedVerdi(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                ID_FRA_SME
            )
        }
        val naeringtypeForNyttKort =
            nyttNaeringsKort.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype)
        if (finnesFlereLikeNaeringtyper(gm, naeringtypeForNyttKort)) {
            val foersteIdentifikatorForFordeltBeregnetPersoninntekt = forekomsterAvFordeltBeregnetNaeringsinntekt
                .first {
                    it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype) == naeringtypeForNyttKort
                        && it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) !=
                        ID_FRA_SME
                }
                .verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt)

            val nyNid = genererNyNaeringsIdentifikator(gm)

            val oppdatertNyttKort = nyttNaeringsKort.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                    nyttNaeringsKort.rotForekomstIder(),
                    foersteIdentifikatorForFordeltBeregnetPersoninntekt
                ),
                InformasjonsElement(
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                    nyttNaeringsKort.rotForekomstIder(),
                    nyNid
                )
            )
            return GeneriskModell.fra(oppdatertNyttKort.alleInformasjonsElementer())
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
        var naeringsInntektSomSkalOppdateres = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }
        naeringsInntektSomSkalOppdateres = naeringsInntektSomSkalOppdateres.erstattEllerLeggTilFelter(
            InformasjonsElement(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                naeringsInntektSomSkalOppdateres.gruppeForekomstIder,
                nyNid
            ),
            InformasjonsElement(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                naeringsInntektSomSkalOppdateres.gruppeForekomstIder,
                nyPid
            )
        )

        // Personinntekt kort
        val alleForekomstIder = forekomsterAvFordeltBeregnetPersoninntekt.map {
            it.rotIdVerdi()
        }
        var forekomstId = nyPid
        while (alleForekomstIder.contains(nyPid.toString())) {
            forekomstId++
        }
        val fordeltBeregnetPersoninntektForekomstId =
            modell.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to forekomstId.toString()
        return GeneriskModell.fra(
            InformasjonsElement(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                nyPid
            ),
            InformasjonsElement(
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                nyNid
            ),
            InformasjonsElement(
                modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "100"
            ),
        ).concat(naeringsInntektSomSkalOppdateres)
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
        fordeltBeregnetPersoninntektForekomst: GeneriskModell? = null,
    ): GeneriskModell {
        val eksisterendeForekomstId = fordeltBeregnetPersoninntektForekomst?.rotIdVerdi() ?: "1"
        val fordeltBeregnetPersoninntektForekomstId =
            modell.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to eksisterendeForekomstId

        val medStandardverdier = GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier.erstattEllerLeggTilFelter(
                oppdaterVerdiEllerLagElement(
                    gm,
                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                    mapOf(
                        fordeltBeregnetPersoninntektForekomstId,
                        modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
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