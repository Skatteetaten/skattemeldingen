package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

/**
 * Saldogruppe A og B håndteres helt likt.
 *
 * Spec: https://wiki.sits.no/display/SIR/FR-Spes+av+bal+-+Anleggsmidler
 */
internal object SaldoavskrevetAnleggsmiddel2020 : HarKalkylesamling {

    internal val grunnlagForAvskrivningOgInntektsFoeringKalkyle = kalkyle {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(forekomstType.saldogruppe.verdi() != saldogruppe.kode_b.kode) {
                val grunnlagForAvskrivningOgInntektsfoering = forekomstType.inngaaendeVerdi +
                    forekomstType.nyanskaffelse +
                    forekomstType.paakostning -
                    forekomstType.offentligTilskudd +
                    forekomstType.justeringAvInngaaendeMva -
                    forekomstType.nedskrevetVerdiAvUtskilteDriftsmidler -
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
                    forekomstType.nedskrevetVerdiAvUtskilteDriftsmidler -
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
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) der {
            forekomstType.saldogruppe lik saldogruppe.kode_i
        } forHverForekomst {
            hvis(forekomstType.forretningsbyggAnskaffetFoer01011984_nedskrevetVerdiPr01011984.harVerdi()) {
                settFelt(forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning) {
                    forekomstType.forretningsbyggAnskaffetFoer01011984_historiskKostpris -
                        forekomstType.forretningsbyggAnskaffetFoer01011984_nedskrevetVerdiPr01011984
                }
            }
        }
    }

    internal val saldogruppeEFGHI = listOf(
        saldogruppe.kode_e,
        saldogruppe.kode_f,
        saldogruppe.kode_g,
        saldogruppe.kode_h,
        saldogruppe.kode_i,
    )

    val aaretsAvskrivningKalkyle = kalkyle {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) der {
            forekomstType.grunnlagForAvskrivningOgInntektsfoering.erPositiv()
        } forHverForekomst {
            settFelt(forekomstType.aaretsAvskrivning) {
                if (forekomstType.saldogruppe lik saldogruppe.kode_d) {
                    (forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                        forekomstType.avskrivningssats.prosent()) +
                        (forekomstType.grunnlagForStartavskrivning *
                            forekomstType.avskrivningssatsForStartavskrivning.prosent())
                } else {
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                        forekomstType.avskrivningssats.prosent()
                }
            }

            hvis(forekomstType.saldogruppe lik saldogruppe.kode_i) {
                val aaretsAvskrivningMellomverdi = beregnHvis(
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEnn forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                ) {
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                        forekomstType.avskrivningssats.prosent()
                }

                val utgaaendeVerdiMellomverdi = forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                    aaretsAvskrivningMellomverdi

                hvis(forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning stoerreEllerLik utgaaendeVerdiMellomverdi) {
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                            forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                    }
                }

                hvis(
                    forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning mindreEnn utgaaendeVerdiMellomverdi
                        || forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning.harIkkeVerdi()
                ) {
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                            forekomstType.avskrivningssats.prosent()
                    }
                }
            }
        }
    }

    internal val saldogruppeACC2J = listOf(
        saldogruppe.kode_a,
        saldogruppe.kode_c,
        saldogruppe2023.kode_c2,
        saldogruppe.kode_j,
    )

    internal val aaretsInntektsfoeringAvNegativSaldoKalkyle = kalkyle {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(forekomstType.saldogruppe likEnAv saldogruppeACC2J) {
                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEnn -14999) {
                    val aaretsInntektsfoeringAvNegativSaldo =
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                            forekomstType.avskrivningssats.prosent()

                    settFelt(forekomstType.aaretsInntektsfoeringAvNegativSaldo) {
                        aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                    }
                }

                hvis(
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik -14999
                        && forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEllerLik -1
                ) {
                    val aaretsInntektsfoeringAvNegativSaldo =
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering.tall()
                    settFelt(forekomstType.aaretsInntektsfoeringAvNegativSaldo) {
                        aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                    }
                }
            }

            hvis(forekomstType.saldogruppe lik saldogruppe.kode_d) {
                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEnn -14999) {
                    val aaretsInntektsfoeringAvNegativSaldo =
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering *
                            forekomstType.avskrivningssats.prosent()

                    settFelt(forekomstType.aaretsInntektsfoeringAvNegativSaldo) {
                        aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                    }
                }

                hvis(
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik -14999
                        && forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEllerLik 0
                ) {
                    val aaretsInntektsfoeringAvNegativSaldo =
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering.tall()
                    settFelt(forekomstType.aaretsInntektsfoeringAvNegativSaldo) {
                        aaretsInntektsfoeringAvNegativSaldo.absoluttverdi() medAntallDesimaler 2
                    }
                }
            }
        }
    }

    internal val utgaaendeVerdiKalkyle = kalkyle {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) forHverForekomst {
            hvis(forekomstType.saldogruppe likEnAv saldogruppeEFGHI) {
                settFelt(forekomstType.utgaaendeVerdi) {
                    forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                        forekomstType.aaretsAvskrivning -
                        forekomstType.tapOverfoertTilGevinstOgTapskonto +
                        forekomstType.gevinstOverfoertTilGevinstOgTapskonto
                }
            }


            hvis(forekomstType.saldogruppe likEnAv saldogruppeACC2J) {
                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                            forekomstType.aaretsAvskrivning
                    }
                }

                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEnn 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering +
                            forekomstType.aaretsInntektsfoeringAvNegativSaldo
                    }
                }
            }

            hvis(forekomstType.saldogruppe lik saldogruppe.kode_d) {
                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                            forekomstType.aaretsAvskrivning
                    }
                }

                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEnn 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering +
                            forekomstType.aaretsInntektsfoeringAvNegativSaldo
                    }
                }
            }

            hvis(forekomstType.saldogruppe lik saldogruppe.kode_b) {
                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering stoerreEllerLik 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering -
                            forekomstType.aaretsAvskrivning
                    }
                }

                hvis(forekomstType.grunnlagForAvskrivningOgInntektsfoering mindreEnn 0) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAvskrivningOgInntektsfoering +
                            forekomstType.gevinstOverfoertTilGevinstOgTapskonto
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            grunnlagForAvskrivningOgInntektsFoeringKalkyle,
            nedreGrenseForAvskrivningKalkyle,
            aaretsAvskrivningKalkyle,
            aaretsInntektsfoeringAvNegativSaldoKalkyle,
            utgaaendeVerdiKalkyle
        )
    }
}