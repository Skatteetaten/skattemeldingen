internal object PermanenteForskjellerBeregning : HarKalkyletre {

    private val permanentForskjellKoder = permanentForskjellstype_2021
        .hentKodeverdier()
        .associateBy { it.kode }

    enum class Kategori(val kategori: String) {
        TILLEGG("tillegg"),
        FRADRAG("fradrag");

        companion object {
            fun create(kategori: String): Kategori? {
                return values()
                    .firstOrNull { it.kategori == kategori }
            }
        }
    }

    private fun hentKategori(permanentForskjelltype: Any?): Kategori {
        val kodeVerdi = permanentForskjellKoder[permanentForskjelltype as String]
            ?: error("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering. Kode: $permanentForskjelltype")

        return Kategori.create(kodeVerdi.kodetillegg!!.kategori!!)!!
    }

    internal val permanenteForskjellerTillegg =
        summer forekomsterAv permanentForskjell filter {
            FeltSpecification(
                permanentForskjell.permanentForskjellstype,
                Specification {
                    hentKategori(it) == Kategori.TILLEGG
                }
            )
        } forVerdi {
            it.beloep
        } verdiSom sumTilleggINaeringsinntekt

    internal val permanenteForskjellerFradrag =
        summer forekomsterAv permanentForskjell filter {
            FeltSpecification(
                permanentForskjell.permanentForskjellstype,
                Specification {
                    hentKategori(it) == Kategori.FRADRAG
                }
            )
        } forVerdi {
            it.beloep
        } verdiSom sumFradragINaeringsinntekt

    internal val kalkyletre = Kalkyletre(
        permanenteForskjellerTillegg,
        permanenteForskjellerFradrag
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}