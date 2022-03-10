package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.avrundTilToDesimalerString
import java.math.BigDecimal
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.InlineKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Verdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.mapping.HarGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.aarsresultat
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.skattemessigResultat
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumEndringIMidlertidigForskjell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumFradragINaeringsinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.sumTilleggINaeringsinntekt
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.InformasjonsElement

object SkattemessigResultatBeregning : InlineKalkyle() {

    private val skattemessigResultatKalkyle =
        aarsresultat.beloep + sumTilleggINaeringsinntekt.beloep - sumFradragINaeringsinntekt.beloep +
            sumEndringIMidlertidigForskjell.beloep

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()

        val resultat: BigDecimal? = overstyrtVerdiHvisOverstyrt(gm, skattemessigResultat.beloep.key)
            ?: skattemessigResultatKalkyle.beregn(gm).normalisertVerdi()

        return Verdi(
            GeneriskModell.fra(
                InformasjonsElement(
                    skattemessigResultat.beloep.key,
                    mapOf(skattemessigResultat.gruppe to "fixed"),
                    resultat?.avrundTilToDesimalerString()
                )
            )
        )
    }
}