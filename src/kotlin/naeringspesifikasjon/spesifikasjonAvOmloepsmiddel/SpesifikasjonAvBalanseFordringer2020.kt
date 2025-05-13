package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

object SpesifikasjonAvBalanseFordringer2020 : HarKalkylesamling {

    val skattemessigNedskrivningPaaKundefordringKalkyle = kalkyle("skattemessigNedskrivningPaaKundefordring") {
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_skattemessigVerdiPaaFordring) {
            val kredittSalgSum = forekomstType.kredittsalg + forekomstType.kredittsalgIFjor
            hvis(
                (kredittSalgSum.harVerdi() && kredittSalgSum ulik 0) && (
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet.harIkkeVerdi() ||
                        forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet mindreEllerLik 0)
            ) {
                settFelt(forekomstType.skattemessigNedskrivningPaaKundefordring) {
                    (forekomstType.konstatertTapPaaKundefordring +
                        forekomstType.konstatertTapPaaKundefordringIFjor) /
                        kredittSalgSum *
                        forekomstType.kundefordringOgIkkeFakturertDriftsinntekt * 4
                }
            }
        }
    }

    val skattemessigVerdiPaaKundefordringKalkyle = kalkyle("skattemessigVerdiPaaKundefordring") {
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_skattemessigVerdiPaaFordring) {
            settFelt(forekomstType.skattemessigVerdiPaaKundefordring) {
                forekomstType.kundefordringOgIkkeFakturertDriftsinntekt -
                    forekomstType.skattemessigNedskrivningPaaKundefordring -
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
            }
        }
    }

    val sumSkattemessigVerdiPaaFordringerKalkyle = kalkyle("sumSkattemessigVerdiPaaFordringer") {
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_skattemessigVerdiPaaFordring) {
            settFelt(forekomstType.sumSkattemessigVerdiPaaFordring) {
                forekomstType.skattemessigVerdiPaaKundefordring + forekomstType.annenFordring
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattemessigNedskrivningPaaKundefordringKalkyle,
            skattemessigVerdiPaaKundefordringKalkyle,
            sumSkattemessigVerdiPaaFordringerKalkyle
        )
    }
}