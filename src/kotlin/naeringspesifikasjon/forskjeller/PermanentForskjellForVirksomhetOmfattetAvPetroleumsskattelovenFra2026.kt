package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.PermanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Fra 2026 er permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven splittet i
 * inntektsoekendePermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven (tillegg) og
 * inntektsreduserendePermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven (fradrag). Kun feltnavnene er endret,
 * beregningssemantikken er den samme som for 2023-2025 (se PermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven).
 */
internal object PermanentForskjellForVirksomhetOmfattetAvPetroleumsskattelovenFra2026 : HarKalkylesamling {

    internal val petroleumFordelt = modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven
    internal val inntektsoekendePermanentForskjellPetroleum = modell.inntektsoekendePermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven
    internal val inntektsreduserendePermanentForskjellPetroleum = modell.inntektsreduserendePermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven

    internal val fordeltTilleggINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltTilleggINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsoekendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, inntektsoekendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltFradragINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsreduserendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, inntektsreduserendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }


    internal val fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsoekendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, inntektsoekendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsreduserendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, inntektsreduserendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsoekendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, inntektsoekendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaLand.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsreduserendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, inntektsreduserendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaLand.tall()
                }
            }
        }

    internal val fordeltTilleggINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltTilleggINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsoekendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, inntektsoekendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltFradragINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                val inntektsaar = inntektsaar
                forekomsterAv(inntektsreduserendePermanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, inntektsreduserendePermanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv.tall()
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            fordeltTilleggINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
            fordeltFradragINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
            fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel,
            fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel,
            fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand,
            fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand,
            fordeltTilleggINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv,
            fordeltFradragINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv
        )
    }
}