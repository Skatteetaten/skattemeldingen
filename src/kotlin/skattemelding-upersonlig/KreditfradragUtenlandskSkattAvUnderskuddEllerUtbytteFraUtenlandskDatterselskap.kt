package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle

object KreditfradragUtenlandskSkattAvUnderskuddEllerUtbytteFraUtenlandskDatterselskap : HarKalkylesamling {

    private val fradragsberettigetAndelAvUnderliggendeSelskapsskattTilFremfoering =
        kalkyle ("fradragsberettigetAndelAvUnderliggendeSelskapsskattTilFremfoering") {
            settUniktFelt(modell.fradragINorskSkattVedSkattleggingAvUtbytteMvFraUtenlandskDatterselskap.fradragsberettigetAndelAvUnderliggendeSelskapsskattTilFremfoering) {
                modell.fradragINorskSkattVedSkattleggingAvUtbytteMvFraUtenlandskDatterselskap.fremfoertUbenyttetUnderliggendeSelskapsskattFom1995 +
                    modell.fradragINorskSkattVedSkattleggingAvUtbytteMvFraUtenlandskDatterselskap.fradragsberettigetAndelAvUnderliggendeSelskapsskattIInntektsaaret -
                    modell.fradragINorskSkattVedSkattleggingAvUtbytteMvFraUtenlandskDatterselskap.anvendtFradragsberettigetAndelAvUnderliggendeSelskapsskattIInntektsaaret
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            fradragsberettigetAndelAvUnderliggendeSelskapsskattTilFremfoering
        )
    }
}