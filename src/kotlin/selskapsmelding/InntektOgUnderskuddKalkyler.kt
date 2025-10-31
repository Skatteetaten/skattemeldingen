package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.Deltakerrolle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.harDeltakerOmfattetAvRederiskatteordning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object InntektOgUnderskuddKalkyler : HarKalkylesamling {

    internal val nettoSkattefriInntektEllerIkkefradragsberettigetKostnadKalkyle = kalkyle {
        val inntektEllerKostnad = modell.inntektOgUnderskudd.skattefriInntekt_aksjeutbytte +
            modell.inntektOgUnderskudd.skattefriInntekt_gevinstVedRealisasjonAvAksjeAndelMv +
            modell.inntektOgUnderskudd.skattefriInntekt_annenInntekt +
            modell.inntektOgUnderskudd.skattefriInntekt_andelAvInntektIUnderliggendeSelskapMedDeltakerfastsetting -
            modell.inntektOgUnderskudd.ikkefradragsberettigetKostnad_tapVedRealisasjonAvAksjeAndelMv -
            modell.inntektOgUnderskudd.ikkefradragsberettigetKostnad_tilbakefoertInntektEtterFritaksmetoden -
            modell.inntektOgUnderskudd.ikkefradragsberettigetKostnad_annenKostnad -
            modell.inntektOgUnderskudd.ikkefradragsberettigetKostnad_andelAvKostnadIUnderliggendeSelskapMedDeltakerfastsetting

        if (inntektEllerKostnad stoerreEllerLik 0) {
            settUniktFelt(modell.inntektOgUnderskudd.nettoSkattefriInntekt) { inntektEllerKostnad.somHeltall() }
        } else {
            settUniktFelt(modell.inntektOgUnderskudd.nettoIkkefradragsberettigetKostnad) {
                inntektEllerKostnad.somHeltall().absoluttverdi()
            }
        }
    }

    internal val samletInntektEllerUnderskudd = kalkyle("samletInntektEllerUnderskudd") {
        val inntektEllerUnderskudd = modell.inntektOgUnderskudd.naeringsinntekt -
            modell.inntektOgUnderskudd.underskudd
        hvis (inntektEllerUnderskudd.harVerdi()) {
            val tilleggOgFradragPgaRentebegrensning = forekomsterAv(modell.rentebegrensning) der {
                forekomstType.deltakerrolle likEnAv listOf(Deltakerrolle.ordinaerDeltaker, Deltakerrolle.annenDeltakerINokus)
            } summerVerdiFraHverForekomst {
                forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt -
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt
            }
            val inntektEllerUnderskuddPlussTilleggOgFradragPgaRentebegrensning = inntektEllerUnderskudd +
                tilleggOgFradragPgaRentebegrensning

            if (inntektEllerUnderskuddPlussTilleggOgFradragPgaRentebegrensning stoerreEllerLik 0) {
                settUniktFelt(modell.inntektOgUnderskudd.samletInntekt) {
                    inntektEllerUnderskuddPlussTilleggOgFradragPgaRentebegrensning
                }
            } else {
                settUniktFelt(modell.inntektOgUnderskudd.samletUnderskudd) {
                    inntektEllerUnderskuddPlussTilleggOgFradragPgaRentebegrensning.absoluttverdi()
                }
            }
        }
    }

    internal val samletInntektEllerUnderskuddForSelskapMedDeltakerIRederiskatteordning =
        kalkyle("samletInntektEllerUnderskuddForSelskapMedDeltakerIRederiskatteordning") {
            hvis(harDeltakerOmfattetAvRederiskatteordning()) {
                val tilleggOgFradragPgaRentebegrensning = forekomsterAv(modell.rentebegrensning) der {
                    forekomstType.deltakerrolle lik Deltakerrolle.deltakerErRederi
                } summerVerdiFraHverForekomst {
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt -
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt
                }
                val beregnetFinansinntekt = modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt -
                    modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd +
                    tilleggOgFradragPgaRentebegrensning

                if (beregnetFinansinntekt stoerreEllerLik 0) {
                    settUniktFelt(modell.inntektOgUnderskudd.samletInntektForSelskapMedDeltakerIRederiskatteordning) { beregnetFinansinntekt }
                } else {
                    settUniktFelt(modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedDeltakerIRederiskatteordning) { beregnetFinansinntekt.absoluttverdi() }
                }
            }
        }

    internal val samletInntektEllerUnderskuddForSelskapMedDeltakerINokus =
        kalkyle("samletInntektEllerUnderskuddForSelskapMedDeltakerINokus") {
            hvis(erNokus()) {
                val tilleggOgFradragPgaRentebegrensning = forekomsterAv(modell.rentebegrensning) der {
                    forekomstType.deltakerrolle lik Deltakerrolle.personligDeltakerINokus
                } summerVerdiFraHverForekomst {
                    forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsTilleggIInntekt -
                        forekomstType.beregningsgrunnlagTilleggEllerFradragIInntekt_aaretsFradragIInntekt
                }
                val inntektEllerUnderskudd = modell.inntektOgUnderskudd.inntektOgUnderskuddForSelskapMedPersonligDeltakerINokus_naeringsinntekt -
                    modell.inntektOgUnderskudd.inntektOgUnderskuddForSelskapMedPersonligDeltakerINokus_underskudd +
                    tilleggOgFradragPgaRentebegrensning

                if (inntektEllerUnderskudd stoerreEllerLik 0) {
                    settUniktFelt(modell.inntektOgUnderskudd.samletInntektForSelskapMedPersonligDeltakerINokus) { inntektEllerUnderskudd }
                } else {
                    settUniktFelt(modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedPersonligDeltakerINokus) { inntektEllerUnderskudd.absoluttverdi() }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            nettoSkattefriInntektEllerIkkefradragsberettigetKostnadKalkyle,
            samletInntektEllerUnderskudd,
            samletInntektEllerUnderskuddForSelskapMedDeltakerIRederiskatteordning,
            samletInntektEllerUnderskuddForSelskapMedDeltakerINokus
        )
    }
}