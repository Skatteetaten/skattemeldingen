package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object InntredenIOrdningen : HarKalkylesamling {

    private val skattepliktigGevinstVedi = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntredenIOrdningen.skattepliktigGevinstVedInntredenIOrdningen) {
                forekomsterAv(modell.rederiskatteordning_inntredenIOrdningen) summerVerdiFraHverForekomst {
                    (forekomstType.markedsverdiForSkip - forekomstType.saldoverdiForSkip) +
                        (forekomstType.markedsverdiForAksjeAndelISdf - forekomstType.skattemessigVerdiForAksjeAndelISdf) +
                        (forekomstType.markedsverdiForUtstyrMv - forekomstType.saldoverdiForUtstyrMv) +
                        (forekomstType.oevrigLatentGevinst - forekomstType.oevrigLatentTap) +
                        (forekomstType.positivGevinstOgTapskonto - forekomstType.negativGevinstOgTapskonto) -
                        forekomstType.underskuddTilFremfoering medMinimumsverdi 0
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattepliktigGevinstVedi
        )
    }
}