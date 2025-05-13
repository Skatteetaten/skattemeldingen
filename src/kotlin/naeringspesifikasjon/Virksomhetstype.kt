package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.GeneriskModellForKalkyler
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kontekster.GeneriskModellLesKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.mapping.tilgenerisk.MappingTilGenerisk.FIXED_COMPONENT_ID
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.NaeringFelterKunForVisning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell

internal fun GeneriskModellKontekst.gjelderBankOgForsikring(): Boolean =
    virksomhetsTypeBankOgFinansieringsforetak() ||
        virksomhetsTypeLivsforsikringsforetakOgPensjonskasse() ||
        virksomhetsTypeSkadeforsikringsforetak()

internal fun GeneriskModellLesKontekst.virksomhetsTypeBankOgFinansieringsforetak(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_bankOgFinansieringsforetak.kode
}

internal fun GeneriskModellLesKontekst.virksomhetsTypeLivsforsikringsforetakOgPensjonskasse(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_livsforsikringsforetakOgPensjonskasse.kode
}

internal fun GeneriskModellLesKontekst.virksomhetsTypeSkadeforsikringsforetak(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_skadeforsikringsforetak.kode
}

internal fun GeneriskModellLesKontekst.virksomhetsTypeNokus(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_nokus.kode
}

internal fun GeneriskModellLesKontekst.virksomhetsTypeOevrigSelskap(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_oevrigSelskap.kode
}

internal fun GeneriskModellKontekst.erPetroleumsforetak(): Boolean {
    val tidligereUtledet = generiskModell.verdiFor(NaeringFelterKunForVisning.erPetroleum)
    if (tidligereUtledet != null) {
        return tidligereUtledet == "true"
    }

    val erPetroleum = NaeringFelterKunForVisning.harPetroleumForekomsterINsp(generiskModell.alleInformasjonsElementer())

    leggTilIKontekst(
        GeneriskModellForKalkyler.fra(
            InformasjonsElement(
                NaeringFelterKunForVisning.erPetroleum,
                mapOf(modell.virksomhet.loevForekomstIdNoekkel to FIXED_COMPONENT_ID),
                erPetroleum
            )
        )
    )

    return erPetroleum
}

internal fun GeneriskModellKontekst.erKraftverk(): Boolean {
    val tidligereUtledet = generiskModell.verdiFor(NaeringFelterKunForVisning.erKraftverk)
    if (tidligereUtledet != null) {
        return tidligereUtledet == "true"
    }

    val erKraftverk = NaeringFelterKunForVisning.harKraftverkINsp(generiskModell.alleInformasjonsElementer())

    leggTilIKontekst(
        GeneriskModellForKalkyler.fra(
            InformasjonsElement(
                NaeringFelterKunForVisning.erKraftverk,
                mapOf(modell.virksomhet.loevForekomstIdNoekkel to FIXED_COMPONENT_ID),
                erKraftverk
            )
        )
    )

    return erKraftverk
}

internal fun GeneriskModellLesKontekst.virksomhetsTypeEnkeltpersonforetak(): Boolean {
    return modell.virksomhet.virksomhetstype.verdi() == virksomhetstype.kode_enkeltpersonforetak.kode
}

internal fun GeneriskModellKontekst.fullRegnskapspliktOgVirksomhetsTypePetroleumsforetak() =
    fullRegnskapsplikt() && erPetroleumsforetak()

internal fun GeneriskModellKontekst.fullRegnskapspliktOgLivsforsikringsforetakOgPensjonskasse() =
    fullRegnskapsplikt() && virksomhetsTypeLivsforsikringsforetakOgPensjonskasse()

