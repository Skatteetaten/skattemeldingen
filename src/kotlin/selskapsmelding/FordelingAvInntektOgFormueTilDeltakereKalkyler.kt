package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erSdf
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV1


object FordelingAvInntektOgFormueTilDeltakereKalkyler : HarKalkylesamling {

    internal val deltakersAndelAvFormueOgGjeldKalkyle = kalkyle {
        val nettoformue = modell.formueOgGjeld.nettoformue.tall()
        val nettogjeld = modell.formueOgGjeld.nettogjeld.tall()
        val nokusErBoersnotert = modell.opplysningOmSelskapMedDeltakerfastsetting.nokusErBoersnotert.erSann()
        val kursverdiAvBoersnoterteAksjer = modell.formueForSelskapMedDeltakerINokus.kursverdiAvBoersnoterteAksjer.tall()
        val nettoformueFraForegaaendeInntektsaar = modell.formueForSelskapMedDeltakerINokus.nettoformueFraForegaaendeInntektsaar.tall()
        val antattSalgsverdiVedUtgangenAvInntektsaaret = modell.formueForSelskapMedDeltakerINokus.antattSalgsverdiVedUtgangenAvInntektsaaret.tall()
        val erNokus = erNokus()

        forekomsterAv(modell.deltaker) forHverForekomst {
            val prosent =
                forekomstType.deltakersAndelAvFormueIProsent.prosent() ?: forekomstType.selskapsandelIProsent.prosent()
            val grunnlagForAndelAvNettoformueEllerGjeld = if (!erNokus) {
                nettoformue - nettogjeld
            } else {
                if (nokusErBoersnotert && kursverdiAvBoersnoterteAksjer != null) {
                    kursverdiAvBoersnoterteAksjer
                } else if (forekomstType.deltakerensNorskePersonidentifikator.harVerdi()) {
                    antattSalgsverdiVedUtgangenAvInntektsaaret ?: nettoformueFraForegaaendeInntektsaar ?: nettoformue ?: BigDecimal.ZERO
                } else if (forekomstType.deltakerensOrganisasjonsnummer.harVerdi()) {
                    antattSalgsverdiVedUtgangenAvInntektsaaret ?: nettoformue ?: BigDecimal.ZERO
                } else {
                    null
                }
            }
            val andelAvNettoformueEllerGjeld = (grunnlagForAndelAvNettoformueEllerGjeld * prosent).somHeltall()
            if (andelAvNettoformueEllerGjeld.erNegativ() && !erNokus) {
                settFelt(forekomstType.andelAvNettogjeld) { andelAvNettoformueEllerGjeld.absoluttverdi() }
            } else {
                settFelt(forekomstType.andelAvNettoformue) { andelAvNettoformueEllerGjeld medMinimumsverdi 0 }
            }

            hvis(forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_erKommandittistEllerStilleDeltakerMedBeloepsbegrensetAnsvar.erSann()) {
                val kapital =
                    forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_andelIkkeinnkaltSelskapskapitalEllerInnskuddsforpliktelse.tall()
                hvis(forekomstType.andelAvNettogjeld stoerreEnn kapital) {
                    settFelt(forekomstType.andelAvNettogjeld) { kapital.absoluttverdi().somHeltall() }
                }
            }
        }
    }

    internal val grunnlagForDeltakersAndelAvInntektKalkyle = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar >= 2024) {
            val samletUnderskudd = modell.inntektOgUnderskudd.samletUnderskudd.tall()
            val inntektEllerUnderskudd = modell.inntektOgUnderskudd.samletInntekt.tall() -
                    modell.inntektOgUnderskudd.samletUnderskudd.tall()
            val inntektEllerUnderskuddRederi =
                modell.inntektOgUnderskudd.samletInntektForSelskapMedDeltakerIRederiskatteordning -
                        modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedDeltakerIRederiskatteordning
            val samletUnderskuddINokus =
                modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedPersonligDeltakerINokus.tall()
            val inntektEllerUnderskuddNokus =
                modell.inntektOgUnderskudd.samletInntektForSelskapMedPersonligDeltakerINokus -
                        samletUnderskuddINokus
            val erNokus = erNokus()

            val landkode = modell.opplysningOmSelskapMedDeltakerfastsetting.hovedkontorforetaketsLandkode.verdi()
            val erNokusEllerSdfIkkeNorge = erNokus() || (erSdf() && !landkode.isNullOrEmpty() && landkode != "NO")
            val underlagsmaterialeForUsdfEllerNokusKanFremlegges =
                modell.opplysningOmSelskapMedDeltakerfastsetting.underlagsmaterialeForUsdfEllerNokusKanFremlegges.verdi()


            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvInntektIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                //SPAP-34433 - andel av inntekt skal beregnes på samme måte som før
                val inntektForDeltaker = if (forekomstType.erOmfattetAvRederiskatteordning.erSann()) {
                    inntektEllerUnderskuddRederi
                } else if (forekomstType.deltakerensNorskePersonidentifikator.harVerdi() && erNokus) {
                    inntektEllerUnderskuddNokus
                } else {
                    inntektEllerUnderskudd
                }

                val underskuddForDeltaker = if (erNokusEllerSdfIkkeNorge &&
                    underlagsmaterialeForUsdfEllerNokusKanFremlegges == "false" &&
                    samletUnderskudd.harVerdi()
                ) {
                    BigDecimal.ZERO
                } else if (forekomstType.erOmfattetAvRederiskatteordning.erSann()) {
                    inntektEllerUnderskuddRederi
                } else if (erNokus &&
                    underlagsmaterialeForUsdfEllerNokusKanFremlegges == "true" &&
                    samletUnderskuddINokus.harVerdi() &&
                    forekomstType.deltakerensNorskePersonidentifikator.harVerdi()
                ) {
                    inntektEllerUnderskuddNokus
                } else {
                    inntektEllerUnderskudd
                }

                val andelAvSamletUnderskudd = (underskuddForDeltaker * prosent).somHeltall()
                val andelAvSamletInntekt = (inntektForDeltaker * prosent).somHeltall() medMinimumsverdi 0

                if (andelAvSamletUnderskudd.erNegativ()) {
                    settFelt(modell.deltaker.andelAvSamletUnderskudd) { andelAvSamletUnderskudd.absoluttverdi() }
                } else {
                    settFelt(modell.deltaker.andelAvSamletInntekt) { andelAvSamletInntekt }
                }
            }
        }
    }

    internal val grunnlagForDeltakersAndelAvInntektKalkyleTom2023 = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar <= 2023) {
            val inntekt = if (inntektsaar.tekniskInntektsaar == 2022) {
                modellV1.inntektOgUnderskudd.naeringsinntekt.tall()
            } else {
                modell.inntektOgUnderskudd.samletInntekt.tall()
            }
            val underskudd = if (inntektsaar.tekniskInntektsaar == 2022) {
                modellV1.inntektOgUnderskudd.underskudd.tall()
            } else {
                modell.inntektOgUnderskudd.samletUnderskudd.tall()
            }
            val inntektEllerUnderskudd = inntekt - underskudd
            val inntektEllerUnderskuddRederi =
                modell.inntektOgUnderskudd.samletInntektForSelskapMedDeltakerIRederiskatteordning -
                        modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedDeltakerIRederiskatteordning
            val inntektEllerUnderskuddNokus =
                modell.inntektOgUnderskudd.samletInntektForSelskapMedPersonligDeltakerINokus -
                        modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedPersonligDeltakerINokus
            val erNokus = erNokus()

            forekomsterAv(modell.deltaker) forHverForekomst {
                val prosent =
                    forekomstType.deltakersAndelAvInntektIProsent.prosent()
                        ?: forekomstType.selskapsandelIProsent.prosent()

                val inntektEllerUnderskuddForDeltaker = if (forekomstType.erOmfattetAvRederiskatteordning.erSann()) {
                    inntektEllerUnderskuddRederi
                } else if (forekomstType.deltakerensNorskePersonidentifikator.harVerdi() && erNokus) {
                    inntektEllerUnderskuddNokus
                } else {
                    inntektEllerUnderskudd
                }

                val andelAvSamletInntektEllerGjeld = (inntektEllerUnderskuddForDeltaker * prosent).somHeltall()

                if (andelAvSamletInntektEllerGjeld.erNegativ()) {
                    settFelt(modell.deltaker.andelAvSamletUnderskudd) { andelAvSamletInntektEllerGjeld.absoluttverdi() }
                } else {
                    settFelt(modell.deltaker.andelAvSamletInntekt) { andelAvSamletInntektEllerGjeld }
                }
            }
        }
    }


    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            deltakersAndelAvFormueOgGjeldKalkyle,
            grunnlagForDeltakersAndelAvInntektKalkyleTom2023,
            grunnlagForDeltakersAndelAvInntektKalkyle
        )
    }
}