package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.anleggsmiddelOgToemmerkonto_grenseverdiEn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.minsteGjenstaaendeLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Saldogruppe.skattemessigLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

/**
 * Saldogruppe A og B håndteres helt likt.
 *
 * Spec: https://wiki.sits.no/pages/viewpage.action?pageId=279550129
 */
internal object SaldoavskrevetAnleggsmiddel : HarKalkylesamling {

    internal val enkeltsaldoene = listOf(
        saldogruppe.kode_e,
        saldogruppe.kode_f,
        saldogruppe.kode_g,
        saldogruppe.kode_h,
        saldogruppe.kode_i,
    )

    val aaretsAvskrivningKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.grunnlagForAvskrivningOgInntektsfoering.erPositiv()
                && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
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
        val harSpesifikasjonAvKraftverk = harMinstEnForekomstAv(modell2023.kraftverk_spesifikasjonAvKraftverk)

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.saldogruppe likEnAv saldogruppeACC2DJ) {
                when {
                    harSpesifikasjonAvKraftverk && modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ() -> {
                        settFelt(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
                            (modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent()).absoluttverdi() medAntallDesimaler 2
                        }
                    }

                    modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering mindreEllerLik satser.sats(
                            anleggsmiddelOgToemmerkonto_grenseverdiEn
                        ) ->
                     {
                        val aaretsInntektsfoeringAvNegativSaldo =
                            if (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret.harIkkeVerdi()) {
                                modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                    modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent()
                            } else {
                                modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering *
                                    modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.avskrivningssats.prosent() *
                                    (forekomstType.antallDagerKnyttetTilSkattepliktigVirksomhetForForetakMedBegrensetSkattepliktIInntektsaaret / 365)
                            }

                        settFelt(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
                            aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                        }
                    }

                    modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering stoerreEnn satser.sats(anleggsmiddelOgToemmerkonto_grenseverdiEn)
                            && modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.erNegativ()
                            && forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann()
                                 -> {
                        val aaretsInntektsfoeringAvNegativSaldo =
                            modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering.tall()
                        settFelt(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo) {
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

    val konsumprisindeksjustertInvesteringskostnadKalkye = kalkyle {
        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
            val konsumprisindeks = hentKonsumprisindeksVannkraft(forekomstType.ervervsdato.aar())
            val konsumprisindeks1997 =
                hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1997))

            hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() && konsumprisindeks != BigDecimal.ZERO && konsumprisindeks != null) {
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_konsumprisindeksjustertInvesteringskostnad) {
                    (forekomstType.nyanskaffelse * konsumprisindeks1997) / konsumprisindeks
                }
            }
        }
    }

    val gjenstaaendeLevetidKalkyle = kalkyle {
        val inntektsaar = inntektsaar

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann()
        } forHverForekomst {
            val aarSidenAnskaffelse = inntektsaar.gjeldendeInntektsaar.toBigDecimal() - forekomstType.ervervsdato.aar() + 1
            val minsteGjenstaaendelevetid = forekomstType.saldogruppe.verdi().minsteGjenstaaendeLevetid(inntektsaar)
            val skattemessigLevetid = forekomstType.saldogruppe.verdi().skattemessigLevetid(inntektsaar)

            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjenstaaendeLevetid) {
                (skattemessigLevetid - aarSidenAnskaffelse).medMinimumsverdi(minsteGjenstaaendelevetid)
            }
        }
    }

    val naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        val satser = satser!!
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann() &&
                forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            val kraftverk = kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi()]
            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val avskrivningstid = forekomstType.saldogruppe.verdi().skattemessigLevetid(inntektsaar)
                val kapitaliseringsrente = satser.sats(Sats.vannkraft_kapitaliseringsrente).toDouble()
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk) {
                    naaverdiAvFremtidigeUtskiftningskostnader(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_konsumprisindeksjustertInvesteringskostnad.tall(),
                        kapitaliseringsrente,
                        avskrivningstid,
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjenstaaendeLevetid.tall()
                    )
                }
            }
        }
    }

    val aaretsFriinntektKalkyle = kalkyle {
        val satser = satser!!
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
        } forHverForekomst {
            val kraftverk = kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi()]
            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                hvis(
                    kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIAnskaffelsesaaret(kraftverk.datoForOverdragelseVedErvervIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        (forekomstType.nyanskaffelse + forekomstType.utgaaendeVerdi) / 2 * normRente * (dagerEid / 365)
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        forekomstType.inngaaendeVerdi / 2 * normRente + (dagerEid / 365)
                    }
                }
            }
        }
    }

    val kalkyleSamling = Kalkylesamling(
        SaldoavskrevetAnleggsmiddelFra2024.grunnlagForAvskrivningOgInntektsFoeringKalkyle,
        SaldoavskrevetAnleggsmiddelFra2024.nedreGrenseForAvskrivningKalkyle,
        aaretsAvskrivningKalkyle,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        SaldoavskrevetAnleggsmiddelFra2024.utgaaendeVerdiKalkyle,
        konsumprisindeksjustertInvesteringskostnadKalkye,
        gjenstaaendeLevetidKalkyle,
        naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle,
        aaretsFriinntektKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
