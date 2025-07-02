package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.anleggsmiddelOgToemmerkonto_grenseverdiEn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.enkeltsaldoene
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.minsteGjenstaaendeLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.skattemessigLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Saldogruppe A og B håndteres helt likt.
 *
 * Spec: https://wiki.sits.no/pages/viewpage.action?pageId=279550129
 */
internal object SaldoavskrevetAnleggsmiddelFra2024 : HarKalkylesamling {

    internal val grunnlagForAvskrivningOgInntektsFoeringKalkyle = kalkyle {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(forekomstType.saldogruppe.verdi() != saldogruppe.kode_b.kode) {
                val grunnlagForAvskrivningOgInntektsfoering = forekomstType.inngaaendeVerdi +
                    forekomstType.nyanskaffelse +
                    forekomstType.paakostning -
                    forekomstType.offentligTilskudd +
                    forekomstType.justeringAvInngaaendeMva -
                    forekomstType.nedskrevetVerdiAvUtskiltAnleggsmiddel -
                    forekomstType.vederlagVedRealisasjonOgUttak -
                    forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    forekomstType.reinvestertBetingetSkattefriSalgsgevinst -
                    forekomstType.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst

                settFelt(forekomstType.grunnlagForAvskrivningOgInntektsfoering) {
                    grunnlagForAvskrivningOgInntektsfoering
                }
            }

            hvis(forekomstType.saldogruppe lik saldogruppe.kode_b) {
                val grunnlagForAvskrivningOgInntektsfoering = forekomstType.inngaaendeVerdi +
                    forekomstType.nyanskaffelse -
                    forekomstType.nedskrevetVerdiAvUtskiltAnleggsmiddel -
                    forekomstType.vederlagVedRealisasjonOgUttak +
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    forekomstType.reinvestertBetingetSkattefriSalgsgevinst -
                    forekomstType.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst

                settFelt(forekomstType.grunnlagForAvskrivningOgInntektsfoering) {
                    grunnlagForAvskrivningOgInntektsfoering
                }
            }

        }
    }

    val nedreGrenseForAvskrivningKalkyle = kalkyle {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe likEnAv listOf(
                saldogruppe.kode_i,
                saldogruppe.kode_j
            )
        } forHverForekomst {
            hvis(forekomstType.forretningsbyggAnskaffetFoer01011984_nedskrevetVerdiPr01011984.harVerdi()) {
                settFelt(forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning) {
                    forekomstType.forretningsbyggAnskaffetFoer01011984_historiskKostpris -
                        forekomstType.forretningsbyggAnskaffetFoer01011984_nedskrevetVerdiPr01011984
                }
            }
        }
    }

    val aaretsAvskrivningKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.grunnlagForAvskrivningOgInntektsfoering.erPositiv()
                && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
                && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_erAnleggsmiddelUnderUtfoerelse.erUsann()
                && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_erAnleggsmiddelUnderUtfoerelse.erUsann()
                && (!(forekomstType.saldogruppe likEnAv enkeltsaldoene) || (if (inntektsaar.tekniskInntektsaar >= 2023) {
                forekomstType.erDetFysiskAnleggsmiddelIUtgaaendeVerdi.erSann()
            } else {
                forekomstType.vederlagVedRealisasjonOgUttak.harIkkeVerdi()
            }))
        } forHverForekomst {
            val skalBrukeNedreGrense = if (forekomstType.saldogruppe likEnAv listOf(saldogruppe.kode_i, saldogruppe.kode_j)) {
                val aaretsAvskrivningMellomverdi = beregnHvis(
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEnn forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                ) {
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                        forekomstType.avskrivningssats.prosent()
                }

                val utgaaendeVerdiMellomverdi = forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                    aaretsAvskrivningMellomverdi
                forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning stoerreEllerLik utgaaendeVerdiMellomverdi
            } else false

            settFelt(forekomstType.aaretsAvskrivning) {
                if (skalBrukeNedreGrense) {
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                        forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                } else {
                    val antallDager = if (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret.harVerdi()) {
                        (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret / 365)
                    } else { 1.toBigDecimal() }

                    forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                        forekomstType.avskrivningssats.prosent() *
                        antallDager
                }
            }
        }
    }

    internal val aaretsInntektsfoeringAvNegativSaldoKalkyle = kalkyle {
        val satser = satser!!
        val harSpesifikasjonAvKraftverk = harMinstEnForekomstAv(modell.kraftverk_spesifikasjonAvKraftverk)
        val saldogruppeBasertPaaInntektsaar = if (inntektsaar.tekniskInntektsaar <= 2023) {
            saldogruppeACC2DJ
        } else {
            saldogruppeACDJ
        }

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe likEnAv saldogruppeBasertPaaInntektsaar) {
                when {
                    harSpesifikasjonAvKraftverk && modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ() -> {
                        settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
                            (modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent()).absoluttverdi() medAntallDesimaler 2
                        }
                    }

                    modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering mindreEllerLik satser.sats(
                            anleggsmiddelOgToemmerkonto_grenseverdiEn
                        ) ->
                     {
                        val aaretsInntektsfoeringAvNegativSaldo =
                            if (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret.harIkkeVerdi()) {
                                modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                    modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent()
                            } else {
                                modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                    modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent() *
                                    (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret / 365)
                            }

                        settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
                            aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                        }
                    }

                    modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering stoerreEnn satser.sats(anleggsmiddelOgToemmerkonto_grenseverdiEn)
                            && modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ()
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_erAnleggsmiddelUnderUtfoerelse.erUsann()
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraftanlegg_erAnleggsmiddelUnderUtfoerelse.erUsann()
                                 -> {
                        val aaretsInntektsfoeringAvNegativSaldo =
                            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.tall()
                        settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
                            aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                        }
                    }
                }
            }
        }
    }

    val saldogruppeACC2DJ = listOf(
        saldogruppe.kode_a,
        saldogruppe.kode_c,
        saldogruppe2023.kode_c2,
        saldogruppe.kode_d,
        saldogruppe.kode_j,
    )

    val saldogruppeACDJ = listOf(
        saldogruppe.kode_a,
        saldogruppe.kode_c,
        saldogruppe.kode_d,
        saldogruppe.kode_j,
    )


    internal val utgaaendeVerdiKalkyle = kalkyle {
        val saldogruppeBasertPaaInntektsaar = if (inntektsaar.tekniskInntektsaar <= 2023) {
            saldogruppeACC2DJ
        } else {
            saldogruppeACDJ
        }

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe likEnAv enkeltsaldoene) {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.utgaaendeVerdi) {
                    modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering -
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsAvskrivning -
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.tapOverfoertTilGevinstOgTapskonto +
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.gevinstOverfoertTilGevinstOgTapskonto
                }
            }


            hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe likEnAv saldogruppeBasertPaaInntektsaar) {
                hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik 0) {
                    settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.utgaaendeVerdi) {
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering -
                            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsAvskrivning
                    }
                }

                hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ()) {
                    settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.utgaaendeVerdi) {
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering +
                            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo
                    }
                }
            }

            hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe lik saldogruppe.kode_b) {
                hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik 0) {
                    settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.utgaaendeVerdi) {
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering -
                            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsAvskrivning
                    }
                }

                hvis(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ()) {
                    settFelt(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.utgaaendeVerdi) {
                        modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering +
                            modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.gevinstOverfoertTilGevinstOgTapskonto
                    }
                }
            }
        }
    }

    val konsumprisindeksjustertInvesteringskostnadKalkye = kalkyle {
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()
        val konsumprisindeksInntektsaar =
            hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall())

        val konsumprisindeksDesemberInntektsaar =
            hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall(), forDesember = true)

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.harVerdi() && forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            hvis(forekomstType.ervervsdato.harVerdi()) {
                val kraftverk =
                    kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]
                hvis(kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
                        val konsumprisindeks1996Desember =
                            hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1996), forDesember = true)

                        val konsumprisindeksAnskaffelsesaar =
                            hentKonsumprisindeksVannkraft(forekomstType.ervervsdato.dato().aar())

                        hvis(
                            forekomstType.ervervsdato.dato().aar() mindreEllerLik 1996 &&
                                konsumprisindeks1996Desember.erTryggAaDelePaa()
                        ) {
                            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad) {
                                forekomstType.nyanskaffelse * konsumprisindeksDesemberInntektsaar / konsumprisindeks1996Desember
                            }
                        }

                        hvis(
                            forekomstType.ervervsdato.dato().aar() stoerreEllerLik 1997 &&
                                konsumprisindeksAnskaffelsesaar.erTryggAaDelePaa()
                        ) {
                            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad) {
                                forekomstType.nyanskaffelse * konsumprisindeksInntektsaar / konsumprisindeksAnskaffelsesaar
                            }
                        }
                    }
                }
            }
        }
    }

    val gjenstaaendeLevetidKalkyle = kalkyle {
        val inntektsaar = inntektsaar

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei
        } forHverForekomst {
            val aarSidenAnskaffelse = inntektsaar.gjeldendeInntektsaar.toBigDecimal() - forekomstType.ervervsdato.aar() + 1
            val minsteGjenstaaendelevetid = forekomstType.saldogruppe.verdi().minsteGjenstaaendeLevetid(inntektsaar)
            val skattemessigLevetid = forekomstType.saldogruppe.verdi().skattemessigLevetid(inntektsaar)

            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_gjenstaaendeLevetid) {
                (skattemessigLevetid - aarSidenAnskaffelse).medMinimumsverdi(minsteGjenstaaendelevetid)
            }
        }
    }

    val naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        val satser = satser!!
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann() &&
                forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            val kraftverk = kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]
            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val avskrivningstid = forekomstType.saldogruppe.verdi().skattemessigLevetid(inntektsaar)
                val kapitaliseringsrente = satser.sats(Sats.vannkraft_kapitaliseringsrente).toDouble()
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk) {
                    naaverdiAvFremtidigeUtskiftningskostnader(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad.tall(),
                        kapitaliseringsrente,
                        avskrivningstid,
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_gjenstaaendeLevetid.tall()
                    )
                }
            }
        }
    }

    val aaretsFriinntektKalkyle = kalkyle {
        val satser = satser!!
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
        } forHverForekomst {
            val kraftverk = kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]
            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                hvis(
                    kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIAnskaffelsesaaret(kraftverk.datoForOverdragelseVedErvervIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        (forekomstType.nyanskaffelse + forekomstType.utgaaendeVerdi) / 2 * normRente * (dagerEid / 365)
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        forekomstType.inngaaendeVerdi / 2 * normRente + (dagerEid / 365)
                    }
                }
            }
        }
    }

    val kalkyleSamling = Kalkylesamling(
        grunnlagForAvskrivningOgInntektsFoeringKalkyle,
        nedreGrenseForAvskrivningKalkyle,
        aaretsAvskrivningKalkyle,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        utgaaendeVerdiKalkyle,
        konsumprisindeksjustertInvesteringskostnadKalkye,
        gjenstaaendeLevetidKalkyle,
        naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle,
        aaretsFriinntektKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
