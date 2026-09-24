package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object FormuesverdiPaaDeltakersAksjerINokusKalkyler : HarKalkylesamling {


    internal val andelAvKursverdi1JanuarIAaretEtterInntektsaaretKalkyle = kalkyle {
        hvis(erNokus()) {
            val kursverdiAvBoersnoterteAksjer =
                modell.formuesverdiTilFordelingPaaDeltaker.kursverdiAvBoersnoterteAksjer.tall()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvFormueIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                settFelt(forekomstType.formuesverdiPaaDeltakersAksjerINokus_andelAvKursverdi1JanuarIAaretEtterInntektsaaret) { (kursverdiAvBoersnoterteAksjer * prosent) medMinimumsverdi 0 }
            }
        }
    }

    internal val andelAvNettoSkattemessigFormueForrigeInntektsaarKalkyle = kalkyle {

        hvis(erNokus()) {
            val nettoSkattemessigFormueForrigeInntektsaar =
                modell.formuesverdiTilFordelingPaaDeltaker.nettoformueFraForegaaendeInntektsaar.tall()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvFormueIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                settFelt(forekomstType.formuesverdiPaaDeltakersAksjerINokus_andelAvNettoSkattemessigFormueForrigeInntektsaar) { (nettoSkattemessigFormueForrigeInntektsaar * prosent) medMinimumsverdi 0 }
            }
        }
    }

    internal val andelAvNettoSkattemessigFormueIInntektsaaretKalkyle = kalkyle {

        hvis(erNokus()) {
            val formueOgGjeld = modell.formueOgGjeld.nettoformue.tall()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvFormueIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                settFelt(forekomstType.formuesverdiPaaDeltakersAksjerINokus_andelAvNettoSkattemessigFormueIInntektsaaret) { (formueOgGjeld * prosent) medMinimumsverdi 0 }
            }
        }
    }

    internal val andelAvAntattSalgsverdi1JanuarIAaretEtterInntektsaaretKalkyle = kalkyle {

        hvis(erNokus()) {
            val antattSalgsverdiVedUtgangenAvInntektsaaret =
                modell.formuesverdiTilFordelingPaaDeltaker.antattSalgsverdiVedUtgangenAvInntektsaaret.tall()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvFormueIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                settFelt(forekomstType.formuesverdiPaaDeltakersAksjerINokus_andelAvAntattSalgsverdi1JanuarIAaretEtterInntektsaaret) { (antattSalgsverdiVedUtgangenAvInntektsaaret * prosent) medMinimumsverdi 0 }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            andelAvKursverdi1JanuarIAaretEtterInntektsaaretKalkyle,
            andelAvNettoSkattemessigFormueForrigeInntektsaarKalkyle,
            andelAvNettoSkattemessigFormueIInntektsaaretKalkyle,
            andelAvAntattSalgsverdi1JanuarIAaretEtterInntektsaaretKalkyle
        )
    }
}
