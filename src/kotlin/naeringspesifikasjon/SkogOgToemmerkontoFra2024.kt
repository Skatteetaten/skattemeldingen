package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skogfond_skattefriAndelAvSkogfond
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.medBegrensninger
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object SkogOgToemmerkontoFra2024 : HarKalkylesamling {

    internal val skattefordelAvSkogfondKalkyle = kalkyle("skattefordelAvSkogfond") {
        val satser = satser!!
        forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto.skogfond) {
            settFelt(forekomstType.skattefordelAvSkogfond) {
                (forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkonto
                    ).times(satser.sats(skogfond_skattefriAndelAvSkogfond)) medMinimumsverdi 0
            }
        }
    }

    internal val tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle =
        kalkyle("tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto") {
            val satser = satser!!
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto.skogfond) {
                settFelt(forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto) {
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkontoTilInvesteringForetattTidligereAar.times(
                        satser.sats(skogfond_skattefriAndelAvSkogfond)
                    )
                }
            }
        }

    internal val skattepliktigInntektAvSkogfondKalkyle =
        kalkyle("skattepliktigInntektAvSkogfond") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto.skogfond) {
                settFelt(forekomstType.skattepliktigInntektAvSkogfond) {
                    forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                        forekomstType.skattefordelAvSkogfond +
                        forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto +
                        forekomstType.utbetaltFraSkogfondkontoTilFormaalUtenSkattefordel
                }
            }
        }

    private val driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle =
        kalkyle("driftsresultatSomKanOverfoeresTilToemmerkonto") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                val skattepliktigInntektAvAlleSkogfondForDriftsenhet = forekomsterAv(
                    forekomstType.skogfond
                ) summerVerdiFraHverForekomst {
                    forekomstType.skattepliktigInntektAvSkogfond.tall()
                }

                settFelt(forekomstType.driftsresultatSomKanOverfoeresTilToemmerkonto) {
                    forekomstType.inntektOgKostnad_salgsinntektOgUttakFraSkurtoemmerMassevirkeRotsalgMv +
                        forekomstType.inntektOgKostnad_salgsinntektOgUttakAvVedOgBiomasse +
                        forekomstType.inntektOgKostnad_andreDriftsinntekter +
                        skattepliktigInntektAvAlleSkogfondForDriftsenhet -
                        forekomstType.inntektOgKostnad_driftskostnader +
                        forekomstType.inntektOgKostnad_andelAvDriftskostnaderTilJaktFiskeMv -
                        forekomstType.inntektOgKostnad_sjablongberegnetFradragKnyttetTilBiomasseOgVedproduksjon +
                        forekomstType.inntektOgKostnad_tilleggForPositivMidlertidigForskjell -
                        forekomstType.inntektOgKostnad_reduksjonForNegativMidlertidigForskjell
                }

            }
        }

    internal val driftsresultatFraJaktFiskeOgLeieinntektKalkyle =
        kalkyle("driftsresultatFraJaktFiskeOgLeieinntekt") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.inntektOgKostnad_driftsresultatFraJaktFiskeOgLeieinntekt) {
                    forekomstType.inntektOgKostnad_leieinntekterFraJaktFiskeTorvuttakMv +
                        forekomstType.inntektOgKostnad_inntektFraGevinstOgTapskonto -
                        forekomstType.inntektOgKostnad_inntektsfradragFraGevinstOgTapskonto +
                        forekomstType.inntektOgKostnad_inntektsfoeringAvNegativSaldo -
                        forekomstType.inntektOgKostnad_andelAvDriftskostnaderTilJaktFiskeMv
                }
            }
        }

    internal val grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle =
        kalkyle("grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret) {
                    forekomstType.inngaaendeVerdiPaaToemmerkonto +
                        forekomstType.andelAvDriftsresultatOverfoertTilToemmerkonto +
                        forekomstType.inngaaendeVerdiFraErvervetToemmerkonto -
                        forekomstType.verdiPaaAndelAvRealisertToemmerkonto
                }
            }
        }

    internal val inntektEllerInntektsfradragFraToemmerkontoKalkyle =
        kalkyle("inntektEllerInntektsfradragFraToemmerkonto") {
            val satser = satser!!
            val grenseverdiEn = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiEn).toInt()
            val grenseverdiTo = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiTo).toInt()
            val satsIntervallFraGrenseverdiEnTilTo = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraGrenseverdiEnTilTo)
            val satsIntervallTilOgMedGrenseverdiEn = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallTilOgMedGrenseverdiEn)
            val satsIntervallFraOgMedGrenseverdiTre = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraOgMedGrenseverdiTre)

            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                val grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret =
                    forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret.tall()
                val sats = forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()

                if (grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret stoerreEllerLik grenseverdiTo) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraOgMedGrenseverdiTre.first,
                        min = satsIntervallFraOgMedGrenseverdiTre.first
                    )
                    settFelt(forekomstType.inntektFraToemmerkonto) { grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret * justertSats }
                } else if (grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret mindreEnn grenseverdiTo
                    && grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret stoerreEnn  grenseverdiEn
                ) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.first,
                        maks = satsIntervallFraGrenseverdiEnTilTo.second
                    )
                    settFelt(forekomstType.inntektsfradragFraToemmerkonto) { (grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret * justertSats).absoluttverdi() }
                } else if (grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret mindreEllerLik grenseverdiEn) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallTilOgMedGrenseverdiEn.second,
                        maks = satsIntervallTilOgMedGrenseverdiEn.second
                    )
                    settFelt(forekomstType.inntektsfradragFraToemmerkonto) { (grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret * justertSats).absoluttverdi() }
                }
            }
        }

    internal val utgaaendeVerdiPaaToemmerkontoKalkyle =
        kalkyle("utgaaendeVerdiPaaToemmerkonto") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.utgaaendeVerdiPaaToemmerkonto) {
                    forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret -
                        forekomstType.inntektFraToemmerkonto +
                        forekomstType.inntektsfradragFraToemmerkonto
                }
            }
        }

    internal val settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle =
        kalkyle("settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret") {
            forAlleForekomsterAv(modell.skogbruk_skogOgToemmerkonto) {
                hvis(forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret.harIkkeVerdi()) {
                    settFelt(forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret) { BigDecimal.ZERO }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattefordelAvSkogfondKalkyle,
            tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle,
            skattepliktigInntektAvSkogfondKalkyle,
            driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle,
            driftsresultatFraJaktFiskeOgLeieinntektKalkyle,
            grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle,
            inntektEllerInntektsfradragFraToemmerkontoKalkyle,
            utgaaendeVerdiPaaToemmerkontoKalkyle,
            settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle
        )
    }
}
