package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object KonsernbidragFra2024 : HarKalkylesamling {

    internal val konsernbidragKalkyle = kalkyle {
        val mottattKonsernbidrag =
            modell.inntektOgUnderskudd.inntekt_mottattKonsernbidrag_mottattKonsernbidragPerAvgiver
        val samletMottattKonsernbidrag =
            forekomsterAv(mottattKonsernbidrag) summerVerdiFraHverForekomst {
                if (mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall() +
                        mottattKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                } else {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkning.tall() +
                        mottattKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                }
            }

        settUniktFelt(modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag) { samletMottattKonsernbidrag }

        val avgittKonsernbidrag =
            modell.inntektOgUnderskudd.inntektsfradrag_avgittKonsernbidrag_avgittKonsernbidragPerMottaker
        val samletAvgittKonsernbidrag =
            forekomsterAv(avgittKonsernbidrag) summerVerdiFraHverForekomst {
                if (avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall() +
                        avgittKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                } else {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkning.tall() +
                        avgittKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                }
            }

        settUniktFelt(modell.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag) { samletAvgittKonsernbidrag }

    }

    internal val konsernbidragSvalbardKalkyle = kalkyle {
        val mottattKonsernbidrag =
            modell.inntektOgUnderskuddSvalbard.mottattKonsernbidrag_mottattKonsernbidragPerAvgiver
        val samletMottattKonsernbidrag =
            forekomsterAv(mottattKonsernbidrag) summerVerdiFraHverForekomst {
                if (mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall() +
                        mottattKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                } else {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkning.tall() +
                        mottattKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                }
            }

        settUniktFelt(modell.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag) { samletMottattKonsernbidrag }

        val avgittKonsernbidrag = modell.inntektOgUnderskuddSvalbard.avgittKonsernbidrag_avgittKonsernbidragPerMottaker
        val samletAvgittKonsernbidrag =
            forekomsterAv(avgittKonsernbidrag) summerVerdiFraHverForekomst {
                if (avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall() +
                        avgittKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                } else {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkning.tall() +
                        avgittKonsernbidrag.beloepMedSkattemessigVirkningTilknyttetUtenlandskSelskap.tall()
                }
            }

        settUniktFelt(modell.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag) { samletAvgittKonsernbidrag }

    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            konsernbidragKalkyle,
            konsernbidragSvalbardKalkyle
        )
    }
}