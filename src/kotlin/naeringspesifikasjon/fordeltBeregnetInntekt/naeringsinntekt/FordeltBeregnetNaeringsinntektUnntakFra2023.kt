package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.lagDefaultElementHvisDetIkkeEksisterer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object FordeltBeregnetNaeringsinntektUnntakFra2023 : HarKalkylesamling {

    val fordeltBeregnetNaeringsinntektUnntak = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val virksomhetstypeVerdi = gm.verdiFor(modell.virksomhet.virksomhetstype)
        val resultat = gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
        val unntak = if (resultat == null) {
            GeneriskModell.tom()
        } else if (virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode) {
            unntakPersonlig(gm)
        } else if (
            virksomhetstypeVerdi == virksomhetstype.kode_selskapMedDeltakerfastsetting.kode
            || virksomhetstypeVerdi == virksomhetstype.kode_nokus.kode
        ) {
            unntakSDF(gm)
        } else if (erPetroleumsforetak()) {
            GeneriskModell.tom()
        } else if (virksomhetstypeVerdi == virksomhetstype.kode_oevrigSelskap.kode
            || virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode
            || virksomhetstypeVerdi == virksomhetstype.kode_bankOgFinansieringsforetak.kode
            || virksomhetstypeVerdi == virksomhetstype.kode_livsforsikringsforetakOgPensjonskasse.kode
            || virksomhetstypeVerdi == virksomhetstype.kode_skadeforsikringsforetak.kode
        ) {
            unntakForUpersonlige(gm)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(unntak))
    }

    private fun unntakSDF(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardVerdierSDF(gm, GeneriskModell.tom())
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            val forekomst = forekomsterAvFordeltBeregnetNaeringsinntekt[0]
            val oppdaterteFelter = fyllUtStandardVerdierSDF(gm, forekomst)
            oppdaterteFelter.erstattEllerLeggTilFelter(oppdaterFordeltSkattemessigResultatPersonlig(gm, forekomst))
        } else {
            GeneriskModell.tom()
        }
    }

    private fun fyllUtStandardVerdierSDF(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell = GeneriskModell.tom(),
    ): GeneriskModell {
        val eksisterendeForekomstId = if (fordeltBeregnetNaeringsinntektForekomst.isEmpty) {
            "1"
        } else {
            fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()
        }
        val fordeltBeregnetNaeringsinntektForekomstId =
            mapOf(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to eksisterendeForekomstId)
        return GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                fordeltBeregnetNaeringsinntektForekomstId,
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
                fordeltBeregnetNaeringsinntektForekomstId +
                    mapOf(
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat.gruppe to "fixed"
                    ),
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
        )
    }

    private fun unntakForUpersonlige(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupper(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            oppdaterFordeltSkattemessigResultatUpersonlig(gm, forekomsterAvFordeltBeregnetNaeringsinntekt[0])
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandarVerdierForUpersonlig(gm)
        } else {
            GeneriskModell.tom()
        }
    }

    private fun oppdaterFordeltSkattemessigResultatUpersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell = GeneriskModell.tom(),
    ): GeneriskModell {
        val skattemessigResultatVerdi =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat) ?: return GeneriskModell.tom()
        val eksisterendeForekomstId = fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()!!
        val forekomstId =
            modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.rotForekomstIdNoekkel to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat,
                mapOf(
                    forekomstId,
                    modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                skattemessigResultatVerdi
            )
        )
    }

    private fun unntakPersonlig(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardVerdierPersonlig(gm)
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            val forekomst = forekomsterAvFordeltBeregnetNaeringsinntekt[0]
            val oppdaterteFelter = fyllUtStandardVerdierPersonlig(gm, forekomst)
            oppdaterteFelter.erstattEllerLeggTilFelter(oppdaterFordeltSkattemessigResultatPersonlig(gm, forekomst))
        } else {
            GeneriskModell.tom()
        }
    }

    private fun fyllUtStandardVerdierPersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell = GeneriskModell.tom(),
    ): GeneriskModell {
        val eksisterendeForekomstId = if (fordeltBeregnetNaeringsinntektForekomst.isEmpty) {
            "1"
        } else {
            fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()
        }
        val fordeltBeregnetNaeringsinntektForekomstId =
            mapOf(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to eksisterendeForekomstId)

        var gm = GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                fordeltBeregnetNaeringsinntektForekomstId,
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                fordeltBeregnetNaeringsinntektForekomstId,
                "1"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype,
                fordeltBeregnetNaeringsinntektForekomstId,
                "annenNaering"
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringsbeskrivelse,
                fordeltBeregnetNaeringsinntektForekomstId,
                ""
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
                fordeltBeregnetNaeringsinntektForekomstId +
                    mapOf(
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat.gruppe to "fixed"
                    ),
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.andelAvFordeltSkattemessigResultatTilordnetInnehaver,
                fordeltBeregnetNaeringsinntektForekomstId,
                "100"
            ),
        )

        if (fordeltBeregnetNaeringsinntektForekomst.harVerdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fradragForRenterINaeringPaaSvalbard)) {
            gm = gm.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fradragForRenterINaeringPaaSvalbard,
                    fordeltBeregnetNaeringsinntektForekomstId,
                    fordeltBeregnetNaeringsinntektForekomst.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fradragForRenterINaeringPaaSvalbard),
                )
            )
        }

        return gm
    }

    private fun fyllUtStandarVerdierForUpersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell = GeneriskModell.tom(),
    ): GeneriskModell {
        val eksisterendeForekomstId = if (fordeltBeregnetNaeringsinntektForekomst.isEmpty) {
            "1"
        } else {
            fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()
        }
        val fordeltBeregnetNaeringsinntektForekomstId =
            mapOf(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.rotForekomstIdNoekkel to eksisterendeForekomstId)

        return GeneriskModell.fra(
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat,
                fordeltBeregnetNaeringsinntektForekomstId +
                    mapOf(
                        modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat.gruppe to "fixed"
                    ),
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
            lagDefaultElementHvisDetIkkeEksisterer(
                fordeltBeregnetNaeringsinntektForekomst,
                modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultatEtterKorreksjon,
                fordeltBeregnetNaeringsinntektForekomstId +
                    mapOf(
                        modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultatEtterKorreksjon.gruppe to "fixed"
                    ),
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
        )
    }

    private fun oppdaterFordeltSkattemessigResultatPersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell,
    ): GeneriskModell {
        val skattemessigResultatVerdi =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat) ?: return GeneriskModell.tom()
        val eksisterendeForekomstId = fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()!!
        val forekomstId =
            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
                mapOf(
                    forekomstId,
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                skattemessigResultatVerdi
            )
        )
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetNaeringsinntektUnntak)
    }
}