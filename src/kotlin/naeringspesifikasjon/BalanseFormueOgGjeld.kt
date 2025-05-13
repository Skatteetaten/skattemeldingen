package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse.balansekontoerSomGirVerdsettingsrabatt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse.balansekontoerSomSkalOverfoeresTilSkattemeldingen
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object BalanseFormueOgGjeld : HarKalkylesamling {

    private fun GeneriskModellKontekst.sumAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): BigDecimal? {
        val inntektsaar = inntektsaar
        return forekomsterAv(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) der {
            forekomstType.type.harVerdi()
                && (forekomstType.type likEnAv balansekontoerSomGirVerdsettingsrabatt(inntektsaar)) == balansekontoGirVerdsettingsrabattForventetVerdi
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
        return forekomsterAv(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) der {
            forekomstType.type.harVerdi()
                && (forekomstType.type likEnAv balansekontoerSomGirVerdsettingsrabatt(inntektsaar)) == balansekontoGirVerdsettingsrabattForventetVerdi
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
            hvis(ingenEllerBegrensetRegnskapsplikt() && virksomhetsTypeEnkeltpersonforetak()) {
                val sumAnleggsmiddel = sumAnleggsmiddel(true)
                val sumOmloepsmiddel = sumOmloepsmiddel(true)

                hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                    settUniktFelt(modell.samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt) {
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
            balansekontoerSomSkalOverfoeresTilSkattemeldingen(inntektsaar).contains(kode)
        }
    }

    internal val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle = kalkyle("formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && virksomhetsTypeEnkeltpersonforetak()) {
            val sumAnleggsmiddel = sumAnleggsmiddel(false)
            val sumOmloepsmiddel = sumOmloepsmiddel(false)

            hvis(sumAnleggsmiddel.erPositiv() || sumOmloepsmiddel.erPositiv()) {
                settUniktFelt(modell.samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt) {
                    sumAnleggsmiddel + sumOmloepsmiddel
                }
            }
        }
    }

    internal val samletGjeldKalkyle = kalkyle("samletGjeld") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && virksomhetsTypeEnkeltpersonforetak()) {
            val inntektsaar = inntektsaar
            val sumKortsiktigGjeld = forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_kortsiktigGjeld.gjeld) der {
                forekomstType.type.harVerdi() && !(forekomstType.type likEnAv balansekontoerSomGirVerdsettingsrabatt(inntektsaar)) && skalOverfoeresTilSkattemeldingen(
                    forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                    forekomstType.type.verdi(),
                    inntektsaar
                )
            } summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
            val sumLangsiktigGjeld = forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_langsiktigGjeld.gjeld) der {
                forekomstType.type.harVerdi() && !(forekomstType.type likEnAv balansekontoerSomGirVerdsettingsrabatt(inntektsaar)) && skalOverfoeresTilSkattemeldingen(
                    forekomstType.ekskluderesFraSkattemeldingen.verdi(),
                    forekomstType.type.verdi(),
                    inntektsaar
                )
            } summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }

            settUniktFelt(modell.samletGjeldOgFormuesobjekter.samletGjeld) {
                sumKortsiktigGjeld + sumLangsiktigGjeld
            }
        }
    }

    val sumEgenkapitalKalkyle = kalkyle("sumEgenkapital") {
        hvis(!virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
            settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumEgenkapital) {
                summerEgenkapitalForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV) -
                    summerEgenkapitalForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            }
        }
    }

    private fun GeneriskModellKontekst.summerEgenkapitalForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerEgenkapitalForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private val sumEgenkapitalLivsforsikringKalkyle = kalkyle("sumEgenkapitalLivsforsikring") {
        hvis(virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
            settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumEgenkapital) {
                forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital) summerVerdiFraHverForekomst {
                    forekomstType.kundeportefoeljebeloep +
                        forekomstType.selskapsportefoeljebeloep
                }
            }
        }
    }

    private val sumLangsiktigGjeldKalkyle = kalkyle("sumLangsiktigGjeld") {
        settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumLangsiktigGjeld) {
            forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_langsiktigGjeld.gjeld) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumKortsiktigGjeldKalkyle = kalkyle("sumKortsiktigGjeld") {
        settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumKortsiktigGjeld) {
            forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_kortsiktigGjeld.gjeld) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumGjeldInnenBankOgForsikringBankOgSkadeforsikringKalkyle = kalkyle("sumGjeldInnenBankOgForsikringBankOgSkadeforsikring") {
        hvis(!virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
            settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumGjeldInnenBankOgForsikring) {
                forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_gjeldInnenBankOgForsikring.gjeld) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }

    private val sumGjeldInnenBankOgForsikringLivsforsikringKalkyle = kalkyle("sumGjeldInnenBankOgForsikringLivsforsikring") {
        hvis(virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
            settUniktFelt(modell.balanseregnskap_gjeldOgEgenkapital_sumGjeldInnenBankOgForsikring) {
                forekomsterAv(modell.balanseregnskap_gjeldOgEgenkapital_gjeldInnenBankOgForsikring.gjeld) summerVerdiFraHverForekomst {
                    forekomstType.kundeportefoeljebeloep +
                        forekomstType.selskapsportefoeljebeloep
                }
            }
        }
    }

    private val sumGjeldOgEgenkapitalKalkyle = kalkyle("sumGjeldOgEgenkapital") {
        settUniktFelt(modell.balanseregnskap_sumGjeldOgEgenkapital) {
            modell.balanseregnskap_gjeldOgEgenkapital_sumLangsiktigGjeld +
                modell.balanseregnskap_gjeldOgEgenkapital_sumKortsiktigGjeld +
                modell.balanseregnskap_gjeldOgEgenkapital_sumGjeldInnenBankOgForsikring +
                modell.balanseregnskap_gjeldOgEgenkapital_sumEgenkapital
        }
    }

    internal val kalkyletre = Kalkylesamling(
        verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle,
        formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle,
        samletGjeldKalkyle,
        sumEgenkapitalKalkyle,
        sumEgenkapitalLivsforsikringKalkyle,
        sumLangsiktigGjeldKalkyle,
        sumKortsiktigGjeldKalkyle,
        sumGjeldInnenBankOgForsikringBankOgSkadeforsikringKalkyle,
        sumGjeldInnenBankOgForsikringLivsforsikringKalkyle,
        sumGjeldOgEgenkapitalKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyletre
    }
}