@file:Suppress("ClassName")

package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV2

object BegrensningAvRentefradragIKonsernOgMellomNaerstaaende2022 : HarKalkylesamling {

    private val nettoPaaloepteRenterKalkyle = kalkyle("nettoPaaloepteRenter") {
        forAlleForekomsterAv(modellV2.begrensningAvRentefradragIKonsernOgMellomNaerstaaende_grunnlagForBeregningAvSelskapetsNettoPaaloepteRenter) {
            settFelt(forekomstType.nettoPaaloepteRenter) {
                forekomstType.totalePaaloepteRenter +
                    forekomstType.garantiprovisjonForGjeld -
                    forekomstType.totaleOpptjenteRenter -
                    forekomstType.gevinstVedRealisasjonAvOverEllerUnderkursobligasjon +
                    forekomstType.tapVedRealisasjonAvOverEllerUnderkursobligasjon -
                    forekomstType.gevinstVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres +
                    forekomstType.tapVedRealisasjonAvSammensattMengdegjeldsbrevEllerFordringSomIkkeSkalDekomponeres
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            nettoPaaloepteRenterKalkyle
        )
    }
}