@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.datoInnenforInntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.*
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.biltype2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerMellom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.maanederMellom

internal object MotorkjoeretoeyINaering2022 : HarKalkylesamling {

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
        val inntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            settFelt(bilensAlder) {
                (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.aarForFoerstegangsregistrering - inntektsaar).absoluttverdi()
            }
        }
    }

    internal val disponertIPeriodeSkalVaereInnenforInntektsaarKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val inntektsaar = inntektsaar.gjeldendeInntektsaar

        val nyeElementer = mutableListOf<InformasjonsElement>()
        gm.grupper(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering)
            .forEach { forekomst ->
                val disponertFraOgMed =
                    forekomst.verdiFor(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertFraOgMedDato)
                val disponertTilOgMed =
                    forekomst.verdiFor(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertTilOgMedDato)

                if (disponertFraOgMed != null && disponertTilOgMed != null) {
                    var datoStart = datoInnenforInntektsaar(disponertFraOgMed, inntektsaar)
                    var datoSlutt = datoInnenforInntektsaar(disponertTilOgMed, inntektsaar)

                    if (datoStart > datoSlutt) {
                        val temp = datoSlutt
                        datoSlutt = datoStart
                        datoStart = temp
                    }

                    nyeElementer.add(
                        forekomst.felt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertFraOgMedDato)
                            .element()
                            .medVerdi(datoStart.toString())
                    )
                    nyeElementer.add(
                        forekomst.felt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.disponertTilOgMedDato)
                            .element()
                            .medVerdi(datoSlutt.toString())
                    )
                }
            }
        leggTilIKontekst(GeneriskModellForKalkyler.fra(nyeElementer))
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

    internal val initielleBeregningerAvSatsPersonbilEllerBilRegistrertFor9PassasjererEllerFlere =
        kalkyle("initielleBeregninger") {
            val satser = satser!!
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_personbil,
                    biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
                )
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

    internal val initielleBeregningerAvSatsElEllerHydrogenbil =
        kalkyle("initielleBeregningerAvSatsElEllerHydrogenbil") {
            val satser = satser!!
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_hydrogenbil,
                    biltype2022.kode_elbil
                )
            } forHverForekomst {
                settFelt(satsBeregningsgrunnlag1) {
                    if (bilensAlder mindreEllerLik 3) {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik satser.sats(privatBrukAvBil_grenseverdiForYrkeskjoering)) {
                            satser.sats(privatBrukAvBil_elbilNyereEnnTreAar)
                        } else {
                            satser.sats(privatBrukAvBil_elbilNyereEnnTreAarYrkeskjoeringOverGrenseverdi)
                        }
                    } else {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik satser.sats(privatBrukAvBil_grenseverdiForYrkeskjoering)) {
                            satser.sats(privatBrukAvBil_elbilEldreEnnTreAar)
                        } else {
                            satser.sats(privatBrukAvBil_elbilEldreEnnTreAarYrkeskjoeringOverGrenseverdi)
                        }
                    }
                }
            }
        }

    private val initielleBeregningerAvSatsVareEllerLastebil = kalkyle("initielleBeregningerAvSatsVareEllerLastebil") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg
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
                biltype2022.kode_personbil,
                biltype2022.kode_elbil,
                biltype2022.kode_hydrogenbil,
                biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 =
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.listeprisSomNy * satsBeregningsgrunnlag1
                ((beregningsgrunnlagAlt1 medMaksimumsverdi satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)) *
                    satser.sats(privatBrukAvBil_personbilSjablongUnderGrenseverdi) +
                    (beregningsgrunnlagAlt1 andelOver satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)) *
                    satser.sats(privatBrukAvBil_personbilSjablongOverGrenseverdi)) *
                    antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val prosentAvBilensVerdiVarebilEllerLastebil = kalkyle("prosentAvBilensVerdiVarebilEllerLastebil") {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 =
                    modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.listeprisSomNy * satsBeregningsgrunnlag1
                val beregningsgrunnlag =
                    beregningsgrunnlagAlt1 - ((beregningsgrunnlagAlt1 * satser.sats(privatBrukAvBil_varebilBunnfradrag)) medMaksimumsverdi satser.sats(privatBrukAvBil_varebilMaksverdiForBunnfradrag))
                ((beregningsgrunnlag medMaksimumsverdi satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)) *
                    satser.sats(privatBrukAvBil_personbilSjablongUnderGrenseverdi) +
                    (beregningsgrunnlag andelOver satser.sats(privatBrukAvBil_personbilGrenseverdiForSjablongberegning)) *
                    satser.sats(privatBrukAvBil_personbilSjablongOverGrenseverdi)) *
                    antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val kmSatsForPrivatBrukKalkyle = kalkyle("kmSatsForPrivatBruk") {
        val satser = satser!!
        forAlleForekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) {
            val gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg =
                modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                    biltype2022.kode_varebilKlasse2,
                    biltype2022.kode_lastebilMedTotalvektUnder7500Kg
                ) && modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.erSann()

            val gjelderPrivatBrukLastebil =
                modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                    biltype2022.kode_lastebilMedTotalvekt7500KgEllerMer,
                    biltype2022.kode_bilRegistrertFor16PassasjererEllerFlere
                )
            hvis(gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg || gjelderPrivatBrukLastebil) {
                settFelt(kmSatsForPrivatBruk) {
                    (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.antallKilometerKjoertIAar - modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.antallKilometerYrkeskjoering) * satser.sats(privatBrukAvBil_kilometersatsVedPrivatBruk)
                }
            }
        }
    }

    internal val saldoavskrivningPrivatBrukKalkyle = kalkyle("saldoavskrivningPrivatBruk") {
        val satser = satser!!
        val inntektsaar = inntektsaar
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
            modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                biltype2022.kode_personbil,
                biltype2022.kode_elbil,
                biltype2022.kode_hydrogenbil,
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg,
                biltype2022.kode_lastebilMedTotalvekt7500KgEllerMer,
                biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere,
                biltype2022.kode_bilRegistrertFor16PassasjererEllerFlere
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
                biltype2022.kode_personbil,
                biltype2022.kode_elbil,
                biltype2022.kode_hydrogenbil,
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg,
                biltype2022.kode_lastebilMedTotalvekt7500KgEllerMer,
                biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere,
                biltype2022.kode_bilRegistrertFor16PassasjererEllerFlere
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
                modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.biltype likEnAv listOf(
                    biltype2022.kode_personbil,
                    biltype2022.kode_elbil,
                    biltype2022.kode_hydrogenbil,
                    biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
                )
            } forHverForekomst {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    if (modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering.erYrkesbilenIBrukPrivat.erSann()) {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    } else {
                        (prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()) * 0
                    }
                }
            }
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_varebilKlasse2,
                    biltype2022.kode_lastebilMedTotalvektUnder7500Kg
                )
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    if (forekomstType.erYrkesbilenIBrukPrivat.erUsann()) {
                        prosentAvBilensVerdi * 0
                    } else if (forekomstType.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.erSann()) {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi kmSatsForPrivatBruk.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    } else {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    }
                }
            }
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleLastebil =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_lastebilMedTotalvekt7500KgEllerMer,
                    biltype2022.kode_bilRegistrertFor16PassasjererEllerFlere
                )
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    if (forekomstType.erYrkesbilenIBrukPrivat.erSann()) {
                        kmSatsForPrivatBruk.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    } else {
                        kmSatsForPrivatBruk * 0
                    }
                }
            }
        }

    internal val kalkyleSamling = Kalkylesamling(
        disponertIPeriodeSkalVaereInnenforInntektsaarKalkyle,
        bilensAlderKalkyle,
        antallDagerTilDisposisjonKalkyle,
        antallMaanederTilDisposisjonKalkyle,
        initielleBeregningerAvSatsPersonbilEllerBilRegistrertFor9PassasjererEllerFlere,
        initielleBeregningerAvSatsElEllerHydrogenbil,
        initielleBeregningerAvSatsVareEllerLastebil,
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
