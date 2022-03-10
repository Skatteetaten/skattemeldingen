package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.personinntekt

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.InlineKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Verdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.opprettGyldigeIdentifikatorer
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.skalBeregnePersoninntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.mapping.HarGeneriskModell
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell

object FordeltBeregnetIdentifikatorHaandtering : InlineKalkyle() {
    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        return if (skalBeregnePersoninntekt(gm)) {
            return Verdi(opprettGyldigeIdentifikatorer(gm))
        } else {
            Verdi(GeneriskModell.tom())
        }
    }
}