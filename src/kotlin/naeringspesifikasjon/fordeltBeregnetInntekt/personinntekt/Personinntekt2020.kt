package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.avrundTilToDesimalerString
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.divideInternal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskGruppe
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v1_2020.Skjermingsgrunnlagstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.tilGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.sum
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

/**
 * Spesifisert her: https://wiki.sits.no/display/SIR/FR-Personinntekt+fra+ENK
 */
object PersoninntektBeregning2020 : HarKalkylesamling {
    val fordeltBeregnetPersoninntektKalkyle = kalkyle {
        val gm = generiskModell.tilGeneriskModell()
        val personinntektForekomster = gm.grupperV2(modell2020.personinntektFraEnkeltpersonforetak)

        val nyeElementerForSkjemingsfradrag: List<GeneriskGruppe> = personinntektForekomster
            .map { forekomstPerson ->
                val gjennomsnittsverdier =
                    forekomstPerson.grupper(modell2020.personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag)
                        .groupBy(
                            { it.verdiFor(modell2020.personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.skjermingsgrunnlagstype) },
                            {
                                (it.verdiSomBigDecimal(modell2020.personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.inngaaendeVerdi) +
                                    it.verdiSomBigDecimal(modell2020.personinntektFraEnkeltpersonforetak.spesifikasjonAvSkjermingsgrunnlag.utgaaendeVerdi))
                                    ?.divideInternal(
                                        BigDecimal(2)
                                    )
                            })
                val skjermingsgrunnlagtyper = listOf(
                    Skjermingsgrunnlagstype.saldogruppeA,
                    Skjermingsgrunnlagstype.saldogruppeB,
                    Skjermingsgrunnlagstype.saldogruppeC,
                    Skjermingsgrunnlagstype.saldogruppeC2,
                    Skjermingsgrunnlagstype.saldogruppeD,
                    Skjermingsgrunnlagstype.saldogruppeE,
                    Skjermingsgrunnlagstype.saldogruppeF,
                    Skjermingsgrunnlagstype.saldogruppeG,
                    Skjermingsgrunnlagstype.saldogruppeH,
                    Skjermingsgrunnlagstype.saldogruppeI,
                    Skjermingsgrunnlagstype.saldogruppeJ,
                    Skjermingsgrunnlagstype.lineaertavskrevetAnleggsmiddel,
                    Skjermingsgrunnlagstype.ikkeAvskrivbartAnleggsmiddel,
                    Skjermingsgrunnlagstype.ervervetImmatriellRettighet,
                    Skjermingsgrunnlagstype.aktivertForskningsOgUtvklingskostnad,
                    Skjermingsgrunnlagstype.varelager,
                    Skjermingsgrunnlagstype.kundefordring,
                )

                if (gjennomsnittsverdier.isNotEmpty()) {

                    val foersteLedd = gjennomsnittsverdier
                        .filter { skjermingsgrunnlagtyper.contains(it.key) }
                        .flatMap { it.value }
                        .sum()

                    val leverandoergjeld =
                        gjennomsnittsverdier
                            .filter { it.key == Skjermingsgrunnlagstype.leverandoergjeld }
                            .flatMap { it.value }
                            .sum()

                    val foretaksgjeld = gjennomsnittsverdier
                        .filter { it.key == Skjermingsgrunnlagstype.foretaksgjeld }
                        .flatMap { it.value }
                        .sum()

                    val sumSkjermingsgrunnlagFoerGjeldsfradrag =
                        (foersteLedd - leverandoergjeld) medMinimumsverdi 0

                    val sumSkjermingsgrunnlagEtterGjeldsfradrag =
                        (sumSkjermingsgrunnlagFoerGjeldsfradrag - foretaksgjeld) medMinimumsverdi 0

                    val skjermingsrente =
                        forekomstPerson.verdiSomBigDecimal(modell2020.personinntektFraEnkeltpersonforetak.skjermingsrente)
                            ?.divideInternal(BigDecimal(100))

                    val faktorForSkjermingsrente =
                        (forekomstPerson.verdiSomBigDecimal(modell2020.personinntektFraEnkeltpersonforetak.antallMaanederDrevetIAar)
                            ?: BigDecimal(12)).divideInternal(BigDecimal(12))

                    val skjermingsfradrag =
                        if (sumSkjermingsgrunnlagFoerGjeldsfradrag.erNegativ()) {
                            BigDecimal.ZERO
                        } else if (skjermingsrente != null) {
                            sumSkjermingsgrunnlagEtterGjeldsfradrag * faktorForSkjermingsrente * skjermingsrente
                        } else {
                            null
                        }

                    GeneriskGruppe(forekomstPerson.forekomstIder)
                        .leggTilFeltMedEgenskaper(
                            modell2020.personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagFoerGjeldsfradrag,
                            sumSkjermingsgrunnlagFoerGjeldsfradrag?.avrundTilToDesimalerString()
                        )
                        .leggTilFeltMedEgenskaper(
                            modell2020.personinntektFraEnkeltpersonforetak.sumSkjermingsgrunnlagEtterGjeldsfradrag,
                            sumSkjermingsgrunnlagEtterGjeldsfradrag?.avrundTilToDesimalerString()
                        )
                        .leggTilFeltMedEgenskaper(
                            modell2020.personinntektFraEnkeltpersonforetak.skjermingsfradrag,
                            skjermingsfradrag?.avrundTilToDesimalerString()
                        )
                } else {
                    GeneriskGruppe.tom()
                }
            }

        //Personinntekt
        val oppdatertePersonForekomster = gm
            .concat(nyeElementerForSkjemingsfradrag.tilGeneriskModell())
            .grupperV2(modell2020.personinntektFraEnkeltpersonforetak)

        val personForekomstPerIdentifikator =
            oppdatertePersonForekomster.groupBy { it.verdiFor(modell2020.personinntektFraEnkeltpersonforetak.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt) }

        val fordelingAvNaeringinntektPerIdentifikator: Map<String, List<GeneriskGruppe>> =
            gm.grupperV2(modell2020.fordelingAvNaeringsinntekt)
                .filter { it.harVerdiFor(modell2020.fordelingAvNaeringsinntekt.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt) }
                .groupBy { it.verdiFor(modell2020.fordelingAvNaeringsinntekt.identifikatorForFordelingAvNaeringsinntektOgPersoninntekt)!! }

        //Beregningen er lenient og beregner kun der det er verdier begge steder, hvis ikke så kan vi ikke kjøre kalkylen
        val oppdaterteFeltForPersonForekomst: List<InformasjonsElement> = personForekomstPerIdentifikator
            .mapNotNull {
                if (it.value.size != 1) {
                    error(
                        "Vi kan ikke ha mer enn en forekomst av personinntektFraEnkeltmannsforetak per identifikatortype, " +
                            "identifikatorForFordelingAvNaeringsinntektOgPersoninntekt=${it.key}, har verdier=${it.value}"
                    )
                }
                val personForekomst = it.value[0]!!
                val naeringsfordelingForekomster = fordelingAvNaeringinntektPerIdentifikator[it.key]
                if (naeringsfordelingForekomster != null) {
                    if (naeringsfordelingForekomster.size != 1) {
                        error(
                            "Vi kan ikke ha mer enn en forekomst av fordelingAvNaeringsinntekt per identifikatortype, " +
                                "identifikatorForFordelingAvNaeringsinntektOgPersoninntekt=${it.key}, har verdier=${it.value}"
                        )
                    }

                    val forekomstAvNaeringsinntekt = naeringsfordelingForekomster[0]
                    val verdi =
                        forekomstAvNaeringsinntekt.verdiSomBigDecimal(modell2020.fordelingAvNaeringsinntekt.skattemessigResultatForNaeringEtterKorreksjon)
                    if (verdi != null) {
                        val aaretsBeregnedePersoninntektFoerFordelingOgSamordning =
                            verdi
                                .minus(
                                    personForekomst.verdiSomBigDecimalEller0(
                                        modell2020.personinntektFraEnkeltpersonforetak.rentekostnadPaaForetaksgjeld
                                    )
                                )
                                .minus(personForekomst.verdiSomBigDecimalEller0(modell2020.personinntektFraEnkeltpersonforetak.kapitalinntekt))
                                .plus(personForekomst.verdiSomBigDecimalEller0(modell2020.personinntektFraEnkeltpersonforetak.kapitalkostnad))
                                .minus(
                                    personForekomst.verdiSomBigDecimalEller0(
                                        modell2020.personinntektFraEnkeltpersonforetak.reduksjonsbeloepForLeidEiendomMotInnskudd
                                    )
                                )
                                .minus(
                                    personForekomst.verdiSomBigDecimalEller0(
                                        modell2020.personinntektFraEnkeltpersonforetak.gevinstVedRealisasjonAvAlminneligGaardsbrukEllerSkogbruk
                                    )
                                )
                                .minus(personForekomst.verdiSomBigDecimalEller0(modell2020.personinntektFraEnkeltpersonforetak.skjermingsfradrag))

                        return@mapNotNull personForekomst.lagFeltMedEgenskaper(
                            modell2020.personinntektFraEnkeltpersonforetak.aaretsBeregnedePersoninntektFoerFordelingOgSamordning,
                            aaretsBeregnedePersoninntektFoerFordelingOgSamordning
                        )
                    }
                }
                null
            }

        val resultat = nyeElementerForSkjemingsfradrag.tilGeneriskModell().concat(oppdaterteFeltForPersonForekomst)
        leggTilIKontekst(GeneriskModellForKalkyler(resultat))
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(fordeltBeregnetPersoninntektKalkyle)
    }
}