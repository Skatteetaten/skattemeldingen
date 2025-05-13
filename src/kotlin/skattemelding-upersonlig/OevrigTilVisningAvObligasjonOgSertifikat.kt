package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object OevrigTilVisningAvObligasjonOgSertifikat : HarKalkylesamling {

    internal val samletRenteinntektAvObligasjonOgSertifikat =
        kalkyle("samletRenteinntektAvObligasjonOgSertifikat") {
            settUniktFelt(
                modell
                    .spesifikasjonAvForholdRelevanteForBeskatning_oevrigTilVisningAvObligasjonOgSertifikat
                    .samletRenteinntektAvObligasjonOgSertifikat
            ) {
                forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_obligasjonOgSertifikat) summerVerdiFraHverForekomst {
                    forekomstType.renteinntektAvObligasjonOgSertifikat.tall()
                }
            }

        }

    internal val samletGevinstVedRealisasjonAvObligasjonOgSertifikat =
        kalkyle("samletGevinstVedRealisasjonAvObligasjonOgSertifikat") {
            settUniktFelt(
                modell
                    .spesifikasjonAvForholdRelevanteForBeskatning_oevrigTilVisningAvObligasjonOgSertifikat
                    .samletGevinstVedRealisasjonAvObligasjonOgSertifikat
            ) {
                forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_obligasjonOgSertifikat) summerVerdiFraHverForekomst {
                    forekomstType.gevinstVedRealisasjonAvObligasjonOgSertifikat.tall()
                }
            }
        }

    internal val tapVedRealisasjonAvObligasjonOgSertifikat =
        kalkyle("tapVedRealisasjonAvObligasjonOgSertifikat") {
            settUniktFelt(
                modell
                    .spesifikasjonAvForholdRelevanteForBeskatning_oevrigTilVisningAvObligasjonOgSertifikat
                    .samletTapVedRealisasjonAvObligasjonOgSertifikat
            ) {
                forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_obligasjonOgSertifikat) summerVerdiFraHverForekomst {
                    forekomstType.tapVedRealisasjonAvObligasjonOgSertifikat.tall()
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletRenteinntektAvObligasjonOgSertifikat,
            samletGevinstVedRealisasjonAvObligasjonOgSertifikat,
            tapVedRealisasjonAvObligasjonOgSertifikat
        )
    }
}