package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

internal object SaerskiltAnleggsmiddelIKraftverk2023 : HarKalkylesamling {

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk.aaretsAvskrivningKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.utgaaendeVerdiKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk.konsumprisindeksjustertInvesteringskostnadKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.gjenstaaendeLevetidKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverk.naaverdiAvFremtidigeUtskiftningskostnaderKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.aaretsFriinntektKalkyle,
            AnskaffelseAvEllerPaakostningPaaSaerskiltAnleggsmiddelIKraftverkFra2024.gjenstaaendeSkattemessigVerdiPaaRealisasjonstidspunktetKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.gevinstOgTapVedRealisasjonKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.grunnlagForAaretsInntektsfoeringOgInntektsfradragKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.inntektEllerInntektsfradragFraGevinstOgTapskontoKalkyle,
            GevinstOgTapskontoPerSaerskiltAnleggsmiddelIKraftverk.utgaaendeVerdiKalkyle,
            SaerskiltAnleggsmiddelIKraftverkFra2024.aaretsSamledeAvskrivningForSaerskiltAnleggsmiddelIKraftverk,
            SaerskiltAnleggsmiddelIKraftverkFra2024.utgaaendeVerdiForSaerskiltAnleggsmiddelIKraftverk,
            SaerskiltAnleggsmiddelIKraftverkFra2024.aaretsSamledeFriinntekt
        )
    }
}