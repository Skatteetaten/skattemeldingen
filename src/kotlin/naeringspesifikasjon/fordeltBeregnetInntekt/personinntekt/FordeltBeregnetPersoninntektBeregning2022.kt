package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.divideInternal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.overstyrtVerdiHvisOverstyrt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.erNegativ
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.sum
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.ulik0
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object FordeltBeregnetPersoninntektBeregning2022 : HarKalkylesamling {

    private val skjermingsgrunnlagtyper = listOf(
        skjermingsgrunnlagstype.kode_saldogruppeA.kode,
        skjermingsgrunnlagstype.kode_saldogruppeB.kode,
        skjermingsgrunnlagstype.kode_saldogruppeC.kode,
        skjermingsgrunnlagstype2023.kode_saldogruppeC2.kode,
        skjermingsgrunnlagstype.kode_saldogruppeD.kode,
        skjermingsgrunnlagstype.kode_saldogruppeE.kode,
        skjermingsgrunnlagstype.kode_saldogruppeF.kode,
        skjermingsgrunnlagstype.kode_saldogruppeG.kode,
        skjermingsgrunnlagstype.kode_saldogruppeH.kode,
        skjermingsgrunnlagstype.kode_saldogruppeI.kode,
        skjermingsgrunnlagstype.kode_saldogruppeJ.kode,
        skjermingsgrunnlagstype.kode_lineaertavskrevetAnleggsmiddel.kode,
        skjermingsgrunnlagstype.kode_ikkeAvskrivbartAnleggsmiddel.kode,
        skjermingsgrunnlagstype.kode_ervervetImmatriellRettighet.kode,
        skjermingsgrunnlagstype.kode_aktivertForskningsOgUtviklingskostnad.kode,
        skjermingsgrunnlagstype.kode_varelager.kode,
        skjermingsgrunnlagstype.kode_kundefordring.kode,
    )

    val fodeltBeregnetPersoninntektKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()

        val resultat = if (skalBeregnePersoninntekt(gm)) {
            val beregnetSkjermingsgrunnlag = beregnSkjermingsgrunnlag(gm)

            //Personinntekt
            val oppdatertePersoninntektForekomster = gm
                .erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
                .grupperV2(modell.fordeltBeregnetPersoninntekt)

            val personinntektForekomsterPerIdentifikator: Map<String, List<GeneriskGruppe>> =
                oppdatertePersoninntektForekomster
                    .groupBy { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)!! }

            val naeringinntektForekomsterPerIdentifikator: Map<String, List<GeneriskGruppe>> =
                gm.grupperV2(modell2022.fordeltBeregnetNaeringsinntekt)
                    .filter { it.harVerdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt) }
                    .groupBy { it.verdiFor(modell2022.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt)!! }

            //Beregningen er lenient og beregner kun der det er verdier begge steder, hvis ikke så kan vi ikke kjøre kalkylen
            val oppdaterteFeltForPersonForekomst: List<InformasjonsElement> = personinntektForekomsterPerIdentifikator
                .flatMap { (personinntektIdentifikator, personinntektForekomster) ->
                    val resultat = mutableListOf<InformasjonsElement>()
                    if (personinntektForekomster.size != 1) {
                        error(
                            "Vi kan ikke ha mer enn én forekomst av fordeltBeregnetPersoninntekt" +
                                    " per identifikatorForFordeltBeregnetPersoninntekt. " +
                                    " identifikatorForFordeltBeregnetPersoninntekt=${personinntektIdentifikator}," +
                                    " har verdier=${personinntektForekomster}"
                        )
                    }

                    val personinntektForekomst = personinntektForekomster[0]

                    val fordeltSkattemessigResultatEtterKorreksjonBeloep =
                        naeringinntektForekomsterPerIdentifikator[personinntektIdentifikator]
                            ?.mapNotNull { forekomst -> forekomst.verdiSomBigDecimal(modell2022.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjon) }

                    if (fordeltSkattemessigResultatEtterKorreksjonBeloep != null
                        && fordeltSkattemessigResultatEtterKorreksjonBeloep.isNotEmpty()
                    ) {
                        val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning =
                            personinntektForekomst.overstyrtVerdiHvisOverstyrt(modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning)
                                ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
                                    personinntektForekomst,
                                    fordeltSkattemessigResultatEtterKorreksjonBeloep.sum()
                                )

                        resultat.add(
                            personinntektForekomst.lagFeltMedEgenskaper(
                                modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning
                            )
                        )

                        if (beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning.ulik0()) {
                            val andelAvPersoninntektTilordnetInnehaver =
                                personinntektForekomst.verdiFor(modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver)
                                    ?: "100"
                            val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver =
                                personinntektForekomst.overstyrtVerdiHvisOverstyrt(modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver)
                                    ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
                                        beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                        BigDecimal(andelAvPersoninntektTilordnetInnehaver)
                                    )

                            resultat.add(
                                personinntektForekomst.lagFeltMedEgenskaper(
                                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver,
                                    beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver
                                )
                            )
                        }
                    }
                    resultat
                }

            val resultat = oppdaterteFeltForPersonForekomst.tilGeneriskModell()

            resultat.erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun beregnSkjermingsgrunnlag(
        gm: GeneriskModell,
    ): GeneriskModell {
        return gm.grupperV2(modell.fordeltBeregnetPersoninntekt)
            .map { fordeltBeregnetPersoninntektForekomst ->
                val gjennomsnittsverdier =
                    fordeltBeregnetPersoninntektForekomst.grupper(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag)
                        .filter { it.harVerdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) }
                        .groupBy(
                            { it.verdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype)!! },
                            {
                                val inngaaendeVerdi =
                                    it.verdiSomBigDecimalEller0(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi)
                                val utgaaendeVerdi =
                                    it.verdiSomBigDecimalEller0(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi)
                                (inngaaendeVerdi + utgaaendeVerdi).divideInternal(
                                    BigDecimal(2)
                                )
                            })

                if (gjennomsnittsverdier.isNotEmpty()) {
                    val sumSkjermingsgrunnlagFoerGjeldsfradrag =
                        fordeltBeregnetPersoninntektForekomst.overstyrtVerdiHvisOverstyrt(modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag)
                            ?: beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(gjennomsnittsverdier)

                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        fordeltBeregnetPersoninntektForekomst.overstyrtVerdiHvisOverstyrt(modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag)
                            ?: beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
                                gjennomsnittsverdier,
                                sumSkjermingsgrunnlagFoerGjeldsfradrag
                            )

                    val skjermingsrente =
                        fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimal(modell.fordeltBeregnetPersoninntekt.skjermingsrente)
                            ?.divideInternal(BigDecimal(100))

                    val faktorForSkjermingsrente =
                        (fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimal(modell.fordeltBeregnetPersoninntekt.antallMaanederDrevetIAar)
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag = fordeltBeregnetPersoninntektForekomst.overstyrtVerdiHvisOverstyrt(modell.fordeltBeregnetPersoninntekt.skjermingsfradrag)
                        ?: beregnSkjermingsfradrag(
                            sumSkjermingsgrunnlagFoerGjeldsfradrag,
                            skjermingsrente,
                            sumSkjermingsgrunnlagEtterGjeldsfradrag,
                            faktorForSkjermingsrente
                        )

                    GeneriskGruppe(fordeltBeregnetPersoninntektForekomst.forekomstIder)
                        .leggTilFeltMedEgenskaper(
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag,
                            sumSkjermingsgrunnlagFoerGjeldsfradrag.avrundTilToDesimalerString()
                        )
                        .leggTilFeltMedEgenskaper(
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag,
                            sumSkjermingsgrunnlagEtterGjeldsfradrag.avrundTilToDesimalerString()
                        )
                        .leggTilFeltMedEgenskaper(
                            modell.fordeltBeregnetPersoninntekt.skjermingsfradrag,
                            skjermingsfradrag?.avrundTilToDesimalerString()
                        )
                } else {
                    GeneriskGruppe.tom()
                }
            }
            .tilGeneriskModell()
    }

    private fun beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>
    ): BigDecimal {
        val foersteLedd = gjennomsnittsverdier
            .filter { skjermingsgrunnlagtyper.contains(it.key) }
            .flatMap { it.value }
            .sum()

        val leverandoergjeld =
            gjennomsnittsverdier
                .filter { it.key == skjermingsgrunnlagstype.kode_leverandoergjeld.kode }
                .flatMap { it.value }
                .sum()

        return (foersteLedd - leverandoergjeld).coerceAtLeast(BigDecimal.ZERO)
    }

    private fun beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>,
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal
    ): BigDecimal {
        val foretaksgjeld = gjennomsnittsverdier
            .filter { it.key == skjermingsgrunnlagstype.kode_foretaksgjeld.kode }
            .flatMap { it.value }
            .sum()

        return (sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld)
            .coerceAtLeast(BigDecimal.ZERO)
    }

    private fun beregnSkjermingsfradrag(
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal,
        skjermingsrente: BigDecimal?,
        sumSkjermingsgrunnlagEtterGjeldsfradrag: BigDecimal,
        faktorForSkjermingsrente: BigDecimal
    ): BigDecimal? {
        return if (sumSkjermingsgrunnlagFoerGjeldsfradrag.erNegativ()) {
            BigDecimal.ZERO
        } else if (skjermingsrente != null) {
            sumSkjermingsgrunnlagEtterGjeldsfradrag * faktorForSkjermingsrente * skjermingsrente
        } else {
            null
        }
    }

    private fun beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
        fordeltBeregnetPersoninntektForekomst: GeneriskGruppe,
        fordeltSkattemessigResultatEtterKorreksjon: BigDecimal,
    ): BigDecimal {
        return fordeltSkattemessigResultatEtterKorreksjon
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.rentekostnadPaaForetaksgjeld
                )
            )
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.kapitalinntektTilKorrigeringAvPersoninntekt
                )
            )
            .plus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.kapitalkostnadTilKorrigeringAvPersoninntekt
                )
            )
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.reduksjonsbeloepForLeidEiendomMotInnskudd
                )
            )
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                )
            )
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.inntektsfoertBeloepFraGevinstOgTapskontoSomIkkeInngaarIPersoninntekt
                )
            )
            .plus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.fradragsfoertBeloepFraGevinstOgTapskontoSomIkkeInngaarIPersoninntekt
                )
            )
            .minus(
                fordeltBeregnetPersoninntektForekomst.verdiSomBigDecimalEller0(
                    modell.fordeltBeregnetPersoninntekt.skjermingsfradrag
                )
            )
    }

    private fun beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
        aaretsBeregnedePersoninntektFoerFordelingOgSamordning: BigDecimal,
        andelAvPersoninntektTilordnetInnehaver: BigDecimal
    ): BigDecimal {
        return aaretsBeregnedePersoninntektFoerFordelingOgSamordning
            .times(andelAvPersoninntektTilordnetInnehaver)
            .div(BigDecimal(100))
            .avrundTilToDesimaler()
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fodeltBeregnetPersoninntektKalkyle)
    }
}