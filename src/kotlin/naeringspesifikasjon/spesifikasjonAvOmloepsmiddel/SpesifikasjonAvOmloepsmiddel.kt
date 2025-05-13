package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.omloepsmiddel_skattemessigVerdiPaaFordringer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object SpesifikasjonAvOmloepsmiddel : HarKalkylesamling {

    val skattemessigNedskrivningPaaKundefordringKalkyle = kalkyle("skattemessigNedskrivningPaaKundefordring") {
        val satser = satser!!
        forAlleForekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            val kredittSalgSum = forekomstType.kredittsalg + forekomstType.kredittsalgIFjor
            hvis(
                (kredittSalgSum.harVerdi() && kredittSalgSum ulik 0) && (
                forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet.harIkkeVerdi() ||
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet mindreEllerLik 0)
            ) {
                settFelt(forekomstType.skattemessigNedskrivningPaaKundefordring) {
                    (((forekomstType.konstatertTapPaaKundefordring +
                        forekomstType.konstatertTapPaaKundefordringIFjor) *
                        forekomstType.kundefordringOgIkkeFakturertDriftsinntekt * satser.sats(omloepsmiddel_skattemessigVerdiPaaFordringer) /
                        kredittSalgSum) medMaksimumsverdi forekomstType.kundefordringOgIkkeFakturertDriftsinntekt.tall()
                        ).nullHvisNegativt()
                }
            }
        }
    }

    val skattemessigVerdiPaaKundefordringKalkyle = kalkyle("skattemessigVerdiPaaKundefordring") {
        forAlleForekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            settFelt(forekomstType.skattemessigVerdiPaaKundefordring) {
                forekomstType.kundefordringOgIkkeFakturertDriftsinntekt -
                    forekomstType.skattemessigNedskrivningPaaKundefordring -
                    forekomstType.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
            }
        }
    }

    val sumSkattemessigVerdiPaaFordringerKalkyle = kalkyle("sumSkattemessigVerdiPaaFordringer") {
        forAlleForekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) {
            settFelt(forekomstType.sumSkattemessigVerdiPaaFordring) {
                forekomstType.skattemessigVerdiPaaKundefordring + forekomstType.annenFordring
            }
        }
    }

    val sumVerdiAvVarelagerKalkyle = kalkyle("sumVerdiAvVarelager") {
        forAlleForekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) {
            settFelt(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager.sumVerdiAvVarelager) {
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