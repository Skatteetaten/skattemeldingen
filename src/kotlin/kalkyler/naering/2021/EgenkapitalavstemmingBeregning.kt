package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.FeltSpecification
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.felter.FeltKoordinat
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.InlineKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Verdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.summer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.minusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.plusNullsafe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.mapping.HarGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.egenkapitalavstemming
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.egenkapitalavstemming.egenkapitalendring
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.egenkapitalavstemming.prinsippendring
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.InformasjonsElement

object EgenkapitalavstemmingBeregning : InlineKalkyle() {

    private fun summerForekomsterAvEgenkapitalendring(
        kategori: KodelisteEgenkapitalendringstype.Kategori,
        feltHenter: (egenkapitalendring) -> FeltKoordinat<*>
    ) =
        summer forekomsterAv egenkapitalendring filter {
            FeltSpecification(egenkapitalendring.egenkapitalendringstype) {
                KodelisteEgenkapitalendringstype.kategori(it) == kategori
            }
        } forVerdi {
            feltHenter.invoke(it)
        }

    internal val sumTilleggIEgenkapitalKalkyle =
        summerForekomsterAvEgenkapitalendring(KodelisteEgenkapitalendringstype.Kategori.TILLEGG) {
            it.beloep
        }

    internal val sumFradragIEgenkapitalKalkyle =
        summerForekomsterAvEgenkapitalendring(KodelisteEgenkapitalendringstype.Kategori.FRADRAG) {
            it.beloep
        }

    internal val sumPositivPrinsippendringKalkyle =
        summer forekomsterAv prinsippendring forVerdi {
            it.positivPrinsippendring
        }

    internal val sumNegativPrinsippendringKalkyle =
        summer forekomsterAv prinsippendring forVerdi {
            it.negativPrinsippendring
        }

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()

        val inngaaendeEgenkapitalVerdi = hentEgenkapitalavstemmingForekomst(gm)
            ?.verdiFor(egenkapitalavstemming.inngaaendeEgenkapital.key)?.let { BigDecimal(it) }

        val sumNettoPositivEllerNegativPrinsippendring = beregnSumNettoPositivEllerNegativPrinsippendring(gm)

        val sumTilleggIEgenkapitalVerdi = sumTilleggIEgenkapitalKalkyle
            .kalkulertPaa(gm).tilBigDecimal()
        val sumFradragIEgenkapitalVerdi = sumFradragIEgenkapitalKalkyle
            .kalkulertPaa(gm).tilBigDecimal()

        val utgaaendeEgenkapitalVerdi = inngaaendeEgenkapitalVerdi
            .plusNullsafe(sumNettoPositivEllerNegativPrinsippendring)
            .plusNullsafe(sumTilleggIEgenkapitalVerdi)
            .minusNullsafe(sumFradragIEgenkapitalVerdi)

        val egenkapitalavstemmingForekomstId = egenkapitalavstemming.forekomstType[0] to
            (hentEgenkapitalavstemmingForekomst(gm)?.rotIdVerdi() ?: "1")

        val nyeElementer = mutableListOf<InformasjonsElement>()

        sumNettoPositivEllerNegativPrinsippendring?.let {
            if (it >= BigDecimal.ZERO) {
                nyeElementer.add(
                    InformasjonsElement(
                        egenkapitalavstemming.sumNettoPositivPrinsippendring.key,
                        mapOf(
                            egenkapitalavstemmingForekomstId,
                            egenkapitalavstemming.sumNettoPositivPrinsippendring.gruppe to "fixed"
                        ),
                        it.avrundTilToDesimalerString()
                    )
                )
            } else {
                nyeElementer.add(
                    InformasjonsElement(
                        egenkapitalavstemming.sumNettoNegativPrinsippendring.key,
                        mapOf(
                            egenkapitalavstemmingForekomstId,
                            egenkapitalavstemming.sumNettoNegativPrinsippendring.gruppe to "fixed"
                        ),
                        it.abs().avrundTilToDesimalerString()
                    )
                )
            }
        }

        sumTilleggIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumTilleggIEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumTilleggIEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        sumFradragIEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.sumFradragIEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.sumFradragIEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        utgaaendeEgenkapitalVerdi?.let {
            nyeElementer.add(
                InformasjonsElement(
                    egenkapitalavstemming.utgaaendeEgenkapital.key,
                    mapOf(
                        egenkapitalavstemmingForekomstId,
                        egenkapitalavstemming.utgaaendeEgenkapital.gruppe to "fixed"
                    ),
                    it.avrundTilToDesimalerString()
                )
            )
        }

        return Verdi(GeneriskModell.fra(nyeElementer))
    }

    internal fun beregnSumNettoPositivEllerNegativPrinsippendring(gm: GeneriskModell): BigDecimal? {
        val sumPositivPrinsippendring = sumPositivPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .tilBigDecimal()

        val sumNegativPrinsippendring = sumNegativPrinsippendringKalkyle
            .kalkulertPaa(gm)
            .tilBigDecimal()

        val sumNettoPrinsippendring =  sumPositivPrinsippendring
            .minusNullsafe(sumNegativPrinsippendring)

        val egenkapitalavstemmingForekomst = hentEgenkapitalavstemmingForekomst(gm)
        val utsattSkatt = egenkapitalavstemmingForekomst
            ?.verdiFor(egenkapitalavstemming.utsattSkatt.key)
        val utsattSkattefordel = egenkapitalavstemmingForekomst
            ?.verdiFor(egenkapitalavstemming.utsattSkattefordel.key)

        return sumNettoPrinsippendring
            ?.minus(BigDecimal(utsattSkatt ?: "0"))
            ?.plus(BigDecimal(utsattSkattefordel ?: "0"))
    }

    private fun hentEgenkapitalavstemmingForekomst(gm: GeneriskModell): GeneriskModell? {
        return gm.grupper(egenkapitalavstemming.forekomstType[0]).firstOrNull()
    }
}