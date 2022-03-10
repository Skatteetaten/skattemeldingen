package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.codegen.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.FeltOgVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.ForekomstOgVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.NyForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specification
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErMindreEnn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErStoerreEnn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.summer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.Regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.balanseverdiForAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.balanseverdiForOmloepsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.egenkapital
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.gevinstOgTapskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.lineaertavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1080
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1105
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1115
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1117
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1120
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1205
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1221
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1225
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1238
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1239
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1280
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1295
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1296
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1298
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForOmloepsmiddel.kode_1400
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForOmloepsmiddel.kode_1401
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.balanseverdiForOmloepsmiddel.kode_1500
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.egenkapital.kode_2095
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.egenkapital.kode_2096
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.egenkapital.kode_2097
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.resultatregnskapOgBalanse_2021.egenkapital.kode_2098
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldoavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_a
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_b
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_c
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_c2
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_d
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_e
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_f
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_g
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_h
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_i
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.saldogruppe_2021.kode_j
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.skogOgToemmerkonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.spesifikasjonAvSkattemessigVerdiPaaFordring
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.spesifikasjonAvVarelager
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumBalanseverdiForAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumBalanseverdiForEiendel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumBalanseverdiForOmloepsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.virksomhet

internal object Balanse : HarKalkyletre {

    internal val sumBalanseverdiForEiendelKalkyle =
        BalanseverdiForAnleggsmiddel.sumBalanseverdiForAnleggsmiddelKalkyle
            .plus(BalanseverdiForOmloepsmiddel.sumBalanseverdiForOmloepsmiddelKalkyle) verdiSom sumBalanseverdiForEiendel

    private val kalkyletre = Kalkyletre(
        BalanseverdiForAnleggsmiddel.goodWill,
        BalanseverdiForAnleggsmiddel.forretningsBygg,
        BalanseverdiForAnleggsmiddel.byggAnleggHotell,
        BalanseverdiForAnleggsmiddel.elektrotekniskUtrustningIKraftforetak,
        BalanseverdiForAnleggsmiddel.fastTekniskInstallasjonIByggninger,
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
        Egenkapital.negativSaldoKalkyle,
        Egenkapital.ufrivilligRealisasjonKalkyle,
        Egenkapital.positivGevinstOgTapskonto,
        Egenkapital.positivToemmerkonto,
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}

internal object BalanseverdiForAnleggsmiddel {

    private fun lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(
        saldogruppe: String,
        kodeVerdi: KodeVerdi,
        filter: Specification<Any>
    ): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.saldogruppe.filterFelt(
                    Specifications.derVerdiErLik(saldogruppe)
                ),
                it.utgaaendeVerdi.filterFelt(filter),
                it.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.filterFelt(Specifications.erUsannEllerNull())
            )
        } forVerdi { it.utgaaendeVerdi } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForAnleggsmiddel.balanseverdi,
            idVerdi = kodeVerdi.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kodeVerdi.kode)
                )
            }
        )
    }

    internal val goodWill =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_b.kode, kode_1080, Specifications.allwaysTrue())

    internal val forretningsBygg =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_i.kode, kode_1105, Specifications.allwaysTrue())

    internal val byggAnleggHotell =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_h.kode, kode_1115, Specifications.allwaysTrue())

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_g.kode, kode_1117, Specifications.allwaysTrue())

    internal val fastTekniskInstallasjonIByggninger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_j.kode, kode_1120, derVerdiErStoerreEnn(0))

    internal val personbilerMaskinerInventar =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_d.kode, kode_1205, derVerdiErStoerreEnn(0))

    internal val skipRigger =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_e.kode, kode_1221, Specifications.allwaysTrue())

    internal val flyHelikopter =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_f.kode, kode_1225, Specifications.allwaysTrue())

    internal val vareOgLastebilerBusser =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_c.kode, kode_1238, derVerdiErStoerreEnn(0))

    internal val varebilerMedNullutslipp =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_c2.kode, kode_1239, derVerdiErStoerreEnn(0))

    internal val kontormaskiner =
        lagBalanseverdiFraSaldoavskrevetAnleggsmiddel(kode_a.kode, kode_1280, derVerdiErStoerreEnn(0))

    internal val negativGevinstOgTapskonto =
        regnskapspliktstype1Og5Filter forekomsterAv gevinstOgTapskonto filter {
            it.utgaaendeVerdi.filterFelt(derVerdiErMindreEnn(0))
        } forVerdi {
            it.utgaaendeVerdi.der(derVerdiErMindreEnn(0)) * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForAnleggsmiddel.balanseverdi,
            idVerdi = kode_1296.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1296.kode)
                )
            }
        )

    internal val negativToemmerkonto =
        regnskapspliktstype1Og5Filter forekomsterAv skogOgToemmerkonto filter {
            it.utgaaendeVerdiPaaToemmerkonto.filterFelt(derVerdiErMindreEnn(0))
        } forVerdi {
            it.utgaaendeVerdiPaaToemmerkonto.der(derVerdiErMindreEnn(0)) * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForAnleggsmiddel.balanseverdi,
            idVerdi = kode_1298.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1298.kode)
                )
            }
        )

    internal val driftsmidlerSomAvskrivesLineaertKalkyle =
        regnskapspliktstype1Og5Filter forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi {
            it.utgaaendeVerdi
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForAnleggsmiddel.balanseverdi,
            idVerdi = kode_1295.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1295.kode)
                )
            }
        )

    internal val sumBalanseverdiForAnleggsmiddelKalkyle =
        summer forekomsterAv balanseverdiForAnleggsmiddel.balanseverdi forVerdi {
            it.beloep
        } verdiSom sumBalanseverdiForAnleggsmiddel
}

internal object BalanseverdiForOmloepsmiddel {

    internal val kundefordringKalkyle =
        regnskapspliktstype1Filter forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdi {
            it.skattemessigVerdiPaaKundefordring
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForOmloepsmiddel.balanseverdi,
            idVerdi = kode_1500.kode,
            feltKoordinat = balanseverdiForOmloepsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1500.kode)
                )
            }
        )

    internal val balanseverdi1400Kalkyle =
        regnskapspliktstype1Og5Filter forekomsterAv spesifikasjonAvVarelager forVerdi {
            it.sumVerdiAvVarelager - it.selvprodusertVareBenyttetIEgenProduksjon
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForOmloepsmiddel.balanseverdi,
            idVerdi = kode_1400.kode,
            feltKoordinat = balanseverdiForOmloepsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1400.kode)
                )
            }
        )

    internal val balanseverdi1401Kalkyle =
        regnskapspliktstype1Og5Filter forekomsterAv spesifikasjonAvVarelager forVerdi {
            it.selvprodusertVareBenyttetIEgenProduksjon
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdiForOmloepsmiddel.balanseverdi,
            idVerdi = kode_1401.kode,
            feltKoordinat = balanseverdiForOmloepsmiddel.balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_1401.kode)
                )
            }
        )

    internal val sumBalanseverdiForOmloepsmiddelKalkyle =
        summer forekomsterAv balanseverdiForOmloepsmiddel.balanseverdi forVerdi {
            it.beloep
        } verdiSom sumBalanseverdiForOmloepsmiddel
}

internal object Egenkapital {

    internal val negativSaldoKalkyle =
        regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.saldogruppe.filterFelt(
                    Specifications.harEnAvVerdiene(
                        kode_a.kode,
                        kode_c.kode,
                        kode_c2.kode,
                        kode_d.kode,
                        kode_j.kode
                    )
                ), it.utgaaendeVerdi.filterFelt(derVerdiErMindreEnn(0))
            )
        } forVerdi {
            it.utgaaendeVerdi * -1  //TODO: hvorfor fungerer ikke abs her
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = egenkapital.kapital,
            idVerdi = kode_2095.kode,
            feltKoordinat = egenkapital.kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2095.kode)
                )
            }
        )

    internal val ufrivilligRealisasjonKalkyle =
        regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.filterFelt(Specifications.erSann()),
                it.utgaaendeVerdi.filterFelt(Specifications.erNegativ())
            )
        } forVerdi {
            it.utgaaendeVerdi * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = egenkapital.kapital,
            idVerdi = kode_2097.kode,
            feltKoordinat = egenkapital.kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2097.kode)
                )
            }
        )

    internal val positivGevinstOgTapskonto =
        regnskapspliktstype1Og5Filter forekomsterAv gevinstOgTapskonto forVerdi {
            it.utgaaendeVerdi.der(derVerdiErStoerreEnn(0)) * if (false) {
                -1
            } else {
                1
            }
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = egenkapital.kapital,
            idVerdi = kode_2096.kode,
            feltKoordinat = egenkapital.kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2096.kode)
                )
            }
        )

    internal val positivToemmerkonto =
        regnskapspliktstype1Og5Filter forekomsterAv skogOgToemmerkonto forVerdi {
            it.utgaaendeVerdiPaaToemmerkonto.der(derVerdiErStoerreEnn(0)) * if (false) {
                -1
            } else {
                1
            }
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = egenkapital.kapital,
            idVerdi = kode_2098.kode,
            feltKoordinat = egenkapital.kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2098.kode)
                )
            }
        )
}

private val regnskapspliktstype1Og5Filter = summer gitt ForekomstOgVerdi(virksomhet) {
    it.regnskapspliktstype.filterFelt(
        Specifications.harEnAvVerdiene(
            Regnskapspliktstype.type_1,
            Regnskapspliktstype.type_5
        )
    )
}

private val regnskapspliktstype1Filter = summer gitt ForekomstOgVerdi(virksomhet) {
    it.regnskapspliktstype.filterFelt(Specifications.derVerdiErLik(Regnskapspliktstype.type_1))
}