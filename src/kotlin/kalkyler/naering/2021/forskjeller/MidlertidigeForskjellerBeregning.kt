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

private fun itererForekomsterAvMidlertidigForskjellHvor(
    kategori: Kategori,
    tilleggskategori: Tilleggskategori,
    feltHenter: (midlertidigForskjell) -> FeltKoordinat<*>
) =
    itererForekomster forekomsterAv midlertidigForskjell filter {
        FeltSpecification(midlertidigForskjell.midlertidigForskjellstype) {
            kategori(it) == kategori
                && tilleggskategori(it) == tilleggskategori
        }
    } forVerdi {
        feltHenter.invoke(it)
    }

internal object MidlertidigeForskjellerIAar {

    val forskjellIAar = SyntetiskFelt(
        midlertidigForskjell,
        "forskjellIAar",
        fieldDataType = FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapOgSkattemessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.regnskapsmessigVerdi - it.skattemessigVerdi somFelt forskjellIAar
        }

    internal val regnskapsmessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdi somFelt forskjellIAar }

    internal val skattemessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdi somFelt forskjellIAar }

    internal val regnskapOgSkattemessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) { it.skattemessigVerdi - it.regnskapsmessigVerdi somFelt forskjellIAar }

    internal val regnskapsmessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdi * (-1) somFelt forskjellIAar }

    internal val skattemessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
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
        fieldDataType = FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapOgSkattemessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.regnskapsmessigVerdiForrigeInntektsaar -
                it.skattemessigVerdiForrigeInntektsaar somFelt forskjellIFjor
        }

    internal val regnskapsmessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdiForrigeInntektsaar somFelt forskjellIFjor }

    internal val skattemessigeForskjellerTillegg =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.TILLEGG,
            Tilleggskategori.SKATTEMESSIG
        ) { it.skattemessigVerdiForrigeInntektsaar somFelt forskjellIFjor }

    internal val regnskapOgSkattemessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAP_OG_SKATTEMESSIG
        ) {
            it.skattemessigVerdiForrigeInntektsaar -
                it.regnskapsmessigVerdiForrigeInntektsaar somFelt forskjellIFjor
        }

    internal val regnskapsmessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
            Kategori.FRADRAG,
            Tilleggskategori.REGNSKAPMESSIG
        ) { it.regnskapsmessigVerdiForrigeInntektsaar * (-1) somFelt forskjellIFjor }

    internal val skattemessigeForskjellerFradrag =
        itererForekomsterAvMidlertidigForskjellHvor(
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
        fieldDataType = FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )
    val fradrag = SyntetiskFelt(
        midlertidigForskjell,
        "andreForskjellerFradrag",
        fieldDataType = FieldDataType(FieldDataType.ActualType.DecimalType, 2)
    )

    internal val regnskapEllerSkattemessigeForskjellerTillegg =
        itererForekomster forekomsterAv midlertidigForskjell filter {
            FeltSpecification(
                midlertidigForskjell.midlertidigForskjellstype
            ) {
                kategori(it) == Kategori.TILLEGG
                    && tilleggskategori(it) == Tilleggskategori.REGNSKAP_ELLER_SKATTEMESSIG
            }
        } forVerdier (
            listOf(
                { f -> f.regnskapsmessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt tillegg },
                { f -> f.skattemessigVerdi.der(Specifications.derVerdiIkkeErNull()) somFelt tillegg }
            ))

    internal val regnskapEllerSkattemessigeForskjellerFradrag =
        itererForekomster forekomsterAv midlertidigForskjell filter {
            FeltSpecification(midlertidigForskjell.midlertidigForskjellstype) {
                kategori(it) == Kategori.FRADRAG
                    && tilleggskategori(it) == Tilleggskategori.REGNSKAP_ELLER_SKATTEMESSIG
            }
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
