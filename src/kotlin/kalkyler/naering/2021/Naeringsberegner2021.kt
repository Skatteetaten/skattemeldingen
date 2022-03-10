class Naeringsberegner2021(
    val naeringsregisterMapping: NaeringsregisterMapping
) : GenerellBeregner(
    kalkyletre = DefaultKalkyletre2021,
    xmlData = naeringsregisterMapping.mappingData[2021]!!,
    avlededeFelt = naeringsregisterMapping.avlededeFelt[2021]!!,
    filtrerInput = filtrerInput,
    filtrerResultat = filtrerResultat
)

internal val filtrerInput: (GeneriskModell) -> GeneriskModell = { filtrerBortSpesifikkeTyperFraInput(it) }

internal val filtrerResultat: (GeneriskModell) -> GeneriskModell = { filtrerBortFordelBeregnetInntekt(it) }

private fun filtrerBortSpesifikkeTyperFraInput(generiskModell: GeneriskModell): GeneriskModell {
    val regnskapsPliktype = generiskModell.gruppe(virksomhet.gruppe).felt(virksomhet.regnskapspliktstype.key).verdi()
    val balanse = balanseverdiForAnleggsmiddel.balanseverdi.forekomstType[1]
    val egenkapitalType = egenkapital.kapital.forekomstType[1]
    val annenDriftsinntektType = annenDriftsinntekt.inntekt.forekomstType[1]
    val annenDriftskostnadType = annenDriftskostnad.kostnad.forekomstType[1]
    val anleggsmiddeltypeNoekkel = balanseverdiForAnleggsmiddel.balanseverdi.type.key
    val egenkapitalNoekkel = egenkapital.kapital.type.key
    val andreDriftsinntekterNoekkel = annenDriftsinntekt.inntekt.type.key
    val annenDriftskostnadNoekkel = annenDriftskostnad.kostnad.type.key
    if (regnskapsPliktype == "2") {
        val resultatregnskapetTyper = listOf(
            resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_7099.kode,
        )
        val filtrerBortAnnenDriftskostnad =
            GeneriskModell.merge(generiskModell.grupper(annenDriftskostnad.forekomstType[0])
                .filter { resultatregnskapetTyper.contains(it.verdiFor(annenDriftskostnadNoekkel)) })
        return generiskModell
            .fjernFelter(filtrerBortAnnenDriftskostnad)
    }
    val anleggsmiddelTyper = listOf(
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1080.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1105.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1115.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1117.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1120.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1205.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1221.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1225.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1238.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1239.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1280.kode,
        resultatregnskapOgBalanse_2021.balanseverdiForAnleggsmiddel.kode_1296.kode,
    )
    val egenkapitalTyper = listOf(
        resultatregnskapOgBalanse_2021.egenkapital.kode_2095.kode,
        resultatregnskapOgBalanse_2021.egenkapital.kode_2096.kode,
    )
    val resultatregnskapetTyper = listOf(
        resultatregnskapOgBalanse_2021.annenDriftsinntekt.kode_3890.kode,
        resultatregnskapOgBalanse_2021.annenDriftsinntekt.kode_3895.kode,
        resultatregnskapOgBalanse_2021.annenDriftsinntekt.kode_3910.kode,
        resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_6000.kode,
        resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_7099.kode,
        resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_7890.kode,
        resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_7911.kode,
        resultatregnskapOgBalanse_2021.annenDriftskostnad.kode_7910.kode,
    )
    val filtrerBortAnleggsmidler = GeneriskModell.merge(generiskModell.grupper(balanse)
        .filter { anleggsmiddelTyper.contains(it.verdiFor(anleggsmiddeltypeNoekkel)) })
        .filter { it.key != balanseverdiForAnleggsmiddel.balanseverdi.ekskluderesFraSkattemeldingen.key }
    val filtrerBortEgenkapital = GeneriskModell.merge(generiskModell.grupper(egenkapitalType)
        .filter { egenkapitalTyper.contains(it.verdiFor(egenkapitalNoekkel)) })
    val filtrerBortAnnenDriftsinntekt = GeneriskModell.merge(generiskModell.grupper(annenDriftsinntektType)
        .filter { resultatregnskapetTyper.contains(it.verdiFor(andreDriftsinntekterNoekkel)) })
    val filtrerBortAnnenDriftskostnad = GeneriskModell.merge(generiskModell.grupper(annenDriftskostnadType)
        .filter { resultatregnskapetTyper.contains(it.verdiFor(annenDriftskostnadNoekkel)) })
    return generiskModell
        .fjernFelter(filtrerBortAnleggsmidler)
        .fjernFelter(filtrerBortEgenkapital)
        .fjernFelter(filtrerBortAnnenDriftsinntekt)
        .fjernFelter(filtrerBortAnnenDriftskostnad)
}

private fun filtrerBortFordelBeregnetInntekt(gm: GeneriskModell): GeneriskModell {
    val resultat = gm.verdiFor(skattemessigResultat.beloep.key) ?: gm.verdiFor(aarsresultat.beloep.key)
    return if(resultat == null) {
        val eksisterendeFordeltBeregnetNaeringsinntektFelter = gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
            .stream()
            .collect(GeneriskModell.collectorFraGm())
        val eksisterendeFordeltBeregnetPersoninntektFelter = gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
            .stream()
            .collect(GeneriskModell.collectorFraGm())
        gm.fjernFelter(eksisterendeFordeltBeregnetNaeringsinntektFelter)
            .fjernFelter(eksisterendeFordeltBeregnetPersoninntektFelter)
    } else {
        gm
    }

}
