object FordeltBeregnetNaeringsinntektUnntak : InlineKalkyle() {

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        val virksomhetsstype = gm.verdiFor(virksomhet.virksomhetstype.key)
        val resultat = gm.verdiFor(skattemessigResultat.beloep.key) ?: gm.verdiFor(aarsresultat.beloep.key)
        return if (resultat == null) {
            Verdi(GeneriskModell.tom())
        } else if (virksomhetsstype == virksomhetstype_2021.kode_enkeltpersonforetak.kode) {
            Verdi(unntakForEnkeltmannsforetak(gm))
        } else {
            Verdi(unntakForUpersonlige(gm))
        }
    }

    private fun unntakForUpersonlige(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            oppdaterFordeltSkattemessigResultat(gm, forekomsterAvFordeltBeregnetNaeringsinntekt[0])
        } else {
            GeneriskModell.tom()
        }
    }

    private fun unntakForEnkeltmannsforetak(gm: GeneriskModell): GeneriskModell {
        val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
        return if (forekomsterAvFordeltBeregnetNaeringsinntekt.isEmpty()) {
            fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm)
        } else if (forekomsterAvFordeltBeregnetNaeringsinntekt.size == 1) {
            val forekomst = forekomsterAvFordeltBeregnetNaeringsinntekt[0]
            val oppdaterteFelter = fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(gm, forekomst)
            oppdaterteFelter.erstattEllerLeggTilFelter(oppdaterFordeltSkattemessigResultat(gm, forekomst))
        } else {
            GeneriskModell.tom()
        }
    }

    private fun fyllUtStandardverdierForFordeltBeregnetNaeringsinntektVedMangler(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell? = null
    ): GeneriskModell {
        val eksisterendeForekomstId = fordeltBeregnetNaeringsinntektForekomst?.rotIdVerdi() ?: "1"
        val fordeltBeregnetNaeringsinntektForekomstId =
            fordeltBeregnetNaeringsinntekt.forekomstType[0] to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key,
                mapOf(fordeltBeregnetNaeringsinntektForekomstId),
                fordeltBeregnetNaeringsinntektForekomst
                    ?.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key)
                    ?: "1"
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key,
                mapOf(fordeltBeregnetNaeringsinntektForekomstId),
                fordeltBeregnetNaeringsinntektForekomst
                    ?.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key)
                    ?: "1"
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.naeringstype.key,
                mapOf(fordeltBeregnetNaeringsinntektForekomstId),
                fordeltBeregnetNaeringsinntektForekomst
                    ?.verdiFor(fordeltBeregnetNaeringsinntekt.naeringstype.key)
                    ?: "annenNaering"
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.naeringsbeskrivelse.key,
                mapOf(fordeltBeregnetNaeringsinntektForekomstId),
                fordeltBeregnetNaeringsinntektForekomst
                    ?.verdiFor(fordeltBeregnetNaeringsinntekt.naeringsbeskrivelse.key)
                    ?: ""
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.key,
                mapOf(
                    fordeltBeregnetNaeringsinntektForekomstId,
                    fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                gm.verdiFor(skattemessigResultat.beloep.key) ?: gm.verdiFor(aarsresultat.beloep.key)
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver.key,
                mapOf(fordeltBeregnetNaeringsinntektForekomstId),
                fordeltBeregnetNaeringsinntektForekomst
                    ?.verdiFor(fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver.key)
                    ?: "100"
            ),
        )
    }

    private fun oppdaterFordeltSkattemessigResultat(
        gm: GeneriskModell,
        fordeltBeregnetNaeringsinntektForekomst: GeneriskModell
    ): GeneriskModell {
        val skattemessigResultatVerdi = gm.verdiFor(skattemessigResultat.beloep.key) ?: return GeneriskModell.tom()
        val eksisterendeForekomstId = fordeltBeregnetNaeringsinntektForekomst.rotIdVerdi()!!
        val forekomstId = fordeltBeregnetNaeringsinntekt.forekomstType[0] to eksisterendeForekomstId

        return GeneriskModell.fra(
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.key,
                mapOf(
                    forekomstId,
                    fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                skattemessigResultatVerdi
            )
        )
    }
}