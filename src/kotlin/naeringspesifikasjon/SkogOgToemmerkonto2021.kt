package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.timesNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object SkogOgToemmerkonto2021 : HarKalkylesamling {

    private val skattefordelAvSkogfondKalkyle = kalkyle("skattefordelAvSkogfond") {
        forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
            settFelt(forekomstType.skogfond_skattefordelAvSkogfond) {
                ((forekomstType.skogfond_utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                    forekomstType.skogfond_innbetaltOffentligTilskuddTilSkogfondkonto
                    ) * 0.85) medMinimumsverdi 0
            }
        }
    }

    private val tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle =
        kalkyle("tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.skogfond_tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto) {
                    forekomstType.skogfond_innbetaltOffentligTilskuddTilSkogfondkontoTilInvesteringForetattTidligereAar * 0.85
                }
            }
        }

    private val skattepliktigInntektAvSkogfondKalkyle =
        kalkyle("skattepliktigInntektAvSkogfond") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.skogfond_skattepliktigInntektAvSkogfond) {
                    forekomstType.skogfond_utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
                        forekomstType.skogfond_skattefordelAvSkogfond +
                        forekomstType.skogfond_tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto +
                        forekomstType.skogfond_utbetaltFraSkogfondkontoTilFormaalUtenSkattefordel
                }
            }
        }

    private val utgaaendeVerdiAvToemmeravvirkningKalkyle =
        kalkyle("utgaaendeVerdiAvToemmeravvirkning") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_utgaaendeVerdiAvToemmeravvirkning) {
                    forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inngaaendeVerdiAvToemmeravvirkning -
                        forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inntektsfoertBeloepAvToemmeravvirkning
                }
            }
        }

    private val driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle =
        kalkyle("driftsresultatSomKanOverfoeresTilToemmerkonto") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.driftsresultatSomKanOverfoeresTilToemmerkonto) {
                    forekomstType.inntektOgKostnad_salgsinntektOgUttakFraSkurtoemmerMassevirkeRotsalgMv +
                        forekomstType.inntektOgKostnad_salgsinntektOgUttakAvVedOgBiomasse +
                        forekomstType.inntektOgKostnad_andreDriftsinntekter +
                        forekomstType.skogfond_skattepliktigInntektAvSkogfond -
                        forekomstType.inntektOgKostnad_driftskostnader +
                        forekomstType.inntektOgKostnad_andelAvDriftskostnaderTilJaktFiskeMv +
                        forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inntektsfoertBeloepAvToemmeravvirkning -
                        forekomstType.inntektOgKostnad_sjablongberegnetFradragKnyttetTilBiomasseOgVedproduksjon
                }
            }
        }

    private val driftsresultatFraJaktFiskeOgLeieinntektKalkyle =
        kalkyle("driftsresultatFraJaktFiskeOgLeieinntekt") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
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
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret) {
                    forekomstType.inngaaendeVerdiPaaToemmerkonto +
                        forekomstType.andelAvDriftsresultatOverfoertTilToemmerkonto +
                        forekomstType.inngaaendeVerdiFraErvervetToemmerkonto -
                        forekomstType.verdiPaaAndelAvRealisertToemmerkonto
                }
            }
        }

    private val inntektEllerInntektsfradragFraToemmerkontoKalkyle =
        kalkyle("inntektEllerInntektsfradragFraToemmerkonto") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                val inntektEllerInntektsfradragFraToemmerkonto =
                    forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret.tall().timesNullsafe(
                        forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()
                    ).somHeltall()
                hvis(inntektEllerInntektsfradragFraToemmerkonto.erPositiv()) {
                    settFelt(forekomstType.inntektFraToemmerkonto) { inntektEllerInntektsfradragFraToemmerkonto }
                }
                hvis(inntektEllerInntektsfradragFraToemmerkonto.erNegativ()) {
                    settFelt(forekomstType.inntektsfradragFraToemmerkonto) { inntektEllerInntektsfradragFraToemmerkonto.absoluttverdi() }
                }
            }
        }

    private val utgaaendeVerdiPaaToemmerkontoKalkyle =
        kalkyle("utgaaendeVerdiPaaToemmerkonto") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.utgaaendeVerdiPaaToemmerkonto) {
                    forekomstType.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret -
                        forekomstType.inntektFraToemmerkonto +
                        forekomstType.inntektsfradragFraToemmerkonto
                }
            }
        }

    private val settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle =
        kalkyle("settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret") {
            forAlleForekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) {
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