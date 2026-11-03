package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object SkattemessigResultatBeregningFra2023 : HarKalkylesamling {

    internal val skattemessigResultatKalkyle = kalkyle("skattemessigResultatKalkyle") {
        if (!erPetroleumsforetak()) {
            if (inntektsaar.tekniskInntektsaar >= 2026 && gjelderBankOgForsikring()) {
                settUniktFelt(modell.beregnetNaeringsinntekt_skattemessigResultat) {
                    modell.resultatregnskap_totalresultatInnenBankOgForsikring.tall() +
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntekt -
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntekt +
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntektKnyttetTilIkkeTekniskRegnskapForLivsforsikringsforetak -
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntektKnyttetTilIkkeTekniskRegnskapForLivsforsikringsforetak +
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumEndringIMidlertidigForskjell
                }
            } else {
                settUniktFelt(modell.beregnetNaeringsinntekt_skattemessigResultat) {
                    modell.resultatregnskap_aarsresultat.tall() +
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntekt -
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntekt +
                        modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumEndringIMidlertidigForskjell
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattemessigResultatKalkyle
        )
    }
}