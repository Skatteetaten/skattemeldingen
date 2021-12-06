internal object Balanse : HarKalkyletre {

    private val regnskapspliktstype1Og5Filter = summer gitt ForekomstOgVerdi(virksomhet) {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }

    private fun lagBalanseForekomst(saldogruppe: String, kodeVerdi: KodeVerdi, filter: Specification<Any>): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.saldogruppe.filterFelt(
                    Specifications.derVerdiErLik(saldogruppe)
                ),
                it.utgaaendeVerdi.filterFelt(filter),
                it.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.filterFelt(Specifications.erUsannEllerNull())
            )
        } forVerdi { it.utgaaendeVerdi } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdi,
            idVerdi = kodeVerdi.kode,
            feltKoordinat = balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kodeVerdi.kode)
                )
            }
        )
    }

    /**
     * Denne fungerer kun hvis det er negative tall i testdata.
     * Dette tegnet: - (minustegn) må være med i testdata
     * Hvis ikke blir det ingen verdi.
     */
    private fun lagBalanseForGevinstOgTapskonto(
        kodeVerdi: KodeVerdi,
        filter: Specification<Any>,

        ): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv gevinstOgTapskonto filter {
            it.utgaaendeVerdi.filterFelt(filter)
        } forVerdi {
            it.utgaaendeVerdi.der(filter) * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = balanseverdi,
            idVerdi = kodeVerdi.kode,
            feltKoordinat = balanseverdi.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kodeVerdi.kode)
                )
            }
        )
    }

    /**
     * Denne fungerer kun hvis det er negative tall i testdata.
     * Dette tegnet: - (minustegn) må være med i testdata
     * Hvis ikke blir det ingen verdi.
     */
    private fun lagBalanseForNegativSaldo(): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
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
            it.utgaaendeVerdi * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = kapital,
            idVerdi = kode_2095.kode,
            feltKoordinat = kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2095.kode)
                )
            }
        )
    }

    private fun lagBalanseForUfrivilligRealisasjon(): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel filter {
            Specifications.og(
                it.erRealisasjonenUfrivilligOgGevinstenBetingetSkattefri.filterFelt(Specifications.erSann()),
                it.utgaaendeVerdi.filterFelt(Specifications.erNegativ())
            )
        } forVerdi {
            it.utgaaendeVerdi * -1
        } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = kapital,
            idVerdi = kode_2097.kode,
            feltKoordinat = kapital.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_2097.kode)
                )
            }
        )
    }

    internal val goodWill = lagBalanseForekomst(kode_b.kode, kode_1080, Specifications.allwaysTrue())

    internal val forretningsBygg = lagBalanseForekomst(kode_i.kode, kode_1105, Specifications.allwaysTrue())

    internal val byggAnleggHotell = lagBalanseForekomst(kode_h.kode, kode_1115, Specifications.allwaysTrue())

    internal val elektrotekniskUtrustningIKraftforetak =
        lagBalanseForekomst(kode_g.kode, kode_1117, Specifications.allwaysTrue())

    internal val fastTekniskInstallasjonIByggninger =
        lagBalanseForekomst(kode_j.kode, kode_1120, derVerdiErStoerreEnn(0))

    internal val personbilerMaskinerInventar = lagBalanseForekomst(kode_d.kode, kode_1205, derVerdiErStoerreEnn(0))

    internal val skipRigger = lagBalanseForekomst(kode_e.kode, kode_1221, Specifications.allwaysTrue())

    internal val flyHelikopter = lagBalanseForekomst(kode_f.kode, kode_1225, Specifications.allwaysTrue())

    internal val vareOgLastebilerBusser = lagBalanseForekomst(kode_c.kode, kode_1238, derVerdiErStoerreEnn(0))

    internal val varebilerMedNullutslipp = lagBalanseForekomst(kode_c2.kode, kode_1239, derVerdiErStoerreEnn(0))

    internal val kontormaskiner = lagBalanseForekomst(kode_a.kode, kode_1280, derVerdiErStoerreEnn(0))

    internal val negativGevinstOgTapskonto = lagBalanseForGevinstOgTapskonto(kode_1296, derVerdiErMindreEnn(0))

    internal val sumAnleggsmiddelSkattemessigVerdiKalkyle =
        summer forekomsterAv balanseverdi forVerdi {
            it.beloep
        } verdiSom sumBalanseverdiForAnleggsmiddel

    internal val sumOmloepsmiddelSkattemessigVerdiKalkyle =
        summer forekomsterAv balanseverdiForOmloepsmiddel.balanseverdi forVerdi {
            it.beloep
        } verdiSom sumBalanseverdiForOmloepsmiddel

    internal val sumEiendelSkattemessigVerdiKalkyle = sumAnleggsmiddelSkattemessigVerdiKalkyle.plus(
        sumOmloepsmiddelSkattemessigVerdiKalkyle
    ) verdiSom sumBalanseverdiForEiendel

    internal val sumLangsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv langsiktigGjeld.gjeld forVerdi {
            it.beloep
        } verdiSom sumLangsiktigGjeld

    internal val sumKortsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv kortsiktigGjeld.gjeld forVerdi {
            it.beloep
        } verdiSom sumKortsiktigGjeld

    internal val negativSaldoKalkyle = lagBalanseForNegativSaldo()

    internal val ufrivilligRealisasjonKalkyle = lagBalanseForUfrivilligRealisasjon()

    internal val positivGevinstOgTapskonto = regnskapspliktstype1Og5Filter forekomsterAv gevinstOgTapskonto forVerdi {
        it.utgaaendeVerdi.der(derVerdiErStoerreEnn(0)) * if (false) {
            -1
        } else {
            1
        }
    } verdiSom NyForekomst(
        forekomstTypeSpesifikasjon = kapital,
        idVerdi = kode_2096.kode,
        feltKoordinat = kapital.beloep,
        feltMedFasteVerdier =
        {
            listOf(
                FeltOgVerdi(it.type, kode_2096.kode)
            )
        }
    )

    internal val sumEgenkapitalKalkyle =
        summer forekomsterAv kapital forVerdi {
            it.beloep
        } verdiSom sumEgenkapital

    internal val sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle =
        sumLangsiktigGjeldSkattemessigVerdiKalkyle.plus(sumKortsiktigGjeldSkattemessigVerdiKalkyle)
            .plus(sumEgenkapitalKalkyle) verdiSom sumGjeldOgEgenkapital

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
        ufrivilligRealisasjonKalkyle,
        positivGevinstOgTapskonto,
        sumEgenkapitalKalkyle,
        sumGjeldOgEgenkapitalSkattemessigVerdiKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}