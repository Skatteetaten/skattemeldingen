---
icon: "cloud"
title: "API"
description: "Api-beskrivelser"
---

Det tilbys to sett med API-er som hver har endel tjenester:

- Skatteetatens-API for tjenester rundt innholdet i en skattemelding.
- Altinn3-API for opprettelse og innsending av en skattemeldinger.

![apier.png](apier.png)

# Skatteetatens-API

Skatteetaten har utviklet en testklient (i python) som viser hvordan koble seg på ID-porten og kalle skatteetatens-API:
[skattemelding-eksternt-api-test.zip](skattemelding-eksternt-api-test.zip)

## Autentisering

Når en skattepliktig skal benytte et sluttbrukersystem for å sende inn skattemeldingen og næringsopplysninger gjennom API må sluttbrukeren og/eller sluttbrukersystemet være autentisert og autorisert gjennom en påloggingsprosess.

Ved kall til skattemelding-API ønsker skatteetaten å kjenne til identiteten til innsender. Identiteten til pålogget bruker, kombinert med informasjon fra Altinn autorisasjon vil avgjøre hvilken person/selskap en pålogget bruker kan hente skattemeldingen til eller sende inn skattemelding for.

Autentisering skjer enten via ID-porten eller Maskinporten:

- Personlig innlogging vil skje via ID-porten.
- Systemer/maskiner som ønsker å opptre på vegne av en organisasjon kan autentisere seg via maskinporten.

**Merk** at dagens Altinn innlogging med brukernavn/passord vil ikke lenger kunne brukes.

### ID-porten

Via ID-porten kan selve sluttbrukeren autentiseres og da via sitt personnummer.

![idporten.png](idporten.png)

#### Registrering av et sluttbrukersystem i ID-porten

Når et sluttbrukersystem initierer en påloggingsprosess mot ID-porten må SBS sende med en klient-ID. Denne klient-id-en er unik for SBS-typen og vil bli tildelt ved at programvareleverandøren av SBS på forhånd har gjennomført en registrering (onboarding) i en selvbetjeningsportal hos Digdir/Difi. Dette er beskrevet her: https://difi.github.io/felleslosninger/oidc_index.html. Lenken beskriver også standarden OIDC som ID-porten er basert på.

I registreringen inngår følgende data:

- Klient-id. Denne er en GUID som tildeles fra Digdir/Difi.
- Klient-navn. Dette er navn på SBS-systemet. Denne settes av SBS-leverandøren og det er ingen videre krav til navnestandard eller om programvareversjon skal inngå i navnet.
- Klient-navn skal presenteres for brukeren under påloggingen.
- Hvilken offentlig API-tjeneste registreringen gjelder. Dette identifiseres ved navn på en autorisasjonstype som er definert i ID-porten. For skattemeldingen foreslår vi autorisasjonstypenavnet "Skattemeldingen-API". merk! Dette støttes ikke i dag, men vil komme senere.
- Registreringen må knyttes opp mot en juridisk ansvarlig aktør, dvs et norsk selskap identifisert ved et orgnr.

### Maskin-porten

Maskinporten sørger for sikker autentisering og tilgangskontroll for datautveksling mellom virksomheter. Løsningen garanterer identiteten mellom virksomheter og gjør det mulig å binde sammen systemer.

Et sluttbrukersystem som kjører på en sikker server kan integreres i Maskinporten og da være autentisert med sitt organisasjonsnummer. Det vil da være organisasjonen som autentiserer seg. Hvilken sluttbruker som utfører hvilken handling i deres system må organisasjonen selv holde kontroll på. En forutsetning for bruk av Maskinporten er derfor at organisasjonen har bygget et godt tilgangskontroll av sine sluttbrukere.

En autentisering gjort via Maskinporten tilrettelegger for høyere grad av automatisering da det ikke krever en personlig kodebrikke eller liknende. Vi tror Maskinporten vil passe for store selskap og regnskapsførere som skal levere skattemeldingen for mange.

Bruk av Maskinporten forutsetter at organisasjonen har et virksomhetssertifikat eller tilsvarende mekanisme. Figuren under skisserer hvordan samhandlingen fungerer:

![maskinporten.png](maskinporten.png)

Les detaljer om maksinporten her: https://difi.github.io/felleslosninger/maskinporten_guide_apikonsument.html

## Autorisasjon

API-ene som tilbys vil sjekke at sluttbrukeren eller eier av sluttbrukersystemet har tilgang til å utføre operasjoner gjennom API-et. Slik tilgangskontroll/autorisering skjer via Altinns autorisasjonskomponent.

Dette betyr at sluttbrukeren eller eier av sluttbrukersystemet må ha de nødvendige rollene i Altinn. Dette blir som i eksisterende løsninger.

## Ping tjeneste

API tilbyr en ping tjeneste som kan kalles for å teste at integrasjonen fungerer.

**URL** : `GET https://<env>/api/eksterntapi/formueinntekt/skattemelding/ping`

**Eksempel URL** : `GET https://mp-test.sits.no/api/eksterntapi/formueinntekt/skattemelding/ping`

**Forespørsel** : `<env>: Miljøspesifikk adresse`

**Respons** :

```json
{
    "name": "ping": "pong"
}
```

For detaljer om ping-tjenesten og "hent Skattemelding" tjenestene, se filen hent.py [skattemelding-eksternt-api-test.zip](skattemelding-eksternt-api-test.zip)

## Hent skattemelding

API som returnerer siste gjeldende skattemeldingen for skattepliktige for gitt inntektsår. Den siste gjeldende skattemeldingen kan enten være utkast eller fastsatt:

- Utkast er en preutfylt skattemelding Skatteetaten har laget for den skattepliktige basert på innrapporterte data og data fra skattemeldingen tidligere år.
- Fastsatt betyr at skattemeldingen er manuelt innlevert eller automatisk innlevert ved utløp av innleveringsfrist. Dette kan også inneholde et eller flere myndighetsfastsatte felter.

**URL** : `GET https://<env>/api/eksterntapi/formueinntekt/skattemelding/<inntektsaar>/<identifikator>/`

**Eksempel URL** : `GET https://mp-test.sits.no/api/eksterntapi/formueinntekt/skattemelding/2020/974761076`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige som man spør om skattemeldingen for.`

**Respons** :

```xml
<hentSkattemeldingMvResponse xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemelding:hentskattemeldingmv:response:v1">
    <dokumenter>
        <skattemeldingdokument>
            <id>SKI:138:5829</id>
            <encoding>utf-8</encoding>
            <content>Base64-enkodet skattemelding.xml iht XSD</content>
            <type>skattemeldingUtkastPersonligSkattepliktig</type>
        </skattemeldingdokument>
    </dokumenter>
</hentSkattemeldingMvResponse>
```

## Hent Skattemelding (basert på type)

API som returnerer siste gjeldende skattemeldingen av gitt type for skattepliktige for gitt inntektsår. Følgende type skattemeldinger er støttet:

- Utkast er en preutfylt skattemelding Skatteetaten har laget for den skattepliktige basert på innrapporterte data og data fra skattemeldingen tidligere år.
- Fastsatt betyr at skattemeldingen er manuelt innlevert eller automatisk innlevert ved utløp av innleveringsfrist. Dette kan også inneholde et eller flere myndighetsfastsatte felter.

**URL** : `GET https://<env>/api/eksterntapi/formueinntekt/skattemelding/<type>/<inntektsaar>/<identifikator>/`

**Eksempel URL** : `GET https://mp-test.sits.no/api/eksterntapi/formueinntekt/skattemelding/utkast/2020/974761076`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige som man spør om skattemeldingen for.`

**Respons** :

```xml
<hentSkattemeldingMvResponse xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemelding:hentskattemeldingmv:response:v1">
    <dokumenter>
        <skattemeldingdokument>
            <id>SKI:138:5829</id>
            <encoding>utf-8</encoding>
            <content>Base64-enkodet skattemelding.xml iht XSD</content>
            <type>skattemeldingUtkastPersonligSkattepliktig</type>
        </skattemeldingdokument>
    </dokumenter>
</hentSkattemeldingMvResponse>
```

## Valider skattemelding

Tjenesten validerer innholdet i en skattemelding og returnerer en respons med eventuelle feil, avvik og advarsler. Tjenesten vil foreta følgende:

1. Kontroll av meldingsformatet.
2. Kontroll av innholdet og sammensetningen av elementene i skattemeldingen.
3. Beregninger/kalkyler.

Skatteetaten ønsker at valideringstjenesten blir kalt i forkant av innsending av skattemeldingen. Dette for å sikre at skattemeldingen er korrekt og vil mest sannsynligvis bli godkjent ved innsending.
Uansett versjon vil skatteetaten ikke lagre eller følge opp informasjonen som sendes inn i valideringstjenesten på noen måte. Skatteetaten anser at disse dataene eies av den skattepliktige og ikke av skatteetaten.

**URL** : `POST https://<env>/api/eksterntapi/formueinntekt/skattemelding/valider/<inntektsaar>/<identifikator>`

**Eksempel URL** : `POST https://mp-test.sits.no/api/eksterntapi/formueinntekt/skattemelding/valider/2020/01028312345`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse`
- `<inntektsår>: Inntektsåret man spør om informasjon for, i formatet YYYY.`
- `<identifikator>: Fødselsnummer, D-nummer eller organisasjonsnummer til den skattepliktige`

**Body** :

```xml
<valideringsrequest xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemelding:valideringsrequest:v1">
  <dokumenter>
    <dokument>
      <type>skattemeldingPersonligSkattepliktig</type>
      <encoding>utf-8</encoding>
      <content>Base64-enkodet skattemelding.xml iht xsd-en</content>
    </dokument>
  </dokumenter>
</valideringsrequest>
```

**Respons** :

```xml
<valideringsresponse xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemelding:validering:response:v1">
        <dokumenter>
            <dokument>
                <type>skattemeldingEtterBeregning</type>
                <encoding>utf-8</encoding>
                <content>Base64-enkodet xml</content>
            </dokument>
            <dokument>
                <type>naeringsopplysningerEtterBeregning</type>
                <encoding>utf-8</encoding>
                <content>Base64-enkodet xml</content>
            </dokument>
            <dokument>
                <type>beregnetSkatt</type>
                <encoding>utf-8</encoding>
                <content>Base64-enkodet xml</content>
            </dokument>
            <dokument>
                <type>summertSkattegrunnlagForVisning</type>
                <encoding>utf-8</encoding>
                <content>Base64-enkodet xml</content>
            </dokument>
        </dokumenter>
        <avvikEtterBeregning>
            <avvik>
                <avvikstype>manglerNaeringsopplysninger</avvikstype>
                <forekomstidentifikator>global</forekomstidentifikator>
                <beregnetVerdi>0</beregnetVerdi>
                <sti>resultatregnskap/driftsinntekt/sumDriftsinntekt/beloep/beloepSomHeltall</sti>
            </avvik>
        </avvikEtterBeregning></valideringsresponse>
```

For detaljer om valider-tjenesten, se filen valider.py [skattemelding-eksternt-api-test.zip](skattemelding-eksternt-api-test.zip)

# Altinn3-API

For applikasjonsbrukere, dvs. organisasjoner og personer som kaller Altinn gjennom et klient API (typisk skattepliktige som bruker et sluttbrukersystem) tilbyr Altinn API-er med følgende funksjonalitet:

- Opprette en instans - i vårt tilfelle vil en instans være en skattemelding for et gitt år.
- Laste opp data - Brukes til å laste opp skattedata til en instans i Altinn. Relevante filer å laste opp vil være metadata og selve skattemeldingen.
- Laste ned data - Brukes til å laste ned data som tidligere er blitt lastet opp.
- Hente og trigge prosesstilstandendring.
- Hente eventer skjedd på en instans.

Les mer om Altinn API-ene på [altinn sine sider](https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/). Altinn har utviklet POSTMAN skript som viser hvordan deres APIer kan bli kalt. Postman skriptene [finnes her](https://github.com/Altinn/altinn-studio/blob/master/src/test/Postman/collections/App.postman_collection.json)

Skatteetaten har også laget POSTMAN-skript som gjør kallene beskrevet under, se: [postman_collection.json](./Skattemelding - Innsending - Altinn3.postman_collection.json)

Tjenestene listet under kalles for å sende inn skattemelding til Altinn. Under følger en nærmere beskrivelse av hvordan skatteetatens innsendingstjenste vil fungere.

_Merk at Base URL-en_ til applikasjonen vår i Altinn er: `https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/.`

## Autentisering

Første trinn er å få generert et autentiseringstoken i Altinn. Autentisering skjer enten via maskinporten eller ID-porten. Les mer om det på [altinn sine sider](https://docs.altinn.studio/teknologi/altinnstudio/altinn-api/authentication/)

Tokenet fra maskinporten/ID-porten brukes til å veksle det inn i et Altinn JWT access token. Det er Altinn tokenet som brukes videre til å kalle Altinn-APIer beskervet under.

## Hent partyID

PartyID er et intern Id hos Altinn som brukes til å kalle de øvrige tjenestene.

**URL** : `GET https://<env>/api/v1/profile/user`

**Eksempel URL** : `GET https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/api/v1/profile/user`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse på format: https://{{org}}.apps.{{envUrl}}/{{org}}/{{app}}.`
- `En header kalt Authorization må inneholde Altinn token (ref. avsnittet autentisering).`

**Respons** : PartyId

## Opprette instans

Første trinn i innsendingsløpet er opprettelse av en instans av skattemeldingen. Dette gjøres ved å kalle:

**URL** : `POST https://<env>/instances`

**Eksempel URL** : `https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/instances/`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse på format: https://{{org}}.apps.{{envUrl}}/{{org}}/{{app}}.`
- `En header kalt Authorization må inneholde Altinn token (ref. avsnittet autentisering).`

**Body** :

```xml
{
    "instanceOwner": {
        "partyId": "50006841"
    },
    "appOwner":{
        "labels" : [ "gr", "x2" ]
    },
    "appId" : "skd/sirius-skattemelding-v1",
    "dueBefore": "2020-06-01T12:00:00Z",
    "visibleAfter": "2019-05-20T00:00:00Z",
    "title": { "nb": "Skattemelding" }
}
```

**Respons** : Metadata om instansen som ble opprettet. En unik instanceId vil være med i responen og kan brukes seinere til å hente/oppdatere instansen.

## Laste opp metadata

Neste trinn er å laste opp meta-data (informasjon om hvem innsender er, dato etc.) om skattemeldingen. Meta-data skal være en XML-fil iht. Skattemeldingsapp_v1.xsd (se også eksempelfilen: Skattemeldingsapp_v1.xml).

**URL** : `POST https://<env>/instances`
**Eksempel URL** : `https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/instances/50006841/55e65419-361f-4da5-9ad2-dce08ba6a0e1/data?dataType=Skattemeldingsapp_v1`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse på format: https://{{org}}.apps.{{envUrl}}/{{org}}/{{app}}.`
- `En header kalt Authorization må inneholde Altinn token (ref. avsnittet autentisering).`

## Laste opp skattedata

Neste trinn er å laste opp vedlegg.

**URL** : `POST {appPath}/instances/{partyId}/{instanceId}/data?dataType={type}`

**Eksempel URL** : `https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/instances/50006841/ef796f7b-8168-4373-bcc6-cf6c42a8873e/data?dataType=skattemelding`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse på format: https://{{org}}.apps.{{envUrl}}/{{org}}/{{app}}.`
- `En header kalt Authorization må inneholde Altinn token (ref. avsnittet autentisering).`

**Body** : `data-binary '../skattemelding.xml'. Filen skattemelding.xml skal være på samme format som responsen fra tjenesten "Hent Skattemelding".`

**Respons** : `Respons vil inneholde metadata om objektet som ble opprettet. En unik identifikator (id) vil bli retunert som seinere kan brukes til å oppdatere, sjekke status etc..`

## Trigge prosesstilstandsendring

For å endre prosesstilstanden kan følgende API kalles:

**URL** : `GET {appPath}/instances/{instanceOwnerId}/{instanceId}/prosess/start|next|completeProcess`

**Eksempel URL** : `https://skd.apps.tt02.altinn.no/skd/sirius-skattemelding-v1/instances/50006841/ef796f7b-8168-4373-bcc6-cf6c42a8873e/process/completeProcess`

**Forespørsel** :

- `<env>: Miljøspesifikk adresse på format: https://{{org}}.apps.{{envUrl}}/{{org}}/{{app}}.`
- `En header kalt Authorization må inneholde Altinn token (ref. avsnittet autentisering).`
