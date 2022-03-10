object SkattemessigResultatBeregning : InlineKalkyle() {

    private val skattemessigResultatKalkyle =
        aarsresultat.beloep + sumTilleggINaeringsinntekt.beloep - sumFradragINaeringsinntekt.beloep +
            sumEndringIMidlertidigForskjell.beloep

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()

        val resultat: BigDecimal? = overstyrtVerdiHvisOverstyrt(gm, skattemessigResultat.beloep.key)
            ?: skattemessigResultatKalkyle.beregn(gm).normalisertVerdi()

        return Verdi(
            GeneriskModell.fra(
                InformasjonsElement(
                    skattemessigResultat.beloep.key,
                    mapOf(skattemessigResultat.gruppe to "fixed"),
                    resultat?.avrundTilToDesimalerString()
                )
            )
        )
    }
}