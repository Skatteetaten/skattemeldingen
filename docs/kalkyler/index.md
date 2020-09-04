---
icon: "cloud"
title: "Kalkyler"
description: ""
---

# Kalkyler

Dette er en oversikt over de kalkylene som er laget til nå. Merk at Skatteetaten bruker dette som en kjørbar spesifikasjon og forbeholder seg retten til
å gjøre endringer i formen på disse kallylene. Formålet med å inkludere dem her er at de sammenfaller at spesifikasjonen er identisk med hvordan det er spesifisert
i vår egen implemementasjon av disse beregningene.

## GevinstOgTapskonto

```kotlin
private val forekomsterGevinstOgTapskonto = itererForekomster forekomsterAv gevinstOgTapskonto
    private val grunnlagForAaretsInntektsOgFradragsfoeringKalkyle = forekomsterGevinstOgTapskonto forVerdi {
        it.inngaaendeVerdi + it.sumGevinstVedRealisasjonOgUttak - it.sumTapVedRealisasjonOgUttak + it.sumAnnenBetingetSkattefriAvsetning somFelt gevinstOgTapskonto.grunnlagForAaretsInntektsOgFradragsfoering
    }

    private val inntektFraGevinstOgTapskontoKalkyle = forekomsterGevinstOgTapskonto forVerdier (
            listOf(
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErStoerreEnn(15000)) * f.satsForInntektsfoeringOgInntektsfradrag.prosent() somFelt gevinstOgTapskonto.inntektFraGevinstOgTapskonto },
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMellom(0, 15000)) somFelt gevinstOgTapskonto.inntektFraGevinstOgTapskonto },
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMellom(-15000, -1)) somFelt gevinstOgTapskonto.inntektsfradragFraGevinstOgTapskonto.abs() },
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(derVerdiErMindreEnn(-15000)) * f.satsForInntektsfoeringOgInntektsfradrag.prosent() somFelt gevinstOgTapskonto.inntektsfradragFraGevinstOgTapskonto.abs() }
            ))

    private val utgaaendeVerdiKalkyle = forekomsterGevinstOgTapskonto forVerdier (
            listOf(
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(Specifications.erPositiv()) - f.inntektFraGevinstOgTapskonto somFelt gevinstOgTapskonto.utgaaendeVerdi },
                    { f -> f.grunnlagForAaretsInntektsOgFradragsfoering.der(Specifications.erNegativ()) + f.inntektsfradragFraGevinstOgTapskonto somFelt gevinstOgTapskonto.utgaaendeVerdi }

            ))

    private val kalkyletre =  Kalkyletre(grunnlagForAaretsInntektsOgFradragsfoeringKalkyle, inntektFraGevinstOgTapskontoKalkyle, utgaaendeVerdiKalkyle)
```

## Spesifikasjon av balanse

```kotlin
// Denne genererer summer på forekomstnivå; for forekomsten som har verdiene
    private val forekomsterSaldogruppeAogC: SammensattUttrykk<saldoavskrevetAnleggsmiddel> = saldoAvskrevetForekomster filter { it.saldogruppe harEnAvVerdiene listOf("a", "c") }
    private val forekomsterSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> = saldoAvskrevetForekomster filter { it.saldogruppe harVerdi "b" }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC: SammensattUttrykk<saldoavskrevetAnleggsmiddel> = forekomsterSaldogruppeAogC forVerdi {
        it.inngaaendeVerdi + it.nyanskaffelse + it.paakostning - it.offentligTilskudd - it.justeringAvInngaaendeMva - it.nedskrevetVerdiAvUtskilteDriftsmidler - it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene + it.vederlagVedRealisasjonOgUttakInntekstfoertIAar somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
    }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> = forekomsterSaldogruppeB forVerdi {
        it.inngaaendeVerdi + it.nyanskaffelse - it.nedskrevetVerdiAvUtskilteDriftsmidler - it.vederlagVedRealisasjonOgUttak + it.vederlagVedRealisasjonOgUttakInntekstfoertIAar somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
    }

    internal val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogC = forekomsterSaldogruppeAogC forVerdier (
            listOf(
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning },
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(-14999)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo },
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMellom(-14999, 0)) somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo }

            ))

    internal val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
            listOf(
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning }

            ))

    private val utgaaendeVerdiSaldogruppeAogC = forekomsterSaldogruppeAogC forVerdier (
            listOf(
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnnEllerLik(0)) - f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi },
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(0)) + f.aaretsInntektsfoeringAvNegativSaldo somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi }

            ))

    private val utgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
            listOf(
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnnEllerLik(0)) - f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi },
                    { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(0)) + f.gevinstOverfoertTilGevinstOgTapskonto somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi }

            ))

    private val kalkyletreSaldogruppeAogC = Kalkyletre(grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC, avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogC, utgaaendeVerdiSaldogruppeAogC)
    private val kalkyletreSaldogruppeB = Kalkyletre(grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB, avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB, utgaaendeVerdiSaldogruppeB)

    val kalkyle = Kalkyletre(kalkyletreSaldogruppeAogC, kalkyletreSaldogruppeB)
```

## Resultatregnskapet

```kotlin
val salgsinntekterKalkyle =
            summer forekomsterAv salgsinntekt forVerdi { it.beloep }

    val annenDriftsinntektKalkyle =
            summer forekomsterAv annenDriftsinntekt forVerdi { it.beloep }

    val sumDriftsinntekterKalkyle: Kalkyle =
            (salgsinntekterKalkyle + annenDriftsinntektKalkyle) verdiSom sumDriftsinntekt

    val driftskostnadstypeKalkyle = summer gitt ForekomstOgVerdi(virksomhet, { it.regnskapspliktstype harEnAvVerdiene listOf("1", "5") }) forekomsterAv saldoavskrevetAnleggsmiddel forVerdi { it.aaretsAvskrivning } verdiSom NyForekomst(annenDriftskostnad, "6000", annenDriftskostnad.beloep, {
        listOf(
                FeltOgVerdi(it.type, "6000")
        )
    })

    private val summeringGevinstOgTap = summer gitt ForekomstOgVerdi(virksomhet, { it.regnskapspliktstype harEnAvVerdiene listOf("1", "5") })

    internal val annenDriftsinntektstypeInntektKalkyle = summer forekomsterAv gevinstOgTapskonto forVerdi { it.inntektFraGevinstOgTapskonto } verdiSom NyForekomst(forekomststTypeSpesifikasjon = annenDriftsinntekt, idVerdi = "3890", feltKoordinat = annenDriftsinntekt.beloep, feltMedFasteVerdier =
    {
        listOf(
                FeltOgVerdi(it.type, "3890")
        )
    }
    )

    private val annenDriftsinntektstypeFradragKalkyle = summeringGevinstOgTap forekomsterAv gevinstOgTapskonto forVerdi { it.inntektsfradragFraGevinstOgTapskonto } verdiSom NyForekomst(annenDriftsinntekt, "3890", annenDriftsinntekt.beloep, {
        listOf(
                FeltOgVerdi(it.type, "3890")
        )
    })

    val sumVarekostnadKalkyle = summer forekomsterAv varekostnad forVerdi { it.beloep }
    val sumLoennskostnadKalkyle = summer forekomsterAv loennskostnad forVerdi { it.beloep }
    val sumAnnenDriftskostnadKalkyle = summer forekomsterAv annenDriftskostnad forVerdi { it.beloep }

    internal val sumDriftskostnaderKalkyle =
            (sumVarekostnadKalkyle + sumLoennskostnadKalkyle + sumAnnenDriftskostnadKalkyle) verdiSom sumDriftskostnad

    val sumFinansinntektKalkyle = summer forekomsterAv finansinntekt forVerdi { it.beloep } verdiSom sumFinansinntekt
    val sumFinanskostnadKalkyle = summer forekomsterAv finanskostnad forVerdi { it.beloep } verdiSom sumFinanskostnad

    val sumEkstraordinaerePosterInntektKalkyle = summer forekomsterAv ekstraordinaerPost forVerdi { it.inntekt }
    val sumEkstraordinaerePosterKostnadKalkyle = summer forekomsterAv ekstraordinaerPost forVerdi { it.kostnad }
    val sumEkstraordinaerePosterKalkyle =
            sumEkstraordinaerePosterInntektKalkyle - sumEkstraordinaerePosterKostnadKalkyle verdiSom sumEkstraordinaerPost

    val skattekostnadKalkyle = summer forekomsterAv skattekostnad forVerdi { it.skattekostnad }
    val negativSkattekostnadkalkyle = summer forekomsterAv skattekostnad forVerdi { it.negativSkattekostnad }
    val sumSkattekostnadKalkyle =
            (negativSkattekostnadkalkyle - skattekostnadKalkyle) verdiSom sumSkattekostnad

    val aarsresultatKalkyle =
            (sumDriftsinntekterKalkyle - sumDriftskostnaderKalkyle) + (sumFinansinntektKalkyle - sumFinanskostnadKalkyle) + (sumEkstraordinaerePosterKalkyle + sumSkattekostnadKalkyle) verdiSom aarsresultat

    val tre = Kalkyletre(
            driftskostnadstypeKalkyle,
            annenDriftsinntektstypeInntektKalkyle,
            annenDriftsinntektstypeFradragKalkyle,
            sumDriftsinntekterKalkyle,
            sumDriftskostnaderKalkyle,
            sumFinansinntektKalkyle,
            sumFinanskostnadKalkyle,
            sumEkstraordinaerePosterKalkyle,
            sumSkattekostnadKalkyle,
            aarsresultatKalkyle)
```
