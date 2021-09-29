internal object BalanseFormueOgGjeld : HarKalkyletre {
    private val logger = KotlinLogging.logger { }

    fun getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi)
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

    fun getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi)
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
        balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean,
    ): Boolean {
        val kodeVerdi = Kodeliste2021Helper.kodeliste[it as String]

        if (kodeVerdi == null) {
            logger.warn("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering: {}",it)
            return false
        }
        return kodeVerdi.kodetillegg?.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi
    }

    fun getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi) }
        return Specifications.og(
            FeltSpecification(
                gjeld.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(gjeld.ekskluderesFraSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(gjeld.type, balansekontoSpecification),
        )
    }

    fun getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi) }
        return Specifications.og(
            FeltSpecification(
                langsiktigGjeld.gjeld.type,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(langsiktigGjeld.gjeld.ekskluderesFraSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(langsiktigGjeld.gjeld.type, balansekontoSpecification),
        )
    }

    private fun summerAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv balanseverdi filter {
            getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv balanseverdiForOmloepsmiddel.balanseverdi filter {
            getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv gjeld filter {
            getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.beloep }
    }

    private fun summerLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv langsiktigGjeld.gjeld filter {
            getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.beloep }
    }

    internal val verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(true) + summerOmloepsmiddel(true) verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    internal val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(false) + summerOmloepsmiddel(false) verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    internal val samletGjeldKalkyle =
        summerKortsiktigGjeld(false) + summerLangsiktigGjeld(false) verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.samletGjeld,
            feltMedFasteVerdier = { emptyList() }
        )

    internal val kalkyletre = Kalkyletre(
        verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle,
        formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle,
        samletGjeldKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}