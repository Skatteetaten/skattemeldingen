---
icon: "cloud"
title: "Informasjonsmodeller og kodeverk"
description: "Informasjonsmodeller og kodeverk"
---

# Generelt

Skattemeldingen og næringsopplysninger skal leveres som XML-filer. Innhold og format på XML-filene er spesifisert
gjennom XML Schema Definition, XSD.

Følgende informasjonsmodeller skal brukes for fastsetting: 

- skattemelding for formues- og inntektsskatt for personlige skattepliktige
- skattemelding for formues- og inntektsskatt for upersonlige skattepliktige
- selskapsmeldingen for selskap med deltaker fastsetting (SDF)
- næringsspesifikasjon

I tillegg kan det bli aktuelt å legge ved en metadatafil med teknisk innhold.


![Skattemeldingsvarianter.PNG](Skattemeldingsvarianter.PNG)


# Årsrevisjon

XSD-spesifikasjonene vil gjennomgå en årlig revisjon slik at det normalt kommer en ny versjon av disse per inntektsår.
Skatteetaten har valgt å ikke ha inntektsåret i filnavnet, men derimot ha et løpende versjonsnummer som er basert på 
"semantisk versjonering".

## Semantic Versioning:

Given a version number MAJOR.MINOR.PATCH, increment the:

- MAJOR version when you make incompatible changes.
- MINOR version when you add functionality in a backwards compatible manner, and
- PATCH version when you make backwards compatible bug fixes.

Innenfor et inntektsår kan det forutsettes av det kun kommer MINOR- og PATCH-versjoner.

## Kompakt XSD
Alle XSD-ene kommer i en "kompakt utgave" (se filnavnet). De kompakte utgavene inneholder ikke referanser til begrepskatalogen.
Siden de ikke inneholder referansen til begrepskatalogen, så vil det være enklere å følge med på funksjonelle endringer fra en versjon til neste ved hjelp av GIT-funksjonalitet.


# Ekstra beskrivelse for inntektsåret 2021
Inntektsåret 2021 var et pilotår, og en del feltnavn har endret seg for inntektsåret 2022. 

## XSD Næringsspesifikasjon for 2021

Versjon 2.0.0 av denne XSD-en
heter [naeringsspesifikasjon_v2_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_ekstern.xsd)
og [naeringsspesifikasjon_v2_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_kompakt_ekstern.xsd)

I Resultatregnskap og Balanse benyttes kodelister til å spesifisere hvilke konti som kan benyttes. I tillegg til
kontonummer inneholder kodelistene regnskapspliktstype (1 = Ikke årsregnskapspliktig, 2 = Årsregnskapspliktig, 5 =
Begrenset regnskapsplikt. Tilsvarer dagens næringsoppgave 1, 2 og 5). Dette for å indikere hvilke konti som er relevante
for det enkelte foretaket. Noen konti er for eksempel kun relevante for foretak med årsregnskapsplikt.
Det er en kodeliste pr. kontoklasse/gruppe (Resultatregnskap: Salgsinntekt, AnnenDriftsinntekt, Varekostnad,
Lønnskostnad, AnnenDriftskostnad, Finansinntekt, Finanskostnad, EkstraordinærPost, Skattekostnad. Balanse:
Anleggsmidler, Omløpsmidler, Langsiktiggjeld, Kortsiktiggjeld og Egenkaptial).
Alle kodelistene er samlet i en fysisk fil hvor den enkelte kodeliste kalles "underkodeliste".

## XSD Skattemelding for formues- og inntektsskatt for upersonlige skattepliktige for 2021

En tekstlig beskrivelse av overgangen mellom informasjonselementer i XSD-en og postnummer/OR-id i eksisterende
RF-skjemaer finnes
her: [veiledning_fraRFSkjemaTilNæringsspesifikasjon_2021](https://github.com/Skatteetaten/skattemeldingen/blob/master/docs/informasjonsmodell/veiledning_fraRFSkjemaTilN%C3%A6ringsspesifikasjon_2021.xlsx)

# XSD Respons fra validering

Valideringstjenesten vil returnere en respons som beskriver avvik og feil. Et eksempel på en slik respons er vist i
kapittelet om Valideringstjenesten, og XSD for denne tjenesten er beskrevet i seksjonen
for [API](../api-v2/README.md).

# XSD Tilbakemelding



Etter innsending av skattemelding/næringsopplysninger vil skatteetaten kvittere med en [tilbakemeldingsfil](../../src/resources/xsd/skattemeldingognaeringsspesifikasjonresponse_v2.xsd) i Innboksen i
Altinn som inneholder status og eventuelle avvik.

# Kodelister

En oversikt over kodelistene som er definert så langt finnes i [katalogen kodeliste](../../src/resources/kodeliste)

# Oversikt over inntektsår og XSD-versjoner

OBS! Det er egne regler for hvilken versjon en skal bruke når en gjør forhåndsfastsetting! Tabellen nedenfor gjelder kun
normal fastsetting

| Inntektsår | Skattemelding personlig                                                                | Skattemelding upersonlig                                                                                 | Selskapsmeldingen                                                                                                                                  | Næringspesifikasjon                                                                                  | 
|:-----------|:---------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------|
| 2020       | [skattemelding_v8_ekstern.xsd](../../src/resources/xsd/skattemelding_v8_ekstern.xsd)   | NA                                                                                                       | NA                                                                                                                                                 | [naeringsopplysninger_v1_ekstern.xsd](../../src/resources/xsd/naeringsopplysninger_v1_ekstern.xsd)   |
| 2021       | [skattemelding_v9_ekstern.xsd](../../src/resources/xsd/skattemelding_v9_ekstern.xsd)   | [skattemeldingUpersonlig_v1_ekstern.xsd](../../src/resources/xsd/skattemeldingUpersonlig_v1_ekstern.xsd) | NA                                                                                                                                                 | [naeringsspesifikasjon_v2_ekstern.xsd](../../src/resources/xsd/naeringsspesifikasjon_v2_ekstern.xsd) |
| 2022       | [skattemelding_v10_ekstern.xsd](../../src/resources/xsd/skattemelding_v10_ekstern.xsd) | [skattemeldingUpersonlig_v2_ekstern.xsd](../../src/resources/xsd/skattemeldingUpersonlig_v2_ekstern.xsd) | [selskapsmeldingSelskapMedDeltakerfastsetting_v1_ekstern.xsd](../../src/resources/xsd/selskapsmeldingSelskapMedDeltakerfastsetting_v1_ekstern.xsd) | [naeringsspesifikasjon_v3_ekstern.xsd](../../src/resources/xsd/naeringsspesifikasjon_v3_ekstern.xsd) |  
| 2023       | [skattemelding_v11_ekstern.xsd](../../src/resources/xsd/skattemelding_v11_ekstern.xsd) | [skattemeldingUpersonlig_v3_ekstern.xsd](../../src/resources/xsd/skattemeldingUpersonlig_v3_ekstern.xsd) | [selskapsmeldingSelskapMedDeltakerfastsetting_v2_ekstern.xsd](../../src/resources/xsd/selskapsmeldingSelskapMedDeltakerfastsetting_v2_ekstern.xsd) | [naeringsspesifikasjon_v4_ekstern.xsd](../../src/resources/xsd/naeringsspesifikasjon_v4_ekstern.xsd) |  


Modeller som kan komme i retur etter beregning. Hvilke modeller som kommer i retur avhenger av om det er personlig eller
upersonlig skattemelding som har blitt beregnet.

| Inntektsår | Beregnet skatt personlig                                               | Beregnet skatt upersonlig                                                                                    | Skatteberegningsgrunnlag                                                                   | Summert skattegrunnlag for visning upersonlig                                                                                                    |
|------------|:-----------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| 2020       | [beregnet_skatt_v2.xsd](../../src/resources/xsd/beregnet_skatt_v2.xsd) | NA                                                                                                           | [skatteberegningsgrunnlag_v6.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v6.xsd) | NA                                                                                                                                               |
| 2021       | [beregnet_skatt_v3.xsd](../../src/resources/xsd/beregnet_skatt_v3.xsd) | [beregnetskatt_upersonligskattyter_v2.xsd](../../src/resources/xsd/beregnetskatt_upersonligskattyter_v2.xsd) | [skatteberegningsgrunnlag_v7.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v7.xsd) | [summertSkattegrunnlagForVisning_upersonligskattyter_v1.xsd](../../src/resources/xsd/summertSkattegrunnlagForVisning_upersonligskattyter_v1.xsd) |
| 2022       | [beregnet_skatt_v4.xsd](../../src/resources/xsd/beregnet_skatt_v4.xsd) | [beregnetskatt_upersonligskattyter_v4.xsd](../../src/resources/xsd/beregnetskatt_upersonligskattyter_v4.xsd) | [skatteberegningsgrunnlag_v7.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v7.xsd) | [summertSkattegrunnlagForVisning_upersonligskattyter_v2.xsd](../../src/resources/xsd/summertSkattegrunnlagForVisning_upersonligskattyter_v2.xsd) | 
| 2023       | [beregnet_skatt_v5.xsd](../../src/resources/xsd/beregnet_skatt_v5.xsd) | [beregnetskatt_upersonligskattyter_v4.xsd](../../src/resources/xsd/beregnetskatt_upersonligskattyter_v4.xsd) | [skatteberegningsgrunnlag_v8.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v8.xsd) | [summertSkattegrunnlagForVisning_upersonligskattyter_v2.xsd](../../src/resources/xsd/summertSkattegrunnlagForVisning_upersonligskattyter_v2.xsd) |

# Poster, felter og temaer i ny skattemelding for næringsdrivende

Beskrivelse av poster og felter i ny
skattemelding [ligger på skatteetaten.no](https://www.skatteetaten.no/bedrift-og-organisasjon/skatt/skattemelding-naringsdrivende/ny-skattemelding/poster-felter-og-temaer/)
Det er også laget
en [oversikt over hvilke temaer i næringsspesifikasjonen som er aktuell for hver type skattemelding](temaINaeringsspesifikasjonen.md) 
