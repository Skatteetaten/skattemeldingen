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
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Aarsliste
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.skalBeregnePersoninntektFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.skjermingsgrunnlagstype2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object FordeltBeregnetPersoninntektBeregningFra2023 : HarKalkylesamling {

    private val skjermingsgrunnlagtyper = Aarsliste<String>()
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeA.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeB.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeC.kode)
        .bareI(2023, skjermingsgrunnlagstype2023.kode_saldogruppeC2.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeD.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeE.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeF.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeG.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeH.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeI.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_saldogruppeJ.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_lineaertavskrevetAnleggsmiddel.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_ikkeAvskrivbartAnleggsmiddel.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_ervervetImmatriellRettighet.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_aktivertForskningsOgUtviklingskostnad.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_varelager.kode)
        .fra(2023, skjermingsgrunnlagstype.kode_kundefordring.kode)
        .sorterPaaAarFra(2023)


    val fodeltBeregnetPersoninntektKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()

        val resultat = if (skalBeregnePersoninntektFra2023(gm)) {
            val beregnetSkjermingsgrunnlag = beregnSkjermingsgrunnlag(gm, inntektsaar)

            //Personinntekt
            val oppdatertePersoninntektForekomster = gm
                .erstattEllerLeggTilFelter(beregnetSkjermingsgrunnlag)
                .grupper(modell.fordeltBeregnetPersoninntekt)

            val personinntektForekomsterPerIdentifikator: Map<String, List<GeneriskModell>> =
                oppdatertePersoninntektForekomster
                    .groupBy { it.verdiFor(modell.fordeltBeregnetPersoninntekt.identifikatorForFordeltBeregnetPersoninntekt) }

            val naeringinntektForekomsterPerIdentifikator: Map<String, List<GeneriskModell>> =
                gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
                    .filter { it.harVerdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt) }
                    .groupBy { it.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.identifikatorForFordeltBeregnetPersoninntekt) }

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
                            ?.map { forekomst -> forekomst.verdiFor(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf.fordeltSkattemessigResultatEtterKorreksjon) }
                            ?.filter { forekomst -> forekomst != null && forekomst.isNotBlank() }

                    if (fordeltSkattemessigResultatEtterKorreksjonBeloep != null
                        && fordeltSkattemessigResultatEtterKorreksjonBeloep.isNotEmpty()
                    ) {
                        val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning = overstyrtVerdiHvisOverstyrt(
                            personinntektForekomst,
                            modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.key
                        )
                            ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordning(
                                personinntektForekomst,
                                fordeltSkattemessigResultatEtterKorreksjonBeloep.sumOf { s -> BigDecimal(s) }
                            )

                        resultat = GeneriskModell.fra(
                            InformasjonsElement(
                                modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                mapOf(
                                    modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to personinntektForekomst.rotIdVerdi(),
                                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordning.gruppe to "fixed"
                                ),
                                beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning
                            )
                        )

                        if (beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning != BigDecimal.ZERO) {
                            val andelAvPersoninntektTilordnetInnehaver =
                                personinntektForekomst.verdiFor(modell.fordeltBeregnetPersoninntekt.andelAvPersoninntektTilordnetInnehaver)
                                    ?: "100"
                            val beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver =
                                overstyrtVerdiHvisOverstyrt(
                                    personinntektForekomst,
                                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.key
                                )
                                    ?: beregnAaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver(
                                        beregnetAaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                                        BigDecimal(andelAvPersoninntektTilordnetInnehaver)
                                    )

                            resultat = resultat.erstattEllerLeggTilFelter(
                                InformasjonsElement(
                                    modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver,
                                    mapOf(
                                        modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to personinntektForekomst.rotIdVerdi(),
                                        modell.fordeltBeregnetPersoninntekt.aaretsBeregnedePersoninntektFoerFordelingOgSamordningTilordnetInnehaver.gruppe to "fixed"
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
        gm: GeneriskModell,
        inntektsaar: Inntektsaar,
    ): GeneriskModell {
        return gm.grupper(modell.fordeltBeregnetPersoninntekt)
            .stream()
            .map { fordeltBeregnetPersoninntektForekomst ->
                val gjennomsnittsverdier =
                    fordeltBeregnetPersoninntektForekomst.grupper(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag)
                        .filter { it.harVerdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) }
                        .groupBy(
                            { it.verdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) },
                            {
                                val inngaaendeVerdi =
                                    it.verdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi)
                                val utgaaendeVerdi =
                                    it.verdiFor(modell.fordeltBeregnetPersoninntekt.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi)
                                (BigDecimal(inngaaendeVerdi ?: "0") + BigDecimal(utgaaendeVerdi ?: "0")).divideInternal(
                                    BigDecimal(2)
                                )
                            })

                if (gjennomsnittsverdier.isNotEmpty()) {
                    val sumSkjermingsgrunnlagFoerGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            fordeltBeregnetPersoninntektForekomst,
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.key
                        )
                            ?: beregnSumSkjermingsgrunnlagFoerGjeldsfradrag(gjennomsnittsverdier, inntektsaar)

                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        overstyrtVerdiHvisOverstyrt(
                            fordeltBeregnetPersoninntektForekomst,
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.key
                        )
                            ?: beregnSumSkjermingsgrunnlagEtterGjeldsfradrag(
                                gjennomsnittsverdier,
                                sumSkjermingsgrunnlagFoerGjeldsfradrag
                            )

                    val skjermingsrente =
                        fordeltBeregnetPersoninntektForekomst.verdiFor(modell.fordeltBeregnetPersoninntekt.skjermingsrente)
                            ?.let { BigDecimal(it).divideInternal(BigDecimal(100)) }

                    val faktorForSkjermingsrente =
                        (fordeltBeregnetPersoninntektForekomst.verdiFor(modell.fordeltBeregnetPersoninntekt.antallMaanederDrevetIAar)
                            ?.let { BigDecimal(it) }
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag = overstyrtVerdiHvisOverstyrt(
                        fordeltBeregnetPersoninntektForekomst,
                        modell.fordeltBeregnetPersoninntekt.skjermingsfradrag.key
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
                        modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag,
                        mapOf(
                            modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagFoerGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagFoerGjeldsfradrag
                    )

                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag,
                        mapOf(
                            modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell.fordeltBeregnetPersoninntekt.sumSkjermingsgrunnlagEtterGjeldsfradrag.gruppe!! to "fixed"
                        ),
                        sumSkjermingsgrunnlagEtterGjeldsfradrag
                    )
                    leggTilInformasjonselement(
                        skjermingsgrunnlagElementer,
                        modell.fordeltBeregnetPersoninntekt.skjermingsfradrag,
                        mapOf(
                            modell.fordeltBeregnetPersoninntekt.rotForekomstIdNoekkel to idPersonInntekt,
                            modell.fordeltBeregnetPersoninntekt.skjermingsfradrag.gruppe!! to "fixed"
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
        gjennomsnittsverdier: Map<String, List<BigDecimal>>,
        inntektsaar: Inntektsaar
    ): BigDecimal {
        var skjermingsgrunnlagtyper = skjermingsgrunnlagtyper[inntektsaar.tekniskInntektsaar]!!

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
        fordeltSkattemessigResultatEtterKorreksjon: BigDecimal,
    ): BigDecimal {
        return fordeltSkattemessigResultatEtterKorreksjon
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.rentekostnadPaaForetaksgjeld
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.kapitalinntektTilKorrigeringAvPersoninntekt
                )
            )
            .plus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.kapitalkostnadTilKorrigeringAvPersoninntekt
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.reduksjonsbeloepForLeidEiendomMotInnskudd
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.salgAvMelkekvote
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.inntektsfoertBeloepFraGevinstOgTapskontoSomIkkeInngaarIPersoninntekt
                )
            )
            .plus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
                    modell.fordeltBeregnetPersoninntekt.fradragsfoertBeloepFraGevinstOgTapskontoSomIkkeInngaarIPersoninntekt
                )
            )
            .minus(
                nullsafe(
                    fordeltBeregnetPersoninntektForekomst,
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

    private fun leggTilInformasjonselement(
        elementer: MutableList<InformasjonsElement>,
        felt: Felt<*>,
        id: Map<String, String>,
        verdi: BigDecimal?,
    ) {
        if (verdi != null) {
            elementer.add(
                InformasjonsElement(
                    felt,
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
        return Kalkylesamling(fodeltBeregnetPersoninntektKalkyle)
    }
}