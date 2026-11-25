package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object OpplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse : HarKalkylesamling {

    internal val alminneligInntektEllerUnderskuddEtterKorrigeringForKundemidler = kalkyle("alminneligInntektEllerUnderskuddEtterKorrigeringForKundemidler") {
        forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
            forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_reduksjonIAndelAvAlminneligInntektForAndelKnyttetTilKundemidler.harVerdi() ||
                forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_tilleggIAndelAvAlminneligInntektForAndelKnyttetTilKundemidler.harVerdi()
        } forHverForekomst {
            val inntektEllerUnderskudd = forekomstType.alminneligInntekt -
                forekomstType.underskudd -
                forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_reduksjonIAndelAvAlminneligInntektForAndelKnyttetTilKundemidler +
                forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_tilleggIAndelAvAlminneligInntektForAndelKnyttetTilKundemidler

            if (inntektEllerUnderskudd stoerreEllerLik 0) {
                settFelt(forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_alminneligInntektEtterKorrigeringForKundemidler) {
                    inntektEllerUnderskudd
                }
            } else {
                settFelt(forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_underskuddEtterKorrigeringForKundemidler) {
                    inntektEllerUnderskudd.absoluttverdi()
                }
            }
        }
    }

    internal val utbetalingMvTilDeltakerEtterKorrigeringForKundemidler = kalkyle("utbetalingMvTilDeltakerEtterKorrigeringForKundemidler") {
        forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
            forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_reduksjonIUtbetalingMvTilDeltakerForAndelKnyttetTilKundemidler.harVerdi() ||
                forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_tilleggIUtbetalingMvTilDeltakerForAndelKnyttetTilKundemidler.harVerdi()
        } forHverForekomst {
            settFelt(forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_utbetalingMvTilDeltakerEtterKorrigeringForKundemidler) {
                forekomstType.utbetalingMvTilDeltaker -
                    forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_reduksjonIUtbetalingMvTilDeltakerForAndelKnyttetTilKundemidler +
                    forekomstType.opplysningerForDeltakerSomErLivsforsikringsforetakEllerPensjonskasse_tilleggIUtbetalingMvTilDeltakerForAndelKnyttetTilKundemidler
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            alminneligInntektEllerUnderskuddEtterKorrigeringForKundemidler,
            utbetalingMvTilDeltakerEtterKorrigeringForKundemidler,
        )
    }
}