package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

/**
 * Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=489349748
 */
internal object FordeltBeregnetNaeringsinntektBeregning2022 : HarKalkylesamling {

    const val reindrift = "reindrift"

    internal val korrigertResultatKalkyle = kalkyle {
        forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
            hvis(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype.verdi() == reindrift) {
                settFelt(modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigertResultat) {
                    modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat +
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigeringForKapitalInntektOgKapitalkostnad +
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigeringForBeloepFraGevinstOgTapskonto
                }
            }
        }
    }

    internal val gjennomsnittsInntektKalkyle = kalkyle {
        forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
            hvis(
                modell2022.fordeltBeregnetNaeringsinntekt.naeringstype.verdi() == reindrift &&
                    modell2022.fordeltBeregnetNaeringsinntekt.reindrift_resultatIFjor.harVerdi() &&
                    modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigertResultat.harVerdi()

            ) {
                settFelt(modell2022.fordeltBeregnetNaeringsinntekt.reindrift_gjennomsnittsinntekt) {
                    (modell2022.fordeltBeregnetNaeringsinntekt.reindrift_resultatForToAarSiden +
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_resultatIFjor +
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigertResultat) / 3
                }
            }

        }
    }

    internal val korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle =
        kalkyle("korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt") {
            forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
                hvis(
                    modell2022.fordeltBeregnetNaeringsinntekt.naeringstype.verdi() == reindrift &&
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_gjennomsnittsinntekt.harVerdi() &&
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigertResultat.harVerdi()
                ) {
                    settFelt(modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt) {
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_gjennomsnittsinntekt -
                            modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korrigertResultat
                    }
                }
            }
        }

    internal val korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle =
        kalkyle("korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere") {
            forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
                hvis(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype.verdi() == reindrift) {
                    settFelt(modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere) {
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_uttakFraAvviklingsOgOmstillingsfondForReineiere -
                            modell2022.fordeltBeregnetNaeringsinntekt.reindrift_innskuddIAvviklingsOgOmstillingsfondForReineiere
                    }
                }
            }
        }

    internal val korreksjonFraReindriftKalkyle = kalkyle("korreksjonFraReindrift") {
        forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
            hvis(modell2022.fordeltBeregnetNaeringsinntekt.naeringstype.verdi() == reindrift) {
                settFelt(modell2022.fordeltBeregnetNaeringsinntekt.korreksjonFraReindrift) {
                    modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere +
                        modell2022.fordeltBeregnetNaeringsinntekt.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt
                }
            }
        }
    }

    internal val skattemessigResultatForNaeringEtterKorreksjonKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjon") {
            forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
                settFelt(modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjon) {
                    modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultat +
                        modell2022.fordeltBeregnetNaeringsinntekt.sjablongberegnetInntektFraBiomasseVedproduksjon -
                        modell2022.fordeltBeregnetNaeringsinntekt.sjablongberegnetFradragFraBiomasseVedproduksjon +
                        modell2022.fordeltBeregnetNaeringsinntekt.korreksjonFraReindrift -
                        modell2022.fordeltBeregnetNaeringsinntekt.fradragForRenterINaeringPaaSvalbard -
                        modell2022.fordeltBeregnetNaeringsinntekt.fremfoertUnderskuddIEnkeltpersonforetakPaaSvalbardFraTidligereAar
                }
            }
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaver") {
            forAlleForekomsterAv(modell2022.fordeltBeregnetNaeringsinntekt) {
                settFelt(modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjonTilordnetInnehaver) {
                    modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjon *
                        modell2022.fordeltBeregnetNaeringsinntekt.andelAvFordeltSkattemessigResultatTilordnetInnehaver.prosent()
                }
            }
        }

    private val kalkyletre = Kalkylesamling(
        korrigertResultatKalkyle,
        gjennomsnittsInntektKalkyle,
        korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle,
        korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle,
        korreksjonFraReindriftKalkyle,
        skattemessigResultatForNaeringEtterKorreksjonKalkyle,
        skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyletre
    }
}