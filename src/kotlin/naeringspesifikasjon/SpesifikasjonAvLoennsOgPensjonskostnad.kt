package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

internal object SpesifikasjonAvLoennsOgPensjonskostnad : HarKalkylesamling {

    val samletOpplysningspliktigYtelsePerKontonummer = kalkyle("samletOpplysningspliktigYtelsePerKontonummer") {
        forAlleForekomsterAv(modell.spesifikasjonAvLoennsOgPensjonskostnad.loennsOgPensjonskostnad) {
            settFelt(forekomstType.samletOpplysningspliktigYtelse) {
                forekomstType.loennsopplysningspliktigYtelse +
                    forekomstType.tilleggForKostnadsfoertLoennMvTidligereAar -
                    forekomstType.fradragForPaaloeptIkkeForfaltLoennMvSomIkkeErRapportert
            }
        }
    }

    val samletOpplysningspliktigYtelseKalkyle = kalkyle("samletOpplysningspliktigYtelse") {
        settUniktFelt(modell.spesifikasjonAvLoennsOgPensjonskostnad.samletOpplysningspliktigYtelse) {
            forekomsterAv(modell.spesifikasjonAvLoennsOgPensjonskostnad.loennsOgPensjonskostnad) summerVerdiFraHverForekomst {
                forekomstType.samletOpplysningspliktigYtelse.tall()
            }
        }
    }

    val samletArbeidsgiveravgiftspliktigYtelseKalkyle = kalkyle("samletArbeidsgiveravgiftspliktigYtelse") {
        settUniktFelt(modell.spesifikasjonAvLoennsOgPensjonskostnad.samletArbeidsgiveravgiftspliktigYtelse) {
            forekomsterAv(modell.spesifikasjonAvLoennsOgPensjonskostnad.loennsOgPensjonskostnad) summerVerdiFraHverForekomst {
                forekomstType.arbeidsgiveravgiftspliktigYtelse +
                forekomstType.aaretsInnbetalingAvArbeidsgiveravgiftspliktigTilskuddOgPremieTilPensjonsordning
            }
        }
    }

    private val defaultKonsernbidragSkalBekreftesAvRevisor = kalkyle {
        hvis(harForekomsterAv(modell.spesifikasjonAvLoennsOgPensjonskostnad)
            && modell.spesifikasjonAvLoennsOgPensjonskostnad.skalBekreftesAvRevisor.harIkkeVerdi()
        ) {
            settUniktFelt(modell.spesifikasjonAvLoennsOgPensjonskostnad.skalBekreftesAvRevisor, "false")
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletOpplysningspliktigYtelsePerKontonummer,
            samletOpplysningspliktigYtelseKalkyle,
            samletArbeidsgiveravgiftspliktigYtelseKalkyle,
            defaultKonsernbidragSkalBekreftesAvRevisor
        )
    }
}