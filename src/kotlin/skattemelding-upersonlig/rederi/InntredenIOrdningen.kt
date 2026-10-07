package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler.rederi

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil.skalBeregneRederi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.skattepliktForekomst.erOmfattetAvRederiskatteordningenForrigeInntektsaar
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.util.RederiUtil

internal fun GeneriskModellKontekst.erInntredenIRederiskatteordningenIInntektsaaret(): Boolean =
    skalBeregneRederi(RederiUtil.beskatningsordning.verdi()) &&
        erOmfattetAvRederiskatteordningenForrigeInntektsaar.harVerdi() &&
        erOmfattetAvRederiskatteordningenForrigeInntektsaar.erUsann()


internal fun GeneriskModellKontekst.restFremfoertUnderskudd(): BigDecimal? {
    if (erInntredenIRederiskatteordningenIInntektsaaret()) {
        return BigDecimal.ZERO
    }
    val underhaandsakkordMotregnetFremfoertUnderskudd = minsteVerdiAv(
        modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar.tall(),
        modell.inntektOgUnderskudd.underskuddTilFremfoering_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
    )
    return modell.inntektOgUnderskudd.underskuddTilFremfoering_fremfoertUnderskuddFraTidligereAar -
        underhaandsakkordMotregnetFremfoertUnderskudd
}

object InntredenIOrdningen : HarKalkylesamling {

    private val skattepliktigGevinstVedi = kalkyle {
        hvis(skalBeregneRederi(RederiUtil.beskatningsordning.verdi())) {
            settUniktFelt(modell.rederiskatteordning_inntredenIOrdningen.skattepliktigGevinstVedInntredenIOrdningen) {
                forekomsterAv(modell.rederiskatteordning_inntredenIOrdningen) summerVerdiFraHverForekomst {
                    (forekomstType.markedsverdiForSkip - forekomstType.saldoverdiForSkip) +
                        (forekomstType.markedsverdiForAksjeAndelISdf - forekomstType.skattemessigVerdiForAksjeAndelISdf) +
                        (forekomstType.markedsverdiForUtstyrMv - forekomstType.saldoverdiForUtstyrMv) +
                        (forekomstType.oevrigLatentGevinst - forekomstType.oevrigLatentTap) +
                        (forekomstType.positivGevinstOgTapskonto - forekomstType.negativGevinstOgTapskonto) -
                        forekomstType.underskuddTilFremfoering medMinimumsverdi 0
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            skattepliktigGevinstVedi
        )
    }
}