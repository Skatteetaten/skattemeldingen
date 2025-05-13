package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

object SkattemessigResultatBeregning : HarKalkylesamling {

    internal val skattemessigResultatKalkyle = kalkyle("skattemessigResultatKalkyle") {
        settUniktFelt(modell2022.beregnetNaeringsinntekt_skattemessigResultat) {
            modell2022.resultatregnskap_aarsresultat +
                modell2022.beregnetNaeringsinntekt_sumTilleggINaeringsinntekt -
                modell2022.beregnetNaeringsinntekt_sumFradragINaeringsinntekt +
                modell2022.beregnetNaeringsinntekt_sumEndringIMidlertidigForskjell
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattemessigResultatKalkyle
        )
    }
}