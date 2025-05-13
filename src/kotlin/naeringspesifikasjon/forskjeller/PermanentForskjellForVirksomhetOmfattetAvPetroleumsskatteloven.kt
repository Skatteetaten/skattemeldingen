package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.PermanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object PermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    internal val petroleumFordelt = modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven
    internal val permanentForskjellPetroleum = modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven

    internal val fordeltTilleggINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltTilleggINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltFradragINaeringsinntektBeloepSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }


    internal val fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaSokkel") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel.tall()
                }
            }
        }

    internal val fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltTilleggINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaLand.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltFradragINaeringsinntektBeloepAlminneligInntektFraVirksomhetPaaLand") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepAlminneligInntektFraVirksomhetPaaLand.tall()
                }
            }
        }

    internal val fordeltTilleggINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltTilleggINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv") {
            settUniktFelt(petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erTillegg(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv.tall()
                }
            }
        }

    internal val fordeltFradragINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltFradragINaeringsinntektBeloepResultatAvFinansinntektOgFinanskostnadMv") {
            settUniktFelt(petroleumFordelt.fordeltFradragINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                val inntektsaar = inntektsaar
                forekomsterAv(permanentForskjellPetroleum) der {
                    PermanentForskjellstype.erFradrag(inntektsaar, permanentForskjellPetroleum.permanentForskjellstype.verdi())
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