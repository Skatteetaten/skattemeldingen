package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Spesifikasjon+av+anleggsmidler
 */
internal object GevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    val inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11 = listOf(
        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3b,
        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf11
    )

    val inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11 = listOf(
        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3b,
        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf11
    )

    internal val faktorAntallAar =
        opprettSyntetiskFelt(
            modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.perRealisasjonsaar,
            "faktorAntallAar"
        )

    internal val faktorAntallAarKalkyle = kalkyle("faktorAntallAar") {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningsregelAar = BigDecimal(
                if (forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11) 3
                else if (forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11) 6
                else 0
            )

            hvis(avskrivningsregelAar stoerreEnn 0) {
                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    settFelt(faktorAntallAar) {
                        val antallAar = inntektsaar - forekomstType.realisasjonsaar + 1
                        (avskrivningsregelAar - antallAar) / avskrivningsregelAar
                    }
                }
            }
        }
    }

    internal val aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                val inntektErEnAv: (kodeVerdi: List<KodeVerdi>) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
                }

                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        inntektErEnAv(inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                            forekomstType.gevinstIAlminneligInntektFraVirksomhetPaaSokkel / 6
                        }
                    }
                    hvis(
                        inntektErEnAv(inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                            forekomstType.gevinstIAlminneligInntektFraVirksomhetPaaSokkel / 3
                        }
                    }
                }
            }
        }

    internal val aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                val fradragErEnAv: (kodeVerdi: List<KodeVerdi>) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
                }

                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        fradragErEnAv(inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                            forekomstType.tapIAlminneligInntektFraVirksomhetPaaSokkel / 6
                        }
                    }
                    hvis(
                        fradragErEnAv(inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                            forekomstType.tapIAlminneligInntektFraVirksomhetPaaSokkel / 3
                        }
                    }
                }
            }
        }

    internal val utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.perRealisasjonsaar) forHverForekomst {
                settFelt(forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel) {
                    (forekomstType.gevinstIAlminneligInntektFraVirksomhetPaaSokkel - forekomstType.tapIAlminneligInntektFraVirksomhetPaaSokkel) * faktorAntallAar
                }
            }
        }

    internal val aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            val inntektsaar = inntektsaar
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                val inntektErEnAv: (kodeVerdi: List<KodeVerdi>) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
                }

                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        (forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021))
                            &&
                            inntektErEnAv(inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel / 6
                        }
                    }
                    hvis(
                        forekomstType.realisasjonsaar lik 2021
                            && inntektErEnAv(inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel / 3
                        }
                    }
                    hvis(forekomstType.realisasjonsaar lik inntektsaar.gjeldendeInntektsaar ) {
                        settFelt(forekomstType.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                        }
                    }
                }
            }
        }

    internal val aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            val inntektsaar = inntektsaar
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                val fradragErEnAv: (kodeVerdi: List<KodeVerdi>) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
                }

                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        (forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021))
                            && fradragErEnAv(inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel / 6
                        }
                    }
                    hvis(
                        forekomstType.realisasjonsaar lik 2021
                            && fradragErEnAv(inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel / 3
                        }
                    }
                    hvis(forekomstType.realisasjonsaar lik inntektsaar.gjeldendeInntektsaar) {
                        settFelt(forekomstType.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                        }
                    }
                }
            }
        }

    internal val utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                val utgaaendeErLik: (kodeVerdi: KodeVerdi) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel lik it
                }
                val utgaaendeErEnAv: (kodeVerdi: List<KodeVerdi>) -> Boolean = {
                    forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
                }
                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        (forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021))
                            && utgaaendeErEnAv(inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11)
                    ) {
                        settFelt(forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            (forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel - forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel) * faktorAntallAar
                        }
                    }
                }
                forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                    hvis(
                        forekomstType.realisasjonsaar lik 2021
                            && utgaaendeErLik(realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3b)
                    ) {
                        settFelt(forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            (forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel - forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel) * faktorAntallAar
                        }
                    }
                }
            }
        }

    internal val aaretsFriinntekt = kalkyle("aaretsFriinntekt") {
        val satser = satser!!
        val satsForfriinntekt = satser.sats(Sats.petroleum_satsForFriinntekt).toDouble()
        val satsForFriinntektPaaOvergangsreglene = satser.sats(Sats.petroleum_satsForFriinntektPaaOvergangsreglene).toDouble()
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val regelErEnAv: (kodelisteVerdier: List<KodeVerdi>) -> Boolean = {
                forekomstType.inntektsEllerFradragsfoeringsregel likEnAv it
            }

            forekomsterAv(forekomstType.perRealisasjonsaar) forHverForekomst {
                hvis (forekomstType.realisasjonsaar stoerreEllerLik 2020 && regelErEnAv(
                    listOf(
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3b,
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3b
                    )
                )) {
                    settFelt(forekomstType.aaretsFriinntekt) {
                        (forekomstType.grunnlagForFriinntektAvTapVedRealisasjon - forekomstType.grunnlagForFriinntektAvGevinstVedRealisasjon) * satsForFriinntektPaaOvergangsreglene
                    }
                }

                hvis (forekomstType.realisasjonsaar stoerreEllerLik 2021 && regelErEnAv(
                    listOf(
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf11,
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf11
                    )
                )) {
                    settFelt(forekomstType.aaretsFriinntekt) {
                        (forekomstType.grunnlagForFriinntektAvTapVedRealisasjon - forekomstType.grunnlagForFriinntektAvGevinstVedRealisasjon) * satsForFriinntektPaaOvergangsreglene
                    }
                }

                hvis (forekomstType.realisasjonsaar lik inntektsaar.gjeldendeInntektsaar && regelErEnAv(
                    listOf(
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf11,
                        realisasjonsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf11
                    )
                )) {
                    settFelt(forekomstType.aaretsFriinntekt) {
                        (forekomstType.grunnlagForFriinntektAvTapVedRealisasjon - forekomstType.grunnlagForFriinntektAvGevinstVedRealisasjon) * satsForfriinntekt
                    }
                }
            }
        }
    }

    internal val samletNettogevinstIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("samletNettogevinstIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.samletNettogevinstIAlminneligInntektFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.gevinstIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val samletNettotapIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("samletNettotapIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.samletNettotapIAlminneligInntektFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.tapIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("aaretsSamledeInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.aaretsSamledeInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("aaretsSamledeFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.aaretsSamledeFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                        forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeFriinntekt = kalkyle("aaretsSamledeFriinntekt") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.aaretsSamledeFriinntekt) {
                forekomsterAv(forekomstType.perRealisasjonsaar) summerVerdiFraHverForekomst {
                    forekomstType.aaretsFriinntekt.tall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            faktorAntallAarKalkyle,
            aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsFriinntekt,
            samletNettogevinstIAlminneligInntektFraVirksomhetPaaSokkel,
            samletNettotapIAlminneligInntektFraVirksomhetPaaSokkel,
            samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel,
            samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsSamledeInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsSamledeFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsSamledeFriinntekt
        )
    }
}