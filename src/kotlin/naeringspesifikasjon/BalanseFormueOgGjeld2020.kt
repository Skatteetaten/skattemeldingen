package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

internal object BalanseFormueOgGjeld2020 : HarKalkylesamling {

    private fun GeneriskModellKontekst.sumAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): BigDecimal? {
        val inntektsaar = inntektsaar
        return forekomsterAv(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel) der {
            forekomstType.anleggsmiddeltype.harVerdi()
                && (forekomstType.anleggsmiddeltype likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                inntektsaar
            )) == balansekontoGirVerdsettingsrabattForventetVerdi
                && forekomstType.overfoeresIkkeTilSkattemeldingen.erUsann()
        } summerVerdiFraHverForekomst {
            forekomstType.skattemessigVerdi.tall()
        }
    }

    private fun GeneriskModellKontekst.sumOmloepsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): BigDecimal? {
        val inntektsaar = inntektsaar
        return forekomsterAv(modell2020.balanse_omloepsmiddel_balanseverdiForOmloepsmidler.balanseverdiForOmloepsmiddel) der {
            forekomstType.omloepsmiddeltype.harVerdi()
                && (forekomstType.omloepsmiddeltype likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                inntektsaar
            )) == balansekontoGirVerdsettingsrabattForventetVerdi
                && forekomstType.overfoeresIkkeTilSkattemeldingen.erUsann()
        } summerVerdiFraHverForekomst {
            forekomstType.skattemessigVerdi.tall()
        }
    }

    internal val verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle =
        kalkyle("verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt") {
            val sumAnleggsmiddel = sumAnleggsmiddel(true)
            val sumOmloepsmiddel = sumOmloepsmiddel(true)

            hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                settUniktFelt(modell2020.samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt) {
                    sumAnleggsmiddel + sumOmloepsmiddel
                }
            }
        }

    internal val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle =
        kalkyle("formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt") {
            val sumAnleggsmiddel = sumAnleggsmiddel(false)
            val sumOmloepsmiddel = sumOmloepsmiddel(false)

            hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                settUniktFelt(modell2020.samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt) {
                    sumAnleggsmiddel + sumOmloepsmiddel
                }
            }
        }

    internal val samletGjeldKalkyle = kalkyle("samletGjeld") {
        val inntektsaar = inntektsaar

        val sumKortsiktigGjeld =
            forekomsterAv(modell2020.balanse_gjeldOgEgenkapital_allKortsiktigGjeld.kortsiktigGjeld) der {
                forekomstType.kortsiktigGjeldtype.harVerdi() &&
                    !(forekomstType.kortsiktigGjeldtype likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                        inntektsaar
                    )) && forekomstType.overfoeresIkkeTilSkattemeldingen.erUsann()
            } summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }
        val sumLangsiktigGjeld =
            forekomsterAv(modell2020.balanse_gjeldOgEgenkapital_allLangsiktigGjeld.langsiktigGjeld) der {
                forekomstType.langsiktigGjeldtype.harVerdi() &&
                    !(forekomstType.langsiktigGjeldtype likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                        inntektsaar
                    )) && forekomstType.overfoeresIkkeTilSkattemeldingen.erUsann()
            } summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }

        hvis(sumKortsiktigGjeld.erPositiv() || sumLangsiktigGjeld.erPositiv()) {
            settUniktFelt(modell2020.samletGjeldOgFormuesobjekter.samletGjeld) {
                sumKortsiktigGjeld + sumLangsiktigGjeld
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle,
            formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle,
            samletGjeldKalkyle
        )
    }
}