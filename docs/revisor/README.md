# Revisors signering

Hvis dere har et tema/forekomst i skattemeldingen eller i næringsspesifikasjonen som skal signeres av revisor, så beskrives det her hvordan det skal gjennomføres

## Elementer som kan få signering av revisor

### Skattemelding personlig

Forskning og utvikling (`prosjektSkalBekreftesAvRevisor`)

### Skattemelding upersonlig

Følgende elementer kan signeres av revisor
Konsernbidrag (`konsernbidragSkalBekreftesAvRevisor`)
Forskning og utvikling (`prosjektSkalBekreftesAvRevisor`)

### Selskapsmelding

Forskning og utvikling (`prosjektSkalBekreftesAvRevisor`)

### Næringsspesifikasjon

Hele dokumentet skal signeres, så her ligger signeringselementet på rotnoden (`skalBekreftesAvRevisor`)

## Altinn3

**For å aktivere** signeringssteget i Altinn3 så må instansen opprettes med paramrteret
`"skalBekreftesAvRevisor": true` i `"dataValues":` elementet. 

_Det er viktig at dataValues settes ved opprettelse av Altinn3 instansen._

Eksempel på opprettet instans data vil da være:

```json
{
  "instanceOwner": {
    "organisationNumber": "99999999"
  },
  "appOwner": {
    "labels": [
      "gr",
      "x2"
    ]
  },
  "appId": "skd/formueinntekt-skattemelding-v2",
  "dataValues": {
    "inntektsaar": 2022,
    "skalBekreftesAvRevisor": true
  },
  "dueBefore": "2023-06-01T12:00:00Z",
  "visibleAfter": "2023-01-20T00:00:00Z",
  "title": {
    "nb": "Skattemelding"
  }
}
```
Dersom skattemeldingen eller næringsspesifiasjonen skal bekreftes av revisor så må _både_ altinn3 instansen og innholdet i skattemeldingen/næringsspesifikasjonen være satt korrekt.

Bekreftelsesstegene kan fullføres:

- Ved å gå til Altinn innboks, åpne instansen og gå til visningsklienten for å signere (begge steg- rollestyrt). 
  - PS! Lenken i Altinn går til produksjon, er dere i tt02 så må dere manuelt gå til "https://skatt-sbstest.sits.no/web/skattemelding-visning/altinn?appId=skd/formueinntekt-skattemelding-v2&instansId={instans-id}"
- Det er mulig å laste opp revisorvedlegget og sette instansen over i neste steg via deres eget fagsystem. 

1. Første steg, den skattepliktige signerer, utføres med en ‘next’ som er tilgangsstyrt
2. Andre steg, revisor signerer, utføres ved å laste opp et signeringsdokument og deretter ‘next’, tilgangsstyrt
   (beskrivelse av dokumentet på neste side).

Etter to `next` sendes skattemeldingen inn (går til feedback steget), og Skatt vil returnere en kvittering (ok/ikke ok)

## Bekreftelsesdokument

Signeringsdokumentet har sin egen xsd `revisorsbekreftelse_v1_ekstern.xsd`.

![informasjonsmodell.png](revisors_bekreftelse.png)

- Bekreftelse: peke på informasjonselementidentifikator (entitet) som signeres og evt forekomstidentifikator
- Vedlegg: Nytt i 2024 er at vedlegg ikke trenger å være knyttet til en informasjonselementidentifikator og evt. forekomstidentifikator. Det er fortsatt lovlig å knytte vedlegg til en entitet slik som før: 
  - Peke på samme informasjonselementidentifikator (entitet) og evt. forekomstidentifikator som bekreftelsen man ønsker å dokumentere med vedlegg.
- Bekreftelsedokumentet kan også lages ved å bruke skatteetatens klient for revisors bekreftelse. Hvis instansen har status som illustrert nedenfor, så kan en gå til innboksen til selskapet som har opprettet instansen, gå til instansen og følge lenken som fører til tjenesten som revisor kan bruke for å signere og laste opp bekreftelsedokument 
```json
  {
  "currentTask": {
    "flow": 4,
    "elementId": "Task_2Revisor",
    "name": "BekreftelseRevisor",
    "altinnTaskType": "confirmation",
    "flowType": "CompleteCurrentMoveToNext"
  }
}
```


## Feilhåndtering

Siden informasjonen om revisors signatur er fordelt mellom både Altinn instansen og informasjon i
skattemeldingen/næringsspesifikasjonen så kan det oppstå inkonsistens mellom disse to.

Gitt følgende styring av revisors bekreftelseflag

| Altinn3 | Skattemelding/Næringspesifikasjon | Oppførsel |
|---------|-----------------------------------|-----------|
| Ja      | Ja                                | Normal    |
| Ja      | Nei                               | Case1     |
| Nei     | Ja                                | Case2     |
| Nei     | Nei                               | Normal    |

### Case 1a, Revisorsteg i Altinn aktiveres, men ingen flagg i skattemeldingen/næringsspesifikasjonen

Her vil skattepliktig bli veiledet i visningsklienten at revisor ikke har noe å signere, og vil mest sannsynlig bli tatt
ut i kontroll om det ikke rettes

### Case 1b, Revisorsteg i Altinn aktiveres, men selskapet har ingen revisor

Innsender/regnskapsfører har mulighet til å slette instansen i Task2_rev

### Case 2: Revisorsteg i Altinn aktiveres ikke, men skattemeldingen/næringspesifikasjonen har satt flagget

Slipper igjennom og blir fastsatt, vil bli fanget opp på kontroll.

## Opplasntning og håndtering av revisors bekreftelse fra sluttbrukersystem
Sluttbrukersystemer trenger ikke å gå via Altinn og skatteetatens visningklient for å håndtere revisors signatur.
Alt kan håndteres fra sluttbrukersystemet.

### Last opp revisorsBekreftelse vedlegg
Når instansen metadata `currentTask.elementid = Task_2Revisor` så kan revisors bekreftelse xml lastes opp som vedlegg til Altinn3 instansen.

Hvis en av bekreftelsene er et avslag (`erBekreftetGodkjent = false`), så kan det dokumenteres ved hjelp av et vedlegg. Last opp vedlegg til instansen. Det kan være flere vedlegg for en bekreftelse, se eksempelet nedenfor

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=revisor-vedlegg' \ --header 'Content-Disposition: attachment; filename=Eksempel_Vedlegg.pdf' \ --header 'Content-Type: image/png' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@./Testfiler/vedlegg_revisor1.png'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=revisor-vedlegg' \ --header 'Content-Disposition: attachment; filename=Eksempel_Vedlegg.pdf' \ --header 'Content-Type: image/png' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@./Testfiler/vedlegg_revisor1.png`

**Merk**
- dataType=revisor-vedlegg
- Aksepterte content-types er: application/pdf, image/jpeg og image/png
- Content-disposition skal være: **attachment; filename=\<filnavn>**


Id'n til vedlegget legges inn i revisorsBekreftelse.xml, når denne xml'n er ferdig bygd opp, lastes den opp på instansen.
Når revisor-bekreftelse xml'n er lastet, så må en pålogget bruker med rettigheter til revisoren sette instansen klar til henting ved å kalle `next`

**Testmiljø:** `curl --location --request POST 'https://skd.apps.tt02.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=revisor-bekreftelse' \ --header 'Content-Disposition: attachment; filename=revisorsBekreftelse.xml' \ --header 'Content-Type: text/xml' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@./Testfiler/revisor_bekreftelse.xml'`

**Produksjonsmiljø:** `curl --location --request POST 'https://skd.apps.altinn.no/skd/formueinntekt-skattemelding-v2/instances/50028539/82652921-88e4-47d9-9551-b9da483e86c2/data?dataType=revisor-bekreftelse' \ --header 'Content-Disposition: attachment; filename=revisorsBekreftelse.xml' \ --header 'Content-Type: text/xml' \ --header 'Authorization: Bearer <Altinn token>' \ --data-binary '@./Testfiler/revisor_bekreftelse.xml`

**Merk**
- dataType=revisor-bekreftelse

revisorsBekreftelse.xml eksempel:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<revisorsBekreftelse xmlns="urn:no:skatteetaten:fastsetting:formueinntekt:skattemelding:revisorsbekreftelse:ekstern:v1">
    <bekreftelse>
        <id>6b3eeb0f-537c-40dc-8de7-0180fec01a4c</id>
        <informasjonselementidentifikator>
            /skattemelding/spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling/forskningOgUtviklingsprosjekt
        </informasjonselementidentifikator>
        <forekomstidentifikator>1</forekomstidentifikator>
        <erBekreftetGodkjent>false</erBekreftetGodkjent>
    </bekreftelse>
    <bekreftelse>
        <id>014db24d-5cf3-4814-9621-16b56804ce59</id>
        <informasjonselementidentifikator>/naeringsspesifikasjon</informasjonselementidentifikator>
        <erBekreftetGodkjent>true</erBekreftetGodkjent>
    </bekreftelse>
    <partsnummer>900416193856</partsnummer>
    <inntektsaar>2022</inntektsaar>
    <bekreftetAv>KORREKT PRESENTASJON</bekreftetAv>
    <vedlegg>
        <id>73e84423-45d2-4a4e-adad-4a4956069956</id>
        <vedleggsfil>
            <opprinneligFilnavn>
                <tekst>vedlegg_revisor3.png</tekst>
            </opprinneligFilnavn>
        </vedleggsfil>
        <informasjonselementidentifikator>
            <tekst>
                /skattemelding/spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling/forskningOgUtviklingsprosjekt
            </tekst>
        </informasjonselementidentifikator>
        <forekomstidentifikator>
            <tekst>1</tekst>
        </forekomstidentifikator>
    </vedlegg>
    <vedlegg>
        <id>8433c570-58cc-417d-93ad-05c54ee8816c</id>
        <vedleggsfil>
            <opprinneligFilnavn>
                <tekst>vedlegg_revisor2.png</tekst>
            </opprinneligFilnavn>
        </vedleggsfil>
        <informasjonselementidentifikator>
            <tekst>
                /skattemelding/spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling/forskningOgUtviklingsprosjekt
            </tekst>
        </informasjonselementidentifikator>
        <forekomstidentifikator>
            <tekst>1</tekst>
        </forekomstidentifikator>
    </vedlegg>
    <vedlegg>
        <id>8043f993-e6e2-4f3d-8af3-fa831a495c03</id>
        <vedleggsfil>
            <opprinneligFilnavn>
                <tekst>vedlegg_revisor1.png</tekst>
            </opprinneligFilnavn>
        </vedleggsfil>
        <informasjonselementidentifikator>
            <tekst>
                /skattemelding/spesifikasjonAvSkattefradragForKostnaderTilForskningOgUtvikling/forskningOgUtviklingsprosjekt
            </tekst>
        </informasjonselementidentifikator>
        <forekomstidentifikator>
            <tekst>1</tekst>
        </forekomstidentifikator>
    </vedlegg>
</revisorsBekreftelse>
```

**Merk:**
`informasjonselementidentifikator`: xpath til elementet som skal bekreftes
`forekomstidentifikator`: forekomstidentifikator til xpath elementet
`erBekreftetGodkjent`: true/false, om elementet er bekreftet av revisor eller ikke
`/vedlegg/id`: id til vedlegget i altinn3 instansen