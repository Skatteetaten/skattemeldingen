@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import java.time.LocalDate
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.datoInnenforInntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.*
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.biltype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerMellom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.maanederMellom

internal object MotorkjoeretoeyINaeringFra2023 : HarKalkylesamling {

    internal val bilensAlder =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering, "bilensAlder")
    internal val antallDagerTilDisposisjon =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering, "antallDagerTilDisposisjon")
    internal val antallMaanederTilDisposisjon = opprettSyntetiskFelt(
        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering,
        "antallMaanederTilDisposisjon"
    )
    internal val satsBeregningsgrunnlag1 =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering, "satsBeregningsgrunnlag1")
    internal val prosentAvBilensVerdi =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering, "prosentAvBilensVerdi")
    internal val prosentandelAvFaktiskeKostnader = opprettSyntetiskFelt(
        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering,
        "prosentandelAvFaktiskeKostnader"
    )
    internal val kmSatsForPrivatBruk =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering, "kmSatsForPrivatBruk")

    internal val bilensAlderKalkyle = kalkyle("bilensAlder") {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            settFelt(bilensAlder) {
                (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.aarForFoerstegangsregistrering - inntektsaar).absoluttverdi()
            }
        }
    }

    internal val preprossesserMotorkjoeretoey = kalkyle {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) forHverForekomst {
            val disponertFraOgMed = forekomstType.disponertFraOgMedDato.dato()
            val disponertTilOgMed = forekomstType.disponertTilOgMedDato.dato()

            var datoStart = disponertFraOgMed
                ?.let { datoInnenforInntektsaar(it, this@kalkyle.inntektsaar.gjeldendeInntektsaar) }
                ?: LocalDate.of(this@kalkyle.inntektsaar.gjeldendeInntektsaar, 1, 1)
            var datoSlutt = disponertTilOgMed
                ?.let { datoInnenforInntektsaar(it, this@kalkyle.inntektsaar.gjeldendeInntektsaar) }
                ?: LocalDate.of(this@kalkyle.inntektsaar.gjeldendeInntektsaar, 12, 31)

            if (datoStart > datoSlutt) {
                val temp = datoSlutt
                datoSlutt = datoStart
                datoStart = temp
            }

            if (disponertFraOgMed != datoStart) {
                settFelt(forekomstType.disponertFraOgMedDato, datoStart.toString())
            }

            if (disponertTilOgMed != datoSlutt) {
                settFelt(forekomstType.disponertTilOgMedDato, datoSlutt.toString())
            }

            // Beregningene i denne filen handler om å finne tilbakefoertBilkostnadForPrivatBrukAvYrkesbil
            // noe som ikke er aktuelt dersom yrkesbilen IKKE har blitt brukt privat
            if (
                forekomstType.erYrkesbilenIBrukPrivat.erUsann()
                && forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil.harVerdi()
            ) {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) { BigDecimal.ZERO }
            }
        }
    }


    internal val antallDagerTilDisposisjonKalkyle = kalkyle("antallDagerTilDisposisjon") {
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            settFelt(antallDagerTilDisposisjon) {
                dagerMellom(
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertFraOgMedDato.dato(),
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertTilOgMedDato.dato()
                )
            }
        }
    }

    internal val antallMaanederTilDisposisjonKalkyle = kalkyle("antallMaanederTilDisposisjon") {
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            settFelt(antallMaanederTilDisposisjon) {
                maanederMellom(
                    forekomstType.disponertFraOgMedDato.dato(),
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertTilOgMedDato.dato()
                )
            }
        }
    }

    internal val satsPersonbilEllerBussFor9PassasjererEllerFlere =
        kalkyle("satsPersonbilEllerBussFor9PassasjererEllerFlere") {
            val satser = satser!!
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.biltype likEnAv listOf(biltype.kode_personbilEllerBussFor9PassasjererEllerFlere)
            } forHverForekomst {
                if (bilensAlder mindreEllerLik 3) {
                    settFelt(satsBeregningsgrunnlag1) {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik satser.sats(privatBrukAvBil_grenseverdiForYrkeskjoering)) {
                            satser.sats(privatBrukAvBil_personbilNyereEnnTreAar)
                        } else {
                            satser.sats(privatBrukAvBil_personbilNyereEnnTreAarYrkeskjoeringOverGrenseverdi)
                        }
                    }
                } else {
                    settFelt(satsBeregningsgrunnlag1) {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik satser.sats(privatBrukAvBil_grenseverdiForYrkeskjoering)) {
                            satser.sats(privatBrukAvBil_personbilEldreEnnTreAar)
                        } else {
                            satser.sats(privatBrukAvBil_personbilEldreEnnTreAarYrkeskjoeringOverGrenseverdi)
                        }
                    }
                }
            }
        }

    private val satsVareEllerLastebil = kalkyle("satsVareEllerLastebil") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype.kode_varebilKlasse2,
                biltype.kode_lastebilMedTotalvektUnder7500Kg
            )
        } forHverForekomst {
            settFelt(satsBeregningsgrunnlag1) {
                if (bilensAlder mindreEllerLik 3) {
                    satser.sats(privatBrukAvBil_varebilNyereEnnTreAar)
                } else {
                    satser.sats(privatBrukAvBil_varebilEldreEnnTreAar)
                }
            }
        }
    }

    internal val prosentAvBilensVerdiKalkyle = kalkyle("prosentAvBilensVerdi") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype.kode_personbilEllerBussFor9PassasjererEllerFlere
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 =
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.listeprisSomNy * satsBeregningsgrunnlag1

                val grunnlagOppTilGrense = beregningsgrunnlagAlt1 medMaksimumsverdi satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)
                val andelAvGrunnlagOverGrense = beregningsgrunnlagAlt1 andelOver satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)

                (grunnlagOppTilGrense * satser.sats(privatBrukAvBil_personbilSjablongUnderGrenseverdi) +
                    andelAvGrunnlagOverGrense * satser.sats(privatBrukAvBil_personbilSjablongOverGrenseverdi)) *
                    antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val prosentAvBilensVerdiVarebilEllerLastebil = kalkyle("prosentAvBilensVerdiVarebilEllerLastebil") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype.kode_varebilKlasse2,
                biltype.kode_lastebilMedTotalvektUnder7500Kg
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 =
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.listeprisSomNy * satsBeregningsgrunnlag1
                val beregningsgrunnlag =
                    beregningsgrunnlagAlt1 - ((beregningsgrunnlagAlt1 * satser.sats(privatBrukAvBil_varebilBunnfradrag)) medMaksimumsverdi satser.sats(privatBrukAvBil_varebilMaksverdiForBunnfradrag))

                val grunnlagOppTilGrense = beregningsgrunnlag medMaksimumsverdi satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)
                val andelAvGrunnlagOverGrense = beregningsgrunnlag andelOver satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)

                (grunnlagOppTilGrense * satser.sats(privatBrukAvBil_personbilSjablongUnderGrenseverdi) +
                    andelAvGrunnlagOverGrense * satser.sats(privatBrukAvBil_personbilSjablongOverGrenseverdi)) *
                    antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val kmSatsForPrivatBrukKalkyle = kalkyle("kmSatsForPrivatBruk") {
        val satser = satser!!
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            val gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg =
                forekomstType.biltype likEnAv listOf(
                    biltype.kode_varebilKlasse2,
                    biltype.kode_lastebilMedTotalvektUnder7500Kg
                )
            val erElektroniskKjoerebokFoertVedroerendeYrkeskjoering =
                modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering

            val gjelderPrivatBrukLastebil =
                forekomstType.biltype likEnAv listOf(
                    biltype.kode_lastebilMedTotalvekt7500KgEllerMer,
                    biltype.kode_bilRegistrertFor16PassasjererEllerFlere
                )

            hvis(
                (gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg)
                    || gjelderPrivatBrukLastebil
            ) {
                val antallKmPrivat =
                    forekomstType.antallKilometerKjoertIAar - forekomstType.antallKilometerYrkeskjoering
                val grenseForSats = satser.sats(privatBrukAvBil_kilometersatsVedPrivatBruk_grenseverdi)
                val kmSatsTilOgMedGrense =
                    satser.sats(privatBrukAvBil_kilometersatsVedPrivatBruk_satsIntervallTilOgMedGrenseverdiEn)
                val kmSatsFraGrense =
                    satser.sats(privatBrukAvBil_kilometersatsVedPrivatBruk_satsIntervallFraGrenseverdiEn)
                val resultat = if (gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg && erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.erSann()) {
                    antallKmPrivat * kmSatsTilOgMedGrense
                } else if (antallKmPrivat mindreEllerLik grenseForSats) {
                    antallKmPrivat * kmSatsTilOgMedGrense
                } else {
                    val kmMedLavereSats = (antallKmPrivat - grenseForSats) medMinimumsverdi 0
                    val del1 = grenseForSats * kmSatsTilOgMedGrense
                    val del2 = kmMedLavereSats * kmSatsFraGrense
                    del1 + del2
                }

                settFelt(kmSatsForPrivatBruk) { resultat }
            }
        }
    }

    internal val saldoavskrivningPrivatBrukKalkyle = kalkyle("saldoavskrivningPrivatBruk") {
        val satser = satser!!
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype.kode_personbilEllerBussFor9PassasjererEllerFlere,
                biltype.kode_varebilKlasse2,
                biltype.kode_lastebilMedTotalvektUnder7500Kg,
                biltype.kode_lastebilMedTotalvekt7500KgEllerMer,
                biltype.kode_bilRegistrertFor16PassasjererEllerFlere
            )
        } forHverForekomst {
            hvis(
                (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.leasingleie.harIkkeVerdi()
                    || modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.leasingleie lik 0)
                    && modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.erYrkesbilenIBrukPrivat.erSann()
            ) {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.saldoavskrivningPrivatBruk) {
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.listeprisSomNy * (1.toBigDecimal() - satser.sats(privatBrukAvBil_saldoavskrivning)).pow(
                        bilensAlder.tall()
                    ) *
                        satser.sats(privatBrukAvBil_saldoavskrivning) * antallDagerTilDisposisjon / antallDagerIAar(inntektsaar)
                }
            }
        }
    }

    internal val prosentandelAvFaktiskeKostnaderKalkyle = kalkyle("prosentandelAvFaktiskeKostnader") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype.kode_personbilEllerBussFor9PassasjererEllerFlere,
                biltype.kode_varebilKlasse2,
                biltype.kode_lastebilMedTotalvektUnder7500Kg,
                biltype.kode_lastebilMedTotalvekt7500KgEllerMer,
                biltype.kode_bilRegistrertFor16PassasjererEllerFlere
            )
        } forHverForekomst {
            settFelt(prosentandelAvFaktiskeKostnader) {
                if (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.leasingleie.erPositiv()) {
                    (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.drivstoffkostnad +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.vedlikeholdskostnad +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.kostnadTilForsikringOgAvgift +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.leasingleie) *
                        satser.sats(privatBrukAvBil_faktiskeKostnader)
                } else {
                    (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.drivstoffkostnad +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.vedlikeholdskostnad +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.kostnadTilForsikringOgAvgift +
                        modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.saldoavskrivningPrivatBruk) *
                        satser.sats(privatBrukAvBil_faktiskeKostnader)
                }
            }
        }
    }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyle =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbil") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.erYrkesbilenIBrukPrivat.erSann() &&
                forekomstType.biltype likEnAv listOf(biltype.kode_personbilEllerBussFor9PassasjererEllerFlere)
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                }
            }
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.erYrkesbilenIBrukPrivat.erSann() &&
                forekomstType.biltype likEnAv listOf(
                    biltype.kode_varebilKlasse2,
                    biltype.kode_lastebilMedTotalvektUnder7500Kg
                )
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    if (forekomstType.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.erSann()) {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall() medMaksimumsverdi kmSatsForPrivatBruk.tall()
                    } else {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    }
                }
            }
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.erYrkesbilenIBrukPrivat.erSann() &&
                forekomstType.biltype likEnAv listOf(
                    biltype.kode_lastebilMedTotalvekt7500KgEllerMer,
                    biltype.kode_bilRegistrertFor16PassasjererEllerFlere
                )
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    prosentandelAvFaktiskeKostnader.tall() medMaksimumsverdi kmSatsForPrivatBruk.tall()
                }
            }
        }

    internal val kalkyleSamling = Kalkylesamling(
        preprossesserMotorkjoeretoey,
        bilensAlderKalkyle,
        antallDagerTilDisposisjonKalkyle,
        antallMaanederTilDisposisjonKalkyle,
        satsPersonbilEllerBussFor9PassasjererEllerFlere,
        satsVareEllerLastebil,
        kmSatsForPrivatBrukKalkyle,
        saldoavskrivningPrivatBrukKalkyle,
        prosentAvBilensVerdiKalkyle,
        prosentAvBilensVerdiVarebilEllerLastebil,
        prosentandelAvFaktiskeKostnaderKalkyle,
        tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyle,
        tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil,
        tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil
    )
    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
