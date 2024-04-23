# Asynkront API

## Hvem skal bruke asynrkont API
Det asynkrone api'et er laget for beregninger som tar for lang tid til at en kan bruke de synkrone endepunktene som er beskrevet under api-v2. 
Alle som har en næringsspesifikasjon som er større en 10MB skal bruke API'ene beskrevet her.

## Beskrivelse av bruksmønster
1. `/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/last-opp.vedlegg`
   Først lastes næringsspesifikasjonen opp base64 encoded, retur objektet inneholder en referanse
2. `/api/skattemelding/v2/jobb/<identifikator>/start`
   Når en skal gjøre en validering, så må en egen valideringsjobb startes. I skattemeldingOgNaeringsspesifikasjonRequest så brukes referansen ved å  legge ved et dokument av typen naeringsspesifikasjonReferanse. Her retuneres en jobId
3. `/api/skattemelding/v2/jobb/status`
   Henter status på valideringsjobben mens den kjører. 
4. `/api/skattemelding/v2/jobb/<identifikator>/<jobbId>/resultat`
   Når valideringen er ferdig så vil en kunne hente ned hele valideringsresultatet. 
5. Send inn via Altinn. Når valideringen er validert ok, så brukes samme naeringsspesifikasjonReferanse når en sender inn skattemeldingen via Altinn. 


PS! det er ikke støtte for signering av revisor for dette innsendingsmønsteret per nå. 


## 1. Last opp næringsspesifikasjonen 
Dette api'et bruker en multipart formdata for å laste opp vedlegget. 

**Header:** 
- Content-Type: multipart/form-data; boundary=<id for boundary>
- Content-disiposition

URL: `POST https://<env>/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/last-opp.vedlegg`



### Eksempel kall i testmiljøet: 
curl -XPOST https://idporten-api-sbstest.sits.no/api/skattemelding/v2/jobb/2023/{{identifikator}}/last-opp.vedlegg
Authorization: Bearer {{token}}
Content-Type: multipart/form-data; boundary=boundary

--boundary
Content-Disposition: form-data; name="naeringsspesifikasjon.xml"; filename="naeringsspesifikasjon.xml"
PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNrYXR0ZW1lbGRpbmcgeG1s
bnM9InVybjpubzpza2F0dGVldGF0ZW46ZmFzdHNldHRpbmc6Zm9ybXVlaW5udGVrdDpza2F0dGVt
ZWxkaW5nOmVrc3Rlcm46djgiPgogIDxwYXJ0c3JlZmVyYW5zZT4xMjM8L3BhcnRzcmVmZXJhbnNl
PgogIDxpbm50ZWt0c2Fhcj4yMDIwPC9pbm50ZWt0c2Fhcj4KICA8c2thdHRlbWVsZGluZ09wcHJl
dHRldD4KICAgIDxicnVrZXJpZGVudGlmaWthdG9yPmlra2UtaW1wbGVtZW50ZXJ0PC9icnVrZXJp
ZGVudGlmaWthdG9yPgogICAgPGJydWtlcmlkZW50aWZpa2F0b3J0eXBlPnN5c3RlbWlkZW50aWZp
a2F0b3I8L2JydWtlcmlkZW50aWZpa2F0b3J0eXBlPgogICAgPG9wcHJldHRldERhdG8+MjAyMC0x
MC0yMVQwNjozMjowNi45OTMwMzlaPC9vcHByZXR0ZXREYXRvPgogIDwvc2thdHRlbWVsZGluZ09w
cHJldHRldD4KPC9za2F0dGVtZWxkaW5nPg==
--boundary


**Respons body:**

```json
{
  "referanse": "a7a076307d21385cf50209cebdc2b2886467"
}
```

## 2. Start en validerings jobb
Dette api'et er veldig likt det synkrone validerings api'et, men her er responsen en jobId, og en bytter ut dokumentet naeringsspesifikasjon med naeringsspesifikasjonReferanse


URL: `POST https://<env>/api/skattemelding/v2/jobb/<identifikator>/start`


### Eksempel kall i testmiljøet

curl -XPOST `https://idporten-api-sbstest.sits.no/api/skattemelding/v2/jobb/<identifikator>/start` -d\

```xml
<skattemeldingOgNaeringsspesifikasjonRequest xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemeldingognaeringsspesifikasjon:request:v2">
    <dokumenter>
        <dokument>
            <type>skattemeldingUpersonlig</type>
            <encoding>utf-8</encoding>
            <content>base64 encoded str
             </content>
        </dokument>
        <dokument>
            <type>naeringsspesifikasjonReferanse</type>
            <encoding>utf-8</encoding>
            <content>a7a076307d21385cf50209cebdc2b2886467</content>
        </dokument>
    </dokumenter>
    <dokumentreferanseTilGjeldendeDokument>
        <dokumenttype>skattemeldingUpersonlig</dokumenttype>
        <dokumentidentifikator>SKI:755:15086617</dokumentidentifikator>
    </dokumentreferanseTilGjeldendeDokument>
    <inntektsaar>2023</inntektsaar>
    <innsendingsinformasjon>
        <innsendingstype>komplett</innsendingstype>
        <opprettetAv>sluttbrukersystem</opprettetAv>
    </innsendingsinformasjon>
</skattemeldingOgNaeringsspesifikasjonRequest>
```

**Respons body:**

```json
{
  "jobbStatus": "OPPRETTET",
  "jobbId": "668226ad7643181b4c525c98ec96773492b9"
}

```


## 3. Hent status
Enkelte jobber kan ta tid, vår estimat for den største næringsspeifikasjonen for inntektsår 2023, kan ta over en time. Det er mulig å spørre på status på en jobb, uten at det påvirker beregningstiden
URL: `GET https://<env>/api/skattemelding/v2/jobb/<identifikator>/status`

**Response body:**

```json
{
  "jobbStatus": "FERDIG",
  "forrigeStatus": "KJOERER",
  "sekunderSidenOpprettelse": 10,
  "sekunderSidenEndring": 9
}
```

**Forklaring til respons**

- jobbStatus: viser om jobben, følgende status er mulige:
  - NY - jobben er opprettet, men ingenting er gjort enda
  - KJOERER - jobben utføres
  - PAUSET - jobben er satt på vent til det er nok tilgjenglige resurser. 
  - AVBRUTT - jobben ble avbrutt, av f.eks en nyere jobb for samme identifikator
  - FEILET - jobben feilet av en teknisk årsak
  - FERDIG - jobben er ferdig
- forrigeStatus: sist jobbstatus
- sekunderSidenOpprettelse: type Int, viser hvor mange sekunder det har vært siden jobben ble opprettet
- sekunderSidenEndring: type Int, viser hvor mange sekunder jobben har hatt statusen den har nå. 


## 4. Hent resultat
Når jobben har status ferdig, så kan resultatet hentes, da vil en få alle de beregnede modellene og valideringsrultatene. 


URL: `GET https://<env>/api/skattemelding/v2/jobb/<identifikator>/resultat`


### Respons jobbstatus=FERDIG
Responsen vil vær helt lik en normal validering på validering v2 og følge xsd'n til `skattemeldingOgNaeringsspesifikasjonResponse`.

Rspons content-typen være av type application/xml


### Respons jobbstatus!=FERDIG
Hvis jobben eksisterer og jobben ikke er ferdig, vil vi retunere en http 204


# Vedlegg i næringsspesifikasjonen er ikke støttet
Asynk api'et støter ikke eksterne vedlegg i næringsspesifikasjonen. Ønsker en å legge til vedlegg for dokumentasjon, så må dette referes i skattemeldingen
