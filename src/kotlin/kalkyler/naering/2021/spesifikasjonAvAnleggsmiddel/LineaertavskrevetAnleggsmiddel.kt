object LineaertavskrevetAnleggsmiddel : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val antallAarErvervet = SyntetiskFelt(lineaertavskrevetAnleggsmiddel, "antallAarErvervet")
        val antallMaanederEidIAaret = SyntetiskFelt(lineaertavskrevetAnleggsmiddel, "antallMaanederEidIAaret")
        val aaretsInntektsfoeringAvNegativSaldoGrunnlag =
            SyntetiskFelt(lineaertavskrevetAnleggsmiddel, ".aaretsInntektsfoeringAvNegativSaldoGrunnlag")
        val korrigeringer = SyntetiskFelt(lineaertavskrevetAnleggsmiddel, "korrigeringer")
        val paakostningEllerAnskaffelseskost =
            SyntetiskFelt(lineaertavskrevetAnleggsmiddel, "paakostningEllerAnskaffelseskost")
    }

    internal val antallAarErvervetKalkyle = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi {
        it.ervervsdato.aar() - naeringsspesifikasjon.inntektsaar somFelt beregnedeFelter.antallAarErvervet.abs()
    }

    internal val hjelpeBeregningForUtgaaendeVerdi =
        itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
            {
                der(it,
                    { it.paakostning somFelt beregnedeFelter.paakostningEllerAnskaffelseskost },
                    it.anskaffelseskost.filterFelt(Specifications.derVerdiErNull())
                )
            },
            {
                der(
                    it,
                    { it.anskaffelseskost somFelt beregnedeFelter.paakostningEllerAnskaffelseskost },
                    it.paakostning.filterFelt(Specifications.derVerdiErNull())
                )
            },
            {
                der(
                    it,
                    { it.paakostning somFelt beregnedeFelter.paakostningEllerAnskaffelseskost }
                )
            },
        )

    internal val utgaaendeVerdiKalkyle =
        itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
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
                    beregnedeFelter.antallAarErvervet.filterFelt(derVerdiErStoerreEnn(0))
                )
            },
            {
                der(
                    lineaertavskrevetAnleggsmiddel, {
                        beregnedeFelter.paakostningEllerAnskaffelseskost -
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
                    beregnedeFelter.antallAarErvervet.filterFelt(derVerdiErLik(0))
                )
            }
        )

    internal val korrigeringerKalkyle = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdi {
        it.justeringAvInngaaendeMva +
            it.justeringForAapenbarVerdiendring -
            it.offentligTilskudd -
            it.vederlagVedRealisasjonOgUttak +
            it.vederlagVedRealisasjonOgUttakInntektsfoertIAar -
            it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
            it.reinvestertBetingetSkattefriSalgsgevinst -
            it.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst somFelt beregnedeFelter.korrigeringer
    }

    internal val grunnlagForAvskrivningOgInntektsfoeringKalkyle =
        itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
            {
                der(
                    lineaertavskrevetAnleggsmiddel, {
                        (it.paakostning + beregnedeFelter.korrigeringer) somFelt lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
                    },
                    it.paakostning.filterFelt(Specifications.derVerdiIkkeErNull()),
                    erErvervetIInntektsaaret()
                )
            },
            {
                der(
                    lineaertavskrevetAnleggsmiddel, {
                        (it.anskaffelseskost + beregnedeFelter.korrigeringer) somFelt lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
                    },
                    it.paakostning.filterFelt(Specifications.derVerdiErNull()),
                    erErvervetIInntektsaaret()
                )
            },
            {
                der(
                    lineaertavskrevetAnleggsmiddel, {
                        (it.inngaaendeVerdi + beregnedeFelter.korrigeringer) somFelt lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
                    },
                    erErvervetFoerInntektsaaret()
                )
            }
        )

    private fun erErvervetIInntektsaaret(): Specification<GeneriskModell> {
        return beregnedeFelter.antallAarErvervet.filterFelt(derVerdiErLik(0))
    }

    private fun erErvervetFoerInntektsaaret(): Specification<GeneriskModell> {
        return beregnedeFelter.antallAarErvervet.filterFelt(derVerdiErStoerreEnn(0))
    }

    private val kalkyler = Kalkyletre(
        hjelpeBeregningForUtgaaendeVerdi,
        antallAarErvervetKalkyle,
        utgaaendeVerdiKalkyle,
        korrigeringerKalkyle,
        grunnlagForAvskrivningOgInntektsfoeringKalkyle
    ).medPostprosessering(this)

    override fun getKalkyletre(): Kalkyletre {
        return kalkyler
    }

    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == beregnedeFelter.antallAarErvervet.key
                || it.key == beregnedeFelter.antallMaanederEidIAaret.key
                || it.key == beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag.key
                || it.key == beregnedeFelter.korrigeringer.key
                || it.key == beregnedeFelter.paakostningEllerAnskaffelseskost.key
                )
        }
    }
}