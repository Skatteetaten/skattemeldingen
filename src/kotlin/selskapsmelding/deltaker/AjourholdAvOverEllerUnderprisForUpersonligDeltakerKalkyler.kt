package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object AjourholdAvOverEllerUnderprisForUpersonligDeltakerKalkyler : HarKalkylesamling {
    internal val akkumulertPrisKalkyle = kalkyle {
        hvis(!erNokus()) {
            forekomsterAv(modell.deltaker) der { forekomstType.deltakerensOrganisasjonsnummer.harVerdi() } forHverForekomst {
                val akkumulertPris =
                    (forekomstType.ajourholdAvOverEllerUnderprisForUpersonligDeltaker_inngaaendeOverpris -
                        forekomstType.ajourholdAvOverEllerUnderprisForUpersonligDeltaker_inngaaendeUnderpris +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_inngangsverdi -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_innbetaltEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_opptjentEgenkapital -
                        forekomstType.ajourholdAvOverEllerUnderprisForUpersonligDeltaker_realisertAndelAvOverEllerUnderpris).somHeltall()

                if (akkumulertPris stoerreEllerLik 0) {
                    settFelt(forekomstType.ajourholdAvOverEllerUnderprisForUpersonligDeltaker_akkumulertOverpris) {
                        akkumulertPris
                    }
                } else {
                    settFelt(forekomstType.ajourholdAvOverEllerUnderprisForUpersonligDeltaker_akkumulertUnderpris) {
                        akkumulertPris.absoluttverdi()
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            akkumulertPrisKalkyle
        )
    }
}