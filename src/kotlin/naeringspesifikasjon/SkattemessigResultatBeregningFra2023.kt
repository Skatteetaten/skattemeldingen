package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object SkattemessigResultatBeregningFra2023 : HarKalkylesamling {

    internal val skattemessigResultatKalkyle = kalkyle("skattemessigResultatKalkyle") {
        if (!erPetroleumsforetak()) {
            settUniktFelt(modell.beregnetNaeringsinntekt_skattemessigResultat) {
                modell.resultatregnskap_aarsresultat +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntekt -
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntekt +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumEndringIMidlertidigForskjell
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattemessigResultatKalkyle
        )
    }
}