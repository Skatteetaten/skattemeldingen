
---
icon: "cloud"
title: "API"
description: "Api-beskrivelser"
---

Det tilbys to sett med API-er:

- Skatteetatens-API: har tjenester for henting- og validering av skattemeldinger, eiendomskalkulator, hent vedlegg og
  foreløpig avregning.
- Altinn3-API: for: har tjenester for opprettelse og innsending av en skattemelding.

![apier.png](apier.png)

# Skatteetatens-API

Skatteetaten har utviklet en demo klient (i python/jupyter notebook) som viser hvordan koble seg på ID-porten og kalle
skatteetatens-API, og sende inn skattemeldingen med vedlegg via Altinn3:
[jupyter notebook](../test/testinnsending/person-enk-med-vedlegg-2021.ipynb)

## Autentisering

Når en skattepliktig skal benytte et sluttbrukersystem for å sende inn skattemeldingen og næringsopplysninger gjennom
API må sluttbrukeren og/eller sluttbrukersystemet være autentisert og autorisert gjennom en påloggingsprosess.

Ved kall til skattemelding-API ønsker Skatteetaten å kjenne til identiteten til innsender. Identiteten til pålogget
bruker, kombinert med informasjon fra Altinn autorisasjon vil avgjøre hvilken person/selskap en pålogget bruker kan
hente skattemeldingen til eller sende inn skattemelding for.

Autentisering skjer enten via ID-porten eller Maskinporten:

- Personlig innlogging vil skje via ID-porten.
- Systemer/maskiner som ønsker å opptre på vegne av en organisasjon kan autentisere seg via maskinporten.

**Merk** at dagens Altinn innlogging med brukernavn/passord vil ikke lenger kunne brukes.<br/>

### ID-porten

Via ID-porten kan selve sluttbrukeren autentiseres og via sitt personnummer.

![idporten.png](idporten.png)

#### Dataflyt og sekvensdiagram

Figuren under skisserer hvordan innloggingsprosessen ser ut:

![ID-porten_program.png](ID-porten_program.png)

#### Registrering av et sluttbrukersystem i ID-porten

Når et sluttbrukersystem initierer en påloggingsprosess mot ID-porten må SBS sende med en klient-ID. Denne klient-id-en
er unik for SBS-typen og vil bli tildelt ved at programvareleverandøren av SBS på forhånd har gjennomført en
registrering (onboarding) i en selvbetjeningsportal hos Digdir/Difi. Dette er beskrevet
her: https://docs.digdir.no/docs/idporten/idporten/idporten_overordnet. Lenken beskriver også standarden OIDC som
ID-porten er basert på.

Under følger en beskrivelse av hvordan en integrasjon kan opprettes hos DigDir slik at programvareleverandør kan få tildelt en klient-ID.

#### Hvordan opprette ID-porten interasjon hos DigDir

- Først må en integrasjon hos DigDir (gamle DIFI) opprettes gjennom
  deres [selvbetjeningsløsning](https://samarbeid.digdir.no/).
- Klikk på integrasjoner under Ver2 og klikk så på knappen "Ny integrasjon".
- Det er denne integrasjonen som deres applikasjon vil snakke med senere når deres sluttbruker skal autentisere seg mot
  ID-porten.
    - Verdien i feltet "Integrasjonens identifikator" (kalt klient-ID over) er en GUID som tildeles av Digdir/Difi og
      som SBS må sende med i kallet til ID-porten.
- Velg _"API-klient"_ under "Digdir-tjeneste".
- Velg så et scope som angir hvilken offentlig API-tjeneste registreringen gjelder for:
    - Klikk på knappen "Rediger Scopes" og velg _"skatteetaten:formueinntekt/skattemelding"_ fra lista over scopes.
    - PS: hvis dere ikke finner scopet _"skatteetaten:formueinntekt/skattemelding"_ i lista må dere
      ta [kontakt med Skatteetaten](mailto:skattemelding-sbs-brukerstotte@skatteetaten.no) slik at vi kan gi dere
      tilgang til scopet (i mellomtiden kan dere forsatt bruke denne integrasjonen da Skatteetaten pt. ikke sjekker
      scope ved validering av access tokenet. Men denne sjekken vil vi på et senere tidspunkt slå på).
- Skriv inn redirect uri-er (kommaseparert og uten mellomrom). Dette er Uri-(er) som klienten får lov å gå til etter
  innlogging (ref. pilnummer 6 i figuren over)
- Sett ønskede verdier for levetiden på autoriasjons-, access og refresh-token.
- Et eksempel på hvordan integrasjonen kan bli seende ut:

![id-porten_integrasjon.png](id-porten_integrasjon.png)

### Maskinporten

Maskinporten sørger for sikker autentisering og tilgangskontroll for datautveksling mellom virksomheter. Løsningen
garanterer identiteten mellom virksomheter og gjør det mulig å binde sammen systemer.

Et sluttbrukersystem som kjører på en sikker server kan integreres i Maskinporten og da være autentisert med sitt
organisasjonsnummer. Det vil da være organisasjonen som autentiserer seg. Hvilken sluttbruker som utfører hvilken
handling i deres system må organisasjonen selv holde kontroll på. En forutsetning for bruk av Maskinporten er derfor at
organisasjonen har bygget et godt system for tilgangskontroll av sine sluttbrukere.

En autentisering gjort via Maskinporten tilrettelegger for høyere grad av automatisering da det ikke krever en personlig
kodebrikke eller liknende. Vi tror Maskinporten vil passe for store selskap og regnskapsførere som skal levere
skattemeldingen for mange.

Bruk av Maskinporten forutsetter at organisasjonen har et virksomhetssertifikat eller en tilsvarende mekanisme. Figuren
under skisserer hvordan samhandlingen fungerer:

![maskinporten.png](maskinporten.png)

Les detaljer om Maskinporten her: https://docs.digdir.no/docs/Maskinporten/maskinporten_guide_apikonsument

## Autorisasjon

API-ene som tilbys vil sjekke at sluttbrukeren eller eier av sluttbrukersystemet har tilgang til å utføre operasjoner
gjennom API-et. Slik tilgangskontroll/autorisering skjer via Altinns autorisasjonskomponent.

Dette betyr at sluttbrukeren eller eier av sluttbrukersystemet må ha de nødvendige rollene i Altinn. Dette blir som i
eksisterende løsninger.

## Oppsummering API endepunkt <a name="table-of-requests">

| TYPE | API path                                                                                                                                                       | Virksomhetssertifikat |
|------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| GET  | [/api/skattemelding/v2/ping](#user-content-ping)                                                                                                               | Ja                    |
| GET  | [/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>](#user-content-hentGjeldende)                                                                         | Ja                    |
| GET  | [/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>?inkluderUtvidetVeiledning=\<inkluderUtvidetVeiledning\>](#user-content-hentGjeldendeUtvidet)          | Ja                    |
| GET  | [/api/skattemelding/v2/\<type\>/\<inntektsaar\>/\<identifikator\>](#user-content-hentType)                                                                     | Ja                    |
| POST | [/api/skattemelding/v2/valider/\<inntektsaar\>/\<identifikator\>](#user-content-valider)                                                                       | Nei                   |
| POST | [/api/skattemelding/v2/validertest/\<inntektsaar\>/\<identifikator\>](#user-content-validerTest)                                                               | Planlagt              |
| GET  | [/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>/vedlegg/\<vedleggId\>](#user-content-hentVedlegg)                                                     | Nei                   |
| GET  | [/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>/gjeldende-fastsetting.pdf](#user-content-hentGjeldendeFastsettingPdf)                                 | Nei                   |
| GET  | [/api/skattemelding/v2/eiendom/soek/\<inntektsår\>?query=\<tekst\>](#user-content-eiendomSoek)                                                                 | Ja                    |
| GET  | [/api/skattemelding/v2/eiendom/formuesgrunnlag/\<inntektsår\>/\<eiendomsidentifikator\>/\<identifikator\>](#user-content-hentFormuesgrunnlag)                  | Ja                    |
| POST | [/api/skattemelding/v2/eiendom/markedsverdi/bolig/\<inntektsår\>/\<eiendomsidentifikator\>](#user-content-markedsverdiBolig)                                   | Ja                    |
| POST | [/api/skattemelding/v2/eiendom/markedsverdi/flerbolig/\<inntektsår\>/\<eiendomsidentifikator\>](#user-content-markedsverdiFlerbolig)                           | Ja                    |
| POST | [/api/skattemelding/v2/avregning/avregn/\<inntektsaar\>/\<identifikator\>](#user-content-avregning)                                                            | Nei                   |
| POST | [/api/skattemelding/v2/eiendom/utleieverdi/\<inntektsår\>/\<eiendomsidentifikator\>](#user-content-markedsverdiIkkeUtiledNaeringseiendom)                      | Ja                    |
| POST | [/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning](#user-content-lagret-skattemelding-for-visning-personlig)                             | Nei                   |
| POST | [/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning-upersonlig/<identifikator>](#user-content-lagret-skattemelding-for-visning-upersonlig) | Nei                   |
| POST | [/api/skattemelding/v2/klargjoerforhaandsfastsetting/\<inntektsaar\>/\<identifikator\>](#user-content-klargjoer-part-for-forhaandsfastsetting)                 | Nei                   |
| POST | [/api/skattemelding/v2/klargjoerpart/\<inntektsaar\>/\<identifikator\>](#user-content-klargjoer-part-som-mangler-utkast)                                       | Nei                   |
| POST | [/api/skattemelding/v2/utsattfristsoeknad/\<identifikator\>](#utsattfrist-skattemeldingen)                                                                     | Nei                   |

| Miljø                             | Adresse                      | Påloggingsmetode      |
|-----------------------------------|------------------------------|-----------------------|
| Test                              | idporten-api-sbstest.sits.no | OIDC                  |
| Test virksomhetssertifikat        | api-sbstest.sits.no          | Virksomhetssertifikat |
| Produksjon                        | idporten.api.skatteetaten.no | OIDC                  |
| Produksjon virksomhetssertifikat | api.skatteetaten.no          | Virksomhetssertifikat |

## Ping tjeneste <a name="ping"></a> [[back up]](#user-content-table-of-requests)

API tilbyr en ping tjeneste som kan kalles for å teste at integrasjonen fungerer.

**URL** : `GET https://<env>/api/skattemelding/v2/ping`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/ping`

**Forespørsel** : `<env>: Miljøspesifikk adresse`

**Respons** :

```json
{
  "ping": "pong"
}
```

## Hent skattemelding <a name="hentGjeldende"></a> [[back up]](#user-content-table-of-requests)

API som returnerer siste gjeldende skattemelding for skattepliktige for gitt inntektsår. Den siste gjeldende
skattemeldingen kan enten være utkast eller fastsatt:

- Utkast er en preutfylt skattemelding Skatteetaten har laget for den skattepliktige basert på innrapporterte data og
  data fra skattemeldingen tidligere år.
- Fastsatt betyr at skattemeldingen er manuelt innlevert eller automatisk innlevert ved utløp av innleveringsfrist.
  Denne kan også inneholde et eller flere myndighetsfastsatte felter. For mer informasjon om myndighetsfastsatte felter se avsnittet under valider skattemeldingen

**URL** : `GET https://<env>/api/skattemelding/v2/<inntektsaar>/<identifikator>/`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/2020/974761076`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige som man spør om skattemeldingen for.`

**Respons** :

- Iht.
  XSD: [skattemeldingognaeringsspesifikasjonforespoerselresponse_v2_kompakt.xsd](/src/resources/xsd/skattemeldingognaeringsspesifikasjonforespoerselresponse_v2_kompakt.xsd)
- Eksempel
  XML: [personligSkattemeldingerOgNaeringsspesifikasjonResponse.xml](/src/resources/eksempler/2021/personligSkattemeldingerOgNaeringsspesifikasjonResponse.xml)

skattemeldingerOgNaeringsopplysningerforespoerselResponse:

- dokumenter – konvolutt for dokumenter

    - skattemeldingdokument – complex type
      -
      type – [valg fra xsd](/src/resources/xsd/skattemeldingognaeringsspesifikasjonforespoerselresponse_v2_kompakt.xsd#L62:L69)
        - id – dokumentidentifikator til dokumentet i skatteetatens system.
        - encoding – kodeliste – [utf-8]
        - content – serialisert dokumentinnhold i base64 encodet format
    - naeringsopplysningsdokument – complex type
        - id – dokumentidentifikator til dokumentet i skatteetatens system
        - encoding – kodeliste – [utf-8]
        - content – serialisert dokumentinnhold i base64 encodet format
    - utvidetVeiledningdokument - complex type
        - id – dokumentidentifikator til dokumentet i skatteetatens system
        - encoding – kodeliste – [utf-8]
        - content – serialisert dokumentinnhold i base64 encodet format

### Utvidet veiledning <a name="hentGjeldendeUtvidet"></a> [[back up]](#user-content-table-of-requests)

Fra og med inntektsår 2022 er det mulig å etterspørre eventuelle ubesvarte utvidede veiledninger som del av dette API'et, som kan sees i response-spesifikasjonen over.

En utvidet veiledning representerer opplysninger som Skatteetaten har om skatteyter som muligens burde vært oppgitt i skattmeldingen, men som ikke er det. Disse opplysningene er gjerne ikke komplette, og kan derfor ikke forhåndsutfylles.

`content`-delen av `utvidetVeiledningdokument` inneholder en skattemelding-xml med foreslått tillegg for skatteyter. Dette er ikke en fullstendig XML ihht. skattemelding-XSD og vil ikke nødvendigvis validere mot sistnevnte.
Normalt inneholder dokumentet informasjon som tilsvarer en entitet i skattemeldingen. I de tilfeller det er flere, så betyr det i praksis at Skatteetaten foreslår at en av entitetene skal legges til (ikke alle).

Det kan være mange `utvidetVeiledningdokument` i `skattemeldingerOgNaeringsopplysningerforespoerselResponse`, en per opplysning Skattetaten ønsker at skatteyter skal ta stilling til.

Bare "ubesvarte" utvidede veiledninger returneres i responsen. En veiledning kan besvares på to måter:
- Gjennom Skatteetatens innleveringsportal for personlige skatteytere, der de kan velge å avvise eller legge til opplysningene
- Ved innsending av `komplett` skattemelding fra et sluttbrukersystem. Når en slik innsending er fullført og fører til fastsetting, så vil alle ubesvarte veiledninger _på fastsettingstidspunktet_ bli besvart som at de er "hentet av SBS".
    - OBS! Siden nye utvidede veiledninger kan oppstå fortløpende, så finnes det en risiko for at det har kommet nye mellom tidspunktet hvor SBS henter veiledningene og fastsetting utføres. Det er derfor en risiko for at veiledninger som ikke har blitt hentet av SBSen og fremvist bruker blir besvart som det.

**URL** : `GET https://<env>/api/skattemelding/v2/<inntektsaar>/<identifikator>?inkluderUtvidetVeiledning=<inkluderUtvidetVeiledning>`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/2022/974761076?inkluderUtvidetVeiledning=true`

**Forespørsel** :

- `<inkluderUtvidetVeiledning>: Hvorvidt man ønsker å hente eventuelle ubesvarte utvidede veiledninger. Settes til 'true' eller 'false'`
    - dersom request parameteren ikke sendes med som del av URL'en, så settes den til 'false' som default
    - denne fungerer bare dersom `<inntektsaar>` er 2022 eller senere. Hvis request parameteren sendes med ved tidligere år så vil den ignoreres.


### Serialisert dokumentinnhold

Det serialiserte dokumentinnholdet er skattemelding eller næringsopplysninger i base64 encodet format.
Dette er formattert i henhold til https://datatracker.ietf.org/doc/html/rfc4648.
Responsen fra Skatteetaten vil alltid inneholde base64 som er formatert på denne måten.

Eksempel (inneholder linjeskift for lesbarhet):

    <content>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNrYXR0ZW1lbGRpbmcgeG1s
    bnM9InVybjpubzpza2F0dGVldGF0ZW46ZmFzdHNldHRpbmc6Zm9ybXVlaW5udGVrdDpza2F0dGVt
    ZWxkaW5nOmVrc3Rlcm46djgiPgogIDxwYXJ0c3JlZmVyYW5zZT4xMjM8L3BhcnRzcmVmZXJhbnNl
    PgogIDxpbm50ZWt0c2Fhcj4yMDIwPC9pbm50ZWt0c2Fhcj4KICA8c2thdHRlbWVsZGluZ09wcHJl
    dHRldD4KICAgIDxicnVrZXJpZGVudGlmaWthdG9yPmlra2UtaW1wbGVtZW50ZXJ0PC9icnVrZXJp
    ZGVudGlmaWthdG9yPgogICAgPGJydWtlcmlkZW50aWZpa2F0b3J0eXBlPnN5c3RlbWlkZW50aWZp
    a2F0b3I8L2JydWtlcmlkZW50aWZpa2F0b3J0eXBlPgogICAgPG9wcHJldHRldERhdG8+MjAyMC0x
    MC0yMVQwNjozMjowNi45OTMwMzlaPC9vcHByZXR0ZXREYXRvPgogIDwvc2thdHRlbWVsZGluZ09w
    cHJldHRldD4KPC9za2F0dGVtZWxkaW5nPg==<content>

## Hent Skattemelding (basert på type)  <a name="hentType"></a> [[back up]](#user-content-table-of-requests)

API som returnerer siste gjeldende skattemeldingen av gitt type for skattepliktige for gitt inntektsår. Følgende type
skattemeldinger er støttet:

- Utkast er en preutfylt skattemelding Skatteetaten har laget for den skattepliktige basert på innrapporterte data og
  data fra skattemeldingen tidligere år.
- Fastsatt betyr at skattemeldingen er manuelt innlevert eller automatisk innlevert ved utløp av innleveringsfrist.
  Dette kan også inneholde et eller flere myndighetsfastsatte felter.

**URL** : `GET https://<env>/api/skattemelding/v2/<type>/<inntektsaar>/<identifikator>/`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/utkast/2020/974761076`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige som man spør om skattemeldingen for.`

**Respons** :

- Iht.
  XSD: [skattemeldingognaeringsspesifikasjonforespoerselresponse_v2_kompakt.xsd](/src/resources/xsd/skattemeldingognaeringsspesifikasjonforespoerselresponse_v2_kompakt.xsd)
- Eksempel
  XML: skattemeldingerognaeringsopplysninger_response.xml (ikke noe eksempel)

For nærmere beskrivelse av felt i XSDen eller hvordan man henter ut utvidede veiledninger, se forrige kapittel.

## Valider skattemelding <a name="valider"></a> [[back up]](#user-content-table-of-requests)

Tjenesten validerer innholdet i en skattemelding og returnerer en respons med eventuelle feil, avvik og advarsler.
Tjenesten vil foreta følgende:

1. Kontroll av meldingsformatet.
2. Kontroll av innholdet og sammensetningen av elementene i skattemeldingen.
3. Beregninger/kalkyler.

Skatteetaten ønsker at valideringstjenesten blir kalt i forkant av innsending av skattemeldingen. Dette for å sikre at skattemeldingen er korrekt og vil mest sannsynligvis bli godkjent ved innsending.
Uansett versjon vil Skatteetaten ikke lagre eller følge opp informasjonen som sendes inn i valideringstjenesten på noen måte. Skatteetaten anser at disse dataene eies av den skattepliktige og ikke av Skatteetaten.

**URL** : `POST https://<env>/api/skattemelding/v2/valider/<inntektsaar>/<identifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/valider/2021/01028312345`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige`

**Body** :

- Iht. XSD: [skattemeldingognaeringsspesifikasjonrequest_v2_kompakt.xsd](/src/resources/xsd/skattemeldingognaeringsspesifikasjonrequest_v2.xsd)
- Eksempel XML: [personligSkattemeldingOgNaeringsspesifikasjonRequest.xml](/src/resources/eksempler/2021/personligSkattemeldingOgNaeringsspesifikasjonRequest.xml)

skattemeldingOgNaeringsspesifikasjonRequest:
- dokumenter
    - dokument
        - type - [skattemeldingPersonlig | skattemeldingUpersonlig | naeringsspesifikasjon | selskapsmeldingSdf | naeringsspesifikasjonReferanse]
        - encoding - kodeliste – [utf-8]
        - content - base64 encodet xml dokument
- dokumentreferanseTilGjeldendeDokument
    - dokumenttype (samme som dokumenter.dokument.type)
    - dokumentidentifikator - referanse hentet fra hentSkattemelding kall. Bruk referansen til skattemeldingen
- inntektsår - fire siffer for inntektsår innsendingen gjelder. Husk å bruk riktig xsd versjon for skattemeldingen og næringspesifikasjon for tilhørende inntektsår
- innsendingsinformasjon
    - innsendingstype - [ikkeKomplett | komplett]
    - opprettetAv - system navn brukt for å gjøre innsendingen
    - innsendingsformaal - [egenfastsetting | klage | endringsanmodning]


**Respons** :

- Iht. XSD: [skattemeldingognaeringsspesifikasjonresponse_v2.xsd](/src/resources/xsd/skattemeldingognaeringsspesifikasjonresponse_v2.xsd)
- Eksempel XML: [personligSkattemeldingerOgNaeringsspesifikasjonResponse.xml](/src/resources/eksempler/2021/personligSkattemeldingerOgNaeringsspesifikasjonResponse.xml)

skattemeldingOgNaeringsspesifikasjonResponse:

- Dokumenter – konvolutt for relevante dokumenter
    - Dokument – complex type
        - Type – kodeliste [skattemeldingPersonligEtterBeregning|beregnetSkattPersonlig|summertSkattegrunnlagForVisningPersonlig|naeringsspesifikasjonEtterBeregning|skattemeldingUpersonligEtterBeregning|beregnetSkattUpersonlig|summertSkattegrunnlagForVisningUpersonlig]
        - Encoding – kodeliste – [utf-8]
        - Content – serialisert dokumentinnhold
- avvikEtterBeregning – konvolutt for avvik funnet etter beregning
    - avvik – complex type
        - avvikstype – [kodeliste](/src/resources/kodeliste/2021/2021_avvikskodeVedValidertMedFeil.xml)
            - OBS! manglerSkattemelding kan henvise til følgende dokumenttyper : skattemelding, skattemeldingUpersonlig og selskapsmelding
        - forekomstidentifikator – identifikator av felt i skattemeldingen
        - mottattVerdi – verdien som ble sendt inn
        - beregnetVerdi – verdien som ble beregnet
        - avvikIVerdi – avviket mellom den innsendte verdien og den beregnede verdien
        - sti – stien til elementet som har avvik
- avvikVedValidering – konvolutt for avvik funnet ved validering
    - avvik – complex type
        - avvikstype – [kodeliste](/src/resources/kodeliste/2021/2021_avvikskodeVedValidertMedFeil.xml)
        - forekomstidentifikator – identifikator av felt i skattemeldingen
        - mottattVerdi – verdien som ble sendt inn
        - beregnetVerdi – verdien som ble beregnet
        - avvikIVerdi – avviket mellom den innsendte verdien og den beregnede verdien
        - sti – stien til elementet som har avvik
- veiledningEtterKontroll – konvolutt for veiledninger
    - veiledning – complex type
        - veiledningstype – kodeliste [kontrollnavnet i SMIA, mulighetsrom kommer fra dem]
        - forekomstidentifikator – identifikator til felt i skattemeldingen
        - sti – stien til elementet med veiledning

### Validering av låste felter
![myndighetsfastsatt_kort.png](myndighetsfastsatt_kort.png)
Skatteetaten har muligheten til å låse enkeltfelter eller hele skattemeldingen og/eller næringsspesifikasjonen. Dette vil kun forekomme på en fastsatt skattemelding, og aldri utkast.
Informasjon om hvilke felter som er låst er ikke med i de eksterne modellene, men når dere prøver å validere en skattemelding med endringer på et felt som er låst vil dere få følgende valideringsresultat: `KanIkkeOverskriveMyndighetsfastsattVerdi` eller `KanIkkeSletteMyndighetsfastsattVerdi`
Dette skyldes at en forekomst som har blitt låst har blitt endret eller slettet.


## Valider skattemeldingen uten dokumentreferanseTilGjeldendeDokument <a name="validerTest"></a> [[back up]](#user-content-table-of-requests)

Hvis dere har behov for å gjøre beregninger før Skatteetaten har publisert utkast for et inntektsår, kan dere kalle denne tjenesten.
Den er helt lik som valideringstjenesten, men krever ikke `dokumentreferanseTilGjeldendeDokument`.

Denne tjenesten skal ikke brukes for validering for innsending, da vi har en del kontroller som sjekker mot gjeldende utkast, og det gjør ikke denne.

**URL** : `POST https://<env>/api/skattemelding/v2/validertest/<inntektsaar>/<identifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/validertest/2021/01028312345`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige`

**Body**
Likt som valider ovenfor

## Lagre skattemelding midlertidig for visning <a name="midlertidigVisning"></a> [[back up]](#user-content-table-of-requests)
Hvis dere har behov for å vise skattemeldingen i visningsklienten, kan den lastet opp via dette endepunktet. Skattemeldingen vil bli lagret i 24 timer for visning via URLen som returneres i responsen.

### Enkeltpersonsforetak <a name="lagret-skattemelding-for-visning-personlig"></a> [[back up]](#user-content-table-of-requests)
**URL** : `POST https://<env>/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`

**Body**
Likt som valider ovenfor

**Response**

```json
{
  "url": "https://skatt.skatteetaten.no/web/skattemelding-visning/midlertidig-lagret-skattemelding-for-visning?id=<id>"
}
```
- `<id>: Unik identifikator på midlertidig lagret skattemelding`

### Selskap <a name="lagret-skattemelding-for-visning-upersonlig"></a> [[back up]](#user-content-table-of-requests)
**URL** : `POST https://<env>/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning-upersonlig/<identifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/til-midlertidig-lagret-skattemelding-for-visning-upersonlig/910236490`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<identifikator>: Organisasjonsnummer til den skattepliktige`

**Body**
Likt som valider ovenfor

**Response**

```json
{
  "url": "https://skatt.skatteetaten.no/web/skattemelding-visning/midlertidig-lagret-skattemelding-for-visning?id=<id>"
}
```
- `<id>: Unik identifikator på midlertidig lagret skattemelding`

## Hent vedlegg <a name="hentVedlegg"></a> [[back up]](#user-content-table-of-requests)

Api som returnerer tidligere innsendte vedlegg til fastsatte skattemeldinger, enten fastsatt i gjeldende/nyeste skattemelding eller fra tidligere fastsettinger.

**URL** : `GET https://<env>/api/skattemelding/v2/<inntektsaar>/<identifikator>/vedlegg/<vedleggId>`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/<inntektsaar>/<identifikator>/vedlegg/<vedleggId>`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige`
- `<vedleggId>: ID fra XML-stien "/skattemelding/vedlegg/id"`

**Respons** :

- nedlastbar fil

## Hent gjeldende fastsetting som PDF <a name="hentGjeldendeFastsettingPdf"></a> [[back up]](#user-content-table-of-requests)

Api som returnerer gjeldende fastsetting i PDF-format.

**URL** : `GET https://<env>/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>/gjeldende-fastsetting.pdf`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/\<inntektsaar\>/\<identifikator\>/gjeldende-fastsetting.pdf`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige`

**Respons** :

- En PDF på formatet `application/pdf`.

## Eiendom API

Eiendom API tilbyr endepunkter for å søke opp eiendommer, hente eiendommenes formuesgrunnlag og for å beregne eiendommers markedsverdi.

### Testdata

Oversikt over hvilke eiendommer dere kan søke opp ligger i [dette regnearket](Syntetiske_eiendommer_v3.csv)

### Søk <a name="eiendomSoek"></a> [[back up]](#user-content-table-of-requests)

Det er mulig å søke på alle norske vegadresser, matrikkelnummer og boligselskap (organisasjonsnummer og andelsnr/aksjeboenhetsnr)

**URL** : `GET https://<env>/api/skattemelding/v2/eiendom/soek/<inntektsår>?query=<tekst>`

**Eksempel URL vegadresse** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/soek/2021?query=Storgata 1`

**Eksempel URL matrikkelnummer** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/soek/2021?query=36/120`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<query>: Fritekst søkestreng.`

**_Fritekst søkestreng_**

- `Hvis første tegn man angir er et tall vil søket kun lete blant matrikkeladresser.`
- `Hvis første tegn man angir er en bokstav vil søket kun lete blant veiadresser.`
- `Søket krever streng plassering av tegn.`

**Respons vegadresse** :

```json
{
  "resultatStorrelse": 1,
  "eiendommer": [],
  "vegadresser": [
    {
      "sergEiendomsidentifikator": 200,
      "unikeiendomsidentifikator": 200,
      "adressenavn": "Storgata",
      "husnr": 1,
      "postnummer": "4900",
      "poststedsnavn": "TVEDESTRAND",
      "highlight": "<em>Storgata</em> 1, 4900 Tvedestrand",
      "eiendommer": [
        {
          "sergEiendomsidentifikator": 200,
          "unikeiendomsidentifikator": 200,
          "eiendometablertdato": "2018-06-01",
          "kommunenr": "0914",
          "kommunenavn": "TVEDESTRAND",
          "gaardsnr": 97,
          "bruksnr": 13,
          "festenr": 0,
          "seksjonsnr": 0,
          "historisk": false,
          "highlight": "0914-97/13/0/0"
        }
      ]
    }
  ],
  "sokStart": "2021-10-05T09:53:06.118374",
  "sokSlutt": "2021-10-05T09:53:06.149235"
}
```

**Respons matrikkelnummer** :

```json
{
  "resultatStorrelse": 1,
  "eiendommer": [
    {
      "sergEiendomsidentifikator": 1,
      "unikeiendomsidentifikator": 1,
      "eiendometablertdato": "1925-03-31",
      "kommunenr": "1919",
      "kommunenavn": "GRATANGEN",
      "gaardsnr": 36,
      "bruksnr": 120,
      "festenr": 0,
      "seksjonsnr": 0,
      "highlight": "1919-<em>36/120</em>/0/0"
    }
  ],
  "vegadresser": [],
  "sokStart": "2020-10-05T09:55:03.197953",
  "sokSlutt": "2020-10-05T09:55:03.206961"
}
```

**_Forklaring til respons_**

- `sergEiendomsidentifikator: eiendomsidentifikator som skal benyttes for å hente eiendom og formuesinformasjon.`

### Hent formuesgrunnlag <a name="hentFormuesgrunnlag"></a> [[back up]](#user-content-table-of-requests)

Hent formuesgrunnlag for valgt unik eiendomsidentifikator og inntektsår.

Merk at hvilken informasjon responsen vil inneholde avhenger av valgt inntektsår, og at formuesopplysninger vil variere basert på hvilken eiendomstype eiendomsidentifikator har. Noen detaljer vil fjernes fra responsen hvis skatteyter ikke er eier av eiendommen.

**URL** : `GET https://<env>/api/skattemelding/v2/eiendom/formuesgrunnlag/<inntektsår>/<eiendomsidentifikator>/<identifikator>`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/formuesgrunnlag/2023/1/02095300173`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<eiendomsidentifikator>: Unik eiendomsidentifikator.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige som man henter eiendom for.`

<table>
<tr>
<td>Request type</td><td>2019-2023</td><td>2024 og fremover</td>
</tr>
<tr>
<td>
Respons hent formuesgrunnlag selveid bolig
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "39",
      "gaardsnummer": "61",
      "internEiendomsidentifikator": "c4a7484c-8ad7-4cff-b096-23931a0b6381fastEiendom",
      "kommunenummer": "4203",
      "postnummer": "4810",
      "poststedsnavn": "EYDEHAVN",
      "sergEiendomsidentifikator": "1",
      "vegadresse": [
        {
          "adressenavn": "Nedre Gartavei",
          "husnummer": "96"
        }
      ]
    }
  ],
  "formuesspesifikasjonForBolig": [
    {
      "eiendomstype": "selveidBolig",
      "internEiendomsidentifikator": "c4a7484c-8ad7-4cff-b096-23931a0b6381fastEiendom"
    }
  ]
}
```
</td>



<td>

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "39",
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "selveidBolig"
        }
      ],
      "gaardsnummer": "61",
      "kommunenummer": "4203",
      "postnummer": "4810",
      "poststedsnavn": "EYDEHAVN",
      "sergEiendomsidentifikator": "1",
      "vegadresse": [
        {
          "adressenavn": "Nedre Gartavei",
          "husnummer": "96"
        }
      ]
    }
  ]
}
```
</td>
</tr>

</table>



**_Forklaring til respons_**

`Responsen kan inneholde følgende objekt:`

- `fastEiendom: innholder eiendommens adresseinfomasjon og gjeldende skatteyters eierandel. Merk at det kan være flere vegadresser knyttet til eiendommen.`
- `formuesspesifikasjonFor*: innholder eiendommens formuesspesifikasjon og gjeldende skatteyters andel av formuesverdi. Detaljer som xxxx er med hvis skatteyter er eier av eiendommen. * Kan ha følgende verdier: Bolig, Flerboligbygning, SkalIkkeFastsettes, Tomt, SelveidFritidseiendom, AnnenFastEiendomInnenforInntektsgivendeAktivitet, AnnenFastEiendomUtenforInntektsgivendeAktivitet.`
- `ukjentEiendomINorge: hvis vi ikke støtter denne eiendomstypen.`

### Beregn markedsverdi for bolig <a name="markedsverdiBolig"></a> [[back up]](#user-content-table-of-requests)

Beregningen er basert på sjablong fra SSB hvor boligegenskaper, inntektsår inngår i beregningen.

Det er også mulig å oppgi dokumentert markedsverdi. Ugyldig dokumentert markedsverdi i forhold til klagegrense vil ikke hensyntas.

Sender man inn hele responsen fra hent formuesgrunnlag vil responsen på beregn innholde alt som ble sendt inn pluss de beregnede feltene.

**URL** : `POST https://<env>/api/skattemelding/v2/eiendom/markedsverdi/bolig/<inntektsår>/<eiendomsidentifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/markedsverdi/bolig/2024/1`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<eiendomsidentifikator>: Unik eiendomsidentifikator.`


**Body uten dokumentert markedsverdi for inntektsår 2019-2023**

<table>
<tr>
<td> Kall type </td> <td> inntektsår 2019-2023 </td> <td> inntektsår >=2024</td>
</tr>
<tr>
<td>Req selveidbolig, beregn markedsverdi</td>
<td>

```json
{
  "formuesspesifikasjonForBolig": [
    {
      "eiendomstype": "selveidBolig",
      "byggeaar": "2000",
      "boligensAreal": "150",
      "boligtype": "enebolig",
      "andelAvFormuesverdi": "100.00"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "selveidBolig",
          "boligbruk": "primaerbolig",
          "boligtype": "enebolig",
          "byggeaar": "2000",
          "boligensAreal": "150"
        }
      ]
    }
  ]
}
```
</td>
</tr>

<tr>
<td> Response selveidbolig, beregn markedsverdi </td>
<td> 

```json
{
  "formuesspesifikasjonForBolig": [
    {
      "eiendomstype": "selveidBolig",
      "andelAvFormuesverdi": "100.00",
      "byggeaar": "2000",
      "boligensAreal": "150",
      "boligtype": "enebolig",
      "beregnetMarkedsverdi": "2592619"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "beregnetMarkedsverdiForBolig": "4206687",
          "boligensAreal": "150",
          "boligtype": "enebolig",
          "byggeaar": "1980",
          "eiendomstype": "selveidBolig"
        }
      ]
    }
  ]
}
```
</td>
</tr>
<tr>
<td> Req selveid bolig med dokumentert markedsverdi</td>
<td>

```json
{
  "formuesspesifikasjonForBolig": [
    {
      "eiendomstype": "selveidBolig",
      "byggeaar": "2000",
      "boligensAreal": "150",
      "boligtype": "enebolig",
      "andelAvFormuesverdi": "100.00",
      "dokumentertMarkedsverdi": "2000000"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "selveidBolig",
          "boligbruk": "primaerbolig",
          "boligtype": "enebolig",
          "byggeaar": "2000",
          "boligensAreal": "150",
          "dokumentertMarkedsverdiForBolig": "2000000"
        }
      ]
    }
  ]
}
```
Her dokumentertMarkedsverdi blitt endret til dokumentertMarkedsverdiForBolig

</td>
</tr>
<tr>
<td>Response selveid bolig dokumentert markedsverdi</td>
<td>

```json
{
  "formuesspesifikasjonForBolig": [
    {
      "eiendomstype": "selveidBolig",
      "dokumentertMarkedsverdi": "2000000",
      "andelAvFormuesverdi": "100.00",
      "byggeaar": "2000",
      "boligensAreal": "150",
      "boligtype": "enebolig",
      "justertMarkedsverdi": "2000000",
      "beregnetMarkedsverdi": "4206687"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "beregnetMarkedsverdiForBolig": "4206687",
          "boligensAreal": "150",
          "boligtype": "enebolig",
          "byggeaar": "1980",
          "eiendomstype": "selveidBolig",
          "justertMarkedsverdi": "2000000"
        }
      ]
    }
  ]
}
```
</td>
</tr>
</table>


**_Forklaring til respons_**

- `beregnetMarkedsverdi: beregnet markedverdi for boligen.`
- `dokumentertMarkedsverdi: dokumentert markedsverdi når denne er innenfor reglene slik at den er hensynstatt.`
- `dokumentertMarkedsverdiForBolig: nytt navn for 2024.`
- `justertMarkedsverdi: justert markedsverdi er med når dokumentert markedsverdi er hensynstatt.`

### Beregn markedsverdi for flerbolig <a name="markedsverdiFlerbolig"></a> [[back up]](#user-content-table-of-requests)

Beregningen er basert på sjablong fra SSB hvor boligegenskaper, inntektsår inngår i beregningen.

Det beregnes markedsverdi for hver useksjonert boenhet.

Det er også mulig å oppgi dokumentert markedsverdi. Ugyldig dokumentert markedsverdi i forhold til klagegrense vil ikke hensyntas.

Sender man inn hele responsen fra hent formuesgrunnlag vil responsen på beregn inneholde alt som ble sendt inn pluss de beregnede feltene.

**URL** : `POST https://<env>/api/skattemelding/v2/eiendom/markedsverdi/flerbolig/<inntektsår>/<eiendomsidentifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/markedsverdi/flerbolig/2024/200`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<eiendomsidentifikator>: Unik eiendomsidentifikator.`


<table>
<tr>
<td>Kall type</td> <td>Inntektsår 2019-2023 </td> <td>Inntektsår >=2024</td>
</tr>
<tr>
<td>Request flerboligbygning beregn markedsverdi</td>
<td>

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ],
  "formuesspesifikasjonForFlerboligbygning": [
    {
      "eiendomstype": "flerboligbygning",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "useksjonertBoenhet": [
        {
          "bruksenhetsnummer": "H0101",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0102",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0103",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0104",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0201",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0202",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0203",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0204",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        }
      ]
    }
  ]
}
```
</td>

<td> 

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ],
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "flerboligbygning",
          "useksjonertBoenhet": [
            {
              "bruksenhetsnummer": "H0101",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0102",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0103",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0104",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0201",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0202",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0203",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0204",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            }
          ]
        }
      ]
    }
  ]
}

```
</td>
</tr>
<tr>
<td>Response fleboligbygning beregn markedsverdi</td>
<td>

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ],
  "formuesspesifikasjonForFlerboligbygning": [
    {
      "beregnetMarkedsverdi": "29765976",
      "eiendomstype": "flerboligbygning",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "useksjonertBoenhet": [
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0101",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0102",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0103",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0104",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0201",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0202",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0203",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0204",
          "byggeaar": "2016"
        }
      ]
    }
  ]
}
```
</td>

<td>

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "fastEiendomSomFormuesobjekt": [
        {
          "beregnetMarkedsverdiForFlerboligbygning": "29765976",
          "eiendomstype": "flerboligbygning",
          "useksjonertBoenhet": [
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0101",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0102",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0103",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0104",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0201",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0202",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0203",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0204",
              "byggeaar": "2016"
            }
          ]
        }
      ],
      "gaardsnummer": "70",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ]
}
```
</td>
</tr>
<tr>
<td>Request flerboligbygning med dokumentert markedsverdi</td>
<td>

Her det lagt til `"dokumentertMarkedsverdi": "11000000"`

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ],
  "formuesspesifikasjonForFlerboligbygning": [
    {
      "eiendomstype": "flerboligbygning",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "dokumentertMarkedsverdi": "11000000",
      "useksjonertBoenhet": [
        {
          "bruksenhetsnummer": "H0101",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0102",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0103",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0104",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0201",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0202",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0203",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        },
        {
          "bruksenhetsnummer": "H0204",
          "boligensAreal": "100",
          "byggeaar": "2016",
          "boligtype": "leilighet"
        }
      ]
    }
  ]
}

```
</td>

<td> 

Legg merke til at `dokumentertMarkedsverdi` har blitt endret til `dokumentertMarkedsverdiForFlerboligbygning`

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ],
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "flerboligbygning",
          "dokumentertMarkedsverdiForFlerboligbygning": "11000000",
          "aarForMottattMarkedsverdiForFlerboligbygning": "2024",
          "useksjonertBoenhet": [
            {
              "bruksenhetsnummer": "H0101",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0102",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0103",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0104",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0201",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0202",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0203",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            },
            {
              "bruksenhetsnummer": "H0204",
              "boligensAreal": "100",
              "byggeaar": "2016",
              "boligtype": "leilighet"
            }
          ]
        }
      ]
    }
  ]
}


```
</td>
</tr>
<tr>
<td>Response flerboligbygning med dokumentert markdsverdi </td>
<td>

Legg merke til at `dokumentertMarkedsverdi` og `justertMarkedsverdi` er med i reponsen

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "gaardsnummer": "70",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ],
  "formuesspesifikasjonForFlerboligbygning": [
    {
      "beregnetMarkedsverdi": "11000000",
      "dokumentertMarkedsverdi": "11000000",
      "eiendomstype": "flerboligbygning",
      "internEiendomsidentifikator": "1fbb4cd2-9ae5-4060-a984-331e4bf1bbb9fastEiendom",
      "justertMarkedsverdi": "11000000",
      "useksjonertBoenhet": [
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0101",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0102",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0103",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0104",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0201",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0202",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0203",
          "byggeaar": "2016"
        },
        {
          "boligensAreal": "100",
          "boligtype": "leilighet",
          "boligverdi": "3720747",
          "bruksenhetsnummer": "H0204",
          "byggeaar": "2016"
        }
      ]
    }
  ]
}

```
</td>

<td> 

`dokumentertMarkedsverdi` har blitt endret til `dokumentertMarkedsverdiForFlerboligbygning`
`justertMarkedsverdi` har blitt endret til `justertMarkedsverdiForFlerboligbygning`

```json
{
  "fastEiendom": [
    {
      "bruksnummer": "14",
      "fastEiendomSomFormuesobjekt": [
        {
          "beregnetMarkedsverdiForFlerboligbygning": "11000000",
          "dokumentertMarkedsverdiForFlerboligbygning": "11000000",
          "eiendomstype": "flerboligbygning",
          "justertMarkedsverdiForFlerboligbygning": "11000000",
          "useksjonertBoenhet": [
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0101",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0102",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0103",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0104",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0201",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0202",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0203",
              "byggeaar": "2016"
            },
            {
              "boligensAreal": "100",
              "boligtype": "leilighet",
              "boligverdi": "3720747",
              "bruksenhetsnummer": "H0204",
              "byggeaar": "2016"
            }
          ]
        }
      ],
      "gaardsnummer": "70",
      "kommunenummer": "3101",
      "postnummer": "1792",
      "poststedsnavn": "TISTEDAL",
      "sergEiendomsidentifikator": "200",
      "vegadresse": [
        {
          "adressenavn": "Anders Syvertsens vei",
          "husnummer": "8"
        }
      ]
    }
  ]
}
```
</td>
</tr>
</table>


**_Forklaring til respons_**

- `beregnetMarkedsverdi/beregnetMarkedsverdiForFlerboligbygning: beregnet markedverdi for boligen.`
- `boligverdi: beregnet markedsverdi for useksjonert boenhet. Beregnes uavhengig av dokumentert markedsverdi.`
- `dokumentertMarkedsverdi/dokumentertMarkedsverdiForFlerboligbygning: dokumentert markedsverdi når denne er innenfor reglene slik at den er hensynstatt.`
- `justertMarkedsverdi/justertMarkedsverdiForFlerboligbygning: justert markedsverdi er med når dokumentert markedsverdi er hensynstatt.`

**Feil response ifm bad request**

```json
{
  "feilkode": "EIENDOM-014",
  "beskrivelse": "Eiendommen finnes ikke."
}
```

**_Feilkoder ifm bad request_**

- EIENDOM-001: Ugyldig verdi: boligtype må være (enebolig, leilighet, smaahus).
- EIENDOM-002: Ugyldig verdi: byggeaar må være tall.
- EIENDOM-003: Ugyldig verdi: byggeaar må være mindre eller lik skatteleggingsperiode og større enn 1250.
- EIENDOM-004: Ugyldig verdi: boligensAreal må være et tall større enn 9 og mindre enn 10000.
- EIENDOM-005: Ugyldig verdi: dokumentertMarkedsverdiForBolig må være et tall.
- EIENDOM-008: Ugyldig verdi: Boligtype for boenhet må være leilighet.
- EIENDOM-013: Eiendom kunne ikke entydig identifiseres med oppgitte verdier.
- EIENDOM-014: Eiendommen finnes ikke.
- EIENDOM-015: Eiendommen er utgått.
- EIENDOM-016: Ingen bruksareal.
- EIENDOM-017: Ingen bruksareal. Flere bygninger har registrert bruksareal.
- EIENDOM-018: Ugyldig bruksareal.
- EIENDOM-019: Formuesgrunnlag mangler for denne eiendommen. Forespørselen kunne ikke fullføres.
- EIENDOM-020: Ugyldig verdi: Dokumentert markedsverdi kan ikke overstige 2.1 milliarder.
- EIENDOM-021: Ugyldig Naeringstype. Naeringstypen er ikke en gyldig naeringstype for ikke utleid naeringseiendom.
- EIENDOM-022: Ugyldig verdi: Dokumentert markedsverdi må være stoerre enn 0.
- EIENDOM-050: støtter ikke inntektsaar: <inntektsår>.
- EIENDOM-051: <Ulike mangler på input>.
- EIENDOM-999: Noe gikk galt. Forespørselen kunne ikke fullføres.

### Beregn utleieverdi for ikke-utleid næringseiendom <a name="markedsverdiIkkeUtiledNaeringseiendom"></a> [[back up]](#user-content-table-of-requests)

BeregnetUtleieverdi er basert på næringssjablong fra SSB hvor næringstype, areal, bystatus, sentralitet og skatteleggingsperiode inngår i beregningen.

Det er også mulig å oppgi dokumentert markedsverdi. Ugyldig dokumentert markedsverdi i forhold til klagegrense vil ikke hensyntas.

Sender man inn hele responsen fra hent formuesgrunnlag vil responsen på beregn innholde alt som ble sendt inn pluss de beregnede feltene.

**URL** : `POST https://<env>/api/skattemelding/v2/eiendom/utleieverdi/<inntektsår>/<eiendomsidentifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/eiendom/utleieverdi/2021/102`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<eiendomsidentifikator>: Unik eiendomsidentifikator.`

<table>
<tr>
<td>Kall type</td> <td>Inntektsår 2019-2023 </td> <td>Inntektsår >=2024</td>
</tr>
<tr>
<td>
Requesten markedsverdi ikke utleid næringsieindom 
</td>
<td>

```json
{
  "formuesspesifikasjonForIkkeUtleidNaeringseiendomINorge": [
    {
      "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
      "naeringseiendomstype": "tomtGrunnarealHovedfunksjon",
      "areal": "250"
    }
  ]
}
```
</td>

<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
          "naeringseiendomstypeForIkkeUtleidNaeringseiendomINorge": "tomtGrunnarealHovedfunksjon",
          "arealForIkkeUtleidNaeringseiendomINorge": "250"
        }
      ]
    }
  ]
}
```
</td>
</tr>
<tr>
<td>Response ikke utleid næringseiendom</td>
<td>

```json
{
  "formuesspesifikasjonForIkkeUtleidNaeringseiendomINorge": [
    {
      "areal": "250",
      "beregnetUtleieverdi": "3232059",
      "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
      "naeringseiendomstype": "tomtGrunnarealHovedfunksjon",
      "utleieverdi": "3232059"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "arealForIkkeUtleidNaeringseiendomINorge": "250",
          "beregnetUtleieverdiForIkkeUtleidNaeringseiendomINorge": "3232059",
          "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
          "naeringseiendomstypeForIkkeUtleidNaeringseiendomINorge": "tomtGrunnarealHovedfunksjon",
          "utleieverdiFraSerg": "3232059"
        }
      ]
    }
  ]
}

```
</td>
</tr>

<tr>
<td>Request med  dokumentert utleieverdi</td>
<td>

```json
{
  "formuesspesifikasjonForIkkeUtleidNaeringseiendomINorge": [
    {
      "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
      "naeringseiendomstype": "tomtGrunnarealHovedfunksjon",
      "areal": "250",
      "dokumentertMarkedsverdi": "200000"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
          "naeringseiendomstypeForIkkeUtleidNaeringseiendomINorge": "tomtGrunnarealHovedfunksjon",
          "arealForIkkeUtleidNaeringseiendomINorge": "250",
          "dokumentertMarkedsverdiForIkkeUtleidNaeringseiendomINorge": "200000"
        }
      ]
    }
  ]
}
```
</td>
</tr>

<tr>
<td>Response dokumentert utleieverdi</td>
<td>

```json
{
  "formuesspesifikasjonForIkkeUtleidNaeringseiendomINorge": [
    {
      "areal": "250",
      "beregnetUtleieverdi": "200000",
      "dokumentertMarkedsverdi": "200000",
      "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
      "naeringseiendomstype": "tomtGrunnarealHovedfunksjon",
      "utleieverdi": "3232059"
    }
  ]
}
```
</td>
<td>

```json
{
  "fastEiendom": [
    {
      "fastEiendomSomFormuesobjekt": [
        {
          "arealForIkkeUtleidNaeringseiendomINorge": "250",
          "beregnetUtleieverdiForIkkeUtleidNaeringseiendomINorge": "200000",
          "dokumentertMarkedsverdiForIkkeUtleidNaeringseiendomINorge": "200000",
          "eiendomstype": "ikkeUtleidNaeringseiendomINorge",
          "naeringseiendomstypeForIkkeUtleidNaeringseiendomINorge": "tomtGrunnarealHovedfunksjon",
          "utleieverdiFraSerg": "3232059"
        }
      ]
    }
  ]
}
```

</td>
</tr>
</table>


**_Forklaring til respons_**

- `beregnetUtleieverdi/beregnetUtleieverdiForIkkeUtleidNaeringseiendomINorge: beregnet utleieverdi for eiendommen.`
- `utleieverdi/utleieverdiFraSerg: beregnet utleieverdi fra serg.`
- `dokumentertMarkedsverdiForIkkeUtleidNaeringseiendomINorge`


**Feil response ifm bad request**

```json
{
  "feilkode": "EIENDOM-014",
  "beskrivelse": "Eiendommen finnes ikke."
}
```

**_Feilkoder ifm bad request_**

- EIENDOM-001: Ugyldig verdi: boligtype må være (enebolig, leilighet, smaahus).
- EIENDOM-002: Ugyldig verdi: byggeaar må være tall.
- EIENDOM-003: Ugyldig verdi: byggeaar må være mindre eller lik skatteleggingsperiode og større enn 1250.
- EIENDOM-004: Ugyldig verdi: boligensAreal må være et tall større enn 9 og mindre enn 10000.
- EIENDOM-005: Ugyldig verdi: dokumentertMarkedsverdiForBolig må være et tall.
- EIENDOM-008: Ugyldig verdi: Boligtype for boenhet må være leilighet.
- EIENDOM-013: Eiendom kunne ikke entydig identifiseres med oppgitte verdier.
- EIENDOM-014: Eiendommen finnes ikke.
- EIENDOM-015: Eiendommen er utgått.
- EIENDOM-016: Ingen bruksareal.
- EIENDOM-017: Ingen bruksareal. Flere bygninger har registrert bruksareal.
- EIENDOM-018: Ugyldig bruksareal.
- EIENDOM-019: Formuesgrunnlag mangler for denne eiendommen. Forespørselen kunne ikke fullføres.
- EIENDOM-020: Ugyldig verdi: Dokumentert markedsverdi kan ikke overstige 2.1 milliarder.
- EIENDOM-021: Ugyldig Naeringstype. Naeringstypen er ikke en gyldig naeringstype for ikke utleid naeringseiendom.
- EIENDOM-022: Ugyldig verdi: Dokumentert markedsverdi må være stoerre enn 0.
- EIENDOM-050: støtter ikke inntektsaar: <inntektsår>.
- EIENDOM-051: <Ulike mangler på input>.
- EIENDOM-999: Noe gikk galt. Forespørselen kunne ikke fullføres.

## Forløpig avregning <a name="avregning"></a> [[back up]](#user-content-table-of-requests)
Tjenesten avregning er en tjeneste som mottar fødselsnummer og beregnet skatt og returnerer avregning. Denne tjenesten vil IKKE ta høyde for eventuelte tidligere skatteoppgjør for aktuelt inntektsår. Dvs at hvis skattyter har et skatteoppgjør og fått utbetalt tilgode, og skal gjøre en endring så vil denne tjenesten avregne som om det var første skatteoppgjør

*URL** : `POST https://<env>/api/skattemelding/v2/avregning/avregn/{inntektsaar}/{identifikator}`
```json
{
  "beregnetSkatt" : 10000
}
```

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsaar>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødsels eller D-nummer for parten som skal avregnes.`
- `<beregnetSkatt>: Sum beregnet skatt for aktuelt inntektsår.`

**Respons**
responsen er json med disse feltene. Spørsmålstegn indikerer at feltet ikke er obligatorisk og dersom det ikke er noe verdi i dette feltet, blir det ikke returnert

- beregnetSkatt: Long,
- forskuddstrekk: Long?,
- manueltKorrigertForskuddstrekk: Long?,
- manueltRegistrertForskuddstrekk: Long?,
- nektetGodskrevet: Long?,
- tilbakebetaltFoerAvregning: Long?,
- utskrevetForskuddsskatt: Long?,
- betaltTilleggsforskudd: Long?,
- restskatt: Long?,
- overskytende: Long?,
- rentetillegg: Long?,
- rentegodtgjoerelse: Long?,
- beregnedeDebetAvsavnsrenter: Long?,
- beregnedeKreditAvsavnsrenter: Long?,
- forskuddPaaRestskatt: Long?,
- ubetaltForskuddsskatt: Long?,
- utbetaltEtterTidligereSkatteoppgjoer: Long?,
- innbetaltEtterTidligereSkatteoppgjoer: Long?,
- aaBetale: Long?,
- tilGode: Long?,
- aaBetaleFrafaltUnderOrdinaerBeloepsgrense: Long?,
- aaBetaleFrafaltUnderBeloepsgrenseForSjoemenn: Long?,
- tilGodeBlirIkkeUtbetaltUnderOrdinaerBeloepsgrense: Long?,
- tilGodeBlirIkkeUtbetaltUnderBeloepsgrenseForSjoemenn: Long?,
- enoekfradrag: Long?,
- restskattFrafaltUnderOrdinaerBeloepsgrense: Long?,
- fastsattKildeskattPaaLoenn: Long?,
- refusjonAvKildeskattPaaLoenn: Long?

## Forhåndsfastsetting <a name="Forhandsfastsetting"></a> [[back up]](#user-content-table-of-requests)
Det er mulig å be om forhåndsfastsetting for upersonlige skattemelding før ordinær fastsettingsperioden starter.
For eksempel, så skal et selskap kunne få forhåndsfastsetting i mars i 2023. Da skal skattemeldingen for 2022 og 2023 leveres.

Skattemeldingen 2022 leveres i 2022-modellen, som "vanlig". I tillegg skal skattemeldingen for 2023 leveres, også den i 2022-modellen.

Dersom en skal forhåndsfastsette før skattemeldingen er tilgjenglig via vanlig hent api'et så må en kjøre et "klargjøringskall".
Når skattemeldingen er tilgjenglig så må skattemeldingen inneholde

```xml
<skattemelding xmlns="urn:no:skatteetaten:fastsetting:formueinntekt:skattemelding:upersonlig:ekstern:v2">
    <partsnummer>900408015031</partsnummer>
    <inntektsaar>2023</inntektsaar>
<!--    resten av skattemelding innformasjon her-->
    <gjelderForhaandsfastsetting>
      <innsendingsformat>
        <forhaandsfastsettingsformattype>fjoraaretsSkattemelding</forhaandsfastsettingsformattype>
      </innsendingsformat>
    </gjelderForhaandsfastsetting>
</skattemelding>
```
I tillegg så må en i skattemeldingOgNaeringsspesifikasjonRequest anngi hvilket navnerom skattemeldingen er lagret på.
Dersom en skal forhåndsfastsette 2022 og 2023 nå i februar 2023 så skal følgende være satt:

For skattemeldingen dokumentet:
```xml
<dokument>
    <type>skattemeldingUpersonlig</type>
    <encoding>utf-8</encoding>
    <content><!-- base64-enkodet innhold her --></content>
    <navneromVedForhaandsfastsetting>urn:no:skatteetaten:fastsetting:formueinntekt:skattemelding:upersonlig:ekstern:v2</navneromVedForhaandsfastsetting>
</dokument>
```

For næringspesifikasjonen
```xml
    <dokument>
    <type>naeringsspesifikasjon</type>
    <encoding>utf-8</encoding>
    <content><!-- base64-enkodet innhold her --></content>
    <navneromVedForhaandsfastsetting>urn:no:skatteetaten:fastsetting:formueinntekt:naeringsopplysninger:ekstern:v3</navneromVedForhaandsfastsetting>
</dokument>
```

### Klargjør part for forhåndsfastsetting: <a name="klargjoer-part-for-forhaandsfastsetting"></a> [[back up]](#user-content-table-of-requests)
Dette kallet skal kjøres for å klargjøre en part for forhåndsfastsetting
dersom skattemeldingen ikke er klar på forhåndsfastsettingtidspunktet

**URL** `POST https://<env>/api/skattemelding/v2/klargjoerforhaandsfastsetting/<inntektsår>/<identifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/klargjoerforhaandsfastsetting/2023/312787016`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse.`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Organisasjonsnummer som krever forhåndsfastsetting.`

**Respons**
Ved vellykket klargjøring:
```json
{
  "status": "OK"
}
```

Part som har utkast tilgjenglig:
```json
{
  "status": "PART_HAR_GJELDENDE",
  "melding": "part har allerede gjeldende skattemelding for part, inntektsaar=2022, partType=upersonlig"
}
```

Innteksår ikke støttet
```json
{
  "status": "FORHAANDSFASTSETTING_ER_IKKE_STOETTET_FOR_INNTEKTSAAR",
  "melding": "forhåndsfastsetting er ikke støttet for dette inntektsåret, inntektsaar=2021"
}
```

Partstype ikke støttet (per nå støttes kun upersonlig skattemelding)
```json
{
  "status": "PART_TYPE_ER_IKKE_STOETTET",
  "melding": "forhåndsfastsetting er ikke støttet for denne partstypen, inntektsaar=2023, partType=personlig"
}
```

Orgnr er ikke meldt oppløst, slettet, konkurs eller under tvangsoppløsning.
Denne sjekken er skrudd av i testmiljøet for å forenkle testingen av forhåndsfastsettingen.
```json
{
  "status": "PART_HAR_UGYLDIG_STATUS_I_ER",
  "melding": "part har ugyldig status i Enhetsregisteret, inntektsaar=2023, partType=upersonlig"
}
```

Andre feiltilstander
```json
{
  "status": "UKJENT",
  "melding": "feil tilstand oppdaget ifm klargjøring av forhåndsfastsetting, inntektsaar=2023, partType=personlig"
}
```

# Klargjør part som mangler utkast <a name="klargjoer-part-som-mangler-utkast"></a> [[back up]](#user-content-table-of-requests)
Dersom dere har en organisasjon som av eller annen årsak mangler utkast (får feilmelding http 403, skattemelding ikke tilgjenglig) for et aktivt inntektsår, så kan dere bruke dette API'et for for å klargjøre parten.
Det API'et støttes kun for enhetstyper som skal levere skattemelding upersonlig.

**URL** `POST https://<env>/api/skattemelding/v2/klargjoerpart/<inntektsår>/<identifikator>`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/klargjoerpart/2023/312787016`




# Søknad om utsatt frist for skattemeldingen <a name="utsattfrist-skattemeldingen"></a> [[back up]](#user-content-table-of-requests)
Tjeneste for å søke om utsatt frist for levering av skattemeldingen. 

**Swagger dokumentasjon for API**
- https://app.swaggerhub.com/apis/skatteetaten/utsattfrist-mottak_api/0.0.1

**Eks for bulk innsending med request body**

```json
{
  "referanse": "Unik referanse fra innsender systemet",
  "klienter": [
    {
      "identifikator": {
        "foedselsnummer": "12345678901"
      },
      "naering": false,
      "navn": "Per Nordmann",
      "epost": "per@nordmann.no"
    },
    {
      "identifikator": {
        "organisasjonsnummer": "123456789"
      },
      "naering": false,
      "navn": "Kari Nordmann",
      "epost": "kari@nordmann.no"
    }
  ]
}
```

**Eks på Http 200 response**
```json
{
  "melding":"Søknad om utsatt frist mottatt",
  "gjeldendeInntektsaar":2024,
  "innsender":"<identifikator>",
  "antall":2
}
```

**Http response koder**
- Http 200  Innsending OK.
- Http 403  Ikke tilgang. Dette blir også sendt i den perioden av året hvor tjenesten er stengt.
- Http 4xx  En 400 feiltype ved innsendingsfeil
- Http 5xx  En 500 feiltype ved intern feil


# Altinn3-API

For applikasjonsbrukere, dvs. organisasjoner og personer som kaller Altinn gjennom et klient API (typisk skattepliktige som bruker et sluttbrukersystem) tilbyr Altinn API-er med følgende funksjonalitet:

1. opprette en instans i Altinn
2. populere instansen med metadata
3. populere instansen med vedlegg (ved behov)
4. populere instansen med skattemeldingsdata
5. trigger neste steg slik at instansen havner i status _Bekreftelse_ (betyr "data lastet opp").
6. trigger neste steg slik at instansen havner i status _Tilbakemelding_ (betyr "skattemeldingen innsendt").
7. hente kvittering/tilbakemelding. Merk at det kan gå litt tid før kvittering er tilgjengelig (Skatteetaten må laste ned, behandle innsendingen og laste opp kvitteringen)

Les mer om Altinn API-ene på [altinn sine sider](https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/).

Tjenestene listet under kalles for å sende inn skattemelding til Altinn.

_Merk at Base URL-en_ til applikasjonen vår i Altinn er:

**Testmiljø:** `https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/`

**Produksjonsmiljø:** `https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/`

## Hent token

Første trinn er å få generert et autentiseringstoken i Altinn. Autentisering skjer enten via maskinporten eller ID-porten. Les mer om det på [altinn sine sider](https://docs.altinn.studio/api/authentication/)

Tokenet fra maskinporten/ID-porten brukes til å veksle det inn i et Altinn JWT access token. Det er Altinn tokenet som brukes videre til å kalle Altinn-APIer beskrevet under.

**Testmiljø:** `curl --location --request GET 'https://platform.tt02.altinn.no//authentication/api/v1/exchange/id-porten' \ --header 'Authorization: Bearer <ID-porten/maskinporten Token>'`

**Produksjonsmiljø:** `curl --location --request GET 'https://platform.altinn.no//authentication/api/v1/exchange/id-porten' \ --header 'Authorization: Bearer <ID-porten/maskinporten Token>'`

Responsen til dette kallet vil være et Altinn-token, dette tokenet skal brukes i kallene under.
<br />

## Hent PartyId fra Altinn

Altinn krever at det brukes Altinn sin interne ID-en, kalt _PartyId_ ved kall til Altinn tjenestene.

**Testmiljø:** `curl --location --request GET 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/api/v1/profile/user' \ --header 'Authorization: Bearer <altinn Token>'`

**Produksjonsmiljø:** `curl --location --request GET 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/api/v1/profile/user' \ --header 'Authorization: Bearer <altinn Token>'`

<br />

Merk at partyId kan også fås ved å opprette instans basert på fødselsnummer. For detaljer se neste avsnitt.

## Opprett en instans i Altinn

Det er to måter å opprette en instans i Altinn på.
Alternativ 1 er foretrukket, og innebærer å sende med inntektsaar i payloaden. Dette gjør "Oppdater skjema-metadata til instansen" overflødig.

### Opprett en instans i Altinn (Alternativ 1)

For å opprette en instans av skattemeldingen i Altinn3, så skal det tekniske navnet på instansen være `formueinntekt-skattemelding-v2`

Beskrivelse: `inntektsaar: Inntektsår skattemeldingen gjelder for`

Første trinn i innsendingsløpet er opprettelse av en instans av skattemeldingen. Plukk ut partyId fra forrige respons og bruk det i body under.


**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "partyId": "50006875" }, "appId" : "skd/formueinntekt-skattemelding-v2", "dataValues":{"inntektsaar":2021} }'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "partyId": "50006875" }, "appId" : "skd/formueinntekt-skattemelding-v2", "dataValues":{"inntektsaar":2021} }'`

En instans kan også opprettes ved å oppgi fødselsnummer (i stedet for partyId) i payloaden, da vil partyId bli returnert i responsen og kan brukes til å gjøre resterende kall mot Altinn.

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "personNumber": "12345678910" }, "appId" : "skd/formueinntekt-skattemelding-v2", "dataValues":{"inntektsaar":2021} }'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "personNumber": "12345678910" }, "appId" : "skd/formueinntekt-skattemelding-v2", "dataValues":{"inntektsaar":2021} }'`

Merk at skattemelding for "personlige skattepliktige med næring (ENK)" skal alltid sendes inn på personens fødselsnummer (det kan ikke benyttes orgnummer til ENK).

Les mer om endepunktet på Altinn sine sider:
https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/app-api/instances/#create-instance

**Respons** : Metadata om instansen som ble opprettet. En unik instanceId vil være med i responsen og kan brukes senere til å hente/oppdatere instansen.
<br />


### Opprett en instans i Altinn (Alternativ 2)
For å opprette en instans av skattemeldingen i Altinn3, så skal det tekniske navnet på instansen være `formueinntekt-skattemelding-v2`


Første trinn i innsendingsløpet er opprettelse av en instans av skattemeldingen. Plukk ut partyId fra forrige responsen og bruk det i body under.

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "partyId": "50006875" }, "appId" : "skd/formueinntekt-skattemelding-v2" }'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "partyId": "50006875" }, "appId" : "skd/formueinntekt-skattemelding-v2" }'`

En instans kan også opprettes ved å oppgi fødselsnummer (i stedet for partyId) i payloaden, da vil partyId bli returnert i responsen og kan brukes til å gjøre resterende kall mot Altinn.

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "personNumber": "12345678910" }, "appId" : "skd/formueinntekt-skattemelding-v2" }'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{ "instanceOwner": { "personNumber": "12345678910" }, "appId" : "skd/formueinntekt-skattemelding-v2" }'`

Merk at skattemelding for "personlige skattepliktige med næring (ENK)" skal alltid sendes inn på personens fødselsnummer (det kan ikke benyttes orgnummer til ENK).

Les mer om endepunktet på Altinn sine sider:
https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/app-api/instances/#create-instance

**Respons** : Metadata om instansen som ble opprettet. En unik instanceId vil være med i responsen og kan brukes senere til å hente/oppdatere instansen.
<br />

### Oppdater skjema-metadata til instansen (Kun hvis alternativ 2 benyttes ved opprettelse av instans)

_Dette erstatter "Oppdater skjema-metadata (skattemeldingv_V1.xml) til instansen" fra v1-piloten._

Neste trinn er å laste opp meta-data om skattemeldingen. Meta-data skal være en json tilsvarende eksempelet under.

```json
{
  "inntektsaar": 2021,
  "skalBekreftesAvRevisor": false
}
```
Beskrivelse:
- `inntektsaar: Inntektsår skattemeldingen gjelder for`
- `skalBekreftesAvRevisor: Settes til true om det er forekomster som skal bekreftes av revisor`

Plukk ut _id_ og _data.id_ fra forrige responsen og bruk de på slutten av url-en under:

- erstatt 50028539/82652921-88e4-47d9-9551-b9da483e86c2 med verdien fra _id_
- erstatt 58c560b4-90a2-42ac-af26-98e1e60336cd med verdien fra _data.id_

**Testmiljø:** `curl --location --request PUT 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data/58c560b4-90a2-42ac-af26-98e1e60336cd' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{"inntetksaar": 2021}'`

**Produksjonsmiljø:** `curl --location --request PUT 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data/58c560b4-90a2-42ac-af26-98e1e60336cd' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <altinn Token>' \ --data-raw '{"inntetksaar": 2021}'`

<br />

## Last opp vedlegg (ved behov)

Opplasting av vedlegg utføres etter instansopprettelse og før innsending av skattemeldingdata.

Ved opplasting av vedlegg må denne prosedyren følges:
1. Last opp vedlegg til instans
2. Oppdater skattemelding.xml med vedleggsreferanse(er) iht. XSD: [skattemelding_v9_kompakt_ekstern.xsd](/src/resources/xsd).
    - Ved oppdatering av skattemelding med referanser til vedlegg trengs en vedleggsId. Denne vedleggsId-en finner man i
      responsen til kallet for opplasting av vedlegg.

Plukk ut _id_ fra responsen til "Opprett en instans i Altinn"-kallet og bruk det på slutten av url-en under.
Merk at dataType skal settes til **skattemelding-vedlegg**.

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=skattemelding-vedlegg' \ --header 'Content-Disposition: attachment; filename=Eksempel_Vedlegg.pdf' \ --header 'Content-Type: application/pdf' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@/home/k83452/Documents/Altinn3/Testfiler/Eksempel_Vedlegg.pdf'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=skattemelding-vedlegg' \ --header 'Content-Disposition: attachment; filename=Eksempel_Vedlegg.pdf' \ --header 'Content-Type: application/pdf' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@/home/k83452/Documents/Altinn3/Testfiler/Eksempel_vedlegg.pdf'`

**Merk**
- Aksepterte content-types er: application/pdf, image/jpeg, image/png og application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
- Content-disposition skal være: **attachment; filename=\<filnavn>**

<br />

## Last opp skattemeldingdata (skattemelding.xml) til instansen

Neste trinn er å laste opp skattemeldingsdata.

**Merk:** Hvis vedlegg er lastet opp til instansen må disse være lagt til i skattemeldingen før opplasting av skattemelding.xml.
Det anbefales å gjøre ny validering etter oppdatering av skattemelding.xml.

Forklaring av attributter til vedlegg-seksjonen i skattemelding.xml
- id: Identifiserer vedlegget. Denne id-en hentes fra responsen ved opplasting av vedlegget. Se beskrivelse "Last opp vedlegg (ved behov)"
- vedleggsnavn: Forklarende navn på vedlegget. (ikke påkrevd)
- vedleggsfil/opprinneligFilnavn: Filnavnet som filen hadde da vedlegget ble lastet opp.
- vedleggsfil/opprinneligFiltype: Filtypen som filen hadde da vedlegget ble lastet opp.  (jpg, pdf, osv.)
- vedleggsfil/filensOpprinneligDatoOgTid: Tidspunkt for når vedlegget ble lastet opp
- vedleggstype/vedleggskategori: Vedleggstype som velges ut fra en kodeliste.  [2021_vedleggskategori.xml](/src/resources/kodeliste/2021/2021_vedleggskategori.xml)
- informasjonselementidentifikator: Sti som beskriver hvilket felt vedlegget tilhører. Hvis vedlegget ikke skal tilhøre et felt, skal denne være tom. Stien bygges opp av skattemeldingsdokumentets xml-struktur. Eksempel: "/skattemelding/bankLaanOgForsikring/konto/paaloepteRenter/beloepUtenHensynTilValgtPrioritertFradragstype/beloep/beloepIValuta/beloep" Se fullstendig eksempel: [personligSkattemeldingV9EksempelFeltvedlegg.xml](/src/resources/eksempler/2021/personligSkattemeldingV9EksempelFeltvedlegg.xml)
- forekomstidentifikator: Forekomstid til feltet som vedlegget tilhører. Hvis vedlegget ikke skal tilhøre et felt, skal denne være tom.
- utvekslingsarkividentifikator: Dette er nøkkelen til dokumentet i Skatteetatens arkiv. Nøkkelen skal ikke fylles ut av SBS.

Plukk ut _id_ fra responsen til "Opprett en instans i Altinn"-kallet og bruk det på slutten av url-en under.
(ved en hvilken som helst xml-fil). Merk at dataType skal settes til **skattemeldingOgNaeringsspesifikasjon**.

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=skattemeldingOgNaeringsspesifikasjon' \ --header 'Content-Disposition: attachment; filename=skattemelding.xml' \ --header 'Content-Type: text/xml' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@/home/k83452/Documents/Altinn3/Testfiler/Eksempel1_skattemeldingen..xml'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=skattemeldingOgNaeringsspesifikasjon' \ --header 'Content-Disposition: attachment; filename=skattemelding.xml' \ --header 'Content-Type: text/xml' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@/home/k83452/Documents/Altinn3/Testfiler/Eksempel1_skattemeldingen..xml'`

**Merk** følgende i curl-kommandoen over:
- content-type skal være **text/xml** (i dokumentasjonen hos altinn3 står det at content-type skal være application/xml, det er feil)
- Content-Disposition skal være **attachment; filename=skattemelding.xml** (skattemelding.xml skal ikke ha double quotes. Dette vil gi feil: filename="skattemelding.xml").



**Body :** `data-binary '../skattemelding.xml'.`
Innholdet i filen skattemelding.xml skal være på format:
- Iht. XSD: [skattemeldingognaeringsspesifikasjonrequest_v2_kompakt.xsd](/src/resources/xsd)
- Eksempel XML: [skattemeldingOgNaeringsspesifikasjonRequest.xml](/src/resources/eksempler/2021)

Merk at det er samme format som benyttes ved kall til valideringstjenesten.

**Respons :** `Respons vil inneholde metadata om objektet som ble opprettet. En unik identifikator (id) vil bli returnert som senere kan brukes til å oppdatere, sjekke status etc..`

<br />

## Trigge prosess/next for å få prosessen til status _Bekreftelse_

Når data opplastingen er gjort kan følgende kall gjøres for å få instansen over i neste status:

Plukk ut _id_ fra responsen til "Opprett en instans i Altinn"-kallet og bruk det på slutten av url-en under.

**Testmiljø:** `curl --location --request PUT 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/process/next' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <Altinn Token>' \ --data-raw '''`

**Produksjonsmiljø:** `curl --location --request PUT 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/process/next' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <Altinn Token>' \ --data-raw '''`  
<br />

## Åpne visningsklient for å se beregnet skattemelding med næringsspesifikasjon
Skatteetaten tilbyr en visningsklient for å se innholdet av skattemelding og næringsspesifikasjon slik Skatteetaten ser den.

Url'en til visningsklient kan åpnes fra nettleser:

**Testmiljø:** `https://skatt-sbstest.sits.no/web/skattemelding-visning/altinn?appId=skd/formueinntekt-skattemelding-v2&instansId=50091259/d7bdc27a-6d8f-4fee-8d95-8cc46a39504c`

**Produksjonsmiljø:** `https://skatt.skatteetaten.no/web/skattemelding-visning/altinn?appId=skd/formueinntekt-skattemelding-v2&instansId=50091259/d7bdc27a-6d8f-4fee-8d95-8cc46a39504c`

- Erstatt 50091259/d7bdc27a-6d8f-4fee-8d95-8cc46a39504c med verdien _id_ fra instansen

## Trigge prosess/next for å få prosessen til status _Tilbakemelding_

Gjør kall under for å gjøre seg ferdig med instansen/innsendingen, dette slik at Skatteetaten kan plukke den opp og behandle:

Plukk ut _id_ fra responsen til "Opprett en instans i Altinn"-kallet og bruk det på slutten av url-en under.

**Testmiljø:** `curl --location --request PUT 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/process/next' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <Altinn Token>' \ --data-raw ''`

**Produksjonsmiljø:** `curl --location --request PUT 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/process/next' \ --header 'Content-Type: application/json' \ --header 'Authorization: Bearer <Altinn Token>' \ --data-raw ''`

<br />

## Hente kvittering

Etter at innsendingen er blitt behandlet hos Skatteetaten vil det bli lastet opp en kvittering/tilbakemelding på instansen i altinn.
Kvitteringen (xml-fil) kan lastes ned ved å:
1. hente ut instans-data:
* **Testmiljø:** `curl --location --request GET 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50006869/060d4d74-dbb9-4ba3-a7d2-968e9b6e31ed' \ --header 'Authorization: Bearer <altinn Token>'`
* **Produksjonsmiljø:** `curl --location --request GET 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50006869/060d4d74-dbb9-4ba3-a7d2-968e9b6e31ed' \ --header 'Authorization: Bearer <altinn Token>'`

Les mer om det hos Altinn [get-instance](https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/app-api/instances/#get-instance)
2. plukke ut id-en til vedlegget 'tilbakemelding' fra payloaden i #1 (data --> dataType=tilbakemelding).
3. bruke Id-en fra #2 og kalle følgende API for å hente ut tilbakemeldingen:
* **Testmiljø:** `curl --location --request GET 'https://skd.apps.tt02.altinn.no/{{appId}}/instances/50006869/060d4d74-dbb9-4ba3-a7d2-968e9b6e31ed/data/<ID til vedlegget>' \ --header 'Authorization: Bearer <altinn Token>'`
* **Produksjonsmiljø:** `curl --location --request GET 'https://skd.apps.altinn.no/{{appId}}/instances/50006869/060d4d74-dbb9-4ba3-a7d2-968e9b6e31ed/data/<ID til vedlegget>' \ --header 'Authorization: Bearer <altinn Token>'`

Les mer om det hos Altinn [get-data](https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/app-api/data-elements/#get-data)
