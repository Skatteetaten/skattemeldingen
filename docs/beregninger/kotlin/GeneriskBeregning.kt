package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler


interface GeneriskBeregning {
    fun beregn(generiskModell: GeneriskModell): GeneriskModell
}
