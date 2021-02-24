package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

internal object GevinstOgTapskonto : HarKalkyletre {
    private val forekomsterGevinstOgTapskonto = itererForekomster forekomsterAv gevinstOgTapskonto
    private val grunnlagForAaretsInntektsOgFradragsfoeringKalkyle = forekomsterGevinstOgTapskonto forVerdi {
        it.inngaaendeVerdi + it.sumGevinstVedRealisasjonOgUttak - it.sumTapVedRealisasjonOgUttak somFelt gevinstOgTapskonto.grunnlagForAaretsInntektsOgFradragsfoering
    }

    private val inntektFraGevinstOgTapskontoKalkyle = forekomsterGevinstOgTapskonto forVerdier (
        listOf(
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErStoerreEnn(15000)) * f.satsForInntektsfoeringOgInntektsfradrag.prosent() somFelt gevinstOgTapskonto.inntektFraGevinstOgTapskonto },
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMellom(0, 15000)) somFelt gevinstOgTapskonto.inntektFraGevinstOgTapskonto },
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMellom(-15000, -1)) somFelt gevinstOgTapskonto.inntektsfradragFraGevinstOgTapskonto.abs() },
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMindreEnn(-15000)) * f.satsForInntektsfoeringOgInntektsfradrag.prosent() somFelt gevinstOgTapskonto.inntektsfradragFraGevinstOgTapskonto.abs() }
        )
        )

    private val utgaaendeVerdiKalkyle = forekomsterGevinstOgTapskonto forVerdier (
        listOf(
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(Specifications.erPositiv()) - f.inntektFraGevinstOgTapskonto somFelt gevinstOgTapskonto.utgaaendeVerdi },
            { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(Specifications.erNegativ()) + f.inntektsfradragFraGevinstOgTapskonto somFelt gevinstOgTapskonto.utgaaendeVerdi }

        )
        )

    private val kalkyletre = Kalkyletre(grunnlagForAaretsInntektsOgFradragsfoeringKalkyle, inntektFraGevinstOgTapskontoKalkyle, utgaaendeVerdiKalkyle)
    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}
