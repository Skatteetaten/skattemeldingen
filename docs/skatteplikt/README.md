## Perioder API

API-ene er rettet mot brukere av SBS som bistår skattepliktige med begrenset skatteplikt, med leveringen av skattemeldingen.

API-ene gir tilgang til å hente, vurdere og lagre periodegrunnlaget med hensikten å korrigere tolvdeler og skatteplikt for den skattepliktige.
Tolvdeler og skatteplikt kan ikke registreres eller overstyres manuelt, de beregnes automatisk basert på den skattepliktiges registrerte oppholdsperioder og arbeidsforholdsperioder.

Funksjonaliteten tilsvarer det som er tilgjengelig i Min skatt under «Arbeid og opphold i Norge», der skattepliktige selv kan logge inn og registrere eller endre oppholds- og arbeidsforholdsperioder.

### Hent perioder

API-et returnerer gjeldende antall tolvdeler, skatteplikt og alle registrerte oppholds- og arbeidsforholdsperioder for en skattepliktig for et gitt inntektsår.

**URL** : `GET https://<env>/api/skattemelding/v2/skatteplikt/perioder/<inntektsaar>/<identifikator>/`

**Eksempel URL** : `GET https://idporten.api.skatteetaten.no/api/skattemelding/v2/skatteplikt/perioder/2025/60875800372`


**Forespørsel** :

- `<inntektsaar>`: inntektsåret det hentes informasjon for (format: YYYY)
- `<identifikator>`: Fødselsnummer eller D‑nummer til den skattepliktig

**Respons** :

- Iht. XSD: [perioderformottakogutveksling_v1.xsd](/src/resources/xsd/perioderformottakogutveksling_v1.xsd)

- Eksempel respons JSON: 

``` json
{
     "tin" : "28818698897",
     "skattepliktTilNorge" : "BEGRENSET",
     "skatteordning" : null,
     "tolvdeler" : 1,
     "perioder" : {
          "oppholdsperiode" : [
               {
                    "kanEndres" : true,
                    "norskPersonidentifikator" : "28818698897",
                    "kommune" : null,
                    "periodeidentifikator" : "2518d6ba-4989-4aee-b86a-c5054fc49a94",
                    "fraOgMedDato" : "2025-05-01",
                    "tilOgMedDato" : "2025-05-31",
                    "informasjonskilde" : "OPPLYST_TIL_SAKSBEHANDLER_AV_SKATTEPLIKTIG",
                    "erMerketSomSlettet" : false,
                    "antallDagerOverstyrt" : null
               }
          ],
          "arbeidsforholdsperiode" : [
               {
                    "kanEndres" : true,
                    "arbeidsforholdIOppdrag" : null,
                    "arbeidsforholdSomSjoemann" : null,
                    "spesifikasjonAvInntektsgivendeAktivitet" : {
                         "oppholdsstedVedInntektsgivendeAktivitet" : "FASTLAND",
                         "inntektsgivendeAktivitetstype" : "ARBEIDSTAKER"
                    },
                    "ansvarligEnhetEllerPart" : {
                         "norskIdentifikator" : {
                              "organisasjonsnummer" : "315078385",
                              "personidentifikator" : null
                         },
                         "hovedorganisasjon" : null,
                         "underenhetensOrganisasjonsnummer" : null,
                         "naeringsdrivendeUtenKravTilOrganisasjonsnummer" : null,
                         "navn" : "ENKEL ENKEL KATT INVITASJON"
                    },
                    "norskPersonidentifikator" : "28818698897",
                    "kommune" : "4641",
                    "periodeidentifikator" : "b1e6b902-07b7-495b-9933-1fbb6cb23dc9",
                    "fraOgMedDato" : "2025-05-01",
                    "tilOgMedDato" : "2025-05-31",
                    "informasjonskilde" : "OPPLYST_TIL_SAKSBEHANDLER_AV_SKATTEPLIKTIG",
                    "erMerketSomSlettet" : false,
                    "antallDagerOverstyrt" : null
               }
          ],
          "endringsnoekkel" : "42653453"
     }
}
```

**Forklaring til respons**

- tin - fødselsnummer/d-nummer
- skattepliktTilNorge - hvilken skatteplikt den skattepliktige har (Begrenset | Global | Ingen skatteplikt)
- skatteordning – hvilken skatteordning den skattepliktige er omfattet av (f.eks. kildeskatt på lønn)
- tolvdeler – antall tolvdeler
- perioder - se forklaring av opphold og arbeidsforholdsperioder under XSD: Beskrivelse av oppholdsperioder og arbeidsforholdsperioder

### Beregne perioder
API‑et mottar alle endrede perioder, validerer dem og beregner skatteplikt samt tolvdeler, som returneres i responsen. Endringene lagres ikke, men API‑et opplyser om beregningen ville ha medført et nytt utkast til skattemeldingen.

**URL** : `POST https://<env>/api/skattemelding/v2/skatteplikt/perioder/<inntektsaar>/<identifikator>/beregn`

**Eksempel URL** : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/skatteplikt/perioder/2025/60875800372/beregn`



**Forespørsel** :

- `<inntektsaar>`: inntektsåret det hentes informasjon for (format: YYYY)
- `<identifikator>`: Fødselsnummer eller D‑nummer til den skattepliktig

Body:

- Iht. XSD: [perioderformottakogutveksling_v1.xsd](/src/resources/xsd/perioderformottakogutveksling_v1.xsd) - se forklaring av oppholdperiode og arbeidsforholdsperioder under XSD: Beskrivelse av oppholdsperioder og arbeidsforholdsperioder
- Eksempel input JSON:

```json
{
  "oppholdsperiode" : [
    {
      "kanEndres" : true,
      "norskPersonidentifikator" : "28818698897",
      "kommune" : null,
      "periodeidentifikator" : "2518d6ba-4989-4aee-b86a-c5054fc49a94",
      "fraOgMedDato" : "2025-05-01",
      "tilOgMedDato" : "2025-05-31",
      "informasjonskilde" : "OPPLYST_TIL_SAKSBEHANDLER_AV_SKATTEPLIKTIG",
      "erMerketSomSlettet" : false,
      "antallDagerOverstyrt" : null
    }
  ],
  "arbeidsforholdsperiode" : [
    {
      "kanEndres" : true,
      "arbeidsforholdIOppdrag" : null,
      "arbeidsforholdSomSjoemann" : null,
      "spesifikasjonAvInntektsgivendeAktivitet" : {
        "oppholdsstedVedInntektsgivendeAktivitet" : "FASTLAND",
        "inntektsgivendeAktivitetstype" : "ARBEIDSTAKER"
      },
      "ansvarligEnhetEllerPart" : {
        "norskIdentifikator" : {
          "organisasjonsnummer" : "315078385",
          "personidentifikator" : null
        },
        "hovedorganisasjon" : null,
        "underenhetensOrganisasjonsnummer" : null,
        "naeringsdrivendeUtenKravTilOrganisasjonsnummer" : null,
        "navn" : "ENKEL ENKEL KATT INVITASJON"
      },
      "norskPersonidentifikator" : "28818698897",
      "kommune" : "4641",
      "periodeidentifikator" : "b1e6b902-07b7-495b-9933-1fbb6cb23dc9",
      "fraOgMedDato" : "2025-05-01",
      "tilOgMedDato" : "2025-05-31",
      "informasjonskilde" : "OPPLYST_TIL_SAKSBEHANDLER_AV_SKATTEPLIKTIG",
      "erMerketSomSlettet" : false,
      "antallDagerOverstyrt" : null
    }
  ],
  "endringsnoekkel" : "42653453"
}
```

***Respons***:

Ved vellykket beregning:

```json
{
  "response" : [
    {
      "inntektsaar" : "2025",
      "skattepliktTilNorge" : "BEGRENSET",
      "skatteordning" : null,
      "tolvdeler" : 1,
      "nyttUtkastProduseres" : true,
      "merknader" : [ ]
    }
  ]
}
```
Forklaring til respons

- inntektsaar – hvilket inntektsår opplysningene gjelder
- skattepliktTilNorge – hvilken skatteplikt den skattepliktige har (Begrenset | Global | Ingen skatteplikt)
- skatteordning – hvilken skatteordning den skattepliktige er omfattet av (f.eks. kildeskatt på lønn)
- tolvdeler – antall tolvdeler etter endringen
- nyttUtkastProduseres – angir om endringen medfører at et nytt utkast til skattemeldingen opprettes
- merknader – eventuelle merknader

### Lagre perioder

API-et mottar og validerer alle perioder, beregner nødvendige verdier og oppdaterer periodedataene. Oppdateringen kan resultere i et nytt utkast til skattemeldingen

**URL** : `POST https://<env>//api/skattemelding/v2/skatteplikt/perioder/<inntektsaar>/<identifikator>/lagre`

**Eksempel** URL : `POST https://idporten.api.skatteetaten.no/api/skattemelding/v2/skatteplikt/perioder/2025/60875800372/lagre`



Forespørsel :

- `<inntektsaar>`: inntektsåret det hentes informasjon for (format: YYYY)
- `<identifikator>`: Fødselsnummer eller D‑nummer til den skattepliktig

Body:

- Iht. XSD: [perioderformottakogutveksling_v1.xsd](/src/resources/xsd/perioderformottakogutveksling_v1.xsd) - se forklaring av oppholdperioder og arbeidsforholdsperioder under XSD: Beskrivelse av oppholdsperioder og arbeidsforholdsperioder
- JSON: samme som ved beregning


**Respons**

Ved vellykket lagring:

```json
{
  "response" : [
    {
      "inntektsaar" : "2025",
      "skattepliktTilNorge" : "BEGRENSET",
      "skatteordning" : null,
      "tolvdeler" : 1,
      "nyttUtkastProduseres" : true,
      "merknader" : [ ]
    }
  ]
}
```
Forklaring til respons

- inntektsaar – hvilket inntektsår opplysningene gjelder
- skattepliktTilNorge – hvilken skatteplikt den skattepliktige har (Begrenset | Global | Ingen skatteplikt)
- skatteordning – hvilken skatteordning den skattepliktige er omfattet av (f.eks. kildeskatt på lønn)
- tolvdeler – antall tolvdeler etter endringen
- nyttUtkastProduseres – angir om endringen medfører at et nytt utkast til skattemeldingen opprettes
- merknader– eventuelle merknader

### Feil respons ifm bad request

```json
{
  "feilmeldinger" : [
    {
      "feilkode" : "Skatteplikt-001",
      "beskrivelse" : "Til og med dato er satt til før fra og med dato på periode",
      "periodeIdentifikator" : "2518d6ba-4989-4aee-b86a-c5054fc49a94"
    }
  ]
}
```

**Feilmeldinger**
- SKATTEPLIKT-001 - Til og med dato er satt til før fra og med dato på periode
- SKATTEPLIKT-002 - Når du skal endre fra og med dato i en periode, må du stå i det det første året som blir påvirket av endringen
- SKATTEPLIKT-003 - Overstyring av antall dager må skje innen inntektsår
- SKATTEPLIKT-004 - Til og med dato er obligatorisk når overstyrte dager er satt
- SKATTEPLIKT-005 - Antall overstyrte dager kan ikke overstige antall dager i perioden
- SKATTEPLIKT-006 - Antall overstyrte dager kan ikke være mindre enn 1 dag eller mer enn inntektsårets lengde
- SKATTEPLIKT-007 - Tin skrevet på periode tilhører ikke denne personen
- SKATTEPLIKT-008 - Mangler informasjon om oppholdssted
- SKATTEPLIKT-009 - Ugyldig kombinasjon av oppholdssted og inntektsgivende aktivitet
- SKATTEPLIKT-010 - Når arbeidsperioder er registrert på fastland i Norge og/eller på fiskefartøy innenfor tolvmilssonen, må opphold i Norge også registreres for samme tidsrom
- SKATTEPLIKT-011 - Datoer på fastsatt periode må være innenfor inntektsår
- SKATTEPLIKT-012 - Mangler informasjon om kommune
- SKATTEPLIKT-013 - Arbeid som grensegjenger, men kommunenummer grenser ikke til Sverige eller Finland
- SKATTEPLIKT-014 - Kan ikke slette periode som opprinnelig startet før inntektsåret
- SKATTEPLIKT-015 - Når du skal endre til og med dato i en periode, må du stå i det første året som blir påvirket av endringen. Du kan også sette til og med datoen til 31.12. året før, hvis denne datoen var dekket av den opprinnelige perioden
- SKATTEPLIKT-016 - Kan ikke flytte periode fra tidligere inntektsår
- SKATTEPLIKT-017 - Mangler informasjon om arbeidsgiver
- SKATTEPLIKT-018 - Ikke mulig å lagre eller endre periode med oppholdssted
- SKATTEPLIKT-019 - Ikke mulig å lagre eller endre periode med oppholdssted og inntektsgivende aktivitet
- SKATTEPLIKT-020 - Ikke mulig å overstyre antall dager for periode med oppholdssted
- SKATTEPLIKT-021 - Ikke mulig å overstyre antall dager for periode med oppholdssted og inntektsgivende aktivitet
- SKATTEPLIKT-022 - Kan ikke legge til en ny periode med oppholdssteds og inntektsgivende aktivitet
- SKATTEPLIKT-023 - Kan ikke slette periode med oppholdssteds og inntektsgivende aktivitet
- SKATTEPLIKT-024 - Kun datoer er redigerbare for tredjeparts informasjonskilde
- SKATTEPLIKT-025 - Ugyldig verdi i felt
- SKATTEPLIKT-026 - Ukjent arbeidsgiver
- SKATTEPLIKT-027 - Periode er myndighetsfastsatt og kan derfor ikke endres
- SKATTEPLIKT-028 - Det er ikke registrert arbeidsperioder som krever registrering av oppholdstid i Norge. Dersom det foreligger arbeid utført på norsk fastland, må dette registreres
- SKATTEPLIKT-101 - Ugyldig inntektsår
- SKATTEPLIKT-102 - Mangler perioder
- SKATTEPLIKT-103 - Ukjent personidentifikator
- SKATTEPLIKT-104 - XSD validering feilet
- SKATTEPLIKT-105 - Periode innhold tilhører ikke request TIN
- SKATTEPLIKT-106 - Flere perioder registrert på samme ID
- SKATTEPLIKT-107 - Utdatert endringsnøkkel
- SKATTEPLIKT-108 - Feil ved beregning for inntektsår

### XSD: Beskrivelse av oppholdsperioder og arbeidsforholdsperioder

En oppholdsperiode angir hvor mange dager den skattepliktige har oppholdt seg på fastlandet i Norge. Dersom oppholdet varer deler av en dag, regnes dette som én hel dag. Antall registrerte dager påvirker om den skattepliktige anses som global eller begrenset skattepliktig. Det er derfor viktig at korrekt antall dager registreres.

Arbeidsperiode skal vise tidsrommet den ansatte var ansatt og utførte arbeid for arbeidsgiver. Perioden skal inkludere avspasering, helger og ferier. Antall tolvdeler blir beregnet ut i fra arbeidsperiodene.

- Oppholdsperiode
  - norskPersonidentifikator - Norsk personidentifikator (fødselsnummer eller D-nummer).
  - periodeidentifikator - Unik identifikator per TIN / norskPersonidentifikator. 
  - fraOgMedDato - Første dag med opphold på fastlandet i Norge. 
  - tilOgMedDato - Siste dag med opphold på fastlandet i Norge. 
  - antallDagerOverstyrt (ikke påkrevd)- Dersom den skattepliktige har mange korte opphold i Norge i løpet av året, kan totalt antall oppholdsdager registreres samlet. Ankomstdag og utreisedag må være innenfor samme kalenderår. Alle dager med opphold i Norge skal telles som hele dager. 
  - erMerketSomSlettet (ikke påkrevd) - true/false 
  - informasjonskilde - Hvor informasjonen kommer fra. [Oppdragasregister | AA-register | Folkeregister | OpplystAvSkatteplikt | OpplystTilSaksbehandlerAvSkattepliktig ]
  - KanEndres - Om perioden kan oppdateres eller slettes
- Arbeidsperiode - Arbeidet skal vise tidsrommet den ansatte var ansatt og utførte arbeid for arbeidsgiver. Perioden skal inkludere avspasering, helger og ferier. 
  - spesifikasjonAvInntektsgivendeAktivitet 
    - oppholdsstedVedInntektsgivendeAktivitet -  Arbeidssted (kodeliste OppholdsstedVedInntektsgivendeAktivitet)
    - inntektsgivendeAktivitetstype - Beskrivelse av arbeidsforhold (kodeliste RolleVedInntektsgivendeAktivitet)
  - ansvarligEnhetEllerPart 
    - hovedorganisasjon 
      - organisasjonsnummer" : Enkeltperson foretakets organisasjonsnummer 
    - naeringsdrivendeUtenKravTilOrganisasjonsnummer - Om virksomheten er selvstendig næringsdrivende uten krav om norsk organisasjonsnummer. 
    - norskIdentifikator 
      - organisasjonsnummer - Arbeidsgivers organisasjonsnummer 
    - navn - Arbeidsgivers navn 
  - norskPersonidentifikator - Norsk personidentifikator (fødselsnummer eller D-nummer). 
  - kommune - Oppgi kommunenr der arbeidstakeren først bosatte seg eller oppholdt seg dette året 
  - periodeidentifikator - Unik identifikator per TIN / norskPersonidentifikator. 
  - fraOgMedDato - Første arbeidsdag 
  - tilOgMedDato (ikke påkrevd) - Siste arbeidsdag 
  - antallDagerOverstyrt (ikke påkrevd) - Dersom den skattepliktige har mange korte arbeidsperioder i Norge i løpet av året for samme arbeidsgiver, kan totalt antall oppholdsdager registreres samlet. Første og siste arbeidsdag må være innenfor samme kalenderår. Når man teller antall dager i arbeidsperiode skal helger, ferier og avspasering telles med 
  - erMerketSomSlettet (ikke påkrevd) - true/false 
  - informasjonskilde - Hvor informasjonen kommer fra. [Oppdragasregister | AA-register | Folkeregister | OpplystAvSkatteplikt | OpplystTilSaksbehandlerAvSkattepliktig ] Perioder oppdatert/opprettet via api-et vil få kilde OpplystAvSkatteplikt. 
  - KanEndres - Om perioden kan oppdateres eller slettes 
- endringsnøkkel – Send med endringsnøkkelen som ble hentet via API-et «hent perioder». Dersom endringsnøkkelen ikke samsvarer, vil beregning eller lagring feile fordi periodene har blitt oppdatert.

### Opprett ny oppholdsperiode
Når du skal registrere en ny oppholdsperiode, må følgende informasjon fylles ut:

- norskPersonidentifikator
- periodeidentifikator - Oppgi en unik ID for perioden. Tradisjonelt brukt UUID 
- fraOgMedDato 
- tilOgMedDato (valgfritt)
- antallDagerOverstyrt (valgfritt)

### Endre oppholdsperiode
For å endre en periode, skriv inn periodeidentifikatoren til perioden du vil oppdatere.

Følgende felter kan endres:

- fraOgMedDato
- tilOgMedDato
- antallDagerOverstyrt
- erMerketSomSlettet 

Dersom informasjonen er fra en tredjepartskilde (Oppdragsregisteret, AA-registeret eller Folkeregisteret), vil den opprinnelige perioden ikke bli endret av API-et. I stedet oppretter API-et en ny fastsatt periode. Når en slik periode opprettes, vil den opprinnelige perioden ikke inngå i beregningsgrunnlaget. Den vil ikke bli hente dersom du kaller get, bare perioder som inngår i beregningsgrunnlaget vil bli hentet.

Dersom den fastsatte perioden senere slettes, vil den opprinnelige perioden automatisk inngå i beregningsgrunnlaget igjen.

### Opprett ny arbeidsforholdsperiode
Når du skal registrere en ny arbeidsperiode, må følgende informasjon fylles ut:

- spesifikasjonAvInntektsgivendeAktivitet - se tabell for gyldige kombinasjoner
  - oppholdsstedVedInntektsgivendeAktivitet -  Arbeidssted (kodeliste OppholdsstedVedInntektsgivendeAktivitet)
  - inntektsgivendeAktivitetstype - beskrivelse av arbeidsforhold (kodeliste RolleVedInntektsgivendeAktivitet)
- ansvarligEnhetEllerPart
  - norskIdentifikator
    - organisasjonsnummer - arbeidsgivers organisasjonsnummer
- norskPersonidentifikator
- kommune - kommune må fylles inn for FASTLAND
- periodeidentifikator - Oppgi en unik ID for perioden. Tradisjonelt brukt UUID
- fraOgMedDato
- tilOgMedDato (valgfritt)
- antallDagerOverstyrt (valgfritt)

Gyldig kombinasjon av oppholdsstedVedInntektsgivendeAktivitet og inntektsgivendeAktivitetstype:

<table>
<tr>
    <td>oppholdsstedVedInntektsgivendeAktivitet</td>
    <td>inntektsgivendeAktivitetstype</td>
    <td>opprettes ny</td>
    <td>endres</td>
    <td>slettes</td>
</tr>
<tr><td>FASTLAND</td><td>ARBEIDSTAKER</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>FASTLAND</td><td>SELVSTENDIG_NAERINGSDRIVENDE</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>FASTLAND</td><td>GRENSEGJENGER</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>FASTLAND</td><td>MOTTAR_INTRODUKSJONSSTOENAD</td><td>nei</td><td>nei</td><td>nei</td></tr>
<tr><td>SJOE</td><td>ARBEIDSTAKER</td><td>nei</td><td>ja</td><td>ja</td></tr>
<tr><td>FISKEFARTOEY_INNENFOR_12_MILSSONE</td><td>ARBEIDSTAKER</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>KONTINENTALSOKKEL</td><td>ARBEIDSTAKER_PAA_SKIP_I_FART</td><td>nei</td><td>Ja</td><td>Ja</td></tr>
<tr><td>KONTINENTALSOKKEL</td><td>ARBEIDSTAKER_INNENFOR_PETROLEUMSVIRKSOMHET</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>KONTINENTALSOKKEL</td><td>ARBEIDSTAKER_INNENFOR_HAVBRUK_MINERALVIRKSOMHET_FORNYBAR_ENERGI_ELLER_KARBONFANGST</td><td>Ja</td><td>Ja</td><td>Ja</td></tr>
<tr><td>FISKEFARTOEY_UTENFOR_12_MILSSONE</td><td>ARBEIDSTAKER</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>ARBEIDSTAKER_FINANSIERT_AV_DEN_NORSKE_STAT</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>ARBEIDSTAKER_FOR_FORSVARET_ELLER_UTENRIKSSTASJON</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>ARBEIDSTAKER_SOM_GJESTEFORELESER_ELLER_FORSKER</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>FLYVENDE_PERSONELL</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>LANDTRANSPORTSJAAFOER</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
<tr><td>FASTLAND_UTENFOR_NORGE</td><td>MOTTAR_STYREHONORAR</td><td>Nei</td><td>Nei</td><td>Nei</td></tr>
</table>

Eksempel på opprettelse av ny arbeidsperiode

```json
{
  "arbeidsforholdsperiode" : [
    {
      "ansvarligEnhetEllerPart" : {
        "norskIdentifikator" : {
          "organisasjonsnummer" : "315072492",
          "personidentifikator" : null
        },
        "navn" : "RETTFERDIG SPISS KATT DALSIDE"
      },
      "spesifikasjonAvInntektsgivendeAktivitet" : {
        "inntektsgivendeAktivitetstype" : "ARBEIDSTAKER_INNENFOR_PETROLEUMSVIRKSOMHET",
        "oppholdsstedVedInntektsgivendeAktivitet" : "KONTINENTALSOKKEL"
      },
      "norskPersonidentifikator" : "03904299382",
      "periodeidentifikator" : "b3597abd-efc3-48ad-8207-36a3ead1b59a",
      "fraOgMedDato" : "2025-03-01",
      "tilOgMedDato" : "2025-05-03",
      "kommune" : "4203"
    }
  ],
  "endringsnoekkel" : "92"
}
```

### Endre arbeidsforholdperioder
For å endre en arbeidsperiode, skriv inn periodeidentifikatoren til perioden du vil oppdatere.

Følgende felter kan endres dersom informasjonskilde IKKE er tredjepartskilde:

- spesifikasjonAvInntektsgivendeAktivitet
  - oppholdsstedVedInntektsgivendeAktivitet
  - inntektsgivendeAktivitetstype
- fraOgMedDato
- tilOgMedDato
- antallDagerOverstyrt
- erMerketSomSlettet


Følgende felter kan endres dersom informasjonskilde er tredjepartskilde:

- fraOgMedDato
- tilOgMedDato
- antallDagerOverstyrt
- erMerketSomSlettet

Dersom informasjonen er fra en tredjepartskilde (Oppdragsregisteret, AA-registeret eller Folkeregisteret), vil den opprinnelige perioden ikke bli endret av API-et. I stedet oppretter API-et en ny fastsatt periode. Når en slik periode opprettes, vil den opprinnelige perioden ikke inngå i beregningsgrunnlaget. Den vil ikke bli hente dersom du kaller get, bare perioder som inngår i beregningsgrunnlaget vil bli hentet.

Dersom den fastsatte perioden senere slettes, vil den opprinnelige perioden automatisk inngå i beregningsgrunnlaget igjen.

#### Selvstendig næringsdrivende
Når inntektsgivendeAktivitetstype er satt til SELVSTENDIG_NAERINGSDRIVENDE, skal ansvarligEnhetEllerPart tolkes annerledes enn for arbeidstakerforhold.

I disse tilfellene representerer ansvarligEnhetEllerPart den selvstendige virksomheten, og ikke en arbeidsgiver.

- ansvarligEnhetEllerPart
  - norskIdentifikator : null
  - hovedorganisasjon 
    - organisasjonsnummer : Enkeltperson foretakets norske organisasjonsnummer 
    - navn : null 
  - naeringsdrivendeUtenKravTilOrganisasjonsnummer - Settes til true dersom virksomheten er selvstendig næringsdrivende uten krav om norsk organisasjonsnummer. Default verdi er false.

Dette innebærer at ansvarligEnhetEllerPart alltid er påkrevd, men at identifikasjonsmåten varierer basert på type inntektsgivende aktivitet.

***Eksempel selvstendig næringsdrivende:***
```json
{

  "arbeidsforholdsperiode" : [
    {
      "ansvarligEnhetEllerPart" : {
        "norskIdentifikator" : null,
        "hovedorganisasjon" : {
          "organisasjonsnummer" : "621905422",
          "navn" : null
        },
        "naeringsdrivendeUtenKravTilOrganisasjonsnummer" : false,
        "underenhetensOrganisasjonsnummer" : null,
        "navn" : null
      },
      "spesifikasjonAvInntektsgivendeAktivitet" : {
        "inntektsgivendeAktivitetstype" : "SELVSTENDIG_NAERINGSDRIVENDE",
        "oppholdsstedVedInntektsgivendeAktivitet" : "FASTLAND"
      },
      "norskPersonidentifikator" : "50679699854",
      "periodeidentifikator" : "2c2c6214-3917-485f-b35a-1ebc214a8b4c_92",
      "fraOgMedDato" : "2025-02-02",
      "tilOgMedDato" : "2025-02-04",
      "informasjonskilde" : "OPPLYST_AV_SKATTEPLIKTIG",
      "kommune" : "1554"
    }
  ],
  "endringsnoekkel" : "42824332"

}
```