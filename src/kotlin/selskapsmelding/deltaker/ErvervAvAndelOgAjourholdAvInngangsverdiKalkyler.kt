package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErPersonlig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object ErvervAvAndelOgAjourholdAvInngangsverdiKalkyler : HarKalkylesamling {

    internal val kostprisVedErvervAvAndelKalkyle = kalkyle {
        forekomsterAv(modell.deltaker.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
            val kostpris = forekomstType.vederlag +
                forekomstType.kjoepskostnad -
                forekomstType.overtattNegativInngangsverdiForAndelSomArvEllerGave +
                forekomstType.overtattInngangsverdiForAndelSomArvEllerGave -
                forekomstType.kostprisPaaRealiserteAndeler +
                forekomstType.tilfoertKapitalUtenOekningAvEierandel -
                forekomstType.ikkeSkattepliktigTilbakebetaltKapital +
                forekomstType.akkumulertRiskPaaAndelVedInngangTil2006

            if (kostpris stoerreEllerLik 0) {
                settFelt(forekomstType.samletKostpris) { kostpris }
            } else {
                settFelt(forekomstType.samletNegativKostpris) {
                    kostpris.absoluttverdi()
                }
            }
        }
    }

    internal val ubenyttetSkjermingsfradragTilFremfoeringKalkyle = kalkyle {
        forekomsterAv(modell.deltaker) forHverForekomst {
            hvis (deltakerErPersonlig() && this@kalkyle.erNokus()) {
                forAlleForekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) {
                    hvis(forekomstType.aaretsSkjermingsfradrag stoerreEnn forekomstType.skattepliktigUtbytteFoerSkjermingsfradrag) {
                        settFelt(forekomstType.ubenyttetSkjermingsfradragTilFremfoering) {
                            forekomstType.aaretsSkjermingsfradrag -
                                forekomstType.skattepliktigUtbytteFoerSkjermingsfradrag
                        }
                    }
                }
            }
        }
    }

    object SkjermingsfradragNokus {

        internal val skjermingsgrunnlagKalkyle = kalkyle {
            forekomsterAv(modell.deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && this@kalkyle.erNokus()) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
                        settFelt(forekomstType.skjermingsgrunnlag) {
                            (forekomstType.samletKostpris -
                                forekomstType.samletNegativKostpris +
                                forekomstType.ubenyttetSkjermingsfradragFraTidligereAar).somHeltall()
                        }
                    }
                }
            }
        }

        internal val aaretsSkjermingsfradragKalkyle = kalkyle {
            val satser = satser!!
            forekomsterAv(modell.deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && this@kalkyle.erNokus()) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
                        val aaretsSkjermingsfradrag = forekomstType.skjermingsgrunnlag * satser.sats(
                            Sats.skjermingsrenteForPersonligeAksjonaererOgDeltakereIAnsvarligSelskapMv
                        )
                        settFelt(forekomstType.aaretsSkjermingsfradrag) {
                            (aaretsSkjermingsfradrag medMinimumsverdi 0).somHeltall()
                        }
                    }
                }
            }
        }

        internal val skattepliktigUtbytteFoerSkjermingsfradragKalkyle = kalkyle {
            val satser = satser!!
            forekomsterAv(modell.deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && this@kalkyle.erNokus()) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
                        val skattepliktigUtbytteFoerSkjermingsfradrag = (
                            (forekomstType.mottattSkatterettsligUtbytte + forekomstType.andelKreditfradrag) *
                                ((BigDecimal.ONE - satser.sats(Sats.skattPaaAlminneligInntekt_sats))))

                        settFelt(forekomstType.skattepliktigUtbytteFoerSkjermingsfradrag) {
                            (skattepliktigUtbytteFoerSkjermingsfradrag medMinimumsverdi 0).somHeltall()
                        }
                    }
                }
            }
        }

        internal val skattepliktigUtbytteKalkyle = kalkyle {
            forekomsterAv(modell.deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && this@kalkyle.erNokus()) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
                        val skattepliktigUtbytteEtterSkjermingsfradrag =
                            (forekomstType.skattepliktigUtbytteFoerSkjermingsfradrag -
                                forekomstType.aaretsSkjermingsfradrag -
                                forekomstType.ubenyttetSkjermingsfradragFraTidligereAar)
                        hvis(skattepliktigUtbytteEtterSkjermingsfradrag stoerreEllerLik 0) {
                            settFelt(forekomstType.skattepliktigUtbytte) {
                                skattepliktigUtbytteEtterSkjermingsfradrag
                            }
                        }
                    }
                }
            }
        }

        internal val ubenyttetSkjermingsfradragTilFremfoeringKalkyle = kalkyle {
            forekomsterAv(modell.deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && this@kalkyle.erNokus()) {
                    forekomsterAv(forekomstType.ervervAvAndelOgAjourholdAvInngangsverdi) forHverForekomst {
                        val skattepliktigUtbytteEtterSkjermingsfradrag =
                            (forekomstType.skattepliktigUtbytteFoerSkjermingsfradrag -
                                forekomstType.aaretsSkjermingsfradrag -
                                forekomstType.ubenyttetSkjermingsfradragFraTidligereAar)
                        hvis(skattepliktigUtbytteEtterSkjermingsfradrag mindreEnn 0) {
                            settFelt(forekomstType.ubenyttetSkjermingsfradragTilFremfoering) {
                                skattepliktigUtbytteEtterSkjermingsfradrag.absoluttverdi()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            kostprisVedErvervAvAndelKalkyle,
            ubenyttetSkjermingsfradragTilFremfoeringKalkyle,
            SkjermingsfradragNokus.skjermingsgrunnlagKalkyle,
            SkjermingsfradragNokus.aaretsSkjermingsfradragKalkyle,
            SkjermingsfradragNokus.skattepliktigUtbytteFoerSkjermingsfradragKalkyle,
            SkjermingsfradragNokus.skattepliktigUtbytteKalkyle,
            SkjermingsfradragNokus.ubenyttetSkjermingsfradragTilFremfoeringKalkyle,
        )
    }
}