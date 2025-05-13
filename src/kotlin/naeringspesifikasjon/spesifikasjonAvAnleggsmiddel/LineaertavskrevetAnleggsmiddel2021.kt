package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

object LineaertavskrevetAnleggsmiddel2021 : HarKalkylesamling {

    internal val antallAarErvervet =
        opprettSyntetiskFelt(
            modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel,
            "antallAarErvervet"
        )
    internal val antallAarErvervetKalkyle = kalkyle("antallAarErvervetKalkyle") {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) {
            settFelt(antallAarErvervet) {
                (forekomstType.ervervsdato.aar() - inntektsaar).absoluttverdi()
            }
        }
    }

    internal val utgaaendeVerdiKalkyle = kalkyle("utgaaendeVerdiKalkyle") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) {
            hvis(antallAarErvervet stoerreEnn 0) {
                val utgaaendeVerdi = forekomstType.inngaaendeVerdi -
                    forekomstType.offentligTilskudd +
                    forekomstType.justeringAvInngaaendeMva +
                    forekomstType.justeringForAapenbarVerdiendring -
                    forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                    forekomstType.aaretsAvskrivning -
                    forekomstType.vederlagVedRealisasjonOgUttak +
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    forekomstType.gevinstOverfoertTilGevinstOgTapskonto -
                    forekomstType.tapOverfoertTilGevinstOgTapskonto +
                    forekomstType.verdiOverfoertFraPaakostningVedRealisasjon -
                    forekomstType.verdiOverfoertTilDriftsmiddelVedRealisasjon

                settFelt(forekomstType.utgaaendeVerdi) { utgaaendeVerdi medMinimumsverdi 0 }
            }

            hvis(antallAarErvervet lik 0) {
                val paakostningEllerAnskaffelseskost =
                    if (forekomstType.paakostning.harIkkeVerdi()) {
                        forekomstType.anskaffelseskost.tall()
                    } else {
                        forekomstType.paakostning.tall()
                    }

                val utgaaendeVerdi = paakostningEllerAnskaffelseskost -
                    forekomstType.offentligTilskudd +
                    forekomstType.justeringAvInngaaendeMva +
                    forekomstType.justeringForAapenbarVerdiendring -
                    forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                    forekomstType.aaretsAvskrivning -
                    forekomstType.vederlagVedRealisasjonOgUttak +
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    forekomstType.gevinstOverfoertTilGevinstOgTapskonto -
                    forekomstType.tapOverfoertTilGevinstOgTapskonto

                settFelt(forekomstType.utgaaendeVerdi) { utgaaendeVerdi medMinimumsverdi 0 }
            }
        }
    }

    internal val grunnlagForAvskrivningOgInntektsfoeringKalkyle =
        kalkyle("grunnlagForAvskrivningOgInntektsfoeringKalkyle") {
            forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) {
                val korrigeringer = forekomstType.justeringAvInngaaendeMva +
                    forekomstType.justeringForAapenbarVerdiendring -
                    forekomstType.offentligTilskudd -
                    forekomstType.vederlagVedRealisasjonOgUttak +
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar -
                    forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                    forekomstType.reinvestertBetingetSkattefriSalgsgevinst -
                    forekomstType.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst

                val erErvervetIInntektsaaret = antallAarErvervet lik 0
                hvis(erErvervetIInntektsaaret && forekomstType.paakostning.harVerdi()) {
                    settFelt(forekomstType.grunnlagForAvskrivningOgInntektsfoering) {
                        forekomstType.paakostning + korrigeringer
                    }
                }

                hvis(erErvervetIInntektsaaret && forekomstType.paakostning.harIkkeVerdi()) {
                    settFelt(forekomstType.grunnlagForAvskrivningOgInntektsfoering) {
                        forekomstType.anskaffelseskost + korrigeringer
                    }
                }

                hvis(!erErvervetIInntektsaaret) {
                    settFelt(forekomstType.grunnlagForAvskrivningOgInntektsfoering) {
                        forekomstType.inngaaendeVerdi + korrigeringer
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            antallAarErvervetKalkyle,
            utgaaendeVerdiKalkyle,
            grunnlagForAvskrivningOgInntektsfoeringKalkyle
        )
    }
}