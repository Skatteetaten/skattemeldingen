package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.rederi

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil.skalBeregneRederi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object FinansinntektOgFinansunderskudd : HarKalkylesamling {

    val samletFinansinntektEllerUnderskudd = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val inntektstillegg =
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    forekomstType.inntektstillegg.tall()
                }
            val samletSkattepliktigValutagevinst =
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    forekomstType.samletSkattepliktigValutagevinst.tall()
                }
            val samletFradragsberettigetValutatap =
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    forekomstType.samletFradragsberettigetValutatap.tall()
                }
            val fradragsberettigetRentekostnad =
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    forekomstType.fradragsberettigetRentekostnad.tall()
                }
            val sumFinansinntektOgFinansunderskudd =
                forekomsterAv(modell.rederiskatteordning_finansinntektOgFinansunderskudd) summerVerdiFraHverForekomst {
                    forekomstType.renteinntekt +
                        forekomstType.annenFinansinntekt +
                        forekomstType.gevinstPaaRenteswapavtale +
                        forekomstType.skattepliktigGevinstVedRealisasjonAvAksjeMv +
                        (forekomstType.skattepliktigGevinstVedRealisasjonAvFinansiellValutakontraktInngaattFom2010 *
                            forekomstType.finanskapitalandelVedUtgangenAvInntektsaaret.prosent()) +
                        forekomstType.inntektsfoeringAvSkattefrittUtbytte +
                        forekomstType.inntektKnyttetTilSolidaransvarForArbeidstakere +
                        forekomstType.andelAvSkattemessigFinansoverskuddFraSdfOgNokus +
                        forekomstType.garantiprovisjonsinntekt +
                        forekomstType.skattepliktigUtbytte -
                        forekomstType.annenFinanskostnad -
                        forekomstType.tapPaaRenteswapavtale -
                        forekomstType.fradragsberettigetTapVedRealisasjonAvAksjeMv -
                        (forekomstType.fradragsberettigetTapVedRealisasjonAvFinansiellValutakontraktInngaattFom2010 *
                            forekomstType.finanskapitalandelVedUtgangenAvInntektsaaret.prosent()) -
                        forekomstType.andelAvFradragsberettigetFinansunderskuddFraSdf -
                        forekomstType.garantiprovisjonskostnad
                }
            val sum = sumFinansinntektOgFinansunderskudd +
                inntektstillegg +
                samletSkattepliktigValutagevinst -
                fradragsberettigetRentekostnad -
                samletFradragsberettigetValutatap

            hvis(sum.harVerdi()) {
                hvis(sum stoerreEllerLik 0) {
                    settUniktFelt(modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt) {
                        sum medAntallDesimaler 0
                    }
                }
                hvis(sum mindreEnn 0) {
                    settUniktFelt(modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd) {
                        sum.absoluttverdi() medAntallDesimaler 0
                    }
                }
            }
        }
    }

    internal val andelAvSkattemessigFinansoverskuddFraSdfOgNokus =
        kalkyle("andelAvSkattemessigFinansoverskuddFraSdfOgNokus") {
            settUniktFelt(modell.rederiskatteordning_finansinntektOgFinansunderskudd.andelAvSkattemessigFinansoverskuddFraSdfOgNokus) {
                forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                    forekomstType.andelAvFinansinntektRederiskatteordningen.tall()
                }
            }
        }

    internal val andelAvFradragsberettigetFinansunderskuddFraSdf =
        kalkyle("andelAvSkattemessigFinansoverskuddFraSdfOgNokus") {
            settUniktFelt(modell.rederiskatteordning_finansinntektOgFinansunderskudd.andelAvFradragsberettigetFinansunderskuddFraSdf) {
                forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                    forekomstType.selskapMedDeltakerfastsettingErNokus.erUsann()
                } summerVerdiFraHverForekomst {
                    forekomstType.andelAvFinansunderskudd.tall()
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            andelAvSkattemessigFinansoverskuddFraSdfOgNokus,
            andelAvFradragsberettigetFinansunderskuddFraSdf,
            samletFinansinntektEllerUnderskudd
        )
    }
}