package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsAnvendelseAvFremfoertRentefradragFraTidligereAarKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsFradragIInntektKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.aaretsTilleggIInntektKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.andelAvRentekostnadAnnenNaerstaaendePartKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.andelAvRentekostnadSelskapMvISammeKonsernKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.differanseMellomNettoRentekostnadOgRentefradragsrammenKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.egenkapitalandelForSelskapetEllerNorskDelAvKonsernetKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fradragForKonsernbidragSomSkalEkskluderes
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbarRentekostnadKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektIInntektsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoerbartRentefradragIInntektTidligereAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.fremfoertRentefradragFraTidligereAarInnenforAaretsTillatteRentefradragKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.grunnlagForRentefradragsrammeKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.justeringForSdfEllerNokusKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.korrigertRentestoerrelse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadBeregningsgrunnlagTilleggEllerFradragIInntektKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.nettoRentekostnadTilAnnenNaerstaaendePartUtenforKonsernKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.rentefradragsrammeKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletMottattKonsernbidrag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletRentekostnadOgGarantiprovisjonKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.sumAndelAvMottattKonsernbidragSomErAvgittTilAnnetSelskap
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilbakefoertTilleggIInntektForSelskapIKonsernMvKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggEllerFradragIInntektSomFoelgeAvRentebegrensningKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggIInntektForSelskapIKonsernMvSomFaarAvskaaretFradragForRenterTilAndreNaerstaaendeKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.tilleggIInntektSomFoelgeAvAtNettoRentekostnadOverstigerRentefradragsrammenKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalNettoRentekostnadTilNaerstaaendeMvKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalNettoRentekostnadTilSelskapISammeKonsernKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRenteinntektMottattFraAnnenNaerstaaendePartKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRenteinntektMottattFraSelskapMvISammeKonsernKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRentekostnadBetaltTilAnnenNaerstaaendePartKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.totalRentekostnadBetaltTilSelskapMvISammeKonsernKalkyle
import java.math.BigDecimal

val modell = no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.selskapsmelding.sdf.domenemodell.v4_2025.v4

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaendeFra2024Til2025  : HarKalkylesamling  {

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
            sumAndelAvMottattKonsernbidragSomErAvgittTilAnnetSelskap,
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
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.egenkapitalandelIKonsernregnskapKalkyle,
            samletOekningAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvEgenkapitalOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletOekningAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            samletReduksjonAvBalansesumOmarbeidelseTilKonsernregnskapetsRegnskapsprinsipper,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletOekningAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletReduksjonAvEgenkapitalJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletOekningAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletReduksjonAvBalansesumJusteringAvSelskapsregnskapetEllerKonsolidertBalanseForNorskDelAvKonsernet,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletKorrigertEgenkapitalISelskapetEllerNorskDelAvKonsernetKalkyle,
            BegrensningAvRentefradragIKonsernOgMellomNaerstaaende.samletKorrigertBalansesumISelskapetEllerNorskDelAvKonsernetKalkyle,
            egenkapitalandelForSelskapetEllerNorskDelAvKonsernetKalkyle,
        )
    }
}