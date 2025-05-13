@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaendeFra2024 : HarKalkylesamling {

    val forekomstType = modell.rentebegrensning
    private const val UNNTAKSREGEL_PAA_SELSKAPSNIVAA = "unntaksregelPaaSelskapsnivaa"
    private const val UNNTAKSREGEL_PAA_NASJONALTNIVAA = "unntaksregelPaaNasjonaltNivaa"


    val samletRentekostnadOgGarantiprovisjonKalkyle = kalkyle("samletRentekostnadOgGarantiprovisjon") {
        val samletRentekostnadOgGarantiprovisjon = forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRentekostnad +
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_garantiprovisjonForGjeld

        hvis(forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedInngangenTilInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedUtgangenAvInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret.harVerdi()
        ) {
            settUniktFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletRentekostnadOgGarantiprovisjon) {
                samletRentekostnadOgGarantiprovisjon
            }
        }

        hvis(forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedInngangenTilInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedUtgangenAvInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret.harVerdi()
            || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret.harVerdi()
        ) {
            settUniktFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletRentekostnadOgGarantiprovisjon) {
                samletRentekostnadOgGarantiprovisjon
            }
        }
    }

    val andelAvRentekostnadSelskapMvISammeKonsernKalkyle = kalkyle("andelAvRentekostnadSelskapMvISammeKonsern") {
        val samletGjeldTilNaerstaaende =
            forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret
        val samletGjeld =
            forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedInngangenTilInntektsaaret +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedUtgangenAvInntektsaaret
        val rentekostnad = forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletRentekostnadOgGarantiprovisjon
        hvis(samletGjeldTilNaerstaaende.harVerdi() && samletGjeld.erPositiv() && rentekostnad.harVerdi()) {
            settUniktFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_andelAvRentekostnad) {
                ((samletGjeldTilNaerstaaende / samletGjeld) * rentekostnad).somHeltall()
            }
        }
    }

    val andelAvRentekostnadAnnenNaerstaaendePartKalkyle = kalkyle("andelAvRentekostnadAnnenNaerstaaendePart") {
        val samletGjeldTilNaerstaaende =
            forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret
        val samletGjeld =
            forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedInngangenTilInntektsaaret +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedUtgangenAvInntektsaaret
        val rentekostnad = forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletRentekostnadOgGarantiprovisjon
        hvis(samletGjeldTilNaerstaaende.harVerdi() && samletGjeld.erPositiv() && rentekostnad.harVerdi()) {
            settUniktFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_andelAvRentekostnad) {
                ((samletGjeldTilNaerstaaende / samletGjeld) * rentekostnad).somHeltall()
            }
        }
    }

    val nettoRentekostnadKalkyle = kalkyle("nettoRentekostnad") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad) {
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRentekostnad +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_garantiprovisjonForGjeld -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRenteinntekt -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_gevinstVedRealisasjonAvOverEllerUnderkursobligasjon +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_tapVedRealisasjonAvOverEllerUnderkursobligasjon -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_gevinstVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_tapVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres
        }
    }

    val totalRentekostnadBetaltTilSelskapMvISammeKonsernKalkyle = kalkyle("totalRentekostnadBetaltTilSelskapMvISammeKonsern") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilSelskapMvISammeKonsern) {
            val rentekostnaderTransaksjonerTilEllerFraNaerstaaendeMv =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_transaksjonTilEllerFraNaerstaaendeMv) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilSelskapMvISammeKonsern.tall()
                }
            val rentekostnaderNaerstaaendeSomGarantist =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadMedNaerstaaendeSomGarantist) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilSelskapMvISammeKonsern.tall()
                }
            val rentekostnaderNaerstaaendeSomFordringshaver =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadMedNaerstaaendeSomFordringshaver) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilSelskapMvISammeKonsern.tall()
                }
            rentekostnaderTransaksjonerTilEllerFraNaerstaaendeMv +
                rentekostnaderNaerstaaendeSomGarantist +
                rentekostnaderNaerstaaendeSomFordringshaver +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadTilGarantiprovisjonForGjeldTilSelskapMvISammeKonsern +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_andelAvRentekostnad
        }
    }

    val totalRenteinntektMottattFraSelskapMvISammeKonsernKalkyle = kalkyle("totalRenteinntektMottattFraSelskapMvISammeKonsern") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraSelskapMvISammeKonsern) {
            val renteinntekterTransaksjonerTilEllerFraNaerstaaendeMv =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_transaksjonTilEllerFraNaerstaaendeMv) summerVerdiFraHverForekomst {
                    forekomstType.renteinntektMottattFraSelskapMvISammeKonsern.tall()
                }
            renteinntekterTransaksjonerTilEllerFraNaerstaaendeMv -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_tapVedRealisasjonAvOverEllerUnderkursobligasjonTilSelskapMvISammeKonsern +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_gevinstVedRealisasjonAvOverEllerUnderkursobligasjonTilSelskapMvISammeKonsern -
                forekomstType.tapVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeresTilSelskapMvISammeKonsern +
                forekomstType.gevinstVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeresTilSelskapMvISammeKonsern
        }
    }

    val totalRentekostnadBetaltTilAnnenNaerstaaendePartKalkyle = kalkyle("totalRentekostnadBetaltTilAnnenNaerstaaendePart") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilAnnenNaerstaaendePart) {
            val rentekostnaderTransaksjonerTilEllerFraNaerstaaendeMv =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_transaksjonTilEllerFraNaerstaaendeMv) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilAnnenNaerstaaendePart.tall()
                }
            val rentekostnaderNaerstaaendeSomGarantist =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadMedNaerstaaendeSomGarantist) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilAnnenNaerstaaendePart.tall()
                }
            val rentekostnaderNaerstaaendeSomFordringshaver =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadMedNaerstaaendeSomFordringshaver) summerVerdiFraHverForekomst {
                    forekomstType.rentekostnadBetaltTilAnnenNaerstaaendePart.tall()
                }
            rentekostnaderTransaksjonerTilEllerFraNaerstaaendeMv +
                rentekostnaderNaerstaaendeSomGarantist +
                rentekostnaderNaerstaaendeSomFordringshaver +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_rentekostnadTilGarantiprovisjonForGjeldTilAnnenNaerstaaendePart +
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_andelAvRentekostnad
        }
    }

    val totalRenteinntektMottattFraAnnenNaerstaaendePartKalkyle = kalkyle("totalRenteinntektMottattFraAnnenNaerstaaendePart") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraAnnenNaerstaaendePart) {
            val renteinntekterTransaksjonerTilEllerFraNaerstaaendeMv =
                forekomsterAv(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_transaksjonTilEllerFraNaerstaaendeMv) summerVerdiFraHverForekomst {
                    forekomstType.renteinntektMottattFraAnnenNaerstaaendePart.tall()
                }
            renteinntekterTransaksjonerTilEllerFraNaerstaaendeMv -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_tapVedRealisasjonAvOverEllerUnderkursobligasjonTilAnnenNaerstaaendePart +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_gevinstVedRealisasjonAvOverEllerUnderkursobligasjonTilAnnenNaerstaaendePart -
                forekomstType.tapVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeresTilAnnenNaerstaaendePart +
                forekomstType.gevinstVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeresTilAnnenNaerstaaendePart
        }
    }

    val totalNettoRentekostnadTilSelskapISammeKonsernKalkyle = kalkyle("totalNettoRentekostnadTilSelskapISammeKonsern") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern) {
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilSelskapMvISammeKonsern -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraSelskapMvISammeKonsern
        }
    }

    val nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsernKalkyle = kalkyle("nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern) {
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilAnnenNaerstaaendePart -
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraAnnenNaerstaaendePart
        }
    }

    val totalNettoRentekostnadTilNaerstaaendeMvKalkyle = kalkyle("totalNettoRentekostnadTilNaerstaaendeMv") {
        settUniktFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnaderTilNaerstaaendeMv) {
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern +
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern
        }
    }

    val grunnlagForRentefradragsrammeKalkyle = kalkyle("grunnlagForRentefradragsramme") {
        hvis(harForekomsterAv(forekomstType)) {
            settUniktFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme) {
                modell.inntektOgUnderskudd.inntektFoerFradragForEventueltAvgittKonsernbidrag +
                    modell.inntektOgUnderskuddSvalbard.inntektFoerFradragForEventueltAvgittKonsernbidrag -
                    modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag -
                    modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag +
                    modell.rederiskatteordning_gevinstkonto.inntektsfoeringAvGevinstkonto +
                    forekomstType.beregningsgrunnlagForRentefradragsramme_tilleggForSkattemessigAvskrivning -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_periodisertLeiekostnad -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_fradragForKonsernbidragSomSkalEkskluderes +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad
            }
        }
    }

    val nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntektKalkyle = kalkyle("nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntekt") {
        settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad) {
            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad.tall() medMinimumsverdi 0
        }
    }

    val rentefradragsrammeKalkyle = kalkyle("rentefradragsramme") {
        hvis(harForekomsterAv(forekomstType)) {
            hvis(forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme.erPositiv()) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme) {
                    (forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme * satser!!.sats(
                        Sats.rentebegrensning_fradragsrammeProsent
                    )).somHeltall()
                }
            }
            hvis(
                forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme.erNegativ() ||
                    forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme lik 0
            ) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme) {
                    BigDecimal.ZERO
                }
            }
        }
    }


    val differanseMellomNettoRentekostnadOgRentefradragsrammenKalkyle = kalkyle("differanseMellomNettoRentekostnadOgRentefradragsrammen") {
        settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen) {
            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad -
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme
        }
    }

    val tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle =
        kalkyle("tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle") {
            hvis(forekomstType.erKonsernIhtRegelverkForRentebegrensning.erUsann()) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme) {
                    if (forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad mindreEllerLik satser!!.sats(
                            Sats.rentebegrensning_grensebeloepUtenforKonsern
                        )
                    ) {
                        BigDecimal.ZERO
                    } else {
                        lavesteTallAv(
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen.tall(),
                            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern.tall()
                        ) medMinimumsverdi 0
                    }
                }
            }
            hvis(forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme) {
                    if (
                        forekomstType.harRentekostnadLavereEnnTerskelbeloepPerNorskDelAvKonsern.erSann() &&
                        ((forekomstType.annetForetakRapportererPaaVegneAvKonsern.erSann() && forekomstType.annetForetakSomRapportererPaaVegneAvKonsern_identifikator.harVerdi()) ||
                            ((forekomstType.annetForetakRapportererPaaVegneAvKonsern.erUsann() ||
                                forekomstType.annetForetakRapportererPaaVegneAvKonsern.harIkkeVerdi()) &&
                                !generiskModell.verdiFor(forekomstType.annetSelskapIKonsern.selskapetsOrganisasjonsnummer)
                                    .isNullOrBlank() &&
                                !generiskModell.verdiFor(forekomstType.annetSelskapIKonsern.nettoFradragsfoertRentekostnad)
                                    .isNullOrBlank()))
                    ) {
                        BigDecimal.ZERO
                    } else {
                        lavesteTallAv(
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad.tall(),
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen.tall()
                        ) medMinimumsverdi 0
                    }
                }
            }
        }

    val tilbakefoertTilleggIInntektForSelskapIKonsernMvKalkyle =
        kalkyle("tilbakefoertTilleggIInntektForSelskapIKonsernMv") {
            hvis(
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.erPositiv()
                    && forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                    && modell.unntakForRentebegrensning.unntakForRentebegrensningSkalBekreftesAvRevisor.erSann()
                    && modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.verdi() == UNNTAKSREGEL_PAA_SELSKAPSNIVAA
                    && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
                    && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
                    && (modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.harVerdi()
                    || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt.harVerdi()
                    )
                    && modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.harVerdi()
                    && ((modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + BigDecimal(
                    2) stoerreEllerLik modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap.tall()))
            ) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                }
            }
            hvis(
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.erPositiv()
                    && forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                    && modell.unntakForRentebegrensning.unntakForRentebegrensningSkalBekreftesAvRevisor.erSann()
                    && modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.verdi() == UNNTAKSREGEL_PAA_NASJONALTNIVAA
            ) {
                hvis(
                    modell.unntakForRentebegrensning.anvendtUnntaksregel_erHelnorskKonsern.erSann()
                        && modell.unntakForRentebegrensning.anvendtUnntaksregel_selskapetErInkludertIAnnenInnrapportering.erUsann()
                        && !generiskModell.verdiFor(modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapINorskDelAvEllerInnenlandskKonsern.selskapetsOrganisasjonsnummer)
                        .isNullOrBlank()
                ) {
                    settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                    }
                }
                hvis(
                    modell.unntakForRentebegrensning.anvendtUnntaksregel_selskapetErInkludertIAnnenInnrapportering.erSann()
                        && modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapSomRapportererPaaVegneAvKonsern_identifikator.harVerdi()
                ) {
                    settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                    }
                }
                hvis(
                    modell.unntakForRentebegrensning.anvendtUnntaksregel_erHelnorskKonsern.erUsann()
                        && modell.unntakForRentebegrensning.anvendtUnntaksregel_selskapetErInkludertIAnnenInnrapportering.erUsann()
                        && !generiskModell.verdiFor(modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapINorskDelAvEllerInnenlandskKonsern.selskapetsOrganisasjonsnummer)
                        .isNullOrBlank()
                        && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
                        && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
                        && (modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.harVerdi()
                        || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt.harVerdi())
                        && modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.harVerdi()
                        && ((modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + BigDecimal(
                        2) stoerreEllerLik modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap.tall()))
                ) {
                    settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                    }
                }
            }
        }

    val korrigertRentestoerrelse = kalkyle("korrigertRentestoerrelse") {
        hvis(
            forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann() &&
                (forekomstType.harRentekostnadLavereEnnTerskelbeloepPerNorskDelAvKonsern.erSann() &&
                        (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.harIkkeVerdi() ||
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme lik 0)) ||
                (modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.harVerdi() &&
                        (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv.harVerdi() ||
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme lik 0)
                )
            )
        {
            var verdi =
                modell.rentebegrensning.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad.tall()
            val totalNettoRentekostnadTilSelskapISammeKonsern =
                modell.rentebegrensning.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern.tall()

            if (totalNettoRentekostnadTilSelskapISammeKonsern.erNegativ()) {
                verdi -= totalNettoRentekostnadTilSelskapISammeKonsern
            }
            hvis(verdi.erPositiv()) {
                settUniktFelt(modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse) {
                    verdi
                }
            }
        }
    }

    val tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaendeKalkyle =
        kalkyle("tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende") {
                val skalSetteFelt = forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse.harVerdi()
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse stoerreEnn satser!!.sats(
                    Sats.rentebegrensning_grensebeloepUtenforKonsern
                )
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse stoerreEnn forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme.tall()
                    && forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern.erPositiv()
                hvis(skalSetteFelt) {
                    settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende) {
                        lavesteTallAv(
                            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern.tall(),
                            (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse - forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme)
                        )
                    }
                }
        }

    val aaretsTilleggIInntektKalkyle = kalkyle("aaretsTilleggIInntekt") {
        settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt) {
            (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme -
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv +
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende) medMinimumsverdi 0
        }
    }

    val aaretsFradragIInntektKalkyle = kalkyle("aaretsFradragIInntekt") {
        hvis(
            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen mindreEllerLik 0 &&
                (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt lik 0 ||
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt.harIkkeVerdi())
        ) {
            val sumFremfoertRentefradragFraTidligereAar = forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
                forekomstType.fremfoertRentefradragFraTidligereAar.tall()
            }
            hvis (sumFremfoertRentefradragFraTidligereAar.erPositiv()) {
                settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt) {
                    lavesteTallAv(
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen.tall().absoluttverdi(),
                        sumFremfoertRentefradragFraTidligereAar
                    )
                }
            }
        }
    }

    val tilleggEllerFradragIInntektSomFoelgeAvRentebegrensningKalkyle = kalkyle("tilleggIInntektSomFoelgeAvRentebegrensning") {
        val tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning = forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt -
            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt
        if (tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning.erPositiv()) {
            settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning) {
                tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning
            }
        } else {
            settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning) {
                tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning.absoluttverdi()
            }
        }
    }

    val fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradragKalkyle = kalkyle("fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag") {
        val sumFremfoertRentefradragFraTidligereAar = forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
            forekomstType.fremfoertRentefradragFraTidligereAar.tall()
        }
        hvis (sumFremfoertRentefradragFraTidligereAar.erPositiv() && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad.stoerreEllerLik(0)) {
            val nettoRentekostnad = (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad -
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning +
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning) medMinimumsverdi 0
            settUniktFelt(forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag) {
                lavesteTallAv(sumFremfoertRentefradragFraTidligereAar, nettoRentekostnad)
            }
        }
    }

    val fremfoerbartRentefradragIInntektIInntektsaaret = kalkyle("fremfoerbartRentefradragIInntektIInntektsaaret") {
        settUniktFelt(forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoerbartRentefradragIInntekt) {
            (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt +
                forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag -
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt -
                forekomstType.rentefradragTilFremfoeringIInntektsaaret_justeringForSdfEllerNokus) medMinimumsverdi 0
        }
    }

    val aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle = kalkyle("aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar") {
        var resterendeFremfoertRentefradrag = forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag.tall()
        forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) transformerListe {
            liste -> liste.sortedBy { it.generiskModell.verdiFor(it.forekomstType.inntektsaar) }
        } forHverForekomst {
            hvis (resterendeFremfoertRentefradrag.erPositiv()) {
                val aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar =
                    forekomstType.fremfoertRentefradragFraTidligereAar.tall() medMaksimumsverdi resterendeFremfoertRentefradrag
                settFelt(forekomstType.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar) {
                    aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
                }
                resterendeFremfoertRentefradrag -= aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
            }
        }
    }

    val fremfoerbartRentefradragIInntektTidligereAar = kalkyle("fremfoerbartRentefradragIInntektTidligereAar") {
        forAlleForekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) {
            settFelt(forekomstType.fremfoerbartRentefradragIInntekt) {
                forekomstType.fremfoertRentefradragFraTidligereAar -
                    forekomstType.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
            }
        }
    }

    val fremfoerbarRentekostnadKalkyle = kalkyle("fremfoerbarRentekostnad") {
        val sumFremfoerbarRentefradragIInntekt = forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
            forekomstType.fremfoerbartRentefradragIInntekt.tall()
        }
        settUniktFelt(forekomstType.fremfoerbarRentekostnad) {
            sumFremfoerbarRentefradragIInntekt + forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoerbartRentefradragIInntekt
        }
    }

    val samletMottattKonsernbidrag = kalkyle("samletMottattKonsernbidrag") {
        settUniktFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_samletMottattKonsernbidrag) {
            forekomsterAv(forekomstType.beregningsgrunnlagForRentefradragsramme_konsernbidragPerMotpart) summerVerdiFraHverForekomst {
                forekomstType.mottattBeloep.tall()
            }
        }
    }

    val sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap = kalkyle("sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap") {
        settUniktFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap) {
            forekomsterAv(forekomstType.beregningsgrunnlagForRentefradragsramme_konsernbidragPerMotpart.spesifikasjonAvAvgittAndelAvKonsernbidrag) summerVerdiFraHverForekomst {
                forekomstType.andelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap.tall()
            }
        }
    }

    val fradragForKonsernbidragSomSkalEkskluderes = kalkyle("fradragForKonsernbidragSomSkalEkskluderes") {
        settUniktFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_fradragForKonsernbidragSomSkalEkskluderes) {
            forekomstType.beregningsgrunnlagForRentefradragsramme_samletMottattKonsernbidrag -
                forekomstType.beregningsgrunnlagForRentefradragsramme_sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap
        }
    }

    val egenkapitalandelIKonsernregnskapKalkyle = kalkyle("egenkapitalandelIKonsernregnskap") {
        hvis(modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta().erPositiv()) {
            settUniktFelt(modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap) {
                if (modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.beloepIValuta().erPositiv()) {
                    (modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta()
                        / modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.beloepIValuta()) * 100 medAntallDesimaler 2
                } else {
                    BigDecimal(100)
                }
            }
        }

        hvis(
            modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt lik 0
                || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt stoerreEllerLik 0
        ) {
            settUniktFelt(modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap) {
                BigDecimal.ZERO
            }
        }
    }

    val samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernetKalkyle = kalkyle("samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernet) {
            (modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret.beloepIValuta() +
                modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletOekningAvEgenkapital -
                modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletReduksjonAvEgenkapital +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletOekningAvEgenkapital -
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletReduksjonAvEgenkapital).somHeltall()
        }
    }

    val samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernetKalkyle = kalkyle("samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernet) {
            (modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.beloepIValuta() +
                modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletOekningAvBalansesum -
                modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletReduksjonAvBalansesum +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletOekningAvBalansesum -
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletReduksjonAvBalansesum).somHeltall()
        }
    }

    val egenkapitalandelForSelskapetEllerNorskDelAvKonsernetKalkyle = kalkyle("egenkapitalandelForSelskapetEllerNorskDelAvKonsernet") {
        hvis(modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernet ulik 0
            && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernet.harVerdi()
            && modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernet ulik 0
        ) {
            settUniktFelt(modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet) {
                (modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernet / modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernet) * 100 medAntallDesimaler 2 medMinimumsverdi 0
            }
        }
    }

    val samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper = kalkyle("samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper") {
        settUniktFelt(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletOekningAvEgenkapital) {
            (forekomsterAv(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipperPerKonto) summerVerdiFraHverForekomst {
                forekomstType.positivEffektPaaEgenkapital.beloepIValuta()
            }).somHeltall()
        }
    }

    val samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper = kalkyle("samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper") {
        settUniktFelt(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletReduksjonAvEgenkapital) {
            (forekomsterAv(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipperPerKonto) summerVerdiFraHverForekomst {
                forekomstType.negativEffektPaaEgenkapital.beloepIValuta()
            }).somHeltall()
        }
    }

    val samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper = kalkyle("samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper") {
        settUniktFelt(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletOekningAvBalansesum) {
            (forekomsterAv(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipperPerKonto) summerVerdiFraHverForekomst {
                forekomstType.positivEffektPaaBalansesum.beloepIValuta()
            }).somHeltall()
        }
    }

    val samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper = kalkyle("samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper") {
        settUniktFelt(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_samletReduksjonAvBalansesum) {
            (forekomsterAv(modell.unntakForRentebegrensning.spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper_spesifikasjonAvOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipperPerKonto) summerVerdiFraHverForekomst {
                forekomstType.negativEffektPaaBalansesum.beloepIValuta()
            }).somHeltall()
        }
    }

    val samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet = kalkyle("samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletOekningAvEgenkapital) {
            (modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_positivForretningsverdi.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_merverdiFraKonsernregnskap.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_utsattSkatteforpliktelseKnyttetTilMerverdi.beloepIValuta()
            ).somHeltall()
        }
    }

    val samletReduksjonAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet = kalkyle("samletReduksjonAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletReduksjonAvEgenkapital) {
            (modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_negativForretningsverdi.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_mindreverdiFraKonsernregnskap.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_utsattSkattefordelKnyttetTilMindreverdi.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_aksjeOgAndel.beloepIValuta()
                ).somHeltall()
        }
    }

    val samletOekningAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet = kalkyle("samletOekningAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletOekningAvBalansesum) {
            (modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_positivForretningsverdi.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_merverdiFraKonsernregnskap.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_gjeldVerdsattHoeyereIKonsernregnskap.beloepIValuta()
                ).somHeltall()
        }
    }

    val samletReduksjonAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet = kalkyle("samletReduksjonAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet") {
        settUniktFelt(modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_samletReduksjonAvBalansesum) {
            (modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_negativForretningsverdi.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_mindreverdiFraKonsernregnskap.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_gjeldVerdsattLavereIKonsernregnskap.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_aksjeOgAndel.beloepIValuta() +
                modell.unntakForRentebegrensning.justeringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_fordringMotSelskapMvSomErKonsolidertLinjeForLinjeIKonsernregnskapet.beloepIValuta()
                ).somHeltall()
        }
    }

    private fun lavesteTallAv(vararg args: BigDecimal?): BigDecimal? = if (args.contains(null)) {
        null
    } else {
        args.toList().filterNotNull().minOrNull()
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletRentekostnadOgGarantiprovisjonKalkyle,
            andelAvRentekostnadSelskapMvISammeKonsernKalkyle,
            andelAvRentekostnadAnnenNaerstaaendePartKalkyle,
            nettoRentekostnadKalkyle,
            totalRentekostnadBetaltTilSelskapMvISammeKonsernKalkyle,
            totalRenteinntektMottattFraSelskapMvISammeKonsernKalkyle,
            totalRentekostnadBetaltTilAnnenNaerstaaendePartKalkyle,
            totalRenteinntektMottattFraAnnenNaerstaaendePartKalkyle,
            totalNettoRentekostnadTilSelskapISammeKonsernKalkyle,
            nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsernKalkyle,
            totalNettoRentekostnadTilNaerstaaendeMvKalkyle,
            samletMottattKonsernbidrag,
            sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap,
            fradragForKonsernbidragSomSkalEkskluderes,
            grunnlagForRentefradragsrammeKalkyle,
            nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntektKalkyle,
            rentefradragsrammeKalkyle,
            differanseMellomNettoRentekostnadOgRentefradragsrammenKalkyle,
            tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle,
            tilbakefoertTilleggIInntektForSelskapIKonsernMvKalkyle,
            korrigertRentestoerrelse,
            tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaendeKalkyle,
            aaretsTilleggIInntektKalkyle,
            aaretsFradragIInntektKalkyle,
            tilleggEllerFradragIInntektSomFoelgeAvRentebegrensningKalkyle,
            fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradragKalkyle,
            fremfoerbartRentefradragIInntektIInntektsaaret,
            aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle,
            fremfoerbartRentefradragIInntektTidligereAar,
            fremfoerbarRentekostnadKalkyle,
            egenkapitalandelIKonsernregnskapKalkyle,
            samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletReduksjonAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletOekningAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletReduksjonAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernetKalkyle,
            samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernetKalkyle,
            egenkapitalandelForSelskapetEllerNorskDelAvKonsernetKalkyle,
        )
    }
}