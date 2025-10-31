package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.rederi

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil.skalBeregneRederi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object ValutatapValutagevinstOgRentefradrag : HarKalkylesamling {
    private val andelFinansaktivaFraUnderliggendeSelskapVedUtgangenAvInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.andelFinansaktivaFraUnderliggendeSelskapVedUtgangenAvInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.andelAksjeIRederibeskattetSelskap) summerVerdiFraHverForekomst {
                    (forekomstType.bokfoertUtgaaendeVerdi * forekomstType.andelFinanskapital.prosent()).somHeltall()
                }
            }
        }
    }

    private val andelFinansaktivaFraUnderliggendeSelskapVedInngangenTilInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.andelFinansaktivaFraUnderliggendeSelskapVedInngangenTilInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.andelAksjeIRederibeskattetSelskap) summerVerdiFraHverForekomst {
                    (forekomstType.bokfoertInngaaendeVerdi * forekomstType.andelFinanskapital.prosent()).somHeltall()
                }
            }
        }
    }

    private val samletFinansaktivaISelskapetVedInngangenTilInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletFinansaktivaISelskapetVedInngangenTilInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    (forekomstType.andelFinansaktivaFraUnderliggendeSelskapVedInngangenTilInntektsaaret +
                        forekomstType.andreFinansaktivaFraEgetSelskapVedInngangenTilInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val samletFinansaktivaISelskapetVedUtgangenAvInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletFinansaktivaISelskapetVedUtgangenAvInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    (forekomstType.andelFinansaktivaFraUnderliggendeSelskapVedUtgangenAvInntektsaaret +
                        forekomstType.andreFinansaktivaFraEgetSelskapVedUtgangenAvInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            hvis(
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedInngangenTilInntektsaaret.harVerdi() ||
                    modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedUtgangenAvInntektsaaret.harVerdi()
            ) {
                val gjennomsnittFinansaktivaIBeloep =
                    forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        (forekomstType.samletFinansaktivaISelskapetVedInngangenTilInntektsaaret +
                            forekomstType.samletFinansaktivaISelskapetVedUtgangenAvInntektsaaret) / 2
                    }

                val gjennomsnittTotalKapital =
                    forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        (forekomstType.samletEiendelVedInngangenTilInntektsaaret +
                            forekomstType.samletEiendelVedUtgangenAvInntektsaaret +
                            forekomstType.korrigeringAvSamletEiendelVedUtgangenAvInntektsaaret) / 2
                    }

                val gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital =
                    (gjennomsnittFinansaktivaIBeloep / gjennomsnittTotalKapital) * 100
                settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital) {
                    gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital medAntallDesimaler 10 medMaksimumsverdi 100
                }
            }
        }
    }

    private val fradragsberettigetRentekostnad = kalkyle {
        hvis(
            skalBeregneRederi(RederiUtil.beskatningsordning.verdi()) &&
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.harVerdi()
        ) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.fradragsberettigetRentekostnad) {
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    ((forekomstType.faktiskRentekostnad *
                        forekomstType.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital) / 100).somHeltall()
                }
            }
        }
    }

    private val samletSkattepliktigValutagevinstEllerTap = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            var nettoKortsiktigValutagevinst = BigDecimal.ZERO
            var nettoKortsiktigValutatap = BigDecimal.ZERO
            var nettoRealisertLangsiktigValutagevinstEtablertFom2005 = BigDecimal.ZERO
            var nettoRealisertLangsiktigValutatapEtablertFom2005 = BigDecimal.ZERO
            hvis(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.harVerdi()) {
                nettoKortsiktigValutagevinst =
                    (forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        forekomstType.kortsiktigValutagevinst *
                            forekomstType.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital / 100
                    }) ?: BigDecimal.ZERO
                nettoKortsiktigValutatap =
                    (forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        forekomstType.kortsiktigValutatap *
                            forekomstType.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital / 100
                    }) ?: BigDecimal.ZERO
                nettoRealisertLangsiktigValutagevinstEtablertFom2005 =
                    (forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        forekomstType.realisertLangsiktigValutagevinstEtablertFom2005 *
                            forekomstType.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital / 100
                    }) ?: BigDecimal.ZERO
                nettoRealisertLangsiktigValutatapEtablertFom2005 =
                    (forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                        forekomstType.realisertLangsiktigValutatapEtablertFom2005 *
                            forekomstType.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital / 100
                    }) ?: BigDecimal.ZERO
            }
            val samletSkattepliktigValutagevinstEllerTap =
                forekomsterAv(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag) summerVerdiFraHverForekomst {
                    nettoKortsiktigValutagevinst -
                        nettoKortsiktigValutatap +
                        forekomstType.realisertLangsiktigValutagevinstEtablertFoer2005 -
                        forekomstType.realisertLangsiktigValutatapEtablertFoer2005 +
                        nettoRealisertLangsiktigValutagevinstEtablertFom2005 -
                        nettoRealisertLangsiktigValutatapEtablertFom2005 +
                        forekomstType.positivOmvurderingskontoVedInngangenTilInntektsaaret -
                        forekomstType.negativOmvurderingskontoVedInngangenTilInntektsaaret -
                        forekomstType.positivOmvurderingskontoVedUtgangenAvInntektsaaret +
                        forekomstType.negativOmvurderingskontoVedUtgangenAvInntektsaaret
                }

            hvis(samletSkattepliktigValutagevinstEllerTap stoerreEllerLik 0) {
                settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletSkattepliktigValutagevinst) {
                    samletSkattepliktigValutagevinstEllerTap.somHeltall()
                }
            }
            hvis(samletSkattepliktigValutagevinstEllerTap mindreEnn 0) {
                settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletFradragsberettigetValutatap) {
                    samletSkattepliktigValutagevinstEllerTap.somHeltall().absoluttverdi()
                }
            }
        }
    }

    val skattepliktigKortsiktigValutagevinst = kalkyle {
        settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.skattepliktigKortsiktigValutagevinst) {
            (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.kortsiktigValutagevinst *
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.prosent()).somHeltall()
        }
    }

    val fradragsberettigetKortsiktigValutatap = kalkyle {
        settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.fradragsberettigetKortsiktigValutatap) {
            (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.kortsiktigValutatap *
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.prosent()).somHeltall()
        }
    }

    val skattepliktigRealisertLangsiktigValutagevinstEtablertFom2005 = kalkyle {
        settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.skattepliktigRealisertLangsiktigValutagevinstEtablertFom2005) {
            (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.realisertLangsiktigValutagevinstEtablertFom2005 *
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.prosent()).somHeltall()
        }
    }

    val fradragsberettigetRealisertLangsiktigValutatapEtablertFom2005 = kalkyle {
        settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.fradragsberettigetRealisertLangsiktigValutatapEtablertFom2005) {
            (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.realisertLangsiktigValutatapEtablertFom2005 *
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital.prosent()).somHeltall()
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            andelFinansaktivaFraUnderliggendeSelskapVedUtgangenAvInntektsaaret,
            andelFinansaktivaFraUnderliggendeSelskapVedInngangenTilInntektsaaret,
            samletFinansaktivaISelskapetVedInngangenTilInntektsaaret,
            samletFinansaktivaISelskapetVedUtgangenAvInntektsaaret,
            gjennomsnittligFinansaktivaIProsentAvGjennomsnittligTotalkapital,
            fradragsberettigetRentekostnad,
            samletSkattepliktigValutagevinstEllerTap,
            skattepliktigKortsiktigValutagevinst,
            fradragsberettigetKortsiktigValutatap,
            skattepliktigRealisertLangsiktigValutagevinstEtablertFom2005,
            fradragsberettigetRealisertLangsiktigValutatapEtablertFom2005
        )
    }
}
