package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Egenkapitalendringstype.erFradrag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Egenkapitalendringstype.erTillegg
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object EgenkapitalavstemmingBeregning : HarKalkylesamling {
    internal val sumTilleggIEgenkapitalKalkyle = kalkyle("sumTilleggIEgenkapital") {
        val inntektsaar = this.inntektsaar
        settUniktFelt(modell.egenkapitalavstemming.sumTilleggIEgenkapital) {
            forekomsterAv(modell.egenkapitalavstemming.egenkapitalendring) der {
                forekomstType.egenkapitalendringstype.verdi().erTillegg(inntektsaar)
            } summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    internal val sumFradragIEgenkapitalKalkyle = kalkyle("sumFradragIEgenkapital") {
        val inntektsaar = this.inntektsaar
        settUniktFelt(modell.egenkapitalavstemming.sumFradragIEgenkapital) {
            forekomsterAv(modell.egenkapitalavstemming.egenkapitalendring) der {
                forekomstType.egenkapitalendringstype.verdi().erFradrag(inntektsaar)
            } summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    fun GeneriskModellKontekst.sumNettoPrinsippendring(): BigDecimal? {
        val sumPositivPrinsippEndring =
            forekomsterAv(modell.egenkapitalavstemming.prinsippendring) summerVerdiFraHverForekomst {
                forekomstType.positivPrinsippendring.tall()
            }

        val sumNegativPrinsippEndring =
            forekomsterAv(modell.egenkapitalavstemming.prinsippendring) summerVerdiFraHverForekomst {
                forekomstType.negativPrinsippendring.tall()
            }

        return sumPositivPrinsippEndring -
            sumNegativPrinsippEndring
    }

    val sumNettoPositivEllerNegativPrinsippendringKalkyle = kalkyle {
        val sumNettoPositivEllerNegativPrinsippendring =
            sumNettoPrinsippendring() -
                modell.egenkapitalavstemming.utsattSkatt +
                modell.egenkapitalavstemming.utsattSkattefordel
        if (sumNettoPositivEllerNegativPrinsippendring stoerreEllerLik 0) {
            settUniktFelt(modell.egenkapitalavstemming.sumNettoPositivPrinsippendring)
            { sumNettoPositivEllerNegativPrinsippendring }
        } else {
            settUniktFelt(modell.egenkapitalavstemming.sumNettoNegativPrinsippendring)
            { sumNettoPositivEllerNegativPrinsippendring.absoluttverdi() }
        }
    }

    val utgaaendeEgenkapitalKalkyle = kalkyle {
        settUniktFelt(modell.egenkapitalavstemming.utgaaendeEgenkapital) {
            modell.egenkapitalavstemming.inngaaendeEgenkapital +
                modell.egenkapitalavstemming.sumNettoPositivPrinsippendring -
                modell.egenkapitalavstemming.sumNettoNegativPrinsippendring +
                modell.egenkapitalavstemming.sumTilleggIEgenkapital -
                modell.egenkapitalavstemming.sumFradragIEgenkapital
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            sumTilleggIEgenkapitalKalkyle,
            sumFradragIEgenkapitalKalkyle,
            sumNettoPositivEllerNegativPrinsippendringKalkyle,
            utgaaendeEgenkapitalKalkyle
        )
    }
}