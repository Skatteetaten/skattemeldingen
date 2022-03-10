package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.PostProsessering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.Specifications
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.felter.SyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.itererForekomster
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.SkogOgToemmerkonto.hjelpeberegninger.inntektEllerInntektsfradragFraToemmerkonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.skogOgToemmerkonto
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell

internal object SkogOgToemmerkonto : HarKalkyletre, PostProsessering {

    internal object hjelpeberegninger {
        val inntektEllerInntektsfradragFraToemmerkonto = SyntetiskFelt(skogOgToemmerkonto, "inntektEllerInntektsfradragFraToemmerkonto")
    }

    private val skattefordelAvSkogfond = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        ((it.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
            it.innbetaltOffentligTilskuddTilSkogfondkonto) * 0.85).max(0) somFelt it.skattefordelAvSkogfond
    }

    private val tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.innbetaltOffentligTilskuddTilSkogfondkontoTilInvesteringForetattTidligereAar * 0.85 somFelt it.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto
    }

    private val skattepliktigInntektAvSkogfond = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.utbetaltFraSkogfondkontoTilFormaalMedSkattefordel -
            it.skattefordelAvSkogfond +
            it.tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto +
            it.utbetaltFraSkogfondkontoTilFormaalUtenSkattefordel somFelt it.skattepliktigInntektAvSkogfond
    }

    private val utgaaendeVerdiAvToemmeravvirkning = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.inngaaendeVerdiAvToemmeravvirkning - it.inntektsfoertBeloepAvToemmeravvirkning somFelt it.utgaaendeVerdiAvToemmeravvirkning
    }

    private val driftsresultatSomKanOverfoeresTilToemmerkonto = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.salgsinntektOgUttakFraSkurtoemmerMassevirkeRotsalgMv +
            it.salgsinntektOgUttakAvVedOgBiomasse +
            it.andreDriftsinntekter +
            it.skattepliktigInntektAvSkogfond -
            it.driftskostnader +
            it.inntektsfoertBeloepAvToemmeravvirkning -
            it.sjablongberegnetFradragKnyttetTilBiomasseOgVedproduksjon somFelt it.driftsresultatSomKanOverfoeresTilToemmerkonto
    }

    private val driftsresultatFraJaktFiskeOgLeieinntekt = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.leieinntekterFraJaktFiskeTorvuttakMv +
            it.inntektFraGevinstOgTapskonto -
            it.inntektsfradragFraGevinstOgTapskonto +
            it.inntektsfoeringAvNegativSaldo -
            it.andelAvDriftskostnaderTilJaktFiskeMv somFelt it.driftsresultatFraJaktFiskeOgLeieinntekt
    }

    internal val grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.inngaaendeVerdiPaaToemmerkonto +
            it.andelAvDriftsresultatOverfoertTilToemmerkonto +
            it.inngaaendeVerdiFraErvervetToemmerkonto -
            it.verdiPaaAndelAvRealisertToemmerkonto somFelt it.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret
    }

    private val inntektEllerInntektsfradragFraToemmerkontoKalkyle = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret * it.satsForInntektsfoeringOgInntektsfradrag.prosent() somFelt inntektEllerInntektsfradragFraToemmerkonto
    }

    private val inntektFraToemmerkonto = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        inntektEllerInntektsfradragFraToemmerkonto.der(Specifications.erPositiv()) somFelt it.inntektFraToemmerkonto
    }

    private val inntektsfradragFraToemmerkonto = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        inntektEllerInntektsfradragFraToemmerkonto.der(Specifications.erNegativ()) somFelt it.inntektsfradragFraToemmerkonto.abs()
    }

    private val utgaaendeVerdiPaaToemmerkonto = itererForekomster forekomsterAv skogOgToemmerkonto forVerdi {
        it.inngaaendeVerdiPaaToemmerkonto +
            it.andelAvDriftsresultatOverfoertTilToemmerkonto -
            it.inntektFraToemmerkonto +
            it.inntektsfradragFraToemmerkonto somFelt it.utgaaendeVerdiPaaToemmerkonto
    }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(
            skattefordelAvSkogfond,
            tilbakefoeringAvTidligereBeregnetSkattefordelPaaSkogfondkonto,
            skattepliktigInntektAvSkogfond,
            utgaaendeVerdiAvToemmeravvirkning,
            driftsresultatSomKanOverfoeresTilToemmerkonto,
            driftsresultatFraJaktFiskeOgLeieinntekt,
            grunnlagForInntektOgFradragPaaToemmerkontoIInntektsaaret,
            inntektEllerInntektsfradragFraToemmerkontoKalkyle,
            inntektFraToemmerkonto,
            inntektsfradragFraToemmerkonto,
            utgaaendeVerdiPaaToemmerkonto
        ).medPostprosessering(this)
    }

    /**
     * Vi filtrerer vekk hjelpefelt
     */
    override fun postprosessering(generiskModell: GeneriskModell): GeneriskModell {
        return generiskModell.filter {
            it.key != inntektEllerInntektsfradragFraToemmerkonto.key
        }
    }
}
