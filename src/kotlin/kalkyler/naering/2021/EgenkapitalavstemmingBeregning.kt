object EgenkapitalavstemmingBeregning : InlineKalkyle() {

    private fun summerForekomsterAvEgenkapitalendring(
        kategori: Kategori,
        feltHenter: (egenkapitalendring) -> FeltKoordinat<*>
    ) =
        summer forekomsterAv egenkapitalendring filter {
            FeltSpecification(
                egenkapitalendring.egenkapitalendringstype,
                Specification { hentKategori(it) == kategori }
            )
        } forVerdi {
            feltHenter.invoke(it)
        }

    internal val sumTilleggIEgenkapitalKalkyle =
        summerForekomsterAvEgenkapitalendring(Kategori.TILLEGG) {
            it.beloep
        }

    internal val sumFradragIEgenkapitalKalkyle =
        summerForekomsterAvEgenkapitalendring(Kategori.FRADRAG) {
            it.beloep
        }

    internal val sumPositivPrinsippendringKalkyle =
        summer forekomsterAv prinsippendring forVerdi {
            it.positivPrinsippendring
        }

    internal val sumNegativPrinsippendringKalkyle =
        summer forekomsterAv prinsippendring forVerdi {
            it.negativPrinsippendring
        }

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()

        val sumTilleggIEgenkapitalVerdi = sumTilleggIEgenkapitalKalkyle
            .kalkulertPaa(gm).bigDecimal()
        val sumFradragIEgenkapitalVerdi = sumFradragIEgenkapitalKalkyle
            .kalkulertPaa(gm).bigDecimal()

        val sumNettoPositivPrinsippendringVerdi = beregnSumNettoPositivPrinsippendring(gm)
        val sumNettoNegativPrinsippendringVerdi = beregnSumNettoNegativPrinsippendring(gm)

        val inngaaendeEgenkapitalVerdi = hentEgenkapitalavstemmingForekomst(gm)
            ?.verdiFor(egenkapitalavstemming.inngaaendeEgenkapital.key)?.let { BigDecimal(it) }

        val utgaaendeEgenkapitalVerdi = inngaaendeEgenkapitalVerdi
            .plusNullsafe(sumNettoPositivPrinsippendringVerdi)
            .plusNullsafe(sumNettoNegativPrinsippendringVerdi)
            .plusNullsafe(sumTilleggIEgenkapitalVerdi)
            .minusNullsafe(sumFradragIEgenkapitalVerdi)

        val egenkapitalavstemmingForekomstId = egenkapitalavstemming.forekomstType[0] to
                (hentEgenkapitalavstemmingForekomst(gm)?.rotIdVerdi() ?: "1")

        val nyeElementer = mutableListOf<InformasjonsElement>()

        sumTilleggIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumTilleggIEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumTilleggIEgenkapital.gruppe to "fixed"
                    ),
                    avrundTilToDesimaler(it)
                )
            )
        }

        sumFradragIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumFradragIEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumFradragIEgenkapital.gruppe to "fixed"
                    ),
                    avrundTilToDesimaler(it)
                )
            )
        }

        sumNettoPositivPrinsippendringVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumNettoPositivPrinsippendring.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumNettoPositivPrinsippendring.gruppe to "fixed"
                    ),
                    avrundTilToDesimaler(it)
                )
            )
        }

        sumNettoNegativPrinsippendringVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumNettoNegativPrinsippendring.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumNettoNegativPrinsippendring.gruppe to "fixed"
                    ),
                    avrundTilToDesimaler(it)
                )
            )
        }

        utgaaendeEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.utgaaendeEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.utgaaendeEgenkapital.gruppe to "fixed"
                    ),
                    avrundTilToDesimaler(it)
                )
            )
        }

        return Verdi(GeneriskModell.fra(nyeElementer))
    }

    private fun beregnSumNettoPositivPrinsippendring(gm: GeneriskModell): BigDecimal? {
        val sumPositivPrinsippendring = sumPositivPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .bigDecimal()

        val sumNegativPrinsippendring = sumNegativPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .bigDecimal()

        val sumNettoPrinsippendring = sumPositivPrinsippendring.minusNullsafe(sumNegativPrinsippendring)

        return if (sumNettoPrinsippendring != null && sumNettoPrinsippendring > BigDecimal.ZERO) {
            val utsattSkattefordel = hentEgenkapitalavstemmingForekomst(gm)
                ?.verdiFor(egenkapitalavstemming.utsattSkattefordel.key)

            sumNettoPrinsippendring.minus(BigDecimal(utsattSkattefordel ?: "0"))
        } else {
            null
        }
    }

    private fun beregnSumNettoNegativPrinsippendring(gm: GeneriskModell): BigDecimal? {
        val sumPositivPrinsippendring = sumPositivPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .bigDecimal()

        val sumNegativPrinsippendring = sumNegativPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .bigDecimal()

        val sumNettoPrinsippendring = sumPositivPrinsippendring.minusNullsafe(sumNegativPrinsippendring)

        return if (sumNettoPrinsippendring != null && sumNettoPrinsippendring < BigDecimal.ZERO) {
            val utsattSkatt = hentEgenkapitalavstemmingForekomst(gm)
                ?.verdiFor(egenkapitalavstemming.utsattSkatt.key)

            sumNettoPrinsippendring.plus(BigDecimal(utsattSkatt ?: "0"))
        } else {
            null
        }
    }

    private fun hentEgenkapitalavstemmingForekomst(gm: GeneriskModell): GeneriskModell? {
        return gm.grupper(egenkapitalavstemming.forekomstType[0]).firstOrNull()
    }

    private fun avrundTilToDesimaler(tall: BigDecimal): String {
        return tall.setScale(2, RoundingMode.UP).stripTrailingZeros().toPlainString()
    }

    private val egenkapitalendringstype = egenkapitalendringstype_2021
        .hentKodeverdier()
        .associateBy { it.kode }

    private enum class Kategori(val kategori: String) {
        TILLEGG("tillegg"),
        FRADRAG("fradrag");

        companion object {
            fun create(kategori: String): Kategori? {
                return values()
                    .firstOrNull { it.kategori == kategori }
            }
        }
    }

    private fun hentKategori(egenkapitaltype: Any?): Kategori {
        val kodeVerdi = egenkapitalendringstype[egenkapitaltype as String]
            ?: error("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering. Kode: $egenkapitaltype")

        return Kategori.create(kodeVerdi.kodetillegg!!.kategori!!)!!
    }
}