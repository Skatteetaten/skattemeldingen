package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

internal object SpesifikasjonAvIkkeAvskrivbare : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val utgaaendeVerdiPositivOgNegativ =
            SyntetiskFelt(ikkeAvskrivbartAnleggsmiddel, "utgaaendeVerdiPositivOgNegativ")
    }

    internal val utgaaendeVerdiPositivOgNegativ =
        itererForekomster forekomsterAv ikkeAvskrivbartAnleggsmiddel forVerdi { f ->
            f.inngaaendeVerdi +
                f.nyanskaffelse +
                f.paakostning -
                f.offentligTilskudd +
                f.justeringAvInngaaendeMva +
                f.justeringForAapenbarVerdiendring -
                f.vederlagVedRealisasjonOgUttak -
                f.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                f.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                f.gevinstOverfoertTilGevinstOgTapskonto -
                f.tapOverfoertTilGevinstOgTapskonto somFelt beregnedeFelter.utgaaendeVerdiPositivOgNegativ
        }

    internal val utgaaendeVerdi =
        itererForekomster forekomsterAv ikkeAvskrivbartAnleggsmiddel forVerdi { f ->
            beregnedeFelter.utgaaendeVerdiPositivOgNegativ.der(derVerdiErStoerreEnnEllerLik(0)) somFelt f.utgaaendeVerdi
        }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(utgaaendeVerdiPositivOgNegativ, utgaaendeVerdi).medPostprosessering(this)
    }

    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            it.key != beregnedeFelter.utgaaendeVerdiPositivOgNegativ.key
        }
    }
}