package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object InntektOgUnderskuddFra2025 {

    internal val samletInntektEllerUnderskuddKalkyle = kalkyle {
        val tilbakefoertUnderskuddFraForhaandsfastsetting = modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraForhaandsfastsetting_tilbakefoertUnderskudd

        hvis(!RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val samletInntektEllerUnderskudd = modell.inntektOgUnderskudd.naeringsinntekt -
                modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
                modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
                modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                modell.inntektOgUnderskudd.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak -
                modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag +
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning -
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand +
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_underskuddFraVirksomhetPaaLandSomKrevesTilbakefoertTilTidligereInntektsaar -
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand

            if (samletInntektEllerUnderskudd mindreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.samletUnderskudd) { samletInntektEllerUnderskudd?.abs() }
            } else {
                if(generiskModell.grupper(modell.inntektOgUnderskuddForVirksomhetPaaSokkel).isNotEmpty()) {
                    settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                        samletInntektEllerUnderskudd?.abs()
                    }
                } else {
                    settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                        samletInntektEllerUnderskudd?.abs() - tilbakefoertUnderskuddFraForhaandsfastsetting medMinimumsverdi 0
                    }
                }
            }
        }
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val verdi = modell.inntektOgUnderskudd.inntektFoerFradragForEventueltAvgittKonsernbidrag +
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning

            val beregnetFinansinntekt =
                if (!tilbakefoertUnderskuddFraForhaandsfastsetting.harVerdi() || (inntektsaar.tekniskInntektsaar >= 2024 && verdi mindreEllerLik 0)) {
                    verdi
                } else if (verdi stoerreEnn 0) {
                    verdi - tilbakefoertUnderskuddFraForhaandsfastsetting medMinimumsverdi 0
                } else {
                    null
                }

            hvis(
                modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto stoerreEllerLik 0 &&
                    beregnetFinansinntekt stoerreEllerLik 0
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                    modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto +
                        beregnetFinansinntekt
                }
            }
            hvis(
                modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto stoerreEllerLik 0 &&
                    beregnetFinansinntekt mindreEnn 0
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                    modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto.tall()
                }
            }
            hvis(
                modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto mindreEnn 0 &&
                    beregnetFinansinntekt stoerreEllerLik 0
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                    beregnetFinansinntekt
                }
            }
            hvis(beregnetFinansinntekt mindreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.samletUnderskudd) {
                    beregnetFinansinntekt.absoluttverdi()
                }
            }
        }
    }

    internal fun GeneriskModellKontekst.forloepigSamletInntektEllerUnderskudd() =
        modell.inntektOgUnderskudd.naeringsinntekt -
            modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
            modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
            modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand -
            modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand -
            modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag

    internal val tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand") {
            val forloepigSamletInntektEllerUnderskudd = forloepigSamletInntektEllerUnderskudd()
            hvis(forloepigSamletInntektEllerUnderskudd stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    val tilbakefoertUnderskudd =
                        modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraForhaandsfastsetting_tilbakefoertUnderskudd.tall()
                    if (tilbakefoertUnderskudd stoerreEnn 0 && forloepigSamletInntektEllerUnderskudd mindreEnn tilbakefoertUnderskudd) {
                        forloepigSamletInntektEllerUnderskudd
                    } else {
                        tilbakefoertUnderskudd
                    }
                }
            }
        }

    internal val tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand") {
            val forloepigSamletInntektEllerUnderskudd = forloepigSamletInntektEllerUnderskudd()
            val tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel.tall()
            val tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand =
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand.tall()
            val tilbakefoertUnderskudd =
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.tilbakefoertUnderskuddFraForhaandsfastsettingFraVirksomhetPaaSokkel_tilbakefoertSokkelunderskudd.tall()
            hvis(
                forloepigSamletInntektEllerUnderskudd stoerreEnn 0 && tilbakefoertUnderskudd stoerreEnn 0 &&
                    (tilbakefoertUnderskudd - tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel) stoerreEnn 0
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    if (forloepigSamletInntektEllerUnderskudd - tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand mindreEnn
                        tilbakefoertUnderskudd - tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel
                    ) {
                        (forloepigSamletInntektEllerUnderskudd - tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand).absoluttverdi()
                    } else {
                        (tilbakefoertUnderskudd - tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel).absoluttverdi()
                    }
                }
            }
        }

    internal val aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaar =
        kalkyle("aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaar") {
            settUniktFelt(modell.inntektOgUnderskudd.tilbakefoertUnderskuddForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaar) {
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand +
                    modell.inntektOgUnderskuddForVirksomhetPaaSokkel.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel
            }
        }
}