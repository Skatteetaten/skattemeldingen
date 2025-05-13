package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellLesKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v2_2021.Regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal fun GeneriskModellLesKontekst.ingenEllerBegrensetRegnskapsplikt(): Boolean {
    val regnskapspliktstypeVerdi = modell.virksomhet.regnskapspliktstype.verdi()
    return regnskapspliktstypeVerdi == regnskapspliktstype.ingenRegnskapsplikt
        || regnskapspliktstypeVerdi == regnskapspliktstype.begrensetRegnskapsplikt
}

internal fun GeneriskModellLesKontekst.ingenRegnskapsplikt(): Boolean {
    return modell.virksomhet.regnskapspliktstype.verdi() == regnskapspliktstype.ingenRegnskapsplikt
}

internal fun GeneriskModellLesKontekst.fullRegnskapsplikt(): Boolean {
    return modell.virksomhet.regnskapspliktstype.verdi() == regnskapspliktstype.fullRegnskapsplikt
}

internal fun GeneriskModellLesKontekst.begrensetRegnskapsplikt(): Boolean {
    return modell.virksomhet.regnskapspliktstype.verdi() == regnskapspliktstype.begrensetRegnskapsplikt
}

internal fun GeneriskModellLesKontekst.regnskapspliktstype1Eller5(): Boolean {
    val regnskapspliktstypeVerdi = modell.virksomhet.regnskapspliktstype.verdi()
    return regnskapspliktstypeVerdi == Regnskapspliktstype.type_1
        || regnskapspliktstypeVerdi == Regnskapspliktstype.type_5
}