package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object InntektOgUnderskuddForVirksomhetPaaSokkelFra2025 {

    val forekomstType = modell.inntektOgUnderskuddForVirksomhetPaaSokkel

    internal val beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt =
        kalkyle("beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt") {
            val selskapsskatt =
                forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar

            settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt) {
                if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar stoerreEllerLik selskapsskatt) {
                    selskapsskatt
                } else {
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar.tall()
                }
            }
        }

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
                    && (forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar.harVerdi()
                        || forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt.harVerdi())
            ) {
                settUniktFelt(forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    if (forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar
                        - forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt mindreEnn
                        tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats
                    )
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar.tall()
                    else
                        (tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand * sats).somHeltall()
                }
            }
        }

    internal val beregnetSelskapsskattSomFradrasISaerskattegrunnlaget =
        kalkyle("beregnetSelskapsskattSomFradrasISaerskattegrunnlaget") {
            settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetSelskapsskattSomFradragsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                (forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                    forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar
                ) medMinimumsverdi 0
            }
        }

    internal val beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget =
        kalkyle("beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget") {
            val beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget =
                forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                    forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt -
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                    forekomstType.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                    forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar

            hvis(beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget mindreEnn 0) {
                settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget.absoluttverdi()
                }
            }
        }

    internal val aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            val underskuddFraVirksomhetPaaSokkelSomKrevesTilbakefoertTilTidligereInntektsaar =
                forekomstType.tilbakefoertUnderskuddFraForhaandsfastsettingFraVirksomhetPaaSokkel_underskuddFraVirksomhetPaaSokkelSomKrevesTilbakefoertTilTidligereInntektsaar
            hvis(underskuddFraVirksomhetPaaSokkelSomKrevesTilbakefoertTilTidligereInntektsaar stoerreEnn 0) {
                val sats = satser!!.sats(skattPaaAlminneligInntekt_sats)
                val tall = underskuddFraVirksomhetPaaSokkelSomKrevesTilbakefoertTilTidligereInntektsaar * sats
                settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    if (forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand mindreEnn tall) {
                        forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand.tall()
                    } else {
                        tall
                    }
                }
            }
        }

    internal val fremfoerbarBeregnetNegativSelskapsskatt = kalkyle("fremfoerbarBeregnetNegativSelskapsskatt") {
        settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
            (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                forekomstType.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand -
                forekomstType.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand +
                forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel +
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaar -
                forekomstType.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt).absoluttverdi()
        }
    }

    internal fun GeneriskModellKontekst.forloepigSamletInntektEllerUnderskuddSokkel() =
        forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel

    internal val tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel") {
            val forloepigSamletInntektEllerUnderskuddSokkel = forloepigSamletInntektEllerUnderskuddSokkel()
            val tilbakefoertSokkelunderskudd =
                forekomstType.tilbakefoertUnderskuddFraForhaandsfastsettingFraVirksomhetPaaSokkel_tilbakefoertSokkelunderskudd.tall()
            hvis(forloepigSamletInntektEllerUnderskuddSokkel stoerreEnn 0 && tilbakefoertSokkelunderskudd stoerreEnn 0) {
                settUniktFelt(forekomstType.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel) {
                    if (forloepigSamletInntektEllerUnderskuddSokkel mindreEnn tilbakefoertSokkelunderskudd)
                        forloepigSamletInntektEllerUnderskuddSokkel
                    else
                        tilbakefoertSokkelunderskudd
                }
            }
        }

    internal val aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaar =
        kalkyle("aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaar") {
            settUniktFelt(forekomstType.tilbakefoertUnderskuddFraForhaandsfastsettingFraVirksomhetPaaSokkel_aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaar) {
                modell.inntektOgUnderskudd.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand +
                    forekomstType.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel
            }
        }

    internal val samletInntektEllerUnderskuddAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("samletInntektEllerUnderskuddAlminneligInntektFraVirksomhetPaaSokkel") {
            val inntektEllerUnderskudd =
                forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                    forekomstType.tilbakefoertUnderskuddFraForhaandsfastsettingFraVirksomhetPaaSokkel_underskuddFraVirksomhetPaaSokkelSomKrevesTilbakefoertTilTidligereInntektsaar -
                    forekomstType.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel

            if (inntektEllerUnderskudd stoerreEllerLik 0) {
                settUniktFelt(forekomstType.samletInntektAlminneligInntektFraVirksomhetPaaSokkel) {
                    inntektEllerUnderskudd
                }
            } else {
                settUniktFelt(forekomstType.samletUnderskuddAlminneligInntektFraVirksomhetPaaSokkel) {
                    inntektEllerUnderskudd.absoluttverdi()
                }
            }
        }
}
