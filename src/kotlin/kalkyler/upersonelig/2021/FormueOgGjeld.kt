@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.InlineKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.FeltSpecification
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.ForekomstOgVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Verdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.itererForekomster
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.summer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.mapping.HarGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.domain.skatteplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formueOgGjeld
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagBoenhetIBoligselskap
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagBorett
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagEgenFritaksbehandletBolig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagEgenFritaksbehandletFritidseiendom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagFlerboligbygning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagGaardsbruk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagIkkeUtleidNaeringseiendomINorge
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagRegnskapsbehandletBolig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagRegnskapsbehandletFritidseiendom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagSelveidBolig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagSelveidFritidseiendom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagSkogeiendomINorge
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagSkogeiendomIUtlandet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagTomt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagUtleidFlerboligbygningIUtlandet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesgrunnlagUtleidNaeringseiendom
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesobjekttypeUpersonligSkattepliktig_2021
import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.formuesobjekt
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.MappingTilGenerisk.FIXED_COMPONENT_ID
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.InformasjonsElement
import mu.KotlinLogging

object FormueOgGjeld : HarKalkyletre {

    internal val erFritattForFormuesskatt =
        ForekomstOgVerdi(skatteplikt) { it.erFritattForFormuesskatt.filterFelt(Specifications.erSann()) }
    private val logger = KotlinLogging.logger {}

    object samletGjeldKalkyle : InlineKalkyle() {

        override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
            if (erFritatt(naeringsopplysninger)) {
                var sum = BigDecimal.ZERO
                val gm = naeringsopplysninger.tilGeneriskModell()
                gm.felt(
                    formueOgGjeld.annenGjeld.forekomstType[1]).forEach { f ->
                    if (f.verdi != null) {
                        sum = sum.add(f.verdi.toBigDecimal())
                    }
                }
                if (BigDecimal.ZERO.equals(sum)) {
                    return Verdi(GeneriskModell.tom())
                }

                val informasjonsElement = InformasjonsElement(
                    formueOgGjeld.samletGjeld.beloep.key,
                    mapOf(
                        formueOgGjeld.forekomstType[0] to FIXED_COMPONENT_ID,
                        formueOgGjeld.samletGjeld.gruppe to FIXED_COMPONENT_ID
                    ),
                    sum
                )
                return Verdi(informasjonsElement)
            } else {
                return Verdi(GeneriskModell.tom())
            }
        }
    }

    internal val verdsettingsrabatt =
        itererForekomster gitt erFritattForFormuesskatt forekomsterAv formuesobjekt filter {
            FeltSpecification(it.formuesobjekttype, { felt -> verdsettingsrabattsats(felt) != null })
        } forVerdi {
            it.verdiFoerEventuellVerdsettingsrabatt - (it.verdiFoerEventuellVerdsettingsrabatt * it.formuesobjekttype.medFunksjon { type ->
                verdsettingsrabattsats(type)
            }) somFelt it.verdsettingsrabatt
        }

    object verdiFoerVerdsettingsrabattFlerboligbygning : InlineKalkyle() {

        override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
            var sum = BigDecimal.ZERO
            val flerboliger = naeringsopplysninger.tilGeneriskModell()
                .grupper(formuesgrunnlagFlerboligbygning.forekomstType[0])
            for (flerbolig in flerboliger) {
                val verdiFoerVerdsettingsrabatt = flerbolig
                    .felt(formuesgrunnlagFlerboligbygning.verdiFoerVerdsettingsrabattForFormuesandel.key)
                if (verdiFoerVerdsettingsrabatt?.verdi() != null) {
                    sum = sum.add(verdiFoerVerdsettingsrabatt.verdi().toBigDecimal())
                } else {
                    val verdiFoerVerdsettingsrabattUseksjonertBoenheter =
                        flerbolig.felter(
                            formuesgrunnlagFlerboligbygning.useksjonertBoenhet
                                .verdiFoerVerdsettingsrabattForFormuesandel.key)
                    for (foerVerdsettingsrabatt in verdiFoerVerdsettingsrabattUseksjonertBoenheter) {
                        sum = sum.add(foerVerdsettingsrabatt.verdi().toBigDecimal())
                    }
                }
            }
            if (BigDecimal.ZERO.equals(sum)) {
                return Verdi(GeneriskModell.tom())
            }
            return Verdi(sum)
        }
    }

    private val verdiFoerVerdsettingsrabattFormuesobjekt =
        summer forekomsterAv formuesobjekt forVerdi {
            it.verdiFoerEventuellVerdsettingsrabatt
        }

    private val verdiFoerVerdsettingsrabattBorett =
        summer forekomsterAv formuesgrunnlagBorett forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattGaardsbruk =
        summer forekomsterAv formuesgrunnlagGaardsbruk forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattInnenforInntektsgivendeAktivitet =
        summer forekomsterAv formuesgrunnlagAnnenFastEiendomInnenforInntektsgivendeAktivitet forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattUtenforInntektsgivendeAktivitet =
        summer forekomsterAv formuesgrunnlagAnnenFastEiendomUtenforInntektsgivendeAktivitet forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattBoenhetIBoligselskap =
        summer forekomsterAv formuesgrunnlagBoenhetIBoligselskap forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattEgenFritaksbehandletBolig =
        summer forekomsterAv formuesgrunnlagEgenFritaksbehandletBolig forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattEgenFritaksbehandletFritidseiendom =
        summer forekomsterAv formuesgrunnlagEgenFritaksbehandletFritidseiendom forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattIkkeUtleidNaeringseiendomINorge =
        summer forekomsterAv formuesgrunnlagIkkeUtleidNaeringseiendomINorge forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattRegnskapsbehandletBolig =
        summer forekomsterAv formuesgrunnlagRegnskapsbehandletBolig forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattRegnskapsbehandletFritidseiendom =
        summer forekomsterAv formuesgrunnlagRegnskapsbehandletFritidseiendom forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattSelveidBolig =
        summer forekomsterAv formuesgrunnlagSelveidBolig forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattSelveidFritidseiendom =
        summer forekomsterAv formuesgrunnlagSelveidFritidseiendom forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattSkogeiendomINorge =
        summer forekomsterAv formuesgrunnlagSkogeiendomINorge forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattSkogeiendomIUtlandet =
        summer forekomsterAv formuesgrunnlagSkogeiendomIUtlandet forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattTomt =
        summer forekomsterAv formuesgrunnlagTomt forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattUtleidFlerboligbygningIUtlandet =
        summer forekomsterAv formuesgrunnlagUtleidFlerboligbygningIUtlandet forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattUtleidNaeringseiendom =
        summer forekomsterAv formuesgrunnlagUtleidNaeringseiendom forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val verdiFoerVerdsettingsrabattIkkeUtleidUtleidNaeringseiendomIUtlandet =
        summer forekomsterAv formuesgrunnlagIkkeUtleidNaeringseiendomIUtlandet forVerdi {
            it.verdiFoerVerdsettingsrabattForFormuesandel
        }

    private val samletVerdiFoerVerdsettingsrabattTotal =
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

    object samletVerdiFoerEventuellVerdsettingsrabattKalkyle : InlineKalkyle() {

        override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
            val sum = samletVerdiFoerVerdsettingsrabattTotal
                .kalkulertPaa(naeringsopplysninger.tilGeneriskModell()).underlyingValue

            if (BigDecimal.ZERO.equals(sum)) {
                return Verdi(GeneriskModell.tom())
            }

            val informasjonsElement = InformasjonsElement(
                formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.beloep.key,
                mapOf(
                    formueOgGjeld.forekomstType[0] to FIXED_COMPONENT_ID,
                    formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.gruppe to FIXED_COMPONENT_ID
                ),
                sum
            )
            return Verdi(informasjonsElement)
        }
    }

    object samletVerdiBakAksjeneISelskapet : InlineKalkyle() {

        override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
            if (erFritatt(naeringsopplysninger)) {

                val gm = naeringsopplysninger.tilGeneriskModell()
                var sum = BigDecimal.ZERO

                val samletVerdi = gm
                    .felt(formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt.beloep.key)
                if (samletVerdi?.verdi() != null) {
                    sum = sum.add(samletVerdi.verdi().toBigDecimal())
                }
                val samletGjeld = gm.felt(formueOgGjeld.samletGjeld.beloep.key)
                if (samletGjeld?.verdi() != null) {
                    sum = sum.subtract(samletGjeld.verdi().toBigDecimal())
                }

                if (BigDecimal.ZERO.equals(sum)) {
                    return Verdi(GeneriskModell.tom())
                }

                if (sum < BigDecimal.ZERO) {
                    sum = BigDecimal.ZERO
                }

                val informasjonsElement = InformasjonsElement(
                    formueOgGjeld.samletVerdiBakAksjeneISelskapet.beloep.key,
                    mapOf(
                        formueOgGjeld.forekomstType[0] to FIXED_COMPONENT_ID,
                        formueOgGjeld.samletVerdiBakAksjeneISelskapet.gruppe to FIXED_COMPONENT_ID
                    ),
                    sum
                )
                return Verdi(informasjonsElement)
            } else {
                return Verdi(GeneriskModell.tom())
            }
        }
    }

    private fun verdsettingsrabattsats(
        it: Any?,
    ): BigDecimal? {
        val kodeliste = formuesobjekttypeUpersonligSkattepliktig_2021.hentKodeverdier()
            .associateBy { it.kode }
        val kodeVerdi = kodeliste[it as String]

        if (kodeVerdi == null) {
            logger.warn("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering")
        }
        return kodeVerdi?.kodetillegg?.verdsettingsrabattsats
    }

    private fun erFritatt(naeringsopplysninger: HarGeneriskModell): Boolean {
        return naeringsopplysninger.tilGeneriskModell()
            .felt(skatteplikt.erFritattForFormuesskatt.key)
            .verdi().toBoolean()
    }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            samletVerdiFoerEventuellVerdsettingsrabattKalkyle,
            verdsettingsrabatt,
            samletGjeldKalkyle,
            samletVerdiBakAksjeneISelskapet
        )
    }
}