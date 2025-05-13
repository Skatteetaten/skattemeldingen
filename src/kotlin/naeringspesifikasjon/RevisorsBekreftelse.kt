package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

internal object RevisorsBekreftelse : HarKalkylesamling {

    private val defaultSkalBekreftesAvRevisor = kalkyle {
        hvis(modell.naeringsspesifikasjon_skalBekreftesAvRevisor.harIkkeVerdi()) {
            settUniktFelt(modell.naeringsspesifikasjon_skalBekreftesAvRevisor, "false")
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(defaultSkalBekreftesAvRevisor)
    }
}
