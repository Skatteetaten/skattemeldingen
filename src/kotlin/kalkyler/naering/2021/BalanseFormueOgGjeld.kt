internal object BalanseFormueOgGjeld : HarKalkyletre {
    private val logger = KotlinLogging.logger { }

    private fun getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForventetVerdi)
            }
        return Specifications.og(
            FeltSpecification(
                balanseverdi.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(
                balanseverdi.ekskluderesFraSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdi.type, balansekontoSpecification),
        )
    }

    private fun getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForventetVerdi)
            }
        return Specifications.og(
            FeltSpecification(
                balanseverdiForOmloepsmiddel.balanseverdi.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(
                balanseverdiForOmloepsmiddel.balanseverdi.ekskluderesFraSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdiForOmloepsmiddel.balanseverdi.type, balansekontoSpecification),
        )
    }

    private fun hentKodeverdi(
        it: Any?,
        balansekontoGirVerdsettingsrabattForventetVerdi: Boolean,
    ): Boolean {
        val kodeVerdi = Kodeliste2021Helper.kodeliste[it as String]

        if (kodeVerdi == null) {
            logger.warn("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering: {}", it)
            return false
        }
        return kodeVerdi.kodetillegg?.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForventetVerdi
    }

    private fun getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForventetVerdi) }
        return Specifications.og(
            FeltSpecification(
                gjeld.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(gjeld.ekskluderesFraSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(gjeld.type, balansekontoSpecification),
        )
    }

    private fun getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForventetVerdi) }
        return Specifications.og(
            FeltSpecification(
                langsiktigGjeld.gjeld.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(langsiktigGjeld.gjeld.ekskluderesFraSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(langsiktigGjeld.gjeld.type, balansekontoSpecification),
        )
    }

    private val regnskapspliktstype1Og5Filter = summer gitt
        ForekomstOgVerdi(virksomhet) {
            it.regnskapspliktstype.filterFelt(
                Specifications.harEnAvVerdiene(
                    Regnskapspliktstype.type_1,
                    Regnskapspliktstype.type_5,
                )
            )
        }

    private fun summerAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv balanseverdi filter {
            getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerOmloepsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv balanseverdiForOmloepsmiddel.balanseverdi filter {
            getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForventetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerKortsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv gjeld filter {
            getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerLangsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi: Boolean): Kalkyle {
        return regnskapspliktstype1Og5Filter forekomsterAv langsiktigGjeld.gjeld filter {
            getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForventetVerdi)
        } forVerdi { it.beloep }
    }

    internal val verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(true) + summerOmloepsmiddel(true) verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    internal val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(false) + summerOmloepsmiddel(false) verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    internal val samletGjeldKalkyle =
        summerKortsiktigGjeld(false) + summerLangsiktigGjeld(false) verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.samletGjeld,
            feltMedFasteVerdier = { emptyList() }
        )

    private fun egenkapitalMedKategori(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori,
    ): SammensattUttrykk<egenkapital.kapital> {
        return summer forekomsterAv egenkapital.kapital filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.egenkapitalKategori(it) == kategori
            }
        }
    }

    internal val kapitalMedPositivKategori =
        egenkapitalMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.POSITIV) forVerdi { it.beloep }
    internal val kapitalMedNegativKategori =
        egenkapitalMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV) forVerdi { it.beloep }

    internal val sumEgenkapitalKalkyle =
        kapitalMedPositivKategori
            .minus(kapitalMedNegativKategori) verdiSom sumEgenkapital

    internal val sumLangsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv langsiktigGjeld.gjeld forVerdi {
            it.beloep
        } verdiSom sumLangsiktigGjeld

    internal val sumKortsiktigGjeldSkattemessigVerdiKalkyle =
        summer forekomsterAv kortsiktigGjeld.gjeld forVerdi {
            it.beloep
        } verdiSom sumKortsiktigGjeld

    internal val sumGjeldOgEgenkapitalKalkyle =
        sumLangsiktigGjeldSkattemessigVerdiKalkyle
            .plus(sumKortsiktigGjeldSkattemessigVerdiKalkyle)
            .plus(sumEgenkapitalKalkyle) verdiSom sumGjeldOgEgenkapital

    internal val kalkyletre = Kalkyletre(
        verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle,
        formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle,
        samletGjeldKalkyle,
        sumEgenkapitalKalkyle,
        sumLangsiktigGjeldSkattemessigVerdiKalkyle,
        sumKortsiktigGjeldSkattemessigVerdiKalkyle,
        sumGjeldOgEgenkapitalKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}