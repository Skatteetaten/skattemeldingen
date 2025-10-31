package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object KreditfradragNokusSokkel : HarKalkylesamling {

    private fun GeneriskModellKontekst.tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() =
        forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) der {
            forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragNokus() =
        if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
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

    private val utenlandsinntektFoerAvgittKonsernbidragNokus =
        kalkyle ("utenlandsinntektFoerAvgittKonsernbidragNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_mottattKonsernbidragTilordnetUtlandet
            }
        }

    private val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektNokus =
        kalkyle("resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektNokus") {
            val resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd =
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletDriftsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletDriftskostnad +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletFinansinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_samletFinanskostnad

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.stoerreEllerLik(0)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd
                }
            }

            hvis(resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd.erNegativ()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_underskudd) {
                    resultatFraNaeringsvirksomhetIUtlandetNaeringsinntektUnderskudd?.abs()
                }
            }
        }

    private val samletInntektsskattBetaltIUtlandetNokus =
        kalkyle("samletInntektsskattBetaltIUtlandet") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletInntektsskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.inntektsskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val samletFormuesskattBetaltIUtlandetNokus =
        kalkyle("samletFormuesskattBetaltIUtlandetNokus") {
            settUniktFelt(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletFormuesskattBetaltIUtlandet) {
                forekomsterAv(modell.fradragINorskFormuesskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.formuesskattBetaltIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }

    private val beloepTilordnetUtlandetNokus =
        kalkyle("beloepTilordnetUtlandetNokus") {
            forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) der {
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
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis(tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsmetodeMetode.harVerdi() ||
                tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus().harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap.harVerdi()
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt) {
                    val samletMottattKonsernbidrag = if (modell.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningPaaSvalbard.erSann()) {
                        modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag
                    } else {
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag
                    }
                    inntektFoerFradragForAvgittKonsernbidragNokus() +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsmetodeMetode +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() -
                        samletMottattKonsernbidrag +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }
        }

    private val nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnaderNokus =
        kalkyle("nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnaderNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_naeringsinntekt -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.resultatFraNaeringsvirksomhetIUtlandet_underskudd +
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_inntektUnderlagtKildeskattIUtlandet -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_fremfoerbartUnderskuddIInntektTilordnetUtlandet
            }
        }

    internal val samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetodeNokus =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetodeNokus") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt.stoerreEnn(0)
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt
                    .stoerreEnn(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    ((tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus() *
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader) /
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt).somHeltall()
                }
            }
            hvis(
                (modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt.mindreEllerLik(0))
                    || modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigertNettoinntekt
                    .mindreEllerLik(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    tilleggForKostnaderSomSkalFordelesEtterIndirekteMetodeNokus()
                }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetodeNokus =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetodeNokus") {

            val kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) summerVerdiFraHverForekomst {
                    forekomstType.beloepTilordnetUtlandet.tall()
                }

            hvis(kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode.harVerdi() ||
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode +
                        kostnaderTilordnetUtlandetEtterAnnenFordelingsmetode
                }
            }
        }

    private val norskAndelAvInntektFoerAvgittKonsernbidragNokus =
        kalkyle("norskAndelFoerAvgittKonsernbidragNokus") {
            if (harForekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel)) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.norskAndelAvInntektFoerAvgittKonsernbidrag) {
                    inntektFoerFradragForAvgittKonsernbidragNokus() -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag
                }
            }
        }

    private val samletUtenlandsinntektNokus=
        kalkyle("samletUtenlandsinntektNokus") {
            settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletUtenlandsinntekt) {
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.utlandsinntektFoerAvgittKonsernbidrag -
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_avgittKonsernbidragIUtenlandsinntekt
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
            samletUtenlandsinntektNokus,
        )
    }
}