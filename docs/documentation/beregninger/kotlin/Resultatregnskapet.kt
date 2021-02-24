package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

/**
 *  Kodelister for dette er her: https://git.aurora.skead.no/projects/KOLI/repos/kodeliste/browse/formuesOgInntektsskatt
 *
 *
 *  inntektFraGevinstOgTapskonto er  et oppsummert felt fra Forekomster under GevinstOgTapskonto. Denne verdien skal inn som
 *  annenDriftsinntekt med type/konto 3890. Innslaget som er en forekomst av annenDriftsinntekt
 *  med annenDriftsinntektstype (3890) er således en sum
 *  av denne typen. Det skal ikke være mer enn en sum per type i denne listen.
 *
 *
 */
internal object Resultatregnskapet : HarKalkyletre {
    val salgsinntekterKalkyle =
        summer forekomsterAv salgsinntekter.salgsinntekt forVerdi { it.beloep }

    val annenDriftsinntektKalkyle =
        summer forekomsterAv annenDriftsinntekt forVerdi { it.beloep }

    val sumDriftsinntekterKalkyle: Kalkyle =
        (salgsinntekterKalkyle + annenDriftsinntektKalkyle) verdiSom sumDriftsinntekt

    private val aaretsAvskrivningForSaldoavskrevetAnleggsmiddel = summer gitt ForekomstOgVerdi(virksomhet, {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }) forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning }

    private val aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel = summer gitt ForekomstOgVerdi(virksomhet, {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }) forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning }

    val aaretsAvskrivning =
        aaretsAvskrivningForSaldoavskrevetAnleggsmiddel + aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel verdiSom NyForekomst(
            annenDriftskostnad,
            resultatOgBalansekonti_2020.annenDriftskostnad.kode_6000.kode,
            annenDriftskostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, resultatOgBalansekonti_2020.annenDriftskostnad.kode_6000.kode)
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
        summeringGevinstOgTap forekomsterAv gevinstOgTapskonto filter
            { it.inntektsfradragFraGevinstOgTapskonto.filterFelt(derVerdiErStoerreEnn(0)) } forVerdi { it.inntektsfradragFraGevinstOgTapskonto } verdiSom NyForekomst(
            annenDriftskostnad,
            kode_7890.kode,
            annenDriftskostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7890.kode)
                )
            }
        )

    private val tilbakefoertKostnadForPrivatBrukAvNaeringsbil =
        summer forekomsterAv transportmiddelINaering filter
            { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil.filterFelt(derVerdiErStoerreEnn(0)) } forVerdi { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil * -1 } verdiSom NyForekomst(
            annenDriftskostnad,
            kode_7099.kode,
            annenDriftskostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7099.kode)
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
        aaretsAvskrivning,
        annenDriftsinntektstypeInntektKalkyle,
        annenDriftsinntektstypeFradragKalkyle,
        tilbakefoertKostnadForPrivatBrukAvNaeringsbil,
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
