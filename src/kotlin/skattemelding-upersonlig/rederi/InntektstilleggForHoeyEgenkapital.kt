package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.rederi

import java.math.BigDecimal
import java.time.Year
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil.skalBeregneRederi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellNaering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

object InntektstilleggForHoeyEgenkapital : HarKalkylesamling {

    private val sumBalanseverdiForEiendel = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedUtgangenAvInntektsaaret) {
                modellNaering.balanseregnskap_sumBalanseverdiForEiendel.tall().somHeltall()
            }
        }
    }

    private val samletKorrigertEiendelVedInngangenTilInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedInngangenTilInntektsaaret) {
                (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedInngangenTilInntektsaaret -
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.fradragForBokfoertVerdiAvAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenOgAksjeIRederibeskattetAksjeselskapVedInngangenTilInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenVedInngangenTilInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelGjeldForAndelISdfEllerAksjeINokusBokfoertEtterEgenkapitalmetodenVedInngangenTilInntektsaaret).somHeltall()
            }
        }
    }

    private val samletKorrigertEiendelVedUtgangenAvInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedUtgangenAvInntektsaaret) {
                (modell.rederiskatteordning_valutatapValutagevinstOgRentefradrag.samletEiendelVedUtgangenAvInntektsaaret -
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.fradragForBokfoertVerdiAvAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenOgAksjeIRederibeskattetAksjeselskapVedUtgangenAvInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostmetodenVedUtgangenAvInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelGjeldForAndelISdfEllerAksjeINokusBokfoertEtterEgenkapitalmetodenVedUtgangenAvInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.annenKorrigeringAvSamletEiendelVedUtgangenAvInntektsaaret).somHeltall()
            }
        }
    }

    private val gjennomsnittligSamletKorrigertEgenandel = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertEgenandel) {
                ((modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedInngangenTilInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertEiendelVedUtgangenAvInntektsaaret) / 2).somHeltall()
            }
        }
    }

    private val samletKorrigertGjeldVedInngangenTilInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedInngangenTilInntektsaaret) {
                (modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.egenGjeldVedInngangenTilInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostEllerEgenkapitalmetodenVedInngangenTilInntektsaaret).somHeltall()
            }
        }
    }

    private val samletKorrigertGjeldVedUtgangenAvInntektsaaret = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedUtgangenAvInntektsaaret) {
                (modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.egenGjeldVedUtgangenAvInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.tilleggForAndelISdfEllerAksjeINokusBokfoertEtterKostEllerEgenkapitalmetodenVedUtgangenAvInntektsaaret).somHeltall()
            }
        }
    }

    private val gjennomsnittligSamletKorrigertGjeld = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertGjeld) {
                ((modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedInngangenTilInntektsaaret +
                    modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.samletKorrigertGjeldVedUtgangenAvInntektsaaret) / 2).somHeltall()
            }
        }
    }

    private fun finnAntallDagerIAar(aar: Int): BigDecimal = Year.of(aar).length().toBigDecimal()

    private val inntektstillegg = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val gjennomsnittligSamletKorrigertEgenandel =
                modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertEgenandel.tall()
            val gjennomsnittligSamletKorrigertGjeld =
                modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.gjennomsnittligSamletKorrigertGjeld.tall()
            var inntektstillegg =
                (gjennomsnittligSamletKorrigertEgenandel * BigDecimal(0.3)) - gjennomsnittligSamletKorrigertGjeld
            hvis(inntektstillegg stoerreEnn 0) {
                inntektstillegg *= satser!!.sats(Sats.normrente_rederi)
                if (modell.rederiskatteordning_spesifikasjonAvSkattleggingEtterSaerskiltBeskatningsordning.antallDagerIOrdningenIInntektsaaret.harVerdi()
                    || modell.rederiskatteordning_spesifikasjonAvSkattleggingEtterSaerskiltBeskatningsordning.antallDagerIOrdningenIInntektsaaret stoerreEnn 0
                ) {
                    val antallDagerIOrdningenIInntektsaaret =
                        modell.rederiskatteordning_spesifikasjonAvSkattleggingEtterSaerskiltBeskatningsordning.antallDagerIOrdningenIInntektsaaret / finnAntallDagerIAar(
                            inntektsaar.gjeldendeInntektsaar
                        )
                    inntektstillegg *= antallDagerIOrdningenIInntektsaaret
                }
                settUniktFelt(modell.rederiskatteordning_inntektstilleggForHoeyEgenkapital.inntektstillegg) {
                    inntektstillegg.somHeltall()
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            sumBalanseverdiForEiendel,
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