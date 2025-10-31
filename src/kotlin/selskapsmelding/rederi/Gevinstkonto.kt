package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.prosent
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object Gevinstkonto : HarKalkylesamling {

    private val grunnlagForAaretsInntektsfoering = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val differanse = modell.rederiskatteordning_gevinstkonto.markedsverdiVedKonserninterntErvervAvAndel.tall() -
                modell.rederiskatteordning_gevinstkonto.skattemessigVerdiVedKonserninterntErvervAvAndel.tall()
            hvis(differanse stoerreEllerLik 0) {
                val skattepliktigGevinstVedInntredenIOrdningen =
                    modell.rederiskatteordning_inntredenIOrdningen.skattepliktigGevinstVedInntredenIOrdningen.tall()
                val hjelpeBeregning =
                    forekomsterAv(modell.rederiskatteordning_gevinstkonto) summerVerdiFraHverForekomst {
                        forekomstType.inngaaendeVerdi +
                            (forekomstType.markedsverdiVedKonserninterntErvervAvAndel
                                - forekomstType.skattemessigVerdiVedKonserninterntErvervAvAndel)
                    }
                val grunnlagForAaretsInntektsfoering = skattepliktigGevinstVedInntredenIOrdningen + hjelpeBeregning
                settUniktFelt(modell.rederiskatteordning_gevinstkonto.grunnlagForAaretsInntektsfoering) {
                    (grunnlagForAaretsInntektsfoering medMinimumsverdi 0).somHeltall()
                }
            }
            hvis(differanse mindreEnn 0) {
                settUniktFelt(modell.rederiskatteordning_gevinstkonto.grunnlagForAaretsInntektsfoering) {
                    (modell.rederiskatteordning_gevinstkonto.inngaaendeVerdi +
                        modell.rederiskatteordning_inntredenIOrdningen.skattepliktigGevinstVedInntredenIOrdningen).somHeltall()
                }
            }
        }
    }

    private val inntektsfoeringAvGevinstkonto = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            hvis(modell.rederiskatteordning_gevinstkonto.grunnlagForAaretsInntektsfoering mindreEnn 15000) {
                settUniktFelt(modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto) {
                    forekomsterAv(modell.rederiskatteordning_gevinstkonto) summerVerdiFraHverForekomst {
                        modell.rederiskatteordning_gevinstkonto.grunnlagForAaretsInntektsfoering.tall().somHeltall()
                    }
                }
            }
            hvis(modell.rederiskatteordning_gevinstkonto.grunnlagForAaretsInntektsfoering stoerreEllerLik 15000) {
                var satsMinst20 =
                    modell.rederiskatteordning_gevinstkonto.satsForInntektsfoering.tall() medMinimumsverdi 20 medMaksimumsverdi 100
                if (satsMinst20 == null) {
                    satsMinst20 = BigDecimal(20)
                }
                settUniktFelt(modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto) {
                    forekomsterAv(modell.rederiskatteordning_gevinstkonto) summerVerdiFraHverForekomst {
                        (forekomstType.grunnlagForAaretsInntektsfoering * satsMinst20.prosent()).somHeltall()
                    }
                }
            }
        }
    }

    private val utgaaendeVerdi = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_gevinstkonto.utgaaendeVerdi) {
                forekomsterAv(modell.rederiskatteordning_gevinstkonto) summerVerdiFraHverForekomst {
                    (forekomstType.grunnlagForAaretsInntektsfoering -
                        forekomstType.inntektsfoeringAvGevinstkonto medMinimumsverdi 0).somHeltall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            grunnlagForAaretsInntektsfoering,
            inntektsfoeringAvGevinstkonto,
            utgaaendeVerdi
        )
    }
}