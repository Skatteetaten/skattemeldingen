/**
 *  https://wiki.sits.no/display/SIR/Kalkyler+resultatregnskap+for+regnskapspliktstype+1+og+5
 *
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

    private val regnskapspliktstype1Og5Filter = summer gitt ForekomstOgVerdi(virksomhet) {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }
    val salgsinntekterKalkyle =
        summer forekomsterAv salgsinntekt.inntekt forVerdi { it.beloep }

    val annenDriftsinntektKalkyle =
        summer forekomsterAv annenDriftsinntekt.inntekt forVerdi { it.beloep }

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
            annenDriftskostnad.kostnad,
            resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_6000.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_6000.kode)
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
        regnskapspliktstype1Og5Filter forekomsterAv gevinstOgTapskonto forVerdi { it.inntektFraGevinstOgTapskonto } verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = annenDriftsinntekt.inntekt,
            idVerdi = kode_3890.kode,
            feltKoordinat = annenDriftsinntekt.inntekt.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3890.kode)
                )
            }
        )

    private val vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear =
        regnskapspliktstype1Og5Filter forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi { it.vederlagVedRealisasjonOgUttakInntektsfoertIAar }
    private val vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart =
        regnskapspliktstype1Og5Filter forekomsterAv ikkeAvskrivbartAnleggsmiddel forVerdi { it.vederlagVedRealisasjonOgUttakInntektsfoertIAar }
    private val vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo =
        regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.vederlagVedRealisasjonOgUttakInntektsfoertIAar }
    internal val aaretsInntektsfoeringAvNegativSaldo =
        regnskapspliktstype1Og5Filter forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsInntektsfoeringAvNegativSaldo }

    internal val aaretsInntektsfoeringAvNegativSaldoKalkyle =
        vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear +
            vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart +
            vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo +
            aaretsInntektsfoeringAvNegativSaldo verdiSom NyForekomst(
            forekomststTypeSpesifikasjon = annenDriftsinntekt.inntekt,
            idVerdi = kode_3895.kode,
            feltKoordinat = annenDriftsinntekt.inntekt.beloep,
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
            annenDriftskostnad.kostnad,
            kode_7890.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7890.kode)
                )
            }
        )

    private val tilbakefoertKostnadForPrivatBrukAvNaeringsbil =
        summer forekomsterAv transportmiddelINaering filter
            { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil.filterFelt(Specifications.derVerdiErStoerreEnnEllerLik(0)) } forVerdi { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil * -1 } verdiSom NyForekomst(
            annenDriftskostnad.kostnad,
            kode_7099.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7099.kode)
                )
            }
        )

    val sumVarekostnadKalkyle = summer forekomsterAv varekostnad.kostnad forVerdi { it.beloep }
    val sumLoennskostnadKalkyle = summer forekomsterAv loennskostnad.kostnad forVerdi { it.beloep }
    val sumAnnenDriftskostnadKalkyle = summer forekomsterAv annenDriftskostnad.kostnad forVerdi { it.beloep }

    internal val sumDriftskostnaderKalkyle =
        (sumVarekostnadKalkyle + sumLoennskostnadKalkyle + sumAnnenDriftskostnadKalkyle) verdiSom sumDriftskostnad

    val sumFinansinntektKalkyle =
        summer forekomsterAv finansinntekt.inntekt forVerdi { it.beloep } verdiSom sumFinansinntekt
    val sumFinanskostnadKalkyle =
        summer forekomsterAv finanskostnad.kostnad forVerdi { it.beloep } verdiSom sumFinanskostnad

    val sumEkstraordinaerePosterKalkyle =
        summer forekomsterAv ekstraordinaerPost.post forVerdi { it.beloep } verdiSom sumEkstraordinaerPost

    val sumSkattekostnadKalkyle =
        summer forekomsterAv skattekostnad.kostnad forVerdi { it.beloep } verdiSom sumSkattekostnad

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
