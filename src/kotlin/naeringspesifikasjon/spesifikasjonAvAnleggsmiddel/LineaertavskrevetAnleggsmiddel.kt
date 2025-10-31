@file:Suppress("MoveLambdaOutsideParentheses")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import kotlin.math.ceil
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.erTryggAaDelePaa
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.antallDagerIAar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIAnskaffelsesaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.dagerEidIRealisasjonsaaret
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.KonsumprisindeksVannkraft.hentKonsumprisindeksVannkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.statisk

object LineaertavskrevetAnleggsmiddel : HarKalkylesamling {

    val konsumprisindeksjustertInvesteringskostnadKalkyle = kalkyle {
        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) forHverForekomst {
            val konsumprisindeks = hentKonsumprisindeksVannkraft(forekomstType.ervervsdato.aar())
            val konsumprisindeks1997 =
                hentKonsumprisindeksVannkraft(BigDecimal.valueOf(1997))

            hvis(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() && konsumprisindeks.erTryggAaDelePaa()) {

                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_konsumprisindeksjustertInvesteringskostnad) {
                    (forekomstType.anskaffelseskost * konsumprisindeks1997) / konsumprisindeks
                }
            }
        }
    }

    val gjenstaaendeLevetidKalkyle = kalkyle {
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann()
        } forHverForekomst {
            val avskrivningstid = avskrivningstid(forekomstType.levetid.tall())
            val aarSidenAnskaffelse = inntektsaar - forekomstType.ervervsdato.aar() + 1
            val gjenstaaendeLevetidAar = avskrivningstid - aarSidenAnskaffelse

            settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjenstaaendeLevetid) {
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

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_erAnleggsmiddelUnderUtfoerelse.erUsann() &&
                forekomstType.realisasjonsdato.harIkkeVerdi()
        } forHverForekomst {
            val kraftverk =
                kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi()]

            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val avskrivningstid = avskrivningstid(forekomstType.levetid.tall())
                val kapitaliseringsrente = satser.sats(Sats.vannkraft_kapitaliseringsrente).toDouble()
                settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverk) {
                    naaverdiAvFremtidigeUtskiftningskostnader(
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_konsumprisindeksjustertInvesteringskostnad.tall(),
                        kapitaliseringsrente,
                        avskrivningstid,
                        forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjenstaaendeLevetid.tall()
                    )
                }
            }
        }
    }

    val aaretsFriinntektKalkyle = kalkyle {
        val satser = satser!!
        val inntektsaar = statisk.naeringsspesifikasjon.inntektsaar.tall()
        val antallDagerIAar = antallDagerIAar(inntektsaar!!.toInt())

        val kraftverkMap = lagSpesifikasjonAvKraftverkMap()

        forekomsterAv(modell2023.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) der {
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_gjelderVannkraftverk.erSann() &&
                forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.harVerdi()
            forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_investeringskostnadErDirekteUtgiftsfoertIGrunnrenteinntekt.erUsann()
        } forHverForekomst {
            val kraftverk =
                kraftverkMap[forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_kraftverketsLoepenummer.verdi()]

            if (kraftverk != null && kraftverk.samletPaastempletMerkeytelseIKvaOverGrense) {
                val normRente = satser.sats(Sats.vannkraft_normrenteForFriinntekt)
                hvis(
                    kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() != inntektsaar && kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() != inntektsaar
                ) {
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        (forekomstType.inngaaendeVerdi + forekomstType.utgaaendeVerdi) / 2 * normRente
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedErvervIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIAnskaffelsesaaret(kraftverk.datoForOverdragelseVedErvervIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        (forekomstType.anskaffelseskost + forekomstType.utgaaendeVerdi) / 2 * normRente * (dagerEid / antallDagerIAar)
                    }
                }

                hvis(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret.aar() == inntektsaar) {
                    val dagerEid = dagerEidIRealisasjonsaaret(kraftverk.datoForOverdragelseVedRealisasjonIInntektsaaret)
                    settFelt(forekomstType.spesifikasjonAvOrdinaertAnleggsmiddelIKraftverk_aaretsFriinntekt) {
                        forekomstType.inngaaendeVerdi / 2 * normRente * (dagerEid / antallDagerIAar)
                    }
                }
            }
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        LineaertavskrevetAnleggsmiddelFra2024.antallAarErvervetKalkyle,
        LineaertavskrevetAnleggsmiddelFra2024.utgaaendeVerdiKalkyle,
        LineaertavskrevetAnleggsmiddelFra2024.grunnlagForAvskrivningOgInntektsfoeringKalkyle,
        konsumprisindeksjustertInvesteringskostnadKalkyle,
        gjenstaaendeLevetidKalkyle,
        naaverdiAvFremtidigeUtskiftningskostnaderForVannkraftverkKalkyle,
        aaretsFriinntektKalkyle
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}