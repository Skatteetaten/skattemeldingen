package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import mu.KotlinLogging
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.hentFoersteFeltMedVerdiEllerNull
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerIkkeEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerMellom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.AnleggsmiddeltypeForSaerskiltAnleggsmiddelIKraftverk.skattemessigLevetid
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

internal object AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk {
    private val logger = KotlinLogging.logger("AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk")
    internal val aaretsAvskrivningKalkyle = kalkyle("aaretsAvskrivningKalkyle") {
        val inntektsaar = inntektsaar
        val gjeldendeInnteksaar = inntektsaar.gjeldendeInntektsaar.toBigDecimal()
        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) forHverForekomst {
            val realisasjonsdato = forekomstType.realisasjonsdato.dato()
            val realisasjonsaar = realisasjonsdato?.year?.toBigDecimal()
            val anleggsmiddeltype = forekomstType.anleggsmiddeltype.verdi()

            forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {
                val avskrivningstid = anleggsmiddeltype.skattemessigLevetid(inntektsaar)
                hvis(realisasjonsaar != gjeldendeInnteksaar && forekomstType.anskaffelsesEllerPaakostningsdato.aar() != gjeldendeInnteksaar) {
                    val gjenstaaendeAvskrivningstid =
                        avskrivningstid - (gjeldendeInnteksaar - forekomstType.anskaffelsesEllerPaakostningsdato.aar())

                    if (gjenstaaendeAvskrivningstid.erPositiv()) {
                        settFelt(forekomstType.aaretsAvskrivning) {
                            hentFoersteFeltMedVerdiEllerNull(
                                forekomstType.historiskKostpris, forekomstType.gjenanskaffelsesverdiFoer1Januar1997
                            )?.tall() / avskrivningstid
                        }
                    } else if (gjenstaaendeAvskrivningstid lik 0) {
                        settFelt(forekomstType.aaretsAvskrivning) {
                            val dagerEid =
                                forekomstType.anskaffelsesEllerPaakostningsdato.dato()
                                    ?.let { dagerIkkeEidIAnskaffelsesaaret(it) }
                            (hentFoersteFeltMedVerdiEllerNull(
                                forekomstType.historiskKostpris, forekomstType.gjenanskaffelsesverdiFoer1Januar1997
                            )?.tall() / avskrivningstid) * (dagerEid / 365)
                        }
                    }
                }

                hvis(forekomstType.anskaffelsesEllerPaakostningsdato.aar() == gjeldendeInnteksaar) {
                    val dagerEid =
                        dagerEidIAnskaffelsesaaret(forekomstType.anskaffelsesEllerPaakostningsdato.dato())
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / avskrivningstid * (dagerEid / antallDagerIAar(inntektsaar))
                    }
                }

                hvis(realisasjonsaar == gjeldendeInnteksaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(realisasjonsdato)
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / avskrivningstid * (dagerEid / antallDagerIAar(inntektsaar))
                    }
                }

                hvis(realisasjonsaar == gjeldendeInnteksaar && forekomstType.anskaffelsesEllerPaakostningsdato.aar() == gjeldendeInnteksaar) {
                    val dagerEid = dagerMellom(
                        forekomstType.anskaffelsesEllerPaakostningsdato.dato(),
                        realisasjonsdato
                    )
                    settFelt(forekomstType.aaretsAvskrivning) {
                        forekomstType.historiskKostpris / avskrivningstid * (dagerEid / antallDagerIAar(inntektsaar))
                    }
                }
            }
        }
    }

    internal val konsumprisindeksjustertInvesteringskostnadKalkyle =
        kalkyle("konsumprisindeksjustertInvesteringskostnadKalkyle") {

            val konsumprisindeksInntektsaar =
                hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall())

            val konsumprisindeksDesemberInntektsaar =
                hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall(), forDesember = true)

            forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) forHverForekomst {

                val konsumprisindeks1996Desember =
                    hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1996), forDesember = true)

                val konsumprisindeksAnskaffelsesaar = hentKonsumprisindeksVannkraft(forekomstType.anskaffelsesEllerPaakostningsdato.aar())

                hvis(forekomstType.gjenanskaffelsesverdiFoer1Januar1997.harVerdi() && konsumprisindeks1996Desember.erTryggAaDelePaa()) {
                    settFelt(forekomstType.konsumprisindeksjustertInvesteringskostnad) {
                        forekomstType.gjenanskaffelsesverdiFoer1Januar1997 * konsumprisindeksDesemberInntektsaar / konsumprisindeks1996Desember
                    }
                }

                hvis(forekomstType.gjenanskaffelsesverdiFoer1Januar1997.harIkkeVerdi() && konsumprisindeksAnskaffelsesaar.erTryggAaDelePaa()) {
                    settFelt(forekomstType.konsumprisindeksjustertInvesteringskostnad) {
                        forekomstType.historiskKostpris * konsumprisindeksInntektsaar / konsumprisindeksAnskaffelsesaar
                    }
                }
            }
        }

    internal val naaverdiAvFremtidigeUtskiftningskostnaderKalkyle =
        kalkyle("naaverdiAvFremtidigeUtskiftningskostnaderKalkyle") {
            val inntektsaar = inntektsaar
            val satser = satser!!
            val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

            forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) der {
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
                            logger.warn { "SPAP-26728: forekomstType.konsumprisindeksjustertInvesteringskostnad: ${forekomstType.konsumprisindeksjustertInvesteringskostnad} kapitaliseringsrente: $kapitaliseringsrente" }
                        }
                        val teller = forekomstType.konsumprisindeksjustertInvesteringskostnad * (BigDecimal.ONE + kapitaliseringsrente).pow(eksponent1)

                        val nevner = ((BigDecimal.ONE + kapitaliseringsrente).pow(avskrivningstid) - 1)

                        if (forekomstType.gjenstaaendeLevetid.harVerdi()) {
                            settFelt(forekomstType.naaverdiAvFremtidigeUtskiftningskostnader) { teller / nevner }
                        }
                    }
                }
            }
        }
}