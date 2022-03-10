internal fun skalBeregnePersoninntekt(gm: GeneriskModell): Boolean {
    val virksomhetsstype = gm.verdiFor(virksomhet.virksomhetstype.key)
    val forekomsterAvFordeltBeregnetNaeringsinntekt = gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
    return virksomhetsstype == virksomhetstype_2021.kode_enkeltpersonforetak.kode
        && forekomsterAvFordeltBeregnetNaeringsinntekt.isNotEmpty()
}

object FordeltBeregnetNaeringsinntektUtil {
    private fun sortertFordeltBeregnetNaeringsinntektForekomster(gm: GeneriskModell): List<GeneriskModell> {
        return gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
            .sortedBy { it.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key) }
    }

    internal fun finnDefaultFordeltBeregnetNaeringsinntekt(
        gm: GeneriskModell
    ): GeneriskModell? {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetNaeringsinntektForekomster(gm)
            .map { it.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key) }
            .toSet()
    }
}

object FordeltBeregnetPersoninntektUtil {

    private fun sortertFordeltBeregnetPersoninntektForekomster(gm: GeneriskModell): List<GeneriskModell> {
        return gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
            .sortedBy { it.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key) }
    }

    internal fun finnDefaultFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskModell? {
        return sortertFordeltBeregnetPersoninntektForekomster(gm).firstOrNull()
    }

    internal fun identifikatorerForFordeltBeregnetPersoninntekt(gm: GeneriskModell): Set<String> {
        return sortertFordeltBeregnetPersoninntektForekomster(gm)
            .map { it.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key) }
            .toSet()
    }
}

private const val FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetNaeringsinntekt " +
        "med identifikatorForFordeltBeregnetNaeringsinntekt på dette tidspunktet"

private const val FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE =
    "Det skal alltid finnes minst én forekomst av fordeltBeregnetPersoninntekt " +
        "med identifikatorForFordeltBeregnetPersoninntekt på dette tidspunktet"

internal fun opprettGyldigeIdentifikatorer(gm: GeneriskModell): GeneriskModell {
    val manglendeIdentifikatorer = opprettManglendeIdentifikatorer(gm)
    var oppdatertGm = gm.erstattEllerLeggTilFelter(manglendeIdentifikatorer)
    val manglendeOgUnikeIdentifikatorer = manglendeIdentifikatorer.erstattEllerLeggTilFelter(
        opprettUnikeIdentifikatorer(oppdatertGm)
    )
    oppdatertGm = oppdatertGm.erstattEllerLeggTilFelter(manglendeOgUnikeIdentifikatorer)
    val synkroniserteIdentifikatorer = synkroniserIdentifikatorer(oppdatertGm)
    return manglendeOgUnikeIdentifikatorer.erstattEllerLeggTilFelter(synkroniserteIdentifikatorer)
}

private fun opprettManglendeIdentifikatorer(gm: GeneriskModell): GeneriskModell {
    val fordeltBeregnetNaeringsinntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0]),
        fordeltBeregnetNaeringsinntekt.forekomstType[0],
        fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt,
        fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0]),
        fordeltBeregnetPersoninntekt.forekomstType[0],
        fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
        fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettManglendeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskModell>,
    forekomstType: String,
    identifikatorForFordeltBeregnetPersoninntekt: FeltKoordinat<*>,
    identifikatorForFordeltBeregnetNaeringsinntekt: FeltKoordinat<*>,
): GeneriskModell {
    val nyeIdentifikatorer = mutableListOf<InformasjonsElement>()

    val eksisterendePid = forekomster
        .filter { it.harVerdiFor(identifikatorForFordeltBeregnetPersoninntekt.key) }
        .map { it.verdiFor(identifikatorForFordeltBeregnetPersoninntekt.key) }

    val eksisterendeNid = forekomster
        .filter { it.harVerdiFor(identifikatorForFordeltBeregnetNaeringsinntekt.key) }
        .map { it.verdiFor(identifikatorForFordeltBeregnetNaeringsinntekt.key) }

    forekomster.forEachIndexed { index, forekomst ->
        val forekomstId = forekomstType to forekomst.rotIdVerdi()
        val pid = forekomst
            .verdiFor(identifikatorForFordeltBeregnetPersoninntekt.key)
        if (pid == null) {
            var nyPid: Int = index + 1
            while (eksisterendePid.contains(nyPid.toString())) {
                nyPid++
            }
            nyeIdentifikatorer.add(
                InformasjonsElement(
                    identifikatorForFordeltBeregnetPersoninntekt.key,
                    mapOf(forekomstId),
                    nyPid
                )
            )
        }

        val nid = forekomst
            .verdiFor(identifikatorForFordeltBeregnetNaeringsinntekt.key)
        if (nid == null) {
            var nyNid: Int = index + 1
            while (eksisterendeNid.contains(nyNid.toString())) {
                nyNid++
            }
            nyeIdentifikatorer.add(
                InformasjonsElement(
                    identifikatorForFordeltBeregnetNaeringsinntekt.key,
                    mapOf(forekomstId),
                    nyNid
                )
            )
        }
    }
    return GeneriskModell.fra(nyeIdentifikatorer)
}

private fun opprettUnikeIdentifikatorer(
    gm: GeneriskModell
): GeneriskModell {
    val fordeltBeregnetNaeringsinntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0]),
        fordeltBeregnetNaeringsinntekt.forekomstType[0],
        fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt,
    )

    val fordeltBeregnetPersoninntektIdentifikatorer = opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
        gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0]),
        fordeltBeregnetPersoninntekt.forekomstType[0],
        fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt,
    )

    return fordeltBeregnetNaeringsinntektIdentifikatorer
        .erstattEllerLeggTilFelter(fordeltBeregnetPersoninntektIdentifikatorer)
}

private fun opprettUnikeIdentifikatorerForFordeltBeregnetInntekt(
    forekomster: List<GeneriskModell>,
    forekomstType: String,
    identifikator: FeltKoordinat<*>,
): GeneriskModell {
    val nyeIdentifikatorer = mutableListOf<InformasjonsElement>()

    val eksisterendeIdentifikatorerTilForekomster = forekomster
        .filter { it.harVerdiFor(identifikator.key) }
        .groupBy { it.verdiFor(identifikator.key) }

    val forekomsterSomMaaOppdateres = eksisterendeIdentifikatorerTilForekomster
        .filter { it.value.size > 1 }
        .flatMap { it.value.subList(1, it.value.size) }

    var nyIdentifikatorVerdi = 1
    forekomsterSomMaaOppdateres.forEach { forekomst ->
        val forekomstId = forekomstType to forekomst.rotIdVerdi()
        while (eksisterendeIdentifikatorerTilForekomster.containsKey(nyIdentifikatorVerdi.toString())) {
            nyIdentifikatorVerdi++
        }
        nyeIdentifikatorer.add(
            InformasjonsElement(
                identifikator.key,
                mapOf(forekomstId),
                nyIdentifikatorVerdi
            )
        )
    }
    return GeneriskModell.fra(nyeIdentifikatorer)
}

internal fun synkroniserIdentifikatorer(
    gm: GeneriskModell
): GeneriskModell {
    val oppdatertePersoninntektIder = synkroniserFordeltBeregnetPersoninntekt(gm)
    val oppdaterteNaeringsinntektIder =
        synkroniserFordeltBeregnetNaeringsinntekt(gm.erstattEllerLeggTilFelter(oppdatertePersoninntektIder))

    return oppdatertePersoninntektIder.erstattEllerLeggTilFelter(oppdaterteNaeringsinntektIder)
}

private fun synkroniserFordeltBeregnetPersoninntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetNaeringsinntekt =
        FordeltBeregnetNaeringsinntektUtil.finnDefaultFordeltBeregnetNaeringsinntekt(gm)
            ?.verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key)
            ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_NAERINGSINNTEKT_SKAL_EKSISTERE)

    return gm.grupper(fordeltBeregnetPersoninntekt.forekomstType[0])
        .stream()
        .map { fordeltPersoninntekt ->
            val nid = fordeltPersoninntekt
                .verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key)
            val rotForekomstId = fordeltPersoninntekt.rotIdVerdi()
            val forekomstIdFordeltBeregnetPersoninntekt =
                fordeltBeregnetPersoninntekt.forekomstType[0] to rotForekomstId
            if (!FordeltBeregnetNaeringsinntektUtil.identifikatorerForFordeltBeregnetNaeringsinntekt(gm)
                    .contains(nid)
            ) {
                GeneriskModell.fra(
                    InformasjonsElement(
                        fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetNaeringsinntekt.key,
                        mapOf(forekomstIdFordeltBeregnetPersoninntekt),
                        defaultIdentifikatorForFordeltBeregnetNaeringsinntekt
                    )
                )
            } else {
                GeneriskModell.tom()
            }
        }
        .collect(GeneriskModell.collectorFraGm())
}

private fun synkroniserFordeltBeregnetNaeringsinntekt(gm: GeneriskModell): GeneriskModell {
    val defaultIdentifikatorForFordeltBeregnetPersoninntekt = FordeltBeregnetPersoninntektUtil
        .finnDefaultFordeltBeregnetPersoninntekt(gm)
        ?.verdiFor(fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt.key)
        ?: throw IllegalStateException(FEILMELDING_FORDELT_BEREGNET_PERSONINNTEKT_SKAL_EKSISTERE)

    return gm.grupper(fordeltBeregnetNaeringsinntekt.forekomstType[0])
        .stream()
        .map { fordeltNaeringsinntekt ->
            val pid = fordeltNaeringsinntekt
                .verdiFor(fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key)
            val rotForekomstId = fordeltNaeringsinntekt.rotIdVerdi()
            val forekomstIdFordeltBeregnetNaeringsinntekt =
                fordeltBeregnetNaeringsinntekt.forekomstType[0] to rotForekomstId
            if (!FordeltBeregnetPersoninntektUtil
                    .identifikatorerForFordeltBeregnetPersoninntekt(gm)
                    .contains(pid)
            ) {
                GeneriskModell.fra(
                    InformasjonsElement(
                        fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt.key,
                        mapOf(forekomstIdFordeltBeregnetNaeringsinntekt),
                        defaultIdentifikatorForFordeltBeregnetPersoninntekt
                    )
                )
            } else {
                GeneriskModell.tom()
            }
        }
        .collect(GeneriskModell.collectorFraGm())
}