package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.minusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.plusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
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

        val inngaaendeEgenkapitalVerdi = hentEgenkapitalavstemmingForekomst(gm)
            ?.verdiFor(modell2021.egenkapitalavstemming.inngaaendeEgenkapital)?.let { BigDecimal(it) }

        val sumNettoPositivEllerNegativPrinsippendring = beregnSumNettoPositivEllerNegativPrinsippendring(gm)

        val sumTilleggIEgenkapitalVerdi =
            summerForekomsterAvEgenkapitalendring(Egenkapitalendringstype.Kategori.TILLEGG)
        val sumFradragIEgenkapitalVerdi =
            summerForekomsterAvEgenkapitalendring(Egenkapitalendringstype.Kategori.FRADRAG)

        val utgaaendeEgenkapitalVerdi = inngaaendeEgenkapitalVerdi
            .plusNullsafe(sumNettoPositivEllerNegativPrinsippendring)
            .plusNullsafe(sumTilleggIEgenkapitalVerdi)
            .minusNullsafe(sumFradragIEgenkapitalVerdi)

        val egenkapitalavstemmingForekomstId = modell2021.egenkapitalavstemming.rotForekomstIdNoekkel to
            (hentEgenkapitalavstemmingForekomst(gm)?.rotIdVerdi() ?: "1")

        val nyeElementer = mutableListOf<InformasjonsElement>()

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

        sumTilleggIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    modell2021.egenkapitalavstemming.sumTilleggIEgenkapital,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        modell2021.egenkapitalavstemming.sumTilleggIEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        sumFradragIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    modell2021.egenkapitalavstemming.sumFradragIEgenkapital,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        modell2021.egenkapitalavstemming.sumFradragIEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        utgaaendeEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    modell2021.egenkapitalavstemming.utgaaendeEgenkapital,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        modell2021.egenkapitalavstemming.utgaaendeEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        leggTilIKontekst(GeneriskModellForKalkyler.Companion.fra(nyeElementer))
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

        val egenkapitalavstemmingForekomst = hentEgenkapitalavstemmingForekomst(gm)
        val utsattSkatt = egenkapitalavstemmingForekomst
            ?.verdiFor(modell2021.egenkapitalavstemming.utsattSkatt)
        val utsattSkattefordel = egenkapitalavstemmingForekomst
            ?.verdiFor(modell2021.egenkapitalavstemming.utsattSkattefordel)

        return sumNettoPrinsippendring
            ?.minus(BigDecimal(utsattSkatt ?: "0"))
            ?.plus(BigDecimal(utsattSkattefordel ?: "0"))
    }

    private fun hentEgenkapitalavstemmingForekomst(gm: GeneriskModell): GeneriskModell? {
        return gm.grupper(modell2021.egenkapitalavstemming).firstOrNull()
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            egenkapitalAvstemingKalkyle
        )
    }
}