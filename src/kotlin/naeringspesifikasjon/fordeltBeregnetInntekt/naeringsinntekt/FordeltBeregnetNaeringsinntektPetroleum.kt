package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.permanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.saldogruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=856685903
 */

internal object FordeltBeregnetNaeringsinntektPetroleum : HarKalkylesamling {

    internal val petroleum = modell.fordeltBeregnetNaeringsinntektForVirksomhetOmfattetAvPetroleumsskatteloven
    internal val petroleumFordelt = modell.forskjellForVirksomhetOmfattetAvPetroleumsskatteloven
    internal val petroleumFordeling = modell.fordelingAvNettoFinanskostnaderPaaloeptPaaRentebaerendeGjeldOmfattetAvPetroleumsskatteloven
    internal val beregnetSelskapsskatt = modell.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig

    internal val varigOgBetydeligAnleggsmiddelMv = kalkyle("varigOgBetydeligAnleggsmiddelMv") {

        hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
            settUniktFelt(
                petroleumFordeling.skattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel_varigOgBetydeligAnleggsmiddelMv
            ) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) der {
                    forekomstType.gjelderVirksomhetPaaSokkel.erSann() &&
                        forekomstType.saldogruppe ulik saldogruppe.kode_b
                } summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdi.tall()
                }
            }
        }
    }

    internal val anleggsmiddelSomRoerledningProduksjonsinnretningMv = kalkyle("anleggsmiddelSomRoerledningProduksjonsinnretningMv") {
        hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
            settUniktFelt(petroleumFordeling.skattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel_anleggsmiddelSomRoerledningProduksjonsinnretningMv) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                    forekomstType.utgaaendeVerdiSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
                }
            }
        }
    }

    internal val sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel =
        kalkyle("sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleumFordeling.sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel) {
                    petroleumFordeling.skattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel_anleggsmiddelSomRoerledningProduksjonsinnretningMv +
                        petroleumFordeling.skattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel_varigOgBetydeligAnleggsmiddelMv +
                        petroleumFordeling.skattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel_anleggsmiddelKnyttetTilEgetForskningOgUtviklingsprosjekt +
                        petroleumFordeling.annetErvervetImmaterieltFormuesobjektEnnErvervetForretningsverdi +
                        petroleumFordeling.annenKorreksjonISkattemessigNedskrevetFormuesverdi
                }
            }

        }

    internal val sumNettoFinanskostnader = kalkyle("sumNettoFinanskostnader") {
        hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
            settUniktFelt(petroleumFordeling.sumNettoFinanskostnader) {
                petroleumFordeling.rentekostnaderPaaRentebaerendeGjeld -
                    petroleumFordeling.realisertValutagevinstPaaKortsiktigRentebaerendeGjeld +
                    petroleumFordeling.realisertValutatapPaaKortsiktigRentebaerendeGjeld -
                    petroleumFordeling.urealisertValutagevinstPaaKortsiktigRentebaerendeGjeld +
                    petroleumFordeling.urealisertValutatapPaaKortsiktigRentebaerendeGjeld -
                    petroleumFordeling.realisertValutagevinstPaaLangsiktigRentebaerendeGjeld +
                    petroleumFordeling.realisertValutatapPaaLangsiktigRentebaerendeGjeld +
                    modell.separatOmvurderingskontoForLangsiktigFordringsOgGjeldspostIFremmedValuta.endringIOmvurderingskonto
            }
        }
    }

    internal val sumNettoFinanskostnaderForVirksomhetPaaSokkel =
        kalkyle("sumNettoFinanskostnaderForVirksomhetPaaSokkel") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                var gjennomsnittligRentebaerendeGjeld =
                    petroleumFordeling.gjennomsnittligRentebaerendeGjeld.tall()
                if (!gjennomsnittligRentebaerendeGjeld.harVerdi() || gjennomsnittligRentebaerendeGjeld.erNegativ() || gjennomsnittligRentebaerendeGjeld lik 0) {
                    gjennomsnittligRentebaerendeGjeld = BigDecimal.ONE
                }
                val mellomBeregning =
                    petroleumFordeling.sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel /
                        gjennomsnittligRentebaerendeGjeld
                settUniktFelt(petroleumFordeling.sumNettoFinanskostnaderForVirksomhetPaaSokkel) {
                    val beregnetVerdi =
                        ((BigDecimal(0.5) * petroleumFordeling.sumNettoFinanskostnader) * mellomBeregning) medAntallDesimaler 2
                    if (petroleumFordeling.sumNettoFinanskostnader.erNegativ()) {
                        beregnetVerdi medMinimumsverdi petroleumFordeling.sumNettoFinanskostnader.tall()
                    } else {
                        beregnetVerdi medMaksimumsverdi petroleumFordeling.sumNettoFinanskostnader.tall()
                    }
                }
            }
        }

    internal val andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel =
        kalkyle("andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleumFordeling.andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel) {
                    if (petroleumFordeling.sumNettoFinanskostnaderForVirksomhetPaaSokkel.erNegativ()) {
                        petroleumFordeling.sumNettoFinanskostnaderForVirksomhetPaaSokkel.tall()
                    } else {
                        (petroleumFordeling.sumNettoFinanskostnaderForVirksomhetPaaSokkel -
                            petroleumFordeling.aaretsSkattemessigeAktiverteRentekostnader) medMinimumsverdi 0
                    }
                }
            }
        }

    internal val fordeltSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("fordeltSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleum.fordeltSkattemessigResultat_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultat_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                        petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel -
                        petroleumFordelt.fordeltFradragINaeringsinntekt_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                        petroleumFordelt.fordeltEndringIMidlertidigForskjell_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel
                }
                settUniktFelt(petroleum.fordeltSkattemessigResultatEtterKorreksjon_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    petroleum.fordeltSkattemessigResultat_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel -
                        petroleumFordeling.andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel
                }
            }
        }

    internal val fordeltAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fordeltAlminneligInntektFraVirksomhetPaaSokkel") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleum.fordeltSkattemessigResultat_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                    modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultat_beloepAlminneligInntektFraVirksomhetPaaSokkel +
                        petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel -
                        petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaSokkel +
                        petroleumFordelt.fordeltEndringIMidlertidigForskjell_beloepAlminneligInntektFraVirksomhetPaaSokkel
                }
                settUniktFelt(petroleum.fordeltSkattemessigResultatEtterKorreksjon_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                    petroleum.fordeltSkattemessigResultat_beloepAlminneligInntektFraVirksomhetPaaSokkel -
                        petroleumFordeling.andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel
                }
            }
        }

    internal val fordeltResultatAvFinansinntektOgFinanskostnadMv =
        kalkyle("fordeltResultatAvFinansinntektOgFinanskostnadMv") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleum.fordeltSkattemessigResultat_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                    modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultat_beloepResultatAvFinansinntektOgFinanskostnadMv +
                    petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv -
                        petroleumFordelt.fordeltFradragINaeringsinntekt_beloepResultatAvFinansinntektOgFinanskostnadMv +
                        petroleumFordelt.fordeltEndringIMidlertidigForskjell_beloepResultatAvFinansinntektOgFinanskostnadMv
                }
            }
        }

    internal val fordeltAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("fordeltAlminneligInntektFraVirksomhetPaaLand") {
            hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
                settUniktFelt(petroleum.fordeltSkattemessigResultat_beloepAlminneligInntektFraVirksomhetPaaLand) {
                    modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultat_beloepAlminneligInntektFraVirksomhetPaaLand +
                    petroleumFordelt.fordeltTilleggINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand -
                        petroleumFordelt.fordeltFradragINaeringsinntekt_beloepAlminneligInntektFraVirksomhetPaaLand +
                        petroleumFordelt.fordeltEndringIMidlertidigForskjell_beloepAlminneligInntektFraVirksomhetPaaLand
                }
                settUniktFelt(petroleum.fordeltSkattemessigResultatEtterKorreksjon_beloepAlminneligInntektFraVirksomhetPaaLand) {
                    petroleum.fordeltSkattemessigResultat_beloepAlminneligInntektFraVirksomhetPaaLand +
                        petroleum.fordeltSkattemessigResultat_beloepResultatAvFinansinntektOgFinanskostnadMv +
                        petroleumFordeling.andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel
                }
            }
        }

    internal val aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        settUniktFelt(beregnetSelskapsskatt.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) der {
                forekomstType.investeringsaar stoerreEllerLik 2022
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel +
                    forekomstType.korreksjonAvTidligereAarsAvskrivningSomFoelgeAvSamordningEllerOmfordelingISaerskattegrunnlagFraVirksomhetPaaSokkel +
                    forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonISaerskattegrunnlagFraVirksomhetPaaSokkel
            }
        }
    }

    internal val aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val inntektsaar = inntektsaar
        settUniktFelt(beregnetSelskapsskatt.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.perRealisasjonsaar) der {
                forekomstType.realisasjonsaar lik inntektsaar.gjeldendeInntektsaar
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
        }
    }

    internal val aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel = kalkyle("aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel") {
        val inntektsaar = inntektsaar
        settUniktFelt(beregnetSelskapsskatt.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven.perRealisasjonsaar) der {
                forekomstType.realisasjonsaar lik inntektsaar.gjeldendeInntektsaar
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
        }
    }

    internal val aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel = kalkyle("aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel") {
        settUniktFelt(beregnetSelskapsskatt.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel) {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven.perInvesteringsaar) der {
                forekomstType.investeringsaar stoerreEllerLik 2022
            } summerVerdiFraHverForekomst {
                forekomstType.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel +
                    forekomstType.korreksjonAvTidligereAarsAvskrivningSomFoelgeAvSamordningEllerOmfordelingIAlminneligInntektFraVirksomhetPaaSokkel +
                    forekomstType.fradragForGjenvaerendeKostprisVedAvsluttetProduksjonIAlminneligInntektFraVirksomhetPaaSokkel
            }
        }
    }

    internal val aaretsKorrigeringForFriinntekt = kalkyle("aaretsKorrigeringForFriinntekt") {
        settUniktFelt(beregnetSelskapsskatt.aaretsKorrigeringForFriinntekt) {
            forekomsterAv(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) der {
                forekomstType.permanentForskjellstype lik permanentForskjellstype.kode_friinntektEtterPetroleumsskatteloven
            } summerVerdiFraHverForekomst {
                forekomstType.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.tall()
            }
        }
    }

    internal val grunnlagForBeregningAvSelskapsskatt = kalkyle("grunnlagForBeregningAvSelskapsskatt") {
        hvis(fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak()) {
            settUniktFelt(petroleum.grunnlagForBeregningAvSelskapsskatt) {
                petroleum.fordeltSkattemessigResultatEtterKorreksjon_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                    beregnetSelskapsskatt.aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel -
                    beregnetSelskapsskatt.aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel +
                    beregnetSelskapsskatt.aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel -
                    beregnetSelskapsskatt.aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel +
                    beregnetSelskapsskatt.aaretsInntektsfoeringIAlminneligInntektFraVirksomhetPaaSokkel -
                    beregnetSelskapsskatt.aaretsFradragsfoeringIAlminneligInntektFraVirksomhetPaaSokkel +
                    beregnetSelskapsskatt.aaretsKorrigeringForFriinntekt +
                    beregnetSelskapsskatt.annenKorreksjon

            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            varigOgBetydeligAnleggsmiddelMv,
            anleggsmiddelSomRoerledningProduksjonsinnretningMv,
            sumSkattemessigNedskrevetFormuesverdiForAnleggsmiddelISaerskattegrunnlagTilordnetVirksomhetPaaSokkel,
            sumNettoFinanskostnader,
            sumNettoFinanskostnaderForVirksomhetPaaSokkel,
            andelAvFinanskostnaderSomErFradragsberettigetIInntektFraVirksomhetPaaSokkel,
            fordeltSaerskattegrunnlagFraVirksomhetPaaSokkel,
            fordeltAlminneligInntektFraVirksomhetPaaSokkel,
            fordeltResultatAvFinansinntektOgFinanskostnadMv,
            fordeltAlminneligInntektFraVirksomhetPaaLand,
            aaretsAvskrivningISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsInntektsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsFradragsfoeringISaerskattegrunnlagFraVirksomhetPaaSokkel,
            aaretsAvskrivningIAlminneligInntektFraVirksomhetPaaSokkel,
            aaretsKorrigeringForFriinntekt,
            grunnlagForBeregningAvSelskapsskatt
        )
    }
}