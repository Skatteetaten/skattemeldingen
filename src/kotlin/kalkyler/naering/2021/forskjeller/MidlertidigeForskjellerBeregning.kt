internal object MidlertidigeForskjellerBeregning : HarKalkyletre, PostProsessering {

    internal val sumEndringIMidlertidigForskjellKalkyle =
        summer forekomsterAv midlertidigForskjell forVerdi {
            MidlertidigeForskjellerIFjor.forskjellIFjor -
                    MidlertidigeForskjellerIAar.forskjellIAar +
                    AndreMidlertidigeForskjeller.tillegg -
                    AndreMidlertidigeForskjeller.fradrag
        } verdiSom sumEndringIMidlertidigForskjell

    private val kalkyletre = Kalkyletre(
        MidlertidigeForskjellerIFjor.kalkyletre,
        MidlertidigeForskjellerIAar.kalkyletre,
        AndreMidlertidigeForskjeller.kalkyletre,
        Kalkyletre(sumEndringIMidlertidigForskjellKalkyle)
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre.medPostprosessering(this)
    }

    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == MidlertidigeForskjellerIAar.forskjellIAar.key
                    || it.key == MidlertidigeForskjellerIFjor.forskjellIFjor.key
                    || it.key == AndreMidlertidigeForskjeller.tillegg.key
                    || it.key == AndreMidlertidigeForskjeller.fradrag.key)
        }
    }
}

private fun summerForekomsterAvMidlertidigForsjellHvor(
    kategori: Kategori,
    tilleggskategori: Tilleggskategori,
    feltHenter: (midlertidigForskjell) -> FeltKoordinat<*>
) =
    summer forekomsterAv midlertidigForskjell filter {
        FeltSpecification(
            midlertidigForskjell.midlertidigForskjellstype,
            Specification {
                hentKategori(it) == kategori
                        && hentTilleggskategori(it) == tilleggskategori
            }
        )
    } forVerdi {
        feltHenter.invoke(it)
    }

internal object MidlertidigeForskjellerIAar {

    val forskjellIAar = SyntetiskFelt(
        midlertidigForskjell,
        "forskjellIAar",
        FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapOgSkattemessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.regnskapsmessigVerdi - it.skattemessigVerdi somFelt forskjellIAar
        }

    internal val regnskapsmessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdi somFelt forskjellIAar }

    internal val skattemessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdi somFelt forskjellIAar }

    internal val regnskapOgSkattemessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) { it.skattemessigVerdi - it.regnskapsmessigVerdi somFelt forskjellIAar }

    internal val regnskapsmessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdi * (-1) somFelt forskjellIAar }

    internal val skattemessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdi * (-1) somFelt forskjellIAar }

    val kalkyletre = Kalkyletre(
        regnskapOgSkattemessigeForskjellerTillegg,
        regnskapsmessigeForskjellerTillegg,
        skattemessigeForskjellerTillegg,
        regnskapOgSkattemessigeForskjellerFradrag,
        regnskapsmessigeForskjellerFradrag,
        skattemessigeForskjellerFradrag,
    )
}

internal object MidlertidigeForskjellerIFjor {

    val forskjellIFjor = SyntetiskFelt(
        midlertidigForskjell,
        "forskjellIFjor",
        FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapOgSkattemessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.regnskapsmessigVerdiForrigeInntektsaar -
                    it.skattemessigVerdiForrigeInntektsaar somFelt forskjellIFjor
        }

    internal val regnskapsmessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdiForrigeInntektsaar somFelt forskjellIFjor }

    internal val skattemessigeForskjellerTillegg =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdiForrigeInntektsaar somFelt forskjellIFjor }

    internal val regnskapOgSkattemessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.skattemessigVerdiForrigeInntektsaar -
                    it.regnskapsmessigVerdiForrigeInntektsaar somFelt forskjellIFjor
        }

    internal val regnskapsmessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdiForrigeInntektsaar * (-1) somFelt forskjellIFjor }

    internal val skattemessigeForskjellerFradrag =
        summerForekomsterAvMidlertidigForsjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdiForrigeInntektsaar * (-1) somFelt forskjellIFjor }

    val kalkyletre = Kalkyletre(
        regnskapOgSkattemessigeForskjellerTillegg,
        regnskapOgSkattemessigeForskjellerFradrag,
        regnskapsmessigeForskjellerTillegg,
        regnskapsmessigeForskjellerFradrag,
        skattemessigeForskjellerTillegg,
        skattemessigeForskjellerFradrag,
    )
}

internal object AndreMidlertidigeForskjeller {

    val tillegg = SyntetiskFelt(
        midlertidigForskjell,
        "andreForskjellerTillegg",
        FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )
    val fradrag = SyntetiskFelt(
        midlertidigForskjell,
        "andreForskjellerFradrag",
        FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapEllerSkattemessigeForskjellerTillegg =
        summer forekomsterAv midlertidigForskjell filter {
            FeltSpecification(
                midlertidigForskjell.midlertidigForskjellstype,
                Specification {
                    hentKategori(it) == Kategori.TILLEGG
                            && hentTilleggskategori(it) == Tilleggskategori.REGNSKAP_ELLER_SKATTEMESSIG
                }
            )
        } forVerdier (
                listOf(
                    { f -> f.regnskapsmessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt tillegg },
                    { f -> f.skattemessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt tillegg }
                ))

    internal val regnskapEllerSkattemessigeForskjellerFradrag =
        summer forekomsterAv midlertidigForskjell filter {
            FeltSpecification(
                midlertidigForskjell.midlertidigForskjellstype,
                Specification {
                    hentKategori(it) == Kategori.FRADRAG
                            && hentTilleggskategori(it) == Tilleggskategori.REGNSKAP_ELLER_SKATTEMESSIG
                }
            )
        } forVerdier (
                listOf(
                    { f -> f.regnskapsmessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt fradrag },
                    { f -> f.skattemessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt fradrag }
                ))

    val kalkyletre = Kalkyletre(
        regnskapEllerSkattemessigeForskjellerTillegg,
        regnskapEllerSkattemessigeForskjellerFradrag
    )
}

private val midlertidigForskjellKoder = midlertidigForskjellstype_2021
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

private fun hentKategori(midlertidigForskjelltype: Any?): Kategori {
    val kodeVerdi = midlertidigForskjellKoder[midlertidigForskjelltype as String]
        ?: error("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering. Kode: $midlertidigForskjelltype")

    return Kategori.create(kodeVerdi.kodetillegg!!.kategori!!)!!
}

enum class Tilleggskategori(val tilleggskategori: String) {
    REGNSKAP_OG_SKATTEMESSIG("regnskapsmessigOgSkattemessig"),
    REGNSKAPMESSIG("regnskapsmessig"),
    SKATTEMESSIG("skattemessig"),
    REGNSKAP_ELLER_SKATTEMESSIG("");

    companion object {
        fun create(tilleggskategori: String?): Tilleggskategori? {
            return values()
                .firstOrNull { it.tilleggskategori == tilleggskategori }
        }
    }
}

private fun hentTilleggskategori(midlertidigForskjelltype: Any?): Tilleggskategori {
    val kodeVerdi = midlertidigForskjellKoder[midlertidigForskjelltype as String]

    if (kodeVerdi == null) {
        error("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering. Kode: $midlertidigForskjelltype")
    }

    return Tilleggskategori.create(kodeVerdi.kodetillegg!!.tilleggskategori)!!
}
