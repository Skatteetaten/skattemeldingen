package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErUpersonligEllerSdf
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object RiskForUpersonligDeltakerKalkylerFra2024 : HarKalkylesamling {

    private val deltaker = modell.deltaker

    val sumReguleringForPositivRiskIEiertidenFelt = opprettSyntetiskFelt(deltaker, "sumReguleringForPositivRiskIEiertiden")
    val sumReguleringForNegativRiskIEiertidenFelt = opprettSyntetiskFelt(deltaker, "sumReguleringForNegativRiskIEiertiden")

    internal val reguleringForPositivRiskIEiertidenSumKalkyle = kalkyle {

        forekomsterAv(modell.deltaker) forHverForekomst {
            settFelt(sumReguleringForPositivRiskIEiertidenFelt) {
                forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) summerVerdiFraHverForekomst {
                    forekomstType.reguleringForPositivRiskIEiertiden.tall()
                }
            }
        }
    }

    internal val reguleringForNegativRiskIEiertidenSumKalkyle = kalkyle {
        forekomsterAv(modell.deltaker) forHverForekomst {
            settFelt(sumReguleringForNegativRiskIEiertidenFelt) {
                forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) summerVerdiFraHverForekomst {
                    forekomstType.reguleringForNegativRiskIEiertiden.tall()
                }
            }
        }
    }

    internal val samletRiskForUpersonligDeltakerKalkyle = kalkyle {
        forekomsterAv(modell.deltaker) forHverForekomst {
            hvis (deltakerErUpersonligEllerSdf() && this@kalkyle.erNokus()) {
                settFelt(forekomstType.riskForUpersonligDeltaker_samletRisk) {
                    (forekomstType.riskForUpersonligDeltaker_akkumulertRisk -
                        sumReguleringForPositivRiskIEiertidenFelt +
                        sumReguleringForNegativRiskIEiertidenFelt +
                        forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_endringISelskapetsSkattlagteKapital)
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            reguleringForPositivRiskIEiertidenSumKalkyle,
            reguleringForNegativRiskIEiertidenSumKalkyle,
            samletRiskForUpersonligDeltakerKalkyle,
        )
    }
}