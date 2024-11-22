from dataclasses import dataclass
import operator

import xmltodict

@dataclass(unsafe_hash=True)
class Kommune:
    kommunenummer: str
    tidligere_kommunenummer: set
    kommunenavn: str

    def __lt__(self, other):
        return self.kommunenummer < other.kommunenummer


with open("2024_aktiveKommunenummer.xml") as f:
    kodeliste = xmltodict.parse(f.read())


kommunennumer_som_skal_med = set([
    "0301", #OSLO
    "5601", #Alta tidligere 5403
    "5628", #Tana tidligere "5441"
    "2100", #Svalbard
    "1580", #Haram
    "1508" #Ålesund
])

alle_kommuner = list()


for kode in kodeliste["kodeliste"]["kode"]:
    kommunenummer = kode['tekniskNavn']

    try:
        kommunenavn = kode["kodetillegg"].get('OffisieltKommunenavn', None)

        tidligere_kommuner = ""
        for k,v  in kode["kodetillegg"].items():
            if k == "forrigeKommunenummer":
                tidligere_kommuner = v

    except KeyError:
        kommunenavn = None
        tidligere_kommuner = None
    if kommunenummer == "2100":
        kommunenavn = "Svalbard"

    kommune = Kommune(kommunenummer, tidligere_kommuner, kommunenavn)
    alle_kommuner.append(kommune)

alle_kommuner.sort(reverse=True)

anonymiserte_kommuner = []
fylke = ""
antall_i_fylke = 0

for kommune in alle_kommuner:
    if kommune.kommunenummer in kommunennumer_som_skal_med:
        anonymiserte_kommuner.append(kommune)
        antall_i_fylke += 1
        continue

    kommunes_fylke = kommune.kommunenummer[:2]
    if fylke == kommunes_fylke and antall_i_fylke < 3:
        antall_i_fylke += 1
        anonymiserte_kommuner.append(kommune)
    elif fylke != kommunes_fylke:
        fylke = kommunes_fylke
        antall_i_fylke = 1
        anonymiserte_kommuner.append(kommune)


print("kommunenummer, kommunenavn, tdiligere_kommunenummer")
for kommune in anonymiserte_kommuner:
    print(f'{kommune.kommunenummer},{kommune.kommunenavn},"{kommune.tidligere_kommunenummer}"')