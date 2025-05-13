package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.maksAntallDesimaler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell


internal object OmsetningOgEtterbetalingMvISamvirkeforetakFra2024 : HarKalkylesamling {

    internal val samletOmsetningKjoepFraMedlemmerISamvirkeforetakOgOevrige = kalkyle("samletOmsetningKjoepFraMedlemmerISamvirkeforetakOgOevrige") {
        val virksomhetstypeVerdi = generiskModell.verdiFor(modell.virksomhet.virksomhetstype.key)
        hvis(virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode) {
            forekomsterAv(modell.andreForhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige) forHverForekomst {
                settFelt(forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_samletOmsetning) {
                    forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_omsetningMedMedlemIEgetSamvirkeforetak +
                            forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_omsetningMedMedlemIAnnetSamvirkeforetak +
                            forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_oevrigOmsetning
                }
            }
        }
    }

    internal val samletOmsetningSalgTilMedlemmerISamvirkeforetakOgOevrige = kalkyle("samletOmsetningSalgTilMedlemmerISamvirkeforetakOgOevrige") {
        val virksomhetstypeVerdi = generiskModell.verdiFor(modell.virksomhet.virksomhetstype.key)
        hvis(virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode) {
            forekomsterAv(modell.andreForhold_salgTilMedlemmerISamvirkeforetakOgOevrige) forHverForekomst {
                settFelt(forekomstType.samletOmsetning) {
                    forekomstType.omsetningMedMedlemIEgetSamvirkeforetak +
                            forekomstType.omsetningMedMedlemIAnnetSamvirkeforetak +
                            forekomstType.oevrigOmsetning
                }
            }
        }
    }

    internal val andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetakKjoepFraMedlemmerISamvirkeforetakOgOevrige =
        kalkyle("andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak") {
            val virksomhetstypeVerdi = generiskModell.verdiFor(modell.virksomhet.virksomhetstype.key)
            val forekomstType = modell.andreForhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige
            val maksAntallDesimaler = 10
            hvis(virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode) {
                forekomsterAv(forekomstType) forHverForekomst {
                    hvis(
                        forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_samletOmsetning.harVerdi()
                                && forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_samletOmsetning ulik 0
                    ) {
                        val verdi = (forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_omsetningMedMedlemIEgetSamvirkeforetak / forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_samletOmsetning) * 100
                        settFelt(forekomstType.andreforhold_kjoepFraMedlemmerISamvirkeforetakOgOevrige_andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak, maksAntallDesimaler) {
                            verdi?.maksAntallDesimaler(maksAntallDesimaler)
                        }
                    }
                }
            }
        }

    internal val andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetakSalgTilMedlemmerISamvirkeforetakOgOevrige =
        kalkyle("andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak") {
            val virksomhetstypeVerdi = generiskModell.verdiFor(modell.virksomhet.virksomhetstype.key)
            val forekomstType = modell.andreForhold_salgTilMedlemmerISamvirkeforetakOgOevrige
            val maksAntallDesimaler = 10
            hvis(virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode) {
                forekomsterAv(forekomstType) forHverForekomst {
                    hvis(
                        forekomstType.samletOmsetning.harVerdi()
                                && forekomstType.samletOmsetning ulik 0
                    ) {
                        val verdi = (forekomstType.omsetningMedMedlemIEgetSamvirkeforetak / forekomstType.samletOmsetning) * 100
                        settFelt(forekomstType.andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetak, maksAntallDesimaler) {
                            verdi?.maksAntallDesimaler(maksAntallDesimaler)
                        }
                    }
                }
            }
        }

    internal val fremfoerbarFradragsramme = kalkyle("fremfoerbarFradragsramme") {
        val virksomhetstypeVerdi = generiskModell.verdiFor(modell.virksomhet.virksomhetstype.key)
        hvis(virksomhetstypeVerdi == virksomhetstype.kode_samvirkeforetak.kode) {
            forekomsterAv(modell.andreForhold_fradragsrammeForEtterbetalingISamvirkeforetakTilFremfoering) forHverForekomst {
                settFelt(forekomstType.fremfoerbarFradragsramme) {
                    forekomstType.fremfoerbarFradragsrammeFraTidligereAar +
                        forekomstType.fremfoerbarFradragsrammeIInntektsaaret -
                        forekomstType.aaretsAnvendelseAvFremfoerbarFradragsramme
                }
            }
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            samletOmsetningKjoepFraMedlemmerISamvirkeforetakOgOevrige,
            samletOmsetningSalgTilMedlemmerISamvirkeforetakOgOevrige,
            andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetakKjoepFraMedlemmerISamvirkeforetakOgOevrige,
            andelAvOmsetningSomKommerFraMedlemIEgetSamvirkeforetakSalgTilMedlemmerISamvirkeforetakOgOevrige,
            fremfoerbarFradragsramme
        )
    }
}