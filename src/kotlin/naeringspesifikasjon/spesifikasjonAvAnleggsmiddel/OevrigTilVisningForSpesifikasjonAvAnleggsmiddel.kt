package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.grunnrenteomraade
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023

object OevrigTilVisningForSpesifikasjonAvAnleggsmiddel : HarKalkylesamling {

    internal val samletSaldoavskrivning = kalkyle("samletSaldoavskrivning") {
        settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletSaldoavskrivning) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivning.tall()
            }
        }
    }

    internal val samletInntektsfoeringAvNegativSaldo = kalkyle("samletInntektsfoeringAvNegativSaldo") {
        settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfoeringAvNegativSaldo) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
            }
        }
    }

    internal val samletLineaerAvskrivning = kalkyle("samletLineaerAvskrivning") {
        settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletLineaerAvskrivning) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivning.tall()
            }
        }
    }

    internal val samletInntektFraGevinstOgTapskonto = kalkyle("samletInntektFraGevinstOgTapskonto") {
        settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektFraGevinstOgTapskonto) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                forekomstType.inntektFraGevinstOgTapskonto.tall()
            }
        }
    }

    internal val samletInntektsfradragFraGevinstOgTapskonto = kalkyle("samletInntektsfradragFraGevinstOgTapskonto") {
        settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfradragFraGevinstOgTapskonto) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                forekomstType.inntektsfradragFraGevinstOgTapskonto.tall()
            }
        }
    }

    internal val samletFriinntektFoer2024 = kalkyle {
        if (inntektsaar.tekniskInntektsaar < 2024) {
            settUniktFelt(modell2023.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletFriinntekt) {
                val saerskilteFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                val ikkeAvskrivbareFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                    }
                val lineaereFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                    }
                val saldoFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                    }
                val anleggsmiddelUnderUtfoerelseFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) summerVerdiFraHverForekomst {
                        forekomstType.aaretsFriinntekt.tall()
                    }
                val saerskiltePetroleumFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                val gevinstOgTapskontoFriinntekt =
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }

                saerskilteFriinntekt +
                    ikkeAvskrivbareFriinntekt +
                    lineaereFriinntekt +
                    saldoFriinntekt +
                    anleggsmiddelUnderUtfoerelseFriinntekt +
                    saerskiltePetroleumFriinntekt +
                    gevinstOgTapskontoFriinntekt
            }
        }
    }

    internal val samletFriinntekt = kalkyle {
        if (inntektsaar.tekniskInntektsaar >= 2024) {
            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletFriinntekt) {
                val saerskilteFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                val ikkeAvskrivbareFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                    }
                val lineaereFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                    }
                val saldoFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                    }
                val anleggsmiddelUnderUtfoerelseFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) summerVerdiFraHverForekomst {
                        forekomstType.aaretsFriinntektForAnleggsmiddelIVannkraftOmfattetAvGrunnrenteskatt.tall()
                    }
                val saerskiltePetroleumFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                val gevinstOgTapskontoFriinntekt =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }

                saerskilteFriinntekt +
                    ikkeAvskrivbareFriinntekt +
                    lineaereFriinntekt +
                    saldoFriinntekt +
                    anleggsmiddelUnderUtfoerelseFriinntekt +
                    saerskiltePetroleumFriinntekt +
                    gevinstOgTapskontoFriinntekt
            }
        }
    }

    internal val samletAvskrivningInntektsfoeringOgInntektsfradrag =
        kalkyle("samletAvskrivningInntektsfoeringOgInntektsfradrag") {
            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletAvskrivningAvSaerskiltAnleggsmiddelIAlminneligInntektFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletAvskrivningAvSaerskiltAnleggsmiddelISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfoeringIAlminneligInntektFraGevinstOgTapskontoFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfradragIAlminneligInntektFraGevinstOgTapskontoFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfoeringISaerskattegrunnlagFraGevinstOgTapskontoFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfradragISaerskattegrunnlagFraGevinstOgTapskontoFraVirksomhetPaaSokkel) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletAvskrivningIGrunnrenteinntektForHavbruksvirksomhet) {

                val lineartAaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert.tall()
                    }

                val saldoAaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert.tall()
                    }

                val lineartAaretsAvskrivningNaerstaaende =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        beregnHvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaDelvis) {
                            forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende.tall()
                        }
                    }

                val saldoAaretsAvskrivningNaerstaaende =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        beregnHvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaDelvis) {
                            forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende.tall()
                        }
                    }

                    lineartAaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert +
                        saldoAaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomAvskrivesOrdinaert +
                        lineartAaretsAvskrivningNaerstaaende +
                        saldoAaretsAvskrivningNaerstaaende
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletAvskrivningIGrunnrenteinntektAvAnleggsmiddelErvervetFraNaerstaaendeForHavbruksvirksomhet) {
                val lineartAaretsAvskrivningNaerstaaende =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        beregnHvis(
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaDelvisAnleggsmiddelAnskaffetFraNaerstaaende ||
                                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningAnleggsmiddelAnskaffetFraNaerstaaende
                        ) {
                            forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende.tall()
                        }
                    }

                val saldoAaretsAvskrivningNaerstaaende =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        beregnHvis(
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaDelvisAnleggsmiddelAnskaffetFraNaerstaaende ||
                                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivningAnleggsmiddelAnskaffetFraNaerstaaende
                        ) {
                            forekomstType.aaretsAvskrivningIGrunnrenteinntektPaaAnleggsmiddelSomBenyttesDelvisIHavbruksvirksomhetEllerErAnskaffetFraNaerstaaende.tall()
                        }
                    }
                lineartAaretsAvskrivningNaerstaaende + saldoAaretsAvskrivningNaerstaaende
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletAvskrivningAvSaerskiltAnleggsmiddelFraVannkraftverk) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon.tall()
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektFraGevinstOgTapskontoAvSaerskiltAnleggsmiddelFraVannkraftverk) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                    beregnHvis(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnrenteomraade lik grunnrenteomraade.kode_vannkraft) {
                        forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto.tall()
                    }
                }
            }

            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletInntektsfradragFraGevinstOgTapskontoAvSaerskiltAnleggsmiddelFraVannkraftverk) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                    beregnHvis(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnrenteomraade lik grunnrenteomraade.kode_vannkraft) {
                        forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto.tall()
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletSaldoavskrivning,
            samletInntektsfoeringAvNegativSaldo,
            samletLineaerAvskrivning,
            samletInntektFraGevinstOgTapskonto,
            samletInntektsfradragFraGevinstOgTapskonto,
            samletFriinntektFoer2024,
            samletFriinntekt,
            samletAvskrivningInntektsfoeringOgInntektsfradrag
        )
    }
}