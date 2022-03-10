package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.HarKalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.beregning.api.Kalkyletre
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetIdentifikatorHaandtering
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.naeringsinntekt.FordeltBeregnetNaeringsinntektUnntak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.fordeltBeregnetInntekt.personinntekt.FordeltBeregnetPersoninntektUnntak
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.forskjeller.MidlertidigeForskjellerBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.forskjeller.PermanenteForskjellerBeregning
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.spesifikasjonAvAnleggsmiddel.GevinstOgTapskonto
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.spesifikasjonAvAnleggsmiddel.IkkeAvskrivbartAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.spesifikasjonAvAnleggsmiddel.LineaertavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.spesifikasjonAvAnleggsmiddel.SaldoavskrevetAnleggsmiddel
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler.v2_2021.spesifikasjonAvAnleggsmiddel.TransportmiddelNaering

/**
 * Toppnivå - referanser til alle kalkyler som skal kjøres. Kalkyler som brukes av andre kalkyler
 * (lavere nivå) refereres ikke direkte her.
 *
 * Det finnes foreløpig ikke noe logikk som kan fange opp at noen beregninger i kalkyletreet kan ha blitt kjørt i andre sammenhenger
 * slik at man ikke trenger å kjøre dem igen.
 */
object DefaultKalkyletre2021 : Kalkyletre(
    GevinstOgTapskonto,
    LineaertavskrevetAnleggsmiddel,
    SaldoavskrevetAnleggsmiddel,
    SpesifikasjonAvOmloepsmiddel,
    TransportmiddelNaering,
    SkogOgToemmerkonto,
    Resultatregnskapet,
    IkkeAvskrivbartAnleggsmiddel,
    PermanenteForskjellerBeregning, // Kun relevant for regnskapspliktstype 2 (kun de som sender inn forskjeller)
    MidlertidigeForskjellerBeregning, // Kun relevant for regnskapspliktstype 2 (kun de som sender inn forskjeller)
    HarKalkyletre { Kalkyletre(SkattemessigResultatBeregning) },
    HarKalkyletre { Kalkyletre(FordeltBeregnetNaeringsinntektUnntak) }, // Maskinell behandling ved én forekomst for alle, unntak kun for enkeltpersonforetak
    FordeltBeregnetNaeringsinntektBeregning,
    // TODO: FordeltBeregnetPersoninntektUnntak, FordeltBeregnetIdentifikatorHaandtering og FordeltBeregnetPersoninntektBeregning
    //  er tre separate InlineKalkyler() som trengs for å beregne fordeltBeregnetPersoninntekt. De burde nok slås sammen
    //  til en InlineKalyle() (men koden kan fortsatt gjerne være spredt over flere filer)
    HarKalkyletre { Kalkyletre(FordeltBeregnetPersoninntektUnntak) }, // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    HarKalkyletre { Kalkyletre(FordeltBeregnetIdentifikatorHaandtering) }, // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    HarKalkyletre { Kalkyletre(FordeltBeregnetPersoninntektBeregning) }, // Kun relevant for enkeltpersonforetak når det finnes fordeltBeregnetNaeringsinntekt
    Balanse,
    BalanseFormueOgGjeld,
    HarKalkyletre { Kalkyletre(OvernattingsOgServeringsstedBeregning) },
    HarKalkyletre { Kalkyletre(EgenkapitalavstemmingBeregning) },
)
