package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v6_2025.v6
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.kontraktstypeForKraftLevertAvKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2024

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Beregnet+formuesverdi+og+grunnlag+for+beregning+av+særskilt+eiendomsskattegrunnlag
 */
internal object SpesifikasjonAvGrunnrenteinntektFra2024 : HarKalkylesamling {
    private val salgsinntekt =
        kalkyle("salgsinntekt") {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon.harVerdi() &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris.harVerdi()
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon *
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.harVerdi() &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris.harVerdi()
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon *
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon.harVerdi() &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_konsesjonsEllerKontraktspris.harVerdi()
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon *
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_konsesjonsEllerKontraktspris
                    }
                }

                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.produksjon.harVerdi() && forekomstType.konsesjonsEllerKontraktspris.harVerdi()
                } forHverForekomst {
                    settFelt(forekomstType.salgsinntekt) {
                        forekomstType.produksjon * forekomstType.konsesjonsEllerKontraktspris
                    }
                }
            }
        }

    internal val gevinstOgTapVedRealisasjonAvAnleggsmiddelSomBenyttesIKraftproduksjon =
        kalkyle("gevinstOgTapVedRealisasjonAvAnleggsmiddelSomBenyttesIKraftproduksjon") {

            fun summerInntektFraGevinstOgTapskonto(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer lik loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto.tall()
                }
            }

            fun summerInntektsfradragFraGevinstOgTapskonto(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer lik loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto.tall()
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon) {
                    summerInntektFraGevinstOgTapskonto(forekomstType.loepenummer.verdi())
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon) {
                    summerInntektsfradragFraGevinstOgTapskonto(forekomstType.loepenummer.verdi()).absoluttverdi()
                }
            }
        }

    private val skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon =
        kalkyle("skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon") {
            fun summerSaerskiltAnleggsmiddelAaretsAvskrivning(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer &&
                        forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
                } summerVerdiFraHverForekomst {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.aaretsAvskrivning.tall()
                    }
                }
            }

            fun summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(loepenummer: String?): BigDecimal? {
                val inntektsaar = inntektsaar
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    val aaretsAvskrivning = forekomstType.aaretsAvskrivning.tall()

                    val delAvAaretsInvesteringskostnad = if(inntektsaar.tekniskInntektsaar >= 2025) {
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar
                    } else {
                        felt2024.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                    }

                    val grunnlagForAvskrivningOgInntektsfoering = forekomstType.grunnlagForAvskrivningOgInntektsfoering
                    val nedreGrenseForAvskrivning = forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning

                    val aaretsAvskrivningMellomverdi = beregnHvis(
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEnn forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                    ) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                            forekomstType.avskrivningssats.prosent()
                    }

                    val utgaaendeVerdiMellomverdi = forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                        aaretsAvskrivningMellomverdi

                    if(
                        forekomstType.vederlagVedRealisasjonOgUttak.harIkkeVerdi() && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
                    ) {
                        when {
                            forekomstType.saldogruppe ulik saldogruppe.kode_i &&
                                grunnlagForAvskrivningOgInntektsfoering - delAvAaretsInvesteringskostnad stoerreEnn 0 -> {
                                aaretsAvskrivning - (delAvAaretsInvesteringskostnad * forekomstType.avskrivningssats.prosent())
                            }
                            forekomstType.saldogruppe lik saldogruppe.kode_i -> {
                                if(nedreGrenseForAvskrivning.harIkkeVerdi() || nedreGrenseForAvskrivning mindreEnn utgaaendeVerdiMellomverdi)
                                    aaretsAvskrivning - (delAvAaretsInvesteringskostnad * forekomstType.avskrivningssats.prosent())
                                else
                                    aaretsAvskrivning - delAvAaretsInvesteringskostnad - nedreGrenseForAvskrivning

                            }
                        }
                    }

                    aaretsAvskrivning
                }
            }

            fun summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            fun summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet likEnAv listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning,
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                    ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
                }
            }

            fun summerSaldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet likEnAv listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning,
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                    ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert.tall()
                }
            }

            fun summerLineaertAvskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet likEnAv listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedAvskrivning,
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                    ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert.tall()
                }
            }

            fun summerIkkeAvskrivbartAnleggsmiddelAaretsInntektsfoeringAvGevinst(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet likEnAv listOf(
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaUtenDirekteFradragOgAvskrivning
                    ) && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsInntektsfoeringAvGevinstVedRealisasjonOgUttakAvAnleggsmiddelSomErDirekteUtgiftsfoert.tall()
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon) {
                    summerSaerskiltAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                        summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                        summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(forekomstType.loepenummer.verdi()) -
                        summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(forekomstType.loepenummer.verdi()) -
                        summerSaldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                        summerLineaertAvskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                        summerIkkeAvskrivbartAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi())
                }
            }
        }

    val investeringskostnadKnyttetTilKraftproduksjon =
        kalkyle("investeringskostnadKnyttetTilKraftproduksjon") {
            val gjeldendeInntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
            val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
            fun investeringskostnadKnyttetTilSaerskilteAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer &&
                        forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann()
                } summerVerdiFraHverForekomst {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) der {
                        forekomstType.anskaffelsesEllerPaakostningsdato.aar() == gjeldendeInntektsaar
                    } summerVerdiFraHverForekomst {
                        forekomstType.historiskKostpris -
                                forekomstType.delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                    }
                }
            }

            fun investeringskostnadKnyttetTilSaldoavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if ((forekomstType.nyanskaffelse.harVerdi() || forekomstType.paakostning.harVerdi()) &&
                        forekomstType.ervervsdato.harVerdi()
                    ) {
                        val delAvAaretsInvesteringskostnad = if(tekniskInntektsaar >= 2025) {
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar
                        } else {
                            felt2024.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }
                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.nyanskaffelse.tall() - delAvAaretsInvesteringskostnad
                        } else {
                            forekomstType.nyanskaffelse + forekomstType.paakostning - delAvAaretsInvesteringskostnad
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetTilLineaertavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (
                        (forekomstType.anskaffelseskost.harVerdi() || forekomstType.paakostning.harVerdi()) && forekomstType.ervervsdato.harVerdi()
                    ) {
                        val delAvAaretsInvesteringskostnad = if(tekniskInntektsaar >= 2025) {
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar
                        } else {
                            felt2024.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }
                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.anskaffelseskost.tall() - delAvAaretsInvesteringskostnad
                        } else {
                            forekomstType.paakostning.tall() - delAvAaretsInvesteringskostnad
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetTilIkkeAvskrivbarAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (
                        (forekomstType.nyanskaffelse.harVerdi() || forekomstType.paakostning.harVerdi()) && forekomstType.ervervsdato.harVerdi()
                    ) {
                        val delAvAaretsInvesteringskostnad = if(tekniskInntektsaar >= 2025) {
                            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntektTidligereInntektsaar
                        } else {
                            felt2024.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }

                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.nyanskaffelse.tall() - delAvAaretsInvesteringskostnad
                        } else {
                            forekomstType.nyanskaffelse + forekomstType.paakostning - delAvAaretsInvesteringskostnad
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetAnleggsmiddelUnderUtfoerelse(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                    val kraftverketsLoepenummer = if(tekniskInntektsaar >= 2025) {
                        forekomstType.vannkraftverketsLoepenummer
                    } else {
                        felt2024.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert.kraftverketsLoepenummer
                    }
                    kraftverketsLoepenummer lik loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntektIInntektsaaret.tall()
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilKraftproduksjon) {
                    investeringskostnadKnyttetTilSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        investeringskostnadKnyttetTilSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        investeringskostnadKnyttetTilLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        investeringskostnadKnyttetTilIkkeAvskrivbarAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        investeringskostnadKnyttetAnleggsmiddelUnderUtfoerelse(forekomstType.loepenummer.verdi())
                }
            }

        }

    val aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoertgrunnlag_GrunnlagForBeregningAvSelskapsskatt =
        kalkyle("aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoertgrunnlag_GrunnlagForBeregningAvSelskapsskatt") {

            val satser = satser!!
            fun aaretsAvkastningSaerskilteAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    val aaretsAvskrivning =
                        if (forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()) {
                            forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                                forekomstType.aaretsAvskrivning.tall()
                            }
                        } else null
                    forekomstType.aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk.tall() -
                        aaretsAvskrivning

                }
            }

            fun aaretsAvskrivningSaldoavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.aaretsAvskrivning.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            fun aaretsAvskrivningLineaertavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet lik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag &&
                        forekomstType.aaretsAvskrivning.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelKnyttetTilVannkraftverkSomErDirekteUtgiftsfoert) {
                    (aaretsAvkastningSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        aaretsAvskrivningSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        aaretsAvskrivningLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi())).somHeltall()
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt) {
                    (forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt -
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_dekningskjoep +
                        summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() -
                        summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt() +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_driftsstoetteTilProduksjonAvNyVannkraft +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraUtstedtElsertifikat +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_opprinnelsesgaranti +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektVedAvslutningEllerEndringAvFastpriskontrakt -
                        (forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelKnyttetTilVannkraftverkSomErDirekteUtgiftsfoert +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                            )).somHeltall()
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt
                        .stoerreEllerLik(0)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        (forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                            satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt
                        .mindreEnn(0)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        (forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                            satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall().absoluttverdi()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        stoerreEnn 0 &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        stoerreEllerLik
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                    }

                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        stoerreEnn 0 &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        mindreEnn
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet
                }
            }
        }

    private fun ForekomstKontekst<v6.kraftverk_spesifikasjonAvKraftverkForekomst>.summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() =
        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
            forekomstType.salgsinntekt.harVerdi()
        } summerVerdiFraHverForekomst {
            forekomstType.salgsinntekt.tall()
        }

    private fun ForekomstKontekst<v6.kraftverk_spesifikasjonAvKraftverkForekomst>.summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt() =
        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
            forekomstType.dekningskjoep.harVerdi()
        } summerVerdiFraHverForekomst {
            forekomstType.dekningskjoep.tall()
        }

    private val samletBruttoInntektOgFradragIGrunnrenteinntekt =
        kalkyle("samletBruttoInntektIGrunnrenteinntekt") {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoInntektIGrunnrenteinntekt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_dekningskjoep +
                        summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() -
                        summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt() +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_driftsstoetteTilProduksjonAvNyVannkraft +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraUtstedtElsertifikat +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_opprinnelsesgaranti +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektVedAvslutningEllerEndringAvFastpriskontrakt
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoFradragIGrunnrenteinntekt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilKraftproduksjon +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet -
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                }
            }
        }

    private val friinntekt =
        kalkyle("friinntekt") {
            fun summerSaldoavskrevetAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerIkkeAvskrivbartAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerSaerskiltAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeFriinntekt.tall()
                }
            }

            fun summerLineaertavskrevetAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerAnleggsmiddelIKraftverkUnderUtfoerelseAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                val inntektsaar = inntektsaar
                return forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) der {
                    val loepenummerForekomst = if(inntektsaar.tekniskInntektsaar >= 2025) {
                        forekomstType.vannkraftverketsLoepenummer
                    } else {
                        felt2024.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert.kraftverketsLoepenummer
                    }
                    loepenummerForekomst lik loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsFriinntektForAnleggsmiddelIVannkraftOmfattetAvGrunnrenteskatt.tall()
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_friinntekt) {
                    summerSaerskiltAnleggsmiddelAaretsFriinntekt(forekomstType.loepenummer.verdi()) +
                        summerSaldoavskrevetAnleggsmiddelAaretsFriinntekt(forekomstType.loepenummer.verdi()) +
                        summerIkkeAvskrivbartAnleggsmiddelAaretsFriinntekt(forekomstType.loepenummer.verdi()) +
                        summerLineaertavskrevetAnleggsmiddelAaretsFriinntekt(forekomstType.loepenummer.verdi()) +
                        summerAnleggsmiddelIKraftverkUnderUtfoerelseAaretsFriinntekt(forekomstType.loepenummer.verdi())
                }

            }
        }

    internal val positivGrunnrenteinntektFoerFradragForNegativGrunnrenteinntektFraTidligereAarEllerNegativGrunnrenteinntektForInntektsaaret =
        kalkyle {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                val sum =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoInntektIGrunnrenteinntekt -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoFradragIGrunnrenteinntekt -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_friinntekt

            hvis(sum stoerreEllerLik 0) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_positivGrunnrenteinntektFoerFradragForNegativGrunnrenteinntektFraTidligereAar) {
                    sum
                }
            }

            hvis(sum mindreEnn 0) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_negativGrunnrenteinntektForInntektsaaret) { sum.absoluttverdi() }
            }
        }
    }

    internal val renterKnyttetTilFremfoerbarNegativGrunnrenteinntektFraFoer2007 = kalkyle {
        val satser = satser!!
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val normRente = satser.sats(Sats.vannkraft_normrenteForFremfoerbarNegativGrunnrenteinntektFraFoer2007)
            settFelt(modell.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_renterKnyttetTilFremfoerbarNegativGrunnrenteinntektFraFoer2007) {
                forekomstType.spesifikasjonAvGrunnrenteinntekt_fremfoerbarNegativGrunnrenteinntektFraFoer2007 * normRente
            }
        }
    }

    internal val positivGrunnrenteinntektEllerRestAvFremfoerbarNegativGrunnrenteinntektFraFoer2007 = kalkyle {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val sum =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_positivGrunnrenteinntektFoerFradragForNegativGrunnrenteinntektFraTidligereAar -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_fremfoerbarNegativGrunnrenteinntektFraFoer2007 -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_renterKnyttetTilFremfoerbarNegativGrunnrenteinntektFraFoer2007 +
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_avgittFremfoerbarNegativGrunnrenteinntektTilOpprustningsOgUtvidelsesprosjekt -
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_mottattFremfoerbarNegativGrunnrenteinntektTilOpprustningsOgUtvidelsesprosjekt

            hvis(sum stoerreEllerLik 0) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_positivGrunnrenteinntekt) { sum }
            }

            hvis(sum mindreEnn 0) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_restAvFremfoerbarNegativGrunnrenteinntektFraFoer2007) { sum.absoluttverdi() }
            }
        }
    }

    internal val kontraktstypeLeieavtale = kalkyle("kontraktstypeLeieavtale") {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeLeieavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.produksjon.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeLeieavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.salgsinntekt.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeLeieavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.volumDekningskjoep.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeLeieavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.dekningskjoep.tall()
                }
            }
        }
    }

    internal val kontraktstypeKjoepekontrakt = kalkyle("kontraktstypeKjoepekontrakt") {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeKjoepekontrakt) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                } summerVerdiFraHverForekomst {
                    forekomstType.produksjon.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeKjoepekontrakt) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                } summerVerdiFraHverForekomst {
                    forekomstType.salgsinntekt.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeKjoepekontrakt) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                } summerVerdiFraHverForekomst {
                    forekomstType.volumDekningskjoep.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeKjoepekontrakt) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                } summerVerdiFraHverForekomst {
                    forekomstType.dekningskjoep.tall()
                }
            }
        }
    }

    internal val kontraktstypeFastprisavtale = kalkyle("kontraktstypeFastprisavtale") {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeFastprisavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.produksjon.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeFastprisavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.salgsinntekt.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeFastprisavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.volumDekningskjoep.tall()
                }
            }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeFastprisavtale) {
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                    forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                } summerVerdiFraHverForekomst {
                    forekomstType.dekningskjoep.tall()
                }
            }
        }
    }

    internal val samletVolumForOevrigKraftsalg = kalkyle("samletVolumForOevrigKraftsalg") {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val kraftTattUtIhtKonsesjonProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon.tall()
                }
            val kraftForbruktIEgenProduksjonsvirksomhetProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.tall()
                }

            val oevrigAarsproduksjonProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon.tall()
                }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForOevrigKraftsalg) {
                kraftTattUtIhtKonsesjonProduksjon + kraftForbruktIEgenProduksjonsvirksomhetProduksjon + oevrigAarsproduksjonProduksjon
            }
        }
    }

    internal val samletSalgsinntektForOevrigKraftsalg = kalkyle("samletSalgsinntektForOevrigKraftsalg") {
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val kraftTattUtIhtKonsesjonSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt.tall()
                }
            val kraftForbruktIEgenProduksjonsvirksomhetSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt.tall()
                }

            val oevrigAarsproduksjonSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt.tall()
                }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForOevrigKraftsalg) {
                kraftTattUtIhtKonsesjonSalgsinntekt + kraftForbruktIEgenProduksjonsvirksomhetSalgsinntekt + oevrigAarsproduksjonSalgsinntekt
            }
        }
    }


    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            salgsinntekt,
            gevinstOgTapVedRealisasjonAvAnleggsmiddelSomBenyttesIKraftproduksjon,
            skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon,
            investeringskostnadKnyttetTilKraftproduksjon,
            aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoertgrunnlag_GrunnlagForBeregningAvSelskapsskatt,
            samletBruttoInntektOgFradragIGrunnrenteinntekt,
            friinntekt,
            positivGrunnrenteinntektFoerFradragForNegativGrunnrenteinntektFraTidligereAarEllerNegativGrunnrenteinntektForInntektsaaret,
            renterKnyttetTilFremfoerbarNegativGrunnrenteinntektFraFoer2007,
            positivGrunnrenteinntektEllerRestAvFremfoerbarNegativGrunnrenteinntektFraFoer2007,
            kontraktstypeLeieavtale,
            kontraktstypeKjoepekontrakt,
            kontraktstypeFastprisavtale,
            samletVolumForOevrigKraftsalg,
            samletSalgsinntektForOevrigKraftsalg
        )
    }
}
