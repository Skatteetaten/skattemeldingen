package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettUniktSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object Kraftverk : HarKalkylesamling {

    internal val samletNegativGrunnrenteinntektFoerSamordningKalkyle = kalkyle {
        settUniktFelt(modell.kraftverk_samordnetGrunnrenteinntekt.samletNegativGrunnrenteinntektFoerSamordning) {
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.negativGrunnrenteinntektFoerSamordning.tall()
            }
        }
    }

    internal val sumAvgittGrunnrenteinntekt =
        opprettUniktSyntetiskFelt(modell.kraftverk_samordnetGrunnrenteinntekt, "sumAvgittGrunnrenteinntekt")
    internal val sumAvgittGrunnrenteinntektKalkyle = kalkyle {
        settUniktFelt(sumAvgittGrunnrenteinntekt) {
            forekomsterAv(modell.kraftverk_samordnetGrunnrenteinntekt.avgittSamordnetGrunnrenteinntektTilSelskapISammeKonsern) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    internal val sumMottattGrunnrenteinntekt =
        opprettUniktSyntetiskFelt(modell.kraftverk_samordnetGrunnrenteinntekt, "sumMottattGrunnrenteinntekt")
    internal val sumMottattGrunnrenteinntektKalkyle = kalkyle {
        settUniktFelt(sumMottattGrunnrenteinntekt) {
            forekomsterAv(modell.kraftverk_samordnetGrunnrenteinntekt.mottattSamordnetGrunnrenteinntektFraSelskapISammeKonsern) summerVerdiFraHverForekomst {
                forekomstType.beloep.tall()
            }
        }
    }

    internal val sumPositiveGrunnrenteinntekter =
        opprettUniktSyntetiskFelt(modell.kraftverk_samordnetGrunnrenteinntekt, "sumPositiveGrunnrenteinntekter")
    internal val sumPositiveGrunnrenteinntekterKalkyle = kalkyle {
        settUniktFelt(sumPositiveGrunnrenteinntekter) {
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.positivGrunnrenteinntektFoerSamordning.tall()
            }
        }
    }

    internal val endeligSamordnetGrunnrenteinntektKalkyle = kalkyle {
        forekomsterAv(modell.kraftverk_samordnetGrunnrenteinntekt) forHverForekomst {
            val sum =
                sumPositiveGrunnrenteinntekter -
                    forekomstType.samletNegativGrunnrenteinntektFoerSamordning -
                    sumAvgittGrunnrenteinntekt +
                    sumMottattGrunnrenteinntekt
            hvis(sum stoerreEllerLik 0) {
                settFelt(forekomstType.endeligSamordnetPositivGrunnrenteinntekt) { sum }
            }
            hvis(sum mindreEnn 0) {
                settFelt(forekomstType.endeligSamordnetNegativGrunnrenteinntekt) { sum.absoluttverdi() }
            }
        }
    }

    internal val fordelingsNoekkelKalkyle = kalkyle {
        val sum = sumPositiveGrunnrenteinntekter.tall()

        hvis(sum stoerreEnn 0) {
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) forHverForekomst {
                settFelt(forekomstType.fordelingsnoekkel) {
                    (forekomstType.positivGrunnrenteinntektFoerSamordning.tall() / sum)
                }
            }
        }
    }

    internal val fordeltPositivGrunnrenteinntektEtterSamordning = kalkyle {
        val endeligSamordnetPositivGrunnrenteinntekt =
            modell.kraftverk_samordnetGrunnrenteinntekt.endeligSamordnetPositivGrunnrenteinntekt.tall()
        forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) forHverForekomst {
            settFelt(forekomstType.fordeltPositivGrunnrenteinntektEtterSamordning) {
                (endeligSamordnetPositivGrunnrenteinntekt * forekomstType.fordelingsnoekkel.tall()).somHeltall()
            }
        }
    }

    internal val samletEiendomsskattegrunnlag = kalkyle {
        settUniktFelt(modell.kraftverk_samletEiendomsskattegrunnlag) {
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.eiendomsskattegrunnlag.tall()
            }
        }
    }

    internal val samletGrunnlagForNaturressursskattKalkyle = kalkyle {
        settUniktFelt(modell.kraftverk_samletGrunnlagForNaturressursskatt) {
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.grunnlagForNaturressursskatt.tall()
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletNegativGrunnrenteinntektFoerSamordningKalkyle,
            sumPositiveGrunnrenteinntekterKalkyle,
            sumAvgittGrunnrenteinntektKalkyle,
            sumMottattGrunnrenteinntektKalkyle,
            endeligSamordnetGrunnrenteinntektKalkyle,
            fordelingsNoekkelKalkyle,
            fordeltPositivGrunnrenteinntektEtterSamordning,
            samletEiendomsskattegrunnlag,
            samletGrunnlagForNaturressursskattKalkyle
        )
    }
}