package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.mapping.util.Aarsliste
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordelingAvNaeringsinntekt2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektBeregning2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektBeregning2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektBeregningFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektPetroleum
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektPetroleum2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektUnntak2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektUnntak2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektUnntakFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetIdentifikatorHaandtering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetIdentifikatorHaandteringFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektBeregning2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektBeregning2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektBeregningFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektUnntak2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektUnntak2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektUnntakFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.fordeltBeregnetInntekt.personinntekt.PersoninntektBeregning2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaLand
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaSokkel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigForskjellPetroleumsskattelovenResultatAvFinansinntektOgFinanskostnadMv
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigForskjellPetroleumsskattelovenSaerskattegrunnlagFraVirksomhetPaaSokkel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigeForskjellerBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.MidlertidigeForskjellerBeregning2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.Omvurderingskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PermanenteForskjellerBeregning2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PermanenteForskjellerBeregning2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.PermanenteForskjellerBeregningFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.forskjeller.SpesifikasjonAvForskjellForSelskapOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.havbruk.GrunnrenteskattHavbruk
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.havbruk.GrunnrenteskattHavbruk2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk.Eiendomsskattegrunnlag
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk.Naturressursskatt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk.SpesifikasjonAvGrunnrenteinntekt
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.kraftverk.SpesifikasjonAvGrunnrenteinntektFra2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskonto2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskonto2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskontoFra2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.GevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.IkkeAvskrivbartAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.IkkeAvskrivbartAnleggsmiddel2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.IkkeAvskrivbartAnleggsmiddel2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.LineaertavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.LineaertavskrevetAnleggsmiddel2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.LineaertavskrevetAnleggsmiddelFra2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.MotorkjoeretoeyINaering2022
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.MotorkjoeretoeyINaeringFra2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.OevrigTilVisningForSpesifikasjonAvAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaerskiltAnleggsmiddelForSelskapOmfattetAvPetroleumsskatteloven
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaerskiltAnleggsmiddelIKraftverk2023
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaerskiltAnleggsmiddelIKraftverkFra2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaldoavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaldoavskrevetAnleggsmiddel2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaldoavskrevetAnleggsmiddel2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SaldoavskrevetAnleggsmiddelFra2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SpesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft2024
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.SpesifikasjonVedBeregningAvRentefradragsramme
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.TransportmiddelNaering2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvAnleggsmiddel.TransportmiddelNaering2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel.SpesifikasjonAvBalanseFordringer2020
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel.SpesifikasjonAvOmloepsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel.SpesifikasjonAvOmloepsmiddel2021
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.spesifikasjonAvOmloepsmiddel.SpesifikasjonAvSkattemessigVerdiPaaFordringUnntak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.kalkyler.kalkyler.vindkraft.GrunnrenteinntektLandbasertVindkraft

/**
 * Toppnivå - referanser til alle kalkyler som skal kjøres. Kalkyler som brukes av andre kalkyler
 * (lavere nivå) refereres ikke direkte her.
 */
val defaultKalkyleSamlingPerAar: Map<Int, Kalkylesamling> = Aarsliste<HarKalkylesamling>()
    .bareI(2020, GevinstOgTapskonto2020)
    .bareI(2021, GevinstOgTapskonto2021)
    .fraTil(2022, 2023, GevinstOgTapskonto)
    .fra(2024, GevinstOgTapskontoFra2024)
    .bareI(2021, LineaertavskrevetAnleggsmiddel2021)
    .fraTil(2022, 2023, LineaertavskrevetAnleggsmiddel)
    .fra(2024, LineaertavskrevetAnleggsmiddelFra2024)
    .bareI(2020, SaldoavskrevetAnleggsmiddel2020)
    .bareI(2021, SaldoavskrevetAnleggsmiddel2021)
    .fraTil(2022, 2023, SaldoavskrevetAnleggsmiddel)
    .fra(2024, SaldoavskrevetAnleggsmiddelFra2024)
    .bareI(2020, SpesifikasjonAvBalanseFordringer2020)
    .bareI(2021, SpesifikasjonAvOmloepsmiddel2021)
    .fra(2022, SpesifikasjonAvOmloepsmiddel)
    .fra(2021, SpesifikasjonAvSkattemessigVerdiPaaFordringUnntak)
    .bareI(2020, TransportmiddelNaering2020)
    .bareI(2021, TransportmiddelNaering2021)
    .bareI(2022, MotorkjoeretoeyINaering2022)
    .fra(2023, MotorkjoeretoeyINaeringFra2023)
    .bareI(2021, SkogOgToemmerkonto2021)
    .bareI(2022, SkogOgToemmerkonto2022)
    .bareI(2023, SkogOgToemmerkonto2023)
    .fra(2024, SkogOgToemmerkontoFra2024)
    .bareI(2023, SaerskiltAnleggsmiddelIKraftverk2023)
    .fra(2024, SaerskiltAnleggsmiddelIKraftverkFra2024)
    .fra(2023, ResultatregnskapForVirksomhetOmfattetAvPetroleumsskatteloven)
    .bareI(2020, Resultatregnskapet2020)
    .bareI(2021, Resultatregnskapet2021)
    .fra(2022, Resultatregnskapet)
    .bareI(2020, IkkeAvskrivbartAnleggsmiddel2020)
    .bareI(2021, IkkeAvskrivbartAnleggsmiddel2021)
    .fra(2022, IkkeAvskrivbartAnleggsmiddel)
    .bareI(2021, PermanenteForskjellerBeregning2021) // Kun relevant for regnskapspliktstype 2 (kun de som sender inn forskjeller)
    .bareI(2022, PermanenteForskjellerBeregning2022) // Kun relevant for fullRegnskapsplikt (kun de som sender inn forskjeller)
    .fra(2023, PermanenteForskjellerBeregningFra2023) // Kun relevant for fullRegnskapsplikt (kun de som sender inn forskjeller)
    .bareI(2021, MidlertidigeForskjellerBeregning2021) // Kun relevant for regnskapspliktstype 2 (kun de som sender inn forskjeller)
    .fra(2022, MidlertidigeForskjellerBeregning) // Kun relevant for fullRegnskapsplikt (kun de som sender inn forskjeller)
    .fraTil(2021, 2022, SkattemessigResultatBeregning)
    .fra(2023, SkattemessigResultatBeregningFra2023)
    .bareI(2020, FordelingAvNaeringsinntekt2020)
    .bareI(2021, FordeltBeregnetNaeringsinntektUnntak2021) // Maskinell behandling ved én forekomst for alle, unntak kun for enkeltpersonforetak
    .bareI(2022, FordeltBeregnetNaeringsinntektUnntak2022) // Maskinell behandling ved én forekomst for alle, unntak kun for enkeltpersonforetak
    .fra(2023, FordeltBeregnetNaeringsinntektUnntakFra2023)
    .bareI(2021, FordeltBeregnetNaeringsinntektBeregning2021)
    .bareI(2022, FordeltBeregnetNaeringsinntektBeregning2022)
    .fra(2023, FordeltBeregnetNaeringsinntektBeregningFra2023)
    .bareI(2021, FordeltBeregnetPersoninntektUnntak2021) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .bareI(2022, FordeltBeregnetPersoninntektUnntak2022) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .fra(2023, FordeltBeregnetPersoninntektUnntakFra2023) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .fraTil(2021, 2022, FordeltBeregnetIdentifikatorHaandtering) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .fra(2023, FordeltBeregnetIdentifikatorHaandteringFra2023) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .bareI(2021, FordeltBeregnetPersoninntektBeregning2021) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .bareI(2022, FordeltBeregnetPersoninntektBeregning2022) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .fra(2023, FordeltBeregnetPersoninntektBeregningFra2023) // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    .bareI(2020, Balanse2020)
    .bareI(2021, Balanse2021)
    .fra(2022, Balanse)
    .bareI(2020, BalanseFormueOgGjeld2020)
    .bareI(2021, BalanseFormueOgGjeld2021)
    .fra(2022, BalanseFormueOgGjeld)
    .bareI(2020, PersoninntektBeregning2020)
    .fra(2023, BalanseregnskapImmateriellEiendelOgVarigDriftsmiddelForSelskapOmfattetAvPetroleumsskatteloven)
    .fra(2021, OvernattingsOgServeringsstedBeregning)
    .bareI(2021, EgenkapitalavstemmingBeregning2021)
    .fra(2022, EgenkapitalavstemmingBeregning)
    .fra(2023, SaerskiltAnleggsmiddelForSelskapOmfattetAvPetroleumsskatteloven)
    .fra(2023, GevinstOgTapskontoVedRealisasjonAvAnleggsmiddelOmfattetAvPetroleumsskatteloven)
    .bareI(2023, OmsetningOgEtterbetalingMvISamvirkeforetak)
    .fra(2024, OmsetningOgEtterbetalingMvISamvirkeforetakFra2024)
    .fra(2024, SpesifikasjonAvOrdinaertAnleggsmiddelIHavbruksvirksomhet)
    .bareI(2024, SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft2024)
    .fra(2025, SpesifikasjonAvOrdinaertAnleggsmiddelILandbasertVindkraft)
    .fra(2024, SpesifikasjonAvGrunnrenteinntektFra2024)
    .fra(2022, OevrigTilVisningForSpesifikasjonAvAnleggsmiddel)
    .fra(2023, SpesifikasjonAvForskjellForSelskapOmfattetAvPetroleumsskatteloven)
    .fra(2023, PermanentForskjellForVirksomhetOmfattetAvPetroleumsskatteloven)
    .fra(2023, MidlertidigForskjellPetroleumsskattelovenSaerskattegrunnlagFraVirksomhetPaaSokkel)
    .fra(2023, MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaSokkel)
    .fra(2023, MidlertidigForskjellPetroleumsskattelovenAlminneligInntektFraVirksomhetPaaLand)
    .fra(2023, MidlertidigForskjellPetroleumsskattelovenResultatAvFinansinntektOgFinanskostnadMv)
    .fra(2023, Omvurderingskonto)
    .bareI(2023, FordeltBeregnetNaeringsinntektPetroleum2023)
    .fra(2024, FordeltBeregnetNaeringsinntektPetroleum)
    .bareI(2023, SpesifikasjonAvGrunnrenteinntekt)
    .fra(2023, Naturressursskatt)
    .fra(2023, Eiendomsskattegrunnlag)
    .fra(2024, GrunnrenteskattHavbruk)
    .bareI(2023, GrunnrenteskattHavbruk2023)
    .fra(2023, SpesifikasjonVedBeregningAvRentefradragsramme)
    .fra(2023, SpesifikasjonAvLoennsOgPensjonskostnad)
    .fra(2023, RevisorsBekreftelse)
    .fra(2024, GrunnrenteinntektLandbasertVindkraft)
    .fra(2024, SkogfondForSelskap)
    .fra(2025, Jordbrukskonto)
    .sorterPaaAarFra(2020).mapValues { Kalkylesamling(*it.value.toTypedArray()) }
