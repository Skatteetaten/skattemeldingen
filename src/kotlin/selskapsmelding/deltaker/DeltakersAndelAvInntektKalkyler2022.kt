package no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.kalkyler.deltaker

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.somHeltall
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.opprettSyntetiskFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_sats
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsForFinansskattepliktigVirksomhet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skattPaaAlminneligInntekt_satsITiltakssone
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Sats.skjermingsrenteForPersonligeAksjonaererOgDeltakereIAnsvarligSelskapMv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.deltakerErPersonlig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erBosattInnenforTiltakssonenINordTromsOgFinnmark
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.beregning.erFinansskattepliktig
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.selskapsmelding.sdf.modellV1

object DeltakersAndelAvInntektKalkyler2022 : HarKalkylesamling {

    private val deltaker = modellV1.deltaker

    val sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerFelt =
        opprettSyntetiskFelt(deltaker, "sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltaker")
    val sumFoerArbeidsgodtgjoerelseFelt = opprettSyntetiskFelt(deltaker, "sumFoerArbeidsgodtgjoerelse")

    object KorreksjonIAlminneligInntekt {
        internal val sumFoerArbeidsgodtgjoerelseKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val sumFoerArbeidsgodtgjoerelse = forekomstType.andelAvSamletInntekt -
                    forekomstType.andelAvSamletUnderskudd +
                    forekomstType.korreksjonIAlminneligInntekt_gevinstVedUttakFraNorskBeskatningsomraade -
                    forekomstType.korreksjonIAlminneligInntekt_tapVedUttakFraNorskBeskatningsomraade

                settFelt(sumFoerArbeidsgodtgjoerelseFelt) { sumFoerArbeidsgodtgjoerelse }
            }
        }

        internal val sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                hvis(forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_erKommandittistEllerStilleDeltakerMedBeloepsbegrensetAnsvar.erSann()) {
                    settFelt(sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerFelt) {
                        sumFoerArbeidsgodtgjoerelseFelt +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFiskeOgFangst +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFamiliebarnehageIDeltakersHjem +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenAnnenNaering
                    }
                }

            }
        }

        internal val alminneligInntektEllerUnderskuddKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val inntektEllerUnderskudd =
                    if (forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_erKommandittistEllerStilleDeltakerMedBeloepsbegrensetAnsvar.erUsann()) {
                        sumFoerArbeidsgodtgjoerelseFelt +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFiskeOgFangst +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenFamiliebarnehageIDeltakersHjem +
                            forekomstType.korreksjonIAlminneligInntekt_arbeidsgodtgjoerelseInnenAnnenNaering
                    } else {
                        sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerFelt.tall().medMinimumsverdi(0) -
                            forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar
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
            val satser = satser!!
            forekomsterAv(deltaker) forHverForekomst {
                val skattesats = if (deltakerErPersonlig() && !erBosattInnenforTiltakssonenINordTromsOgFinnmark(this@kalkyle.inntektsaar.tekniskInntektsaar)) {
                    satser.sats(skattPaaAlminneligInntekt_sats)
                } else if (deltakerErPersonlig() && erBosattInnenforTiltakssonenINordTromsOgFinnmark(this@kalkyle.inntektsaar.tekniskInntektsaar)) {
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

        internal val samletPositivUtdelingEllerUegentligInnskuddKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val uegentligInnskuddEllerSamletPositivUtdeling = (forekomstType.utdelingMv_kontantUtbetaling +
                    forekomstType.utdelingMv_verdIAvEiendelOgTjenesteOverfoertTilDeltaker -
                    forekomstType.utdelingMv_tilbakebetalingAvInnbetaltEgenkapital -
                    forekomstType.utdelingMv_skattPaaDeltakersAndelAvSelskapetsOverskudd).somHeltall()

                if (uegentligInnskuddEllerSamletPositivUtdeling stoerreEllerLik 0) {
                    settFelt(forekomstType.utdelingMv_samletPositivUtdeling) { uegentligInnskuddEllerSamletPositivUtdeling }
                } else {
                    settFelt(forekomstType.utdelingMv_uegentligInnskudd) { uegentligInnskuddEllerSamletPositivUtdeling.absoluttverdi() }
                }
            }
        }
    }

    object Skjermingsfradrag {
        internal val inngangsverdiJustertForInnskuddErvervRealisasjonMvKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_inngangsverdiJustertForInnskuddErvervRealisasjonMv) {
                    (forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_inngaaendeVerdi_inngangsverdi +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_kostprisVedErvervAvAndel_inngangsverdi -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_realisasjonAvAndel_inngangsverdi +
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_innskudd_inngangsverdi -
                        forekomstType.spesifikasjonAvSkattemessigInngangverdiOgEgenkapitalkonto_tilbakebetalingAvInnbetaltKapital_inngangsverdi).somHeltall()
                }
            }
        }

        internal val skjermingsgrunnlagKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_skjermingsgrunnlag) {
                    (forekomstType.skjermingsfradrag_inngangsverdiJustertForInnskuddErvervRealisasjonMv +
                        forekomstType.skjermingsfradrag_ubenyttetSkjermingsfradragFraTidligereAar -
                        forekomstType.skjermingsfradrag_anvendtSkjermingsfradragVedRealisasjonMv +
                        forekomstType.skjermingsfradrag_annetTilleggIInngangsverdi -
                        forekomstType.skjermingsfradrag_annenReduksjonIInngangsverdi).somHeltall()
                }
            }
        }

        internal val aaretsSkjermingsfradragKalkyle = kalkyle {
            val satser = satser!!
            forekomsterAv(deltaker) forHverForekomst {
                val aaretsSkjermingsfradrag = forekomstType.skjermingsfradrag_skjermingsgrunnlag * satser.sats(skjermingsrenteForPersonligeAksjonaererOgDeltakereIAnsvarligSelskapMv)
                settFelt(forekomstType.skjermingsfradrag_aaretsSkjermingsfradrag) {
                    (aaretsSkjermingsfradrag medMinimumsverdi 0).somHeltall()
                }
            }
        }

        internal val korrigertUbenyttetSkjermingsfradragFraTidlgereAarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_korrigertUbenyttetSkjermingsfradragFraTidligereAar) {
                    (forekomstType.skjermingsfradrag_ubenyttetSkjermingsfradragFraTidligereAar +
                        forekomstType.skjermingsfradrag_tilleggTilSkjermingsfradragFraArvEllerGave -
                        forekomstType.skjermingsfradrag_reduksjonISkjermingsfradragFraArvEllerGave +
                        forekomstType.skjermingsfradrag_overfoertUbenyttetSkjermingsfradragFraTidligereAarFraAnnetSDF -
                        forekomstType.skjermingsfradrag_overfoertUbenyttetSkjermingsfradragFraTidligereAarTilAnnetSDF).somHeltall()
                }
            }
        }

        internal val skjermingsfradragTilAnvendelseOgFremfoeringKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_skjermingsfradragTilAnvendelseOgFremfoering) {
                    (forekomstType.skjermingsfradrag_korrigertUbenyttetSkjermingsfradragFraTidligereAar +
                        forekomstType.skjermingsfradrag_aaretsSkjermingsfradrag +
                        forekomstType.skjermingsfradrag_overfoertUbenyttetSkjermingsfradragFraTidligereAarFraAnnetSDF -
                        forekomstType.skjermingsfradrag_overfoertUbenyttetSkjermingsfradragFraTidligereAarTilAnnetSDF +
                        forekomstType.skjermingsfradrag_annetTilleggTilSkjermingsfradrag -
                        forekomstType.skjermingsfradrag_annenReduksjonISkjermingsfradrag).somHeltall()
                }
            }
        }

        internal val anvendtSkjermingsfradragIInntektsaarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val verdi = if (
                    forekomstType.utdelingMv_samletPositivUtdeling
                        .mindreEnn(forekomstType.skjermingsfradrag_skjermingsfradragTilAnvendelseOgFremfoering.tall())
                ) {
                    forekomstType.utdelingMv_samletPositivUtdeling.tall()
                } else {
                    forekomstType.skjermingsfradrag_skjermingsfradragTilAnvendelseOgFremfoering.tall()
                }

                settFelt(forekomstType.skjermingsfradrag_anvendtSkjermingsfradragIInntektsaar) {
                    verdi.medMinimumsverdi(0)
                }
            }
        }

        internal val skjermingsfradragTilFremfoeringFoerMotregningMotGevinstKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_skjermingsfradragTilFremfoeringFoerMotregningMotGevinst) {
                    (forekomstType.skjermingsfradrag_skjermingsfradragTilAnvendelseOgFremfoering -
                        forekomstType.skjermingsfradrag_anvendtSkjermingsfradragIInntektsaar).somHeltall()
                }
            }
        }

        internal val skjermingsfradragTilFremfoeringTilNesteInntektsaarKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                settFelt(forekomstType.skjermingsfradrag_skjermingsfradragTilFremfoeringTilNesteInntektsaar) {
                    forekomstType.skjermingsfradrag_skjermingsfradragTilFremfoeringFoerMotregningMotGevinst
                        .tall().somHeltall()
                }
            }
        }
    }

    internal val tilleggIAlminneligInntektKalkyle = kalkyle {
        forekomsterAv(deltaker) forHverForekomst {
            val tilleggIAlminneligInntekt = forekomstType.utdelingMv_samletPositivUtdeling +
                forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_annetTillegg -
                forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_annenReduksjon -
                forekomstType.korreksjonITilleggIAlminneligInntektForPersonligDeltaker_begrensningKnyttetTilUtenlandsforhold -
                forekomstType.skjermingsfradrag_anvendtSkjermingsfradragIInntektsaar

            settFelt(forekomstType.tilleggIAlminneligInntekt) {
                tilleggIAlminneligInntekt.medMinimumsverdi(0).somHeltall()
            }
        }
    }

    object ForholdKnyttetTilKommandittistOgStilleDeltaker {
        val fremfoerbartUnderskuddKalkyle = kalkyle {
            forekomsterAv(deltaker) forHverForekomst {
                val samletUnderskuddSomKanFremfoeres =
                    if (sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerFelt mindreEnn 0) {
                        forekomstType.forholdKnyttetTilKommandittistOgStilleDeltaker_fremfoertUnderskuddFraTidligereAar +
                            sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerFelt.tall().absoluttverdi()
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

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            KorreksjonIAlminneligInntekt.sumFoerArbeidsgodtgjoerelseKalkyle,
            KorreksjonIAlminneligInntekt.sumFoerFremfoeringAvUnderskuddForKomandittistOgStilleDeltakerKalkyle,
            KorreksjonIAlminneligInntekt.alminneligInntektEllerUnderskuddKalkyle,
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
        )
    }
}