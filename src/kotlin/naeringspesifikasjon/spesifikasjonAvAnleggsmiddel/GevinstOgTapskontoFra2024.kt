package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.antallMaanederIRegnskapsperiode
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.medBegrensninger
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object GevinstOgTapskontoFra2024 : HarKalkylesamling {

    internal val gevinstOgTapskontoKnyttetTilGrunnrenteKalkyle =
        kalkyle("grunnlagForInntektsfoeringOgFradragsfoeringKalkyle") {
            forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
                hvis(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnrenteomraade.harVerdi()) {
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering) {
                        forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inngaaendeVerdi +
                            forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_sumGevinstVedRealisasjonOgUttak -
                            forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_sumTapVedRealisasjonOgUttak +
                            forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inngaaendeVerdiFraErvervetGevinstOgTapskonto -
                            forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_verdiAvRealisertGevinstOgTapskonto
                    }
                }
            }
        }

    internal val grunnlagForAaretsInntektsOgFradragsfoeringKalkyle =
        kalkyle("grunnlagForAaretsInntektsOgFradragsfoeringKalkyle") {
            forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
                settFelt(forekomstType.grunnlagForAaretsInntektsOgFradragsfoering) {
                    forekomstType.inngaaendeVerdi +
                        forekomstType.sumGevinstVedRealisasjonOgUttak -
                        forekomstType.sumTapVedRealisasjonOgUttak +
                        forekomstType.gevinstVedRealisasjonAvHelBuskapPaaGaardsbrukVedOpphoerAvDriftsgren +
                        forekomstType.inngaaendeVerdiFraErvervetGevinstOgTapskonto -
                        forekomstType.verdiAvRealisertGevinstOgTapskonto
                }
            }
        }

    private val inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle = kalkyle("inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
            forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.harVerdi() &&
                forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_satsForInntektsfoeringOgInntektsfradrag.harVerdi()
        } forHverForekomst {
            val beloep = forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.tall()
            val sats = forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_satsForInntektsfoeringOgInntektsfradrag.prosent()
            settFelt(
                if (beloep stoerreEllerLik 0) {
                    forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto
                } else {
                    forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto
                }
            ) {
                (beloep * sats).absoluttverdi()
            }
        }
    }


    internal val utgaaendeVerdiKnyttetTilGrunnrenteKalkyle = kalkyle("utgaaendeVerdiutgaaendeVerdiKnyttetTilGrunnrenteKalkyle") {
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
            hvis(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.harVerdi()) {
                if (forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.erPositiv()) {
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_utgaaendeVerdi) {
                        forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering - modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto
                    }
                } else if (forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.erNegativ()) {
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_utgaaendeVerdi) {
                        forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering + modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto
                    }
                } else {
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_utgaaendeVerdi) { BigDecimal.ZERO }
                }
            }
        }
    }

    internal val inntektFraGevinstOgTapskontoKalkyle = kalkyle("inntektFraGevinstOgTapskonto") {
        val satser = satser!!
        val grenseverdiEn = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiEn).toInt()
        val grenseverdiTo = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiTo).toInt()
        val grenseverdiTre = satser.sats(Sats.anleggsmiddelOgToemmerkonto_grenseverdiTre).toInt()
        val satsIntervallTilOgMedGrenseverdiEn = satser
            .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallTilOgMedGrenseverdiEn)
        val satsIntervallFraGrenseverdiEnTilTo = satser
            .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraGrenseverdiEnTilTo)
        val satsIntervallFraOgMedGrenseverdiTre = satser
            .satsIntervall(Sats.anleggsmiddelOgToemmerkonto_satsIntervallFraOgMedGrenseverdiTre)
        val virksomhetstypeVerdi = modell.virksomhet.virksomhetstype.verdi()
        val harSpesifikasjonAvKraftverk = harMinstEnForekomstAv(modell.kraftverk_spesifikasjonAvKraftverk)

        val antallMaanederIRegnskapsperiode = antallMaanederIRegnskapsperiode(
            modell.virksomhet.regnskapsperiode_start.dato() ?: LocalDate.of(inntektsaar.gjeldendeInntektsaar, 1, 1),
            modell.virksomhet.regnskapsperiode_slutt.dato() ?: LocalDate.of(inntektsaar.gjeldendeInntektsaar, 12, 1),
        )
        val inntektsaar = inntektsaar

        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
            val beloep = forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.tall()
            val sats = forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()

            when {
                harSpesifikasjonAvKraftverk && sats.harVerdi() -> {
                    val justertBeloep = beloep * sats
                    if (justertBeloep.erNegativ()) {
                        settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) { justertBeloep.absoluttverdi() }
                    } else {
                        settFelt(forekomstType.inntektFraGevinstOgTapskonto) { justertBeloep }
                    }
                }

                // beloep større enn øverste grenseverdi
                beloep stoerreEllerLik grenseverdiTre -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraOgMedGrenseverdiTre.first,
                        min = satsIntervallFraOgMedGrenseverdiTre.first
                    )
                    val satsjustertBeloep = beloep * justertSats
                    val inntekt = if (justertSats < BigDecimal.ONE) { // Dersom sats < 100%
                        beregnBeloepUtfraAntallMaaneder(inntektsaar, antallMaanederIRegnskapsperiode, satsjustertBeloep)
                    } else satsjustertBeloep
                    settFelt(forekomstType.inntektFraGevinstOgTapskonto) { inntekt }
                }

                // beloep fra 0 til grenseverdiTre, positivt
                beloep stoerreEllerLik grenseverdiTo && beloep mindreEnn grenseverdiTre -> {
                    settFelt(forekomstType.inntektFraGevinstOgTapskonto) { beloep }
                }

                // beloep mellom grenseverdiEn og 0, negativt
                beloep stoerreEnn grenseverdiEn && beloep mindreEnn grenseverdiTo -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.second
                    )
                    val satsjustertBeloep = beloep * justertSats
                    val fradrag = if (justertSats < BigDecimal.ONE) { // Dersom sats < 100%
                        beregnBeloepUtfraAntallMaaneder(inntektsaar, antallMaanederIRegnskapsperiode, satsjustertBeloep)
                    } else satsjustertBeloep
                    settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) { fradrag.absoluttverdi() }
                }

                // beloep mindre enn nederste grenseverdi, negativt og enkeltpersonforetak
                beloep mindreEllerLik grenseverdiEn &&
                    virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallTilOgMedGrenseverdiEn.second,
                        maks = satsIntervallTilOgMedGrenseverdiEn.second
                    )
                    settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) {
                        beregnBeloepUtfraAntallMaaneder(inntektsaar, antallMaanederIRegnskapsperiode, beloep * justertSats).absoluttverdi()
                    }
                }

                // beloep mindre enn nederste grenseverdi, negativt ikke enkeltpersonforetak
                beloep mindreEllerLik grenseverdiEn &&
                    virksomhetstypeVerdi != virksomhetstype.kode_enkeltpersonforetak.kode -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.second
                    )
                    settFelt(forekomstType.inntektsfradragFraGevinstOgTapskonto) {
                        beregnBeloepUtfraAntallMaaneder(inntektsaar, antallMaanederIRegnskapsperiode, beloep * justertSats).absoluttverdi()
                    }
                }
            }
        }
    }

    private fun beregnBeloepUtfraAntallMaaneder(inntektsaar: Inntektsaar, antallMaanederIRegnskapsperiode: BigDecimal, satsjustertBeloep: BigDecimal?): BigDecimal? {
        return if (inntektsaar.tekniskInntektsaar >= 2023 && satsjustertBeloep != null) {
            (satsjustertBeloep.times(antallMaanederIRegnskapsperiode)).divide(12.toBigDecimal(), 2, RoundingMode.HALF_UP)
        } else satsjustertBeloep
    }

    internal val utgaaendeVerdiKalkyle = kalkyle("utgaaendeVerdi") {
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) {
            hvis(forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.harVerdi()) {
                if (forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.erPositiv()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAaretsInntektsOgFradragsfoering - modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto.inntektFraGevinstOgTapskonto
                    }
                } else if (forekomstType.grunnlagForAaretsInntektsOgFradragsfoering.erNegativ()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.grunnlagForAaretsInntektsOgFradragsfoering + modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto.inntektsfradragFraGevinstOgTapskonto
                    }
                } else {
                    settFelt(forekomstType.utgaaendeVerdi) { BigDecimal.ZERO }
                }
            }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        gevinstOgTapskontoKnyttetTilGrunnrenteKalkyle ,
        inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle,
        utgaaendeVerdiKnyttetTilGrunnrenteKalkyle,
        grunnlagForAaretsInntektsOgFradragsfoeringKalkyle,
        inntektFraGevinstOgTapskontoKalkyle,
        utgaaendeVerdiKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
