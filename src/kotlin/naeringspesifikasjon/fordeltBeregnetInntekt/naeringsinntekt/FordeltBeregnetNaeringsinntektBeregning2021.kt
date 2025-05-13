package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

/**
 * Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=489349748
 */
internal object FordeltBeregnetNaeringsinntektBeregning2021 : HarKalkylesamling {

    const val reindrift = "reindrift"

    internal val korrigertResultatKalkyle = kalkyle("korrigertResultat") {
        forekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) der {
            forekomstType.naeringstype lik reindrift
        } forHverForekomst {
            settFelt(forekomstType.reindrift_korrigertResultat) {
                forekomstType.fordeltSkattemessigResultat +
                    forekomstType.reindrift_korrigeringForKapitalInntektOgKapitalkostnad +
                    forekomstType.reindrift_korrigeringForBeloepFraGevinstOgTapskonto
            }
        }
    }

    internal val gjennomsnittsinntektKalkyle = kalkyle("gjennomsnittsinntekt") {
        forekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) der {
            forekomstType.naeringstype lik reindrift &&
                forekomstType.reindrift_resultatIFjor.harVerdi() &&
                forekomstType.reindrift_korrigertResultat.harVerdi()
        } forHverForekomst {
            settFelt(forekomstType.reindrift_gjennomsnittsinntekt) {
                (forekomstType.reindrift_resultatForToAarSiden +
                    forekomstType.reindrift_resultatIFjor +
                    forekomstType.reindrift_korrigertResultat) / 3
            }
        }
    }

    internal val korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle =
        kalkyle("korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt") {
            forekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) der {
                forekomstType.naeringstype lik reindrift &&
                    forekomstType.reindrift_gjennomsnittsinntekt.harVerdi() &&
                    forekomstType.reindrift_korrigertResultat.harVerdi()
            } forHverForekomst {
                settFelt(forekomstType.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt) {
                    forekomstType.reindrift_gjennomsnittsinntekt -
                        forekomstType.reindrift_korrigertResultat
                }
            }
        }

    internal val korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle =
        kalkyle("korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere") {
            forekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) der {
                forekomstType.naeringstype lik reindrift
            } forHverForekomst {
                settFelt(forekomstType.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere) {
                    forekomstType.reindrift_uttakFraAvviklingsOgOmstillingsfondForReineiere -
                        forekomstType.reindrift_innskuddIAvviklingsOgOmstillingsfondForReineiere
                }
            }
        }

    internal val korreksjonFraReindriftKalkyle = kalkyle("korreksjonFraReindrift") {
        forekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) der {
            forekomstType.naeringstype lik reindrift
        } forHverForekomst {
            settFelt(forekomstType.korreksjonFraReindrift) {
                forekomstType.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere +
                    forekomstType.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt
            }
        }
    }

    internal val skattemessigResultatForNaeringEtterKorreksjonKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjon") {
            forAlleForekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) {
                settFelt(forekomstType.fordeltSkattemessigResultatEtterKorreksjon) {
                    forekomstType.fordeltSkattemessigResultat +
                        forekomstType.sjablongberegnetInntektFraBiomasseVedproduksjon +
                        forekomstType.korreksjonFraReindrift -
                        forekomstType.fradragForRenterINaeringPaaSvalbard -
                        forekomstType.fremfoertUnderskuddIEnkeltpersonforetakPaaSvalbardFraTidligereAar
                }
            }
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaver") {
            forAlleForekomsterAv(modell2021.fordeltBeregnetNaeringsinntekt) {
                settFelt(forekomstType.fordeltSkattemessigResultatEtterKorreksjonTilordnetInnehaver) {
                    forekomstType.fordeltSkattemessigResultatEtterKorreksjon *
                        forekomstType.andelAvFordeltSkattemessigResultatTilordnetInnehaver.prosent()
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            korrigertResultatKalkyle,
            gjennomsnittsinntektKalkyle,
            korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle,
            korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle,
            korreksjonFraReindriftKalkyle,
            skattemessigResultatForNaeringEtterKorreksjonKalkyle,
            skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle
        )
    }
}