package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForAnleggsmiddel_2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForOmloepsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.egenkapital
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object Balanse2021 : HarKalkylesamling {

    internal val sumBalanseverdiForEiendelKalkyle =
        kalkyle("sumBalanseverdiForEiendel") {
            settUniktFelt(modell.balanseregnskap_sumBalanseverdiForEiendel) {
                modell.balanseregnskap_anleggsmiddel_sumBalanseverdiForAnleggsmiddel + modell.balanseregnskap_omloepsmiddel_sumBalanseverdiForOmloepsmiddel
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            BalanseverdiForAnleggsmiddel2021.goodWill,
            BalanseverdiForAnleggsmiddel2021.forretningsBygg,
            BalanseverdiForAnleggsmiddel2021.byggAnleggHotell,
            BalanseverdiForAnleggsmiddel2021.elektrotekniskUtrustningIKraftforetak,
            BalanseverdiForAnleggsmiddel2021.fastTekniskInstallasjonIBygninger,
            BalanseverdiForAnleggsmiddel2021.personbilerMaskinerInventar,
            BalanseverdiForAnleggsmiddel2021.skipRigger,
            BalanseverdiForAnleggsmiddel2021.flyHelikopter,
            BalanseverdiForAnleggsmiddel2021.vareOgLastebilerBusser,
            BalanseverdiForAnleggsmiddel2021.varebilerMedNullutslipp,
            BalanseverdiForAnleggsmiddel2021.kontormaskiner,
            BalanseverdiForAnleggsmiddel2021.negativGevinstOgTapskonto,
            BalanseverdiForAnleggsmiddel2021.negativToemmerkonto,
            BalanseverdiForAnleggsmiddel2021.driftsmidlerSomAvskrivesLineaertKalkyle,
            BalanseverdiForAnleggsmiddel2021.sumBalanseverdiForAnleggsmiddelKalkyle,
            BalanseverdiForOmloepsmiddel2021.kundefordringKalkyle,
            BalanseverdiForOmloepsmiddel2021.balanseverdi1400Kalkyle,
            BalanseverdiForOmloepsmiddel2021.balanseverdi1401Kalkyle,
            BalanseverdiForOmloepsmiddel2021.sumBalanseverdiForOmloepsmiddelKalkyle,
            sumBalanseverdiForEiendelKalkyle,
            Egenkapital2021.negativSaldoKalkyle,
            Egenkapital2021.ufrivilligRealisasjonKalkyle,
            Egenkapital2021.positivGevinstOgTapskontoKalkyle,
            Egenkapital2021.positivToemmerkontoKalkyle,
        )
    }
}

internal object BalanseverdiForAnleggsmiddel2021 {

    private fun lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
        kalkyleNavn: String,
        saldogruppe: KodeVerdi,
        kode: KodeVerdi,
        beloepsfilter: ForekomstKontekst<*>.(beloep: BigDecimal?) -> Boolean = { true },
    ) = kalkyle(kalkyleNavn) {
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(saldogruppe, kode, beloepsfilter)
    }

    private fun GeneriskModellKontekst.lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
        saldogruppe: KodeVerdi,
        kode: KodeVerdi,
        beloepsfilter: ForekomstKontekst<*>.(beloep: BigDecimal?) -> Boolean = { true }
    ) {
        hvis(regnskapspliktstype1Eller5()) {
            val forekomster = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe lik saldogruppe &&
                    forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erUsann() &&
                    beloepsfilter.invoke(this, forekomstType.utgaaendeVerdi.tall())
            }
            val beloep = forekomster summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            }
            opprettNyForekomstForBalanseverdiForAnleggsmiddel(beloep, kode)
        }
    }

    private fun GeneriskModellKontekst.opprettNyForekomstForBalanseverdiForAnleggsmiddel(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2021.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) {
                medId(kode.kode)
                medFelt(
                    modell2021.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type,
                    kode.kode
                )
                medFelt(
                    modell2021.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.beloep,
                    beloep
                )
            }
        }
    }

    internal val goodWill =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("goodwill", saldogruppe.kode_b, balanseverdiForAnleggsmiddel.kode_1080)

    internal val forretningsBygg =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("forretningsbygg", saldogruppe.kode_i, balanseverdiForAnleggsmiddel.kode_1105)

    internal val byggAnleggHotell =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("byggAnleggHotell", saldogruppe.kode_h, balanseverdiForAnleggsmiddel.kode_1115)

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("elektrotekniskUtrustningIKraftforetak", saldogruppe.kode_g, balanseverdiForAnleggsmiddel.kode_1117)

    internal val fastTekniskInstallasjonIBygninger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("fastTekniskInstallasjonIBygninger", saldogruppe.kode_j, balanseverdiForAnleggsmiddel.kode_1120) { it stoerreEllerLik 0 }

    internal val personbilerMaskinerInventar =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("personbilerMaskinerInventar", saldogruppe.kode_d, balanseverdiForAnleggsmiddel.kode_1205) { it stoerreEllerLik 0 }

    internal val skipRigger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("skipRigger", saldogruppe.kode_e, balanseverdiForAnleggsmiddel.kode_1221)

    internal val flyHelikopter =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("flyHelikopter", saldogruppe.kode_f, balanseverdiForAnleggsmiddel.kode_1225)

    internal val vareOgLastebilerBusser =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("vareOgLastebilerBusser", saldogruppe.kode_c, balanseverdiForAnleggsmiddel.kode_1238) { it stoerreEllerLik 0 }

    internal val varebilerMedNullutslipp =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("varebilerMedNullutslipp", saldogruppe2023.kode_c2, balanseverdiForAnleggsmiddel_2023.kode_1239) { it stoerreEllerLik 0 }

    internal val kontormaskiner =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("kontormaskiner", saldogruppe.kode_a, balanseverdiForAnleggsmiddel.kode_1280) { it stoerreEllerLik 0 }

    internal val negativGevinstOgTapskonto = kalkyle("negativGevinstOgTapskonto") {
        hvis(regnskapspliktstype1Eller5()) {
            val sumUtgaaendeVerdi = forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
            opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1296)
        }
    }

    internal val negativToemmerkonto =
        kalkyle("negativToemmerkonto") {
            hvis(regnskapspliktstype1Eller5()) {
                val sumUtgaaendeVerdi = forekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) der {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto.erNegativ()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto * -1
                }
                opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1298)
            }
        }

    internal val driftsmidlerSomAvskrivesLineaertKalkyle =
        kalkyle("driftsmidlerSomAvskrivesLineaertKalkyle") {
            hvis(regnskapspliktstype1Eller5()) {
                val sumUtgaaendeVerdi = forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
                opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1295)
            }
        }

    internal val sumBalanseverdiForAnleggsmiddelKalkyle =
        kalkyle("sumBalanseverdiForAnleggsmiddelKalkyle") {
            val sum =
                forekomsterAv(modell2021.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            settUniktFelt(modell2021.balanseregnskap_anleggsmiddel_sumBalanseverdiForAnleggsmiddel) { sum }
        }
}

internal object BalanseverdiForOmloepsmiddel2021 {

    private fun GeneriskModellKontekst.opprettNyForekomstForBalanseverdiForOmloepsmiddel(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2021.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) {
                medId(kode.kode)
                medFelt(
                    modell2021.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type,
                    kode.kode
                )
                medFelt(
                    modell2021.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.beloep,
                    beloep
                )
            }
        }
    }

    internal val kundefordringKalkyle =
        kalkyle("kundefordring") {
            hvis(regnskapspliktstype1Eller5()) {
                opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                    forekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) summerVerdiFraHverForekomst {
                        forekomstType.skattemessigVerdiPaaKundefordring.tall()
                    },
                    balanseverdiForOmloepsmiddel.kode_1500
                )
            }
        }

    internal val balanseverdi1400Kalkyle = kalkyle("balanseverdi1400") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                forekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) summerVerdiFraHverForekomst {
                    forekomstType.sumVerdiAvVarelager - forekomstType.selvprodusertVareBenyttetIEgenProduksjon
                },
                balanseverdiForOmloepsmiddel.kode_1400
            )
        }
    }

    internal val balanseverdi1401Kalkyle = kalkyle("balanseverdi1401") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                forekomsterAv(modell2021.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) summerVerdiFraHverForekomst {
                    forekomstType.selvprodusertVareBenyttetIEgenProduksjon.tall()
                },
                balanseverdiForOmloepsmiddel.kode_1401
            )
        }
    }

    internal val sumBalanseverdiForOmloepsmiddelKalkyle =
        kalkyle("sumBalanseverdiForOmloepsmiddelKalkyle") {
            settUniktFelt(modell2021.balanseregnskap_omloepsmiddel_sumBalanseverdiForOmloepsmiddel) {
                forekomsterAv(modell2021.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
}

internal object Egenkapital2021 {

    private fun GeneriskModellKontekst.opprettNyForekomstForEgenkapital(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2021.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital) {
                medId(kode.kode)
                medFelt(
                    modell2021.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type,
                    kode.kode
                )
                medFelt(
                    modell2021.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.beloep,
                    beloep
                )
            }
        }
    }

    internal val negativSaldoKalkyle = kalkyle("negativSaldo") {
        hvis(regnskapspliktstype1Eller5()) {
            val saldogrupper = listOf(
                saldogruppe.kode_a,
                saldogruppe.kode_c,
                saldogruppe2023.kode_c2,
                saldogruppe.kode_d,
                saldogruppe.kode_j
            )
            val utgaaendeVerdi = forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe likEnAv saldogrupper
                    && forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
            opprettNyForekomstForEgenkapital(utgaaendeVerdi, egenkapital.kode_2095)
        }
    }

    internal val ufrivilligRealisasjonKalkyle = kalkyle("ufrivilligRealisasjon") {
        hvis(regnskapspliktstype1Eller5()) {
            val utgaaendeVerdi = forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erSann() &&
                    forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
            opprettNyForekomstForEgenkapital(utgaaendeVerdi, egenkapital.kode_2097)
        }
    }

    internal val positivGevinstOgTapskontoKalkyle = kalkyle("positivGevinstOgTapskonto") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForEgenkapital(
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.utgaaendeVerdi.erPositiv()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                },
                egenkapital.kode_2096
            )
        }
    }

    internal val positivToemmerkontoKalkyle = kalkyle("positivToemmerkonto") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForEgenkapital(
                forekomsterAv(modell.skogbruk_skogOgToemmerkonto) der {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto.erPositiv()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto.tall()
                },
                egenkapital.kode_2098
            )
        }
    }
}