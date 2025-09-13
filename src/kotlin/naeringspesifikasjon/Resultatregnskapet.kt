package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v4_2023.resultatregnskapOgBalanse_2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v4_2023.resultatregnskapOgBalanse_2023.kostnadIFilialregnskap.kode_avskrivningIFilialregnskap
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftsinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftskostnad
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2023

/**
 *  https://wiki.sits.no/display/SIR/Kalkyler+resultatregnskap+for+regnskapspliktstype+1+og+5
 *
 *  inntektFraGevinstOgTapskonto er et oppsummert felt fra Forekomster under GevinstOgTapskonto. Denne verdien skal inn som
 *  annenDriftsinntekt med type/konto 3890. Innslaget som er en forekomst av annenDriftsinntekt
 *  med annenDriftsinntektstype (3890) er således en sum
 *  av denne typen. Det skal ikke være mer enn en sum per type i denne listen.
 */
internal object Resultatregnskapet : HarKalkylesamling {

    val aaretsAvskrivningKalkyle = kalkyle("aaretsAvskrivning") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            val aaretsAvskrivningForSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val beloepBasertPaaInntektsaar = if (inntektsaar.tekniskInntektsaar < 2023)
                modell2022.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.samletAvskrivningForSaerskiltAnleggsmiddelIKraftverk.tall()
            else
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk.tall()
                }

            val beloep = aaretsAvskrivningForSaldoavskrevetAnleggsmiddel +
                aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel +
                beloepBasertPaaInntektsaar

            if (
                harMinstEnForekomstAv(modell.resultatregnskap_inntektIFilialregnskap.inntekt) ||
                harMinstEnForekomstAv(modell.resultatregnskap_kostnadIFilialregnskap.kostnad)
            ) {
                opprettNyForekomstForFilialkostnad(beloep)
            } else {
                opprettNyForekomstForAnnenDriftskostnad(
                    beloep,
                    annenDriftskostnad.kode_6000
                )
            }
        }
    }

    val annenDriftsinntektstypeInntektKalkyle = kalkyle("annenDriftsinntektstypeInntekt") {
        hvis(
            ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()
        ) {
            opprettNyForekomstForAnnenDriftsinntekt(
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektFraGevinstOgTapskonto.tall()
                },
                annenDriftsinntekt.kode_3890
            )
        }
    }

    val aaretsInntektsfoeringAvNegativSaldoKalkyle = kalkyle("aaretsInntektsfoeringAvNegativSaldo") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val aaretsInntektsfoeringAvNegativSaldo =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
                }
            opprettNyForekomstForAnnenDriftsinntekt(
                vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear +
                    vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart +
                    vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo +
                    aaretsInntektsfoeringAvNegativSaldo,
                annenDriftsinntekt.kode_3895
            )
        }
    }

    val annenDriftsinntektstypeFradragKalkyle = kalkyle("annenDriftsinntektstypeFradrag") {
        hvis(ingenEllerBegrensetRegnskapsplikt() && erIkkeSkjoennsfastsatt()) {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto stoerreEnn 0
                } summerVerdiFraHverForekomst {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto.tall()
                },
                annenDriftskostnad.kode_7890
            )
        }
    }

    val inntektFraToemmerkontoKalkyle = kalkyle("inntektFraToemmerkonto") {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
            opprettNyForekomstForAnnenDriftsinntekt(
                forekomsterAv(modell.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektFraToemmerkonto.tall()
                },
                annenDriftsinntekt.kode_3910
            )
        }
    }

    val inntektsfradragFraToemmerkontoKalkyle = kalkyle("inntektsfradragFraToemmerkonto") {
        hvis(ingenEllerBegrensetRegnskapsplikt()) {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektsfradragFraToemmerkonto.tall()
                },
                annenDriftskostnad.kode_7911
            )
        }
    }

    val andelAvDriftsresultatOverfoertTilToemmerkontoKalkyle =
        kalkyle("andelAvDriftsresultatOverfoertTilToemmerkonto") {
            hvis(ingenEllerBegrensetRegnskapsplikt()) {
                opprettNyForekomstForAnnenDriftskostnad(
                    forekomsterAv(modell.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                        forekomstType.andelAvDriftsresultatOverfoertTilToemmerkonto.tall()
                    },
                    annenDriftskostnad.kode_7910
                )
            }
        }

    val tilbakefoertKostnadForPrivatBrukAvNaeringsbilKalkyle =
        kalkyle("tilbakefoertKostnadForPrivatBrukAvNaeringsbil") {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_motorkjoeretoeyINaering) der {
                    forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil stoerreEllerLik 0
                } summerVerdiFraHverForekomst {
                    forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil.tall()
                },
                annenDriftskostnad.kode_7099
            )
        }

    private fun GeneriskModellKontekst.opprettNyForekomstForAnnenDriftsinntekt(
        beloep: BigDecimal?,
        kode: KodeVerdi
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt) {
                medId(kode.kode)
                medFelt(
                    modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt.type,
                    kode.kode
                )
                medFelt(
                    modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt.beloep,
                    beloep
                )
            }
        }
    }

    private fun GeneriskModellKontekst.opprettNyForekomstForAnnenDriftskostnad(
        beloep: BigDecimal?,
        kode: KodeVerdi,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad) {
                medId(kode.kode)
                medFelt(
                    modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad.type,
                    kode.kode
                )
                medFelt(
                    modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad.beloep,
                    beloep
                )
            }
        }
    }
    private fun GeneriskModellKontekst.opprettNyForekomstForFilialkostnad(
        beloep: BigDecimal?,
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell.resultatregnskap_kostnadIFilialregnskap.kostnad) {
                medId(kode_avskrivningIFilialregnskap.kode)
                medFelt(
                    modell.resultatregnskap_kostnadIFilialregnskap.kostnad.type,
                    kode_avskrivningIFilialregnskap.kode
                )
                medFelt(
                    modell.resultatregnskap_kostnadIFilialregnskap.kostnad.beloep,
                    beloep
                )
            }
        }
    }

    private val sumDriftsinntekterKalkyle = kalkyle("sumDriftsinntekter") {
        hvis(!gjelderBankOgForsikring()) {
            val sumPositivSalgsinntekt =
                summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativSalgsinntekt =
                summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)

            val sumSalgsinntekt = sumPositivSalgsinntekt - sumNegativSalgsinntekt

            val sumPositivAnnenDriftsinntekt = summerAnnenDriftsinntekt(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativAnnenDriftsinntekt = summerAnnenDriftsinntekt(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            val sumAnnenDriftsinntekt = sumPositivAnnenDriftsinntekt - sumNegativAnnenDriftsinntekt

            settUniktFelt(modell.resultatregnskap_driftsinntekt_sumDriftsinntekt) {
                sumSalgsinntekt + sumAnnenDriftsinntekt
            }
        }
    }

    private val sumDriftsinntekterBankOgForsikringKalkyle = kalkyle("sumDriftsinntekterBankOgForsikring") {
        hvis(gjelderBankOgForsikring()) {
            settUniktFelt(modell.resultatregnskap_driftsinntekt_sumDriftsinntekt) {
                forekomsterAv(modell.resultatregnskap_driftsinntekt_bankOgForsikringsinntekt.inntekt) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }

    private val sumDriftsinntekterSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak =
        kalkyle("sumDriftsinntekterSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                val inntektsaar = inntektsaar
                settUniktFelt(modell.resultatregnskap_driftsinntekt_heravAndelInntektTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    forekomsterAv(modell.resultatregnskap_driftsinntekt_bankOgForsikringsinntekt.inntekt) der {
                        if (inntektsaar.tekniskInntektsaar >= 2024) {
                            ResultatregnskapOgBalanse.inngaarITekniskRegnskapForLivsforsikringOgPensjonskasseforetak(
                                inntektsaar,
                                forekomstType.type.verdi()
                            )
                        } else {
                            forekomstType.type.harVerdi() && forekomstType.type likEnAv listOf(
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_05_0_10,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_05_0_20,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_07_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_30_2_20,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_73_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_40,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_60,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_80,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_79_0_00,
                            )
                        }
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private val sumDriftsinntekterSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak =
        kalkyle("sumDriftsinntekterSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                val inntektsaar = inntektsaar
                settUniktFelt(modell.resultatregnskap_driftsinntekt_heravAndelInntektIkkeTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    forekomsterAv(modell.resultatregnskap_driftsinntekt_bankOgForsikringsinntekt.inntekt) der {
                        if (inntektsaar.tekniskInntektsaar >= 2024) {
                            ResultatregnskapOgBalanse.inngaarIkkeITekniskRegnskapForLivsforsikringOgPensjonskasseforetak(
                                inntektsaar,
                                forekomstType.type.verdi()
                            )
                        } else {
                            forekomstType.type.harVerdi() && forekomstType.type likEnAv listOf(
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_0_40_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_0_91_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_0_99_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_1_16_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_2_30_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_11_3_50_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_30_2_20_51,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_30_2_20_91,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_30_2_20_99,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_0_40_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_0_99_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_2_20_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_2_30_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_3_50_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_62_5_91_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_0_40_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_0_99_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_2_20_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_2_30_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_3_50_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_1_63_5_91_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_73_0_00_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_40_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_60_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_40_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringsinntekt.kode_2_78_0_60_00
                            )
                        }
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private val sumDriftskostnaderSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak =
        kalkyle("sumDriftskostnaderSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                val inntektsaar = inntektsaar
                settUniktFelt(modell.resultatregnskap_driftskostnad_heravAndelKostnadTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    forekomsterAv(modell.resultatregnskap_driftskostnad_bankOgForsikringskostnad.kostnad) der {
                        if (inntektsaar.tekniskInntektsaar >= 2024) {
                            ResultatregnskapOgBalanse.inngaarITekniskRegnskapForLivsforsikringOgPensjonskasseforetak(
                                inntektsaar,
                                forekomstType.type.verdi()
                            )
                        } else {
                            forekomstType.type.harVerdi() && forekomstType.type likEnAv listOf(
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_05_0_10,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_05_0_20,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_07_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_08_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_09_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_11_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_00_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_9_00_0_00
                            )
                        }
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private val sumDriftskostnaderSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak =
        kalkyle("sumDriftskostnaderSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                val inntektsaar = inntektsaar
                settUniktFelt(modell.resultatregnskap_driftskostnad_heravAndelKostnadIkkeTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    forekomsterAv(modell.resultatregnskap_driftskostnad_bankOgForsikringskostnad.kostnad) der {
                        if (inntektsaar.tekniskInntektsaar >= 2024) {
                            ResultatregnskapOgBalanse.inngaarIkkeITekniskRegnskapForLivsforsikringOgPensjonskasseforetak(
                                inntektsaar,
                                forekomstType.type.verdi()
                            )
                        } else {
                            forekomstType.type.harVerdi() && forekomstType.type likEnAv listOf(
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_11_0_40,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_11_0_91,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_11_0_99,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_4_11_8_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_51_0_10,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_51_0_20,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_51_0_50,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_51_0_99,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_71_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_73_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_78_0_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_78_0_40,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_78_0_51,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_5_78_0_52,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_6_62_5_00,
                                resultatregnskapOgBalanse_2023.bankOgForsikringskostnad.kode_6_63_5_00
                            )
                        }
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }

    private fun GeneriskModellKontekst.summerSalgsinntekterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftsinntekt_salgsinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSalgsinntekterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }

    private fun GeneriskModellKontekst.summerAnnenDriftsinntekt(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerAnnenDriftsinntekt.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }


    val sumDriftskostnaderKalkyle = kalkyle("sumDriftskostnader") {
        hvis(!gjelderBankOgForsikring()) {
            val sumPositiveVarekostnader =
                summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativeVarekostnader =
                summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            val sumVarekostnader = sumPositiveVarekostnader - sumNegativeVarekostnader


            val sumPositiveLoennskostnader =
                summerLoennskostnader(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativeLoennskostnader =
                summerLoennskostnader(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            val sumLoennskostnader = sumPositiveLoennskostnader - sumNegativeLoennskostnader


            val sumPositiveAndreDriftskostnader =
                summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativeAndreDriftskostnader =
                summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            val sumAndreDriftskostnader = sumPositiveAndreDriftskostnader - sumNegativeAndreDriftskostnader

            settUniktFelt(modell.resultatregnskap_driftskostnad_sumDriftskostnad) {
                sumVarekostnader + sumLoennskostnader + sumAndreDriftskostnader
            }
        }
    }

    private fun GeneriskModellKontekst.summerLoennskostnader(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_loennskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerLoennskostnader.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }

    val sumDriftskostnaderBankOgForsikringKalkyle = kalkyle("sumDriftskostnaderBankOgForsikring") {
        hvis(gjelderBankOgForsikring()) {
            settUniktFelt(modell.resultatregnskap_driftskostnad_sumDriftskostnad) {
                forekomsterAv(modell.resultatregnskap_driftskostnad_bankOgForsikringskostnad.kostnad) summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }

    private fun GeneriskModellKontekst.summerVarekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_varekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerVarekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }

    private fun GeneriskModellKontekst.summerAndreDriftskostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerAndreDriftskostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }

    private val sumFinansinntektKalkyle = kalkyle("sumFinansinntekt") {
        settUniktFelt(modell.resultatregnskap_sumFinansinntekt) {
            forekomsterAv(modell.resultatregnskap_finansinntekt.inntekt) summerVerdiFraHverForekomst {
                forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
            }
        }
    }

    private val sumFinanskostnadKalkyle = kalkyle("sumFinanskostnad") {
        settUniktFelt(modell.resultatregnskap_sumFinanskostnad) {
            forekomsterAv(modell.resultatregnskap_finanskostnad.kostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
            }
        }
    }

    val sumSkattekostnadKalkyle = kalkyle("sumSkattekostnad") {
        hvis(fullRegnskapsplikt()) {
            val sumPositiveSkattekostnader =
                summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativeSkattekostnader =
                summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)

            settUniktFelt(modell.resultatregnskap_sumSkattekostnad) {
                sumPositiveSkattekostnader - sumNegativeSkattekostnader
            }
        }
    }

    private fun GeneriskModellKontekst.summerSkattekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_skattekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSkattekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
        }

    private val sumBankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultatKalkyle = kalkyle("sumBankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultat") {
        settUniktFelt(modell.resultatregnskap_sumBankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultatet) {
            forekomsterAv(modell.resultatregnskap_bankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultatet.inntektEllerKostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumBankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultatKalkyle = kalkyle("sumBankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultat") {
        settUniktFelt(modell.resultatregnskap_sumBankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultatet) {
            forekomsterAv(modell.resultatregnskap_bankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultatet.inntektEllerKostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumEndringIEgenkapitalBankOgForsikringKalkyle = kalkyle("sumEndringIEgenkapitalBankOgForsikring") {
        settUniktFelt(modell.resultatregnskap_sumEndringIEgenkapitalBankOgForsikring) {
            forekomsterAv(modell.resultatregnskap_endringIEgenkapitalInnenBankOgForsikring.endringsbeloep) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumInntektIFilialregnskapKalkyle = kalkyle("sumInntektIFilialregnskap") {
        val sumPositive = summerInntektIFilialRegnskapForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
        val sumNegative = summerInntektIFilialRegnskapForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
        settUniktFelt(modell.resultatregnskap_sumInntektIFilialregnskap) {
            sumPositive - sumNegative
        }
    }
    private fun GeneriskModellKontekst.summerInntektIFilialRegnskapForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_inntektIFilialregnskap.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerInntektIFilialRegnskapForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }


    private val sumKostnadIFilialregnskapKalkyle = kalkyle("sumKostnadIFilialregnskap") {
        val sumPositive = summerKostnadIFilialRegnskapForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
        val sumNegative = summerKostnadIFilialRegnskapForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
        settUniktFelt(modell.resultatregnskap_sumKostnadIFilialregnskap) {
            sumPositive - sumNegative
        }
    }

    private fun GeneriskModellKontekst.summerKostnadIFilialRegnskapForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_kostnadIFilialregnskap.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerKostnadIFilialRegnskapForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private val sumResultatkomponentForIFRSForetak = kalkyle("sumResultatkomponentForIFRSForetak") {
        settUniktFelt(modell.resultatregnskap_sumResultatkomponentForIFRSForetak) {
            forekomsterAv(modell.resultatregnskap_resultatkomponentForIFRSForetak.resultatkomponent) summerVerdiFraHverForekomst {
                forekomstType.beloep + forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep
            }
        }
    }

    private val heravAndelResultatTekniskRegnskapLivsforsikringOgPensjonskasse =
        kalkyle("heravAndelResultatTekniskRegnskapLivsforsikringOgPensjonskasse") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                settUniktFelt(modell.resultatregnskap_heravAndelResultatTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    modell.resultatregnskap_driftsinntekt_heravAndelInntektTekniskRegnskapILivsforsikringOgPensjonskasseforetak -
                            modell.resultatregnskap_driftskostnad_heravAndelKostnadTekniskRegnskapILivsforsikringOgPensjonskasseforetak
                }
            }
    }

    private val heravAndelResultatIkkeTekniskRegnskapLivsforsikringOgPensjonskasse =
        kalkyle("heravAndelResultatIkkeTekniskRegnskapLivsforsikringOgPensjonskasse") {
            hvis(fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse()) {
                settUniktFelt(modell.resultatregnskap_heravAndelResultatIkkeTekniskRegnskapILivsforsikringOgPensjonskasseforetak) {
                    modell.resultatregnskap_driftsinntekt_heravAndelInntektIkkeTekniskRegnskapILivsforsikringOgPensjonskasseforetak -
                            modell.resultatregnskap_driftskostnad_heravAndelKostnadIkkeTekniskRegnskapILivsforsikringOgPensjonskasseforetak
                }
            }
        }

    internal val aarsresultatKalkyle = kalkyle("aarsresultat") {
        val skjoennsfastsattInntektEllerKostnad =
            forekomsterAv(modell.resultatregnskap_skjoennsfastsattInntektEllerKostnad.inntektEllerKostnad)
                .summerVerdiFraHverForekomst { forekomstType.beloep.tall() }

        val filialRegnskapSum = modell.resultatregnskap_sumInntektIFilialregnskap - modell.resultatregnskap_sumKostnadIFilialregnskap

        settUniktFelt(modell.resultatregnskap_aarsresultat) {
            filialRegnskapSum
                ?: (modell.resultatregnskap_driftsinntekt_sumDriftsinntekt -
                    modell.resultatregnskap_driftskostnad_sumDriftskostnad +
                    modell.resultatregnskap_sumFinansinntekt -
                    modell.resultatregnskap_sumFinanskostnad -
                    modell.resultatregnskap_sumSkattekostnad +
                    modell.resultatregnskap_sumResultatkomponentForIFRSForetak +
                    modell.resultatregnskap_sumBankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultatet +
                    modell.resultatregnskap_sumBankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultatet +
                    skjoennsfastsattInntektEllerKostnad)
        }
    }

    private val fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad =
        kalkyle("fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad") {
            if (erPetroleumsforetak()) {
                settUniktFelt(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                    modell.resultatregnskap_aarsresultat -
                        modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaLand -
                        modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepResultatAvFinansinntektOgFinanskostnadMv
                }

                settUniktFelt(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    modell.resultatregnskap_aarsresultat -
                        modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaLand -
                        modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepResultatAvFinansinntektOgFinanskostnadMv
                }
            }
    }

    private val fordeltAarsresultat =
        kalkyle("fordeltAarsresultat") {
            if (erPetroleumsforetak()) {
                val grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel =
                    if (inntektsaar.tekniskInntektsaar >= 2024) {
                        forekomsterAv(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel) summerVerdiFraHverForekomst {
                            forekomstType.omklassifisertInntekt - forekomstType.omklassifisertKostnad
                        }
                    } else {
                        forekomsterAv(modell2023.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel) summerVerdiFraHverForekomst {
                            forekomstType.omklassifisertInntektEllerKostnad.tall()
                        }
                    }

                val grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv =
                    if (inntektsaar.tekniskInntektsaar >= 2024) {
                        forekomsterAv(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv) summerVerdiFraHverForekomst {
                            forekomstType.omklassifisertInntekt - forekomstType.omklassifisertKostnad
                        }
                    } else {
                        forekomsterAv(modell2023.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv) summerVerdiFraHverForekomst {
                            forekomstType.omklassifisertInntektEllerKostnad.tall()
                        }
                    }

                forekomsterAv(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven) forHverForekomst {
                    settFelt(forekomstType.fordeltAarsresultat_beloepAlminneligInntektFraVirksomhetPaaSokkel) {
                        forekomstType.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaSokkel +
                            grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel -
                            grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv
                    }

                    settFelt(forekomstType.fordeltAarsresultat_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                        forekomstType.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel +
                            grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel -
                            grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv
                    }

                    settFelt(forekomstType.fordeltAarsresultat_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                        forekomstType.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepResultatAvFinansinntektOgFinanskostnadMv +
                            grunnlagOmklassifisertFraVirksomhetPaaSokkelTilFinansinntektOgFinanskostnadMv -
                            grunnlagOmklassifisertFraFinansinntektOgFinanskostnadMvTilVirksomhetPaaSokkel
                    }

                    settFelt(forekomstType.fordeltAarsresultat_beloepAlminneligInntektFraVirksomhetPaaLand) {
                        forekomstType.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaLand.tall()
                    }
                }
            }
        }

    private val kalkyleSamling = Kalkylesamling(
        aaretsAvskrivningKalkyle,
        annenDriftsinntektstypeInntektKalkyle,
        annenDriftsinntektstypeFradragKalkyle,
        tilbakefoertKostnadForPrivatBrukAvNaeringsbilKalkyle,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        inntektFraToemmerkontoKalkyle,
        inntektsfradragFraToemmerkontoKalkyle,
        andelAvDriftsresultatOverfoertTilToemmerkontoKalkyle,
        sumDriftsinntekterKalkyle,
        sumDriftsinntekterBankOgForsikringKalkyle,
        sumDriftsinntekterSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak,
        sumDriftsinntekterSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak,
        sumDriftskostnaderKalkyle,
        sumDriftskostnaderBankOgForsikringKalkyle,
        sumDriftskostnaderSomInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak,
        sumDriftskostnaderSomIkkeInngaarITekniskRegnskapLivsforsikringOgPensjonskasseforetak,
        sumFinansinntektKalkyle,
        sumFinanskostnadKalkyle,
        sumSkattekostnadKalkyle,
        sumBankOgForsikringsinntektEllerKostnadSomIkkeBlirOmklassifisertTilResultatKalkyle,
        sumBankOgForsikringsinntektEllerKostnadSomKanBliOmklassifisertTilResultatKalkyle,
        sumEndringIEgenkapitalBankOgForsikringKalkyle,
        sumInntektIFilialregnskapKalkyle,
        sumKostnadIFilialregnskapKalkyle,
        sumResultatkomponentForIFRSForetak,
        aarsresultatKalkyle,
        heravAndelResultatTekniskRegnskapLivsforsikringOgPensjonskasse,
        heravAndelResultatIkkeTekniskRegnskapLivsforsikringOgPensjonskasse,
        fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad,
        fordeltAarsresultat
    )
    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
