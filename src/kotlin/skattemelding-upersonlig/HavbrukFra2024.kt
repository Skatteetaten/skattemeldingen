package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v5_2025.v5.havbruksvirksomhetForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.overdragelsestype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet

object HavbrukFra2024 : HarKalkylesamling {

    internal val grunnrenteinntektHavbruk = kalkyle("grunnrenteinntektHavbruk") {
        hvis(erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
            val satser = satser!!

            forAlleForekomsterAv(modell.havbruksvirksomhet) {
                val sumAvgittGrunnrenteinntekt = sumAvgittGrunnrenteinntekt()

                val sumMottattGrunnrenteinntekt = sumMottattGrunnrenteinntekt()

                if (forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.harVerdi()) {
                    settFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                        BigDecimal.ZERO
                    }
                } else {
                    settFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                        (forekomstType.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                            forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                            sumAvgittGrunnrenteinntekt +
                            sumMottattGrunnrenteinntekt -
                            forekomstType.fremfoertNegativGrunnrenteinntektInkludertRenterFraTidligereAar) medMinimumsverdi 0
                    }
                }


                hvis(forekomstType.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimalTillattBiomasse.harVerdi()) {
                    settFelt(forekomstType.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag) {
                        (satser.sats(Sats.havbruk_maksimaltBunnfradrag) *
                            (BigDecimal(1) - satser.sats(Sats.skattPaaAlminneligInntekt_sats)) *
                            forekomstType.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimalTillattBiomasse / 100)
                            .somHeltall()
                    }
                }


                settFelt(forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt) {
                    (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag -
                        forekomstType.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag).medMinimumsverdi(0)
                }

                settFelt(forekomstType.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt) {
                    (forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                        sumMottattGrunnrenteinntekt).medMinimumsverdi(0)
                }

               settFelt(forekomstType.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift) {
                    (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt *
                        satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
               }


                settFelt(forekomstType.beregnetGrunnrenteskatt_positivGrunnrenteskatt) {
                    (   forekomstType.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift -
                        forekomstType.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_egenProduksjonsavgift +
                            sumAvgittProduksjonsavgift() -
                            sumMottattProduksjonsavgift()).medMinimumsverdi(0)
                }

                var erUttredenEllerSalgMedOpphoerAvVirksomhet = false
                forAlleForekomsterAv(forekomstType.overdragelseAvHavbruksvirksomhet) {
                    hvis (forekomstType.overdragelsestype lik overdragelsestype.kode_uttreden ||
                        (forekomstType.overdragelsestype lik overdragelsestype.kode_salgVedOpphoerAvVirksomhet &&
                            forekomstType.overdragelsenErGjennomfoertMedKontinuitet.erSann())) {
                        erUttredenEllerSalgMedOpphoerAvVirksomhet = true
                    }
                }

                if (erUttredenEllerSalgMedOpphoerAvVirksomhet) {
                    settFelt(forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteskattOpphoerAvGrunnrenteskattepliktigHavbruksvirksomhet) {
                        (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt *
                            satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
                    }
                }
            }
        }
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumMottattProduksjonsavgift(): BigDecimal? {
        val sumMottattProduksjonsavgift =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_selskapSomDelerProduksjonsavgift) summerVerdiFraHverForekomst {
                forekomstType.mottattProduksjonsavgift.tall()
            }
        return sumMottattProduksjonsavgift
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumAvgittProduksjonsavgift(): BigDecimal? {
        val sumAvgittProduksjonsavgift =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_selskapSomDelerProduksjonsavgift) summerVerdiFraHverForekomst {
                forekomstType.avgittProduksjonsavgift.tall()
            }
        return sumAvgittProduksjonsavgift
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumMottattGrunnrenteinntekt(): BigDecimal? {
        val sumMottattGrunnrenteinntekt =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_samordningAvGrunnrenteinntekt) summerVerdiFraHverForekomst  {
                forekomstType.mottattGrunnrenteinntekt.tall()
            }
        return sumMottattGrunnrenteinntekt
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumAvgittGrunnrenteinntekt(): BigDecimal? {
        val sumAvgittGrunnrenteinntekt =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_samordningAvGrunnrenteinntekt) summerVerdiFraHverForekomst {
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
