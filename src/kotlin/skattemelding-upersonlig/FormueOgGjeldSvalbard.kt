@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.core.kodeliste.Eiendomstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.FormueOgGjeld.satsForFormuesobjekttype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skatteplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.KOMMUNENUMMER_SVALBARD
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.LANDKODE_NORGE

object FormueOgGjeldSvalbard : HarKalkylesamling {

    private fun GeneriskModellKontekst.eiendommerSomSkalTilSvalbard(harIkkeSkattepliktTilNorge: Boolean) =
        forekomsterAv(modell.fastEiendom) der {
            forekomstType.kommunenummerBeliggenhet.verdi() == KOMMUNENUMMER_SVALBARD
                || (
                harIkkeSkattepliktTilNorge
                    && (forekomstType.landkodeBeliggenhet.harVerdi()
                    && forekomstType.landkodeBeliggenhet.verdi() != LANDKODE_NORGE)
                )
        }


    private val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle {

        val verdiFoerVerdsettingsrabattFormuesobjekt =
            forekomsterAv(modell.formuesobjektSvalbard) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val eiendommerSomSkalTilSvalbard = eiendommerSomSkalTilSvalbard(skatteplikt.harSkattepliktTilNorge.erUsann())

        val verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom =
            eiendommerSomSkalTilSvalbard summerVerdiFraHverForekomst {
                forekomsterAv(forekomstType.fastEiendomSomFormuesobjekt) summerVerdiFraHverForekomst {
                    if (forekomstType.eiendomstype.equals(Eiendomstype.flerboligbygning)) {
                        if (forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.harVerdi()) {
                            forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                        } else {
                            forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                            }
                        }
                    } else {
                        forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                    }
                }
            }

        val formueOgInntektISelskapMedDeltakerfastsetting =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                KOMMUNENUMMER_SVALBARD == forekomstType.kommunenummer.verdi()
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForNettoformue.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt =
            verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom +
                verdiFoerVerdsettingsrabattFormuesobjekt +
                formueOgInntektISelskapMedDeltakerfastsetting

        hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
            settUniktFelt(modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt) {
                sumVerdiFoerVerdsettingsrabatt
            }
        }
    }

    internal val verdsettingsrabatt = kalkyle {
        val satser = satser!!
        val inntektsaar = inntektsaar

        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            forekomsterAv(modell.formuesobjektSvalbard) der {
                satser.satsForFormuesobjekttype(inntektsaar, forekomstType.formuesobjekttype.verdi()).harVerdi()
            } forHverForekomst {
                val sats = 1.toBigDecimal() - satser.satsForFormuesobjekttype(inntektsaar, forekomstType.formuesobjekttype.verdi())
                settFelt(forekomstType.verdsettingsrabatt) {
                    (forekomstType.verdiFoerEventuellVerdsettingsrabatt * sats).somHeltall()
                }
            }
        }
    }

    internal val formuesverdi = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            forekomsterAv(modell.formuesobjektSvalbard) forHverForekomst {
                settFelt(forekomstType.formuesverdi) {
                    forekomstType.verdiFoerEventuellVerdsettingsrabatt - forekomstType.verdsettingsrabatt
                }
            }
        }
    }

    internal val samletFormuesverdiEtterVerdsettingsrabatt = kalkyle {
        val satser = satser!!
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {

            val sumFormuesverdiFraFormuesobjekt =
                forekomsterAv(modell.formuesobjektSvalbard) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdi.tall()
                }

            val eiendommerSomSkalTilSvalbard = eiendommerSomSkalTilSvalbard(skatteplikt.harSkattepliktTilNorge.erUsann())

            val formuesverdiForFormuesandelForFastEiendom =
                eiendommerSomSkalTilSvalbard summerVerdiFraHverForekomst {
                    forekomsterAv(forekomstType.fastEiendomSomFormuesobjekt) summerVerdiFraHverForekomst {
                        if (forekomstType.eiendomstype.equals(Eiendomstype.flerboligbygning)) {
                            if (forekomstType.formuesverdiForFormuesandel.harVerdi()) {
                                forekomstType.formuesverdiForFormuesandel.tall()
                            } else {
                                forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                                    forekomstType.formuesverdiForFormuesandel.tall()
                                }
                            }
                        } else {
                            forekomstType.formuesverdiForFormuesandel.tall()
                        }
                    }
                }

            val formueOgInntektISelskapMedDeltakerfastsetting =
                forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                    forekomstType.kommunenummer.verdi() == KOMMUNENUMMER_SVALBARD
                } summerVerdiFraHverForekomst {
                    val sats = satser.sats(verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF)
                    (forekomstType.verdiFoerVerdsettingsrabattForNettoformue * sats).somHeltall()
                }

            val verdiFoerVerdsettingsrabattKapitalisertFesteavgift =
                forekomsterAv(modell.kapitalisertFesteavgift) der {
                    KOMMUNENUMMER_SVALBARD == forekomstType.kommunenummer.verdi()
                } summerVerdiFraHverForekomst {
                    forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift.tall()
                }

            val formuesverdiForKapitalisertFesteavgift =
                forekomsterAv(modell.kapitalisertFesteavgift) der {
                    forekomstType.kommunenummer.verdi() == KOMMUNENUMMER_SVALBARD
                } summerVerdiFraHverForekomst {
                    val sats = satser.sats(verdiFoerVerdsettingsrabattForKapitalisertFesteavgift)
                    (forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift * sats).somHeltall()
                }

            val sumVerdiFoerVerdsettingsrabatt = sumFormuesverdiFraFormuesobjekt +
                formuesverdiForFormuesandelForFastEiendom +
                verdiFoerVerdsettingsrabattKapitalisertFesteavgift +
                formueOgInntektISelskapMedDeltakerfastsetting +
                formuesverdiForKapitalisertFesteavgift

            hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
                settUniktFelt(modell.formueOgGjeldSvalbard.samletFormuesverdiEtterVerdsettingsrabatt) {
                    sumVerdiFoerVerdsettingsrabatt
                }
            }
        }
    }

    internal val samletGjeld = kalkyle {
        val sumAnnenGjeld = modell.formueOgGjeldSvalbard.gjeld_annenGjeld.tall()

        val gjeldISelskapMedDeltakerfastsetting =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                forekomstType.kommunenummer.verdi() == KOMMUNENUMMER_SVALBARD
            } summerVerdiFraHverForekomst {
                forekomstType.gjeld.tall()
            }
        settUniktFelt(modell.formueOgGjeldSvalbard.samletGjeld) {
            sumAnnenGjeld + gjeldISelskapMedDeltakerfastsetting
        }
    }

    internal val reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            val sumVerdsettingsrabatt = forekomsterAv(modell.formuesobjektSvalbard) summerVerdiFraHverForekomst {
                forekomstType.verdsettingsrabatt.tall()
            }

            val delsum = modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt +
                modell.forholdKnyttetTilFormuesbeskatningSvalbard.skattefriFormueIUtlandet
            val reduksjonAvGjeldForVerdsettingsrabattPaaEiendel =
                if (delsum != null && delsum.ulik(0)) {
                    (modell.formueOgGjeldSvalbard.samletGjeld * sumVerdsettingsrabatt) / delsum
                } else {
                    null
                }

            settUniktFelt(modell.forholdKnyttetTilFormuesbeskatningSvalbard.reduksjonAvGjeldForVerdsettingsrabattPaaEiendel) {
                reduksjonAvGjeldForVerdsettingsrabattPaaEiendel.somHeltall()
            }
        }
    }

    internal val nettoformue = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            settUniktFelt(modell.formueOgGjeldSvalbard.nettoformue) {
                (modell.formueOgGjeldSvalbard.samletFormuesverdiEtterVerdsettingsrabatt -
                    modell.formueOgGjeldSvalbard.samletGjeld +
                    modell.forholdKnyttetTilFormuesbeskatningSvalbard.reduksjonAvGjeldForVerdsettingsrabattPaaEiendel -
                    modell.forholdKnyttetTilFormuesbeskatningSvalbard.formueFritattForFormuesskattSomFoelgeAvBasisbevilgningFraStaten).somHeltall()
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            verdsettingsrabatt,
            formuesverdi,
            samletFormuesverdiEtterVerdsettingsrabatt,
            samletGjeld,
            reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler,
            nettoformue
        )
    }
}