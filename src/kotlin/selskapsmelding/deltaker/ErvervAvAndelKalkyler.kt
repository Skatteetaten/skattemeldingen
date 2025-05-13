package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV1

object ErvervAvAndelKalkyler : HarKalkylesamling {

    internal val kostprisVedErvervAvAndelKalkyle = kalkyle {
        forekomsterAv(modellV1.deltaker.ervervAvAndel) forHverForekomst {
            val kostpris = forekomstType.vederlag +
                forekomstType.kjoepskostnad -
                forekomstType.overtattNegativInngangsverdiForAndelSomArvEllerGave +
                forekomstType.overtattInngangsverdiForAndelSomArvEllerGave

            if (kostpris stoerreEllerLik 0) {
                settFelt(forekomstType.kostprisVedErvervAvAndel) { kostpris }
            } else {
                settFelt(forekomstType.negativKostprisVedErvervAvAndel) {
                    kostpris.absoluttverdi()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            kostprisVedErvervAvAndelKalkyle
        )
    }
}