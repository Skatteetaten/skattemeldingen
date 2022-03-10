internal object PermanenteForskjellerBeregning : HarKalkyletre {

    internal val permanenteForskjellerTillegg =
        summer forekomsterAv permanentForskjell filter {
            FeltSpecification(permanentForskjell.permanentForskjellstype) {
                kategori(it) == Kategori.TILLEGG
            }
        } forVerdi {
            it.beloep
        } verdiSom sumTilleggINaeringsinntekt

    internal val permanenteForskjellerFradrag =
        summer forekomsterAv permanentForskjell filter {
            FeltSpecification(permanentForskjell.permanentForskjellstype) {
                kategori(it) == Kategori.FRADRAG
            }
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