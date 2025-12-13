package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object InntektstilleggForHoeyEgenkapital : HarKalkylesamling {

    private val samletKorrigertEiendelVedInngangenTilInntektsaaret = kalkyle {
        hvis(inntektsaar.tekniskInntektsaar <= 2024 && RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val samletEiendelVedInngangenTilInntektsaaret =
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedInngangenTilInntektsaaret.tall()
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedInngangenTilInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    (samletEiendelVedInngangenTilInntektsaaret -
                        forekomstType.fradragForBokfoertVerdiAvAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenOgAksjeIRederibeskattetAksjeselskapVedInngangenTilInntektsaaret +
                        forekomstType.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenVedInngangenTilInntektsaaret +
                        forekomstType.tilleggForAndelGjeldForAndelISdfEllerAksjeINokusBokfoertEtterEgenkapitalmetodenVedInngangenTilInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val samletKorrigertEiendelVedUtgangenAvInntektsaaret = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val samletEiendelVedUtgangenAvInntektsaaret =
                modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedUtgangenAvInntektsaaret.tall()
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedUtgangenAvInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    (samletEiendelVedUtgangenAvInntektsaaret -
                        forekomstType.fradragForBokfoertVerdiAvAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenOgAksjeIRederibeskattetAksjeselskapVedUtgangenAvInntektsaaret +
                        forekomstType.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenVedUtgangenAvInntektsaaret +
                        forekomstType.tilleggForAndelGjeldForAndelISdfEllerAksjeINokusBokfoertEtterEgenkapitalmetodenVedUtgangenAvInntektsaaret +
                        modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.annenKorrigeringAvSamletEiendelVedUtgangenAvInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val gjennomsnittligSamletKorrigertEgenandel = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertEgenandel) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    ((forekomstType.samletKorrigertEiendelVedInngangenTilInntektsaaret +
                        forekomstType.samletKorrigertEiendelVedUtgangenAvInntektsaaret) / 2).somHeltall()
                }
            }
        }
    }

    private val samletKorrigertGjeldVedInngangenTilInntektsaaret = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedInngangenTilInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    (forekomstType.egenGjeldVedInngangenTilInntektsaaret +
                        forekomstType.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostEllerEgenkapitalmetodenVedInngangenTilInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val samletKorrigertGjeldVedUtgangenAvInntektsaaret = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedUtgangenAvInntektsaaret) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    (forekomstType.egenGjeldVedUtgangenAvInntektsaaret +
                        forekomstType.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostEllerEgenkapitalmetodenVedUtgangenAvInntektsaaret).somHeltall()
                }
            }
        }
    }

    private val gjennomsnittligSamletKorrigertGjeld = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertGjeld) {
                forekomsterAv(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital) summerVerdiFraHverForekomst {
                    ((forekomstType.samletKorrigertGjeldVedInngangenTilInntektsaaret +
                        forekomstType.samletKorrigertGjeldVedUtgangenAvInntektsaaret) / 2).somHeltall()
                }
            }
        }
    }

    private val inntektstillegg = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val gjennomsnittligSamletKorrigertEgenandel =
                modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertEgenandel.tall()
            val gjennomsnittligSamletKorrigertGjeld =
                modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertGjeld.tall()
            var inntektstillegg =
                (gjennomsnittligSamletKorrigertEgenandel * BigDecimal(0.3)) - gjennomsnittligSamletKorrigertGjeld
            hvis(inntektstillegg stoerreEnn 0) {
                inntektstillegg *= satser!!.sats(Sats.normrente_rederi)
                settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.inntektstillegg) {
                    inntektstillegg.somHeltall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletKorrigertEiendelVedInngangenTilInntektsaaret,
            samletKorrigertEiendelVedUtgangenAvInntektsaaret,
            gjennomsnittligSamletKorrigertEgenandel,
            samletKorrigertGjeldVedInngangenTilInntektsaaret,
            samletKorrigertGjeldVedUtgangenAvInntektsaaret,
            gjennomsnittligSamletKorrigertGjeld,
            inntektstillegg
        )
    }
}