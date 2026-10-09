package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v7_2026.v7
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.InntektOgFradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2025
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2025

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Beregnet+formuesverdi+og+grunnlag+for+beregning+av+særskilt+eiendomsskattegrunnlag
 */
internal object Eiendomsskattegrunnlag : HarKalkylesamling {

    private val indeksRegulerteVerdierForegaaendeInntektsaar =
        kalkyle("indeksRegulerteVerdierForegaaendeInntektsaar") {
            val gjeldendeInntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                forekomsterAv(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagForegaaendeInntektsaar) der {
                    forekomstType.inntektsaar.harVerdi()
                } forHverForekomst {
                    val konsumprisindeks = hentKonsumprisindeksVannkraft(gjeldendeInntektsaar) /
                        hentKonsumprisindeksVannkraft(
                            forekomstType.inntektsaar.tall()
                        )

                    hvis(forekomstType.bruttoSalgsinntekt.harVerdi()) {
                        settFelt(forekomstType.indeksregulertBruttoSalgsinntekt) {
                            forekomstType.bruttoSalgsinntekt * konsumprisindeks
                        }
                    }

                    hvis(forekomstType.bruttoDriftskostnad.harVerdi()) {
                        settFelt(forekomstType.indeksregulertBruttoDriftskostnad) {
                            forekomstType.bruttoDriftskostnad * konsumprisindeks
                        }
                    }

                    hvis(forekomstType.fradragForGrunnrenteskatt.harVerdi()) {
                        settFelt(forekomstType.indeksregulertFradragForGrunnrenteskatt) {
                            forekomstType.fradragForGrunnrenteskatt * konsumprisindeks
                        }
                    }
                }
            }
        }

    internal val salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraftTil2025 =
        kalkyle("salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft") {
            hvis(inntektsaar.tekniskInntektsaar <= 2025) {
                forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt) {
                        forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon * forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_konsesjonsEllerKontraktspris
                    }
                }
            }
        }

    internal val produksjonFraTotalAarsproduksjonRedusertMedKonsesjonskraftFra2026 =
        kalkyle("salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft") {
            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon) {
                        (forekomstType.totalAarsproduksjon -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_produksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumDekningskjoepTilknyttetKonsesjonskraft).somHeltall()
                    }
                }
            }
        }

    internal val spotmarkedsprisFraTotalAarsproduksjonRedusertMedKonsesjonskraftFra2026 =
        kalkyle("salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft") {
            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                    hvis(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon.tall().erTryggAaDelePaa()) {
                        settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_spotmarkedspris) {
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt /
                                forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon
                        }
                    }
                }
            }
        }

    private val bruttoSalgsinntektTil2024 =
        kalkyle("bruttoSalgsinntektOgFradragForKostnader") {
            hvis(inntektsaar.tekniskInntektsaar <= 2024) {
                forekomsterAv(modell2024.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrenseV5()
                } forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_bruttoSalgsinntekt) {
                        (forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonskraft *
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonspris) +
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft
                    }
                }
            }
        }

    private val bruttoSalgsinntekt2025 =
        kalkyle("bruttoSalgsinntektOgFradragForKostnader") {
            hvis(inntektsaar.tekniskInntektsaar == 2025) {
                forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrenseV6()
                } forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_bruttoSalgsinntekt) {
                        (forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonskraft *
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonspris) +
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt
                    }
                }
            }
        }

    internal val bruttoSalgsinntektFra2026 =
        kalkyle("bruttoSalgsinntektOgFradragForKostnader") {
            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrense()
                } forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_bruttoSalgsinntekt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_salgsinntekt +
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_dekningskjoepTilknyttetKonsesjonskraft
                    }
                }
            }
        }


    internal val fradragForKostnader =
        kalkyle("bruttoSalgsinntektOgFradragForKostnader") {
            val inntektsaar = inntektsaar
            val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (tekniskInntektsaar <= 2025) {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForKostnader) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                    }
                } else {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForKostnader) {
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVannkraftverk) der {
                            forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrenteVannkraft(inntektsaar) &&
                                !(forekomstType.type likEnAv listOf(
                                    fradragIGrunnrente.kode_skattemessigAvskrivningAvDriftsmiddelBenyttetIVannkraftproduksjon,
                                    fradragIGrunnrente.kode_investeringskostnad
                                ))
                        } summerVerdiFraHverForekomst {
                            forekomstType.beloep.tall()
                        }
                    }
                }
            }
        }

    val gjennomsnittligIndeksregulertSisteFemAar =
        kalkyle("gjennomsnittligIndeksregulertSisteFemAar") {
            val inntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {

                val aktuelleForekomster =
                    forekomsterAv(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagForegaaendeInntektsaar) der {
                        erEttAvDeForegaaendeFireAar(inntektsaar) &&
                            (forekomstType.indeksregulertBruttoSalgsinntekt.harVerdi() ||
                                forekomstType.indeksregulertBruttoDriftskostnad.harVerdi() ||
                                forekomstType.indeksregulertFradragForGrunnrenteskatt.harVerdi()
                                )
                    }
                val antallAar = antallForekomsterAv(aktuelleForekomster) + 1

                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertBruttoSalgsinntektSisteFemAar) {
                    val sum = aktuelleForekomster summerVerdiFraHverForekomst {
                        forekomstType.indeksregulertBruttoSalgsinntekt.tall()
                    }
                    (sum + forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_bruttoSalgsinntekt) / antallAar
                }

                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertFradragForKostnaderSisteFemAar) {
                    val sum = aktuelleForekomster summerVerdiFraHverForekomst {
                        forekomstType.indeksregulertBruttoDriftskostnad.tall()
                    }
                    (sum + forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForKostnader) / antallAar
                }

                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertFradragForGrunnrenteskattSisteFemAar) {
                    val sum = aktuelleForekomster summerVerdiFraHverForekomst {
                        forekomstType.indeksregulertFradragForGrunnrenteskatt.tall()
                    }
                    (sum + forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForGrunnrenteskatt) / antallAar
                }
            }
        }

    private val kontantstroemForDriften =
        kalkyle("kontantstroemForDriften") {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_kontantstroemForDriften) {
                    forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertBruttoSalgsinntektSisteFemAar -
                        forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertFradragForKostnaderSisteFemAar -
                        forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_gjennomsnittligIndeksregulertFradragForGrunnrenteskattSisteFemAar
                }
            }
        }

    private val naaverdiPaaKontantstroemOverUendeligLevetid =
        kalkyle("naaverdiPaaKontantstroemOverUendeligLevetid") {
            val satser = satser!!
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_naaverdiPaaKontantstroemOverUendeligLevetid) {
                    forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_kontantstroemForDriften.div(
                        satser.sats(Sats.vannkraft_kapitaliseringsrente)
                    )
                }
            }
        }

    val fradragForFremtidigeUtskiftningskostnader =
        kalkyle("fradragForFremtidigeUtskiftningskostnader") {
            fun summerNaaverdiAvFremtidigeUtskiftningskostnaderSaerskiltAnleggsmiddel(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.naaverdiAvFremtidigeUtskiftningskostnader.tall()
                    }
                }
            }
            fun summerNaaverdiAvFremtidigeUtskiftningskostnaderLineaertAvskrevetAnleggsmiddel(loepenummer: String?): BigDecimal? {
                if (inntektsaar.tekniskInntektsaar >= 2024) {
                    return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk.tall()
                    }
                } else {
                    return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk.tall()
                    }
                }
            }
            fun summerNaaverdiAvFremtidigeUtskiftningskostnaderSaldoavskrevetAnleggsmiddel(loepenummer: String?): BigDecimal? {
                if (inntektsaar.tekniskInntektsaar >= 2024) {
                    return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk.tall()
                    }
                } else {
                    return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk.tall()
                    }
                }
            }
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_fradragForFremtidigeUtskiftningskostnader) {
                    summerNaaverdiAvFremtidigeUtskiftningskostnaderSaerskiltAnleggsmiddel(forekomstType.loepenummer.verdi()) +
                        summerNaaverdiAvFremtidigeUtskiftningskostnaderLineaertAvskrevetAnleggsmiddel(forekomstType.loepenummer.verdi()) +
                        summerNaaverdiAvFremtidigeUtskiftningskostnaderSaldoavskrevetAnleggsmiddel(forekomstType.loepenummer.verdi())
                }
            }
        }

    private val formuesverdi =
        kalkyle("formuesverdi") {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_formuesverdi) {
                    (forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_naaverdiPaaKontantstroemOverUendeligLevetid -
                        forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_fradragForFremtidigeUtskiftningskostnader) medMinimumsverdi 0
                }
            }
        }

    private val minimumsOgMaksimumsverdiForEiendomsskattegrunnlag =
        kalkyle("minimumsOgMaksimumsverdiForEiendomsskattegrunnlag") {
            val satser = satser!!
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                val antallAar = antallForekomsterAv(forekomstType.grunnlagForBeregningAvNaturressursskatt_grunnlagForNaturressursskattPerInntektsaar) medMaksimumsverdi 7
                hvis (antallAar stoerreEnn 0) {
                    settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_minimumsverdiForEiendomsskattegrunnlag) {
                        forekomstType.grunnlagForBeregningAvNaturressursskatt_samletAarsproduksjon.div(antallAar).times(
                            satser.sats(Sats.vannkraft_satsForMinimumsverdiEiendomsskattegrunnlag)
                        )
                    }
                    settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_maksimumsverdiForEiendomsskattegrunnlag) {
                        forekomstType.grunnlagForBeregningAvNaturressursskatt_samletAarsproduksjon.div(antallAar).times(
                            satser.sats(Sats.vannkraft_satsForMaksimumsverdiEiendomsskattegrunnlag)
                        )
                    }
                }
            }
        }

    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagForegaaendeInntektsaarForekomst>.erEttAvDeForegaaendeFireAar(
        gjeldendeInntektsaar: BigDecimal?
    ) =
        (forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 1 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 2 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 3 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 4)

    private val eiendomsskattegrunnlag = kalkyle("eiendomsskattegrunnlag") {
        val inntektsaar = inntektsaar

        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
            samletPaastempletMerkeytelseIKvaOverGrense() && forekomstType.aarForDriftssettelse.mindreEllerLik(inntektsaar.gjeldendeInntektsaar)
        } forHverForekomst {
            settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_eiendomsskattegrunnlag) {
                forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_formuesverdi.tall()
                    .medMinimumsverdi(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_minimumsverdiForEiendomsskattegrunnlag.tall())
                    .medMaksimumsverdi(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_maksimumsverdiForEiendomsskattegrunnlag.tall())
            }
        }

        fun summerUtgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk(loepenummer: String?): BigDecimal? {
            return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk.tall()
            }
        }

        fun summerUtgaaendeVerdiSaldoavskrevetAnleggsmiddel(loepenummer: String?): BigDecimal? {
            if (inntektsaar.tekniskInntektsaar >= 2024) {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                            (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_utgaaendeVerdiInngaarIEiendomsskattegrunnlaget  ||
                                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning) &&
                        forekomstType.erDetFysiskAnleggsmiddelIUtgaaendeVerdi.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            } else {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.erDetFysiskAnleggsmiddelIUtgaaendeVerdi.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }

            }
        }

        fun summerUtgaaendeVerdiLineaertavskrevetAnleggsmiddel(loepenummer: String?): BigDecimal? {
            if (inntektsaar.tekniskInntektsaar >= 2024) {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                            (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_utgaaendeVerdiInngaarIEiendomsskattegrunnlaget ||
                             forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning) &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            } else {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            }
        }

        fun summerUtgaaendeVerdiIkkeAvskrivbartAnleggsmiddel(loepenummer: String?): BigDecimal? {
            if (inntektsaar.tekniskInntektsaar >= 2024) {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                            (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_utgaaendeVerdiInngaarIEiendomsskattegrunnlaget ||
                             forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning) &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            } else {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            }
        }

        fun summerUtgaaendeVerdiAnleggsmiddelUnderUtfoerelse(loepenummer: String?): BigDecimal? {
            return when (inntektsaar.tekniskInntektsaar) {
                2025 ->
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                        forekomstType.vannkraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.anskaffelseskost.tall()
                    }
                2024 ->
                    forekomsterAv(modell2024.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                        forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.anskaffelseskost.tall()
                    }
                else ->
                    forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) der {
                        forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                    } summerVerdiFraHverForekomst {
                        forekomstType.anskaffelseskost.tall()
                    }
            }
        }

        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
            samletPaastempletMerkeytelseIKvaUnderGrense() ||
                    (samletPaastempletMerkeytelseIKvaOverGrense() && forekomstType.aarForDriftssettelse.stoerreEnn(inntektsaar.gjeldendeInntektsaar))
        } forHverForekomst {
            settFelt(forekomstType.beregnetFormuesverdiOgGrunnlagForBeregningAvSaerskiltEiendomsskattegrunnlag_eiendomsskattegrunnlag) {
                summerUtgaaendeVerdiSaldoavskrevetAnleggsmiddel(forekomstType.loepenummer.verdi()) +
                    summerUtgaaendeVerdiIkkeAvskrivbartAnleggsmiddel(forekomstType.loepenummer.verdi()) +
                    summerUtgaaendeVerdiLineaertavskrevetAnleggsmiddel(forekomstType.loepenummer.verdi()) +
                    summerUtgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk(forekomstType.loepenummer.verdi()) +
                    summerUtgaaendeVerdiAnleggsmiddelUnderUtfoerelse(forekomstType.loepenummer.verdi())
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            indeksRegulerteVerdierForegaaendeInntektsaar,
            salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraftTil2025,
            produksjonFraTotalAarsproduksjonRedusertMedKonsesjonskraftFra2026,
            spotmarkedsprisFraTotalAarsproduksjonRedusertMedKonsesjonskraftFra2026,
            bruttoSalgsinntektTil2024,
            bruttoSalgsinntekt2025,
            bruttoSalgsinntektFra2026,
            fradragForKostnader,
            gjennomsnittligIndeksregulertSisteFemAar,
            kontantstroemForDriften,
            naaverdiPaaKontantstroemOverUendeligLevetid,
            fradragForFremtidigeUtskiftningskostnader,
            formuesverdi,
            minimumsOgMaksimumsverdiForEiendomsskattegrunnlag,
            eiendomsskattegrunnlag
        )
    }
}
