package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV1

object InntektOgUnderskudd2021 : HarKalkylesamling {

    internal val samletInntektEllerUnderskuddKalkyle = kalkyle("samletInntektEllerUnderskudd") {
        forekomsterAv(modellV1.inntektOgUnderskudd) forHverForekomst {
            val samletInntektEllerUnderskudd = forekomstType.naeringsinntekt -
                forekomstType.inntektsfradrag_underskudd +
                forekomstType.inntekt_mottattKonsernbidrag -
                forekomstType.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                forekomstType.inntektsfradrag_yttKonsernbidrag

            if (samletInntektEllerUnderskudd.erPositiv()) {
                settFelt(forekomstType.samletInntekt) {
                    samletInntektEllerUnderskudd
                }
            } else {
                settFelt(forekomstType.samletUnderskudd) {
                    samletInntektEllerUnderskudd.absoluttverdi()
                }
            }
        }
    }

    internal val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle = kalkyle("aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar") {
        forekomsterAv(modellV1.inntektOgUnderskudd) forHverForekomst {
            val inntektFoerAnvendelseAvUnderskudd = forekomstType.naeringsinntekt -
                forekomstType.inntektsfradrag_underskudd +
                forekomstType.inntekt_mottattKonsernbidrag

            val underhaandsakkordMotregnetFremfoertUnderskudd = if (
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
                stoerreEllerLik forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall()
            ) {
                forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar
            } else {
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
            }

            val restFremfoertUnderskudd = forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
                underhaandsakkordMotregnetFremfoertUnderskudd

            val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar = beregnHvis(inntektFoerAnvendelseAvUnderskudd stoerreEnn 0) {
                if (inntektFoerAnvendelseAvUnderskudd stoerreEllerLik restFremfoertUnderskudd) {
                    restFremfoertUnderskudd
                } else {
                    inntektFoerAnvendelseAvUnderskudd
                }
            }
            settFelt(forekomstType.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
            }
        }
    }

    internal val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle = kalkyle("restOppnaaddUnderhaandsakkordOgGjeldsettergivelse") {
        forekomsterAv(modellV1.inntektOgUnderskudd) forHverForekomst {
            val underhaandsakkordMotregnetFremfoertUnderskudd = if (
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
                stoerreEllerLik forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall()
            ) {
                forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar
            } else {
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
            }
            settFelt(forekomstType.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse) {
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse -
                    underhaandsakkordMotregnetFremfoertUnderskudd
            }
        }
    }

    internal val fremfoerbartUnderskuddIInntektKalkyle = kalkyle("fremfoerbartUnderskuddIInntekt") {
        forekomsterAv(modellV1.inntektOgUnderskudd) forHverForekomst {
            val underhaandsakkordMotregnetFremfoertUnderskudd = if (
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
                stoerreEllerLik forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall()
            ) {
                forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar
            } else {
                forekomstType.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse
            }
            val restFremfoertUnderskudd = forekomstType.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
                underhaandsakkordMotregnetFremfoertUnderskudd

            val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd = if (
                forekomstType.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse
                stoerreEllerLik forekomstType.samletUnderskudd.tall()
            ) {
                forekomstType.samletUnderskudd
            } else {
                forekomstType.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse
            }

            settFelt(forekomstType.underskuddTilFremfoering_fremfoerbartUnderskuddIInntekt) {
                restFremfoertUnderskudd -
                    forekomstType.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    forekomstType.samletUnderskudd -
                    restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle,
            samletInntektEllerUnderskuddKalkyle,
            restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
            fremfoerbartUnderskuddIInntektKalkyle
        )
    }
}