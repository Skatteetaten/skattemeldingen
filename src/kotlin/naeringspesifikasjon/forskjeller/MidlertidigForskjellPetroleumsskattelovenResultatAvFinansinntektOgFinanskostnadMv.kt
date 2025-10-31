package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.MidlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object MidlertidigForskjellPetroleumsskattelovenResultatAvFinansinntektOgFinanskostnadMv :
    HarKalkylesamling {

    internal val forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv = opprettSyntetiskFelt(
        modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
        "forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv"
    )
    internal val forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv"
        )
    internal val annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv =
        opprettSyntetiskFelt(
            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven,
            "annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv"
        )

    internal val midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepResultatAvFinansinntektOgFinanskostnadMvTilleggKalkyle =
        kalkyle("midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepResultatAvFinansinntektOgFinanskostnadMvTilleggKalkyle") {
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
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi.tall()
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdiForrigeInntektsaar.tall()
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi.tall()
                        }

                        settFelt(annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi.tall()
                        }
                    }
                }
            }
        }
    internal val midlertidigeForskjellerbeloepResultatAvFinansinntektOgFinanskostnadMvFradragKalkyle =
        kalkyle("midlertidigeForskjellerbeloepResultatAvFinansinntektOgFinanskostnadMvFradragKalkyle") {
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
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi - modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdiForrigeInntektsaar -
                                modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdiForrigeInntektsaar
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erRegnskapmessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erSkattemessigForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi * (-1)
                        }

                        settFelt(forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdiForrigeInntektsaar * (-1)
                        }
                    }

                    hvis(
                        MidlertidigForskjellstype.erAnnenForskjell(
                            inntektsaar,
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellstype.verdi(),
                        )
                    ) {
                        settFelt(annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_regnskapsmessigVerdi * (-1)
                        }

                        settFelt(annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv) {
                            modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_skattemessigVerdi * (-1)
                        }
                    }
                }
            }
        }

    internal val fordeltEndringIMidlertidigForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltEndringIMidlertidigForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv") {
            hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {
                val sumEndringIMidlertidigForskjell =
                    forekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv -
                            forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv +
                            annenForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv
                    }

                settUniktFelt(modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltEndringIMidlertidigForskjell_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                    sumEndringIMidlertidigForskjell.medToDesimaler()
                }
            }
        }

    internal val finansinntektOgFinanskostnad_forskjell_forskjellForrigeInntektsaar_endringIForskjell =
        kalkyle {
            hvis(fullRegnskapsplikt()) {
                forAlleForekomsterAv(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_forskjell) {
                        forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv.tall()
                            .somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_forskjellForrigeInntektsaar) {
                        forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv.tall()
                            .somHeltall()
                    }
                    settFelt(modell.midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.midlertidigForskjellKnyttetTilFinansinntektOgFinanskostnad_endringIForskjell) {
                        (forskjellForrigeInntektsaarbeloepResultatAvFinansinntektOgFinanskostnadMv - forskjellbeloepResultatAvFinansinntektOgFinanskostnadMv).somHeltall()
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            midlertidigForskjellForVirksomhetOmfattetAvPetroleumsskattelovenbeloepResultatAvFinansinntektOgFinanskostnadMvTilleggKalkyle,
            midlertidigeForskjellerbeloepResultatAvFinansinntektOgFinanskostnadMvFradragKalkyle,
            fordeltEndringIMidlertidigForskjellbeloepResultatAvFinansinntektOgFinanskostnadMv,
            finansinntektOgFinanskostnad_forskjell_forskjellForrigeInntektsaar_endringIForskjell
        )
    }
}