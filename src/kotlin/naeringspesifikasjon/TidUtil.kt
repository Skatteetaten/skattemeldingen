package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.dagerMellomInkludertStartOgSluttBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.maanederMellomInkludertStartOgSluttBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.konstanter.Inntektsaar

fun dagerMellom(fraOgMed: LocalDate?, tilOgMed: LocalDate?): BigDecimal? {
    if (fraOgMed == null || tilOgMed == null) {
        return null
    }
    return BigDecimal(dagerMellomInkludertStartOgSluttBeregning(fraOgMed, tilOgMed))
}

fun maanederMellom(fraOgMed: LocalDate?, tilOgMed: LocalDate?): BigDecimal? {
    if (fraOgMed == null || tilOgMed == null) {
        return null
    }
    return BigDecimal(maanederMellomInkludertStartOgSluttBeregning(fraOgMed, tilOgMed))
}

fun dagerIAaretFoer(til: LocalDate): BigDecimal =
    BigDecimal(ChronoUnit.DAYS.between(LocalDate.of(til.year, Month.JANUARY, 1), til))

fun dagerEidIAnskaffelsesaaret(anskaffelsesdato: LocalDate?): BigDecimal? {
    if (anskaffelsesdato == null) return null
    return dagerMellom(
        anskaffelsesdato,
        LocalDate.of(anskaffelsesdato.year, Month.DECEMBER, 31)
    )
}

fun dagerEidIRealisasjonsaaret(realisasjonsdato: LocalDate?): BigDecimal? {
    if(realisasjonsdato == null) return null
    return dagerMellom(
        LocalDate.of(realisasjonsdato.year, Month.JANUARY, 1),
        realisasjonsdato
    )
}
fun dagerIkkeEidIAnskaffelsesaaret(anskaffelsesdato: LocalDate) = dagerIAaretFoer(anskaffelsesdato)

fun erSkuddAar(aar: Int): Boolean {
    return (aar % 4 == 0) && (aar % 100 != 0 || aar % 400 == 0)
}

fun antallDagerIAar(aar: Int) = if (erSkuddAar(aar)) {
    BigDecimal(366)
} else {
    BigDecimal(365)
}

fun antallDagerIAar(aar: Inntektsaar) = antallDagerIAar(aar.gjeldendeInntektsaar)