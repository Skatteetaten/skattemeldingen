package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object SkattemessigResultatBeregningFra2023 : HarKalkylesamling {

    internal val skattemessigResultatKalkyle = kalkyle("skattemessigResultatKalkyle") {
        if (!erPetroleumsforetak()) {
            val resultatgrunnlag =
                if (inntektsaar.tekniskInntektsaar >= 2026 && gjelderBankOgForsikring())
                    modell.resultatregnskap_totalresultatInnenBankOgForsikring.tall()
                else
                    modell.resultatregnskap_aarsresultat.tall()
            settUniktFelt(modell.beregnetNaeringsinntekt_skattemessigResultat) {
                resultatgrunnlag +
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