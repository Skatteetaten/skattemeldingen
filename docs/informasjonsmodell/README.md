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

I utgangspunktet er planen at XSD for næringsspesifikasjon etterhvert skal inneholde alle de forholdene som er aktuelle å innrapportere fra næringsdrivende og selskap. Det kan imidlertid bli aktuelt at det legges til noen tilleggs-XSD-er for helt spesielle forhold.

# Årsrevisjon
XSD-spesifikasjonene vil gjennomgå en årlig revisjon slik at det normalt kommer en ny versjon av disse per inntektsår. Skatteetaten har valgt å ikke ha inntektsåret i filnavnet, men derimot ha et løpende versjonsnummer som er basert på "semantisk versjonering". 

## Semantic Versioning:

Given a version number MAJOR.MINOR.PATCH, increment the:

- MAJOR version when you make incompatible changes.
- MINOR version when you add functionality in a backwards compatible manner, and
- PATCH version when you make backwards compatible bug fixes.

Innenfor et inntektsår kan det forutsettes av det kun kommer MINOR- og PATCH-versjoner.

# XSD Skattemelding for formues- og inntektsskatt for personlige skattepliktige for 2021
Versjon  9.0.0 av denne XSD'en heter [skattemelding_v9_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemelding_v9_ekstern.xsd) og [skattemelding_v9_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemelding_v9_kompakt_ekstern.xsd)

# XSD Næringsspesifikasjon for 2021
Versjon 2.0.0 av denne XSD'en heter [naeringsspesifikasjon_v2_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_ekstern.xsd) og [naeringsspesifikasjon_v2_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/naeringsspesifikasjon_v2_kompakt_ekstern.xsd)

I Resultatregnskap og Balanse benyttes kodelister til å spesifisere hvilke konti som kan benyttes.  I tillegg til kontonummer inneholder kodelistene regnskapspliktstype (1 = Ikke årsregnskapspliktig, 2 = Årsregnskapspliktig, 5 = Begrenset regnskapsplikt.  Tilsvarer dagens næringsoppgave 1, 2 og 5).  Dette for å indikere hvilke konti som er relevante for det enkelte foretaket.  Noen konti er for eksempel kun relevante for foretak med årsregnskapsplikt.
Det er en kodeliste pr. kontoklasse/gruppe (Resultatregnskap: Salgsinntekt, AnnenDriftsinntekt, Varekostnad, Lønnskostnad, AnnenDriftskostnad, Finansinntekt, Finanskostnad, EkstraordinærPost, Skattekostnad.  Balanse: Anleggsmidler, Omløpsmidler, Langsiktiggjeld, Kortsiktiggjeld og Egenkaptial).
Alle kodelistene er samlet i en fysisk fil hvor den enkelte kodeliste kalles "underkodeliste". 

# XSD Skattemelding for formues- og inntektsskatt for upersonlige skattepliktige for 2021
Versjon 2.0.0 av denne XSD'en heter [skattemeldingUpersonlig_v1_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemeldingUpersonlig_v1_ekstern.xsd) og [skattemeldingUpersonlig_v1_kompakt_ekstern.xsd](https://github.com/Skatteetaten/skattemeldingen/blob/master/src/resources/xsd/skattemeldingUpersonlig_v1_kompakt_ekstern.xsd)

Eksempel på innsendt XML finnes her: [eksempler](https://github.com/Skatteetaten/skattemeldingen/tree/master/src/resources/eksempler/v2) 

Grafisk fremstilling ligger finnes her: [UML](https://github.com/Skatteetaten/skattemeldingen/tree/master/docs/informasjonsmodell)

En tekstlig beskrivelse av overgangen mellom informasjonselementer i XSD'en og postnummer/OR-id i eksisterende RF-skjemaer finnes her: [veiledning_fraRFSkjemaTilNæringsspesifikasjon_2021](https://github.com/Skatteetaten/skattemeldingen/blob/master/docs/informasjonsmodell/veiledning_fraRFSkjemaTilN%C3%A6ringsspesifikasjon_2021.xlsx)

# XSD Respons fra validering
Valideringstjenesten vil returnere en respons som beskriver avvik og feil. Et eksempel på en slik respons er vist i kapittelet om Valideringstjenesten, og XSD for denne tjenesten er beskrevet i seksjonen for [API](https://skatteetaten.github.io/skattemeldingen/documentation/api).

# XSD Tilbakemelding
Etter innsending av skattemelding/næringsopplysninger vil skatteetaten kvittere med en tilbakemeldingsfil i Innboksen i Altinn som inneholder status og eventuelle avvik. 

# Kodelister
En oversikt over kodelistene som er definert så langt finnes i [Oversikten over kodelister](https://github.com/Skatteetaten/skattemeldingen/tree/master/src/resources/kodeliste)
