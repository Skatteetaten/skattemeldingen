internal object Resultatregnskapet : HarKalkyletre {

    private val regnskapspliktstype1Og5Filter = summer gitt ForekomstOgVerdi(virksomhet) {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }

    private val summerHvisRegnskapsplikttype2 = summer gitt ForekomstOgVerdi(virksomhet) {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_2
            )
        )
    }

    private fun salgsinntekterMedKategori(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori
    ): SammensattUttrykk<salgsinntekt.inntekt> {
        return summer forekomsterAv salgsinntekt.inntekt filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.salgsinntektKategori(it) == kategori
            }
        }
    }

    private val sumPositivSalgsinntektKalkyle =
        salgsinntekterMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.POSITIV) forVerdi { it.beloep }

    private val sumNegativSalgsinntektKalkyle =
        salgsinntekterMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV) forVerdi { it.beloep }

    internal val sumSalgsinntektKalkyle = sumPositivSalgsinntektKalkyle - sumNegativSalgsinntektKalkyle

    val sumAnnenDriftsinntektKalkyle =
        summer forekomsterAv annenDriftsinntekt.inntekt forVerdi { it.beloep }

    val sumDriftsinntekterKalkyle: Kalkyle =
        (sumSalgsinntektKalkyle + sumAnnenDriftsinntektKalkyle) verdiSom sumDriftsinntekt

    internal val aaretsAvskrivningForSaldoavskrevetAnleggsmiddel = summer gitt ForekomstOgVerdi(virksomhet, {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }) forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning }

    internal val aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel = summer gitt ForekomstOgVerdi(virksomhet, {
        it.regnskapspliktstype.filterFelt(
            Specifications.harEnAvVerdiene(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_5
            )
        )
    }) forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning }

    internal val aaretsAvskrivning =
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
            forekomstTypeSpesifikasjon = annenDriftsinntekt.inntekt,
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
            forekomstTypeSpesifikasjon = annenDriftsinntekt.inntekt,
            idVerdi = kode_3895.kode,
            feltKoordinat = annenDriftsinntekt.inntekt.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3895.kode)
                )
            }
        )

    internal val annenDriftsinntektstypeFradragKalkyle =
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

    internal val inntektFraToemmerkontoKalkyle =
        summeringGevinstOgTap forekomsterAv skogOgToemmerkonto forVerdi { it.inntektFraToemmerkonto } verdiSom NyForekomst(
            forekomstTypeSpesifikasjon = annenDriftsinntekt.inntekt,
            idVerdi = kode_3910.kode,
            feltKoordinat = annenDriftsinntekt.inntekt.beloep,
            feltMedFasteVerdier =
            {
                listOf(
                    FeltOgVerdi(it.type, kode_3910.kode)
                )
            }
        )

    internal val inntektsfradragFraToemmerkontoKalkyle =
        summeringGevinstOgTap forekomsterAv skogOgToemmerkonto forVerdi { it.inntektsfradragFraToemmerkonto } verdiSom NyForekomst(
            annenDriftskostnad.kostnad,
            kode_7911.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7911.kode)
                )
            }
        )

    internal val andelAvDriftsresultatOverfoertTilToemmerkontoKalkyle =
        summeringGevinstOgTap forekomsterAv skogOgToemmerkonto forVerdi { it.andelAvDriftsresultatOverfoertTilToemmerkonto } verdiSom NyForekomst(
            annenDriftskostnad.kostnad,
            kode_7910.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7910.kode)
                )
            }
        )

    internal val tilbakefoertKostnadForPrivatBrukAvNaeringsbil =
        summer forekomsterAv transportmiddelINaering filter
            { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil.filterFelt(Specifications.derVerdiErStoerreEnnEllerLik(0)) } forVerdi { it.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil } verdiSom NyForekomst(
            annenDriftskostnad.kostnad,
            kode_7099.kode,
            annenDriftskostnad.kostnad.beloep,
            {
                listOf(
                    FeltOgVerdi(it.type, kode_7099.kode)
                )
            }
        )

    private fun varekostnaderMedKategori(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori
    ): SammensattUttrykk<varekostnad.kostnad> {
        return summer forekomsterAv varekostnad.kostnad filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.varekostnadKategori(it) == kategori
            }
        }
    }

    private val sumPositivVarekostnadKalkyle =
        varekostnaderMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.POSITIV) forVerdi { it.beloep }

    private val sumNegativVarekostnadKalkyle =
        varekostnaderMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV) forVerdi { it.beloep }

    val sumVarekostnadKalkyle = sumPositivVarekostnadKalkyle - sumNegativVarekostnadKalkyle

    val sumLoennskostnadKalkyle = summer forekomsterAv loennskostnad.kostnad forVerdi { it.beloep }

    private fun andreDriftskostnaderMedKategori(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori
    ): SammensattUttrykk<annenDriftskostnad.kostnad> {
        return summer forekomsterAv annenDriftskostnad.kostnad filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.annenDriftskostnadKategori(it) == kategori
            }
        }
    }

    private val sumPositivAnnenDriftskostnadKalkyle =
        andreDriftskostnaderMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.POSITIV) forVerdi { it.beloep }

    private val sumNegativAnnenDriftskostnadKalkyle =
        andreDriftskostnaderMedKategori(KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV) forVerdi { it.beloep }

    val sumAnnenDriftskostnadKalkyle = sumPositivAnnenDriftskostnadKalkyle - sumNegativAnnenDriftskostnadKalkyle

    internal val sumDriftskostnaderKalkyle =
        (sumVarekostnadKalkyle + sumLoennskostnadKalkyle + sumAnnenDriftskostnadKalkyle) verdiSom sumDriftskostnad

    val sumFinansinntektKalkyle =
        summer forekomsterAv finansinntekt.inntekt forVerdi { it.beloep } verdiSom sumFinansinntekt
    val sumFinanskostnadKalkyle =
        summer forekomsterAv finanskostnad.kostnad forVerdi { it.beloep } verdiSom sumFinanskostnad

    private fun ekstraordinaerePosterMedKategoriForRegnskapsplikttype2(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori
    ): SammensattUttrykk<ekstraordinaerPost.post> {
        return summerHvisRegnskapsplikttype2 forekomsterAv ekstraordinaerPost.post filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.ekstraordinaerPostKategori(it) == kategori
            }
        }
    }

    private val sumPositivEkstraordinaerPostKalkyle =
        ekstraordinaerePosterMedKategoriForRegnskapsplikttype2(
            KodelisteResultatregnskapOgBalanse.Kategori.POSITIV
        ) forVerdi { it.beloep }

    private val sumNegativEkstraordinaerPostKalkyle =
        ekstraordinaerePosterMedKategoriForRegnskapsplikttype2(
            KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV
        ) forVerdi { it.beloep }

    val sumEkstraordinaerPostKalkyle = (sumPositivEkstraordinaerPostKalkyle
        - sumNegativEkstraordinaerPostKalkyle) verdiSom sumEkstraordinaerPost

    private fun skattekostnaderMedKategoriForRegnskapsplikttype2(
        kategori: KodelisteResultatregnskapOgBalanse.Kategori
    ): SammensattUttrykk<skattekostnad.kostnad> {
        return summerHvisRegnskapsplikttype2 forekomsterAv skattekostnad.kostnad filter { forekomst ->
            FeltSpecification(forekomst.type) {
                KodelisteResultatregnskapOgBalanse.skattekostnadKategori(it) == kategori
            }
        }
    }

    private val sumPositivSkattekostnadKalkyle =
        skattekostnaderMedKategoriForRegnskapsplikttype2(
            KodelisteResultatregnskapOgBalanse.Kategori.POSITIV
        ) forVerdi { it.beloep }

    private val sumNegativSkattekostnadKalkyle =
        skattekostnaderMedKategoriForRegnskapsplikttype2(
            KodelisteResultatregnskapOgBalanse.Kategori.NEGATIV
        ) forVerdi { it.beloep }

    val sumSkattekostnadKalkyle = (sumPositivSkattekostnadKalkyle
        - sumNegativSkattekostnadKalkyle) verdiSom sumSkattekostnad

    val aarsresultatKalkyle =
        (sumDriftsinntekterKalkyle - sumDriftskostnaderKalkyle) +
            (sumFinansinntektKalkyle - sumFinanskostnadKalkyle) +
            (sumEkstraordinaerPostKalkyle - sumSkattekostnadKalkyle) verdiSom aarsresultat

    private val tre = Kalkyletre(
        aaretsAvskrivning,
        annenDriftsinntektstypeInntektKalkyle,
        annenDriftsinntektstypeFradragKalkyle,
        tilbakefoertKostnadForPrivatBrukAvNaeringsbil,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        inntektFraToemmerkontoKalkyle,
        inntektsfradragFraToemmerkontoKalkyle,
        andelAvDriftsresultatOverfoertTilToemmerkontoKalkyle,
        sumDriftsinntekterKalkyle,
        sumDriftskostnaderKalkyle,
        sumFinansinntektKalkyle,
        sumFinanskostnadKalkyle,
        sumEkstraordinaerPostKalkyle,
        sumSkattekostnadKalkyle,
        aarsresultatKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return tre
    }
}
