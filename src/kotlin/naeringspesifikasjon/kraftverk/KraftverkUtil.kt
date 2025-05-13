package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.ForekomstKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v5_2024.v5

const val grenseverdiKva = 10000

fun ForekomstKontekst<v5.kraftverk_spesifikasjonAvKraftverkForekomst>.samletPaastempletMerkeytelseIKvaOverGrense() =
    forekomstType.samletPaastempletMerkeytelseIKva stoerreEllerLik grenseverdiKva

fun ForekomstKontekst<v5.kraftverk_spesifikasjonAvKraftverkForekomst>.samletPaastempletMerkeytelseIKvaUnderGrense() =
    forekomstType.samletPaastempletMerkeytelseIKva mindreEnn grenseverdiKva


