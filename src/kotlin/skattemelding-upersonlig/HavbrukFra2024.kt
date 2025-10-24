package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettUniktSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.overdragelsestype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV4
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet

object HavbrukFra2024 : HarKalkylesamling {

    internal val negativGrunnrenteinntektIHavbruksvirksomhetTilFremfoering  =
        opprettUniktSyntetiskFelt(modell.havbruksvirksomhet, "negativGrunnrenteinntektIHavbruksvirksomhetTilFremfoering")

    internal val grunnrenteinntektHavbruk = kalkyle("grunnrenteinntektHavbruk") {
        hvis(erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
            val satser = satser!!

                val sumAvgittGrunnrenteinntekt = sumAvgittGrunnrenteinntekt()

                val sumMottattGrunnrenteinntekt = sumMottattGrunnrenteinntekt()

                val positivGrunnrenteinntektFoerBunnfradrag = if (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.harVerdi()) {
                    BigDecimal.ZERO
                } else {
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                        sumAvgittGrunnrenteinntekt +
                        sumMottattGrunnrenteinntekt medMinimumsverdi 0
                }

            //Nye havbruksberegninger fra og med 2025
            if (inntektsaar.tekniskInntektsaar >= 2025) {
                //Beregning av "aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar"
                hvis(
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar stoerreEnn BigDecimal.ZERO
                        && modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning stoerreEnn BigDecimal.ZERO
                ) {
                    hvis(
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar.tall() stoerreEllerLik
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.tall()
                    ) {
                        settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                                modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.tall()
                        }
                    }
                    hvis(
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.tall() stoerreEnn
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar.tall()
                    ) {
                        settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar.tall()
                        }
                    }
                }
            }

            //Hjelpeberegning Rest negativ grunnrenteinntekt til fremføring
            val restNegativGrunnrenteinntektTilFremfoering =
                modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar +
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar


            if (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.harVerdi()) {
                settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                    BigDecimal.ZERO
                }
            } else {
                hvis(
                    inntektsaar.tekniskInntektsaar >= 2025 &&
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.harVerdi()
                ) {
                    if (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar.harVerdi()) {
                        //Hjelpeberegning rest positiv grunnrenteinntekt etter anvendelse av tidligere års negative grunnrenteinntekt
                        val restPositivGrunnrenteinntektEtterAnvendelseAvTidligereAarsNegativeGrunnrenteinntekt = if (
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.tall()!! stoerreEllerLik
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar.tall()
                        ) {
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                                modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar
                        } else {
                            BigDecimal.ZERO
                        }
                        hvis(restPositivGrunnrenteinntektEtterAnvendelseAvTidligereAarsNegativeGrunnrenteinntekt stoerreEllerLik BigDecimal.ZERO) {
                            settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                                restPositivGrunnrenteinntektEtterAnvendelseAvTidligereAarsNegativeGrunnrenteinntekt -
                                    sumAvgittGrunnrenteinntekt medMinimumsverdi 0
                            }
                        }
                    } else {
                        settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                                sumAvgittGrunnrenteinntekt medMinimumsverdi 0
                        }
                    }
                }
            }


                if (inntektsaar.tekniskInntektsaar <= 2024) {
                    if (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.harVerdi()) {
                        settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                            BigDecimal.ZERO
                        }
                    } else {
                        settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                            (positivGrunnrenteinntektFoerBunnfradrag -
                                modellV4.havbruksvirksomhet.fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar) medMinimumsverdi 0
                        }
                    }
                }
                val andelAvMaksimaltTillattBiomasse = if (inntektsaar.tekniskInntektsaar >= 2025) {
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimaltTillattBiomasse
                } else {
                    modellV4.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimalTillattBiomasse
                }

                hvis(andelAvMaksimaltTillattBiomasse.harVerdi()) {
                    settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag) {
                        (satser.sats(Sats.havbruk_maksimaltBunnfradrag) *
                            (BigDecimal(1) - satser.sats(Sats.skattPaaAlminneligInntekt_sats)) *
                            andelAvMaksimaltTillattBiomasse / 100)
                            .somHeltall()
                    }
                }


                settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt) {
                    (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag -
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag).medMinimumsverdi(0)
                }

            settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt) {
                (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                    sumMottattGrunnrenteinntekt).medMinimumsverdi(0)
            }

            settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift) {
                    (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt *
                        satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
               }


                settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteskatt) {
                    (   modell.havbruksvirksomhet.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift -
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_egenProduksjonsavgift +
                            sumAvgittProduksjonsavgift() -
                            sumMottattProduksjonsavgift()).medMinimumsverdi(0)
                }

                var erUttredenEllerSalgMedOpphoerAvVirksomhet = false
                forAlleForekomsterAv(modell.havbruksvirksomhet.overdragelseAvHavbruksvirksomhet) {
                    hvis (forekomstType.overdragelsestype lik overdragelsestype.kode_uttreden ||
                        (forekomstType.overdragelsestype lik overdragelsestype.kode_salgVedOpphoerAvVirksomhet &&
                            forekomstType.overdragelsenErGjennomfoertMedKontinuitet.erSann())) {
                        erUttredenEllerSalgMedOpphoerAvVirksomhet = true
                    }
                }

                if (erUttredenEllerSalgMedOpphoerAvVirksomhet) {
                    settUniktFelt(
                        if (inntektsaar.tekniskInntektsaar >= 2025) {
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteskattVedOpphoerAvGrunnrenteskattepliktigHavbruksvirksomhet
                        } else {
                            modellV4.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteskattOpphoerAvGrunnrenteskattepliktigHavbruksvirksomhet
                        }
                    ) {
                        (modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt *
                            satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
                    }
                }

            if (inntektsaar.tekniskInntektsaar <= 2024) {
                settUniktFelt(negativGrunnrenteinntektIHavbruksvirksomhetTilFremfoering) {
                    (modellV4.havbruksvirksomhet.fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar +
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning +
                        sumAvgittGrunnrenteinntekt() -
                        sumMottattGrunnrenteinntekt()
                        ).medMinimumsverdi(0)

                }
            }

            if (inntektsaar.tekniskInntektsaar >= 2025) {
                hvis(restNegativGrunnrenteinntektTilFremfoering stoerreEnn sumMottattGrunnrenteinntekt) {
                    settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_spesifikasjonAvNegativGrunnrenteinntekt_fremfoerbarNegativGrunnrenteinntekt) {
                        restNegativGrunnrenteinntektTilFremfoering - sumMottattGrunnrenteinntekt
                    }
                }
            }

        }
    }

    private fun GeneriskModellKontekst.sumMottattProduksjonsavgift(): BigDecimal? {
        val sumMottattProduksjonsavgift =
            forekomsterAv(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_selskapSomDelerProduksjonsavgift) summerVerdiFraHverForekomst {
                forekomstType.mottattProduksjonsavgift.tall()
            }
        return sumMottattProduksjonsavgift
    }

    private fun GeneriskModellKontekst.sumAvgittProduksjonsavgift(): BigDecimal? {
        val sumAvgittProduksjonsavgift =
            forekomsterAv(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_selskapSomDelerProduksjonsavgift) summerVerdiFraHverForekomst {
                forekomstType.avgittProduksjonsavgift.tall()
            }
        return sumAvgittProduksjonsavgift
    }

    private fun GeneriskModellKontekst.sumMottattGrunnrenteinntekt(): BigDecimal? {
        val sumMottattGrunnrenteinntekt =
            forekomsterAv(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_samordningAvGrunnrenteinntekt) summerVerdiFraHverForekomst  {
                forekomstType.mottattGrunnrenteinntekt.tall()
            }
        return sumMottattGrunnrenteinntekt
    }

    private fun GeneriskModellKontekst.sumAvgittGrunnrenteinntekt(): BigDecimal? {
        val sumAvgittGrunnrenteinntekt =
            forekomsterAv(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_samordningAvGrunnrenteinntekt) summerVerdiFraHverForekomst {
                forekomstType.avgittGrunnrenteinntekt.tall()
            }
        return sumAvgittGrunnrenteinntekt
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            grunnrenteinntektHavbruk
        )
    }
}
