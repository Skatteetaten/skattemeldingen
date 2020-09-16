from hent import main_relay
import requests

ALTINN_URL = "https://skd.apps.tt02.altinn.no"

def hent_altinn_token(idporten_token: dict) -> dict:
    altinn3 = "https://platform.tt02.altinn.no/authentication/api/v1/exchange/id-porten"
    r = requests.get(altinn3, headers=idporten_token)
    r.raise_for_status()
    altinn_header = {"Authorization": "Bearer " + r.text}
    print(altinn_header)
    return altinn_header


def hent_party_id(token: dict) -> str:
    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1/api/v1/profile/user"
    r = requests.get(url, headers=token)
    r.raise_for_status()
    return str(r.json()["partyId"])


def opprett_ny_instans(header: dict, party_id: str) -> dict:
    payload = {
        "instanceOwner": {
            "partyId": party_id
        },
        "appOwner": {
            "labels": ["gr", "x2"]
        },
        "appId": "skd/sirius-skattemelding-v1",
        "dueBefore": "2020-06-01T12:00:00Z",
        "visibleAfter": "2019-05-20T00:00:00Z",
        "title": {"nb": "Skattemelding"}
    }
    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1/instances/"
    r = requests.post(url, headers=header, json=payload)
    r.raise_for_status()
    return r.json()


def last_opp_metadata(instans_data: dict, token: dict, xml: str = None) -> None:
    id = instans_data['id']
    data_id = instans_data['data'][0]['id']

    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1/instances/{id}/data/{data_id}"
    token["content-type"] = "application/xml"
    r = requests.put(url, data=xml, headers=token)
    r.raise_for_status()
    return r


def last_opp_skattedata(instans_data: dict, token: dict, xml: str) -> None:
    data_type = "skattemelding"
    id = instans_data['id']
    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1/instances/{id}/data?dataType={data_type}"
    token["content-type"] = "text/xml"
    token["Content-Disposition"] = "attachment; filename=skattemelding.xml"

    r = requests.post(url, data=xml, headers=token)
    r.raise_for_status()
    return r

def get_innstans(partyid, id, token):
    r = requests.get(f"{ALTINN_URL}/skd/sirius-skattemelding-v1/instances/{partyid}/{id}", headers=token)
    return r.json()


def endre_prosess_status(instans_data: dict, token: dict, neste_status: str) -> str:
    if neste_status not in ["start", "next", "completeProcess"]:
        raise NotImplementedError

    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1/instances/{instans_data['id']}/process/{neste_status}"
    r = requests.put(url, headers=token)
    r.raise_for_status()
    return r.text


if __name__ == '__main__':
    print("0. Generer ID-porten token.")
    idporten_header = main_relay()
    print('1. kall "hent AltinnToken" (bruk ID-porten token fra punkt0 i header-felt "Authorization").')
    altinn_header = hent_altinn_token(idporten_header)
    party_id = hent_party_id(altinn_header)
    print("2. Kall 'Opprett ny Instans' (bruk Altinn-tokenet fra punkt1 i header-felt 'Authorization', legg på Bearer først).")
    instans_data = opprett_ny_instans(altinn_header, party_id)
    print("3.1 Kall 'Last opp metadata (skattemelding_V1)' (bruk instanceId og data.id fra punkt2 på slutten av URL-en).")
    last_opp_metadata(instans_data, altinn_header)
    print("4. Kall 'Last opp skattemelding.xml' (bruk Altinn-token fra punkt1 og instanceId fra punkt2).")
    last_opp_skattedata(instans_data, altinn_header)

    print("Sett proses til neste steg - Bekreftelse")
    print(endre_prosess_status(instans_data, altinn_header, "next"))
    print("Sett proses til neste steg - Tilbakemelding")
    print(endre_prosess_status(instans_data, altinn_header, "next"))
    # Dette steget gjøres av skatteetaten og ikke sluttbruker
    # print("Sett proses til neste steg - Avslutt")
    # print(endre_prosess_status(instans_data, altinn_header, "completeProcess"))

    url = f"{ALTINN_URL}/skd/sirius-skattemelding-v1#/instance/{instans_data['id']}"
    print(f"Visning i Altinn: {url}")

