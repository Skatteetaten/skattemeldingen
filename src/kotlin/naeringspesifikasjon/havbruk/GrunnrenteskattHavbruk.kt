package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.havbruk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Havbruksvirksomhet
 */
internal object GrunnrenteskattHavbruk : HarKalkylesamling {

    private val salgsinntektFraSlaktetFiskFastsattAvSelskapet =
        kalkyle("salgsinntektFraSlaktetFiskFastsattAvSelskapet") {
            settUniktFelt(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattAvSelskapet) {
                forekomsterAv(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_spesifikasjonAvSlaktetFiskFastsattAvSelskapet) summerVerdiFraHverForekomst {
                    forekomstType.salgsinntekt.tall()
                }
            }
        }

    private val salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad =
        kalkyle("salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad") {
            settUniktFelt(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad) {
                forekomsterAv(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_spesifikasjonAvSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad) summerVerdiFraHverForekomst {
                    forekomstType.salgsinntekt.tall()
                }
            }
        }

    private val samletDriftskostnad = kalkyle("samletDriftskostnad") {
        settUniktFelt(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_samletDriftskostnad) {
            forekomsterAv(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnadKnyttetTilHavbruksvirksomhet) summerVerdiFraHverForekomst {
                forekomstType.kostnad.tall()
            }
        }
    }

    private val skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet = kalkyle("skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet") {
        settUniktFelt(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet) {
            val lineaertavskrevet = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert +
                    forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedRealisasjonAvAnleggsmiddelSomErDirekteUtgiftsfoert -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
            }
            val saldoavskrevet = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert +
                    forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedRealisasjonAvAnleggsmiddelSomErDirekteUtgiftsfoert -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
            }
            val ikkeAvskrivbart = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedRealisasjonAvAnleggsmiddelSomErDirekteUtgiftsfoert +
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsInntektsfoeringAvGevinstVedUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
            }
            lineaertavskrevet + saldoavskrevet - ikkeAvskrivbart
        }
    }

    private val investeringskostnadKnyttetTilHavbruksvirksomhet = kalkyle("investeringskostnadKnyttetTilHavbruksvirksomhet") {
        settUniktFelt(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilHavbruksvirksomhet) {
            val lineaertavskrevet = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }
            val saldoavskrevet = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }
            val ikkeAvskrivbart = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }
            lineaertavskrevet + saldoavskrevet + ikkeAvskrivbart
        }
    }

    private val grunnrenteskattHavbruk =
        kalkyle("grunnrenteskattHavbruk") {
            if (statisk.skatteplikt.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
                val satser = satser!!
                forekomsterAv(modell.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet) forHverForekomst {

                    val gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet =
                        forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_bruttoVederlag -
                            forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_andelAvVederlagKnyttetTilAkvakulturtillatelse -
                            forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_skattepliktigVerdiAvAnleggsmiddelSomBenyttesIDenGrunnrentepliktigeHavbruksvirksomheten

                    if (gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet stoerreEllerLik 0) {
                        settFelt(forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonAvHavbruksvirksomhet) {
                            gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet
                        }
                    } else {
                        settFelt(forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonAvHavbruksvirksomhet) {
                            gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet.absoluttverdi()
                        }
                    }

                    settFelt(forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt) {
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattAvSelskapet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraLevendeFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraAvtaleOmFinansiellSikringFastsattAvSelskapet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraAvtaleOmFinansiellSikringFastsattMedSkatteavregningsprisFraPrisraad +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_annenGrunnrentepliktigInntekt +
                            forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonAvHavbruksvirksomhet +
                            forekomstType.beregnetSelskapsskatt_tilleggForAaretsGevinstIAlminneligInntektVedRealisasjonAvAnleggsmiddelSomErDirekteFradragsfoertIGrunnrenteinntekt -
                            (forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_samletDriftskostnad +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_vederlagForKjoepAvLevendeFisk +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForEiendomsskatt +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForForskningsavgift +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_andelAvVederlagPaaKjoepAvLisenserMv +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapFraAvtaleOmFinansiellSikringFastsattAvSelskapet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapFraAvtaleOmFinansiellSikringFastsattMedSkatteavregningsprisFraPrisraad +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_annetGrunnrentepliktigFradrag +
                                forekomstType.beregnetSelskapsskatt_tilbakefoeringAvGevinstPaaInvesteringSomBenyttesIHavbruksvirksomhetSomErDirekteUtgiftsfoert +
                                forekomstType.beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelSomBenyttesIHavbruksvirksomhetSomErDirekteUtgiftsfoert +
                                forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonAvHavbruksvirksomhet +
                                forekomstType.beregnetSelskapsskatt_fradragForAaretsTapIAlminneligInntektVedRealisasjonAvAnleggsmiddelSomErDirekteFradragsfoertIGrunnrenteinntekt)
                    }

                    if (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt stoerreEllerLik 0) {
                        settFelt(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats))
                        }
                    } else if (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt mindreEnn 0) {
                        settFelt(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt.tall()
                                .absoluttverdi() *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)
                        }
                    }

                    if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar stoerreEnn 0 &&
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .stoerreEllerLik(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) //beregnes over
                    ) {
                        settFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                        }
                    } else if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar stoerreEnn 0 &&
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .mindreEnn(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                    ) {
                        settFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                        }
                    }

                    settFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                            forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                            forekomstType.beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    }


                    settFelt(forekomstType.samletBruttoInntektIGrunnrenteinntekt) {
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattAvSelskapet +
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraLevendeFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraAvtaleOmFinansiellSikringFastsattAvSelskapet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraAvtaleOmFinansiellSikringFastsattMedSkatteavregningsprisFraPrisraad +
                            forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonAvHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektOgFradragFraDeltakersAndelISelskapMedDeltakerfastsetting_andelAvPositivGrunnrenteinntekt +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_annenGrunnrentepliktigInntekt
                    }

                    settFelt(forekomstType.samletBruttoFradragIGrunnrenteinntekt) {
                        forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_samletDriftskostnad +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_vederlagForKjoepAvLevendeFisk +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForEiendomsskatt +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForForskningsavgift +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_andelAvVederlagPaaKjoepAvLisenserMv +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapFraAvtaleOmFinansiellSikringFastsattAvSelskapet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapFraAvtaleOmFinansiellSikringFastsattMedSkatteavregningsprisFraPrisraad +
                            forekomstType.realisasjonAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonAvHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektOgFradragFraDeltakersAndelISelskapMedDeltakerfastsetting_andelAvNegativGrunnrenteinntekt +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_annetGrunnrentepliktigFradrag +
                            forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet -
                            forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt
                    }

                    settFelt(forekomstType.grunnrenteinntektFoerSamordning) {
                        forekomstType.samletBruttoInntektIGrunnrenteinntekt -
                            forekomstType.samletBruttoFradragIGrunnrenteinntekt
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            salgsinntektFraSlaktetFiskFastsattMedSkatteavregningsprisFraPrisraad,
            salgsinntektFraSlaktetFiskFastsattAvSelskapet,
            samletDriftskostnad,
            skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet,
            investeringskostnadKnyttetTilHavbruksvirksomhet,
            grunnrenteskattHavbruk,
        )
    }
}
