@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.core.kodeliste.Eiendomstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Satser
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellNaering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skatteplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.KOMMUNENUMMER_SVALBARD
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.LANDKODE_NORGE

object FormueOgGjeld : HarKalkylesamling {

    private fun GeneriskModellKontekst.eiendommerSomIkkeSkalTilSvalbard(harSkattepliktTilNorge: Boolean) =
        forekomsterAv(modell.fastEiendom) der {
            (forekomstType.kommunenummerBeliggenhet.harVerdi()
                && forekomstType.kommunenummerBeliggenhet.verdi() != KOMMUNENUMMER_SVALBARD)
                || (harSkattepliktTilNorge
                && (forekomstType.landkodeBeliggenhet.harVerdi()
                && forekomstType.landkodeBeliggenhet.verdi() != LANDKODE_NORGE))
        }

    val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle {

        val formueOgInntektAkvakulturtillatelseSomFormuesobjekt =
            forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt = samletVerdiEiendelerUtenomAkvakultur() +
            formueOgInntektAkvakulturtillatelseSomFormuesobjekt

        hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
            settUniktFelt(modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt) {
                sumVerdiFoerVerdsettingsrabatt
            }
        }
    }

    internal val verdsettingsrabatt = kalkyle {
        val satser = satser!!
        val inntektsaar = inntektsaar

        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            forekomsterAv(modell.formuesobjekt) der {
                satser.satsForFormuesobjekttype(inntektsaar, forekomstType.formuesobjekttype.verdi()).harVerdi()
            } forHverForekomst {
                val sats = 1.toBigDecimal() - satser.satsForFormuesobjekttype(inntektsaar, forekomstType.formuesobjekttype.verdi())
                settFelt(forekomstType.verdsettingsrabatt) {
                    (forekomstType.verdiFoerEventuellVerdsettingsrabatt * sats).somHeltall()
                }
            }

            hvis(inntektsaar.tekniskInntektsaar <= 2024) {
                val satsVerdsettingsrabatt =
                    satser.verdsettingsrabattsatsForFormuesobjekttype("formuesobjektOmfattetAvVerdsettingsrabattDriftsmidlerMv")

                forAlleForekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) {
                    settFelt(forekomstType.verdsettingsrabatt) {
                        (forekomstType.verdiFoerEventuellVerdsettingsrabatt *
                            (1.toBigDecimal() - satsVerdsettingsrabatt)).somHeltall()
                    }
                }
            }
        }
    }

    fun Satser.satsForFormuesobjekttype(
        inntektsaar: Inntektsaar,
        formuesobjekttype: String?
    ): BigDecimal? {
        return if (inntektsaar.gjeldendeInntektsaar <= 2024) {
            verdsettingsrabattsatsForFormuesobjekttype(formuesobjekttype)
        } else {
           tekniskNavnForFormuesobjekttype[formuesobjekttype]?.let { sats(it) }
        }
    }

    internal val formuesverdi = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            forekomsterAv(modell.formuesobjekt) forHverForekomst {
                settFelt(forekomstType.formuesverdi) {
                    forekomstType.verdiFoerEventuellVerdsettingsrabatt - forekomstType.verdsettingsrabatt
                }
            }

            forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) forHverForekomst {
                settFelt(forekomstType.formuesverdi) {
                    forekomstType.verdiFoerEventuellVerdsettingsrabatt - forekomstType.verdsettingsrabatt
                }
            }
        }
    }

    internal val samletFormuesverdiEtterVerdsettingsrabatt = kalkyle {
        val satser = satser!!
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge.erSann()

            val formuesverdiForFormuesobjekt =
                forekomsterAv(modell.formuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdi.tall()
                }

            val eiendommerSomIkkeSkalTilSvalbard = eiendommerSomIkkeSkalTilSvalbard(harSkattepliktTilNorge)
            val formuesverdiForFastEiendom =
                eiendommerSomIkkeSkalTilSvalbard summerVerdiFraHverForekomst {
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
                    forekomstType.kommunenummer.verdi() != KOMMUNENUMMER_SVALBARD
                } summerVerdiFraHverForekomst {
                    val sats = satser.sats(verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF)
                    (forekomstType.verdiFoerVerdsettingsrabattForNettoformue * sats).somHeltall()
                }

            val formueAkvakultur =
                forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdi.tall()
                }

            val formuesverdiForKapitalisertFesteavgift =
                forekomsterAv(modell.kapitalisertFesteavgift) der {
                    forekomstType.kommunenummer.verdi() != KOMMUNENUMMER_SVALBARD
                } summerVerdiFraHverForekomst {
                    val sats = satser.sats(verdiFoerVerdsettingsrabattForKapitalisertFesteavgift)
                    (forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift * sats).somHeltall()
                }

            val sumFormuesverdiForFormuesandel =
                formuesverdiForFormuesobjekt +
                    formuesverdiForFastEiendom +
                    formueOgInntektISelskapMedDeltakerfastsetting +
                    formueAkvakultur +
                    formuesverdiForKapitalisertFesteavgift

            hvis(sumFormuesverdiForFormuesandel ulik 0) {
                settUniktFelt(modell.formueOgGjeld.samletFormuesverdiEtterVerdsettingsrabatt) {
                    sumFormuesverdiForFormuesandel
                }
            }
        }
    }

    internal val samletGjeld = kalkyle {
        val sumAnnenGjeld = modell.formueOgGjeld.gjeld_annenGjeld.tall()

        val gjeldISelskapMedDeltakerfastsetting =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                forekomstType.kommunenummer.verdi() != KOMMUNENUMMER_SVALBARD
            } summerVerdiFraHverForekomst {
                forekomstType.gjeld.tall()
            }

        val andelAvSamletGjeldKnyttetTilAkvakulturtillatelse =
            if (skattepliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
                forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.andelAvSamletGjeldKnyttetTilAkvakulturtillatelse.tall()
                }
            } else {
                null
            }

        val gjeldKnyttetTilAkvakulturtillatelseEtterGjeldsreduksjon =
            if (skattepliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()) {
                (andelAvSamletGjeldKnyttetTilAkvakulturtillatelse *
                    satser?.sats(Sats.havbruk_satsForVerdsettelseAvOmsetningsverdiForAkvakulturtillatelse)).somHeltall()
            } else {
                null
            }

        settUniktFelt(modell.formueOgGjeld.samletGjeld) {
            sumAnnenGjeld + gjeldISelskapMedDeltakerfastsetting - andelAvSamletGjeldKnyttetTilAkvakulturtillatelse +
                gjeldKnyttetTilAkvakulturtillatelseEtterGjeldsreduksjon
        }
    }

    internal val reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            val sumVerdsettingsrabatt = forekomsterAv(modell.formueOgGjeld) summerVerdiFraHverForekomst {
                forekomstType.samletVerdiFoerEventuellVerdsettingsrabatt -
                    forekomstType.samletFormuesverdiEtterVerdsettingsrabatt
            }
            val delsum = modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt +
                modell.forholdKnyttetTilFormuesbeskatning.skattefriFormueIUtlandet
            val reduksjonAvGjeldForVerdsettingsrabattPaaEiendel =
                if (delsum != null && delsum.ulik(0)) {
                    (modell.formueOgGjeld.samletGjeld * sumVerdsettingsrabatt) / delsum
                } else {
                    null
                }

            settUniktFelt(modell.forholdKnyttetTilFormuesbeskatning.reduksjonAvGjeldForVerdsettingsrabattPaaEiendel) {
                reduksjonAvGjeldForVerdsettingsrabattPaaEiendel.somHeltall()
            }
        }
    }

    internal val reduksjonAvGjeldForSkattefriFormueIUtlandet = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann() && skattepliktForekomst.harSkattepliktTilSvalbard.erUsann()) {
            hvis(
                modellNaering.balanseregnskap_sumBalanseverdiForEiendel.tall() != null &&
                modellNaering.balanseregnskap_sumBalanseverdiForEiendel ulik 0) {
                val reduksjonAvGjeldForSkattefriFormueIUtlandet =
                    ((modell.formueOgGjeld.samletGjeld *
                        modell.forholdKnyttetTilFormuesbeskatning.bokfoertVerdiAvSkattefriFormueIUtlandet) /
                        modellNaering.balanseregnskap_sumBalanseverdiForEiendel).somHeltall()

                settUniktFelt(modell.forholdKnyttetTilFormuesbeskatning.reduksjonAvGjeldForSkattefriFormueIUtlandet) {
                    reduksjonAvGjeldForSkattefriFormueIUtlandet
                }
            }
        }
    }

    internal val nettoformue = kalkyle {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {
            settUniktFelt(modell.formueOgGjeld.nettoformue) {
                (modell.formueOgGjeld.samletFormuesverdiEtterVerdsettingsrabatt -
                    modell.formueOgGjeld.samletGjeld +
                    modell.forholdKnyttetTilFormuesbeskatning.reduksjonAvGjeldForVerdsettingsrabattPaaEiendel +
                    modell.forholdKnyttetTilFormuesbeskatning.reduksjonAvGjeldForSkattefriFormueIUtlandet -
                    modell.forholdKnyttetTilFormuesbeskatning.formueFritattForFormuesskattSomFoelgeAvBasisbevilgningFraStaten).somHeltall() medMinimumsverdi 0
            }
        }
    }

    private val akvakulturtillatelseSomFormuesobjekt =
        kalkyle("akvakulturtillatelseSomformuesobjekt") {
            val erOmfattetAvSaerreglerForHavbruksvirksomhet = skattepliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()

            forAlleForekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) {
                settFelt(forekomstType.verdiFoerEventuellVerdsettingsrabatt) {
                    if (erOmfattetAvSaerreglerForHavbruksvirksomhet) {
                        (forekomstType.omsetningsverdi.tall() *
                            this@kalkyle.satser!!.sats(Sats.havbruk_satsForVerdsettelseAvOmsetningsverdiForAkvakulturtillatelse)).somHeltall()
                    } else {
                        forekomstType.omsetningsverdi.tall()
                    }
                }
            }

            hvis(erOmfattetAvSaerreglerForHavbruksvirksomhet) {

                val annenGjeld = modell.formueOgGjeld.gjeld_annenGjeld.tall()
                val gjeldSDF = forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                    forekomstType.gjeld.tall()
                }

                val sumAkvakulturtillatelseOmsetningsverdi =
                    forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                        forekomstType.omsetningsverdi.tall()
                    }

                val samletVerdiEiendeler =
                    samletVerdiEiendelerUtenomAkvakultur() + sumAkvakulturtillatelseOmsetningsverdi

                forAlleForekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) {
                    val akvakulturtillatelsenesAndelAvSamletVerdi =
                        forekomstType.omsetningsverdi.tall() / samletVerdiEiendeler

                    settFelt(forekomstType.andelAvSamletGjeldKnyttetTilAkvakulturtillatelse) {
                        ((annenGjeld + gjeldSDF) * akvakulturtillatelsenesAndelAvSamletVerdi).somHeltall()
                    }
                }
            }
        }

    private fun GeneriskModellKontekst.samletVerdiEiendelerUtenomAkvakultur(): BigDecimal? {
        val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge.erSann()

        val verdiFoerVerdsettingsrabattFormuesobjekt =
            forekomsterAv(modell.formuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val eiendommerSomIkkeSkalTilSvalbard = eiendommerSomIkkeSkalTilSvalbard(harSkattepliktTilNorge)

        val verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom =
            eiendommerSomIkkeSkalTilSvalbard summerVerdiFraHverForekomst {
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
                KOMMUNENUMMER_SVALBARD != forekomstType.kommunenummer.verdi()
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForNettoformue.tall()
            }

        val verdiFoerVerdsettingsrabattKapitalisertFesteavgift =
            forekomsterAv(modell.kapitalisertFesteavgift) der {
                forekomstType.kommunenummer.verdi() != KOMMUNENUMMER_SVALBARD
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt =
            verdiFoerVerdsettingsrabattFormuesobjekt +
                verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom +
                verdiFoerVerdsettingsrabattKapitalisertFesteavgift +
                formueOgInntektISelskapMedDeltakerfastsetting
        return sumVerdiFoerVerdsettingsrabatt
    }

    internal val tekniskNavnForFormuesobjekttype = mapOf(
        "formuesobjektOmfattetAvVerdsettingsrabattDriftsmidlerMv" to Sats.verdiFoerVerdsettingsrabattForFormuesobjekterINaeringOmfattetAvVerdsettingsrabatt,
        "formuesobjektOmfattetAvVerdsettingsrabattAksjerMv" to Sats.verdiFoerVerdsettingsrabattForAksje
    )

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            akvakulturtillatelseSomFormuesobjekt,
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            verdsettingsrabatt,
            formuesverdi,
            samletFormuesverdiEtterVerdsettingsrabatt,
            samletGjeld,
            reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler,
            reduksjonAvGjeldForSkattefriFormueIUtlandet,
            nettoformue
        )
    }
}