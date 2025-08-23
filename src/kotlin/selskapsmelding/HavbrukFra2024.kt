package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.felles.naeringsspesifikasjon.modellNaering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.sdfharOrgformANS
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV3

object HavbrukFra2024 : HarKalkylesamling {

    private val positivNegativGrunnrenteinntektFoerSamordning =
        kalkyle("positivNegativGrunnrenteinntektFoerSamordning") {
            val grunnrenteinntektFoerSamordningFraNaering =
                modellNaering.spesifikasjonAvGrunnrenteinntektIHavbruksvirksomhet.grunnrenteinntektFoerSamordning.tall()
                    .somHeltall()
            hvis(grunnrenteinntektFoerSamordningFraNaering stoerreEllerLik 0) {
                settUniktFelt(
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning
                ) { grunnrenteinntektFoerSamordningFraNaering }
            }
            hvis(grunnrenteinntektFoerSamordningFraNaering mindreEnn 0) {
                settUniktFelt(
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning
                ) { grunnrenteinntektFoerSamordningFraNaering }
            }

        }

    private val bunnfradrag = kalkyle("bunnfradrag") {
        val satser = satser!!
        val andelAvMaksimaltTillattBiomasse = if (inntektsaar.tekniskInntektsaar >= 2025) {
            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimaltTillattBiomasse
        } else {
            modellV3.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_andelAvMaksimalTillattBiomasse
        }

        hvis(andelAvMaksimaltTillattBiomasse.harVerdi()) {
            val maksimaltBunnfradrag = satser.sats(Sats.havbruk_maksimaltBunnfradrag)
            val skattesatsAlminneligInntektUtenforTiltakssone = satser.sats(Sats.skattPaaAlminneligInntekt_sats)
            val bunnfradrag =
                (maksimaltBunnfradrag * (BigDecimal.ONE - skattesatsAlminneligInntektUtenforTiltakssone)) * andelAvMaksimaltTillattBiomasse / 100

            hvis(bunnfradrag != null) {
                settUniktFelt(modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag) { bunnfradrag }
            }
        }
    }

    private val positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag =
        kalkyle("positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag") {
            hvis(sdfharOrgformANS(generiskModell.tilGeneriskModell())) {
                val positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag =
                    modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning -
                        modell.havbruksvirksomhet.beregnetGrunnrenteskatt_andelAvBunnfradrag_bunnfradrag

                hvis(positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag.harVerdi()) {
                    val positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradragVerdi =
                        if (positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag.erNegativ()) BigDecimal.ZERO
                        else positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag

                    hvis(positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradragVerdi.harVerdi()) {
                        settUniktFelt(
                            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag
                        ) { positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradragVerdi }
                    }
                }
            }
        }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            positivNegativGrunnrenteinntektFoerSamordning,
            bunnfradrag,
            positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag
        )
    }
}
