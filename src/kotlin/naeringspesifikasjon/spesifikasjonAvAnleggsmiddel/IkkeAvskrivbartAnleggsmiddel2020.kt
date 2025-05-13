package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

internal object IkkeAvskrivbartAnleggsmiddel2020 : HarKalkylesamling {

    internal val utgaaendeVerdi = kalkyle("utgaaendeVerdi") {
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_ikkeAvskrivbartAnleggsmiddel) {
            val utgaaendeVerdiPositivOgNegativ = forekomstType.inngaaendeVerdi +
                forekomstType.nyanskaffelse +
                forekomstType.paakostning -
                forekomstType.offentligTilskudd +
                forekomstType.justeringAvInngaaendeMva +
                forekomstType.justeringForAapenbarVerdiendring -
                forekomstType.vederlagVedRealisasjonOgUttak -
                forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                forekomstType.gevinstOverfoertTilGevinstOgTapskonto -
                forekomstType.tapOverfoertTilGevinstOgTapskonto

            hvis(utgaaendeVerdiPositivOgNegativ stoerreEllerLik 0) {
                settFelt(forekomstType.utgaaendeVerdi) {
                    utgaaendeVerdiPositivOgNegativ
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(utgaaendeVerdi)
    }
}