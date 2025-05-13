@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.verdiFoerVerdsettingsrabattForAndelIFellesNettoformueISDF
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skatteplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.KOMMUNENUMMER_SVALBARD
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.mapping.LANDKODE_NORGE

object FormueOgGjeldSvalbardTom2023 : HarKalkylesamling {
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
        } hentVerdiFraHverForekomst {
            forekomstType.internEiendomsidentifikator.verdi()
        }

    val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle {

        val eiendommerPaaSvalbard = eiendommerPaaSvalbard()
        val harSkattepliktTilSvalbard = skatteplikt.harSkattepliktTilSvalbard.erSann()
        val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge
        val eiendommerIUtlandet = if (harSkattepliktTilNorge.erUsann()) {
            eiendommerIUtlandet()
        } else emptyList()

        val verdiFoerVerdsettingsrabattFormuesobjekt =
            forekomsterAv(modell.formuesobjektSvalbard) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val verdiFoerVerdsettingsrabattBoenhetIBoligselskap =
            forekomsterAv(modell.formuesgrunnlagBoenhetIBoligselskap) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattBorett =
            forekomsterAv(modell.formuesgrunnlagBorett) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig =
            forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletBolig) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletFritidseiendom) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattFlerboligbygning =
            forekomsterAv(modell.formuesgrunnlagFlerboligbygning) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
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
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge =
            forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet =
            forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet =
            forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletBolig =
            forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletBolig) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletFritidseiendom) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidBolig =
            forekomsterAv(modell.formuesgrunnlagSelveidBolig) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidFritidseiendom =
            forekomsterAv(modell.formuesgrunnlagSelveidFritidseiendom) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomIUtlandet =
            forekomsterAv(modell.formuesgrunnlagSkogeiendomIUtlandet) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattTomt =
            forekomsterAv(modell.formuesgrunnlagTomt) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet =
            forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet =
            forekomsterAv(modell.formuesgrunnlagUtleidFlerboligbygningIUtlandet) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidNaeringseiendom =
            forekomsterAv(modell.formuesgrunnlagUtleidNaeringseiendom) der {
                skalMappesTilFormueOgGjeldSvalbard(
                    forekomstType.internEiendomsidentifikator.key,
                    eiendommerPaaSvalbard,
                    eiendommerIUtlandet,
                    harSkattepliktTilSvalbard
                )
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val formueOgInntektISelskapMedDeltakerfastsetting =
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) der {
                KOMMUNENUMMER_SVALBARD == forekomstType.kommunenummer.verdi()
            } summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForNettoformue.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt = verdiFoerVerdsettingsrabattBoenhetIBoligselskap +
            verdiFoerVerdsettingsrabattBorett +
            verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig +
            verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom +
            verdiFoerVerdsettingsrabattFlerboligbygning +
            verdiFoerVerdsettingsrabattFormuesobjekt +
            verdiFoerVerdsettingsrabattGaardsbruk +
            verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge +
            verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet +
            verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet +
            verdiFoerVerdsettingsrabattRegnskapsbehandletBolig +
            verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom +
            verdiFoerVerdsettingsrabattSelveidBolig +
            verdiFoerVerdsettingsrabattSelveidFritidseiendom +
            verdiFoerVerdsettingsrabattSkogeiendomIUtlandet +
            verdiFoerVerdsettingsrabattTomt +
            verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet +
            verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet +
            verdiFoerVerdsettingsrabattUtleidNaeringseiendom +
            formueOgInntektISelskapMedDeltakerfastsetting

        hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
            settUniktFelt(modell.formueOgGjeldSvalbard.samletVerdiFoerEventuellVerdsettingsrabatt) {
                sumVerdiFoerVerdsettingsrabatt
            }
        }
    }

    internal val samletFormuesverdiEtterVerdsettingsrabatt = kalkyle {
        val satser = satser!!
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erUsann()) {

            val eiendommerPaaSvalbard = eiendommerPaaSvalbard()
            val harSkattepliktTilSvalbard = skatteplikt.harSkattepliktTilSvalbard.erSann()
            val harSkattepliktTilNorge = skatteplikt.harSkattepliktTilNorge
            val eiendommerIUtlandet = if (harSkattepliktTilNorge.erUsann()) {
                eiendommerIUtlandet()
            } else emptyList()

            val sumFormuesverdiFraFormuesobjekt =
                forekomsterAv(modell.formuesobjektSvalbard) summerVerdiFraHverForekomst {
                    forekomstType.formuesverdi.tall()
                }

            val formuesverdiForFormuesandelBoenhetIBoligselskap =
                forekomsterAv(modell.formuesgrunnlagBoenhetIBoligselskap) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelBorett =
                forekomsterAv(modell.formuesgrunnlagBorett) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelEgenFritaksbehandletBolig =
                forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletBolig) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelEgenFritaksbehandletFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagEgenFritaksbehandletFritidseiendom) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelFlerboligbygning =
                forekomsterAv(modell.formuesgrunnlagFlerboligbygning) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
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
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelIkkeUtleidNaeringseiendomINorge =
                forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelIkkeUtleidUtleidNaeringseiendomIUtlandet =
                forekomsterAv(modell.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelInnenforInntektsgivendeAktivitet =
                forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelRegnskapsbehandletBolig =
                forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletBolig) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelRegnskapsbehandletFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagRegnskapsbehandletFritidseiendom) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSelveidBolig =
                forekomsterAv(modell.formuesgrunnlagSelveidBolig) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSelveidFritidseiendom =
                forekomsterAv(modell.formuesgrunnlagSelveidFritidseiendom) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelSkogeiendomIUtlandet =
                forekomsterAv(modell.formuesgrunnlagSkogeiendomIUtlandet) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelTomt =
                forekomsterAv(modell.formuesgrunnlagTomt) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtenforInntektsgivendeAktivitet =
                forekomsterAv(modell.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtleidFlerboligbygningIUtlandet =
                forekomsterAv(modell.formuesgrunnlagUtleidFlerboligbygningIUtlandet) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
                }

            val formuesverdiForFormuesandelUtleidNaeringseiendom =
                forekomsterAv(modell.formuesgrunnlagUtleidNaeringseiendom) der {
                    skalMappesTilFormueOgGjeldSvalbard(
                        forekomstType.internEiendomsidentifikator.key,
                        eiendommerPaaSvalbard,
                        eiendommerIUtlandet,
                        harSkattepliktTilSvalbard
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.formuesverdiForFormuesandel.tall()
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

            val sumVerdiFoerVerdsettingsrabatt = sumFormuesverdiFraFormuesobjekt +
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
                verdiFoerVerdsettingsrabattKapitalisertFesteavgift +
                formuesverdiForFormuesandelSkogeiendomIUtlandet +
                formuesverdiForFormuesandelTomt +
                formuesverdiForFormuesandelUtenforInntektsgivendeAktivitet +
                formuesverdiForFormuesandelUtleidFlerboligbygningIUtlandet +
                formuesverdiForFormuesandelUtleidNaeringseiendom +
                formueOgInntektISelskapMedDeltakerfastsetting

            hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
                settUniktFelt(modell.formueOgGjeldSvalbard.samletFormuesverdiEtterVerdsettingsrabatt) {
                    sumVerdiFoerVerdsettingsrabatt
                }
            }
        }
    }

    private fun ForekomstKontekst<*>.skalMappesTilFormueOgGjeldSvalbard(
        internEiendomsidentifikatorNoekkel: String,
        eiendommerPaaSvalbard: List<String>,
        eiendommerIUtlandet: List<String>,
        harSkattepliktTilSvalbard: Boolean
    ): Boolean {
        val gm = this.generiskModell
        val internEiendomsidentifikator = gm.verdiFor(internEiendomsidentifikatorNoekkel)

        return eiendommerPaaSvalbard.contains(internEiendomsidentifikator)
            || (harSkattepliktTilSvalbard && eiendommerIUtlandet.contains(internEiendomsidentifikator))
    }


    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            FormueOgGjeldSvalbard.verdsettingsrabatt,
            FormueOgGjeldSvalbard.formuesverdi,
            samletFormuesverdiEtterVerdsettingsrabatt,
            FormueOgGjeldSvalbard.samletGjeld,
            FormueOgGjeldSvalbard.reduksjonAvGjeldForVerdsettingsrabattPaaEiendeler,
            FormueOgGjeldSvalbard.nettoformue
        )
    }
}