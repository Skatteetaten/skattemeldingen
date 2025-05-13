package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023

internal object SkogOgToemmerkonto2023 : HarKalkylesamling {

    private val utgaaendeVerdiAvToemmeravvirkningKalkyle =
        kalkyle("utgaaendeVerdiAvToemmeravvirkning") {
            forAlleForekomsterAv(modell2023.skogbruk_skogOgToemmerkonto) {
                settFelt(forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_utgaaendeVerdiAvToemmeravvirkning) {
                    forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inngaaendeVerdiAvToemmeravvirkning -
                        forekomstType.ekstraordinaerFordelingAvToemmeravvirkningEtterNaturkatastrofe_inntektsfoertBeloepAvToemmeravvirkning
                }
            }
        }

    private val driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle =
        kalkyle("driftsresultatSomKanOverfoeresTilToemmerkonto") {
            forAlleForekomsterAv(modell2023.skogbruk_skogOgToemmerkonto) {
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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            SkogOgToemmerkontoFra2024.skattefordelAvSkogfondKalkyle,
            SkogOgToemmerkontoFra2024.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkontoKalkyle,
            SkogOgToemmerkontoFra2024.skattepliktigInntektAvSkogfondKalkyle,
            utgaaendeVerdiAvToemmeravvirkningKalkyle,
            driftsresultatSomKanOverfoeresTilToemmerkontoKalkyle,
            SkogOgToemmerkontoFra2024.driftsresultatFraJaktFiskeOgLeieinntektKalkyle,
            SkogOgToemmerkontoFra2024.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle,
            SkogOgToemmerkontoFra2024.inntektEllerInntektsfradragFraToemmerkontoKalkyle,
            SkogOgToemmerkontoFra2024.utgaaendeVerdiPaaToemmerkontoKalkyle,
            SkogOgToemmerkontoFra2024.settTomtGrunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaretKalkyle
        )
    }
}
