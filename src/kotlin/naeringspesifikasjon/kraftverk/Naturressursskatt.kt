package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.rundAvNedTilNaermesteTusen
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Grunnlag+for+beregning+av+naturressursskatt
 */
internal object Naturressursskatt : HarKalkylesamling {

    private val samletAarsproduksjon = kalkyle("samletAarsproduksjon") {
        hvis(erKraftverk()) {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                val samletAarsproduksjon = forekomsterAv(forekomstType.grunnlagForBeregningAvNaturressursskatt_grunnlagForNaturressursskattPerInntektsaar) summerVerdiFraHverForekomst {
                    forekomstType.aarsproduksjon.tall()
                }

                settFelt(forekomstType.grunnlagForBeregningAvNaturressursskatt_samletAarsproduksjon) {
                    samletAarsproduksjon.somHeltall()
                }
            }
        }
    }

    private val samletMedgaattPumpekraft = kalkyle("samletMedgaattPumpekraft") {
        hvis(erKraftverk()) {
            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrenseV6()
            } forHverForekomst {
                settFelt(forekomstType.grunnlagForBeregningAvNaturressursskatt_samletMedgaattPumpekraft) {
                    forekomsterAv(forekomstType.grunnlagForBeregningAvNaturressursskatt_grunnlagForNaturressursskattPerInntektsaar) summerVerdiFraHverForekomst {
                        forekomstType.medgaattPumpekraft.tall().somHeltall()
                    }
                }
            }
        }
    }

    private val samletGrunnlagForNaturressursskatt = kalkyle("samletGrunnlagForNaturressursskatt") {
        val antallAar = 7
        forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
            samletPaastempletMerkeytelseIKvaOverGrenseV6()
        } forHverForekomst {
            settFelt(forekomstType.grunnlagForBeregningAvNaturressursskatt_samletGrunnlagForNaturressursskatt) {
                ((modell.kraftverk_spesifikasjonAvKraftverk.grunnlagForBeregningAvNaturressursskatt_samletAarsproduksjon
                - modell.kraftverk_spesifikasjonAvKraftverk.grunnlagForBeregningAvNaturressursskatt_samletMedgaattPumpekraft) / antallAar
                    + modell.kraftverk_spesifikasjonAvKraftverk.grunnlagForBeregningAvNaturressursskatt_korrigeringAvGrunnlagForNaturressursskatt
                ).rundAvNedTilNaermesteTusen()
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletAarsproduksjon,
            samletMedgaattPumpekraft,
            samletGrunnlagForNaturressursskatt,
        )
    }
}
