package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3

object Konsernbidrag : HarKalkylesamling {

    internal val konsernbidragKalkyle = kalkyle {
        val mottattKonsernbidrag = modellV3.inntektOgUnderskudd.mottattKonsernbidrag
        val samletMottattKonsernbidrag =
            forekomsterAv(mottattKonsernbidrag) summerVerdiFraHverForekomst {
                if (mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall()
                } else {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkning.tall()
                }
            }

        settUniktFelt(modellV3.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag) { samletMottattKonsernbidrag }

        val avgittKonsernbidrag = modellV3.inntektOgUnderskudd.avgittKonsernbidrag
        val samletAvgittKonsernbidrag =
            forekomsterAv(avgittKonsernbidrag) summerVerdiFraHverForekomst {
                if (avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall()
                } else {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkning.tall()
                }
            }

        settUniktFelt(modellV3.inntektOgUnderskudd.inntektsfradrag_samletAvgittKonsernbidrag) { samletAvgittKonsernbidrag }

    }

    internal val konsernbidragSvalbardKalkyle = kalkyle {
        val mottattKonsernbidrag = modellV3.inntektOgUnderskuddSvalbard.mottattKonsernbidrag
        val samletMottattKonsernbidrag =
            forekomsterAv(mottattKonsernbidrag) summerVerdiFraHverForekomst {
                if (mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall()
                } else {
                    mottattKonsernbidrag.beloepMedSkattemessigVirkning.tall()
                }
            }

        settUniktFelt(modellV3.inntektOgUnderskuddSvalbard.inntekt_samletMottattKonsernbidrag) { samletMottattKonsernbidrag }

        val avgittKonsernbidrag = modellV3.inntektOgUnderskuddSvalbard.avgittKonsernbidrag
        val samletAvgittKonsernbidrag =
            forekomsterAv(avgittKonsernbidrag) summerVerdiFraHverForekomst {
                if (avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.harVerdi()) {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkningJustertIhtFinansskatteregel.tall()
                } else {
                    avgittKonsernbidrag.beloepMedSkattemessigVirkning.tall()
                }
            }

        settUniktFelt(modellV3.inntektOgUnderskuddSvalbard.inntektsfradrag_samletAvgittKonsernbidrag) { samletAvgittKonsernbidrag }

    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            konsernbidragKalkyle,
            konsernbidragSvalbardKalkyle
        )
    }
}