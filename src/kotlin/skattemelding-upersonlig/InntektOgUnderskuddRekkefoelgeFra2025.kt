package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling

object InntektOgUnderskuddRekkefoelgeFra2025 : HarKalkylesamling {
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
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.samletInntektEllerUnderskuddAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddFra2025.tilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddFra2025.tilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.beregnetNegativSelskapsskattKnyttetTilTilbakefoertUnderskuddFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaSokkelFraFremtidigInntektsaar,
            InntektOgUnderskuddFra2025.aaretsAnvendelseAvTilbakefoertUnderskuddFraVirksomhetPaaLandFraFremtidigInntektsaar,
            InntektOgUnderskuddFra2025.samletInntektEllerUnderskuddKalkyle,
            InntektOgUnderskudd.restOppnaaddUnderhaandsakkordOgGjeldsettergivelseKalkyle,
            InntektOgUnderskudd.fremfoerbartUnderskuddIInntektKalkyle,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelUnderskudd,
            InntektOgUnderskudd.fremfoerbartUnderskuddIInntektFraVirksomhetPaaSokkelKalkyle,
            InntektOgUnderskuddForVirksomhetPaaSokkel.andelAvUnderskuddTilFremfoeringPaaLandFremfoerbartMotSokkel_fremfoerbartUnderskuddIAlminneligInntektFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsBeregnedeSelskapsskatt,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsAnvendelseAvFremfoertBeregnetNegativSelskapsskattFraTidligereAar,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.beregnetSelskapsskattSomFradrasISaerskattegrunnlaget,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.negativSelskapsskattKnyttetTilVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.beregnetNegativSelskapsskattKnyttetTilUnderskuddFraVirksomhetPaaSokkelFraTidligereInntektsaarFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.beregnetNegativSelskapsskattSomInntektsfoeresISaerskattegrunnlaget,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.beregnetNegativSelskapsskattTilbakefoertFraFremtidigInntektsaarBenyttetMotAaretsBeregnedeSelskapsskatt,
            InntektOgUnderskuddForVirksomhetPaaSokkel.aaretsBeregnedeNegativeSelskapsskattEtterKorrigeringForUnderskuddIAlminneligInntektFraVirksomhetPaaSokkelFoertMotAlminneligInntektFraVirksomhetPaaLand,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.aaretsBeregnedeNegativeSelskapsskattTilbakefoertMotTidligereAarsSaerskattegrunnlagFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkelFra2025.fremfoerbarBeregnetNegativSelskapsskatt,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletInntektEllerUnderskuddSaerskattegrunnlagFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletAnnetSkattefradragFraVirksomhetPaaSokkel,
            InntektOgUnderskuddForVirksomhetPaaSokkel.samletAnnetSkattefradragFraVirksomhetPaaLand,
            InntektOgUnderskudd.defaultKonsernbidragSkalBekreftesAvRevisor // denne må kjøres til slutt
        )
}