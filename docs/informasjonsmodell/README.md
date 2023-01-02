---
icon: "cloud"
title: "Informasjonsmodeller og kodeverk"
description: "Informasjonsmodeller og kodeverk"
---

# Generelt
Skattemeldingen og næringsopplysninger skal leveres som XML-filer. Innhold og format på XML-filene er spesifisert gjennom XML Schema Definition, XSD. 

For inntektsåret 2021 er det 3 XSD-er som er aktuelle:

- skattemelding for formues- og inntektsskatt for personlige skattepliktige
- skattemelding for formues- og inntektsskatt for upersonlige skattepliktige
- næringsspesifikasjon

I tillegg kan det bli aktuelt å legge ved en metadatafil med teknisk innhold.

For inntektsårene etter 2021 vil det også komme andre varianter av skattemeldinger for å støtte ulike selskapsformer slik at totalsamlingen relevante skattemeldinger blir:

![Skattemeldingsvarianter.PNG](Skattemeldingsvarianter.PNG)

I utgangspunktet er planen at XSD for næringsspesifikasjon etterhvert skal inneholde alle de forholdene som er aktuelle å innrapportere fra næringsdrivende og selskap. Det kan imidlertid bli aktuelt at det legges til noen tilleggs-XSD-er for helt spesielle forhold.

# Årsrevisjon
XSD-spesifikasjonene vil gjennomgå en årlig revisjon slik at det normalt kommer en ny versjon av disse per inntektsår. Skatteetaten har valgt å ikke ha inntektsåret i filnavnet, men derimot ha et løpende versjonsnummer som er basert på "semantisk versjonering". 

## Semantic Versioning:

Given a version number MAJOR.MINOR.PATCH, increment the:

- MAJOR version when you make incompatible changes.
- MINOR version when you add functionality in a backwards compatible manner, and
- PATCH version when you make backwards compatible bug fixes.

Innenfor et inntektsår kan det forutsettes av det kun kommer MINOR- og PATCH-versjoner.

# XSD Skattemelding for formues- og inntektsskatt for personlige skattepliktige for 2021
Versjon 9.0.0 av denne XSD'en heter [skattemelding_v9_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemelding_v9_ekstern.xsd) og [skattemelding_v9_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemelding_v9_kompakt_ekstern.xsd)

# XSD Næringsspesifikasjon for 2021
Versjon 2.0.0 av denne XSD'en heter [naeringsspesifikasjon_v2_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_ekstern.xsd) og [naeringsspesifikasjon_v2_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_kompakt_ekstern.xsd)

I Resultatregnskap og Balanse benyttes kodelister til å spesifisere hvilke konti som kan benyttes. I tillegg til kontonummer inneholder kodelistene regnskapspliktstype (1 = Ikke årsregnskapspliktig, 2 = Årsregnskapspliktig, 5 = Begrenset regnskapsplikt.  Tilsvarer dagens næringsoppgave 1, 2 og 5).  Dette for å indikere hvilke konti som er relevante for det enkelte foretaket.  Noen konti er for eksempel kun relevante for foretak med årsregnskapsplikt.
Det er en kodeliste pr. kontoklasse/gruppe (Resultatregnskap: Salgsinntekt, AnnenDriftsinntekt, Varekostnad, Lønnskostnad, AnnenDriftskostnad, Finansinntekt, Finanskostnad, EkstraordinærPost, Skattekostnad.  Balanse: Anleggsmidler, Omløpsmidler, Langsiktiggjeld, Kortsiktiggjeld og Egenkaptial).
Alle kodelistene er samlet i en fysisk fil hvor den enkelte kodeliste kalles "underkodeliste". 

# XSD Skattemelding for formues- og inntektsskatt for upersonlige skattepliktige for 2021

En tekstlig beskrivelse av overgangen mellom informasjonselementer i XSD'en og postnummer/OR-id i eksisterende RF-skjemaer finnes her: [veiledning_fraRFSkjemaTilNæringsspesifikasjon_2021](https://github.com/Skatteetaten/skattemeldingen/blob/master/docs/informasjonsmodell/veiledning_fraRFSkjemaTilN%C3%A6ringsspesifikasjon_2021.xlsx)

# XSD Respons fra validering
Valideringstjenesten vil returnere en respons som beskriver avvik og feil. Et eksempel på en slik respons er vist i kapittelet om Valideringstjenesten, og XSD for denne tjenesten er beskrevet i seksjonen for [API](https://skatteetaten.github.io/skattemeldingen/documentation/api).

# XSD Tilbakemelding
Etter innsending av skattemelding/næringsopplysninger vil skatteetaten kvittere med en tilbakemeldingsfil i Innboksen i Altinn som inneholder status og eventuelle avvik. 

# Kodelister
En oversikt over kodelistene som er definert så langt finnes i [Oversikten over kodelister](https://github.com/Skatteetaten/skattemeldingen/tree/master/src/resources/kodeliste)


# Oversikt over inntektsår og xsd versjoner

OBS! Det er egne regler for hvilken versjon en skal bruke når en gjør forhåndsfastsetting! Tabellen nedenfor gjelder kun normal fastsetting

| Inntektsår | Skattemelding personlig                                                                | Skattemelding upersonlig                                                                                 | Selskapsmeldingen                                                                                                                                  | Næringspesifikasjon                                                                                  | 
|:-----------|:---------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------|
| 2020       | [skattemelding_v8_ekstern.xsd](../../src/resources/xsd/skattemelding_v8_ekstern.xsd)   | NA                                                                                                       | NA                                                                                                                                                 | [naeringsopplysninger_v1_ekstern.xsd](../../src/resources/xsd/naeringsopplysninger_v1_ekstern.xsd)   |
| 2021       | [skattemelding_v9_ekstern.xsd](../../src/resources/xsd/skattemelding_v9_ekstern.xsd)   | [skattemeldingUpersonlig_v1_ekstern.xsd](../../src/resources/xsd/skattemeldingUpersonlig_v1_ekstern.xsd) | NA                                                                                                                                                 | [naeringsspesifikasjon_v2_ekstern.xsd](../../src/resources/xsd/naeringsspesifikasjon_v2_ekstern.xsd) |
| 2022       | [skattemelding_v10_ekstern.xsd](../../src/resources/xsd/skattemelding_v10_ekstern.xsd) | [skattemeldingUpersonlig_v2_ekstern.xsd](../../src/resources/xsd/skattemeldingUpersonlig_v2_ekstern.xsd) | [selskapsmeldingSelskapMedDeltakerfastsetting_v1_ekstern.xsd](../../src/resources/xsd/selskapsmeldingSelskapMedDeltakerfastsetting_v1_ekstern.xsd) | [naeringsspesifikasjon_v3_ekstern.xsd](../../src/resources/xsd/naeringsspesifikasjon_v3_ekstern.xsd) |


Modeller som kan komme i retur etter beregning. Hvilke modeller som kommer i retur avhenger av om det er personlig eller upersonlig skattemelding som har blitt beregnet

| Inntektsår | Bergnet skatt personlig                                                | Beregnet skatt upersonlig                                                                                    | Skatteberegningsgrunnlag                                                                   |
|:-----------|:-----------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------|
| 2020       | [beregnet_skatt_v2.xsd](../../src/resources/xsd/beregnet_skatt_v2.xsd) | NA                                                                                                           | [skatteberegningsgrunnlag_v6.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v6.xsd) |
| 2021       | [beregnet_skatt_v3.xsd](../../src/resources/xsd/beregnet_skatt_v3.xsd) | [beregnetskatt_upersonligskattyter_v2.xsd](../../src/resources/xsd/beregnetskatt_upersonligskattyter_v2.xsd) | [skatteberegningsgrunnlag_v7.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v7.xsd) |
| 2022       | [beregnet_skatt_v4.xsd](../../src/resources/xsd/beregnet_skatt_v4.xsd) | [beregnetskatt_upersonligskattyter_v4.xsd](../../src/resources/xsd/beregnetskatt_upersonligskattyter_v4.xsd) | [skatteberegningsgrunnlag_v7.xsd](../../src/resources/xsd/skatteberegningsgrunnlag_v7.xsd) |


# Poster, felter og temaer i ny skattemelding for næringsdrivende

Beskrivelse av poster og felter i ny skattemelding [ligger på skatteetaten.no](https://www.skatteetaten.no/bedrift-og-organisasjon/skatt/skattemelding-naringsdrivende/ny-skattemelding/poster-felter-og-temaer/)
