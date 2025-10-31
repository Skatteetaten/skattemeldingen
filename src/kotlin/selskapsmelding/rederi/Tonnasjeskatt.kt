package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.rederi

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.rundAvOppTilNaermesteHundre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.rundAvTilNaermesteHundre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.rundAvTilNaermesteTusen
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.skipsstatusKnyttetTilRederiskatteordning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object Tonnasjeskatt : HarKalkylesamling {

    private val tonnasjeskattKalkyle = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            val satser = satser!!
            forAlleForekomsterAv(modell.rederiskatteordning_tonnasjeskatt.tonnasjeskattPerSkip) {
                val beregningFremTil100 =
                    forekomstType.nettotonnasje.tall().medMaksimumsverdi(100).rundAvOppTilNaermesteHundre() *
                        forekomstType.antallDager *
                        satser.sats(Sats.rederi_tonnasjeskattNivaaEn) / 100

                val beregningFra100Til1000 =
                    if (forekomstType.nettotonnasje stoerreEnn 100) {
                        (forekomstType.nettotonnasje.tall()
                            .medMaksimumsverdi(1000) - 100).rundAvTilNaermesteHundre() *
                            forekomstType.antallDager *
                            satser.sats(Sats.rederi_tonnasjeskattNivaaEn) / 100
                    } else null

                val beregningFra1000Til10000 =
                    if (forekomstType.nettotonnasje stoerreEnn 1000) {
                        (forekomstType.nettotonnasje.tall()
                            .medMaksimumsverdi(10000) - 1000).rundAvTilNaermesteTusen() *
                            forekomstType.antallDager *
                            satser.sats(Sats.rederi_tonnasjeskattNivaaTo) / 1000
                    } else null

                val beregningFra10000Til25000 =
                    if (forekomstType.nettotonnasje stoerreEnn 10000) {
                        (forekomstType.nettotonnasje.tall()
                            .medMaksimumsverdi(25000) - 10000).rundAvTilNaermesteTusen() *
                            forekomstType.antallDager *
                            satser.sats(Sats.rederi_tonnasjeskattNivaaTre) / 1000
                    } else null

                val beregningOver25000 =
                    if (forekomstType.nettotonnasje stoerreEnn 25000) {
                        (forekomstType.nettotonnasje - 25000)!!.rundAvTilNaermesteTusen() *
                            forekomstType.antallDager *
                            satser.sats(Sats.rederi_tonnasjeskattNivaaFire) / 1000
                    } else null

                val maxTonnasjeskattAndel = (beregningFremTil100 +
                    beregningFra100Til1000 +
                    beregningFra1000Til10000 +
                    beregningFra10000Til25000 +
                    beregningOver25000) * 25 / 100
                var miljoereduksjon = forekomstType.miljoereduksjon.tall()
                if (forekomstType.miljoereduksjon.harVerdi()
                    && forekomstType.miljoereduksjon.tall()!! > maxTonnasjeskattAndel
                ) {
                    miljoereduksjon = maxTonnasjeskattAndel
                }
                settFelt(forekomstType.tonnasjeskatt) {
                    (beregningFremTil100 +
                        beregningFra100Til1000 +
                        beregningFra1000Til10000 +
                        beregningFra10000Til25000 +
                        beregningOver25000 - miljoereduksjon) medAntallDesimaler 0
                }
            }
        }
    }

    private val samletTonnasjeskattIEgetSelskap = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.samletTonnasjeskattIEgetSelskap) {
                forekomsterAv(modell.rederiskatteordning_tonnasjeskatt.tonnasjeskattPerSkip) summerVerdiFraHverForekomst {
                    forekomstType.tonnasjeskatt.tall()
                }
            }
        }
    }

    internal val andelTonnasjeskattFraSdfEllerNokus = kalkyle("andelTonnasjeskattFraSdfEllerNokus") {
        settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.andelTonnasjeskattFraSdf) {
            forekomsterAv(modell.deltakersAndelAvFormueOgInntekt) summerVerdiFraHverForekomst {
                forekomstType.andelAvTonnasjeskatt.tall()
            }
        }
    }

    private val samletTonnasjeskatt = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.samletTonnasjeskatt) {
                modell.rederiskatteordning_tonnasjeskatt.samletTonnasjeskattIEgetSelskap +
                    modell.rederiskatteordning_tonnasjeskatt.andelTonnasjeskattFraSdf
            }
        }
    }

    val nettotonnasjeEideSkip = kalkyle {
        settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.nettotonnasjeEideSkip) {
            forekomsterAv(modell.rederiskatteordning_tonnasjeskatt.tonnasjeskattPerSkip) der {
                forekomstType.skipsstatus lik skipsstatusKnyttetTilRederiskatteordning.kode_skipSomErEidOgSeilerUnderEoesflaggVedUtgangenAvInntektsaaret ||
                    forekomstType.skipsstatus lik skipsstatusKnyttetTilRederiskatteordning.kode_skipSomErEidOgIkkeSeilerUnderEoesflaggVedUtgangenAvInntektsaaret
            } summerVerdiFraHverForekomst {
                forekomstType.nettotonnasje.tall()
            }
        }
    }

    val heravAndelEoestonnasje = kalkyle {
        settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.heravAndelEoestonnasje) {
            forekomsterAv(modell.rederiskatteordning_tonnasjeskatt.tonnasjeskattPerSkip) der {
                forekomstType.skipsstatus lik skipsstatusKnyttetTilRederiskatteordning.kode_skipSomErEidOgSeilerUnderEoesflaggVedUtgangenAvInntektsaaret
            } summerVerdiFraHverForekomst {
                forekomstType.nettotonnasje.tall()
            }
        }
    }

    private val andelEoesregistrertTonnasjeForAksjeselskap = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            hvis(
                modell.rederiskatteordning_tonnasjeskatt.nettotonnasjeEideSkip.harVerdi() &&
                    modell.rederiskatteordning_tonnasjeskatt.nettotonnasjeEideSkip stoerreEnn 0
            ) {
                settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.andelEoesregistrertTonnasjeForAksjeselskap) {
                    (modell.rederiskatteordning_tonnasjeskatt.heravAndelEoestonnasje
                        / modell.rederiskatteordning_tonnasjeskatt.nettotonnasjeEideSkip) * 100
                }
            }
        }
    }

    private val andelNettotonnasjeForRederibeskattetDeltaker = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.andelNettotonnasjeForRederibeskattetDeltaker) {
                (modell.rederiskatteordning_tonnasjeskatt.nettotonnasjeEideSkip *
                    modell.rederiskatteordning_tonnasjeskatt.prosentandelEidAvRederibeskattetDeltaker.prosent()
                    ).somHeltall()
            }
        }
    }

    private val andelEoestonnasjeForRederibeskattetDeltaker = kalkyle {
        hvis(RederiUtil.skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_tonnasjeskatt.andelEoestonnasjeForRederibeskattetDeltaker) {
                (modell.rederiskatteordning_tonnasjeskatt.heravAndelEoestonnasje *
                    modell.rederiskatteordning_tonnasjeskatt.prosentandelEidAvRederibeskattetDeltaker.prosent()
                    ).somHeltall()
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            nettotonnasjeEideSkip,
            andelTonnasjeskattFraSdfEllerNokus,
            heravAndelEoestonnasje,
            tonnasjeskattKalkyle,
            samletTonnasjeskattIEgetSelskap,
            samletTonnasjeskatt,
            andelEoesregistrertTonnasjeForAksjeselskap,
            andelNettotonnasjeForRederibeskattetDeltaker,
            andelEoestonnasjeForRederibeskattetDeltaker
        )
    }
}