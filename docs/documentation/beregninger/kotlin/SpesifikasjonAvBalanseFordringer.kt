object SpesifikasjonAvBalanseFordringer : HarKalkyletre {

    val skattemessigNedskrivningPaaKundefordring =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdier listOf(
            {
                der(
                    skattemessigVerdiPaaFordring,
                    {
                        (it.konstatertTapPaaKundefordring +
                            it.konstatertTapPaaKundefordringIFjor) / (it.kredittsalg + it.kredittsalgIFjor) *
                            it.kundefordringOgIkkeFakturertDriftsinntekt * 4 somFelt skattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordring
                    },
                    it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
                        .filterFelt(Specifications.derVerdiErNull())
                )

            },
            {
                der(
                    skattemessigVerdiPaaFordring,
                    {
                        it.kundefordringOgIkkeFakturertDriftsinntekt * FordringerSatser.sats somFelt skattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
                    },
                    it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet
                        .filterFelt(Specifications.ikke(Specifications.derVerdiErNull()))
                )
            }
        )

    val skattemessigVerdiPaaKundefordring =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdier listOf(
            {
                der(
                    skattemessigVerdiPaaFordring,
                    { it.kundefordringOgIkkeFakturertDriftsinntekt - it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet somFelt it.skattemessigVerdiPaaKundefordring },
                    skattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet.filterFelt(
                        Specifications.ikke(Specifications.derVerdiErNull())
                    )
                )
            },
            {
                der(
                    skattemessigVerdiPaaFordring,
                    { it.kundefordringOgIkkeFakturertDriftsinntekt - it.skattemessigNedskrivningPaaKundefordring somFelt it.skattemessigVerdiPaaKundefordring },
                    skattemessigVerdiPaaFordring.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet.filterFelt(
                        Specifications.derVerdiErNull()
                    )
                )
            }
        )

    val sumSkattemessigVerdiPaaFordringer =
        itererForekomster forekomsterAv skattemessigVerdiPaaFordring forVerdi {
            it.skattemessigVerdiPaaKundefordring +
                it.annenFordring somFelt skattemessigVerdiPaaFordring.sumSkattemessigVerdiPaaFordring
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            skattemessigNedskrivningPaaKundefordring, skattemessigVerdiPaaKundefordring,
            sumSkattemessigVerdiPaaFordringer
        )
    }
}