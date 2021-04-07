object SpesifikasjonAvBalanseLineaereAvskrivninger : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val antallAarErhvervet: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.antallAarErhvervet")
        val antallMaanederEidIAaret: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.antallMaanederEidIAaret")
        val aaretsInntektsfoeringAvNegativSaldoGrunnlag: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldoGrunnlag")
    }

    var utgaaendeVerdiKalkyle = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
        {
            der(
                lineaertavskrevetAnleggsmiddel, {
                    it.inngaaendeVerdi -
                        it.offentligTilskudd +
                        it.justeringAvInngaaendeMva +
                        it.justeringForAapenbarVerdiendring -
                        it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                        it.aaretsAvskrivning -
                        it.vederlagVedRealisasjonOgUttak +
                        it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                        it.gevinstOverfoertTilGevinstOgTapskonto -
                        it.tapOverfoertTilGevinstOgTapskonto +
                        it.verdiOverfoertFraPaakostningVedRealisasjon -
                        it.verdiOverfoertTilDriftsmiddelVedRealisasjon somFelt lineaertavskrevetAnleggsmiddel.utgaaendeVerdi.nullHvisNegativt()
                },
                beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErStoerreEnn(0))
            )
        },
        {
            der(
                lineaertavskrevetAnleggsmiddel, {
                    it.anskaffelseskost +
                        it.paakostning -
                        it.offentligTilskudd +
                        it.justeringAvInngaaendeMva +
                        it.justeringForAapenbarVerdiendring -
                        it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                        it.aaretsAvskrivning -
                        it.vederlagVedRealisasjonOgUttak +
                        it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                        it.gevinstOverfoertTilGevinstOgTapskonto -
                        it.tapOverfoertTilGevinstOgTapskonto somFelt lineaertavskrevetAnleggsmiddel.utgaaendeVerdi.nullHvisNegativt()
                },
                beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErLik(0))
            )
        }
    )

    private val kalkyler = Kalkyletre(
        utgaaendeVerdiKalkyle
    ).medPostprosessering(this)

    override fun getKalkyletre(): Kalkyletre {
        return kalkyler
    }

    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == beregnedeFelter.antallAarErhvervet.key
                || it.key == beregnedeFelter.antallMaanederEidIAaret.key
                || it.key == beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag.key
                )
        }
    }
}