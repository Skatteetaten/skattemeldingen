package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fullRegnskapsplikt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.permanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Spec: https://wiki.sits.no/display/SIR/FR+-+Forskjeller
 */
internal object SpesifikasjonAvForskjellForSelskapOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    private val omregnetSalg = kalkyle("omregnetSalg") {

        hvis (erPetroleumsforetak()
            && modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_spesifikasjonAvForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.oljesalgIUsdTilNormpriskursGjennomFastOrdning.erSann()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) der {
                forekomstType.erLoeftetNormprisregulert.erSann()
            } forHverForekomst {
                settFelt(forekomstType.omregnetSalg) {
                    forekomstType.kvantumIFat * forekomstType.prisPerFat * forekomstType.normpriskurs
                }
            }
        }
    }

    private val normprisinntekt = kalkyle("normprisinntekt") {
        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) forHverForekomst {
                if (forekomstType.erLoeftetNormprisregulert.erSann()) {
                    settFelt(forekomstType.normprisinntekt) {
                        forekomstType.kvantumIFat * forekomstType.normpris
                    }
                } else {
                    settFelt(forekomstType.normprisinntekt) {
                        forekomstType.bokfoertSalg.tall()
                    }
                }
            }
        }
    }

    private val valutatillegg = kalkyle("valutatillegg") {

        hvis (erPetroleumsforetak()
            && modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_spesifikasjonAvForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.oljesalgIUsdTilNormpriskursGjennomFastOrdning.erSann()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) der {
                forekomstType.erLoeftetNormprisregulert.erSann()
                    && (forekomstType.bokfoertSalg - forekomstType.omregnetSalg).erPositiv()
            } forHverForekomst {
                settFelt(forekomstType.valutatillegg) {
                    forekomstType.bokfoertSalg - forekomstType.omregnetSalg
                }
            }
        }
    }

    private val valutafradrag = kalkyle("valutafradrag") {
        hvis (erPetroleumsforetak()
            && modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_spesifikasjonAvForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.oljesalgIUsdTilNormpriskursGjennomFastOrdning.erSann()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) der {
                forekomstType.erLoeftetNormprisregulert.erSann()
                    && (forekomstType.bokfoertSalg - forekomstType.omregnetSalg).erNegativ()
            } forHverForekomst {
                settFelt(forekomstType.valutafradrag) {
                    (forekomstType.bokfoertSalg - forekomstType.omregnetSalg).absoluttverdi()
                }
            }
        }
    }

    private val normpristillegg = kalkyle("normpristillegg") {
        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) der {
                forekomstType.erLoeftetNormprisregulert.erSann()
                    && (forekomstType.normprisinntekt - forekomstType.bokfoertSalg).erPositiv()
            } forHverForekomst {
                settFelt(forekomstType.normpristillegg) {
                    forekomstType.normprisinntekt - forekomstType.bokfoertSalg
                }
            }
        }
    }

    private val normprisfradrag = kalkyle("normprisfradrag") {
        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt.oljesalgOgNormprisinntektPerLoeft) der {
                forekomstType.erLoeftetNormprisregulert.erSann()
                    && (forekomstType.normprisinntekt - forekomstType.bokfoertSalg).erNegativ()
            } forHverForekomst {
                settFelt(forekomstType.normprisfradrag) {
                    (forekomstType.normprisinntekt - forekomstType.bokfoertSalg).absoluttverdi()
                }
            }
        }
    }

    private val samletKvantumIFat = kalkyle("samletKvantumIFat") {

        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
                val samletKvantumIFat = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                    forekomstType.kvantumIFat.tall()
                }

                settFelt(forekomstType.samletKvantumIFat) {
                    samletKvantumIFat
                }
            }
        }
    }

    private val samletBokfoertSalgPerFelt = kalkyle("samletBokfoertSalgPerFelt") {

        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
                val samletBokfoertSalgPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                    forekomstType.bokfoertSalg.tall()
                }

                settFelt(forekomstType.samletBokfoertSalgPerFelt) {
                    samletBokfoertSalgPerFelt +
                        forekomstType.mottattKvalitetskompensasjonInCashForFeltLoeftetITeesside +
                        forekomstType.korreksjonIRegnskapForForegaaendeInntektsaarAvMottattKvalitetskompensasjonInCash_korreksjonIBokfoertSalg +
                        forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormprisIInntektsaaretsRegnskap_korreksjonIBokfoertSalg +
                        forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormpriskursIInntektsaaretsRegnskap_korreksjonIBokfoertSalg
                }
            }
        }
    }

    private val heravMottattKvalitetskompensasjonInCash = kalkyle("heravMottattKvalitetskompensasjonInCash") {

        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
                val heravMottattKvalitetskompensasjonInCash = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
                    (forekomstType.korreksjonIRegnskapForForegaaendeInntektsaarAvMottattKvalitetskompensasjonInCash_korreksjonIBokfoertSalg
                        + forekomstType.mottattKvalitetskompensasjonInCashForFeltLoeftetITeesside)
                }

                settFelt(forekomstType.heravMottattKvalitetskompensasjonInCash) {
                    heravMottattKvalitetskompensasjonInCash
                }
            }
        }
    }

    private val heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash = kalkyle("heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash") {

        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
                val heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
                    (forekomstType.samletBokfoertSalgPerFelt - forekomstType.heravMottattKvalitetskompensasjonInCash)
                }

                settFelt(forekomstType.heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash) {
                    heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash
                }
            }
        }
    }

    private val samletOmregnetSalgPerFelt = kalkyle("samletOmregnetSalgPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
            val samletOmregnetSalgPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                forekomstType.omregnetSalg.tall()
            }

            settFelt(forekomstType.samletOmregnetSalgPerFelt) {
                samletOmregnetSalgPerFelt
            }
        }
    }

    private val samletNormprisinntektPerFelt = kalkyle("samletNormprisinntektPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
            val samletNormprisinntektPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                forekomstType.normprisinntekt.tall()
            }

            settFelt(forekomstType.samletNormprisinntektPerFelt) {
                samletNormprisinntektPerFelt
            }
        }
    }

    private val samletNettoValutatilleggPerFelt = kalkyle("samletNettoValutatilleggPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
             val samletNettoValutatilleggPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                 forekomstType.valutatillegg.tall()
             }

            settFelt(forekomstType.samletNettoValutatilleggPerFelt) {
                samletNettoValutatilleggPerFelt +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormpriskursIInntektsaaretsRegnskap_korreksjonIValutatillegg
            }
        }
    }

    private val samletNettoValutafradragPerFelt = kalkyle("samletNettoValutafradragPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
             val samletNettoValutafradragPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                 forekomstType.valutafradrag.tall()
             }

            settFelt(forekomstType.samletNettoValutafradragPerFelt) {
                samletNettoValutafradragPerFelt +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormpriskursIInntektsaaretsRegnskap_korreksjonIValutafradrag
            }
        }
    }

    private val samletNettoNormpristilleggPerFelt = kalkyle("samletNettoNormpristilleggPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
            val samletNettoNormpristilleggPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                forekomstType.normpristillegg.tall()
            }

            settFelt(forekomstType.samletNettoNormpristilleggPerFelt) {
                samletNettoNormpristilleggPerFelt +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormprisIInntektsaaretsRegnskap_korreksjonINormpristillegg +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormpriskursIInntektsaaretsRegnskap_korreksjonINormpristillegg
            }
        }
    }

    private val samletNettoNormprisfradragPerFelt = kalkyle("samletNettoNormprisfradragPerFelt") {

        forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
            val samletNettoNormprisfradragPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                forekomstType.normprisfradrag.tall()
            }

            settFelt(forekomstType.samletNettoNormprisfradragPerFelt) {
                samletNettoNormprisfradragPerFelt +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormprisIInntektsaaretsRegnskap_korreksjonINormprisfradrag +
                    forekomstType.korreksjonNaarSalgBokfoertIForegaaendeInntektsaarErJustertTilNormpriskursIInntektsaaretsRegnskap_korreksjonINormprisfradrag
            }
        }
    }

    private val samletFraktkostnadMvPerFelt = kalkyle("samletFraktkostnadMvPerFelt") {

        hvis(erPetroleumsforetak()) {
            forekomsterAv(modell.oljesalgOgNormprisinntekt) forHverForekomst {
                val samletFraktkostnadMvPerFelt = forekomsterAv(forekomstType.oljesalgOgNormprisinntektPerLoeft) summerVerdiFraHverForekomst {
                    forekomstType.fraktkostnadMv.tall()
                }

                settFelt(forekomstType.samletFraktkostnadMvPerFelt) {
                    samletFraktkostnadMvPerFelt + forekomstType.fraktkostnaderMvIkkeHenfoerbareTilDetEnkelteLoeft
                }
            }
        }
    }

    private val samletBokfoertSalg = kalkyle("samletBokfoertSalg") {
        val samletBokfoertSalg = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletBokfoertSalgPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletBokfoertSalg) {
            samletBokfoertSalg
        }
    }

    private val samletOmregnetSalg = kalkyle("samletOmregnetSalg") {
        val samletOmregnetSalg = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletOmregnetSalgPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletOmregnetSalg) {
            samletOmregnetSalg
        }
    }

    private val samletNormprisinntekt = kalkyle("samletNormprisinntekt") {
        val samletNormprisinntekt = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletNormprisinntektPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNormprisinntekt) {
            samletNormprisinntekt
        }
    }

    private val samletNettoValutatillegg = kalkyle("samletNettoValutatillegg") {
        val samletNettoValutatillegg = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletNettoValutatilleggPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoValutatillegg) {
            samletNettoValutatillegg
        }
    }

    private val samletNettoValutafradrag = kalkyle("samletNettoValutafradrag") {
        val samletNettoValutafradrag = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletNettoValutafradragPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoValutafradrag) {
            samletNettoValutafradrag
        }
    }

    private val samletNettoNormpristillegg = kalkyle("samletNettoNormpristillegg") {
        val samletNettoNormpristillegg = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletNettoNormpristilleggPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormpristillegg) {
            samletNettoNormpristillegg
        }
    }

    private val samletNettoNormprisfradrag = kalkyle("samletNettoNormprisfradrag") {
        val samletNettoNormprisfradrag = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletNettoNormprisfradragPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormprisfradrag) {
            samletNettoNormprisfradrag
        }
    }

    private val samletFraktkostnadMv = kalkyle("samletFraktkostnadMv") {
        val samletFraktkostnadMv = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
            forekomstType.samletFraktkostnadMvPerFelt.tall()
        }

        settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletFraktkostnadMv) {
            samletFraktkostnadMv
        }
    }

    private val samletSalgsprovisjonOgRabattMv = kalkyle("samletSalgsprovisjonOgRabattMv") {
        hvis(erPetroleumsforetak()) {
            val samletSalgsprovisjonOgRabattMv = forekomsterAv(modell.oljesalgOgNormprisinntekt) summerVerdiFraHverForekomst {
                forekomstType.samletSalgsprovisjonOgRabattMvPerFelt.tall()
            }

            settUniktFelt(modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletSalgsprovisjonOgRabattMv) {
                samletSalgsprovisjonOgRabattMv
            }
        }
    }

    private val normprisTilleggForskjellstypeFelter = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val beloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
                modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormpristillegg +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletFraktkostnadMv +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletSalgsprovisjonOgRabattMv

            val beloepAlminneligInntektFraVirksomhetPaaSokkel =
                modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormpristillegg +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletFraktkostnadMv +
                    modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletSalgsprovisjonOgRabattMv

            val beloepResultatAvFinansinntektOgFinanskostnadMv = modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoValutatillegg.tall()

            oppdaterEllerOpprettForskjellstypePermanentPetroleum(
                forskjellstype = permanentForskjellstype.kode_normprisTillegg,
                beloepSaerskattegrunnlagFraVirksomhetPaaSokkel = beloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
                beloepAlminneligInntektFraVirksomhetPaaSokkel = beloepAlminneligInntektFraVirksomhetPaaSokkel,
                beloepAlminneligInntektFraVirksomhetPaaLand = null,
                beloepResultatAvFinansinntektOgFinanskostnadMv = beloepResultatAvFinansinntektOgFinanskostnadMv,
            )
        }
    }

    private val normprisFradragForskjellstypeFelter = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val beloepSaerskattegrunnlagFraVirksomhetPaaSokkel =
                modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormprisfradrag.tall()

            val beloepAlminneligInntektFraVirksomhetPaaSokkel =
                modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoNormprisfradrag.tall()

            val beloepResultatAvFinansinntektOgFinanskostnadMv = modell.forskjellMellomRegnskapsmessigOgSkattemessigVerdi_samletNettoValutafradrag.tall()

            oppdaterEllerOpprettForskjellstypePermanentPetroleum(
                forskjellstype = permanentForskjellstype.kode_normprisFradrag,
                beloepSaerskattegrunnlagFraVirksomhetPaaSokkel = beloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
                beloepAlminneligInntektFraVirksomhetPaaSokkel = beloepAlminneligInntektFraVirksomhetPaaSokkel,
                beloepAlminneligInntektFraVirksomhetPaaLand = null,
                beloepResultatAvFinansinntektOgFinanskostnadMv = beloepResultatAvFinansinntektOgFinanskostnadMv
            )
        }
    }
    private val friinntektEtterPetroleumsskattelovenFelter = kalkyle {
        hvis(fullRegnskapsplikt() && erPetroleumsforetak()) {

            val beloepSaerskattegrunnlagFraVirksomhetPaaSokkel = if (inntektsaar.tekniskInntektsaar >= 2024) {
                val aaretsSamledeFriinntektSaerskiltAnleggsmiddel =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelForVirksomhetOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                val aaretsSamledeFriinntektGevinstOgTapskonto =
                    forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven) summerVerdiFraHverForekomst {
                        forekomstType.aaretsSamledeFriinntekt.tall()
                    }
                aaretsSamledeFriinntektSaerskiltAnleggsmiddel + aaretsSamledeFriinntektGevinstOgTapskonto
            } else {
                modell.spesifikasjonAvAnleggsmiddel_oevrigTilVisningForSpesifikasjonAvAnleggsmiddel.samletFriinntekt.tall()
            }

            oppdaterEllerOpprettForskjellstypePermanentPetroleum(
                permanentForskjellstype.kode_friinntektEtterPetroleumsskatteloven,
                beloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
            )
        }
    }

    private fun GeneriskModellKontekst.oppdaterEllerOpprettForskjellstypePermanentPetroleum(
        forskjellstype: KodeVerdi,
        beloepSaerskattegrunnlagFraVirksomhetPaaSokkel: BigDecimal? = null,
        beloepAlminneligInntektFraVirksomhetPaaSokkel: BigDecimal? = null,
        beloepAlminneligInntektFraVirksomhetPaaLand: BigDecimal? = null,
        beloepResultatAvFinansinntektOgFinanskostnadMv: BigDecimal? = null,
    ) {
        val forekomst =
            finnForekomstMed(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.permanentForskjellstype lik forskjellstype
            }

        if (forekomst.eksisterer()) {
            oppdaterForekomst(forekomst) {
                settFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) { beloepSaerskattegrunnlagFraVirksomhetPaaSokkel }
                settFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel) { beloepAlminneligInntektFraVirksomhetPaaSokkel }
                settFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaLand) { beloepAlminneligInntektFraVirksomhetPaaLand }
                settFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv) { beloepResultatAvFinansinntektOgFinanskostnadMv }
            }
        } else if (
            beloepSaerskattegrunnlagFraVirksomhetPaaSokkel.harVerdi() ||
            beloepAlminneligInntektFraVirksomhetPaaSokkel.harVerdi() ||
            beloepAlminneligInntektFraVirksomhetPaaLand.harVerdi() ||
            beloepResultatAvFinansinntektOgFinanskostnadMv.harVerdi()
        ) {
            opprettNyForekomstAv(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven) {
                medFelt(
                    modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.permanentForskjellstype,
                    forskjellstype.kode
                )
                medFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) { beloepSaerskattegrunnlagFraVirksomhetPaaSokkel }
                medFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel) { beloepAlminneligInntektFraVirksomhetPaaSokkel }
                medFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaLand) { beloepAlminneligInntektFraVirksomhetPaaLand }
                medFelt(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv) { beloepResultatAvFinansinntektOgFinanskostnadMv }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            omregnetSalg,
            normprisinntekt,
            valutatillegg,
            valutafradrag,
            normpristillegg,
            normprisfradrag,
            samletKvantumIFat,
            samletBokfoertSalgPerFelt,
            heravMottattKvalitetskompensasjonInCash,
            heravSamletBokfoertSalgEksklusivMottattKvalitetskompensasjonInCash,
            samletOmregnetSalgPerFelt,
            samletNormprisinntektPerFelt,
            samletNettoValutatilleggPerFelt,
            samletNettoValutafradragPerFelt,
            samletNettoNormpristilleggPerFelt,
            samletNettoNormprisfradragPerFelt,
            samletFraktkostnadMvPerFelt,
            samletBokfoertSalg,
            samletOmregnetSalg,
            samletNormprisinntekt,
            samletNettoValutatillegg,
            samletNettoValutafradrag,
            samletNettoNormpristillegg,
            samletNettoNormprisfradrag,
            samletFraktkostnadMv,
            samletSalgsprovisjonOgRabattMv,
            normprisTilleggForskjellstypeFelter,
            normprisFradragForskjellstypeFelter,
            friinntektEtterPetroleumsskattelovenFelter
        )
    }
}
