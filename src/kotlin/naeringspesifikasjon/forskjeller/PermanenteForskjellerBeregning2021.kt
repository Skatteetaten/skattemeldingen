package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.PermanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object PermanenteForskjellerBeregning2021 : HarKalkylesamling {

    internal val permanenteForskjellerKalkyle = kalkyle {
        val inntektsaar = inntektsaar
        val sumPermanenteForskjellerTillegg = forekomsterAv(modell2021.permanentForskjell) der {
            PermanentForskjellstype.erTillegg(inntektsaar, forekomstType.permanentForskjellstype.verdi())
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        } medAntallDesimaler 2

        settUniktFelt(modell2021.beregnetNaeringsinntekt_sumTilleggINaeringsinntekt) { sumPermanenteForskjellerTillegg }

        val sumPermanenteForskjellerFradrag = forekomsterAv(modell2021.permanentForskjell) der {
            PermanentForskjellstype.erFradrag(inntektsaar, forekomstType.permanentForskjellstype.verdi())
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        } medAntallDesimaler 2

        settUniktFelt(modell2021.beregnetNaeringsinntekt_sumFradragINaeringsinntekt) { sumPermanenteForskjellerFradrag }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            permanenteForskjellerKalkyle
        )
    }
}