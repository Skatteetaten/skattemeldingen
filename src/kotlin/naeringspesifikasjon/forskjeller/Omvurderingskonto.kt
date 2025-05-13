package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023

internal object Omvurderingskonto : HarKalkylesamling {


    internal val alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta = kalkyle {
        hvis(erPetroleumsforetak()) {
            val urealisertForrigeInntektsaar =
                modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutagevinstForrigeInntektsaar -
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutatapForrigeInntektsaar
            if (urealisertForrigeInntektsaar.stoerreEllerLik(0)) {
                settUniktFelt(
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinstForrigeInntektsaar
                ) {
                    urealisertForrigeInntektsaar.absoluttverdi()
                }
            } else {
                settUniktFelt(
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatapForrigeInntektsaar
                ) {
                    urealisertForrigeInntektsaar.absoluttverdi()
                }
            }

            val urealisert =
                modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutagevinst -
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutatap
            if (urealisert.stoerreEllerLik(0)) {
                settUniktFelt(
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinst
                ) {
                    urealisert.absoluttverdi()
                }
            } else {
                settUniktFelt(
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatap
                ) {
                    urealisert.absoluttverdi()
                }
            }

            settUniktFelt(modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskontoForrigeInntektsaar) {
                modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinstForrigeInntektsaar -
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatapForrigeInntektsaar -
                    modell2023.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattInntektsfoeringAvValutagevinstForrigeInntektsaar +
                    modell2023.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattFradragsfoeringAvValutatapForrigeInntektsaar +
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.korrigertOmvurderingskontoForrigeInntektsaar
            }

            settUniktFelt(modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskonto) {
                modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinst -
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatap -
                    modell2023.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattInntektsfoeringAvValutagevinst +
                    modell2023.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattFradragsfoeringAvValutatap +
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.korrigertOmvurderingskonto
            }

            settUniktFelt(modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.endringIOmvurderingskonto) {
                modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskontoForrigeInntektsaar -
                    modell.alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskonto
            }
        }
    }

    internal val separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta = kalkyle {
        hvis(erPetroleumsforetak()) {
            val urealisertForrigeInntektsaar =
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutagevinstForrigeInntektsaar -
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutatapForrigeInntektsaar
            if (urealisertForrigeInntektsaar.stoerreEllerLik(0)) {
                settUniktFelt(
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinstForrigeInntektsaar
                ) {
                    urealisertForrigeInntektsaar.absoluttverdi()
                }
            } else {
                settUniktFelt(
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatapForrigeInntektsaar
                ) {
                    urealisertForrigeInntektsaar.absoluttverdi()
                }
            }

            val urealisert =
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutagevinst -
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.urealisertValutatap
            if (urealisert.stoerreEllerLik(0)) {
                settUniktFelt(
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinst
                ) {
                    urealisert.absoluttverdi()
                }
            } else {
                settUniktFelt(
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatap
                ) {
                    urealisert.absoluttverdi()
                }
            }

            settUniktFelt(modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskontoForrigeInntektsaar) {
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinstForrigeInntektsaar -
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatapForrigeInntektsaar -
                modell2023.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattInntektsfoeringAvValutagevinstForrigeInntektsaar +
                modell2023.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattFradragsfoeringAvValutatapForrigeInntektsaar +
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.korrigertOmvurderingskontoForrigeInntektsaar
            }

            settUniktFelt(modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskonto) {
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutagevinst -
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.nettoUrealisertValutatap -
                    modell2023.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattInntektsfoeringAvValutagevinst +
                    modell2023.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.utsattFradragsfoeringAvValutatap +
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.korrigertOmvurderingskonto
            }

            settUniktFelt(modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.endringIOmvurderingskonto) {
                modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskontoForrigeInntektsaar -
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.resultatOmvurderingskonto
            }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        alminneligOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta,
        separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}