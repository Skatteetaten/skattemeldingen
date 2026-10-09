package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.FeltMedEgenskaper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v7_2026.v7
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.inntektIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.kontraktstypeForKraftLevertAvKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

/**
 * Fra og med 2026 flyttes kontrakter for vannkraftverk fra kraftverksnivå (kraftLevertIhtKontrakt, se
 * [SpesifikasjonAvGrunnrenteinntektFra2024]) til selskapsnivå (kontraktForVannkraftverk), etter samme prinsipp
 * som kontraktForLandbasertVindkraft for vindkraft.
 */
internal object KontraktForVannkraftverk : HarKalkylesamling {

    internal val salgsinntektFraFysiskAvtale =
        kalkyle("salgsinntektFraFysiskAvtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) forHverForekomst {
                    settFelt(forekomstType.salgsinntektFraFysiskAvtale) {
                        forekomstType.kontraktspris * forekomstType.volumIKWIInntektsaaret
                    }
                }
            }
        }

    /**
     * Fordeler et beløp fra en kontrakt på selskapsnivå til riktig kraftverk, basert på kontraktspartens
     * løpenummer og andelAvKontrakt. Samme mønster som
     * GrunnrenteinntektLandbasertVindkraft.summerBeloepPerAndelPerLoepenummer for vindkraft.
     */
    private fun ForekomstKontekst<v7.kontraktForVannkraftverkForekomst.spesifikasjonAvKontraktIVannkraftverkForekomst>.summerBeloepPerAndelPerLoepenummer(
        beloepsfelt: FeltMedEgenskaper<v7.kontraktForVannkraftverkForekomst.spesifikasjonAvKontraktIVannkraftverkForekomst>,
        nyeForekomster: MutableMap<String, BigDecimal>
    ) {
        val beloep = beloepsfelt.tall()
        if (beloep != null) {
            forekomsterAv(forekomstType.kontraktspart) forHverForekomst {
                val andel: BigDecimal =
                    forekomstType.andelAvKontrakt.prosent()
                        ?: BigDecimal.ZERO
                val loepenummer: String = forekomstType.loepenummer.verdi()
                    ?: throw IllegalArgumentException("Kontraktsparten har ikke loepenummer")

                val andelAvBeloep: BigDecimal =
                    ((nyeForekomster[loepenummer]
                        ?: BigDecimal.ZERO) + (beloep * andel)) ?: BigDecimal.ZERO

                nyeForekomster[loepenummer] = andelAvBeloep
            }
        }
    }

    private fun ForekomstKontekst<v7.kraftverk_spesifikasjonAvKraftverkForekomst>.opprettNyForekomstInntekt(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntekt_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVannkraftverk
            opprettNySubforekomstAv(forekomsttype) {
                medId(kodeverdi.kode)
                medFelt(
                    forekomsttype.type,
                    kodeverdi.kode
                )
                medFelt(
                    forekomsttype.beloep,
                    beloep
                )
            }
        }
    }

    internal val kontraktsinntektFordeltPerKraftverk =
        kalkyle("kontraktsinntektFordeltPerKraftverk") {
            val nyeForekomsterSalgsinntektFraLeieavtale = mutableMapOf<String, BigDecimal>()
            val nyeForekomsterSalgsinntektFraKjoepekontrakt = mutableMapOf<String, BigDecimal>()
            val nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt = mutableMapOf<String, BigDecimal>()

            forekomsterAv(modell.kontraktForVannkraftverk.spesifikasjonAvKontraktIVannkraftverk) forHverForekomst {
                if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale) {
                    summerBeloepPerAndelPerLoepenummer(
                        forekomstType.salgsinntektFraFysiskAvtale,
                        nyeForekomsterSalgsinntektFraLeieavtale
                    )
                }
                if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt) {
                    summerBeloepPerAndelPerLoepenummer(
                        forekomstType.salgsinntektFraFysiskAvtale,
                        nyeForekomsterSalgsinntektFraKjoepekontrakt
                    )
                }
                if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale) {
                    summerBeloepPerAndelPerLoepenummer(
                        forekomstType.salgsinntektFraFysiskAvtale,
                        nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt
                    )
                }
            }

            forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) der {
                samletPaastempletMerkeytelseIKvaOverGrense()
            } forHverForekomst {
                opprettNyForekomstInntekt(
                    inntektIGrunnrente.kode_salgsinntektFraLeieavtale,
                    nyeForekomsterSalgsinntektFraLeieavtale[forekomstType.loepenummer.verdi()]
                )
                opprettNyForekomstInntekt(
                    inntektIGrunnrente.kode_salgsinntektFraKjoepekontrakt,
                    nyeForekomsterSalgsinntektFraKjoepekontrakt[forekomstType.loepenummer.verdi()]
                )
                opprettNyForekomstInntekt(
                    inntektIGrunnrente.kode_salgsinntektFraLangsiktigFastpriskontrakt,
                    nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt[forekomstType.loepenummer.verdi()]
                )
            }
        }

    internal val samletSalgsinntektForLeieavtale =
        kalkyle("samletSalgsinntektForLeieavtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraLeieavtale) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_leieavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    internal val samletSalgsinntektForKjoepekontrakt =
        kalkyle("samletSalgsinntektForKjoepekontrakt") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraKjoepekontrakt) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontrakt
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    internal val samletSalgsinntektForFastprisavtale =
        kalkyle("samletSalgsinntektForFastprisavtale") {
            forekomsterAv(modell.kontraktForVannkraftverk) forHverForekomst {
                settFelt(forekomstType.samletSalgsinntektFraFastprisavtale) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVannkraftverk) der {
                        forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_fastprisavtale
                    } summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            salgsinntektFraFysiskAvtale,
            kontraktsinntektFordeltPerKraftverk,
            samletSalgsinntektForLeieavtale,
            samletSalgsinntektForKjoepekontrakt,
            samletSalgsinntektForFastprisavtale
        )
    }
}

