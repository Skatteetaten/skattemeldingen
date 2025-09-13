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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v4_2023.saldogruppe_2023.kode_i as saldoGruppeI

internal object Balanse : HarKalkylesamling {

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
                    forekomsterAv(modell.balanseregnskap_balanseverdiForBankOgForsikringseiendel.balanseverdi) summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private val sumBalanseverdiForEiendelLivsforsikringKalkyle =
        kalkyle("sumBalanseverdiForEiendelLivsforsikring") {
            hvis(virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()) {
                settUniktFelt(modell.balanseregnskap_sumBalanseverdiForEiendel) {
                    forekomsterAv(modell.balanseregnskap_balanseverdiForBankOgForsikringseiendel.balanseverdi) summerVerdiFraHverForekomst {
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

internal object BalanseverdiForAnleggsmiddel {

    private fun lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
        kalkyleNavn: String,
        saldogruppe: KodeVerdi,
        kode: KodeVerdi,
        skalTrekkeFraNedreGrenseForAvskrivning: Boolean = false,
        beloepsfilter: ForekomstKontekst<*>.(beloep: BigDecimal?) -> Boolean = { true },
    ) = kalkyle(kalkyleNavn) {
        if (erIkkeSkjoennsfastsatt()) {
            lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
                saldogruppe,
                kode,
                skalTrekkeFraNedreGrenseForAvskrivning,
                beloepsfilter
            )
        }
    }

    private fun GeneriskModellKontekst.lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
        saldogruppe: KodeVerdi,
        kode: KodeVerdi,
        skalTrekkeFraNedreGrenseForAvskrivning: Boolean = false,
        beloepsfilter: ForekomstKontekst<*>.(beloep: BigDecimal?) -> Boolean = { true }
    ) {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
            val forekomster = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe lik saldogruppe &&
                    forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erUsann() &&
                    beloepsfilter.invoke(this, forekomstType.utgaaendeVerdi.tall())
            }
            var beloep = forekomster summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi.tall()
            }
            hvis(skalTrekkeFraNedreGrenseForAvskrivning && beloep.harVerdi()) {
                val nedreGrenseForAvskrivning = forekomster summerVerdiFraHverForekomst {
                    forekomstType.forretningsbyggAnskaffetFoer01011984_nedreGrenseForAvskrivning.tall()
                }
                beloep -= nedreGrenseForAvskrivning
            }
            opprettNyForekomstForBalanseverdiForAnleggsmiddel(beloep, kode)
        }
    }

    private fun GeneriskModellKontekst.opprettNyForekomstForBalanseverdiForAnleggsmiddel(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) {
                medId(kode.kode)
                medFelt(
                    modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type,
                    kode.kode
                )
                medFelt(
                    modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.beloep,
                    beloep
                )
            }
        }
    }

    internal val goodWill = lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("goodwill", saldogruppe.kode_b, balanseverdiForAnleggsmiddel.kode_1080)

    internal val forretningsBygg =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
            "forretningsbygg",
            saldoGruppeI,
            balanseverdiForAnleggsmiddel.kode_1105,
            skalTrekkeFraNedreGrenseForAvskrivning = true
        )

    internal val byggAnleggHotell =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("byggAnleggHotell", saldogruppe.kode_h, balanseverdiForAnleggsmiddel.kode_1115)

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("elektrotekniskUtrustningIKraftforetak", saldogruppe.kode_g, balanseverdiForAnleggsmiddel.kode_1117)

    internal val fastTekniskInstallasjonIBygninger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
            "fastTekniskInstallasjonIBygninger",
            saldogruppe.kode_j,
            balanseverdiForAnleggsmiddel.kode_1120,
            skalTrekkeFraNedreGrenseForAvskrivning = true
        ) { it stoerreEllerLik 0 }

    internal val personbilerMaskinerInventar =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
            "personbilerMaskinerInventar",
            saldogruppe.kode_d,
            balanseverdiForAnleggsmiddel.kode_1205
        ) { it stoerreEllerLik 0 }

    internal val skipRigger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("skipRigger", saldogruppe.kode_e, balanseverdiForAnleggsmiddel.kode_1221)

    internal val flyHelikopter =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("flyHelikopter", saldogruppe.kode_f, balanseverdiForAnleggsmiddel.kode_1225)

    internal val vareOgLastebilerBusser =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("vareOgLastebilerBusser", saldogruppe.kode_c, balanseverdiForAnleggsmiddel.kode_1238) { it stoerreEllerLik 0 }

    internal val varebilerMedNullutslipp = kalkyle("varebilerMedNullutslipp") {
        hvis(inntektsaar.tekniskInntektsaar < 2024) {
            lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
                saldogruppe2023.kode_c2,
                balanseverdiForAnleggsmiddel_2023.kode_1239
            ) { it stoerreEllerLik 0 }
        }
    }

    internal val kontormaskiner =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel("kontormaskiner", saldogruppe.kode_a, balanseverdiForAnleggsmiddel.kode_1280) { it stoerreEllerLik 0 }

    internal val negativGevinstOgTapskonto = kalkyle("negativGevinstOgTapskonto") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            val sumUtgaaendeVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
            opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1296)
        }
    }

    internal val negativToemmerkonto =
        kalkyle("negativToemmerkonto") {
            hvis(ingenEllerBegrensetRegnskapsplikt()) {
                val sumUtgaaendeVerdi = forekomsterAv(modell.skogbruk_skogOgToemmerkonto) der {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto.erNegativ()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiPaaToemmerkonto * -1
                }
                opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1298)
            }
        }

    internal val driftsmidlerSomAvskrivesLineaertKalkyle =
        kalkyle("driftsmidlerSomAvskrivesLineaertKalkyle") {
            hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
                val sumUtgaaendeVerdiForLinaertAvskrevetAnleggsmiddel =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                        forekomstType.utgaaendeVerdi.tall()
                    }
                val sumUtgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk.tall()
                    }
                val sumUtgaaendeVerdi =
                    sumUtgaaendeVerdiForLinaertAvskrevetAnleggsmiddel + sumUtgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk
                opprettNyForekomstForBalanseverdiForAnleggsmiddel(sumUtgaaendeVerdi, balanseverdiForAnleggsmiddel.kode_1295)
            }
        }

    internal val sumBalanseverdiForAnleggsmiddelKalkyle =
        kalkyle("sumBalanseverdiForAnleggsmiddelKalkyle") {
            val sum =
                forekomsterAv(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            settUniktFelt(modell.balanseregnskap_anleggsmiddel_sumBalanseverdiForAnleggsmiddel) { sum }
        }
}

internal object BalanseverdiForOmloepsmiddel {

    private fun GeneriskModellKontekst.opprettNyForekomstForBalanseverdiForOmloepsmiddel(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) {
                medId(kode.kode)
                medFelt(
                    modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type,
                    kode.kode
                )
                medFelt(
                    modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.beloep,
                    beloep
                )
            }
        }
    }

    internal val kundefordringKalkyle =
        kalkyle("kundefordring") {
            hvis(ingenEllerBegrensetRegnskapsplikt()) {
                opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                    forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvSkattemessigVerdiPaaFordring) summerVerdiFraHverForekomst {
                        forekomstType.skattemessigVerdiPaaKundefordring.tall()
                    },
                    balanseverdiForOmloepsmiddel.kode_1500
                )
            }
        }

    internal val balanseverdi1400Kalkyle = kalkyle("balanseverdi1400") {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
            opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) summerVerdiFraHverForekomst {
                    forekomstType.sumVerdiAvVarelager - forekomstType.selvprodusertVareBenyttetIEgenProduksjon
                },
                balanseverdiForOmloepsmiddel.kode_1400
            )
        }
    }

    internal val balanseverdi1401Kalkyle = kalkyle("balanseverdi1401") {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
            opprettNyForekomstForBalanseverdiForOmloepsmiddel(
                forekomsterAv(modell.spesifikasjonAvOmloepsmiddel_spesifikasjonAvVarelager) summerVerdiFraHverForekomst {
                    forekomstType.selvprodusertVareBenyttetIEgenProduksjon.tall()
                },
                balanseverdiForOmloepsmiddel.kode_1401
            )
        }
    }

    internal val sumBalanseverdiForOmloepsmiddelKalkyle =
        kalkyle("sumBalanseverdiForOmloepsmiddelKalkyle") {
            settUniktFelt(modell.balanseregnskap_omloepsmiddel_sumBalanseverdiForOmloepsmiddel) {
                forekomsterAv(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
}

internal object Egenkapital {

    private fun GeneriskModellKontekst.opprettNyForekomstForEgenkapital(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital) {
                medId(kode.kode)
                medFelt(
                    modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type,
                    kode.kode
                )
                medFelt(
                    modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.beloep,
                    beloep
                )
            }
        }
    }

    val acc2dj = listOf(
        saldogruppe.kode_a,
        saldogruppe.kode_c,
        saldogruppe2023.kode_c2,
        saldogruppe.kode_d,
        saldogruppe.kode_j
    )

    val acdj = listOf(
        saldogruppe.kode_a,
        saldogruppe.kode_c,
        saldogruppe.kode_d,
        saldogruppe.kode_j
    )

    internal val negativSaldoKalkyle = kalkyle("negativSaldo") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            val saldogruppeBaserPaaAar = if (inntektsaar.tekniskInntektsaar < 2024) {
                acc2dj
            } else {
                acdj
            }
            val utgaaendeVerdi = forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                forekomstType.saldogruppe likEnAv saldogruppeBaserPaaAar
                    && forekomstType.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.erUsann()
                    && forekomstType.utgaaendeVerdi.erNegativ()
            } summerVerdiFraHverForekomst {
                forekomstType.utgaaendeVerdi * -1
            }
            opprettNyForekomstForEgenkapital(utgaaendeVerdi, egenkapital.kode_2095)
        }
    }

    internal val positivGevinstOgTapskontoKalkyle = kalkyle("positivGevinstOgTapskonto") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            opprettNyForekomstForEgenkapital(
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.utgaaendeVerdi.erPositiv()
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                },
                egenkapital.kode_2096
            )
        }
    }

    internal val positivToemmerkontoKalkyle = kalkyle("positivToemmerkonto") {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
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

internal object Balanseregnskap {

    internal val kontoForUtsattInntektsfoeringKalkyle = kalkyle("kontoForUtsattInntektsfoering") {
        forAlleForekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_kontoForUtsattInntektsfoering) {
            settFelt(forekomstType.utgaaendeVerdi) {
                forekomstType.inngaaendeVerdi - forekomstType.inntektsfoertVerdi
            }
        }
    }

    internal val sumSkattemessigVerdiAvFinansieltDerivatSomEiendelKalkyle =
        kalkyle("sumSkattemessigVerdiAvFinansieltDerivatSomEiendel") {
            settUniktFelt(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_sumSkattemessigVerdiAvFinansieltDerivatSomEiendel) {
                forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_finansieltDerivatSomEiendel) summerVerdiFraHverForekomst {
                    forekomstType.skattemessigVerdi.tall()
                }
            }
        }

    internal val sumSkattemessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning =
        kalkyle("sumSkattemessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning") {
            settUniktFelt(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_sumSkattemessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning) {
                forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_finansieltDerivatSomGjeldEllerAvsetning) summerVerdiFraHverForekomst {
                    forekomstType.skattemessigVerdi.tall()
                }
            }
        }

    internal val sumRegnskapsmessigVerdiAvFinansieltDerivatSomEiendel =
        kalkyle("sumRegnskapsmessigVerdiAvFinansieltDerivatSomEiendel") {
            settUniktFelt(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_sumRegnskapsmessigVerdiAvFinansieltDerivatSomEiendel) {
                forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_finansieltDerivatSomEiendel) summerVerdiFraHverForekomst {
                    forekomstType.regnskapsmessigVerdi.tall()
                }
            }
        }

    internal val sumRegnskapsmessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning =
        kalkyle("sumRegnskapsmessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning") {
            settUniktFelt(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_sumRegnskapsmessigVerdiAvFinansieltDerivatSomGjeldEllerAvsetning) {
                forekomsterAv(modell.balanseregnskap_annenSpesifikasjonAvBalanseregnskap_finansieltDerivatSomGjeldEllerAvsetning) summerVerdiFraHverForekomst {
                    forekomstType.regnskapsmessigVerdi.tall()
                }
            }
        }
}