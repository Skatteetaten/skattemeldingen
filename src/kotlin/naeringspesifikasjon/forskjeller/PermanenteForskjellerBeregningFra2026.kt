package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.virksomhetsTypeLivsforsikringsforetakOgPensjonskasse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.PermanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Fra 2026 er permanentForskjell splittet i inntektsoekendePermanentForskjell (tillegg) og
 * inntektsreduserendePermanentForskjell (fradrag).
 * For 2023-2025 se [PermanenteForskjellerBeregningFra2023].
 */
internal object PermanenteForskjellerBeregningFra2026 : HarKalkylesamling {

    internal val sumTilleggOgFradragINaeringsinntektKalkyle = kalkyle("sumTilleggOgFradragINaeringsinntekt") {
        hvis(fullRegnskapsplikt()) {
            val inntektsaar = inntektsaar
            val erLivsforsikringOgPensjonskasse = virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()

            val sumTillegg = forekomsterAv(modell.inntektsoekendePermanentForskjell) der {
                val forskjellstype = modell.inntektsoekendePermanentForskjell.permanentForskjellstype.verdi()
                PermanentForskjellstype.erTillegg(inntektsaar, forskjellstype) &&
                    (!erLivsforsikringOgPensjonskasse || !PermanentForskjellstype.gjelderIkkeTekniskRegnskap(inntektsaar, forskjellstype))
            } summerVerdiFraHverForekomst {
                modell.inntektsoekendePermanentForskjell.beloep.tall()
            } medAntallDesimaler 2

            settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntekt) { sumTillegg }

            val sumFradrag = forekomsterAv(modell.inntektsreduserendePermanentForskjell) der {
                val forskjellstype = modell.inntektsreduserendePermanentForskjell.permanentForskjellstype.verdi()
                PermanentForskjellstype.erFradrag(inntektsaar, forskjellstype) &&
                    (!erLivsforsikringOgPensjonskasse || !PermanentForskjellstype.gjelderIkkeTekniskRegnskap(inntektsaar, forskjellstype))
            } summerVerdiFraHverForekomst {
                modell.inntektsreduserendePermanentForskjell.beloep.tall()
            } medAntallDesimaler 2

            settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntekt) { sumFradrag }
        }
    }

    internal val sumTilleggOgFradragKnyttetTilIkkeTekniskRegnskapKalkyle =
        kalkyle("sumTilleggOgFradragINaeringsinntektKnyttetTilIkkeTekniskRegnskapForLivsforsikringsforetak") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                val inntektsaar = inntektsaar

                val sumTilleggIkkeTekniskRegnskap = forekomsterAv(modell.inntektsoekendePermanentForskjell) der {
                    val forskjellstype = modell.inntektsoekendePermanentForskjell.permanentForskjellstype.verdi()
                    PermanentForskjellstype.erTillegg(inntektsaar, forskjellstype) &&
                        PermanentForskjellstype.gjelderIkkeTekniskRegnskap(inntektsaar, forskjellstype)
                } summerVerdiFraHverForekomst {
                    modell.inntektsoekendePermanentForskjell.beloep.tall()
                } medAntallDesimaler 2

                settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumTilleggINaeringsinntektKnyttetTilIkkeTekniskRegnskapForLivsforsikringsforetak) {
                    sumTilleggIkkeTekniskRegnskap
                }

                val sumFradragIkkeTekniskRegnskap = forekomsterAv(modell.inntektsreduserendePermanentForskjell) der {
                    val forskjellstype = modell.inntektsreduserendePermanentForskjell.permanentForskjellstype.verdi()
                    PermanentForskjellstype.erFradrag(inntektsaar, forskjellstype) &&
                        PermanentForskjellstype.gjelderIkkeTekniskRegnskap(inntektsaar, forskjellstype)
                } summerVerdiFraHverForekomst {
                    modell.inntektsreduserendePermanentForskjell.beloep.tall()
                } medAntallDesimaler 2

                settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_sumFradragINaeringsinntektKnyttetTilIkkeTekniskRegnskapForLivsforsikringsforetak) {
                    sumFradragIkkeTekniskRegnskap
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            sumTilleggOgFradragINaeringsinntektKalkyle,
            sumTilleggOgFradragKnyttetTilIkkeTekniskRegnskapKalkyle,
        )
    }
}
