package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.selskapsmelding.sdf.domenemodell.v2_2023.v2.havbruksvirksomhetForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV2
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.opplysningspliktForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.opplysningspliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet

object Havbruk : HarKalkylesamling {

    internal val grunnrenteinntektHavbruk = kalkyle("grunnrenteinntektHavbruk") {
        hvis(erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
            val satser = satser!!
            val organisasjonsnummer = opplysningspliktForekomst.organisasjonsnummer.verdi()

            forAlleForekomsterAv(modellV2.havbruksvirksomhet) {
                val sumAvgittGrunnrenteinntekt = sumAvgittGrunnrenteinntekt(organisasjonsnummer)

                val sumMottattGrunnrenteinntekt = sumMottattGrunnrenteinntekt(organisasjonsnummer)

                if (forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.harVerdi()) {
                    settFelt(modellV2.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                        BigDecimal.ZERO
                    }
                } else {
                    settFelt(modellV2.havbruksvirksomhet.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag) {
                        (forekomstType.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                            forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                            sumAvgittGrunnrenteinntekt + sumMottattGrunnrenteinntekt) medMinimumsverdi 0
                    }
                }

                forAlleForekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) {
                    hvis(forekomstType.andelAvMaksimalTillattBiomasse.harVerdi()) {
                        settFelt(forekomstType.bunnfradrag) {
                            (satser.sats(Sats.havbruk_maksimaltBunnfradrag) *
                                (BigDecimal(1) - satser.sats(Sats.skattPaaAlminneligInntekt_sats)) *
                                forekomstType.andelAvMaksimalTillattBiomasse / 100).somHeltall()
                        }
                    }
                }

                settFelt(forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt) {
                    (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntektFoerBunnfradrag -
                        sumBunnfradrag(organisasjonsnummer)) medMinimumsverdi 0
                }

                settFelt(forekomstType.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt) {
                    (forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning -
                        sumMottattGrunnrenteinntekt) medMinimumsverdi 0
                }

                settFelt(forekomstType.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift) {
                    (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetPositivGrunnrenteinntekt *
                        satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
                }


                settFelt(forekomstType.beregnetGrunnrenteskatt_positivGrunnrenteskatt) {
                    (forekomstType.beregnetGrunnrenteskatt_beregnetGrunnrenteskattFoerProduksjonsavgift -
                        sumEgenProduksjonsavgift(organisasjonsnummer) +
                        sumAvgittProduksjonsavgift(organisasjonsnummer) -
                        sumMottattProduksjonsavgift(organisasjonsnummer)) medMinimumsverdi 0
                }

                var erUttredenEllerSalgMedOpphoerAvVirksomhet = false
                forAlleForekomsterAv(forekomstType.overdragelseAvHavbruksvirksomhet) {
                    hvis(
                        forekomstType.overdragelsestype lik overdragelsestype.kode_uttreden ||
                            (forekomstType.overdragelsestype lik overdragelsestype.kode_salgVedOpphoerAvVirksomhet &&
                                forekomstType.overdragelsenErGjennomfoertMedKontinuitet.erSann())
                    ) {
                        erUttredenEllerSalgMedOpphoerAvVirksomhet = true
                    }
                }

                if (erUttredenEllerSalgMedOpphoerAvVirksomhet) {
                    settFelt(forekomstType.beregnetGrunnrenteskatt_negativGrunnrenteskattVedRealisasjonEllerOpphoerAvGrunnrenteskattepliktigHavbruksvirksomhet) {
                        (forekomstType.beregnetGrunnrenteskatt_endeligSamordnetNegativGrunnrenteinntekt *
                            satser.sats(Sats.havbruk_skattesatsGrunnrenteinntekt)).somHeltall()
                    }
                }
            }
        }
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumMottattProduksjonsavgift(organisasjonsnummer: String?): BigDecimal? {
        val sumMottattProduksjonsavgift =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
                forekomstType.mottattProduksjonsavgift.tall()
            }
        return sumMottattProduksjonsavgift
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumAvgittProduksjonsavgift(organisasjonsnummer: String?): BigDecimal? {
        val sumAvgittProduksjonsavgift =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
                forekomstType.avgittProduksjonsavgift.tall()
            }
        return sumAvgittProduksjonsavgift
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumEgenProduksjonsavgift(organisasjonsnummer: String?): BigDecimal? {
        val sumEgenProduksjonsavgift =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
                forekomstType.egenProduksjonsavgift.tall()
            }
        return sumEgenProduksjonsavgift
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumBunnfradrag(organisasjonsnummer: String?): BigDecimal? {
        val sumBunnfradrag =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
                forekomstType.bunnfradrag.tall()
            }
        return sumBunnfradrag
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumMottattGrunnrenteinntekt(organisasjonsnummer: String?): BigDecimal? {
        val sumMottattGrunnrenteinntekt =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
                forekomstType.mottattGrunnrenteinntekt.tall()
            }
        return sumMottattGrunnrenteinntekt
    }

    private fun ForekomstKontekst<havbruksvirksomhetForekomst>.sumAvgittGrunnrenteinntekt(organisasjonsnummer: String?): BigDecimal? {
        val sumAvgittGrunnrenteinntekt =
            forekomsterAv(forekomstType.beregnetGrunnrenteskatt_forholdSomPaavirkerGrunnrenteskattKnyttetTilSelskapISammeKonsern) der {
                forekomstType.organisasjonsnummer.harVerdi() && forekomstType.organisasjonsnummer.lik(
                    organisasjonsnummer
                )
            } summerVerdiFraHverForekomst {
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
