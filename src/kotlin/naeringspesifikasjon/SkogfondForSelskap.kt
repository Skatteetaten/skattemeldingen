package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skogfond_skattefriAndelAvSkogfond
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object SkogfondForSelskap : HarKalkylesamling {

    internal val skattefordelAvSkogfondKalkyle = kalkyle("skattefordelAvSkogfond") {
        val satser = satser!!
        forAlleForekomsterAv(modell.skogbruk_skogfondForSelskap) {
            settFelt(forekomstType.skattefordelAvSkogfond) {
                (forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkonto
                    ).times(satser.sats(skogfond_skattefriAndelAvSkogfond)) medMinimumsverdi 0
            }
        }
    }

    internal val tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle =
        kalkyle("tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto") {
            val satser = satser!!
            forAlleForekomsterAv(modell.skogbruk_skogfondForSelskap) {
                settFelt(forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto) {
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkontoTilInvesteringForetattTidligereAar.times(
                        satser.sats(skogfond_skattefriAndelAvSkogfond)
                    )
                }
            }
        }

    internal val skattepliktigInntektAvSkogfondKalkyle =
        kalkyle("skattepliktigInntektAvSkogfond") {
            forAlleForekomsterAv(modell.skogbruk_skogfondForSelskap) {
                settFelt(forekomstType.skattepliktigInntektAvSkogfond) {
                    forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                        forekomstType.skattefordelAvSkogfond +
                        forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto +
                        forekomstType.utbetaltFraSkogfondkontoTilFormaalUtenSkattefordel
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattefordelAvSkogfondKalkyle,
            tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle,
            skattepliktigInntektAvSkogfondKalkyle
        )
    }
}