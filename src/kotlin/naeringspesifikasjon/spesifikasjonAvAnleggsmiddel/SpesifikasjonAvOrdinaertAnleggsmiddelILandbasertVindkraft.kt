package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.samlesaldoene
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

internal object SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft : HarKalkylesamling {

    private val direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt =
        kalkyle("direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt")
        {
            val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
            val satser = satser!!

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning
                    ) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                                benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente *
                                satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.ervervsdato.aar() == inntektsaar
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt) {
                        forekomstType.nyanskaffelse +
                            forekomstType.paakostning
                    }
                }
            }

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
                hvis(
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt) {
                        if (forekomstType.ervervsdato.aar() == inntektsaar) {
                            forekomstType.anskaffelseskost.tall()
                        } else {
                            forekomstType.paakostning.tall()
                        }
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn inntektsaar
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_justeringPaaSaldo -
                                forekomstType.vederlagVedRealisasjonOgUttak
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && (forekomstType.ervervsdato.harIkkeVerdi() || forekomstType.ervervsdato.aar() stoerreEllerLik inntektsaar)
                    ) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering.tall()
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt stoerreEnn 0
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn inntektsaar
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt / deltPaa(inntektsaar)
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt stoerreEnn 0
                            && (forekomstType.ervervsdato.harIkkeVerdi() || forekomstType.ervervsdato.aar() stoerreEllerLik inntektsaar)
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        (forekomstType.aaretsAvskrivning.tall())
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt +
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_gevinstVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_tapVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto +
                    forekomstType.aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
                }

                hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente) {
                        beregnHvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024.harIkkeVerdi()) {
                            (forekomstType.inngaaendeVerdi + forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) / 2
                        }
                    }
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente) {
                        beregnHvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024.harVerdi()) {
                            (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024 +
                                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) / 2
                        }
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente *
                            satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }
            }

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                    ) {
                        forekomstType.nyanskaffelse +
                            forekomstType.paakostning
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn inntektsaar
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_justeringPaaSaldo -
                                forekomstType.vederlagVedRealisasjonOgUttak
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && (forekomstType.ervervsdato.harIkkeVerdi() || forekomstType.ervervsdato.aar() stoerreEllerLik inntektsaar)
                    ) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering.tall()
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn inntektsaar
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt / deltPaa(inntektsaar)
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                            && (forekomstType.ervervsdato.harIkkeVerdi() || forekomstType.ervervsdato.aar() stoerreEllerLik inntektsaar)
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt *
                                (forekomstType.avskrivningssats / 100)
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForAvskrivningIGrunnrenteinntekt -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt +
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_gevinstVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto -
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_tapVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto +
                    forekomstType.aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
                }

                hvis(
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning &&
                        forekomstType.utgaaendeVerdi stoerreEllerLik 0
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente) {
                        beregnHvis(
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024.harIkkeVerdi() &&
                                forekomstType.inngaaendeVerdi stoerreEllerLik 0
                        ) {
                            (forekomstType.inngaaendeVerdi + forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) / 2
                        }
                    }
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente) {
                        beregnHvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024.harVerdi()) {
                            (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_oppjustertGrunnlagForAvskrivningPer01012024 +
                                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_utgaaendeVerdi) / 2
                        }
                    }
                }


                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_grunnlagForBeregningAvVenterente *
                            satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }
            }
        }

    internal val skattemessigAvskrivningAvDriftsmiddel = kalkyle {

        fun sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt.tall()
            }

        fun sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt.tall()
            }

        fun sumAaretsAvskrivningSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet.lik(
                    benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt.harIkkeVerdi()
                    && (forekomstType.realisasjonsdato.harIkkeVerdi() || forekomstType.saldogruppe likEnAv samlesaldoene)
                    && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivning.tall()
            }

        fun sumAaretsAvskrivningLineaert(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet.lik(
                    benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt.harIkkeVerdi()
                    && forekomstType.realisasjonsdato.harIkkeVerdi()
                    && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivning.tall()
            }

        fun sumaAretsInntektsfoerigAvNegativSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_benyttesIGrunnrenteskattepliktigVirksomhet.lik(
                    benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_aaretsAvskrivningIGrunnrenteinntekt.harIkkeVerdi()
                    && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
            }

        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            loepenummer?.let {
                val sumAaretsAvskrivningSaldo = sumAaretsAvskrivningSaldo(loepenummer)
                val sumAaretsAvskrivningLineaert = sumAaretsAvskrivningLineaert(loepenummer)
                val sumaAretsInntektsfoerigAvNegativSaldo = sumaAretsInntektsfoerigAvNegativSaldo(loepenummer)
                val sumSaldoOgLinaert = sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer) + sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer)

                val beloep =
                    sumAaretsAvskrivningSaldo + sumAaretsAvskrivningLineaert - sumaAretsInntektsfoerigAvNegativSaldo + sumSaldoOgLinaert

                if (beloep != null) {
                    val kode = fradragIGrunnrente.kode_skattemessigAvskrivningAvDriftsmiddel.kode

                    opprettNySubforekomstAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) {
                        medId(kode)
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.type,
                            kode
                        )
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.beloep,
                            beloep
                        )
                    }
                }
            }
        }
    }

    internal val investeringskostnad = kalkyle {
        fun saldoForekomster(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun lineaereForekomster(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun ikkeAvskrivbareForekomster(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektSaldo(loepenummer: String) =
            saldoForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektLineaere(loepenummer: String) =
            lineaereForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektIkkeAvskrivbare(loepenummer: String) =
            ikkeAvskrivbareForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_direkteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntektIInntektsaaret.tall()
            }

        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            loepenummer?.let {
                val sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt =
                    sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektSaldo(loepenummer) +
                        sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektLineaere(loepenummer) +
                        sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektIkkeAvskrivbare(loepenummer)

                val sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt =
                    sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt(loepenummer)

                val beloep =
                    sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt + sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt

                if (beloep != null) {
                    val kode = fradragIGrunnrente.kode_investeringskostnad.kode
                    opprettNySubforekomstAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) {
                        medId(kode)
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.type,
                            kode
                        )
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.beloep,
                            beloep
                        )
                    }
                }
            }
        }
    }

    internal val venterente = kalkyle {
        fun sumVenterenteSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente.tall() }

        fun sumVenterenteLineaere(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente.tall() }

        fun sumVenterenteIkkeAvskrivbare(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_venterente.tall() }

        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            loepenummer?.let {
                val sumVenterente =
                    sumVenterenteSaldo(loepenummer) + sumVenterenteLineaere(loepenummer) + sumVenterenteIkkeAvskrivbare(loepenummer)

                if (sumVenterente != null) {
                    val kode = fradragIGrunnrente.kode_venterente.kode

                    opprettNySubforekomstAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) {
                        medId(kode)
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.type,
                            kode
                        )
                        medFelt(
                            forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk.beloep,
                            sumVenterente
                        )
                    }
                }
            }
        }

    }

    private fun deltPaa(inntektsaar: BigDecimal?): Int {
        return when (inntektsaar?.toInt()) {
                2024 -> 5
                2025 -> 4
                2026 -> 3
                2027 -> 2
                else -> 1
            }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt,
            skattemessigAvskrivningAvDriftsmiddel,
            investeringskostnad,
            venterente
        )
    }
}