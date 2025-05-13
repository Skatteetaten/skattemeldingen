package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v5_2024.v5.andreForhold_overnattingsOgServeringsstedForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object OvernattingsOgServeringsstedBeregning : HarKalkylesamling {

    val antallVaretyperFelt = Felt<andreForhold_overnattingsOgServeringsstedForekomst>("andreForhold.overnattingsOgServeringssted.antallVaretyper", modell.andreForhold_overnattingsOgServeringssted)
    val antallKassasystemFelt = Felt<andreForhold_overnattingsOgServeringsstedForekomst>("andreForhold.overnattingsOgServeringssted.antallKassasystem", modell.andreForhold_overnattingsOgServeringssted)

    val overnattingsOgServeringsstedBeregning = kalkyle("overnattingsOgServeringsstedBeregning") {
        forAlleForekomsterAv(modell.andreForhold_overnattingsOgServeringssted) {
            val antallVaretyper = antallForekomsterAv(
                forekomstType.varelagerPerVareart
            )

            hvis(antallVaretyper ulik 0) {
                settFelt(antallVaretyperFelt) { antallVaretyper }
            }

            val antallKassasystem = antallForekomsterAv(
                forekomstType.kassasystem
            )

            hvis(antallKassasystem ulik 0) {
                settFelt(antallKassasystemFelt) { antallKassasystem }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            overnattingsOgServeringsstedBeregning
        )
    }
}