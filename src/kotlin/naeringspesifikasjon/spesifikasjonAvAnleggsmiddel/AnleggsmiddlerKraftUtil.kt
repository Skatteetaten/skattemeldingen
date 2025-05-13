package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel

import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.pow
import kotlin.reflect.full.memberProperties
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v4_2023.v4
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk.samletPaastempletMerkeytelseIKvaOverGrense
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

fun naaverdiAvFremtidigeUtskiftningskostnader(
    konsumprisindeksjustertInvesteringskostnad: BigDecimal?,
    kapitaliseringsrente: Double,
    avskrivningstid: BigDecimal?,
    gjenstaaendeLevetid: BigDecimal?
): BigDecimal? {
    if (gjenstaaendeLevetid == null || konsumprisindeksjustertInvesteringskostnad == null ||
        avskrivningstid == null || gjenstaaendeLevetid > avskrivningstid
    ) {
        return null
    }

    return (konsumprisindeksjustertInvesteringskostnad * (1 + kapitaliseringsrente).pow((avskrivningstid - gjenstaaendeLevetid).toInt())
        .toBigDecimal() /
        ((1 + kapitaliseringsrente).pow(avskrivningstid.toInt()) - 1).toBigDecimal())
}

fun GeneriskModellKontekst.lagSpesifikasjonAvKraftverkMap(): Map<String, SpesifikasjonAvKraftverk> {
    val kraftverkMap = mutableMapOf<String, SpesifikasjonAvKraftverk>()

    forekomsterAv(modell.kraftverk_spesifikasjonAvKraftverk) forHverForekomst {
        forekomstType.loepenummer.verdi()?.let {
            kraftverkMap[it] = SpesifikasjonAvKraftverk(
                datoForOverdragelseVedErvervIInntektsaaret = forekomstType.datoForOverdragelseVedErvervIInntektsaaret.dato(),
                datoForOverdragelseVedRealisasjonIInntektsaaret = forekomstType.datoForOverdragelseVedRealisasjonIInntektsaaret.dato(),
                samletPaastempletMerkeytelseIKvaOverGrense = samletPaastempletMerkeytelseIKvaOverGrense()
            )
        }
    }

    return kraftverkMap
}

data class SpesifikasjonAvKraftverk(
    val datoForOverdragelseVedErvervIInntektsaaret: LocalDate?,
    val datoForOverdragelseVedRealisasjonIInntektsaaret: LocalDate?,
    val samletPaastempletMerkeytelseIKvaOverGrense: Boolean
)

fun LocalDate?.aar(): BigDecimal? = this?.year?.toBigDecimal()
