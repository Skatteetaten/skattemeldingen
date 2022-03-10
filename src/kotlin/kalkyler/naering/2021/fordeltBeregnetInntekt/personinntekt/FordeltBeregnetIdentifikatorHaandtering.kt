object FordeltBeregnetIdentifikatorHaandtering : InlineKalkyle() {
    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        return if (skalBeregnePersoninntekt(gm)) {
            return Verdi(opprettGyldigeIdentifikatorer(gm))
        } else {
            Verdi(GeneriskModell.tom())
        }
    }
}