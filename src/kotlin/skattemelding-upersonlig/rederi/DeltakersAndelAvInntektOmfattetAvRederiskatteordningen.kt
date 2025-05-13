package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.rederi

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object DeltakersAndelAvInntektOmfattetAvRederiskatteordningen : HarKalkylesamling {

    internal val andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenTilFremfoering =
        kalkyle("andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenTilFremfoering") {
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) forHverForekomst {
                settFelt(forekomstType.andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenTilFremfoering) {
                    forekomstType.aaretsFremfoerteDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttreden -
                        forekomstType.andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenBenyttetIInntektsaaret
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenTilFremfoering)
    }
}