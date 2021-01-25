package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler


internal object BalanseFormueOgGjeld : HarKalkyletre {

    fun getFilterForAnleggsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { it != null && Kodeliste2020Helper.kodeliste[it as String]!!.kodetillegg.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi }
        return Specifications.og(
            FeltSpecification(
                balanseverdiForAnleggsmiddel.overfoeresIkkeTilSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdiForAnleggsmiddel.anleggsmiddeltype, balansekontoSpecification),
        )
    }

    fun getFilterForOmloepsmiddel(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { it != null && Kodeliste2020Helper.kodeliste[it as String]!!.kodetillegg.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi }
        return Specifications.og(
            FeltSpecification(
                balanseverdiForOmloepsmiddel.overfoeresIkkeTilSkattemeldingen,
                Specifications.erUsannEllerNull()
            ),
            FeltSpecification(balanseverdiForOmloepsmiddel.omloepsmiddeltype, balansekontoSpecification),
        )
    }

    fun getFilterForKortsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { it != null && Kodeliste2020Helper.kodeliste[it as String]!!.kodetillegg.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi }
        return Specifications.og(
            FeltSpecification(kortsiktigGjeld.overfoeresIkkeTilSkattemeldingen, Specifications.erUsannEllerNull()),
            FeltSpecification(kortsiktigGjeld.kortsiktigGjeldtype, balansekontoSpecification),
        )
    }

    fun getFilterForLangsiktigGjeld(balansekontoGirVerdsettingsrabattForvenetVerdi: Boolean): Specification<GeneriskModell> {
        val balansekontoSpecification =
            Specification<Any> { it != null && Kodeliste2020Helper.kodeliste[it as String]!!.kodetillegg.BalansekontoGirVerdsettingsrabatt == balansekontoGirVerdsettingsrabattForvenetVerdi }
        return Specifications.og(
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

    private val verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(true) + summerOmloepsmiddel(true) verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.verdiFoerVerdsettingsrabattForFormuesobjekterOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    private val formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabattKalkyle =
        summerAnleggsmiddel(false) + summerOmloepsmiddel(false) verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = samletGjeldOgFormuesobjekter,
            idVerdi = "1",
            feltKoordinat = samletGjeldOgFormuesobjekter.formuesverdiForFormuesobjekterIkkeOmfattetAvVerdsettingsrabatt,
            feltMedFasteVerdier = { emptyList() }
        )

    private val samletGjeldKalkyle =
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