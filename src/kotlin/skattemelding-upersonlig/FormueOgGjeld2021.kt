@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV1
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst

object FormueOgGjeld2021 : HarKalkylesamling {

    internal val samletVerdiFoerEventuellVerdsettingsrabattKalkyle = kalkyle("samletVerdiFoerEventuellVerdsettingsrabatt") {
        val verdiFoerVerdsettingsrabattBoenhetIBoligselskap =
            forekomsterAv(modellV1.formuesgrunnlagBoenhetIBoligselskap) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattBorett =
            forekomsterAv(modellV1.formuesgrunnlagBorett) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig =
            forekomsterAv(modellV1.formuesgrunnlagEgenFritaksbehandletBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom =
            forekomsterAv(modellV1.formuesgrunnlagEgenFritaksbehandletFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattFlerboligbygning =
            forekomsterAv(modellV1.formuesgrunnlagFlerboligbygning) summerVerdiFraHverForekomst  {
                if (forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.harVerdi()) {
                    forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                } else {
                    forekomsterAv(forekomstType.useksjonertBoenhet) summerVerdiFraHverForekomst {
                        forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
                    }
                }
            }

        val verdiFoerVerdsettingsrabattFormuesobjekt =
            forekomsterAv(modellV1.formuesobjekt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerEventuellVerdsettingsrabatt.tall()
            }

        val verdiFoerVerdsettingsrabattGaardsbruk =
            forekomsterAv(modellV1.formuesgrunnlagGaardsbruk) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge =
            forekomsterAv(modellV1.formuesgrunnlagIkkeUtleidNaeringseiendomINorge) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet =
            forekomsterAv(modellV1.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet =
            forekomsterAv(modellV1.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletBolig =
            forekomsterAv(modellV1.formuesgrunnlagRegnskapsbehandletBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom =
            forekomsterAv(modellV1.formuesgrunnlagRegnskapsbehandletFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidBolig =
            forekomsterAv(modellV1.formuesgrunnlagSelveidBolig) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSelveidFritidseiendom =
            forekomsterAv(modellV1.formuesgrunnlagSelveidFritidseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomINorge =
            forekomsterAv(modellV1.formuesgrunnlagSkogeiendomINorge) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattSkogeiendomIUtlandet =
            forekomsterAv(modellV1.formuesgrunnlagSkogeiendomIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattTomt =
            forekomsterAv(modellV1.formuesgrunnlagTomt) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet =
            forekomsterAv(modellV1.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet =
            forekomsterAv(modellV1.formuesgrunnlagUtleidFlerboligbygningIUtlandet) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val verdiFoerVerdsettingsrabattUtleidNaeringseiendom =
            forekomsterAv(modellV1.formuesgrunnlagUtleidNaeringseiendom) summerVerdiFraHverForekomst {
                forekomstType.verdiFoerVerdsettingsrabattForFormuesandel.tall()
            }

        val samletVerdiFoerVerdsettingsrabattTotal =
            verdiFoerVerdsettingsrabattBoenhetIBoligselskap +
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
                verdiFoerVerdsettingsrabattSkogeiendomINorge +
                verdiFoerVerdsettingsrabattSkogeiendomIUtlandet +
                verdiFoerVerdsettingsrabattTomt +
                verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet +
                verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet +
                verdiFoerVerdsettingsrabattUtleidNaeringseiendom

        hvis(samletVerdiFoerVerdsettingsrabattTotal ulik 0) {
            settUniktFelt(modellV1.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt) {
                samletVerdiFoerVerdsettingsrabattTotal
            }
        }
    }

    internal val verdsettingsrabattKalkyle = kalkyle("verdsettingsrabatt") {
        val satser = satser!!
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erSann()) {
            forekomsterAv(modellV1.formuesobjekt) der {
                satser.verdsettingsrabattsatsForFormuesobjekttype(forekomstType.formuesobjekttype.verdi()).harVerdi()
            } forHverForekomst {
                val sats = satser.verdsettingsrabattsatsForFormuesobjekttype(forekomstType.formuesobjekttype.verdi())
                settFelt(forekomstType.verdsettingsrabatt) {
                    forekomstType.verdiFoerEventuellVerdsettingsrabatt -
                        (forekomstType.verdiFoerEventuellVerdsettingsrabatt * sats)
                }
            }
        }
    }

    internal val samletGjeldKalkyle = kalkyle("samletGjeld") {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erSann() && modellV1.formueOgGjeld.gjeld_annenGjeld ulik 0) {
            settUniktFelt(modellV1.formueOgGjeld.samletGjeld) {
                modellV1.formueOgGjeld.gjeld_annenGjeld.tall()
            }
        }
    }

    internal val samletVerdiBakAksjeneISelskapetKalkyle = kalkyle("samletVerdiBakAksjeneISelskapet") {
        hvis(skattepliktForekomst.erFritattForFormuesskatt.erSann()) {
            val samletVerdiBakAksjeneISelskapet = modellV1.formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt -
                modellV1.formueOgGjeld.samletGjeld
            hvis(samletVerdiBakAksjeneISelskapet ulik 0) {
                settUniktFelt(modellV1.formueOgGjeld.samletVerdiBakAksjeneISelskapet) {
                    samletVerdiBakAksjeneISelskapet medMinimumsverdi 0
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            verdsettingsrabattKalkyle,
            samletGjeldKalkyle,
            samletVerdiBakAksjeneISelskapetKalkyle,
        )
    }
}