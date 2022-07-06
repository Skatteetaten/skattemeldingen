import requests
from pathlib import Path

# Slå av sertifikat verifikasjon i test
import urllib3

urllib3.disable_warnings()

ALTINN_URL = "https://skd.apps.tt02.altinn.no"


def hent_altinn_token(idporten_token: dict) -> dict:
    altinn3 = "https://platform.tt02.altinn.no/authentication/api/v1/exchange/id-porten"
    r = requests.get(altinn3, headers=idporten_token, verify=False)
    r.raise_for_status()
    altinn_header = {"Authorization": "Bearer " + r.text}
    print(altinn_header)
    return altinn_header


def hent_party_id(token: dict, appnavn: str = "skd/formueinntekt-skattemelding-v2") -> str:
    url = f"{ALTINN_URL}/{appnavn}/api/v1/profile/user"
    r = requests.get(url, headers=token, verify=False)
    r.raise_for_status()
    return str(r.json()["partyId"])


def opprett_ny_instans(header: dict, fnr: str, appnavn: str = "skd/formueinntekt-skattemelding-v2") -> dict:
    payload = {
        "instanceOwner": {
            "personNumber": fnr
        },
        "appOwner": {
            "labels": ["gr", "x2"]
        },
        "appId": appnavn,
        "dueBefore": "2020-06-01T12:00:00Z",
        "visibleAfter": "2019-05-20T00:00:00Z",
        "title": {"nb": "Skattemelding"}
    }
    url = f"{ALTINN_URL}/{appnavn}/instances/"
    r = requests.post(url, headers=header, json=payload, verify=False)
    r.raise_for_status()
    return r.json()


def opprett_ny_instans_med_inntektsaar(header: dict, inntektsaar: str, fnr: str = None, orgnr=None,
                                       appnavn: str = "skd/formueinntekt-skattemelding-v2") -> dict:

    instans_owner = {"personNumber": fnr} if fnr else {"organisationNumber": orgnr}

    payload = {
        "instanceOwner": instans_owner,
        "appOwner": {
            "labels": ["gr", "x2"]
        },
        "appId": appnavn,
        "dataValues": {"inntektsaar": inntektsaar},
        "dueBefore": "2022-06-01T12:00:00Z",
        "visibleAfter": "2019-05-20T00:00:00Z",
        "title": {"nb": "Skattemelding"}
    }
    url = f"{ALTINN_URL}/{appnavn}/instances/"
    r = requests.post(url, headers=header, json=payload, verify=False)
    r.raise_for_status()
    return r.json()


def last_opp_metadata(instans_data: dict, token: dict, xml: str = None,
                      appnavn: str = "skd/formueinntekt-skattemelding-v2") -> requests:

    id = instans_data['id']
    data_id = instans_data['data'][0]['id']

    url = f"{ALTINN_URL}/{appnavn}/instances/{id}/data/{data_id}"
    token["content-type"] = "application/xml"
    r = requests.put(url, data=xml, headers=token, verify=False)
    r.raise_for_status()
    return r


def last_opp_metadata_json(instans_data: dict, token: dict, inntektsaar: int = 2021,
                           appnavn: str = "skd/sirius-skattemelding-v2") -> requests:
    id = instans_data['id']
    data_id = instans_data['data'][0]['id']

    url = f"{ALTINN_URL}/{appnavn}/instances/{id}/data/{data_id}"
    token["content-type"] = "application/json"
    payload = {"inntektsaar": inntektsaar}
    r = requests.put(url, json=payload, headers=token, verify=False)
    r.raise_for_status()
    return r


def last_opp_skattedata(instans_data: dict, token: dict, xml: str,
                        data_type: str = "skattemelding",
                        appnavn: str = "skd/formueinntekt-skattemelding-v2") -> requests:
    url = f"{ALTINN_URL}/{appnavn}/instances/{instans_data['id']}/data?dataType={data_type}"
    token["content-type"] = "text/xml"
    token["Content-Disposition"] = "attachment; filename=skattemelding.xml"

    r = requests.post(url, data=xml, headers=token, verify=False)
    return r


def last_opp_vedlegg(instans_data: dict, token: dict, vedlegg_fil, content_type: str,
                     data_type="skattemelding-vedlegg",
                     appnavn: str = "skd/formueinntekt-skattemelding-v2") -> requests:
    url = f"{ALTINN_URL}/{appnavn}/instances/{instans_data['id']}/data?dataType={data_type}"
    filnavn = Path(vedlegg_fil).name
    token["content-type"] = content_type
    token["Content-Disposition"] = f"attachment; filename={filnavn}"

    with open(vedlegg_fil, 'rb') as f:
        vedlegg_blob = f.read()

    r = requests.post(url, data=vedlegg_blob, headers=token, verify=False)
    r.raise_for_status()
    return r


def endre_prosess_status(instans_data: dict, token: dict, neste_status: str,
                         appnavn: str = "skd/formueinntekt-skattemelding-v2") -> str:
    if neste_status not in ["start", "next", "completeProcess"]:
        raise NotImplementedError

    url = f"{ALTINN_URL}/{appnavn}/instances/{instans_data['id']}/process/{neste_status}"
    r = requests.put(url, headers=token, verify=False)
    r.raise_for_status()
    return r.text


def hent_instans(instans_data: dict, token: dict, appnavn: str = "skd/formueinntekt-skattemelding-v2") -> requests:
    url = f"{ALTINN_URL}/{appnavn}/instances/{instans_data['id']}"
    r = requests.get(url, headers=token, verify=False)
    r.raise_for_status()
    return r


if __name__ == '__main__':
    print("Dette er en rekke med metoder jupyter notebook applikasjonen bruker")
