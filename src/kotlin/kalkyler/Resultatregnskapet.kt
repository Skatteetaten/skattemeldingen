package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

internal object Resultatregnskapet : HarKalkyletre {
    val salgsinntekterKalkyle =
        summer forekomsterAv salgsinntekt forVerdi { it.beloep }

    val annenDriftsinntektKalkyle =
        summer forekomsterAv annenDriftsinntekt forVerdi { it.beloep }

    val sumDriftsinntekterKalkyle: Kalkyle =
        (salgsinntekterKalkyle + annenDriftsinntektKalkyle) verdiSom sumDriftsinntekt

    val driftskostnadstypeKalkyle = summer gitt ForekomstOgVerdi(virksomhet, {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }) forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning } verdiSom NyForekomst(
        annenDriftskostnad,
        kode_6000.kode,
        annenDriftskostnad.beloep,
        {
            listOf(
                FeltOgVerdi(it.type, kode_6000.kode)
            )
        }
    )

    private val summeringGevinstOgTap = summer gitt ForekomstOgVerdi(
        virksomhet,
        {
            it.regnskapspliktstype.filterFelt(
                Specifications.harEnAvVerdiene(
                    Regnskapspliktstype.type_1,
                    Regnskapspliktstype.type_5
                )
            )
        })

    internal val annenDriftsinntektstypeInntektKalkyle =
        summer forekomsterAv gevinstOgTapskonto forVerdi { it.inntektFraGevinstOgTapskonto } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = annenDriftsinntekt,
            idVerdi = kode_3890.kode,
            feltKoordinat = annenDriftsinntekt.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3890.kode)
                )
            }
        )

    internal val aaretsInntektsfoeringAvNegativSaldoKalkyle =
        summer forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsInntektsfoeringAvNegativSaldo } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = annenDriftsinntekt,
            idVerdi = kode_3895.kode,
            feltKoordinat = annenDriftsinntekt.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3895.kode)
                )
            }
        )

    private val annenDriftsinntektstypeFradragKalkyle =
        summeringGevinstOgTap forekomsterAv gevinstOgTapskonto forVerdi { it.inntektsfradragFraGevinstOgTapskonto } verdiSom NyForekomst(
            annenDriftsinntekt,
            kode_3890.kode,
            annenDriftsinntekt.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3890.kode)
                )
            }
        )

    val sumVarekostnadKalkyle = summer forekomsterAv varekostnad forVerdi { it.beloep }
    val sumLoennskostnadKalkyle = summer forekomsterAv loennskostnad forVerdi { it.beloep }
    val sumAnnenDriftskostnadKalkyle = summer forekomsterAv annenDriftskostnad forVerdi { it.beloep }

    internal val sumDriftskostnaderKalkyle =
        (sumVarekostnadKalkyle + sumLoennskostnadKalkyle + sumAnnenDriftskostnadKalkyle) verdiSom sumDriftskostnad

    val sumFinansinntektKalkyle = summer forekomsterAv finansinntekt forVerdi { it.beloep } verdiSom sumFinansinntekt
    val sumFinanskostnadKalkyle = summer forekomsterAv finanskostnad forVerdi { it.beloep } verdiSom sumFinanskostnad

    val sumEkstraordinaerePosterInntektKalkyle = summer forekomsterAv ekstraordinaerPost forVerdi { it.inntekt }
    val sumEkstraordinaerePosterKostnadKalkyle = summer forekomsterAv ekstraordinaerPost forVerdi { it.kostnad }
    val sumEkstraordinaerePosterKalkyle =
        sumEkstraordinaerePosterInntektKalkyle - sumEkstraordinaerePosterKostnadKalkyle verdiSom sumEkstraordinaerPost

    val skattekostnadKalkyle = summer forekomsterAv skattekostnad forVerdi { it.skattekostnad }
    val negativSkattekostnadkalkyle = summer forekomsterAv skattekostnad forVerdi { it.negativSkattekostnad }
    val sumSkattekostnadKalkyle =
        (negativSkattekostnadkalkyle - skattekostnadKalkyle) verdiSom sumSkattekostnad

    val aarsresultatKalkyle =
        (
            sumDriftsinntekterKalkyle -
                sumDriftskostnaderKalkyle) +
            (sumFinansinntektKalkyle - sumFinanskostnadKalkyle) +
            (sumEkstraordinaerePosterKalkyle + sumSkattekostnadKalkyle) verdiSom aarsresultat

    private val tre = Kalkyletre(
        driftskostnadstypeKalkyle,
        annenDriftsinntektstypeInntektKalkyle,
        annenDriftsinntektstypeFradragKalkyle,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        sumDriftsinntekterKalkyle,
        sumDriftskostnaderKalkyle,
        sumFinansinntektKalkyle,
        sumFinanskostnadKalkyle,
        sumEkstraordinaerePosterKalkyle,
        sumSkattekostnadKalkyle,
        aarsresultatKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return tre
    }
}
