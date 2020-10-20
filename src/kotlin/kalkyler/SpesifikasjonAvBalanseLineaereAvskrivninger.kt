@file:Suppress("MoveLambdaOutsideParentheses")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.FeltKoordinat
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErLik
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErMindreEnn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErNull
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.derVerdiErStoerreEnn
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications.ikke
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.der
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.itererForekomster
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.minus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.naeringsopplysninger
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.lineaertavskrevetAnleggsmiddel
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell
import java.math.BigDecimal

object SpesifikasjonAvBalanseLineaereAvskrivninger : HarKalkyletre, PostProsessering {

    internal object beregnedeFelter {
        val antallAarErhvervet: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.antallAarErhvervet")
        val antallMaanederEidIAaret: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.antallMaanederEidIAaret")
        val aaretsInntektsfoeringAvNegativSaldoGrunnlag: FeltKoordinat<lineaertavskrevetAnleggsmiddel> =
            FeltKoordinat("spesifikasjonAvResultatregnskapOgBalanse.lineaertavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldoGrunnlag")
    }

    var initielleBeregninger = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
        { it.ervervsdato.aar() - naeringsopplysninger.inntektsaar somFelt beregnedeFelter.antallAarErhvervet.abs() }

    )

    var initielleBeregninger2 = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(

        {
            der(
                it, {
                    12 - it.ervervsdato.maaned() + 1 somFelt beregnedeFelter.antallMaanederEidIAaret
                }, Specifications.og(
                    beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErLik(BigDecimal(0))),
                    it.realisasjonsdato.filterFelt(derVerdiErNull())
                )
            )
        },
        {
            der(
                it, {
                    it.realisasjonsdato.maaned() somFelt beregnedeFelter.antallMaanederEidIAaret
                },
                Specifications.og(
                    beregnedeFelter.antallAarErhvervet.filterFelt(Specifications.derVerdiErStoerreEnn(0)),
                    it.realisasjonsdato.filterFelt(ikke(derVerdiErNull())),
                )
            )
        },
        {
            der(
                it, {
                    it.realisasjonsdato.maaned() - it.ervervsdato.maaned() + 1 somFelt beregnedeFelter.antallMaanederEidIAaret
                },
                Specifications.og(
                    beregnedeFelter.antallAarErhvervet.filterFelt(Specifications.derVerdiErLik(0)),
                    it.realisasjonsdato.filterFelt(ikke(derVerdiErNull())),
                    it.ervervsdato.filterFelt(ikke(derVerdiErNull()))
                )
            )
        }
    )

    var aaretsAvskrivningKalkyle = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
        {
            der(
                it, {
                    (it.grunnlagForAvskrivningOgInntektsfoering / it.levetid) *
                        beregnedeFelter.antallMaanederEidIAaret somFelt lineaertavskrevetAnleggsmiddel.aaretsAvskrivning
                }, beregnedeFelter.antallAarErhvervet.filterFelt(
                    derVerdiErLik(BigDecimal(0))
                )
            )
        },
        {
            der(
                it, {
                    (it.grunnlagForAvskrivningOgInntektsfoering / it.levetid) * 12 somFelt lineaertavskrevetAnleggsmiddel.aaretsAvskrivning
                }, Specifications.og(
                    beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErStoerreEnn(0)),
                    it.realisasjonsdato.filterFelt(derVerdiErNull())
                )
            )
        },
        {
            der(
                it, {
                    (it.grunnlagForAvskrivningOgInntektsfoering / it.levetid) *
                        beregnedeFelter.antallMaanederEidIAaret somFelt lineaertavskrevetAnleggsmiddel.aaretsAvskrivning
                },
                it.realisasjonsdato.filterFelt(ikke(derVerdiErNull()))
            )
        })

    var aaretsInntektsfoeringAvNegativSaldoGrunnlag =
        itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
            {
                der(
                    it, {
                        it.grunnlagForAvskrivningOgInntektsfoering -
                            it.aaretsAvskrivning -
                            it.vederlagVedRealisasjonOgUttak +
                            it.vederlagVedRealisasjonOgUttakInntektsfoertIAar somFelt beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag
                    }, it.vederlagVedRealisasjonOgUttak.filterFelt(
                        ikke(derVerdiErNull())
                    )
                )
            })

    var aaretsInntektsfoeringAvNegativSaldo =
        itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
            {
                der(
                    lineaertavskrevetAnleggsmiddel,
                    {
                        beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag somFelt lineaertavskrevetAnleggsmiddel.tapOverfoertTilGevinstOgTapskonto
                    },
                    beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag.filterFelt(derVerdiErStoerreEnn(0))
                )
            },
            {
                der(
                    lineaertavskrevetAnleggsmiddel,
                    {
                        beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag somFelt lineaertavskrevetAnleggsmiddel.gevinstOverfoertTilGevinstOgTapskonto.abs()
                    },
                    beregnedeFelter.aaretsInntektsfoeringAvNegativSaldoGrunnlag.filterFelt(derVerdiErMindreEnn(0))
                )
            }
        )

    var utgaaendeVerdiKalkyle = itererForekomster forekomsterAv lineaertavskrevetAnleggsmiddel forVerdier listOf(
        {
            der(
                lineaertavskrevetAnleggsmiddel, {
                    it.inngaaendeVerdi -
                        it.offentligTilskudd +
                        it.justeringAvInngaaendeMva +
                        it.justeringForAapenbarVerdiendring -
                        it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                        it.aaretsAvskrivning -
                        it.vederlagVedRealisasjonOgUttak +
                        it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                        it.gevinstOverfoertTilGevinstOgTapskonto -
                        it.tapOverfoertTilGevinstOgTapskonto somFelt lineaertavskrevetAnleggsmiddel.utgaaendeVerdi.nullHvisNegativt()
                },
                beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErStoerreEnn(0))
            )
        },
        {
            der(
                lineaertavskrevetAnleggsmiddel, {
                    it.anskaffelseskost +
                        it.paakostning -
                        it.offentligTilskudd +
                        it.justeringAvInngaaendeMva +
                        it.justeringForAapenbarVerdiendring -
                        it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene -
                        it.aaretsAvskrivning -
                        it.vederlagVedRealisasjonOgUttak +
                        it.vederlagVedRealisasjonOgUttakInntektsfoertIAar +
                        it.gevinstOverfoertTilGevinstOgTapskonto -
                        it.tapOverfoertTilGevinstOgTapskonto somFelt lineaertavskrevetAnleggsmiddel.utgaaendeVerdi.nullHvisNegativt()
                },
                beregnedeFelter.antallAarErhvervet.filterFelt(derVerdiErLik(0))
            )
        }
    )

    private val kalkyler = Kalkyletre(
        initielleBeregninger,
        initielleBeregninger2,
        aaretsAvskrivningKalkyle,
        aaretsInntektsfoeringAvNegativSaldoGrunnlag,
        aaretsInntektsfoeringAvNegativSaldo,
        utgaaendeVerdiKalkyle
    ).medPostprosessering(this)

    override fun getKalkyletre(): Kalkyletre {
        return kalkyler
    }

}