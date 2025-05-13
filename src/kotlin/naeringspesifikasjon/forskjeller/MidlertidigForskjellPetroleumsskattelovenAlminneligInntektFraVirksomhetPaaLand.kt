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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.MidlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaLand : HarKalkylesamling {

    internal val forskjellbeloepAlminneligInntektFraVirksomhetPaaLand = opprettSyntetiskFelt(
        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
        "forskjellbeloepAlminneligInntektFraVirksomhetPaaLand"
    )
    internal val forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand"
        )
    internal val annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand"
        )

    internal val midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaLandTilleggKalkyle =
        kalkyle("midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaLandTilleggKalkyle") {
            val inntektsaar = inntektsaar

            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) der {
                    MidlertidigForskjellstype.erTillegg(
                        inntektsaar,
                        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                    )
                } forHverForekomst {
                    hvis(
                        MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi.tall()
                        }

                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi.tall()
                        }
                    }
                }
            }
        }
    internal val midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaLandFradragKalkyle =
        kalkyle("midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaLandFradragKalkyle") {
            val inntektsaar = inntektsaar

            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) der {
                    MidlertidigForskjellstype.erFradrag(
                        inntektsaar,
                        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                    )
                } forHverForekomst {
                    hvis(
                        MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi * (-1)
                        }
                    }
                }
            }
        }

    internal val fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaLand") {
            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                val sumEndringIMidlertidigForskjell =
                    forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand -
                            forskjellbeloepAlminneligInntektFraVirksomhetPaaLand +
                            annenForskjellbeloepAlminneligInntektFraVirksomhetPaaLand
                    }

                settUniktFelt(modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltEndringIMidlertidigForskjell_beloepAlminneligInntektFraVirksomhetPaaLand) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            }
        }

    internal val alminneligInntektFraVirksomhetPaaLand_forskjell_forskjellForrigeInntektsaar_endringIForskjell =
        kalkyle {
            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
            forAlleForekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_forskjell) {
                        forskjellbeloepAlminneligInntektFraVirksomhetPaaLand.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_forskjellForrigeInntektsaar) {
                        forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_endringIForskjell) {
                        (forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaLand - forskjellbeloepAlminneligInntektFraVirksomhetPaaLand).somHeltall()
                    }
                }
            }
        }

    private val petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleAlminneligInntektPaaLand = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
            val utgaaendeVerdiSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erUsann()) {
                        if (forekomstType.saldogruppe lik saldogruppe.kode_i) {
                            forekomstType.utgaaendeVerdi -
                                forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning medMinimumsverdi 0
                        } else {
                            forekomstType.utgaaendeVerdi.tall()
                        }
                    } else {
                        BigDecimal.ZERO
                    }
                }

            val utgaaendeVerdiLineartavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erUsann()) {
                        forekomstType.utgaaendeVerdi.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }

            val utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erUsann()) {
                        forekomstType.utgaaendeVerdi.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }

            val skattemessigVerdi = utgaaendeVerdiSaldoavskrevetAnleggsmiddel +
                utgaaendeVerdiLineartavskrevetAnleggsmiddel +
                utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagLand(
                midlertidigForskjellstype.kode_driftsmiddelOgGoodwill,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private val positivSaldoPaaGevinstOgTapskontoLand = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
            val utgaaendeVerdiGevinstOgTapskonto =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.utgaaendeVerdi stoerreEnn BigDecimal.ZERO &&
                    forekomstType.gjelderVirksomhetPaaSokkel.erUsann()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagLand(
                midlertidigForskjellstype.kode_positivSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private val negativSaldoPaaGevinstOgTapskontoAlminneligLand = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
            val utgaaendeVerdiGevinstOgTapskonto =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.utgaaendeVerdi mindreEnn BigDecimal.ZERO &&
                    forekomstType.gjelderVirksomhetPaaSokkel.erUsann()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto.absoluttverdi()

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagLand(
                midlertidigForskjellstype.kode_negativSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private fun GeneriskModellKontekst.oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagLand(
        forskjellstype: KodeVerdi,
        regnskapmessigVerdi: BigDecimal? = null,
        skattemessigVerdi: BigDecimal? = null,
    ) {
        val forekomst =
            finnForekomstMed(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype lik forskjellstype
            }

        if (forekomst.eksisterer()) {
            oppdaterForekomst(forekomst) {
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi) { regnskapmessigVerdi }
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi) { skattemessigVerdi }
            }
        } else if (regnskapmessigVerdi.harVerdi() || skattemessigVerdi.harVerdi()) {
            opprettNyForekomstAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                medFelt(
                    modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype,
                    forskjellstype.kode
                )
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_regnskapsmessigVerdi) { regnskapmessigVerdi }
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaLand_skattemessigVerdi) { skattemessigVerdi }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleAlminneligInntektPaaLand,
            positivSaldoPaaGevinstOgTapskontoLand,
            negativSaldoPaaGevinstOgTapskontoAlminneligLand,
            midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaLandTilleggKalkyle,
            midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaLandFradragKalkyle,
            fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaLand,
            alminneligInntektFraVirksomhetPaaLand_forskjell_forskjellForrigeInntektsaar_endringIForskjell
        )
    }
}