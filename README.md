# Leseveiledning for systemleverandører som skal utvikle integrasjon for utfylling og innsending av skattemelding og næringsspesifikasjon

Her finner du informasjon om hvordan du skal tilpasse et sluttbrukersystem for utfylling og innsending av ny skattemelding for lønnstakere/pensjonister, personlig næringsdrivende, selskap (AS mfl).

Området er delt inn i to hoveddeler:

**Docs** – Beskrivelser av informasjonsmodell, API, test, valideringer, tekster (som ikke er beskrevet i kode)

**Src** – kodefiler for informasjonsmodell (XSD, kodelister), kall/respons (XSD) kalkyler (beregning) og eksempelfiler

Innhold i de ulike sidene/katalogene:

## Forside
Denne leseveiledningen + funksjonell beskrivelse (PDF fil). Vi anbefaler at du leser denne beskrivelsen først.

## Docs

**api-v2** – API beskrivelser for inntektsår 2021 - må brukes for 2021 og for upersonlig.

**faq** – spørsmål/svar fra brukerstøttekanal (teknisk/fag)

**informasjonsmodell** – overordnet beskrivelse, UML modeller, mapping fra skjema/post til tema/felt

**kontroller** – dokumentasjon på kontroller i valideringstjenesten utover beregninger/kalkyler

**tekster** – lede og hjelpetekster i skattemeldingene og næringsspesifikasjon

**test** – informasjon om hvordan komme i gang med test/testklienter

## Src

**Kotlin** – overordnet beskrivelse av beregninger, kodefiler for beregning/kalkyler

**XSD for skattemelding og næringsspesifikasjon**

Næringsspesifikasjon_v2 – XSD for næringsspesifikasjon 2021

skattemeldingUpersonlig_v1 – XSD for skattemelding AS 2021

skattemelding_v9 –  XSD skattemelding person 2021


**XSD for konvolutter**

V2 versjoner (må brukes for 2021, kan brukes for 2020):

skattemeldingognaeringsspesifikasjonrequest_v2 – xsd for å validere og laste opp i Altinn

skattemeldingognaeringsspesifikasjonforespoerselresponse_v2 – XSD for hente utkast/gjeldende skattemelding

skattemeldingognaeringsspesifikasjonresponse_v2 - xsd for valideringsresultat


Øvrige XSDer:

Beregnet_skatt_v3- XSD for beregnet skatt

no.skatteetaten.kodelistebibliotek.domain.v1 – xsd for begrepsreferanse

skatteberegningsgrunnlag_v6 – XSD for summert skattegrunnlag for visning


**Kodelister**

Tilhørende XSD for næringsspesifikasjon_v2

**Eksempel**

**V2**

Naeringspesifikasjon-AS-v2.xml – eksempelfil næringsspesifikasjon 2021 for et AS

Naeringspesifikasjon-enk-v2.xml - eksempelfil næringsspesifikasjon 2021 for et ENK

personligSkattemeldingOgNaeringsspesifikasjonRequest.xml – eksempel på konvolutt forespørsel v2

personligSkattemeldingV9Eksempel.xml – eksempelfil på skattemelding personlig for 2021

personligSkattemeldingV9EksempelVedlegg.xml – eksempelfil på skattemelding personlig 2021 med vedlegg.

personligSkattemeldingerOgNaeringsspesifikasjonResponse.xml - eksempel på konvolutt respons v2

upersonligSkattemeldingV1.xml – eksempelfil på skattemelding upersonlig (AS) for 2021


[Funksjonell spesifikasjon.pdf - oppdatert 29. mars 2023](Funksjonell%20spesifikasjon%20-%2029.03.2023.pdf)

## Brukerstøtte

Send oss en [henvendelse](https://eksternjira.sits.no/servicedesk/customer/user/login?destination=plugins/servlet/desk/site/global) her hvis du skal bestille tilgang til API for skattemeldingen. Det er også her dere som allerede er integrert mot skattemeldingen kan sende inn henvendelser til Skatteetaten.  
Ved å få tilgang aksepterer dere samtidig [Bruksvilkår for tilgang til Skatteetatens tjenester - Leverandør av sluttbrukersystem ](https://www.skatteetaten.no/samarbeidspartnere/sluttbrukersystemer/bruksvilkar/)