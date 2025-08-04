---
icon: "cloud"
title: "Test"
description: ""
---

# Hvordan komme igang

Formålet med testen er å verifisere uthenting av skattemelding, bruk av valideringstjenesten hos Skatteetaten, sende inn og få tilbakmelding.

# Innhold i testen

Konkret må sluttbrukersystemet gjøre følgende:

1. Få etablert et kundeforhold hos Digitaliseringsdirektoratet (DigDir) og ta i bruk ID-porten.
2. Logge inn hos ID-porten for å få en sesjon (og token).
3. Utvikle en applikasjon/klient som:
   - Sende en request til Skatteetatens tjeneste for å hente ut skattemeldingen for en skattepliktig.
   - Sende en request til Skatteetatens tjeneste for å validere skattemelding med næringsinformasjon for en skattepliktig
   - Motta og tolke resultatet fra valideringstjenesten.
   - Når det ikke er feil i retur fra valideringstjenesten kan man gå til neste punkt.
   - Kalle Altinn sin nye plattform for å instansiere opp App'en for innsending av skattemelding for næringsdrivende. 
   - Hvis nødvendig, kalle Altinn for å laste opp vedlegg til skattemeldingen eller næringsspesifikasjonen
   - Kalle Altinn for å laste opp skattemelding og næringsspesifikasjon på den instansierte App'en fra forrige punkt.
   - Kalle Altinn for å laste ned kvittering/tilbakemelding fra Skatteetaten.

Skatteetaten har tilgjengeliggjort en testapplikasjon som viser hvordan trinnene beskrevet over kan utføres.
Den er skrevet i [jupyter notebook formatet](https://jupyter.org/):

1. [Jupyter notebook demo for henting, validering og innsending](testinnsending/person-enk-med-vedlegg-2021.ipynb). Last ned katalogen testinnsending og kjør skriptet demo.ipynb (skriptet vil utføre alle trinn som inngår i prosessen: hente/velidere skattemelding og innsending av skattemelding til Altinn)  
2. [Eksempel XML-er](../../src/resources/eksempler)

## Ta i bruk ID-porten

Det første som en konsument av skattemeldings-API må gjøre er å etablere et kundeforhold hos Digitaliseringsdirektoratet (DigDir). Detaljer rundt hvordan ta i bruk ID-porten er beskrevet her: https://samarbeid.digdir.no/id-porten/ta-i-bruk-id-porten/94. Denne prosessen er delvis manuell så det er lurt å starte prosessen tidlig slik at dere kan komme i gang med testingen. Ved bestilling må dere oppgi at dere ønsker tilgang til skattemeldings-API fra skatteetaten.

Et kundeforhold hos DigDir gir tilgang til deres selvbetjeningsløsning som igjen gir tilgang til administrasjon av Kundens bruk av ID-porten. I selvbetjeningsløsning kan kunden genere et såkalt client_id og definere et callback-url:

- client_id: er unik automatisk generert identifikator for tjenesten.
- callback-url: Uri-en som klienten får lov å gå til etter innlogging. Etter en vellykket innlogging i ID-porten vil denne url-en bli kalt.
  Dersom det skulle ta for lang tid å få opprettet et kundeforhold hos DigDir kan sluttbrukersystemene i mellomtiden benytte Skatteetatens client_id. For denne testen har Skatteetaten opprettet følgende client_id som kan benyttes av sluttbrukersystemene:

      - `client_id: 38e634d9-5682-44ae-9b60-db636efe3156`
      - Callback-URL til denne client_id er satt til http://localhost:12345/token (Hvis det er konsumenter som ønsker andre callback-URL kan vi ordne det)

**Nyttige lenker:**

- Klienten bruker test-miljøet i DigDir, "verifikasjon 2": https://www.skatteetaten.no/skjema/testdata/
- OIDC-integrasjonen er beskrevet her: https://docs.digdir.no/docs/idporten/oidc/oidc_guide_idporten
- Hvordan lage en klient i selvregistreringen: https://docs.digdir.no/docs/idporten/oidc/oidc_api_admin.html
- Tenor testdatasøk: https://www.skatteetaten.no/skjema/testdata

## Logge inn hos ID-porten

Klienten må foreta følgende REST kall mot ID-porten:

- Starte system browser og gjøre autorisasjonskall mot ID-porten. Les mer om det her: https://docs.digdir.no/docs/idporten/oidc/oidc_protocol_authorize
- Brukeren blir da sendt til ID-porten for innlogging. Dere kan benytte eksisterende test-brukere som dere benytter til test mot skattemeldingen i dag.
- Sette opp en webserver som venter på callback fra browseren. Etter vellykket pålogging i ID-porten sendes brukes til denne webserveren. Denne webserveren må være satt til å lytte på callback-URL http://localhost:12345/token (ref. forrige kapittel).
- Gjøre et tokenforespørsel. Les mer om det her: https://docs.digdir.no/docs/idporten/oidc/oidc_protocol_token

Vi benytter følgende testmiljø hos ID-porten:

- /authorize endpoint: https://login.test.idporten.no/authorize
- /token endpoint: https://test.idporten.no/token

For detaljer rundt hvilken HTTP parametere som må sendes med i kallet, se filen [hent.py](testinnsending/skatteetaten_api.py)

## Kalle skattemeldings-API

Når callback-URL blir kalt må klienten plukke ut JWT-tokenet fra responsen og legge det i header-feltet Authorization og kalle skattemeldings-API. For detaljer, se [demo klient](./testinnsending/person-enk-2022.ipynb)

URL til skattemeldings-API i test er: https://idporten-api-test.sits.no/

Første testen bør være å teste at klienten når frem, dette kan gjøres ved å kalle ping tjenesten:

- `GET https://<env>/api/skattemelding/v2/ping`
- `Eksempel: GET https://idporten-api-test.sits.no/api/skattemelding/v2/ping`

For nærmere beskrivelse av skattemelding-API og Altinn3-API, se kapitelet [API](/docs/api-v2/README.md)

# Se skattemelding i portal

Hvis du gjør en ikke komplett insending så dukker det opp en melding i innboksen i Altinn. Denne sier at du må logge på skatteetatens sider for å fullføre innsendingen, med en lenke til "Min skatt" sidene i produksjon. 
Lenken i testmiljøet til "Min skatt" sidene er: https://skatt-test.sits.no/web/mineskatteforhold/

Det er også mulig å gå direkte til skattemelding portalen.  
Direkte URL til skattemelding portal: https://skatt-test.sits.no/web/skattemeldingen/2021


Bruk TestId innlogging; fnr på testbruker

Merk at dere også kan endre/tilføye opplysninger og sende inn skattemelding. Fastsatt skattemelding (endret skattemelding) vil da også være tilgjengelig for nedlasting. Se kapittel 4.3.6.3 i implementasjonsguide.

