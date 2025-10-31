package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object InntektFoerFradragForEventueltAvgittKonsernbidrag : HarKalkylesamling {

    internal val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle = kalkyle {
        val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
            modell.inntektOgUnderskudd.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )
        val restFremfoertUnderskudd =
            modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
                underhaandsakkordMotregnetFremfoertUnderskudd
        hvis(
            (!harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel) &&
                !RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi()))
        ) {

            val inntektFoerAnvendelseAvUnderskudd = modell.inntektOgUnderskudd.naeringsinntekt -
                modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag

            hvis(inntektFoerAnvendelseAvUnderskudd stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                    minsteVerdiAv(restFremfoertUnderskudd, inntektFoerAnvendelseAvUnderskudd)
                }
            }
        }
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val inntektFoerAnvendelseAvUnderskudd =
                modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt.tall()

            hvis(inntektFoerAnvendelseAvUnderskudd stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                    minsteVerdiAv(restFremfoertUnderskudd, inntektFoerAnvendelseAvUnderskudd)
                }
            }
        }
    }

    internal val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarSvalbardKalkyle = kalkyle {
        val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
            modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
        )

        val restFremfoertUnderskudd = modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
            underhaandsakkordMotregnetFremfoertUnderskudd

        val inntektFoerAnvendelseAvUnderskudd = modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
            modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
            modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag

        hvis(inntektFoerAnvendelseAvUnderskudd stoerreEnn 0) {
            settUniktFelt(modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                minsteVerdiAv(restFremfoertUnderskudd, inntektFoerAnvendelseAvUnderskudd)
            }
        }
    }

    internal val inntektFoerFradragForEventueltAvgittKonsernbidrag =
        kalkyle("inntektFoerFradragForEventueltAvgittKonsernbidrag") {
            hvis(
                !harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel)
                    && !RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.inntektFoerFradragForEventueltAvgittKonsernbidrag) {
                    modell.inntektOgUnderskudd.naeringsinntekt -
                        modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
                        modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                        modell.inntektOgUnderskudd.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak
                }
            }
            hvis(
                !harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel)
                    && RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())
                    && (modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt.harVerdi() ||
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd.harVerdi())
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.inntektFoerFradragForEventueltAvgittKonsernbidrag) {
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt -
                        modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd -
                        modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
                }
            }
        }

    internal val inntektFoerFradragForEventueltAvgittKonsernbidragSvalbard =
        kalkyle("inntektFoerFradragForEventueltAvgittKonsernbidragSvalbard") {
                settUniktFelt(modell.inntektOgUnderskuddSvalbard.inntektFoerFradragForEventueltAvgittKonsernbidrag) {
                    modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
                        modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
                        modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag -
                        modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                        modell.inntektOgUnderskuddSvalbard.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak
                }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle,
            aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarSvalbardKalkyle,
            inntektFoerFradragForEventueltAvgittKonsernbidrag,
            inntektFoerFradragForEventueltAvgittKonsernbidragSvalbard,
        )
    }
}