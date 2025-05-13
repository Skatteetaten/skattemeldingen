package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.virksomhetsTypeEnkeltpersonforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.erPetroleumsforetak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal object SpesifikasjonVedBeregningAvRentefradragsramme : HarKalkylesamling {

    internal val tilleggForSkattemessigAvskrivning = kalkyle("tilleggForSkattemessigAvskrivning") {
        hvis(!virksomhetsTypeEnkeltpersonforetak() && !erPetroleumsforetak()) {
            val saldoavskrevetAnleggsmiddelAaretsAvskrivning =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val saldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvNegativSaldo =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsInntektsfoeringAvNegativSaldo.tall()
                }
            val lineaertAvskrevetAnleggsmiddelAaretsAvskrivning =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            val kraftverkAaretsAvskrivning =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saerskiltAnleggsmiddelIKraftverk.anskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk) summerVerdiFraHverForekomst {
                    forekomstType.aaretsAvskrivning.tall()
                }
            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_spesifikasjonVedBeregningAvRentefradragsramme.tilleggForSkattemessigAvskrivning) {
                saldoavskrevetAnleggsmiddelAaretsAvskrivning -
                    saldoavskrevetAnleggsmiddelAaretsInntektsfoeringAvNegativSaldo +
                    lineaertAvskrevetAnleggsmiddelAaretsAvskrivning +
                    kraftverkAaretsAvskrivning
            }
        }
    }

    internal val direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel = kalkyle("direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel") {
        hvis(!virksomhetsTypeEnkeltpersonforetak() && !erPetroleumsforetak()) {
            val vederlagForSaldoavskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_saldoavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            val vederlagForLineaertAvskrevetAnleggsmiddel =
                forekomsterAv(modell.spesifikasjonAvAnleggsmiddel_lineaertavskrevetAnleggsmiddel) summerVerdiFraHverForekomst {
                    forekomstType.vederlagVedRealisasjonOgUttakInntektsfoertIAar.tall()
                }
            settUniktFelt(modell.spesifikasjonAvAnleggsmiddel_spesifikasjonVedBeregningAvRentefradragsramme.direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel) {
                vederlagForSaldoavskrevetAnleggsmiddel +
                    vederlagForLineaertAvskrevetAnleggsmiddel
            }
        }
    }

    val kalkyleSamling = Kalkylesamling(
        tilleggForSkattemessigAvskrivning,
        direkteInntektsfoertVederlagForAvskrevetAnleggsmiddel,
    )

    override fun kalkylesamling(): Kalkylesamling {
        return kalkyleSamling
    }
}
