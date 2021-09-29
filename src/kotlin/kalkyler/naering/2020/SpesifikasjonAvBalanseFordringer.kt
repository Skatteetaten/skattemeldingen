object SpesifikasjonAvBalanseFordringer : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val sumKreditsalg = SyntetiskFelt(skattemessigVerdiPaaFordring, "sumKreditsalg")
    }

    private val sumKreditsalgKalkyle =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdier listOf {
            der(
                skattemessigVerdiPaaFordring,
                {

                    (it.kredittsalg + it.kredittsalgIFjor) somFelt beregnedeFelter.sumKreditsalg
                }
            )

        }

    val skattemessigNedskrivningPaaKundefordring =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdier listOf {
            der(
                skattemessigVerdiPaaFordring,
                {
                    (it.konstatertTapPaaKundefordring +
                        it.konstatertTapPaaKundefordringIFjor) / (it.kredittsalg + it.kredittsalgIFjor) *
                        it.kundefordringOgIkkeFakturertDriftsinntekt * 4 somFelt skattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordring
                },
                it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
                    .filterFelt(
                        Specifications.eller(
                            Specifications.derVerdiErNull(),
                            Specifications.derVerdiErMindreEnnEllerLik(0)
                        )
                    ),
                beregnedeFelter.sumKreditsalg.filterFelt(derVerdiIkkeErLik(BigDecimal.ZERO))
            )
        }

    val skattemessigVerdiPaaKundefordring =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdier listOf {
            der(
                skattemessigVerdiPaaFordring,
                {
                    it.kundefordringOgIkkeFakturertDriftsinntekt -
                        it.skattemessigNedskrivningPaaKundefordring - it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet somFelt it.skattemessigVerdiPaaKundefordring
                }
            )
        }

    val sumSkattemessigVerdiPaaFordringer =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdi {
            it.skattemessigVerdiPaaKundefordring +
                it.annenFordring somFelt skattemessigVerdiPaaFordring.sumSkattemessigVerdiPaaFordring
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            sumKreditsalgKalkyle,
            skattemessigNedskrivningPaaKundefordring,
            skattemessigVerdiPaaKundefordring,
            sumSkattemessigVerdiPaaFordringer
        ).medPostprosessering(this)
    }

    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == beregnedeFelter.sumKreditsalg.key)
        }
    }
}