package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell as modell

object InntektOgUnderskuddForVirksomhetPaaSokkelFra2026 {

    val forekomstType = modell.inntektOgUnderskuddForVirksomhetPaaSokkel

    internal val negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand") {
            val sats = satser!!.sats(skattPaaAlminneligInntekt_sats)
            val aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand =
                forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand.tall()
            val aaretsBeregnedeNegativeSelskapsskatt =
                forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt.tall()

            hvis(aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand stoerreEnn 0
                    && forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt.harVerdi()) {
                settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    if (aaretsBeregnedeNegativeSelskapsskatt mindreEllerLik
                        aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand * sats
                    ) {
                        aaretsBeregnedeNegativeSelskapsskatt
                    } else {
                        (aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand * sats).somHeltall()
                    }
                }
            }
        }

    internal val beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle(
            "beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand"
        ) {
            val sats = satser!!.sats(skattPaaAlminneligInntekt_sats)
            val fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand =
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand.tall()

            val negativSelskapsskatt =
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar

            hvis(fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand stoerreEnn 0
                    && forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.harVerdi()
            ) {
                settUniktFelt(forekomstType.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    if (negativSelskapsskatt mindreEllerLik fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats
                    ) {
                        negativSelskapsskatt
                    } else {
                        (fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats).somHeltall()
                    }
                }
            }
        }

    internal val beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand") {
            val tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand =
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand

            val sats = satser!!.sats(skattPaaAlminneligInntekt_sats)
            hvis(
                tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand stoerreEnn 0
                        && forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar.harVerdi()
            ) {
                settUniktFelt(forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar
                        - forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt mindreEnn
                        tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats
                    )
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar -
                                forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt
                    else
                        (tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats).somHeltall()
                }
            }
        }


    internal val beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget =
        kalkyle("beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget") {
                settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
                        forekomstType.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand +
                            forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand
            }
        }

    internal val aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle("aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand") {
            hvis(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt.erPositiv()) {
                settUniktFelt(forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt -
                            forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand
                }
            }
        }

    internal val fremfoerbarBeregnetNegativSelskapsskatt = kalkyle("fremfoerbarBeregnetNegativSelskapsskatt") {
        val sum = forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                forekomstType.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
                forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel +
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar -
                forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt

        hvis(sum.stoerreEnn(0)) {
            settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
                sum
            }
        }
    }

}
