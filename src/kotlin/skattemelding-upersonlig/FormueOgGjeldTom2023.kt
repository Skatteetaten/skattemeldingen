@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skatteplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.KOMMUNENUMMER_SVALBARD
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.LANDKODE_NORGE

object FormueOgGjeldTom2023 : HarKalkylesamling {
    val modell = modellV3

    private fun GeneriskModellKontekst.eiendommerPaaSvalbard() =
        forekomsterAv(modell.fastEiendom) der {
            forekomstType.kommunenummer.verdi() == KOMMUNENUMMER_SVALBARD
        } hentVerdiFraHverForekomst {
            forekomstType.internEiendomsidentifikator.verdi()
        }

    private fun GeneriskModellKontekst.eiendommerIUtlandet() =
        forekomsterAv(modell.fastEiendom) der {
            forekomstType.landkode.harVerdi()
                && forekomstType.landkode.verdi() != LANDKODE_NORGE
                && forekomstType.kommunenummer.verdi() != KOMMUNENUMMER_SVALBARD
        } hentVerdiFraHverForekomst {
            forekomstType.internEiendomsidentifikator.verdi()
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

    internal val samletFormuesverdiEtterVerdsettingsrabatt = kalkyle {
        val satser = satser!!
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {

            val eiendommerPaaSvalbard = eiendommerPaaSvalbard()
            val eiendommerIUtlandet = eiendommerIUtlandet()
            val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge.erSann()

            val formuesverdiForFormuesobjekt =
                forekomsterAv(modell.formuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdi.tall()
                }

            val formuesverdiForFormuesandelBoenhetIBoligselskap =
                forekomsterAv(modell.formuesgrunnlagBoenhetIBoligselskap) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelBorett =
                forekomsterAv(modell.formuesgrunnlagBorett) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelEgenFritaksbehandletBolig =
                forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletBolig) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelEgenFritaksbehandletFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletFritidseiendom) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelFlerboligbygning =
                forekomsterAv(modell.formuesgrunnlagFlerboligbygning) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    if (forekomstType.formuesverdiForFormuesandel.harVerdi()) {
                        forekomstType.formuesverdiForFormuesandel.tall()
                    } else {
                        forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                            forekomstType.formuesverdiForFormuesandel.tall()
                        }
                    }
                }

            val formuesverdiForFormuesandelGaardsbruk =
                forekomsterAv(modell.formuesgrunnlagGaardsbruk) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelIkkeUtleidNaeringseiendomINorge =
                forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelIkkeUtleidUtleidNaeringseiendomIUtlandet =
                if (skatteplikt.harSkattepliktTilSvalbard.erUsann()) {
                    forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) summerVerdiFraHverForekomst {
                        forekomstType.formuesverdiForFormuesandel.tall()
                    }
                } else null

            val formuesverdiForFormuesandelInnenforInntektsgivendeAktivitet =
                forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelRegnskapsbehandletBolig =
                forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletBolig) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelRegnskapsbehandletFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletFritidseiendom) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSelveidBolig =
                forekomsterAv(modell.formuesgrunnlagSelveidBolig) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSelveidFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagSelveidFritidseiendom) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSkogeiendomINorge =
                forekomsterAv(modell.formuesgrunnlagSkogeiendomINorge) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSkogeiendomIUtlandet =
                forekomsterAv(modell.formuesgrunnlagSkogeiendomIUtlandet) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelTomt =
                forekomsterAv(modell.formuesgrunnlagTomt) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtenforInntektsgivendeAktivitet =
                forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) der {
                    skalMappesTilFormueOgGjeldFastland(
                        modell.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtleidFlerboligbygningIUtlandet =
                forekomsterAv(modell.formuesgrunnlagUtleidFlerboligbygningIUtlandet) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtleidNaeringseiendom =
                forekomsterAv(modell.formuesgrunnlagUtleidNaeringseiendom) der {
                    skalMappesTilFormueOgGjeldFastland(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilNorge
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
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

            val sumFormuesverdiForFormuesandel =
                formuesverdiForFormuesobjekt +
                    formuesverdiForFormuesandelBoenhetIBoligselskap +
                    formuesverdiForFormuesandelBorett +
                    formuesverdiForFormuesandelEgenFritaksbehandletBolig +
                    formuesverdiForFormuesandelEgenFritaksbehandletFritidseiendom +
                    formuesverdiForFormuesandelFlerboligbygning +
                    formuesverdiForFormuesandelGaardsbruk +
                    formuesverdiForFormuesandelIkkeUtleidNaeringseiendomINorge +
                    formuesverdiForFormuesandelIkkeUtleidUtleidNaeringseiendomIUtlandet +
                    formuesverdiForFormuesandelInnenforInntektsgivendeAktivitet +
                    formuesverdiForFormuesandelRegnskapsbehandletBolig +
                    formuesverdiForFormuesandelRegnskapsbehandletFritidseiendom +
                    formuesverdiForFormuesandelSelveidBolig +
                    formuesverdiForFormuesandelSelveidFritidseiendom +
                    formuesverdiForFormuesandelSkogeiendomINorge +
                    formuesverdiForFormuesandelSkogeiendomIUtlandet +
                    formuesverdiForFormuesandelTomt +
                    formuesverdiForFormuesandelUtenforInntektsgivendeAktivitet +
                    formuesverdiForFormuesandelUtleidFlerboligbygningIUtlandet +
                    formuesverdiForFormuesandelUtleidNaeringseiendom +
                    formueOgInntektISelskapMedDeltakerfastsetting +
                    formueAkvakultur

            hvis(sumFormuesverdiForFormuesandel ulik 0) {
                settUniktFelt(modell.formueOgGjeld.samletFormuesverdiEtterVerdsettingsrabatt) {
                    sumFormuesverdiForFormuesandel
                }
            }
        }
    }

    private fun ForekomstKontekst<*>.skalMappesTilFormueOgGjeldFastland(
        internEiendomsidentifikatorNoekkel: String,
        eiendommerPaaSvalbard: List<String>,
        eiendommerIUtlandet: List<String>,
        harSkattepliktTilNorge: Boolean
    ): Boolean {
        val gm = this.generiskModell
        val internEiendomsidentifikator = gm.verdiFor(internEiendomsidentifikatorNoekkel)
        val eiendomErINorge = !eiendommerPaaSvalbard.contains(internEiendomsidentifikator)
            && !eiendommerIUtlandet.contains(internEiendomsidentifikator)

        return (eiendomErINorge ||
            (harSkattepliktTilNorge && eiendommerIUtlandet.contains(internEiendomsidentifikator)))
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
                val gjeldSDF = forekomsterAv(modell.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                    forekomstType.andelAvSamletGjeldKnyttetTilAkvakulturtillatelse.tall()
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
        val eiendommerPaaSvalbard = eiendommerPaaSvalbard()
        val eiendommerIUtlandet = eiendommerIUtlandet()
        val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge.erSann()

        val verdiFoerVerdsettingsrabattFormuesobjekt =
            forekomsterAv(modell.formuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val verdiFoerVerdsettingsrabattBoenhetIBoligselskap =
            forekomsterAv(modell.formuesgrunnlagBoenhetIBoligselskap) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattBorett =
            forekomsterAv(modell.formuesgrunnlagBorett) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig =
            forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletBolig) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletFritidseiendom) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattFlerboligbygning =
            forekomsterAv(modell.formuesgrunnlagFlerboligbygning) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                if (forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.harVerdi()) {
                    forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                } else {
                    forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                        forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                    }
                }
            }

        val verdiFoerVerdsettingsrabattGaardsbruk =
            forekomsterAv(modell.formuesgrunnlagGaardsbruk) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge =
            forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet =
            if (skatteplikt.harSkattepliktTilSvalbard.erUsann()) {
                forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) summerVerdiFraHverForekomst {
                    forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                }
            } else null

        val verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet =
            forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletBolig =
            forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletBolig) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletFritidseiendom) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidBolig =
            forekomsterAv(modell.formuesgrunnlagSelveidBolig) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagSelveidFritidseiendom) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomINorge =
            forekomsterAv(modell.formuesgrunnlagSkogeiendomINorge) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomIUtlandet =
            forekomsterAv(modell.formuesgrunnlagSkogeiendomIUtlandet) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattTomt =
            forekomsterAv(modell.formuesgrunnlagTomt) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet =
            forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet =
            forekomsterAv(modell.formuesgrunnlagUtleidFlerboligbygningIUtlandet) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidNaeringseiendom =
            forekomsterAv(modell.formuesgrunnlagUtleidNaeringseiendom) der {
                skalMappesTilFormueOgGjeldFastland(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilNorge
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
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
                verdiFoerVerdsettingsrabattBoenhetIBoligselskap +
                verdiFoerVerdsettingsrabattBorett +
                verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig +
                verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom +
                verdiFoerVerdsettingsrabattFlerboligbygning +
                verdiFoerVerdsettingsrabattGaardsbruk +
                verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge +
                verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet +
                verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet +
                verdiFoerVerdsettingsrabattRegnskapsbehandletBolig +
                verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom +
                verdiFoerVerdsettingsrabattSelveidBolig +
                verdiFoerVerdsettingsrabattSelveidFritidseiendom +
                verdiFoerVerdsettingsrabattSkogeiendomINorge +
                verdiFoerVerdsettingsrabattKapitalisertFesteavgift +
                verdiFoerVerdsettingsrabattSkogeiendomIUtlandet +
                verdiFoerVerdsettingsrabattTomt +
                verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet +
                verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet +
                verdiFoerVerdsettingsrabattUtleidNaeringseiendom +
                formueOgInntektISelskapMedDeltakerfastsetting
        return sumVerdiFoerVerdsettingsrabatt
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            akvakulturtillatelseSomFormuesobjekt,
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            FormueOgGjeld.verdsettingsrabatt,
            FormueOgGjeld.formuesverdi,
            samletFormuesverdiEtterVerdsettingsrabatt,
            FormueOgGjeld.samletGjeld,
            FormueOgGjeld.reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler,
            FormueOgGjeld.reduksjonAvGjeldForSkattefriFormueIUtlandet,
            FormueOgGjeld.nettoformue
        )
    }
}