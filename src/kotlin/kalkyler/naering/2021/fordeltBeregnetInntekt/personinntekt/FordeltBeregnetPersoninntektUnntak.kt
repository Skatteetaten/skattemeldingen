object FordeltBeregnetPersoninntektUnntak : InlineKalkyle() {

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        return if (skalBeregnePersoninntekt(gm)) {
            Verdi(unntakForEnkeltmannsforetak(gm))
        } else {
            Verdi(GeneriskModell.tom())
        }
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetPersoninntekt = gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
        var unntak = GeneriskModell.tom()
        if (forekomsterAvFordeltBeregnetPersoninntekt.isEmpty()) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetPersoninntekt.size == 1) {
            unntak = fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
                gm,
                forekomsterAvFordeltBeregnetPersoninntekt[0]
            )
        }

        return unntak
    }

    private fun fyllUtStandardverdierForFordeltBeregnetPersoninntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetPersoninntektForekomst: GeneriskModell? = null
    ): GeneriskModell {
        val eksisterendeForekomstId = fordeltBeregnetPersoninntektForekomst?.rotIdVerdi() ?: "1"
        val fordeltBeregnetPersoninntektForekomstId =
            fordeltBeregnetPersoninntekt.forekomstType[0] to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                fordeltBeregnetPersoninntektForekomst
                    ?.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key)
                    ?: "1"
            ),
            InformasjonsElement(
                fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                fordeltBeregnetPersoninntektForekomst
                    ?.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key)
                    ?: "1"
            ),
            InformasjonsElement(
                fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key,
                mapOf(
                    fordeltBeregnetPersoninntektForekomstId,
                    fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                ),
                gm.verdiFor(skattemessigResultat.beloep.key) ?: gm.verdiFor(aarsresultat.beloep.key)
            ),
            InformasjonsElement(
                fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver.key,
                mapOf(fordeltBeregnetPersoninntektForekomstId),
                fordeltBeregnetPersoninntektForekomst
                    ?.verdiFor(fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver.key)
                    ?: "100"
            ),
        )
    }
}