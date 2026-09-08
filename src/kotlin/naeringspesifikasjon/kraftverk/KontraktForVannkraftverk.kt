package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.kontraktstypeForKraftLevertAvKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Fra og med 2026 flyttes kontrakter for vannkraftverk fra kraftverksnivå (kraftLevertIhtKontrakt, se
 * [SpesifikasjonAvGrunnrenteinntektFra2024]) til selskapsnivå (kontraktForVannkraftverk), etter samme prinsipp
 * som kontraktForLandbasertVindkraft for vindkraft.
 */
internal object KontraktForVannkraftverk : HarKalkylesamling {

    internal val salgsinntektFraFysiskAvtale =
        kalkyle("salgsinntektFraFysiskAvtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) forHverForekomst {
                    settFelt(forekomstType.salgsinntektFraFysiskAvtale) {
                        forekomstType.kontraktspris * forekomstType.volumIKWIInntektsaaret
                    }
                }
            }
        }

    internal val samletSalgsinntektForLeieavtale =
        kalkyle("samletSalgsinntektForLeieavtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraLeieavtale) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    internal val samletSalgsinntektForKjoepekontrakt =
        kalkyle("samletSalgsinntektForKjoepekontrakt") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraKjoepekontrakt) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    internal val samletSalgsinntektForFastprisavtale =
        kalkyle("samletSalgsinntektForFastprisavtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraFastprisavtale) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            salgsinntektFraFysiskAvtale,
            samletSalgsinntektForLeieavtale,
            samletSalgsinntektForKjoepekontrakt,
            samletSalgsinntektForFastprisavtale
        )
    }
}

