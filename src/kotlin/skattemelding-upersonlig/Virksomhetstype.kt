package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

internal fun GeneriskModellKontekst.erPetroleumsforetak(): Boolean {
    return generiskModell.grupper(modell.inntektOgUnderskuddForVirksomhetPaaSokkel).isNotEmpty()
}
