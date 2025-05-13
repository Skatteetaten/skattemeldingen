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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.egenkapital
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

internal object Balanse2020 : HarKalkylesamling {

    private fun lagBalanseForekomst(
        kalkyleNavn: String,
        saldogruppe: KodeVerdi,
        kode: KodeVerdi,
        beloepsfilter: ForekomstKontekst<*>.(beloep: BigDecimal?) -> Boolean = { true }
    ) = kalkyle(kalkyleNavn) {
        val forekomster =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe lik saldogruppe &&
                    forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erUsann() &&
                    beloepsfilter.invoke(this, forekomstType.utgaaendeVerdi.tall())
            }
        val beloep = forekomster summerVerdiFraHverForekomst {
            forekomstType.utgaaendeVerdi.tall()
        }
        opprettNyForekomstForBalanseverdiForAnleggsmiddel(beloep, kode)
    }

    private fun GeneriskModellKontekst.opprettNyForekomstForBalanseverdiForAnleggsmiddel(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel) {
                medId(kode.kode)
                medFelt(
                    modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.anleggsmiddeltype,
                    kode.kode
                )
                medFelt(
                    modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.skattemessigVerdi,
                    beloep
                )
            }
        }
    }

    internal val goodWill = lagBalanseForekomst("goodWill", saldogruppe.kode_b, balanseverdiForAnleggsmiddel.kode_1080)

    internal val forretningsBygg = lagBalanseForekomst("forretningsBygg", saldogruppe.kode_i, balanseverdiForAnleggsmiddel.kode_1105)

    internal val byggAnleggHotell = lagBalanseForekomst("byggAnleggHotell", saldogruppe.kode_h, balanseverdiForAnleggsmiddel.kode_1115)

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseForekomst("elektrotekniskUtrustningIKraftforetak", saldogruppe.kode_g, balanseverdiForAnleggsmiddel.kode_1117)

    internal val fastTekniskInstallasjonIBygninger =
        lagBalanseForekomst("fastTekniskInstallasjonIBygninger", saldogruppe.kode_j, balanseverdiForAnleggsmiddel.kode_1120) { it stoerreEllerLik 0 }

    internal val personbilerMaskinerInventar = lagBalanseForekomst("personbilerMaskinerInventar", saldogruppe.kode_d, balanseverdiForAnleggsmiddel.kode_1205) { it stoerreEllerLik 0 }

    internal val skipRigger = lagBalanseForekomst("skipRigger", saldogruppe.kode_e, balanseverdiForAnleggsmiddel.kode_1221)

    internal val flyHelikopter = lagBalanseForekomst("flyHelikopter", saldogruppe.kode_f, balanseverdiForAnleggsmiddel.kode_1225)

    internal val vareOgLastebilerBusser = lagBalanseForekomst("vareOgLastebilerBusser", saldogruppe.kode_c, balanseverdiForAnleggsmiddel.kode_1238) { it stoerreEllerLik 0 }

    internal val varebilerMedNullutslipp = lagBalanseForekomst("varebilerMedNullutslipp", saldogruppe2023.kode_c2, balanseverdiForAnleggsmiddel_2023.kode_1239) { it stoerreEllerLik 0 }

    internal val kontormaskiner = lagBalanseForekomst("kontormaskiner", saldogruppe.kode_a, balanseverdiForAnleggsmiddel.kode_1280) { it stoerreEllerLik 0 }

    internal val negativGevinstOgTapskonto = kalkyle("negativGevinstOgTapskonto") {
        val sumUtgaaendeVerdi =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
        opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1296)
    }

    internal val sumAnleggsmiddelSkattemessigVerdiKalkyle = kalkyle("sumAnleggsmiddelSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_anleggsmiddel_sumAnleggsmiddelSkattemessigVerdi) {
            forekomsterAv(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }
        }
    }

    internal val sumOmloepsmiddelSkattemessigVerdiKalkyle = kalkyle("sumOmloepsmiddelSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_omloepsmiddel_sumOmloepsmiddelSkattemessigVerdi) {
            forekomsterAv(modell2020.balanse_omloepsmiddel_balanseverdiForOmloepsmidler.balanseverdiForOmloepsmiddel) summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }
        }
    }

    internal val sumEiendelSkattemessigVerdiKalkyle = kalkyle("sumEiendelSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_sumEiendelSkattemessigVerdi) {
            modell2020.balanse_anleggsmiddel_sumAnleggsmiddelSkattemessigVerdi +
                modell2020.balanse_omloepsmiddel_sumOmloepsmiddelSkattemessigVerdi
        }
    }

    internal val sumLangsiktigGjeldSkattemessigVerdiKalkyle = kalkyle("sumLangsiktigGjeldSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_gjeldOgEgenkapital_sumLangsiktigGjeldSkattemessigVerdi) {
            forekomsterAv(modell2020.balanse_gjeldOgEgenkapital_allLangsiktigGjeld.langsiktigGjeld) summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }
        }
    }

    internal val sumKortsiktigGjeldSkattemessigVerdiKalkyle = kalkyle("sumKortsiktigGjeldSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_gjeldOgEgenkapital_sumKortsiktigGjeldSkattemessigVerdi) {
            forekomsterAv(modell2020.balanse_gjeldOgEgenkapital_allKortsiktigGjeld.kortsiktigGjeld) summerVerdiFraHverForekomst {
                forekomstType.skattemessigVerdi.tall()
            }
        }
    }

    private fun GeneriskModellKontekst.opprettNyForekomstForEgenkapital(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital) {
                medId(kode.kode)
                medFelt(
                    modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital.egenkapitaltype,
                    kode.kode
                )
                medFelt(
                    modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital.beloep,
                    beloep
                )
            }
        }
    }

    internal val negativSaldoKalkyle = kalkyle("negativSaldo") {
        val saldogrupper = listOf(
            saldogruppe.kode_a,
            saldogruppe.kode_c,
            saldogruppe2023.kode_c2,
            saldogruppe.kode_d,
            saldogruppe.kode_j
        )
        val utgaaendeVerdi =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe likEnAv saldogrupper
                    && forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
        opprettNyForekomstForEgenkapital(utgaaendeVerdi, egenkapital.kode_2095)
    }

    internal val ufrivilligRealisasjonKalkyle = kalkyle("ufrivilligRealisasjon") {
        val utgaaendeVerdi =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) der {
                forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erSann() &&
                    forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
        opprettNyForekomstForEgenkapital(utgaaendeVerdi, egenkapital.kode_2097)
    }

    internal val positivGevinstOgTapskonto = kalkyle("positivGevinstOgTapskonto") {
        opprettNyForekomstForEgenkapital(
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erPositiv()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            },
            egenkapital.kode_2096
        )
    }

    internal val sumEgenkapitalKalkyle = kalkyle("sumEgenkapital") {
        settUniktFelt(modell2020.balanse_gjeldOgEgenkapital_sumEgenkapital) {
            forekomsterAv(modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    internal val sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle = kalkyle("sumGjeldOgEgenkapitalSkattemessigVerdi") {
        settUniktFelt(modell2020.balanse_sumGjeldOgEgenkapitalSkattemessigVerdi) {
            modell2020.balanse_gjeldOgEgenkapital_sumLangsiktigGjeldSkattemessigVerdi +
                modell2020.balanse_gjeldOgEgenkapital_sumKortsiktigGjeldSkattemessigVerdi +
                modell2020.balanse_gjeldOgEgenkapital_sumEgenkapital
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            goodWill,
            forretningsBygg,
            byggAnleggHotell,
            elektrotekniskUtrustningIKraftforetak,
            fastTekniskInstallasjonIBygninger,
            personbilerMaskinerInventar,
            skipRigger,
            flyHelikopter,
            vareOgLastebilerBusser,
            varebilerMedNullutslipp,
            kontormaskiner,
            negativGevinstOgTapskonto,
            sumAnleggsmiddelSkattemessigVerdiKalkyle,
            sumOmloepsmiddelSkattemessigVerdiKalkyle,
            sumEiendelSkattemessigVerdiKalkyle,
            sumLangsiktigGjeldSkattemessigVerdiKalkyle,
            sumKortsiktigGjeldSkattemessigVerdiKalkyle,
            negativSaldoKalkyle,
            ufrivilligRealisasjonKalkyle,
            positivGevinstOgTapskonto,
            sumEgenkapitalKalkyle,
            sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle
        )
    }
}