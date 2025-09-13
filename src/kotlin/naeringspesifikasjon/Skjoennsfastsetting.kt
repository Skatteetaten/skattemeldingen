package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal fun GeneriskModellKontekst.erIkkeSkjoennsfastsatt(): Boolean {
    return generiskModell.verdiFor(modell.resultatregnskap_skjoennsfastsattInntektEllerKostnad.inntektEllerKostnad.type)
        .isNullOrBlank()
}