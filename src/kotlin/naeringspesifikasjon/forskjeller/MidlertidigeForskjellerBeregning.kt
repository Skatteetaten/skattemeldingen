package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erIkkeSkjoennsfastsatt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.gjelderBankOgForsikring
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.MidlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2024

internal object MidlertidigeForskjellerBeregning : HarKalkylesamling {

    internal val forskjellstypeDriftsmiddelOgGoodwillKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum() && erIkkeSkjoennsfastsatt()) {
            val inntektsaar = inntektsaar
            val utgaaendeVerdiSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (inntektsaar.tekniskInntektsaar >= 2024) {
                        if (forekomstType.saldogruppe likEnAv listOf(saldogruppe.kode_i, saldogruppe.kode_j)) {
                            if (forekomstType.utgaaendeVerdi.erNegativ()) {
                                forekomstType.utgaaendeVerdi.tall()
                            } else {
                                forekomstType.utgaaendeVerdi -
                                    forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning
                            }
                        } else {
                            forekomstType.utgaaendeVerdi.tall()
                        }
                    } else {
                        if (forekomstType.saldogruppe lik saldogruppe.kode_i ||
                            (forekomstType.saldogruppe lik saldogruppe.kode_j && forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning.harVerdi())
                        ) {
                            forekomstType.utgaaendeVerdi -
                                forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning medMinimumsverdi 0
                        } else {
                            forekomstType.utgaaendeVerdi.tall()
                        }
                    }
                }

            val utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk.tall()
                }

            val utgaaendeVerdiLineartavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }

            val utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }

            val anskaffelseskost = if (inntektsaar.tekniskInntektsaar >= 2024) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_anleggsmiddelUnderUtfoerelseSomIkkeErAktivert) summerVerdiFraHverForekomst {
                    forekomstType.anskaffelseskost.tall()
                }
            } else {
                forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_anleggsmiddelIKraftverkUnderUtfoerelse) summerVerdiFraHverForekomst {
                    forekomstType.anskaffelseskost.tall()
                }
            }


            val skattemessigVerdi = utgaaendeVerdiSaldoavskrevetAnleggsmiddel +
                utgaaendeVerdiLineartavskrevetAnleggsmiddel +
                utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel +
                utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk +
                anskaffelseskost medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_driftsmiddelOgGoodwill,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private fun GeneriskModellKontekst.fullRegnskapspliktOgIkkeBankForsikringPetroleum() =
        fullRegnskapsplikt() && !gjelderBankOgForsikring() && !erPetroleumsforetak()

    private fun GeneriskModellKontekst.fullRegnskapspliktOgIkkePetroleum() =
        fullRegnskapsplikt() && !erPetroleumsforetak()


    private object SpesifikasjonAvVarelagerVerditype {
        const val skattemessigVerdi = "skattemessigVerdi"
    }

    internal val forskjellstypeVarebeholdingOgBiologiskEiendelKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkeBankForsikringPetroleum()) {
            val skattemessigVerdi = if (inntektsaar.tekniskInntektsaar <= 2024) {
                forekomsterAv(modell2024.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) der {
                    forekomstType.verditype.verdi() ==
                        SpesifikasjonAvVarelagerVerditype.skattemessigVerdi
                } summerVerdiFraHverForekomst {
                    forekomstType.sumVerdiAvVarelager.tall() - forekomstType.buskap.tall()
                } medAntallDesimaler 2
            } else {
                forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVarelager) summerVerdiFraHverForekomst {
                    forekomstType.sumVerdiAvVarelager.tall() - forekomstType.buskap.tall()
                } medAntallDesimaler 2
            }

            oppdaterEllerOpprettForskjellstype(
                if (inntektsaar.tekniskInntektsaar <= 2022) {
                    midlertidigForskjellstype2022.kode_varebeholdningOgBiologiskEiendel
                } else {
                    midlertidigForskjellstype.kode_varebeholdning
                },
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjellstypeKundefordringKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkeBankForsikringPetroleum()) {
            val skattemessigVerdi = modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring
                .skattemessigVerdiPaaKundefordring.tall() medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_kundefordring,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjellstypeAnnenFordringKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkeBankForsikringPetroleum() && inntektsaar.tekniskInntektsaar < 2023) {
            val skattemessigVerdi =
                forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) summerVerdiFraHverForekomst {
                    forekomstType.annenFordring.tall()
                }

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_annenFordring,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjellstypePositivSaldoPaaGevinstOgTapskontoKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum() && erIkkeSkjoennsfastsatt()) {
            val skattemessigVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erPositiv()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            }

            val gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverkUtgaaendeVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_utgaaendeVerdi.erPositiv()
            } summerVerdiFraHverForekomst {
                forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_utgaaendeVerdi.tall()
            }

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_positivSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi + gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverkUtgaaendeVerdi
            )
        }
    }

    internal val forskjellstypeNegativSaldoPaaGevinstOgTapskontoKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum() && erIkkeSkjoennsfastsatt()) {
            val skattemessigVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall().absoluttverdi()
            } medAntallDesimaler 2

            val gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverkUtgaaendeVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_utgaaendeVerdi.tall().absoluttverdi()
            }

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_negativSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi + gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverkUtgaaendeVerdi
            )
        }
    }

    internal val forskjellstypeBetingetSkattefriGevinstKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum() && erIkkeSkjoennsfastsatt()) {
            val skattemessigVerdi =
                forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_spesifikasjonAvBetingetSkattefriGevinst) summerVerdiFraHverForekomst {
                    forekomstType.betingetSkattefriGevinst.tall()
                } medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_betingetSkattefriGevinst,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjellstypePositivSaldoToemmerkontoKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum()) {
            val skattemessigVerdi = forekomsterAv(modell.skogbruk_skogOgToemmerkonto) der {
                forekomstType.utgaaendeVerdiPaaToemmerkonto.erPositiv()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdiPaaToemmerkonto.tall()
            }

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_positivSaldoToemmerkonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjellstypeNegativSaldoToemmerkontoKalkyle = kalkyle {
        hvis(fullRegnskapspliktOgIkkePetroleum()) {
            val skattemessigVerdi = forekomsterAv(modell.skogbruk_skogOgToemmerkonto) der {
                forekomstType.utgaaendeVerdiPaaToemmerkonto.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdiPaaToemmerkonto.tall().absoluttverdi()
            }

            oppdaterEllerOpprettForskjellstype(
                midlertidigForskjellstype.kode_negativSaldoToemmerkonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    internal val forskjell = opprettSyntetiskFelt(modell.midlertidigForskjell, "forskjell")
    internal val forskjellForrigeInntektsaar =
        opprettSyntetiskFelt(modell.midlertidigForskjell, "forskjellForrigeInntektsaar")
    internal val annenForskjell = opprettSyntetiskFelt(modell.midlertidigForskjell, "annenForskjell")

    internal val midlertidigeForskjellerTilleggKalkyle = kalkyle {
        val inntektsaar = inntektsaar

        hvis(fullRegnskapsplikt()) {
            forekomsterAv(modell.midlertidigForskjell) der {
                MidlertidigForskjellstype.erTillegg(
                    inntektsaar,
                    modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                )
            } forHverForekomst {
                hvis(
                    MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.regnskapsmessigVerdi - modell.midlertidigForskjell.skattemessigVerdi
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.regnskapsmessigVerdiForrigeInntektsaar -
                            modell.midlertidigForskjell.skattemessigVerdiForrigeInntektsaar
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erRegnskapmessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.regnskapsmessigVerdi.tall()
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.regnskapsmessigVerdiForrigeInntektsaar.tall()
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erSkattemessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.skattemessigVerdi.tall()
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.skattemessigVerdiForrigeInntektsaar.tall()
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erAnnenForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(annenForskjell) {
                        modell.midlertidigForskjell.regnskapsmessigVerdi.tall()
                    }

                    settFelt(annenForskjell) {
                        modell.midlertidigForskjell.skattemessigVerdi.tall()
                    }
                }
            }
        }
    }

    internal val midlertidigeForskjellerFradragKalkyle = kalkyle {
        val inntektsaar = inntektsaar

        hvis(fullRegnskapsplikt()) {
            forekomsterAv(modell.midlertidigForskjell) der {
                MidlertidigForskjellstype.erFradrag(
                    inntektsaar,
                    modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                )
            } forHverForekomst {
                hvis(
                    MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.skattemessigVerdi - modell.midlertidigForskjell.regnskapsmessigVerdi
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.skattemessigVerdiForrigeInntektsaar -
                            modell.midlertidigForskjell.regnskapsmessigVerdiForrigeInntektsaar
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erRegnskapmessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.regnskapsmessigVerdi * (-1)
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.regnskapsmessigVerdiForrigeInntektsaar * (-1)
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erSkattemessigForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(forskjell) {
                        modell.midlertidigForskjell.skattemessigVerdi * (-1)
                    }

                    settFelt(forskjellForrigeInntektsaar) {
                        modell.midlertidigForskjell.skattemessigVerdiForrigeInntektsaar * (-1)
                    }
                }

                hvis(
                    MidlertidigForskjellstype.erAnnenForskjell(
                        inntektsaar,
                        modell.midlertidigForskjell.midlertidigForskjellstype.verdi(),
                    )
                ) {
                    settFelt(annenForskjell) {
                        modell.midlertidigForskjell.regnskapsmessigVerdi * (-1)
                    }

                    settFelt(annenForskjell) {
                        modell.midlertidigForskjell.skattemessigVerdi * (-1)
                    }
                }
            }
        }
    }

    internal val sumEndringIForskjellKalkyle = kalkyle {
        hvis(fullRegnskapsplikt()) {
            val sumEndringIMidlertidigForskjell =
                forekomsterAv(modell.midlertidigForskjell) summerVerdiFraHverForekomst {
                    forskjellForrigeInntektsaar - forskjell + annenForskjell
                }
            if (inntektsaar.tekniskInntektsaar <= 2022) {
                settUniktFelt(modell2022.beregnetNaeringsinntekt_sumEndringIMidlertidigForskjell) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            } else {
                settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumEndringIMidlertidigForskjell) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            }
        }
    }

    internal val oevrigTilVisningAvMidlertidigForskjellKalkyle = kalkyle {
        hvis(fullRegnskapsplikt()) {
            forAlleForekomsterAv(modell.midlertidigForskjell) {
                settFelt(modell.midlertidigForskjell.oevrigTilVisningAvMidlertidigForskjell_forskjell) {
                    forskjell.tall().somHeltall()
                }
                settFelt(modell.midlertidigForskjell.oevrigTilVisningAvMidlertidigForskjell_forskjellForrigeInntektsaar) {
                    forskjellForrigeInntektsaar.tall().somHeltall()
                }
                settFelt(modell.midlertidigForskjell.oevrigTilVisningAvMidlertidigForskjell_endringIForskjell) {
                    (forskjellForrigeInntektsaar - forskjell).somHeltall()
                }
            }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        forskjellstypeDriftsmiddelOgGoodwillKalkyle,
        forskjellstypeVarebeholdingOgBiologiskEiendelKalkyle,
        forskjellstypeKundefordringKalkyle,
        forskjellstypeAnnenFordringKalkyle,
        forskjellstypePositivSaldoPaaGevinstOgTapskontoKalkyle,
        forskjellstypeNegativSaldoPaaGevinstOgTapskontoKalkyle,
        forskjellstypeBetingetSkattefriGevinstKalkyle,
        forskjellstypePositivSaldoToemmerkontoKalkyle,
        forskjellstypeNegativSaldoToemmerkontoKalkyle,
        midlertidigeForskjellerTilleggKalkyle,
        midlertidigeForskjellerFradragKalkyle,
        sumEndringIForskjellKalkyle,
        oevrigTilVisningAvMidlertidigForskjellKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }

    private fun GeneriskModellKontekst.oppdaterEllerOpprettForskjellstype(
        forskjellstype: KodeVerdi,
        regnskapmessigVerdi: BigDecimal? = null,
        skattemessigVerdi: BigDecimal? = null,
    ) {
        val midlertidigForskjellForekomst = finnForekomstMed(modell.midlertidigForskjell) {
            modell.midlertidigForskjell.midlertidigForskjellstype lik forskjellstype
        }

        if (midlertidigForskjellForekomst.eksisterer()) {
            oppdaterForekomst(midlertidigForskjellForekomst) {
                settFelt(modell.midlertidigForskjell.regnskapsmessigVerdi) { regnskapmessigVerdi }
                settFelt(modell.midlertidigForskjell.skattemessigVerdi) { skattemessigVerdi }
            }
        } else if (regnskapmessigVerdi.harVerdi() || skattemessigVerdi.harVerdi()) {
            opprettNyForekomstAv(modell.midlertidigForskjell) {
                medFelt(
                    modell.midlertidigForskjell.midlertidigForskjellstype,
                    forskjellstype.kode
                )
                medFelt(modell.midlertidigForskjell.regnskapsmessigVerdi) { regnskapmessigVerdi }
                medFelt(modell.midlertidigForskjell.skattemessigVerdi) { skattemessigVerdi }
            }
        }
    }
}
