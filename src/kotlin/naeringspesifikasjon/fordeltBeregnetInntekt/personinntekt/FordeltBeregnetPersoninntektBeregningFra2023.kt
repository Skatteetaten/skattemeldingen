package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Aarsliste
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.erNegativ
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.sum
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.ulik0
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntektFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object FordeltBeregnetPersoninntektBeregningFra2023 : HarKalkylesamling {

    private val skjermingsgrunnlagtyper = Aarsliste<KodeVerdi>(2023)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeA)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeB)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeC)
        .bareI(2023, skjermingsgrunnlagstype2023.kode_saldogruppeC2)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeD)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeE)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeF)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeG)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeH)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeI)
        .alleAar(skjermingsgrunnlagstype.kode_saldogruppeJ)
        .alleAar(skjermingsgrunnlagstype.kode_lineaertavskrevetAnleggsmiddel)
        .alleAar(skjermingsgrunnlagstype.kode_ikkeAvskrivbartAnleggsmiddel)
        .alleAar(skjermingsgrunnlagstype.kode_ervervetImmatriellRettighet)
        .alleAar(skjermingsgrunnlagstype.kode_aktivertForskningsOgUtviklingskostnad)
        .alleAar(skjermingsgrunnlagstype.kode_varelager)
        .alleAar(skjermingsgrunnlagstype.kode_kundefordring)
        .listerSortertPaaAar { it.kode }


    val fodeltBeregnetPersoninntektKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()

        val resultat = if (skalBeregnePersoninntektFra2023(gm)) {
            val beregnetSkjermingsgrunnlag = beregnSkjermingsgrunnlag(gm, inntektsaar)

            //Personinntekt
            val oppdatertePersoninntektForekomster = gm
                .erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
                .grupperV2(modell.fordeltBeregnetPersoninntekt)

            val personinntektForekomsterPerIdentifikator: Map<String, List<GeneriskGruppe>> =
                oppdatertePersoninntektForekomster
                    .groupBy { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt)!! }

            val naeringinntektForekomsterPerIdentifikator: Map<String, List<GeneriskGruppe>> =
                gm.grupperV2(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
                    .filter { it.harVerdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt) }
                    .groupBy { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt)!! }

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
                            ?.mapNotNull { forekomst -> forekomst.verdiSomBigDecimal(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultatEtterKorreksjon) }

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

            val resultat = GeneriskModell.fra(oppdaterteFeltForPersonForekomst)

            resultat.erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun beregnSkjermingsgrunnlag(
        gm: GeneriskModell,
        inntektsaar: Inntektsaar,
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
                            ?: beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(gjennomsnittsverdier, inntektsaar)

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
        gjennomsnittsverdier: Map<String, List<BigDecimal>>,
        inntektsaar: Inntektsaar
    ): BigDecimal {
        val skjermingsgrunnlagtyper = skjermingsgrunnlagtyper[inntektsaar.tekniskInntektsaar]!!

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
                    modell.fordeltBeregnetPersoninntekt.salgAvMelkekvote
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