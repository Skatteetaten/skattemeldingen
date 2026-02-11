package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.vindkraft

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.maksAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.FeltMedEgenskaper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v6_2025.v6
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Satser
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.felt2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.GrunnlagIBeregningAvSelskapsskatt.erFradrag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.GrunnlagIBeregningAvSelskapsskatt.erTillegg
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.InntektOgFradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.InntektOgFradragIGrunnrente.harBehandlingsregelFradrag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.InntektOgFradragIGrunnrente.harBehandlingsregelTillegg
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.inntektIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.kontraktstypeForKraftLevertAvKraftverk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2024

/**
 * Spec: https://wiki.sits.no/display/SPAP/FR+-+Grunnrenteinntekt+Landbasert+vindkraft
 */
internal object GrunnrenteinntektLandbasertVindkraft : HarKalkylesamling {

    private val kontraktForLandbasertVindkraft =
        kalkyle("kontraktForLandbasertVindkraft") {
            val inntektsaar = inntektsaar

            forekomsterAv(modell.kontraktForLandbasertVindkraft) forHverForekomst {

                forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) forHverForekomst {
                    if (forekomstType.kontraktstype ulik kontraktstypeForKraftLevertAvKraftverk.kode_finansiellSikringsavtaleInngaattFoer28092022) {
                        val volum = if (inntektsaar.tekniskInntektsaar >= 2025) {
                            modell.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk.volumIKWhIInntektsaaret
                        } else {
                            felt2024.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk.volumIKWh
                        }

                        settFelt(forekomstType.salgsinntektFraFysiskAvtale) {
                            forekomstType.kontraktspris * volum
                        }
                    }
                }

                settFelt(forekomstType.samletSalgsinntektFraFysiskAvtale) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.salgsinntektFraFysiskAvtale.tall()
                    }
                }

                hvis(this@kalkyle.inntektsaar.tekniskInntektsaar >= 2025) {
                    settFelt(forekomstType.samletDekningskjoepForFysiskAvtale) {
                        forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                            forekomstType.dekningskjoep.tall()
                        }
                    }
                }
                settFelt(forekomstType.samletGevinstFraFoertidigTerminering) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.gevinstVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring.tall()
                    }
                }

                settFelt(forekomstType.samletTapFraFoertidigTerminering) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.tapVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring.tall()
                    }
                }

                settFelt(forekomstType.samletGevinstForKontrakt) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.gevinstFraAvtaleOmFinansiellSikring.tall()
                    }
                }

                settFelt(forekomstType.samletTapForKontrakt) {
                    forekomsterAv(forekomstType.spesifikasjonAvKontraktIVindkraftverk) summerVerdiFraHverForekomst {
                        forekomstType.tapFraAvtaleOmFinansiellSikring.tall()
                    }
                }

                settFelt(forekomstType.avkortetSamletGevinst) {
                    forekomstType.samletGevinstForKontrakt * forekomstType.avkortingsandel.prosent()
                }

                settFelt(forekomstType.avkortetSamletTap) {
                    forekomstType.samletTapForKontrakt * forekomstType.avkortingsandel.prosent()
                }

            }
        }

    private val kontraktsinformasjonPerVindkraftanlegg = kalkyle("kontraktsinformasjonPerVindkraftanlegg") {

        val samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022 = mutableMapOf<String, BigDecimal>()
        val samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030 = mutableMapOf<String, BigDecimal>()
        val samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022 = mutableMapOf<String, BigDecimal>()
        val samletVolumForKontraktstypeLangsiktigFastpriskontrakt = mutableMapOf<String, BigDecimal>()
        val samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattFoer28092022 = mutableMapOf<String, BigDecimal>()
        val samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030 = mutableMapOf<String, BigDecimal>()
        val samletVolumForDekningskjoepForKontraktstypeLangsiktigFastpriskontrakt = mutableMapOf<String, BigDecimal>()

        forekomsterAv(modell.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk) forHverForekomst {

            val volum = if (this@kalkyle.inntektsaar.tekniskInntektsaar >= 2025) {
                modell.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk.volumIKWhIInntektsaaret
            } else {
                modell2024.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk.volumIKWh
            }


            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerAntallPerAndelPerLoepenummer(
                    volum,
                    samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022
                )
            }

            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerAntallPerAndelPerLoepenummer(
                    volum,
                    samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030
                )
            }

            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_finansiellSikringsavtaleInngaattFoer28092022) {
                summerAntallPerAndelPerLoepenummer(
                    volum,
                    samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022
                )
            }

            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_langsiktigFastpriskontrakt) {
                summerAntallPerAndelPerLoepenummer(
                    volum,
                    samletVolumForKontraktstypeLangsiktigFastpriskontrakt
                )
            }

            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerAntallPerAndelPerLoepenummer(
                    forekomstType.volumDekningskjoep,
                    samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerAntallPerAndelPerLoepenummer(
                    forekomstType.volumDekningskjoep,
                    samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_langsiktigFastpriskontrakt) {
                summerAntallPerAndelPerLoepenummer(
                    forekomstType.volumDekningskjoep,
                    samletVolumForDekningskjoepForKontraktstypeLangsiktigFastpriskontrakt
                )
            }
        }

        forekomsterAv(modell2024.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            hvis(this@kalkyle.inntektsaar.tekniskInntektsaar == 2024) {
                val loepenummer = forekomstType.loepenummer.verdi()

                if (samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022.containsKey(loepenummer)) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftanlegg_samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022) {
                        samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030.containsKey(
                        loepenummer
                    )
                ) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftanlegg_samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                        samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022.containsKey(loepenummer)) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftanlegg_samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022) {
                        samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeLangsiktigFastpriskontrakt.containsKey(loepenummer)) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_oevrigTilVisningAvKontraktsinformasjonPerVindkraftanlegg_samletVolumForKontraktstypeLangsiktigFastpriskontrakt) {
                        samletVolumForKontraktstypeLangsiktigFastpriskontrakt[loepenummer]
                    }
                }
            }
        }

        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {

            hvis(this@kalkyle.inntektsaar.tekniskInntektsaar >= 2025) {
                val loepenummer = forekomstType.loepenummer.verdi()

                if (samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022.containsKey(loepenummer)) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022) {
                        samletVolumForKontraktstypeKjoepekontraktInngaattFoer28092022[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030.containsKey(
                        loepenummer
                    )
                ) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                        samletVolumForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022.containsKey(loepenummer)) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022) {
                        samletVolumForKontraktstypeFinansiellSikringsavtaleInngaattFoer28092022[loepenummer]
                    }
                }

                if (samletVolumForKontraktstypeLangsiktigFastpriskontrakt.containsKey(loepenummer)) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForKontraktstypeLangsiktigFastpriskontrakt) {
                        samletVolumForKontraktstypeLangsiktigFastpriskontrakt[loepenummer]
                    }
                }

                if (samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattFoer28092022.containsKey(
                        loepenummer
                    )
                ) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattFoer28092022) {
                        samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattFoer28092022[loepenummer]
                    }
                }

                if (samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030.containsKey(
                        loepenummer
                    )
                ) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                        samletVolumForDekningskjoepForKontraktstypeKjoepekontraktInngaattIPeriodenFra01012024Til31122030[loepenummer]
                    }
                }

                if (samletVolumForDekningskjoepForKontraktstypeLangsiktigFastpriskontrakt.containsKey(loepenummer)) {
                    settFelt(forekomstType.oevrigTilVisningAvKontraktsinformasjonPerVindkraftverk_samletVolumForDekningskjoepForKontraktstypeLangsiktigFastpriskontrakt) {
                        samletVolumForDekningskjoepForKontraktstypeLangsiktigFastpriskontrakt[loepenummer]
                    }
                }

            }
        }
    }

    private val lagNyForekomstSpesifikasjonAvInntekt = kalkyle("lagNyForekomstSpesifikasjonAvInntekt") {
        val nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt = mutableMapOf<String, BigDecimal>()
        val nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterGevinstVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterTapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030 =
            mutableMapOf<String, BigDecimal>()
        val nyeForekomsterdekningskjoepForKjoepekontraktMellomUavhengigeParterInngaattFoer28092022 =
            mutableMapOf<String, BigDecimal>()
        val dekningskjoepKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030 =
            mutableMapOf<String, BigDecimal>()
        val dekningskjoepForLangsiktigFastpriskontrakt =
            mutableMapOf<String, BigDecimal>()

        forekomsterAv(modell.kontraktForLandbasertVindkraft.spesifikasjonAvKontraktIVindkraftverk) forHverForekomst {
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.salgsinntektFraFysiskAvtale,
                    nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_langsiktigFastpriskontrakt) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.salgsinntektFraFysiskAvtale,
                    nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.salgsinntektFraFysiskAvtale,
                    nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.gevinstVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_finansiellSikringsavtaleInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.gevinstVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterGevinstVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.gevinstVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.tapVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_finansiellSikringsavtaleInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.tapVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterTapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.tapVedFoertidigTermineringMvAvAvtaleOmFinansiellSikring,
                    nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattFoer28092022) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.dekningskjoep,
                    nyeForekomsterdekningskjoepForKjoepekontraktMellomUavhengigeParterInngaattFoer28092022
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_kjoepekontraktInngaattIPeriodenFra01012024Til31122030) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.dekningskjoep,
                    dekningskjoepKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030
                )
            }
            if (forekomstType.kontraktstype lik kontraktstypeForKraftLevertAvKraftverk.kode_langsiktigFastpriskontrakt) {
                summerBeloepPerAndelPerLoepenummer(
                    forekomstType.dekningskjoep,
                    dekningskjoepForLangsiktigFastpriskontrakt
                )
            }
        }

        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_salgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattFoer28092022,
                nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_salgsinntektFraLangsiktigFastpriskontrakt,
                nyeForekomsterSalgsinntektFraLangsiktigFastpriskontrakt[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_salgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030,
                nyeForekomsterSalgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_bruttoSalgsinntektSpotmarkedspris,
                forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvBruttoSalgsinntektTilSpotmarkedspris_produksjon *
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvBruttoSalgsinntektTilSpotmarkedspris_spotmarkedspris
            )

            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022,
                nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022,
                nyeForekomsterGevinstVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030,
                nyeForekomsterGevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstFradrag(
                fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022,
                nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstFradrag(
                fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022,
                nyeForekomsterTapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstFradrag(
                fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030,
                nyeForekomsterTapVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_dekningskjoepForKjoepekontraktMellomUavhengigeParterInngaattFoer28092022,
                nyeForekomsterdekningskjoepForKjoepekontraktMellomUavhengigeParterInngaattFoer28092022[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_dekningskjoepKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030,
                dekningskjoepKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030[forekomstType.loepenummer.verdi()]
            )
            opprettNyForekomstInntekt(
                inntektIGrunnrente.kode_dekningskjoepForLangsiktigFastpriskontrakt,
                dekningskjoepForLangsiktigFastpriskontrakt[forekomstType.loepenummer.verdi()]
            )
        }
    }

    private fun ForekomstKontekst<v6.kontraktForLandbasertVindkraftForekomst.spesifikasjonAvKontraktIVindkraftverkForekomst>.summerBeloepPerAndelPerLoepenummer(
        beloepsfelt: FeltMedEgenskaper<v6.kontraktForLandbasertVindkraftForekomst.spesifikasjonAvKontraktIVindkraftverkForekomst>,
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

    private fun ForekomstKontekst<*>.summerAntallPerAndelPerLoepenummer(
        felt: Felt<*>,
        nyeForekomster: MutableMap<String, BigDecimal>
    ) {

        val antall = generiskModell.verdiFor(felt)?.toBigDecimal()
        if (antall != null) {
            forekomsterAv(v6.kontraktForLandbasertVindkraftForekomst.spesifikasjonAvKontraktIVindkraftverkForekomst.kontraktspart) forHverForekomst {
                val andel: BigDecimal =
                    forekomstType.andelAvKontrakt.prosent()
                        ?: BigDecimal.ZERO
                val loepenummer: String = forekomstType.loepenummer.verdi()
                    ?: throw IllegalArgumentException("Kontraktsparten har ikke loepenummer")

                val andelAvAntall: BigDecimal =
                    ((nyeForekomster[loepenummer]
                        ?: BigDecimal.ZERO) + (antall * andel)).somHeltall() ?: BigDecimal.ZERO

                nyeForekomster[loepenummer] = andelAvAntall
            }
        }
    }

    private fun ForekomstKontekst<v6.spesifikasjonAvEnhetIVindkraftverkForekomst>.opprettNyForekomstFradrag(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk
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

    private fun ForekomstKontekst<v6.spesifikasjonAvEnhetIVindkraftverkForekomst>.opprettNyForekomstInntekt(
        kodeverdi: KodeVerdi,
        beloep: BigDecimal?
    ) {
        if (beloep != null) {
            val forekomsttype =
                forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk
            opprettNySubforekomstAv(forekomsttype) {
                medId(kodeverdi.kode)
                medFelt(
                    forekomsttype.type,
                    kodeverdi.kode
                )
                medFelt(
                    forekomsttype.beloep,
                    beloep maksAntallDesimaler 2
                )
            }
        }
    }

    private val spesifikasjonAvEnhetIVindkraftverk =
        kalkyle("spesifikasjonAvEnhetIVindkraftverk") {
            val satser = satser!!
            val inntektsaar = inntektsaar
            if (inntektsaar.tekniskInntektsaar <= 2024) {
                spesifikasjonAvEnhetIVindkraftverk2024(inntektsaar, satser)
            } else {
                spesifikasjonAvEnhetIVindkraftverkFom2025(inntektsaar, satser)
            }

        }

    internal fun GeneriskModellKontekst.spesifikasjonAvEnhetIVindkraftverk2024(
        inntektsaar: Inntektsaar,
        satser: Satser
    ) {
        forekomsterAv(modell2024.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {

            val sumInntektIGrunnrente =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumInntektIGrunnrenteUnntattAndelFraSDF =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar) &&
                            forekomstType.type ulik inntektIGrunnrente.kode_andelAvPositivGrunnrenteinntektFraSelskapMedDeltakerfastsetting
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumFradragIGrunnrente =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrente(inntektsaar)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumFradragIGrunnrenteUnntattInvesteringskostnadOgVenterenteMv =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrente(inntektsaar) &&
                            !(forekomstType.type likEnAv listOf(
                                fradragIGrunnrente.kode_investeringskostnad,
                                fradragIGrunnrente.kode_venterente,
                                fradragIGrunnrente.kode_andelAvNegativGrunnrenteinntektFraSelskapMedDeltakerfastsetting
                            ))
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis(
                forekomstType.installertEffektIKwhIHenholdTilKonsesjon.stoerreEllerLik(1)
                        || forekomstType.antallTurbiner.stoerreEnn(5)
            ) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt) {
                    val inntekt = sumInntektIGrunnrenteUnntattAndelFraSDF.plus(
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVindkraft) summerVerdiFraHverForekomst {
                            if (forekomstType.type.verdi().erTillegg(inntektsaar)) {
                                forekomstType.beloep.tall()
                            } else {
                                BigDecimal.ZERO
                            }
                        }
                    )

                    val fradrag = sumFradragIGrunnrenteUnntattInvesteringskostnadOgVenterenteMv.plus(
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVindkraft) summerVerdiFraHverForekomst {
                            if (forekomstType.type.verdi().erFradrag(inntektsaar)) {
                                forekomstType.beloep.tall()
                            } else {
                                BigDecimal.ZERO
                            }
                        }
                    )

                    inntekt.minus(fradrag)
                }

                if (forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt
                        .stoerreEllerLik(0)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)
                    }
                } else {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        (forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).absoluttverdi()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .stoerreEnn(0)
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.harVerdi()
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt
                        .stoerreEllerLik(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .stoerreEnn(0)
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .mindreEnn(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                    }
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoerbarBeregnetNegativSelskapsskatt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt)
                        .plus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletInntektIBruttoGrunnrenteinntekt) {
                    sumInntektIGrunnrente
                }
            }

            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletFradragIBruttoGrunnrenteinntekt) {
                sumFradragIGrunnrente.plus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                    .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt)
            }

            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnrenteinntektFoerSamordning) {
                forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletInntektIBruttoGrunnrenteinntekt
                    .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletFradragIBruttoGrunnrenteinntekt)
            }
        }
    }

    internal fun GeneriskModellKontekst.spesifikasjonAvEnhetIVindkraftverkFom2025(
        inntektsaar: Inntektsaar,
        satser: Satser
    ) {
        forekomsterAv(modell.spesifikasjonAvEnhetIVindkraftverk) forHverForekomst {

            val sumInntektIGrunnrenteTillegg =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar)
                } summerVerdiFraHverForekomst {
                    if (forekomstType.type.verdi().harBehandlingsregelTillegg(inntektsaar)) {
                        forekomstType.beloep.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }
            val sumInntektIGrunnrenteFradrag =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar)
                } summerVerdiFraHverForekomst {
                    if (forekomstType.type.verdi().harBehandlingsregelFradrag(inntektsaar)) {
                        forekomstType.beloep.tall()
                    } else {
                        BigDecimal.ZERO
                    }
                }


            val sumInntektIGrunnrenteUnntattAndelFraSDF =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar) &&
                            forekomstType.type ulik inntektIGrunnrente.kode_andelAvPositivGrunnrenteinntektFraSelskapMedDeltakerfastsetting
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumInntektIGrunnrenteMedBehandlingsregelFradrag =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.inntekterIGrunnrente(inntektsaar) &&
                            forekomstType.type.verdi().harBehandlingsregelFradrag(inntektsaar)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumFradragIGrunnrente =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrente(inntektsaar)
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            val sumFradragIGrunnrenteUnntattInvesteringskostnadOgVenterenteMv =
                forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk) der {
                    forekomstType.type likEnAv InntektOgFradragIGrunnrente.fradragIGrunnrente(inntektsaar) &&
                            !(forekomstType.type likEnAv listOf(
                                fradragIGrunnrente.kode_investeringskostnad,
                                fradragIGrunnrente.kode_venterente,
                                fradragIGrunnrente.kode_andelAvNegativGrunnrenteinntektFraSelskapMedDeltakerfastsetting
                            ))
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }

            hvis(
                forekomstType.installertEffektIMwIHenholdTilKonsesjon.stoerreEllerLik(1)
                        || forekomstType.antallTurbiner.stoerreEnn(5)
            ) {
                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt) {
                    val inntekt = sumInntektIGrunnrenteUnntattAndelFraSDF.plus(
                        forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVindkraft) summerVerdiFraHverForekomst {
                            if (forekomstType.type.verdi().erTillegg(inntektsaar)) {
                                forekomstType.beloep.tall()
                            } else {
                                BigDecimal.ZERO
                            }
                        }
                    )

                    val fradrag = sumFradragIGrunnrenteUnntattInvesteringskostnadOgVenterenteMv.plus(
                        sumInntektIGrunnrenteMedBehandlingsregelFradrag.plus(
                            forekomsterAv(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvGrunnlagIBeregningAvSelskapsskattIVindkraft) summerVerdiFraHverForekomst {
                                if (forekomstType.type.verdi().erFradrag(inntektsaar)) {
                                    forekomstType.beloep.tall()
                                } else {
                                    BigDecimal.ZERO
                                }
                            }
                        )
                    )

                    inntekt.minus(fradrag)
                }

                if (forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt
                        .stoerreEllerLik(0)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)
                    }
                } else {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet) {
                        (forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt *
                                satser.sats(Sats.skattPaaAlminneligInntekt_sats)).absoluttverdi()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .stoerreEnn(0)
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.harVerdi()
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnlagForBeregningAvSelskapsskatt
                        .stoerreEllerLik(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet.tall()
                    }
                }

                hvis(
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .stoerreEnn(0)
                            && forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .mindreEnn(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                ) {
                    settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt) {
                        forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                    }
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoerbarBeregnetNegativSelskapsskatt) {
                    forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_fremfoertBeregnetNegativSelskapsskattFraTidligereAar
                        .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt)
                        .plus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeNegativeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                }

                settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletInntektIBruttoGrunnrenteinntekt) {
                    sumInntektIGrunnrenteTillegg - sumInntektIGrunnrenteFradrag
                }
            }

            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletFradragIBruttoGrunnrenteinntekt) {
                sumFradragIGrunnrente.plus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_aaretsBeregnedeSelskapsskattPaaGrunnrentepliktigVirksomhet)
                    .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_beregnetNegativSelskapsskattTilFremfoeringIVindkraftverk_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskatt)
            }

            settFelt(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_grunnrenteinntektFoerSamordning) {
                forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletInntektIBruttoGrunnrenteinntekt
                    .minus(forekomstType.spesifikasjonAvGrunnrenteinntektIVindkraftverk_samletFradragIBruttoGrunnrenteinntekt)
            }
        }
    }



    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            kontraktForLandbasertVindkraft,
            lagNyForekomstSpesifikasjonAvInntekt,
            spesifikasjonAvEnhetIVindkraftverk,
            kontraktsinformasjonPerVindkraftanlegg
        )
    }
}
