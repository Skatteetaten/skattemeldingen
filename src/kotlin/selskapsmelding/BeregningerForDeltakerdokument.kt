package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV2

object BeregningerForDeltakerdokument: HarKalkylesamling {

    val endeligSamordnetPositivGrunnrenteinntektHavbruksvirksomhet = opprettSyntetiskFelt(modell.deltaker, "endeligSamordnetPositivGrunnrenteinntektHavbruksvirksomhet")
    val endeligSamordnetNegativGrunnrenteinntektHavbruksvirksomhet = opprettSyntetiskFelt(modell.deltaker, "endeligSamordnetNegativGrunnrenteinntektHavbruksvirksomhet")

    internal val endeligSamordnetGrunnrenteinntektHavbruksvirksomhetKalkyle = kalkyle("havbruksvirksomhet") {
        hvis(inntektsaar.tekniskInntektsaar < 2024) {
            val endeligSamordnetPositivGrunnrenteinntekt =
                modellV2.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt.tall()
            val endeligSamordnetNegativGrunnrenteinntekt =
                modellV2.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt.tall()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvInntektIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                settFelt(endeligSamordnetPositivGrunnrenteinntektHavbruksvirksomhet) {
                    (endeligSamordnetPositivGrunnrenteinntekt * prosent).somHeltall()
                }

                settFelt(endeligSamordnetNegativGrunnrenteinntektHavbruksvirksomhet) {
                    (endeligSamordnetNegativGrunnrenteinntekt * prosent).somHeltall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            endeligSamordnetGrunnrenteinntektHavbruksvirksomhetKalkyle
        )
    }
}