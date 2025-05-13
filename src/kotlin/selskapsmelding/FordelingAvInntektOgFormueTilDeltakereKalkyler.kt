package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
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
        val inntektEllerUnderskuddRederi = modell.inntektOgUnderskudd.samletInntektForSelskapMedDeltakerIRederiskatteordning -
            modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedDeltakerIRederiskatteordning
        val inntektEllerUnderskuddNokus = modell.inntektOgUnderskudd.samletInntektForSelskapMedPersonligDeltakerINokus -
            modell.inntektOgUnderskudd.samletUnderskuddForSelskapMedPersonligDeltakerINokus
        val erNokus = erNokus()

        forekomsterAv(modell.deltaker) forHverForekomst {
            val prosent =
                forekomstType.deltakersAndelAvInntektIProsent.prosent() ?: forekomstType.selskapsandelIProsent.prosent()

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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            deltakersAndelAvFormueOgGjeldKalkyle,
            grunnlagForDeltakersAndelAvInntektKalkyle
        )
    }
}