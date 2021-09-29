object SpesifikasjonAvBalanseFordringer : HarKalkyletre {

    val skattemessigNedskrivningPaaKundefordring =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdier listOf {
            der(
                spesifikasjonAvSkattemessigVerdiPaaFordring,
                {
                    (it.konstatertTapPaaKundefordring +
                        it.konstatertTapPaaKundefordringIFjor) / (it.kredittsalg + it.kredittsalgIFjor) *
                        it.kundefordringOgIkkeFakturertDriftsinntekt * 4 somFelt spesifikasjonAvSkattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordring
                },
                it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
                    .filterFelt(
                        Specifications.eller(
                            Specifications.derVerdiErNull(),
                            Specifications.derVerdiErMindreEnnEllerLik(0)
                        )
                    )
            )

        }

    val skattemessigVerdiPaaKundefordring =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdier listOf {
            der(
                spesifikasjonAvSkattemessigVerdiPaaFordring,
                {
                    it.kundefordringOgIkkeFakturertDriftsinntekt -
                        it.skattemessigNedskrivningPaaKundefordring - it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet somFelt it.skattemessigVerdiPaaKundefordring
                }
            )
        }

    val sumSkattemessigVerdiPaaFordringer =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdi {
            it.skattemessigVerdiPaaKundefordring +
                it.annenFordring somFelt spesifikasjonAvSkattemessigVerdiPaaFordring.sumSkattemessigVerdiPaaFordring
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            skattemessigNedskrivningPaaKundefordring, skattemessigVerdiPaaKundefordring,
            sumSkattemessigVerdiPaaFordringer
        )
    }
}