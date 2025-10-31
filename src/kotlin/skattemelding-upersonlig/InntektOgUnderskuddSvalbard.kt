package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3

object InntektOgUnderskuddSvalbard : HarKalkylesamling {

    internal val samletInntektEllerUnderskuddKalkyle = kalkyle {
        hvis(harForekomsterAv(modell.inntektOgUnderskuddSvalbard)) {
            val samletInntektEllerUnderskudd = if (inntektsaar.tekniskInntektsaar < 2024) {
                modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
                    modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag -
                    modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    modell.inntektOgUnderskuddSvalbard.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag +
                    modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                    modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning
            } else {
                modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
                    modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag -
                    modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    modell.inntektOgUnderskuddSvalbard.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag
            }

            if (samletInntektEllerUnderskudd mindreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskuddSvalbard.samletUnderskudd) { samletInntektEllerUnderskudd?.abs() }
            } else {
                settUniktFelt(modell.inntektOgUnderskuddSvalbard.samletInntekt) {
                    (samletInntektEllerUnderskudd?.abs() -
                        modellV3.inntektOgUnderskuddSvalbard.tilbakefoertUnderskuddFraForhaandsfastsettingSenereAar_tilbakefoertUnderskudd - // t.o.m. 2023
                        modell.inntektOgUnderskuddSvalbard.tilbakefoertUnderskuddFraForhaandsfastsetting_tilbakefoertUnderskudd // f.o.m. 2024
                    ) medMinimumsverdi 0
                }
            }
        }
    }

    internal val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle = kalkyle {
        val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )

        settUniktFelt(modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse) {
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse -
                underhaandsakkordMotregnetFremfoertUnderskudd
        }
    }

    internal val fremfoerbartUnderskuddIInntektKalkyle = kalkyle {
        val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )

        val restFremfoertUnderskudd = modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
            underhaandsakkordMotregnetFremfoertUnderskudd

        val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskuddSvalbard.samletUnderskudd.tall(),
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_restOppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )

        settUniktFelt(modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoerbartUnderskuddIInntekt) {
            restFremfoertUnderskudd -
                modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                modell.inntektOgUnderskuddSvalbard.samletUnderskudd -
                restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd
        }
    }

    internal val defaultKonsernbidragSkalBekreftesAvRevisor = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar == 2023) {
            forAlleForekomsterAv(modellV3.inntektOgUnderskuddSvalbard) {
                hvis(forekomstType.konsernbidragSkalBekreftesAvRevisor.harIkkeVerdi()) {
                    settFelt(forekomstType.konsernbidragSkalBekreftesAvRevisor, "false")
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletInntektEllerUnderskuddKalkyle,
            restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
            fremfoerbartUnderskuddIInntektKalkyle,
            defaultKonsernbidragSkalBekreftesAvRevisor // denne må kjøres til slutt
        )
    }
}