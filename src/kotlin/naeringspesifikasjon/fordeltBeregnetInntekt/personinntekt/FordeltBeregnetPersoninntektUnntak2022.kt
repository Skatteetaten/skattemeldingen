package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
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
            gm.grupper(modell2022.fordeltBeregnetPersoninntekt)
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupper(modell2022.fordeltBeregnetNaeringsinntekt)
        var unntak = GeneriskModell.tom()
        val harNyNaeringsinntekt = forekomsterAvFordeltBeregnetNaeringsinntekt.any {
            it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) ==
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
            .filter { it.harNoekkel(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype) && it.verdi == naeringtype }
            .size > 1
    }

    private fun opprettPersoninntektKortISammenhengMedNaeringsinntektKort(
        gm: GeneriskModell,
        forekomsterAvFordeltBeregnetPersoninntekt: List<GeneriskModell>,
        forekomsterAvFordeltBeregnetNaeringsinntekt: List<GeneriskModell>,
    ): GeneriskModell? {
        val nyttNaeringsKort = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.harFeltMedVerdi(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                ID_FRA_SME
            )
        }
        val naeringtypeForNyttKort =
            nyttNaeringsKort.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype)
        if (finnesFlereLikeNaeringtyper(gm, naeringtypeForNyttKort)) {
            val foersteIdentifikatorForFordeltBeregnetPersoninntekt = forekomsterAvFordeltBeregnetNaeringsinntekt
                .first {
                    it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype) == naeringtypeForNyttKort
                        && it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) !=
                        ID_FRA_SME
                }
                .verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt)

            val nyNid = genererNyNaeringsIdentifikator(gm)

            val oppdatertNyttKort = nyttNaeringsKort.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
                    nyttNaeringsKort.rotForekomstIder(),
                    foersteIdentifikatorForFordeltBeregnetPersoninntekt
                ),
                InformasjonsElement(
                    modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
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
        val eksisterendePider = FordeltBeregnetPersoninntektUtil.identifikatorerForFordeltBeregnetPersoninntekt(gm)
        while (eksisterendePider.contains(nyPid.toString())) {
            nyPid++
        }

        // Naeringsinntekt kort
        var naeringsInntektSomSkalOppdateres = forekomsterAvFordeltBeregnetNaeringsinntekt.first {
            it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt) ==
                ID_FRA_SME
        }
        naeringsInntektSomSkalOppdateres = naeringsInntektSomSkalOppdateres.erstattEllerLeggTilFelter(
            InformasjonsElement(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                naeringsInntektSomSkalOppdateres.gruppeForekomstIder,
                nyNid
            ),
            InformasjonsElement(
                modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
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
            modell2022.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to forekomstId.toString()
        return GeneriskModell.fra(
            InformasjonsElement(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                nyPid
            ),
            InformasjonsElement(
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                nyNid
            ),
            InformasjonsElement(
                modell2022.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "100"
            ),
        ).concat(naeringsInntektSomSkalOppdateres)
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
        fordeltBeregnetPersoninntektForekomst: GeneriskModell? = null,
    ): GeneriskModell {
        val eksisterendeForekomstId = fordeltBeregnetPersoninntektForekomst?.rotIdVerdi() ?: "1"
        val fordeltBeregnetPersoninntektForekomstId =
            modell2022.fordeltBeregnetPersoninntekt.loevForekomstIdNoekkel to eksisterendeForekomstId

        val medStandardverdier = GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2022.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                gm,
                modell2022.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                "100"
            ),
        )

        val aarsresultatEllerSkattemessigResultat: String? =
            gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat)
                ?: gm.verdiFor(modell2022.resultatregnskap_aarsresultat)

        return if (aarsresultatEllerSkattemessigResultat != null) {
            medStandardverdier.erstattEllerLeggTilFelter(
                oppdaterVerdiEllerLagElement(
                    gm,
                    modell2022.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                    mapOf(
                        fordeltBeregnetPersoninntektForekomstId,
                        modell2022.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
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