package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object IkkeAvskrivbartAnleggsmiddel : HarKalkylesamling {

    internal val utgaaendeVerdi = kalkyle("utgaaendeVerdi") {
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) {
            val utgaaendeVerdiPositivOgNegativ = forekomstType.inngaaendeVerdi +
                forekomstType.nyanskaffelse +
                forekomstType.paakostning -
                forekomstType.offentligTilskudd +
                forekomstType.justeringAvInngaaendeMva +
                forekomstType.justeringForAapenbarVerdiendring -
                forekomstType.vederlagVedRealisasjonOgUttak -
                forekomstType.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                forekomstType.gevinstOverfoertTilGevinstOgTapskonto -
                forekomstType.tapOverfoertTilGevinstOgTapskonto -
                forekomstType.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst +
                forekomstType.reinvestertBetingetSkattefriSalgsgevinst

            hvis(utgaaendeVerdiPositivOgNegativ stoerreEllerLik 0) {
                settFelt(forekomstType.utgaaendeVerdi) {
                    utgaaendeVerdiPositivOgNegativ
                }
            }
        }
    }

    val aaretsFriinntektKalkyle = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar >= 2025) {
            val satser = satser!!
            val inntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
            val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) der {
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                    forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
            } forHverForekomst {
                val kraftverk =
                    kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]
                if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                    val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                    hvis(
                        kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                    ) {
                        settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                            (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                        }
                    }

                    hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                        val dagerEid = dagerEidIAnskaffelsesaaret(kraftverk.datoForOverdragelseVedErvervIInntektsaaret)
                        settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                            (forekomstType.nyanskaffelse + forekomstType.utgaaendeVerdi) / 2 * normRente * (dagerEid / antallDagerIAar(
                                inntektsaar!!.toInt()
                            ))
                        }
                    }

                    hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                        val dagerEid =
                            dagerEidIRealisasjonsaaret(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret)
                        settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                            forekomstType.inngaaendeVerdi / 2 * normRente * (dagerEid / antallDagerIAar(inntektsaar!!.toInt()))
                        }
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            utgaaendeVerdi,
            aaretsFriinntektKalkyle
        )
    }
}