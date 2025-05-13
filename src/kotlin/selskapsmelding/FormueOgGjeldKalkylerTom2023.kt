package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV2

object FormueOgGjeldKalkylerTom2023 : HarKalkylesamling {

    internal val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle {
        val sumVerdiFoerVerdsettingsrabatt =
            samletVerdiEiendelerUtenomAkvakultur()

        val formueOgInntektAkvakulturtillatelseSomFormuesobjekt =
            forekomsterAv(modellV2.akvakulturtillatelseSomFormuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        hvis(sumVerdiFoerVerdsettingsrabatt ulik 0) {
            settUniktFelt(modellV2.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt) {
                (sumVerdiFoerVerdsettingsrabatt + formueOgInntektAkvakulturtillatelseSomFormuesobjekt).somHeltall()
            }
        }
    }

    private fun GeneriskModellKontekst.samletVerdiEiendelerUtenomAkvakultur(): BigDecimal? {
        val verdiFraFormuesobjekter = forekomsterAv(modellV2.formuesobjekt) summerVerdiFraHverForekomst {
            forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
        }

        val verdiFoerVerdsettingsrabattBoenhetIBoligselskap =
            forekomsterAv(modellV2.formuesgrunnlagBoenhetIBoligselskap) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattBorett =
            forekomsterAv(modellV2.formuesgrunnlagBorett) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig =
            forekomsterAv(modellV2.formuesgrunnlagEgenFritaksbehandletBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom =
            forekomsterAv(modellV2.formuesgrunnlagEgenFritaksbehandletFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattFlerboligbygning =
            forekomsterAv(modellV2.formuesgrunnlagFlerboligbygning) summerVerdiFraHverForekomst {
                if (forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.harVerdi()) {
                    forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                } else {
                    forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                        forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                    }
                }
            }

        val verdiFoerVerdsettingsrabattGaardsbruk =
            forekomsterAv(modellV2.formuesgrunnlagGaardsbruk) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge =
            forekomsterAv(modellV2.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet =
            forekomsterAv(modellV2.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet =
            forekomsterAv(modellV2.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletBolig =
            forekomsterAv(modellV2.formuesgrunnlagRegnskapsbehandletBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom =
            forekomsterAv(modellV2.formuesgrunnlagRegnskapsbehandletFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidBolig =
            forekomsterAv(modellV2.formuesgrunnlagSelveidBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidFritidseiendom =
            forekomsterAv(modellV2.formuesgrunnlagSelveidFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomINorge =
            forekomsterAv(modellV2.formuesgrunnlagSkogeiendomINorge) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomIUtlandet =
            forekomsterAv(modellV2.formuesgrunnlagSkogeiendomIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattTomt =
            forekomsterAv(modellV2.formuesgrunnlagTomt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet =
            forekomsterAv(modellV2.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet =
            forekomsterAv(modellV2.formuesgrunnlagUtleidFlerboligbygningIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidNaeringseiendom =
            forekomsterAv(modellV2.formuesgrunnlagUtleidNaeringseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFraSdfSomDeltakerISdf =
            forekomsterAv(modellV2.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForNettoformue.tall()
            }

        val verdiFoerVerdsettingsrabattKapitalisertFesteavgift =
            forekomsterAv(modellV2.kapitalisertFesteavgift) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForKapitalisertFesteavgift.tall()
            }

        val sumVerdiFoerVerdsettingsrabatt =
            verdiFraFormuesobjekter +
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
                verdiFraSdfSomDeltakerISdf
        return sumVerdiFoerVerdsettingsrabatt
    }


    internal val nettoFormueEllerGjeldKalkyle = kalkyle {
        val nettoFormueEllerGjeld = (modellV2.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt -
            modellV2.formueOgGjeld.samletGjeld).somHeltall()

        if (nettoFormueEllerGjeld stoerreEllerLik 0) {
            settUniktFelt(modellV2.formueOgGjeld.nettoformue) { nettoFormueEllerGjeld }
        } else {
            settUniktFelt(modellV2.formueOgGjeld.nettogjeld) { nettoFormueEllerGjeld.absoluttverdi() }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            FormueOgGjeldKalkyler.akvakulturtillatelseSomFormuesobjekt,
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            FormueOgGjeldKalkyler.samletGjeldKalkyle,
            nettoFormueEllerGjeldKalkyle
        )
    }
}