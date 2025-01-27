#!/usr/bin/env python3

# Et eksempelprogram som viser integrasjon med ID-porten og
# APIer fra skatteetaten.
import base64
import webbrowser
import random
import time
import datetime
import json
import os
import re

from urllib.parse import urlparse, parse_qs, quote
from base64 import urlsafe_b64encode, urlsafe_b64decode
from hashlib import sha256
from http.server import BaseHTTPRequestHandler, HTTPServer

import xmltodict

import requests
from jose import jwt

CLIENT_ID = 'cfe28bad-c77c-40e2-9c54-a1c658f4478b'
AUTH_DOMAIN = 'test.idporten.no'
ALGORITHMS = ["RS256"]
VERIFY_SSL = True

if not VERIFY_SSL:
    requests.packages.urllib3.disable_warnings()

def valider(payload, inntektsår=2022,
            s: requests.Session() = None,
            tin: str = "",
            idporten_header: dict = dict,
            base_url: str = "idporten-api-sbstest.sits.no"):
    url_valider = f'https://{base_url}/api/skattemelding/v2/valider/{inntektsår}/{tin}'
    header = dict(idporten_header)
    header["Content-Type"] = "application/xml"
    return s.post(url_valider, headers=header, data=payload)


def print_request_as_curl(r):
    command = "curl -X {method} -H {headers} {data} '{uri}'"
    method = r.request.method
    uri = r.request.url
    data = f"-d '{r.request.body}'" if r.request.body else ""
    headers = ['"{0}: {1}"'.format(k, v) for k, v in r.request.headers.items()]
    headers = " -H ".join(headers)
    print(command.format(method=method, headers=headers, data=data, uri=uri))


# En enkel webserver som venter på callback fra browseren, og lagrer unna callback-urlen.
class BrowserRedirectHandler(BaseHTTPRequestHandler):
    timeout = 1000
    result = None

    def do_GET(self) -> None:
        self.send_response(200)
        self.send_header('Content-Type', 'text/html')
        self.end_headers()
        self.wfile.write(b"""
            <!DOCTYPE html>
            <title>Authentication complete</title>
            <body>
            <h1>Authentication complete</h1>
            <p>You may close this page.
            """)
        BrowserRedirectHandler.result = self


# Return a random byte array of length len
def random_bytes(n: int) -> bytes:
    return bytearray(random.getrandbits(8) for _ in range(n))


def base64_response(s: str, encoding: str) -> str:
    base64_bytes = s.encode(encoding)
    message_bytes = base64.b64decode(base64_bytes)
    return message_bytes.decode(encoding)


def decode_dokument(dokument):
    orginal_conten = dokument["content"]
    encoding = dokument["encoding"]
    dokument["content"] = base64_response(orginal_conten, encoding)
    return dokument


def iter_dokumenter(d):
    for k, v in d.items():
        if k == 'dokument':  # valider response har en liste med dokumenter
            for dok in v:
                decode_dokument(dok)
        elif k == "skattemeldingdokument":
            decode_dokument(v)
        elif k == "dokumenter":
            iter_dokumenter(v)
        else:
            pass
    return d


def base64_decode_response(r: requests):
    if not r:  # ikke 200 ok
        return r.text
    utkast_resp = xmltodict.parse(r.text)
    for k, v in utkast_resp.items():
        v = iter_dokumenter(v)
        utkast_resp[k] = v
    return xmltodict.unparse(utkast_resp)


def skattemelding_visning(instans_data: dict,
                          appnavn: str = "skd/formueinntekt-skattemelding-v2",
                          url_testmiljoe = "https://skatt-sbstest.sits.no") -> None:
    instans_id = instans_data['id']
    url_visning = f"{url_testmiljoe}/web/skattemelding-visning/altinn?appId={appnavn}&instansId={instans_id}"
    webbrowser.open(url_visning, new=0, autoraise=True)
    return url_visning


def get_access_token(**kwargs) -> dict:
    if "verbose" in kwargs:
        verbose = kwargs["verbose"]
    else:
        verbose = False

    # Get the jwks from idporten (for token verification later)
    url_auth = requests.get('https://{}/.well-known/openid-configuration'.format(AUTH_DOMAIN), verify=VERIFY_SSL).json()["jwks_uri"].replace("\\", "")
    jwks = requests.get(url_auth, verify=VERIFY_SSL).json()

    server = HTTPServer(('127.0.0.1', 12345), BrowserRedirectHandler)
    port = server.server_address[1]
    assert 0 < port < 65536

    # /authorize endpoint
    # https://docs.digdir.no/docs/idporten/oidc/oidc_protocol_authorize.html
    # State and PKCE is requiered
    code_verifier = base64.urlsafe_b64encode(os.urandom(64)).decode('utf-8')
    code_verifier = re.sub('[^a-zA-Z0-9]+', '', code_verifier)

    code_challenge = sha256(code_verifier.encode('utf-8')).digest()
    code_challenge = base64.urlsafe_b64encode(code_challenge).decode('utf-8')
    code_challenge = code_challenge.replace('=', '')

    state = urlsafe_b64encode(random_bytes(16)).decode().rstrip("=")
    nonce = "{}".format(int(time.time() * 1e6))

    quory_params = '?response_type=code' \
                   +f'&client_id={CLIENT_ID}' \
                   +f'&redirect_uri=http://localhost:{port}/token' \
                   +'&scope=skatteetaten:formueinntekt/skattemelding altinn:instances.read altinn:instances.write openid' \
                   +f'&state={state}' \
                   +f'&nonce={nonce}' \
                   +'&acr_values=idporten-loa-high' \
                   +'&ui_locales=nb'\
                   +f'&code_challenge={code_challenge}' \
                   +'&code_challenge_method=S256'

    url_auth = 'https://login.{}/authorize'.format(AUTH_DOMAIN) + quote((quory_params), safe='?&=_')
    if verbose:
        print(url_auth)

    # Open web browser to get ID-porten authorization token
    webbrowser.open(url_auth, new=0, autoraise=True)

    # Wait for callback from ID-porten
    while not hasattr(BrowserRedirectHandler.result, 'path'):
        server.handle_request()

    # Free the port, no more callbacks expected
    server.server_close()

    auth_token_json = parse_qs(urlparse(BrowserRedirectHandler.result.path).query)
    if verbose:
        print("Authorization token received")
        print(auth_token_json)

    assert len(auth_token_json['state']) == 1 and auth_token_json['state'][0] == state

    # Use the authorization code to get access and id token from /token
    # https://docs.digdir.no/docs/idporten/oidc/oidc_protocol_token.html
    payload = {
               'client_id': CLIENT_ID,
               'grant_type': 'authorization_code',
               'code': auth_token_json['code'][0],
               'redirect_uri': 'http://localhost:{}/token'.format(port),
               'code_verifier': code_verifier,
    }
    headers = {'Accept': 'application/json', "Content-type": "application/x-www-form-urlencoded"}

    response_token = requests.post('https://{}/token'.format(AUTH_DOMAIN), headers=headers, data=payload, verify=VERIFY_SSL)

    if not response_token:
        print(response_token, response_token.headers)
        print(response_token.text)
        response_token.raise_for_status()

    token_json = response_token.json()
    assert token_json['token_type'] == 'Bearer'

    # Validate tokens according to https://tools.ietf.org/html/rfc7519#section-7.2
    # A list of 3rd party libraries for various languages on https://jwt.io/
    # python possibilites: pyjwt, python-jose, jwcrypto, authlib
    # We use python-jose here:
    jwt.decode(
        token_json['id_token'],
        jwks,
        algorithms=ALGORITHMS,
        issuer="https://" + AUTH_DOMAIN,
        audience=CLIENT_ID,
        access_token=token_json['access_token']
    )
    id_encoded = token_json['id_token'].split(".")[1]
    id_token = json.loads(urlsafe_b64decode(id_encoded + "==").decode())
    assert id_token['nonce'] == nonce

    # Also validate the access token separately, this is what we have to pass
    # on to our APIs
    jwt.decode(
        token_json['access_token'],
        jwks,
        algorithms=ALGORITHMS,
        issuer="https://" + AUTH_DOMAIN
    )
    at_encoded = token_json['access_token'].split(".", 3)[1]
    access_token = json.loads(urlsafe_b64decode(at_encoded + "==").decode())
    assert access_token['client_id'] == CLIENT_ID

    exp_time = datetime.datetime.fromtimestamp(access_token['exp']).strftime("%H:%M:%S")
    exp_secs = access_token['exp'] - int(time.time())
    print(f"The token is good, expires at {exp_time} ({exp_secs} seconds from now)")
    if verbose:
        print("\nBearer {}".format(token_json['access_token']))

    header = {'Authorization': 'Bearer ' + token_json['access_token']}
    return header


if __name__ == "__main__":
    get_access_token() #Test fnr: 14888999060