package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skogfond_skattefriAndelAvSkogfond
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.medBegrensninger
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

internal object SkogOgToemmerkonto2022 : HarKalkylesamling {

    private val skattefordelAvSkogfondKalkyle = kalkyle("skattefordelAvSkogfond") {
        val satser = satser!!
        forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto.skogfond) {
            settFelt(forekomstType.skattefordelAvSkogfond) {
                (forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkonto
                    ).times(satser.sats(skogfond_skattefriAndelAvSkogfond)) medMinimumsverdi 0
            }
        }
    }

    private val tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle =
        kalkyle("tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto") {
            val satser = satser!!
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto.skogfond) {
                settFelt(forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto) {
                    forekomstType.innbetaltOffentligTilskuddTilSkogfondkontoTilInvesteringForetattTidligereAar.times(
                        satser.sats(skogfond_skattefriAndelAvSkogfond)
                    )
                }
            }
        }

    private val skattepliktigInntektAvSkogfondKalkyle =
        kalkyle("skattepliktigInntektAvSkogfond") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto.skogfond) {
                settFelt(forekomstType.skattepliktigInntektAvSkogfond) {
                    forekomstType.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                        forekomstType.skattefordelAvSkogfond +
                        forekomstType.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto +
                        forekomstType.utbetaltFraSkogfondkontoTilFormaalUtenSkattefordel
                }
            }
        }

    private val utgaaendeVerdiAvToemmeravvirkningKalkyle =
        kalkyle("utgaaendeVerdiAvToemmeravvirkning") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_utgaaendeVerdiAvToemmeravvirkning) {
                    forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inngaaendeVerdiAvToemmeravvirkning -
                        forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inntektsfoertBeloepAvToemmeravvirkning
                }
            }
        }

    private val driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle =
        kalkyle("driftsresultatSomKanOverfoeresTilToemmerkonto") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
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
                        forekomstType.inntektOgKostnad_andelAvDriftskostnaderTilJaktFiskeMv +
                        forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inntektsfoertBeloepAvToemmeravvirkning -
                        forekomstType.inntektOgKostnad_sjablongberegnetFradragKnyttetTilBiomasseOgVedproduksjon +
                        forekomstType.inntektOgKostnad_tilleggForPositivMidlertidigForskjell -
                        forekomstType.inntektOgKostnad_reduksjonForNegativMidlertidigForskjell
                }

            }
        }

    private val driftsresultatFraJaktFiskeOgLeieinntektKalkyle =
        kalkyle("driftsresultatFraJaktFiskeOgLeieinntekt") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
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
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret) {
                    forekomstType.inngaaendeVerdiPaaToemmerkonto +
                        forekomstType.andelAvDriftsresultatOverfoertTilToemmerkonto +
                        forekomstType.inngaaendeVerdiFraErvervetToemmerkonto -
                        forekomstType.verdiPaaAndelAvRealisertToemmerkonto
                }
            }
        }

    val inntektEllerInntektsfradragFraToemmerkontoKalkyle =
        kalkyle("inntektEllerInntektsfradragFraToemmerkonto") {
            val satser = satser!!
            val grenseverdiEn = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiEn).toInt()
            val grenseverdiTo = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiTo).toInt()
            val grenseverdiTre = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiTre).toInt()
            val satsIntervallTilOgMedGrenseverdiEn = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallTilOgMedGrenseverdiEn)
            val satsIntervallFraGrenseverdiEnTilTo = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraGrenseverdiEnTilTo)
            val satsIntervallFraOgMedGrenseverdiTre = satser
                .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraOgMedGrenseverdiTre)

            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
                val beloep = forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret.tall()
                val sats = forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()

                if (beloep stoerreEllerLik grenseverdiTre) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraOgMedGrenseverdiTre.first,
                        min = satsIntervallFraOgMedGrenseverdiTre.first
                    )
                    settFelt(forekomstType.inntektFraToemmerkonto) { beloep * justertSats }
                } else if (beloep stoerreEllerLik grenseverdiTo && beloep mindreEnn grenseverdiTre) {
                    settFelt(forekomstType.inntektFraToemmerkonto) { beloep }
                } else if (beloep stoerreEnn grenseverdiEn && beloep mindreEnn grenseverdiTo) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.second
                    )
                    settFelt(forekomstType.inntektsfradragFraToemmerkonto) { (beloep * justertSats).absoluttverdi() }
                } else if (beloep mindreEllerLik grenseverdiEn) {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallTilOgMedGrenseverdiEn.second,
                        maks = satsIntervallTilOgMedGrenseverdiEn.second
                    )
                    settFelt(forekomstType.inntektsfradragFraToemmerkonto) {
                        (beloep * justertSats).absoluttverdi()
                    }
                }
            }
        }

    private val utgaaendeVerdiPaaToemmerkontoKalkyle =
        kalkyle("utgaaendeVerdiPaaToemmerkonto") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.utgaaendeVerdiPaaToemmerkonto) {
                    forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret -
                        forekomstType.inntektFraToemmerkonto +
                        forekomstType.inntektsfradragFraToemmerkonto
                }
            }
        }

    private val settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle =
        kalkyle("settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret") {
            forAlleForekomsterAv(modell2022.skogbruk_skogOgToemmerkonto) {
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
            utgaaendeVerdiAvToemmeravvirkningKalkyle,
            driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle,
            driftsresultatFraJaktFiskeOgLeieinntektKalkyle,
            grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle,
            inntektEllerInntektsfradragFraToemmerkontoKalkyle,
            utgaaendeVerdiPaaToemmerkontoKalkyle,
            settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle
        )
    }
}
