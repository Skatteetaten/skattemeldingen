class Naeringsberegner2020 : GenerellBeregner(
    DefaultKalkyletre2020,
    NaeringsregisterMapping.mappingData[2020]!!,
    filtrerBeregnedeVerdierForNaering()
)

fun filtrerBeregnedeVerdierForNaering(): Function1<GeneriskModell, GeneriskModell> {
    return {
        filtrerBortSpesifikkeTyperFraInput(it)
    }
}

private fun filtrerBortSpesifikkeTyperFraInput(generiskModell: GeneriskModell): GeneriskModell {
    val balanse = balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.forekomstType[1]
    val egenkapital = allEgenkapital.egenkapital.forekomstType[1]
    val annenDriftsinntekt = andreDriftsinntekter.annenDriftsinntekt.forekomstType[1]
    val annenDriftskostnad = andreDriftskostnader.annenDriftskostnad.forekomstType[1]
    val anleggsmiddeltypeNoekkel = balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.anleggsmiddeltype.key
    val egenkapitalNoekkel = allEgenkapital.egenkapital.egenkapitaltype.key
    val andreDriftsinntekterNoekkel = andreDriftsinntekter.annenDriftsinntekt.type.key
    val annenDriftskostnadNoekkel = andreDriftskostnader.annenDriftskostnad.type.key
    val anleggsmiddelTyper = listOf(
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1080.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1105.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1115.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1117.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1120.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1205.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1221.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1225.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1238.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1239.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1280.kode,
        resultatregnskapOgBalanse_2020.balanseverdiForAnleggsmiddel.kode_1296.kode,
    )
    val egenkapitalTyper = listOf(
        resultatregnskapOgBalanse_2020.egenkapital.kode_2095.kode,
        resultatregnskapOgBalanse_2020.egenkapital.kode_2096.kode,
    )
    val resultatregnskapetTyper = listOf(
        resultatregnskapOgBalanse_2020.annenDriftsinntekt.kode_3890.kode,
        resultatregnskapOgBalanse_2020.annenDriftsinntekt.kode_3895.kode,
        resultatregnskapOgBalanse_2020.annenDriftskostnad.kode_6000.kode,
        resultatregnskapOgBalanse_2020.annenDriftskostnad.kode_7099.kode,
        resultatregnskapOgBalanse_2020.annenDriftskostnad.kode_7890.kode,
    )
    val filtrerBortAnleggsmidler = GeneriskModell.merge(generiskModell.grupper(balanse)
        .filter { anleggsmiddelTyper.contains(it.verdiFor(anleggsmiddeltypeNoekkel)) })
        .filter { it.key != balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.overfoeresIkkeTilSkattemeldingen.key }
    val filtrerBortEgenkapital = GeneriskModell.merge(generiskModell.grupper(egenkapital)
        .filter { egenkapitalTyper.contains(it.verdiFor(egenkapitalNoekkel)) })
    val filtrerBortAnnenDriftsinntekt = GeneriskModell.merge(generiskModell.grupper(annenDriftsinntekt)
        .filter { resultatregnskapetTyper.contains(it.verdiFor(andreDriftsinntekterNoekkel)) })
    val filtrerBortAnnenDriftskostnad = GeneriskModell.merge(generiskModell.grupper(annenDriftskostnad)
        .filter { resultatregnskapetTyper.contains(it.verdiFor(annenDriftskostnadNoekkel)) })
    val ferdigFiltrert = generiskModell
        .fjernFelter(filtrerBortAnleggsmidler)
        .fjernFelter(filtrerBortEgenkapital)
        .fjernFelter(filtrerBortAnnenDriftsinntekt)
        .fjernFelter(filtrerBortAnnenDriftskostnad)
    return ferdigFiltrert
}
