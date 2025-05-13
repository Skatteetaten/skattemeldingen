package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object SpesifikasjonAvSkattemessigVerdiPaaFordringUnntak : HarKalkylesamling {

    private val spesifikasjonAvSkattemessigVerdiPaaFordringUnntakKalkyle =
        kalkyle {
            forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) der {
                forekomstType.sumSkattemessigVerdiPaaFordring.harIkkeVerdi()
            } forHverForekomst {
                settFelt(forekomstType.sumSkattemessigVerdiPaaFordring) { 0.tall() }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(spesifikasjonAvSkattemessigVerdiPaaFordringUnntakKalkyle)
    }
}