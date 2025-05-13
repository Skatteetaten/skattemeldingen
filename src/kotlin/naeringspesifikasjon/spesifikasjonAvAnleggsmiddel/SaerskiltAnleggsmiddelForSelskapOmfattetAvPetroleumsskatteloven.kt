package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Spesifikasjon+av+anleggsmidler
 */
internal object SaerskiltAnleggsmiddelForSelskapOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    val avskrivningOver3AarEtterPetroleumsskattelovenParagraf3bOg11 = listOf(
        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver3AarEtterPetroleumsskattelovenParagraf3b,
        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver3AarEtterPetroleumsskattelovenParagraf11
    )

    val avskrivningOver6AarEtterPetroleumsskattelovenParagraf3bOg11 = listOf(
        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf3b,
        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf11
    )

    internal val faktorAntallAar =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar, "faktorAntallAar")

    internal val kostprisEtterKorreksjon = kalkyle("kostprisEtterKorreksjon") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) forHverForekomst {
            settFelt(forekomstType.kostprisEtterKorreksjon) {
                forekomstType.kostpris +
                    forekomstType.korreksjonAvKostprisSomFoelgeAvRealisasjon +
                    forekomstType.korreksjonAvKostprisSomFoelgeAvOverdragelseMv +
                    forekomstType.korreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling
            }
        }
    }

    internal val aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningErEnAv: (kodelisteVerdier: Collection<KodeVerdi>) -> Boolean = {
                forekomstType.avskrivningsregel likEnAv it
            }

            forekomsterAv(forekomstType.perInvesteringsaar) forHverForekomst {
                hvis(avskrivningErEnAv(avskrivningOver6AarEtterPetroleumsskattelovenParagraf3bOg11)) {
                    settFelt(forekomstType.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel) {
                        forekomstType.kostprisEtterKorreksjon / 6
                    }
                }
                hvis(avskrivningErEnAv(avskrivningOver3AarEtterPetroleumsskattelovenParagraf3bOg11)) {
                    settFelt(forekomstType.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel) {
                        forekomstType.kostprisEtterKorreksjon / 3
                    }
                }
            }
        }
    }

    internal val faktorAntallAarKalkyle = kalkyle("faktorAntallAar") {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningsregelAar = BigDecimal(
                if (forekomstType.avskrivningsregel likEnAv avskrivningOver3AarEtterPetroleumsskattelovenParagraf3bOg11) 3
                else if (forekomstType.avskrivningsregel likEnAv avskrivningOver6AarEtterPetroleumsskattelovenParagraf3bOg11) 6
                else 0
            )

            hvis(avskrivningsregelAar stoerreEnn 0) {
                forekomsterAv(forekomstType.perInvesteringsaar) forHverForekomst {
                    settFelt(faktorAntallAar) {
                        val antallAar = inntektsaar - forekomstType.investeringsaar + 1
                        (avskrivningsregelAar - antallAar) / avskrivningsregelAar
                    }
                }
            }
        }
    }

    internal val utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) forHverForekomst {
            settFelt(forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel) {
                (forekomstType.kostprisEtterKorreksjon * faktorAntallAar) -
                    forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonIAlminneligInntektFraVirksomhetPaaSokkel
            }
        }
    }

    internal val aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningErLik: (kodelisteVerdi: KodeVerdi) -> Boolean = {
                forekomstType.avskrivningsregel lik it
            }

            forekomsterAv(forekomstType.perInvesteringsaar) forHverForekomst {
                hvis(forekomstType.investeringsaar lik 2021 && avskrivningErLik(avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver3AarEtterPetroleumsskattelovenParagraf3b)) {
                    settFelt(forekomstType.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                        forekomstType.kostprisEtterKorreksjon / 3
                    }
                }

                hvis(forekomstType.investeringsaar likEnAv listOf(2018, 2019) && avskrivningErLik(avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf3b)) {
                    settFelt(forekomstType.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                        forekomstType.kostprisEtterKorreksjon / 6
                    }
                }

                hvis (forekomstType.investeringsaar lik inntektsaar.gjeldendeInntektsaar) {
                    settFelt(forekomstType.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                        forekomstType.kostprisEtterKorreksjon.tall()
                    }
                }
            }
        }
    }

    internal val utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningErLik: (kodelisteVerdi: KodeVerdi) -> Boolean = {
                forekomstType.avskrivningsregel lik it
            }

            forekomsterAv(forekomstType.perInvesteringsaar) der {
                forekomstType.investeringsaar lik 2019 && avskrivningErLik(avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf3b)
            } forHverForekomst {
                settFelt(forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    (forekomstType.kostprisEtterKorreksjon * faktorAntallAar) -
                        forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonISaerskattegrunnlagFraVirksomhetPaaSokkel
                }
            }
        }
    }

    internal val aaretsFriinntekt = kalkyle("aaretsFriinntekt") {
        val satser = satser!!
        val satsForFriinntekt = satser.sats(Sats.petroleum_satsForFriinntekt).toDouble()
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            val avskrivningErEnAv: (kodelisteVerdier: Collection<KodeVerdi>) -> Boolean = {
                forekomstType.avskrivningsregel likEnAv it
            }

            forekomsterAv(forekomstType.perInvesteringsaar) forHverForekomst {
                hvis (forekomstType.investeringsaar lik inntektsaar.gjeldendeInntektsaar && avskrivningErEnAv(
                    listOf(
                        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf11,
                        avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver3AarEtterPetroleumsskattelovenParagraf11
                    )
                )) {
                    settFelt(forekomstType.aaretsFriinntekt) {
                        forekomstType.kostprisEtterKorreksjon * satsForFriinntekt
                    }
                }
            }
        }
    }

    internal val samletKostpris = kalkyle("samletKostpris") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.samletKostpris) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.kostpris.tall()
                }
            }
        }
    }

    internal val samletKorreksjonAvKostprisSomFoelgeAvRealisasjon = kalkyle("samletKorreksjonAvKostprisSomFoelgeAvRealisasjon") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.samletKorreksjonAvKostprisSomFoelgeAvRealisasjon) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.korreksjonAvKostprisSomFoelgeAvRealisasjon.tall()
                }
            }
        }
    }

    internal val samletKorreksjonAvKostprisSomFoelgeAvOverdragelseMv = kalkyle("samletKorreksjonAvKostprisSomFoelgeAvOverdragelseMv") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.samletKorreksjonAvKostprisSomFoelgeAvOverdragelseMv) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.korreksjonAvKostprisSomFoelgeAvOverdragelseMv.tall()
                }
            }
        }
    }

    internal val samletKorreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling = kalkyle("samletKorreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.samletKorreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.korreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling.tall()
                }
            }
        }
    }

    internal val samletKostprisEtterKorreksjon = kalkyle("samletKostprisEtterKorreksjon") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.samletKostprisEtterKorreksjon) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.kostprisEtterKorreksjon.tall()
                }
            }
        }
    }

    internal val aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel +
                        forekomstType.korreksjonAvTidligereAarsAvskrivningSomFoelgeAvSamordningEllerOmfordelingIAlminneligInntektFraVirksomhetPaaSokkel +
                        forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonIAlminneligInntektFraVirksomhetPaaSokkel
                }
            }
        }
    }

    internal val samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
        }
    }

    internal val aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel +
                        forekomstType.korreksjonAvTidligereAarsAvskrivningSomFoelgeAvSamordningEllerOmfordelingISaerskattegrunnlagFraVirksomhetPaaSokkel +
                        forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonISaerskattegrunnlagFraVirksomhetPaaSokkel
                }
            }
        }
    }

    internal val samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }
    }

    internal val aaretsSamledeFriinntekt = kalkyle("aaretsSamledeFriinntekt") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
            settFelt(forekomstType.aaretsSamledeFriinntekt) {
                forekomsterAv(forekomstType.perInvesteringsaar) summerVerdiFraHverForekomst {
                    forekomstType.aaretsFriinntekt +
                        forekomstType.korreksjonAvTidligereAarsFriinntektSomFoelgeAvSamordningEllerOmfordeling
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            kostprisEtterKorreksjon,
            aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel,
            faktorAntallAarKalkyle,
            utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel,
            utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsFriinntekt,
            samletKostpris,
            samletKorreksjonAvKostprisSomFoelgeAvRealisasjon,
            samletKorreksjonAvKostprisSomFoelgeAvOverdragelseMv,
            samletKorreksjonAvKostprisSomFoelgeAvSamordningEllerOmfordeling,
            samletKostprisEtterKorreksjon,
            aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel,
            samletUtgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel,
            samletUtgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsSamledeFriinntekt
        )
    }
}
