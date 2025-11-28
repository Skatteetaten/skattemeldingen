package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v6_2025.v6
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Beregnet+formuesverdi+og+grunnlag+for+beregning+av+særskilt+eiendomsskattegrunnlag
 */
internal object Eiendomsskattegrunnlag : HarKalkylesamling {

    private val indeksRegulerteVerdierForegaaendeInntektsaar =
        kalkyle("indeksRegulerteVerdierForegaaendeInntektsaar") {
            val gjeldendeInntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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

    internal val salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft =
        kalkyle("salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft") {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {

                val sumKraftTattUtIhtKonsesjonProduksjon =
                    forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon.tall()
                    }

                settFelt(forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon) {
                    forekomstType.totalAarsproduksjon - sumKraftTattUtIhtKonsesjonProduksjon
                }


                settFelt(forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt) {
                    forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_produksjon * forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_konsesjonsEllerKontraktspris
                }
            }
        }

    private val bruttoSalgsinntektOgFradragForKostnaderTil2024 =
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

                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForKostnader) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                    }
                }
            }
        }

    private val bruttoSalgsinntektOgFradragForKostnaderFra2025 =
        kalkyle("bruttoSalgsinntektOgFradragForKostnader") {
            hvis(inntektsaar.tekniskInntektsaar >= 2025) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrenseV6()
                } forHverForekomst {
                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_bruttoSalgsinntekt) {
                        (forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonskraft *
                            forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_konsesjonspris) +
                            forekomstType.salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft_salgsinntekt
                    }

                    settFelt(forekomstType.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagIInntektsaaret_fradragForKostnader) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                    }
                }
            }
        }

    val gjennomsnittligIndeksregulertSisteFemAar =
        kalkyle("gjennomsnittligIndeksregulertSisteFemAar") {
            val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
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

    private fun ForekomstKontekst<v6.kraftverk_spesifikasjonAvKraftverkForekomst.grunnlagForBeregningAvFormuesverdiOgSaerskiltEiendomsskattegrunnlagForegaaendeInntektsaarForekomst>.erEttAvDeForegaaendeFireAar(
        gjeldendeInntektsaar: BigDecimal?
    ) =
        (forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 1 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 2 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 3 || forekomstType.inntektsaar.tall() == gjeldendeInntektsaar - 4)

    private val eiendomsskattegrunnlag = kalkyle("eiendomsskattegrunnlag") {
        val inntektsaar = inntektsaar

        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
            samletPaastempletMerkeytelseIKvaOverGrenseV6() && forekomstType.aarForDriftssettelse.mindreEllerLik(inntektsaar.gjeldendeInntektsaar)
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
                             forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning) &&
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
            if (inntektsaar.tekniskInntektsaar >= 2024) {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.anskaffelseskost.tall()
                }
            } else {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.anskaffelseskost.tall()
                }
            }
        }

        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
            samletPaastempletMerkeytelseIKvaUnderGrense() ||
                    (samletPaastempletMerkeytelseIKvaOverGrenseV6() && forekomstType.aarForDriftssettelse.stoerreEnn(inntektsaar.gjeldendeInntektsaar))
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
            bruttoSalgsinntektOgFradragForKostnaderTil2024,
            bruttoSalgsinntektOgFradragForKostnaderFra2025,
            gjennomsnittligIndeksregulertSisteFemAar,
            kontantstroemForDriften,
            naaverdiPaaKontantstroemOverUendeligLevetid,
            fradragForFremtidigeUtskiftningskostnader,
            formuesverdi,
            minimumsOgMaksimumsverdiForEiendomsskattegrunnlag,
            eiendomsskattegrunnlag,
            salgsinntektFraTotalAarsproduksjonRedusertMedKonsesjonskraft
        )
    }
}
