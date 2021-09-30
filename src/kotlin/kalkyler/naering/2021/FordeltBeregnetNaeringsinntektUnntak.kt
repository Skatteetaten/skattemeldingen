object FordeltBeregnetNaeringsinntektUnntak : InlineKallkyle() {

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        val forekomsterAvBeregnetNaeringsinntekt = gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
        val forekomsterAvPersoninntekt = gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
        val forekomstIderBeregnetNaeringsinntekt = mapOf(fordeltBeregnetNaeringsinntekt.forekomstType[0] to "1")
        val forekomstIderIdentifikator = mapOf(fordeltBeregnetPersoninntekt.forekomstType[0] to "1")
        val forekomstIderAaretsBeregnendePersoninntekt = mapOf(
            fordeltBeregnetPersoninntekt.forekomstType[0] to "1",
            fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
        )
        var underliggendeVerdi = GeneriskModell.fra(
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.identifikatorForPersoninntekt.key,
                forekomstIderBeregnetNaeringsinntekt,
                "1"
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.naeringsbeskrivelse.key,
                forekomstIderBeregnetNaeringsinntekt,
                ""
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.key,
                mapOf(
                    fordeltBeregnetNaeringsinntekt.forekomstType[0] to "1",
                    fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat.gruppe to "fixed"
                ),
                gm.verdiFor(aarsresultat.beloep.key)
            ),
            InformasjonsElement(
                fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver.key,
                forekomstIderBeregnetNaeringsinntekt,
                "100"
            )
        )
        if (forekomsterAvPersoninntekt.isEmpty()) {
            underliggendeVerdi = underliggendeVerdi.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    fordeltBeregnetPersoninntekt.identifikatorForPersoninntekt.key,
                    forekomstIderIdentifikator,
                    "1"
                ),
                InformasjonsElement(
                    fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key,
                    forekomstIderAaretsBeregnendePersoninntekt,
                    gm.verdiFor(aarsresultat.beloep.key)
                )
            )
        }
        if (forekomsterAvBeregnetNaeringsinntekt.isEmpty()) {
            return Verdi(underliggendeVerdi.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    fordeltBeregnetNaeringsinntekt.naeringstype.key,
                    forekomstIderBeregnetNaeringsinntekt,
                    "annenNaering"
                )
            ))
        }
        val forekomst = forekomsterAvBeregnetNaeringsinntekt[0]
        return if (forekomsterAvBeregnetNaeringsinntekt.size == 1
            && forekomst.harVerdiFor(fordeltBeregnetNaeringsinntekt.naeringstype.key)
            && forekomst.harVerdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForPersoninntekt.key)
            && forekomst.harVerdiFor(fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver.key)
            && forekomst.size() == 3
        ) {
            Verdi(underliggendeVerdi.erstattEllerLeggTilFelter(
                InformasjonsElement(
                    fordeltBeregnetNaeringsinntekt.naeringstype.key,
                    forekomstIderBeregnetNaeringsinntekt,
                    gm.verdiFor(fordeltBeregnetNaeringsinntekt.naeringstype.key)
                ))
            )
        } else {
            Verdi(gm)
        }
    }
}