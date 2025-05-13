package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object Vindkraft : HarKalkylesamling {

    internal val vindkraftverk = kalkyle {
        val satser = satser!!
        forekomsterAv(modell.spesifikasjonPerVindkraftverk) forHverForekomst {
            hvis (forekomstType.positivGrunnrenteinntektFoerSamordning stoerreEllerLik
                forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoertNegativGrunnrenteinntektFraTidligereAar.tall()) {
                settFelt(forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                    forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoertNegativGrunnrenteinntektFraTidligereAar.tall()
                }
            }
            hvis (forekomstType.positivGrunnrenteinntektFoerSamordning mindreEnn
                forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoertNegativGrunnrenteinntektFraTidligereAar.tall()) {
                settFelt(forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                    forekomstType.positivGrunnrenteinntektFoerSamordning.tall()
                }
            }

            settFelt(forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoertNegativGrunnrenteinntektFraTidligereAar.tall() medMaksimumsverdi
                    forekomstType.positivGrunnrenteinntektFoerSamordning.tall()
            }

            settFelt(forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoerbarNegativGrunnrenteinntekt) {
                if (forekomstType.negativGrunnrenteskattVedOpphoer.harVerdi()) {
                    BigDecimal.ZERO
                } else {
                    forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_fremfoertNegativGrunnrenteinntektFraTidligereAar +
                        forekomstType.negativGrunnrenteinntektFoerSamordning -
                        forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar
                }
            }

            settFelt(forekomstType.positivGrunnrenteinntektEtterAnvendelseAvNegativGrunnrenteinntektFraTidligereAar) {
                forekomstType.positivGrunnrenteinntektFoerSamordning -
                    forekomstType.spesifikasjonAvNegativGrunnrenteinntektIVindkraft_aaretsAnvendelseAvNegativGrunnrenteinntektFraTidligereAar
            }

            settFelt(forekomstType.beregnetGrunnrenteskattFoerProduksjonsavgift) {
                (forekomstType.positivGrunnrenteinntektEtterAnvendelseAvNegativGrunnrenteinntektFraTidligereAar *
                    satser.sats(Sats.landbasertVindkraft_skattesatsGrunnrenteinntekt)).somHeltall()
            }

            val samletProduksjonsavgift = forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_fremfoertProduksjonsavgiftFraTidligereAar +
                forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsProduksjonsavgift


            hvis(forekomstType.beregnetGrunnrenteskattFoerProduksjonsavgift stoerreEllerLik samletProduksjonsavgift) {
                settFelt(forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsAnvendelseAvProduksjonsavgift) {
                    samletProduksjonsavgift
                }
            }
            hvis(forekomstType.beregnetGrunnrenteskattFoerProduksjonsavgift mindreEnn samletProduksjonsavgift) {
                settFelt(forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsAnvendelseAvProduksjonsavgift) {
                    forekomstType.beregnetGrunnrenteskattFoerProduksjonsavgift.tall()
                }
            }

            settFelt(forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_fremfoerbarProduksjonsavgift) {
                forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_fremfoertProduksjonsavgiftFraTidligereAar +
                    forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsProduksjonsavgift -
                    forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsAnvendelseAvProduksjonsavgift
            }

            settFelt(forekomstType.positivGrunnrenteskatt) {
                (forekomstType.beregnetGrunnrenteskattFoerProduksjonsavgift -
                    forekomstType.spesifikasjonAvProduksjonsavgiftIVindkraft_aaretsAnvendelseAvProduksjonsavgift) medMinimumsverdi BigDecimal.ZERO
            }
        }

    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            vindkraftverk,
        )
    }
}