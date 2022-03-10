internal object SaldoavskrevetAnleggsmiddel : HarKalkyletre, PostProsessering {

    val saldoAvskrevetForekomster = itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel

    internal object beregnedeFelter {
        val aaretsAvskrivningMellomverdi = SyntetiskFelt(
            saldoavskrevetAnleggsmiddel,
            "aaretsAvskrivningMellomverdi"
        )
        val utgaaendeVerdiMellomverdi = SyntetiskFelt(
            saldoavskrevetAnleggsmiddel,
            "utgaaendeVerdiMellomverdi"
        )
    }

    val nedreGrenseForAvskrivningForForretningsbygg =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.derVerdiErLik(saldogruppe_2021.kode_i.kode)
            )
        } forVerdier listOf { it ->
            der(saldoavskrevetAnleggsmiddel, {
                it.historiskKostpris -
                    it.nedskrevetVerdiPr01011984 somFelt it.nedreGrenseForAvskrivning
            }, it.nedskrevetVerdiPr01011984.filterFelt(Specifications.ikke(derVerdiErNull())))
        }

    val grunnlagForAvskrivningOgInntektsFoeringSaldogruppeEogFogGogHogI =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    saldogruppe_2021.kode_e.kode,
                    saldogruppe_2021.kode_f.kode,
                    saldogruppe_2021.kode_g.kode,
                    saldogruppe_2021.kode_h.kode,
                    saldogruppe_2021.kode_i.kode
                )
            )
        } forVerdi {
            it.inngaaendeVerdi +
                it.nyanskaffelse +
                it.paakostning -
                it.offentligTilskudd +
                it.justeringAvInngaaendeMva -
                it.nedskrevetVerdiAvUtskilteDriftsmidler -
                it.vederlagVedRealisasjonOgUttak -
                it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                it.reinvestertBetingetSkattefriSalgsgevinst -
                it.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst somFelt it.grunnlagForAvskrivningOgInntektsfoering
        }

    val aaretsAvskrivningSaldogruppeEogFogHogI = itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
        og(
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    saldogruppe_2021.kode_e.kode,
                    saldogruppe_2021.kode_f.kode,
                    saldogruppe_2021.kode_g.kode,
                    saldogruppe_2021.kode_h.kode,
                    saldogruppe_2021.kode_i.kode
                )
            ),
            it.nedreGrenseForAvskrivning.filterFelt(
                derVerdiErNull()
            )
        )
    } forVerdier listOf {
        der(
            saldoavskrevetAnleggsmiddel,
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt f.aaretsAvskrivning },
            it.vederlagVedRealisasjonOgUttak.filterFelt(
                eller(derVerdiErNull(), derVerdiErLik(0))
            )
        )
    }

    private val aaretsAvskrivningSaldogruppeIMellomverdi =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            og(
                it.saldogruppe.filterFelt(
                    Specifications.harEnAvVerdiene(
                        saldogruppe_2021.kode_i.kode
                    )
                ),
                Specifications.binaryFeltSpec(
                    it.grunnlagForAvskrivningOgInntektsfoering,
                    it.nedreGrenseForAvskrivning
                ) {
                        grunnlagForAvskrivningOgInntektsfoering, nedreGrenseForAvskrivning,
                    ->
                    grunnlagForAvskrivningOgInntektsfoering > nedreGrenseForAvskrivning
                }
            )
        } forVerdier listOf {
            der(
                saldoavskrevetAnleggsmiddel,
                { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt beregnedeFelter.aaretsAvskrivningMellomverdi },
                it.vederlagVedRealisasjonOgUttak.filterFelt(derVerdiErNull())
            )
        }

    private val utgaaendeVerdiSaldogruppEogFogGogH =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    saldogruppe_2021.kode_e.kode,
                    saldogruppe_2021.kode_f.kode,
                    saldogruppe_2021.kode_g.kode,
                    saldogruppe_2021.kode_h.kode,
                )
            )
        } forVerdi {
            it.grunnlagForAvskrivningOgInntektsfoering -
                it.aaretsAvskrivning -
                it.tapOverfoertTilGevinstOgTapskonto +
                it.gevinstOverfoertTilGevinstOgTapskonto somFelt it.utgaaendeVerdi
        }
    private val utgaaendeVerdiSaldogruppeIMellomverdi =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            og(
                it.saldogruppe.filterFelt(
                    Specifications.harEnAvVerdiene(
                        saldogruppe_2021.kode_i.kode
                    )
                ),

                )
        } forVerdier listOf {
            der(
                saldoavskrevetAnleggsmiddel, { f ->
                    f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) -
                        beregnedeFelter.aaretsAvskrivningMellomverdi somFelt beregnedeFelter.utgaaendeVerdiMellomverdi
                }, it.vederlagVedRealisasjonOgUttak.filterFelt(
                    eller(
                        derVerdiErNull(),
                        derVerdiErMindreEnnEllerLik(0)
                    )
                )
            )
        }

    private val aaretsAvskrivningSaldogruppeI = itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
        og(
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    saldogruppe_2021.kode_i.kode
                )
            ),
            it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                derVerdiErStoerreEnn(
                    0
                )
            )

        )

    } forVerdier listOf(
        {
            der(
                saldoavskrevetAnleggsmiddel,
                { f -> f.grunnlagForAvskrivningOgInntektsfoering - f.nedreGrenseForAvskrivning somFelt it.aaretsAvskrivning },
                og(
                    Specifications.binaryFeltSpec(
                        beregnedeFelter.utgaaendeVerdiMellomverdi,
                        it.nedreGrenseForAvskrivning
                    ) {
                            utgaaendeVerdi, nedreGrenseForAvskrivning,
                        ->
                        utgaaendeVerdi < nedreGrenseForAvskrivning
                    }

                )
            )
        },
        {
            der(
                saldoavskrevetAnleggsmiddel,
                { f -> f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt it.aaretsAvskrivning },
                eller(
                    og(
                        Specifications.binaryFeltSpec(
                            beregnedeFelter.utgaaendeVerdiMellomverdi,
                            it.nedreGrenseForAvskrivning
                        ) {
                                utgaaendeVerdi, nedreGrenseForAvskrivning,
                            ->
                            utgaaendeVerdi > nedreGrenseForAvskrivning
                        }
                    ),
                    og(
                        it.nedreGrenseForAvskrivning.filterFelt(derVerdiErNull())
                    )
                )
            )
        }
    )

    private val utgaaendeVerdiSaldogruppeI =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            og(
                it.saldogruppe.filterFelt(
                    Specifications.harEnAvVerdiene(
                        saldogruppe_2021.kode_i.kode
                    )
                ),
            )
        } forVerdi {
            it.grunnlagForAvskrivningOgInntektsfoering -
                it.aaretsAvskrivning -
                it.tapOverfoertTilGevinstOgTapskonto +
                it.gevinstOverfoertTilGevinstOgTapskonto somFelt it.utgaaendeVerdi
        }

    // Denne genererer summer på forekomstnivå; for forekomsten som har verdiene
    private val forekomsterSaldogruppeAogCogJ: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    saldogruppe_2021.kode_a.kode,
                    saldogruppe_2021.kode_c.kode,
                    saldogruppe_2021.kode_c2.kode,
                    saldogruppe_2021.kode_j.kode
                )
            )
        }
    private val forekomsterSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter { it.saldogruppe.filterFelt(Specifications.derVerdiErLik(saldogruppe_2021.kode_b.kode)) }
    private val forekomsterSaldogruppeD: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter { it.saldogruppe.filterFelt(Specifications.derVerdiErLik(saldogruppe_2021.kode_d.kode)) }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeAogCogJ forVerdi {
            it.inngaaendeVerdi +
                it.nyanskaffelse +
                it.paakostning -
                it.offentligTilskudd +
                it.justeringAvInngaaendeMva -
                it.vederlagVedRealisasjonOgUttak -
                it.nedskrevetVerdiAvUtskilteDriftsmidler -
                it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                it.reinvestertBetingetSkattefriSalgsgevinst -
                it.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeB forVerdi {
            it.inngaaendeVerdi +
                it.nyanskaffelse -
                it.nedskrevetVerdiAvUtskilteDriftsmidler -
                it.vederlagVedRealisasjonOgUttak +
                it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                it.reinvestertBetingetSkattefriSalgsgevinst -
                it.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeD: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeD forVerdi {
            it.inngaaendeVerdi +
                it.nyanskaffelse +
                it.paakostning -
                it.offentligTilskudd +
                it.justeringAvInngaaendeMva -
                it.nedskrevetVerdiAvUtskilteDriftsmidler -
                it.vederlagVedRealisasjonOgUttak -
                it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                it.reinvestertBetingetSkattefriSalgsgevinst -
                it.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    internal val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogCogJ =
        forekomsterSaldogruppeAogCogJ forVerdier (
            listOf(
                {
                    der(
                        saldoavskrevetAnleggsmiddel, { f ->
                            f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning
                        }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                            derVerdiErStoerreEnn(0)
                        )
                    )
                },
                {
                    der(
                        saldoavskrevetAnleggsmiddel, { f ->
                            f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
                        }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                            derVerdiErMindreEnn(-14999)
                        )
                    )
                },
                {
                    der(
                        saldoavskrevetAnleggsmiddel, { f ->
                            f.grunnlagForAvskrivningOgInntektsfoering somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
                        }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                            derVerdiErMellom(
                                -14999,
                                -1
                            )
                        )
                    )
                }
            )
            )

    private val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
        listOf {
            der(
                saldoavskrevetAnleggsmiddel, { f ->
                    f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning
                }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                    derVerdiErStoerreEnn(0)
                )
            )
        }
        )
    private val avskrivningSaldogruppeD = forekomsterSaldogruppeD filter {
        it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
            derVerdiErStoerreEnn(0)
        )
    } forVerdier (
        listOf {
            der(
                saldoavskrevetAnleggsmiddel, { f ->
                    f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning
                }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                    derVerdiErStoerreEnn(0)
                )
            )
        }
        )

    private val inntektsfoeringOgUtgaaendeVerdiSaldogruppeD = forekomsterSaldogruppeD forVerdier (
        listOf(
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErMindreEnn(-14999)
                    )
                )
            },
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErMellom(
                            -14999,
                            0
                        )
                    )
                )
            }
        )
        )

    private val utgaaendeVerdiSaldogruppeAogC = forekomsterSaldogruppeAogCogJ forVerdier (
        listOf(
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering -
                            f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErStoerreEnnEllerLik(0)
                    )
                )
            },
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering +
                            f.aaretsInntektsfoeringAvNegativSaldo somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErMindreEnn(0)
                    )
                )
            }
        )
        )

    private val utgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
        listOf(
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering -
                            f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErStoerreEnnEllerLik(0)
                    )
                )

            },
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering +
                            f.gevinstOverfoertTilGevinstOgTapskonto somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
                        derVerdiErMindreEnn(0)
                    )
                )
            }
        )
        )

    private val utgaaendeVerdiSaldogruppeD = forekomsterSaldogruppeD forVerdier (
        listOf(
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering -
                            f.aaretsAvskrivning somFelt f.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(derVerdiErStoerreEnnEllerLik(0))
                )
            },
            {
                der(
                    saldoavskrevetAnleggsmiddel, { f ->
                        f.grunnlagForAvskrivningOgInntektsfoering +
                            f.aaretsInntektsfoeringAvNegativSaldo somFelt f.utgaaendeVerdi
                    }, it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(derVerdiErMindreEnn(0))
                )
            }
        )
        )

    internal val kalkyletreSaldogruppeAogC = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC,
        avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogCogJ,
        utgaaendeVerdiSaldogruppeAogC
    )
    private val kalkyletreSaldogruppeB = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB,
        avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB,
        utgaaendeVerdiSaldogruppeB
    )
    private val kalkyletreSaldogruppeD = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeD,
        avskrivningSaldogruppeD,
        inntektsfoeringOgUtgaaendeVerdiSaldogruppeD,
        utgaaendeVerdiSaldogruppeD
    )

    val kalkyletreSaldogruppeEogFogGogHogI = Kalkyletre(
        nedreGrenseForAvskrivningForForretningsbygg,
        grunnlagForAvskrivningOgInntektsFoeringSaldogruppeEogFogGogHogI,
        aaretsAvskrivningSaldogruppeEogFogHogI,
        aaretsAvskrivningSaldogruppeIMellomverdi,
        utgaaendeVerdiSaldogruppeIMellomverdi,
        aaretsAvskrivningSaldogruppeI,
        utgaaendeVerdiSaldogruppeI,
        utgaaendeVerdiSaldogruppEogFogGogH
    )

    val kalkyle = Kalkyletre(
        kalkyletreSaldogruppeAogC,
        kalkyletreSaldogruppeB,
        kalkyletreSaldogruppeD,
        kalkyletreSaldogruppeEogFogGogHogI
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyle.medPostprosessering(this)
    }

    /**
     * Vi filtrerer vekk mellomregninger
     */
    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == beregnedeFelter.aaretsAvskrivningMellomverdi.key
                || it.key == beregnedeFelter.utgaaendeVerdiMellomverdi.key)
        }
    }
}
