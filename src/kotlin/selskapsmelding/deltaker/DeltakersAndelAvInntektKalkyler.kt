package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.medAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.selskapsmelding.sdf.domenemodell.v4_2025.v4
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsForFinansskattepliktigVirksomhet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsITiltakssone
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skjermingsrenteForPersonligeAksjonaererOgDeltakereIAnsvarligSelskapMv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErPersonlig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErUpersonlig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErUpersonligEllerSdf
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erBosattInnenforTiltakssonenINordTromsOgFinnmark
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erFinansskattepliktig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erNokus
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modell

object DeltakersAndelAvInntektKalkyler : HarKalkylesamling {

    private val deltaker = modell.deltaker

    val sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt =
        opprettSyntetiskFelt(deltaker, "sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltaker")
    val sumFoerArbeidsgodtgjoerelseFelt = opprettSyntetiskFelt(deltaker, "sumFoerArbeidsgodtgjoerelse")
    val grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt =
        opprettSyntetiskFelt(deltaker, "grunnlagForBeregningAvAaretsAnvendelseAvUnderskudd")
    val realisertAndelAvUnderskuddPer31122002 =
        opprettSyntetiskFelt(deltaker.realisasjonOgAnnenOverdragelseAvAndel, "realisertAndelAvUnderskuddPer31122002")

    object KorreksjonIAlminneligInntekt {
        internal val sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt) {
                    forekomstType.andelAvSamletInntekt -
                        forekomstType.andelAvSamletUnderskudd +
                        forekomstType.korreksjonIAlminneligInntekt_gevinstVedUttakFraNorskBeskatningsomraade -
                        forekomstType.korreksjonIAlminneligInntekt_tapVedUttakFraNorskBeskatningsomraade
                }
            }
        }

        internal val sumFoerArbeidsgodtgjoerelseKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(sumFoerArbeidsgodtgjoerelseFelt) {
                    if (forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_erKommandittistEllerStilleDeltakerMedBeloepsbegrensetAnsvar.erSann()) {
                        (sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt - forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) medMinimumsverdi 0
                    } else {
                        sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt.tall()
                    }
                }
            }
        }

        internal val alminneligInntektEllerUnderskuddKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {

                val inntektEllerUnderskudd =
                    if (this@kalkyle.erNokus() && deltakerErUpersonligEllerSdf()) {
                        forekomstType.andelAvSamletInntekt -
                            forekomstType.andelAvSamletUnderskudd +
                            forekomstType.korreksjonIAlminneligInntekt_utbetaltOverskuddMvTilUpersonligDeltakerFraMidlerOpptjentFoerSelskapBleNokus -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                            forekomstType.underskuddTilFremfoeringINokus_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
                    } else if (this@kalkyle.erNokus() && deltakerErPersonlig()) {
                        forekomstType.andelAvSamletInntekt -
                            forekomstType.andelAvSamletUnderskudd -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                            forekomstType.underskuddTilFremfoeringINokus_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
                    } else {
                        sumFoerArbeidsgodtgjoerelseFelt +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFiskeOgFangst +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFamiliebarnehageIDeltakersHjem +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenAnnenNaering
                    }

                if (inntektEllerUnderskudd stoerreEllerLik 0) {
                    settFelt(forekomstType.alminneligInntekt) { inntektEllerUnderskudd.somHeltall() }
                } else {
                    settFelt(forekomstType.underskudd) { inntektEllerUnderskudd.somHeltall().absoluttverdi() }
                }
            }
        }
    }

    object UtdelingMv {
        internal val skattPaaDeltakersAndelAvSelskapetsOverskuddKalkyle = kalkyle {
            hvis(!erNokus()) {
                val satser = satser!!
                forekomsterAv(deltaker) forHverForekomst {
                    val skattesats = if (deltakerErPersonlig() && !erBosattInnenforTiltakssonenINordTromsOgFinnmark()) {
                        satser.sats(skattPaaAlminneligInntekt_sats)
                    } else if (deltakerErPersonlig() && erBosattInnenforTiltakssonenINordTromsOgFinnmark()) {
                        satser.sats(skattPaaAlminneligInntekt_satsITiltakssone)
                    } else if (!deltakerErPersonlig() && erFinansskattepliktig()) {
                        satser.sats(skattPaaAlminneligInntekt_satsForFinansskattepliktigVirksomhet)
                    } else {
                        satser.sats(skattPaaAlminneligInntekt_sats)
                    }

                    if (sumFoerArbeidsgodtgjoerelseFelt.stoerreEnn(0)) {
                        settFelt(forekomstType.utdelingMv_skattPaaDeltakersAndelAvSelskapetsOverskudd) {
                            (sumFoerArbeidsgodtgjoerelseFelt * skattesats).somHeltall()
                        }
                    }
                }
            }
        }

        internal val samletPositivUtdelingEllerUegentligInnskuddKalkyle = kalkyle {
            hvis(!erNokus()) {
                val satser = satser!!
                val inntektsaar = inntektsaar
                forekomsterAv(deltaker) forHverForekomst {
                    val uegentligInnskuddEllerSamletPositivUtdeling = if (inntektsaar.tekniskInntektsaar >= 2024) {
                        if (forekomstType.erOmfattetAvRederiskatteordning.erSann()) {
                            ((forekomstType.utdelingMv_kontantUtbetaling +
                                    forekomstType.utdelingMv_verdIAvEiendelOgTjenesteOverfoertTilDeltaker -
                                    forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital medMinimumsverdi 0) -
                                    (forekomstType.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvFinansinntekt
                                        * satser.sats(skattPaaAlminneligInntekt_sats)).somHeltall())
                        } else {
                            ((forekomstType.utdelingMv_kontantUtbetaling +
                                 forekomstType.utdelingMv_verdIAvEiendelOgTjenesteOverfoertTilDeltaker -
                                 forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital medMinimumsverdi 0) -
                                 forekomstType.utdelingMv_skattPaaDeltakersAndelAvSelskapetsOverskudd).somHeltall()
                        }

                    } else {
                        (forekomstType.utdelingMv_kontantUtbetaling +
                            forekomstType.utdelingMv_verdIAvEiendelOgTjenesteOverfoertTilDeltaker -
                            forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital -
                            forekomstType.utdelingMv_skattPaaDeltakersAndelAvSelskapetsOverskudd).somHeltall()
                    }

                    if (uegentligInnskuddEllerSamletPositivUtdeling stoerreEllerLik 0) {
                        settFelt(forekomstType.utdelingMv_samletPositivUtdeling) { uegentligInnskuddEllerSamletPositivUtdeling }
                    } else {
                        settFelt(forekomstType.utdelingMv_uegentligInnskudd) { uegentligInnskuddEllerSamletPositivUtdeling.absoluttverdi() }
                    }
                }
            }
        }
    }

    object Skjermingsfradrag {
        internal val inngangsverdiJustertForInnskuddErvervRealisasjonMvKalkyle = kalkyle {
            val inntektsaar = inntektsaar
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    val tilbakebetalingAvInnbetaltKapital = if (inntektsaar.tekniskInntektsaar >= 2024) {
                        forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital
                    } else {
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_inngangsverdi
                    }
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_inngangsverdiJustertForInnskuddErvervRealisasjonMv) {
                        (forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_inngangsverdi -
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_realisasjonAvAndel_inngangsverdi +
                            forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_innskudd_inngangsverdi -
                            tilbakebetalingAvInnbetaltKapital).somHeltall()
                    }
                }
            }
        }

        internal val skjermingsgrunnlagKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig() && forekomstType.selskapsandelIProsent stoerreEnn 0) {
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsgrunnlag) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_inngangsverdiJustertForInnskuddErvervRealisasjonMv +
                            forekomstType.skjermingsfradragForPersonligDeltaker_ubenyttetSkjermingsfradragFraTidligereAar -
                            forekomstType.skjermingsfradragForPersonligDeltaker_anvendtSkjermingsfradragVedRealisasjonMv +
                            forekomstType.skjermingsfradragForPersonligDeltaker_annetTilleggIInngangsverdi -
                            forekomstType.skjermingsfradragForPersonligDeltaker_annenReduksjonIInngangsverdi).somHeltall()
                    }
                }
            }
        }

        internal val aaretsSkjermingsfradragKalkyle = kalkyle {
            val satser = satser!!
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    val aaretsSkjermingsfradrag =
                        forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsgrunnlag * satser.sats(
                            skjermingsrenteForPersonligeAksjonaererOgDeltakereIAnsvarligSelskapMv
                        )
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_aaretsSkjermingsfradrag) {
                        (aaretsSkjermingsfradrag medMinimumsverdi 0).somHeltall()
                    }
                }
            }
        }

        internal val korrigertUbenyttetSkjermingsfradragFraTidlgereAarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_korrigertUbenyttetSkjermingsfradragFraTidligereAar) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_ubenyttetSkjermingsfradragFraTidligereAar +
                            forekomstType.skjermingsfradragForPersonligDeltaker_tilleggTilSkjermingsfradragFraArvEllerGave -
                            forekomstType.skjermingsfradragForPersonligDeltaker_reduksjonISkjermingsfradragFraArvEllerGave +
                            forekomstType.skjermingsfradragForPersonligDeltaker_overfoertUbenyttetSkjermingsfradragFraTidligereAarFraAnnetSDF -
                            forekomstType.skjermingsfradragForPersonligDeltaker_overfoertUbenyttetSkjermingsfradragFraTidligereAarTilAnnetSDF).somHeltall()
                    }
                }
            }
        }

        internal val skjermingsfradragTilAnvendelseOgFremfoeringKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilAnvendelseOgFremfoering) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_korrigertUbenyttetSkjermingsfradragFraTidligereAar +
                            forekomstType.skjermingsfradragForPersonligDeltaker_aaretsSkjermingsfradrag +
                            forekomstType.skjermingsfradragForPersonligDeltaker_overfoertUbenyttetSkjermingsfradragIInntektsaarFraAnnetSDF -
                            forekomstType.skjermingsfradragForPersonligDeltaker_overfoertUbenyttetSkjermingsfradragIInntektsaarTilAnnetSDF +
                            forekomstType.skjermingsfradragForPersonligDeltaker_annetTilleggTilSkjermingsfradrag -
                            forekomstType.skjermingsfradragForPersonligDeltaker_annenReduksjonISkjermingsfradrag).somHeltall()
                    }
                }
            }
        }

        internal val anvendtSkjermingsfradragIInntektsaarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    val verdi = if (
                        forekomstType.utdelingMv_samletPositivUtdeling
                            .mindreEnn(forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilAnvendelseOgFremfoering.tall())
                    ) {
                        forekomstType.utdelingMv_samletPositivUtdeling.tall()
                    } else {
                        forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilAnvendelseOgFremfoering.tall()
                    }

                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_anvendtSkjermingsfradragIInntektsaar) {
                        verdi.medMinimumsverdi(0)
                    }
                }
            }
        }

        internal val skjermingsfradragTilFremfoeringFoerMotregningMotGevinstKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilFremfoeringFoerMotregningMotGevinst) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilAnvendelseOgFremfoering -
                            forekomstType.skjermingsfradragForPersonligDeltaker_anvendtSkjermingsfradragIInntektsaar).somHeltall()
                    }
                }
            }
        }

        internal val skjermingsfradragTilFremfoeringTilNesteInntektsaarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(deltakerErPersonlig()) {
                    val sumUbenyttetSkjermingsfradrag =
                        forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) summerVerdiFraHverForekomst {
                            forekomstType.ubenyttetSkjermingsfradrag.tall()
                        }
                    settFelt(forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilFremfoeringTilNesteInntektsaar) {
                        (forekomstType.skjermingsfradragForPersonligDeltaker_skjermingsfradragTilFremfoeringFoerMotregningMotGevinst -
                            sumUbenyttetSkjermingsfradrag).somHeltall()
                    }
                }
            }
        }
    }

    internal val tilleggIAlminneligInntektKalkyle = kalkyle {
        hvis(!erNokus()) {
            forekomsterAv(deltaker) der { forekomstType.deltakerensNorskePersonidentifikator.harVerdi() } forHverForekomst {
                val tilleggIAlminneligInntekt = forekomstType.utdelingMv_samletPositivUtdeling +
                    forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_annetTillegg -
                    forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_annenReduksjon -
                    forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_begrensningKnyttetTilUtenlandsforhold -
                    forekomstType.skjermingsfradragForPersonligDeltaker_anvendtSkjermingsfradragIInntektsaar

                settFelt(forekomstType.tilleggIAlminneligInntekt) {
                    tilleggIAlminneligInntekt.medMinimumsverdi(0).somHeltall()
                }
            }
        }
    }

    object ForholdKnyttetTilKommandittistOgStilleDeltaker {
        val fremfoerbartUnderskuddKalkyle = kalkyle {
            forekomsterAv(deltaker) der { forekomstType.erOmfattetAvRederiskatteordning.erUsann() } forHverForekomst {
                val samletUnderskuddSomKanFremfoeres =
                    if (sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt mindreEnn 0) {
                        forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_fremfoertUnderskuddFraTidligereAar +
                            sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerFelt.tall().absoluttverdi()
                    } else {
                        forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_fremfoertUnderskuddFraTidligereAar.tall()
                    }

                val underskuddSomMotregnesUnderhandsakkord =
                    if (
                        forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_oppnaaddUnderhaandsakkordOgGjeldsettergivelse stoerreEllerLik
                        samletUnderskuddSomKanFremfoeres
                    ) {
                        samletUnderskuddSomKanFremfoeres
                    } else {
                        forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_oppnaaddUnderhaandsakkordOgGjeldsettergivelse.tall()
                    }

                val underskuddFoerAnvendelse = samletUnderskuddSomKanFremfoeres - underskuddSomMotregnesUnderhandsakkord

                hvis(forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_erKommandittistEllerStilleDeltakerMedBeloepsbegrensetAnsvar.erSann()) {
                    settFelt(forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_fremfoerbartUnderskudd) {
                        underskuddFoerAnvendelse -
                            forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                            forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_skattepliktigGevinstVedRealisasjonAvAndel
                    }
                }
            }
        }
    }

    object UnderskuddTilFremfoeringINokus {

        internal val grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddKalkyle = kalkyle {
            hvis(erNokus()) {
                forekomsterAv(deltaker) forHverForekomst {
                    var grunnlagForBeregningAvAaretsAnvendelseAvUnderskudd =
                        forekomstType.andelAvSamletInntekt -
                            forekomstType.andelAvSamletUnderskudd

                    hvis(deltakerErUpersonligEllerSdf()) {
                        grunnlagForBeregningAvAaretsAnvendelseAvUnderskudd += forekomstType.korreksjonIAlminneligInntekt_utbetaltOverskuddMvTilUpersonligDeltakerFraMidlerOpptjentFoerSelskapBleNokus
                    }

                    settFelt(grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt) {
                        grunnlagForBeregningAvAaretsAnvendelseAvUnderskudd
                    }
                }
            }
        }

        internal val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarPer31122002Kalkyle = kalkyle {
            hvis(erNokus()) {
                forekomsterAv(deltaker) forHverForekomst {
                    if (grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt.stoerreEllerLik(forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoertUnderskuddFraTidligereAar.tall())) {
                        settFelt(forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoertUnderskuddFraTidligereAar.tall()
                        }
                    } else if (grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt.stoerreEnn(0) &&
                        grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt.mindreEnn(forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoertUnderskuddFraTidligereAar.tall())
                    ) {
                        settFelt(forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                            grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt.tall()
                        }
                    }
                }
            }
        }

        internal val realisertAndelAvUnderskuddPer31122002Kalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val fremfoerbartUnderskuddFoer2002 =
                    forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoertUnderskuddFraTidligereAar -
                        forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
                hvis(fremfoerbartUnderskuddFoer2002.harVerdi()) {
                    val selskapsandelIProsent = forekomstType.selskapsandelIProsent.prosent()
                    forekomsterAv(forekomstType.realisasjonOgAnnenOverdragelseAvAndel) forHverForekomst {
                        settFelt(realisertAndelAvUnderskuddPer31122002) {
                            (fremfoerbartUnderskuddFoer2002 *
                                forekomstType.realisertAndel.prosent() /
                                selskapsandelIProsent).somHeltall()
                        }
                    }
                }
            }
        }

        internal val underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002Kalkyle = kalkyle {
            hvis(erNokus()) {
                forekomsterAv(deltaker) forHverForekomst {
                    val sumRealisertAndelAvUnderskuddPer31122002 =
                        forekomsterAv(deltaker.realisasjonOgAnnenOverdragelseAvAndel) summerVerdiFraHverForekomst {
                            realisertAndelAvUnderskuddPer31122002.tall()
                        }
                    settFelt(forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoerbartUnderskudd) {
                        forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_fremfoertUnderskuddFraTidligereAar -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar -
                            sumRealisertAndelAvUnderskuddPer31122002
                    }
                }
            }
        }

        internal val aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle = kalkyle {
            hvis(erNokus()) {
                forekomsterAv(deltaker) forHverForekomst {
                    if (
                        (grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar)
                            .stoerreEllerLik(forekomstType.underskuddTilFremfoeringINokus_fremfoertUnderskuddFraTidligereAar.tall())
                    ) {
                        settFelt(forekomstType.underskuddTilFremfoeringINokus_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                            forekomstType.underskuddTilFremfoeringINokus_fremfoertUnderskuddFraTidligereAar.tall()
                        }
                    } else if (
                        (grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar).stoerreEnn(
                            0
                        )
                        && (grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt -
                            forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar).mindreEnn(
                            forekomstType.underskuddTilFremfoeringINokus_fremfoertUnderskuddFraTidligereAar.tall()
                        )
                    ) {
                        settFelt(forekomstType.underskuddTilFremfoeringINokus_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar) {
                            grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddFelt.tall() -
                                forekomstType.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
                        }
                    }
                }
            }
        }

        internal val fremfoerbartUnderskuddKalkyle = kalkyle {
            hvis(erNokus()) {
                forekomsterAv(deltaker) forHverForekomst {
                    settFelt(forekomstType.underskuddTilFremfoeringINokus_fremfoerbartUnderskudd) {
                        forekomstType.underskuddTilFremfoeringINokus_fremfoertUnderskuddFraTidligereAar -
                            forekomstType.underskuddTilFremfoeringINokus_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar +
                            forekomstType.andelAvSamletUnderskudd
                    }
                }
            }
        }
    }

    object SpesifikasjonAvSkattemessigInngangsverdiINokus {

        internal val endringISelskapetsSkattlagteKapitalKalkyle = kalkyle {

            hvis(erNokus()) {
                val inntektsaar = inntektsaar
                forekomsterAv(deltaker) forHverForekomst {
                    hvis(
                        (inntektsaar.tekniskInntektsaar <= 2023 && deltakerErUpersonligEllerSdf()) ||
                            (inntektsaar.tekniskInntektsaar >= 2024 && deltakerErUpersonlig())
                    ) {
                        settFelt(forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_endringISelskapetsSkattlagteKapital) {
                            forekomstType.alminneligInntekt -
                                forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_selskapsskattBetaltAvNokus -
                                forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_andelAvAvsattUtbytte -
                                forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_andelAvEkstraordinaertUtbytte +
                                forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_annenPositivEndring -
                                forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_annenNegativEndring
                        }
                    }
                }
            }
        }

        internal val regulertInngangsverdiPerAksjeKalkyle = kalkyle {

            hvis(erNokus()) {
                val inntektsaar = inntektsaar
                val antallDesimaler = if (inntektsaar.tekniskInntektsaar <= 2023) 0 else 2
                forekomsterAv(deltaker) forHverForekomst {
                    hvis(
                        ((inntektsaar.tekniskInntektsaar <= 2023 && deltakerErUpersonligEllerSdf()) ||
                            (inntektsaar.tekniskInntektsaar >= 2024 && deltakerErUpersonlig())) &&
                            forekomstType.direkteEidEllerKontrollertAksjeEllerAndelINokus_antallAksjeEllerAndel.stoerreEnn(
                                0
                            )
                    ) {
                        settFelt(forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_regulertInngangsverdiPerAksje) {
                            (forekomstType.regulertInngangsverdiPerAksjeINokusForUpersonligDeltaker_endringISelskapetsSkattlagteKapital / forekomstType.direkteEidEllerKontrollertAksjeEllerAndelINokus_antallAksjeEllerAndel).medAntallDesimaler(
                                antallDesimaler
                            )
                        }
                    }
                }
            }
        }
    }

    internal val havbruk = kalkyle {
        val positivGrunnrenteinntektFoerSamordning =
            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_positivGrunnrenteinntektFoerSamordning.tall()
        val negativGrunnrenteinntektFoerSamordning =
            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_negativGrunnrenteinntektFoerSamordning.tall()
        val egenProduksjonsavgift =
            modell.havbruksvirksomhet.beregnetGrunnrenteskatt_produksjonsavgiftTilFradragIGrunnrenteskatt_egenProduksjonsavgift.tall()

        forekomsterAv(deltaker) forHverForekomst {
            val deltakersAndelAvInntektIProsent = deltakersAndelAvInntektIProsent()

            hvis(deltakersAndelAvInntektIProsent != null) {
                settFelt(forekomstType.deltakersAndelAvFormueOgInntekt_positivGrunnrenteinntektFoerSamordningHavbruksvirksomhet) {
                    beregnHvis(forekomstType.deltakersAndelAvFormueOgInntekt_positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag.harVerdi()) {
                        (forekomstType.deltakersAndelAvFormueOgInntekt_positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag * deltakersAndelAvInntektIProsent).somHeltall()
                    }
                    beregnHvis(forekomstType.deltakersAndelAvFormueOgInntekt_positivGrunnrenteinntektForAnsvarligSelskapFoerSamordningEtterBunnfradrag.harIkkeVerdi()) {
                        (positivGrunnrenteinntektFoerSamordning * deltakersAndelAvInntektIProsent).somHeltall()
                    }
                }

                settFelt(forekomstType.deltakersAndelAvFormueOgInntekt_negativGrunnrenteinntektFoerSamordningHavbruksvirksomhet) { (negativGrunnrenteinntektFoerSamordning * deltakersAndelAvInntektIProsent).somHeltall() }

                settFelt(forekomstType.deltakersAndelAvFormueOgInntekt_produksjonsavgiftHavbruksvirksomhet) { (egenProduksjonsavgift * deltakersAndelAvInntektIProsent).somHeltall() }
            }
        }
    }

    internal val kraftverk = kalkyle {
        val positivGrunnrenteinntektFoerSamordning =
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.positivGrunnrenteinntektFoerSamordning.tall()
            }

        val negativGrunnrenteinntektFoerSamordning =
            forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) summerVerdiFraHverForekomst {
                forekomstType.negativGrunnrenteinntektFoerSamordning.tall()
            }

        val grunnlagForNaturressursskattPerKommune = mutableMapOf<String, BigDecimal?>()
        forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) forHverForekomst {
            forekomsterAv(forekomstType.grunnlagForNaturressursskattPerKommune) forHverForekomst {
                val kommune = forekomstType.kommunenummer.verdi()
                val grunnlag = forekomstType.grunnlag.tall()
                if (kommune != null && grunnlag != null) {
                    if (grunnlagForNaturressursskattPerKommune[kommune] != null) {
                        grunnlagForNaturressursskattPerKommune[kommune] += grunnlag
                    } else {
                        grunnlagForNaturressursskattPerKommune[kommune] = grunnlag
                    }
                }
            }
        }

        val eiendomsskattegrunnlagPerKraftverk = mutableMapOf<String, Map<Felt<*>, String>>()
        forekomsterAv(modell.kraftverk_spesifikasjonPerKraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            val kraftverketsNavn = forekomstType.kraftverketsNavn.verdi()
            val eiendomsskattegrunnlag = forekomstType.eiendomsskattegrunnlag.verdi()

            if (loepenummer != null && kraftverketsNavn != null && eiendomsskattegrunnlag != null) {
                eiendomsskattegrunnlagPerKraftverk[loepenummer] = mapOf(
                    deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.kraftverketsNavn to kraftverketsNavn,
                    deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.eiendomsskattegrunnlag to eiendomsskattegrunnlag,
                )
            }
        }

        forekomsterAv(deltaker) forHverForekomst {
            val deltakersAndelAvInntektIProsent = deltakersAndelAvInntektIProsent()

            settFelt(deltaker.deltakersAndelAvFormueOgInntekt_positivGrunnrenteinntektFoerSamordning) {
                (positivGrunnrenteinntektFoerSamordning * deltakersAndelAvInntektIProsent).somHeltall()
            }
            settFelt(deltaker.deltakersAndelAvFormueOgInntekt_negativGrunnrenteinntektFoerSamordning) {
                (negativGrunnrenteinntektFoerSamordning * deltakersAndelAvInntektIProsent).somHeltall()
            }

            grunnlagForNaturressursskattPerKommune.map {
                opprettNySubforekomstAv(deltaker.deltakersAndelAvFormueOgInntekt_grunnlagForNaturressursskattPerKommune) {
                    medId(it.key)
                    medFelt(
                        noekkel = deltaker.deltakersAndelAvFormueOgInntekt_grunnlagForNaturressursskattPerKommune.kommunenummer,
                        verdi = it.key
                    )
                    medFelt(deltaker.deltakersAndelAvFormueOgInntekt_grunnlagForNaturressursskattPerKommune.grunnlag) {
                        grunnlagForNaturressursskattPerKommune[it.key] * deltakersAndelAvInntektIProsent
                    }
                }
            }

            eiendomsskattegrunnlagPerKraftverk.map {
                opprettNySubforekomstAv(deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk) {
                    medId(it.key)
                    medFelt(
                        noekkel = deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.loepenummer,
                        verdi = it.key
                    )
                    medFelt(
                        noekkel = deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.kraftverketsNavn,
                        verdi = it.value[deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.kraftverketsNavn]
                    )

                    medFeltSomHeltall(
                        deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.eiendomsskattegrunnlag,
                        it.value[deltaker.deltakersAndelAvFormueOgInntekt_deltakersAndelAvEiendomsskattegrunnlagPerKraftverk.eiendomsskattegrunnlag]?.toBigDecimal() * deltakersAndelAvInntektIProsent
                    )
                }
            }
        }
    }

    internal val skattefunn = kalkyle {
        val samletSkattefradrag =
            modell.spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling.samletSkattefradrag.tall()
        val samletTilleggIBeregnetSkatt =
            modell.spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling.samletTilleggIBeregnetSkatt.tall()

        forekomsterAv(deltaker) forHverForekomst {
            val deltakersAndelAvInntektIProsent = deltakersAndelAvInntektIProsent()
            settFelt(deltaker.deltakersAndelAvFormueOgInntekt_samletSkattefradragFou) {
                (samletSkattefradrag * deltakersAndelAvInntektIProsent).somHeltall()
            }

            settFelt(deltaker.deltakersAndelAvFormueOgInntekt_samletTilleggIBeregnetSkattFou) {
                (samletTilleggIBeregnetSkatt * deltakersAndelAvInntektIProsent).somHeltall()
            }
        }
    }

    internal val rederi = kalkyle {
        val samletTonnasjeskatt = modell.rederiskatteordning_tonnasjeskatt.samletTonnasjeskatt.tall()
        val samletFinansinntekt = modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansinntekt.tall()
        val samletFinansunderskudd =
            modell.rederiskatteordning_finansinntektOgFinansunderskudd.samletFinansunderskudd.tall()

        forekomsterAv(deltaker) der { forekomstType.erOmfattetAvRederiskatteordning.erSann() } forHverForekomst {
            val deltakersAndelAvInntektIProsent = deltakersAndelAvInntektIProsent()

            settFelt(deltaker.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvTonnasjeskatt) {
                (samletTonnasjeskatt * deltakersAndelAvInntektIProsent).somHeltall()
            }

            settFelt(deltaker.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvFinansinntekt) {
                (samletFinansinntekt * deltakersAndelAvInntektIProsent).somHeltall()
            }

            settFelt(deltaker.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvFinansUnderskudd) {
                (samletFinansunderskudd * deltakersAndelAvInntektIProsent).somHeltall()
            }

            settFelt(deltaker.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenTilFremfoering) {
                forekomstType.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_aaretsFremfoerteDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttreden -
                    forekomstType.deltakersAndelAvInntektOmfattetAvRederiskatteordningen_andelAvDifferanseMellomVirkeligVerdiOgSkattemessigVerdiVedUttredenBenyttetIInntektsaaret
            }
        }
    }

    internal val landbasertVindkraft = kalkyle {
        val spesifikasjonPerVindkraftverk = mutableMapOf<String, Map<Felt<*>, String?>>()
        forekomsterAv(modell.spesifikasjonPerVindkraftverk) forHverForekomst {
            val loepenummer = forekomstType.loepenummer.verdi()
            if (loepenummer != null) {
                spesifikasjonPerVindkraftverk[loepenummer] = mapOf(
                    modell.spesifikasjonPerVindkraftverk.kraftverketsNavn to forekomstType.kraftverketsNavn.verdi(),
                    modell.spesifikasjonPerVindkraftverk.positivGrunnrenteinntektFoerSamordning to forekomstType.positivGrunnrenteinntektFoerSamordning.verdi(),
                    modell.spesifikasjonPerVindkraftverk.negativGrunnrenteinntektFoerSamordning to forekomstType.negativGrunnrenteinntektFoerSamordning.verdi(),
                    modell.spesifikasjonPerVindkraftverk.negativGrunnrenteskattVedDriftssettelse to forekomstType.negativGrunnrenteskattVedDriftssettelse.verdi(),
                    modell.spesifikasjonPerVindkraftverk.negativGrunnrenteskattVedOpphoer to forekomstType.negativGrunnrenteskattVedOpphoer.verdi(),
                )
            }
        }

        forekomsterAv(deltaker) forHverForekomst {
            val deltakersAndelAvInntektIProsent = deltakersAndelAvInntektIProsent()
            val deltakerForekomst = forekomstType
            spesifikasjonPerVindkraftverk.forEach {
                opprettNySubforekomstAv(deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft) {
                    medId(it.key)
                    medFelt(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.loepenummer,
                        it.key
                    )
                    medFelt(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.kraftverketsNavn,
                        it.value[modell.spesifikasjonPerVindkraftverk.kraftverketsNavn]
                    )
                    medFeltSomHeltall(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.andelAvPositivGrunnrenteinntektFoerSamordning,
                        it.value[modell.spesifikasjonPerVindkraftverk.positivGrunnrenteinntektFoerSamordning]?.toBigDecimal() * deltakersAndelAvInntektIProsent
                    )
                    medFeltSomHeltall(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.andelAvNegativGrunnrenteinntektFoerSamordning,
                        it.value[modell.spesifikasjonPerVindkraftverk.negativGrunnrenteinntektFoerSamordning]?.toBigDecimal() * deltakersAndelAvInntektIProsent
                    )
                    medFeltSomHeltall(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.andelAvNegativGrunnrenteskattVedDriftssettelse,
                        it.value[modell.spesifikasjonPerVindkraftverk.negativGrunnrenteskattVedDriftssettelse]?.toBigDecimal() * deltakersAndelAvInntektIProsent
                    )
                    medFeltSomHeltall(
                        deltakerForekomst.deltakersAndelAvFormueOgInntekt_deltakersAndelILandbasertVindkraft.andelAvNegativGrunnrenteskattVedOpphoer,
                        it.value[modell.spesifikasjonPerVindkraftverk.negativGrunnrenteskattVedOpphoer]?.toBigDecimal() * deltakersAndelAvInntektIProsent
                    )
                }
            }
        }
    }

    private fun ForekomstKontekst<v4.deltakerForekomst>.deltakersAndelAvInntektIProsent() =
        forekomstType.deltakersAndelAvInntektIProsent.prosent() ?: forekomstType.selskapsandelIProsent.prosent()

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            UnderskuddTilFremfoeringINokus.grunnlagForBeregningAvAaretsAnvendelseAvUnderskuddKalkyle,
            UnderskuddTilFremfoeringINokus.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarPer31122002Kalkyle,
            UnderskuddTilFremfoeringINokus.realisertAndelAvUnderskuddPer31122002Kalkyle,
            UnderskuddTilFremfoeringINokus.underskuddTilFremfoeringKnyttetTilAksjeEllerAndelINokusEidPer31122002Kalkyle,
            UnderskuddTilFremfoeringINokus.aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAarKalkyle,
            UnderskuddTilFremfoeringINokus.fremfoerbartUnderskuddKalkyle,
            KorreksjonIAlminneligInntekt.sumFoerFremfoeringAvUnderskuddForKommandittistOgStilleDeltakerKalkyle,
            KorreksjonIAlminneligInntekt.sumFoerArbeidsgodtgjoerelseKalkyle,
            KorreksjonIAlminneligInntekt.alminneligInntektEllerUnderskuddKalkyle,
            rederi,
            UtdelingMv.skattPaaDeltakersAndelAvSelskapetsOverskuddKalkyle,
            UtdelingMv.samletPositivUtdelingEllerUegentligInnskuddKalkyle,
            Skjermingsfradrag.inngangsverdiJustertForInnskuddErvervRealisasjonMvKalkyle,
            Skjermingsfradrag.skjermingsgrunnlagKalkyle,
            Skjermingsfradrag.aaretsSkjermingsfradragKalkyle,
            Skjermingsfradrag.korrigertUbenyttetSkjermingsfradragFraTidlgereAarKalkyle,
            Skjermingsfradrag.skjermingsfradragTilAnvendelseOgFremfoeringKalkyle,
            Skjermingsfradrag.anvendtSkjermingsfradragIInntektsaarKalkyle,
            Skjermingsfradrag.skjermingsfradragTilFremfoeringFoerMotregningMotGevinstKalkyle,
            Skjermingsfradrag.skjermingsfradragTilFremfoeringTilNesteInntektsaarKalkyle,
            tilleggIAlminneligInntektKalkyle,
            AjourholdAvOverEllerUnderprisForUpersonligDeltakerKalkyler.akkumulertPrisKalkyle,
            ForholdKnyttetTilKommandittistOgStilleDeltaker.fremfoerbartUnderskuddKalkyle,
            SpesifikasjonAvSkattemessigInngangsverdiINokus.endringISelskapetsSkattlagteKapitalKalkyle,
            SpesifikasjonAvSkattemessigInngangsverdiINokus.regulertInngangsverdiPerAksjeKalkyle,
            havbruk,
            kraftverk,
            skattefunn,
            landbasertVindkraft
        )
    }
}
