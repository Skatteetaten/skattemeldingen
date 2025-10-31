package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object KreditfradragNokus : HarKalkylesamling {

    private fun GeneriskModellKontekst.tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() =
        forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) der {
            forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    internal fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragNokus(): BigDecimal? {
        val inntektFoerFradragForAvgittKonsernbidragNokus = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
            modell.inntektOgUnderskuddSvalbard.naeringsinntekt -
                modell.inntektOgUnderskuddSvalbard.inntektsfradrag_underskudd +
                modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag -
                modell.inntektOgUnderskuddSvalbard.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
        } else {
            modell.inntektOgUnderskudd.naeringsinntekt -
                modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
                modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag -
                modell.inntektOgUnderskudd.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
        }
        if(inntektsaar.tekniskInntektsaar >= 2024) {
            return inntektFoerFradragForAvgittKonsernbidragNokus +
                modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt -
                modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd
        }
        return inntektFoerFradragForAvgittKonsernbidragNokus
    }

    private val utenlandsinntektFoerAvgittKonsernbidragNokus =
        kalkyle ("utenlandsinntektFoerAvgittKonsernbidragNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.utlandsinntektFoerAvgittKonsernbidrag) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet
            }
        }

    private val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektNokus =
        kalkyle("resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektNokus") {
            val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd =
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_samletDriftsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_samletDriftskostnad +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_samletFinansinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_samletFinanskostnad

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.stoerreEllerLik(0)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd
                }
            }

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.erNegativ()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_underskudd) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd?.abs()
                }
            }
        }

    private val samletInntektsskattBetaltIUtlandetNokus =
        kalkyle("samletInntektsskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletInntektsskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.inntektsskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val samletFormuesskattBetaltIUtlandetNokus =
        kalkyle("samletFormuesskattBetaltIUtlandetNokus") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletFormuesskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.formuesskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val beloepTilordnetUtlandetNokus =
        kalkyle("beloepTilordnetUtlandetNokus") {
            forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) der {
                forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
            } forHverForekomst {
                settFelt(forekomstType.beloepTilordnetUtlandet)  {
                    (forekomstType.beloep.tall() * forekomstType.andelAvKostnadTilordnetUtlandet.prosent()).somHeltall()
                }
            }
        }

    private val korrigertNettoinntektNokus =
        kalkyle("korrigertNettoinntektNokus") {

            val tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsmetodeMetode =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis(tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsmetodeMetode.harVerdi() ||
                tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus().harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap.harVerdi()
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt) {
                    val samletMottattKonsernbidrag = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                        modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag
                    } else {
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag
                    }
                    inntektFoerFradragForAvgittKonsernbidragNokus() +
                        modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning -
                        modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsmetodeMetode +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() -
                        samletMottattKonsernbidrag +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }
        }

    private val nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnaderNokus =
        kalkyle("nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnaderNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.resultatFraNaeringsvirksomhetIUtlandet_underskudd +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_inntektUnderlagtKildeskattIUtlandet -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_fremfoerbartUnderskuddIInntektTilordnetUtlandet
            }
        }

    internal val samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetodeNokus =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetodeNokus") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt.stoerreEnn(0)
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt
                    .stoerreEnn(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    ((tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() *
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) /
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt).somHeltall()
                }
            }
            hvis(
                (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt.mindreEllerLik(0))
                    || modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigertNettoinntekt
                    .mindreEllerLik(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus()
                }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetodeNokus =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetodeNokus") {

            val kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) summerVerdiFraHverForekomst {
                    forekomstType.beloepTilordnetUtlandet.tall()
                }

            hvis(kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode.harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode +
                        kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode
                }
            }
        }

    private val norskAndelAvInntektFoerAvgittKonsernbidragNokus =
        kalkyle("norskAndelFoerAvgittKonsernbidragNokus") {
            if (harForekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.norskAndelAvInntektFoerAvgittKonsernbidrag) {
                    inntektFoerFradragForAvgittKonsernbidragNokus() -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.utlandsinntektFoerAvgittKonsernbidrag
                }
            }
        }

    private val avgittKonsernbidragIUtenlandsinntektNokus =
        kalkyle("avgittKonsernbidragIUtenlandsinntektNokus") {

            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet.harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.utlandsinntektFoerAvgittKonsernbidrag.harVerdi()
            ) {
                val samletAvgittKonsernbidrag = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag
                } else {
                    modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag
                }

                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt) {
                    if (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.norskAndelAvInntektFoerAvgittKonsernbidrag.erNegativ() &&
                        samletAvgittKonsernbidrag.erPositiv()) {
                        samletAvgittKonsernbidrag.tall()
                    } else if (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.norskAndelAvInntektFoerAvgittKonsernbidrag.erPositiv() &&
                        samletAvgittKonsernbidrag.erPositiv()) {
                        samletAvgittKonsernbidrag -
                            minsteVerdiAv(
                                samletAvgittKonsernbidrag.tall(),
                                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.norskAndelAvInntektFoerAvgittKonsernbidrag.tall()
                            )
                    } else {
                        BigDecimal.ZERO
                    }
                }
            }
        }

    private val samletUtenlandsinntektNokus=
        kalkyle("samletUtenlandsinntektNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletUtenlandsinntekt) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.utlandsinntektFoerAvgittKonsernbidrag -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt
            }
        }

    private val bruttoformueIUtlandetNokus =
        kalkyle("bruttoformueIUtlandetNokus") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.bruttoformueIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.formuesobjektUnderlagtBeskatningIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val samletFradragIFormueIUtlandetNokus =
        kalkyle("samletFradragIFormueIUtlandetNokus") {
            hvis(
                modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()
                    && modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt.harVerdi()
                    && modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt.ulik(0)
            ) {
                settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletFradragIFormueIUtlandet) {
                    (modell.formueOgGjeldSvalbard.samletGjeld *
                        modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.bruttoformueIUtlandet /
                        modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt).somHeltall()
                }
            }

            hvis(
                modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erUsann()
                    && modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.harVerdi()
                    && modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.ulik(0)
            ) {
                settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletFradragIFormueIUtlandet) {
                    (modell.formueOgGjeld.samletGjeld *
                        modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.bruttoformueIUtlandet /
                        modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt).somHeltall()
                }
            }
        }

    private val nettoformueIUtlandetNokus =
        kalkyle("nettoformueIUtlandetNokus") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.nettoformueIUtlandet) {
                modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.bruttoformueIUtlandet -
                    modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletFradragIFormueIUtlandet
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektNokus,
            samletInntektsskattBetaltIUtlandetNokus,
            samletFormuesskattBetaltIUtlandetNokus,
            beloepTilordnetUtlandetNokus,
            korrigertNettoinntektNokus,
            nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnaderNokus,
            samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetodeNokus,
            samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetodeNokus,
            utenlandsinntektFoerAvgittKonsernbidragNokus,
            norskAndelAvInntektFoerAvgittKonsernbidragNokus,
            avgittKonsernbidragIUtenlandsinntektNokus,
            samletUtenlandsinntektNokus,
            bruttoformueIUtlandetNokus,
            samletFradragIFormueIUtlandetNokus,
            nettoformueIUtlandetNokus
        )
    }
}