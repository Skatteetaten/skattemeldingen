package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.havbruk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Havbruksvirksomhet
 */
internal object GrunnrenteskattHavbruk2023 : HarKalkylesamling {

    private val grunnrenteskattHavbruk =
        kalkyle("grunnrenteskattHavbruk") {
            if (statisk.skatteplikt.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
                val satser = satser!!
                forekomsterAv(modell2023.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet) forHverForekomst {

                    val gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet =
                        forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_bruttoVederlag -
                            forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_andelAvVederlagKnyttetTilAkvakulturtillatelse -
                            forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_skattepliktigVerdiAvAnleggsmiddelSomBenyttesIDenGrunnrentepliktigeHavbruksvirksomheten

                    if (gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet.stoerreEllerLik(0)) {
                        settFelt(forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonEllerOpphoerAvHavbruksvirksomhet) {
                            gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet
                        }
                    } else {
                        settFelt(forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet) {
                            gevinstTapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet.absoluttverdi()
                        }
                    }

                    settFelt(forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt) {
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_samletSalgsinntektFraLevendeFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_annenGrunnrentepliktigInntekt +
                            forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonEllerOpphoerAvHavbruksvirksomhet +
                            forekomstType.beregnetSelskapsskatt_tilleggForAaretsGevinstIAlminneligInntektVedRealisasjonAvAnleggsmiddelSomErDirekteFradragsfoertIGrunnrenteinntekt -
                            (forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_vederlagForKjoepAvLevendeFisk +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForEiendomsskatt +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForForskningsavgift +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_andelAvVederlagPaaKjoepAvLisenserMv +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelFraHistoriskInvesteringVedInntreden +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelErvervetFraNaerstaaende +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                                forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_annetGrunnrentepliktigFradrag +
                                forekomstType.beregnetSelskapsskatt_tilbakefoeringAvGevinstPaaInvesteringSomBenyttesIHavbruksvirksomhetSomErDirekteUtgiftsfoert +
                                forekomstType.beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelSomBenyttesIHavbruksvirksomhetSomErDirekteUtgiftsfoert +
                                forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet +
                                forekomstType.beregnetSelskapsskatt_fradragForAaretsTapIAlminneligInntektVedRealisasjonAvAnleggsmiddelSomErDirekteFradragsfoertIGrunnrenteinntekt)
                    }

                    if (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt.stoerreEllerLik(0)) {
                        settFelt(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats))
                        }
                    } else if (forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt.mindreEnn(0)) {
                        settFelt(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            forekomstType.beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt.tall()
                                .absoluttverdi() *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)
                        }
                    }

                    if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .stoerreEnn(0) &&
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .stoerreEllerLik(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()) //beregnes over
                    ) {
                        settFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                        }
                    } else if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .stoerreEnn(0) &&
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            .mindreEnn(forekomstType.beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall())
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
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_salgsinntektFraSlaktetFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_samletSalgsinntektFraLevendeFisk +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_gevinstVedRealisasjonEllerOpphoerAvHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvInntektOgFradragFraDeltakersAndelISelskapMedDeltakerfastsetting_andelAvPositivGrunnrenteinntekt +
                            forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_annenGrunnrentepliktigInntekt
                    }

                    settFelt(forekomstType.samletBruttoFradragIGrunnrenteinntekt) {
                        forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_vederlagForKjoepAvLevendeFisk +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForEiendomsskatt +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_fradragForForskningsavgift +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_andelAvVederlagPaaKjoepAvLisenserMv +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelFraHistoriskInvesteringVedInntreden +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelErvervetFraNaerstaaende +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedUttakAvAnleggsmiddelSomBenyttesIHavbruksvirksomhet +
                            forekomstType.realisasjonEllerOpphoerAvGrunnrentepliktigHavbruksvirksomhet_tapVedRealisasjonEllerOpphoerAvHavbruksvirksomhet +
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
            grunnrenteskattHavbruk,
        )
    }
}
