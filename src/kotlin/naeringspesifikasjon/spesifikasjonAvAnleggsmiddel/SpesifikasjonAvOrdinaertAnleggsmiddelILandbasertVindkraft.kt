package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft : HarKalkylesamling {

    private val direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt =
        kalkyle("direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt")
        {
            val inntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
            val satser = satser!!

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) forHverForekomst {

                hvis(
                    listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag.kode,
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse.kode,
                    ).contains(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.verdi())
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt) {
                        if (forekomstType.ervervsdato.aar() == inntektsaar) {
                            forekomstType.nyanskaffelse + forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                        } else {
                            forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                        }
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning
                    ) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                                benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente *
                                satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }
            }

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
                hvis(
                    listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag.kode,
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse.kode,
                    ).contains(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.verdi())
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt) {
                        if (forekomstType.ervervsdato.aar() == inntektsaar) {
                            forekomstType.anskaffelseskost + forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                        } else {
                            forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                        }
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024) {
                    beregnHvis(
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                            )
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn 2024
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelOppjustertPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_justeringPaaSaldo -
                                forekomstType.vederlagVedRealisasjonOgUttak
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.anskaffelseskost +
                            forekomstType.paakostning
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024) {
                    beregnHvis(
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                            )
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 stoerreEnn 0
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn 2024
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 / deltPaa(inntektsaar)
                    }
                }

                hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024.harVerdi()) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvOppjustertVerdiPr01012024) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_gevinstVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_tapVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
                    }
                }

                hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024.harVerdi()) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvAnskaffelseEtter01012024) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024 -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024
                    }
                }

                hvis(
                    (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                        )
                        &&
                        forekomstType.utgaaendeVerdi stoerreEllerLik 0 && forekomstType.inngaaendeVerdi stoerreEllerLik 0
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente) {
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelOppjustertPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvOppjustertVerdiPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvAnskaffelseEtter01012024) / 2
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente.harVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente *
                            satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }
            }

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt) {
                    beregnHvis(
                        listOf(
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag.kode,
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse.kode,
                        ).contains(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.verdi())
                    ) {
                        forekomstType.nyanskaffelse + forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024) {
                    beregnHvis(
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                            )
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn 2024
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelOppjustertPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_justeringPaaSaldo -
                                forekomstType.vederlagVedRealisasjonOgUttak
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.nyanskaffelse +
                            forekomstType.paakostning
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_andelAvDriftsmiddelAnskaffetIInntektsaaretSomAvskrives
                    }
                }

                hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024.harVerdi()) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvAnskaffelseEtter01012024) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024 -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024
                    }
                }

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024) {
                    beregnHvis(
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                            )
                            && forekomstType.ervervsdato.harVerdi() && forekomstType.ervervsdato.aar() mindreEnn 2024
                            && forekomstType.realisasjonsdato.harIkkeVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 / deltPaa(inntektsaar)
                    }
                }

                hvis (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024.harVerdi()) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvOppjustertVerdiPr01012024) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_gevinstVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto -
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_tapVedRealisasjonOgUttakSomSkalOverfoeresTilGevinstOgTapskonto +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert
                    }
                }

                hvis(
                    (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning ||
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningPaaOppjustertVerdiOgDirekteFradragPaaHeleEllerDelAvAnskaffelse
                        )
                        &&
                        forekomstType.utgaaendeVerdi stoerreEllerLik 0 && forekomstType.inngaaendeVerdi stoerreEllerLik 0
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente) {
                        (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelOppjustertPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_inngaaendeVerdiForDriftsmiddelAnskaffetEtter01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvOppjustertVerdiPr01012024 +
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_utgaaendeVerdiAvAnskaffelseEtter01012024) / 2
                    }
                }


                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente.harVerdi()
                    ) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_grunnlagForBeregningAvVenterente *
                            satser.sats(Sats.landbasertVindkraft_normrenteForBeregningAvVenterente)
                    }
                }
            }
        }

    internal val skattemessigAvskrivningAvOppjustertDriftsmiddel = kalkyle {

        fun sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024.tall()
            }

        fun sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvOppjustertVerdiPr01012024.tall()
            }


        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            loepenummer?.let {
                val beloep = sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer) + sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer)

                if (beloep != null) {
                    val kode = fradragIGrunnrente.kode_skattemessigAvskrivningAvOppjustertDriftsmiddel.kode

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

    internal val skattemessigAvskrivningAvDriftsmiddelAnskaffetEtter01012024 = kalkyle {

        fun sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024.tall()
            }

        fun sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_aaretsAvskrivningIGrunnrenteinntektAvAnskaffelseEtter01012024.tall()
            }


        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            loepenummer?.let {
                val beloep = sumAaretsAvskrivningIGrunnrenteinntektSaldo(loepenummer) + sumAaretsAvskrivningIGrunnrenteinntektLineaert(loepenummer)

                if (beloep != null) {
                    val kode = fradragIGrunnrente.kode_skattemessigAvskrivningAvDriftsmiddelAnskaffetEtter01012024.kode

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
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun lineaereForekomster(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun ikkeAvskrivbareForekomster(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektSaldo(loepenummer: String) =
            saldoForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektLineaere(loepenummer: String) =
            lineaereForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntektIkkeAvskrivbare(loepenummer: String) =
            ikkeAvskrivbareForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt.tall()
            }

        fun sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt(loepenummer: String): BigDecimal? {
            val inntektsaar = inntektsaar
            return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                val kraftverketsLoepenummer = if (inntektsaar.tekniskInntektsaar >= 2025) {
                    forekomstType.vindkraftverketsLoepenummer
                } else {
                    felt2024.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert.kraftverketsLoepenummer
                }
                kraftverketsLoepenummer lik loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntektIInntektsaaret.tall()
            }
        }

        fun sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarSaldo(loepenummer: String) =
            saldoForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar.tall()
            }

        fun sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarLineaere(loepenummer: String) =
            lineaereForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar.tall()
            }

        fun sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarIkkeAvskrivbare(loepenummer: String) =
            ikkeAvskrivbareForekomster(loepenummer) summerVerdiFraHverForekomst {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar.tall()
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

                val sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar =
                    sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarSaldo(loepenummer) +
                    sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarLineaere(loepenummer) +
                    sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaarIkkeAvskrivbare(loepenummer)

                val beloep = sumDirekteUtgiftsfoertInvesteringsavgiftIGrunnrenteinntekt +
                    sumDirekteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt -
                    sumDelAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar

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
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente.tall() }

        fun sumVenterenteLineaere(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente.tall() }

        fun sumVenterenteIkkeAvskrivbare(loepenummer: String) =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst { forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftverk_venterente.tall() }

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
            skattemessigAvskrivningAvOppjustertDriftsmiddel,
            skattemessigAvskrivningAvDriftsmiddelAnskaffetEtter01012024,
            investeringskostnad,
            venterente
        )
    }
}