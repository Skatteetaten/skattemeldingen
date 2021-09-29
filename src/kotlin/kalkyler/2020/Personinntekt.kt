/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object PersoninntektBereging : InlineKallkyle() {
    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        val personinntektForekomster = gm.grupper(personinntektFraEnkeltpersonforetak.forekomstType[0])

        val nyeElementerForSkjemingsfradrag: List<GeneriskModell> = personinntektForekomster
            .map { forekomstPerson ->
                val gjennomsnittsverdier =
                    forekomstPerson.grupperUtenNamespace(personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.forekomstType[1])
                        .groupBy(
                            { it.verdiFor(personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype.key) },
                            {
                                (BigDecimal(it.verdiFor(personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi.key)) +
                                    BigDecimal(it.verdiFor(personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi.key)))
                                    .divideInternal(
                                        BigDecimal(2)
                                    )
                            })
                val skjermingsgrunnlagtyper = listOf(
                    Skjermingsgrunnlagstype.saldogruppeA,
                    Skjermingsgrunnlagstype.saldogruppeB,
                    Skjermingsgrunnlagstype.saldogruppeC,
                    Skjermingsgrunnlagstype.saldogruppeC2,
                    Skjermingsgrunnlagstype.saldogruppeD,
                    Skjermingsgrunnlagstype.saldogruppeE,
                    Skjermingsgrunnlagstype.saldogruppeF,
                    Skjermingsgrunnlagstype.saldogruppeG,
                    Skjermingsgrunnlagstype.saldogruppeH,
                    Skjermingsgrunnlagstype.saldogruppeI,
                    Skjermingsgrunnlagstype.saldogruppeJ,
                    Skjermingsgrunnlagstype.lineaertavskrevetAnleggsmiddel,
                    Skjermingsgrunnlagstype.ikkeAvskrivbartAnleggsmiddel,
                    Skjermingsgrunnlagstype.ervervetImmatriellRettighet,
                    Skjermingsgrunnlagstype.aktivertForskningsOgUtvklingskostnad,
                    Skjermingsgrunnlagstype.varelager,
                    Skjermingsgrunnlagstype.kundefordring,
                )

                if (gjennomsnittsverdier.isNotEmpty()) {

                    val foersteLedd = gjennomsnittsverdier
                        .filter { skjermingsgrunnlagtyper.contains(it.key) }
                        .flatMap { it.value }
                        .fold(BigDecimal.ZERO, BigDecimal::add)

                    val leverandoergjeld =
                        gjennomsnittsverdier
                            .filter { it.key == Skjermingsgrunnlagstype.leverandoergjeld }
                            .flatMap { it.value }
                            .fold(BigDecimal.ZERO, BigDecimal::add)

                    val foretaksgjeld = gjennomsnittsverdier
                        .filter { it.key == Skjermingsgrunnlagstype.foretaksgjeld }
                        .flatMap { it.value }
                        .fold(BigDecimal.ZERO, BigDecimal::add)

                    val sumSkjermingsgrunnlagFoerGjeldsfradrag = if ((foersteLedd - leverandoergjeld).signum() == -1) {
                        BigDecimal.ZERO
                    } else {
                        foersteLedd - leverandoergjeld
                    }
                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        if ((sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld).signum() == -1) {
                            BigDecimal.ZERO
                        } else {
                            sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld
                        }
                    val skjermingsrente =
                        forekomstPerson.verdiFor(personinntektFraEnkeltpersonforetak.skjermingsrente.key)
                            ?.let { BigDecimal(it).divideInternal(BigDecimal(100)) }

                    val faktorForSkjermingsrente =
                        (forekomstPerson.verdiFor(personinntektFraEnkeltpersonforetak.antallMaanederDrevetIAar.key)
                            ?.let { BigDecimal(it) }
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag =
                        if (sumSkjermingsgrunnlagFoerGjeldsfradrag.signum() == -1) {
                            BigDecimal.ZERO
                        } else if (skjermingsrente != null) {
                            sumSkjermingsgrunnlagEtterGjeldsfradrag * faktorForSkjermingsrente * skjermingsrente
                        } else {
                            null
                        }

                    //BigDecimal(product.stripTrailingZeros().toPlainString())
                    val idPersonInntekt = forekomstPerson.rotIdVerdi()!!
                    val nyeElementer = mutableListOf<InformasjonsElement>()

                    leggTilInformasjonselement(
                        nyeElementer,
                        personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagFoerGjeldsfradrag.key,
                        mapOf(
                            personinntektFraEnkeltpersonforetak.forekomstType[0] to idPersonInntekt,
                            personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagFoerGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagFoerGjeldsfradrag
                    )

                    leggTilInformasjonselement(
                        nyeElementer,
                        personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagEtterGjeldsfradrag.key,
                        mapOf(
                            personinntektFraEnkeltpersonforetak.forekomstType[0] to idPersonInntekt,
                            personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagEtterGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagEtterGjeldsfradrag
                    )
                    leggTilInformasjonselement(
                        nyeElementer,
                        personinntektFraEnkeltpersonforetak.skjermingsfradrag.key,
                        mapOf(
                            personinntektFraEnkeltpersonforetak.forekomstType[0] to idPersonInntekt,
                            personinntektFraEnkeltpersonforetak.skjermingsfradrag.gruppe!! to "fixed"
                        ),
                        skjermingsfradrag
                    )
                    GeneriskModell.fra(nyeElementer)
                } else {
                    GeneriskModell.tom()
                }
            }.toList()

        //Personinntekt
        val oppdatertePersonForekomster = gm
            .concat(nyeElementerForSkjemingsfradrag.flatMap { it.informasjonsElementer() })
            .grupper(personinntektFraEnkeltpersonforetak.forekomstType[0])

        val personForekomstPerIdentifikator =
            oppdatertePersonForekomster.groupBy { it.verdiFor(personinntektFraEnkeltpersonforetak.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt.key) }

        val fordelingAvNaeringinntektPerIdentifikator: Map<String, List<GeneriskModell>> =
            gm.grupper(fordelingAvNaeringsinntekt.forekomstType[0])
                .filter { it.harVerdiFor(fordelingAvNaeringsinntekt.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt.key) }
                .groupBy { it.verdiFor(fordelingAvNaeringsinntekt.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt.key) }

        //Beregningen er lenient og beregner kun der det er verdier begge steder, hvis ikke så kan vi ikke kjøre kalkylen
        val oppdaterteFeltForPersonForekomst: List<InformasjonsElement> = personForekomstPerIdentifikator
            .mapNotNull {
                var informasjonsElement: InformasjonsElement? = null
                if (it.value.size != 1) {
                    error(
                        "Vi kan ikke ha mer enn en forekomst av personinntektFraEnkeltmannsforetek per identifikatortype, " +
                            "identifikatorForFordelingAvNaeringsinntektOgPersoninntekt=${it.key}, har verdier=${it.value}"
                    )
                }
                val personForekomst = it.value[0]!!
                val naeringsfordelingForekomster = fordelingAvNaeringinntektPerIdentifikator[it.key]
                if (naeringsfordelingForekomster != null) {
                    if (naeringsfordelingForekomster.size != 1) {
                        error(
                            "Vi kan ikke ha mer enn en forekomst av fordelingAvNaeringsinntekt per identifikatortype, " +
                                "identifikatorForFordelingAvNaeringsinntektOgPersoninntekt=${it.key}, har verdier=${it.value}"
                        )
                    }

                    val forekomstAvNaeringsinntekt = naeringsfordelingForekomster[0]
                    val verdi =
                        forekomstAvNaeringsinntekt.verdiFor(fordelingAvNaeringsinntekt.skattemessigResultatForNaeringEtterKorreksjon.key)
                    if (verdi != null) {
                        val aaretsBeregnedePersonInntektFoerForedelingOgSamordning =
                            BigDecimal(verdi)
                                .minus(
                                    nullsafe(
                                        personForekomst,
                                        personinntektFraEnkeltpersonforetak.rentekostnadPaaForetaksgjeld
                                    )
                                )
                                .minus(nullsafe(personForekomst, personinntektFraEnkeltpersonforetak.kapitalinntekt))
                                .plus(nullsafe(personForekomst, personinntektFraEnkeltpersonforetak.kapitalkostnad))
                                .minus(
                                    nullsafe(
                                        personForekomst,
                                        personinntektFraEnkeltpersonforetak.reduksjonsbeloepForLeidEiendomMotInnskudd
                                    )
                                )
                                .minus(
                                    nullsafe(
                                        personForekomst,
                                        personinntektFraEnkeltpersonforetak.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                                    )
                                )
                                .minus(nullsafe(personForekomst, personinntektFraEnkeltpersonforetak.skjermingsfradrag))

                        informasjonsElement = InformasjonsElement(
                            personinntektFraEnkeltpersonforetak.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key,
                            mapOf(
                                personinntektFraEnkeltpersonforetak.forekomstType[0] to personForekomst.rotIdVerdi(),
                                personinntektFraEnkeltpersonforetak.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                            ),
                            aaretsBeregnedePersonInntektFoerForedelingOgSamordning
                        )
                    }
                }
                informasjonsElement
            }


        return Verdi(GeneriskModell.merge(nyeElementerForSkjemingsfradrag).concat(oppdaterteFeltForPersonForekomst))
    }

    private fun leggTilInformasjonselement(
        elementer: MutableList<InformasjonsElement>,
        key: String,
        id: Map<String, String>,
        verdi: BigDecimal?
    ) {
        if (verdi != null) {
            elementer.add(
                InformasjonsElement(
                    key,
                    id,
                    verdi.setScale(2, RoundingMode.HALF_UP)
                        .stripTrailingZeros().toPlainString()
                )
            )
        }
    }

    private fun nullsafe(
        personForekomst: GeneriskModell, koordinat: FeltKoordinat<*>
    ): BigDecimal {
        val verdiFor = personForekomst.verdiFor(koordinat.key)
        return BigDecimal(verdiFor ?: "0")
    }

    override fun feltPaavirketAvKalkylen(): List<FeltKoordinat<*>> {
        return listOf(
            personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagFoerGjeldsfradrag,
            personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagEtterGjeldsfradrag,
            personinntektFraEnkeltpersonforetak.skjermingsfradrag,
            personinntektFraEnkeltpersonforetak.aaretsBeregnedePersoninntektFoerFordelingOgSamordning
        )
    }
}

fun BigDecimal.divideInternal(other: BigDecimal) = this.divide(other, MathContext(12, RoundingMode.HALF_UP))