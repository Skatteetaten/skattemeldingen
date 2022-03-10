package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.InlineKalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.domene.dsl.kalkyle.Verdi
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.mapping.HarGeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.v2_2021.overnattingsOgServeringssted
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.GeneriskModell
import ske.fastsetting.formueinntekt.skattemelding.core.generiskmapping.jsonobjekter.InformasjonsElement

object OvernattingsOgServeringsstedBeregning : InlineKalkyle() {

    override fun kalkulertPaa(naeringsopplysninger: HarGeneriskModell): Verdi {
        val gm = naeringsopplysninger.tilGeneriskModell()
        val antallVaretyperPerForekomst = mutableMapOf<String, String>()
        val antallKassasystemPerForekomst = mutableMapOf<String, String>()

        for (i in 0 until gm.grupper(overnattingsOgServeringssted.forekomstType[0]).size) {

            val forekomstId = gm.grupper(overnattingsOgServeringssted.forekomstType[0])[i]
                .getForekomstId(overnattingsOgServeringssted.forekomstType[0])
            val antallVaretyperForForekomst = gm.grupper(overnattingsOgServeringssted.forekomstType[0])[i]
                .felter(overnattingsOgServeringssted.varelagerPerVareart.vareart.key).size
            val antallKassasystemForForekomst = gm.grupper(overnattingsOgServeringssted.forekomstType[0])[i]
                .felter(overnattingsOgServeringssted.kassasystem.kassasystemtype.key).size

            antallVaretyperPerForekomst[forekomstId] = antallVaretyperForForekomst.toString()
            antallKassasystemPerForekomst[forekomstId] = antallKassasystemForForekomst.toString()
        }

        return if (antallVaretyperPerForekomst.isNotEmpty() && antallKassasystemPerForekomst.isNotEmpty()) {

            var result = GeneriskModell.tom()
            antallVaretyperPerForekomst.forEach { (forekomstId, antallVaretyper) ->
                result = result.erstattEllerLeggTilFelter(
                    InformasjonsElement(
                        "andreForhold.overnattingsOgServeringssted.antallVaretyper",
                        mapOf(overnattingsOgServeringssted.forekomstType[0] to forekomstId),
                        antallVaretyper
                    )
                )
            }
            antallKassasystemPerForekomst.forEach { (forekomstId, antallKassasystem) ->
                result = result.erstattEllerLeggTilFelter(
                    InformasjonsElement(
                        "andreForhold.overnattingsOgServeringssted.antallKassasystem",
                        mapOf(overnattingsOgServeringssted.forekomstType[0] to forekomstId),
                        antallKassasystem
                    )
                )
            }
            Verdi(result)
        } else {
            Verdi(GeneriskModell.tom())
        }
    }
}