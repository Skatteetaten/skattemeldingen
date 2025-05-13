package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.ResultatregnskapOgBalanse
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object ResultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven : HarKalkylesamling {

    private val fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad =
        kalkyle("fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad") {
            if (erPetroleumsforetak()) {
                val sumPositiveAndreDriftsinntekter =
                    summerAndreDriftsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeAndreDriftsinntekter =
                    summerAndreDriftsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val annenDriftsinntekt = sumPositiveAndreDriftsinntekter - sumNegativeAndreDriftsinntekter

                val sumPositiveSalgsinntekter =
                    summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeSalgsinntekter =
                    summerSalgsinntekterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val driftsinntektSalgsinntekt = sumPositiveSalgsinntekter - sumNegativeSalgsinntekter

                val sumPositiveAndreDriftskostnader =
                    summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeAndreDriftskostnader =
                    summerAndreDriftskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val annenDriftskostnad = sumPositiveAndreDriftskostnader - sumNegativeAndreDriftskostnader

                val sumPositiveLoennskostnader =
                    summerLoennskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeLoennskostnader =
                    summerLoennskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val driftskostnadLoennskostnad = sumPositiveLoennskostnader - sumNegativeLoennskostnader

                val sumPositiveVarekostnader =
                    summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeVarekostnader =
                    summerVarekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val driftskostnadVarekostnad = sumPositiveVarekostnader - sumNegativeVarekostnader

                val sumPositiveFinansinntekter =
                    summerFinansinntekterForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeFinansinntekter =
                    summerFinansinntekterForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val finansinntekt = sumPositiveFinansinntekter - sumNegativeFinansinntekter

                val sumPositiveFinanskostnader =
                    summerFinanskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeFinanskostnader =
                    summerFinanskostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val finanskostnad = sumPositiveFinanskostnader - sumNegativeFinanskostnader

                val sumPositiveSkattekostnader =
                    summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeSkattekostnader =
                    summerSkattekostnaderForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val skattekostnad = sumPositiveSkattekostnader - sumNegativeSkattekostnader

                val sumPositiveResultatkomponenterForIFRSForetak =
                    summerResultatkomponenterForIFRSForetakForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeResultatkomponenterForIFRSForetak =
                    summerResultatkomponenterForIFRSForetakForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val resultatkomponentForIFRSForetak = sumPositiveResultatkomponenterForIFRSForetak - sumNegativeResultatkomponenterForIFRSForetak

                settUniktFelt(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepAlminneligInntektFraVirksomhetPaaLand) {
                    driftsinntektSalgsinntekt +
                        annenDriftsinntekt -
                        annenDriftskostnad -
                        driftskostnadLoennskostnad -
                        driftskostnadVarekostnad +
                        finansinntekt -
                        finanskostnad -
                        skattekostnad +
                        resultatkomponentForIFRSForetak
                }

                val sumPositiveFinansinntektDiffer =
                    summerFinansinntektdifferForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeFinansinntektDiffer =
                    summerFinansinntektdifferForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val finansinntektDiff = sumPositiveFinansinntektDiffer - sumNegativeFinansinntektDiffer

                val sumPositiveFinanskostnadDiffer =
                    summerFinanskostnaddifferForKategori(ResultatregnskapOgBalanse.Kategori.POSITIV)
                val sumNegativeFinanskostnadDiffer =
                    summerFinanskostnaddifferForKategori(ResultatregnskapOgBalanse.Kategori.NEGATIV)
                val finanskostnadDiff = sumPositiveFinanskostnadDiffer - sumNegativeFinanskostnadDiffer

                settUniktFelt(modell.resultatregnskap_resultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven.fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad_beloepResultatAvFinansinntektOgFinanskostnadMv) {
                    finansinntektDiff - finanskostnadDiff
                }
            }
        }

    private fun GeneriskModellKontekst.summerAndreDriftsinntekterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerAndreDriftsinntekterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerSalgsinntekterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftsinntekt_salgsinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSalgsinntekterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerAndreDriftskostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerAndreDriftskostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerLoennskostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_loennskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerLoennskostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerVarekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_driftskostnad_varekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerVarekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerFinansinntekterForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_finansinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerFinansinntekterForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerFinanskostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_finanskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerFinanskostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerSkattekostnaderForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_skattekostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerSkattekostnaderForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerResultatkomponenterForIFRSForetakForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_resultatkomponentForIFRSForetak.resultatkomponent) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerResultatkomponenterForIFRSForetakForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }


    private fun GeneriskModellKontekst.summerFinansinntektdifferForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_finansinntekt.inntekt) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerFinansinntektdifferForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep.tall() -
                forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    private fun GeneriskModellKontekst.summerFinanskostnaddifferForKategori(
        kategori: ResultatregnskapOgBalanse.Kategori
    ) =
        forekomsterAv(modell.resultatregnskap_finanskostnad.kostnad) der {
            ResultatregnskapOgBalanse.hentKategori(this@summerFinanskostnaddifferForKategori.inntektsaar, forekomstType.type.verdi()) == kategori
        } summerVerdiFraHverForekomst {
            forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_beloep.tall() -
                forekomstType.beloepForVirksomhetOmfattetAvPetroleumsskatteloven_heravBeloepFraVirksomhetPaaLand.tall()
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            fordeltAarsresultatFoerOmklassifisertInntektEllerKostnad
        )
    }
}
