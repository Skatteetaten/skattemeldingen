package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.maksAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023

internal object OmsetningOgEtterbetalingMvISamvirkeforetak : HarKalkylesamling {

    private val samletOmsetning = kalkyle("samletOmsetning") {
        hvis(modell2023.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_samvirkeforetak.kode) {
            val forekomstType = modell2023.andreForhold_omsetningOgEtterbetalingISamvirkeforetak
            settUniktFelt(forekomstType.samletOmsetning) {
                forekomstType.omsetningMedMedlemIEgetSamvirkeforetak +
                    forekomstType.omsetningMedMedlemIAnnetSamvirkeforetak +
                    forekomstType.oevrigOmsetning
            }
            settVilkaar()
        }
    }

    internal val andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak =
        kalkyle("andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak") {
            val forekomstType = modell2023.andreForhold_omsetningOgEtterbetalingISamvirkeforetak
            hvis(
                modell2023.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_samvirkeforetak.kode
                    && forekomstType.samletOmsetning.harVerdi()
                    && forekomstType.samletOmsetning ulik 0
            ) {
                val verdi = (forekomstType.omsetningMedMedlemIEgetSamvirkeforetak / forekomstType.samletOmsetning) * 100
                settUniktFelt(forekomstType.andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak) {
                    verdi maksAntallDesimaler 10
                }
                settVilkaar()
            }
        }

    private val skattepliktigNaeringsinntektForSamvirkeforetak =
        kalkyle("skattepliktigNaeringsinntektForSamvirkeforetak") {
            hvis(modell2023.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_samvirkeforetak.kode) {
                settUniktFelt(modell2023.andreForhold_omsetningOgEtterbetalingISamvirkeforetak.skattepliktigNaeringsinntektForSamvirkeforetak) {
                    modell2023.beregnetNaeringsinntekt_skattemessigResultat.tall()
                }
                settVilkaar()
            }
        }

    private fun GeneriskModellKontekst.settVilkaar() {
        if (modell2023.andreForhold_omsetningOgEtterbetalingISamvirkeforetak.vilkaarForFradragForEtterbetalingUtdeltTilMedlemmerErOppfylt.harIkkeVerdi()) {
            settUniktFelt(
                modell2023.andreForhold_omsetningOgEtterbetalingISamvirkeforetak.vilkaarForFradragForEtterbetalingUtdeltTilMedlemmerErOppfylt,
                "false"
            )
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletOmsetning,
            andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak,
            skattepliktigNaeringsinntektForSamvirkeforetak,
        )
    }
}