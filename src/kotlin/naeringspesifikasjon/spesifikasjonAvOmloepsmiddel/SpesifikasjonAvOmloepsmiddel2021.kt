package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

object SpesifikasjonAvOmloepsmiddel2021 : HarKalkylesamling {

    val skattemessigNedskrivningPaaKundefordringKalkyle = kalkyle("skattemessigNedskrivningPaaKundefordring") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            val kredittSalgSum = forekomstType.kredittsalg + forekomstType.kredittsalgIFjor
            hvis(
                (kredittSalgSum.harVerdi() && kredittSalgSum ulik 0) && (
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet.harIkkeVerdi() ||
                        forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet mindreEllerLik 0)
            ) {
                settFelt(forekomstType.skattemessigNedskrivningPaaKundefordring) {
                    ((forekomstType.konstatertTapPaaKundefordring +
                        forekomstType.konstatertTapPaaKundefordringIFjor) *
                        forekomstType.kundefordringOgIkkeFakturertDriftsinntekt * 4 /
                        kredittSalgSum) medMaksimumsverdi forekomstType.kundefordringOgIkkeFakturertDriftsinntekt.tall()
                }
            }
        }
    }

    val skattemessigVerdiPaaKundefordringKalkyle = kalkyle("skattemessigVerdiPaaKundefordring") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            settFelt(forekomstType.skattemessigVerdiPaaKundefordring) {
                forekomstType.kundefordringOgIkkeFakturertDriftsinntekt -
                    forekomstType.skattemessigNedskrivningPaaKundefordring -
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
            }
        }
    }

    val sumSkattemessigVerdiPaaFordringerKalkyle = kalkyle("sumSkattemessigVerdiPaaFordringer") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            settFelt(forekomstType.sumSkattemessigVerdiPaaFordring) {
                forekomstType.skattemessigVerdiPaaKundefordring + forekomstType.annenFordring
            }
        }
    }

    val sumVerdiAvVarelagerKalkyle = kalkyle("sumVerdiAvVarelager") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) {
            settFelt(forekomstType.sumVerdiAvVarelager) {
                forekomstType.raavareOgInnkjoeptHalvfabrikata +
                    forekomstType.vareUnderTilvirkning +
                    forekomstType.ferdigTilvirketVare +
                    forekomstType.innkjoeptVareForVideresalg +
                    forekomstType.buskap +
                    forekomstType.selvprodusertVareBenyttetIEgenProduksjon +
                    forekomstType.reinPelsdyrOgPelsdyrskinnPaaLager
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattemessigNedskrivningPaaKundefordringKalkyle,
            skattemessigVerdiPaaKundefordringKalkyle,
            sumSkattemessigVerdiPaaFordringerKalkyle,
            sumVerdiAvVarelagerKalkyle
        )
    }
}