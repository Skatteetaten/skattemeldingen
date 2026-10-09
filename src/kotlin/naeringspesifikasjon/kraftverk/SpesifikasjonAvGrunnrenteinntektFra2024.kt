package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v7_2026.v7
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.GrunnlagIBeregningAvSelskapsskatt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.GrunnlagIBeregningAvSelskapsskatt.erFradrag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.GrunnlagIBeregningAvSelskapsskatt.erTillegg
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.InntektOgFradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.grunnlagIBeregningAvSelskapsskatt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.inntektIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.kontraktstypeForKraftLevertAvKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2025
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2025

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Beregnet+formuesverdi+og+grunnlag+for+beregning+av+særskilt+eiendomsskattegrunnlag
 */
internal object SpesifikasjonAvGrunnrenteinntektFra2024 : HarKalkylesamling {

    /**
     * Oppretter en beregnet type/beloep-forekomst under spesifikasjonAvInntektIBruttoGrunnrenteinntektIVannkraftverk
     * for inntektsaar >= 2026. Speiler mønsteret fra KontraktForVannkraftverk/vindkraft (opprettNySubforekomstAv +
     * medId + medFelt).
     */
    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.opprettNyForekomstInntektIBruttoGrunnrenteinntektIVannkraftverk(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVannkraftverk
            opprettNySubforekomstAv(forekomsttype) {
                medId(kodeverdi.kode)
                medFelt(forekomsttype.type, kodeverdi.kode)
                medFelt(forekomsttype.beloep, beloep)
            }
        }
    }

    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.opprettNyForekomstFradragIBruttoGrunnrenteinntektIVannkraftverk(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVannkraftverk
            opprettNySubforekomstAv(forekomsttype) {
                medId(kodeverdi.kode)
                medFelt(forekomsttype.type, kodeverdi.kode)
                medFelt(forekomsttype.beloep, beloep)
            }
        }
    }

    /**
     * Tilsvarende for spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVannkraftverk (en annen kodeliste enn
     * inntektOgFradragIGrunnrente).
     */
    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.opprettNyForekomstGrunnlagIBeregningAvSelskapsskattIVannkraftverk(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVannkraftverk
            opprettNySubforekomstAv(forekomsttype) {
                medId(kodeverdi.kode)
                medFelt(forekomsttype.type, kodeverdi.kode)
                medFelt(forekomsttype.beloep, beloep)
            }
        }
    }

    internal val salgsinntekt =
        kalkyle("salgsinntekt") {
            val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (tekniskInntektsaar <= 2025) {
                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon.harVerdi() &&
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris.harVerdi()
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt) {
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon *
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris
                        }
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.harVerdi() &&
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris.harVerdi()
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt) {
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon *
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris
                        }
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon.harVerdi() &&
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_konsesjonsEllerKontraktspris.harVerdi()
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt) {
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon *
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_konsesjonsEllerKontraktspris
                        }
                    }

                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.produksjon.harVerdi() && forekomstType.konsesjonsEllerKontraktspris.harVerdi()
                    } forHverForekomst {
                        settFelt(forekomstType.salgsinntekt) {
                            forekomstType.produksjon * forekomstType.konsesjonsEllerKontraktspris
                        }
                    }
                } else {
                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_produksjon.harVerdi() &&
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris.harVerdi()
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_salgsinntekt) {
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_produksjon *
                                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_konsesjonsEllerKontraktspris
                        }
                    }

                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.harVerdi() &&
                            forekomstType.spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris.harVerdi()
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt) {
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_produksjon *
                                forekomstType.spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_konsesjonsEllerKontraktspris
                        }
                    }
                }
            }
        }

    internal val oevrigAarsproduksjonProduksjonFra2026 =
        kalkyle("oevrigAarsproduksjonProduksjon") {
            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                val sumVolumIKWIInntektsaaret =
                    forekomsterAv(modell.kontraktForVannkraftverk.spesifikasjonAvKontraktIVannkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.volumIKWIInntektsaaret.tall()
                    }
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrense()
                } forHverForekomst {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_produksjon) {
                        (forekomstType.totalAarsproduksjon -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_produksjon -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_produksjon -
                            sumVolumIKWIInntektsaaret +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumTotaltDekningskjoep).somHeltall()
                    }
                }
            }
        }

    internal val oevrigAarsproduksjonSpotmarkedsprisFra2026 =
        kalkyle("oevrigAarsproduksjonSpotmarkedspris") {
            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrense()
                } forHverForekomst {
                    hvis(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_produksjon.tall().erTryggAaDelePaa()) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_spotmarkedspris) {
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_salgsinntekt /
                                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_produksjon
                        }
                    }
                }
            }
        }

    internal val gjennomsnittligDekningskjoep = kalkyle("gjennomsnittligDekningskjoep") {
        hvis (inntektsaar.tekniskInntektsaar >= 2026) {
            forAlleForekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) {
                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_totaltDekningskjoep.harVerdi() &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumTotaltDekningskjoep.harVerdi()
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_gjennomsnittligTotaltDekningskjoepPerKWh) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_totaltDekningskjoep /
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumTotaltDekningskjoep
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_dekningskjoepTilknyttetKonsesjonskraft.harVerdi() &&
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumDekningskjoepTilknyttetKonsesjonskraft.harVerdi()
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_gjennomsnittligDekningskjoepTilknyttetKonsesjonskraftPerKWh) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_dekningskjoepTilknyttetKonsesjonskraft /
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvDekningskjoep_volumDekningskjoepTilknyttetKonsesjonskraft
                    }
                }
            }
        }
    }

    internal val gevinstOgTapVedRealisasjonAvAnleggsmiddelSomBenyttesIKraftproduksjon =
        kalkyle("gevinstOgTapVedRealisasjonAvAnleggsmiddelSomBenyttesIKraftproduksjon") {
            val inntektsaar = inntektsaar

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
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (inntektsaar.tekniskInntektsaar <= 2025) {
                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon) {
                        summerInntektFraGevinstOgTapskonto(forekomstType.loepenummer.verdi())
                    }

                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon) {
                        summerInntektsfradragFraGevinstOgTapskonto(forekomstType.loepenummer.verdi()).absoluttverdi()
                    }
                } else {
                    opprettNyForekomstInntektIBruttoGrunnrenteinntektIVannkraftverk(
                        inntektIGrunnrente.kode_gevinstVedRealisasjonAvSaerskiltDriftsmiddelBenyttetIVannkraftproduksjon,
                        summerInntektFraGevinstOgTapskonto(forekomstType.loepenummer.verdi())
                    )

                    opprettNyForekomstFradragIBruttoGrunnrenteinntektIVannkraftverk(
                        fradragIGrunnrente.kode_tapVedRealisasjonAvSaerskiltDriftsmiddelBenyttetIVannkraftproduksjon,
                        summerInntektsfradragFraGevinstOgTapskonto(forekomstType.loepenummer.verdi()).absoluttverdi()
                    )
                }
            }
        }

    internal val skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon =
        kalkyle("skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon") {
            val inntektsaar = inntektsaar
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
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (inntektsaar.tekniskInntektsaar <= 2025) {
                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon) {
                        summerSaerskiltAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                            summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                            summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(forekomstType.loepenummer.verdi()) -
                            summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(forekomstType.loepenummer.verdi()) -
                            summerSaldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                            summerLineaertAvskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                            summerIkkeAvskrivbartAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi())
                    }
                } else {
                    opprettNyForekomstFradragIBruttoGrunnrenteinntektIVannkraftverk(
                        fradragIGrunnrente.kode_skattemessigAvskrivningAvDriftsmiddelBenyttetIVannkraftproduksjon,
                        summerSaerskiltAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                            summerSaldoavskrevetAnleggsmiddelAaretsAvskrivning(forekomstType.loepenummer.verdi()) +
                            summerLineaertavskrevetAnleggsmiddelAaaretsAvskrivning(forekomstType.loepenummer.verdi()) -
                            summerSaldoavskrevetAnleggsmiddelAaretsInntektAvNegativSaldo(forekomstType.loepenummer.verdi()) -
                            summerSaldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                            summerLineaertAvskrevetAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi()) -
                            summerIkkeAvskrivbartAnleggsmiddelAaretsInntektsfoeringAvGevinst(forekomstType.loepenummer.verdi())
                    )
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
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (tekniskInntektsaar <= 2025) {
                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilKraftproduksjon) {
                        investeringskostnadKnyttetTilSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilIkkeAvskrivbarAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetAnleggsmiddelUnderUtfoerelse(forekomstType.loepenummer.verdi())
                    }
                } else {
                    opprettNyForekomstFradragIBruttoGrunnrenteinntektIVannkraftverk(
                        fradragIGrunnrente.kode_investeringskostnad,
                        investeringskostnadKnyttetTilSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetTilIkkeAvskrivbarAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            investeringskostnadKnyttetAnleggsmiddelUnderUtfoerelse(forekomstType.loepenummer.verdi())
                    )
                }
            }

        }

    val aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoertgrunnlag_GrunnlagForBeregningAvSelskapsskatt =
        kalkyle("aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoertgrunnlag_GrunnlagForBeregningAvSelskapsskatt") {
            val inntektsaar = inntektsaar

            val satser = satser!!
            val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
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

            hvis(inntektsaar.tekniskInntektsaar <= 2025) {
                forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrenseV6()
                } forHverForekomst {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelKnyttetTilVannkraftverkSomErDirekteUtgiftsfoert) {
                        (aaretsAvkastningSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            aaretsAvskrivningSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            aaretsAvskrivningLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi())).somHeltall()
                    }
                }
            }

            hvis(inntektsaar.tekniskInntektsaar >= 2026) {
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                    samletPaastempletMerkeytelseIKvaOverGrense()
                } forHverForekomst {
                    opprettNyForekomstGrunnlagIBeregningAvSelskapsskattIVannkraftverk(
                        grunnlagIBeregningAvSelskapsskatt.kode_aaretsAvskrivningPaaAnleggsmiddelSomErDirekteUtgiftsfoert,
                        (aaretsAvkastningSaerskilteAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            aaretsAvskrivningSaldoavskrevetAnleggsmidler(forekomstType.loepenummer.verdi()) +
                            aaretsAvskrivningLineaertavskrevetAnleggsmidler(forekomstType.loepenummer.verdi())).somHeltall()
                    )
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (tekniskInntektsaar <= 2025) {
                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt) {
                        (felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt -
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_dekningskjoep +
                            summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar) -
                            summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar) +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_driftsstoetteTilProduksjonAvNyVannkraft +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraUtstedtElsertifikat +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_opprinnelsesgaranti +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektVedAvslutningEllerEndringAvFastpriskontrakt -
                            (felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsAvskrivningPaaAnleggsmiddelKnyttetTilVannkraftverkSomErDirekteUtgiftsfoert +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                                )).somHeltall()
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt
                            .stoerreEllerLik(0)
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall()
                        }
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt
                            .mindreEnn(0)
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall().absoluttverdi()
                        }
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEnn 0 &&
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEllerLik
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                        }
                    }

                    hvis(
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEnn 0 &&
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            mindreEnn
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    ) {
                        settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                        }
                    }
                    settFelt(felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                                felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    }
                } else {
                    val sumInntektIGrunnrente =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVannkraftverk) der {
                            forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrenteVannkraft(inntektsaar)
                        } summerVerdiFraHverForekomst {
                            forekomstType.beloep.tall()
                        }

                    val sumFradragIGrunnrente =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVannkraftverk) der {
                            forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrenteVannkraft(inntektsaar) &&
                                forekomstType.type ulik fradragIGrunnrente.kode_investeringskostnad
                        } summerVerdiFraHverForekomst {
                            forekomstType.beloep.tall()
                        }

                    val sumGrunnlagTillegg =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVannkraftverk) der {
                            forekomstType.type likEnAv GrunnlagIBeregningAvSelskapsskatt.koderVannkraft(inntektsaar)
                        } summerVerdiFraHverForekomst {
                            if (forekomstType.type.verdi().erTillegg(inntektsaar)) {
                                forekomstType.beloep.tall()
                            } else {
                                BigDecimal.ZERO
                            }
                        }

                    val sumGrunnlagFradrag =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVannkraftverk) der {
                            forekomstType.type likEnAv GrunnlagIBeregningAvSelskapsskatt.koderVannkraft(inntektsaar)
                        } summerVerdiFraHverForekomst {
                            if (forekomstType.type.verdi().erFradrag(inntektsaar)) {
                                forekomstType.beloep.tall()
                            } else {
                                BigDecimal.ZERO
                            }
                        }

                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_grunnlagForBeregningAvSelskapsskatt) {
                        (forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_salgsinntekt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_salgsinntekt +
                            sumInntektIGrunnrente + sumGrunnlagTillegg -
                            sumFradragIGrunnrente - sumGrunnlagFradrag).somHeltall()
                    }

                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_grunnlagForBeregningAvSelskapsskatt
                            .stoerreEllerLik(0)
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (forekomstType.spesifikasjonAvGrunnrenteinntekt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall()
                        }
                    }

                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_grunnlagForBeregningAvSelskapsskatt
                            .mindreEnn(0)
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                            (forekomstType.spesifikasjonAvGrunnrenteinntekt_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).somHeltall().absoluttverdi()
                        }
                    }

                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEnn 0 &&
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEllerLik
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                        }
                    }

                    hvis(
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            stoerreEnn 0 &&
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                            mindreEnn
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    ) {
                        settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                        }
                    }
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoerbarBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                                forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet
                    }
                }
            }
        }

    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar: Int) =
        if (tekniskInntektsaar <= 2025) {
            forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                forekomstType.salgsinntekt.harVerdi()
            } summerVerdiFraHverForekomst {
                forekomstType.salgsinntekt.tall()
            }
        } else null

    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar: Int) =
        if (tekniskInntektsaar <= 2025) {
            forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                forekomstType.dekningskjoep.harVerdi()
            } summerVerdiFraHverForekomst {
                forekomstType.dekningskjoep.tall()
            }
        } else null

    internal val samletBruttoInntektOgFradragIGrunnrenteinntekt =
        kalkyle("samletBruttoInntektIGrunnrenteinntekt") {
            val inntektsaar = inntektsaar
            val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                if (tekniskInntektsaar <= 2025) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoInntektIGrunnrenteinntekt) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt -
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_dekningskjoep +
                            summenAvSalgsinntektFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar) -
                            summenAvDekningskjoepFraAlleForekomsterKraftLevertIhtKontrakt(tekniskInntektsaar) +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_gevinstVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_driftsstoetteTilProduksjonAvNyVannkraft +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektFraUtstedtElsertifikat +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_opprinnelsesgaranti +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_inntektVedAvslutningEllerEndringAvFastpriskontrakt
                    }
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoFradragIGrunnrenteinntekt) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_driftskostnad +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadTilPumpingAvKraft +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_konsesjonsavgift +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_eiendomsskatt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_skattemessigAvskrivningAvAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvSaerskiltAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_tapVedRealisasjonAvOrdinaertAnleggsmiddelSomBenyttesIKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_investeringskostnadKnyttetTilKraftproduksjon +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetSelskapsskatt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet -
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt +
                            felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntekt_kostnadVedAvslutningEllerEndringAvFastpriskontrakt
                    }
                } else {
                    val sumInntektIGrunnrente =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVannkraftverk) der {
                            forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrenteVannkraft(inntektsaar)
                        } summerVerdiFraHverForekomst {
                            forekomstType.beloep.tall()
                        }

                    val sumFradragIGrunnrente =
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVannkraftverk) der {
                            forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrenteVannkraft(inntektsaar)
                        } summerVerdiFraHverForekomst {
                            forekomstType.beloep.tall()
                        }

                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoInntektIGrunnrenteinntekt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_salgsinntekt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_salgsinntekt +
                            sumInntektIGrunnrente
                    }
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_samletBruttoFradragIGrunnrenteinntekt) {
                        sumFradragIGrunnrente +
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet -
                            forekomstType.spesifikasjonAvGrunnrenteinntekt_beregnetNegativSelskapsskattTilFremfoeringIVannkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt
                    }
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
        val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
        if (tekniskInntektsaar <= 2025) {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeLeieavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.produksjon.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeLeieavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntekt.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeLeieavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.volumDekningskjoep.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeLeieavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.dekningskjoep.tall()
                    }
                }
            }
        }
    }

    internal val kontraktstypeKjoepekontrakt = kalkyle("kontraktstypeKjoepekontrakt") {
        val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
        if (tekniskInntektsaar <= 2025) {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeKjoepekontrakt) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.produksjon.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeKjoepekontrakt) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntekt.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeKjoepekontrakt) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.volumDekningskjoep.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeKjoepekontrakt) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.dekningskjoep.tall()
                    }
                }
            }
        }
    }

    internal val kontraktstypeFastprisavtale = kalkyle("kontraktstypeFastprisavtale") {
        val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
        if (tekniskInntektsaar <= 2025) {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForKontraktstypeFastprisavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.produksjon.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForKontraktstypeFastprisavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntekt.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForDekningskjoepForKontraktstypeFastprisavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.volumDekningskjoep.tall()
                    }
                }
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForDekningskjoepForKontraktstypeFastprisavtale) {
                    forekomsterAv(modell2025.kraftverk_spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftLevertIhtKontrakt) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.dekningskjoep.tall()
                    }
                }
            }
        }
    }

    internal val samletVolumForOevrigKraftsalg = kalkyle("samletVolumForOevrigKraftsalg") {
        val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val kraftTattUtIhtKonsesjonProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_produksjon.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_produksjon.tall()
                    }
                }
            val kraftForbruktIEgenProduksjonsvirksomhetProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_produksjon.tall()
                    }
                }

            val oevrigAarsproduksjonProduksjon =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_produksjon.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_produksjon.tall()
                    }
                }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletVolumForOevrigKraftsalg) {
                kraftTattUtIhtKonsesjonProduksjon + kraftForbruktIEgenProduksjonsvirksomhetProduksjon + oevrigAarsproduksjonProduksjon
            }
        }
    }

    internal val samletSalgsinntektForOevrigKraftsalg = kalkyle("samletSalgsinntektForOevrigKraftsalg") {
        val tekniskInntektsaar = inntektsaar.tekniskInntektsaar
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
            val kraftTattUtIhtKonsesjonSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftTattUtIhtKonsesjon_salgsinntekt.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftTattUtIhtKonsesjon_salgsinntekt.tall()
                    }
                }
            val kraftForbruktIEgenProduksjonsvirksomhetSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_kraftForbruktIEgenProduksjonsvirksomhet_salgsinntekt.tall()
                    }
                }

            val oevrigAarsproduksjonSalgsinntekt =
                forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) summerVerdiFraHverForekomst {
                    if (tekniskInntektsaar <= 2025) {
                        felt2025.spesifikasjonAvKraftverk.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntekt_oevrigAarsproduksjon_salgsinntekt.tall()
                    } else {
                        forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvOevrigInntektIBruttoGrunnrenteinntektIVannkraftverk_oevrigAarsproduksjon_salgsinntekt.tall()
                    }
                }
            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntekt_oevrigTilVisningAvKontraktsinformasjonPerVannkraftverk_samletSalgsinntektForOevrigKraftsalg) {
                kraftTattUtIhtKonsesjonSalgsinntekt + kraftForbruktIEgenProduksjonsvirksomhetSalgsinntekt + oevrigAarsproduksjonSalgsinntekt
            }
        }
    }


    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            salgsinntekt,
            oevrigAarsproduksjonProduksjonFra2026,
            oevrigAarsproduksjonSpotmarkedsprisFra2026,
            gjennomsnittligDekningskjoep,
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
