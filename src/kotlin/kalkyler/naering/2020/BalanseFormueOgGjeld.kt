internal object BalanseFormueOgGjeld : HarKalkyletre {
    private val logger = KotlinLogging.logger { }

    fun getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi)
            }
        return Specifications.og(
            FeltSpecification(
                balanseverdiForAnleggsmiddel.anleggsmiddeltype,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(
                balanseverdiForAnleggsmiddel.overfoeresIkkeTilSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdiForAnleggsmiddel.anleggsmiddeltype, balansekontoSpecification),
        )
    }

    fun getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> {
                hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi)
            }
        return Specifications.og(
            FeltSpecification(
                balanseverdiForOmloepsmiddel.omloepsmiddeltype,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(
                balanseverdiForOmloepsmiddel.overfoeresIkkeTilSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdiForOmloepsmiddel.omloepsmiddeltype, balansekontoSpecification),
        )
    }

    private fun hentKodeverdi(
        it: Any?,
        balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean,
    ): Boolean {
        val kodeVerdi = Kodeliste2020Helper.kodeliste[it as String]

        if (kodeVerdi == null) {
            logger.warn("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering: {}", it)
            return false
        }
        return kodeVerdi.kodetillegg?.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi
    }

    fun getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi) }
        return Specifications.og(
            FeltSpecification(
                kortsiktigGjeld.kortsiktigGjeldtype,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(kortsiktigGjeld.overfoeresIkkeTilSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(kortsiktigGjeld.kortsiktigGjeldtype, balansekontoSpecification),
        )
    }

    fun getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { hentKodeverdi(it, balansekontoGirVerdsettingsrabattForvenetVerdi) }
        return Specifications.og(
            FeltSpecification(
                langsiktigGjeld.langsiktigGjeldtype,
                Specifications.derVerdiIkkeErNull()
            ),
            FeltSpecification(langsiktigGjeld.overfoeresIkkeTilSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(langsiktigGjeld.langsiktigGjeldtype, balansekontoSpecification),
        )
    }

    private fun summerAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv balanseverdiForAnleggsmiddel filter {
            getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.skattemessigVerdi }
    }

    private fun summerOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv balanseverdiForOmloepsmiddel filter {
            getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.skattemessigVerdi }
    }

    private fun summerKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv kortsiktigGjeld filter {
            getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.skattemessigVerdi }
    }

    private fun summerLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Kalkyle {
        return summer forekomsterAv langsiktigGjeld filter {
            getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi)
        } forVerdi { it.skattemessigVerdi }
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