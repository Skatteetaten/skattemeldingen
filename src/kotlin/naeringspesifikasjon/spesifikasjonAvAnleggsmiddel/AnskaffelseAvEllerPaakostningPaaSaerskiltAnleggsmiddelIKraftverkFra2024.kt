package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import mu.KotlinLogging
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.hentFoersteFeltMedVerdiEllerNull
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerIkkeEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerMellom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.AnleggsmiddeltypeForSaerskiltAnleggsmiddelIKraftverk.minsteGjenstaaendeLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.AnleggsmiddeltypeForSaerskiltAnleggsmiddelIKraftverk.skattemessigLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

internal object AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024 {
    private val logger = KotlinLogging.logger("AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk")
    internal val aaretsAvskrivningKalkyle = kalkyle("aaretsAvskrivningKalkyle") {
        val gjeldendeInntektsaar = inntektsaar.gjeldendeInntektsaar
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            val realisasjonsdato = forekomstType.realisasjonsdato.dato()
            val realisasjonsaar = realisasjonsdato?.year?.toBigDecimal()

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                hvis(realisasjonsaar != inntektsaar && forekomstType.anskaffelsesEllerPaakostningsdato.aar() != inntektsaar) {
                    val gjenstaaendeAvskrivningstid =
                        forekomstType.avskrivningstid.tall() - (inntektsaar - forekomstType.anskaffelsesEllerPaakostningsdato.aar())

                    if (gjenstaaendeAvskrivningstid.erPositiv()) {
                        settFelt(forekomstType.aaretsAvskrivning) {
                            hentFoersteFeltMedVerdiEllerNull(
                                forekomstType.historiskKostpris, forekomstType.gjenanskaffelsesverdiFoer1Januar1997
                            )?.tall() / forekomstType.avskrivningstid.tall()
                        }
                    } else if (gjenstaaendeAvskrivningstid lik 0) {
                        settFelt(forekomstType.aaretsAvskrivning) {
                            val dagerEid =
                                forekomstType.anskaffelsesEllerPaakostningsdato.dato()
                                    ?.let { dagerIkkeEidIAnskaffelsesaaret(it) }
                            (hentFoersteFeltMedVerdiEllerNull(
                                forekomstType.historiskKostpris, forekomstType.gjenanskaffelsesverdiFoer1Januar1997
                            )?.tall() / forekomstType.avskrivningstid.tall()) * (dagerEid / antallDagerIAar(gjeldendeInntektsaar))
                        }
                    }
                }

                hvis(forekomstType.anskaffelsesEllerPaakostningsdato.aar() == inntektsaar) {
                    val dagerEid =
                        dagerEidIAnskaffelsesaaret(forekomstType.anskaffelsesEllerPaakostningsdato.dato())
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / forekomstType.avskrivningstid.tall() * (dagerEid / antallDagerIAar(gjeldendeInntektsaar))
                    }
                }

                hvis(realisasjonsaar == inntektsaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(realisasjonsdato)
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / forekomstType.avskrivningstid.tall() * (dagerEid / antallDagerIAar(gjeldendeInntektsaar))
                    }
                }

                hvis(realisasjonsaar == inntektsaar && forekomstType.anskaffelsesEllerPaakostningsdato.aar() == inntektsaar) {
                    val dagerEid = dagerMellom(
                        forekomstType.anskaffelsesEllerPaakostningsdato.dato(),
                        realisasjonsdato
                    )
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / forekomstType.avskrivningstid.tall() * (dagerEid / antallDagerIAar(gjeldendeInntektsaar))
                    }
                }
            }
        }
    }

    internal val utgaaendeVerdiKalkyle = kalkyle("utgaaendeVerdiKalkyle") {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            val realisasjonsdato = forekomstType.realisasjonsdato.aar()
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                hvis(forekomstType.inngaaendeVerdi.harVerdi()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        forekomstType.inngaaendeVerdi - forekomstType.aaretsAvskrivning
                    }
                }
                hvis(!forekomstType.inngaaendeVerdi.harVerdi()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        hentFoersteFeltMedVerdiEllerNull(
                            forekomstType.historiskKostpris, forekomstType.gjenanskaffelsesverdiFoer1Januar1997
                        )?.tall() - forekomstType.aaretsAvskrivning
                    }
                }
                hvis(realisasjonsdato.harVerdi()) {
                    settFelt(forekomstType.utgaaendeVerdi) {
                        BigDecimal.ZERO
                    }
                }
            }
        }
    }

    internal val konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdiKalkyle =
        kalkyle("konsumprisindeksjustertInvesteringskostnadKalkyle") {
            val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

            val konsumprisindeksInntektsaar =
                hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall())

            val konsumprisindeksDesemberInntektsaar =
                hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall(), forDesember = true)

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                forekomstType.kraftverketsLoepenummer.harVerdi() && forekomstType.realisasjonsdato.harIkkeVerdi()
            } forHverForekomst {
                val kraftverk = kraftverkMap[forekomstType.kraftverketsLoepenummer.verdi()]
                hvis(kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                        val konsumprisindeks1996Desember =
                            hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1996), forDesember = true)

                        val konsumprisindeksAnskaffelsesaar =
                            hentKonsumprisindeksVannkraft(forekomstType.anskaffelsesEllerPaakostningsdato.aar())

                        hvis(forekomstType.gjenanskaffelsesverdiFoer1Januar1997.harVerdi() && konsumprisindeks1996Desember.erTryggAaDelePaa()) {
                            settFelt(forekomstType.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdi) {
                                forekomstType.gjenanskaffelsesverdiFoer1Januar1997 * konsumprisindeksDesemberInntektsaar / konsumprisindeks1996Desember
                            }
                        }

                        hvis(forekomstType.gjenanskaffelsesverdiFoer1Januar1997.harIkkeVerdi() && konsumprisindeksAnskaffelsesaar.erTryggAaDelePaa()) {
                            settFelt(forekomstType.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdi) {
                                forekomstType.historiskKostpris * konsumprisindeksInntektsaar / konsumprisindeksAnskaffelsesaar
                            }
                        }
                    }
                }
            }
        }

    internal val gjenstaaendeLevetidKalkyle = kalkyle("gjenstaaendeLevetidKalkyle") {
        val inntektsaar = inntektsaar
        val gjeldendeInntektsaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            hvis(forekomstType.anleggsmiddeltype.harVerdi()) {
                val avskrivningstid = forekomstType.anleggsmiddeltype.verdi().skattemessigLevetid(inntektsaar)
                val minsteGjenstaaendelevetid = forekomstType.anleggsmiddeltype.verdi().minsteGjenstaaendeLevetid(inntektsaar)

                forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) der {
                    forekomstType.anskaffelsesEllerPaakostningsdato.harVerdi() && forekomstType.anskaffelsesEllerPaakostningsdato.aar()!!
                        .mindreEllerLik(gjeldendeInntektsaar)
                } forHverForekomst {
                    val aarSidenPaakostningEllerAnskaffelse =
                        gjeldendeInntektsaar - forekomstType.anskaffelsesEllerPaakostningsdato.aar() + 1 // Inklusiv inntektsaaret, legger derfor til 1
                    val gjenstaaendeLevetid = avskrivningstid - aarSidenPaakostningEllerAnskaffelse
                    settFelt(forekomstType.gjenstaaendeLevetid) {
                        gjenstaaendeLevetid.medMinimumsverdi(minsteGjenstaaendelevetid)
                    }
                }
            }
        }
    }

    internal val naaverdiAvFremtidigeUtskiftningskostnaderKalkyle =
        kalkyle("naaverdiAvFremtidigeUtskiftningskostnaderKalkyle") {
            val inntektsaar = inntektsaar
            val satser = satser!!
            val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
                forekomstType.kraftverketsLoepenummer.harVerdi() && forekomstType.realisasjonsdato.harIkkeVerdi()
            } forHverForekomst {
                val kraftverk = kraftverkMap[forekomstType.kraftverketsLoepenummer.verdi()]

                val anleggsmiddeltype = forekomstType.anleggsmiddeltype.verdi()
                val avskrivningstid = anleggsmiddeltype.skattemessigLevetid(inntektsaar)

                if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                        val kapitaliseringsrente = satser.sats(Sats.vannkraft_kapitaliseringsrente).toDouble()

                        val eksponent1 = avskrivningstid - forekomstType.gjenstaaendeLevetid

                        if (eksponent1.erNegativ()) {
                            logger.warn { "SPAP-26728: anleggsmiddeltype: $anleggsmiddeltype avskrivningstid: $avskrivningstid - forekomstType.gjenstaaendeLevetid: ${forekomstType.gjenstaaendeLevetid} eksponent1 er negativ." }
                            logger.warn { "SPAP-26728: forekomstType.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdi: ${forekomstType.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdi} kapitaliseringsrente: $kapitaliseringsrente" }
                        }
                        val teller = forekomstType.konsumprisindeksjustertInvesteringskostnadForBeregningAvNaaverdi * (BigDecimal.ONE + kapitaliseringsrente).pow(eksponent1)

                        val nevner = ((BigDecimal.ONE + kapitaliseringsrente).pow(avskrivningstid) - 1)

                        if (forekomstType.gjenstaaendeLevetid.harVerdi()) {
                            settFelt(forekomstType.naaverdiAvFremtidigeUtskiftningskostnader) { teller / nevner }
                        }
                    }
                }
            }
        }

    internal val aaretsFriinntektKalkyle = kalkyle("aaretsFriinntektKalkyle") {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        val satser = satser!!
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
            forekomstType.kraftverketsLoepenummer.harVerdi()
        } forHverForekomst {
            val kraftverk = kraftverkMap[forekomstType.kraftverketsLoepenummer.verdi()]
            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense && forekomstType.investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()) {
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                    val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                    hvis(
                        kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                    ) {
                        settFelt(forekomstType.aaretsFriinntekt) {
                            (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                        }
                    }

                    hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                        settFelt(forekomstType.aaretsFriinntekt) {
                            (forekomstType.historiskKostpris + forekomstType.utgaaendeVerdi) / 2 * normRente
                        }
                    }

                    hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                        settFelt(forekomstType.aaretsFriinntekt) {
                            forekomstType.inngaaendeVerdi / 2 * normRente
                        }
                    }
                }
            }
        }
    }

    internal val gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktetKalkyle =
        kalkyle("gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktetKalkyle") {
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                hvis(forekomstType.realisasjonsdato.harVerdi()) {
                    forekomsterAv(forekomstType.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                        settFelt(forekomstType.gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktet) {
                            forekomstType.inngaaendeVerdi - forekomstType.aaretsAvskrivning
                        }
                    }
                }
            }
        }

    internal val konsumprisindeksjustertInvesteringskostnadForKorrigeringAvKommunefordelingAvEiendomsskattegrunnlagKalkyle =
        kalkyle("konsumprisindeksjustertInvesteringskostnadForKorrigeringAvKommunefordelingAvEiendomsskattegrunnlagKalkyle") {
            val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
            val konsumprisindeks1997Desember =
                hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1997))
            val konsumprisindeksInntektsaar =
                hentKonsumprisindeksVannkraft(inntektsaar)
            forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                hvis(forekomstType.anskaffelsesEllerPaakostningsdato.aar() == inntektsaar) {
                    settFelt(forekomstType.konsumprisindeksjustertInvesteringskostnadForKorrigeringAvKommunefordelingAvEiendomsskattegrunnlag) {
                        forekomstType.historiskKostpris * konsumprisindeks1997Desember / konsumprisindeksInntektsaar
                    }
                }
            }
        }
}