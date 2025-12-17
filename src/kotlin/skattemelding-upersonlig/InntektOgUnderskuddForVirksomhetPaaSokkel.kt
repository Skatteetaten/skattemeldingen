package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.petroleum_andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.minsteVerdiAv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV3
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modellV4 as modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.service.overfoeringAvFelter.FORDELTSKATTEMESSIGRESULTAT_ANDELFINANSTILLAND_FRA_NAERINGSSPESIFIKASJON_FELT

object InntektOgUnderskuddForVirksomhetPaaSokkel {

    val forekomstType = modell.inntektOgUnderskuddForVirksomhetPaaSokkel

    internal val aaretsBeregnedeSelskapsskatt = kalkyle("aaretsBeregnedeSelskapsskatt") {
        val sats = satser!!.sats(skattPaaAlminneligInntekt_sats)
        val aaretsBeregnedeSelskapsskatt =
            (forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_grunnlagForBeregningAvSelskapsskatt * sats).somHeltall()
        if (forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_grunnlagForBeregningAvSelskapsskatt stoerreEllerLik 0) {
            settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt) {
                aaretsBeregnedeSelskapsskatt
            }
        } else {
            settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt) {
                aaretsBeregnedeSelskapsskatt.absoluttverdi()
            }
        }
    }

    internal val aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar =
        kalkyle("aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar") {
            hvis(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt stoerreEnn 0) {
                settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar) {
                    minsteVerdiAv(
                        forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt.tall(),
                        forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar.tall()
                    )
                }
            }
        }

    internal val beregnetSelskapsskattSomFradrasISaerskattegrunnlaget =
        kalkyle("beregnetSelskapsskattSomFradrasISaerskattegrunnlaget") {
            settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetSelskapsskattSomFradragsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                (forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeSelskapsskatt -
                    forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) medMinimumsverdi 0
            }
        }

    internal val beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget =
        kalkyle("beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget") {
            hvis(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_aaretsBeregnedeNegativeSelskapsskatt.harVerdi()) {
                settUniktFelt(forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand.tall() medMinimumsverdi 0
                }
            }
        }

    internal val nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle {
            val andelFinansFoertMotLandinntekt = generiskModell.verdiFor(
                        FORDELTSKATTEMESSIGRESULTAT_ANDELFINANSTILLAND_FRA_NAERINGSSPESIFIKASJON_FELT
                    )?.toBigDecimal()

            hvis(andelFinansFoertMotLandinntekt mindreEnn 0 && modell.inntektOgUnderskudd.inntektsfradrag_underskudd stoerreEnn 0) {
                settUniktFelt(forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel) {
                    minsteVerdiAv(
                        modell.inntektOgUnderskudd.inntektsfradrag_underskudd.tall(),
                        andelFinansFoertMotLandinntekt.absoluttverdi()
                    )
                }
            }
        }

    internal val aaretsUnderskudd = kalkyle("aaretsUnderskudd") {
        hvis(harForekomsterAv(forekomstType)) {
            val sats = satser!!.sats(petroleum_andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel)
            val underskudd = modell.inntektOgUnderskudd.inntektsfradrag_underskudd -
                forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel
            hvis(underskudd.erPositiv()) {
                settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskudd) {
                    (underskudd * sats).somHeltall()
                }
            }
        }
    }

    internal val underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd =
        kalkyle {
            hvis(
                (modell.inntektOgUnderskudd.inntektsfradrag_underskudd -
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel) stoerreEnn 0 &&
                    (forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel -
                        forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel -
                        forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel) stoerreEnn 0
            ) {
                settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd) {
                    minsteVerdiAv(
                        forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskudd.tall(),
                        (forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel -
                            forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel -
                            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel)
                    )
                }
            }
        }

    internal val andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle {
            settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel) {
                modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd.tall()
            }
        }

    internal val samletInntektEllerUnderskuddSaerskattegrunnlagFraVirksomhetPaaSokkel =
        kalkyle("samletInntektEllerUnderskuddSaerskattegrunnlagFraVirksomhetPaaSokkel") {
            val samletInntektEllerUnderskudd =
                forekomstType.inntektFraVirksomhetPaaSokkel_inntektSaerskattegrunnlagFraVirksomhetPaaSokkel -
                    forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddSaerskattegrunnlagFraVirksomhetPaaSokkel -
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetSelskapsskattSomFradragsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel +
                    forekomstType.beregnetSelskapsskattForAndelAvVirksomhetSomErSaerskattepliktig_beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlagFraVirksomhetPaaSokkel
            if (samletInntektEllerUnderskudd stoerreEllerLik 0) {
                settUniktFelt(forekomstType.samletInntektSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    samletInntektEllerUnderskudd
                }
            } else {
                settUniktFelt(forekomstType.samletUnderskuddSaerskattegrunnlagFraVirksomhetPaaSokkel) {
                    samletInntektEllerUnderskudd.absoluttverdi()
                }
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
        settUniktFelt(forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoerbarBeregnetNegativSelskapsskatt) {
            (forekomstType.beregnetNegativSelskapsskattTilFremfoering_fremfoertBeregnetNegativSelskapsskattFraTidligereAar -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar +
                forekomstType.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand -
                forekomstType.beregnetNegativSelskapsskattTilFremfoering_aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel) medMinimumsverdi 0
        }
    }

    internal val aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand = kalkyle {
        val grunnlagLandinntekt = grunnlagLandinntekt()
        val inntektAlminneligInntektFraVirksomhetPaaSokkel =
            forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel
        val underskuddAlminneligInntektFraVirksomhetPaaSokkel =
            forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel
        val aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand =
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand

        hvis(harForekomsterAv(modell.inntektOgUnderskuddForVirksomhetPaaSokkel)) {
            if(
                inntektAlminneligInntektFraVirksomhetPaaSokkel stoerreEllerLik 0 || modell.inntektOgUnderskudd.inntektsfradrag_underskudd stoerreEnn 0 ||
                (modell.inntektOgUnderskudd.naeringsinntekt.harVerdi() && grunnlagLandinntekt mindreEllerLik 0)
            ) {
                settUniktFelt(aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    BigDecimal.ZERO
                }
            }

            else if(
                underskuddAlminneligInntektFraVirksomhetPaaSokkel stoerreEnn 0 && underskuddAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    .absoluttverdi() mindreEnn grunnlagLandinntekt.absoluttverdi()
            ) {
                settUniktFelt(aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    underskuddAlminneligInntektFraVirksomhetPaaSokkel.tall().absoluttverdi()
                }
            }

            else if(
                underskuddAlminneligInntektFraVirksomhetPaaSokkel stoerreEnn 0 && underskuddAlminneligInntektFraVirksomhetPaaSokkel.tall()
                    .absoluttverdi() stoerreEllerLik grunnlagLandinntekt.absoluttverdi()
            ) {
                settUniktFelt(aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    grunnlagLandinntekt.absoluttverdi()
                }
            }
        }
    }

    internal val underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd =
        kalkyle {
            hvis(erPetroleumsforetak() && modell.inntektOgUnderskudd.inntektsfradrag_underskudd - forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd) {
                    minsteVerdiAv(
                        modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag.tall(),
                        (modell.inntektOgUnderskudd.inntektsfradrag_underskudd -
                            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                            modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd)
                    )
                }
            }
        }

    internal val andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd =
        kalkyle {
            settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd) {
                minsteVerdiAv(
                    (modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd *
                        BigDecimal.valueOf(0.5)).somHeltall(),
                    (forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskudd -
                        forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel)
                )
            }
        }

    internal val andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsFremfoerbareUnderskudd =
        kalkyle("aaretsFremfoerbareUnderskudd") {
            settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsFremfoerbareUnderskudd) {
                (forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskudd -
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd) medMinimumsverdi 0
            }
        }

    internal val fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle {
            val landinntekt = landinntektEtterAnvendelseAvAaretsUnderskudd()

            hvis(landinntekt + modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    minsteVerdiAv(
                        landinntekt + modell.inntektOgUnderskudd.inntekt_samletMottattKonsernbidrag,
                        modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar.tall()
                    )
                }
            }
        }

    internal val fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle {
            val sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd =
                sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd()
            hvis(sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd -
                forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel stoerreEnn 0 &&
                forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar.harVerdi()
            ) {
                settUniktFelt(forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel) {
                    minsteVerdiAv(
                        sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd - forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
                        forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar.tall(),
                        modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar -
                            modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand
                    )
                }
            }
        }

    internal val andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar =
        kalkyle {
            val restUnderskuddTidligereAar = restUnderskuddTidligereAar()
            settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                if (
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar -
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel mindreEnn restUnderskuddTidligereAar
                ) {
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar -
                        (forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar -
                            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel)
                } else {
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar - restUnderskuddTidligereAar
                }
            }
        }

    internal val andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel - andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel") {
            settUniktFelt(forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel) {
                (forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoertUnderskuddFraTidligereAar -
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                    modellV3.inntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_mottattKonsernbidragTilReduksjonIForegaaendeAarsFremfoerbareUnderskudd +
                    forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsFremfoerbareUnderskudd) medMinimumsverdi 0
            }
        }

    internal val aaretsUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle("aaretsUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel") {
            settUniktFelt(forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_aaretsUnderskudd) {
                forekomstType.samletUnderskuddAlminneligInntektFraVirksomhetPaaSokkel.tall()
            }
        }

    internal val fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelUnderskudd =
        kalkyle("fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel - underskuddTilFremfoeringFraVirksomhetPaaSokkel") {
            settUniktFelt(forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel) {
                (forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_aaretsUnderskudd +
                    forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_fremfoertUnderskuddFraTidligereAar -
                    forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) medMinimumsverdi 0
            }
        }

    internal val samletAnnetSkattefradragFraVirksomhetPaaSokkel =
        kalkyle("samletAnnetSkattefradragFraVirksomhetPaaSokkel") {
            var samletAnnetSkattefradragFraVirksomhetPaaSokkel = forekomstType.skattefradragForVirksomhetPaaSokkel_annetFradragKnyttetTilSaerskattegrunnlaget +
            forekomstType.skattefradragForVirksomhetPaaSokkel_annetFradragKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel

            if (inntektsaar.tekniskInntektsaar <= 2024) {
                samletAnnetSkattefradragFraVirksomhetPaaSokkel = samletAnnetSkattefradragFraVirksomhetPaaSokkel +
                modellV3.inntektOgUnderskuddForVirksomhetPaaSokkel.skattefradragForVirksomhetPaaSokkel_fradragVedVirksomhetsoverdragelseErvervOgRealisasjonAvLisensMvKnyttetTilSaerskattegrunnlaget +
                modellV3.inntektOgUnderskuddForVirksomhetPaaSokkel.skattefradragForVirksomhetPaaSokkel_fradragVedVirksomhetsoverdragelseErvervOgRealisasjonAvLisensMvKnyttetTilAlminneligInntektFraVirksomhetPaaSokkel
            }
            settUniktFelt(forekomstType.samletAnnetSkattefradragFraVirksomhetPaaSokkel) {
                samletAnnetSkattefradragFraVirksomhetPaaSokkel
            }
        }

    internal val samletAnnetSkattefradragFraVirksomhetPaaLand =
        kalkyle("samletAnnetSkattefradragFraVirksomhetPaaLand") {
            settUniktFelt(forekomstType.samletAnnetSkattefradragFraVirksomhetPaaLand) {
                modellV3.inntektOgUnderskuddForVirksomhetPaaSokkel.skattefradragForVirksomhetPaaSokkel_fradragVedVirksomhetsoverdragelseErvervOgRealisasjonAvLisensMvKnyttetTilAlminneligInntektFraVirksomhetPaaLand +
                    forekomstType.skattefradragForVirksomhetPaaSokkel_annetFradragKnyttetTilAlminneligInntektFraVirksomhetPaaLand
            }
        }

    fun GeneriskModellKontekst.landinntektEtterAnvendelseAvAaretsUnderskudd(): BigDecimal? =
        modell.inntektOgUnderskudd.naeringsinntekt -
            modell.inntektOgUnderskudd.inntektsfradrag_underskudd +
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
            forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand

    internal val fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand =
        kalkyle {
            val landinntekt = landinntektEtterAnvendelseAvAaretsUnderskudd()

            hvis(landinntekt stoerreEnn 0) {
                settUniktFelt(modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand) {
                    minsteVerdiAv(
                        landinntekt - modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand,
                        forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_fremfoertUnderskuddFraTidligereAar -
                            modell.inntektOgUnderskuddForVirksomhetPaaSokkel.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel.tall()
                                .absoluttverdi()
                    )
                }
            }
        }

    internal val underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar =
        kalkyle {
            settUniktFelt(modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                    modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand
            }
        }

    fun GeneriskModellKontekst.restUnderskuddTidligereAar() =
        modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar -
            modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
            modellV3.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIForegaaendeAarsFremfoerbareUnderskudd


    fun GeneriskModellKontekst.grunnlagLandinntekt() =
        modell.inntektOgUnderskudd.naeringsinntekt -
            modell.inntektOgUnderskudd.inntektsfradrag_underskudd -
            modell.inntektOgUnderskudd.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_fremfoertUnderskuddFraTidligereAar

    fun GeneriskModellKontekst.sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd() =
        forekomstType.inntektFraVirksomhetPaaSokkel_inntektAlminneligInntektFraVirksomhetPaaSokkel - // overført fra næring
            forekomstType.underskuddFraVirksomhetPaaSokkel_underskuddAlminneligInntektFraVirksomhetPaaSokkel - // overført fra næring
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel -
            forekomstType.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand

    internal val fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAArFoertMotAlminneligInntektFraVirksomhetPaaSokkel =
        kalkyle {
            val sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd =
                sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd()
            hvis(
                sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd stoerreEnn 0 &&
                    forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_fremfoertUnderskuddFraTidligereAar.harVerdi()
            ) {
                settUniktFelt(forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel) {
                    minsteVerdiAv(
                        sumGrunnlagForFradragsfoeringAvFremfoerbareUnderskudd,
                        forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_fremfoertUnderskuddFraTidligereAar.tall()
                    )
                }
            }

        }

    internal val underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAAr = kalkyle {
        settUniktFelt(forekomstType.underskuddTilFremfoeringFraVirksomhetPaaSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
            forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel +
                modell.inntektOgUnderskudd.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand
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
                    forekomstType.korrigeringerIInntektOgUnderskuddForVirksomhetPaaSokkel_fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel

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
