package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV5
import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.UNNTAKSREGEL_PAA_NASJONALTNIVAA
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.UNNTAKSREGEL_PAA_SELSKAPSNIVAA
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.forekomstType

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaendeFra2024til2025 : HarKalkylesamling {

    val modell = modellV5

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
                    && ((modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + 2) stoerreEllerLik modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap)
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
                        && ((modell.unntakForRentebegrensning.opplysningerOmSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet_egenkapitalandelForSelskapetEllerNorskDelAvKonsernet + 2) stoerreEllerLik modell.unntakForRentebegrensning.konsernregnskapOgEgenkapitalandelIKonsern_egenkapitalandelIKonsernregnskap)
                ) {
                    settUniktFelt(forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilbakefoertTilleggIInntektForSelskapIKonsernMv) {
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsramme.tall().somHeltall()
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling =
        Kalkylesamling(
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
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektIInntektsaaret,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektTidligereAar,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbarRentekostnadKalkyle,
        )
    }