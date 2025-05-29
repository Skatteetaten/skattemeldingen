package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v6_2025.v6

const val grenseverdiKva = 10000

fun ForekomstKontekst<v6.kraftverk_spesifikasjonAvKraftverkForekomst>.samletPaastempletMerkeytelseIKvaOverGrense() =
    forekomstType.samletPaastempletMerkeytelseIKva stoerreEllerLik grenseverdiKva

fun ForekomstKontekst<v6.kraftverk_spesifikasjonAvKraftverkForekomst>.samletPaastempletMerkeytelseIKvaUnderGrense() =
    forekomstType.samletPaastempletMerkeytelseIKva mindreEnn grenseverdiKva


