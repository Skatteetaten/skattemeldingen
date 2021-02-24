package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler


internal object Balanse : HarKalkyletre {

    private fun lagBalanseForekomst(saldogruppe: String, kodeVerdi: KodeVerdi, filter: Specification<Any>): Kalkyle {
        return summer forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.saldogruppe.filterFelt(
                    Specifications.derVerdiErLik(saldogruppe)
                ), it.utgaaendeVerdi.filterFelt(filter)
            )
        } forVerdi { it.utgaaendeVerdi } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = balanseverdiForAnleggsmiddel,
            idVerdi = kodeVerdi.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.skattemessigVerdi,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.anleggsmiddeltype, kodeVerdi.kode)
                )
            }
        )
    }

    private fun lagBalanseForGevinstOgTapskonto(
        kodeVerdi: KodeVerdi,
        filter: Specification<Any>

    ): Kalkyle {
        return summer forekomsterAv gevinstOgTapskonto filter { it.utgaaendeVerdi.filterFelt(filter) } forVerdi {
            it.utgaaendeVerdi.der(filter) * -1
        } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = balanseverdiForAnleggsmiddel,
            idVerdi = kodeVerdi.kode,
            feltKoordinat = balanseverdiForAnleggsmiddel.skattemessigVerdi,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.anleggsmiddeltype, kodeVerdi.kode)
                )
            }
        )
    }

    private fun lagBalanseForNegativSaldo(): Kalkyle {
        return summer forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.saldogruppe.filterFelt(
                    Specifications.harEnAvVerdiene(
                        Saldogruppe.a,
                        Saldogruppe.c,
                        Saldogruppe.c2,
                        Saldogruppe.d,
                        Saldogruppe.j
                    )
                ), it.utgaaendeVerdi.filterFelt(derVerdiErMindreEnn(0))
            )
        } forVerdi {
            it.utgaaendeVerdi * -1  //TODO: hvorfor fungerer ikke abs her
        } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = egenkapital,
            idVerdi = kode_2095.kode,
            feltKoordinat = egenkapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.egenkapitaltype, kode_2095.kode)
                )
            }
        )
    }

    internal val goodWill = lagBalanseForekomst(Saldogruppe.b, kode_1080, Specifications.allwaysTrue())

    internal val forretningsBygg = lagBalanseForekomst(Saldogruppe.i, kode_1105, Specifications.allwaysTrue())

    internal val byggAnleggHotell = lagBalanseForekomst(Saldogruppe.h, kode_1115, Specifications.allwaysTrue())

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseForekomst(Saldogruppe.g, kode_1117, Specifications.allwaysTrue())

    internal val fastTekniskInstallasjonIByggninger =
        lagBalanseForekomst(Saldogruppe.j, kode_1120, derVerdiErStoerreEnn(0))

    internal val personbilerMaskinerInventar = lagBalanseForekomst(Saldogruppe.d, kode_1205, derVerdiErStoerreEnn(0))

    internal val skipRigger = lagBalanseForekomst(Saldogruppe.e, kode_1221, Specifications.allwaysTrue())

    internal val flyHelikopter = lagBalanseForekomst(Saldogruppe.f, kode_1225, Specifications.allwaysTrue())

    internal val vareOgLastebilerBusser = lagBalanseForekomst(Saldogruppe.c, kode_1238, derVerdiErStoerreEnn(0))

    internal val varebilerMedNullutslipp = lagBalanseForekomst(Saldogruppe.c2, kode_1239, derVerdiErStoerreEnn(0))

    internal val kontormaskiner = lagBalanseForekomst(Saldogruppe.a, kode_1280, derVerdiErStoerreEnn(0))

    internal val negativGevinstOgTapskonto = lagBalanseForGevinstOgTapskonto(kode_1296, derVerdiErMindreEnn(0))

    internal val sumAnleggsmiddelSkattemessigVerdiKalkyle =
        summer forekomsterAv balanseverdiForAnleggsmiddel forVerdi {
            it.skattemessigVerdi
        } verdiSom sumAnleggsmiddelSkattemessigVerdi

    internal val sumOmloepsmiddelSkattemessigVerdiKalkyle =
        summer forekomsterAv balanseverdiForOmloepsmiddel forVerdi {
            it.skattemessigVerdi
        } verdiSom sumOmloepsmiddelSkattemessigVerdi

    internal val sumEiendelSkattemessigVerdiKalkyle = sumAnleggsmiddelSkattemessigVerdiKalkyle.plus(
        sumOmloepsmiddelSkattemessigVerdiKalkyle
    ) verdiSom sumEiendelSkattemessigVerdi

    internal val sumLangsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv langsiktigGjeld forVerdi {
            it.skattemessigVerdi
        } verdiSom sumLangsiktigGjeldSkattemessigVerdi

    internal val sumKortsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv kortsiktigGjeld forVerdi {
            it.skattemessigVerdi
        } verdiSom sumKortsiktigGjeldSkattemessigVerdi

    internal val negativSaldoKalkyle = lagBalanseForNegativSaldo()

    internal val positivGevinstOgTapskonto = summer forekomsterAv gevinstOgTapskonto forVerdi {
        it.utgaaendeVerdi.der(derVerdiErStoerreEnn(0)) * if (false) {
            -1
        } else {
            1
        }
    } verdiSom NyForekomst(
        forekomststTypeSpesifikasjon = egenkapital,
        idVerdi = kode_2096.kode,
        feltKoordinat = egenkapital.beloep,
        feltMedFasteVerdier =
        {
            listOf(
                FeltOgVerdi(it.egenkapitaltype, kode_2096.kode)
            )
        }
    )

    internal val sumEgenkapitalKalkyle =
        summer forekomsterAv egenkapital forVerdi {
            it.beloep
        } verdiSom sumEgenkapital

    internal val sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle =
        sumLangsiktigGjeldSkattemessigVerdiKalkyle.plus(sumKortsiktigGjeldSkattemessigVerdiKalkyle)
            .plus(sumEgenkapitalKalkyle) verdiSom sumGjeldOgEgenkapitalSkattemessigVerdi

    private val kalkyletre = Kalkyletre(
        goodWill,
        forretningsBygg,
        byggAnleggHotell,
        elektrotekniskUtrustningIKraftforetak,
        fastTekniskInstallasjonIByggninger,
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
        positivGevinstOgTapskonto,
        sumEgenkapitalKalkyle,
        sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}