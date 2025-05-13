@file:Suppress("MoveLambdaOutsideParentheses")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import kotlin.math.ceil
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

object LineaertavskrevetAnleggsmiddelFra2024 : HarKalkylesamling {

    internal val antallAarErvervet =
        opprettSyntetiskFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel, "antallAarErvervet")
    internal val antallAarErvervetKalkyle = kalkyle {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
            settFelt(antallAarErvervet) {
                (modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.ervervsdato.aar() - inntektsaar).absoluttverdi()
            }
        }
    }

    internal val utgaaendeVerdiKalkyle = kalkyle {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
            hvis(antallAarErvervet stoerreEnn 0) {
                val utgaaendeVerdi = modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.inngaaendeVerdi -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.offentligTilskudd +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringAvInngaaendeMva +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringForAapenbarVerdiendring -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.aaretsAvskrivning -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttak +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.gevinstOverfoertTilGevinstOgTapskonto -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.tapOverfoertTilGevinstOgTapskonto +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.verdiOverfoertFraPaakostningVedRealisasjon -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.verdiOverfoertTilAnleggsmiddelVedRealisasjon

                settFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.utgaaendeVerdi) { utgaaendeVerdi.nullHvisNegativt() }
            }

            hvis(antallAarErvervet lik 0) {
                val paakostningEllerAnskaffelseskost =
                    if (modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.paakostning.harIkkeVerdi()) {
                        modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.anskaffelseskost.tall()
                    } else {
                        modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.paakostning.tall()
                    }

                val utgaaendeVerdi = paakostningEllerAnskaffelseskost -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.offentligTilskudd +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringAvInngaaendeMva +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringForAapenbarVerdiendring -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.aaretsAvskrivning -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttak +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.gevinstOverfoertTilGevinstOgTapskonto -
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.tapOverfoertTilGevinstOgTapskonto

                settFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.utgaaendeVerdi) { utgaaendeVerdi.nullHvisNegativt() }
            }
        }
    }

    internal val grunnlagForAvskrivningOgInntektsfoeringKalkyle = kalkyle {
        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
            val korrigeringer = modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringAvInngaaendeMva +
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.justeringForAapenbarVerdiendring -
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.offentligTilskudd -
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttak +
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.vederlagVedRealisasjonOgUttakInntektsfoertIAar -
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.reinvestertBetingetSkattefriSalgsgevinst -
                modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.nedskrivningPaaNyanskaffelserMedBetingetSkattefriSalgsgevinst

            val erErvervetIInntektsaaret = antallAarErvervet lik 0
            hvis(erErvervetIInntektsaaret && modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.paakostning.harVerdi()) {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering) {
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.paakostning + korrigeringer
                }
            }

            hvis(erErvervetIInntektsaaret && modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.paakostning.harIkkeVerdi()) {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering) {
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.anskaffelseskost + korrigeringer
                }
            }

            hvis(!erErvervetIInntektsaaret) {
                settFelt(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering) {
                    modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel.inngaaendeVerdi + korrigeringer
                }
            }
        }
    }

    val konsumprisindeksjustertInvesteringskostnadKalkyle = kalkyle {
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()
        val konsumprisindeksInntektsaar =
            hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall())

        val konsumprisindeksDesemberInntektsaar =
            hentKonsumprisindeksVannkraft(statisk.naeringsspesifikasjon.inntektsaar.tall(), forDesember = true)

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.harVerdi() && forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            hvis(forekomstType.ervervsdato.harVerdi()) {
                val kraftverk =
                    kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]
                hvis(kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
                        val konsumprisindeks1996Desember =
                            hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1996), forDesember = true)

                        val konsumprisindeksAnskaffelsesaar =
                            hentKonsumprisindeksVannkraft(forekomstType.ervervsdato.dato().aar())

                        hvis(
                            forekomstType.ervervsdato.dato().aar() mindreEllerLik 1996 &&
                                konsumprisindeks1996Desember.erTryggAaDelePaa()
                        ) {
                            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad) {
                                forekomstType.anskaffelseskost * konsumprisindeksDesemberInntektsaar / konsumprisindeks1996Desember
                            }
                        }

                        hvis(
                            forekomstType.ervervsdato.dato().aar() stoerreEllerLik 1997 &&
                                konsumprisindeksAnskaffelsesaar.erTryggAaDelePaa()
                        ) {
                            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad) {
                                forekomstType.anskaffelseskost * konsumprisindeksInntektsaar / konsumprisindeksAnskaffelsesaar
                            }
                        }
                    }
                }
            }
        }
    }


    val gjenstaaendeLevetidKalkyle = kalkyle {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei
        } forHverForekomst {
            val avskrivningstid = avskrivningstid(forekomstType.levetid.tall())
            val aarSidenAnskaffelse = inntektsaar - forekomstType.ervervsdato.aar() + 1
            val gjenstaaendeLevetidAar = avskrivningstid - aarSidenAnskaffelse

            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_gjenstaaendeLevetid) {
                if (gjenstaaendeLevetidAar.erNegativ()) BigDecimal.ZERO else gjenstaaendeLevetidAar
            }
        }
    }

    fun avskrivningstid(levetid: BigDecimal?): BigDecimal? {
        if (levetid == null) return null

        return ceil(levetid.toDouble() / 12.0).toBigDecimal()
    }

    val naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle = kalkyle {
        val satser = satser!!
        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann() &&
                forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            val kraftverk =
                kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]

            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val avskrivningstid = avskrivningstid(forekomstType.levetid.tall())
                val kapitaliseringsrente = satser.sats(Sats.vannkraft_kapitaliseringsrente).toDouble()
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk) {
                    naaverdiAvFremtidigeUtskiftningskostnader(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_konsumprisindeksjustertInvesteringskostnad.tall(),
                        kapitaliseringsrente,
                        avskrivningstid,
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_gjenstaaendeLevetid.tall()
                    )
                }
            }
        }
    }

    val aaretsFriinntektKalkyle = kalkyle {
        val satser = satser!!
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()

        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet.harVerdi() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_benyttesIGrunnrenteskattepliktigVirksomhet ulik benyttesIGrunnrenteskattepliktigVirksomhetMedAvskrivningsregel.kode_nei &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.harVerdi()
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
        } forHverForekomst {
            val kraftverk =
                kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_kraftverketsLoepenummer.verdi()]

            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                hvis(
                    kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIAnskaffelsesaaret(kraftverk.datoForOverdragelseVedErvervIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        (forekomstType.anskaffelseskost + forekomstType.utgaaendeVerdi) / 2 * normRente * (dagerEid / 365)
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIVannkraftverk_aaretsFriinntekt) {
                        forekomstType.inngaaendeVerdi / 2 * normRente * (dagerEid / 365)
                    }
                }
            }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        antallAarErvervetKalkyle,
        utgaaendeVerdiKalkyle,
        grunnlagForAvskrivningOgInntektsfoeringKalkyle,
        konsumprisindeksjustertInvesteringskostnadKalkyle,
        gjenstaaendeLevetidKalkyle,
        naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle,
        aaretsFriinntektKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}