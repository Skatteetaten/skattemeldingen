package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErUpersonligEllerSdf
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV2

object RiskForUpersonligDeltakerKalkyler2023 : HarKalkylesamling {

    private val deltaker = modellV2.deltaker

    val sumReguleringForPositivRiskIEiertidenFelt = opprettSyntetiskFelt(deltaker, "sumReguleringForPositivRiskIEiertiden")
    val sumReguleringForNegativRiskIEiertidenFelt = opprettSyntetiskFelt(deltaker, "sumReguleringForNegativRiskIEiertiden")

    internal val samletRiskForUpersonligDeltakerKalkyle = kalkyle {
        forekomsterAv(modellV2.deltaker) forHverForekomst {
            hvis (deltakerErUpersonligEllerSdf() && this@kalkyle.erNokus()) {
                settFelt(forekomstType.riskForUpersonligDeltaker_samletRisk) {
                    (forekomstType.riskForUpersonligDeltaker_akkumulertRisk -
                        sumReguleringForPositivRiskIEiertidenFelt +
                        sumReguleringForNegativRiskIEiertidenFelt +
                        forekomstType.riskForUpersonligDeltaker_aaretsRisk)
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            RiskForUpersonligDeltakerKalkylerFra2024.reguleringForPositivRiskIEiertidenSumKalkyle,
            RiskForUpersonligDeltakerKalkylerFra2024.reguleringForNegativRiskIEiertidenSumKalkyle,
            samletRiskForUpersonligDeltakerKalkyle,
        )
    }
}