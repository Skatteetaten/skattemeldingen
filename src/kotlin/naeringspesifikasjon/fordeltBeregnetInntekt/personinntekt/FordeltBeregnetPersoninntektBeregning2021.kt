package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.divideInternal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.overstyrtVerdiHvisOverstyrt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object FordeltBeregnetPersoninntektBeregning2021 : HarKalkylesamling {

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

    val fordeltBeregnetPersoninntektKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()

        val resultat = if (skalBeregnePersoninntekt(gm)) {
            val beregnetSkjermingsgrunnlag = beregnSkjermingsgrunnlag(gm)

            //Personinntekt
            val oppdatertePersoninntektForekomster = gm
                .erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
                .grupper(modell2021.fordeltBeregnetPersoninntekt)

            val personinntektForekomsterPerIdentifikator: Map<String, List<GeneriskModell>> =
                oppdatertePersoninntektForekomster
                    .groupBy { it.verdiFor(modell2021.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }

            val naeringinntektForekomsterPerIdentifikator: Map<String, List<GeneriskModell>> =
                gm.grupper(modell2021.fordeltBeregnetNaeringsinntekt)
                    .filter { it.harVerdiFor(modell2021.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt) }
                    .groupBy { it.verdiFor(modell2021.fordeltBeregnetNaeringsinntekt.identifikatorForFordeltBeregnetPersoninntekt) }

            //Beregningen er lenient og beregner kun der det er verdier begge steder, hvis ikke så kan vi ikke kjøre kalkylen
            val oppdaterteFeltForPersonForekomst: List<GeneriskModell> = personinntektForekomsterPerIdentifikator
                .mapNotNull { (personinntektIdentifikator, personinntektForekomster) ->
                    var resultat = GeneriskModell.tom()
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
                            ?.map { forekomst -> forekomst.verdiFor(modell2021.fordeltBeregnetNaeringsinntekt.fordeltSkattemessigResultatEtterKorreksjon) }
                            ?.filter { forekomst -> forekomst != null && forekomst.isNotBlank() }

                    if (fordeltSkattemessigResultatEtterKorreksjonBeloep != null
                        && fordeltSkattemessigResultatEtterKorreksjonBeloep.isNotEmpty()
                    ) {
                        val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning = overstyrtVerdiHvisOverstyrt(
                            personinntektForekomst,
                            modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning
                        )
                            ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
                                personinntektForekomst,
                                fordeltSkattemessigResultatEtterKorreksjonBeloep.sumOf { s -> BigDecimal(s) }
                            )

                        resultat = GeneriskModell.fra(
                            InformasjonsElement(
                                modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                mapOf(
                                    modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to personinntektForekomst.rotIdVerdi(),
                                    modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                                ),
                                beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning
                            )
                        )

                        if (beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning != BigDecimal.ZERO) {
                            val andelAvPersoninntektTilordnetInnehaver =
                                personinntektForekomst.verdiFor(modell2021.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver)
                                    ?: "100"
                            val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver =
                                overstyrtVerdiHvisOverstyrt(
                                    personinntektForekomst,
                                    modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver
                                )
                                    ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
                                        beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                        BigDecimal(andelAvPersoninntektTilordnetInnehaver)
                                    )

                            resultat = resultat.erstattEllerLeggTilFelter(
                                InformasjonsElement(
                                    modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver,
                                    mapOf(
                                        modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to personinntektForekomst.rotIdVerdi(),
                                        modell2021.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.gruppe to "fixed"
                                    ),
                                    beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver
                                )
                            )
                        }
                    }
                    resultat
                }

            val resultat = oppdaterteFeltForPersonForekomst
                .stream()
                .collect(GeneriskModell.collectorFraGm())

            resultat.erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
        } else {
            GeneriskModell.tom()
        }
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    private fun beregnSkjermingsgrunnlag(
        gm: GeneriskModell
    ): GeneriskModell {
        return gm.grupper(modell2021.fordeltBeregnetPersoninntekt)
            .stream()
            .map { fordeltBeregnetPersoninntektForekomst ->
                val gjennomsnittsverdier =
                    fordeltBeregnetPersoninntektForekomst.grupper(modell2021.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag)
                        .filter { it.harVerdiFor(modell2021.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) }
                        .groupBy(
                            { it.verdiFor(modell2021.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) },
                            {
                                val inngaaendeVerdi = it.verdiFor(modell2021.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi)
                                val utgaaendeVerdi = it.verdiFor(modell2021.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi)
                                (BigDecimal(inngaaendeVerdi ?: "0") + BigDecimal(utgaaendeVerdi ?: "0")).divideInternal(
                                    BigDecimal(2)
                                )
                            })

                if (gjennomsnittsverdier.isNotEmpty()) {
                    val sumSkjermingsgrunnlagFoerGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            fordeltBeregnetPersoninntektForekomst,
                            modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag
                        )
                            ?: beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(gjennomsnittsverdier)

                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            fordeltBeregnetPersoninntektForekomst,
                            modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag
                        )
                            ?: beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
                                gjennomsnittsverdier,
                                sumSkjermingsgrunnlagFoerGjeldsfradrag
                            )

                    val skjermingsrente =
                        fordeltBeregnetPersoninntektForekomst.verdiFor(modell2021.fordeltBeregnetPersoninntekt.skjermingsrente)
                            ?.let { BigDecimal(it).divideInternal(BigDecimal(100)) }

                    val faktorForSkjermingsrente =
                        (fordeltBeregnetPersoninntektForekomst.verdiFor(modell2021.fordeltBeregnetPersoninntekt.antallMaanederDrevetIAar)
                            ?.let { BigDecimal(it) }
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag = overstyrtVerdiHvisOverstyrt(
                        fordeltBeregnetPersoninntektForekomst,
                        modell2021.fordeltBeregnetPersoninntekt.skjermingsfradrag
                    )
                        ?: beregnSkjermingsfradrag(
                            sumSkjermingsgrunnlagFoerGjeldsfradrag,
                            skjermingsrente,
                            sumSkjermingsgrunnlagEtterGjeldsfradrag,
                            faktorForSkjermingsrente
                        )

                    val idPersonInntekt = fordeltBeregnetPersoninntektForekomst.rotIdVerdi()!!
                    val skjermingsgrunnlagElementer = mutableListOf<InformasjonsElement>()

                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.key,
                        mapOf(
                            modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.gruppe to "fixed"
                        ),
                        sumSkjermingsgrunnlagFoerGjeldsfradrag
                    )

                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.key,
                        mapOf(
                            modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell2021.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.gruppe to "fixed"
                        ),
                        sumSkjermingsgrunnlagEtterGjeldsfradrag
                    )
                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        modell2021.fordeltBeregnetPersoninntekt.skjermingsfradrag.key,
                        mapOf(
                            modell2021.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell2021.fordeltBeregnetPersoninntekt.skjermingsfradrag.gruppe to "fixed"
                        ),
                        skjermingsfradrag
                    )

                    GeneriskModell.fra(skjermingsgrunnlagElementer)
                } else {
                    GeneriskModell.tom()
                }
            }
            .collect(GeneriskModell.collectorFraGm())
    }

    private fun beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>
    ): BigDecimal {
        val foersteLedd = gjennomsnittsverdier
            .filter { skjermingsgrunnlagtyper.contains(it.key) }
            .flatMap { it.value }
            .fold(BigDecimal.ZERO, BigDecimal::add)

        val leverandoergjeld =
            gjennomsnittsverdier
                .filter { it.key == skjermingsgrunnlagstype.kode_leverandoergjeld.kode }
                .flatMap { it.value }
                .fold(BigDecimal.ZERO, BigDecimal::add)

        return if ((foersteLedd - leverandoergjeld).signum() == -1) {
            BigDecimal.ZERO
        } else {
            foersteLedd - leverandoergjeld
        }
    }

    private fun beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
        gjennomsnittsverdier: Map<String, List<BigDecimal>>,
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal
    ): BigDecimal {
        val foretaksgjeld = gjennomsnittsverdier
            .filter { it.key == skjermingsgrunnlagstype.kode_foretaksgjeld.kode }
            .flatMap { it.value }
            .fold(BigDecimal.ZERO, BigDecimal::add)

        return if ((sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld).signum() == -1) {
            BigDecimal.ZERO
        } else {
            sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld
        }
    }

    private fun beregnSkjermingsfradrag(
        sumSkjermingsgrunnlagFoerGjeldsfradrag: BigDecimal,
        skjermingsrente: BigDecimal?,
        sumSkjermingsgrunnlagEtterGjeldsfradrag: BigDecimal,
        faktorForSkjermingsrente: BigDecimal
    ): BigDecimal? {
        return if (sumSkjermingsgrunnlagFoerGjeldsfradrag.signum() == -1) {
            BigDecimal.ZERO
        } else if (skjermingsrente != null) {
            sumSkjermingsgrunnlagEtterGjeldsfradrag * faktorForSkjermingsrente * skjermingsrente
        } else {
            null
        }
    }

    private fun beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
        fordeltBeregnetPersoninntektForekomst: GeneriskModell,
        fordeltSkattemessigResultatEtterKorreksjon: BigDecimal
    ): BigDecimal {
        return fordeltSkattemessigResultatEtterKorreksjon
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.rentekostnadPaaForetaksgjeld
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.kapitalinntektTilKorrigeringAvPersoninntekt
                )
            )
            .plus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.kapitalkostnadTilKorrigeringAvPersoninntekt
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.reduksjonsbeloepForLeidEiendomMotInnskudd
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell2021.fordeltBeregnetPersoninntekt.skjermingsfradrag
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

    private fun leggTilInformasjonselement(
        elementer: MutableList<InformasjonsElement>,
        key: String,
        id: Map<String, String>,
        verdi: BigDecimal?,
    ) {
        if (verdi != null) {
            elementer.add(
                InformasjonsElement(
                    key,
                    id,
                    verdi.avrundTilToDesimalerString()
                )
            )
        }
    }

    private fun nullsafe(
        personForekomst: GeneriskModell, felt: Felt<*>,
    ): BigDecimal {
        val verdiFor = personForekomst.verdiFor(felt)
        return BigDecimal(verdiFor ?: "0")
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetPersoninntektKalkyle)
    }
}