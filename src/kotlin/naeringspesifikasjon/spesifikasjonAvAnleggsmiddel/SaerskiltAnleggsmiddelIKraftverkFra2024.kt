package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object SaerskiltAnleggsmiddelIKraftverkFra2024 : HarKalkylesamling {

    internal val aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk =
        kalkyle("aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk) {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.aaretsAvskrivning.tall()
                    }
                }
            }
        }

    internal val utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk =
        kalkyle("utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk) {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                        forekomstType.utgaaendeVerdi.tall()
                    }
                }
            }
        }

    internal val aaretsSamledeFriinntekt = kalkyle("aaretsSamledeFriinntekt") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            settFelt(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.aaretsSamledeFriinntekt) {
                forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.aaretsFriinntekt.tall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.aaretsAvskrivningKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.utgaaendeVerdiKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdiKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.gjenstaaendeLevetidKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.naaverdiAvFremtidigeUtskiftningskostnaderKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.aaretsFriinntektKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktetKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.konsumprisindeksjustertInvesteringskostnadForKorrigeringAvKommunefordelingAvEiendomsskattegrunnlagKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.gevinstOgTapVedRealisasjonKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.grunnlagForAaretsInntektsfoeringOgInntektsfradragKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.inntektEllerInntektsfradragFraGevinstOgTapskontoKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.utgaaendeVerdiKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente.gevinstOgTapVedRealisasjonKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente.grunnlagForAaretsInntektsfoeringOgInntektsfradragKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente.inntektEllerInntektsfradragFraGevinstOgTapskontoKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelKnyttetTilGrunnrente.utgaaendeVerdiKalkyle,
            aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk,
            utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk,
            aaretsSamledeFriinntekt
        )
    }
}