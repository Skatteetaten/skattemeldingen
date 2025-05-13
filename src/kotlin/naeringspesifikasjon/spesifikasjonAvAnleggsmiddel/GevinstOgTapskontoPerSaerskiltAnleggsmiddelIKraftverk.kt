package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.medBegrensninger
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk {

    internal val gevinstOgTapVedRealisasjonKalkyle = kalkyle("gevinstOgTapVedRealisasjonKalkyle") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            val sumGjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktet =
                forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst { forekomstType.gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktet.tall() }

            hvis(sumGjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktet.harVerdi()) {

                val realisasjonsVerdi =
                    sumGjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktet - forekomstType.vederlagVedRealisasjon
                if (realisasjonsVerdi mindreEllerLik 0) {
                    settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_gevinstVedRealisasjon) {
                        realisasjonsVerdi.absoluttverdi()
                    }
                } else {
                    settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_tapVedRealisasjon) {
                        realisasjonsVerdi
                    }
                }
            }
        }
    }

    internal val grunnlagForAaretsInntektsfoeringOgInntektsfradrag = opprettSyntetiskFelt(
        modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk,
        "grunnlagForAaretsInntektsfoeringOgInntektsfradrag"
    )

    internal val grunnlagForAaretsInntektsfoeringOgInntektsfradragKalkyle =
        kalkyle("grunnlagForAaretsInntektsfoeringOgInntektsfradragKalkyle") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                settFelt(grunnlagForAaretsInntektsfoeringOgInntektsfradrag) {
                    forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inngaaendeVerdi +
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_gevinstVedRealisasjon -
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_tapVedRealisasjon
                }
            }
        }

    internal val inntektEllerInntektsfradragFraGevinstOgTapskontoKalkyle =
        kalkyle("inntektEllerInntektsfradragFraGevinstOgTapskontoKalkyle") {
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

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                hvis(grunnlagForAaretsInntektsfoeringOgInntektsfradrag.harVerdi()) {
                    val beloep = grunnlagForAaretsInntektsfoeringOgInntektsfradrag.tall()
                    val sats =
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_satsForInntektsfoeringOgInntektsfradrag.prosent()

                    when {
                        sats.harVerdi() -> {
                            val satsjustertBeloep = beloep * sats
                            if (satsjustertBeloep.erNegativ()) {
                                settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektsfradragFraGevinstOgTapskonto) {
                                    satsjustertBeloep.absoluttverdi()
                                }
                            } else {
                                settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektFraGevinstOgTapskonto) { satsjustertBeloep }
                            }
                        }

                        //hvis beløpet er større eller lik øverste grenseverdi
                        beloep stoerreEllerLik grenseverdiTre -> {
                            val justertSats = sats.medBegrensninger(
                                defaultVerdi = satsIntervallFraOgMedGrenseverdiTre.first,
                                min = satsIntervallFraOgMedGrenseverdiTre.first
                            )
                            val satsjustertBeloep = beloep * justertSats
                            settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektFraGevinstOgTapskonto) { satsjustertBeloep }
                        }

                        // beloep fra 0 til grenseverdiTre
                        beloep stoerreEllerLik grenseverdiTo && beloep mindreEnn grenseverdiTre -> {
                            settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektFraGevinstOgTapskonto) { beloep }
                        }

                        //beloep mellom grenseverdiEn og 0
                        beloep stoerreEnn grenseverdiEn && beloep mindreEnn grenseverdiTo -> {
                            val justertSats = sats.medBegrensninger(
                                defaultVerdi = satsIntervallFraGrenseverdiEnTilTo.second
                            )
                            val satsjustertBeloep = beloep * justertSats
                            settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektsfradragFraGevinstOgTapskonto) { satsjustertBeloep.absoluttverdi() }
                        }

                        //beloep mindre enn nederste grenseverdi
                        beloep mindreEllerLik grenseverdiEn -> {
                            val justertSats = sats.medBegrensninger(
                                defaultVerdi = satsIntervallTilOgMedGrenseverdiEn.second,
                                maks = satsIntervallTilOgMedGrenseverdiEn.second
                            )
                            settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektsfradragFraGevinstOgTapskonto) {
                                (beloep * justertSats).absoluttverdi()
                            }
                        }
                    }
                }
            }
        }

    internal val utgaaendeVerdiKalkyle = kalkyle("utgaaendeVerdiKalkyle") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            hvis(grunnlagForAaretsInntektsfoeringOgInntektsfradrag.harVerdi()) {
                settFelt(forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_utgaaendeVerdi) {
                    grunnlagForAaretsInntektsfoeringOgInntektsfradrag -
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektFraGevinstOgTapskonto +
                        forekomstType.gevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk_inntektsfradragFraGevinstOgTapskonto
                }
            }
        }
    }
}