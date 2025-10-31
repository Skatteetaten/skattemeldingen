package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.core.kodeliste.Eiendomstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.opplysningspliktForekomst

object FormueOgGjeldKalkyler : HarKalkylesamling {

    internal val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle {
        val sumVerdiFoerVerdsettingsrabatt =
            samletVerdiEiendelerUtenomAkvakultur()

        val formueOgInntektAkvakulturtillatelseSomFormuesobjekt =
            forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
            settUniktFelt(modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt) {
                (sumVerdiFoerVerdsettingsrabatt + formueOgInntektAkvakulturtillatelseSomFormuesobjekt).somHeltall()
            }
        }
    }

    private fun GeneriskModellKontekst.samletVerdiEiendelerUtenomAkvakultur(): BigDecimal? {
        val verdiFraFormuesobjekter = forekomsterAv(modell.formuesobjekt) summerVerdiFraHverForekomst {
            forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
        }

        val verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom =
        forekomsterAv(modell.fastEiendom) summerVerdiFraHverForekomst {
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

        val verdiFraSdfSomDeltakerISdf =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForNettoformue.tall()
            }

        val verdiFoerVerdsettingsrabattKapitalisertFesteavgift =
            forekomsterAv(modell.kapitalisertFesteavgift) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt =
            verdiFraFormuesobjekter +
                verdiFoerVerdsettingsrabattForFormuesandelForFastEiendom +
                verdiFoerVerdsettingsrabattKapitalisertFesteavgift +
                verdiFraSdfSomDeltakerISdf
        return sumVerdiFoerVerdsettingsrabatt
    }

    internal val samletGjeldKalkyle = kalkyle {
        val gjeldFraSdfSomDeltakerISdf =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                forekomstType.gjeld.tall()
            }

        val samletGjeld = if (inntektsaar.tekniskInntektsaar < 2023) {
            modell.formueOgGjeld.gjeld_annenGjeld + gjeldFraSdfSomDeltakerISdf
        } else {
            val andelAvSamletGjeldKnyttetTilAkvakulturtillatelse =
                forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.andelAvSamletGjeldKnyttetTilAkvakulturtillatelse.tall()
                }

            val gjeldKnyttetTilAkvakulturtillatelseEtterGjeldsreduksjon =
                (andelAvSamletGjeldKnyttetTilAkvakulturtillatelse *
                    satser?.sats(Sats.havbruk_satsForVerdsettelseAvOmsetningsverdiForAkvakulturtillatelse)).somHeltall()

            modell.formueOgGjeld.gjeld_annenGjeld +
                gjeldFraSdfSomDeltakerISdf -
                andelAvSamletGjeldKnyttetTilAkvakulturtillatelse +
                gjeldKnyttetTilAkvakulturtillatelseEtterGjeldsreduksjon
        }

        settUniktFelt(modell.formueOgGjeld.samletGjeld) { samletGjeld.somHeltall() }
    }

    internal val nettoFormueEllerGjeldKalkyle = kalkyle {
        val nettoFormueEllerGjeld = (modell.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt -
            modell.formueOgGjeld.samletGjeld).somHeltall()

        if (nettoFormueEllerGjeld stoerreEllerLik 0) {
            settUniktFelt(modell.formueOgGjeld.nettoformue) { nettoFormueEllerGjeld }
        } else {
            settUniktFelt(modell.formueOgGjeld.nettogjeld) { nettoFormueEllerGjeld.absoluttverdi() }
        }
    }

    val akvakulturtillatelseSomFormuesobjekt =
        kalkyle("akvakulturtillatelseSomformuesobjekt") {
            val erOmfattetAvSaerreglerForHavbruksvirksomhet = opplysningspliktForekomst.erOmfattetAvSaerreglerForHavbruksvirksomhet.erSann()
            forAlleForekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) {
                settFelt(forekomstType.verdiFoerEventuellVerdsettingsrabatt) {
                    if ( erOmfattetAvSaerreglerForHavbruksvirksomhet) {
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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            akvakulturtillatelseSomFormuesobjekt,
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            samletGjeldKalkyle,
            nettoFormueEllerGjeldKalkyle
        )
    }
}