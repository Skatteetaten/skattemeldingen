package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.PermanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

internal object PermanenteForskjellerBeregning2022 : HarKalkylesamling {

    internal val permanenteForskjellerKalkyle = kalkyle {
        hvis(fullRegnskapsplikt()) {
            val inntektsaar = inntektsaar
            val sumPermanenteForskjellerTillegg = forekomsterAv(modell.permanentForskjell) der {
                PermanentForskjellstype.erTillegg(inntektsaar, modell.permanentForskjell.permanentForskjellstype.verdi())
            } summerVerdiFraHverForekomst {
                modell.permanentForskjell.beloep.tall()
            } medAntallDesimaler 2

            settUniktFelt(modell2022.beregnetNaeringsinntekt_sumTilleggINaeringsinntekt) { sumPermanenteForskjellerTillegg }

            val sumPermanenteForskjellerFradrag = forekomsterAv(modell.permanentForskjell) der {
                PermanentForskjellstype.erFradrag(inntektsaar, modell.permanentForskjell.permanentForskjellstype.verdi())
            } summerVerdiFraHverForekomst {
                modell.permanentForskjell.beloep.tall()
            } medAntallDesimaler 2

            settUniktFelt(modell2022.beregnetNaeringsinntekt_sumFradragINaeringsinntekt) { sumPermanenteForskjellerFradrag }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        permanenteForskjellerKalkyle
    )
    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}