package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

internal object BalanseregnskapImmateriellEiendelOgVarigDriftsmiddelForSelskapOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    internal val immateriellEiendelOgVarigDriftsmiddelForSelskapOmfattetAvPetroleumsskatteloven =
        kalkyle("immateriellEiendelOgVarigDriftsmiddelForSelskapOmfattetAvPetroleumsskatteloven") {
            val inntektsaar = inntektsaar
            forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_immateriellEiendelOgVarigAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
                settFelt(forekomstType.utgaaendeVerdi) {
                    if (inntektsaar.tekniskInntektsaar >= 2024 && forekomstType.type lik "utsattSkattefordel") {
                        forekomstType.inngaaendeVerdi +
                            forekomstType.endringIUtsattSkattEllerSkattefordel
                    } else {
                        forekomstType.inngaaendeVerdi +
                            forekomstType.tilgang -
                            forekomstType.avgang +
                            forekomstType.omklassifisering -
                            forekomstType.aaretsAvskrivning -
                            forekomstType.nedskrivning
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            immateriellEiendelOgVarigDriftsmiddelForSelskapOmfattetAvPetroleumsskatteloven
        )
    }
}