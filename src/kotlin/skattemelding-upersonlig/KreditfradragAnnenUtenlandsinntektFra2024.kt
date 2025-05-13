package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.mindreEllerLikNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3

object KreditfradragAnnenUtenlandsinntektFra2024 : HarKalkylesamling {

    private fun GeneriskModellKontekst.tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() =
        forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) der {
            forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidrag() =
        if (!erPetroleumsforetak()) {
            if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
                    modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag -
                    modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    modell.inntektOgUnderskuddSvalbard.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak +
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt -
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd
            } else {
                modell.inntektOgUnderskudd.naeringsinntekt -
                    modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                    modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
                    modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                    modell.inntektOgUnderskudd.tilleggForIkkeFradragsberettigetEtterbetalingTilMedlemIEgetSamvirkeforetak +
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt -
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd
            }
        } else null

    private fun GeneriskModellKontekst.gjelderBeskatningEtterPetroleumsskatteloven(): Boolean =
        if (inntektsaar.tekniskInntektsaar >= 2024) {
            harMinstEnForekomstAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel) ||
                harMinstEnForekomstAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel) ||
                harMinstEnForekomstAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel) ||
                harMinstEnForekomstAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel) ||
                harMinstEnForekomstAv(modell.fremfoertUtenlandskSkattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel) ||
                harMinstEnForekomstAv(modell.fremfoertUtenlandskSkattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel)
        } else {
            modellV3.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningEtterPetroleumsskatteloven.erSann()
        }

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragPetroleumLand() =
        if (erPetroleumsforetak()) {
            modell.inntektOgUnderskudd.samletInntekt -
                modell.inntektOgUnderskudd.samletUnderskudd +
                modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag
        } else null

    internal val utenlandsinntektFoerAvgittKonsernbidrag =
        kalkyle("utenlandsinntektFoerAvgittKonsernbidrag") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.utlandsinntektFoerAvgittKonsernbidrag) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet
            }
        }

    internal val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt =
        kalkyle("resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt") {
            val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd =
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_samletDriftsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_samletDriftskostnad +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_samletFinansinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_samletFinanskostnad

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.stoerreEllerLik(0)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd
                }
            }

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.erNegativ()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_underskudd) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd?.abs()
                }
            }
        }

    internal val samletInntektsskattBetaltIUtlandet =
        kalkyle("samletInntektsskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletInntektsskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.inntektsskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    internal val samletFormuesskattBetaltIUtlandet =
        kalkyle("samletFormuesskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletFormuesskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.formuesskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    internal val beloepTilordnetUtlandet =
        kalkyle("beloepTilordnetUtlandet") {
            forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) der {
                forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
            } forHverForekomst {
                settFelt(forekomstType.beloepTilordnetUtlandet)  {
                    (forekomstType.beloep.tall() * forekomstType.andelAvKostnadTilordnetUtlandet.prosent()).somHeltall()
                }
            }
        }

    private val korrigertNettoinntekt =
        kalkyle("korrigertNettoinntekt") {
            val tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis((tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel.harVerdi() ||
                tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode().harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap.harVerdi() &&
                !erPetroleumsforetak())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt) {
                    val samletMottattKonsernbidrag = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                        modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag
                    } else {
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag
                    }
                    inntektFoerFradragForAvgittKonsernbidrag() +
                        modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                        modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() -
                        samletMottattKonsernbidrag +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }

            hvis(erPetroleumsforetak()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt) {
                    inntektFoerFradragForAvgittKonsernbidragPetroleumLand() +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() -
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }
        }

    internal val nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader =
        kalkyle("nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.resultatFraNaeringsvirksomhetIUtlandet_underskudd +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_inntektUnderlagtKildeskattIUtlandet -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_fremfoerbartUnderskuddIInntektTilordnetUtlandet
            }
        }

    internal val samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.stoerreEnn(0)
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt
                    .stoerreEnn(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader.tall())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    ((tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() *
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) /
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt).somHeltall()
                }
            }
            hvis(
                (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.mindreEllerLik(0))
                    || modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt
                    .mindreEllerLik(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader.tall())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode()
                }
            }
        }

     internal val samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode =
         kalkyle("samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode") {

            val kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) summerVerdiFraHverForekomst {
                    forekomstType.beloepTilordnetUtlandet.tall()
                }

             hvis(kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode.harVerdi() ||
                 modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode) {
                     modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode +
                         kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode
                 }
             }
         }

    private val norskAndelAvInntektFoerAvgittKonsernbidrag =
        kalkyle("norskAndelAvInntektFoerAvgittKonsernbidrag") {
            if (harForekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag) {
                    inntektFoerFradragForAvgittKonsernbidrag() +
                        inntektFoerFradragForAvgittKonsernbidragPetroleumLand() -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.utlandsinntektFoerAvgittKonsernbidrag
                }
            }
        }

    internal val avgittKonsernbidragIUtenlandsinntekt =
        kalkyle("avgittKonsernbidragIUtenlandsinntekt") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet.harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.utlandsinntektFoerAvgittKonsernbidrag.harVerdi() &&
                !gjelderBeskatningEtterPetroleumsskatteloven()
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt) {
                    val samletAvgittKonsernbidrag = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                        modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag
                    } else {
                        modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag
                    }

                    if (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag.erNegativ() &&
                        samletAvgittKonsernbidrag.erPositiv()) {
                        samletAvgittKonsernbidrag.tall()
                    } else if (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag.erPositiv() &&
                        samletAvgittKonsernbidrag.erPositiv()) {
                        samletAvgittKonsernbidrag -
                            (if (samletAvgittKonsernbidrag.tall()
                                    .mindreEllerLikNullsafe(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag.tall())
                            ) {
                                samletAvgittKonsernbidrag.tall()
                            } else {
                                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag.tall()
                            })
                    } else {
                        BigDecimal.ZERO
                    }
                }
            }
        }

    internal val samletUtenlandsinntekt =
        kalkyle("samletUtenlandsinntekt") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletUtenlandsinntekt) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.utlandsinntektFoerAvgittKonsernbidrag -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt
            }
        }

    internal val bruttoformueIUtlandet =
        kalkyle("bruttoformueIUtlandet") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.bruttoformueIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.formuesobjektUnderlagtBeskatningIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    internal val samletFradragIFormueIUtlandet =
        kalkyle("samletFradragIFormueIUtlandet") {
            hvis(
                modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt.harVerdi()
                    && modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt.ulik(0)
                    && modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()
            ) {
                settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletFradragIFormueIUtlandet) {
                    (modell.formueOgGjeldSvalbard.samletGjeld *
                        modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.bruttoformueIUtlandet /
                        modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt).somHeltall()
                }
            }

            hvis(
                modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.harVerdi()
                    && modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.ulik(0)
                    && modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erUsann()
            ) {
                settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletFradragIFormueIUtlandet) {
                    (modell.formueOgGjeld.samletGjeld *
                        modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.bruttoformueIUtlandet /
                        modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt).somHeltall()
                }
            }
        }

    internal val nettoformueIUtlandet =
        kalkyle("nettoformueIUtlandet") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoformueIUtlandet) {
                modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.bruttoformueIUtlandet -
                    modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletFradragIFormueIUtlandet
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt,
            samletInntektsskattBetaltIUtlandet,
            samletFormuesskattBetaltIUtlandet,
            beloepTilordnetUtlandet,
            korrigertNettoinntekt,
            nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader,
            samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode,
            samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode,
            utenlandsinntektFoerAvgittKonsernbidrag,
            norskAndelAvInntektFoerAvgittKonsernbidrag,
            avgittKonsernbidragIUtenlandsinntekt,
            samletUtenlandsinntekt,
            bruttoformueIUtlandet,
            samletFradragIFormueIUtlandet,
            nettoformueIUtlandet
        )
    }
}