package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3

object KreditfradragAnnenUtenlandsinntekt : HarKalkylesamling {

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
        modellV3.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningEtterPetroleumsskatteloven.erSann()

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragPetroleumLand() =
        if(erPetroleumsforetak() && !gjelderBeskatningEtterPetroleumsskatteloven()) {
            modell.inntektOgUnderskudd.samletInntekt -
                modell.inntektOgUnderskudd.samletUnderskudd +
                modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag
        } else null

    private fun GeneriskModellKontekst.inntektFoerFradragForAvgittKonsernbidragPetroleumSokkel() =
        if(erPetroleumsforetak() && gjelderBeskatningEtterPetroleumsskatteloven()) {
            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.samletInntektAlminneligInntektFraVirksomhetPaaSokkel -
                modell.inntektOgUnderskuddForVirksomhetPaaSokkel.samletUnderskuddAlminneligInntektFraVirksomhetPaaSokkel
        } else null

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

            hvis(erPetroleumsforetak() && !gjelderBeskatningEtterPetroleumsskatteloven()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt) {
                    inntektFoerFradragForAvgittKonsernbidragPetroleumLand() +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() -
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }

            hvis(erPetroleumsforetak() && gjelderBeskatningEtterPetroleumsskatteloven()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt) {
                    inntektFoerFradragForAvgittKonsernbidragPetroleumSokkel() +
                        tilleggForKostnaderSomSkalFordelesEtterAnnenFordelingsNoekkel +
                        tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode() +
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_utbytteFraUtenlandskDatterselskap
                }
            }
        }

    internal val samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode =
        kalkyle("samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode") {
            hvis(
                modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.harVerdi()
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt.stoerreEnn(0)
                    && modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigertNettoinntekt
                    .stoerreEnn(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
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
                    .mindreEllerLik(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader)
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode) {
                    tilleggForKostnaderSomSkalFordelesEtterIndirekteMetode()
                }
            }
        }

    private val norskAndelAvInntektFoerAvgittKonsernbidrag =
        kalkyle("norskAndelAvInntektFoerAvgittKonsernbidrag") {
            if (harForekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt) &&
                (!erPetroleumsforetak() ||
                    (erPetroleumsforetak() && modellV3.metodeForAaUnngaaDobbeltbeskatning.gjelderBeskatningEtterPetroleumsskatteloven.erUsann()))
            ) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.norskAndelAvInntektFoerAvgittKonsernbidrag) {
                    inntektFoerFradragForAvgittKonsernbidrag() +
                        inntektFoerFradragForAvgittKonsernbidragPetroleumLand() -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.utlandsinntektFoerAvgittKonsernbidrag
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            KreditfradragAnnenUtenlandsinntektFra2024.resultatFraNaeringsvirksomhetIUtlandetNaeringsinntekt,
            KreditfradragAnnenUtenlandsinntektFra2024.samletInntektsskattBetaltIUtlandet,
            KreditfradragAnnenUtenlandsinntektFra2024.samletFormuesskattBetaltIUtlandet,
            KreditfradragAnnenUtenlandsinntektFra2024.beloepTilordnetUtlandet,
            korrigertNettoinntekt,
            KreditfradragAnnenUtenlandsinntektFra2024.nettoinntektUnderlagtBeskatningIUtlandetFoerFradragForIndirekteKostnader,
            KreditfradragAnnenUtenlandsinntektFra2024.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode,
            KreditfradragAnnenUtenlandsinntektFra2024.samletKostnadTilordnetUtlandetEtterIndirektEllerAnnenFordelingsmetode,
            KreditfradragAnnenUtenlandsinntektFra2024.utenlandsinntektFoerAvgittKonsernbidrag,
            norskAndelAvInntektFoerAvgittKonsernbidrag,
            KreditfradragAnnenUtenlandsinntektFra2024.avgittKonsernbidragIUtenlandsinntekt,
            KreditfradragAnnenUtenlandsinntektFra2024.samletUtenlandsinntekt,
            KreditfradragAnnenUtenlandsinntektFra2024.bruttoformueIUtlandet,
            KreditfradragAnnenUtenlandsinntektFra2024.samletFradragIFormueIUtlandet,
            KreditfradragAnnenUtenlandsinntektFra2024.nettoformueIUtlandet
        )
    }
}