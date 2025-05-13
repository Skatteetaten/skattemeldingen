package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektPetroleum.petroleum
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaerskiltAnleggsmiddelForSelskapOmfattetAvPetroleumsskatteloven.avskrivningOver3AarEtterPetroleumsskattelovenParagraf3bOg11
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaerskiltAnleggsmiddelForSelskapOmfattetAvPetroleumsskatteloven.avskrivningOver6AarEtterPetroleumsskattelovenParagraf3bOg11
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.permanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.permanentForskjellstype2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=856685903
 */

internal object FordeltBeregnetNaeringsinntektPetroleum2023 : HarKalkylesamling {

    internal val beregnetSelskapsskatt = modell.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig

    internal val aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                forekomstType.aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }

        val avskrivning6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) der {
            forekomstType.avskrivningsregel lik avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver6AarEtterPetroleumsskattelovenParagraf3b
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perInvesteringsaar) der { forekomstType.investeringsaar likEnAv listOf(2018, 2019) } summerVerdiFraHverForekomst {
                forekomstType.kostprisEtterKorreksjon / 6
            }
        }

        val avskrivning3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) der {
            forekomstType.avskrivningsregel lik avskrivningsregelForVirksomhetOmfattetAvPetroleumsskatteloven.kode_avskrivningOver3AarEtterPetroleumsskattelovenParagraf3b
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perInvesteringsaar) der { forekomstType.investeringsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.kostprisEtterKorreksjon / 3
            }
        }

        val fradragForGjenvaerendeKostprisFoer2022 = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) der {
            forekomstType.investeringsaar likEnAv listOf(2019, 2020, 2021)
        } summerVerdiFraHverForekomst {
            forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            (aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel -
                avskrivning6Aar -
                avskrivning3Aar -
                fradragForGjenvaerendeKostprisFoer2022) medAntallDesimaler 2
        }
    }

    internal val aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                forekomstType.aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }

        val inntektsfoertBeloep6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021) } summerVerdiFraHverForekomst {
                forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel / 6
            }
        }

        val inntektsfoertBeloep3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel / 3
            }
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            (aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel -
                inntektsfoertBeloep6Aar -
                inntektsfoertBeloep3Aar) medAntallDesimaler 2
        }
    }

    internal val aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                forekomstType.aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }

        val fradragsfoertBeloep6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021) } summerVerdiFraHverForekomst {
                forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel / 6
            }
        }

        val fradragsfoertBeloep3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel / 3
            }
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            (aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel -
                fradragsfoertBeloep6Aar -
                fradragsfoertBeloep3Aar) medAntallDesimaler 2
        }
    }

    internal val aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel") {
        val aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                forekomstType.aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel.tall()
            }

        val avskrivning6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) der {
            forekomstType.avskrivningsregel likEnAv avskrivningOver6AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perInvesteringsaar) der { forekomstType.investeringsaar likEnAv listOf(2018, 2019, 2020, 2021) } summerVerdiFraHverForekomst {
                forekomstType.kostprisEtterKorreksjon / 6
            }
        }

        val avskrivning3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) der {
            forekomstType.avskrivningsregel likEnAv avskrivningOver3AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            forekomsterAv(forekomstType.perInvesteringsaar) der { forekomstType.investeringsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.kostprisEtterKorreksjon / 3
            }
        }

        val fradragForGjenvaerendeKostprisFoer2022 = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) der {
            forekomstType.investeringsaar likEnAv listOf(2019, 2020, 2021)
        } summerVerdiFraHverForekomst {
            forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonIAlminneligInntektFraVirksomhetPaaSokkel.tall()
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel) {
            (aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel -
                avskrivning6Aar -
                avskrivning3Aar -
                fradragForGjenvaerendeKostprisFoer2022) medAntallDesimaler 2
        }
    }

    internal val aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
        val inntektsfoertBeloep6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            val gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel = forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021) } summerVerdiFraHverForekomst {
                forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
            (forekomstType.samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel - gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel) / 6
        }

        val inntektsfoertBeloep3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            val gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel = forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
            (forekomstType.samletNettogevinstISaerskattegrunnlagFraVirksomhetPaaSokkel - gevinstISaerskattegrunnlagFraVirksomhetPaaSokkel) / 3
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
            (inntektsfoertBeloep6Aar + inntektsfoertBeloep3Aar) medAntallDesimaler 2
        }
    }

    internal val aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel") {
        val fradragsfoertBeloep6Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver6AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            val tapISaerskattegrunnlagFraVirksomhetPaaSokkel = forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar likEnAv listOf(2018, 2019, 2020, 2021) } summerVerdiFraHverForekomst {
                forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
            (forekomstType.samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel - tapISaerskattegrunnlagFraVirksomhetPaaSokkel) / 6
        }

        val fradragsfoertBeloep3Aar = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) der {
            forekomstType.inntektsEllerFradragsfoeringsregel likEnAv inntektsEllerFradragsfoeresOver3AarEtterPetroleumsskattelovenParagraf3bOg11
        } summerVerdiFraHverForekomst {
            val tapISaerskattegrunnlagFraVirksomhetPaaSokkel = forekomsterAv(forekomstType.perRealisasjonsaar) der { forekomstType.realisasjonsaar lik 2021 } summerVerdiFraHverForekomst {
                forekomstType.tapISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
            (forekomstType.samletNettotapISaerskattegrunnlagFraVirksomhetPaaSokkel - tapISaerskattegrunnlagFraVirksomhetPaaSokkel) / 3
        }

        settUniktFelt(beregnetSelskapsskatt.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel) {
            (fradragsfoertBeloep6Aar + fradragsfoertBeloep3Aar) medAntallDesimaler 2
        }
    }

    internal val grunnlagForBeregningAvSelskapsskatt = kalkyle("grunnlagForBeregningAvSelskapsskatt") {
        hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
            val sumBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
                forekomsterAv(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    if (forekomstType.permanentForskjellstype lik permanentForskjellstype.kode_friinntektEtterPetroleumsskatteloven ||
                        forekomstType.permanentForskjellstype lik permanentForskjellstype2023.kode_friinntektEtterSamordningsforskriften
                    ) {
                        forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }
            settUniktFelt(petroleum.grunnlagForBeregningAvSelskapsskatt) {
                petroleum.fordeltSkattemessigResultatEtterKorreksjon_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel -
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel +
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel -
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel +
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel -
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel +
                    sumBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.aaretsKorrigeringForFriinntekt +
                    FordeltBeregnetNaeringsinntektPetroleum.beregnetSelskapsskatt.annenKorreksjon

            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            FordeltBeregnetNaeringsinntektPetroleum.varigOgBetydeligAnleggsmiddelMv,
            FordeltBeregnetNaeringsinntektPetroleum.anleggsmiddelSomRoerledningProduksjonsinnretningMv,
            FordeltBeregnetNaeringsinntektPetroleum.sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel,
            FordeltBeregnetNaeringsinntektPetroleum.sumNettoFinanskostnader,
            FordeltBeregnetNaeringsinntektPetroleum.sumNettoFinanskostnaderForVirksomhetPaaSokkel,
            FordeltBeregnetNaeringsinntektPetroleum.andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel,
            FordeltBeregnetNaeringsinntektPetroleum.fordeltSaerskattegrunnlagFraVirksomhetPaaSokkel,
            FordeltBeregnetNaeringsinntektPetroleum.fordeltAlminneligInntektFraVirksomhetPaaSokkel,
            FordeltBeregnetNaeringsinntektPetroleum.fordeltResultatAvFinansinntektOgFinanskostnadMv,
            FordeltBeregnetNaeringsinntektPetroleum.fordeltAlminneligInntektFraVirksomhetPaaLand,
            aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel,
            grunnlagForBeregningAvSelskapsskatt
        )
    }
}