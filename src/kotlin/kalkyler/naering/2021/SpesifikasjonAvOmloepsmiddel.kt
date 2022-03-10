object SpesifikasjonAvOmloepsmiddel : HarKalkyletre, PostProsessering {

    internal object BeregnendeFelter {
        val kredittSalgSum =
            SyntetiskFelt(spesifikasjonAvSkattemessigVerdiPaaFordring, "kredittSalgSum")
    }

    internal val kredittSalgSumKalkyle =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdi {
            it.kredittsalg + it.kredittsalgIFjor somFelt BeregnendeFelter.kredittSalgSum
        }

    val skattemessigNedskrivningPaaKundefordringKalkyle =
        itererForekomster gitt
            ForekomstOgVerdi(spesifikasjonAvSkattemessigVerdiPaaFordring) {
                BeregnendeFelter.kredittSalgSum.filterFelt(
                    Specifications.derVerdiIkkeErLik(BigDecimal.ZERO)
                )
            } forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdi {
            der(
                spesifikasjonAvSkattemessigVerdiPaaFordring,
                {
                    (it.konstatertTapPaaKundefordring +
                        it.konstatertTapPaaKundefordringIFjor) / BeregnendeFelter.kredittSalgSum *
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

    val skattemessigVerdiPaaKundefordringKalkyle =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdier listOf {
            der(
                spesifikasjonAvSkattemessigVerdiPaaFordring,
                {
                    it.kundefordringOgIkkeFakturertDriftsinntekt -
                        it.skattemessigNedskrivningPaaKundefordring - it.skattemessigNedskrivningPaaKundefordringForNyetablertVirksomhet somFelt it.skattemessigVerdiPaaKundefordring
                }
            )
        }

    val sumSkattemessigVerdiPaaFordringerKalkyle =
        itererForekomster forekomsterAv spesifikasjonAvSkattemessigVerdiPaaFordring forVerdi {
            it.skattemessigVerdiPaaKundefordring +
                it.annenFordring somFelt spesifikasjonAvSkattemessigVerdiPaaFordring.sumSkattemessigVerdiPaaFordring
        }

    val sumVerdiAvVarelagerKalkyle =
        itererForekomster forekomsterAv spesifikasjonAvVarelager forVerdi {
            it.raavareOgInnkjoeptHalvfabrikata +
                it.vareUnderTilvirkning +
                it.ferdigTilvirketVare +
                it.innkjoeptVareForVideresalg +
                it.buskap +
                it.selvprodusertVareBenyttetIEgenProduksjon +
                it.reinPelsdyrOgPelsdyrskinnPaaLager somFelt it.sumVerdiAvVarelager
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            kredittSalgSumKalkyle,
            skattemessigNedskrivningPaaKundefordringKalkyle,
            skattemessigVerdiPaaKundefordringKalkyle,
            sumSkattemessigVerdiPaaFordringerKalkyle,
            sumVerdiAvVarelagerKalkyle
        ).medPostprosessering(this)
    }

    //Filtrerer bort hjelpeberegning
    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            it.key != BeregnendeFelter.kredittSalgSum.key
        }
    }
}