package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.biltype2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.satser.aar2020.TransportmiddelSatser
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

internal object TransportmiddelNaering2020 : HarKalkylesamling {

    internal val bilensAlder =
        opprettSyntetiskFelt(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering, "bilensAlder")
    internal val satsBeregningsgrunnlag1 =
        opprettSyntetiskFelt(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering, "satsBeregningsgrunnlag1")
    internal val prosentAvBilensVerdi =
        opprettSyntetiskFelt(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering, "prosentAvBilensVerdi")
    internal val prosentandelAvFaktiskeKostnader = opprettSyntetiskFelt(
        modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering,
        "prosentandelAvFaktiskeKostnader"
    )
    internal val kmSatsForPrivatBruk =
        opprettSyntetiskFelt(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering, "kmSatsForPrivatBruk")

    internal val initielleBeregningerKalkyle = kalkyle("initielleBeregninger") {
        val inntektsaar = statisk.naeringsopplysninger.inntektsaar.tall()
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) {
            settFelt(bilensAlder) {
                (forekomstType.registreringsaar - inntektsaar).absoluttverdi()
            }
        }
    }

    internal val initielleBeregningerAvSatsPersonbilEllerBilRegistrertFor9PassasjererEllerFlere =
        kalkyle("initielleBeregninger") {
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_personbil,
                    biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
                )
            } forHverForekomst {
                if (bilensAlder mindreEllerLik 3) {
                    settFelt(satsBeregningsgrunnlag1) {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik 40000) {
                            BigDecimal(1)
                        } else {
                            BigDecimal("0.75")
                        }
                    }
                } else {
                    settFelt(satsBeregningsgrunnlag1) {
                        if (forekomstType.antallKilometerYrkeskjoering mindreEllerLik 40000) {
                            BigDecimal("0.75")
                        } else {
                            BigDecimal("0.5625")
                        }
                    }
                }
            }
        }

    internal val initielleBeregningerAvSatsElEllerHydrogenbil =
        kalkyle("initielleBeregningerAvSatsElEllerHydrogenbil") {
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_hydrogenbil,
                    biltype2022.kode_elbil
                )
            } forHverForekomst {
                settFelt(satsBeregningsgrunnlag1) {
                    if (bilensAlder mindreEllerLik 3) {
                        BigDecimal("0.6")
                    } else {
                        BigDecimal("0.45")
                    }
                }
            }
        }

    internal val initielleBeregningerAvSatsVareEllerLastebil = kalkyle("initielleBeregningerAvSatsVareEllerLastebil") {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
            forekomstType.biltype likEnAv listOf(
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg
            )
        } forHverForekomst {
            settFelt(satsBeregningsgrunnlag1) {
                if (bilensAlder mindreEllerLik 3) {
                    BigDecimal(1)
                } else {
                    BigDecimal("0.75")
                }
            }
        }
    }

    internal val prosentAvBilensVerdiKalkyle = kalkyle("prosentAvBilensVerdi") {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
            forekomstType.biltype likEnAv listOf(
                biltype2022.kode_personbil,
                biltype2022.kode_elbil,
                biltype2022.kode_hydrogenbil,
                biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 = forekomstType.listeprisSomNy * satsBeregningsgrunnlag1
                ((beregningsgrunnlagAlt1 medMaksimumsverdi TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
                    TransportmiddelSatser.satsVedProsentAvBilensVerdiUnderInnslagspunkt2 +
                    (beregningsgrunnlagAlt1 andelOver TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
                    TransportmiddelSatser.satsVedProsentAvBilensVerdiOverInnslagspunkt2) *
                    forekomstType.antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val prosentAvBilensVerdiVarebilEllerLastebil = kalkyle("prosentAvBilensVerdiVarebilEllerLastebil") {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
            forekomstType.biltype likEnAv listOf(
                biltype2022.kode_varebilKlasse2,
                biltype2022.kode_lastebilMedTotalvektUnder7500Kg
            )
        } forHverForekomst {
            settFelt(prosentAvBilensVerdi) {
                val beregningsgrunnlagAlt1 = forekomstType.listeprisSomNy * satsBeregningsgrunnlag1
                val beregningsgrunnlag =
                    beregningsgrunnlagAlt1 - ((forekomstType.listeprisSomNy * TransportmiddelSatser.satsVedProsentandelAvBilensVerdiVareEllerLastebil) medMaksimumsverdi TransportmiddelSatser.bunnfradragSjablongberegningVarebilOgLastebil)
                ((beregningsgrunnlag medMaksimumsverdi TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
                    TransportmiddelSatser.satsVedProsentAvBilensVerdiUnderVarebilOgLastebil +
                    (beregningsgrunnlag andelOver TransportmiddelSatser.innslagspunktTrinn2Beregningsgrunnlag) *
                    TransportmiddelSatser.satsVedProsentAvBilensVerdiOverInnslagspunkt2) *
                    forekomstType.antallMaanederTilDisposisjon / 12
            }
        }
    }

    internal val kmSatsForPrivatBrukKalkyle = kalkyle("kmSatsForPrivatBruk") {
        forAlleForekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) {
            val gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg =
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_varebilKlasse2,
                    biltype2022.kode_lastebilMedTotalvektUnder7500Kg
                ) && forekomstType.erElektroniskKjoerebokFoertVedroerendeYrkeskjoering.erSann()

            val gjelderPrivatBrukLastebil = forekomstType.biltype likEnAv listOf(
                biltype2022.kode_lastebilMedTotalvekt7500KgEllerMer,
                biltype2022.kode_bilRegistrertFor16PassasjererEllerFlere
            )
            hvis(gjelderPrivatBrukVarebilKlasse2EllerLastebilMedTotalvektUnder7500kg || gjelderPrivatBrukLastebil) {
                settFelt(kmSatsForPrivatBruk) {
                    (forekomstType.antallKilometerKjoertIAar - forekomstType.antallKilometerYrkeskjoering) * TransportmiddelSatser.kmSats
                }
            }
        }
    }

    internal val saldoavskrivningPrivatBrukKalkyle = kalkyle("saldoavskrivningPrivatBruk") {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
            forekomstType.biltype likEnAv listOf(
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
                (forekomstType.leasingleie.harIkkeVerdi()
                    || forekomstType.leasingleie lik 0)
                    && forekomstType.erYrkesbilenIBrukPrivat.erSann()
            ) {
                settFelt(forekomstType.saldoavskrivningPrivatBruk) {
                    forekomstType.listeprisSomNy * TransportmiddelSatser.sats1SaldoavskrivningPrivatBruk.pow(bilensAlder.tall()) *
                        TransportmiddelSatser.sats2SaldoavskrivningPrivatBruk * forekomstType.antallMaanederTilDisposisjon / 12
                }
            }
        }
    }

    internal val prosentandelAvFaktiskeKostnaderKalkyle = kalkyle("prosentandelAvFaktiskeKostnader") {
        forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
            forekomstType.biltype likEnAv listOf(
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
                if (forekomstType.leasingleie.erPositiv()) {
                    (forekomstType.driftskostnader +
                        forekomstType.leasingleie) *
                        TransportmiddelSatser.satsVedProsentandelAvFaktiskeKostnader
                } else {
                    (forekomstType.driftskostnader +
                        forekomstType.saldoavskrivningPrivatBruk) *
                        TransportmiddelSatser.satsVedProsentandelAvFaktiskeKostnader
                }
            }
        }
    }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyle =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbil") {
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
                forekomstType.biltype likEnAv listOf(
                    biltype2022.kode_personbil,
                    biltype2022.kode_elbil,
                    biltype2022.kode_hydrogenbil,
                    biltype2022.kode_bilRegistrertFor9PassasjererEllerFlere
                )
            } forHverForekomst {
                settFelt(forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil) {
                    if (forekomstType.erYrkesbilenIBrukPrivat.erSann()) {
                        prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()
                    } else {
                        (prosentAvBilensVerdi.tall() medMaksimumsverdi prosentandelAvFaktiskeKostnader.tall()) * 0
                    }
                }
            }
        }

    internal val tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil =
        kalkyle("tilbakefoertBilkostnadForPrivatBrukAvNaeringsbilKalkyleVarebil") {
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
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
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            initielleBeregningerKalkyle,
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
    }
}