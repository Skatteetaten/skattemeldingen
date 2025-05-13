package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3

object KontrollerteTransaksjoner2023 : HarKalkylesamling {

    internal val samletGarantiansvarPerTransaksjonsmotpart = kalkyle("samletGarantiansvarPerTransaksjonsmotpart") {
        forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
            settFelt(forekomstType.samletGarantiansvarPerTransaksjonsmotpart) {
                forekomsterAv(forekomstType.mellomvaerende) der {
                    forekomstType.mellomvaerendetype likEnAv listOf(
                        mellomvaerendetype2023.kode_garantiansvarKnyttetTilFinansiellGaranti,
                        mellomvaerendetype2023.kode_garantiansvarKnyttetTilOperasjonellGaranti,
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }
    internal val samletGarantigrunnlagPerTransaksjonsmotpart = kalkyle("samletGarantigrunnlagPerTransaksjonsmotpart") {
        forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
            settFelt(forekomstType.samletGarantigrunnlagPerTransaksjonsmotpart) {
                forekomsterAv(forekomstType.mellomvaerende) der {
                    forekomstType.mellomvaerendetype likEnAv listOf(
                        mellomvaerendetype2023.kode_garantigrunnlagKnyttetTilKostnaderForFinansiellGaranti,
                        mellomvaerendetype2023.kode_garantigrunnlagKnyttetTilKostnaderForOperasjonellGaranti,
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }
    internal val samletFordringPerTransaksjonsmotpart = kalkyle("samletFordringPerTransaksjonsmotpart") {
        forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
            settFelt(forekomstType.samletFordringPerTransaksjonsmotpart) {
                forekomsterAv(forekomstType.mellomvaerende) der {
                    forekomstType.mellomvaerendetype likEnAv listOf(
                        mellomvaerendetype2023.kode_langsiktigFordring,
                        mellomvaerendetype2023.kode_langsiktigFordringTilForfallInnevaerendeAar,
                        mellomvaerendetype2023.kode_kortsiktigFordring,
                        mellomvaerendetype2023.kode_fordringIKonsernkontoordning,
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }
    internal val samletGjeldPerTransaksjonsmotpart = kalkyle("samletGjeldPerTransaksjonsmotpart") {
        forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
            settFelt(forekomstType.samletGjeldPerTransaksjonsmotpart) {
                forekomsterAv(forekomstType.mellomvaerende) der {
                    forekomstType.mellomvaerendetype likEnAv listOf(
                        mellomvaerendetype2023.kode_langsiktigGjeld,
                        mellomvaerendetype2023.kode_langsiktigGjeldTilforfallInnevaerendeAar,
                        mellomvaerendetype2023.kode_kortsiktigGjeld,
                        mellomvaerendetype2023.kode_gjeldIKonsernkontoordning,
                    )
                } summerVerdiFraHverForekomst {
                    forekomstType.beloep.tall()
                }
            }
        }
    }
    internal val samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoeringPerTransaksjonsmotpart =
        kalkyle("samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoeringPerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoeringPerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_salgOgOverfoeringAvImmateriellEiendel,
                            transaksjonstype2023.kode_salgOgOverfoeringAvVarigFysiskAnleggsmiddel,
                            transaksjonstype2023.kode_salgOgOverfoeringAvEierandelINaerstaaendeForetak,
                            transaksjonstype2023.kode_salgOgOverfoeringAvFinansieltAnleggsmiddel,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelsePerTransaksjonsmotpart =
        kalkyle("samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelsePerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelsePerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_kjoepOgOvertagelseAvImmateriellEiendel,
                            transaksjonstype2023.kode_kjoepOgOvertagelseAvVarigFysiskAnleggsmiddel,
                            transaksjonstype2023.kode_kjoepOgOvertagelseAvAnleggsmiddelINaerstaaendeForetak,
                            transaksjonstype2023.kode_kjoepOgOvertagelseAvFinansieltAnleggsmiddel,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletOperasjonellInntektPerTransaksjonsmotpart =
        kalkyle("samletOperasjonellInntektPerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletOperasjonellInntektPerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_inntektFraVaresalg,
                            transaksjonstype2023.kode_inntektFraKombinerteVareOgTjenestesalg,
                            transaksjonstype2023.kode_provisjonsinntektVedroerendeSalgOgDistribusjon,
                            transaksjonstype2023.kode_inntektFraOrdreproduksjon,
                            transaksjonstype2023.kode_inntektFraKontraktsforskning,
                            transaksjonstype2023.kode_leieOgLeasinginntekt,
                            transaksjonstype2023.kode_bareboatinntekt,
                            transaksjonstype2023.kode_inntektFraForsikringspremie,
                            transaksjonstype2023.kode_inntektVedroerendeImmateriellEiendel,
                            transaksjonstype2023.kode_mottattMarkedsstoette,
                            transaksjonstype2023.kode_inntektFraSentralisertTjenesteyting,
                            transaksjonstype2023.kode_inntektFraTekniskTjeneste,
                            transaksjonstype2023.kode_inntektFraItRelatertTjeneste,
                            transaksjonstype2023.kode_inntektFraKonserninternTjenesteForOevrig,
                            transaksjonstype2023.kode_inntektFraFellesProsjekt,
                            transaksjonstype2023.kode_mottattKostnadsdekningVedroerendeFellesAdministrativTjenesteMv,
                            transaksjonstype2023.kode_annenInntekt,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletOperasjonellKostnadPerTransaksjonsmotpart =
        kalkyle("samletOperasjonellKostnadPerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletOperasjonellKostnadPerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_kostnadTilVarekjoep,
                            transaksjonstype2023.kode_kostnadTilRaavarekjoep,
                            transaksjonstype2023.kode_kostnadTilKombinertVareOgTjenestesalg,
                            transaksjonstype2023.kode_provisjonskostnadVedroerendeSalgOgDistribusjon,
                            transaksjonstype2023.kode_kostnadTilOrdreproduksjon,
                            transaksjonstype2023.kode_kostnadTilKontraktsforskning,
                            transaksjonstype2023.kode_leieOgLeasingkostnad,
                            transaksjonstype2023.kode_bareboatkostnad,
                            transaksjonstype2023.kode_kostnadTilForsikringspremie,
                            transaksjonstype2023.kode_kostnadVedroerendeImmateriellEiendel,
                            transaksjonstype2023.kode_yttMarkedsstoette,
                            transaksjonstype2023.kode_kostnadTilSentralisertTjenesteyting,
                            transaksjonstype2023.kode_kostnadTilTekniskTjeneste,
                            transaksjonstype2023.kode_kostnadTilITRelatertTjeneste,
                            transaksjonstype2023.kode_kostnadTilKonserninternTjenesteForOevrig,
                            transaksjonstype2023.kode_kostnadTilFellesProsjekt,
                            transaksjonstype2023.kode_kostnadBelastetVedroerendeFellesAdministrativTjeneste,
                            transaksjonstype2023.kode_annenKostnad,
                            transaksjonstype2023.kode_kostnadTilReparasjonOgVedlikeholdKnyttetTilFastDriftssted,
                            transaksjonstype2023.kode_kostnadTilKonsulentEllerUnderleverandoerINorgeEllerPaaNorskSokkelKnyttetTilFastDriftssted,
                            transaksjonstype2023.kode_kostnadTilKonsulentEllerUnderleverandoerIUtlandetEllerPaaUtenlandsksokkelKnyttetTilFastDriftssted,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletFinansiellInntektPerTransaksjonsmotpart =
        kalkyle("samletFinansiellInntektPerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletFinansiellInntektPerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_renteinntektFraLangsiktigFordring,
                            transaksjonstype2023.kode_renteinntektFraKortsiktigFordring,
                            transaksjonstype2023.kode_renteinntektFraFordringIKonsernkontoordning,
                            transaksjonstype2023.kode_mottattTilskuddHerunderEttergittLaan,
                            transaksjonstype2023.kode_inntektFraGarantiprovisjonFraFinansiellGaranti,
                            transaksjonstype2023.kode_inntektFraGarantiprovisjonFraOperasjonellGaranti,
                            transaksjonstype2023.kode_inntektFraFactoring,
                            transaksjonstype2023.kode_annenFinansinntekt,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletFinansiellKostnadPerTransaksjonsmotpart =
        kalkyle("samletFinansiellKostnadPerTransaksjonsmotpart") {
            forAlleForekomsterAv(modellV3.transaksjonsmotpart) {
                settFelt(forekomstType.samletFinansiellKostnadPerTransaksjonsmotpart) {
                    forekomsterAv(forekomstType.transaksjonOgDisposisjon) der {
                        forekomstType.transaksjonstype likEnAv listOf(
                            transaksjonstype2023.kode_rentekostnadPaaLangsiktigGjeld,
                            transaksjonstype2023.kode_rentekostnadPaaKortsiktigGjeld,
                            transaksjonstype2023.kode_rentekostnadPaaGjeldIKonsernkontoordning,
                            transaksjonstype2023.kode_avgittTilskuddHerunderEttergittLaan,
                            transaksjonstype2023.kode_kostnadTilGarantiprovisjonKnyttetTilFinansiellGaranti,
                            transaksjonstype2023.kode_kostnadTilGarantiprovisjonKnyttetTilOperasjonellGaranti,
                            transaksjonstype2023.kode_kostnadsfoertTapPaaFordring,
                            transaksjonstype2023.kode_kostnadTilFactoring,
                            transaksjonstype2023.kode_annenFinanskostnad,
                        )
                    } summerVerdiFraHverForekomst {
                        forekomstType.beloep.tall()
                    }
                }
            }
        }
    internal val samletGarantiansvar = kalkyle("samletGarantiansvar") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletGarantiansvar) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletGarantiansvarPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletGarantigrunnlag = kalkyle("samletGarantigrunnlag") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletGarantigrunnlag) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletGarantigrunnlagPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoering =
        kalkyle("samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoering") {
            settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoering) {
                forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                    forekomstType.samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoeringPerTransaksjonsmotpart.tall()
                }
            }
        }
    internal val samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelse =
        kalkyle("samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelse") {
            settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelse) {
                forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                    forekomstType.samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelsePerTransaksjonsmotpart.tall()
                }
            }
        }
    internal val samletOperasjonellInntekt = kalkyle("samletOperasjonellInntekt") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletOperasjonellInntekt) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletOperasjonellInntektPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletOperasjonellKostnad = kalkyle("samletOperasjonellKostnad") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletOperasjonellKostnad) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletOperasjonellKostnadPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletFinansiellInntekt = kalkyle("samletFinansiellInntekt") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletFinansiellInntekt) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletFinansiellInntektPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletFinansiellKostnad = kalkyle("samletFinansiellKostnad") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletFinansiellKostnad) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletFinansiellKostnadPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletFordring = kalkyle("samletFordring") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletFordring) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletFordringPerTransaksjonsmotpart.tall()
            }
        }
    }
    internal val samletGjeld = kalkyle("samletGjeld") {
        settUniktFelt(modellV3.transaksjonDisposisjonOgMellomvaerende.samletGjeld) {
            forekomsterAv(modellV3.transaksjonsmotpart) summerVerdiFraHverForekomst {
                forekomstType.samletGjeldPerTransaksjonsmotpart.tall()
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletGarantiansvarPerTransaksjonsmotpart,
            samletGarantigrunnlagPerTransaksjonsmotpart,
            samletFordringPerTransaksjonsmotpart,
            samletGjeldPerTransaksjonsmotpart,
            samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoeringPerTransaksjonsmotpart,
            samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelsePerTransaksjonsmotpart,
            samletOperasjonellInntektPerTransaksjonsmotpart,
            samletOperasjonellKostnadPerTransaksjonsmotpart,
            samletFinansiellInntektPerTransaksjonsmotpart,
            samletFinansiellKostnadPerTransaksjonsmotpart,
            samletGarantiansvar,
            samletGarantigrunnlag,
            samletEkstraordinaerTransaksjonKnyttetTilSalgEllerOverfoering,
            samletEkstraordinaerTransaksjonKnyttetTilKjoepEllerOvertagelse,
            samletOperasjonellInntekt,
            samletOperasjonellKostnad,
            samletFinansiellInntekt,
            samletFinansiellKostnad,
            samletFordring,
            samletGjeld,
        )
    }
}



