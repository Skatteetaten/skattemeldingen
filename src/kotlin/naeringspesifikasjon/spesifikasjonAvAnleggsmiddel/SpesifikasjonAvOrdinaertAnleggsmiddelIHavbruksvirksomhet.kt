package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

internal object SpesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet : HarKalkylesamling {

    internal val direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt =
        kalkyle("direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt")
        {
            val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
                hvis(
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik
                        benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt) {
                        if (forekomstType.ervervsdato.aar() == inntektsaar) {
                            forekomstType.anskaffelseskost.tall()
                        } else {
                            forekomstType.paakostning.tall()
                        }
                    }
                }
            }

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) forHverForekomst {
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt) {
                    beregnHvis(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet_benyttesIGrunnrenteskattepliktigVirksomhet lik
                            benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_jaMedDirekteFradrag
                    ) {
                        forekomstType.nyanskaffelse +
                            forekomstType.paakostning
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            direkteUtgiftsfoertInvesteringskostnadIGrunnrenteinntekt
        )
    }
}