package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.api.KodeVerdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellLesKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v2_2021.Regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftsinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftskostnad
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021

/**
 *  https://wiki.sits.no/display/SIR/Kalkyler+resultatregnskap+for+regnskapspliktstype+1+og+5
 *
 *  inntektFraGevinstOgTapskonto er  et oppsummert felt fra Forekomster under GevinstOgTapskonto. Denne verdien skal inn som
 *  annenDriftsinntekt med type/konto 3890. Innslaget som er en forekomst av annenDriftsinntekt
 *  med annenDriftsinntektstype (3890) er således en sum
 *  av denne typen. Det skal ikke være mer enn en sum per type i denne listen.
 */
internal object Resultatregnskapet2021 : HarKalkylesamling {

    internal fun GeneriskModellLesKontekst.regnskapspliktstype2(): Boolean {
        return modell2021.virksomhet.regnskapspliktstype.verdi() == Regnskapspliktstype.type_2
    }

    val aaretsAvskrivningKalkyle = kalkyle("aaretsAvskrivning") {
        hvis(regnskapspliktstype1Eller5()) {
            val aaretsAvskrivningForSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            opprettNyForekomstForAnnenDriftskostnad(
                aaretsAvskrivningForSaldoavskrevetAnleggsmiddel + aaretsAvskrivningForLineaertAvskrevetAnleggsmiddel,
                annenDriftskostnad.kode_6000
            )
        }
    }

    val annenDriftsinntektstypeInntektKalkyle = kalkyle("annenDriftsinntektstypeInntekt") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForAnnenDriftsinntekt(
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektFraGevinstOgTapskonto.tall()
                },
                annenDriftsinntekt.kode_3890
            )
        }
    }

    val aaretsInntektsfoeringAvNegativSaldoKalkyle = kalkyle("aaretsInntektsfoeringAvNegativSaldo") {
        hvis(regnskapspliktstype1Eller5()) {
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarLinear =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarIkkeAvskrivbart =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_ikkeAvskrivbartAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val vederlagVedRealisasjonOgUttakInntektsfoertIAarSaldo =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val aaretsInntektsfoeringAvNegativSaldo =
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
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
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_gevinstOgTapskonto) der {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto stoerreEnn 0
                } summerVerdiFraHverForekomst {
                    forekomstType.inntektsfradragFraGevinstOgTapskonto.tall()
                },
                annenDriftskostnad.kode_7890
            )
        }
    }

    val inntektFraToemmerkontoKalkyle = kalkyle("inntektFraToemmerkonto") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForAnnenDriftsinntekt(
                forekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektFraToemmerkonto.tall()
                },
                annenDriftsinntekt.kode_3910
            )
        }
    }

    val inntektsfradragFraToemmerkontoKalkyle = kalkyle("inntektsfradragFraToemmerkonto") {
        hvis(regnskapspliktstype1Eller5()) {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                    forekomstType.inntektsfradragFraToemmerkonto.tall()
                },
                annenDriftskostnad.kode_7911
            )
        }
    }

    val andelAvDriftsresultatOverfoertTilToemmerkontoKalkyle =
        kalkyle("andelAvDriftsresultatOverfoertTilToemmerkonto") {
            hvis(regnskapspliktstype1Eller5()) {
                opprettNyForekomstForAnnenDriftskostnad(
                    forekomsterAv(modell2021.skogbruk_skogOgToemmerkonto) summerVerdiFraHverForekomst {
                        forekomstType.andelAvDriftsresultatOverfoertTilToemmerkonto.tall()
                    },
                    annenDriftskostnad.kode_7910
                )
            }
        }

    val tilbakefoertKostnadForPrivatBrukAvNaeringsbilKalkyle =
        kalkyle("tilbakefoertKostnadForPrivatBrukAvNaeringsbil") {
            opprettNyForekomstForAnnenDriftskostnad(
                forekomsterAv(modell2021.spesifikasjonAvAnleggsmiddel_transportmiddelINaering) der {
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
            opprettNyForekomstAv(modell2021.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt) {
                medId(kode.kode)
                medFelt(
                    modell2021.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt.type,
                    kode.kode
                )
                medFelt(
                    modell2021.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt.beloep,
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
            opprettNyForekomstAv(modell2021.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad) {
                medId(kode.kode)
                medFelt(
                    modell2021.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad.type,
                    kode.kode
                )
                medFelt(
                    modell2021.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad.beloep,
                    beloep
                )
            }
        }
    }

    private val sumDriftsinntekterKalkyle = kalkyle("sumDriftsinntekter") {
        val sumPositivSalgsinntekt =
            summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
        val sumNegativSalgsinntekt =
            summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)

        val sumSalgsinntekt = sumPositivSalgsinntekt - sumNegativSalgsinntekt

        val sumAnnenDriftsinntekt =
            forekomsterAv(modell2021.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        settUniktFelt(modell2021.resultatregnskap_driftsinntekt_sumDriftsinntekt) {
            sumSalgsinntekt + sumAnnenDriftsinntekt
        }
    }

    private fun GeneriskModellKontekst.summerSalgsinntekterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.resultatregnskap_driftsinntekt_salgsinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSalgsinntekterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    val sumDriftskostnaderKalkyle = kalkyle("sumDriftskostnader") {
        val sumPositiveVarekostnader =
            summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
        val sumNegativeVarekostnader =
            summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
        val sumVarekostnader = sumPositiveVarekostnader - sumNegativeVarekostnader

        val sumLoennskostnader =
            forekomsterAv(modell2021.resultatregnskap_driftskostnad_loennskostnad.kostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }

        val sumPositiveAndreDriftskostnader =
            summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
        val sumNegativeAndreDriftskostnader =
            summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
        val sumAndreDriftskostnader = sumPositiveAndreDriftskostnader - sumNegativeAndreDriftskostnader

        settUniktFelt(modell2021.resultatregnskap_driftskostnad_sumDriftskostnad) {
            sumVarekostnader + sumLoennskostnader + sumAndreDriftskostnader
        }
    }

    private fun GeneriskModellKontekst.summerVarekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.resultatregnskap_driftskostnad_varekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerVarekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private fun GeneriskModellKontekst.summerAndreDriftskostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerAndreDriftskostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private val sumFinansinntektKalkyle = kalkyle("sumFinansinntekt") {
        settUniktFelt(modell2021.resultatregnskap_sumFinansinntekt) {
            forekomsterAv(modell2021.resultatregnskap_finansinntekt.inntekt) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    private val sumFinanskostnadKalkyle = kalkyle("sumFinanskostnad") {
        settUniktFelt(modell2021.resultatregnskap_sumFinanskostnad) {
            forekomsterAv(modell2021.resultatregnskap_finanskostnad.kostnad) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    val sumEkstraordinaerPostKalkyle = kalkyle("sumEkstraordinaerPost") {
        hvis(regnskapspliktstype2()) {
            val sumPositivEkstraordinaerPost =
                summerEkstraordinaerePosterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativEkstraordinaerPost =
                summerEkstraordinaerePosterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
            settUniktFelt(modell2021.resultatregnskap_sumEkstraordinaerPost) {
                sumPositivEkstraordinaerPost - sumNegativEkstraordinaerPost
            }
        }
    }

    private fun GeneriskModellKontekst.summerEkstraordinaerePosterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.resultatregnskap_ekstraordinaerPost.post) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerEkstraordinaerePosterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    val sumSkattekostnadKalkyle = kalkyle("sumSkattekostnad") {
        hvis(regnskapspliktstype2()) {
            val sumPositiveSkattekostnader =
                summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
            val sumNegativeSkattekostnader =
                summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)

            settUniktFelt(modell2021.resultatregnskap_sumSkattekostnad) {
                sumPositiveSkattekostnader - sumNegativeSkattekostnader
            }
        }
    }

    private fun GeneriskModellKontekst.summerSkattekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell2021.resultatregnskap_skattekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSkattekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloep.tall()
        }

    private val aarsresultatKalkyle = kalkyle("aarsresultat") {
        settUniktFelt(modell2021.resultatregnskap_aarsresultat) {
            modell2021.resultatregnskap_driftsinntekt_sumDriftsinntekt -
                modell2021.resultatregnskap_driftskostnad_sumDriftskostnad +
                modell2021.resultatregnskap_sumFinansinntekt -
                modell2021.resultatregnskap_sumFinanskostnad +
                modell2021.resultatregnskap_sumEkstraordinaerPost -
                modell2021.resultatregnskap_sumSkattekostnad
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