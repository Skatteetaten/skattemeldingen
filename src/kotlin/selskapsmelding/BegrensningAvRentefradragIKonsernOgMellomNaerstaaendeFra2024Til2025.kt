package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV4

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaendeFra2024Til2025  : HarKalkylesamling  {

    val modell = modellV4
    private const val UNNTAKSREGEL_PAA_SELSKAPSNIVAA = "unntaksregelPaaSelskapsnivaa"
    private const val UNNTAKSREGEL_PAA_NASJONALTNIVAA = "unntaksregelPaaNasjonaltNivaa"

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
            modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_positivEgenkapitalIKonsernregnskapGlobalt.beloepIValuta() lik 0
             || modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_negativEgenkapitalIKonsernregnskapGlobalt.beloepIValuta() stoerreEllerLik 0
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
                no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_balansesumIKonsernregnskapGlobalt.harVerdi()
            val erHelnorskKonsern = modell.unntakForRentebegrensning.anvendtUnntaksregel_erHelnorskKonsern.erSann()
            val harSelskapetErInkludertIAnnenInnrapportering = modell.unntakForRentebegrensning.anvendtUnntaksregel_selskapetErInkludertIAnnenInnrapportering.erSann()
            val harIdentifikator = modell.unntakForRentebegrensning.anvendtUnntaksregel_annetSelskapSomRapportererPaaVegneAvKonsern_identifikator.harVerdi()
            val harBalansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret = modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_balansesumVedUtgangenAvRegnskapsaaretFoerInntektsaaret.harVerdi()
            val egenkapitalandelForSelskapetEllerNorskDelAvKonsernetSoerst =
                (modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + 2) stoerreEllerLik no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap
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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletRentekostnadOgGarantiprovisjonKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.andelAvRentekostnadSelskapMvISammeKonsernKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.andelAvRentekostnadAnnenNaerstaaendePartKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRentekostnadBetaltTilSelskapMvISammeKonsernKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRenteinntektMottattFraSelskapMvISammeKonsernKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRentekostnadBetaltTilAnnenNaerstaaendePartKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRenteinntektMottattFraAnnenNaerstaaendePartKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalNettoRentekostnadTilSelskapISammeKonsernKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsernKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalNettoRentekostnadTilNaerstaaendeMvKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletMottattKonsernbidrag,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.sumAndelAvMottattKonsernbidragSomErAvgittTilAnnetSelskap,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fradragForKonsernbidragSomSkalEkskluderes,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.grunnlagForRentefradragsrammeKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntektKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.rentefradragsrammeKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.differanseMellomNettoRentekostnadOgRentefradragsrammenKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle,
            egenkapitalandelIKonsernregnskapKalkyle,
            samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletReduksjonAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernetKalkyle,
            samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletOekningAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletReduksjonAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernetKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.egenkapitalandelForSelskapetEllerNorskDelAvKonsernetKalkyle,
            tilbakefoertTilleggIInntektForSelskapIKonsernMvKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.korrigertRentestoerrelse,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaendeKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsTilleggIInntektKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsFradragIInntektKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggEllerFradragIInntektSomFoelgeAvRentebegrensningKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradragKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.justeringForSdfEllerNokusKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektIInntektsaaret,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektTidligereAar,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbarRentekostnadKalkyle,
        )
    }
}