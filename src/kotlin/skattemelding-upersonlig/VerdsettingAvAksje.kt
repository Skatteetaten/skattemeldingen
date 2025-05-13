package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV2
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst

object VerdsettingAvAksje : HarKalkylesamling {

    internal val samletVerdiBakAksjeneISelskapet = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erSann()) {
            hvis(inntektsaar.tekniskInntektsaar >= 2023) {
                hvis(
                    modell.opplysningOmSkattesubjekt.erBoersnotert.harIkkeVerdi() ||
                        modell.opplysningOmSkattesubjekt.erBoersnotert.erUsann()
                ) {
                    val verdiMinusGjeld = modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt -
                        modell.formueOgGjeld.samletGjeld

                    val verdiMinusGjeldSvalbard =
                        modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt -
                            modell.formueOgGjeldSvalbard.samletGjeld

                    val samletVerdi = verdiMinusGjeld + verdiMinusGjeldSvalbard

                    if (samletVerdi.harVerdi() && inntektsaar.tekniskInntektsaar >= 2024 && modell.forhaandsfastsetting_innsendingsformat.harVerdi()) {
                        settUniktFelt(modell.verdsettingAvAksje.samletVerdiBakAksjeneISelskapet) { BigDecimal.ZERO }
                    } else {
                        settUniktFelt(modell.verdsettingAvAksje.samletVerdiBakAksjeneISelskapet) {
                            samletVerdi.medMinimumsverdi(0).somHeltall()
                        }
                    }
                }
            }
            hvis(inntektsaar.tekniskInntektsaar < 2023) {
                hvis(
                    modellV2.opplysningerOmSkattesubjekt.erBoersnotert.harIkkeVerdi() ||
                        modellV2.opplysningerOmSkattesubjekt.erBoersnotert.erUsann()
                ) {
                    val verdiMinusGjeld = modellV2.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt -
                        modellV2.formueOgGjeld.samletGjeld

                    val verdiMinusGjeldSvalbard =
                        modellV2.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt -
                            modellV2.formueOgGjeldSvalbard.samletGjeld

                    val samletVerdi = verdiMinusGjeld + verdiMinusGjeldSvalbard

                    settUniktFelt(modellV2.verdsettingAvAksje.samletVerdiBakAksjeneISelskapet) {
                        samletVerdi.medMinimumsverdi(0).somHeltall()
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletVerdiBakAksjeneISelskapet
        )
    }
}