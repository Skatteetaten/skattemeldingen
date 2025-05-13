package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.virksomhetsTypeNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=489349748
 */
internal object FordeltBeregnetNaeringsinntektBeregningFra2023 : HarKalkylesamling {

    const val reindrift = "reindrift"

    internal val korrigertResultatKalkyle = kalkyle {
        forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
            hvis(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype.verdi() == reindrift) {
                settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigertResultat) {
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigeringForKapitalInntektOgKapitalkostnad +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigeringForBeloepFraGevinstOgTapskonto
                }
            }
        }
    }

    internal val gjennomsnittsInntektKalkyle = kalkyle {
        forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
            hvis(
                modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype.verdi() == reindrift &&
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_resultatIFjor.harVerdi() &&
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigertResultat.harVerdi()

            ) {
                settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_gjennomsnittsinntekt) {
                    (modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_resultatForToAarSiden +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_resultatIFjor +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigertResultat) / 3
                }
            }

        }
    }

    internal val korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle =
        kalkyle("korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt") {
            forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
                hvis(
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype.verdi() == reindrift &&
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_gjennomsnittsinntekt.harVerdi() &&
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigertResultat.harVerdi()
                ) {
                    settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt) {
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_gjennomsnittsinntekt -
                            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korrigertResultat
                    }
                }
            }
        }

    internal val korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle =
        kalkyle("korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere") {
            forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
                hvis(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype.verdi() == reindrift) {
                    settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere) {
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_uttakFraAvviklingsOgOmstillingsfondForReineiere -
                            modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_innskuddIAvviklingsOgOmstillingsfondForReineiere
                    }
                }
            }
        }

    internal val korreksjonFraReindriftKalkyle = kalkyle("korreksjonFraReindrift") {
        forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
            hvis(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.naeringstype.verdi() == reindrift) {
                settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.korreksjonFraReindrift) {
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.reindrift_korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt
                }
            }
        }
    }

    internal val skattemessigResultatForNaeringEtterKorreksjonKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjon") {
            forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
                settFelt(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultatEtterKorreksjon) {
                    modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultat +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.sjablongberegnetInntektFraBiomasseVedproduksjon -
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.sjablongberegnetFradragFraBiomasseVedproduksjon +
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.korreksjonFraReindrift -
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fradragForRenterINaeringPaaSvalbard -
                        modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fremfoertUnderskuddIEnkeltpersonforetakPaaSvalbardFraTidligereAar
                }
            }
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle =
        kalkyle("skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaver") {
            forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
                settFelt(forekomstType.fordeltSkattemessigResultatEtterKorreksjonTilordnetInnehaver) {
                        forekomstType.fordeltSkattemessigResultatEtterKorreksjon *
                                forekomstType.andelAvFordeltSkattemessigResultatTilordnetInnehaver.prosent()
                }
            }
        }

    internal val fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig =
        kalkyle("fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig") {
            forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig) {
                settFelt(forekomstType.fordeltSkattemessigResultatEtterKorreksjon) {
                    forekomstType.fordeltSkattemessigResultat -
                        forekomstType.utbetaltEtterbetalingTilMedlemIEgetSamvirkeforetak
                }
            }
        }

    //SPAP-33260 - beregnes kun for Nokus, men alltid, også når justering ikke er gjort
    internal val fordeltSkattemessigResultatEtterKorreksjonOgJusteringForPersonligDeltakerINokusKalkyle =
        kalkyle("fordeltSkattemessigResultatEtterKorreksjonOgJusteringForPersonligDeltakerINokusKalkyle") {
            hvis(virksomhetsTypeNokus()) {
                forAlleForekomsterAv(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf) {
                    settFelt(forekomstType.fordeltSkattemessigResultatEtterKorreksjonOgJusteringForPersonligDeltakerINokus) {
                        forekomstType.fordeltSkattemessigResultatEtterKorreksjon +
                                forekomstType.justeringAvSkattemessigResultatKnyttetTilFritaksmetodenForPersonligDeltakerINokus
                    }
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
        skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle,
        fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig,
        fordeltSkattemessigResultatEtterKorreksjonOgJusteringForPersonligDeltakerINokusKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyletre
    }
}