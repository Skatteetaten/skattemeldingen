package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v4_2023.v4
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Beregnet+formuesverdi+og+grunnlag+for+beregning+av+særskilt+eiendomsskattegrunnlag
 */
internal object SpesifikasjonAvGrunnrenteinntekt : HarKalkylesamling {
    fun ForekomstKontekst<v4.kraftverk_spesifikasjonAvKraftverkForekomst>.samletPaastempletMerkeytelseIKvaOverGrense() =
        forekomstType.samletPaastempletMerkeytelseIKva stoerreEllerLik grenseverdiKva

    private val salgsinntekt =
        kalkyle("salgsinntekt") {
            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
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
            fun gjenstaaendeVerdiPaaGevinstOgTapskontoForGrunnrenteinntekt(
                realisasjonsAar: BigDecimal?,
                vederlagVedRealisasjon: BigDecimal?
            ): BigDecimal? {
                val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
                return (inntektsaar - realisasjonsAar)?.let {
                    BigDecimal.valueOf(0.8).pow(it.intValueExact())
                } * vederlagVedRealisasjon
            }

            fun summerInntektFraGevinstOgTapskonto(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann())
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektFraGevinstOgTapskonto.tall()
                    else if (forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() && forekomstType.vederlagVedRealisasjon.erPositiv())
                        gjenstaaendeVerdiPaaGevinstOgTapskontoForGrunnrenteinntekt(
                            forekomstType.realisasjonsdato.aar(), forekomstType.vederlagVedRealisasjon.tall()
                        ) * 0.2
                    else null
                }
            }

            fun summerInntektsfradragFraGevinstOgTapskonto(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann())
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektsfradragFraGevinstOgTapskonto.tall()
                    else if (forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() && forekomstType.vederlagVedRealisasjon.erNegativ())
                        gjenstaaendeVerdiPaaGevinstOgTapskontoForGrunnrenteinntekt(
                            forekomstType.realisasjonsdato.aar(), forekomstType.vederlagVedRealisasjon.tall()
                        ) * 0.2
                    else null
                }
            }

            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon) {
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
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer &&
                        forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
                } summerVerdiFraHverForekomst {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.aaretsAvskrivning.tall()
                    }
                }
            }

            fun summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    val aaretsAvskrivning = forekomstType.aaretsAvskrivning.tall()
                    val delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt = forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
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
                        forekomstType.vederlagVedRealisasjonOgUttak.harIkkeVerdi() && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
                    ) {
                        when {
                            forekomstType.saldogruppe ulik saldogruppe.kode_i &&
                                grunnlagForAvskrivningOgInntektsfoering - delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt stoerreEnn 0 -> {
                                aaretsAvskrivning - (delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt * forekomstType.avskrivningssats.prosent())
                            }
                            forekomstType.saldogruppe lik saldogruppe.kode_i -> {
                                if(nedreGrenseForAvskrivning.harIkkeVerdi() || nedreGrenseForAvskrivning mindreEnn utgaaendeVerdiMellomverdi)
                                    aaretsAvskrivning - (delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt * forekomstType.avskrivningssats.prosent())
                                else
                                    aaretsAvskrivning - delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt - nedreGrenseForAvskrivning

                            }
                        }
                    }

                    aaretsAvskrivning
                }
            }

            fun summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            fun summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
                }
            }

            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon) {
                    summerSaerskiltAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                        summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                        summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(forekomstType.loepenummer.verdi()) -
                        summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(forekomstType.loepenummer.verdi())
                }
            }
        }

    val investeringskostnadKnyttetTilKraftproduksjon =
        kalkyle("investeringskostnadKnyttetTilKraftproduksjon") {
            val gjeldendeInntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
            fun investeringskostnadKnyttetTilSaerskilteAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
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
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() &&
                        forekomstType.nyanskaffelse.harVerdi() &&
                        forekomstType.ervervsdato.harVerdi()
                    ) {
                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.nyanskaffelse.tall() - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        } else {
                            forekomstType.nyanskaffelse + forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetTilLineaertavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() &&
                        forekomstType.anskaffelseskost.harVerdi() && forekomstType.ervervsdato.harVerdi()
                    ) {
                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.anskaffelseskost.tall() - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        } else {
                            forekomstType.paakostning.tall() - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetTilIkkeAvskrivbarAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    if (
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() &&
                        forekomstType.nyanskaffelse.harVerdi() && forekomstType.ervervsdato.harVerdi()
                    ) {
                        if (forekomstType.ervervsdato.aar() == gjeldendeInntektsaar) {
                            forekomstType.nyanskaffelse.tall() - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        } else {
                            forekomstType.nyanskaffelse + forekomstType.paakostning - forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_delAvAaretsInvesteringskostnadSomErDirekteUtgiftsfoertIGrunnrenteinntekt
                        }
                    } else
                        null
                }
            }

            fun investeringskostnadKnyttetAnleggsmiddelUnderUtfoerelse(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntektIInntektsaaret.tall()
                }
            }

            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
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
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
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
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() &&
                        forekomstType.aaretsAvskrivning.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            fun aaretsAvskrivningLineaertavskrevetAnleggsmidler(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erSann() &&
                        forekomstType.aaretsAvskrivning.harVerdi() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            }

            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelKnyttetTilVannkraftverkSomErDirekteUtgiftsfoert) {
                    (aaretsAvkastningSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        aaretsAvskrivningSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                        aaretsAvskrivningLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi())).somHeltall()
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt) {
                    (forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt +
                        summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
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

    private fun ForekomstKontekst<v4.kraftverk_spesifikasjonAvKraftverkForekomst>.summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() =
        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
            forekomstType.salgsinntekt.harVerdi()
        } summerVerdiFraHverForekomst {
            forekomstType.salgsinntekt.tall()
        }

    private val samletBruttoInntektOgFradragIGrunnrenteinntekt =
        kalkyle("samletBruttoInntektIGrunnrenteinntekt") {
            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoInntektIGrunnrenteinntekt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt +
                        summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt() +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                        forekomstType.spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
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
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerIkkeAvskrivbartAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerSaerskiltAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeFriinntekt.tall()
                }
            }

            fun summerLineaertavskrevetAnleggsmiddelAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann() &&
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt.tall()
                }
            }

            fun summerAnleggsmiddelIKraftverkUnderUtfoerelseAaretsFriinntekt(loepenummer: String?): BigDecimal? {
                return forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) der {
                    forekomstType.kraftverketsLoepenummer.verdi() == loepenummer
                } summerVerdiFraHverForekomst {
                    forekomstType.aaretsFriinntekt.tall()
                }
            }

            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
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
            forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
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
        forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val normRente = satser.sats(Sats.vannkraft_normrenteForFremfoerbarNegativGrunnrenteinntektFraFoer2007)
            settFelt(modell2023.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_renterKnyttetTilFremfoerbarNegativGrunnrenteinntektFraFoer2007) {
                forekomstType.spesifikasjonAvGrunnrenteinntekt_fremfoerbarNegativGrunnrenteinntektFraFoer2007 * normRente
            }
        }
    }

    internal val positivGrunnrenteinntektEllerRestAvFremfoerbarNegativGrunnrenteinntektFraFoer2007 = kalkyle {
        forekomsterAv(modell2023.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
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
            positivGrunnrenteinntektEllerRestAvFremfoerbarNegativGrunnrenteinntektFraFoer2007
        )
    }
}
