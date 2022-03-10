package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.PostProsessering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.felter.SyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.felter.der
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.itererForekomster
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.UnderskuddTilFremfoering.hjelpeBeregninger.inntektFoerAnvendelseAvUnderskudd
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.UnderskuddTilFremfoering.hjelpeBeregninger.restFremfoertUnderskudd
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.UnderskuddTilFremfoering.hjelpeBeregninger.restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.UnderskuddTilFremfoering.hjelpeBeregninger.samletUnderSkuddEllerSamletInntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.aar_2021.UnderskuddTilFremfoering.hjelpeBeregninger.underhaandsakkordMotregnetFremfoertUnderskudd
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.inntektOgUnderskudd
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell

object UnderskuddTilFremfoering : HarKalkyletre, PostProsessering {

    internal object hjelpeBeregninger {
        val underhaandsakkordMotregnetFremfoertUnderskudd =
            SyntetiskFelt(inntektOgUnderskudd, "underhaandsakkordMotregnetFremfoertUnderskudd")
        val restFremfoertUnderskudd = SyntetiskFelt(inntektOgUnderskudd, "restFremfoertUnderskudd")
        val inntektFoerAnvendelseAvUnderskudd = SyntetiskFelt(inntektOgUnderskudd, "inntektFoerAnvendelseAvUnderskudd")
        val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd = SyntetiskFelt(
            inntektOgUnderskudd,
            "restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd")
        val samletUnderSkuddEllerSamletInntekt =
            SyntetiskFelt(inntektOgUnderskudd, "samletUnderSkuddEllerSamletInntekt")
    }

    internal val samletUnderSkuddEllerSamletInntektKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdi {
            it.naeringsinntekt -
                it.underskudd +
                it.mottattKonsernbidrag -
                it.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                it.yttKonsernbidrag somFelt samletUnderSkuddEllerSamletInntekt
        }

    internal val resultatAvSamletUnderSkuddEllerSamletInntektKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdier
            listOf(
                {
                    der(
                        inntektOgUnderskudd,
                        { samletUnderSkuddEllerSamletInntekt somFelt inntektOgUnderskudd.samletInntekt.abs() },
                        samletUnderSkuddEllerSamletInntekt.filterFelt(
                            Specifications.derVerdiErStoerreEnnEllerLik(0))
                    )
                },
                {
                    der(
                        inntektOgUnderskudd,
                        { samletUnderSkuddEllerSamletInntekt somFelt inntektOgUnderskudd.samletUnderskudd.abs() },
                        samletUnderSkuddEllerSamletInntekt.filterFelt(
                            Specifications.derVerdiErMindreEnn(0))
                    )

                }
            )

    internal val underhaandsakkordMotregnetFremfoertUnderskuddKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdier
            listOf(
                {
                    der(
                        inntektOgUnderskudd,
                        { it.fremfoertUnderskuddFraTidligereAar somFelt underhaandsakkordMotregnetFremfoertUnderskudd },
                        Specifications.binaryFeltSpec(
                            it.oppnaaddUnderhaandsakkordOgGjeldsettergivelse,
                            it.fremfoertUnderskuddFraTidligereAar)
                        { oppnadd, fremfoert ->
                            oppnadd >= fremfoert
                        }
                    )
                },
                {
                    der(
                        inntektOgUnderskudd,
                        { it.oppnaaddUnderhaandsakkordOgGjeldsettergivelse somFelt underhaandsakkordMotregnetFremfoertUnderskudd },
                        Specifications.binaryFeltSpec(
                            it.oppnaaddUnderhaandsakkordOgGjeldsettergivelse,
                            it.fremfoertUnderskuddFraTidligereAar)
                        { oppnadd, fremfoert ->
                            oppnadd < fremfoert
                        }
                    )

                }
            )

    internal val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdi {
            it.oppnaaddUnderhaandsakkordOgGjeldsettergivelse -
                underhaandsakkordMotregnetFremfoertUnderskudd somFelt inntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelse
        }

    internal val restFremfoertUnderskuddKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdi {
            it.fremfoertUnderskuddFraTidligereAar -
                underhaandsakkordMotregnetFremfoertUnderskudd somFelt restFremfoertUnderskudd
        }

    internal val inntektFoerAnvendelseAvUnderskuddKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdi {
            it.naeringsinntekt -
                it.underskudd +
                it.mottattKonsernbidrag somFelt inntektFoerAnvendelseAvUnderskudd
        }

    internal val aaretsAnvendelseAvFremfoertIUnderskuddFraTidligereAar =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdier
            listOf(
                {
                    der(
                        inntektOgUnderskudd,
                        { restFremfoertUnderskudd somFelt inntektOgUnderskudd.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar },
                        Specifications.binaryFeltSpec(
                            inntektFoerAnvendelseAvUnderskudd,
                            restFremfoertUnderskudd)
                        { inntektFoerAnvendelseAvUnderskudd, restFremfoertUnderskudd ->
                            inntektFoerAnvendelseAvUnderskudd >= restFremfoertUnderskudd &&
                                inntektFoerAnvendelseAvUnderskudd > BigDecimal(0)
                        }
                    )
                },
                {
                    der(
                        inntektOgUnderskudd,
                        { inntektFoerAnvendelseAvUnderskudd somFelt inntektOgUnderskudd.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar },
                        Specifications.binaryFeltSpec(
                            inntektFoerAnvendelseAvUnderskudd,
                            restFremfoertUnderskudd)
                        { inntektFoerAnvendelseAvUnderskudd, restFremfoertUnderskudd ->
                            inntektFoerAnvendelseAvUnderskudd < restFremfoertUnderskudd &&
                                inntektFoerAnvendelseAvUnderskudd > BigDecimal(0)
                        }
                    )

                }
            )

    internal val restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskuddKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdier
            listOf(
                {
                    der(
                        inntektOgUnderskudd,
                        { inntektOgUnderskudd.samletUnderskudd somFelt restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd },
                        Specifications.binaryFeltSpec(
                            inntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelse,
                            inntektOgUnderskudd.samletUnderskudd)
                        { restOppnaaddUnderhaandsakkordOgGjeldsettergivelse, samletUnderskudd ->
                            restOppnaaddUnderhaandsakkordOgGjeldsettergivelse >= samletUnderskudd
                        }
                    )
                },
                {
                    der(
                        inntektOgUnderskudd,
                        { inntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelse somFelt restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd },
                        Specifications.binaryFeltSpec(
                            inntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelse,
                            inntektOgUnderskudd.samletUnderskudd)
                        { restOppnaaddUnderhaandsakkordOgGjeldsettergivelse, samletUnderskudd ->
                            restOppnaaddUnderhaandsakkordOgGjeldsettergivelse < samletUnderskudd
                        }
                    )

                }
            )

    internal val fremfoerbartUnderskuddIInntektKalkyle =
        itererForekomster forekomsterAv inntektOgUnderskudd forVerdi {
            restFremfoertUnderskudd -
                it.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                it.samletUnderskudd -
                restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd somFelt
                it.fremfoerbartUnderskuddIInntekt
        }

    internal val kalkyletreUtenPostProsessering = Kalkyletre(
        underhaandsakkordMotregnetFremfoertUnderskuddKalkyle,
        restFremfoertUnderskuddKalkyle,
        inntektFoerAnvendelseAvUnderskuddKalkyle,
        aaretsAnvendelseAvFremfoertIUnderskuddFraTidligereAar,
        samletUnderSkuddEllerSamletInntektKalkyle,
        resultatAvSamletUnderSkuddEllerSamletInntektKalkyle,
        restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
        restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskuddKalkyle,
        fremfoerbartUnderskuddIInntektKalkyle
    )

    private val kalkyletreMedPostProsessering = kalkyletreUtenPostProsessering.medPostprosessering(this)

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletreMedPostProsessering
    }

    /**
     * Vi filtrerer vekk feltene
     */
    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            !(it.key == underhaandsakkordMotregnetFremfoertUnderskudd.key
                || it.key == restFremfoertUnderskudd.key
                || it.key == inntektFoerAnvendelseAvUnderskudd.key
                || it.key == restOppnaaddUnderhaandsakkordOgGjeldsettergivelseMotregnetSamletUnderskudd.key
                || it.key == samletUnderSkuddEllerSamletInntekt.key)
        }
    }
}