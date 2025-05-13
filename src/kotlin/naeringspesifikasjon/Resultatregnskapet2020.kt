package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftsinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftskostnad
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020

/**
 *  https://wiki.sits.no/display/SIR/Kalkyler+resultatregnskap+for+regnskapspliktstype+1+og+5
 *
 *  inntektFraGevinstOgTapskonto er  et oppsummert felt fra Forekomster under GevinstOgTapskonto. Denne verdien skal inn som
 *  annenDriftsinntekt med type/konto 3890. Innslaget som er en forekomst av annenDriftsinntekt
 *  med annenDriftsinntektstype (3890) er således en sum
 *  av denne typen. Det skal ikke være mer enn en sum per type i denne listen.
 */
internal object Resultatregnskapet2020 : HarKalkylesamling {

    val aaretsAvskrivningKalkyle = kalkyle("aaretsAvskrivning") {
        hvis(regnskapspliktstype1Eller5()) {
            val aaretsAvskrivningForSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel =
                forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            opprettNyForekomstForAnnenDriftskostnad(
                aaretsAvskrivningForSaldoavskrevetAnleggsmiddel + aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel,
                annenDriftskostnad.kode_6000
            )
        }
    }

    val annenDriftsinntektstypeInntektKalkyle = kalkyle("annenDriftsinntektstypeInntekt") {
        opprettNyForekomstForAnnenDriftsinntekt(
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                forekomstType.inntektFraGevinstOgTapskonto.tall()
            },
            annenDriftsinntekt.kode_3890
        )
    }

    val aaretsInntektsfoeringAvNegativSaldoKalkyle = kalkyle("aaretsInntektsfoeringAvNegativSaldo") {
        val vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
            }
        val vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
            }
        val vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
            }
        val aaretsInntektsfoeringAvNegativSaldo =
            forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
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

    val annenDriftsinntektstypeFradragKalkyle = kalkyle("annenDriftsinntektstypeFradrag") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_gevinstOgTapskonto) der {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto stoerreEnn 0
                } summerVerdiFraHverForekomst {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto.tall()
                },
                annenDriftskostnad.kode_7890
            )
        }
    }

    val tilbakefoertKostnadForPrivatBrukAvNaeringsbilKalkyle =
        kalkyle("tilbakefoertKostnadForPrivatBrukAvNaeringsbil") {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell2020.spesifikasjonAvResultatregnskapOgBalanse_transportmiddelINaering) der {
                    forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil stoerreEllerLik 0
                } summerVerdiFraHverForekomst {
                    forekomstType.tilbakefoertBilkostnadForPrivatBrukAvYrkesbil * -1
                },
                annenDriftskostnad.kode_7099
            )
        }

    private fun GeneriskModellKontekst.opprettNyForekomstForAnnenDriftsinntekt(
        beloep: BigDecimal?,
        kode: KodeVerdi
    ) {
        hvis(beloep.harVerdi()) {
            opprettNyForekomstAv(modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt) {
                medId(kode.kode)
                medFelt(
                    modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt.type,
                    kode.kode
                )
                medFelt(
                    modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt.beloep,
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
            opprettNyForekomstAv(modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad) {
                medId(kode.kode)
                medFelt(
                    modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad.type,
                    kode.kode
                )
                medFelt(
                    modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad.beloep,
                    beloep
                )
            }
        }
    }

    val sumDriftsinntekterKalkyle = kalkyle("sumDriftsinntekter") {
        val salgsinntekter = forekomsterAv(modell2020.resultatregnskap_driftsinntekt_salgsinntekter.salgsinntekt) summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

        val annenDriftsinntekt = forekomsterAv(modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt) summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }
        settUniktFelt(modell2020.resultatregnskap_driftsinntekt_sumDriftsinntekt) {
            salgsinntekter + annenDriftsinntekt
        }
    }

    val sumDriftskostnaderKalkyle = kalkyle("sumDriftskostnader") {
        val sumVarekostnader =
            forekomsterAv(modell2020.resultatregnskap_driftskostnad_varekostnader.varekostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }

        val sumLoennskostnader =
            forekomsterAv(modell2020.resultatregnskap_driftskostnad_loennskostnader.loennskostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }

        val sumAndreDriftskostnader =
            forekomsterAv(modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }

        settUniktFelt(modell2020.resultatregnskap_driftskostnad_sumDriftskostnad) {
            sumVarekostnader + sumLoennskostnader + sumAndreDriftskostnader
        }
    }

    val sumFinansinntektKalkyle = kalkyle("sumFinansinntekt") {
        settUniktFelt(modell2020.resultatregnskap_sumFinansinntekt) {
            forekomsterAv(modell2020.resultatregnskap_finansinntekter.finansinntekt) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    val sumFinanskostnadKalkyle = kalkyle("sumFinanskostnad") {
        settUniktFelt(modell2020.resultatregnskap_sumFinanskostnad) {
            forekomsterAv(modell2020.resultatregnskap_finanskostnader.finanskostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    val sumEkstraordinaerPostKalkyle = kalkyle("sumEkstraordinaerPost") {
        settUniktFelt(modell2020.resultatregnskap_sumEkstraordinaerPost) {
            forekomsterAv(modell2020.resultatregnskap_ekstraordinaerePoster.ekstraordinaerPost) summerVerdiFraHverForekomst {
                forekomstType.inntekt - forekomstType.kostnad
            }
        }
    }

    val sumSkattekostnadKalkyle = kalkyle("sumSkattekostnad") {
        settUniktFelt(modell2020.resultatregnskap_sumSkattekostnad) {
            forekomsterAv(modell2020.resultatregnskap_skattekostnader.skattekostnad) summerVerdiFraHverForekomst {
                forekomstType.negativSkattekostnad - forekomstType.skattekostnad
            }
        }
    }

    val aarsresultatKalkyle = kalkyle("aarsresultat") {
        settUniktFelt(modell2020.resultatregnskap_aarsresultat) {
            modell2020.resultatregnskap_driftsinntekt_sumDriftsinntekt -
                modell2020.resultatregnskap_driftskostnad_sumDriftskostnad +
                modell2020.resultatregnskap_sumFinansinntekt -
                modell2020.resultatregnskap_sumFinanskostnad +
                modell2020.resultatregnskap_sumEkstraordinaerPost +
                modell2020.resultatregnskap_sumSkattekostnad
        }
    }

    private val kalkyleSamling = Kalkylesamling(
        aaretsAvskrivningKalkyle,
        annenDriftsinntektstypeInntektKalkyle,
        annenDriftsinntektstypeFradragKalkyle,
        tilbakefoertKostnadForPrivatBrukAvNaeringsbilKalkyle,
        aaretsInntektsfoeringAvNegativSaldoKalkyle,
        sumDriftsinntekterKalkyle,
        sumDriftskostnaderKalkyle,
        sumFinansinntektKalkyle,
        sumFinanskostnadKalkyle,
        sumEkstraordinaerPostKalkyle,
        sumSkattekostnadKalkyle,
        aarsresultatKalkyle
    )
    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}