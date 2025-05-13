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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PetroleumUtil.negativeUtgaaendeVerdierGevinstOgTapskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PetroleumUtil.positiveUtgaaendeVerdierGevinstOgTapskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.MidlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaSokkel :
    HarKalkylesamling {

    internal val forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel = opprettSyntetiskFelt(
        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
        "forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel"
    )
    internal val forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel"
        )
    internal val annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel"
        )

    internal val midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaSokkelTilleggKalkyle =
        kalkyle("midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaSokkelTilleggKalkyle") {
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
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi.tall()
                        }

                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi.tall()
                        }
                    }
                }
            }
        }
    internal val midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaSokkelFradragKalkyle =
        kalkyle("midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaSokkelFradragKalkyle") {
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
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi - modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi * (-1)
                        }
                    }
                }
            }
        }

    internal val fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel") {
            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                val sumEndringIMidlertidigForskjell =
                    forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel -
                            forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel +
                            annenForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel
                    }

                settUniktFelt(modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltEndringIMidlertidigForskjell_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            }
        }

    internal val alminneligInntektFraVirksomhetPaaSokkel_forskjell_forskjellForrigeInntektsaar_endringIForskjell =
        kalkyle {
            hvis(fullRegnskapsplikt()) {
                forAlleForekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_forskjell) {
                        forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_forskjellForrigeInntektsaar) {
                        forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_endringIForskjell) {
                        (forskjellForrigeInntektsaarbeloepAlminneligInntektFraVirksomhetPaaSokkel - forskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel).somHeltall()
                    }
                }
            }
        }

    private val petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleAlminneligInntektSokkel = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
            val utgaaendeVerdiSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erSann()) {
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
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erSann()) {
                        forekomstType.utgaaendeVerdi.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }

            val utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                    if (forekomstType.gjelderVirksomhetPaaSokkel.erSann()) {
                        forekomstType.utgaaendeVerdi.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }

            val utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }

            val gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }

            val skattemessigVerdi = utgaaendeVerdiSaldoavskrevetAnleggsmiddel +
                utgaaendeVerdiLineartavskrevetAnleggsmiddel +
                utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel +
                utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel -
                gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstypePetroleumAlminneligInntekt(
                midlertidigForskjellstype.kode_driftsmiddelOgGoodwill,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }
    private val positivSaldoPaaGevinstOgTapskontoAlminneligSokkel = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val utgaaendeVerdiGevinstOgTapskonto =
                positiveUtgaaendeVerdierGevinstOgTapskonto()

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto

            oppdaterEllerOpprettForskjellstypePetroleumAlminneligInntekt(
                midlertidigForskjellstype.kode_positivSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private val negativSaldoPaaGevinstOgTapskontoAlminneligSokkel = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val utgaaendeVerdiGevinstOgTapskonto =
                negativeUtgaaendeVerdierGevinstOgTapskonto()

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto

            oppdaterEllerOpprettForskjellstypePetroleumAlminneligInntekt(
                midlertidigForskjellstype.kode_negativSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }


    private fun GeneriskModellKontekst.oppdaterEllerOpprettForskjellstypePetroleumAlminneligInntekt(
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
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi) { regnskapmessigVerdi }
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi) { skattemessigVerdi }
            }
        } else if (regnskapmessigVerdi.harVerdi() || skattemessigVerdi.harVerdi()) {
            opprettNyForekomstAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                medFelt(
                    modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype,
                    forskjellstype.kode
                )
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_regnskapsmessigVerdi) { regnskapmessigVerdi }
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel_skattemessigVerdi) { skattemessigVerdi }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleAlminneligInntektSokkel,
            positivSaldoPaaGevinstOgTapskontoAlminneligSokkel,
            negativSaldoPaaGevinstOgTapskontoAlminneligSokkel,
            midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepAlminneligInntektFraVirksomhetPaaSokkelTilleggKalkyle,
            midlertidigeForskjellerbeloepAlminneligInntektFraVirksomhetPaaSokkelFradragKalkyle,
            fordeltEndringIMidlertidigForskjellbeloepAlminneligInntektFraVirksomhetPaaSokkel,
            alminneligInntektFraVirksomhetPaaSokkel_forskjell_forskjellForrigeInntektsaar_endringIForskjell
        )
    }
}