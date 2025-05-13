package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object BalanseFormueOgGjeld2021 : HarKalkylesamling {

    private fun GeneriskModellKontekst.sumAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): BigDecimal? {
        val inntektsaar = inntektsaar
        return forekomsterAv(modell2021.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) der {
            forekomstType.type.harVerdi()
                && (forekomstType.type likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                inntektsaar
            )) == balansekontoGirVerdsettingsrabattForventetVerdi
                &&
                skalOverfoeresTilSkattemeldingen(
                    forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                    forekomstType.type.verdi(),
                    inntektsaar
                )
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }
    }

    private fun GeneriskModellKontekst.sumOmloepsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): BigDecimal? {
        val inntektsaar = inntektsaar
        return forekomsterAv(modell2021.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) der {
            forekomstType.type.harVerdi()
                && (forekomstType.type likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                inntektsaar
            )) == balansekontoGirVerdsettingsrabattForventetVerdi
                &&
                skalOverfoeresTilSkattemeldingen(
                    forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                    forekomstType.type.verdi(),
                    inntektsaar
                )
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }
    }

    internal val verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle =
        kalkyle("verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt") {
            hvis(regnskapspliktstype1Eller5()) {
                val sumAnleggsmiddel = sumAnleggsmiddel(true)
                val sumOmloepsmiddel = sumOmloepsmiddel(true)

                hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                    settUniktFelt(modell2021.samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt) {
                        sumAnleggsmiddel + sumOmloepsmiddel
                    }
                }
            }
        }

    private fun skalOverfoeresTilSkattemeldingen(
        ekskluderesFraSkattemeldingen: String?,
        kode: String?,
        inntektsaar: Inntektsaar
    ): Boolean {
        return if (ekskluderesFraSkattemeldingen != null) {
            ekskluderesFraSkattemeldingen == "false"
        } else {
            ResultatregnskapOgBalanse.balansekontoerSomSkalOverfoeresTilSkattemeldingen(inntektsaar).contains(kode)
        }
    }

    internal val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle =
        kalkyle("formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt") {
            hvis(regnskapspliktstype1Eller5()) {
                val sumAnleggsmiddel = sumAnleggsmiddel(false)
                val sumOmloepsmiddel = sumOmloepsmiddel(false)

                hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                    settUniktFelt(modell2021.samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt) {
                        sumAnleggsmiddel + sumOmloepsmiddel
                    }
                }
            }
        }

    internal val samletGjeldKalkyle = kalkyle("samletGjeld") {
        hvis(regnskapspliktstype1Eller5()) {
            val inntektsaar = inntektsaar

            val sumKortsiktigGjeld =
                forekomsterAv(modell2021.balanseregnskap_gjeldOgEgenkapital_kortsiktigGjeld.gjeld) der {
                    forekomstType.type.harVerdi() &&
                        !(forekomstType.type likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                            inntektsaar
                        )) &&
                        skalOverfoeresTilSkattemeldingen(
                            forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                            forekomstType.type.verdi(),
                            inntektsaar
                        )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            val sumLangsiktigGjeld =
                forekomsterAv(modell2021.balanseregnskap_gjeldOgEgenkapital_langsiktigGjeld.gjeld) der {
                    forekomstType.type.harVerdi() &&
                        !(forekomstType.type likEnAv ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt(
                            inntektsaar
                        )) &&
                        skalOverfoeresTilSkattemeldingen(
                            forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                            forekomstType.type.verdi(),
                            inntektsaar
                        )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis(sumKortsiktigGjeld.erPositiv() || sumLangsiktigGjeld.erPositiv()) {
                settUniktFelt(modell2021.samletGjeldOgFormuesobjekter.samletGjeld) {
                    sumKortsiktigGjeld + sumLangsiktigGjeld
                }
            }
        }
    }

    val sumEgenkapitalKalkyle = kalkyle("sumEgenkapital") {
        settUniktFelt(modell2021.balanseregnskap_gjeldOgEgenkapital_sumEgenkapital) {
            summerEgenkapitalForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV) -
                summerEgenkapitalForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
        }
    }

    private fun GeneriskModellKontekst.summerEgenkapitalForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerEgenkapitalForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private val sumLangsiktigGjeldKalkyle = kalkyle("sumLangsiktigGjeld") {
        settUniktFelt(modell2021.balanseregnskap_gjeldOgEgenkapital_sumLangsiktigGjeld) {
            forekomsterAv(modell2021.balanseregnskap_gjeldOgEgenkapital_langsiktigGjeld.gjeld) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumKortsiktigGjeldKalkyle = kalkyle("sumKortsiktigGjeld") {
        settUniktFelt(modell2021.balanseregnskap_gjeldOgEgenkapital_sumKortsiktigGjeld) {
            forekomsterAv(modell2021.balanseregnskap_gjeldOgEgenkapital_kortsiktigGjeld.gjeld) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    internal val sumGjeldOgEgenkapitalKalkyle = kalkyle("sumGjeldOgEgenkapital") {
        settUniktFelt(modell2021.balanseregnskap_sumGjeldOgEgenkapital) {
            modell2021.balanseregnskap_gjeldOgEgenkapital_sumLangsiktigGjeld +
                modell2021.balanseregnskap_gjeldOgEgenkapital_sumKortsiktigGjeld +
                modell2021.balanseregnskap_gjeldOgEgenkapital_sumEgenkapital
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle,
            formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle,
            samletGjeldKalkyle,
            sumEgenkapitalKalkyle,
            sumLangsiktigGjeldKalkyle,
            sumKortsiktigGjeldKalkyle,
            sumGjeldOgEgenkapitalKalkyle
        )
    }
}