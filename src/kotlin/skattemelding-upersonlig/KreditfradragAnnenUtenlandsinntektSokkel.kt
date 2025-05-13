package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object KreditfradragAnnenUtenlandsinntektSokkel : HarKalkylesamling {

    private fun GeneriskModellKontekst.tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() =
        forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) der {
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

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragPetroleumSokkel() =
        if (erPetroleumsforetak()) {
            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.samletInntektAlminneligInntektFraVirksomhetPaaSokkel -
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.samletUnderskuddAlminneligInntektFraVirksomhetPaaSokkel
        } else null

    private val utenlandsinntektFoerAvgittKonsernbidrag =
        kalkyle("utenlandsinntektFoerAvgittKonsernbidrag") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet
            }
        }

    private val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt =
        kalkyle("resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt") {
            val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd =
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletDriftsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletDriftskostnad +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletFinansinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletFinanskostnad

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.stoerreEllerLik(0)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd
                }
            }

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.erNegativ()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_underskudd) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd?.abs()
                }
            }
        }

    private val samletInntektsskattBetaltIUtlandet =
        kalkyle("samletInntektsskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletInntektsskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.inntektsskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val samletFormuesskattBetaltIUtlandet =
        kalkyle("samletFormuesskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletFormuesskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.formuesskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val beloepTilordnetUtlandet =
        kalkyle("beloepTilordnetUtlandet") {
            forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) der {
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
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis((tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel.harVerdi() ||
                tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode().harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap.harVerdi() &&
                !erPetroleumsforetak())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt) {
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
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }

            if (erPetroleumsforetak()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt) {
                    inntektFoerFradragForAvgittKonsernbidragPetroleumSokkel() +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }
        }

    private val nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader =
        kalkyle("nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_underskudd +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_inntektUnderlagtKildeskattIUtlandet -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_fremfoerbartUnderskuddIInntektTilordnetUtlandet
            }
        }

    internal val samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt.stoerreEnn(0)
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt
                    .stoerreEnn(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader.tall())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    ((tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() *
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) /
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt).somHeltall()
                }
            }
            hvis(
                (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt.mindreEllerLik(0))
                    || modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigertNettoinntekt
                    .mindreEllerLik(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader.tall())
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode()
                }
            }
        }

     private val samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode =
         kalkyle("samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode") {

            val kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) summerVerdiFraHverForekomst {
                    forekomstType.beloepTilordnetUtlandet.tall()
                }

             hvis(kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode.harVerdi() ||
                 modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode) {
                     modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode +
                         kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode
                 }
             }
         }

    private val norskAndelAvInntektFoerAvgittKonsernbidrag =
        kalkyle("norskAndelAvInntektFoerAvgittKonsernbidrag") {
            if (harForekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.norskAndelAvInntektFoerAvgittKonsernbidrag) {
                    inntektFoerFradragForAvgittKonsernbidrag() +
                        inntektFoerFradragForAvgittKonsernbidragPetroleumSokkel() -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag
                }
            }
        }

    private val samletUtenlandsinntekt =
        kalkyle("samletUtenlandsinntekt") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletUtenlandsinntekt) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt
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
            samletUtenlandsinntekt,
        )
    }
}