package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.MidlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object MidlertidigeForskjellerBeregning2021 : HarKalkylesamling {

    val sumEndringIMidlertidigForskjellKalkyle = kalkyle("sumEndringIMidlertidigForskjell") {
        settUniktFelt(modell2021.beregnetNaeringsinntekt_sumEndringIMidlertidigForskjell) {
            forekomsterAv(modell2021.midlertidigForskjell) summerVerdiFraHverForekomst {
                (MidlertidigeForskjellerIFjor2021.forskjellIFjor -
                    MidlertidigeForskjellerIAar2021.forskjellIAar +
                    AndreMidlertidigeForskjeller2021.tillegg -
                    AndreMidlertidigeForskjeller2021.fradrag) medAntallDesimaler 2
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            MidlertidigeForskjellerIFjor2021.regnskapOgSkattemessigeForskjellerTillegg,
            MidlertidigeForskjellerIFjor2021.regnskapOgSkattemessigeForskjellerFradrag,
            MidlertidigeForskjellerIFjor2021.regnskapsmessigeForskjellerTillegg,
            MidlertidigeForskjellerIFjor2021.regnskapsmessigeForskjellerFradrag,
            MidlertidigeForskjellerIFjor2021.skattemessigeForskjellerTillegg,
            MidlertidigeForskjellerIFjor2021.skattemessigeForskjellerFradrag,
            MidlertidigeForskjellerIAar2021.regnskapOgSkattemessigeForskjellerTillegg,
            MidlertidigeForskjellerIAar2021.regnskapsmessigeForskjellerTillegg,
            MidlertidigeForskjellerIAar2021.skattemessigeForskjellerTillegg,
            MidlertidigeForskjellerIAar2021.regnskapOgSkattemessigeForskjellerFradrag,
            MidlertidigeForskjellerIAar2021.regnskapsmessigeForskjellerFradrag,
            MidlertidigeForskjellerIAar2021.skattemessigeForskjellerFradrag,
            AndreMidlertidigeForskjeller2021.regnskapEllerSkattemessigeForskjellerTillegg,
            AndreMidlertidigeForskjeller2021.regnskapEllerSkattemessigeForskjellerFradrag,
            sumEndringIMidlertidigForskjellKalkyle,
        )
    }
}

internal object MidlertidigeForskjellerIAar2021 {

    val forskjellIAar = opprettSyntetiskFelt(modell2021.midlertidigForskjell, "forskjellIAar")

    val regnskapOgSkattemessigeForskjellerTillegg = kalkyle("regnskapOgSkattemessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.regnskapsmessigVerdi - forekomstType.skattemessigVerdi }
        }
    }

    val regnskapsmessigeForskjellerTillegg = kalkyle("regnskapsmessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapmessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.regnskapsmessigVerdi.tall() }
        }
    }

    val skattemessigeForskjellerTillegg = kalkyle("skattemessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.skattemessigVerdi.tall() }
        }
    }

    val regnskapOgSkattemessigeForskjellerFradrag = kalkyle("regnskapOgSkattemessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.skattemessigVerdi - forekomstType.regnskapsmessigVerdi }
        }
    }

    val regnskapsmessigeForskjellerFradrag = kalkyle("regnskapsmessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapmessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.regnskapsmessigVerdi * -1 }
        }
    }

    val skattemessigeForskjellerFradrag = kalkyle("skattemessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIAar) { forekomstType.skattemessigVerdi * -1 }
        }
    }
}

internal object MidlertidigeForskjellerIFjor2021 {

    val forskjellIFjor = opprettSyntetiskFelt(modell2021.midlertidigForskjell, "forskjellIFjor")

    val regnskapOgSkattemessigeForskjellerTillegg = kalkyle("regnskapOgSkattemessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.regnskapsmessigVerdiForrigeInntektsaar - forekomstType.skattemessigVerdiForrigeInntektsaar }
        }
    }

    val regnskapsmessigeForskjellerTillegg = kalkyle("regnskapsmessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapmessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.regnskapsmessigVerdiForrigeInntektsaar.tall() }
        }
    }

    val skattemessigeForskjellerTillegg = kalkyle("skattemessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.skattemessigVerdiForrigeInntektsaar.tall() }
        }
    }

    val regnskapOgSkattemessigeForskjellerFradrag = kalkyle("regnskapOgSkattemessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapOgSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.skattemessigVerdiForrigeInntektsaar - forekomstType.regnskapsmessigVerdiForrigeInntektsaar }
        }
    }

    val regnskapsmessigeForskjellerFradrag = kalkyle("regnskapsmessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erRegnskapmessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.regnskapsmessigVerdiForrigeInntektsaar * -1 }
        }
    }

    val skattemessigeForskjellerFradrag = kalkyle("skattemessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erSkattemessigForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(forskjellIFjor) { forekomstType.skattemessigVerdiForrigeInntektsaar * -1 }
        }
    }
}

internal object AndreMidlertidigeForskjeller2021 {

    val tillegg = opprettSyntetiskFelt(modell2021.midlertidigForskjell, "andreForskjellerTillegg")
    val fradrag = opprettSyntetiskFelt(modell2021.midlertidigForskjell, "andreForskjellerFradrag")

    val regnskapEllerSkattemessigeForskjellerTillegg = kalkyle("regnskapEllerSkattemessigeForskjellerTillegg") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erTillegg(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erAnnenForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(tillegg) { forekomstType.regnskapsmessigVerdi.tall() ?: forekomstType.skattemessigVerdi.tall() }
        }
    }

    val regnskapEllerSkattemessigeForskjellerFradrag = kalkyle("regnskapEllerSkattemessigeForskjellerFradrag") {
        val inntektsaar = inntektsaar
        forekomsterAv(modell2021.midlertidigForskjell) der {
            MidlertidigForskjellstype.erFradrag(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            ) && MidlertidigForskjellstype.erAnnenForskjell(
                inntektsaar,
                forekomstType.midlertidigForskjellstype.verdi()
            )
        } forHverForekomst {
            settFelt(fradrag) { forekomstType.regnskapsmessigVerdi.tall() ?: forekomstType.skattemessigVerdi.tall() }
        }
    }
}
