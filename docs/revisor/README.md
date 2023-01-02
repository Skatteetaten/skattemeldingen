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

For å aktivere signeringssteget i Altinn3 så må instansen opprettes med paramteret
`"skalBekreftesAvRevisor": true` i `"dataValues":` elemenet. Eksempel på opprettet instans data vil da være:

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

- Ved å gå til altinn inboks, åpne instansen og gå til visningsklienten for å signere (begge steg- rollestyrt)
- eller utføre bekreftelsen fra eget sluttbrukersystem

1. Første steg, den skattepliktige signerer, utførs med en ‘next’ som er tilgangsstyrt
2. Andre steg, revisor signerer, utføres ved å laste opp et signeringsdokument og deretter ‘next’, tilgangsstyrt
   (beskrivelse av dokumetet på neste side).

Etter to `next` sendes skattemeldingen inn (går til feedback steget), og Skatt vil retunere en kvittering (ok/ikke ok)

## Bekreftelsesdokument

Signeringsdokumentet har sin egen xsd `revisorsbekreftelse_v1_ekstern.xsd`.

![informasjonsmodell.png](revisors_bekreftelse.png)

- Bekreftelse: referere informasjonselementidentifikator (entitet) som signeres og ev forekomstidentifikator
- Vedlegg: peke på informasjonselementidentifikator (entitet) vedlegget skal brukes kun hvis revisors signatur (
  erBekreftetGodkjent) er false og man laster opp dokumentasjon om hvorfor

## Feilhåndtering

Siden informasjonen om revisors signatur er fordelt mellom både Altinn instansen og informasjon i
skattemeldingen/næringsspesifikasjonen så kan det oppstå innkonsistens mellom disse to. 

Gitt følgende styring av revisors bekreftelseflag

| Altinn3 | Skattemelding/Næringspesifikasjon | Oppførsel |
|---------|-----------------------------------|-----------|
| Ja      | Ja                                | Normal    |
| Ja      | Nei                               | Case1     |
| Nei     | Ja                                | Case2     |
| Nei     | Nei                               | Normal    |

### Case 1a, Revisorsteg i Altinn aktiveres, men ingen flagg i skattemeldingen/næringsspesifikasjonen

Her vil skattepliktig bli veiledet i visningsklienten at revisor ikke har noe å signere, og vil mest sanysnlig bli tatt
ut i kontroll om det ikke rettes

### Case 1b, Revisorsteg i Altinn aktiveres, men selskapet har ingen revisor

Innsender/regnskapsfører har mulighet til å slette instansen i Task2_rev

### Case 2: Revisorsteg i Altinn aktveres ikke, men skattemeldingen/næringspesifikasjonen har satt flagget

Slipper igjennom og blir fastsatt, vil bli fanget opp på kontroll. 