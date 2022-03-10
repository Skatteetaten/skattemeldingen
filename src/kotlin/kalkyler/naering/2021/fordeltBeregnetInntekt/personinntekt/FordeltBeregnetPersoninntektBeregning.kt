object FordeltBeregnetPersoninntektBeregning : InlineKalkyle() {

    val skjermingsgrunnlagtyper = listOf(
        skjermingsgrunnlagstype_2021.kode_saldogruppeA.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeB.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeC.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeC2.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeD.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeE.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeF.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeG.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeH.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeI.kode,
        skjermingsgrunnlagstype_2021.kode_saldogruppeJ.kode,
        skjermingsgrunnlagstype_2021.kode_lineaertavskrevetAnleggsmiddel.kode,
        skjermingsgrunnlagstype_2021.kode_ikkeAvskrivbartAnleggsmiddel.kode,
        skjermingsgrunnlagstype_2021.kode_ervervetImmatriellRettighet.kode,
        skjermingsgrunnlagstype_2021.kode_aktivertForskningsOgUtviklingskostnad.kode,
        skjermingsgrunnlagstype_2021.kode_varelager.kode,
        skjermingsgrunnlagstype_2021.kode_kundefordring.kode,
    )

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()

        return if (skalBeregnePersoninntekt(gm)) {
            val beregnetSkjermingsgrunnlag = beregnSkjermingsgrunnlag(gm)

            //Personinntekt
            val oppdatertePersonForekomster = gm
                .erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
                .grupper(fordeltBeregnetPersoninntekt.forekomstType[0])

            val personForekomstPerIdentifikator =
                oppdatertePersonForekomster.groupBy { it.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key) }

            val fordelingAvNaeringinntektPerIdentifikator: Map<String, List<GeneriskModell>> =
                gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
                    .filter { it.harVerdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key) }
                    .groupBy { it.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key) }

            //Beregningen er lenient og beregner kun der det er verdier begge steder, hvis ikke så kan vi ikke kjøre kalkylen
            val oppdaterteFeltForPersonForekomst: List<GeneriskModell> = personForekomstPerIdentifikator
                .mapNotNull {
                    var resultat = GeneriskModell.tom()
                    if (it.value.size != 1) {
                        error(
                            "Vi kan ikke ha mer enn én forekomst av fordeltBeregnetPersoninntekt per identifikatorForFordeltBeregnetPersoninntekt, " +
                                "identifikatorForFordeltBeregnetPersoninntekt=${it.key}, har verdier=${it.value}"
                        )
                    }

                    val personForekomst = it.value[0]!!

                    val fordeltSkattemessigResultatEtterKorreksjonBeloep =
                        fordelingAvNaeringinntektPerIdentifikator[it.key]
                            ?.map { forekomst -> forekomst.verdiFor(fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjon.key) }
                            ?.filter { forekomst -> forekomst != null && forekomst.isNotBlank() }

                    if (fordeltSkattemessigResultatEtterKorreksjonBeloep != null
                        && fordeltSkattemessigResultatEtterKorreksjonBeloep.isNotEmpty()
                    ) {
                        val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning = overstyrtVerdiHvisOverstyrt(
                            gm,
                            fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key
                        )
                            ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
                                personForekomst,
                                fordeltSkattemessigResultatEtterKorreksjonBeloep.sumOf { s -> BigDecimal(s) }
                            )

                        resultat = GeneriskModell.fra(
                            InformasjonsElement(
                                fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key,
                                mapOf(
                                    fordeltBeregnetPersoninntekt.forekomstType[0] to personForekomst.rotIdVerdi(),
                                    fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                                ),
                                beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning
                            )
                        )

                        if (beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning != BigDecimal.ZERO) {
                            val andelAvPersoninntektTilordnetInnehaver =
                                personForekomst.verdiFor(fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver.key)
                                    ?: "100"
                            val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver =
                                overstyrtVerdiHvisOverstyrt(
                                    gm,
                                    fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.key
                                )
                                    ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
                                        beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                        BigDecimal(andelAvPersoninntektTilordnetInnehaver)
                                    )

                            resultat = resultat.erstattEllerLeggTilFelter(
                                InformasjonsElement(
                                    fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.key,
                                    mapOf(
                                        fordeltBeregnetPersoninntekt.forekomstType[0] to personForekomst.rotIdVerdi(),
                                        fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.gruppe to "fixed"
                                    ),
                                    beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver
                                )
                            )
                        }
                    }
                    resultat
                }

            val resultat = oppdaterteFeltForPersonForekomst
                .stream()
                .collect(GeneriskModell.collectorFraGm())

            Verdi(resultat.erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag))
            //Verdi(GeneriskModell.merge(listOf(beregnetSkjermingsgrunnlag)).merge(oppdaterteFeltForPersonForekomst))
        } else {
            Verdi(GeneriskModell.tom())
        }
    }

    private fun beregnSkjermingsgrunnlag(
        gm: GeneriskModell
    ): GeneriskModell {
        return gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
            .stream()
            .map { fordeltBeregnetPersoninntektForekomst ->
                val gjennomsnittsverdier =
                    fordeltBeregnetPersoninntektForekomst.grupperUtenNamespace(fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.forekomstType[1])
                        .groupBy(
                            { it.verdiFor(fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype.key) },
                            {
                                (BigDecimal(it.verdiFor(fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi.key)) +
                                    BigDecimal(it.verdiFor(fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi.key)))
                                    .divideInternal(
                                        BigDecimal(2)
                                    )
                            })

                if (gjennomsnittsverdier.isNotEmpty()) {
                    val sumSkjermingsgrunnlagFoerGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            gm,
                            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.key
                        )
                            ?: beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(gjennomsnittsverdier)

                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            gm,
                            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.key
                        )
                            ?: beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
                                gjennomsnittsverdier,
                                sumSkjermingsgrunnlagFoerGjeldsfradrag
                            )

                    val skjermingsrente =
                        fordeltBeregnetPersoninntektForekomst.verdiFor(fordeltBeregnetPersoninntekt.skjermingsrente.key)
                            ?.let { BigDecimal(it).divideInternal(BigDecimal(100)) }

                    val faktorForSkjermingsrente =
                        (fordeltBeregnetPersoninntektForekomst.verdiFor(fordeltBeregnetPersoninntekt.antallMaanederDrevetIAar.key)
                            ?.let { BigDecimal(it) }
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag = overstyrtVerdiHvisOverstyrt(
                        gm,
                        fordeltBeregnetPersoninntekt.skjermingsfradrag.key
                    )
                        ?: beregnSkjermingsfradrag(
                            sumSkjermingsgrunnlagFoerGjeldsfradrag,
                            skjermingsrente,
                            sumSkjermingsgrunnlagEtterGjeldsfradrag,
                            faktorForSkjermingsrente
                        )

                    //BigDecimal(product.stripTrailingZeros().toPlainString())
                    val idPersonInntekt = fordeltBeregnetPersoninntektForekomst.rotIdVerdi()!!
                    val skjermingsgrunnlagElementer = mutableListOf<InformasjonsElement>()

                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.key,
                        mapOf(
                            fordeltBeregnetPersoninntekt.forekomstType[0] to idPersonInntekt,
                            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagFoerGjeldsfradrag
                    )

                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.key,
                        mapOf(
                            fordeltBeregnetPersoninntekt.forekomstType[0] to idPersonInntekt,
                            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagEtterGjeldsfradrag
                    )
                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        fordeltBeregnetPersoninntekt.skjermingsfradrag.key,
                        mapOf(
                            fordeltBeregnetPersoninntekt.forekomstType[0] to idPersonInntekt,
                            fordeltBeregnetPersoninntekt.skjermingsfradrag.gruppe!! to "fixed"
                        ),
                        skjermingsfradrag
                    )

                    GeneriskModell.fra(skjermingsgrunnlagElementer)
                } else {
                    GeneriskModell.tom()
                }
            }
            .collect(GeneriskModell.collectorFraGm())
    }

    private fun beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>
    ): BigDecimal {
        val foersteLedd = gjennomsnittsverdier
            .filter { skjermingsgrunnlagtyper.contains(it.key) }
            .flatMap { it.value }
            .fold(BigDecimal.ZERO, BigDecimal::add)

        val leverandoergjeld =
            gjennomsnittsverdier
                .filter { it.key == skjermingsgrunnlagstype_2021.kode_leverandoergjeld.kode }
                .flatMap { it.value }
                .fold(BigDecimal.ZERO, BigDecimal::add)

        return if ((foersteLedd - leverandoergjeld).signum() == -1) {
            BigDecimal.ZERO
        } else {
            foersteLedd - leverandoergjeld
        }
    }

    private fun beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>,
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal
    ): BigDecimal {
        val foretaksgjeld = gjennomsnittsverdier
            .filter { it.key == skjermingsgrunnlagstype_2021.kode_foretaksgjeld.kode }
            .flatMap { it.value }
            .fold(BigDecimal.ZERO, BigDecimal::add)

        return if ((sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld).signum() == -1) {
            BigDecimal.ZERO
        } else {
            sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld
        }
    }

    private fun beregnSkjermingsfradrag(
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal,
        skjermingsrente: BigDecimal?,
        sumSkjermingsgrunnlagEtterGjeldsfradrag: BigDecimal,
        faktorForSkjermingsrente: BigDecimal
    ): BigDecimal? {
        return if (sumSkjermingsgrunnlagFoerGjeldsfradrag.signum() == -1) {
            BigDecimal.ZERO
        } else if (skjermingsrente != null) {
            sumSkjermingsgrunnlagEtterGjeldsfradrag * faktorForSkjermingsrente * skjermingsrente
        } else {
            null
        }
    }

    private fun beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
        fordeltBeregnetPersoninntektForekomst: GeneriskModell,
        fordeltSkattemessigResultatEtterKorreksjon: BigDecimal
    ): BigDecimal {
        return fordeltSkattemessigResultatEtterKorreksjon
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    fordeltBeregnetPersoninntekt.rentekostnadPaaForetaksgjeld
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    fordeltBeregnetPersoninntekt.kapitalinntektTilKorrigeringAvPersoninntekt
                )
            )
            .plus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    fordeltBeregnetPersoninntekt.kapitalkostnadTilKorrigeringAvPersoninntekt
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    fordeltBeregnetPersoninntekt.reduksjonsbeloepForLeidEiendomMotInnskudd
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    fordeltBeregnetPersoninntekt.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst, fordeltBeregnetPersoninntekt.skjermingsfradrag
                )
            )
    }

    private fun beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
        aaretsBeregnedePersoninntektFoerFordelingOgSamordning: BigDecimal,
        andelAvPersoninntektTilordnetInnehaver: BigDecimal
    ): BigDecimal {
        return aaretsBeregnedePersoninntektFoerFordelingOgSamordning
            .times(andelAvPersoninntektTilordnetInnehaver)
            .div(BigDecimal(100))
            .avrundTilToDesimaler()
    }

    private fun leggTilInformasjonselement(
        elementer: MutableList<InformasjonsElement>,
        key: String,
        id: Map<String, String>,
        verdi: BigDecimal?,
    ) {
        if (verdi != null) {
            elementer.add(
                InformasjonsElement(
                    key,
                    id,
                    verdi.avrundTilToDesimalerString()
                )
            )
        }
    }

    private fun nullsafe(
        personForekomst: GeneriskModell, koordinat: FeltKoordinat<*>,
    ): BigDecimal {
        val verdiFor = personForekomst.verdiFor(koordinat.key)
        return BigDecimal(verdiFor ?: "0")
    }

    override fun feltPaavirketAvKalkylen(): List<FeltKoordinat<*>> {
        return listOf(
            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag,
            fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag,
            fordeltBeregnetPersoninntekt.skjermingsfradrag,
            fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver
        )
    }
}