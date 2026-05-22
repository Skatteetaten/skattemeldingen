package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.plusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Egenkapitalendringstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Egenkapitalendringstype.kategori
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

object EgenkapitalavstemmingBeregning2021 : HarKalkylesamling {

    private fun GeneriskModellKontekst.summerForekomsterAvEgenkapitalendring(
        kategori: Egenkapitalendringstype.Kategori,
    ) =
        forekomsterAv(modell2021.egenkapitalavstemming.egenkapitalendring) der {
            forekomstType.egenkapitalendringstype.verdi().kategori(this@summerForekomsterAvEgenkapitalendring.inntektsaar) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    internal val egenkapitalAvstemingKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()

        val egenkapitalavstemmingForekomst = gm.hentEgenkapitalavstemmingForekomst()
        val inngaaendeEgenkapitalVerdi = egenkapitalavstemmingForekomst
            ?.verdiSomBigDecimal(modell2021.egenkapitalavstemming.inngaaendeEgenkapital)

        val sumNettoPositivEllerNegativPrinsippendring = beregnSumNettoPositivEllerNegativPrinsippendring(gm)

        val sumTilleggIEgenkapitalVerdi =
            summerForekomsterAvEgenkapitalendring(Egenkapitalendringstype.Kategori.TILLEGG)
        val sumFradragIEgenkapitalVerdi =
            summerForekomsterAvEgenkapitalendring(Egenkapitalendringstype.Kategori.FRADRAG)

        val utgaaendeEgenkapitalVerdi = inngaaendeEgenkapitalVerdi
            .plusNullsafe(sumNettoPositivEllerNegativPrinsippendring)
            .plusNullsafe(sumTilleggIEgenkapitalVerdi)
            .minusNullsafe(sumFradragIEgenkapitalVerdi)

        sumNettoPositivEllerNegativPrinsippendring?.let {
            if (sumNettoPositivEllerNegativPrinsippendring.erPositiv()) {
                settUniktFelt(modell2021.egenkapitalavstemming.sumNettoPositivPrinsippendring) {
                    sumNettoPositivEllerNegativPrinsippendring.avrundTilToDesimaler()
                }
            } else {
                settUniktFelt(modell2021.egenkapitalavstemming.sumNettoNegativPrinsippendring) {
                    sumNettoPositivEllerNegativPrinsippendring.absoluttverdi()?.avrundTilToDesimaler()
                }
            }
        }

        val nyGruppe = GeneriskGruppe(egenkapitalavstemmingForekomst?.forekomstIder ?: mapOf(modell2021.egenkapitalavstemming.rotForekomstIdNoekkel to "1"))
            .leggTilFeltMedEgenskaper(
                modell2021.egenkapitalavstemming.sumTilleggIEgenkapital,
                sumTilleggIEgenkapitalVerdi?.avrundTilToDesimalerString()
            ).leggTilFeltMedEgenskaper(
                modell2021.egenkapitalavstemming.sumFradragIEgenkapital,
                sumFradragIEgenkapitalVerdi?.avrundTilToDesimalerString()
            ).leggTilFeltMedEgenskaper(
                modell2021.egenkapitalavstemming.utgaaendeEgenkapital,
                utgaaendeEgenkapitalVerdi?.avrundTilToDesimalerString()
            )

        leggTilIKontekst(GeneriskModellForKalkyler.fra(nyGruppe.informasjonsElementer))
    }

    internal fun GeneriskModellKontekst.beregnSumNettoPositivEllerNegativPrinsippendring(gm: GeneriskModell): BigDecimal? {
        val sumPositivPrinsippendring =
            forekomsterAv(modell2021.egenkapitalavstemming.prinsippendring) summerVerdiFraHverForekomst {
                forekomstType.positivPrinsippendring.tall()
            }

        val sumNegativPrinsippendring =
            forekomsterAv(modell2021.egenkapitalavstemming.prinsippendring) summerVerdiFraHverForekomst {
                forekomstType.negativPrinsippendring.tall()
            }

        val sumNettoPrinsippendring = sumPositivPrinsippendring
            .minusNullsafe(sumNegativPrinsippendring)

        val egenkapitalavstemmingForekomst = gm.hentEgenkapitalavstemmingForekomst()
        val utsattSkatt = egenkapitalavstemmingForekomst
            ?.verdiSomBigDecimal(modell2021.egenkapitalavstemming.utsattSkatt)
        val utsattSkattefordel = egenkapitalavstemmingForekomst
            ?.verdiSomBigDecimal(modell2021.egenkapitalavstemming.utsattSkattefordel)

        return sumNettoPrinsippendring
            .minusNullsafe(utsattSkatt)
            .plusNullsafe(utsattSkattefordel)
    }

    private fun GeneriskModell.hentEgenkapitalavstemmingForekomst(): GeneriskGruppe? {
        return gruppeOrNull(modell2021.egenkapitalavstemming)
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            egenkapitalAvstemingKalkyle
        )
    }
}