package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

internal object GevinstOgTapskonto2021 : HarKalkylesamling {
    internal val grunnlagForAaretsInntektsOgFradragsfoeringKalkyle =
        kalkyle("grunnlagForAaretsInntektsOgFradragsfoeringKalkyle") {
            forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
                settFelt(forekomstType.grunnlagForAaretsInntektsOgFradragsfoering) {
                    forekomstType.inngaaendeVerdi +
                        forekomstType.sumGevinstVedRealisasjonOgUttak -
                        forekomstType.sumTapVedRealisasjonOgUttak +
                        forekomstType.gevinstVedRealisasjonAvHelBuskapPaaGaardsbrukVedOpphoerAvDriftsgren +
                        forekomstType.inngaaendeVerdiFraErvervetGevinstOgTapskonto -
                        forekomstType.verdiAvRealisertGevinstOgTapskonto
                }
            }
        }

    private val inntektFraGevinstOgTapskontoKalkyle = kalkyle("inntektFraGevinstOgTapskonto") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
            val beloep = forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.tall()
            if (beloep stoerreEnn 15000) {
                settFelt(forekomstType.inntektFraGevinstOgTapskonto) {
                    beloep * forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()
                }
            } else if (beloep stoerreEllerLik 0 && beloep mindreEllerLik 15000) {
                settFelt(forekomstType.inntektFraGevinstOgTapskonto) { beloep }
            } else if (beloep stoerreEllerLik -15000 && beloep mindreEnn 0) {
                settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) { beloep.absoluttverdi() }
            } else if (beloep mindreEnn -15000) {
                settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) {
                    beloep.absoluttverdi() * forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()
                }
            }
        }
    }

    private val utgaaendeVerdiKalkyle = kalkyle("utgaaendeVerdi") {
        forAlleForekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
            hvis(forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.harVerdi()) {
                if (forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.erPositiv()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAaretsInntektsOgFradragsfoering - forekomstType.inntektFraGevinstOgTapskonto
                    }
                } else if (forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.erNegativ()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAaretsInntektsOgFradragsfoering + forekomstType.inntektsfradragFraGevinstOgTapskonto
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            grunnlagForAaretsInntektsOgFradragsfoeringKalkyle,
            inntektFraGevinstOgTapskontoKalkyle,
            utgaaendeVerdiKalkyle
        )
    }
}