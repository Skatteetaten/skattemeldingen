package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import java.time.LocalDate
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker.DeltakersAndelAvInntektKalkyler.realisertAndelAvUnderskuddPer31122002
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object RealisasjonOgAnnenOverdragelseAvAndelKalkyler : HarKalkylesamling {

    internal val gevinstEllerTapVedRealisasjonAvAndelKalkyle = kalkyle {
        forekomsterAv(modell.deltaker) forHverForekomst {
            val inngangsverdi =
                forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_inngangsverdi.tall()
            val skattepliktigGevinstVedRealisasjonAvAndel =
                forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_skattepliktigGevinstVedRealisasjonAvAndel.tall()

            val selskapsandelIProsent = forekomstType.selskapsandelIProsent.prosent()

            val alleRealiserteAndeler =
                forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) summerVerdiFraHverForekomst {
                    forekomstType.realisertAndel.prosent()
                }

            val alleErvervetAndeler =
                forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) summerVerdiFraHverForekomst {
                    forekomstType.ervervetAndel.prosent()
                }

            val selskapandelVedAaretsStart = selskapsandelIProsent +
                alleRealiserteAndeler -
                alleErvervetAndeler

            val inngangsverdiSum =
                forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_inngangsverdi -
                    forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_innskudd_inngangsverdi -
                    forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_inngangsverdi +
                    forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_inngangsverdi +
                    forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_inngangsverdi -
                    forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_inngangsverdi

            var senesteOverdragelsestidspunkt = LocalDate.MIN

            forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) der {
                forekomstType.overdragelsestidspunkt.harVerdi()
            } forHverForekomst {
                val overdragelsestidspunkt = forekomstType.overdragelsestidspunkt.dato()
                if (overdragelsestidspunkt!!.isAfter(senesteOverdragelsestidspunkt)) {
                    senesteOverdragelsestidspunkt = overdragelsestidspunkt
                }
            }

            forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) forHverForekomst {
                val realisertAndel = forekomstType.realisertAndel.prosent()

                val anvendtInngangsverdiVedRealisasjon = if (
                    realisertAndel.harVerdi()
                    && selskapandelVedAaretsStart.harVerdi()
                    && selskapandelVedAaretsStart ulik 0
                ) {
                    inngangsverdi *
                        realisertAndel /
                        selskapandelVedAaretsStart
                } else {
                    null
                }

                var gevinstEllerTap = forekomstType.vederlag -
                    forekomstType.realisasjonskostnad -
                    anvendtInngangsverdiVedRealisasjon +
                    forekomstType.tilleggForIkkeFradragsberettigetTapForArvEllerGave -
                    skattepliktigGevinstVedRealisasjonAvAndel +
                    forekomstType.annetTillegg -
                    forekomstType.annenReduksjon

                hvis(selskapsandelIProsent lik 0
                    && forekomstType.overdragelsestidspunkt.dato() == senesteOverdragelsestidspunkt) {
                    gevinstEllerTap += inngangsverdiSum
                }

                hvis(this@kalkyle.erNokus()) {
                    gevinstEllerTap = gevinstEllerTap +
                        forekomstType.reguleringForNegativRiskIEiertiden -
                        forekomstType.reguleringForPositivRiskIEiertiden -
                        forekomstType.kostprisForRealisertAksjeEllerAndelINokus -
                        realisertAndelAvUnderskuddPer31122002
                }

                hvis(forekomstType.ubenyttetSkjermingsfradrag.erPositiv()
                    && (forekomstType.vederlag - forekomstType.realisasjonskostnad - inngangsverdi) stoerreEllerLik 0
                ) {
                    gevinstEllerTap = (gevinstEllerTap - forekomstType.ubenyttetSkjermingsfradrag) medMinimumsverdi 0
                }

                if (gevinstEllerTap stoerreEllerLik 0) {
                    settFelt(forekomstType.gevinstVedRealisasjonAvAndel) {
                        gevinstEllerTap.somHeltall()
                    }
                } else {
                    settFelt(forekomstType.tapVedRealisasjonAvAndel) {
                        gevinstEllerTap.absoluttverdi().somHeltall()
                    }
                }
            }

        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            gevinstEllerTapVedRealisasjonAvAndelKalkyle
        )
    }
}