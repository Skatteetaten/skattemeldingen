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

internal object GevinstOgTapskonto : HarKalkylesamling {

    private val inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle = kalkyle("inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle") {
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
            val beloep = forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_grunnlagForInntektsfoeringOgFradragsfoering.tall()
            val sats = forekomstType.satsForInntektsfoeringOgInntektsfradrag.prosent()

            when {
                harSpesifikasjonAvKraftverk && sats.harVerdi() -> {
                    val justertBeloep = beloep * sats
                    if (justertBeloep.erNegativ()) {
                        settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto) { justertBeloep.absoluttverdi() }
                    } else {
                        settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto) { justertBeloep }
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
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto) { inntekt }
                }

                // beloep fra 0 til grenseverdiTre, positivt
                beloep stoerreEllerLik grenseverdiTo && beloep mindreEnn grenseverdiTre -> {
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektFraGevinstOgTapskonto) { beloep }
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
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto) { fradrag.absoluttverdi() }
                }

                // beloep mindre enn nederste grenseverdi, negativt og enkeltpersonforetak
                beloep mindreEllerLik grenseverdiEn &&
                    virksomhetstypeVerdi == virksomhetstype.kode_enkeltpersonforetak.kode -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallTilOgMedGrenseverdiEn.second,
                        maks = satsIntervallTilOgMedGrenseverdiEn.second
                    )
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto) {
                        beregnBeloepUtfraAntallMaaneder(inntektsaar, antallMaanederIRegnskapsperiode, beloep * justertSats).absoluttverdi()
                    }
                }

                // beloep mindre enn nederste grenseverdi, negativt ikke enkeltpersonforetak
                beloep mindreEllerLik grenseverdiEn &&
                    virksomhetstypeVerdi != virksomhetstype.kode_enkeltpersonforetak.kode -> {
                    val justertSats = sats.medBegrensninger(
                        defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.second
                    )
                    settFelt(forekomstType.gevinstOgTapskontoKnyttetTilGrunnrente_inntektsfradragFraGevinstOgTapskonto) {
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

    private val kalkyleSamling = Kalkylesamling(
        GevinstOgTapskontoFra2024.gevinstOgTapskontoKnyttetTilGrunnrenteKalkyle ,
        inntektFraGevinstOgTapskontoKnyttetTilGrunnrenteKalkyle,
        GevinstOgTapskontoFra2024.utgaaendeVerdiKnyttetTilGrunnrenteKalkyle,
        GevinstOgTapskontoFra2024.grunnlagForAaretsInntektsOgFradragsfoeringKalkyle,
        GevinstOgTapskontoFra2024.inntektFraGevinstOgTapskontoKalkyle,
        GevinstOgTapskontoFra2024.utgaaendeVerdiKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
