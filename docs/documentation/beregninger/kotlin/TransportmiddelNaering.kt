package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

internal object TransportmiddelNaering : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val bilensAlder = SyntetiskFelt(transportmiddelINaering, "bilensAlder")
        val satsBeregningsgrunnlag1 = SyntetiskFelt(transportmiddelINaering, "satsBeregningsgrunnlag1")
        val beregningsgrunnlagAlt1 = SyntetiskFelt(transportmiddelINaering, "beregningsgrunnlagAlt1")
        val prosentAvBilensVerdi = SyntetiskFelt(transportmiddelINaering, "prosentAvBilensVerdi")
        val prosentandelAvFaktiskeKostnader = SyntetiskFelt(transportmiddelINaering, "prosentandelAvFaktiskeKostnader")
        val kmSatsForPrivatBruk = SyntetiskFelt(transportmiddelINaering, "kmSatsForPrivatBruk")
    }

    internal val initielleBeregningerKalkyle = itererForekomster forekomsterAv transportmiddelINaering forVerdi
        { it.registreringsaar - naeringsopplysninger.inntektsaar somFelt bilensAlder.abs() }

    internal val initielleBeregningerAvSatsPersonbil = itererForekomster forekomsterAv transportmiddelINaering filter {
        it.biltype.filterFelt(derVerdiErLik(Biltype.personbil))
    } forVerdier listOf(
        {
            der(
                transportmiddelINaering,
                {
                    somFelt(
                        SyntetiskFeltMedVerdi(
                            1,
                            value,
                            transportmiddelINaering,
                            FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                        )
                    )
                },
                it.antallKilometerYrkeskjoering.filterFelt(derVerdiErMindreEnnEllerLik(40000)),
                bilensAlder.filterFelt(derVerdiErMindreEnnEllerLik(3)),
            )
        },
        {
            der(
                transportmiddelINaering,
                {
                    somFelt(
                        SyntetiskFeltMedVerdi(
                            0.75,
                            value,
                            transportmiddelINaering,
                            FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                        )
                    )
                },
                it.antallKilometerYrkeskjoering.filterFelt(derVerdiErStoerreEnn(40000)),
                bilensAlder.filterFelt(derVerdiErMindreEnnEllerLik(3)),
            )
        },
        {
            der(
                transportmiddelINaering,
                {
                    somFelt(
                        SyntetiskFeltMedVerdi(
                            0.75,
                            value,
                            transportmiddelINaering,
                            FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                        )
                    )
                },
                it.antallKilometerYrkeskjoering.filterFelt(derVerdiErMindreEnnEllerLik(40000)),
                bilensAlder.filterFelt(derVerdiErStoerreEnn(3)),
            )
        },
        {
            der(
                transportmiddelINaering,
                {
                    somFelt(
                        SyntetiskFeltMedVerdi(
                            0.5625,
                            prosent,
                            transportmiddelINaering,
                            FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                        )
                    )
                },
                it.antallKilometerYrkeskjoering.filterFelt(derVerdiErStoerreEnn(40000)),
                bilensAlder.filterFelt(derVerdiErStoerreEnn(3)),
            )
        }

    )

    internal val initielleBeregningerAvSatsElEllerHydrogenbil =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.hydrogenbil,
                    Biltype.elbil
                )
            )
        } forVerdier listOf(
            {
                der(
                    transportmiddelINaering,
                    {
                        somFelt(
                            SyntetiskFeltMedVerdi(
                                0.6,
                                value,
                                transportmiddelINaering,
                                FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                            )
                        )
                    },
                    bilensAlder.filterFelt(derVerdiErMindreEnnEllerLik(3))
                )
            },
            {
                der(
                    transportmiddelINaering,
                    {
                        somFelt(
                            SyntetiskFeltMedVerdi(
                                0.45,
                                value,
                                transportmiddelINaering,
                                FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                            )
                        )
                    },
                    bilensAlder.filterFelt(derVerdiErStoerreEnn(3))
                )
            }
        )

    internal val initielleBeregningerAvSatsVareEllerLastebil =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.varebilKlasse2,
                    Biltype.lastebilMedTotalvektUnder7500Kg
                )
            )
        } forVerdier listOf(
            {
                der(
                    transportmiddelINaering,
                    {
                        somFelt(
                            SyntetiskFeltMedVerdi(
                                1,
                                value,
                                transportmiddelINaering,
                                FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                            )
                        )
                    },
                    bilensAlder.filterFelt(derVerdiErMindreEnnEllerLik(3))
                )
            },
            {
                der(
                    transportmiddelINaering,
                    {
                        somFelt(
                            SyntetiskFeltMedVerdi(
                                0.75,
                                value,
                                transportmiddelINaering,
                                FeltKoordinat(beregnedeFelter.satsBeregningsgrunnlag1.key)
                            )
                        )
                    },
                    bilensAlder.filterFelt(derVerdiErStoerreEnn(3))
                )
            }
        )

    internal val beregningsgrunnlag1Kalkyle = itererForekomster forekomsterAv transportmiddelINaering forVerdi
        {
            it.listeprisSomNy * beregnedeFelter.satsBeregningsgrunnlag1 somFelt beregningsgrunnlagAlt1
        }

    internal val prosentAvBilensVerdiKalkyle = itererForekomster forekomsterAv transportmiddelINaering filter {
        it.biltype.filterFelt(
            harEnAvVerdiene(
                Biltype.personbil,
                Biltype.elbil,
                Biltype.hydrogenbil
            )
        )
    } forVerdi {
        beregningsgrunnlagAlt1.min(TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
            satsVedProsentAvBilensVerdiUnderInnslagspunkt2 +
            beregningsgrunnlagAlt1.andelOver(TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
            satsVedProsentAvBilensVerdiOverInnslagspunkt2 *
            it.antallMaanederTilDisposisjon / 12 somFelt beregnedeFelter.prosentAvBilensVerdi
    }

    internal val prosentAvBilensVerdiVarebilEllerLastebil =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.varebilKlasse2,
                    Biltype.lastebilMedTotalvektUnder7500Kg
                )
            )
        } forVerdi {
            (beregningsgrunnlagAlt1 -
                (it.listeprisSomNy * TransportmiddelSatser.satsVedProsentandelAvBilensVerdiVareEllerLastebil).min(
                    TransportmiddelSatser.bunnfradragSjablongberegningVarebliOgLastebil
                )) *
                TransportmiddelSatser.satsVedProsentAvBilensVerdiUnderVarebilOgLastebil * (it.antallMaanederTilDisposisjon / 12) somFelt beregnedeFelter.prosentAvBilensVerdi
        }

    internal val kmSatsForPrivatBruk = itererForekomster forekomsterAv transportmiddelINaering filter {
        Specifications.og(
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.varebilKlasse2,
                    Biltype.lastebilMedTotalvektUnder7500Kg,
                    Biltype.lastebilMedTotalvekt7500KgEllerMer,
                    Biltype.bilRegistrertFor16PassasjererEllerFlere,
                    Biltype.bilRegistrertFor9PassasjererEllerFlere
                )
            ),
            it.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.filterFelt(Specifications.erSann())
        )
    } forVerdi {
        (it.antallKilometerKjoertIAar - it.antallKilometerYrkeskjoering) * TransportmiddelSatser.kmSats somFelt beregnedeFelter.kmSatsForPrivatBruk
    }

    internal val kmSatsForPrivatBrukLastebil = itererForekomster forekomsterAv transportmiddelINaering filter {
        it.biltype.filterFelt(
            harEnAvVerdiene(
                Biltype.lastebilMedTotalvekt7500KgEllerMer,
                Biltype.bilRegistrertFor16PassasjererEllerFlere,
                Biltype.bilRegistrertFor9PassasjererEllerFlere
            )
        )

    } forVerdi {
        (it.antallKilometerKjoertIAar - it.antallKilometerYrkeskjoering) * TransportmiddelSatser.kmSats somFelt beregnedeFelter.kmSatsForPrivatBruk
    }

    internal val saldoavskrivningPrivatBrukKalkyle = itererForekomster forekomsterAv transportmiddelINaering filter {
        it.biltype.filterFelt(
            harEnAvVerdiene(
                Biltype.personbil,
                Biltype.elbil,
                Biltype.hydrogenbil
            )
        )
    } forVerdi {
        der(it, {
            it.listeprisSomNy *
                bilensAlder.powFrom(sats1SaldoavskrivningPrivatBruk) *
                sats2SaldoavskrivningPrivatBruk * (it.antallMaanederTilDisposisjon / 12) somFelt saldoavskrivningPrivatBruk
        }, FeltSpecification(it.leasingleie, eller(derVerdiErNull(), derVerdiErLik(0))))
    }

    internal val prosentandelAvFaktiskeKostnaderKalkyle =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.personbil,
                    Biltype.elbil,
                    Biltype.hydrogenbil
                )
            )
        } forVerdier listOf(
            {
                der(it, {
                    (it.driftskostnader + it.saldoavskrivningPrivatBruk) *
                        TransportmiddelSatser.satsVedProsentandelAvFaktiskeKostander somFelt prosentandelAvFaktiskeKostnader
                }, FeltSpecification(it.leasingleie, eller(derVerdiErNull(), derVerdiErLik(0))))
            },
            {
                der(it, {
                    (it.driftskostnader + it.leasingleie) *
                        TransportmiddelSatser.satsVedProsentandelAvFaktiskeKostander somFelt prosentandelAvFaktiskeKostnader
                }, FeltSpecification(it.leasingleie, derVerdiErStoerreEnn(0)))
            }
        )

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyle =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.personbil,
                    Biltype.elbil,
                    Biltype.hydrogenbil
                )
            )
        } forVerdi {
            beregnedeFelter.prosentAvBilensVerdi.min(prosentandelAvFaktiskeKostnader) somFelt transportmiddelINaering.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.varebilKlasse2,
                    Biltype.lastebilMedTotalvektUnder7500Kg
                )
            )
        } forVerdier listOf(
            {
                der(
                    transportmiddelINaering,
                    { beregnedeFelter.prosentAvBilensVerdi.min(beregnedeFelter.kmSatsForPrivatBruk) somFelt transportmiddelINaering.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil },
                    it.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.filterFelt(Specifications.erSann())
                )
            },
            {
                der(
                    transportmiddelINaering,
                    { beregnedeFelter.prosentAvBilensVerdi somFelt transportmiddelINaering.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil },
                    it.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.filterFelt(Specifications.erUsann())
                )
            }
        )

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil =
        itererForekomster forekomsterAv transportmiddelINaering filter {
            it.biltype.filterFelt(
                harEnAvVerdiene(
                    Biltype.lastebilMedTotalvekt7500KgEllerMer,
                    Biltype.bilRegistrertFor16PassasjererEllerFlere,
                    Biltype.bilRegistrertFor9PassasjererEllerFlere
                )
            )
        } forVerdi {
            beregnedeFelter.kmSatsForPrivatBruk somFelt transportmiddelINaering.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            initielleBeregningerKalkyle,
            initielleBeregningerAvSatsPersonbil,
            initielleBeregningerAvSatsElEllerHydrogenbil,
            initielleBeregningerAvSatsVareEllerLastebil,
            kmSatsForPrivatBruk,
            kmSatsForPrivatBrukLastebil,
            saldoavskrivningPrivatBrukKalkyle,
            beregningsgrunnlag1Kalkyle,
            prosentAvBilensVerdiKalkyle,
            prosentAvBilensVerdiVarebilEllerLastebil,
            prosentandelAvFaktiskeKostnaderKalkyle,
            tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyle,
            tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil,
            tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil
        ).medPostprosessering(this)
    }

    /**
     * Vi filtrerer vekk feltene
     */
    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == beregnedeFelter.prosentAvBilensVerdi.key
                || it.key == beregningsgrunnlagAlt1.key
                || it.key == bilensAlder.key
                || it.key == beregnedeFelter.satsBeregningsgrunnlag1.key
                || it.key == prosentandelAvFaktiskeKostnader.key
                || it.key == beregnedeFelter.kmSatsForPrivatBruk.key)
        }
    }
}
