package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object InntektOgUnderskudd : HarKalkylesamling {

    internal val samletInntektEllerUnderskuddKalkyle = kalkyle {
        val tilbakefoertUnderskuddFraForhaandsfastsetting = if (inntektsaar.tekniskInntektsaar <= 2023) {
            modellV3.inntektOgUnderskudd.tilbakefoertUnderskuddFraForhaandsfastsettingSenereAar_tilbakefoertUnderskudd
        } else {
            modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraForhaandsfastsetting_tilbakefoertUnderskudd
        }
        hvis(!RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val samletInntektEllerUnderskudd = modell.inntektOgUnderskudd.naeringsinntekt -
                modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
                modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
                modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                modell.inntektOgUnderskudd.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak -
                modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag +
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning -
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand

            if (samletInntektEllerUnderskudd mindreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.samletUnderskudd) { samletInntektEllerUnderskudd?.abs() }
            } else {
                settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                    samletInntektEllerUnderskudd?.abs() - tilbakefoertUnderskuddFraForhaandsfastsetting medMinimumsverdi 0
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

    internal val aaretsFremfoerbareUnderskuddKalkyle = kalkyle("aaretsFremfoerbareUnderskudd") {
        hvis(
            harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel) &&
                (modell.inntektOgUnderskudd.inntektsfradrag_underskudd - modell.inntektOgUnderskuddForVirksomhetPaaSokkel.nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel).erPositiv()
        ) {
            settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsFremfoerbareUnderskudd) {
                (modell.inntektOgUnderskudd.inntektsfradrag_underskudd -
                    modell.inntektOgUnderskuddForVirksomhetPaaSokkel.nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                    modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd -
                    modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd) medMinimumsverdi 0
            }
        }
    }

    internal val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle = kalkyle {
        val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
            modell.inntektOgUnderskudd.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )

        settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse) {
            modell.inntektOgUnderskudd.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse -
                underhaandsakkordMotregnetFremfoertUnderskudd
        }
    }

    internal val fremfoerbartUnderskuddIInntektKalkyle = kalkyle {
        hvis(!harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel)) {
            val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
                modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
                modell.inntektOgUnderskudd.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
            )

            val restFremfoertUnderskudd =
                modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
                    underhaandsakkordMotregnetFremfoertUnderskudd

            val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd = minsteVerdiAv(
                modell.inntektOgUnderskudd.samletUnderskudd.tall(),
                modell.inntektOgUnderskudd.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
            )

            settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoerbartUnderskuddIInntekt) {
                restFremfoertUnderskudd -
                    modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    modell.inntektOgUnderskudd.samletUnderskudd -
                    restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd
            }
        }
    }

    internal val fremfoerbartUnderskuddIInntektFraVirksomhetPaaSokkelKalkyle = kalkyle {
            hvis(
            harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel) &&
                (modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsFremfoerbareUnderskudd.harVerdi() ||
                    modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar.harVerdi())
        ) {
            settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoerbartUnderskuddIInntekt) {
                (modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsFremfoerbareUnderskudd +
                    modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar -
                    modellV3.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIForegaaendeAarsFremfoerbareUnderskudd -
                    modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) medMinimumsverdi 0
            }
        }
    }

    internal val defaultKonsernbidragSkalBekreftesAvRevisor = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar == 2023) {
            forAlleForekomsterAv(modellV3.inntektOgUnderskudd) {
                hvis(forekomstType.konsernbidragSkalBekreftesAvRevisor.harIkkeVerdi()) {
                    settFelt(forekomstType.konsernbidragSkalBekreftesAvRevisor, "false")
                }
            }
        }
        hvis(inntektsaar.tekniskInntektsaar >= 2024) {
            forAlleForekomsterAv(modell.inntektOgUnderskudd) {
                hvis(
                    antallForekomsterAv(forekomstType.inntekt_mottattKonsernbidrag_mottattKonsernbidragPerAvgiver).erPositiv() &&
                        forekomstType.inntekt_mottattKonsernbidrag_mottattKonsernbidragSkalBekreftesAvRevisor.harIkkeVerdi()
                ) {
                    settFelt(forekomstType.inntekt_mottattKonsernbidrag_mottattKonsernbidragSkalBekreftesAvRevisor, "false")
                }
                hvis(
                    antallForekomsterAv(forekomstType.inntektsfradrag_avgittKonsernbidrag_avgittKonsernbidragPerMottaker).erPositiv() &&
                        forekomstType.inntektsfradrag_avgittKonsernbidrag_avgittKonsernbidragSkalBekreftesAvRevisor.harIkkeVerdi()
                ) {
                    settFelt(forekomstType.inntektsfradrag_avgittKonsernbidrag_avgittKonsernbidragSkalBekreftesAvRevisor, "false")
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletInntektEllerUnderskuddKalkyle,
            restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
            fremfoerbartUnderskuddIInntektKalkyle,
            fremfoerbartUnderskuddIInntektFraVirksomhetPaaSokkelKalkyle,
            aaretsFremfoerbareUnderskuddKalkyle,
            defaultKonsernbidragSkalBekreftesAvRevisor // denne må kjøres til slutt
        )
    }
}