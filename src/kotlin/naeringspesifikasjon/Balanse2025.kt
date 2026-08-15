package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2025

internal object Balanse2025 : HarKalkylesamling {

    internal val sumBalanseverdiForEiendelKalkyle =
        kalkyle("sumBalanseverdiForEiendel") {
            hvis(!gjelderBankOgForsikring()) {
                settUniktFelt(modell.balanseregnskap_sumBalanseverdiForEiendel) {
                    modell.balanseregnskap_anleggsmiddel_sumBalanseverdiForAnleggsmiddel + modell.balanseregnskap_omloepsmiddel_sumBalanseverdiForOmloepsmiddel
                }
            }
        }

    private val sumBalanseverdiForEiendelBankOgSkadeforsikringKalkyle =
        kalkyle("sumBalanseverdiForEiendelBankOgSkadeforsikring") {
            hvis(virksomhetsTypeBankOgFinansieringsforetak() || virksomhetsTypeSkadeforsikringsforetak()) {
                settUniktFelt(modell.balanseregnskap_sumBalanseverdiForEiendel) {
                    forekomsterAv(modell2025.balanseregnskap_balanseverdiForBankOgForsikringseiendel.balanseverdi) summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private val sumBalanseverdiForEiendelLivsforsikringKalkyle =
        kalkyle("sumBalanseverdiForEiendelLivsforsikring") {
            hvis(virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
                settUniktFelt(modell.balanseregnskap_sumBalanseverdiForEiendel) {
                    forekomsterAv(modell2025.balanseregnskap_balanseverdiForBankOgForsikringseiendel.balanseverdi) summerVerdiFraHverForekomst {
                        forekomstType.kundeportefoeljebeloep +
                            forekomstType.selskapsportefoeljebeloep
                    }
                }
            }
        }

    private val kalkyleSamling = Kalkylesamling(
        BalanseverdiForAnleggsmiddel.goodWill,
        BalanseverdiForAnleggsmiddel.forretningsBygg,
        BalanseverdiForAnleggsmiddel.byggAnleggHotell,
        BalanseverdiForAnleggsmiddel.elektrotekniskUtrustningIKraftforetak,
        BalanseverdiForAnleggsmiddel.fastTekniskInstallasjonIBygninger,
        BalanseverdiForAnleggsmiddel.personbilerMaskinerInventar,
        BalanseverdiForAnleggsmiddel.skipRigger,
        BalanseverdiForAnleggsmiddel.flyHelikopter,
        BalanseverdiForAnleggsmiddel.vareOgLastebilerBusser,
        BalanseverdiForAnleggsmiddel.varebilerMedNullutslipp,
        BalanseverdiForAnleggsmiddel.kontormaskiner,
        BalanseverdiForAnleggsmiddel.negativGevinstOgTapskonto,
        BalanseverdiForAnleggsmiddel.negativToemmerkonto,
        BalanseverdiForAnleggsmiddel.negativUtgaaendeVerdiPaaJordbrukskonto,
        BalanseverdiForAnleggsmiddel.driftsmidlerSomAvskrivesLineaertKalkyle,
        BalanseverdiForAnleggsmiddel.sumBalanseverdiForAnleggsmiddelKalkyle,
        BalanseverdiForOmloepsmiddel.kundefordringKalkyle,
        BalanseverdiForOmloepsmiddel.balanseverdi1400Kalkyle,
        BalanseverdiForOmloepsmiddel.balanseverdi1401Kalkyle,
        BalanseverdiForOmloepsmiddel.sumBalanseverdiForOmloepsmiddelKalkyle,
        sumBalanseverdiForEiendelKalkyle,
        sumBalanseverdiForEiendelBankOgSkadeforsikringKalkyle,
        sumBalanseverdiForEiendelLivsforsikringKalkyle,
        Egenkapital.negativSaldoKalkyle,
        Egenkapital.positivGevinstOgTapskontoKalkyle,
        Egenkapital.positivToemmerkontoKalkyle,
        Egenkapital.positivUtgaaendeVerdiPaaJordbrukskonto,
        Balanseregnskap.kontoForUtsattInntektsfoeringKalkyle,
        Balanseregnskap.sumSkattemessigVerdiAvFinansieltDerivatSomEiendelKalkyle,
        Balanseregnskap.sumSkattemessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning,
        Balanseregnskap.sumRegnskapsmessigVerdiAvFinansieltDerivatSomEiendel,
        Balanseregnskap.sumRegnskapsmessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}

