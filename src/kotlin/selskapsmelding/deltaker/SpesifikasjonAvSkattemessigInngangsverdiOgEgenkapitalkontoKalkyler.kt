package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsForFinansskattepliktigVirksomhet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsITiltakssone
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErPersonlig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erBosattInnenforTiltakssonenINordTromsOgFinnmark
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erFinansskattepliktig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erIkkeNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker.DeltakersAndelAvInntektKalkyler.sumFoerArbeidsgodtgjoerelseFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV1

object SpesifikasjonAvSkattemessigInngangsverdiOgEgenkapitalkontoKalkyler : HarKalkylesamling {

    internal val utgaaendeVerdiInngangsverdiKalkyle2022 = kalkyle {
        if (inntektsaar.tekniskInntektsaar == 2022) {
            forekomsterAv(modellV1.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_inngangsverdi) {
                    (forekomstType.skjermingsfradrag_inngangsverdiJustertForInnskuddErvervRealisasjonMv +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_inngangsverdi -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_inngangsverdi +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_inngangsverdi -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_inngangsverdi).somHeltall()
                }
            }
        }
    }

    internal val utgaaendeVerdiInngangsverdiKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2023 && (inntektsaar.tekniskInntektsaar < 2024 || erIkkeNokus())) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_inngangsverdi) {
                    if (forekomstType.deltakerensNorskePersonidentifikator.harVerdi()) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_inngangsverdiJustertForInnskuddErvervRealisasjonMv +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_inngangsverdi).somHeltall()
                    } else if (forekomstType.deltakerensOrganisasjonsnummer.harVerdi()) {
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_realisasjonAvAndel_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_innskudd_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_inngangsverdi
                    } else {
                        null
                    }
                }
            }
        }
    }

    internal val utgaaendeVerdiInnbetaltEgenkapitalKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar < 2024 || erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_innbetaltEgenkapital) {
                    (forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_innbetaltEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_innbetaltEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_realisasjonAvAndel_innbetaltEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_innskudd_innbetaltEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_innbetaltEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_innbetaltEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_innbetaltEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_innbetaltEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_innbetaltEgenkapital).somHeltall()
                }
            }
        }
    }

    internal val utgaaendeVerdiOpptjentEgenkapitalKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        if (inntektsaar.tekniskInntektsaar < 2024 || erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_opptjentEgenkapital) {
                    val aaretsUtdeling = if (inntektsaar.tekniskInntektsaar >= 2024) {
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_aaretsUtdeling.tall() medMinimumsverdi 0
                    } else {
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_aaretsUtdeling.tall()
                    }
                    (forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_opptjentEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_opptjentEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_realisasjonAvAndel_opptjentEgenkapital -
                        aaretsUtdeling -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_opptjentEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_opptjentEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattemessigResultat +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_andelAvSkattefriInntekt -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_andelAvIkkeFradragsberettigetKostnad +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annetTillegg_opptjentEgenkapital -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_annenReduksjon_opptjentEgenkapital).somHeltall()
                }
            }
        }
    }

    internal val samletEgenkapitalUtgaaendeVerdiKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar < 2024 || erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_samletEgenkapitalUtgaaendeVerdi) {
                    (forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_innbetaltEgenkapital +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_utgaaendeVerdi_opptjentEgenkapital).somHeltall()
                }
            }
        }
    }

    internal val kostprisVedErvervAvAndelKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024 && erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_inngangsverdi) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) summerVerdiFraHverForekomst  {
                        forekomstType.kjoepskostnad +
                        forekomstType.vederlag -
                        forekomstType.overtattNegativInngangsverdiForAndelSomArvEllerGave +
                        forekomstType.overtattInngangsverdiForAndelSomArvEllerGave
                     }
                }
            }
        }
    }


    internal val tilbakebetalingAvInnbetaltKapitalKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024 && erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_inngangsverdi) {
                    (forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital).tall()
                }
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_innbetaltEgenkapital) {
                    (forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital).tall()
                }
            }
        }
    }

    internal val aaretsUtdelingKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024 && erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_aaretsUtdeling) {
                    (forekomstType.utdelingMv_kontantUtbetaling +
                        forekomstType.utdelingMv_verdIAvEiendelOgTjenesteOverfoertTilDeltaker -
                        forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital)
                        .somHeltall()
                }
            }
        }
    }

    internal val uegentligInnskuddKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024 && erIkkeNokus()) {
            forekomsterAv(modell.deltaker) forHverForekomst {
                val uegentligInnskudd = forekomstType.utdelingMv_uegentligInnskudd.tall()
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_inngangsverdi) {
                    uegentligInnskudd.somHeltall()
                }
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_innbetaltEgenkapital) {
                    uegentligInnskudd.somHeltall()
                }
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_uegentligInnskudd_opptjentEgenkapital) {
                    uegentligInnskudd.somHeltall()
                }
            }
        }
    }

    internal val skattefordelAvUnderskuddKalkyle = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024 && erIkkeNokus()) {
            val satser = satser!!
            forekomsterAv(modell.deltaker) forHverForekomst {
                val skattesats = if (deltakerErPersonlig() && !erBosattInnenforTiltakssonenINordTromsOgFinnmark()) {
                    satser.sats(skattPaaAlminneligInntekt_sats)
                } else if (deltakerErPersonlig() && erBosattInnenforTiltakssonenINordTromsOgFinnmark()) {
                    satser.sats(skattPaaAlminneligInntekt_satsITiltakssone)
                } else if (!deltakerErPersonlig() && erFinansskattepliktig()) {
                    satser.sats(skattPaaAlminneligInntekt_satsForFinansskattepliktigVirksomhet)
                } else {
                    satser.sats(skattPaaAlminneligInntekt_sats)
                }
                val skattefordelAvUnderskudd = if (forekomstType.erOmfattetAvRederiskatteordning.erSann()) {
                    (forekomstType.andelAvSamletUnderskudd * skattesats)
                        .somHeltall()
                } else {
                    if (sumFoerArbeidsgodtgjoerelseFelt.mindreEnn(0)) {
                        (sumFoerArbeidsgodtgjoerelseFelt * skattesats).somHeltall().absoluttverdi()
                    } else {
                       null
                    }
                }

                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_inngangsverdi) {
                    skattefordelAvUnderskudd
                }
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_innbetaltEgenkapital) {
                    skattefordelAvUnderskudd
                }
                settFelt(forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_skattefordelAvUnderskudd_opptjentEgenkapital) {
                    skattefordelAvUnderskudd
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            aaretsUtdelingKalkyle,
            uegentligInnskuddKalkyle,
            kostprisVedErvervAvAndelKalkyle,
            tilbakebetalingAvInnbetaltKapitalKalkyle,
            skattefordelAvUnderskuddKalkyle,
            utgaaendeVerdiInngangsverdiKalkyle2022,
            utgaaendeVerdiInngangsverdiKalkyle,
            utgaaendeVerdiInnbetaltEgenkapitalKalkyle,
            utgaaendeVerdiOpptjentEgenkapitalKalkyle,
            samletEgenkapitalUtgaaendeVerdiKalkyle,
        )
      }
}