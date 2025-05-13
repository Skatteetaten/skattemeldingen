package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

object InntektOgUnderskuddRekkefoelge : HarKalkylesamling {
    override fun kalkylesamling(): Kalkylesamling =
        Kalkylesamling(
            InntektOgUnderskuddForVirksomhetPaaSokkel.nettoFinanskostnadIAlminneligInntektFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsUnderskudd,
            InntektOgUnderskuddForVirksomhetPaaSokkel.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvAaretsUnderskudd,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsUnderskuddFraVirksomhetPaaLandFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkel.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_mottattKonsernbidragTilReduksjonIAaretsFremfoerbareUnderskudd,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsFremfoerbareUnderskudd,
            InntektOgUnderskudd.aaretsFremfoerbareUnderskuddKalkyle,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAArFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoertUnderskuddFraVirksomhetPaaLandFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoertUnderskuddFraVirksomhetPaaSokkelFraTidligereAarFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkel.underskuddTilFremfoering_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAAr,
            InntektOgUnderskuddForVirksomhetPaaSokkel.underskuddTilFremfoeringForVirksomhetPaaLandOmfattetAvPetroleumsskatteloven_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_aaretsAnvendelseAvFremfoertUnderskuddFraTidligereAar,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletInntektEllerUnderskuddAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskudd.samletInntektEllerUnderskuddKalkyle,
            InntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
            InntektOgUnderskudd.fremfoerbartUnderskuddIInntektKalkyle,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelUnderskudd,
            InntektOgUnderskudd.fremfoerbartUnderskuddIInntektFraVirksomhetPaaSokkelKalkyle,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsBeregnedeSelskapsskatt,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar,
            InntektOgUnderskuddForVirksomhetPaaSokkel.beregnetSelskapsskattSomFradrasISaerskattegrunnlaget,
            InntektOgUnderskuddForVirksomhetPaaSokkel.beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoerbarBeregnetNegativSelskapsskatt,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletInntektEllerUnderskuddSaerskattegrunnlagFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletAnnetSkattefradragFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletAnnetSkattefradragFraVirksomhetPaaLand,
            InntektOgUnderskudd.defaultKonsernbidragSkalBekreftesAvRevisor // denne må kjøres til slutt
        )
}