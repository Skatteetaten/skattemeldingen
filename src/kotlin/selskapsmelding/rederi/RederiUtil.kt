package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.beskatningsordningForSkipsaksjeselskapSkipsalmennaksjeselskapMv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object RederiUtil {
    val beskatningsordning =
        modell.rederiskatteordning_spesifikasjonAvSkattleggingEtterSaerskiltBeskatningsordning.beskatningsordning
    val BEHANDLES_ETTER_SAERSKILTE_REDERIBESKATNINGSREGLER_I_INNTEKTSAARET =
        beskatningsordningForSkipsaksjeselskapSkipsalmennaksjeselskapMv.kode_behandlesEtterSaerskilteRederibeskatningsreglerIInntektsaaret.kode
    val BEHANDLES_IKKE_ETTER_SAERSKILTE_REDERIBESKATNINGSREGLER_I_INNTEKTSAARET =
        beskatningsordningForSkipsaksjeselskapSkipsalmennaksjeselskapMv.kode_behandlesIkkeEtterSaerskilteRederibeskatningsreglerIInntektsaaret.kode
    val SELSKAP_HAR_DELTAKER_SOM_ER_INNENFOR_REDERI_BESKATNINGSORDNINGEN =
        beskatningsordningForSkipsaksjeselskapSkipsalmennaksjeselskapMv.kode_selskapHarDeltakerSomErInnenforRederibeskatningsordningen.kode

    fun skalBeregneRederi(beskatningsordning: String?): Boolean {
        return if (beskatningsordning.isNullOrBlank() || beskatningsordning == BEHANDLES_IKKE_ETTER_SAERSKILTE_REDERIBESKATNINGSREGLER_I_INNTEKTSAARET) {
            false
        } else if (
            beskatningsordning == BEHANDLES_ETTER_SAERSKILTE_REDERIBESKATNINGSREGLER_I_INNTEKTSAARET ||
            beskatningsordning == SELSKAP_HAR_DELTAKER_SOM_ER_INNENFOR_REDERI_BESKATNINGSORDNINGEN
        ) {
            true
        } else {
            false
        }
    }
}