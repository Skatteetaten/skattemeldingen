package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_annenFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.upersonlig.domenemodell.v2_2022.kostnadsfordelingsmetodeVedSkattBetaltIUtlandet_2022.kode_indirekteFordelingsmetode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object KreditfradragKostnadsfordelingFra2026 : HarKalkylesamling {

    // ---- AnnenUtenlandsinntekt ----
    private val tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntekt =
        kalkyle("tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntekt") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterIndirekteMetode) { sum }
            }
        }

    private val tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt =
        kalkyle("tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterAnnenFordelingsmetode) { sum }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt =
        kalkyle("samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt") {
            hvis(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.korrigeringIInntekt_samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntekt.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode
                }
            }
        }

    // ---- Nokus ----
    private val tilleggForKostnadTilFordelingEtterIndirekteMetodeNokus =
        kalkyle("tilleggForKostnadTilFordelingEtterIndirekteMetodeNokus") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterIndirekteMetode) { sum }
            }
        }

    private val tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokus =
        kalkyle("tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokus") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterAnnenFordelingsmetode) { sum }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokus =
        kalkyle("samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokus") {
            hvis(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.korrigeringIInntekt_samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskatteland.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode
                }
            }
        }

    // ---- AnnenUtenlandsinntektSokkel ----
    private val tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntektSokkel =
        kalkyle("tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntektSokkel") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterIndirekteMetode) { sum }
            }
        }

    private val tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel =
        kalkyle("tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterAnnenFordelingsmetode) { sum }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel =
        kalkyle("samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel") {
            hvis(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.korrigeringIInntekt_samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilAnnenUtenlandsinntektForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode
                }
            }
        }

    // ---- NokusSokkel ----
    private val tilleggForKostnadTilFordelingEtterIndirekteMetodeNokusSokkel =
        kalkyle("tilleggForKostnadTilFordelingEtterIndirekteMetodeNokusSokkel") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_indirekteFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterIndirekteMetode) { sum }
            }
        }

    private val tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokusSokkel =
        kalkyle("tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokusSokkel") {
            val sum =
                forekomsterAv(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.fordelingAvKostnad) der {
                    forekomstType.kostnadsfordelingsmetode.lik(kode_annenFordelingsmetode)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            hvis(sum.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_tilleggForKostnadTilFordelingEtterAnnenFordelingsmetode) { sum }
            }
        }

    private val samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokusSokkel =
        kalkyle("samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokusSokkel") {
            hvis(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode.harVerdi()) {
                settUniktFelt(modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.korrigeringIInntekt_samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetode) {
                    modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteEllerAnnenFordelingsmetode -
                        modell.fradragINorskInntektsskattForSkattBetaltTilFremmedStatKnyttetTilInntektFraNOKUSSelskapEllerVirksomhetILavskattelandForVirksomhetPaaSokkel.samletKostnadTilordnetUtlandetEtterIndirekteFordelingsmetode
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling =
        Kalkylesamling(
            tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntekt,
            tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt,
            samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntekt,
            tilleggForKostnadTilFordelingEtterIndirekteMetodeNokus,
            tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokus,
            samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokus,
            tilleggForKostnadTilFordelingEtterIndirekteMetodeAnnenUtenlandsinntektSokkel,
            tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel,
            samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeAnnenUtenlandsinntektSokkel,
            tilleggForKostnadTilFordelingEtterIndirekteMetodeNokusSokkel,
            tilleggForKostnadTilFordelingEtterAnnenFordelingsmetodeNokusSokkel,
            samletKostnadTilordnetUtlandetEtterAnnenFordelingsmetodeNokusSokkel
        )
}
