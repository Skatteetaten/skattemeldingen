package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import mu.KotlinLogging
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.util.fjernAvlededeFelt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.klienter.operasjoner.api.naering.NaeringForespoerselKontekst
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.GeneriskModell.Predicates.feltSomSlutterMed
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.InformasjonsElement
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.Felt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.FeltEgenskap
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.FeltMedEgenskaper
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.domenemodell.ForekomstType
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.mapping.tilgenerisk.MappingTilGenerisk.FIXED_COMPONENT_ID
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.NaeringFelterKunForVisning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v2_2021.Regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.naering.domenemodell.v5_2024.v5
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.xmldata.beregnedefelt.ErAvledetGeneriskNoekkel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.UgyldigInputException
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.NaeringsBeregner
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.Virksomhetstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftsinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.annenDriftskostnad
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForAnleggsmiddel_2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.balanseverdiForOmloepsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.egenkapital
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.fradragIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.inntektIGrunnrente
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.midlertidigForskjellstype2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.permanentForskjellstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kodelister.regnskapspliktstype
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.modell2022

private val logger = KotlinLogging.logger("NaeringsBeregnerFra2020")

class NaeringsBeregnerFra2020(
    private val inntektsaar: Int,
    private val avlededeFelter: Set<ErAvledetGeneriskNoekkel> = emptySet()
) : NaeringsBeregner(
    defaultKalkyleSamlingPerAar[inntektsaar] ?: error("Finner ikke kalkylesamling for inntektsaar $inntektsaar"),
) {

    override fun sjekkInput(generiskModell: GeneriskModell) {
        if (inntektsaar > 2020) {
            validerGlobaleFelt(generiskModell)
            validerVirksomhet(inntektsaar, generiskModell)
        }
    }

    override fun nullstill(
        generiskModell: GeneriskModell,
        forespoerselKontekst: NaeringForespoerselKontekst
    ): GeneriskModell {
        var nullstilt = fjernAvlededeFelt(generiskModell, avlededeFelter)
        nullstilt = filtrerGeneriskeTyperBasertPaaInnhold(
            nullstilt,
            forespoerselKontekst.gjelderSkjoennsfastsetting,
            inntektsaar
        )
        return fjernFelterSomSkalUtledesPaaNyttBasertPaaAnnetInnholdINsp(nullstilt)
    }

    override fun filtrerBeregningsresultat(beregnetModell: GeneriskModell): GeneriskModell {
        return filtrerResultatAvBeregninger(beregnetModell, inntektsaar)
    }
}

private fun validerGlobaleFelt(generiskModell: GeneriskModell) {
    verifiserGlobaltFeltHarVerdi(modell.naeringsspesifikasjon_inntektsaar, generiskModell)
    verifiserGlobaltFeltHarVerdi(modell.naeringsspesifikasjon_partsreferanse, generiskModell)
}

private fun verifiserGlobaltFeltHarVerdi(felt: Felt<*>, generiskModell: GeneriskModell) {
    if (generiskModell.felt(felt).verdi().isNullOrBlank()) {
        logger.warn {
            "Feil i input til beregning, naeringsspesifikasjon mangler det obligatoriske feltet ${felt.key}"
        }
        throw UgyldigInputException("Feil i input til beregning, naeringsspesifikasjon mangler feltet ${felt.key}")
    }
}

private fun validerVirksomhet(inntektsaar: Int, generiskModell: GeneriskModell) {
    val virksomhetNoekkel = modell.virksomhet.gruppe()
    val virksomhet = generiskModell.filter { it.key.startsWith(virksomhetNoekkel) }
    if (virksomhet.any { it.forekomstIder.values.firstOrNull() != FIXED_COMPONENT_ID }) {
        logger.warn {
            "Feil i input til beregning, mottok en eller flere virksomheter med forekomstId != fixed - " +
                " inntektsaar=$inntektsaar" +
                " alleVirksomhetFelter=${virksomhet.alleInformasjonsElementer()}"
        }
        throw UgyldigInputException("Feil i input til beregning, ugyldig virksomhet")
    }

    validerVirksomhetsfelt(
        modell.virksomhet.virksomhetstype,
        Virksomhetstype.gyldigeVirksomhetstyper(inntektsaar),
        virksomhet
    )
    validerVirksomhetsfelt(
        modell.virksomhet.regnskapspliktstype,
        if (inntektsaar < 2022) {
            setOf(
                Regnskapspliktstype.type_1,
                Regnskapspliktstype.type_2,
                Regnskapspliktstype.type_5
            )
        } else {
            setOf(
                regnskapspliktstype.fullRegnskapsplikt,
                regnskapspliktstype.begrensetRegnskapsplikt,
                regnskapspliktstype.ingenRegnskapsplikt
            )
        },
        virksomhet
    )
    validerVirksomhetsfelt(
        modell.virksomhet.regnskapsperiode_start,
        emptySet(),
        virksomhet
    )
}

private fun validerVirksomhetsfelt(
    felt: Felt<*>,
    gyldigeVerdier: Set<String>,
    virksomhet: GeneriskModell
) {
    val verdi: String? = virksomhet.felt(felt).verdi()
    val feilmelding = if (verdi.isNullOrBlank()) {
        "Feil i input til beregning, virksomhet mangler feltet ${felt.key}"
    } else if (gyldigeVerdier.isNotEmpty() && verdi !in gyldigeVerdier) {
        "Feil i input til beregning, feltet ${felt.key}=$verdi har ugyldig verdi"
    } else null

    if (feilmelding != null) {
        logger.warn { "$feilmelding - alleVirksomhetFelter=${virksomhet.alleInformasjonsElementer()}" }
        throw UgyldigInputException(feilmelding)
    }
}

internal fun filtrerGeneriskeTyperBasertPaaInnhold(
    generiskModell: GeneriskModell,
    gjelderSkjoennsfastsetting: Boolean,
    inntektsaar: Int
): GeneriskModell {
    return if (inntektsaar == 2020) {
        filtrerBortSpesifikkeTyperFraInput2020(generiskModell)
    } else {
        generiskModell
            .fjernFelter(beregnedeKontoerSomSkalFjernes(generiskModell, gjelderSkjoennsfastsetting, inntektsaar))
            .fjernFelter(samletGjeldOgFormuesobjekterSomSkalNullstillesFoer2022(generiskModell, inntektsaar))
            .fjernFelter(beregnedeVerdierIMidlertidigeForskjellerSomSkalNullstilles(generiskModell, inntektsaar))
            .fjernFelter(beregnedeVerdierIForskjellForVirksomhetOmfattetAvPetroleumsskatteloven(generiskModell, inntektsaar))
            .fjernFelter(felterSomSkalNullstillesVindkraftInntekt(generiskModell))
            .fjernFelter(felterSomSkalNullstillesVindkraftFradrag(generiskModell))
    }
}

internal fun filtrerBortSpesifikkeTyperFraInput2020(generiskModell: GeneriskModell): GeneriskModell {
    val anleggsmiddelTyper = listOf(
        balanseverdiForAnleggsmiddel.kode_1080.kode,
        balanseverdiForAnleggsmiddel.kode_1105.kode,
        balanseverdiForAnleggsmiddel.kode_1115.kode,
        balanseverdiForAnleggsmiddel.kode_1117.kode,
        balanseverdiForAnleggsmiddel.kode_1120.kode,
        balanseverdiForAnleggsmiddel.kode_1205.kode,
        balanseverdiForAnleggsmiddel.kode_1221.kode,
        balanseverdiForAnleggsmiddel.kode_1225.kode,
        balanseverdiForAnleggsmiddel.kode_1238.kode,
        balanseverdiForAnleggsmiddel_2023.kode_1239.kode,
        balanseverdiForAnleggsmiddel.kode_1280.kode,
        balanseverdiForAnleggsmiddel.kode_1296.kode,
    )
    val egenkapitalTyper = listOf(
        egenkapital.kode_2095.kode,
        egenkapital.kode_2096.kode,
    )
    val resultatregnskapetTyper = listOf(
        annenDriftsinntekt.kode_3890.kode,
        annenDriftsinntekt.kode_3895.kode,
        annenDriftskostnad.kode_6000.kode,
        annenDriftskostnad.kode_7099.kode,
        annenDriftskostnad.kode_7890.kode,
    )
    val filtrerBortAnleggsmidler = GeneriskModell.merge(
        generiskModell.grupper(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel)
            .filter { anleggsmiddelTyper.contains(it.verdiFor(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.anleggsmiddeltype)) })
        .filter { !it.harNoekkel(modell2020.balanse_anleggsmiddel_balanseverdiForAnleggsmidler.balanseverdiForAnleggsmiddel.overfoeresIkkeTilSkattemeldingen) }
    val filtrerBortEgenkapital = GeneriskModell.merge(
        generiskModell.grupper(modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital)
            .filter { egenkapitalTyper.contains(it.verdiFor(modell2020.balanse_gjeldOgEgenkapital_allEgenkapital.egenkapital.egenkapitaltype)) })
    val filtrerBortAnnenDriftsinntekt = GeneriskModell.merge(
        generiskModell.grupper(modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt)
            .filter { resultatregnskapetTyper.contains(it.verdiFor(modell2020.resultatregnskap_driftsinntekt_andreDriftsinntekter.annenDriftsinntekt.type)) })
    val filtrerBortAnnenDriftskostnad = GeneriskModell.merge(
        generiskModell.grupper(modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad)
            .filter { resultatregnskapetTyper.contains(it.verdiFor(modell2020.resultatregnskap_driftskostnad_andreDriftskostnader.annenDriftskostnad.type)) })

    val filtrerBortSamletGjeld = GeneriskModell.merge(
        generiskModell.grupper(modell2020.samletGjeldOgFormuesobjekter))

    val ferdigFiltrert = generiskModell
        .fjernFelter(filtrerBortAnleggsmidler)
        .fjernFelter(filtrerBortEgenkapital)
        .fjernFelter(filtrerBortAnnenDriftsinntekt)
        .fjernFelter(filtrerBortAnnenDriftskostnad)
        .fjernFelter(filtrerBortSamletGjeld)

    return ferdigFiltrert
}

fun felterSomSkalNullstillesVindkraftInntekt(
    generiskModell: GeneriskModell
): GeneriskModell {
    val typerSomSkalSlettesInntekt = listOf(
        inntektIGrunnrente.kode_salgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattFoer28092022.kode,
        inntektIGrunnrente.kode_salgsinntektFraLangsiktigFastpriskontrakt.kode,
        inntektIGrunnrente.kode_salgsinntektFraKjoepekontraktMellomUavhengigeParterInngaattMellom2024Og2030.kode,
        inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022.kode,
        inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022.kode,
        inntektIGrunnrente.kode_gevinstVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030.kode
    )
    val forekomst =
        modell.spesifikasjonAvEnhetIVindkraftverk.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvInntektIBruttoGrunnrenteinntektIVindkraftverk
    val felterSomSkalFjernes: List<InformasjonsElement> = generiskModell.grupper(forekomst)
        .filter { e -> typerSomSkalSlettesInntekt.contains(e.verdiFor(forekomst.type)) }
        .flatMap { e -> e.alleInformasjonsElementer() }

    return GeneriskModell.fra(felterSomSkalFjernes)
}

fun felterSomSkalNullstillesVindkraftFradrag(
    generiskModell: GeneriskModell
): GeneriskModell {
    val typerSomSkalSlettesFradrag =
        listOf(
            fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvKjoepekontraktInngaattFoer28092022.kode,
            fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022.kode,
            fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvFinansiellSikringsavtaleInngaattFoer28092022.kode,
            fradragIGrunnrente.kode_tapVedFoertidigOppgjoerAvKjoepekontraktInngaattMellom2024Og2030.kode,
            fradragIGrunnrente.kode_investeringskostnad.kode,
            fradragIGrunnrente.kode_venterente.kode,
            fradragIGrunnrente.kode_skattemessigAvskrivningAvDriftsmiddel.kode
        )

    val forekomst =
        modell.spesifikasjonAvEnhetIVindkraftverk.spesifikasjonAvGrunnrenteinntektIVindkraftverk_spesifikasjonAvFradragIBruttoGrunnrenteinntektIVindkraftverk
    val felterSomSkalFjernes: List<InformasjonsElement> = generiskModell.grupper(forekomst)
        .filter { e -> typerSomSkalSlettesFradrag.contains(e.verdiFor(forekomst.type)) }
        .flatMap { e -> e.alleInformasjonsElementer() }

    return GeneriskModell.fra(felterSomSkalFjernes)
}

internal fun fjernFelterSomSkalUtledesPaaNyttBasertPaaAnnetInnholdINsp(
    generiskModell: GeneriskModell
): GeneriskModell {
    return generiskModell
        .filter {
            it.key != NaeringFelterKunForVisning.erPetroleum
                && it.key != NaeringFelterKunForVisning.erKraftverk
        }
}

/**
 * Gitt at regnskapsplikttypen er 2, så skal kode_7099.kode ha grunnlag, som betyr at den må filtrers bort. Fordi dette
 * er et felt vi beregner oss frem til, basert på grunnlaget.
 * Resten filteres automatisk på 1 og 5, kalkylene kjører kun når regnskapsplikttypene er 1 og 5 for disse.
 */
private fun beregnedeKontoerSomSkalFjernes(
    generiskModell: GeneriskModell,
    gjelderSkjoennsfastsetting: Boolean,
    inntektsaar: Int
): GeneriskModell {
    val regnskapspliktstypeVerdi = generiskModell.verdiFor(modell.virksomhet.regnskapspliktstype)

    val fellesKontoerForIngenOgBegrensetRegnskapsplikt = setOf(
        balanseverdiForAnleggsmiddel.kode_1080.kode,
        balanseverdiForAnleggsmiddel.kode_1105.kode,
        balanseverdiForAnleggsmiddel.kode_1115.kode,
        balanseverdiForAnleggsmiddel.kode_1117.kode,
        balanseverdiForAnleggsmiddel.kode_1120.kode,
        balanseverdiForAnleggsmiddel.kode_1205.kode,
        balanseverdiForAnleggsmiddel.kode_1221.kode,
        balanseverdiForAnleggsmiddel.kode_1225.kode,
        balanseverdiForAnleggsmiddel.kode_1238.kode,
        balanseverdiForAnleggsmiddel.kode_1280.kode,
        balanseverdiForAnleggsmiddel.kode_1296.kode,
        balanseverdiForAnleggsmiddel.kode_1295.kode,
        balanseverdiForAnleggsmiddel.kode_1298.kode,
        balanseverdiForOmloepsmiddel.kode_1400.kode,
        egenkapital.kode_2095.kode,
        egenkapital.kode_2096.kode,
        egenkapital.kode_2098.kode,
        annenDriftsinntekt.kode_3890.kode,
        annenDriftsinntekt.kode_3895.kode,
        annenDriftsinntekt.kode_3910.kode,
        annenDriftskostnad.kode_6000.kode,
        annenDriftskostnad.kode_7099.kode,
        annenDriftskostnad.kode_7890.kode,
        annenDriftskostnad.kode_7911.kode,
        annenDriftskostnad.kode_7910.kode,
    )

    val fellesKontoerForIngenOgBegrensetRegnskapspliktFoer2024 = setOf(
        balanseverdiForAnleggsmiddel_2023.kode_1239.kode,
    )

    val kontoerOverfoertIfmSkjoennsfastsetting = setOf(
        balanseverdiForAnleggsmiddel.kode_1080.kode,
        balanseverdiForAnleggsmiddel.kode_1105.kode,
        balanseverdiForAnleggsmiddel.kode_1115.kode,
        balanseverdiForAnleggsmiddel.kode_1117.kode,
        balanseverdiForAnleggsmiddel.kode_1120.kode,
        balanseverdiForAnleggsmiddel.kode_1205.kode,
        balanseverdiForAnleggsmiddel.kode_1221.kode,
        balanseverdiForAnleggsmiddel.kode_1225.kode,
        balanseverdiForAnleggsmiddel.kode_1238.kode,
        balanseverdiForAnleggsmiddel.kode_1280.kode,
        balanseverdiForAnleggsmiddel.kode_1295.kode,
        egenkapital.kode_2095.kode,
        egenkapital.kode_2096.kode,
        egenkapital.kode_2097.kode,
        annenDriftsinntekt.kode_3890.kode,
        annenDriftsinntekt.kode_3895.kode,
        annenDriftskostnad.kode_6000.kode,
        annenDriftskostnad.kode_7890.kode,
    )

    val kontoerOverfoertIfmSkjoennsfastsettingFoer2024 = setOf(
        balanseverdiForAnleggsmiddel_2023.kode_1239.kode,
        )

    val kontoerSomSkalFjernesFoerBeregning = mutableSetOf<String>()
    when (regnskapspliktstypeVerdi) {
        regnskapspliktstype.ingenRegnskapsplikt, Regnskapspliktstype.type_1 -> {
            if (inntektsaar < 2024) {
                kontoerSomSkalFjernesFoerBeregning.addAll(fellesKontoerForIngenOgBegrensetRegnskapspliktFoer2024)
            }
            kontoerSomSkalFjernesFoerBeregning.addAll(fellesKontoerForIngenOgBegrensetRegnskapsplikt)
            kontoerSomSkalFjernesFoerBeregning.add(balanseverdiForOmloepsmiddel.kode_1500.kode)
        }

        regnskapspliktstype.begrensetRegnskapsplikt, Regnskapspliktstype.type_5 -> {
            if (inntektsaar < 2024) {
                kontoerSomSkalFjernesFoerBeregning.addAll(fellesKontoerForIngenOgBegrensetRegnskapspliktFoer2024)
            }
            kontoerSomSkalFjernesFoerBeregning.addAll(fellesKontoerForIngenOgBegrensetRegnskapsplikt)
        }

        regnskapspliktstype.fullRegnskapsplikt, Regnskapspliktstype.type_2 -> {
            kontoerSomSkalFjernesFoerBeregning.add(annenDriftskostnad.kode_7099.kode)
        }

        null -> {
            logger.warn { "Naeringsspesifikasjon mangler feltet regnskapspliktstype" }
            error("Mangler obligatorisk felt ${modell.virksomhet.regnskapspliktstype.key}")
        }
    }

    if (gjelderSkjoennsfastsetting) {
        kontoerSomSkalFjernesFoerBeregning.removeAll(kontoerOverfoertIfmSkjoennsfastsetting)
        if (inntektsaar < 2024) {
            kontoerSomSkalFjernesFoerBeregning.removeAll(kontoerOverfoertIfmSkjoennsfastsettingFoer2024)
        }
    }

    return filtrerForekomsterMedResultatOgBalanseregnskapstype(
        generiskModell,
        kontoerSomSkalFjernesFoerBeregning
    )
}

private fun filtrerForekomsterMedResultatOgBalanseregnskapstype(
    generiskModell: GeneriskModell,
    koder: Set<String>
): GeneriskModell {
    val balanseverdiForAnleggsmiddelSomSkalFjernes = filtrerForekomstTypeMedKode(
        generiskModell = generiskModell,
        forekomstType = modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi,
        noekkelTilKodeIForekomst = modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type,
        koder = koder
    ).filter { !it.harNoekkel(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.ekskluderesFraSkattemeldingen) }

    val balanseverdiForOmloepsmiddelSomSkalFjernes = filtrerForekomstTypeMedKode(
        generiskModell = generiskModell,
        forekomstType = modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi,
        noekkelTilKodeIForekomst = modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type,
        koder = koder
    ).filter { !it.harNoekkel(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.ekskluderesFraSkattemeldingen) }

    val egenkapitalSomSkalFjernes = filtrerForekomstTypeMedKode(
        generiskModell = generiskModell,
        forekomstType = modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital,
        noekkelTilKodeIForekomst = modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type,
        koder = koder
    ).filter { !it.harNoekkel(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.ekskluderesFraSkattemeldingen) }

    val annenDriftsinntektSomSkalFjernes = filtrerForekomstTypeMedKode(
        generiskModell = generiskModell,
        forekomstType = modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt,
        noekkelTilKodeIForekomst = modell.resultatregnskap_driftsinntekt_annenDriftsinntekt.inntekt.type,
        koder = koder
    )

    val annenDriftskostnadSomSkalFjernes = filtrerForekomstTypeMedKode(
        generiskModell = generiskModell,
        forekomstType = modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad,
        noekkelTilKodeIForekomst = modell.resultatregnskap_driftskostnad_annenDriftskostnad.kostnad.type,
        koder = koder
    )

    return balanseverdiForAnleggsmiddelSomSkalFjernes
        .concat(balanseverdiForOmloepsmiddelSomSkalFjernes)
        .concat(egenkapitalSomSkalFjernes)
        .concat(annenDriftsinntektSomSkalFjernes)
        .concat(annenDriftskostnadSomSkalFjernes)
}

private fun filtrerForekomstTypeMedKode(
    generiskModell: GeneriskModell,
    forekomstType: ForekomstType<*>,
    noekkelTilKodeIForekomst: Felt<*>,
    koder: Set<String>
): GeneriskModell {
    return GeneriskModell.merge(
        generiskModell.grupper(forekomstType)
            .filter { koder.contains(it.verdiFor(noekkelTilKodeIForekomst)) }
    )
}

private val midlertidigForskjellstypeTilBeregnedeFelterTom2022 = mapOf(
    midlertidigForskjellstype2022.kode_driftsmiddelOgGoodwill.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_varebeholdningOgBiologiskEiendel.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_kundefordring.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_annenFordring.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_positivSaldoPaaGevinstOgTapskonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_negativSaldoPaaGevinstOgTapskonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_betingetSkattefriGevinst.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_positivSaldoToemmerkonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype2022.kode_negativSaldoToemmerkonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
)

private val midlertidigForskjellstypeTilBeregnedeFelterFom2023 = mapOf(
    midlertidigForskjellstype.kode_driftsmiddelOgGoodwill.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype.kode_positivSaldoPaaGevinstOgTapskonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype.kode_negativSaldoPaaGevinstOgTapskonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype.kode_betingetSkattefriGevinst.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype.kode_positivSaldoToemmerkonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
    midlertidigForskjellstype.kode_negativSaldoToemmerkonto.kode to
        setOf(modell.midlertidigForskjell.skattemessigVerdi.key),
)

private val permanentForskjellstypeBeloepsfelt = setOf(
    modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepResultatAvFinansinntektOgFinanskostnadMv,
    modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepSaerskattegrunnlagFraVirksomhetPaaSokkel,
    modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaSokkel,
    modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.beloep_beloepAlminneligInntektFraVirksomhetPaaLand
)

private val permanentForskjellstypeTilBeregnedeFelter = mapOf(
    permanentForskjellstype.kode_normprisTillegg.kode to
            permanentForskjellstypeBeloepsfelt,
    permanentForskjellstype.kode_normprisFradrag.kode to
            permanentForskjellstypeBeloepsfelt,
    permanentForskjellstype.kode_friinntektEtterPetroleumsskatteloven.kode to
            permanentForskjellstypeBeloepsfelt
)

private fun samletGjeldOgFormuesobjekterSomSkalNullstillesFoer2022(generiskModell: GeneriskModell, inntektsaar: Int): GeneriskModell {
    //Fra 2022 er feltene markert som avledede
    if (inntektsaar >= 2022) {
        return GeneriskModell.tom()
    }
    val forekomsterSomSkalNullstilles = GeneriskModell.merge(generiskModell.grupper(modell2021.samletGjeldOgFormuesobjekter))
    val overstyrteFelt = GeneriskModell.merge(
        forekomsterSomSkalNullstilles.filter(feltSomSlutterMed(FeltEgenskap.erOverstyrt.name)).informasjonsElementer()
        .map { forekomsterSomSkalNullstilles.gruppe(it.forekomstIder) }
    )
    return forekomsterSomSkalNullstilles.fjernFelter(overstyrteFelt)
}

internal fun beregnedeVerdierIMidlertidigeForskjellerSomSkalNullstilles(
    generiskModell: GeneriskModell,
    inntektsaar: Int
): GeneriskModell {
    val regnskapspliktstypeVerdi = generiskModell.gruppe(modell.virksomhet)
        .felt(modell.virksomhet.regnskapspliktstype).verdi()

    return if (regnskapspliktstypeVerdi == regnskapspliktstype.fullRegnskapsplikt) {
        val midlertidigeForskjeller = generiskModell.grupper(modell.midlertidigForskjell)
        val felterSomSkalFjernes: List<InformasjonsElement> = midlertidigeForskjeller
            .filter { forskjell ->
                val forskjellstype = forskjell.verdiFor(modell.midlertidigForskjell.midlertidigForskjellstype)
                forskjellstypeSomSkalFjernes(forskjellstype, inntektsaar)
            }
            .flatMap { forskjellMedFelterSomSkalFjernes ->
                val forskjellstype = forskjellMedFelterSomSkalFjernes
                    .verdiFor(modell.midlertidigForskjell.midlertidigForskjellstype)
                val felterSomSkalFjernes = finnFelterSomSkalFjernesMidlertidigForskjell(forskjellstype, inntektsaar)

                forskjellMedFelterSomSkalFjernes.alleInformasjonsElementer()
                    .filter { felterSomSkalFjernes.contains(it.key) }
            }

        return GeneriskModell.fra(felterSomSkalFjernes)
    } else {
        GeneriskModell.tom()
    }
}

internal fun beregnedeVerdierIForskjellForVirksomhetOmfattetAvPetroleumsskatteloven(
    generiskModell: GeneriskModell,
    inntektsaar: Int
): GeneriskModell {
    val regnskapspliktstypeVerdi = generiskModell.gruppe(modell.virksomhet)
        .felt(modell.virksomhet.regnskapspliktstype).verdi()

    return if (regnskapspliktstypeVerdi == regnskapspliktstype.fullRegnskapsplikt) {
        val permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven = generiskModell.grupper(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven)
        val felterSomSkalFjernes: List<GeneriskModell> = permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven
            .filter { forskjell ->
                val forskjellstype = forskjell.verdiFor(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.permanentForskjellstype)
                forskjellstypeSomSkalFjernes(forskjellstype, inntektsaar)
            }
            .flatMap { forskjellMedFelterSomSkalFjernes ->

                val forskjellstype = forskjellMedFelterSomSkalFjernes
                    .verdiFor(modell.permanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven.permanentForskjellstype)

                val felterSomSkalFjernes = finnFelterSomSkalFjernesForskjellForVirksomhetOmfattetAvPetroleumsskatteloven(forskjellstype)

                felterSomSkalFjernes.map { forskjellMedFelterSomSkalFjernes.gruppe(it) }.filter { it.verdiFor(FeltEgenskap.erOverstyrt) != "true" }
            }

        return GeneriskModell.fra(felterSomSkalFjernes.flatten())
    } else {
        GeneriskModell.tom()
    }
}

private fun finnFelterSomSkalFjernesMidlertidigForskjell(forskjellstype: String, inntektsaar: Int): Set<String> {
    return if (inntektsaar <= 2022) {
        (midlertidigForskjellstypeTilBeregnedeFelterTom2022[forskjellstype]
            ?: error("Fant ikke forventet oversikt over felter som skal fjernes fra midlertidig forskjell."))
    } else {
        (midlertidigForskjellstypeTilBeregnedeFelterFom2023[forskjellstype]
            ?: error("Fant ikke forventet oversikt over felter som skal fjernes fra midlertidig forskjell."))
    }
}

private fun finnFelterSomSkalFjernesForskjellForVirksomhetOmfattetAvPetroleumsskatteloven(forskjellstype: String): Set<FeltMedEgenskaper<v5.permanentForskjellForVirksomhetOmfattetAvPetroleumsskattelovenForekomst>> {
    return permanentForskjellstypeTilBeregnedeFelter[forskjellstype]
            ?: error("Fant ikke forventet oversikt over felter som skal fjernes fra midlertidig forskjell.")
    }


private fun forskjellstypeSomSkalFjernes(forskjellstype: String, inntektsaar: Int): Boolean {
    return if (inntektsaar <= 2022) {
        midlertidigForskjellstypeTilBeregnedeFelterTom2022.containsKey(forskjellstype)
    } else {
        midlertidigForskjellstypeTilBeregnedeFelterFom2023.containsKey(forskjellstype)
                || permanentForskjellstypeTilBeregnedeFelter.containsKey(forskjellstype)
    }
}

private fun filtrerResultatAvBeregninger(gm: GeneriskModell, inntektsaar: Int): GeneriskModell {
    val filtrertGm = filtrerBortFordelBeregnetInntekt(gm, inntektsaar)
    return filtrerBortResultatOgBalanseregnskapstyperMedKunEkskluderesFraSkattemeldingenFelt(filtrertGm)
}

private fun filtrerBortResultatOgBalanseregnskapstyperMedKunEkskluderesFraSkattemeldingenFelt(gm: GeneriskModell): GeneriskModell {
    val balanseverdiForAnleggsmiddelForekomster =
        gm.grupper(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi)
    val filtrertBalanseverdiForAnleggsmiddelForekomster = balanseverdiForAnleggsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_anleggsmiddel_balanseverdiForAnleggsmiddel.balanseverdi.type) }

    val balanseverdiForOmloepsmiddelForekomster =
        gm.grupper(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi)
    val filtrertBalanseverdiForOmloepsmiddelForekomster = balanseverdiForOmloepsmiddelForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_omloepsmiddel_balanseverdiForOmloepsmiddel.balanseverdi.type) }

    val egenkapitalForekomster =
        gm.grupper(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital)
    val filtrertEgenkapitalForekomster = egenkapitalForekomster
        .filter { it.harVerdiFor(modell.balanseregnskap_gjeldOgEgenkapital_egenkapital.kapital.type) }

    return gm
        .fjernFelter(GeneriskModell.merge(balanseverdiForAnleggsmiddelForekomster))
        .fjernFelter(GeneriskModell.merge(balanseverdiForOmloepsmiddelForekomster))
        .fjernFelter(GeneriskModell.merge(egenkapitalForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertBalanseverdiForAnleggsmiddelForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertBalanseverdiForOmloepsmiddelForekomster))
        .erstattEllerLeggTilFelter(GeneriskModell.merge(filtrertEgenkapitalForekomster))
}

private fun filtrerBortFordelBeregnetInntekt(gm: GeneriskModell, inntektsaar: Int): GeneriskModell {
    if (inntektsaar < 2023) {
        val resultat = gm.verdiFor(modell2022.beregnetNaeringsinntekt_skattemessigResultat) ?: gm.verdiFor(
            modell2022.resultatregnskap_aarsresultat
        )
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupper(modell2022.fordeltBeregnetNaeringsinntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupper(modell2022.fordeltBeregnetPersoninntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            gm.fjernFelter(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetPersoninntektFelter)
        } else {
            gm
        }
    } else {
        val resultat = gm.verdiFor(modell.beregnetNaeringsinntekt_skattemessigResultat)
            ?: gm.verdiFor(modell.resultatregnskap_aarsresultat)
        return if (resultat == null) {
            val eksisterendeFordeltBeregnetNaeringsinntektFelter =
                gm.grupper(modell.fordeltBeregnetNaeringsinntektForPersonligSkattepliktigEllerSdf)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetPersoninntektFelter =
                gm.grupper(modell.fordeltBeregnetPersoninntekt)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            val eksisterendeFordeltBeregnetUpersonlig =
                gm.grupper(modell.fordeltBeregnetNaeringsinntektForUpersonligSkattepliktig)
                    .stream()
                    .collect(GeneriskModell.collectorFraGm())
            gm.fjernFelter(eksisterendeFordeltBeregnetNaeringsinntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetPersoninntektFelter)
                .fjernFelter(eksisterendeFordeltBeregnetUpersonlig)
        } else {
            gm
        }
    }
}
