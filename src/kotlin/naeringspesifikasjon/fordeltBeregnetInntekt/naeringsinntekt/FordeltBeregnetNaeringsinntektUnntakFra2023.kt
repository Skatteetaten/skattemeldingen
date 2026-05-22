package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
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
            gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardVerdierSDF(gm)
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
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe? = null,
    ): GeneriskModell {
        val forekomst = fordeltBeregnetNaeringsinntektForekomst
            ?: GeneriskGruppe(mapOf(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to "1"))
        return GeneriskModell.fra(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
        )
    }

    private fun unntakForUpersonlige(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig)
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            GeneriskModell.fra(oppdaterFordeltSkattemessigResultatUpersonlig(gm, forekomsterAvFordeltBeregnetNaeringsinntekt[0]))
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardVerdierForUpersonlig(gm).tilGeneriskModell()
        } else {
            GeneriskModell.tom()
        }
    }

    private fun oppdaterFordeltSkattemessigResultatUpersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe
    ): InformasjonsElement? {
        val skattemessigResultatVerdi =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat) ?: return null

        return fordeltBeregnetNaeringsinntektForekomst.lagFeltMedEgenskaper(
            modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat,
            skattemessigResultatVerdi
        )
    }

    private fun unntakPersonlig(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt =
            gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
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
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe? = null,
    ): GeneriskModell {
        val forekomst = fordeltBeregnetNaeringsinntektForekomst
            ?: GeneriskGruppe(mapOf(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.rotForekomstIdNoekkel to "1"))

        return GeneriskModell.fra(
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetNaeringsinntekt,
                "1"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype,
                "annenNaering"
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringsbeskrivelse,
                ""
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            ),
            forekomst.lagDefaultElementHvisDetIkkeEksisterer(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.andelAvFordeltSkattemessigResultatTilordnetInnehaver,
                "100"
            ),
            fordeltBeregnetNaeringsinntektForekomst?.felt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fradragForRenterINaeringPaaSvalbard),
        )
    }

    private fun fyllUtStandardVerdierForUpersonlig(gm: GeneriskModell): GeneriskGruppe {
        return GeneriskGruppe(mapOf(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.rotForekomstIdNoekkel to "1"))
            .leggTilFeltMedEgenskaper(
                modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultat,
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            )
            .leggTilFeltMedEgenskaper(
                modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig.fordeltSkattemessigResultatEtterKorreksjon,
                gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
                    ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
            )
    }

    private fun oppdaterFordeltSkattemessigResultatPersonlig(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskGruppe,
    ): InformasjonsElement? {
        val skattemessigResultatVerdi =
            gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat) ?: return null

        return fordeltBeregnetNaeringsinntektForekomst.lagFeltMedEgenskaper(
            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat,
            skattemessigResultatVerdi
        )
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetNaeringsinntektUnntak)
    }
}