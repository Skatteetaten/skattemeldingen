@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.Deltakerrolle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erSdf
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi.RederiUtil
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi.RederiUtil.skalBeregneRederi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

private const val UNNTAKSREGEL_PAA_SELSKAPSNIVAA = "unntaksregelPaaSelskapsnivaa"
private const val UNNTAKSREGEL_PAA_NASJONALTNIVAA = "unntaksregelPaaNasjonaltNivaa"

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaende : HarKalkylesamling {

    val samletRentekostnadOgGarantiprovisjonKalkyle = kalkyle("samletRentekostnadOgGarantiprovisjon") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val samletRentekostnadOgGarantiprovisjon =
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRentekostnad +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_garantiprovisjonForGjeld

            hvis(
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedInngangenTilInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedUtgangenAvInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret.harVerdi()
            ) {
                settFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletRentekostnadOgGarantiprovisjon) {
                    samletRentekostnadOgGarantiprovisjon
                }
            }

            hvis(
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedInngangenTilInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedUtgangenAvInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret.harVerdi()
                    || forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret.harVerdi()
            ) {
                settFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletRentekostnadOgGarantiprovisjon) {
                    samletRentekostnadOgGarantiprovisjon
                }
            }
        }
    }

    val andelAvRentekostnadSelskapMvISammeKonsernKalkyle = kalkyle("andelAvRentekostnadSelskapMvISammeKonsern") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val samletGjeldTilNaerstaaende =
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret +
                    forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret
            val samletGjeld =
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedInngangenTilInntektsaaret +
                    forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletGjeldVedUtgangenAvInntektsaaret
            val rentekostnad = forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_samletRentekostnadOgGarantiprovisjon
            hvis(samletGjeldTilNaerstaaende.harVerdi() && samletGjeld.erPositiv() && rentekostnad.harVerdi()) {
                settFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilSelskapMvISammeIKonsern_andelAvRentekostnad) {
                    ((samletGjeldTilNaerstaaende / samletGjeld) * rentekostnad).somHeltall()
                }
            }
        }
    }

    val andelAvRentekostnadAnnenNaerstaaendePartKalkyle = kalkyle("andelAvRentekostnadAnnenNaerstaaendePart") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val samletGjeldTilNaerstaaende =
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedInngangenTilInntektsaaret +
                    forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldTilNaerstaaendeVedUtgangenAvInntektsaaret
            val samletGjeld =
                forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedInngangenTilInntektsaaret +
                    forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletGjeldVedUtgangenAvInntektsaaret
            val rentekostnad = forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_samletRentekostnadOgGarantiprovisjon
            hvis(samletGjeldTilNaerstaaende.harVerdi() && samletGjeld.erPositiv() && rentekostnad.harVerdi()) {
                settFelt(forekomstType.filialensRentekostnadOgGarantiprovisjonTilAnnenNaerstaaendePartUtenforKonsern_andelAvRentekostnad) {
                    ((samletGjeldTilNaerstaaende / samletGjeld) * rentekostnad).somHeltall()
                }
            }
        }
    }

    val nettoRentekostnadKalkyle = kalkyle("nettoRentekostnad") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad) {
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRentekostnad +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_garantiprovisjonForGjeld -
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_totalRenteinntekt -
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_gevinstVedRealisasjonAvOverEllerUnderkursobligasjon +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_tapVedRealisasjonAvOverEllerUnderkursobligasjon -
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_gevinstVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_tapVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres
            }
        }
    }

    val totalRentekostnadBetaltTilSelskapMvISammeKonsernKalkyle = kalkyle("totalRentekostnadBetaltTilSelskapMvISammeKonsern") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilSelskapMvISammeKonsern) {
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
    }

    val totalRenteinntektMottattFraSelskapMvISammeKonsernKalkyle = kalkyle("totalRenteinntektMottattFraSelskapMvISammeKonsern") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraSelskapMvISammeKonsern) {
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
    }

    val totalRentekostnadBetaltTilAnnenNaerstaaendePartKalkyle = kalkyle("totalRentekostnadBetaltTilAnnenNaerstaaendePart") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilAnnenNaerstaaendePart) {
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
    }

    val totalRenteinntektMottattFraAnnenNaerstaaendePartKalkyle = kalkyle("totalRenteinntektMottattFraAnnenNaerstaaendePart") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraAnnenNaerstaaendePart) {
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
    }

    val totalNettoRentekostnadTilSelskapISammeKonsernKalkyle = kalkyle("totalNettoRentekostnadTilSelskapISammeKonsern") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern) {
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilSelskapMvISammeKonsern -
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraSelskapMvISammeKonsern
            }
        }
    }

    val nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsernKalkyle = kalkyle("nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern) {
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRentekostnadBetaltTilAnnenNaerstaaendePart -
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalRenteinntektMottattFraAnnenNaerstaaendePart
            }
        }
    }

    val totalNettoRentekostnadTilNaerstaaendeMvKalkyle = kalkyle("totalNettoRentekostnadTilNaerstaaendeMv") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnaderTilNaerstaaendeMv) {
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern
            }
        }
    }

    val grunnlagForRentefradragsrammeKalkyle = kalkyle("grunnlagForRentefradragsramme") {
        val erSdf = erSdf()
        val erNokus = erNokus()

        val naeringsinntekt = modell.inntektOgUnderskudd.naeringsinntekt.tall()
        val underskudd = modell.inntektOgUnderskudd.underskudd.tall()
        val samletFinansinntekt = modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt.tall()
        val samletFinansunderskudd = modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd.tall()
        val inntektForSelskapMedPersonligDeltakerINokus = modell.inntektOgUnderskudd.inntektOgUnderskuddForSelskapMedPersonligDeltakerINokus_naeringsinntekt.tall()
        val underskuddForSelskapMedPersonligDeltakerINokus = modell.inntektOgUnderskudd.inntektOgUnderskuddForSelskapMedPersonligDeltakerINokus_underskudd.tall()

        forekomsterAv(modell.rentebegrensning) der {
            erSdf || (erNokus && forekomstType.deltakerrolle ulik Deltakerrolle.ordinaerDeltaker)
        } forHverForekomst {
            val inntektEllerUnderskudd = if (erNokus && forekomstType.deltakerrolle lik Deltakerrolle.personligDeltakerINokus) {
                inntektForSelskapMedPersonligDeltakerINokus - underskuddForSelskapMedPersonligDeltakerINokus
            } else if (forekomstType.deltakerrolle lik Deltakerrolle.deltakerErRederi) {
                samletFinansinntekt - samletFinansunderskudd
            } else {
                naeringsinntekt - underskudd
            }
            val tilleggForSkattemessigAvskrivning = if (forekomstType.deltakerrolle lik Deltakerrolle.deltakerErRederi) {
                null
            } else {
                forekomstType.beregningsgrunnlagForRentefradragsramme_tilleggForSkattemessigAvskrivning.tall()
            }
            settFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme) {
                inntektEllerUnderskudd +
                    tilleggForSkattemessigAvskrivning -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_periodisertLeiekostnad -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel +
                    forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad
            }
        }
    }

    val nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntektKalkyle = kalkyle("nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntekt") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad) {
                forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad.tall() medMinimumsverdi 0
            }
        }
    }

    val rentefradragsrammeKalkyle = kalkyle("rentefradragsramme") {
        val satser = satser!!
        forAlleForekomsterAv(modell.rentebegrensning) {
            hvis(forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme.erPositiv()) {
                settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme) {
                    (forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme * satser.sats(
                        Sats.rentebegrensning_fradragsrammeProsent
                    )).somHeltall()
                }
            }
            hvis(
                forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme.erNegativ() ||
                    forekomstType.beregningsgrunnlagForRentefradragsramme_grunnlagForRentefradragsramme lik 0
            ) {
                settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme) {
                    BigDecimal.ZERO
                }
            }
        }
    }

    val differanseMellomNettoRentekostnadOgRentefradragsrammenKalkyle = kalkyle("differanseMellomNettoRentekostnadOgRentefradragsrammen") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen) {
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme
            }
        }
    }

    val tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle =
        kalkyle("tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle") {
            forAlleForekomsterAv(modell.rentebegrensning) {
                hvis(forekomstType.erKonsernIhtRegelverkForRentebegrensning.erUsann()) {
                    settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme) {
                        if (forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad mindreEllerLik this@kalkyle.satser!!.sats(
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
                    settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme) {
                        if (
                            forekomstType.harRentekostnadLavereEnnTerskelbeloepPerNorskDelAvKonsern.erSann() &&
                            ((forekomstType.annetForetakRapportererPaaVegneAvKonsern.erSann() && forekomstType.annetForetakSomRapportererPaaVegneAvKonsern_identifikator.harVerdi()) ||
                                (forekomstType.annetForetakRapportererPaaVegneAvKonsern.erUsann() &&
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
        }

    val tilbakefoertTilleggIInntektForSelskapIKonsernMvKalkyle =
        kalkyle("tilbakefoertTilleggIInntektForSelskapIKonsernMv") {
            val harUnntakForRentebegrensningSkalBekreftesAvRevisor =
                modell.unntakForRentebegrensning.unntakForRentebegrensningSkalBekreftesAvRevisor.erSann()
            val harUnntaksregelPaaSelskapsnivaa =
                modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.verdi() == UNNTAKSREGEL_PAA_SELSKAPSNIVAA
            val harUnntaksregelPaaNasjonaltNivaa =
                modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.verdi() == UNNTAKSREGEL_PAA_NASJONALTNIVAA
            val harEgenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret =
                modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
            val balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret =
                modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
            val harPositivEllerNegativEgenkapitalIKonsernregnskapGlobalt = (
                modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.harVerdi()
                    || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt.harVerdi()
                )
            val harBalansesumIKonsernregnskapGlobalt =
                modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.harVerdi()
            val erHelnorskKonsern = modell.unntakForRentebegrensning.anvendtUnntaksregel_erHelnorskKonsern.erSann()
            val harSelskapetErInkludertIAnnenInnrapportering = modell.unntakForRentebegrensning.anvendtUnntaksregel_selskapetErInkludertIAnnenInnrapportering.erSann()
            val harIdentifikator = modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapSomRapportererPaaVegneAvKonsern_identifikator.harVerdi()
            val harBalansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret = modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
            val egenkapitalandelForSelskapetEllerNorskDelAvKonsernetSoerst = ((modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + BigDecimal(
            2) stoerreEllerLik modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap.tall()))
            val harAnnetSelskapINorskDelAvEllerInnenlandskKonsernSelskapetsOrganisasjonsnummer = finnForekomsterMed(modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapINorskDelAvEllerInnenlandskKonsern) {
                forekomstType.selskapetsOrganisasjonsnummer.harVerdi()
            }.isNotEmpty()

            forAlleForekomsterAv(modell.rentebegrensning) {
                hvis(
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.erPositiv()
                        && forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                        && harUnntakForRentebegrensningSkalBekreftesAvRevisor
                        && harUnntaksregelPaaSelskapsnivaa
                        && harEgenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret
                        && balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret
                        && harPositivEllerNegativEgenkapitalIKonsernregnskapGlobalt
                        && harBalansesumIKonsernregnskapGlobalt
                        && egenkapitalandelForSelskapetEllerNorskDelAvKonsernetSoerst
                ) {
                    settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                    }
                }
                hvis(
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.erPositiv()
                        && forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                        && harUnntakForRentebegrensningSkalBekreftesAvRevisor
                        && harUnntaksregelPaaNasjonaltNivaa
                ) {
                    hvis(
                        erHelnorskKonsern
                            && !harSelskapetErInkludertIAnnenInnrapportering
                            && harAnnetSelskapINorskDelAvEllerInnenlandskKonsernSelskapetsOrganisasjonsnummer
                    ) {
                        settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                        }
                    }
                    hvis(
                        harSelskapetErInkludertIAnnenInnrapportering
                            && harIdentifikator
                    ) {
                        settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                        }
                    }
                    hvis(
                        !erHelnorskKonsern
                            && !harSelskapetErInkludertIAnnenInnrapportering
                            && harAnnetSelskapINorskDelAvEllerInnenlandskKonsernSelskapetsOrganisasjonsnummer
                            && harEgenkapitalVedUtgangenAvRegnskapsaaretFoerInntektsaaret
                            && harBalansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret
                            && harPositivEllerNegativEgenkapitalIKonsernregnskapGlobalt
                            && harBalansesumIKonsernregnskapGlobalt
                            && egenkapitalandelForSelskapetEllerNorskDelAvKonsernetSoerst
                    ) {
                        settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                        }
                    }
                }
            }
        }

    val korrigertRentestoerrelse = kalkyle("korrigertRentestoerrelse") {
        var harUnntaksregel = modell.unntakForRentebegrensning.anvendtUnntaksregel_unntaksregeltype.harVerdi()
        forAlleForekomsterAv(modell.rentebegrensning) {
            hvis(
                forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann() &&
                    (forekomstType.harRentekostnadLavereEnnTerskelbeloepPerNorskDelAvKonsern.erSann() &&
                        (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme lik 0 ||
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.harIkkeVerdi())) ||
                        (harUnntaksregel &&
                                (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv.harVerdi() ||
                                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme lik 0)
                                )
            ) {
                var verdi =
                    modell.rentebegrensning.grunnlagForBeregningAvSelskapetsNettoRentekostnad_nettoRentekostnad.tall()
                val totalNettoRentekostnadTilSelskapISammeKonsern =
                    modell.rentebegrensning.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_totalNettoRentekostnadTilSelskapISammeKonsern.tall()

                if (totalNettoRentekostnadTilSelskapISammeKonsern.erNegativ()) {
                    verdi -= totalNettoRentekostnadTilSelskapISammeKonsern
                }
                hvis(verdi.erPositiv()) {
                    settFelt(modell.rentebegrensning.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse) {
                        verdi
                    }
                }
            }
        }
    }

    val tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaendeKalkyle =
        kalkyle("tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende") {
            val satser = satser!!
            forAlleForekomsterAv(modell.rentebegrensning) {
                val skalSetteFelt = forekomstType.erKonsernIhtRegelverkForRentebegrensning.erSann()
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse.harVerdi()
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse stoerreEnn satser.sats(
                    Sats.rentebegrensning_grensebeloepUtenforKonsern
                )
                    && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse stoerreEnn forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme.tall()
                    && forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern.erPositiv()
                hvis(skalSetteFelt) {
                    settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende) {
                        lavesteTallAv(
                            forekomstType.grunnlagForBeregningAvSelskapetsNettoRentekostnadTilNaerstaaendeMv_nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsern.tall(),
                            (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_korrigertRentestoerrelse - forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_rentefradragsramme)
                        )
                    }
                }
            }
        }

    val aaretsTilleggIInntektKalkyle = kalkyle("aaretsTilleggIInntekt") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt) {
                (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv +
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaende) medMinimumsverdi 0
            }
        }
    }

    val aaretsFradragIInntektKalkyle = kalkyle("aaretsFradragIInntekt") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            hvis(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen mindreEllerLik 0 &&
                (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt lik 0 ||
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt.harIkkeVerdi())) {
                val sumFremfoertRentefradragFraTidligereAar =
                    forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
                        forekomstType.fremfoertRentefradragFraTidligereAar.tall()
                    }
                hvis(sumFremfoertRentefradragFraTidligereAar.erPositiv()) {
                    settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt) {
                        lavesteTallAv(
                            forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_differanseMellomNettoRentekostnadOgRentefradragsrammen.tall()
                                .absoluttverdi(),
                            sumFremfoertRentefradragFraTidligereAar
                        )
                    }
                }
            }
        }
    }

    val tilleggEllerFradragIInntektSomFoelgeAvRentebegrensningKalkyle = kalkyle("tilleggIInntektSomFoelgeAvRentebegrensning") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning =
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt
            if (tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning.erPositiv()) {
                settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning) {
                    tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning
                }
            } else {
                settFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning) {
                    tilleggEllerFradragIInntektSomFoelgeAvRentebegrensning.absoluttverdi()
                }
            }
        }
    }

    val fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradragKalkyle = kalkyle("fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val sumFremfoertRentefradragFraTidligereAar =
                forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
                    forekomstType.fremfoertRentefradragFraTidligereAar.tall()
                }
            hvis(sumFremfoertRentefradragFraTidligereAar.erPositiv() && forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad.stoerreEllerLik(0)) {
                val nettoRentekostnad = (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_nettoRentekostnad -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvRentebegrensning +
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_fradragIInntektSomFoelgeAvRentebegrensning) medMinimumsverdi 0
                settFelt(forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag) {
                    lavesteTallAv(sumFremfoertRentefradragFraTidligereAar, nettoRentekostnad)
                }
            }
        }
    }

    val justeringForSdfEllerNokusKalkyle = kalkyle("justeringForSdfEllerNokus") {
        hvis(erSdf() || erNokus()) {
            var naeringsinntekt = modell.inntektOgUnderskudd.naeringsinntekt.tall()
            var underskudd = modell.inntektOgUnderskudd.underskudd.tall()
            hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
                naeringsinntekt =
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt.tall()
                underskudd =
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd.tall()
            }

            val satser = satser!!
            forAlleForekomsterAv(modell.rentebegrensning) {
                val inntektEllerUnderskudd =
                    naeringsinntekt -
                        underskudd +
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt

                var justertVerdiForSdfEllerNokus =
                    (inntektEllerUnderskudd * satser.sats(Sats.rentebegrensning_underskuddSdfOgNokusProsent)).somHeltall()
                        .absoluttverdi()
                if (justertVerdiForSdfEllerNokus stoerreEnn forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt.tall()) {
                    justertVerdiForSdfEllerNokus =
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt.tall()
                }
                hvis(inntektEllerUnderskudd.erNegativ()) {
                    settFelt(forekomstType.rentefradragTilFremfoeringIInntektsaaret_justeringForSdfEllerNokus) {
                        justertVerdiForSdfEllerNokus
                    }
                }
            }
        }
    }

    val fremfoerbartRentefradragIInntektIInntektsaaret = kalkyle("fremfoerbartRentefradragIInntektIInntektsaaret") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoerbartRentefradragIInntekt) {
                (forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt +
                    forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt -
                    forekomstType.rentefradragTilFremfoeringIInntektsaaret_justeringForSdfEllerNokus) medMinimumsverdi 0
            }
        }
    }

    val aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle = kalkyle("aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            var resterendeFremfoertRentefradrag =
                forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradrag.tall()
            forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) transformerListe { liste ->
                liste.sortedBy { it.generiskModell.verdiFor(it.forekomstType.inntektsaar) }
            } forHverForekomst {
                hvis(resterendeFremfoertRentefradrag.erPositiv()) {
                    val aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar =
                        forekomstType.fremfoertRentefradragFraTidligereAar.tall() medMaksimumsverdi resterendeFremfoertRentefradrag
                    settFelt(forekomstType.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar) {
                        aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
                    }
                    resterendeFremfoertRentefradrag -= aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
                }
            }
        }
    }

    val fremfoerbartRentefradragIInntektTidligereAar = kalkyle("fremfoerbartRentefradragIInntektTidligereAar") {
        forAlleForekomsterAv(modell.rentebegrensning.rentefradragTilFremfoeringTidligereAar) {
            settFelt(forekomstType.fremfoerbartRentefradragIInntekt) {
                forekomstType.fremfoertRentefradragFraTidligereAar -
                    forekomstType.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAar
            }
        }
    }

    val fremfoerbarRentekostnadKalkyle = kalkyle("fremfoerbarRentekostnad") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            val sumFremfoerbarRentefradragIInntekt =
                forekomsterAv(forekomstType.rentefradragTilFremfoeringTidligereAar) summerVerdiFraHverForekomst {
                    forekomstType.fremfoerbartRentefradragIInntekt.tall()
                }
            settFelt(forekomstType.fremfoerbarRentekostnad) {
                sumFremfoerbarRentefradragIInntekt + forekomstType.rentefradragTilFremfoeringIInntektsaaret_fremfoerbartRentefradragIInntekt
            }
        }
    }

    val samletMottattKonsernbidrag = kalkyle("samletMottattKonsernbidrag") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_samletMottattKonsernbidrag) {
                forekomsterAv(forekomstType.beregningsgrunnlagForRentefradragsramme_konsernbidragPerMotpart) summerVerdiFraHverForekomst {
                    forekomstType.mottattBeloep.tall()
                }
            }
        }
    }

    val sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap =
        kalkyle("sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap") {
            forAlleForekomsterAv(modell.rentebegrensning) {
                settFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap) {
                    forekomsterAv(forekomstType.beregningsgrunnlagForRentefradragsramme_konsernbidragPerMotpart.spesifikasjonAvAvgittAndelAvKonsernbidrag) summerVerdiFraHverForekomst {
                        forekomstType.andelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap.tall()
                    }
                }
            }
        }

    val fradragForKonsernbidragSomSkalEkskluderes = kalkyle("fradragForKonsernbidragSomSkalEkskluderes") {
        forAlleForekomsterAv(modell.rentebegrensning) {
            settFelt(forekomstType.beregningsgrunnlagForRentefradragsramme_fradragForKonsernbidragSomSkalEkskluderes) {
                forekomstType.beregningsgrunnlagForRentefradragsramme_samletMottattKonsernbidrag -
                    forekomstType.beregningsgrunnlagForRentefradragsramme_sumAndelAvMotattKonsernbidragSomErAvgittTilAnnetSelskap
            }
        }
    }
    
    val egenkapitalandelIKonsernregnskapKalkyle = kalkyle("egenkapitalandelIKonsernregnskap") {
        hvis(modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta().erPositiv()) {
            settUniktFelt(modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap) {
                if (modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.beloepIValuta().erPositiv()) {
                    modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta() / modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.beloepIValuta() * 100 medAntallDesimaler 2
                } else {
                    BigDecimal(100)
                }
            }
        }

        hvis(
            modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta() lik 0 || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt.beloepIValuta() * 100 stoerreEllerLik 0
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

    val samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper =
        kalkyle("samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper") {
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

    val samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet =
        kalkyle("samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet") {
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
            justeringForSdfEllerNokusKalkyle,
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