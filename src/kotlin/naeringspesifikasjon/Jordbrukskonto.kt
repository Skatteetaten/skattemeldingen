package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.jordbrukskonto_andelSaldoJordbrukskontoInntektsOgFradragsfoering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object Jordbrukskonto : HarKalkylesamling {

    internal val driftsresultatSomIkkeInngaarPaaJordbrukskontoKalkyle =
        kalkyle("driftsresultatSomIkkeInngaarPaaJordbrukskonto") {
            forAlleForekomsterAv(modell.jordbruk_jordbrukskonto) {
                settFelt(forekomstType.inntektOgKostnad_driftsresultatSomIkkeInngaarPaaJordbrukskonto) {
                    forekomstType.inntektOgKostnad_driftsinntektKnyttetTilJordHagevirksomhetSomIkkeOmfattesAvJordbrukskonto +
                            forekomstType.inntektOgKostnad_inntektFraGevinstOgTapskonto -
                            forekomstType.inntektOgKostnad_inntektsfradragFraGevinstOgTapskonto +
                            forekomstType.inntektOgKostnad_inntektsfoeringAvNegativSaldo +
                            forekomstType.inntektOgKostnad_tilleggForPositivMidlertidigForskjell -
                            forekomstType.inntektOgKostnad_reduksjonForNegativMidlertidigForskjell -
                            forekomstType.inntektOgKostnad_andelAvDriftskostnadKnyttetTilInntektOgUttakSomIkkeInngaarPaaJordbrukskonto
                }
            }
        }

    internal val driftsresultatSomKanOverfoeresTilJordbrukskontoKalkyle =
        kalkyle("driftsresultatSomKanOverfoeresTilJordbrukskonto") {
            forAlleForekomsterAv(modell.jordbruk_jordbrukskonto) {
                settFelt(forekomstType.driftsresultatSomKanOverfoeresTilJordbrukskonto) {
                    forekomstType.inntektOgKostnad_driftsinntektUttakOgTilskuddKnyttetTilJordHagevirksomhetSomOmfattesAvJordbrukskonto -
                            forekomstType.inntektOgKostnad_sumDriftskostnadJordbruk +
                            forekomstType.inntektOgKostnad_andelAvDriftskostnadKnyttetTilInntektOgUttakSomIkkeInngaarPaaJordbrukskonto +
                            forekomstType.inntektOgKostnad_salgsinntektOgUttakAvVedBiomasseOverfoertFraSkogbruk
                }
            }
        }


    internal val grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaretKalkyle =
        kalkyle("grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaret") {
            forAlleForekomsterAv(modell.jordbruk_jordbrukskonto) {
                settFelt(forekomstType.grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaret) {
                    forekomstType.inngaaendeVerdiPaaJordbrukskonto +
                            forekomstType.andelAvDriftsresultatSomOverfoeresTilJordbrukskonto +
                            forekomstType.inngaaendeVerdiFraErvervetJordbrukskonto -
                            forekomstType.verdiPaaAndelAvRealisertJordbrukskonto
                }
            }
        }

    internal val inntektOgInntektsfradragFraJordbrukskontoKalkyle =
        kalkyle("inntektOgInntektsfradragFraJordbrukskonto") {
            val satser = satser!!
            forAlleForekomsterAv(modell.jordbruk_jordbrukskonto) {
                val sats = if (forekomstType.grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaret stoerreEllerLik 0) {
                    if (forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent() stoerreEnn satser.sats(
                            jordbrukskonto_andelSaldoJordbrukskontoInntektsOgFradragsfoering
                        )
                    ) {
                        forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()
                    } else {
                        satser.sats(jordbrukskonto_andelSaldoJordbrukskontoInntektsOgFradragsfoering)
                    }
                } else {
                    if (forekomstType.satsForInntektsfoeringOgInntektsfradrag.harVerdi() &&
                        forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent() mindreEnn satser.sats(
                            jordbrukskonto_andelSaldoJordbrukskontoInntektsOgFradragsfoering
                        )
                    ) {
                        forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()
                    } else {
                        satser.sats(jordbrukskonto_andelSaldoJordbrukskontoInntektsOgFradragsfoering).absoluttverdi()
                    }
                }

                val inntektEllerFradrag = forekomstType.grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaret.times(sats)

                if (inntektEllerFradrag stoerreEllerLik 0) {
                    settFelt(forekomstType.inntektFraJordbrukskonto) { inntektEllerFradrag }
                } else {
                    settFelt(forekomstType.inntektsfradragFraJordbrukskonto) { inntektEllerFradrag.absoluttverdi() }
                }
           }
        }


    internal val utgaaendeVerdiPaaJordbrukskontokontoKalkyle =
        kalkyle("utgaaendeVerdiPaaToemmerkonto") {
            forAlleForekomsterAv(modell.jordbruk_jordbrukskonto) {
                settFelt(forekomstType.utgaaendeVerdiPaaJordbrukskonto) {
                   forekomstType.inngaaendeVerdiPaaJordbrukskonto  +
                   forekomstType.andelAvDriftsresultatSomOverfoeresTilJordbrukskonto -
                   forekomstType.inntektFraJordbrukskonto +
                   forekomstType.inntektsfradragFraJordbrukskonto
                }
            }
        }


    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            grunnlagForInntektOgFradragPaaJordbrukskontoIInntektsaaretKalkyle,
            inntektOgInntektsfradragFraJordbrukskontoKalkyle,
            driftsresultatSomKanOverfoeresTilJordbrukskontoKalkyle,
            driftsresultatSomIkkeInngaarPaaJordbrukskontoKalkyle,
            utgaaendeVerdiPaaJordbrukskontokontoKalkyle
        )
    }
}
