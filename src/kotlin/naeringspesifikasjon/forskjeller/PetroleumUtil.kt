package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

object PetroleumUtil {
    fun GeneriskModellKontekst.positiveUtgaaendeVerdierGevinstOgTapskonto(): BigDecimal? {
        val utgaaendeVerdiGevinstOgTapskonto =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi stoerreEnn BigDecimal.ZERO &&
                forekomstType.gjelderVirksomhetPaaSokkel.erSann()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            }
        return utgaaendeVerdiGevinstOgTapskonto
    }

    fun GeneriskModellKontekst.negativeUtgaaendeVerdierGevinstOgTapskonto(): BigDecimal? {
        val utgaaendeVerdiGevinstOgTapskonto =
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi mindreEnn BigDecimal.ZERO &&
                    forekomstType.gjelderVirksomhetPaaSokkel.erSann()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            }
        return utgaaendeVerdiGevinstOgTapskonto.absoluttverdi()
    }
}