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

internal object MidlertidigForskjellPetroleumsskattelovenSaerskattegrunnlagFraVirksomhetPaaSokkel :
    HarKalkylesamling {

    internal val forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel = opprettSyntetiskFelt(
        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
        "forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel"
    )
    internal val forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel"
        )
    internal val annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel"
        )

    internal val midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelTilleggKalkyle =
        kalkyle("midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelTilleggKalkyle") {
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
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi.tall()
                        }

                        settFelt(annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi.tall()
                        }
                    }
                }
            }
        }
    internal val midlertidigeForskjellerBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelFradragKalkyle =
        kalkyle("midlertidigeForskjellerBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelFradragKalkyle") {
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
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi - modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi * (-1)
                        }
                    }
                }
            }
        }

    internal val fordeltEndringIMidlertidigForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltEndringIMidlertidigForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                val sumEndringIMidlertidigForskjell =
                    forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel -
                            forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                            annenForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel
                    }

                settUniktFelt(modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltEndringIMidlertidigForskjell_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            }
        }

    internal val saerskattegrunnlagFraVirksomhetPaaSokkel_forskjell_forskjellForrigeInntektsaar_endringIForskjell =
        kalkyle {
            hvis(fullRegnskapsplikt()) {
                forAlleForekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_forskjell) {
                        forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_forskjellForrigeInntektsaar) {
                        forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall().somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_endringIForskjell) {
                        (forskjellForrigeInntektsaarBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel - forskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel).somHeltall()
                    }
                }
            }
        }

    private val petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleSaerskattegrunnlag = kalkyle {
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
                    forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }

            val gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }

            val skattemessigVerdi = utgaaendeVerdiSaldoavskrevetAnleggsmiddel +
                utgaaendeVerdiLineartavskrevetAnleggsmiddel +
                utgaaendeVerdiIkkeAvskrivbartAnleggsmiddel +
                utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel -
                gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven medAntallDesimaler 2

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagFraVirksomhetPaaSokkel(
                midlertidigForskjellstype.kode_driftsmiddelOgGoodwill,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private val positivSaldoPaaGevinstOgTapskontoSaerskattegrunnlag = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val utgaaendeVerdiGevinstOgTapskonto =
                positiveUtgaaendeVerdierGevinstOgTapskonto()

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagFraVirksomhetPaaSokkel(
                midlertidigForskjellstype.kode_positivSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private val negativSaldoPaaGevinstOgTapskontoSaerskattegrunnlag = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val utgaaendeVerdiGevinstOgTapskonto =
                negativeUtgaaendeVerdierGevinstOgTapskonto()

            val skattemessigVerdi = utgaaendeVerdiGevinstOgTapskonto

            oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagFraVirksomhetPaaSokkel(
                midlertidigForskjellstype.kode_negativSaldoPaaGevinstOgTapskonto,
                skattemessigVerdi = skattemessigVerdi
            )
        }
    }

    private fun GeneriskModellKontekst.oppdaterEllerOpprettForskjellstypePetroleumSaerskattegrunnlagFraVirksomhetPaaSokkel(
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
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi) { regnskapmessigVerdi }
                settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi) { skattemessigVerdi }
            }
        } else if (regnskapmessigVerdi.harVerdi() || skattemessigVerdi.harVerdi()) {
            opprettNyForekomstAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                medFelt(
                    modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype,
                    forskjellstype.kode
                )
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_regnskapsmessigVerdi) { regnskapmessigVerdi }
                medFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilSaerskattegrunnlagFraVirksomhetPaaSokkel_skattemessigVerdi) { skattemessigVerdi }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            petroleumForskjellstypeDriftsmiddelOgGoodwillKalkyleSaerskattegrunnlag,
            positivSaldoPaaGevinstOgTapskontoSaerskattegrunnlag,
            negativSaldoPaaGevinstOgTapskontoSaerskattegrunnlag,
            midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelTilleggKalkyle,
            midlertidigeForskjellerBeloepSaerskattegrunnlagFraVirksomhetPaaSokkelFradragKalkyle,
            fordeltEndringIMidlertidigForskjellBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
            saerskattegrunnlagFraVirksomhetPaaSokkel_forskjell_forskjellForrigeInntektsaar_endringIForskjell
        )
    }
}