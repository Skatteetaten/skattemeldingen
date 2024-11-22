from dataclasses import dataclass, field
import xml
import xmltodict
import zlib
import brotli
import requests

from docs.test.testinnsending.skatteetaten_api import decode_dokument

@dataclass
class ValiderData:
    orgnr: str
    partnr: str = ""
    konvolutt: str = ""
    inntektsår: str = "2023"
    sme_b64: str = ""
    nsp_b64: str = ""
    nsp_vedlegg_filnavn: str = ""
    sme_xml: str = ""
    nsp_xml: str = ""
    dokref_gjeldende: str = ""
    vedlegg_referanse: str = ""
    jobb_id: str = ""
    valider_data: str = ""
    jobb_status: str = ""
    resultat_av_valdiering: str = ""
    valider_resultat: dict = field(default_factory=dict)


def last_opp_vedlegg(base_url: str, valider_data: ValiderData, access_token: dict, gzip=False, bruk_brotli=False):
    api_path = f'/api/skattemelding/v2/jobb/{valider_data.inntektsår}/{valider_data.orgnr}/last-opp.vedlegg'
    url = base_url + api_path


    try:
        access_token.pop("Content-Type")
    except KeyError:
        pass

    if gzip and bruk_brotli:
        raise(NotImplementedError)

    if gzip:
        print("Gziper nsp base64 str ", end="")
        compress = zlib.compressobj(wbits=16+zlib.MAX_WBITS)
        valider_data.nsp_b64 = compress.compress(valider_data.nsp_b64.encode("utf-8")) + compress.flush()
        valider_data.nsp_vedlegg_filnavn = valider_data.nsp_vedlegg_filnavn + ".gz"
        print("Gzip ferdig, laster opp vedlegg ", end = "")

    if bruk_brotli:
        valider_data.nsp_b64 = brotli.compress(valider_data.nsp_b64.encode("utf-8"))
        valider_data.nsp_vedlegg_filnavn = valider_data.nsp_vedlegg_filnavn + ".br"

    files = {valider_data.nsp_vedlegg_filnavn: valider_data.nsp_b64}
    return requests.post(url, files=files, headers=access_token, verify=False)


def hent_jobb(base_url: str,
              valider_data: ValiderData,
              access_token: dict,
              type: str = "status") -> requests.get:
    try:
        access_token.pop("Content-Type")
    except KeyError:
        pass

    api_path = f'/api/skattemelding/v2/jobb/{valider_data.inntektsår}/{valider_data.orgnr}/{valider_data.jobb_id}/{type}'
    url = base_url + api_path
    return requests.get(url, headers=access_token)


def hent_gjeldende(base_url: str, orgnr: str, inntekstår: int, access_token: dict):
    url = base_url + f"/api/skattemelding/v2/{inntekstår}/{orgnr}"
    return requests.get(url, headers=access_token)


def decode_gjeldende(gjeldende_response: requests.Response(), valdier_data: ValiderData):
    sme_og_naering_respons = xmltodict.parse(gjeldende_response.text)
    skattemelding_base64 = \
        sme_og_naering_respons['skattemeldingOgNaeringsspesifikasjonforespoerselResponse']['dokumenter'][
            'skattemeldingdokument']
    sme_base64 = skattemelding_base64["content"]
    dokref = sme_og_naering_respons["skattemeldingOgNaeringsspesifikasjonforespoerselResponse"]["dokumenter"][
        'skattemeldingdokument']['id']
    decoded_sme_xml = decode_dokument(skattemelding_base64)
    sme_gjeldende = xml.dom.minidom.parseString(decoded_sme_xml["content"]).toprettyxml()

    sme_dict = xmltodict.parse(decoded_sme_xml['content'])
    partsnummer = sme_dict['skattemelding']['partsnummer']

    valdier_data.sme_b64 = sme_base64
    valdier_data.sme_xml = sme_gjeldende
    valdier_data.dokref_gjeldende = dokref
    valdier_data.partnr = partsnummer
    return valdier_data


def valider_async(base_url: str,
                  valider_data: ValiderData,
                  access_token: dict) -> requests.Response:
    api_path = f"/api/skattemelding/v2/jobb/{valider_data.inntektsår}/{valider_data.orgnr}/start"
    url = base_url + api_path
    headers = dict()
    headers['Content-Type'] = "application/xml"
    headers.update(access_token)
    return requests.post(url, data=valider_data.konvolutt, headers=headers)


def lag_asynk_konvolutt(valider_data: ValiderData) -> str:
    valider_asynk_up_konvolutt = """<skattemeldingOgNaeringsspesifikasjonRequest xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemeldingognaeringsspesifikasjon:request:v2">
    <dokumenter>
        <dokument>
            <type>skattemeldingUpersonlig</type>
            <encoding>utf-8</encoding>
            <content>{sme_up_base64}</content>
        </dokument>
        <dokument>
            <type>naeringsspesifikasjonReferanse</type>
            <encoding>utf-8</encoding>
            <content>{vedleggs_referanse}</content>
        </dokument>
    </dokumenter>
    <dokumentreferanseTilGjeldendeDokument>
        <dokumenttype>skattemeldingUpersonlig</dokumenttype>
        <dokumentidentifikator>{dokumentreferanse}</dokumentidentifikator>
    </dokumentreferanseTilGjeldendeDokument>
    <inntektsaar>{inntektsår}</inntektsaar>
    <innsendingsinformasjon>
        <innsendingstype>komplett</innsendingstype>
        <opprettetAv>Turboskatt</opprettetAv>
    </innsendingsinformasjon>
</skattemeldingOgNaeringsspesifikasjonRequest>"""
    return valider_asynk_up_konvolutt.format(sme_up_base64=valider_data.sme_b64,
                                             vedleggs_referanse=valider_data.vedlegg_referanse,
                                             dokumentreferanse=valider_data.dokref_gjeldende,
                                             inntektsår=valider_data.inntektsår).lstrip("\n")
